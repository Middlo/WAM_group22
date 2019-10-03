<template>
    <div>
        <div class="queue">
            <h2>Queue</h2>
            <b-card
              title="Priority 1"
              tag="article"
              style="max-width: 20rem;"
              class="mb-2"
            >
            <b-card-text>
                Task details: Will look into the integration!
            </b-card-text>

            <b-button href="#" variant="primary">Clear Task</b-button>
            </b-card>
            <b-card
              title="Priority 2"
              tag="article"
              style="max-width: 20rem;"
              class="mb-2"
            >
            <b-card-text>
                Task details: Will look into the integration!
            </b-card-text>

            <b-button href="#" variant="primary">Clear Task</b-button>
            </b-card>
            <b-card
              title="Priority 3"
              tag="article"
              style="max-width: 20rem;"
              class="mb-2"
            >
            <b-card-text>
                Task details: Will look into the integration!
            </b-card-text>

            <b-button href="#" variant="primary">Clear Task</b-button>
            </b-card>

            <!-- Add a button "Show More" that shows more tasks in queue -->

        </div>
        <div class="calender">
            <b-container class="weekDays">
                <b-row>
                    <b-col :style="{backgroundColor: 'lightGreen'}" <b-button @click="updateData(-1)">Prev</b-button></b-col>
                    <b-col>{{currentMonth}}</b-col>
                    <b-col :style="{backgroundColor: 'lightGreen'}" <b-button @click="updateData(1)">Next</b-button></b-col>
                </b-row>
            </b-container>
            <b-container class="weekDays">
                <b-row>
                    <b-col class="cal-days">Mo</b-col>
                    <b-col class="cal-days">Tu</b-col>
                    <b-col class="cal-days">We</b-col>
                    <b-col class="cal-days">Th</b-col>
                    <b-col class="cal-days">Fr</b-col>
                    <b-col class="cal-days">Sa</b-col>
                    <b-col class="cal-days">Su</b-col>
                </b-row>
            </b-container>

            <b-container class="WeekRow1" :style="{backgroundColor: 'lightblue'}">
                <b-row @mouseover="updateBKColor()" :style="{backgroundColor: (bkdclr || 'lightblue')}">
                    <b-col class="cal-date">r1c1</b-col>
                    <b-col class="cal-date">r1c2</b-col>
                    <b-col class="cal-date">r1c3</b-col>
                    <b-col class="cal-date">r1c4</b-col>
                    <b-col class="cal-date">r1c5</b-col>
                    <b-col class="cal-date">r1c6</b-col>
                    <b-col class="cal-date">r1c7</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">r2c1</b-col>
                    <b-col class="cal-date">r2c2</b-col>
                    <b-col class="cal-date">r2c3</b-col>
                    <b-col class="cal-date">r2c4</b-col>
                    <b-col class="cal-date">r2c5</b-col>
                    <b-col class="cal-date">r2c6</b-col>
                    <b-col class="cal-date">r2c7</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">r3c1</b-col>
                    <b-col class="cal-date">r3c2</b-col>
                    <b-col class="cal-date">r3c3</b-col>
                    <b-col class="cal-date">r3c4</b-col>
                    <b-col class="cal-date">r3c5</b-col>
                    <b-col class="cal-date">r3c6</b-col>
                    <b-col class="cal-date">r3c7</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">r4c1</b-col>
                    <b-col class="cal-date">r4c2</b-col>
                    <b-col class="cal-date">r4c3</b-col>
                    <b-col class="cal-date">r4c4</b-col>
                    <b-col class="cal-date">r4c5</b-col>
                    <b-col class="cal-date">r4c6</b-col>
                    <b-col class="cal-date">r4c7</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">r5c1</b-col>
                    <b-col class="cal-date">r5c2</b-col>
                    <b-col class="cal-date">r5c3</b-col>
                    <b-col class="cal-date">r5c4</b-col>
                    <b-col class="cal-date">r5c5</b-col>
                    <b-col class="cal-date">r5c6</b-col>
                    <b-col class="cal-date">r5c7</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">r6c1</b-col>
                    <b-col class="cal-date">r6c2</b-col>
                    <b-col class="cal-date">r6c3</b-col>
                    <b-col class="cal-date">r6c4</b-col>
                    <b-col class="cal-date">r6c5</b-col>
                    <b-col class="cal-date">r6c6</b-col>
                    <b-col class="cal-date">r6c7</b-col>
                </b-row>
            </b-container>
        </div>
        <div class="toolbar">
            <b-button>toolbar</b-button>
        </div>
    </div>
</template>

<script>
var bkdclr = 'lightBlue'
var currentMonth = 'Aug 2019' // updateData(0)// Date().substring(4,7) //'Aug 2019'
var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

export default {
  data() {
    return {
      bkdclr,
      currentMonth
    }
  }, /*,  mounted() {
    this.updateData(0)
  } */
  methods: {
    updateBKColor() {
      this.bkdclr === 'red'
    }
  },
  updateData(direction) {
    // Tue Oct 01 2019 01:42:41
    var todayDate = Date()
    var currMonth = todayDate.substring(4, 7)
    var currYear = todayDate.substring(11, 15)
    var currDate = todayDate.substring(8, 10)

    var displayMonth = this.changeMonth(currYear, currMonth, direction)
    this.currentMonth = displayMonth
    return displayMonth
  },
  changeMonth(yr, mon, dir) {
    var newMonthNum = this.getMonthNum(mon) + dir
    if (newMonthNum === 0) {
      return 'Dec ' + (Integer.parse(yr) - 1)
    } else if (newMonthNum === 13) {
      return 'Jan ' + (Integer.parse(yr) + 1)
    } else { return this.getMonthTxt(newMonthNum) + yr }
  },
  getMonthNum(mon) {
    for (var i = 0; i < 12; i++) {
      if (months[i] === mon) { return i + 1 }
    }
  },
  getMonthTxt(mon) {
    return months[mon - 1]
  },
  getDayOfFirstDate(yr, mon) {
    var txt = Date('01-10-2019')
    // Date()..........................
    console.log('the day is ' + txt)
  }
}
</script>

<style>
.queue{
    text-align: center;
    width: 20%;
    border-style:solid;
    float: left;
}
.calender{
    margin-left: 20%;
    border-style:solid;
}
.toolbar{
    margin-left: 20%;
    border-style:solid;
}
.cal-days{
    background-color: rgb(62, 173, 112);
}
.cal-date{
    background-color: rgb(118, 211, 195);
}
</style>
