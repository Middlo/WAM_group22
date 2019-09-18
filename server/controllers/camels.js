var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Camel = require('../models/Camel');
var Customer = require('../models/Customer');
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

// Create a new customer
router.post('/:camelId/customers', function(req, res, next) {
    var id = req.params.camelId;

    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }
        const newCustomer = new Customer({
            _id: new mongoose.Types.ObjectId(),
            fullName: req.body.fullName
          });

        newCustomer.save(function(err2, addedCustomer){
            if (err2) { return next(err2)};
            camel.customers.push(addedCustomer);
            camel.save();
            res.status(201).json(camel);
        });
    });
});
    
        
// Return list of all customers of a camel
router.get('/:camelId/customers', function(req, res, next) {
    var id = req.params.camelId;

    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        } else if (camel.customers.length === 0)
            return res.status(201).json({'message': 'The camel has yet not a registered customer'});
        res.status(201).json(camel.customers);
    });
});


// Return a specific customer of a camel
router.get('/:camelId/customers/:customerId', function(req, res, next) {
    var camId = req.params.camelId;
    var cusId = req.params.customerId;

    Camel.findById(camId, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }
        Customer.findById({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }
            res.status(201).json(foundCustomer);
        });
    });
});

// Delete a specific customer of a camel
router.delete('/:camelId/customers/:customerId', function(req, res, next) {
    var camId = req.params.camelId;
    var cusId = req.params.customerId;

    Camel.findById(camId, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'The camel is not registered'});
        }
        Customer.deleteMany({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }
            camel.save();
            res.status(201).json(camel);
        });
    });
});


// Delete all customers of a camel (REVISION Needed)
router.delete('/:camelId/customers', function(req, res, next) {
    var camId = req.params.camelId;

    var removable = 1; //to limit the display of a no camel msg after removals

    Camel.findById(camId, function(err, camel) {
        if (err) { 
            return next(err); 
        } else if (camel.length === 0 && removable) {
            return res.status(404).json({'message': 'Camel not found'});
        } else {
            removable = 0;
            if(camel.customers.length == 0)
                return res.json({'message': 'There are no customers to remove'});
            else{
                for(var i = 0; i < camel.customers.length; i++){
                    Customer.findByIdAndRemove({_id: camel.customers[i]._id}, function(err3){
                        if (err3) { return next(err3); }
                    });
                }
                res.json('Existing customers are removed');
            }
        }
        /*Customer.deleteMany({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }
            camel.save();
            res.status(201).json(camel);
        });*/
    });
});

module.exports = router;
