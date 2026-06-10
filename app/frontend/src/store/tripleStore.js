import { reactive } from 'vue'

export const store = reactive({

  /* =========================================
      STATES
  ========================================= */

  states: [
    {
      id: 1,
      name: 'Saxony',
      ruler: 'Frederick Augustus',
      mediatizedStates: [],
      population: '2.000.000',
      stateType: 'Kingdom'
    },

    {
      id: 2,
      name: 'Prussia',
      ruler: 'Frederick William',
      mediatizedStates: [],
      population: '5.000.000',
      stateType: 'Kingdom'
    }
  ],

  /* =========================================
      TRIPLES
  ========================================= */

  triples: [

    {
      subject: 'Saxony',
      predicate: 'hasRuler',
      object: 'Frederick Augustus'
    },

    {
      subject: 'Prussia',
      predicate: 'hasRuler',
      object: 'Frederick William'
    },

    {
      subject: 'Berlin',
      predicate: 'locatedIn',
      object: 'Prussia'
    }
  ],

  /* =========================================
      HISTORY
  ========================================= */

  historyEvents: [
    {
      date: '16.05.2026',
      time: '18:00',
      text: 'The State of Saxony was founded',

      triples: [
        {
          subject: 'Saxony',
          predicate: 'rdf:type',
          object: 'Kingdom'
        }
      ]
    }
  ]
})