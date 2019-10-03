<template>
  <div class="reminders">
    <h1>List of {{ reminders.length }} reminders</h1>
    <b-button type="button" class="createButton" @click="createReminder()">Create Reminder</b-button>
    <b-button class="deleteButton" v-show="reminders.length" @click="deleteAllReminders()">Delete All Reminders</b-button>
    <b-list-group>
      <reminder-item v-for="reminder in reminders" :key="reminder._id" :reminder="reminder" @delete-reminder="deleteReminder" @reminder-content-changed="getReminders"></reminder-item>
    </b-list-group>
  </div>
</template>

<script>
import { Api } from '@/Api'
import ReminderItem from '@/components/ReminderItem'

export default {
  name: 'Reminders',
  data() {
    return {
      reminders: []
    }
  },
  mounted() {
    this.getReminders()
  },
  methods: {
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
    createReminder() {
      var randomReminder = {
        elements: 'To be edited'
      }
      Api.post('/reminders', randomReminder)
        .then(response => {
          this.reminders.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
    },
    deleteAllReminders() {
      Api.delete(`/reminders/`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          this.reminders = []
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
.reminders {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
