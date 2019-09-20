var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Reminder = require('../models/Reminder');
var Customer = require('../models/Customer');

// Create a new reminder
router.post('/', function(req, res, next) {

    if(req.body.topic || req.body.targerMoment || req.body.remindBefore){

        var reminder = new Reminder(req.body);
        reminder.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json(reminder);
        });    
    } else {
        res.json('The request data does not have valid keys or is empty');
    }
    
});

// Return list of all reminders
router.get('/', function(req, res, next) {
    Reminder.find(function(err, reminders) {
        if (err) { 
            return next(err); 
        } else if (reminders.length === 0)
            res.json('There are no reminders registered');
        else {
            res.json({'reminders': reminders});
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
        res.status(201).json(reminder);
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

        if(req.body.topic || req.body.targerMoment || req.body.remindBefore){

            reminder.topic = req.body.topic;
            reminder.targerMoment = req.body.targerMoment;
            reminder.remindBefore = req.body.remindBefore;
    
            reminder.save();
            res.json(reminder);
        } else {
            res.json('The request data does not have valid keys or is empty');
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

        if(req.body.topic || req.body.targerMoment || req.body.remindBefore){

            reminder.topic = (req.body.topic || reminder.topic);
            reminder.targerMoment = (req.body.targerMoment || reminder.targerMoment);
            reminder.remindBefore = (req.body.remindBefore || reminder.remindBefore);
    
            reminder.save();
            res.json(reminder);
        } else {
            res.json('The request data does not have valid keys or is empty');
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
        res.json(reminder);
    });
});


// Delete all reminders
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no reminder msg after removals
    
    Reminder.find(function(err, reminders) {
        if (err) { 
            return next(err); 
        } else if (reminders.length === 0 && removable){
            res.json('There are no Reminders to be deleted');
        } else {
            removable = 0;

            for(var i = 0; i < reminders.length; i++ ){
                Reminder.findByIdAndRemove({_id : reminders[i].id}, function(err, reminder){
                    if (err) { return next(err); }
                });
            }
            res.json('All Reminders are removed');
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
                return res.json({'message': 'There are no Items to remove'});
            else{
                reminder.items = [];
                reminder.save();
                res.json('Existing Items are removed');
            }
        }
    });
});
*/

module.exports = router;
