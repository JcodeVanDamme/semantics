<template>
  <div>
    <table class="triple-table">
      <thead>
        <tr>
          <th v-if="showActionColumn" class="action">Action</th>
          <th>Subject</th>
          <th>Predicate</th>
          <th>Object</th>
        </tr>
      </thead>

      <tbody>
        <tr
          v-for="triple in triples"
          :key="triple.id"
          :class="{ 'clickable-row': !disableSelection }"
          @click="handleRowClick(triple)"
        >
          <td v-if="showActionColumn" class="triple-cell action">
            <span class="action-tag">{{ triple.action }}</span>
          </td>

          <td class="triple-cell">
            <div>{{ triple.subject }}</div>
            <div class="raw-uri" :title="triple.rawSubject">{{ triple.rawSubject }}</div>
          </td>

          <td class="triple-cell">
            <div>{{ triple.predicate }}</div>
            <div class="raw-uri" :title="triple.rawPredicate">{{ triple.rawPredicate }}</div>
          </td>

          <td class="triple-cell">
            <div>{{ triple.object }}</div>

            <div v-if="!triple.isLiteral" class="raw-uri" :title="triple.rawObject">
              {{ triple.rawObject }}
            </div>
            <div v-else class="literal-tag">Literal Value</div>
          </td>
        </tr>

        <tr v-if="triples.length === 0">
          <td :colspan="showActionColumn ? 4 : 3">
            {{ showActionColumn ? '' : 'No Triples found.' }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface EnhancedTriple {
  id: string | number
  subject: string
  rawSubject?: string
  predicate: string
  rawPredicate?: string
  object: string
  rawObject?: string
  isLiteral?: boolean
  action?: string
  raw?: any
}

const props = withDefaults(
  defineProps<{
    triples: EnhancedTriple[]
    disableSelection?: boolean
    showActionColumn?: boolean
  }>(),
  {
    disableSelection: false,
    showActionColumn: false,
  },
)

const hasActions = computed(() => props.triples.some((t) => !!t.action))

const emit = defineEmits<{
  (e: 'select-row', triple: EnhancedTriple): void
}>()

function handleRowClick(triple: EnhancedTriple) {
  if (!props.disableSelection) {
    emit('select-row', triple)
  }
}
</script>

<style scoped>
.triple-table {
  table-layout: fixed;
}

.triple-cell > * + * {
  margin-top: var(--paddingHalf);
}

.raw-uri {
  font-family: var(--dataFontStyle);
  color: var(--mutedFontColor);
  word-break: break-all;
  font-style: italic;
}

.literal-tag {
  color: var(--mutedFontColor);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.clickable-row:hover {
  background-color: var(--highlightColor);
  border-bottom: 2px solid var(--accentColor);
}

.action-tag {
  background: var(--borderColor);
  padding: 4px 8px;
  border-radius: 1px;
  font-size: 0.85em;
  font-weight: bold;
  text-transform: uppercase;
}

.action {
  text-align: center;
  width: 100px;
}
</style>
