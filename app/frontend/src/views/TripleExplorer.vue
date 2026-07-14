<template>
  <MainLayout>
    <HeaderOverview
      title="Triple Explorer"
      description="Query your compact RDF tree store using real-time parameter scanning."
    />

    <div class="card">
      <CollapsiblePanel>
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

      <div v-if="validationNote" class="note-banner">
        {{ validationNote }}
      </div>

      <div v-if="validationError || error" class="error-banner">
        {{ validationError || error }}
      </div>

      <div class="triple-grid">
        <TripleInputGroup
          v-model:value="subject"
          v-model:uri="subjectUri"
          label="Subject"
          placeholderValue="e.g. Napoleon"
          placeholderUri="URI"
        />

        <TripleInputGroup
          v-model:value="predicate"
          v-model:uri="predicateUri"
          label="Predicate"
          placeholderValue="e.g. rules"
          placeholderUri="URI"
        />

        <TripleInputGroup
          v-model:value="object"
          v-model:uri="objectUri"
          v-model:mode="objectMode"
          label="Object"
          :hasToggle="true"
          placeholderValue="e.g. France or '1804'"
          placeholderUri="URI"
        />
      </div>

      <div class="query-button-wrapper">
        <button
          class="button accent"
          :disabled="isLoading || !!validationError"
          @click="executeQuery"
        >
          <ArrowDown :size="18" />
          {{ isLoading ? 'Processing...' : 'Execute Inquiry' }}
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
import { ref, onMounted } from 'vue'
import { Search, ArrowDown } from 'lucide-vue-next'

import MainLayout from '../layouts/MainLayout.vue'
import HeaderOverview from '../components/HeaderOverview.vue'
import TriplesResultsCard from '../components/new/TripleResults.vue'
import TriplesTable from '@/components/new/TripleTable.vue'
import TripleInputGroup from '../components/new/TripleInput.vue'
import CollapsiblePanel from '../components/new/CollapsiblePanel.vue'

import { api } from '../services/api'
import { cleanTripleForDisplay, concatUri } from '../utils/util.ts'
import { useRdfFormValidation } from '../composables/useRdfFormValidation.ts'

// Form Structural States
const subject = ref('')
const subjectUri = ref('')

const predicate = ref('')
const predicateUri = ref('')

const object = ref('')
const objectUri = ref('')
const objectMode = ref<'literal' | 'uri'>('literal')

// Asynchronous Request UI State Handlers
const results = ref<any[]>([])
const isLoading = ref(false)
const error = ref<string | null>(null)

const { validationError, validationNote } = useRdfFormValidation({
  subject,
  subjectUri,
  predicate,
  predicateUri,
  object,
  objectUri,
  objectMode,
})

async function executeQuery() {
  if (validationError.value) return

  isLoading.value = true
  error.value = null

  try {
    const DEFAULT_NS = 'http://semantics.rdf.system/'

    const cleanSubjectVal = subject.value.replace(/\s+/g, '')
    const cleanPredicateVal = predicate.value.replace(/\s+/g, '')
    const cleanObjectVal = object.value.replace(/\s+/g, '')

    const baseSubject = subjectUri.value.trim() || DEFAULT_NS
    const finalSubject = concatUri(baseSubject, cleanSubjectVal)

    const basePredicate = predicateUri.value.trim() || DEFAULT_NS
    const finalPredicate = concatUri(basePredicate, cleanPredicateVal)

    let finalObject = ''
    if (objectMode.value === 'uri') {
      const baseObject = objectUri.value.trim() || DEFAULT_NS
      finalObject = concatUri(baseObject, cleanObjectVal)
    } else {
      finalObject = object.value.trim() // Keep literal spaces intact if desired, or use cleanObjectVal
    }

    const response = await api.getTriples({
      s: finalSubject || undefined,
      p: finalPredicate || undefined,
      o: finalObject || undefined,
    })
    console.log(response)

    results.value = response.triples.map((triple: any, index: number) => {
      const rawTriple = {
        id: index,
        subject: triple.s.value,
        predicate: triple.p.value,
        object: triple.o.value,
        isLiteral: triple.o.isLiteral,
      }
      return cleanTripleForDisplay(rawTriple)
    })
  } catch (err: any) {
    error.value = err.message || 'An error occurred while scanning the remote RDF matrix store.'
    console.error('Query execution failed:', err)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  executeQuery()
})
</script>

<style scoped>
.triple-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  align-items: start;
}

.query-button-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 30px;
}

.button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 1200px) {
  .triple-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}
</style>
