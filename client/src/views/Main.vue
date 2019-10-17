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
            <b-container class="monthChoice">
                <b-row>
                    <b-col :style="{backgroundColor: 'lightGreen'}" @click="updateData(-1)">Prev</b-col>
                    <b-col>{{currentMonth}}</b-col>
                    <b-col :style="{backgroundColor: 'lightGreen'}"  @click="updateData(1)">Next</b-col>
                </b-row>
            </b-container>
            <b-container class="weekDays">
                <b-row>
                    <b-col class="cal-days">M</b-col>
                    <b-col class="cal-days">Tu</b-col>
                    <b-col class="cal-days">W</b-col>
                    <b-col class="cal-days">Th</b-col>
                    <b-col class="cal-days">F</b-col>
                    <b-col class="cal-days">Sa</b-col>
                    <b-col class="cal-days">Su</b-col>
                </b-row>
            </b-container>

            <b-container class="WeekRow1" :style="{backgroundColor: 'lightblue'}">
                <b-row @mouseover="updateBKColor()" :style="{backgroundColor: (bkdclr || 'lightblue')}">
                    <b-col class="cal-date">11</b-col>
                    <b-col class="cal-date">12</b-col>
                    <b-col class="cal-date">13</b-col>
                    <b-col class="cal-date">14</b-col>
                    <b-col class="cal-date">15</b-col>
                    <b-col class="cal-date">16</b-col>
                    <b-col class="cal-date">17</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">21</b-col>
                    <b-col class="cal-date">22</b-col>
                    <b-col class="cal-date">23</b-col>
                    <b-col class="cal-date">24</b-col>
                    <b-col class="cal-date">25</b-col>
                    <b-col class="cal-date">26</b-col>
                    <b-col class="cal-date">27</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">31</b-col>
                    <b-col class="cal-date">32</b-col>
                    <b-col class="cal-date">33</b-col>
                    <b-col class="cal-date">34</b-col>
                    <b-col class="cal-date">35</b-col>
                    <b-col class="cal-date">36</b-col>
                    <b-col class="cal-date">37</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">41</b-col>
                    <b-col class="cal-date">42</b-col>
                    <b-col class="cal-date">43</b-col>
                    <b-col class="cal-date">44</b-col>
                    <b-col class="cal-date">45</b-col>
                    <b-col class="cal-date">46</b-col>
                    <b-col class="cal-date">47</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">51</b-col>
                    <b-col class="cal-date">52</b-col>
                    <b-col class="cal-date">53</b-col>
                    <b-col class="cal-date">54</b-col>
                    <b-col class="cal-date">55</b-col>
                    <b-col class="cal-date">56</b-col>
                    <b-col class="cal-date">57</b-col>
                </b-row>
            </b-container>
            <b-container class="WeekRow2" :style="{backgroundColor: 'lightblue'}">
                <b-row>
                    <b-col class="cal-date">61</b-col>
                    <b-col class="cal-date">62</b-col>
                    <b-col class="cal-date">63</b-col>
                    <b-col class="cal-date">64</b-col>
                    <b-col class="cal-date">65</b-col>
                    <b-col class="cal-date">66</b-col>
                    <b-col class="cal-date">67</b-col>
                </b-row>
            </b-container>
        </div>
        <div class="toolbar" @click="getEntities(toolBarDate)">
            <b-button>Select Date</b-button>

            <b-container>
              <b-row>
                <b-col class="detailView" >
                  <p v-show="currentContent === 0">Selected date has no registered elements</p>

                  <b-list-group>
                    <calendar-item v-for="calendar in partCal" :key="calendar._id" :calendar="calendar" @delete-calendar="deleteCalendar" @calendar-content-changed="contentChanged"></calendar-item>
                  </b-list-group>

                  <b-list-group>
                    <event-item v-for="event in partEvt" :key="event._id" :event="event" @delete-event="deleteEvent" @event-content-changed="contentChanged"></event-item>
                  </b-list-group>

                  <b-list-group>
                    <task-item v-for="task in partTsk" :key="task._id" :task="task" @delete-task="deleteTask" @task-content-changed="contentChanged"></task-item>
                  </b-list-group>

                  <b-list-group>
                    <note-item v-for="note in partNot" :key="note._id" :note="note" @delete-note="deleteNote" @note-content-changed="contentChanged"></note-item>
                  </b-list-group>

                  <b-list-group>
                    <reminder-item v-for="reminder in partRem" :key="reminder._id" :reminder="reminder" @delete-reminder="deleteReminder" @reminder-content-changed="contentChanged"></reminder-item>
                  </b-list-group>

                  <b-row class="createViews" cols="3" md="auto">
                    <b-button id="addCal" @click="addCalendar()">Add Calendars</b-button>
                    <b-button id="addCal" @click="addEvent()">Add Events</b-button>
                    <b-button id="addCal" @click="addTask()">Add Tasks</b-button>
                    <b-button id="addCal" @click="addNote()">Add Notes</b-button>
                    <b-button id="addCal" @click="addReminder()">Add Reminders</b-button>
                  </b-row>
                </b-col>

                <b-col class="allViews" cols="3" md="auto">
                  <b-button variant="primary" v-show="calendars.length">All Calendars</b-button>
                  <b-button variant="primary" v-show="events.length">All Events</b-button>
                  <b-button variant="primary" v-show="tasks.length">All Tasks</b-button>
                  <b-button variant="primary" v-show="notes.length">All Notes</b-button>
                  <b-button variant="primary" v-show="reminders.length">All Reminders</b-button>
                </b-col>
              </b-row>
            </b-container>
        </div>
    </div>
</template>

<script>
import { Api } from '@/Api'
import CalendarItem from '@/components/CalendarItem'
import EventItem from '@/components/EventItem'
import TaskItem from '@/components/TaskItem'
import NoteItem from '@/components/NoteItem'
import ReminderItem from '@/components/ReminderItem'

var bkdclr = 'lightBlue'
var currentMonth = 'Aug 2019' // updateData(0)// Date().substring(4,7) //'Aug 2019'
var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
var toolBarDate = sessionStorage.getItem('selectedDate')
var todayIS = (new Date()).toLocaleDateString()
var currentContent = 0

export default {
  name: 'Main',
  data() {
    return {
      bkdclr,
      currentMonth,
      calendars: [],
      events: [],
      tasks: [],
      notes: [],
      reminders: [],
      queues: [],
      toolBarDate,
      partCal: [],
      partEvt: [],
      partTsk: [],
      partNot: [],
      partRem: [],
      partQue: [],
      currentContent
    }
  },
  mounted() {
    this.getCalendars()
    this.getEvents()
    this.getTasks()
    this.getNotes()
    this.getReminders()
    this.getQueues()
  },
  methods: {
    getCalendars() {
      Api.get('/calendars')
        .then(reponse => {
          this.calendars = reponse.data.calendars
        })
        .catch(error => {
          this.calendars = []
          console.log(error)
          this.networkMsg = 'Check Connection to Database'
        })
        .then(() => {
          // This code is always executed (after success or error).

          // console.log('today is ' + todayIS)
          localStorage.setItem('currentDate', todayIS)
          // var dd = todayIS.getDate();
          // var mm = todayIS.getMonth()+1;
          // var yyyy = todayIS.getFullYear();
        })
    },
    getEvents() {
      Api.get('/events')
        .then(reponse => {
          this.events = reponse.data.events
        })
        .catch(error => {
          this.events = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    getTasks() {
      Api.get('/tasks')
        .then(reponse => {
          this.tasks = reponse.data.tasks
        })
        .catch(error => {
          this.tasks = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    getNotes() {
      Api.get('/notes')
        .then(reponse => {
          this.notes = reponse.data.notes
        })
        .catch(error => {
          this.notes = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    getReminders() {
      Api.get('/reminders')
        .then(reponse => {
          this.reminders = reponse.data.reminders
        })
        .catch(error => {
          this.reminders = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
          if (!toolBarDate) {
            sessionStorage.setItem('selectedDate', todayIS)
            toolBarDate = todayIS
          }

          localStorage.setItem('selectedDate', toolBarDate)
          this.getEntities(toolBarDate)
        })
    },
    getQueues() {
      Api.get('/queues')
        .then(reponse => {
          this.queues = reponse.data.queues
        })
        .catch(error => {
          this.queues = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    getEntities(forDate) { // for tool bar
      // var forDate = "2019-10-09"
      this.toolBarDate = forDate
      sessionStorage.setItem('selectedDate', forDate)
      // localStorage.setItem("selectedDate", forDate)
      console.log('showing details of ' + forDate)
      // console.log('original Cal has details of ' + this.calendars.length)
      this.partCal = []
      this.partEvt = []
      this.partTsk = []
      this.partNot = []
      this.partRem = []
      // this.partQue = []

      for (var i = 0; i < this.calendars.length; i++) {
        if (this.calendars[i].targetDate.substring(0, 10) === forDate) { this.partCal.push(this.calendars[i]) }
      }

      // console.log('calendar has contents of ' + this.partCal.length)

      for (i = 0; i < this.events.length; i++) {
        if (this.events[i].startDate.substring(0, 10) === forDate || this.events[i].endDate.substring(0, 10) === forDate) { this.partEvt.push(this.events[i]) }
      }

      // console.log('Event has contents of ' + this.partEvt.length)

      for (i = 0; i < this.tasks.length; i++) {
        if (this.tasks[i].deadline.substring(0, 10) === forDate) { this.partTsk.push(this.tasks[i]) }
      }

      // console.log('Task has contents of ' + this.partTsk.length)

      for (i = 0; i < this.notes.length; i++) {
        if (this.notes[i].lastUpdated.substring(0, 10) === forDate || this.notes[i].createdOn.substring(0, 10) === forDate) { this.partNot.push(this.notes[i]) }
      }

      // console.log('Note has contents of ' + this.partNot.length)

      for (i = 0; i < this.reminders.length; i++) {
        if (this.reminders[i].targetMoment.substring(0, 10) === forDate) { this.partRem.push(this.reminders[i]) }
      }

      this.currentContent = this.partCal.length + this.partEvt.length + this.partTsk.length + this.partNot.length + this.partRem.length
      console.log('current selection content is ' + this.currentContent)
      // console.log('Reminder has contents of ' + this.partRem.length)
      /*
      for (var i = 0; i < this.queues.length; i++){
        if(this.queues[i].someDate === forDate)
          this.partQue.push(this.queues[i])
      } */
    },
    addCalendar() {
      var randomCalendar = {
        targetDate: (this.toolBarDate || todayIS)
      }
      console.log('on date ' + randomCalendar.targetDate)
      Api.post('/calendars', randomCalendar)
        .then(response => {
          this.calendars.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    deleteCalendar(id) {
      Api.delete(`/calendars/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.calendars.findIndex(calendar => calendar._id === id)
          this.calendars.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    addEvent() {
      var randomEvent = {
        title: 'To be edited',
        startDate: new Date((this.toolBarDate || todayIS)),
        endDate: new Date((this.toolBarDate || todayIS)) // for example
      }
      Api.post('/events', randomEvent)
        .then(response => {
          this.events.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    deleteEvent(id) {
      Api.delete(`/events/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.events.findIndex(event => event._id === id)
          this.events.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    addTask() {
      var randomTask = {
        taskTitle: 'Testing the task',
        importanceLevel: '1',
        deadline: (this.toolBarDate || todayIS)
      }
      // console.log(randomTask)
      Api.post('/tasks', randomTask)
        .then(response => {
          this.tasks.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    deleteTask(id) {
      Api.delete(`/tasks/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.tasks.findIndex(task => task._id === id)
          this.tasks.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    addNote() {
      console.log('date is ' + todayIS)
      var randomNote = {
        topic: 'To be edited',
        textContent: 'content...'
      }
      Api.post('/notes', randomNote)
        .then(response => {
          this.notes.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    deleteNote(id) {
      Api.delete(`/notes/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.notes.findIndex(note => note._id === id)
          this.notes.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    addReminder() {
      var randomReminder = {
        topic: 'To be edited',
        targetMoment: (this.toolBarDate || todayIS)
      }
      Api.post('/reminders', randomReminder)
        .then(response => {
          this.reminders.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.contentChanged()
          // This code is always executed (after success or error).
        })
    },
    deleteReminder(id) {
      Api.delete(`/reminders/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.reminders.findIndex(reminder => reminder._id === id)
          this.reminders.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
    },
    contentChanged() { // for tool bar
      this.calendars = []
      this.getCalendars()
      this.events = []
      this.getEvents()
      this.tasks = []
      this.getTasks()
      this.notes = []
      this.getNotes()
      this.reminders = []
      this.getReminders()
    },
    updateBKColor() { // for calendar ?
      this.bkdclr = 'red'
    },
    updateData(direction) { // for calendar ?
    // Tue Oct 01 2019 01:42:41
      var todayDate = Date()
      var currMonth = todayDate.substring(4, 7)
      var currYear = todayDate.substring(11, 15)
      // var currDate = todayDate.substring(8, 10)

      var displayMonth = this.changeMonth(currYear, currMonth, direction)
      this.currentMonth = displayMonth
      return displayMonth
    },
    changeMonth(yr, mon, dir) { // for calendar ?
      var newMonthNum = this.getMonthNum(mon) + dir
      if (newMonthNum === 0) {
        return 'Dec ' + (parseInt(yr) - 1)
      } else if (newMonthNum === 13) {
        return 'Jan ' + (parseInt(yr) + 1)
      } else { return this.getMonthTxt(newMonthNum) + yr }
    },
    getMonthNum(mon) { // for calendar ?
      for (var i = 0; i < 12; i++) {
        if (months[i] === mon) { return i + 1 }
      }
    },
    getMonthTxt(mon) { // for calendar ?
      return months[mon - 1]
    },
    getDayOfFirstDate(yr, mon) { // for calendar ?
      var txt = Date('01-10-2019')
      // Date()..........................
      console.log('the day is ' + txt)
    }
  },
  components: {
    CalendarItem,
    EventItem,
    TaskItem,
    NoteItem,
    ReminderItem
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
.weekDays{
    background-color: rgb(214, 86, 176);

}
.cal-days{
    background-color: rgb(62, 173, 112);
}
.cal-date{
    background-color: rgb(118, 211, 195);
}
.toolbar{
  margin: 3px;
}
.detailView{
  width: 50%;
}
.allViews button{
  display: block;
  margin: 3px;
}
.createViews button{
  display: block;
  margin: 3px;
}
</style>
