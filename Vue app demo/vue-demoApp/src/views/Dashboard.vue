<template>
  <MainLayout>
    <!-- HEADER -->
    <div class="overview-section">

      <h1>Regional Overview</h1>
      <p>
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
        sed diam nonumy eirmod tempor invidunt ut labore et dolore.
      </p>

    </div>

    <!-- TOP SECTION -->
    <div class="dashboard-top">

      <!-- STATES TABLE -->
      <div class="states-table">
        
        <div class="section-title">
          STATES
        </div>

        <table>
          <thead>
            <tr>
              <th>NAME</th>
              <th>RULER</th>
              <th>MED. STATES</th>
              <th>REGIONS</th>
              <th>POPULATION</th>
              <th>STATE TYPE</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(state, index) in states"
              :key="index"
              @click="selectState(state)"
              :class="{ selected: selectedState?.name === state.name }">

              <td>{{ state.name }}</td>
              <td>
                <div>{{ state.ruler }}</div>
                <small>{{ state.title }}</small>
              </td>
              <td>{{ state.mediatizedStates }}</td>
              <td>{{ state.regions }}</td>
              <td>{{ state.population }}</td>
              <td>{{ state.type }}</td>

            </tr>
          </tbody>
        </table>
      </div>

      <!-- RIGHT SIDE PANEL -->
    <div v-if="!selectedState" class="dashboard-side-cards">
      
      <div class="stats-card">
        <h3>TOTAL STATES</h3>
        <span>{{ states.length }}</span>
      </div>

      <div class="stats-card">
        <h3>STATE CHANGES</h3>
        <span>-00 ↘</span>
      </div>

      <button class="found-btn" @click="showFoundModal = true" >
        <Flag :size="18" />
        FOUND STATE
      </button>
    </div>

    <div v-if="selectedState" class="state-details-panel">
      
      <!-- HEADER -->
      <div class="details-header">
          <span>{{ selectedState.name }}</span>
          <button @click="closePanel">
            <X :size="16" />
          </button>
      </div>

      <!-- CONTENT -->
      <div class="mediatized-title">
        MEDIATIZATED STATES
      </div>
      
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
      
      <!-- BUTTONS -->
      <button class="action-btn" @click="changeRuler">
        <Crown :size="18" />
        CHANGE RULER
      </button>
      <button class="action-btn" @click="mediatizate">
        <Flag :size="18" />
        MEDIATIZATE
      </button>
    </div></div>

    <!-- LATEST EVENT -->
    <div class="latest-event">
      <div class="event-header">
        
        <div class="section-title">
          LATEST EVENT
        </div>
        <button class="history-btn" @click="showHistoryModal = true">
          VIEW HISTORY
        </button>

      </div>
      <p class="event-description">
        {{ latestEvent }}
      </p>

      <table>
        <thead>
          <tr>
            <th>SUBJECT</th>
            <th>PREDICATE</th>
            <th>OBJECT</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(event, index) in eventTriples" :key="index">
            <td>{{ event.subject }}</td>
            <td>{{ event.predicate }}</td>
            <td>{{ event.object }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- =====================================
     FOUND MODAL
===================================== -->
    <div v-if="showFoundModal" class="modal-overlay">
      <div class="modal-box found-modal">
        <div class="modal-header">
          
          <div>
            <h2>STATE FOUNDING:</h2>
            <p>
              Fill out the Fields below in order
              for your new State to be created
            </p>
          </div>

          <button @click="showFoundModal = false">
            <X :size="18" />
          </button>
        
        </div>
        <div class="modal-form">
          
          <input v-model="newState.name" placeholder="Statename"/>
          <input v-model="newState.ruler" placeholder="Ruler"/>
          <input v-model="newState.population" placeholder="Population"/>
          <input v-model="newState.type" placeholder="Statetype"/>
          <button class="modal-action-btn" @click="foundState">
            <Flag :size="18" />
            FOUND STATE
          </button>
        
        </div>
      </div>
    </div>

    <!-- =====================================
        RULER MODAL
    ===================================== -->

    <div v-if="showRulerModal" class="modal-overlay">
      <div class="modal-box">
        <div class="modal-header">
          
          <div>
            <h2>RULER CHANGE:</h2>
            <p>
              Select the new Ruler
              for your selected State
            </p>
          </div>

          <button @click="showRulerModal = false">
            <X :size="18" />
          </button>

        </div>
        
        <input v-model="rulerSearch" class="modal-search" placeholder="Search by Name or Title"/>
        
        <div class="modal-list">
         
          <div
            v-for="(ruler, index) in filteredRulers"
            :key="index"
            class="modal-row"
            :class="{
              selected:
              selectedRuler?.name === ruler.name
            }"
            @click="selectedRuler = ruler"
          >
            <div>{{ ruler.name }}</div>
            <small>{{ ruler.title }}</small>
          </div>

        </div>
        
        <button class="modal-action-btn" @click="applyRulerChange">
          <Crown :size="18" />
          CHANGE RULER
        </button>
      </div>
    </div>

    <!-- =====================================
        MEDIATIZE MODAL
    ===================================== -->
    <div v-if="showMediatizeModal" class="modal-overlay">
      <div class="modal-box large-modal">
        <div class="modal-header">
          
          <div>
            <h2>MEDIATIZATION:</h2>
            <p>
              Select the target State
              this one will be mediatized into
            </p>
          </div>

          <button @click="showMediatizeModal = false">
            <X :size="18" />
          </button>

        </div>

      <input v-model="mediatizeSearch" class="modal-search" placeholder="Search by Statename, Ruler or Statetype"/>

      <table class="modal-table">
        <thead>
          <tr>
            <th>NAME</th>
            <th>RULER</th>
            <th>MED. STATES</th>
            <th>REGIONS</th>
            <th>POPULATION</th>
            <th>STATE TYPE</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(state, index) in filteredStates"
            :key="index"
            @click="selectedMediatizeTarget = state"
            :class="{
              selected:
              selectedMediatizeTarget?.name === state.name
            }"
          >
            <td>{{ state.name }}</td>
            <td>{{ state.ruler }}</td>
            <td>{{ state.mediatizedStates }}</td>
            <td>{{ state.regions }}</td>
            <td>{{ state.population }}</td>
            <td>{{ state.type }}</td>
          </tr>
        </tbody>
      </table>

      <button class="modal-action-btn" @click="applyMediatization">
        <Flag :size="18" />
        MEDIATIZATE
      </button>
    </div>
  </div>
</MainLayout>
<HistoryModal v-if="showHistoryModal" @close="showHistoryModal = false"/>
</template>

<script setup lang="ts">
import {
  ref,
  computed
} from 'vue'

import MainLayout from '../layouts/MainLayout.vue'
import HistoryModal from '../components/HistoryModal.vue'

const showHistoryModal = ref(false)

import {
  Crown,
  Flag,
  X
} from 'lucide-vue-next'

/* =========================================
   TYPES
========================================= */

interface MediatizedState {
  name: string
  type: string
}

interface State {
  name: string
  ruler: string
  title: string
  mediatizedStates: number
  regions: number
  population: string
  type: string

  mediatizedList: MediatizedState[]
}

/* =========================================
   DATA
========================================= */

const states = ref<State[]>([
  {
    name: 'Saxony',
    ruler: 'Frederick Augustus',
    title: 'KING',
    mediatizedStates: 2,
    regions: 14,
    population: '2.000.000',
    type: 'Kingdom',

    mediatizedList: [
      {
        name: 'Leipzig',
        type: 'Duchy'
      },
      {
        name: 'Dresden',
        type: 'County'
      }
    ]
  },

  {
    name: 'Prussia',
    ruler: 'Frederick William',
    title: 'KING',
    mediatizedStates: 3,
    regions: 20,
    population: '5.000.000',
    type: 'Kingdom',

    mediatizedList: [
      {
        name: 'Berlin',
        type: 'Capital'
      },
      {
        name: 'Brandenburg',
        type: 'Province'
      }
    ]
  },

  {
    name: 'France',
    ruler: 'Napoleon',
    title: 'EMPEROR',
    mediatizedStates: 4,
    regions: 40,
    population: '12.000.000',
    type: 'Empire',

    mediatizedList: [
      {
        name: 'Paris',
        type: 'Capital'
      }
    ]
  }
])

/* =========================================
   SELECTION
========================================= */

const selectedState = ref<State | null>(null)

  /* =========================================
   MODALS
========================================= */

const showFoundModal = ref(false)

const showRulerModal = ref(false)

const showMediatizeModal = ref(false)

/* =========================================
   SEARCHES
========================================= */

const rulerSearch = ref('')

const mediatizeSearch = ref('')

/* =========================================
   FOUND STATE FORM
========================================= */

const newState = ref({
  name: '',
  ruler: '',
  population: '',
  type: ''
})

/* =========================================
   AVAILABLE RULERS
========================================= */

const rulers = ref([
  {
    name: 'Napoleon',
    title: 'EMPEROR'
  },
  {
    name: 'Frederick Augustus',
    title: 'KING'
  },
  {
    name: 'Alexander I',
    title: 'TSAR'
  },
  {
    name: 'Francis II',
    title: 'EMPEROR'
  }
])

const selectedRuler = ref<any>(null)

/* =========================================
   MEDIATIZE TARGET
========================================= */

const selectedMediatizeTarget = ref<any>(null)

/* =========================================
   EVENTS
========================================= */

const latestEvent = ref(
  'Select a state to view events.'
)

const eventTriples = ref([
  {
    subject: 'Subject',
    predicate: 'Predicate',
    object: 'Object'
  }
])

/* =========================================
   SELECT STATE
========================================= */

function selectState(state: State) {

  selectedState.value = state

  latestEvent.value =
    `${state.ruler} currently rules ${state.name}.`

  eventTriples.value = [
    {
      subject: state.ruler,
      predicate: 'rules',
      object: state.name
    },
    {
      subject: state.name,
      predicate: 'hasPopulation',
      object: state.population
    }
  ]
}

/* =========================================
   CLOSE PANEL
========================================= */

function closePanel() {

  selectedState.value = null

}

/* =========================================
   CHANGE RULER
========================================= */

function changeRuler() {

  showRulerModal.value = true

}

/* =========================================
   MEDIATIZATE
========================================= */

function mediatizate() {

  showMediatizeModal.value = true

}
/* =========================================
   FOUND STATE
========================================= */

function foundState() {

  if (
    !newState.value.name ||
    !newState.value.ruler ||
    !newState.value.population ||
    !newState.value.type
  ) {
    return
  }

  states.value.push({
    name: newState.value.name,
    ruler: newState.value.ruler,
    title: 'RULER',
    mediatizedStates: 0,
    regions: 1,
    population: newState.value.population,
    type: newState.value.type,

    mediatizedList: []
  })

  latestEvent.value =
    `${newState.value.name} was founded.`

  showFoundModal.value = false

  newState.value = {
    name: '',
    ruler: '',
    population: '',
    type: ''
  }
}

/* =========================================
   APPLY RULER
========================================= */

function applyRulerChange() {

  if (
    !selectedState.value ||
    !selectedRuler.value
  ) {
    return
  }

  selectedState.value.ruler =
    selectedRuler.value.name

  selectedState.value.title =
    selectedRuler.value.title

  latestEvent.value =
    `${selectedRuler.value.name} became ruler of ${selectedState.value.name}.`

  showRulerModal.value = false
}

/* =========================================
   APPLY MEDIATIZATION
========================================= */

function applyMediatization() {

  if (
    !selectedState.value ||
    !selectedMediatizeTarget.value
  ) {
    return
  }

  selectedState.value.mediatizedList.push({
    name: selectedMediatizeTarget.value.name,
    type: selectedMediatizeTarget.value.type
  })

  selectedState.value.mediatizedStates++

  latestEvent.value =
    `${selectedState.value.name} mediatized ${selectedMediatizeTarget.value.name}.`

  showMediatizeModal.value = false
}

/* =========================================
   FILTERED RULERS
========================================= */

const filteredRulers = computed(() => {

  return rulers.value.filter((ruler) => {

    return ruler.name
      .toLowerCase()
      .includes(rulerSearch.value.toLowerCase())

  })

})

/* =========================================
   FILTERED STATES
========================================= */

const filteredStates = computed(() => {

  return states.value.filter((state) => {

    return state.name
      .toLowerCase()
      .includes(mediatizeSearch.value.toLowerCase())

  })

})
</script>