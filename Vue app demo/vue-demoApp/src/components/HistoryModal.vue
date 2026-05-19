<template>
  <div class="overlay" @click.self="$emit('close')">
    <div class="history-modal">

      <!-- HEADER -->
      <div class="modal-header">
        <div class="title-row">
          <h2>HISTORY:</h2>

          <p>
            Listing all Events that took Place in the Region,
            along with the resulting Triple-Store-Actions
          </p>
        </div>

        <button class="close-btn" @click="$emit('close')">
          ✕
        </button>
      </div>

      <!-- EVENTS -->
      <div class="history-list">

        <!-- EVENT -->
        <div
          v-for="(event, index) in historyEvents"
          :key="index"
          class="history-event"
        >
          <!-- EVENT HEADER -->
          <div
            class="event-header"
            @click="toggleEvent(index)"
          >
            <div class="event-info">
              <div class="event-date">
                {{ event.date }} | {{ event.time }}
              </div>

              <div class="event-text">
                {{ event.text }}
              </div>
            </div>

            <div class="arrow">
              {{ openedEvents.includes(index) ? '▲' : '▼' }}
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
              v-for="(triple, tripleIndex) in event.triples"
              :key="tripleIndex"
              class="event-row"
            >
              <span>{{ triple.subject }}</span>
              <span>{{ triple.predicate }}</span>
              <span>{{ triple.object }}</span>
            </div>
          </div>

        </div>

      </div>

    </div>
  </div>
</template>

<script setup >
import { ref } from 'vue'


const openedEvents = ref([0])

const historyEvents = ref([
  {
    date: '00.00.0000',
    time: '00:00',
    text: 'The State of STATENAME STATENAME was mediatizated into',
    triples: [
      {
        subject: 'Subject',
        predicate: 'Predicate',
        object: 'Object'
      },
      {
        subject: 'Subject',
        predicate: 'Predicate',
        object: 'Object'
      },
      {
        subject: 'Subject',
        predicate: 'Predicate',
        object: 'Object'
      }
    ]
  },

  {
    date: '00.00.0000',
    time: '00:00',
    text: 'The State of STATENAME was founded',
    triples: [
      {
        subject: 'Subject',
        predicate: 'Predicate',
        object: 'Object'
      }
    ]
  },

  {
    date: '00.00.0000',
    time: '00:00',
    text: 'The Ruler of STATENAME changed from RULERNAME to RULERNAME',
    triples: [
      {
        subject: 'Subject',
        predicate: 'Predicate',
        object: 'Object'
      }
    ]
  }
])

function toggleEvent(index) {
  if (openedEvents.value.includes(index)) {
    openedEvents.value = openedEvents.value.filter(
      item => item !== index
    )
  } else {
    openedEvents.value.push(index)
  }
}
</script>

