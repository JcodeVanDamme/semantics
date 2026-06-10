import { ref } from 'vue'

/* =========================================
   TYPES
========================================= */

export interface Triple {

  id: number

  subject: string

  predicate: string

  object: string
}

export interface MediatizedState {

  name: string

  type: string
}

export interface State {

  id: number

  name: string

  ruler: string

  title: string

  mediatizedStates: number

  regions: number

  population: string

  type: string

  mediatizedList: MediatizedState[]
}

export interface HistoryEvent {

  id: number

  type: string

  description: string

  timestamp: string
}

/* =========================================
   GLOBAL STATE
========================================= */

const triples = ref<Triple[]>([

  {
    id: 1,
    subject: 'Saxony',
    predicate: 'ruler',
    object: 'Frederick Augustus'
  },

  {
    id: 2,
    subject: 'Prussia',
    predicate: 'ruler',
    object: 'Frederick William'
  }

])

const states = ref<State[]>([

  {
    id: 1,

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
    id: 2,

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
      }
    ]
  }
])

const history = ref<HistoryEvent[]>([])

const originalStateCount = ref(
  states.value.length
)

/* =========================================
   EXPORT STORE
========================================= */

export function useSemanticStore() {

  return {

    triples,

    states,

    history,

    originalStateCount
  }
}