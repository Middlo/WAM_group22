<template>
  <b-list-group-item class="mainCard">
    Reminder for Entity ID {{ reminder.reminderFor }}
    <b-button class="detailButton" variant="primary" @click="showDetail(reminder._id)">Details</b-button>
    <b-button class="close" @click="$emit('delete-reminder', reminder._id)">&times;</b-button>
    <div class="collapsable">
      <b-card id="collapse1" class="firstCard" v-show="allowedItem === reminder._id">
        <div>
          <p class="card-text3">Reminder Code: {{ reminder._id }}</p>
          <b-form id=form1 class="form">
            <b-form-group
              id="input-group-1"
              label="Reminder topic:"
              label-for="input-1"
            >
              <b-form-input
                id="input-1"
                v-model="editform.topic"
                :placeholder="reminder.topic"
                ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-2"
              label="Reminder target Moment:"
              label-for="input-2"
            >
              <b-form-input
                id="input-2"
                v-model="editform.targetMoment"
                :placeholder="reminder.targetMoment"
                ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-3"
              label="Reminder to remind before:"
              label-for="input-3"
            >
              <b-form-input
                id="input-3"
                v-model="editform.remindBefore"
                :placeholder="reminder.remindBefore"
                ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-4"
              label="Reminder linked to Entity ID:"
              label-for="input-4"
            >
              <b-form-input
                id="input-4"
                v-model="editform.reminderFor"
                :placeholder="reminder.reminderFor"
                ></b-form-input>
            </b-form-group>

          </b-form>
          <b-button class="putButton" @click="putData (reminder._id, editform), $emit('reminder-content-changed', reminder._id)">Update Entirely</b-button>  |
          <b-button class="patchButton" @click="patchData (reminder._id, editform), $emit('reminder-content-changed', reminder._id)">Update Partially</b-button>
        </div>
      </b-card>
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
.firstCard{
  background-color: rgba(218, 189, 189, 0.979);
}
.putButton{
  background-color: rgba(87, 61, 129, 0.979);
}
.patchButton{
  background-color: rgba(81, 98, 153, 0.979);
}
</style>
