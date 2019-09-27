<template>
  <div class="tasks">
    <h1>List of {{ tasks.length }} tasks</h1>
    <b-button type="button" class="createButton" @click="createTask()">Create Task</b-button>
    <b-list-group>
      <task-item v-for="task in tasks" :key="task._id" :task="task" @delete-task="deleteTask" @show-task-reminder="showTaskReminder"></task-item>
    </b-list-group>
  </div>
</template>

<script>
import { Api } from '@/Api'
import TaskItem from '@/components/TaskItem'

export default {
  name: 'Tasks',
  data() {
    return {
      tasks: []
    }
  },
  mounted() {
    this.getTasks()
  },
  methods: {
    getTasks() {
      Api.get('tasks')
        .then(reponse => {
          this.tasks = reponse.data.Tasks
        })
        .catch(error => {
          this.tasks = []
          console.log(error)
        })
        .then(() => {
          // This code is always executed (after success or error).
        })
    },
    deleteTask(id) {
      Api.delete(`/tasks/${id}`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          var index = this.tasks.findIndex(task => task._id === id)
          this.tasks.splice(index, 1)
        })
        .catch(error => {
          console.log(error)
        })
    },
    showTaskReminder(id) {
      Api.get(`/tasks/${id}/reminders`)
        .then(response => {
          var currReminders = response.data.reminders
          var amount = currReminders.length
          var text
          if (amount === 0) { text = 'There are no reminders' }
          if (amount === 1) { text = 'There is 1 reminder' }
          if (amount > 1) { text = 'There are ' + amount + ' reminder' }
          console.log(text)
        })
    },
    createTask() {
      var randomTask = {
        taskTitle: 'test',
        importanceLevel: 1
      }
      console.log(randomTask)
      Api.post('/tasks', randomTask)
        .then(response => {
          this.tasks.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
    }
  },
  components: {
    TaskItem
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
}
.tasks {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
