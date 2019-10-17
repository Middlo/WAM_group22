<template>
  <div class="notes">
    <h1 v-if="notes.length === 0"> There are no notes registered </h1>
    <h1 v-else-if="notes.length === 1"> There is one note </h1>
    <h1 v-else > There are {{ notes.length }} notes </h1>
    <b-button type="button" class="createButton" @click="createNote()">Create Note</b-button>
    <b-button class="deleteButton" v-show="notes.length" @click="deleteAllNotes()">Delete All Notes</b-button>
    <b-list-group>
      <note-item v-for="note in notes" :key="note._id" :note="note" @delete-note="deleteNote" @note-content-changed="contentChanged"></note-item>
    </b-list-group>
  </div>
</template>

<script>
import { Api } from '@/Api'
import NoteItem from '@/components/NoteItem'

export default {
  name: 'Notes',
  data() {
    return {
      notes: []
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
    deleteNote(id) {
      Api.delete(`/notes/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.notes.findIndex(note => note._id === id)
          this.notes.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
    },
    createNote() {
      var randomNote = {
        topic: 'To be edited',
        textContent: 'This content needs edit'
      }
      Api.post('/notes', randomNote)
        .then(response => {
          this.notes.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getNotes()
          // This code is always executed (after success or error).
        })
    },
    deleteAllNotes() {
      Api.delete(`/notes/`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          this.notes = []
        })
        .catch(error => {
          console.log(error)
        })
    },
    contentChanged() {
      this.notes = []
      this.getNotes()
    }
  },
  components: {
    NoteItem
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
.notes {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
