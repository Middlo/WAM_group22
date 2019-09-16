var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var queueSchema = new Schema({
    //task.id
});

module.exports = mongoose.model('queues', queueSchema);
