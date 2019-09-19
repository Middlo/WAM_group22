var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var taskSchema = new Schema({
    //userID: { type: String, required: true },
    //_id: Schema.Types.ObjectId,
    userId: Schema.Types.ObjectId,
    taskTitle: { type: String },
    taskDescription: { type: String },
    importanceLevel: { type: Number },
    deadline: { type: Date },   // format example "2019-11-12T14:39:00.000Z"
    remminder: {type: Number}
});

module.exports = mongoose.model('Task', taskSchema);
