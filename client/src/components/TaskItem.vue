<template>
  <div>
    <b-list-group-item>
      Task title: {{ task.taskTitle }}
      <b-button variant="primary" @click="getReminders(task._id, task), showTaskDetail(task._id)">Details</b-button>
      <b-button type="button" class="close" @click="$emit('delete-task', task._id)">&times;</b-button>
      <div class="collapsable">
        <b-card id="collapse1" v-show="tasks.allowPart1 === task._id">
          <div>
            <p class="card-text0">Task Code : {{ task._id }}</p>
            <p class="card-text1">Description : {{ task.taskDescription }}</p>
            <p class="card-text2">Importance Level : {{ task.importanceLevel }}</p>
            <p class="card-text3">Deadline : {{ task.deadline }}</p>
            <b-button v-b-toggle.collapse1-inner size="sm">Reminder Details</b-button>
            <b-collapse id="collapse1-inner" class="mt-2">
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
            </b-collapse>
          </div>
        </b-card>
      </div>
    </b-list-group-item>
  </div>
</template>


<script>
import { Api } from '@/Api'
var tempReminder
var foundTReminderID

export default {
  name: 'task-item',
  props: ['task'],
  data() {
    return {
      tasks: [],
      reminders: [],
      tempReminder,
      foundTReminderID
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
          this.tasks.allowPart1 = ''
          this.tasks.allowPart2 = ''
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
            if (reponse.data.reminder) {
              Api.get(`reminders/${reponse.data.reminder}`)
                .then(reponse => {
                  var foundReminder = reponse.data.reminder
                  this.reminders = []
                  this.reminders.push(foundReminder)
                  foundTReminderID = foundReminder._id
                })
                .catch(error => {
                  this.reminders = []
                  console.log(error)
                })
                .then(() => {
                  // This code is always executed (after success or error).
                })
            } else { this.reminders = [] }
          })
          .catch(error => {
            this.tempReminder = ''
            console.log(error)
          })
          .then(() => {
          // This code is always executed (after success or error).
          })
      } else { this.reminders = [] }
    },
    showTaskDetail(evtID) {
      if (!this.tasks.allowPart1) { this.tasks.allowPart1 = evtID } else if (this.tasks.allowPart1 === evtID) { this.tasks.allowPart1 = '' } else { this.tasks.allowPart1 = evtID }
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
          this.getTasks()
        // This code is always executed (after success or error).
        })
    },
    deleteTaskReminder(currentTask) {
      Api.delete(`/reminders/${foundTReminderID}`)
        .then(reponse => {
          if (reponse.data.message === 'Success') {
            foundTReminderID = ''
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
                  // console.log('after reloding task ' + reponse.data.task._id)

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
        })
        .catch(error => {
          this.tasks = []
          console.log(error)
        })
        .then(() => {
          this.getTasks()
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
