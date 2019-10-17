<template>
  <b-list-group-item class="mainCard">
    <b-button class="close" @click="$emit('delete-reminder', reminder._id)">&times;</b-button>
    <p> Reminder: {{ reminder.topic }} </p>
    <b-button class="detailButton" variant="primary" @click="showDetail(reminder._id)">Details</b-button>
    <div class="collapsable">
      <b-container id="collapse1" class="firstCard" v-show="allowedItem === reminder._id">

        <p class="card-text3">Reminder ID: {{ reminder._id }}</p>

        <b-row>
          <b-col sm="4">
            <label for="input-1">Topic:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-1" size="sm" v-model="editform.topic" :placeholder="reminder.topic"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="4">
            <label description="Reminder target Moment" for="input-2">Target:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-2" size="sm" required v-model="editform.targetMoment" :placeholder="reminder.targetMoment.substring(0,10)"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="4">
            <label description="Reminder to remind before" for="input-3">Minutes:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-3" size="sm" v-model="editform.remindBefore" :placeholder="reminder.remindBefore"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="4">
            <label for="input-4">Importance:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-4" size="sm" v-model="editform.importanceLevel" :placeholder="reminder.importanceLevel"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="4">
            <label for="input-5">Linked to:</label>
          </b-col>
          <b-col sm="7">
            <b-form-input id="input-5" size="sm" v-model="editform.reminderFor" :placeholder="reminder.reminderFor"></b-form-input>
          </b-col>
        </b-row>

        <b-button id="putButton" @click="putData (reminder._id, editform), $emit('reminder-content-changed', reminder._id)">Update Entirely</b-button>
        <b-button id="patchButton" @click="patchData (reminder._id, editform), $emit('reminder-content-changed', reminder._id)">Update Partially</b-button>

      </b-container>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowedItem = ''

export default {
  name: 'reminder-item',
  props: ['reminder'],
  data() {
    return {
      reminders: [],
      allowedItem,
      editform: {
        topic: '',
        targetMoment: '',
        remindBefore: '',
        importanceLevel: '',
        reminderFor: ''
      }
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
    showDetail(calID) {
      if (!this.allowedItem) { this.allowedItem = calID } else if (this.allowedItem === calID) { this.allowedItem = '' } else { this.allowedItem = calID }
      // console.log('collapse display allowed for : ' + this.allowedItem)
    },
    putData(id, form) {
      // console.log('put is requested with ' + form + ' for ' + id)
      Api.put(`/reminders/${id}/`, form)
        .then(reponse => {
          if (reponse.data.reminder._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else {
            console.log(reponse.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getReminders()
          // This code is always executed (after success or error).
        })
    },
    patchData(id, form) {
      // console.log('patch is requested with ' + form + ' for ' + id)
      Api.patch(`/reminders/${id}/`, form)
        .then(reponse => {
          if (reponse.data.reminder._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getReminders()
          // This code is always executed (after success or error).
        })
    }
  }
}
</script>

<style scoped>
img {
  width: 33px;
  margin-right: 10px;
}
.mainCard{
  background-color: rgb(235, 130, 116);
  margin-bottom: 5px;
}
.detailButton{
  background-color: rgb(36, 100, 196);
  margin-left: 10px;
  margin-bottom: 10px;
}
.createButton {
  margin-bottom: 1em;
  background-color: rgb(59, 199, 89)
}
.close{
  margin-top: 1px;
  width: 20px;
  height: 25px;
  background-color: rgb(221, 35, 35);
}
#collapse1{
  background-color: rgba(218, 189, 189, 0.979);
}
#putButton{
  background-color: rgba(87, 61, 129, 0.979);
  margin: 2px;
}
#patchButton{
  background-color: rgba(81, 98, 153, 0.979);
  margin: 2px;
}
</style>
