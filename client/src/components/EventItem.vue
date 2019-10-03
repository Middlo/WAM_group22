<template>
  <b-list-group-item class="mainCard">
    <img alt="Event" src="../assets/event.jpg">
    Event title: {{ event.title }}
    <b-button class="detailButton" variant="primary" @click="showDetail(event._id)">Details</b-button>
    <b-button class="close" @click="$emit('delete-event', event._id)">&times;</b-button>
    <div class="collapsable">
      <b-card id="collapse1" class="firstCard" v-show="allowPart1 === event._id">
        <div>
          <p class="firstDetail">Event Code : {{ event._id }}</p>
          <b-form id=form1 class="form">
            <b-form-group
              id="input-group-0"
              label="Event Title:"
              label-for="input-0"
            >
              <b-form-input
                id="input-0"
                v-model="editform.title"
                :placeholder="event.title"
              ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-1"
              label="Event Description:"
              label-for="input-1"
            >
              <b-form-input
                id="input-1"
                v-model="editform.description"
                :placeholder="event.description"
              ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-2"
              label="Event Start Date:"
              label-for="input-2"
            >
              <b-form-input
                id="input-2"
                v-model="editform.startDate"
                required
                :placeholder="event.startDate.substring(0,10)"
              ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-3"
              label="Event End Date:"
              label-for="input-3"
            >
              <b-form-input
                id="input-3"
                v-model="editform.endDate"
                required
                :placeholder="event.endDate.substring(0,10)"
              ></b-form-input>
            </b-form-group>
          </b-form>
          <div>
            <b-button class="putButton" @click="putData (event._id, editform), $emit('event-content-changed', event._id)">Update Event Entirely</b-button>  |
            <b-button class="patchButton" @click="patchData (event._id, editform), $emit('event-content-changed', event._id)">Update Event Partially</b-button>
          </div>

          <b-button class="reminderDetailBtn" @click="showRemDetail(event._id)">Reminder Details</b-button>
          <b-card id="collapse1-inner" class="secondCard" v-show="allowPart2 === event._id">
            <b-button class="createButton" @click="createEventReminder(event, event._id), $emit('event-content-changed', event._id)">Create Reminder</b-button>
            <div>
              <b-list-group-item class="reminderDetail" v-for="reminder in reminders" v-bind:key="reminder._id">
                <b-button class="close" @click="deleteEventReminder(event._id, reminder._id), $emit('event-content-changed', event._id)">&times;</b-button>
                <p class="secondDetail">Reminder ID : {{ reminder._id }}</p>

                <b-form id=form2 class="form">
                  <b-form-group
                    id="input-group-a"
                    label="Reminder Topic:"
                    label-for="input-a"
                  >
                    <b-form-input
                      id="input-a"
                      v-model="editReminderForm.topic"
                      :placeholder="reminder.topic"
                    ></b-form-input>
                  </b-form-group>

                  <b-form-group
                    id="input-group-b"
                    label="Reminder targer Moment:"
                    label-for="input-b"
                  >
                    <b-form-input
                      id="input-b"
                      v-model="editReminderForm.targetMoment"
                      :placeholder="reminder.targetMoment"
                    ></b-form-input>
                  </b-form-group>

                  <b-form-group
                    id="input-group-c"
                    label="Remind Before Minutes:"
                    label-for="input-c"
                  >
                    <b-form-input
                      id="input-c"
                      v-model="editReminderForm.remindBefore"
                      :placeholder=reminder.remindBefore
                    ></b-form-input>
                  </b-form-group>
                </b-form>
                <b-button class="putButton" @click="putRemData (event._id, reminder._id, editReminderForm), $emit('event-content-changed', event._id)">Update Reminder Entirely</b-button>  |
                <b-button class="patchButton" @click="patchRemData (event._id, reminder._id, editReminderForm), $emit('event-content-changed', event._id)">Update Reminder Partially</b-button>
              </b-list-group-item>
            </div>
          </b-card>
        </div>
      </b-card>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowPart1 = ''
var allowPart2 = ''
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
        endDate: ''
      },
      editReminderForm: {
        topic: '',
        targetMoment: '',
        remindBefore: ''
      }
    }
  },
  mounted() {
    this.getEvents()
  },
  methods: {
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
    showDetail(evtID) {
      this.currentEventID = evtID
      if (!this.allowPart1) { this.allowPart1 = evtID } else if (this.allowPart1 === evtID) { this.allowPart1 = '' } else { this.allowPart1 = evtID }
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
      var newReminder = {
        topic: 'To be Assigned',
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
  margin-top: 20px;
  margin-bottom: 10px;
}
.reminderDetail{
  background-color: rgba(198, 218, 189, 0.979);
}
.putButton{
  background-color: rgba(87, 61, 129, 0.979);
}
.patchButton{
  background-color: rgba(81, 98, 153, 0.979);
}
</style>
