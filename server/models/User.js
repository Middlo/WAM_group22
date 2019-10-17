var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
    //userId: Schema.Types.ObjectId,
    username: { type: String, required: true },
    password: { type: String, required: true },
    email: { type: String, required: true },
    authenticated: { type: Boolean },
    userType: {type: String}
});

module.exports = mongoose.model('User', userSchema);
