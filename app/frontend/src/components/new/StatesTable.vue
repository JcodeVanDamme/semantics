<template>
  <div class="states-table">
    <table>
      <thead>
        <tr>
          <th class="col-small"></th>
          <th class="col-big">NAME</th>
          <th class="col-big">RULER</th>
          <th>MED. STATES</th>
          <th>REGIONS</th>
          <th>POPULATION</th>
          <th>STATE TYPE</th>
        </tr>
      </thead>
      <tbody v-for="state in states" :key="state.name" class="state-row-group">
        <tr
          class="main-row"
          :class="{ expanded: expandedRow === state.name }"
          @click="handleRowClick(state)"
        >
          <td>
            <button @click.stop="toggleExpand(state.name)" class="expand-btn">
              <CircleChevronRight
                class="expand-icon"
                :class="{ rotated: expandedRow === state.name }"
                :size="20"
              />
            </button>
          </td>
          <td>{{ state.name }}</td>
          <td class="ruler">
            <div>{{ state.ruler.name }}</div>
            <div class="title">{{ state.ruler.title }}</div>
          </td>
          <td>{{ state.mediatizatedStates.length }}</td>
          <td>{{ state.regions.length }}</td>
          <td>{{ formatNumber(state.population) }}</td>
          <td>{{ state.stateType }}</td>
        </tr>

        <tr class="details-row">
          <td colspan="7" class="expanded-cell">
            <div class="expand-wrapper" :class="{ expanded: expandedRow === state.name }">
              <div class="region-wrapper">
                <table class="region-table">
                  <thead>
                    <tr>
                      <th class="region-header">Regions</th>
                      <th>Name</th>
                      <th>Population</th>
                      <th>Type</th>
                      <th class="region-expand"></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="region in state.regions" :key="region.name">
                      <td></td>
                      <td>{{ region.name }}</td>
                      <td>{{ formatNumber(region.population) }}</td>
                      <td>{{ region.type }}</td>
                      <td class="region-expand"></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </td>
        </tr>

        <tr v-if="states.length === 0">
          <td colspan="7">No States found.</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { CircleChevronRight } from 'lucide-vue-next'

defineProps({
  states: {
    type: Array as any,
    required: true,
    default: () => [],
  },
})

const expandedRow = ref<string | null>(null)
const emit = defineEmits(['row-clicked'])

const toggleExpand = (name: string) => {
  expandedRow.value = expandedRow.value === name ? null : name
}

const handleRowClick = (state: any) => {
  console.log(state)
  emit('row-clicked', state)
}

const formatNumber = (num: number) => {
  return new Intl.NumberFormat('de-DE').format(num)
}
</script>

<style scoped>
.states-table {
  table-layout: fixed;
  width: 100%;
  position: relative;
  overflow-y: auto;
}

.col-big {
  width: 22%;
}
.col-small {
  width: 40px;
  padding: 0;
}

td,
th {
  text-align: center;
  padding: 10px;
}

.title {
  color: var(--borderColor);
  font-family: var(--fancyFontStyle);
}

.expand-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accentColor); /* Or var(--mainFontColor) */
  transition: opacity 0.2s;
}

.expanded-cell {
  padding: 0 !important;
  border: none;
}

.expand-wrapper {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 0.4s ease;
  background-color: var(--secondaryColor);
}

.region-wrapper {
  overflow: hidden;
}

.expand-wrapper.expanded {
  grid-template-rows: 1fr;
}

.main-row {
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.main-row:hover {
  background-color: var(--highlightColor);
  border-bottom: 2px solid var(--accentColor);
}

.expand-icon {
  transition: transform 0.3s ease;
}

.expand-icon.rotated {
  transform: rotate(90deg);
}

.region-wrapper {
  display: flex;
  flex-direction: row;
}

.region-header {
  color: var(--accentColor);
  font-size: var(--text-h3) !important;
}

.region-table th {
  font-size: calc(var(--base-size) * 1.2);
  border-bottom: 2px solid var(--accentColor);
  background-color: color-mix(in srgb, var(--secondaryColor), var(--accentColor) 10%);
}

.region-table th:not(.region-expand),
.region-table td:not(.region-expand) {
  white-space: nowrap;
  width: 1px;
}

.region-expand {
  text-align: left;
  width: 100%;
}

.details-row {
  height: fit-content;
}
</style>
