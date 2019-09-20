var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var calendarSchema = new Schema({
    //userId: Schema.Types.ObjectId,
    currentMonth: { type: String },
    currentDate: { type: String },
    viewType: { type: String } //how to display the calender
});

module.exports = mongoose.model('Calendar', calendarSchema);