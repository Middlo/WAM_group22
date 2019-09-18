var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var camelNameSchema = new Schema({
    name: {type: []}
});

/*const camelNameSchema = Schema({
    author: { type: Schema.Types.ObjectId, ref: 'Person' },
    title: String,
    customers: [{ type: Schema.Types.ObjectId, ref: 'Person' }]
  });*/

module.exports = mongoose.model('camelNames', camelNameSchema);
