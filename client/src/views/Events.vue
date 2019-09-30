<template>
  <div class="events">
    <h1>List of {{ events.length }} events</h1>
    <b-button type="button" class="createButton" @click="createEvent()">Create Event</b-button>
    <b-list-group>
      <event-item v-for="event in events" :key="event._id" :event="event" @delete-event="deleteEvent" @event-content-changed="getEvents"></event-item>
    </b-list-group>
  </div>
</template>

<script>
import { Api } from '@/Api'
import EventItem from '@/components/EventItem'

export default {
  name: 'Events',
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
    },
    createEvent() {
      var tempYear = 2019
      var tempMonth = (this.getRandomInt(12) || 1)
      var tempDate = this.getRandomDay(tempMonth)
      var tempDay1 = tempYear + '-' + tempMonth + '-' + tempDate

      var randomEvent = {
        startDate: new Date(tempDay1)
      }
      Api.post('/events', randomEvent)
        .then(response => {
          this.events.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    getRandomInt(max) {
      var result = Math.floor(Math.random() * max)

      if (result === 0) { return '01' } else if (result < 10) { return '0' + result } else { return result }
    },
    getRandomDay(calMonth) {
      if (calMonth === ('10' || '12' || '01' || '03' || '05' || '07' || '08')) {
        return this.getRandomInt(31)
      } else if (calMonth === ('11' || '04' || '06' || '09')) {
        return this.getRandomInt(30)
      } else {
        return this.getRandomInt(28) // for simplicity max 28
      }
    }
  },
  components: {
    EventItem
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
a {
  color: #42b983;
}
.createButton {
  margin-bottom: 1em;
}
.events {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
