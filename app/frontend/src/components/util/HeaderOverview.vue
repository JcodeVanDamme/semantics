<template>
  <div class="overview-section">
    <h1 class="fancy">{{ title }}</h1>
    <p>{{ description }}</p>
    <div class="divider accent"></div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description: string
}>()
</script>

<style scoped>
.overview-section {
  margin-bottom: var(--padding);
}

.overview-section h1 {
  font-family: var(--fancyFontStyle);
  margin: 0;
  margin-bottom: var(--paddingHalf);
}

.overview-section p {
  color: var(--darkFontColor);
  margin-bottom: var(--paddingHalf);
}
</style>
