var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var queueSchema = new Schema({
    //task.id
    userId: Schema.Types.ObjectId,
});

module.exports = mongoose.model('Queue', queueSchema);
