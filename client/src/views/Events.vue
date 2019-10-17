<template>
  <div class="events">
    <h1 v-if="events.length === 0"> There are no events registered </h1>
    <h1 v-else-if="events.length === 1"> There is one event </h1>
    <h1 v-else > There are {{ events.length }} events </h1>
    <b-button type="button" class="createButton" @click="createEvent()">Create Event</b-button>
    <b-button class="deleteButton" v-show="events.length" @click="deleteAllEvents()">Delete All Events</b-button>
    <b-list-group>
      <event-item v-for="event in events" :key="event._id" :event="event" @delete-event="deleteEvent" @event-content-changed="contentChanged" @evaluate-collapse="evaluateCollapse"></event-item>
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
      /* var tempYear = 2019
      var tempMonth = (this.getRandomInt(12) || 1)
      var tempDate = this.getRandomDay(tempMonth)
      var tempDay1 = tempYear + '-' + tempMonth + '-' + tempDate
      */
      var curDate = localStorage.getItem('selectedDate')

      if (curDate === '') { curDate = (new Date()).substring(0, 10) }

      var randomEvent = {
        title: 'To be assigned',
        startDate: curDate,
        endDate: curDate // for example
      }
      Api.post('/events', randomEvent)
        .then(response => {
          this.events.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getEvents()
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
    },
    deleteAllEvents() {
      Api.delete(`/events/`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          this.events = []
        })
        .catch(error => {
          console.log(error)
        })
    },
    contentChanged() {
      this.events = []
      this.getEvents()
    },
    evaluateCollapse(eventId) {
      /*
      var storedEvent = localStorage.getItem('allowedEvent')
      var displaying = localStorage.getItem('displaying')
      console.log('clicked : ' + eventId + ', stored: ' + storedEvent)

      if(displaying){
        if(!(eventId === storedEvent)){
        localStorage.setItem('allowedEvent', eventId)
        //console.log('from main view')
        this.contentChanged()
      } else {
        localStorage.setItem('allowedEvent', '')
      }
      } */

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
  background-color: rgb(59, 199, 89)
}
.deleteButton {
  margin-bottom: 1em;
  margin-left: 1em;
  background-color: rgb(226, 43, 128)
}
.events {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
