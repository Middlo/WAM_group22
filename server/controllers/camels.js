var express = require('express');
var router = express.Router();
var Camel = require('../models/Camel');
var allCamels = [];



// Create a new camel
router.post('/', function(req, res, next) {
    var camel = new Camel(req.body);
    camel.save(function(err) {
        if (err) { return next(err); }
        res.status(201).json(camel);
    });
});

// Return list of all camels
router.get('/', function(req, res, next) {
    Camel.find(function(err, camels) {
        if (err) { 
            return next(err); 
        } else if (camels.length === 0)
            res.json('There are no camels registered');
        else {
            res.json({'camels': camels});
        }
    });
});

// Return a camel with a given ID
router.get('/:id', function(req, res, next) {
    var id = req.params.id;
    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'Camel not found'});
        }
        res.status(201).json(camel);
    });
});


//Patch a camel with a given ID
router.patch('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel == null) {
            return res.status(404).json({"message": "Camel not found"});
        }
        camel.color = (req.body.color || camel.color);
        camel.position = (req.body.position || camel.position);
        camel.save();
        res.json(camel);
    });
});


//Delete a camel with a given ID
router.delete('/:id', function(req, res, next) {
    var id = req.params.id;
    Camel.findOneAndDelete({_id: id}, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'Camel not found'});
        }
        res.json(camel);
    });
});


// Delete all camels
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no camel msg after removals
    
    Camel.find(function(err, camels) {
        if (err) { 
            return next(err); 
        } else if (camels.length === 0 && removable){
            res.json('There are no camels to be deleted');
        } else {
            removable = 0;

            for(var i = 0; i < camels.length; i++ ){
                Camel.findByIdAndRemove({_id : camels[i].id}, function(err, camel){
                    if (err) { return next(err); }
                });
            }
            res.json('All camels are removed');
        }
    });
});

module.exports = router;
