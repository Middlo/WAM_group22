var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Task = require('../models/Task');
var Reminder = require('../models/Reminder');

// Create a new task
router.post('/', function(req, res, next) {
    if(req.body.taskTitle || req.body.taskDescription || req.body.importanceLevel 
        || req.body.deadline|| req.body.reminder){
        var task = new Task(req.body);
        task.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json(task);//json({"message" : 'Task Successfully created'});
        });
    } else {
        res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
    }
});


// Return list of all tasks
router.get('/', function(req, res, next) {
    Task.find(function(err, tasks) {
        if (err) { 
            return next(err); 
        } else if (tasks.length === 0)
            res.status(200).json({"message" : 'There are no Tasks registered'});
        else {
            res.status(200).json({'tasks': tasks});
        }
    });
});


// Return a task with a given ID
router.get('/:taskId', function(req, res, next) {
    var id = req.params.taskId;
    Task.findById(id, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'Task not found'});
        }
        res.status(200).json(task);
    });
});


// Update a whole task with a given ID
router.put('/:taskId', function(req, res, next) {
    var id = req.params.taskId;
    Task.findById(id, function(err, task) {
        if (err) { return next(err); }
        if (task == null) {
            return res.status(404).json({"message": "Task not found"});
        }

        if(req.body.taskTitle || req.body.taskDescription || req.body.importanceLevel 
            || req.body.deadline|| req.body.reminder){
            
            task.taskTitle = req.body.taskTitle;
            task.taskDescription = req.body.taskDescription;
            task.importanceLevel = req.body.importanceLevel;
            task.deadline = req.body.deadline;
            task.reminder = req.body.reminder;

            task.save();
            res.status(200).json({"task" : task}); // json({"message" : 'Task successfully updated (put)', task});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
        
    });
});


//Patch a task with a given ID
router.patch('/:taskId', function(req, res, next) {
    var id = req.params.taskId;
    Task.findById(id, function(err, task) {
        if (err) { return next(err); }
        if (task == null) {
            return res.status(404).json({"message": "Task is not found"});
        }

        if(req.body.taskTitle || req.body.taskDescription || req.body.importanceLevel || 
            req.body.deadline|| req.body.reminder){

            task.taskTitle = (req.body.taskTitle || task.taskTitle);
            task.taskDescription = (req.body.taskDescription || task.taskDescription);
            task.importanceLevel = (req.body.importanceLevel || task.importanceLevel);
            task.deadline = (req.body.deadline || task.deadline);
            task.reminder = (req.body.reminder || task.reminder);

            task.save();
            res.status(200).json({"task" : task}); // json({"message" : 'Task successfully updated (patch)', task});
        
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }

        
    });
});


//Delete a task with a given ID
router.delete('/:taskId', function(req, res, next) {
    var id = req.params.taskId;
    Task.findOneAndDelete({_id: id}, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'Task is not found'});
        }
        res.status(200).json({"message" : 'Task successfully removed'});
    });
});


// Delete all tasks
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no task msg after removals
    
    Task.find(function(err, tasks) {
        if (err) { 
            return next(err); 
        } else if (tasks.length === 0 && removable){
            res.status(204).json({"message" : 'There are no Tasks to be deleted'});
        } else {
            removable = 0;

            for(var i = 0; i < tasks.length; i++ ){
                Task.findByIdAndRemove({_id : tasks[i].id}, function(err, task){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json({"message" :'All Tasks are successfully removed'});
        }
    });
});


// Create a new reminder
router.post('/:taskId/reminders', function(req, res, next) {
    var id = req.params.taskId;

    Task.findById(id, function(err, foundTask) {
        if (err) { return next(err); }
        if (foundTask === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        if(req.body.topic || req.body.targetMoment || req.body.remindBefore){
            const newReminder = new Reminder({
                //_id: new mongoose.Types.ObjectId(),
                topic: req.body.topic,
                targetMoment: req.body.targetMoment,
                remindBefore: req.body.remindBefore,
                reminderFor: id
            });

            newReminder.save(function(err2, addedReminder){
                if (err2) { return next(err2)};
                foundTask.reminder = addedReminder;
                foundTask.save();
                res.status(201).json({"task" : foundTask});// json({"message": ("Reminder " + addedReminder._id + " is registered to Task " + foundTask._id)});
            });
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        } 
    });
});
    
        
// Return list of all reminders of a task
router.get('/:taskId/reminders', function(req, res, next) {
    var id = req.params.taskId;

    Task.findById(id, function(err, foundTask) {
        if (err) { return next(err); }
        if (foundTask === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        } else/* if (foundTask.reminders.length === '')
            return res.status(204).json({'message': 'The Task has yet not a registered Reminder'});*/
        res.status(200).json({"reminder": foundTask.reminder});
    });
});


// Return a specific reminder of a task
router.get('/:taskId/reminders/:reminderId', function(req, res, next) {
    var tasId = req.params.taskId;
    var remId = req.params.reminderId;

    Task.findById(tasId, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Task'});
            }
            res.status(200).json({"reminders": foundReminder});
        });
    });
});


// Update part of specific reminder of a task
router.patch('/:taskId/reminders/:reminderId', function(req, res, next) {
    var tasId = req.params.taskId;
    var remId = req.params.reminderId;

    Task.findById(tasId, function(err, foundTask) {
        if (err) { return next(err); }
        if (foundTask === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Task'});
            }

            if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminder){

                foundReminder.topic = (req.body.topic || foundReminder.topic);
                foundReminder.targetMoment = (req.body.targetMoment || foundReminder.targetMoment);
                foundReminder.remindBefore = (req.body.remindBefore || foundReminder.remindBefore);
                foundReminder.reminder = req.body.reminder

                foundReminder.save();
                foundTask.save();
                res.status(200).json({"message" : 'Reminder detail successfully updated (patch)', "updated reminder" : foundReminder});
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }
        });
    });
});


// Update whole of specific reminder of a task
router.put('/:taskId/reminders/:reminderId', function(req, res, next) {
    var tasId = req.params.taskId;
    var remId = req.params.reminderId;

    Task.findById(tasId, function(err, foundTask) {
        if (err) { return next(err); }
        if (foundTask === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Task'});
            }

            if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminder){

                foundReminder.topic = req.body.topic;
                foundReminder.targetMoment = req.body.targetMoment;
                foundReminder.remindBefore = req.body.remindBefore;
                foundReminder.reminder = req.body.reminder
                
                foundReminder.save();
                foundTask.save();
                res.status(200).json({"message" : 'Reminder detail successfully updated (put)', "updated reminder" : foundReminder});
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }

        });
    });
});


// Delete a specific reminder of a task
router.delete('/:taskId/reminders/:reminderId', function(req, res, next) {
    var tasId = req.params.taskId;
    var remId = req.params.reminderId;

    Task.findById(tasId, function(err, foundTask) {
        if (err) { return next(err); }
        if (foundTask === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Task'});
            }
            var updatedReminders = [];
            
            if(!(foundTask.reminder == remId)){
                updatedReminders =foundTask.reminder;
            }

            foundTask.reminder = updatedReminders;
            foundTask.save();
            res.status(200).json({"updated reminders list" : foundTask.reminder});
        });
    });
});


// Delete all reminders of a task
router.delete('/:taskId/reminders', function(req, res, next) {
    var tasId = req.params.taskId;

    var removable = 1; //to limit the display of a no task msg after removals

    Task.findById(tasId, function(err, task) {
        if (err) { 
            return next(err); 
        } else if (task.length === 0 && removable) {
            return res.status(404).json({'message': 'Task is not found'});
        } else {
            removable = 0;
            if(task.reminders.length == 0)
                return res.status(204).json({'message': 'There are no Reminders to remove'});
            else{
                task.reminders = null;
                task.save();
                res.status(200).json('All Reminders are successfully removed');
            }
        }
    });
});

module.exports = router;
