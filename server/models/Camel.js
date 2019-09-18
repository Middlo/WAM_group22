var mongoose = require('mongoose');
//var Customer = require('../models/Customer');
var Schema = mongoose.Schema;

var camelSchema = new Schema({
    //_id: Schema.Types.ObjectId,
    color: { type: String },
    position: { type: Number },
    customers: [{ type: Schema.Types.ObjectId, ref: 'Customer' }]
});

module.exports = mongoose.model('Camel', camelSchema);
