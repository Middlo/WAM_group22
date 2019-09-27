<template>
  <div class="calendars">
    <h1>List of {{ calendars.length }} calendars</h1>
    <b-button type="button" class="createButton" @click="createCalendar()">Create Calendar</b-button>
    <b-list-group>
      <calendar-item v-for="calendar in calendars" :key="calendar._id" :calendar="calendar" @delete-calendar="deleteCalendar"></calendar-item>
    </b-list-group>
  </div>
</template>

<script>
import { Api } from '@/Api'
import CalendarItem from '@/components/CalendarItem'

export default {
  name: 'Calendars',
  data() {
    return {
      calendars: []
    }
  },
  mounted() {
    this.getCalendars()
  },
  methods: {
    getCalendars() {
      Api.get('calendars')
        .then(reponse => {
          this.calendars = reponse.data.Calendars
        })
        .catch(error => {
          this.calendars = []
          console.log(error)
        })
        .then(() => {
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
    },
    createCalendar() {
      var tempMonth = this.getRandomMonth()
      var randomCalendar = {
        currentMonth: tempMonth,
        currentDate: this.getRandomDay(tempMonth)
      }
      Api.post('/calendars', randomCalendar)
        .then(response => {
          this.calendars.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
    },
    getRandomInt(max) {
      var result = Math.floor(Math.random() * max)
      if (result) {
        return result
      } else { return ++result }
    },
    getRandomMonth() {
      var months = ['Sep', 'Oct', 'Nov', 'Dec', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug']
      var monIndex = this.getRandomInt(months.length)
      return months[monIndex]
    },
    getRandomDay(calMonth) {
      if (calMonth === ('Oct' || 'Dec' || 'Jan' || 'Mar' || 'May' || 'Jul' || 'Aug')) {
        return this.getRandomInt(31)
      } else if (calMonth === ('Nov' || 'Apr' || 'Jun' || 'Sep')) {
        return this.getRandomInt(30)
      } else { return this.getRandomInt(28) } // for simplicity max 28
    }
  },
  components: {
    CalendarItem
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
.calendars {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
