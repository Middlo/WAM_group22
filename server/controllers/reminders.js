var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Reminder = require('../models/Reminder');
var Customer = require('../models/Customer');

// Create a new reminder
router.post('/', function(req, res, next) {

    if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminderFor || req.body.importanceLevel){

        var reminder = new Reminder(req.body);
        
        reminder._id = new mongoose.Types.ObjectId();
        
        reminder.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json({"reminder" : reminder}); // json({"message" : 'Reminder Successfully created'});
        });    
    } else {
        res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
    }
    
});

// Return list of all reminders
router.get('/', function(req, res, next) {
    Reminder.find(function(err, reminders) {
        if (err) { 
            return next(err); 
        } else if (reminders.length === 0)
        res.status(200).json({"reminders": [], "message" : 'There are no Reminders registered'});
        else {
            res.status(200).json({"reminders": reminders});
        }
    });
});

// Return a reminder with a given ID
router.get('/:reminderId', function(req, res, next) {
    var id = req.params.reminderId;
    Reminder.findById(id, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder === null) {
            return res.status(404).json({'message': 'Reminder not found'});
        }
        res.status(200).json({"reminder": reminder});
    });
});

// Return a reminders of an Entity
router.get('/entityReminders/:entityId', function(req, res, next) {
    var id = req.params.entityId;
    Reminder.find({reminderFor : id}, function(err, foundReminder) {
        if (err) { return next(err); }
        if (foundReminder === null) {
            return res.status(404).json({'message': 'Reminder not found'});
        }
        res.status(200).json({"reminder": foundReminder});
    });
});

// Update a whole reminder with a given ID
router.put('/:reminderId', function(req, res, next) {
    var id = req.params.reminderId;
    Reminder.findById(id, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder == null) {
            return res.status(404).json({"message": "Reminder not found"});
        }

        if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminderFor || req.body.importanceLevel){

            reminder.topic = req.body.topic;
            reminder.targetMoment = req.body.targetMoment;
            reminder.remindBefore = req.body.remindBefore;
            reminder.reminderFor = req.body.reminderFor;
            reminder.importanceLevel = req.body.importanceLevel;
    
            reminder.save();
            res.status(200).json({"reminder" : reminder}); // json({"message" : 'Reminder successfully updated (put)', reminder});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
    });
});


// Patch a reminder with a given ID
router.patch('/:reminderId', function(req, res, next) {
    var id = req.params.reminderId;
    Reminder.findById(id, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder == null) {
            return res.status(404).json({"message": "Reminder is not found"});
        }

        if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminderFor || req.body.importanceLevel){

            reminder.topic = (req.body.topic || reminder.topic);
            reminder.targetMoment = (req.body.targetMoment || reminder.targetMoment);
            reminder.remindBefore = (req.body.remindBefore || reminder.remindBefore);
            reminder.reminderFor = (req.body.reminderFor || reminder.reminderFor);
            reminder.importanceLevel = (req.body.importanceLevel || reminder.importanceLevel);
    
            reminder.save();
            res.status(200).json({"reminder" : reminder}); // json({"message" : 'Reminder successfully updated (patch)', reminder});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
        
    });
});


// Delete a reminder with a given ID
router.delete('/:reminderId', function(req, res, next) {
    var id = req.params.reminderId;
    Reminder.findOneAndDelete({_id: id}, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder === null) {
            return res.status(404).json({'message': 'Reminder is not found'});
        }
        res.status(200).json({"message" : 'Success'});
    });
});


// Delete all reminders
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no reminder msg after removals
    
    Reminder.find(function(err, reminders) {
        if (err) { 
            return next(err); 
        } else if (reminders.length === 0 && removable){
            res.status(204).json({"message" : 'There are no Reminders to be deleted'});
        } else {
            removable = 0;

            for(var i = 0; i < reminders.length; i++ ){
                Reminder.findByIdAndRemove({_id : reminders[i]._id}, function(err, reminder){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json({"message" :'Success'});
        }
    });
});

/*
// Create a new item
router.post('/:reminderId/items', function(req, res, next) {
    var id = req.params.reminderId;

    Reminder.findById(id, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder === null) {
            return res.status(404).json({'message': 'The Reminder is not registered'});
        }
        const newItem = new Item({
            _id: new mongoose.Types.ObjectId(),
            //fullName: req.body.fullName
          });

        newItem.save(function(err2, addedItem){
            if (err2) { return next(err2)};
            reminder.items.push(addedItem);
            reminder.save();
            res.status(201).json(reminder);
        });
    });
});
    
        
// Return list of all items of a reminder
router.get('/:reminderId/items', function(req, res, next) {
    var id = req.params.reminderId;

    Reminder.findById(id, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder === null) {
            return res.status(404).json({'message': 'The Reminder is not registered'});
        } else if (reminder.items.length === 0)
            return res.status(201).json({'message': 'The Reminder has yet not a registered Item'});
        res.status(201).json(reminder.items);
    });
});


// Return a specific item of a reminder
router.get('/:reminderId/items/:itemId', function(req, res, next) {
    var remId = req.params.reminderId;
    var itmId = req.params.itemId;

    Reminder.findById(remId, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder === null) {
            return res.status(404).json({'message': 'The Reminder is not registered'});
        }
        Item.findById({_id : itmId}, function(err2, foundItem) {
            if (err2) { return next(err2); }
            if (foundItem === null) {
                return res.status(404).json({'message': 'Item is not registered for the Reminder'});
            }
            res.status(201).json(foundItem);
        });
    });
});

// Delete a specific item of a reminder
router.delete('/:reminderId/items/:itemId', function(req, res, next) {
    var remId = req.params.reminderId;
    var itmId = req.params.itemId;

    Reminder.findById(remId, function(err, reminder) {
        if (err) { return next(err); }
        if (reminder === null) {
            return res.status(404).json({'message': 'The Reminder is not registered'});
        }
        Item.findById({_id : itmId}, function(err2, foundItem) {
            if (err2) { return next(err2); }
            if (foundItem === null) {
                return res.status(404).json({'message': 'Item is not registered for the Reminder'});
            }
            var updatedItems = [];
            
            for(var i = 0; i < reminder.items.length;i++){
                if(!(reminder.items[i] == itmId)){
                    updatedItems.push(reminder.items[i]);
                }    
            }

            reminder.items = updatedItems;
            reminder.save();
            res.status(201).json(reminder);
        });
    });
});


// Delete all items of a reminder
router.delete('/:reminderId/items', function(req, res, next) {
    var remId = req.params.reminderId;

    var removable = 1; //to limit the display of a no reminder msg after removals

    Reminder.findById(remId, function(err, reminder) {
        if (err) { 
            return next(err); 
        } else if (reminder.length === 0 && removable) {
            return res.status(404).json({'message': 'Reminder is not found'});
        } else {
            removable = 0;
            if(reminder.items.length == 0)
                return res.status(204).json({'message': 'There are no Items to remove'});
            else{
                reminder.items = [];
                reminder.save();
                res.status(200).json('Existing Items are removed');
            }
        }
    });
});
*/

module.exports = router;
