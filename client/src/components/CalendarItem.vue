<template>
  <b-list-group-item class="mainCard">
    <img alt="Calendar" src="../assets/calendar.jpg">
    <b-button class="close" @click="$emit('delete-calendar', calendar._id)">&times;</b-button>
    <p class="main-header">On {{ calendar.targetDate.substring(0,10) }} </p>
    <b-button class="detailButton" variant="primary" @click="showCalDetail(calendar._id, calendar.targetDate.substring(0,10))">Details</b-button>
    <div class="collapsable">
      <b-container fluid id="collapse1" class="firstCard" align-v="start" v-show="allowedItem === calendar._id">

        <p class="card-text3">Calendar ID: {{ calendar._id }} </p>

        <b-row>
          <b-col sm="2">
            <label for="input-1">Date:</label>
          </b-col>
          <b-col sm="6">
            <b-form-input id="input-1" size="sm" v-model="editform.targetDate" required :placeholder="calendar.targetDate.substring(0,10)"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="2">
            <label size="sm" for="input-2">Type:</label>
          </b-col>
          <b-col sm="6">
            <b-form-input id="input-2" autocomplete="on" size="sm" v-model="editform.viewType" :placeholder="calendar.viewType"></b-form-input>
          </b-col>
        </b-row>

        <b-button id="putButton" @click="putData (calendar._id, editform), $emit('calendar-content-changed', calendar._id)">Update Entirely</b-button>
        <b-button id="patchButton" @click="patchData (calendar._id, editform), $emit('calendar-content-changed', calendar._id)">Update Partially</b-button>

      </b-container>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowedItem = ''

export default {
  name: 'calendar-item',
  props: ['calendar'],
  data() {
    return {
      calendars: [],
      allowedItem,
      editform: {
        targetDate: '',
        viewType: ''
      }
    }
  },
  mounted() {
    this.getCalendars()
  },
  methods: {
    getCalendars() {
      Api.get('/calendars')
        .then(reponse => {
          this.calendars = reponse.data.calendars
        })
        .catch(error => {
          this.calendars = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    showCalDetail(calID, tarDate) {
      if (!this.allowedItem) { this.allowedItem = calID } else if (this.allowedItem === calID) { this.allowedItem = '' } else { this.allowedItem = calID }
      // console.log('collapse display allowed for : ' + this.allowedItem)
    },
    putData(id, form) {
      // console.log('put is requested with ' + form + ' for ' + id)
      Api.put(`/calendars/${id}/`, form)
        .then(reponse => {
          if (reponse.data.calendar._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else {
            console.log(reponse.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getCalendars()
          // This code is always executed (after success or error).
        })
    },
    patchData(id, form) {
      // console.log('patch is requested with ' + form + ' for ' + id)
      Api.patch(`/calendars/${id}/`, form)
        .then(reponse => {
          if (reponse.data.calendar._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getCalendars()
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
  background-color: rgb(235, 116, 185);
  margin-bottom: 5px;
}
.detailButton{
  background-color: rgb(43, 116, 226);
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
  margin:auto;
}
#putButton{
  background-color: rgba(87, 61, 129, 0.979);
  margin: 2px;

}
#patchButton{
  background-color: rgba(81, 98, 153, 0.979);
  margin: 2px;
}
.main-header {
  text-emphasis: none;
}
</style>
