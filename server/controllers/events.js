var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Event = require('../models/Event');
var Reminder = require('../models/Reminder');

// Create a new event
router.post('/', function(req, res, next) {
    if(req.body.title || req.body.description || req.body.startDate ||  req.body.endDate){
            
        var event = new Event(req.body);
        event.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json(event);//json({"message" : 'Event Successfully created'});
        });   
    } else {
        res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
    }
    
});


// Return list of all events
router.get('/', function(req, res, next) {
    Event.find(function(err, events) {
        if (err) { 
            return next(err); 
        } else if (events.length === 0)
            res.status(200).json({"message" : 'There are no Events registered'});
        else {
            res.status(200).json({'events': events});
        }
    });
});


// Return a event with a given ID
router.get('/:eventId', function(req, res, next) {
    var id = req.params.eventId;
    Event.findById(id, function(err, event) {
        if (err) { return next(err); }
        if (event === null) {
            return res.status(404).json({'message': 'Event not found'});
        }
        res.status(200).json(event);
    });
});


// Update a whole event with a given ID
router.put('/:eventId', function(req, res, next) {
    var id = req.params.eventId;
    Event.findById(id, function(err, event) {
        if (err) { return next(err); }
        if (event == null) {
            return res.status(404).json({"message": "Event not found"});
        }

        if(req.body.title || req.body.description || req.body.startDate ||  req.body.endDate ||  req.body.reminder){

            event.title = req.body.title;
            event.description = req.body.description;
            event.startDate = req.body.startDate;
            event.endDate = req.body.endDate;
            event.reminder = req.body.reminder;

            event.save();
            res.status(200).json({"event" : event}); // json({"message" : 'Event successfully updated (put)', event});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
        
    });
});


//Patch a event with a given ID
router.patch('/:eventId', function(req, res, next) {
    var id = req.params.eventId;
    Event.findById(id, function(err, event) {
        if (err) { return next(err); }
        if (event == null) {
            return res.status(404).json({"message": "Event is not found"});
        }

        if(req.body.title || req.body.description || req.body.startDate || 
            req.body.endDate || req.body.remminder){

            event.title = (req.body.title || event.title);
            event.description = (req.body.description || event.description);
            event.startDate = (req.body.startDate || event.startDate);
            event.endDate = (req.body.endDate || event.endDate);
            event.remminder = (req.body.remminder || event.remminder);
    
            event.save();
            res.status(200).json({"event" : event}); // json({"message" : 'Event successfully updated (patch)', event});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
        
    });
});


//Delete a event with a given ID
router.delete('/:eventId', function(req, res, next) {
    var id = req.params.eventId;
    Event.findOneAndDelete({_id: id}, function(err, event) {
        if (err) { return next(err); }
        if (event === null) {
            return res.status(404).json({'message': 'Event is not found'});
        }
        res.status(200).json({"message" : 'Event successfully removed'});
    });
});


// Delete all events
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no event msg after removals
    
    Event.find(function(err, events) {
        if (err) { 
            return next(err); 
        } else if (events.length === 0 && removable){
            res.status(204).json({"message" : 'There are no Events to be deleted'});
        } else {
            removable = 0;

            for(var i = 0; i < events.length; i++ ){
                Event.findByIdAndRemove({_id : events[i].id}, function(err, event){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json({"message" :'All Events are successfully removed'});
        }
    });
});


// Create a new reminder
router.post('/:eventId/reminders', function(req, res, next) {
    var id = req.params.eventId;

    Event.findById(id, function(err, event) {
        if (err) { return next(err); }
        if (event === null) {
            return res.status(404).json({'message': 'The Event is not registered'});
        }
        if(req.body.topic || req.body.targetMoment || req.body.remindBefore){
            var newReminder = new Reminder({
                //_id: new mongoose.Types.ObjectId(),
                topic: req.body.topic,
                targerMoment: req.body.targerMoment,
                remindBefore: req.body.remindBefore,
                reminderFor: id
            });

            newReminder.save(function(err2, addedReminder){
                if (err2) { return next(err2)};
                event.reminder = addedReminder;
                event.save();
                res.status(201).json({"event" : event}); // json({"message": ("Reminder " + addedReminder._id + " is registered to Event " + event._id)});
            });
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        } 
    });
});
    
        
// Return list of all reminders of a event
router.get('/:eventId/reminders', function(req, res, next) {
    var id = req.params.eventId;

    Event.findById(id, function(err, event) {
        if (err) { return next(err); }
        if (event === null) {
            return res.status(404).json({'message': 'The Event is not registered'});
        } else /*if (event.reminders.length === null)
            return res.status(204).json({'message': 'The Event has yet not a registered Reminder'}); */
        res.status(200).json({"reminder": event.reminder});
    });
});


// Return a specific reminder of a event
router.get('/:eventId/reminders/:reminderId', function(req, res, next) {
    var evtId = req.params.eventId;
    var remId = req.params.reminderId;

    Event.findById(evtId, function(err, event) {
        if (err) { return next(err); }
        if (event === null) {
            return res.status(404).json({'message': 'The Event is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Event'});
            }
            res.status(200).json({"reminders": foundReminder});
        });
    });
});


// Update part of specific reminder of a event
router.patch('/:eventId/reminders/:reminderId', function(req, res, next) {
    var evtId = req.params.eventId;
    var remId = req.params.reminderId;

    Event.findById(evtId, function(err, foundEvent) {
        if (err) { return next(err); }
        if (foundEvent === null) {
            return res.status(404).json({'message': 'The Event is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Event'});
            }

            if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminder){

                foundReminder.topic = req.body.topic;
                foundReminder.targetMoment = req.body.targetMoment;
                foundReminder.remindBefore = req.body.remindBefore;
                foundReminder.reminder = req.body.reminder

                foundReminder.save();
                foundEvent.save();
                res.status(200).json({"message" : 'Reminder detail successfully updated (patch)', "updated reminder" : foundReminder});
                
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }

        });
    });
});


// Update whole of specific reminder of a event
router.put('/:eventId/reminders/:reminderId', function(req, res, next) {
    var evtId = req.params.eventId;
    var remId = req.params.reminderId;

    Event.findById(evtId, function(err, foundEvent) {
        if (err) { return next(err); }
        if (foundEvent === null) {
            return res.status(404).json({'message': 'The Event is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Event'});
            }

            if(req.body.topic || req.body.targetMoment || req.body.remindBefore || req.body.reminder){

                foundReminder.topic = req.body.topic;
                foundReminder.targetMoment = req.body.targetMoment;
                foundReminder.remindBefore = req.body.remindBefore;
                foundReminder.reminder = req.body.reminder
                
                foundReminder.save();
                foundEvent.save();
                res.status(200).json({"message" : 'Reminder detail successfully updated (put)', "updated reminder" : foundReminder});
                
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }

        });
    });
});


// Delete a specific reminder of a event
router.delete('/:eventId/reminders/:reminderId', function(req, res, next) {
    var evtId = req.params.eventId;
    var remId = req.params.reminderId;

    Event.findById(evtId, function(err, event) {
        if (err) { return next(err); }
        if (event === null) {
            return res.status(404).json({'message': 'The Event is not registered'});
        }
        Reminder.findById({_id : remId}, function(err2, foundReminder) {
            if (err2) { return next(err2); }
            if (foundReminder === null) {
                return res.status(404).json({'message': 'Reminder is not registered for the Event'});
            }
            var updatedReminders = [];

            if(!(event.reminder == remId)){
                updatedReminders = event.reminder;
            }

            event.reminder = updatedReminders;
            event.save();
            res.status(200).json({"updated reminders list" : event.reminder});
        });
    });
});


// Delete all reminders of a event
router.delete('/:eventId/reminders', function(req, res, next) {
    var evtId = req.params.eventId;

    var removable = 1; //to limit the display of a no event msg after removals

    Event.findById(evtId, function(err, event) {
        if (err) { 
            return next(err); 
        } else if (event.length === 0 && removable) {
            return res.status(404).json({'message': 'Event is not found'});
        } else {
            removable = 0;
            if(event.reminder.length == 0)
                return res.status(204).json({'message': 'There are no Reminders to remove'});
            else{
                event.reminder = null;
                event.save();
                res.status(200).json('All Reminders are removed');
            }
        }
    });
});

module.exports = router;
