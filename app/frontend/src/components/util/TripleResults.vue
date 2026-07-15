<template>
  <div class="results-container">
    <div class="results-header">
      <h2 class="result-title">QUERY RESULTS</h2>
      <h2 class="count">{{ triples.length }} TRIPLES</h2>
    </div>
    <div class="divider accent"></div>
  </div>
</template>

<script setup lang="ts">
import { type BackendTriple } from '@/scripts/type.ts'

defineProps<{
  triples: BackendTriple[]
}>()
</script>

<style scoped>
.results-container {
  margin-bottom: var(--padding);
}

.results-header {
  display: flex;
  align-items: baseline; /* Keeps text aligned even with different font sizes */
  gap: var(--padding);
  margin-bottom: var(--paddingHalf);
}

.result-title {
  margin: 0;
  font-size: var(--text-h2);
  font-style: var(--baseFontStyle);
}

.count {
  margin: 0;
  color: var(--mutedFontColor);
  font-size: var(--text-h3);
  font-weight: normal;
}
</style>
