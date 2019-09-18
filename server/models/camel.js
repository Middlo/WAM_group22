var mongoose = require('mongoose');
var CamelName = require('../models/CamelName');
var Schema = mongoose.Schema;

var camelSchema = new Schema({
    color: { type: String },
    position: { type: Number },
    names: {type: []}
});

module.exports = mongoose.model('camels', camelSchema);
