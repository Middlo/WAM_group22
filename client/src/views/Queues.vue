<template>
  <div class="queues">
    <h1>List of {{ queues.length }} queues</h1>
    <b-button type="button" class="createButton" @click="createQueue()">Create Queue</b-button>
    <b-button class="deleteButton" v-show="queues.length" @click="deleteAllQueues()">Delete All Queues</b-button>
    <b-list-group>
      <queue-item v-for="queue in queues" :key="queue._id" :queue="queue" @delete-queue="deleteQueue" @queue-content-changed="getQueues"></queue-item>
    </b-list-group>
  </div>
</template>

<script>
import { Api } from '@/Api'
import QueueItem from '@/components/QueueItem'

export default {
  name: 'Queues',
  data() {
    return {
      queues: []
    }
  },
  mounted() {
    this.getQueues()
  },
  methods: {
    getQueues() {
      Api.get('/queues')
        .then(reponse => {
          this.queues = reponse.data.queues
        })
        .catch(error => {
          this.queues = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    deleteQueue(id) {
      Api.delete(`/queues/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.queues.findIndex(queue => queue._id === id)
          this.queues.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
    },
    createQueue() {
      var randomQueue = {
        elements: 'To be edited'
      }
      Api.post('/queues', randomQueue)
        .then(response => {
          this.queues.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
    },
    deleteAllQueues() {
      Api.delete(`/queues/`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          this.queues = []
        })
        .catch(error => {
          console.log(error)
        })
    }
  },
  components: {
    QueueItem
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
.queues {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
