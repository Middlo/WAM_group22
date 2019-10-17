var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var noteSchema = new Schema({
    //_id: Schema.Types.ObjectId,
    //taskID: { type: Number },
    userId: Schema.Types.ObjectId,
    topic: { type: String },
    textContent: { type: String },
    createdOn: { type: Date },
    lastUpdated: { type: Date, default: Date.now } // format example "2019-11-12T14:39:00.000Z"
});

module.exports = mongoose.model('Note', noteSchema);
