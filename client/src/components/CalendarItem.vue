<template>
  <b-list-group-item :style="{ color: calendar.color }">
    <img alt="Calendar" src="../assets/calendar.jpg">
    {{ calendar._id }} is on {{ calendar.currentMonth }} {{ calendar.currentDate }}
    <b-button type="button" class="close" @click="$emit('delete-calendar', calendar._id)">&times;</b-button>
  </b-list-group-item>
</template>

<script>

export default {
  name: 'calendar-item',
  props: ['calendar']
}
</script>

<style scoped>
img {
  width: 33px;
  margin-right: 10px;
}
</style>
