<template>
  <div>
    <b-list-group-item>
      <p>List of {{ events.reminders.length }} reminders</p>
      <p class="card-text11">Topic : {{ event.reminders.topic }}</p>
      <p class="card-text12">Target Moment : {{ event.reminders.targetMoment }}</p>
      <p class="card-text13">Remind Before : {{ event.reminders.remindBefore }}</p>
      <b-button type="button" class="close" @click="$emit('delete-eventReminder', event.reminders._id)">&times;</b-button>
    </b-list-group-item>
  </div>
</template>

<script>

export default {
  name: 'event-reminder-item',
  props: ['reminder']
  /* data() {
    return {
      events: []
    }
  } */
}
</script>

<style scoped>
img {
  width: 33px;
  margin-right: 10px;
}
</style>
