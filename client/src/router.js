import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import Calendars from './views/Calendars.vue'
import Events from './views/Events.vue'
import Tasks from './views/Tasks.vue'
import Login from './views/Login.vue'
import Main from './views/Main.vue'
import Notes from './views/Notes.vue'
import Queues from './views/Queues.vue'
import Reminders from './views/Reminders.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.GROUP_DB,
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/calendars',
      name: 'calendars',
      component: Calendars
    },
    {
      path: '/events',
      name: 'events',
      component: Events
    },
    {
      path: '/tasks',
      name: 'tasks',
      component: Tasks
    },
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/main',
      name: 'main',
      component: Main
    },
    {
      path: '/notes',
      name: 'notes',
      component: Notes
    },
    {
      path: '/queues',
      name: 'queues',
      component: Queues
    },
    {
      path: '/reminders',
      name: 'reminders',
      component: Reminders
    }
  ]
})
