var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var reminderSchema = new Schema({
    //userID: { type: String, required: true },
    _id: Schema.Types.ObjectId,
    userId: Schema.Types.ObjectId,
    topic: { type: String },
    targetMoment: { type: Date },
    remindBefore: { type: String },
});

module.exports = mongoose.model('Reminder', reminderSchema);