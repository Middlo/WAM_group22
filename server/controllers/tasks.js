var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Task = require('../models/Task');
//var Item = require('../models/Item');

// Create a new task
router.post('/', function(req, res, next) {
    var task = new Task(req.body);
    task.save(function(err) {
        if (err) { return next(err); }
        res.status(201).json(task);
    });
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

        task.taskTitle = req.body.taskTitle;
        task.taskDescription = req.body.taskDescription;
        task.importanceLevel = req.body.importanceLevel;
        task.deadline = req.body.deadline;
        task.remminder = req.body.remminder;

        task.save();
        res.json(task);
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

        task.taskTitle = (req.body.taskTitle || task.taskTitle);
        task.taskDescription = (req.body.taskDescription || task.taskDescription);
        task.importanceLevel = (req.body.importanceLevel || task.importanceLevel);
        task.deadline = (req.body.deadline || task.deadline);
        task.remminder = (req.body.remminder || task.remminder);

        task.save();
        res.json(task);
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


/*
// Create a new item
router.post('/:taskId/items', function(req, res, next) {
    var id = req.params.taskId;

    Task.findById(id, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        const newItem = new Item({
            _id: new mongoose.Types.ObjectId(),
            fullName: req.body.fullName
          });

        newItem.save(function(err2, addedItem){
            if (err2) { return next(err2)};
            task.items.push(addedItem);
            task.save();
            res.status(201).json(task);
        });
    });
});
    
        
// Return list of all items of a task
router.get('/:taskId/items', function(req, res, next) {
    var id = req.params.taskId;

    Task.findById(id, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        } else if (task.items.length === 0)
            return res.status(201).json({'message': 'The Task has yet not a registered Item'});
        res.status(201).json(task.items);
    });
});


// Return a specific item of a task
router.get('/:taskId/items/:itemId', function(req, res, next) {
    var tasId = req.params.taskId;
    var itmId = req.params.itemId;

    Task.findById(tasId, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        Item.findById({_id : itmId}, function(err2, foundItem) {
            if (err2) { return next(err2); }
            if (foundItem === null) {
                return res.status(404).json({'message': 'Item is not registered for the Task'});
            }
            res.status(201).json(foundItem);
        });
    });
});

// Delete a specific item of a task
router.delete('/:taskId/items/:itemId', function(req, res, next) {
    var tasId = req.params.taskId;
    var itmId = req.params.itemId;

    Task.findById(tasId, function(err, task) {
        if (err) { return next(err); }
        if (task === null) {
            return res.status(404).json({'message': 'The Task is not registered'});
        }
        Item.deleteMany({_id : itmId}, function(err2, foundItem) {
            if (err2) { return next(err2); }
            if (foundItem === null) {
                return res.status(404).json({'message': 'Item is not registered for the Task'});
            }
            task.save();
            res.status(201).json(task);
        });
    });
});


// Delete all items of a task (REVISION Needed)
router.delete('/:taskId/items', function(req, res, next) {
    var tasId = req.params.taskId;

    var removable = 1; //to limit the display of a no task msg after removals

    Task.findById(tasId, function(err, task) {
        if (err) { 
            return next(err); 
        } else if (task.length === 0 && removable) {
            return res.status(404).json({'message': 'Task is not found'});
        } else {
            removable = 0;
            if(task.items.length == 0)
                return res.json({'message': 'There are no Items to remove'});
            else{
                for(var i = 0; i < task.items.length; i++){
                    Item.findByIdAndRemove({_id: task.items[i]._id}, function(err3){
                        if (err3) { return next(err3); }
                    });
                }
                res.json('Existing items are removed');
            }
        }
        /*Item.deleteMany({_id : tasId}, function(err2, foundItem) {
            if (err2) { return next(err2); }
            if (foundItem === null) {
                return res.status(404).json({'message': 'Item is not registered for the Task'});
            }
            task.save();
            res.status(201).json(task);
        });
    });
});
*/

module.exports = router;
