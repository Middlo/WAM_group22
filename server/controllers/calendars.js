var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Calendar = require('../models/Calendar');

// Create a new calendar
router.post('/', function(req, res, next) {
    if((req.body.currentMonth || req.body.currentDate || req.body.viewType)){
        var calendar = new Calendar(req.body);
        calendar.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json(calendar);//json({"message" : 'Calendar is Successfully created'});
        });
    } else {
        res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
    }
});

// Return list of all calendars
router.get('/', function(req, res, next) {
    Calendar.find(function(err, calendars) {
        if (err) { 
            return next(err); 
        } else if (calendars.length === 0)
            res.status(200).json({"message" : 'There are no calendars registered'});
        else {
            res.status(200).json({'Calendars': calendars});
        }
    });
});

// Return a calendar with a given ID
router.get('/:calendarId', function(req, res, next) {
    var id = req.params.calendarId;
    Calendar.findById(id, function(err, calendar) {
        if (err) { return next(err); }
        if (calendar === null) {
            return res.status(404).json({'message': 'Calendar is not found'});
        }
        res.status(200).json(calendar);
    });
});

// Update a whole calendar with a given ID
router.put('/:calendarId', function(req, res, next) {
    var id = req.params.calendarId;
    Calendar.findById(id, function(err, calendar) {
        if (err) { return next(err); }
        if (calendar === null) {
            return res.status(404).json({"message": "Calendar is not found"});
        }

        if(req.body.currentMonth || req.body.currentDate || req.body.viewType){

            calendar.currentMonth = req.body.currentMonth
            calendar.currentDate = req.body.currentDate;
            calendar.viewType = req.body.viewType;

            calendar.save();
            res.status(200).json({"message" : 'Calendar is successfully updated (put)', calendar});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
    });
});

//Patch a calendar with a given ID
router.patch('/:calendarId', function(req, res, next) {
    var id = req.params.calendarId;
    Calendar.findById(id, function(err, calendar) {
        if (err) { return next(err); }
        if (calendar === null) {
            return res.status(404).json({"message": "calendar is not found"});
        }

        if(req.body.currentMonth || req.body.currentDate || req.body.viewType){

            calendar.currentMonth = (req.body.currentMonth || calendar.currentMonth);
            calendar.currentDate = (req.body.currentDate || calendar.currentDate);
            calendar.viewType = (req.body.viewType || calendar.viewType);

            calendar.save();
            res.status(200).json({"message" : 'Calendar successfully updated (patch)', calendar});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
    });
});

//Delete a calendar with a given ID
router.delete('/:calendarId', function(req, res, next) {
    var id = req.params.calendarId;
    Calendar.findOneAndDelete({_id: id}, function(err, calendar) {
        if (err) { return next(err); }
        if (calendar === null) {
            return res.status(404).json({'message': 'Calendar is not found'});
        }
        res.status(200).json({"message" : 'Calendar is successfully removed'});
    });
});

// Delete all calendars
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no calendar msg after removals
    
    Calendar.find(function(err, calendars) {
        if (err) { 
            return next(err); 
        } else if (calendars.length === 0 && removable){
            res.status(204).json('There are no Calendars to be deleted');
        } else {
            removable = 0;

            for(var i = 0; i < calendars.length; i++ ){
                Calendar.findByIdAndRemove({_id : calendars[i].id}, function(err, calendar){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json('All Calendars are removed');
        }
    });
});

module.exports = router;