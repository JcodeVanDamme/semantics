<template>
  <MainLayout>
    <HeaderOverview title="Regional Overview" description="Gabba" />

    <div class="dashboard-top flex">
      <div class="table-header card flex">
        <div class="space-below">
          <h2>States</h2>
          <div class="divider accent"></div>
        </div>
        <StateTable :states="states" :selected-state="selectedState" @select-state="selectState" />
        <div class="spacer"></div>
      </div>

      <div v-if="!selectedState" class="dashboard-side-cards">
        <div class="stats-card card">
          <h3>TOTAL STATES</h3>
          <span>{{ states.length }}</span>
        </div>
        <div class="stats-card card">
          <h3>STATE CHANGES</h3>
          <div class="delta">
            <span>-00</span>
            <TrendingUp />
          </div>
        </div>
        <button class="button accent" @click="showFoundModal = true">
          <Flag :size="18" /> FOUND STATE
        </button>
      </div>

      <div v-if="selectedState" class="state-details-panel">
        <div class="details-header">
          <span>{{ selectedState.name }}</span>
          <button @click="closePanel"><X :size="16" /></button>
        </div>
        <div class="mediatized-title">MEDIATIZATED STATES</div>
        <table class="mini-table">
          <thead>
            <tr>
              <th>NAME</th>
              <th>STATE TYPE</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in selectedState.mediatizedList" :key="index">
              <td>{{ item.name }}</td>
              <td>{{ item.type }}</td>
            </tr>
          </tbody>
        </table>
        <button class="action-btn" @click="changeRuler"><Crown :size="18" /> CHANGE RULER</button>
        <button class="action-btn" @click="mediatizate"><Flag :size="18" /> MEDIATIZATE</button>
      </div>
    </div>

    <div class="table-header card">
      <div>
        <div class="event-header">
          <h2>Latest Event</h2>
          <button class="button accent minimize" @click="showHistoryModal = true">
            View History
          </button>
        </div>
        <div class="divider accent"></div>
        <p class="event-description">{{ latestEvent }}</p>
      </div>
      <!-- TRIPLES TABLE COMPONENT -->
      <TriplesTable :triples="eventTriples" @select-row="handleTripleSelect" />
    </div>

    <!-- MODAL OVERLAYS -->
    <FoundStateModal
      v-if="showFoundModal"
      @close="showFoundModal = false"
      @submit="handleFoundStateSubmit"
    />

    <ChangeRulerModal
      v-if="showRulerModal"
      :rulers="rulers"
      @close="showRulerModal = false"
      @submit="handleChangeRulerSubmit"
    />

    <MediatizeModal
      v-if="showMediatizeModal"
      :states="states"
      @close="showMediatizeModal = false"
      @submit="handleMediatizeSubmit"
    />

    <HistoryModal v-if="showHistoryModal" @close="showHistoryModal = false" />
  </MainLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MainLayout from '../layouts/MainLayout.vue'
import HistoryModal from '../components/HistoryModal.vue'
import FoundStateModal from '../components/new/FoundStateModal.vue'
import ChangeRulerModal from '../components/new/ChangeRulerModal.vue'
import MediatizeModal from '../components/new/MediatizeModal.vue'
import TriplesTable from '@/components/new/TripleTable.vue'
import StateTable from '@/components/new/StateTable.vue'
import { Crown, Flag, X, TrendingUp, TrendingDown } from 'lucide-vue-next'
import { api } from '../services/api'
import { useSemanticStore } from '../store/semanticStore'
import HeaderOverview from '@/components/HeaderOverview.vue'

const store = useSemanticStore()
const states = store.states

const showHistoryModal = ref(false)
const showFoundModal = ref(false)
const showRulerModal = ref(false)
const showMediatizeModal = ref(false)

const selectedState = ref<any>(null)

const rulers = ref([
  { name: 'Napoleon', title: 'EMPEROR' },
  { name: 'Frederick Augustus', title: 'KING' },
  { name: 'Alexander I', title: 'TSAR' },
  { name: 'Francis II', title: 'EMPEROR' },
])

const latestEvent = ref('Select a state to view events.')
const eventTriples = ref<Array<{ subject: string; predicate: string; object: string }>>([
  { subject: 'Subject', predicate: 'Predicate', object: 'Object' },
])

function selectState(state: any) {
  selectedState.value = state
  latestEvent.value = `${state.ruler} currently rules ${state.name}.`
  eventTriples.value = [
    { subject: state.ruler, predicate: 'rules', object: state.name },
    { subject: state.name, predicate: 'hasPopulation', object: state.population },
  ]
}

function closePanel() {
  selectedState.value = null
}

function changeRuler() {
  if (selectedState.value) showRulerModal.value = true
}

function handleChangeRulerSubmit(ruler: any) {
  api.changeRuler(selectedState.value.id, ruler.name, ruler.title)
  latestEvent.value = `${ruler.name} became ruler of ${selectedState.value.name}.`
  eventTriples.value = [
    { subject: ruler.name, predicate: 'rules', object: selectedState.value.name },
  ]
  showRulerModal.value = false
}

function mediatizate() {
  if (selectedState.value) showMediatizeModal.value = true
}

function handleMediatizeSubmit(targetState: any) {
  api.mediatizate(selectedState.value.id, { name: targetState.name, type: targetState.type })
  latestEvent.value = `${selectedState.value.name} mediatized ${targetState.name}.`
  eventTriples.value = [
    { subject: selectedState.value.name, predicate: 'mediatizedInto', object: targetState.name },
  ]
  showMediatizeModal.value = false
}

function handleFoundStateSubmit(payload: any) {
  api.foundState(payload.name, payload.ruler, payload.population, payload.type)
  latestEvent.value = `${payload.name} was founded.`
  eventTriples.value = [{ subject: payload.name, predicate: 'ruler', object: payload.ruler }]
  showFoundModal.value = false
}

function handleTripleSelect(triple: any) {
  console.log('Selected semantic statement:', triple)
}
</script>

<style scoped>
.dashboard-top {
  display: flex;
  flex-direction: row;
  gap: var(--padding);
}

@media (max-width: 900px) {
  .dashboard-top {
    flex-direction: column;
  }
}

.table-header {
  display: flex;
  gap: 0;
}

.event-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.minimize {
  font-size: var(--base-size);
  height: fit-content;
  padding: 5px var(--padding);
}

.space-below {
  padding-bottom: var(--paddingHalf);
}

.stats-card {
  gap: 0;
  padding: var(--paddingHalf) 60px;
  justify-content: center;
  text-align: center;
}

.stats-card h3 {
  font-family: var(--fancyFontStyle);
  color: var(--accentColor);
  margin: 0;
}
.stats-card span {
  font-family: var(--baseFontStyle);
  color: var(--mainFontColor);
  font-size: var(--text-h1);
  font-weight: lighter;
}

.dashboard-side-cards {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
}

.delta {
  display: flex;
  justify-content: center;
}

.delta svg {
  color: var(--accentColor);
  width: 40px;
  height: 40px;
  align-self: flex-end;
}

.flex {
  display: flex;
  flex: 1; /* Spreads out horizontally */
  align-self: stretch; /* Stretches vertically */
}
</style>
