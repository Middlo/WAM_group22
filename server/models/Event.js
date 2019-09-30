var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var eventSchema = new Schema({
    //user.username,
    //id: { type: String },
    userId: Schema.Types.ObjectId,
    title: { type: String },
    description: { type: String},
    startDate: {type: Date},
    endDate: {type: Date},
    reminder: { type: Schema.Types.ObjectId, ref: 'Reminder' }

});

module.exports = mongoose.model('Event', eventSchema);
