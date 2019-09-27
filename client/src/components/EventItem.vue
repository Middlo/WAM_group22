<template>
  <div class="events">
    <b-list-group-item  :style="{ color: event.color }">
      <img alt="Event" src="../assets/event.jpg">
      Event title: {{ event.title }}   |
      <b-button v-b-toggle.collapse1 variant="primary" data-parent="#accordion" @click="$emit('allow-collapse', event._id)">Details</b-button>
      <b-button type="button" class="close" @click="$emit('delete-event', event._id)">&times;</b-button>
      <div class="collapsable">
        <b-collapse id="collapse1" class="mt-2">
          <b-card>
            <p class="card-text1">Description : {{ event.description }}</p>
            <p class="card-text2">Start Date : {{ event.startDate }}</p>
            <p class="card-text3">End Date: {{ event.endDate }}</p>
            <b-button v-b-toggle.collapse1-inner size="sm" @click="$emit('show-reminder', event._id)">Show Reminders</b-button>
            <b-collapse id="collapse1-inner" class="mt-2">
              <div class="eventReminders">
                <b-button type="button" class="createEventReminder" @click="createEventReminder()">Create Reminder</b-button>
                <b-list-group>
                  <event-reminder-item v-for="reminder in event.reminders" v-bind:key="event.reminders._id" :reminder="reminder" @delete-eventReminder="deleteEventReminder"></event-reminder-item>
                </b-list-group>
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
import ReminderItem from '@/components/ReminderItem'

export default {
  name: 'event-item',
  props: ['event'],
  data() {
    return {
      events: []
    }
  },
  mounted() {
    this.getEvents()
  },
  methods: {
    getEvents() {
      Api.get('/events')
        .then(reponse => {
          this.event.reminders = reponse.data.reminders
        })
        .catch(error => {
          this.events = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    deleteEventReminder(id) {
      Api.delete(`/events/${event._id}/reminders/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.events.findIndex(event => event._id === id)
          this.events.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
    },
    createEventReminder() {
      var topic1 = "To be assigned"
      var targetMoment1 = Date()
      var remindBefore1 = "the main event"
      

      var randomEvent = {
        topic : topic1,
        targetMoment : targetMoment1,
        remindBefore : remindBefore1
      }
      Api.post(`/events/${event._id}/reminders/`, randomEvent)
        .then(response => {
          this.events.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
    }
  },
  components: {
    ReminderItem
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
