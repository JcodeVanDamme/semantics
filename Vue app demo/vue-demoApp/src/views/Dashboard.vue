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

/* =========================================
   IMPORTS
========================================= */

import {
  ref,
  computed
} from 'vue'

import MainLayout from '../layouts/MainLayout.vue'

import HistoryModal from '../components/HistoryModal.vue'

import {
  Crown,
  Flag,
  X
} from 'lucide-vue-next'

import { api } from '../services/api'

import {
  useSemanticStore
} from '../store/semanticStore'

/* =========================================
   STORE
========================================= */

const store = useSemanticStore()

const states = store.states

const history = store.history

/* =========================================
   MODALS
========================================= */

const showHistoryModal = ref(false)

const showFoundModal = ref(false)

const showRulerModal = ref(false)

const showMediatizeModal = ref(false)

/* =========================================
   SELECTION
========================================= */

const selectedState = ref<any>(null)

/* =========================================
   SEARCH
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
   RULERS
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
   MEDIATIZATION
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

function selectState(state: any) {

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

  if (!selectedState.value) {
    return
  }

  showRulerModal.value = true
}

/* =========================================
   APPLY RULER CHANGE
========================================= */

function applyRulerChange() {

  if (
    !selectedState.value ||
    !selectedRuler.value
  ) {
    return
  }

  api.changeRuler(

    selectedState.value.id,

    selectedRuler.value.name,

    selectedRuler.value.title
  )

  latestEvent.value =
    `${selectedRuler.value.name} became ruler of ${selectedState.value.name}.`

  eventTriples.value = [

    {
      subject: selectedRuler.value.name,
      predicate: 'rules',
      object: selectedState.value.name
    }
  ]

  showRulerModal.value = false
}

/* =========================================
   MEDIATIZE
========================================= */

function mediatizate() {

  if (!selectedState.value) {
    return
  }

  showMediatizeModal.value = true
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

  api.mediatizate(

    selectedState.value.id,

    {
      name:
        selectedMediatizeTarget.value.name,

      type:
        selectedMediatizeTarget.value.type
    }
  )

  latestEvent.value =
    `${selectedState.value.name} mediatized ${selectedMediatizeTarget.value.name}.`

  eventTriples.value = [

    {
      subject: selectedState.value.name,
      predicate: 'mediatizedInto',
      object: selectedMediatizeTarget.value.name
    }
  ]

  showMediatizeModal.value = false
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

  api.foundState(

    newState.value.name,

    newState.value.ruler,

    newState.value.population,

    newState.value.type
  )

  latestEvent.value =
    `${newState.value.name} was founded.`

  eventTriples.value = [

    {
      subject: newState.value.name,
      predicate: 'ruler',
      object: newState.value.ruler
    }
  ]

  showFoundModal.value = false

  newState.value = {

    name: '',

    ruler: '',

    population: '',

    type: ''
  }
}

/* =========================================
   FILTERED RULERS
========================================= */

const filteredRulers = computed(() => {

  return rulers.value.filter((ruler) => {

    return ruler.name
      .toLowerCase()
      .includes(
        rulerSearch.value.toLowerCase()
      )
  })
})

/* =========================================
   FILTERED STATES
========================================= */

const filteredStates = computed(() => {

  return states.value.filter((state) => {

    return state.name
      .toLowerCase()
      .includes(
        mediatizeSearch.value.toLowerCase()
      )
  })
})

</script>