<template>
  <div class="history-event card">
    <div class="history-header">
      <h3>{{ response.action.replace(/_/g, ' ') }}</h3>
      <h3 class="timestamp">{{ formatDate(response.timeStamp) }}</h3>
    </div>

    <TriplesTable
      :triples="formattedTriples"
      :show-action-column="true"
      :disable-selection="true"
      mode="history"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import TriplesTable from '../util/TripleTable.vue'
import type { HistoryEvent, EnhancedTriple } from '@/scripts/type.ts'

const props = defineProps<{
  response: HistoryEvent
}>()

/**
 * Transforms incoming BackendTriple data into the flattened EnhancedTriple
 * format required by the TriplesTable component.
 */
const formattedTriples = computed<EnhancedTriple[]>(() => {
  return props.response.triples.map((ta, index) => ({
    id: index,
    action: ta.action,
    subject: ta.triple.s.value,
    rawSubject: ta.triple.s.value,
    predicate: ta.triple.p.value,
    rawPredicate: ta.triple.p.value,
    object: ta.triple.o.value,
    rawObject: ta.triple.o.value,
    isLiteral: ta.triple.o.isLiteral,
    raw: ta.triple,
  }))
})

function formatDate(isoString: string): string {
  return new Date(isoString).toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}
</script>

<style scoped>
/* --- LAYOUT --- */
.history-event {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.history-header {
  background-color: var(--secondaryColor);
  border-bottom: 2px solid var(--accentColor);
  display: flex;
  flex-direction: row;
  gap: var(--padding);
  align-items: center;
  height: fit-content;
  padding: var(--paddingHalf);
}

/* --- COMPONENTS & TYPOGRAPHY --- */

.history-header h3 {
  margin: 0;
  color: var(--accentColor);
}

.timestamp {
  font-family: var(--fancyFontStyle);
  font-weight: lighter;
  color: rgba(255, 255, 255, 0.7); /* Slightly muted white for better contrast */
  padding-bottom: 3px;
}

.divider {
  margin-top: var(--paddingHalf);
  height: 1px;
  width: 100%;
}
</style>
