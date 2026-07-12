<template>
  <div>
    <div class="results">
      <h2>QUERY RESULTS:</h2>
      <h2 class="muted">{{ triples.length }} TRIPLES</h2>
    </div>
    <div class="divider accent"></div>
  </div>
</template>

<script setup lang="ts">
import { type Triple } from '../../store/semanticStore'

defineProps<{
  triples: Triple[]
}>()
</script>

<style scoped>
.results {
  display: flex;
  flex-direction: row;
  gap: var(--padding);
}
</style>
