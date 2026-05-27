<template>

  <MainLayout>

    <!-- HEADER -->

    <div class="overview-section">

      <h1>Query Builder</h1>

      <p>
        Execute SPARQL-like semantic queries
        on the shared semantic graph database.
      </p>

    </div>

    <!-- QUERY CARD -->

    <div class="query-card">

      <div class="query-title">

        <Search :size="20" />

        SPARQL QUERY

      </div>

      <!-- TEXTAREA -->

      <textarea

        v-model="query"

        class="query-textarea"

        placeholder="Example:
SELECT * WHERE {
  Napoleon rules France
}"
      />

      <!-- BUTTONS -->

      <div class="builder-buttons">

        <!-- CLEAR -->

        <button
          class="clear-btn"
          @click="clearInput"
        >

          <CircleX :size="18" />

          CLEAR INPUT

        </button>

        <!-- EXECUTE -->

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

import { ref } from 'vue'

import MainLayout from '../layouts/MainLayout.vue'

import {
  Search,
  CircleX,
  ArrowDown
} from 'lucide-vue-next'

import { api } from '../services/api'

import {
  useSemanticStore,
  type Triple
} from '../store/semanticStore'

/* =========================================
   STORE
========================================= */

const store = useSemanticStore()

/* =========================================
   QUERY
========================================= */

const query = ref('')

/* =========================================
   RESULTS
========================================= */

const results = ref<Triple[]>([])

/* =========================================
   EXECUTE QUERY
========================================= */

function executeQuery() {

  const trimmedQuery =
    query.value.trim()

  if (!trimmedQuery) {

    results.value = []

    return
  }

  results.value =
    api.sparqlQuery(trimmedQuery)
}

/* =========================================
   CLEAR INPUT
========================================= */

function clearInput() {

  query.value = ''

  results.value = []
}

</script>