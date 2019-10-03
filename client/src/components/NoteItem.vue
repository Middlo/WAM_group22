<template>
  <b-list-group-item class="mainCard">
    {{ note._id }} is last updated on {{ note.lastUpdated.substring(0,10) }}
    <b-button class="detailButton" variant="primary" @click="showDetail(note._id)">Details</b-button>
    <b-button class="close" @click="$emit('delete-note', note._id)">&times;</b-button>
    <div class="collapsable">
      <b-card id="collapse1" class="firstCard" v-show="allowedItem === note._id">
        <div>
          <p class="card-text3">Note Code: {{ note._id }}</p>
          <b-form id=form1 class="form">
            <b-form-group
              id="input-group-1"
              label="Note topic:"
              label-for="input-1"
            >
              <b-form-input
                id="input-1"
                v-model="editform.topic"
                required
                :placeholder="note.topic"
                ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-2"
              label="Text Content:"
              label-for="input-2"
            >
              <b-form-input
                id="input-2"
                v-model="editform.textContent"
                :placeholder="note.textContent"
              ></b-form-input>
            </b-form-group>
            <p class="card-text3">Last Updated: {{ note.lastUpdated.substring(0,10) }}</p>
          </b-form>
          <b-button class="putButton" @click="putData (note._id, editform), $emit('note-content-changed', note._id)">Update Entirely</b-button>  |
          <b-button class="patchButton" @click="patchData (note._id, editform), $emit('note-content-changed', note._id)">Update Partially</b-button>
        </div>
      </b-card>
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
