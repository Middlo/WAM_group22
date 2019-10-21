var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();
var User = require('../models/User');

// Create a new user
router.post('/', function(req, res, next) {
    if(req.body.username && req.body.password && req.body.email){
        var user = new User(req.body);
        user.save(function(err) {
            if (err) { return next(err); }
            res.status(201).json({"user" : user});
        });
    } else {
        res.status(400).json({"message":'Necessary data is not provided.'});
    }
});

// Return list of all users
router.get('/', function(req, res, next) {
    User.find(function(err, users) {
        if (err) { 
            return next(err); 
        } else if (users.length === 0)
            res.status(200).json({"users": [], "message" : 'There are no Users registered'});
        else {
            res.status(200).json({"users": users});
        }
    });
});

// Return a user with a given ID
router.get('/:userId', function(req, res, next) {
    var id = req.params.userId;
    //var mongoose = require('mongoose');
    //if(mongoose.Types.ObjectId.isValid(id)){
        User.findById({_id: id}, function(err, user) {
            if (err) { return next(err); }
            if (user === null) {
                return res.status(404).json({"user": null, 'message': 'User is not found'});
            }
            res.status(200).json({"user": user});
        });
    //} return res.status(400).json({'message': 'user id is not relevant ObjectId'});
    
});

// Update a whole user with a given ID
router.put('/:userId', function(req, res, next) {
    var id = req.params.userId;
    User.findById({_id: id}, function(err, user) {
        if (err) { return next(err); }
        if (user === null) {
            return res.status(404).json({"message": "User is not found"});
        }

        if(req.body.username || req.body.password || req.body.userType || req.body.email || req.body.authenticated){

            user.username = req.body.username;
            user.password = req.body.password;
            user.email = req.body.email;
            user.authenticated = req.body.authenticated;
            user.userType = req.body.userType;

            user.save();
            res.status(200).json({"user" : user});
        } else {
            res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
        }
    });
});

//Patch a user with a given ID
router.patch('/:userId', function(req, res, next) {
    var id = req.params.userId;
    //if(mongoose.Types.ObjectId.isValid(id)){
        User.findById({_id: id}, function(err, user) {
            if (err) { return next(err); }
            if (user === null) {
                return res.status(404).json({"message": "User is not found"});
            }

        if(req.body.username || req.body.password || req.body.userType || req.body.email || req.body.authenticated){
                
                user.username = (req.body.username || user.username);
                user.password = (req.body.password || user.password);
                user.email = (req.body.email || user.email);
                user.authenticated = (req.body.authenticated || user.authenticated);
                user.userType = (req.body.userType || user.userType);
                
                user.save();
                res.status(200).json({"user" : user});
            } else {
                res.status(400).json({"message":'The request data does not have valid keys or is empty.'});
            }
        });
    //} return res.status(400).json({'message': 'user id is not relevant ObjectId'});
});

//Delete a user with a given ID
router.delete('/:userId', function(req, res, next) {
    var id = req.params.userId;
    User.findOneAndDelete({_id: id}, function(err, user) {
        if (err) { return next(err); }
        if (user === null) {
            return res.status(404).json({'message': 'User is not found'});
        }
        res.status(200).json({"message" : 'Success'});
    });
});

// Delete all users
router.delete('/', function(req, res, next) {
    var removable = 1; //to limit the display of a no user msg after removals
    
    User.find(function(err, users) {
        if (err) { 
            return next(err); 
        } else if (users.length === 0 && removable){
            res.status(204).json({"message" : 'There are no Users to be deleted'});
        } else {
            removable = 0;

            for(var i = 0; i < users.length; i++ ){
                User.findByIdAndRemove({_id : users[i]._id}, function(err, user){
                    if (err) { return next(err); }
                });
            }
            res.status(200).json({"message" : 'Success'});
        }
    });
});

module.exports = router;