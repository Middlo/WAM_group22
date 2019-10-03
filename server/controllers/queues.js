var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Queue = require('../models/Queue');
var sort = req.require.date;

// Create a new queue
router.post('/', function(req, res, next) {
    if(req.body.elements){
        var queue = new Queue(req.body);
        queue.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json({"message" : 'Queue is Successfully created'});
        });
    } else {
        res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
    }
});

// Return list of all queues
router.get('/', function(req, res, next) {
    Queue.find(function(err, queues) {
        if (err) { 
            return next(err); 
        } else if (queues.length === 0)
            res.status(200).json({"message" : 'There are no Queues registered'});
        else {
            res.status(200).json({'Queues': queues});
        }
    });
});

// Return a queue with a given ID
router.get('/:queueId', function(req, res, next) {
    var id = req.params.queueId;
    Queue.findById(id, function(err, queue) {
        if (err) { return next(err); }
        if (queue === null) {
            return res.status(404).json({'message': 'Queue is not found'});
        }
        res.status(200).json(queue);
    });
});

if(sort){
    find({}).sort().exec(function(err, queues){
        if (err) {return next(err);}
        else {
            router.get('/', function(req, res, next) {
                Queue.find(function(err, queues) {
                    if (err) { 
                        return next(err); 
                    } else if (queues.length === 0)
                        res.status(200).json({"message" : 'There are no Queues registered'});
                    else {
                        res.status(200).json({'Queues': queues});
                    }
                });
            });
        }
    })
};

// Update a whole queue with a given ID
router.put('/:queueId', function(req, res, next) {
    var id = req.params.queueId;
    Queue.findById(id, function(err, queue) {
        if (err) { return next(err); }
        if (queue === null) {
            return res.status(404).json({"message": "Queue is not found"});
        }

        if(req.body.elements){

            queue.elements = req.body.elements;

            queue.save();
            res.status(200).json({"message" : 'Queue is successfully updated (put)', queue});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
    });
});

//Patch a queue with a given ID
router.patch('/:queueId', function(req, res, next) {
    var id = req.params.queueId;
    Queue.findById(id, function(err, queue) {
        if (err) { return next(err); }
        if (queue === null) {
            return res.status(404).json({"message": "Queue is not found"});
        }

        if(req.body.elements){
            
            queue.elements = (req.body.elements || queue.elements);
            
            queue.save();
            res.status(200).json({"message" : 'Queue is successfully updated (patch)', queue});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
    });
});

//Delete a queue with a given ID
router.delete('/:queueId', function(req, res, next) {
    var id = req.params.queueId;
    Queue.findOneAndDelete({_id: id}, function(err, queue) {
        if (err) { return next(err); }
        if (queue === null) {
            return res.status(404).json({'message': 'Queue is not found'});
        }
        res.status(200).json({"message" : 'Queue is successfully removed'});
    });
});

// Delete all queues
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no queue msg after removals
    
    Queue.find(function(err, queues) {
        if (err) { 
            return next(err); 
        } else if (queues.length === 0 && removable){
            res.status(204).json({'message':'There are no Queues to be deleted'});
        } else {
            removable = 0;

            for(var i = 0; i < queues.length; i++ ){
                Queue.findByIdAndRemove({_id : queues[i].id}, function(err, queue){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json('All Queues are removed');
        }
    });
});

module.exports = router;