var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var reminderSchema = new Schema({
    //userID: { type: String, required: true },
    _id: Schema.Types.ObjectId,
    targerMoment: { type: Date },
    remindBefore: { type: [Number] },
});

module.exports = mongoose.model('Reminder', reminderSchema);