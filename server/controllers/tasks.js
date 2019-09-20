var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Task = require('../models/Task');
var Reminder = require('../models/Reminder');

// Create a new task
router.post('/', function(req, res, next) {
    if(req.body.taskTitle || req.body.taskDescription || req.body.importanceLevel || req.body.deadline|| req.body.remminder){
        var task = new Task(req.body);
        task.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json(task);
        });
    } else {
        res.json('The request data does not have valid keys or is empty');
    }
});


// Return list of all tasks
router.get('/', function(req, res, next) {
    Task.find(function(err, tasks) {
        if (err) { 
            return next(err); 
        } else if (tasks.length === 0)
            res.json('There are no Tasks registered');
        else {
            res.json({'tasks': tasks});
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
        res.status(201).json(task);
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

        if(req.body.taskTitle || req.body.taskDescription || req.body.importanceLevel || req.body.deadline|| req.body.remminder){
            
            task.taskTitle = req.body.taskTitle;
            task.taskDescription = req.body.taskDescription;
            task.importanceLevel = req.body.importanceLevel;
            task.deadline = req.body.deadline;
            task.remminder = req.body.remminder;

            task.save();
            res.json(task);
        } else {
            res.json('The request data does not have valid keys or is empty');
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

        if(req.body.taskTitle || req.body.taskDescription || req.body.importanceLevel || req.body.deadline|| req.body.remminder){

            task.taskTitle = (req.body.taskTitle || task.taskTitle);
            task.taskDescription = (req.body.taskDescription || task.taskDescription);
            task.importanceLevel = (req.body.importanceLevel || task.importanceLevel);
            task.deadline = (req.body.deadline || task.deadline);
            task.remminder = (req.body.remminder || task.remminder);

            task.save();
            res.json(task);
            
        } else {
            res.json('The request data does not have valid keys or is empty');
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
        res.json(task);
    });
});


// Delete all tasks
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no task msg after removals
    
    Task.find(function(err, tasks) {
        if (err) { 
            return next(err); 
        } else if (tasks.length === 0 && removable){
            res.json('There are no Tasks to be deleted');
        } else {
            removable = 0;

            for(var i = 0; i < tasks.length; i++ ){
                Task.findByIdAndRemove({_id : tasks[i].id}, function(err, task){
                    if (err) { return next(err); }
                });
            }
            res.json('All Tasks are removed');
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
                _id: new mongoose.Types.ObjectId(),
                topic: req.body.topic,
                targetMoment: req.body.targetMoment,
                remindBefore: req.body.remindBefore
            });

            newReminder.save(function(err2, addedReminder){
                if (err2) { return next(err2)};
                foundTask.reminders.push(addedReminder);
                foundTask.save();
                res.status(201).json(foundTask);
            });
        } else {
            res.json('The request data does not have valid keys or is empty');
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
        } else if (foundTask.reminders.length === 0)
            return res.status(201).json({'message': 'The Task has yet not a registered Reminder'});
        res.status(201).json(foundTask.reminders);
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
            res.status(201).json(foundReminder);
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
            
            for(var i = 0; i < foundTask.reminders.length;i++){
                if(!(foundTask.reminders[i] == remId)){
                    updatedReminders.push(foundTask.reminders[i]);
                }    
            }

            foundTask.reminders = updatedReminders;
            foundTask.save();
            res.status(201).json(foundTask);
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
                return res.json({'message': 'There are no Reminders to remove'});
            else{
                task.reminders = [];
                task.save();
                res.json('Existing Reminders are removed');
            }
        }
    });
});

module.exports = router;
