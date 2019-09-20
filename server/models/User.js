var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
    //userId: Schema.Types.ObjectId,
    username: { type: String },
    password: { type: String },
    userType: {type: String}
});

module.exports = mongoose.model('User', userSchema);
