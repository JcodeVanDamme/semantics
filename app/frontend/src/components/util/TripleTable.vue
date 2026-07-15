<template>
  <table class="triple-table">
    <thead>
      <tr>
        <th v-if="showActionColumn" scope="col" class="action">Action</th>
        <th scope="col">Subject</th>
        <th scope="col">Predicate</th>
        <th scope="col">Object</th>
      </tr>
    </thead>

    <tbody>
      <tr
        v-for="triple in triples"
        :key="triple.id"
        :class="{ 'clickable-row': !disableSelection }"
        @click="handleRowClick(triple)"
        @keydown.enter="handleRowClick(triple)"
        :tabindex="disableSelection ? -1 : 0"
        role="button"
        aria-label="Select triple"
      >
        <td v-if="showActionColumn" class="triple-cell action">
          <span class="action-tag" :class="triple.action.toLowerCase().replace(/\s+/g, '-')">{{
            triple.action
          }}</span>
        </td>

        <td class="triple-cell">
          <div class="main">{{ triple.subject }}</div>
          <div class="raw-uri" :title="triple.rawSubject">{{ triple.rawSubject }}</div>
        </td>

        <td class="triple-cell">
          <div class="main">{{ triple.predicate }}</div>
          <div class="raw-uri" :title="triple.rawPredicate">{{ triple.rawPredicate }}</div>
        </td>

        <td class="triple-cell">
          <div class="main">{{ triple.object }}</div>
          <div v-if="!triple.isLiteral" class="raw-uri" :title="triple.rawObject">
            {{ triple.rawObject }}
          </div>
          <div v-else class="literal-tag">Literal Value</div>
        </td>
      </tr>

      <tr v-if="triples.length === 0">
        <td :colspan="showActionColumn ? 4 : 3" class="empty-cell">No Triples found.</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup lang="ts">
import type { EnhancedTriple } from '@/scripts/type.ts'
// Optional: You could create a sub-component for the repeated cells
// but keeping it simple is often better for table performance.

const props = withDefaults(
  defineProps<{
    triples: EnhancedTriple[]
    disableSelection?: boolean
    showActionColumn?: boolean
  }>(),
  { disableSelection: false, showActionColumn: false },
)

const emit = defineEmits<{
  (e: 'select-row', triple: EnhancedTriple): void
}>()

function handleRowClick(triple: EnhancedTriple) {
  if (!props.disableSelection) emit('select-row', triple)
}
</script>

<style scoped>
.triple-table {
  table-layout: fixed;
  width: 100%;
  border-collapse: collapse;
}

.clickable-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.clickable-row:focus {
  outline: 2px solid var(--accentColor);
}

.clickable-row:hover {
  background-color: var(--highlightColor);
}

.triple-cell {
  padding: var(--paddingHalf);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action {
  text-align: center;
  width: 100px;
}

.main {
  padding-bottom: 5px;
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

.created {
  background-color: var(--successSecondaryColor);
}

.deleted {
  background-color: var(--errorSecondaryColor);
}
</style>
