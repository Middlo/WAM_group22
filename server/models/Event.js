var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var eventSchema = new Schema({
    //user.username,
    //id: { type: String },
    userId: Schema.Types.ObjectId,
    title: { type: String },
    description: { type: String},
    startDate: { type: Date, required: true},
    endDate: { type: Date, required: true},
    //hasReminder: { type: Boolean }

});

module.exports = mongoose.model('Event', eventSchema);
