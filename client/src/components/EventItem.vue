<template>
  <div class="events">
          <b-list-group-item>
            <img alt="Event" src="../assets/event.jpg">
            Event title: {{ event.title }}  |
            <b-button v-b-toggle.collapse1 variant="primary" data-parent="#accordion" @click="getReminders(event._id, event)">Details</b-button>
            <b-button type="button" class="close" @click="$emit('delete-event', event._id)">&times;</b-button>
            <div class="collapsable">
              <b-collapse id="collapse1" class="mt-2">
                <b-card>
                  <p class="card-text0">Event Code : {{ event._id }}</p>
                  <p class="card-text1">Description : {{ event.description }}</p>
                  <p class="card-text2">Start Date : {{ event.startDate }}</p>
                  <p class="card-text3">End Date: {{ event.endDate }}</p>
                  <b-button v-b-toggle.collapse1-inner size="sm" @click="$emit('show-reminder', event._id)">Show Reminders</b-button>
                  <b-collapse id="collapse1-inner" class="mt-2">
                    <div class="eventReminders">
                      <b-button type="button" class="createEventReminder" @click="createEventReminder(event._id), $emit('event-content-changed')">Create Reminder</b-button>
                      <div>
                        <b-list-group-item v-for="reminder in reminders" v-bind:key="reminder._id">
                          <b-button type="button" class="close" @click="deleteEventReminder(event), $emit('event-content-changed')">&times;</b-button>
                          <p class="card-text10">Reminder ID : {{ reminder._id }}</p>
                          <p class="card-text11">Topic : {{ reminder.topic }}</p>
                          <p class="card-text12">Target Moment : {{ reminder.targetMoment }}</p>
                          <p class="card-text13">Remind Before : {{ reminder.remindBefore }}</p>
                        </b-list-group-item>
                      </div>
                    </div>
                  </b-collapse>
                </b-card>
              </b-collapse>
            </div>
          </b-list-group-item>
        </div>
</template>

<script>
import { Api } from '@/Api'
var tempReminder
var foundReminderID

export default {
  name: 'event-item',
  props: ['event'],
  data() {
    return {
      events: [],
      reminders: [],
      tempReminder,
      foundReminderID
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
    getReminders(evtID, currentEvent) {
      if (currentEvent.reminder) {
        Api.get(`events/${evtID}/reminders`)
          .then(reponse => {
            this.tempReminder = reponse.data.reminder
            Api.get(`reminders/${reponse.data.reminder}`)
              .then(reponse => {
                var foundReminder = reponse.data.reminder
                this.reminders = []
                this.reminders.push(foundReminder)
                foundReminderID = foundReminder._id
              })
              .catch(error => {
                this.reminders = []
                console.log(error)
              })
              .then(() => {
                // This code is always executed (after success or error).
              })
          })
          .catch(error => {
            this.tempReminder = ''
            console.log(error)
          })
          .then(() => {
          // This code is always executed (after success or error).
          })
      }
    },
    createEventReminder(evtID) {
      var newReminder = {
        topic: 'To be Assigned',
        reminderFor: evtID
      }

      Api.post(`events/${evtID}/reminders`, newReminder)
        .then(reponse => {
          if (reponse.data.event) {
            console.log('new reminder ID is ' + reponse.data.event.reminder._id)
            this.getEvents()
          }
        })
        .catch(error => {
          this.reminders = []
          console.log(error)
        })
        .then(() => {
        // This code is always executed (after success or error).
        })
    },
    deleteEventReminder(currentEvent) {
      Api.delete(`/reminders/${foundReminderID}`)
        .then(reponse => {
          if (reponse.data.message === 'Success') {
            var newEvent = {
              _id: currentEvent._id,
              userId: currentEvent.userId,
              title: currentEvent.title,
              description: currentEvent.description,
              startDate: currentEvent.startDate,
              endDate: currentEvent.endDate
            }
            Api.put(`events/${currentEvent._id}/`, newEvent)
              .then(reponse => {
                if (reponse.data.event._id) {
                  console.log('after reloding event ' + reponse.data.event._id)
                  this.getEvents()
                }
              })
              .catch(error => {
                console.log(error)
              })
              .then(() => {
              // This code is always executed (after success or error).
              })
          }
        })
        .catch(error => {
          this.events = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    }
  }
}
</script>

<style scoped>
.collapsable {
  background-color: rgb(197, 51, 51);
}
.img {
  width: 33px;
  margin-right: 10px;
}
</style>
