<template>

  <MainLayout>

    <!-- HEADER -->

    <div class="overview-section">

      <h1>Triple Explorer</h1>

      <p>
        Explore and filter semantic triples
        from the shared semantic knowledge graph.
      </p>

    </div>

    <!-- QUERY CARD -->

    <div class="query-card">

      <div class="query-title">

        <Search :size="20" />

        TRIPLE QUERY

      </div>

      <!-- INPUT GRID -->

      <div class="triple-grid">

        <!-- SUBJECT -->

        <div class="query-input-group">

          <div class="input-header">

            <span>Subject</span>

            <small>

              <CircleQuestionMark :size="14" />

              Insert Variable

            </small>

          </div>

          <input
            v-model="subject"
            type="text"
            placeholder="e.g. Napoleon"
          />

        </div>

        <!-- PREDICATE -->

        <div class="query-input-group">

          <div class="input-header">

            <span>Predicate</span>

            <small>

              <CircleQuestionMark :size="14" />

              Insert Variable

            </small>

          </div>

          <input
            v-model="predicate"
            type="text"
            placeholder="e.g. rules"
          />

        </div>

        <!-- OBJECT -->

        <div class="query-input-group">

          <div class="input-header">

            <span>Object</span>

            <small>

              <CircleQuestionMark :size="14" />

              Insert Variable

            </small>

          </div>

          <input
            v-model="object"
            type="text"
            placeholder="e.g. France"
          />

        </div>

      </div>

      <!-- BUTTON -->

      <div class="query-button-wrapper">

        <button
          class="execute-btn"
          @click="executeQuery"
        >

          <ArrowDown :size="18" />

          EXECUTE INQUIRY

        </button>

      </div>

    </div>

    <!-- RESULTS -->

    <div class="results-card">

      <div class="results-header">

        <div class="results-title">

          QUERY RESULTS:

        </div>

        <div class="results-count">

          {{ results.length }}
          TRIPLES

        </div>

      </div>

      <!-- TABLE -->

      <table>

        <thead>

          <tr>

            <th>SUBJECT</th>

            <th>PREDICATE</th>

            <th>OBJECT</th>

          </tr>

        </thead>

        <tbody>

          <!-- RESULTS -->

          <tr
            v-for="triple in results"
            :key="triple.id"
          >

            <td>
              {{ triple.subject }}
            </td>

            <td>
              {{ triple.predicate }}
            </td>

            <td>
              {{ triple.object }}
            </td>

          </tr>

          <!-- EMPTY -->

          <tr v-if="results.length === 0">

            <td colspan="3">

              No matching triples found.

            </td>

          </tr>

        </tbody>

      </table>

    </div>

  </MainLayout>

</template>

<script setup lang="ts">

import {
  ref
} from 'vue'

import {
  Search,
  CircleQuestionMark,
  ArrowDown
} from 'lucide-vue-next'

import MainLayout from '../layouts/MainLayout.vue'

import { api } from '../services/api'

import {
  type Triple
} from '../store/semanticStore'

/* =========================================
   QUERY INPUTS
========================================= */

const subject = ref('')

const predicate = ref('')

const object = ref('')

/* =========================================
   RESULTS
========================================= */

const results = ref<Triple[]>([])

/* =========================================
   EXECUTE QUERY
========================================= */

function executeQuery() {

  results.value = api.getTriples({

    s: subject.value,

    p: predicate.value,

    o: object.value
  })
}

</script>