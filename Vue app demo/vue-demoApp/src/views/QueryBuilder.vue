<template>
  <MainLayout>

    <!-- HEADER -->

    <div class="overview-section">
      <h1>Query Builder</h1>
      <p>
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
        sed diam nonumy eirmod tempor invidunt ut labore et dolore.
      </p>
    </div>

    <!-- QUERY BOX -->

    <div class="query-card">
      
      <div class="query-title">
        <Search :size="20" />
        SPARQL QUERY
      </div>

      <textarea class="query-textarea"></textarea>

      <!-- BUTTONS -->
      <div class="builder-buttons">

        <button class="clear-btn">
          <CircleX :size="18" />
          CLEAR INPUT
        </button>

        <button class="execute-btn">
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
          000 TRIPLES
        </div>

      </div>

      <table>
        <thead>
          <tr>
            <th>SUBJECT</th>
            <th>PREDICATE</th>
            <th>OBJECT</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in 5" :key="item">
            <td>Subject</td>
            <td>Predicate</td>
            <td>Object</td>
          </tr>
        </tbody>
      </table>

    </div>
  </MainLayout>
</template>

<script setup>
import MainLayout from '../layouts/MainLayout.vue'

import {
  Search,
  CircleX,
  ArrowDown
} from 'lucide-vue-next'

</script>