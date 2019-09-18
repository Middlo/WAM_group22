var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var camelNameSchema = new Schema({
    name: {type: []}
});

module.exports = mongoose.model('camelNames', camelNameSchema);
