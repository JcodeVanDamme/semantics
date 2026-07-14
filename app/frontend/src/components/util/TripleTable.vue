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
          <td :colspan="showActionColumn ? 4 : 3" class="empty-cell">
            {{ showActionColumn ? '' : 'No Triples found.' }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import type { EnhancedTriple } from '@/scripts/type.ts'

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

// 3. Strongly typed Emits
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
/* 1. Root Table Structure */
.triple-table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;
}

/* 2. Row Behaviors */
.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.clickable-row:hover {
  background-color: var(--highlightColor, #f5f5f5);
  border-bottom: 2px solid var(--accentColor, #007acc);
}

/* 3. Standard Cell Styling */
.triple-cell {
  padding: var(--paddingHalf, 8px);
}

.triple-cell > * + * {
  margin-top: var(--paddingHalf, 4px);
}

/* 4. Specific Column Definitions */
.action {
  text-align: center;
  width: 100px;
}

.action-tag {
  background: var(--borderColor, #ddd);
  padding: 4px 8px;
  border-radius: 1px;
  font-size: 0.85em;
  font-weight: bold;
  text-transform: uppercase;
}

.raw-uri {
  font-family: var(--dataFontStyle), monospace;
  color: var(--mutedFontColor, #666);
  word-break: break-all;
  font-style: italic;
  font-size: 0.9em;
}

.literal-tag {
  color: var(--mutedFontColor, #666);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 0.8em;
}
</style>
