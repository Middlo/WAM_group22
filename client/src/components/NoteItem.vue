<template>
  <b-list-group-item class="mainCard">
    <b-button class="close" @click="$emit('delete-note', note._id)">&times;</b-button>
    <p> Note: {{ note.topic }} </p>
    <b-button class="detailButton" variant="primary" @click="showDetail(note._id)">Details</b-button>
    <div class="collapsable">
      <b-container fluid id="collapse1" class="firstCard" v-show="allowedItem === note._id">

        <p class="card-text3">Note ID: {{ note._id }}</p>

        <b-row>
          <b-col sm="2.3">
            <label for="input-1">Topic:</label>
          </b-col>
          <b-col sm="6">
            <b-form-input id="input-1" size="sm" v-model="editform.topic" :placeholder="note.topic"></b-form-input>
          </b-col>
        </b-row>

        <b-row>
          <b-col sm="2.3">
            <label for="input-2">Content:</label>
          </b-col>
          <b-col sm="6">
            <b-form-input id="input-2" size="sm" v-model="editform.textContent" :placeholder="note.textContent"></b-form-input>
          </b-col>
        </b-row>

        <p class="card-text3">Created on: {{ note.createdOn.substring(0,10) }}</p>
        <p class="card-text4">Last Updated: {{ note.lastUpdated.substring(0,10) }}</p>

        <b-button id="putButton" @click="putData (note._id, editform), $emit('note-content-changed', note._id)">Update Entirely</b-button>
        <b-button id="patchButton" @click="patchData (note._id, editform), $emit('note-content-changed', note._id)">Update Partially</b-button>

      </b-container>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowedItem = ''

export default {
  name: 'note-item',
  props: ['note'],
  data() {
    return {
      notes: [],
      allowedItem,
      editform: {
        topic: '',
        textContent: ''
      }
    }
  },
  mounted() {
    this.getNotes()
  },
  methods: {
    getNotes() {
      Api.get('/notes')
        .then(reponse => {
          this.notes = reponse.data.notes
        })
        .catch(error => {
          this.notes = []
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
      Api.put(`/notes/${id}/`, form)
        .then(reponse => {
          if (reponse.data.note._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else {
            console.log(reponse.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getNotes()
          // This code is always executed (after success or error).
        })
    },
    patchData(id, form) {
      // console.log('patch is requested with ' + form + ' for ' + id)
      Api.patch(`/notes/${id}/`, form)
        .then(reponse => {
          if (reponse.data.note._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getNotes()
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
  background-color: rgb(116, 118, 235);
  margin-bottom: 5px;
}
.detailButton{
  background-color: rgb(13, 55, 117);
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
