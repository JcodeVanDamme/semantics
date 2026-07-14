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

      <tbody v-if="loading">
        <tr>
          <td class="empty-cell" colspan="7">
            <p>Loading states...</p>
          </td>
        </tr>
      </tbody>

      <tbody v-else-if="errorMessage || states.length === 0">
        <tr>
          <td class="empty-cell" colspan="7">
            <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
            <p v-else>No States Found.</p>
          </td>
        </tr>
      </tbody>

      <tbody v-else v-for="state in states" :key="state.name" class="state-row-group">
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
                      <th colspan="3"></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="region in state.regions" :key="region.name">
                      <td></td>
                      <td>{{ region.name }}</td>
                      <td>{{ formatNumber(region.population) }}</td>
                      <td>{{ region.type }}</td>
                      <td colspan="3"></td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { CircleChevronRight } from 'lucide-vue-next'
import type { StateData } from '@/scripts/type.ts'
import { storeToRefs } from 'pinia'
import { useDashboardStore } from '@/stores/dashboardStore'

defineProps<{
  states: StateData[]
  errorMessage?: string | null
}>()

const expandedRow = ref<string | null>(null)
const emit = defineEmits<{
  (e: 'row-clicked', state: StateData): void
}>()

const toggleExpand = (name: string) => {
  expandedRow.value = expandedRow.value === name ? null : name
}

const handleRowClick = (state: StateData) => {
  emit('row-clicked', state)
}

const formatNumber = (num: number) => {
  return new Intl.NumberFormat('de-DE').format(num)
}

const store = useDashboardStore()
const { loading } = storeToRefs(store)
</script>

<style scoped>
/* 1. Table Wrapper & Layout */
.states-table {
  table-layout: fixed;
  width: 100%;
  position: relative;
  overflow-y: auto;
}

td,
th {
  text-align: center;
  padding: 10px;
}

.col-big {
  width: 22%;
}
.col-small {
  width: 40px;
  padding: 0;
}

/* 2. Main Row */
.main-row {
  transition: background-color 0.2s ease;
  cursor: pointer;
}

.main-row:hover {
  background-color: var(--highlightColor);
  border-bottom: 2px solid var(--accentColor);
}

.expand-btn {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accentColor);
  transition: opacity 0.2s;
}

.expand-icon {
  transition: transform 0.3s ease;
}

.expand-icon.rotated {
  transform: rotate(90deg);
}

/* 3. Detail Row (Expansion) */
.details-row {
  height: fit-content;
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

.expand-wrapper.expanded {
  grid-template-rows: 1fr;
}

.region-wrapper {
  overflow: hidden;
  display: flex;
  flex-direction: row;
}

/* 4. Region Table */
.region-table th {
  font-size: calc(var(--base-size) * 1.1);
  border-bottom: 2px solid var(--accentColor);
  background-color: color-mix(in srgb, var(--secondaryColor), var(--accentColor) 10%);
}
.region-table th.region-header {
  font-size: calc(var(--base-size) * 1.2);
  color: var(--accentColor);
}

.region-table th:not(.region-expand),
.region-table td:not(.region-expand) {
  white-space: nowrap;
  width: 1px;
}
.error-container {
  padding: var(--padding);
}
</style>
