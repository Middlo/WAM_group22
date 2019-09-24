var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var Camel = require('../models/Camel');
var Customer = require('../models/Customer');

// Create a new camel
router.post('/', function(req, res, next) {

    if (req.body.color || req.body.position || req.body.customers){
        var camel = new Camel(req.body);
        camel.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json(camel); //{"message" : 'Camel Successfully created'}
        });    
    } else {
        res.status(400).json({"message" : 'The request data does not have valid keys or is empty'});
    }
    
});

// Return list of all camels
router.get('/', function(req, res, next) {
    Camel.find(function(err, foundCamels) {
        if (err) { 
            return next(err); 
        } else if (foundCamels.length === 0)
            res.status(200).json({"message" : 'There are no camels registered'});
        else {
            res.status(200).json({'camels': foundCamels});
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
        res.status(200).json(camel);
    });
});

// Update a whole camel with a given ID
router.put('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel == null) {
            return res.status(404).json({"message": "Camel not found"});
        }

        if(req.body.color || req.body.position || req.body.customers){
            
            camel.color = req.body.color;
            camel.position = req.body.position;
            camel.customers = req.body.customers;
    
            camel.save();
            res.status(200).json({"message" : 'Camel successfully updated (put)', camel});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
        
    });
});


// Patch a camel with a given ID
router.patch('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
    Camel.findById(id, function(err, camel) {
        if (err) { return next(err); }
        if (camel == null) {
            return res.status(404).json({"message": "Camel is not found"});
        }

        if(req.body.color || req.body.position || req.body.customers){

            camel.color = (req.body.color || camel.color);
            camel.position = (req.body.position || camel.position);
            camel.customers = (req.body.customers || camel.customers);
    
            camel.save();
            res.status(200).json({"message" : 'Camel successfully updated (patch)', camel});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
        
    });
});


// Delete a camel with a given ID
router.delete('/:camelId', function(req, res, next) {
    var id = req.params.camelId;
    Camel.findOneAndDelete({_id: id}, function(err, camel) {
        if (err) { return next(err); }
        if (camel === null) {
            return res.status(404).json({'message': 'Camel is not found'});
        }
        res.status(200).json({"message" : 'Camel successfully removed'});
    });
});


// Delete all camels
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no camel msg after removals
    
    Camel.find(function(err, camels) {
        if (err) { 
            return next(err); 
        } else if (camels.length === 0 && removable){
            res.status(204).json('There are no Camels to be deleted');
        } else {
            removable = 0;

            for(var i = 0; i < camels.length; i++ ){
                Camel.findByIdAndRemove({_id : camels[i].id}, function(err, camel){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json({"message" : 'All Camels are successfully removed'});
        }
    });
});


// Create a new customer
router.post('/:camelId/customers', function(req, res, next) {
    try {
        var id = req.params.camelId;

        Camel.findById(id, function(err, foundCamel) {
            if (err) { return next(err); }
            if (foundCamel === null) {
                return res.status(404).json({'message': 'Camel is not registered'});
            }

            if(req.body.fullName){
                const newCustomer = new Customer({
                    _id: new mongoose.Types.ObjectId(),
                    fullName: req.body.fullName,
                    camel : foundCamel._id
                });
        
                newCustomer.save(function(err2, addedCustomer){
                    if (err2) { return next(err2)};
                    foundCamel.customers.push(addedCustomer);
                    foundCamel.save();
                    res.status(201).json({"message": ("Customer " + addedCustomer.fullName + " is registered to Camel " + foundCamel._id)});
                });

            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            } 

        }).populate().exec();

    } catch (e) {
        res.status(404).json({ 'message': e });
    }
    
});
    
        
// Return list of all customers of a camel
router.get('/:camelId/customers', function(req, res, next) {
    var id = req.params.camelId;

    Camel.findById(id, function(err, foundCamel) {
        if (err) { return next(err); }
        if (foundCamel === null) {
            return res.status(404).json({'message': 'The Camel is not registered'});
        } else if (foundCamel.customers.length === 0)
            return res.status(204).json({'message': 'The Camel has yet not have a registered Customer'});
        res.status(200).json(foundCamel.customers);
    });
});


// Return a specific customer of a camel
router.get('/:camelId/customers/:customerId', function(req, res, next) {
    var camId = req.params.camelId;
    var cusId = req.params.customerId;

    Camel.findById(camId, function(err, foundCamel) {
        if (err) { return next(err); }
        if (foundCamel === null) {
            return res.status(404).json({'message': 'The Camel is not registered'});
        }
        Customer.findById({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }
            res.status(200).json(foundCustomer);
        });
    });
});


// Update part of specific customer of a camel
router.patch('/:camelId/customers/:customerId', function(req, res, next) {
    var camId = req.params.camelId;
    var cusId = req.params.customerId;

    Camel.findById(camId, function(err, foundCamel) {
        if (err) { return next(err); }
        if (foundCamel === null) {
            return res.status(404).json({'message': 'The Camel is not registered'});
        }
        Customer.findById({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }

            if(req.body.fullName){
    
                foundCustomer.fullName = (req.body.fullName || foundCustomer.fullName);
                
                foundCustomer.save();
                foundCamel.save();
                res.status(200).json({"message" : 'Customer detail successfully updated (patch)', "updated customer" : foundCustomer});
                
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }

        });
    });
});


// Update whole of specific customer of a camel
router.put('/:camelId/customers/:customerId', function(req, res, next) {
    var camId = req.params.camelId;
    var cusId = req.params.customerId;

    Camel.findById(camId, function(err, foundCamel) {
        if (err) { return next(err); }
        if (foundCamel === null) {
            return res.status(404).json({'message': 'The Camel is not registered'});
        }
        Customer.findById({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }

            if(req.body.fullName){
    
                foundCustomer.fullName = req.body.fullName;
                
                foundCustomer.save();
                foundCamel.save();
                res.status(200).json({"message" : 'Customer detail successfully updated (put)', "updated customer" : foundCustomer});
                
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }

        });
    });
});

// Delete a specific customer of a camel
router.delete('/:camelId/customers/:customerId', function(req, res, next) {
    var camId = req.params.camelId;
    var cusId = req.params.customerId;

    Camel.findById(camId, function(err, foundCamel) {
        if (err) { return next(err); }
        if (foundCamel === null) {
            return res.status(404).json({'message': 'The Camel is not registered'});
        }
        Customer.findById({_id : cusId}, function(err2, foundCustomer) {
            if (err2) { return next(err2); }
            if (foundCustomer === null) {
                return res.status(404).json({'message': 'Customer is not registered for the Camel'});
            }

            var updatedCustomers = [];
            
            for(var i = 0; i < foundCamel.customers.length;i++){
                if(!(foundCamel.customers[i] == cusId)){
                    updatedCustomers.push(foundCamel.customers[i]);
                }    
            }

            foundCamel.customers = updatedCustomers;
            foundCamel.save();
            res.status(200).json(foundCamel);
        });
    });
});


// Delete all customers of a camel
router.delete('/:camelId/customers', function(req, res, next) {
    var camId = req.params.camelId;

    var removable = 1; //to limit the display of a no camel msg after removals

    Camel.findById(camId, function(err, foundCamel) {
        if (err) { 
            return next(err); 
        } else if (foundCamel.length === 0 && removable) {
            return res.status(404).json({'message': 'Camel not found'});
        } else {
            removable = 0;
            if(foundCamel.customers.length == 0)
                return res.status(204).json({'message': 'There are no Customers to remove'});
            else{
                foundCamel.customers = [];
                foundCamel.save();
                res.status(200).json('Existing Customers are successfully removed');
            }
        }

    });
});

module.exports = router;
