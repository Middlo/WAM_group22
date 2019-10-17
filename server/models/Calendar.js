var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var calendarSchema = new Schema({
    userId: Schema.Types.ObjectId,
    targetDate: { type: Date, required: true},
    viewType: { type: String } //how to display the calender
});

module.exports = mongoose.model('Calendar', calendarSchema);