<template>
  <div>
    <table class="triple-table">
      <thead>
        <tr>
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
          <td colspan="3">No Triples found.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
// Define an extended local interface that supports the raw layout keys
export interface EnhancedTriple {
  id: string | number
  subject: string
  rawSubject: string
  predicate: string
  rawPredicate: string
  object: string
  rawObject: string
  isLiteral?: boolean
  raw?: any // Attached raw layout object if present
}

// Added the disableSelection prop option (defaults to false)
const props = withDefaults(
  defineProps<{
    triples: EnhancedTriple[]
    disableSelection?: boolean
  }>(),
  {
    disableSelection: false,
  },
)

// Explicitly declare the custom row selection emit type configuration
const emit = defineEmits<{
  (e: 'select-row', triple: EnhancedTriple): void
}>()

// Fires the event only if selection mechanism isn't explicitly disabled
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
</style>
