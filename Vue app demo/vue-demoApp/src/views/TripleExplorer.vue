<template>
  <MainLayout>

    <!-- HEADER -->
    <div class="overview-section">
      <h1>Triple Explorer</h1>
      <p>
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
        sed diam nonumy eirmod tempor invidunt ut labore et dolore.
      </p>
    </div>

    <!-- QUERY CARD -->
    <div class="query-card">

      <div class="query-title">
        <Search :size="20" />
        TRIPLE QUERY
      </div>

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

          <input type="text" placeholder="e.g. Example"/>
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

          <input type="text" placeholder="e.g. Example" />

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

          <input type="text" placeholder="e.g. Example"/>

        </div>
      </div>

      <!-- BUTTON -->
      <div class="query-button-wrapper">
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


<script setup >
import {
  Search,
  CircleQuestionMark,
  ArrowDown
} from 'lucide-vue-next'

import MainLayout from '../layouts/MainLayout.vue'
</script>