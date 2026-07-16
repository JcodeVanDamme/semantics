<template>
  <MainLayout>
    <HeaderOverview title="Regional Overview" description="View the current geopolitcal Climate and perform Territorial Changes." />

    <div class="dashboard-top">
      <div class="table-layout card">
        <div class="space-below">
          <h2>States</h2>
          <div class="divider accent"></div>
        </div>

        <StateTable
          :states="activeStates"
          :error-message="errorMessage"
          @row-clicked="handleStateSelection"
        />
      </div>

      <aside class="dashboard-sidebar">
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
              <button class="close-button button accent" @click="closePanel()">
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
                <td colspan="3" class="empty-cell">No mediatized states.</td>
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
      </aside>
    </div>

    <section class="table-layout card">
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
      <LatestActionTable :triples="eventTriples" :showActionColumn="true" mode="history" />
    </section>

    <FoundStateModal v-if="showFoundModal" @close="showFoundModal = false" @submit="handleSubmit" />
    <ChangeRulerModal
      v-if="showRulerModal"
      :state-uri="selectedState?.URI"
      @close="showRulerModal = false"
      @submit="handleSubmit"
    />
    <MediatizeModal
      v-if="showMediatizeModal"
      :acting-state="selectedState?.name"
      @close="showMediatizeModal = false"
      @submit="handleSubmit"
    />
    <HistoryModal v-if="showHistoryModal" @close="showHistoryModal = false" />
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Crown, Flag, X, TrendingUp, TrendingDown } from 'lucide-vue-next'
import { storeToRefs } from 'pinia'
import { useDashboardStore } from '@/stores/dashboardStore'

// Types
import type { StateData, HistoryEvent, EnhancedTriple, HistoryApiResponse } from '@/scripts/type.ts'

// Components
import MainLayout from '../layouts/MainLayout.vue'
import HistoryModal from '@/components/dashboard/HistoryModal.vue'
import FoundStateModal from '../components/dashboard/FoundStateModal.vue'
import ChangeRulerModal from '../components/dashboard/ChangeRulerModal.vue'
import MediatizeModal from '../components/dashboard/MediatizeModal.vue'
import LatestActionTable from '@/components/util/TripleTable.vue'
import StateTable from '@/components/dashboard/StatesTable.vue'
import HeaderOverview from '@/components/util/HeaderOverview.vue'

const store = useDashboardStore()
const { activeStates, activeStatesCount, stateChangesFactor, errorMessage } = storeToRefs(store)

const selectedState = ref<StateData | null>(null)
const eventTriples = ref<EnhancedTriple[]>([])
const latestEvent = ref<string>('None')

const showFoundModal = ref(false)
const showRulerModal = ref(false)
const showMediatizeModal = ref(false)
const showHistoryModal = ref(false)

onMounted(() => {
  store.fetchDashboardData()
})

const handleStateSelection = (state: StateData) => (selectedState.value = state)
const closePanel = () => (selectedState.value = null)

async function handleSubmit(payload: HistoryApiResponse) {
  if (payload.history && payload.history.length > 0) {
    updateLatestAction(payload.history[0])
  }
  await store.fetchDashboardData()
}

const updateLatestAction = (event: HistoryEvent) => {
  latestEvent.value = event.action.replace(/_/g, ' ')

  eventTriples.value = event.triples.map(
    (ta, index): EnhancedTriple => ({
      id: index,
      action: ta.action,
      subject: ta.triple.s.value,
      rawSubject: ta.triple.s.value,
      predicate: ta.triple.p.value,
      rawPredicate: ta.triple.p.value,
      object: ta.triple.o.value,
      rawObject: ta.triple.o.value,
      isLiteral: ta.triple.o.isLiteral,
      raw: ta.triple,
    }),
  )
}
</script>

<style scoped>
/* --- LAYOUT --- */
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

.table-layout {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: var(--paddingHalf);
}

/* --- SIDEBAR & STATS --- */
.dashboard-sidebar {
  width: 400px;
  display: flex;
}

.dashboard-side-cards {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: var(--padding);
}

.stats-card {
  padding: var(--paddingHalf) 60px;
  text-align: center;
  text-transform: uppercase;
}

.stats-card h3 {
  font-family: var(--fancyFontStyle);
  color: var(--accentColor);
  margin: 0;
}

.stats-card span {
  font-size: var(--text-h1);
  font-weight: lighter;
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

/* --- DETAILS PANEL --- */
.state-details-panel h3 {
  margin: 0;
  text-align: center;
}

.mini-table th,
td {
  text-align: center;
  padding: var(--paddingHalf);
}

.button-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
}

/* --- EVENTS --- */
.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.event-header-wrapper {
  display: flex;
  gap: var(--padding);
}

.minimize {
  font-size: var(--base-size);
  height: fit-content;
  padding: 5px var(--padding);
}
</style>
