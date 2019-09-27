<template>
  <div>
    <b-list-group-item :style="{ color: task.color }">
      Task {{ task._id }} has title {{ task.taskTitle }}
      <b-button variant="primary" @click="$emit('show-task-reminder', task._id)"> Reminders</b-button>
      <b-button type="button" class="close" @click="$emit('delete-task', task._id)">&times;</b-button>
    </b-list-group-item>
  </div>
</template>

<script>

export default {
  name: 'task-item',
  props: ['task']
}
</script>

<style scoped>
img {
  width: 33px;
  margin-right: 10px;
}
</style>
