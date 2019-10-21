<template>
  <b-list-group-item class="mainCard">
    <img alt="Event" src="../assets/event.jpg">
    <b-button class="close" @click="$emit('delete-event', event._id)">&times;</b-button>
    <p> Event: {{ event.title }} </p>
    <b-button class="detailButton" variant="primary" @click="showDetail(event._id), $emit('evaluate-collapse', event._id)">Details</b-button>
    <div class="collapsable">
      <b-container id="collapse1" class="firstCard" align-v="start" v-show="allowPart1 === event._id">

        <p id="firstDetail">Event Code : {{ event._id }}</p>
        <b-row>
          <b-col sm="3">
            <label for="input-0">Title     :</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-0" size="sm" v-model="editform.title" :placeholder="event.title"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="3">
            <label for="input-1">Description:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-1" size="sm" v-model="editform.description" :placeholder="event.description"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="3">
            <label description="Date" size="sm" for="input-2">Start:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-2" size="sm" required v-model="editform.startDate" :placeholder="event.startDate.substring(0,10)"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="3">
            <label description="Date" for="input-3">End:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-3" size="sm" required v-model="editform.endDate" :placeholder="event.endDate.substring(0,10)"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="3">
            <label for="input-4">Importance:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-4" size="sm" v-model="editform.importanceLevel" :placeholder="event.importanceLevel"></b-form-input>
          </b-col>
        </b-row>
        <div>
          <b-button id="putButton" @click="putData (event._id, editform), $emit('event-content-changed', event._id)">Update Event Entirely</b-button>
          <b-button id="patchButton" @click="patchData (event._id, editform), $emit('event-content-changed', event._id)">Update Event Partially</b-button>
        </div>

        <b-button class="reminderDetailBtn" @click="showRemDetail(event._id)">Reminder Details</b-button>
        <b-container id="collapse1-inner" class="secondCard" v-show="allowPart2 === event._id">
          <b-button v-show="reminders.length < 1" class="createButton" @click="createEventReminder(event, event._id), $emit('event-content-changed', event._id)">Create Reminder</b-button>

          <b-list-group-item id="reminderDetail" v-for="reminder in reminders" v-bind:key="reminder._id">
            <b-button class="close" @click="deleteEventReminder(event._id, reminder._id), $emit('event-content-changed', event._id)">&times;</b-button>
            <p class="secondDetail">Reminder ID : {{ reminder._id }}</p>

            <b-row>
              <b-col sm="4">
                <label  for="input-a">Topic:</label>
              </b-col>
              <b-col sm="7">
                <b-form-input id="input-a" size="sm" v-model="editReminderForm.topic" :placeholder="reminder.topic"></b-form-input>
              </b-col>
            </b-row>

            <b-row>
              <b-col sm="4">
                <label description="Reminder Target Moment" for="input-b">Target:</label>
              </b-col>
              <b-col sm="7">
                <b-form-input id="input-b" size="sm" v-model="editReminderForm.targetMoment" :placeholder="reminder.targetMoment.substring(0,10)" ></b-form-input>
              </b-col>
            </b-row>

            <b-row>
              <b-col sm="4">
                <label description="Remind Before Minutes" for="input-c">Minutes:</label>
              </b-col>
              <b-col sm="7">
                <b-form-input id="input-c" size="sm" v-model="editReminderForm.remindBefore" :placeholder="reminder.remindBefore" ></b-form-input>
              </b-col>
            </b-row>

            <b-row>
              <b-col sm="4">
                <label for="input-d">Importance: </label>
              </b-col>
              <b-col sm="7">
                <b-form-input id="input-d" size="sm" v-model="editReminderForm.importanceLevel" :placeholder="reminder.importanceLevel" ></b-form-input>
              </b-col>
            </b-row>

            <b-button id="putButton" @click="putRemData (event._id, reminder._id, editReminderForm), $emit('event-content-changed', event._id)">Update Reminder Entirely</b-button>
            <b-button id="patchButton" @click="patchRemData (event._id, reminder._id, editReminderForm), $emit('event-content-changed', event._id)">Update Reminder Partially</b-button>

          </b-list-group-item>
        </b-container>
      </b-container>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowPart1 = '' // localStorage.getItem('allowedEvent')
var allowPart2
var currentEventID
var remTrue = 0

export default {
  name: 'event-item',
  props: ['event'],
  data() {
    return {
      events: [],
      currentEventID,
      allowPart1,
      allowPart2,
      reminders: [],
      remTrue,
      editform: {
        title: '',
        description: '',
        startDate: '',
        endDate: '',
        importanceLevel: ''
      },
      editReminderForm: {
        topic: '',
        targetMoment: '',
        remindBefore: '',
        importanceLevel: ''
      }
    }
  },
  mounted() {
    this.getEvents()
  },
  methods: {
    getEvents() {
      // localStorage.setItem("displaying", this.allowPart1)
      // console.log('displaying ' + this.allowPart1)
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
    showDetail(evtID) {
      /* if (typeof(Storage) !== "undefined") {
        // Store
        localStorage.setItem("lastname", "Eyuell")
        // Retrieve
        console.log('stored data is ' + localStorage.getItem("lastname"))
      } else {
        console.log("Sorry, your browser does not support Web Storage...")
      } */
      // console.log('testing')
      // console.log('stored ' + allowPart1)
      this.currentEventID = evtID
      if (!this.allowPart1) { this.allowPart1 = evtID } else if (this.allowPart1 === evtID) { this.allowPart1 = '' } else { this.allowPart1 = evtID }
      /* if (this.allowPart1 === evtID) {
        localStorage.setItem('allowedEvent', '')
        this.allowPart1 = ''
        //console.log('same view has collapsed')
      } else if(this.allowPart1 === ''){
        this.allowPart1 = evtID
      }/*else {
        localStorage.setItem('allowedEvent', evtID)
        //this.events = []
        console.log('the other opened event should be closed now')
        //this.events = this.getEvents()
        this.allowPart1 = evtID
      }
      console.log('displaying ' + this.allowPart1)
      localStorage.setItem("displaying", this.allowPart1) */
    },
    showRemDetail(evtID) {
      this.currentEventID = evtID
      if (!this.allowPart2) { this.allowPart2 = evtID } else if (this.allowPart2 === evtID) { this.allowPart2 = '' } else { this.allowPart2 = evtID }
      Api.get(`events/${evtID}/reminders`)
        .then(reponse => {
          this.reminders = reponse.data.reminders
          remTrue = this.reminders.length
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    createEventReminder(event, evtID) {
      this.currentEventID = evtID
      var curDate = localStorage.getItem('selectedDate')
      var newReminder = {
        topic: 'To be Assigned',
        targetMoment: curDate,
        importanceLevel: event.importanceLevel,
        reminderFor: evtID
      }

      Api.post(`/reminders`, newReminder)
        .then(reponse => {
          if (reponse.data.reminder._id) {
            // console.log('new reminder ID is ' + reponse.data.reminder._id)

            this.getEvents()
          } else console.log(reponse.data.message)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getEvents()
        // This code is always executed (after success or error).
        })
    },
    deleteEventReminder(evtID, remID) {
      this.currentEventID = evtID
      Api.delete(`events/${evtID}/reminders/${remID}`)
        .then(reponse => {
          if (reponse.data.reminder._id) {
            // success
          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getEvents()
          // This code is always executed (after success or error).
        })
    },
    putData(id, form) {
      this.currentEventID = id
      // console.log('put is requested with ' + form + ' for ' + id)
      Api.put(`/events/${id}/`, form)
        .then(reponse => {
          if (reponse.data.event._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else {
            console.log(reponse.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getEvents()
          // This code is always executed (after success or error).
        })
    },
    patchData(id, form) {
      this.currentEventID = id
      // console.log('requested reminder is requested with ' + form + ' for ' + id)
      Api.patch(`/events/${id}/`, form)
        .then(reponse => {
          if (reponse.data.event._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getEvents()
          // This code is always executed (after success or error).
        })
    },
    putRemData(id, eventRemID, form) {
      this.currentEventID = id
      // console.log('put is requested with ' + form + ' for ' + id)
      if (eventRemID) {
        Api.put(`/reminders/${eventRemID}`, form)
          .then(reponse => {
            if (reponse.data.reminder._id) {
            // console.log('after reloding event ' + reponse.data.reminder._id)

            } else {
              console.log(reponse.data.message)
            }
          })
          .catch(error => {
            console.log(error)
          })
          .then(() => {
            this.getEvents()
          // This code is always executed (after success or error).
          })
      }
    },
    patchRemData(id, eventRemID, form) {
      this.currentEventID = id
      // console.log('reminder id is ' + eventRemID)
      if (eventRemID) {
        Api.patch(`/reminders/${eventRemID}`, form)
          .then(reponse => {
            if (reponse.data.reminder._id) {
            // console.log('after reloding event ' + reponse.data.reminder._id)

            } else { console.log(reponse.data.message) }
          })
          .catch(error => {
            console.log(error)
          })
          .then(() => {
            this.getEvents()
          // This code is always executed (after success or error).
          })
      }
    }
  }
}
</script>

<style scoped>
.img {
  width: 33px;
  margin-right: 10px;
  margin-bottom: 10px;
}
.mainCard{
  background-color: rgb(205, 212, 101);
  margin-bottom: 5px;
}
.detailButton{
  background-color: rgb(43, 116, 226);
  margin-left: 10px;
  margin-bottom: 10px;
}
.close{
  margin-top: 1px;
  width: 20px;
  height: 25px;
  background-color: rgb(221, 35, 35);
}
.createButton {
  margin-bottom: 1em;
  background-color: rgb(59, 199, 89)
}
.firstCard{
  background-color: rgba(190, 189, 218, 0.979);
}
.secondCard{
  background-color: rgba(198, 218, 189, 0.979);
}
.reminderDetailBtn{
  background-color: rgba(81, 151, 48, 0.979);
  margin-top: 20px;
  margin-bottom: 10px;
}
#reminderDetail{
  background-color: rgba(198, 218, 189, 0.979);
}
#putButton{
  background-color: rgba(87, 61, 129, 0.979);
  margin: 5px;
}
#patchButton{
  background-color: rgba(81, 98, 153, 0.979);
  margin: 5px;
}
#firstDetail {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
}
#collapse1{
  margin:auto;
}
</style>
