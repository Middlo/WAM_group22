var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var reminderSchema = new Schema({
    //userID: { type: String, required: true },
    userId: Schema.Types.ObjectId,
    topic: { type: String },
    targerMoment: { type: Date },
    remindBefore: { type: [Number] },
});

module.exports = mongoose.model('Reminder', reminderSchema);