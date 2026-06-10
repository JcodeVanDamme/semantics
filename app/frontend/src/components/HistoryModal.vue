<template>

  <div
    class="overlay"
    @click.self="$emit('close')"
  >

    <div class="history-modal">

      <!-- HEADER -->

      <div class="modal-header">

        <div class="title-row">

          <h2>HISTORY:</h2>

          <p>
            Listing all semantic events and
            resulting Triple-Store-Actions
            inside the shared semantic graph.
          </p>

        </div>

        <!-- CLOSE -->

        <button
          class="close-btn"
          @click="$emit('close')"
        >

          ✕

        </button>

      </div>

      <!-- HISTORY LIST -->

      <div class="history-list">

        <!-- EVENTS -->

        <div

          v-for="(event, index) in formattedHistory"

          :key="event.id"

          class="history-event"
        >

          <!-- EVENT HEADER -->

          <div
            class="event-header"
            @click="toggleEvent(index)"
          >

            <div class="event-info">

              <div class="event-date">

                {{ event.date }}
                |
                {{ event.time }}

              </div>

              <div class="event-text">

                {{ event.description }}

              </div>

            </div>

            <!-- ARROW -->

            <div class="arrow">

              {{
                openedEvents.includes(index)
                  ? '▲'
                  : '▼'
              }}

            </div>

          </div>

          <!-- EVENT BODY -->

          <div

            v-if="openedEvents.includes(index)"

            class="event-body"
          >

            <!-- TABLE HEADER -->

            <div class="event-table-header">

              <span>SUBJECT</span>

              <span>PREDICATE</span>

              <span>OBJECT</span>

            </div>

            <!-- TABLE ROWS -->

            <div

              v-for="(triple, tripleIndex)
                in event.triples"

              :key="tripleIndex"

              class="event-row"
            >

              <span>
                {{ triple.subject }}
              </span>

              <span>
                {{ triple.predicate }}
              </span>

              <span>
                {{ triple.object }}
              </span>

            </div>

          </div>

        </div>

        <!-- EMPTY -->

        <div
          v-if="formattedHistory.length === 0"
          class="empty-history"
        >

          No history events available.

        </div>

      </div>

    </div>

  </div>

</template>

<script setup lang="ts">

import {
  ref,
  computed
} from 'vue'

import {
  useSemanticStore
} from '../store/semanticStore'

/* =========================================
   EMITS
========================================= */

defineEmits([
  'close'
])

/* =========================================
   STORE
========================================= */

const store = useSemanticStore()

const history = store.history

const triples = store.triples

/* =========================================
   OPENED EVENTS
========================================= */

const openedEvents = ref<number[]>([])

/* =========================================
   FORMATTED HISTORY
========================================= */

const formattedHistory = computed(() => {

  return history.value.map(event => {

    const dateObject =
      new Date(event.timestamp)

    const date =
      dateObject.toLocaleDateString()

    const time =
      dateObject.toLocaleTimeString()

    /* =====================================
       EVENT TRIPLES
    ===================================== */

    let eventTriples: {
      subject: string
      predicate: string
      object: string
    }[] = []

    /* =====================================
       FOUND STATE
    ===================================== */

    if (
      event.type === 'FOUND_STATE'
    ) {

      const stateName =

        event.description
          .split(' was founded')[0]

      eventTriples =
        triples.value.filter(

          triple =>

            triple.subject ===
            stateName
        )
    }

    /* =====================================
       CHANGE RULER
    ===================================== */

    else if (
      event.type === 'CHANGE_RULER'
    ) {

      eventTriples =
        triples.value.filter(

          triple =>

            triple.predicate ===
            'ruler'
        )
    }

    /* =====================================
       MEDIATIZATION
    ===================================== */

    else if (
      event.type === 'MEDIATIZATION'
    ) {

      eventTriples =
        triples.value.filter(

          triple =>

            triple.predicate ===
            'mediatizedInto'
        )
    }

    /* =====================================
       RETURN EVENT
    ===================================== */

    return {

      id: event.id,

      date,

      time,

      description:
        event.description,

      triples:
        eventTriples
    }
  })
})

/* =========================================
   TOGGLE EVENT
========================================= */

function toggleEvent(index: number) {

  if (
    openedEvents.value.includes(index)
  ) {

    openedEvents.value =

      openedEvents.value.filter(

        item => item !== index
      )

  } else {

    openedEvents.value.push(index)
  }
}

</script>