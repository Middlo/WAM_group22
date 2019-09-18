var mongoose = require('mongoose');
var Schema = mongoose.Schema;

const customerSchema = Schema({
  _id: Schema.Types.ObjectId,
  fullName : {type: String, required: true}
});
  
module.exports = mongoose.model('Customer', customerSchema);
