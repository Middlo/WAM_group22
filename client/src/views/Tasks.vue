<template>
  <div class="tasks">
    <h1 v-if="tasks.length === 0"> There are no tasks registered </h1>
    <h1 v-else-if="tasks.length === 1"> There is one task </h1>
    <h1 v-else > There are {{ tasks.length }} tasks </h1>
    <b-button class="createButton" @click="createTask()">Create new Task</b-button>
    <b-button class="deleteButton" v-show="tasks.length" @click="deleteAllTasks()">Delete All Tasks</b-button>
    <b-list-group>
      <task-item v-for="task in tasks" :key="task._id" :task="task" @delete-task="deleteTask" @task-content-changed="contentChanged"></task-item>
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
    createTask() {
      var curDate = localStorage.getItem('selectedDate')

      if (curDate === '') { curDate = (new Date()).substring(0, 10) }

      var randomTask = {
        taskTitle: 'Testing the task',
        importanceLevel: '1',
        deadline: curDate
      }
      // console.log(randomTask)
      Api.post('/tasks', randomTask)
        .then(response => {
          this.tasks.push(response.data)
        })
        .catch(error => {
          console.log(error)
        })
        .then(() => {
          this.getTasks()
          // This code is always executed (after success or error).
        })
    },
    deleteAllTasks() {
      Api.delete(`/tasks/`)
        .then(response => {
          console.log(response.data.message)
          // console.log(response.data.message)
          this.tasks = []
        })
        .catch(error => {
          console.log(error)
        })
    },
    contentChanged() {
      this.tasks = []
      this.getTasks()
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
  background-color: blueviolet
}
.deleteButton {
  margin-bottom: 1em;
  margin-left: 1em;
  background-color: rgb(226, 43, 128)
}
.tasks {
  margin-left: 5%;
  margin-right: 5%;
  margin-bottom: 2em;
}
</style>
