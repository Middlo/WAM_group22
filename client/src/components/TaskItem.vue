<template>
  <div>
    <b-list-group-item>
      Task title: {{ task.taskTitle }}
      <b-button v-b-toggle.collapse1 variant="primary" data-parent="#accordion" @click="getReminders(task._id, task)">Details</b-button>
      <b-button type="button" class="close" @click="$emit('delete-task', task._id)">&times;</b-button>
      <div class="collapsable">
        <b-collapse id="collapse1" class="mt-2">
          <b-card>
            <p class="card-text0">Task Code : {{ task._id }}</p>
            <p class="card-text1">Description : {{ task.taskDescription }}</p>
            <p class="card-text2">Importance Level : {{ task.importanceLevel }}</p>
            <p class="card-text3">Deadline : {{ task.deadline }}</p>
            <b-button v-b-toggle.collapse1-inner size="sm" @click="$emit('show-reminder', task._id)">Show Reminders</b-button>
            <b-collapse id="collapse1-inner" class="mt-2">
              <div class="taskReminders">
                <b-button type="button" class="createTaskReminder" @click="createTaskReminder(task._id), $emit('task-content-changed')">Create Reminder</b-button>
                <div>
                  <b-list-group-item v-for="reminder in reminders" v-bind:key="reminder._id">
                    <b-button type="button" class="close" @click="deleteTaskReminder(task), $emit('task-content-changed')">&times;</b-button>
                    <p class="card-text10">Reminder ID : {{ reminder._id }}</p>
                    <p class="card-text11">Topic : {{ reminder.topic }}</p>
                    <p class="card-text12">Target Moment : {{ reminder.targetMoment }}</p>
                    <p class="card-text13">Remind Before : {{ reminder.remindBefore }}</p>
                  </b-list-group-item>
                </div>
              </div>
            </b-collapse>
          </b-card>
        </b-collapse>
      </div>
    </b-list-group-item>
  </div>
</template>

<script>
import { Api } from '@/Api'
var tempReminder
var foundReminderID

export default {
  name: 'task-item',
  props: ['task'],
  data() {
    return {
      tasks: [],
      reminders: [],
      tempReminder,
      foundReminderID
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
    getReminders(evtID, currentTask) {
      if (currentTask.reminder) {
        Api.get(`tasks/${evtID}/reminders`)
          .then(reponse => {
            this.tempReminder = reponse.data.reminder
            Api.get(`reminders/${reponse.data.reminder}`)
              .then(reponse => {
                var foundReminder = reponse.data.reminder
                this.reminders = []
                this.reminders.push(foundReminder)
                foundReminderID = foundReminder._id
              })
              .catch(error => {
                this.reminders = []
                console.log(error)
              })
              .then(() => {
                // This code is always executed (after success or error).
              })
          })
          .catch(error => {
            this.tempReminder = ''
            console.log(error)
          })
          .then(() => {
          // This code is always executed (after success or error).
          })
      }
    },
    createTaskReminder(evtID) {
      var newReminder = {
        topic: 'To be Assigned',
        reminderFor: evtID
      }

      Api.post(`tasks/${evtID}/reminders`, newReminder)
        .then(reponse => {
          if (reponse.data.task) {
            console.log('new reminder ID is ' + reponse.data.task.reminder._id)
            this.getTasks()
          }
        })
        .catch(error => {
          this.reminders = []
          console.log(error)
        })
        .then(() => {
        // This code is always executed (after success or error).
        })
    },
    deleteTaskReminder(currentTask) {
      Api.delete(`/reminders/${foundReminderID}`)
        .then(reponse => {
          if (reponse.data.message === 'Success') {
            var newTask = {
              _id: currentTask._id,
              taskTitle: currentTask.taskTitle,
              taskDescription: currentTask.taskDescription,
              importanceLevel: currentTask.importanceLevel,
              deadline: currentTask.deadline
            }
            Api.put(`tasks/${currentTask._id}/`, newTask)
              .then(reponse => {
                if (reponse.data.task._id) {
                  console.log('after reloding task ' + reponse.data.task._id)
                  this.getTasks()
                }
              })
              .catch(error => {
                console.log(error)
              })
              .then(() => {
              // This code is always executed (after success or error).
              })
          }
        })
        .catch(error => {
          this.tasks = []
          console.log(error)
        })
        .then(() => {
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
</style>
