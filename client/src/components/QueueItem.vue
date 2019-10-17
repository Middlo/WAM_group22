<template>
  <b-list-group-item class="mainCard">
    <p> Queue: {{ queue._id }} </p>
    <b-button class="detailButton" variant="primary" @click="showDetail(queue._id)">Details</b-button>
    <b-button class="close" @click="$emit('delete-queue', queue._id)">&times;</b-button>
    <div class="collapsable">
      <b-card id="collapse1" class="firstCard" v-show="allowedItem === queue._id">
        <div>
          <p class="card-text3">Queue Code: {{ queue._id }}</p>
          <b-form id=form1 class="form">
            <b-form-group
              id="input-group-1"
              label="Queue elements:"
              label-for="input-1"
            >
              <b-form-input
                id="input-1"
                v-model="editform.elements"
                required
                :placeholder="queue.elements"
                ></b-form-input>
            </b-form-group>

          </b-form>
          <b-button id="putButton" @click="putData (queue._id, editform), $emit('queue-content-changed', queue._id)">Update Entirely</b-button>
          <b-button id="patchButton" @click="patchData (queue._id, editform), $emit('queue-content-changed', queue._id)">Update Partially</b-button>
        </div>
      </b-card>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowedItem = ''

export default {
  name: 'queue-item',
  props: ['queue'],
  data() {
    return {
      queues: [],
      allowedItem,
      editform: {
        elements: ''
      }
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
    showDetail(calID) {
      if (!this.allowedItem) { this.allowedItem = calID } else if (this.allowedItem === calID) { this.allowedItem = '' } else { this.allowedItem = calID }
      // console.log('collapse display allowed for : ' + this.allowedItem)
    },
    putData(id, form) {
      // console.log('put is requested with ' + form + ' for ' + id)
      Api.put(`/queues/${id}/`, form)
        .then(reponse => {
          if (reponse.data.queue._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else {
            console.log(reponse.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getQueues()
          // This code is always executed (after success or error).
        })
    },
    patchData(id, form) {
      // console.log('patch is requested with ' + form + ' for ' + id)
      Api.patch(`/queues/${id}/`, form)
        .then(reponse => {
          if (reponse.data.queue._id) {
            // console.log('after reloding event ' + reponse.data.event._id)

          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getQueues()
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
  background-color: rgb(235, 184, 116);
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
