<template>
  <div class="history-event card">
    <div class="history-header">
      <div class="event-meta">
        <h3>{{ response.action.replace(/_/g, ' ') }}</h3>
        <h3 class="timestamp">{{ formatDate(response.timeStamp) }}</h3>
      </div>
      <div class="divider accent"></div>
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
import { type EnhancedTriple } from '@/scripts/type.ts'
import TriplesTable from '../util/TripleTable.vue'

interface TripleAction {
  action: string
  triple: {
    s: { value: string }
    p: { value: string }
    o: { value: string; isLiteral: boolean }
  }
}

interface HistoryResponse {
  action: string
  timeStamp: string
  triples: TripleAction[]
}

const props = defineProps<{
  response: HistoryResponse
}>()

// Map the nested TripleAction to the EnhancedTriple format
const formattedTriples = computed<EnhancedTriple[]>(() => {
  return props.response.triples.map((ta, index) => ({
    id: index,
    action: ta.action, // Used for the Action column
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

function formatDate(isoString: string) {
  return new Date(isoString).toLocaleString(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  })
}
</script>

<style scoped>
.history-event {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.history-header {
  background-color: var(--accentColor);
  display: flex;
  align-items: center;
  padding: var(--paddingHalf);
}

.event-meta {
  display: flex;
  flex-direction: row;
  gap: var(--padding);
  align-items: center;
}
.event-meta h3 {
  margin: 0;
  color: white;
}

.timestamp {
  font-family: var(--fancyFontStyle);
  font-weight: lighter;
  color: var(--mutedFontColor);
  padding-bottom: 3px;
}
</style>
