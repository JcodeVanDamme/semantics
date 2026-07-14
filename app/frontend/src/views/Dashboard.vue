<template>
  <MainLayout>
    <HeaderOverview title="Regional Overview" description="Gabba" />

    <div class="dashboard-top flex">
      <div class="table-header card flex">
        <div class="space-below">
          <h2>States</h2>

          <div class="divider accent"></div>
        </div>
        <p class="success-banner banner">Hallo</p>

        <StateTable
          :states="activeStates"
          :selected-state="selectedState"
          @row-clicked="handleStateSelection"
        />
        <div class="spacer"></div>
      </div>

      <div class="dashboard-sidebar">
        <div v-if="!selectedState" class="dashboard-side-cards">
          <div class="stats-card card">
            <h3>Active States</h3>
            <span>{{ activeStatesCount }}</span>
          </div>

          <div class="stats-card card">
            <h3>State Changes</h3>
            <div
              class="delta"
              :class="{ positive: stateChangesFactor > 0, negative: stateChangesFactor < 0 }"
            >
              <span>{{ stateChangesFactor }}</span>
              <TrendingUp v-if="stateChangesFactor > 0" :size="18" />
              <TrendingDown v-else-if="stateChangesFactor < 0" :size="18" />
            </div>
          </div>

          <button class="button accent" @click="showFoundModal = true">
            <Flag :size="18" /> FOUND STATE
          </button>
        </div>

        <div v-else class="state-details-panel card">
          <div class="details-header">
            <div class="top">
              <h2>{{ selectedState.name }}</h2>
              <button type="button" class="close-button button accent" @click="closePanel()">
                <X :size="18" />
              </button>
            </div>
            <div class="divider accent"></div>
          </div>

          <h3 class="upper">Mediatizated States</h3>

          <table class="mini-table">
            <thead>
              <tr>
                <th>NAME</th>
                <th>STATE TYPE</th>
                <th>FORMER RULER</th>
              </tr>
            </thead>
            <tbody>
              <template v-if="selectedState.mediatizatedStates.length > 0">
                <tr v-for="(item, index) in selectedState.mediatizatedStates" :key="index">
                  <td>{{ item.name }}</td>
                  <td>{{ item.stateType }}</td>
                  <td class="ruler">
                    <div>{{ item.ruler.name }}</div>
                    <div class="title">{{ item.ruler.title }}</div>
                  </td>
                </tr>
              </template>

              <tr v-else>
                <td colspan="3" class="empty-state">No mediatized states.</td>
              </tr>
            </tbody>
          </table>

          <div class="details-spacer"></div>

          <div class="button-wrapper">
            <button class="button accent" @click="showRulerModal = true">
              <Crown :size="18" /> CHANGE RULER
            </button>
            <button class="button accent" @click="showMediatizeModal = true">
              <Flag :size="18" /> MEDIATIZATE
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="table-header card">
      <div>
        <div class="event-header">
          <div class="event-header-wrapper">
            <h2>Latest Event:</h2>
            <h2 class="event-description">{{ latestEvent }}</h2>
          </div>
          <button class="button accent minimize" @click="showHistoryModal = true">
            View History
          </button>
        </div>
        <div class="divider accent"></div>
      </div>
      <LatestActionTable :triples="eventTriples" :showActionColumn="true" />
    </div>

    <FoundStateModal
      v-if="showFoundModal"
      @close="showFoundModal = false"
      @submit="handleFoundStateSubmit"
    />
    <ChangeRulerModal
      v-if="showRulerModal"
      :states="allStates"
      @close="showRulerModal = false"
      @submit="handleRulerSubmit"
    />
    <MediatizeModal
      v-if="showMediatizeModal"
      :acting-state="selectedState?.name"
      :states="activeStates"
      @close="showMediatizeModal = false"
      @submit="handleMediatizeSubmit"
    />
    <HistoryModal v-if="showHistoryModal" @close="showHistoryModal = false" />
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../services/api'
import MainLayout from '../layouts/MainLayout.vue'
import HistoryModal from '../components/new/HistoryModal.vue'
import FoundStateModal from '../components/new/FoundStateModal.vue'
import ChangeRulerModal from '../components/new/ChangeRulerModal.vue'
import MediatizeModal from '../components/new/MediatizeModal.vue'
import LatestActionTable from '@/components/new/TripleTable.vue'
import StateTable from '@/components/new/StatesTable.vue'
import HeaderOverview from '@/components/HeaderOverview.vue'
import { Crown, Flag, X, TrendingUp, TrendingDown } from 'lucide-vue-next'
import { type EnhancedTriple } from '../components/new/TripleTable.vue'

const activeStates = ref<any[]>([])
const allStates = ref<any[]>([])

const activeStatesCount = ref<any>()
const stateChangesFactor = ref<any>()

const showFoundModal = ref(false)
const showRulerModal = ref(false)
const showMediatizeModal = ref(false)
const showHistoryModal = ref(false)

const eventTriples = ref<EnhancedTriple[]>([])
const selectedState = ref<any>(null)
const latestEvent = ref('None')

const mediatizeModalRef = ref<InstanceType<typeof MediatizeModal> | null>(null)

const handleStateSelection = (state: any) => {
  selectedState.value = state
}

const closePanel = () => {
  selectedState.value = null
}

onMounted(async () => {
  try {
    const [activeRes, allRes] = await Promise.all([api.getActiveStates(), api.getStates()])
    activeStates.value = activeRes.states
    allStates.value = allRes.states

    console.log(activeStates)
  } catch (error) {
    console.error('Failed to load state data:', error)
  }
  loadStats()
})

const loadStats = async () => {
  try {
    const active = await api.getActiveStateCount()
    activeStatesCount.value = active.count

    const changes = await api.getStateChanges()
    stateChangesFactor.value = changes.factor
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
}

async function handleMediatizeSubmit(payload: { actingState: string; consumedState: string }) {
  try {
    const response = await api.mediatizate({
      absorbed: payload.consumedState,
      into: payload.actingState,
    })

    latestEvent.value = response.action
    eventTriples.value = mapActionsToEnhanced(response.triples || [])
    showMediatizeModal.value = false

    const activeRes = await api.getActiveStates()
    activeStates.value = activeRes.states

    loadStats()
  } catch (error) {
    console.error('Failed to mediatize state:', error)
    mediatizeModalRef.value?.setError('Mediatization failed on server.')
  }
}

function mapActionsToEnhanced(actions: TripleAction[]): EnhancedTriple[] {
  return actions.map((item, index) => ({
    id: index,
    action: item.action,
    subject: item.triple.s.value,
    predicate: item.triple.p.value,
    object: item.triple.o.value,
    raw: item.triple,
  }))
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

.banner {
  margin-top: 0;
}

.minimize {
  font-size: var(--base-size);
  height: fit-content;
  padding: 5px var(--padding);
}

.space-below {
  padding-bottom: var(--paddingHalf);
}

.dashboard-sidebar {
  max-width: 380px;
  width: 380px;
  display: flex;
}

.stats-card {
  gap: 0;
  padding: var(--paddingHalf) 60px;
  justify-content: center;
  text-align: center;
  text-transform: uppercase;
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
  flex: 1;
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

.details-header {
  display: flex;
  flex-direction: column;
}

.top {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex: 1;
}

.top h2 {
  margin-bottom: 0;
}

.text-wrapper * {
  margin: 0;
}

.state-details-panel h3 {
  margin: 0;
}

.mini-table th {
  font-size: calc(var(--base-size) * 1.2);
}

.details-spacer {
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: space-between;
}

.button-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
}

.event-header-wrapper {
  display: flex;
  flex-direction: row;
  gap: var(--padding);
}
</style>
