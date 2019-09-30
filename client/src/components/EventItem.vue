<template>
  <div class="events">
          <b-list-group-item>
            <img alt="Event" src="../assets/event.jpg">
            Event title: {{ event.title }}  |
            <b-button  variant="primary" @click="getReminders(event._id, event), showDetail(event._id)">Details</b-button>
            <b-button type="button" class="close" @click="$emit('delete-event', event._id)">&times;</b-button>
            <div class="collapsable">
              <b-card id="collapse1" v-show="events.allowPart1 === event._id">
                <div>
                  <p class="card-text0">Event Code : {{ event._id }}</p>
                  <p class="card-text1">Description : {{ event.description }}</p>
                  <p class="card-text2">Start Date : {{ event.startDate }}</p>
                  <p class="card-text3">End Date: {{ event.endDate }}</p>
                  <b-button v-b-toggle.collapse1-inner size="sm">Reminder Details</b-button>
                  <b-collapse id="collapse1-inner" class="mt-2">
                    <b-button type="button" class="createEventReminder" @click="createEventReminder(event._id), $emit('event-content-changed', event._id)">Create Reminder</b-button>
                    <div>
                      <b-list-group-item v-for="reminder in reminders" v-bind:key="reminder._id">
                        <b-button type="button" class="close" @click="deleteEventReminder(event), $emit('event-content-changed', event._id)">&times;</b-button>
                        <p class="card-text10">Reminder ID : {{ reminder._id }}</p>
                        <p class="card-text11">Topic : {{ reminder.topic }}</p>
                        <p class="card-text12">Target Moment : {{ reminder.targetMoment }}</p>
                        <p class="card-text13">Remind Before : {{ reminder.remindBefore }}</p>
                      </b-list-group-item>
                    </div>
                  </b-collapse>
                </div>
              </b-card>
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
          this.events.allowPart1 = ''
          this.events.allowPart2 = ''
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
            if (reponse.data.reminder) {
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
            } else { this.reminders = [] }
          })
          .catch(error => {
            this.tempReminder = ''
            console.log(error)
          })
          .then(() => {
            // This code is always executed (after success or error).
          })
      } else { this.reminders = [] }
    },
    showDetail(evtID) {
      if (!this.events.allowPart1) { this.events.allowPart1 = evtID } else if (this.events.allowPart1 === evtID) { this.events.allowPart1 = '' } else { this.events.allowPart1 = evtID }
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
          this.getEvents()
        // This code is always executed (after success or error).
        })
    },
    deleteEventReminder(currentEvent) {
      Api.delete(`/reminders/${foundReminderID}`)
        .then(reponse => {
          if (reponse.data.message === 'Success') {
            foundReminderID = ''
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
                  // console.log('after reloding event ' + reponse.data.event._id)

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
        })
        .catch(error => {
          this.events = []
          console.log(error)
        })
        .then(() => {
          this.getEvents()
          // This code is always executed (after success or error).
        })
    }
  }
}
</script>

<style scoped>
.img {
  width: 33px;
  margin-right: 10px;
}
</style>
