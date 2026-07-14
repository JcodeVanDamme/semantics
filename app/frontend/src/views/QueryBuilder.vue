<template>
  <MainLayout>
    <HeaderOverview
      title="Query Builder"
      description="Compose complex Triple-Queries in SPARQL-Syntax to precisely scan your RDF Tree."
    />

    <div class="card">
      <CollapsiblePanel
        >-

        <template #header>
          <div class="icon-title accent">
            <Search />
            <h2 class="accent">Triple Query</h2>
          </div>
        </template>

        <template #info>
          <div class="info-content">
            <p><strong>How Queries Work (S, P, O)</strong></p>
            <p>
              You can query the RDF triple store using the input fields for
              <strong>Subject</strong>, <strong>Predicate</strong>, and <strong>Object</strong>. If
              you leave a field completely empty, it will automatically be treated as a
              <strong>wildcard</strong>. The system will then scan and return all available entries
              matching that position.
            </p>

            <p><strong>Handling URIs and Namespaces</strong></p>
            <p>
              Entering a custom URI is entirely <strong>optional</strong>. If you leave the URI
              field blank, the system automatically falls back to our default namespace
              <strong><i>http://semantics.rdf.system/</i></strong
              >. However, if you choose to provide a custom URI, it must be valid (meaning it
              requires a correctly formatted protocol like (<strong><i>http://</i></strong> or
              <strong><i>urn:</i></strong
              >).
            </p>

            <p><strong>Composition of the Final Query</strong></p>
            <p>
              The final URI sent to the triple store in the background is formed by automatically
              <strong>concatenating</strong> the chosen namespace (URI field) and the actual search
              term (input value).
            </p>

            <p><strong>Concrete Example of URI Generation:</strong></p>
            <ul>
              <li class="info-row">
                <div class="info-label">Entered / Default URI:</div>
                <div class="info-value">
                  <strong><i>http://semantics.rdf.system/</i></strong>
                </div>
                <p></p>
              </li>

              <li class="info-row">
                <div class="info-label">Entered Value (Input Value):</div>
                <div class="info-value">
                  <strong><i>Bavaria</i></strong>
                </div>
                <p></p>
              </li>

              <li class="info-row">
                <div class="info-label">Resulting Final URI:</div>
                <div class="info-value">
                  <strong><i>http://semantics.rdf.system/Bavaria</i></strong>
                </div>
              </li>
            </ul>

            <p>
              <em>Note on the Object field:</em> For the Object, you can use the toggle switch to
              choose whether it should be treated as a <strong>URI</strong> (which follows the
              concatenation rule above) or a <strong>Literal</strong>. Literals represent raw data
              values like text strings or numbers and are transmitted directly to the store as-is,
              without any namespace modifications.
            </p>
          </div>
        </template>
      </CollapsiblePanel>

      <textarea
        v-model="query"
        class="query-textarea"
        placeholder="Example:
  SELECT * WHERE {
  Bavaria Population
}"
      />

      <div v-if="validationError || error" class="error-banner">
        {{ validationError || error }}
      </div>

      <div class="button-wrapper" @click="clearInput()">
        <button class="button" :disabled="isLoading || !!validationError" @click="clearInput">
          <CircleX :size="18" />
          Clear Input
        </button>

        <button
          class="button accent"
          :disabled="isLoading || !!validationError"
          @click="executeQuery"
        >
          <CircleX :size="18" />
          Execute
        </button>
      </div>
    </div>

    <div class="card">
      <TriplesResultsCard :triples="results" />
      <TriplesTable :triples="results" disable-selection />
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import HeaderOverview from '../components/util/HeaderOverview.vue'

import { ref } from 'vue'

import MainLayout from '../layouts/MainLayout.vue'

import { Search, CircleX } from 'lucide-vue-next'

import CollapsiblePanel from '@/components/util/CollapsiblePanel.vue'
import TriplesResultsCard from '@/components/util/TripleResults.vue'
import TriplesTable from '@/components/util/TripleTable.vue'

const results = ref<Triple[]>([])
const query = ref('')

function executeQuery() {
  const trimmedQuery = query.value.trim()

  if (!trimmedQuery) {
    results.value = []

    return
  }
}

function clearInput() {
  query.value = ''

  results.value = []
}
</script>

<style scoped>
.query-textarea {
  background: #f4f4f4;
  border: 2px solid var(--borderColor);
  resize: none;
  padding: var(--paddingHalf);
  font-size: var(--dataFontStyle);
  outline: none;
  height: 200px;
}

.button-wrapper {
  display: flex;
  flex-direction: row;
  margin-left: auto;
  gap: var(--padding);
}
</style>
