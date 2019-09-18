var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var calendarSchema = new Schema({
    currentMonth: { type: Date },
    currentDate: { type: Date },
    viewType: { type: Number }, //how to display the calender
});

module.exports = mongoose.model('Calendar', calendarSchema);