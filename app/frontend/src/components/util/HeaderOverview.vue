<template>
  <div class="overview-section">
    <h1 class="fancy">{{ title }}</h1>
    <p>{{ description }}</p>
    <div class="divider accent"></div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
})
</script>

<style scoped>
.overview-section h1 {
  margin: var(--padding);
  margin-left: 0;
  margin-bottom: var(--paddingHalf);
}
.overview-section p {
  color: var(--darkFontColor);
}
</style>
