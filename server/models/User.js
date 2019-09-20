var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
    //userId: Schema.Types.ObjectId,
    username: { type: String, required: true },
    password: { type: String, required: true },
    type: {type: Boolean}
});

module.exports = mongoose.model('User', userSchema);
