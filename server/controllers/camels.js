var express = require('express');
var router = express.Router();
var Camel = require('../models/Camel');
var CamelName = require('../models/CamelName');
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
router.get('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'Camel not found'});
        }
        res.status(201).json(camel);
    });
});

// Update a camel with a given ID
router.put('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel == null) {
            return res.status(404).json({"message": "Camel not found"});
        }
        camel.color = req.body.color;
        camel.position = req.body.position;
        camel.save();
        res.json(camel);
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
router.delete('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
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

// Create a new camel name
router.post('/:camelId/names', function(req, res, next) {
    var id = req.params.camelId;

    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }
        var newCamName = new CamelName();
        newCamName.name.push(req.body.name);
        newCamName.save(function(err){
            if (err) { return next(err); }
            //res.status(201).json(camel);
        });
        camel.names.push(newCamName);
        camel.save();
        res.status(201).json(camel);
    });
});


// Return list of all names of a camel
router.get('/:camelId/names', function(req, res, next) {
    var id = req.params.camelId;

    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }
        res.status(201).json(camel.names);
    });
});


// Return a specific name of a camel
router.get('/:camelId/names/:nameID', function(req, res, next) {
    var cId = req.params.camelId;
    var nId = req.params.nameID;

    Camel.findById(cId, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }
        CamelName.findById({_id : nId}, function(err2, camN) {
            if (err2) { return next(err2); }
            if (camN === null) {
                return res.status(404).json({'message': 'Camel Name is not registered'});
            }
            res.status(201).json(camN);
        });

        //res.status(201).json(camel.names);
    });
});

// Delete a specific name of a camel
router.delete('/:camelId/names/:nameID', function(req, res, next) {
    var cId = req.params.camelId;
    var nId = req.params.nameID;

    Camel.findById(cId, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }

        
        /*//var currentNames = [];
        var currentNames = camel.names;
        for(var i = 0; i < camel.names.length; i++){
            console.log('part ' + i + ' ' + currentNames);
            if(camel.names[i]._id === nId)
            camel.names
                currentNames.push(camel.names[i]);
                //camel.names
                //camel.names.findByIdAndRemove(nId);
                //delete camel.names[i];
        }
        camel.names = currentNames;
        camel.save();
        res.status(201).json(camel);*/
        CamelName.findOneAndRemove({_id: nId}, function(err2, name) {
            if (err2) { return next(err2); }
            if (name === null) {
                return res.status(404).json({'message': 'Camel Name is not registered'});
            }
            camel.save()
            res.status(201).json(camel);
        });
    });
});

module.exports = router;
