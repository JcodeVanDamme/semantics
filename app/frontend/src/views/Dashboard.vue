<template>
  <MainLayout>
    <HeaderOverview title="Regional Overview" description="Gabba" />

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
      <LatestActionTable :triples="eventTriples" :showActionColumn="true" />
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

// Layouts & Components
import MainLayout from '../layouts/MainLayout.vue'
import HistoryModal from '@/components/dashboard/HistoryModal.vue'
import FoundStateModal from '../components/dashboard/FoundStateModal.vue'
import ChangeRulerModal from '../components/dashboard/ChangeRulerModal.vue'
import MediatizeModal from '../components/dashboard/MediatizeModal.vue'
import LatestActionTable from '@/components/util/TripleTable.vue'
import StateTable from '@/components/dashboard/StatesTable.vue'
import HeaderOverview from '@/components/util/HeaderOverview.vue'

// Store
const store = useDashboardStore()
const { activeStates, activeStatesCount, stateChangesFactor, errorMessage } = storeToRefs(store)

// State
const selectedState = ref<any>(null)
const eventTriples = ref<any[]>([])
const latestEvent = ref<string>('None')
const showFoundModal = ref(false)
const showRulerModal = ref(false)
const showMediatizeModal = ref(false)
const showHistoryModal = ref(false)

// Lifecycle
onMounted(() => {
  store.fetchDashboardData()
})

// Handlers
const handleStateSelection = (state: any) => (selectedState.value = state)
const closePanel = () => (selectedState.value = null)

async function handleSubmit(payload: any) {
  // Update UI immediately with response
  updateLatestAction(payload)
  // Refresh Store data
  await store.fetchDashboardData()
  // Close modals
  showFoundModal.value = false
  showRulerModal.value = false
  showMediatizeModal.value = false
}

const updateLatestAction = (payload: any) => {
  const latestEventData = payload.history?.[0]

  latestEvent.value = latestEventData.action.replace(/_/g, ' ')

  eventTriples.value = (latestEventData.triples || []).map((item: any, index: number) => ({
    id: index,
    action: item.action, // Top-level property, this worked before

    // Extract nested values to create the flat structure the table expects
    subject: item.triple.s.value,
    rawSubject: item.triple.s.value,

    predicate: item.triple.p.value,
    rawPredicate: item.triple.p.value,

    object: item.triple.o.value,
    rawObject: item.triple.o.value,

    isLiteral: item.triple.o.isLiteral,
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

.table-layout {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: var(--paddingHalf);
}

.dashboard-sidebar {
  width: 400px;
  display: flex;
}

/* Stats Card Styles */
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

/* Details Panel */
.dashboard-side-cards {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: var(--padding);
}
.event-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.event-header-wrapper {
  display: flex;
  gap: var(--padding);
}

.mini-table th {
  font-size: calc(var(--base-size) * 1.1);
}

.mini-table th,td {
  text-align: center;
  padding: var(--paddingHalf);
}
.button-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
}

.minimize {
  font-size: var(--base-size);
  height: fit-content;
  padding: 5px var(--padding);
}
.state-details-panel h3 {
  margin: 0;
  text-align: center;
}
</style>
