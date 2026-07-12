import { useSemanticStore } from '../store/semanticStore'

const store = useSemanticStore()

export const mockApi = {

  /* =========================================
     CRUD
  ========================================= */

  getTriples(filters?: {
    s?: string
    p?: string
    o?: string
  }) {

    return store.triples.value.filter(triple => {

      return (

        (!filters?.s ||
          triple.subject
            .toLowerCase()
            .includes(filters.s.toLowerCase())) &&

        (!filters?.p ||
          triple.predicate
            .toLowerCase()
            .includes(filters.p.toLowerCase())) &&

        (!filters?.o ||
          triple.object
            .toLowerCase()
            .includes(filters.o.toLowerCase()))
      )
    })
  },

  createTriple(triple: {
    subject: string
    predicate: string
    object: string
  }) {

    store.triples.value.push({

      id: Date.now(),

      subject: triple.subject,

      predicate: triple.predicate,

      object: triple.object
    })
  },

  updateTriple(updatedTriple: any) {

    const index =
      store.triples.value.findIndex(

        triple =>
          triple.id === updatedTriple.id
      )

    if (index !== -1) {

      store.triples.value[index] = {
        ...updatedTriple
      }
    }
  },

  deleteTriple(id: number) {

    store.triples.value =
      store.triples.value.filter(

        triple => triple.id !== id
      )
  },

  sparqlQuery(query: string) {

    const q = query.toLowerCase()

    return store.triples.value.filter(triple => {

      return (

        triple.subject
          .toLowerCase()
          .includes(q) ||

        triple.predicate
          .toLowerCase()
          .includes(q) ||

        triple.object
          .toLowerCase()
          .includes(q)
      )
    })
  },

  /* =========================================
     OPERATIONS
  ========================================= */

  getStates() {

    return store.states.value
  },

  getHistory() {

    return store.history.value
  },

  getActiveStates() {

    return store.states.value.length
  },

  getStateChanges() {

    return (
      store.states.value.length -
      store.originalStateCount.value
    )
  },

  foundState(
    name: string,
    ruler: string,
    population: string,
    type: string
  ) {

    const newState = {

      id: Date.now(),

      name,

      ruler,

      title: 'RULER',

      mediatizedStates: 0,

      regions: 1,

      population,

      type,

      mediatizedList: []
    }

    store.states.value.push(newState)

    store.triples.value.push({

      id: Date.now() + 1,

      subject: name,

      predicate: 'ruler',

      object: ruler
    })

    store.history.value.push({

      id: Date.now(),

      type: 'FOUND_STATE',

      description:
        `${name} was founded.`,

      timestamp:
        new Date().toLocaleString()
    })
  },

  changeRuler(
    stateId: number,
    newRuler: string,
    title: string
  ) {

    const state =
      store.states.value.find(

        s => s.id === stateId
      )

    if (!state) {
      return
    }

    const oldRuler = state.ruler

    state.ruler = newRuler

    state.title = title

    const rulerTriple =
      store.triples.value.find(

        triple =>

          triple.subject === state.name &&
          triple.predicate === 'ruler'
      )

    if (rulerTriple) {

      rulerTriple.object = newRuler
    }

    store.history.value.push({

      id: Date.now(),

      type: 'CHANGE_RULER',

      description:
        `${oldRuler} was replaced by ${newRuler} in ${state.name}.`,

      timestamp:
        new Date().toLocaleString()
    })
  },

  mediatizate(
    stateId: number,
    targetState: {
      name: string
      type: string
    }
  ) {

    const state =
      store.states.value.find(
        s => s.id === stateId
      )

    if (!state) {
      return
    }

    state.mediatizedList.push({

      name: targetState.name,

      type: targetState.type
    })

    state.mediatizedStates++

    store.triples.value.push({

      id: Date.now(),

      subject: state.name,

      predicate: 'mediatizedInto',

      object: targetState.name
    })

    store.history.value.push({

      id: Date.now(),

      type: 'MEDIATIZATION',

      description:
        `${state.name} mediatized ${targetState.name}.`,

      timestamp:
        new Date().toLocaleString()
    })
  }
}
