var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var reminderSchema = new Schema({
    userId: Schema.Types.ObjectId,
    topic: { type: String },
    targetMoment: { type: Date },
    remindBefore: { type: String },
    reminderFor: { type: String },
    importanceLevel: { type: String },

});

module.exports = mongoose.model('Reminder', reminderSchema);