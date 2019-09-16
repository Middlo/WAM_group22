var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var eventSchema = new Schema({
    //user.username,
    //id: { type: String },
    title: { type: Number },
    description: { type: description},
    startDate: {type: Date},
    endDate: {type: Date}

});

module.exports = mongoose.model('events', eventSchema);
