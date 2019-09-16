var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var taskSchema = new Schema({
    //userID: { type: String, required: true },
    taskID: { type: Number, required: true },
    taskTitle: { type: String },
    taskDescription: { type: String },
    importanceLevel: { type: Number },
    deadline: { type: Date },
    remminder: {type: Number}
});

module.exports = mongoose.model('tasks', taskSchema);