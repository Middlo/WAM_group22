var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var eventSchema = new Schema({
    //user.username,
    //id: { type: String },
    userId: Schema.Types.ObjectId,
    title: { type: Number },
    description: { type: String},
    startDate: {type: Date},
    endDate: {type: Date}

});

module.exports = mongoose.model('Event', eventSchema);
