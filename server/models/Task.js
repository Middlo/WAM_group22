var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var taskSchema = new Schema({
    //userID: { type: String, required: true },
    //_id: Schema.Types.ObjectId,
    userId: Schema.Types.ObjectId,
    taskTitle: { type: String },
    taskDescription: { type: String },
    importanceLevel: { type: String },
    deadline: { type: Date },   // format example "2019-11-12T14:39:00.000Z"
    //reminder: { type: Schema.Types.ObjectId, ref: 'Reminder' }
});

module.exports = mongoose.model('Task', taskSchema);
