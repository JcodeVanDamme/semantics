<template>
  <div>
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Ruler</th>
          <th>Med. Count</th>
          <th>Mediatized States</th>
          <th>Regions</th>
          <th>Population</th>
          <th>State Type</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="(state, index) in states" :key="state.name + index">
          <td class="state-cell">
            <div class="primary-text">{{ state.name }}</div>
          </td>

          <td class="state-cell">
            <div v-if="state.ruler">
              <strong>{{ state.ruler.title }}</strong> {{ state.ruler.name }}
            </div>
            <div v-else class="muted-text">No active ruler</div>
          </td>

          <td class="state-cell numeric-cell">
            {{ state.mediatizatedStates?.count ?? 0 }}
          </td>

          <td class="state-cell">
            <div
              v-for="(medState, mIdx) in state.mediatizatedStates?.states"
              :key="mIdx"
              class="sub-item"
            >
              <span class="sub-item-title">{{ medState.name }}</span>
              <span class="meta-tag">{{ medState.stateType }}</span>
            </div>
            <div v-if="!state.mediatizatedStates?.states?.length" class="muted-text">-</div>
          </td>

          <td class="state-cell">
            <div class="meta-summary">Total: {{ state.regions?.count ?? 0 }}</div>
            <div v-for="(region, rIdx) in state.regions?.regions" :key="rIdx" class="sub-item">
              <span>{{ region.name }}</span>
              <span class="meta-tag italic">{{ region.type }}</span>
            </div>
          </td>

          <td class="state-cell numeric-cell font-data">
            {{ state.population ? state.population.toLocaleString() : '0' }}
          </td>

          <td class="state-cell">
            <div class="type-badge">{{ state.stateType }}</div>
          </td>
        </tr>

        <tr v-if="states.length === 0">
          <td colspan="7" class="empty-banner">
            No States found matching the active specifications.
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
// ==========================================
// API Type Specification Specifications
// ==========================================
export interface Ruler {
  name: string
  title: string
}

export interface MediatizatedState {
  name: string
  stateType: string
}

export interface MediatizatedStatesData {
  count: number
  states: MediatizatedState[]
}

export interface Region {
  name: string
  type: string
}

export interface RegionsData {
  count: number
  regions: Region[]
}

export interface StateRecord {
  name: string
  ruler: Ruler
  mediatizatedStates: MediatizatedStatesData
  regions: RegionsData
  population: number
  stateType: string
}

// Props Configuration (Simplified down to read-only data)
defineProps<{
  states: StateRecord[]
}>()
</script>

<style scoped>
.state-cell {
  vertical-align: top;
  padding: var(--paddingHalf, 8px);
}

.state-cell > * + * {
  margin-top: 4px;
}

.primary-text {
  font-weight: 600;
}

.numeric-cell {
  text-align: right;
}

.font-data {
  font-family: var(--dataFontStyle, monospace);
}

/* Nested Object Item Stylings */
.sub-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9em;
  margin-bottom: 2px;
}

.sub-item-title {
  color: var(--mainFontColor, #333);
}

.meta-tag {
  font-family: var(--dataFontStyle, monospace);
  color: var(--mutedFontColor, #666);
  font-size: 0.85em;
}

.meta-tag.italic {
  font-style: italic;
}

.meta-summary {
  font-size: 0.8em;
  font-weight: bold;
  color: var(--mutedFontColor, #666);
  margin-bottom: 4px;
}

.type-badge {
  display: inline-block;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 0.85em;
  font-weight: 500;
}

.muted-text {
  color: var(--mutedFontColor, #999);
  font-style: italic;
}

.empty-banner {
  text-align: center;
  padding: 24px;
  color: var(--mutedFontColor, #666);
  font-style: italic;
}
</style>
