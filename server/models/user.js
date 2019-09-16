var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
    username: { type: String, required: true },
    password: { type: String, required: true },
    type: {type: Boolean}
});

module.exports = mongoose.model('users', userSchema);