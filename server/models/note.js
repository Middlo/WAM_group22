var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var noteSchema = new Schema({
    taskID: { type: Number },
    noteText: { type: String }
});

module.exports = mongoose.model('notes', noteSchema);