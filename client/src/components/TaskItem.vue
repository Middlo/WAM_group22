<template>
  <b-list-group-item class="mainCard">
    Task title: {{ task.taskTitle }}
    <b-button class="detailButton" variant="primary" @click="showTaskDetail(task._id)">Details</b-button>
    <b-button class="close" @click="$emit('delete-task', task._id)">&times;</b-button>
    <div class="collapsable">
      <b-card id="collapse1" class="firstCard" v-show="allowPart1 === task._id">
        <div>
          <p class="firstDetail">Task Code : {{ task._id }}</p>
          <b-form id=form1 class="form">
            <b-form-group
              id="input-group-0"
              label="Task Title:"
              label-for="input-0"
            >
              <b-form-input
                id="input-0"
                v-model="editform.taskTitle"
                :placeholder="task.taskTitle"
              ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-1"
              label="Task Description:"
              label-for="input-1"
            >
              <b-form-input
                id="input-1"
                v-model="editform.taskDescription"
                :placeholder="task.taskDescription"
              ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-2"
              label="Task Importance Level:"
              label-for="input-2"
            >
              <b-form-input
                id="input-2"
                v-model="editform.importanceLevel"
                :placeholder="task.importanceLevel"
              ></b-form-input>
            </b-form-group>

            <b-form-group
              id="input-group-3"
              label="Task Deadline:"
              label-for="input-3"
            >
              <b-form-input
                id="input-3"
                v-model="editform.deadline"
                required
                :placeholder="task.deadline.substring(0,10)"
              ></b-form-input>
            </b-form-group>
          </b-form>
          <div>
            <b-button class="putButton" @click="putData (task._id, editform), $emit('task-content-changed', task._id)">Update Task Entirely</b-button>  |
            <b-button class="patchButton" @click="patchData (task._id, editform), $emit('task-content-changed', task._id)">Update Task Partially</b-button>
          </div>
          <b-button class="reminderDetailBtn" @click="showRemDetail(task._id)">Reminder Details</b-button>
          <b-card id="collapse1-inner" class="secondCard" v-show="allowPart2 === task._id">
            <b-button class="createButton" @click="createTaskReminder(task, task._id), $emit('task-content-changed')">Create Reminder</b-button>
            <div >
              <b-list-group-item class="reminderDetail" v-for="reminder in reminders" v-bind:key="reminder._id">
                <b-button class="close" @click="deleteTaskReminder(task._id, reminder._id), $emit('task-content-changed')">&times;</b-button>
                <p class="secondDetail">Reminder ID : {{ reminder._id }}</p>

                <b-form id=form2 class="form">
                  <b-form-group
                    id="input-group-a"
                    label="Reminder Topic:"
                    label-for="input-a"
                  >
                    <b-form-input
                      id="input-a"
                      v-model="editReminderForm.topic"
                      :placeholder="reminder.topic"
                    ></b-form-input>
                  </b-form-group>

                  <b-form-group
                    id="input-group-b"
                    label="Reminder targer Moment:"
                    label-for="input-b"
                  >
                    <b-form-input
                      id="input-b"
                      v-model="editReminderForm.targetMoment"
                      :placeholder="reminder.targetMoment"
                    ></b-form-input>
                  </b-form-group>

                  <b-form-group
                    id="input-group-c"
                    label="Remind Before Minutes:"
                    label-for="input-c"
                  >
                    <b-form-input
                      id="input-c"
                      v-model="editReminderForm.remindBefore"
                      :placeholder=reminder.remindBefore
                    ></b-form-input>
                  </b-form-group>
                </b-form>
                <b-button class="putButton" @click="putRemData (task._id, reminder._id, editReminderForm), $emit('task-content-changed', task._id)">Update Reminder Entirely</b-button>  |
                <b-button class="patchButton" @click="patchRemData (task._id, reminder._id, editReminderForm), $emit('task-content-changed', task._id)">Update Reminder Partially</b-button>
              </b-list-group-item>
            </div>
          </b-card>
        </div>
      </b-card>
    </div>
  </b-list-group-item>
</template>

<script>
import { Api } from '@/Api'
var allowPart1 = ''
var allowPart2 = ''
var currentTaskID

export default {
  name: 'task-item',
  props: ['task'],
  data() {
    return {
      tasks: [],
      currentTaskID,
      allowPart1,
      allowPart2,
      reminders: [],
      editform: {
        taskTitle: '',
        taskDescription: '',
        importanceLevel: '',
        deadline: ''
      },
      editReminderForm: {
        topic: '',
        targetMoment: '',
        remindBefore: ''
      }
    }
  },
  mounted() {
    this.getTasks()
  },
  methods: {
    getTasks() {
      Api.get('/tasks')
        .then(reponse => {
          this.tasks = reponse.data.tasks
        })
        .catch(error => {
          this.tasks = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    showTaskDetail(evtID) {
      this.currentTaskID = evtID
      if (!this.allowPart1) { this.allowPart1 = evtID } else if (this.allowPart1 === evtID) { this.allowPart1 = '' } else { this.allowPart1 = evtID }
    },
    showRemDetail(evtID) {
      this.currentTaskID = evtID
      if (!this.allowPart2) { this.allowPart2 = evtID } else if (this.allowPart2 === evtID) { this.allowPart2 = '' } else { this.allowPart2 = evtID }
      Api.get(`tasks/${evtID}/reminders`)
        .then(reponse => {
          this.reminders = reponse.data.reminders
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    createTaskReminder(task, evtID) {
      this.currentTaskID = evtID
      var newReminder = {
        topic: 'To be Assigned',
        reminderFor: evtID
      }

      Api.post(`/reminders`, newReminder)
        .then(reponse => {
          if (reponse.data.reminder._id) {
            // console.log('new reminder ID is ' + reponse.data.task.reminder._id)
            this.getTasks()
          } else console.log(reponse.data.message)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getTasks()
        // This code is always executed (after success or error).
        })
    },
    deleteTaskReminder(evtID, remID) {
      this.currentTaskID = evtID
      Api.delete(`tasks/${evtID}/reminders/${remID}`)
        .then(reponse => {
          if (reponse.data.reminder._id) {
            // success
          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getTasks()
          // This code is always executed (after success or error).
        })
    },
    putData(id, form) {
      this.currentTaskID = id
      // console.log('put is requested with ' + form + ' for ' + id)
      Api.put(`/tasks/${id}/`, form)
        .then(reponse => {
          if (reponse.data.task._id) {
            // console.log('after reloding task ' + reponse.data.task._id)

          } else {
            console.log(reponse.data.message)
          }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getTasks()
          // This code is always executed (after success or error).
        })
    },
    patchData(id, form) {
      this.currentTaskID = id
      // console.log('requested reminder is requested with ' + form + ' for ' + id)
      Api.patch(`/tasks/${id}/`, form)
        .then(reponse => {
          if (reponse.data.task._id) {
            // console.log('after reloding task ' + reponse.data.task._id)

          } else { console.log(reponse.data.message) }
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getTasks()
          // This code is always executed (after success or error).
        })
    },
    putRemData(id, taskRemID, form) {
      this.currentTaskID = id
      // console.log('put is requested with ' + form + ' for ' + id)
      if (taskRemID) {
        Api.put(`/reminders/${taskRemID}`, form)
          .then(reponse => {
            if (reponse.data.reminder._id) {
            // console.log('after reloding task ' + reponse.data.reminder._id)

            } else {
              console.log(reponse.data.message)
            }
          })
          .catch(error => {
            console.log(error)
          })
          .then(() => {
            this.getTasks()
          // This code is always executed (after success or error).
          })
      }
    },
    patchRemData(id, taskRemID, form) {
      this.currentTaskID = id
      // console.log('reminder id is ' + taskRemID)
      if (taskRemID) {
        Api.patch(`/reminders/${taskRemID}`, form)
          .then(reponse => {
            if (reponse.data.reminder._id) {
            // console.log('after reloding task ' + reponse.data.reminder._id)

            } else { console.log(reponse.data.message) }
          })
          .catch(error => {
            console.log(error)
          })
          .then(() => {
            this.getTasks()
          // This code is always executed (after success or error).
          })
      }
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
  background-color: rgb(101, 212, 119);
  margin-bottom: 5px;
}
.detailButton{
  background-color: rgb(43, 116, 226);
  margin-left: 10px;
  margin-bottom: 10px;
}
.createButton {
  margin-bottom: 1em;
  background-color: blueviolet
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
.secondCard{
  background-color: rgba(198, 218, 189, 0.979);
}
.reminderDetailBtn{
  margin-top: 20px;
  margin-bottom: 10px;
}
.reminderDetail{
  background-color: rgba(198, 218, 189, 0.979);
}
.putButton{
  background-color: rgba(87, 61, 129, 0.979);
}
.patchButton{
  background-color: rgba(81, 98, 153, 0.979);
}
</style>
