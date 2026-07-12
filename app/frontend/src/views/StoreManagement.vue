<template>
  <MainLayout>
    <HeaderOverview
      title="Store Management"
      description="Manage your RDF data structures directly by searching, building, editing, or deleting active triples inside your store."
    />

    <div class="store-layout">
      <div class="form-panel card">
        <div class="icon-title accent">
          <PackagePlus v-if="!editMode" :size="20" />
          <Pencil v-else :size="20" />
          <h2 class="accent">
            {{ editMode ? 'Edit Triple' : 'Create Triple' }}
          </h2>
        </div>

        <div v-if="validationNote" class="note-banner">
          {{ validationNote }}
        </div>

        <div v-if="validationError || error" class="error-banner">
          {{ validationError || error }}
        </div>

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

        <div class="form-actions">
          <button
            v-if="!editMode"
            class="button accent"
            :disabled="isLoading"
            @click="createTriple"
          >
            <ArrowDown :size="18" />
            {{ isLoading ? 'Processing...' : 'CREATE TRIPLE' }}
          </button>

          <template v-else>
            <button class="button" :disabled="isLoading" @click="cancelEdit">
              <Undo2 :size="18" />
              ABORT
            </button>

            <button class="button accent" :disabled="isLoading" @click="commitChanges">
              <ArrowDown :size="18" />
              {{ isLoading ? 'Saving...' : 'COMMIT' }}
            </button>

            <button class="button error" :disabled="isLoading" @click="deleteTriple">
              <FileX2 :size="18" />
              {{ isLoading ? 'Deleting...' : 'DELETE TRIPLE' }}
            </button>
          </template>
        </div>
      </div>

      <div class="triple-store-panel composite-card">
        <div class="composite-card-header">
          <div class="icon-title white">
            <Database :size="18" />
            <h2 class="white">TRIPLE STORE</h2>
          </div>
        </div>

        <div class="composite-card-content">
          <div class="search-wrapper">
            <Search />
            <input
              v-model="search"
              type="text"
              placeholder="Search local records by Subject, Predicate or Object..."
            />
          </div>

          <div v-if="isLoading && results.length === 0" class="loading-state">
            Loading Triples from Store...
          </div>
          <TriplesTable v-else :triples="filteredTriples" @select-row="selectTriple" />
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import MainLayout from '../layouts/MainLayout.vue'
import HeaderOverview from '../components/HeaderOverview.vue'
import TripleInputGroup from '../components/new/TripleInput.vue'
import TriplesTable from '@/components/new/TripleTable.vue'
import { Database, Search, ArrowDown, Undo2, FileX2, Pencil, PackagePlus } from 'lucide-vue-next'

import { api, type BackendTriple } from '../services/api'
import { cleanTripleForDisplay, stripSpaces, concatUri } from '../utils/util.ts'
import { useRdfFormValidation } from '@/composables/useRdfFormValidation'

// Lokale Interface-Definition für die UI-Tabelle samt Backend-Backup
interface DisplayTriple {
  id: number
  subject: string
  predicate: string
  object: string
  isLiteral: boolean
  raw: BackendTriple
}

const subject = ref('')
const subjectUri = ref('')
const predicate = ref('')
const predicateUri = ref('')
const object = ref('')
const objectUri = ref('')
const objectMode = ref<'uri' | 'literal'>('uri')

const editMode = ref(false)
const isLoading = ref(false)
const search = ref('')
const results = ref<DisplayTriple[]>([]) // Verwendet jetzt das UI-kompatible Format
const originalTriple = ref<BackendTriple | null>(null)
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

function compileBackendPayload(): BackendTriple {
  return {
    s: { value: concatUri(subjectUri.value, stripSpaces(subject.value)) },
    p: { value: concatUri(predicateUri.value, stripSpaces(predicate.value)) },
    o: {
      value:
        objectMode.value === 'literal'
          ? object.value.trim()
          : concatUri(objectUri.value, stripSpaces(object.value)),
      isLiteral: objectMode.value === 'literal',
    },
  }
}

async function fetchStoreTriples() {
  isLoading.value = true
  error.value = null
  try {
    const response = await api.getTriples()

    results.value = (response.triples || []).map((triple: any, index: number) => {
      const rawTriple = {
        id: index,
        subject: triple.s.value,
        predicate: triple.p.value,
        object: triple.o.value,
        isLiteral: triple.o.isLiteral,
      }

      // 1. Run your standard display cleanup function
      const cleanedElement = cleanTripleForDisplay(rawTriple)

      // 2. CRITICAL FIX: Securely attach the original backend triple back onto the object
      cleanedElement.raw = triple

      return cleanedElement
    })
  } catch (err: any) {
    error.value = err.message || 'Encountered an unexpected Error.'
    console.error('Triple Retrieval failed:', err)
  } finally {
    isLoading.value = false
  }
}

function clearForm() {
  subject.value = ''
  subjectUri.value = ''
  predicate.value = ''
  predicateUri.value = ''
  object.value = ''
  objectUri.value = ''
  objectMode.value = 'uri'
  error.value = null
}

async function createTriple() {
  if (!subject.value || !predicate.value || !object.value) return
  isLoading.value = true
  try {
    await api.createTriple(compileBackendPayload())
    await fetchStoreTriples()
    clearForm()
  } catch (err: any) {
    error.value = err.message || 'Encountered an unexpected Error.'
    console.error('Create Call failed:', err)
  } finally {
    isLoading.value = false
  }
}

async function commitChanges() {
  if (!originalTriple.value || !subject.value || !predicate.value || !object.value) return
  isLoading.value = true
  try {
    await api.updateTriple({
      original: originalTriple.value,
      update: compileBackendPayload(),
    })
    await fetchStoreTriples()
    cancelEdit()
  } catch (err: any) {
    error.value = err.message || 'Encountered an unexpected Error.'
    console.error('Update Call failed:', err)
  } finally {
    isLoading.value = false
  }
}

async function deleteTriple() {
  if (!originalTriple.value) return
  if (!confirm('Are you sure?')) return
  isLoading.value = true
  try {
    await api.deleteTriple(originalTriple.value)
    await fetchStoreTriples()
    cancelEdit()
  } catch (err: any) {
    error.value = err.message || 'Delete Error.'
    console.error('Delete Call failed:', err)
  } finally {
    isLoading.value = false
  }
}

function splitRawUri(fullUri: string): { prefix: string; identifier: string } {
  if (!fullUri) return { prefix: '', identifier: '' }

  // Find the position of the last trailing path delimiter
  const lastSlash = fullUri.lastIndexOf('/')
  const lastHash = fullUri.lastIndexOf('#')
  const delimiterIndex = Math.max(lastSlash, lastHash)

  if (delimiterIndex !== -1) {
    return {
      // Includes the '/' or '#' character so concatUri works seamlessly on save
      prefix: fullUri.substring(0, delimiterIndex + 1),
      identifier: fullUri.substring(delimiterIndex + 1),
    }
  }

  // Fallback if no URI delimiters are found
  return { prefix: '', identifier: fullUri }
}

// Triggered when selecting a row from the TriplesTable
function selectTriple(triple: any) {
  if (!triple || !triple.raw) {
    console.warn('Cannot Select Triple, raw Data is missing.', triple)
    return
  }

  // Preserve the deep backend track object for Update/Delete operations
  originalTriple.value = JSON.parse(JSON.stringify(triple.raw))
  editMode.value = true

  // 1. Process Subject URI
  const subjectParts = splitRawUri(triple.rawSubject)
  subject.value = subjectParts.identifier
  subjectUri.value = subjectParts.prefix

  // 2. Process Predicate URI
  const predicateParts = splitRawUri(triple.rawPredicate)
  predicate.value = predicateParts.identifier
  predicateUri.value = predicateParts.prefix

  // 3. Process Object (Differentiating between Literal values and URIs)
  objectMode.value = triple.isLiteral ? 'literal' : 'uri'

  if (triple.isLiteral) {
    // Literals go directly into the value field without namespace modifications
    object.value = triple.rawObject
    objectUri.value = ''
  } else {
    // Objects matching the URI format get split identically to subject/predicate
    const objectParts = splitRawUri(triple.rawObject)
    object.value = objectParts.identifier
    objectUri.value = objectParts.prefix
  }
}

function cancelEdit() {
  editMode.value = false
  originalTriple.value = null
  error.value = null
}

const filteredTriples = computed(() => {
  const term = search.value.toLowerCase().trim()
  if (!term) return results.value
  return results.value.filter(
    (t) =>
      t.subject.toLowerCase().includes(term) ||
      t.predicate.toLowerCase().includes(term) ||
      t.object.toLowerCase().includes(term),
  )
})

onMounted(() => {
  fetchStoreTriples()
})
</script>

<style scoped>
.store-layout {
  display: flex;
  gap: var(--paddingDouble);
  flex: 1;
}

.triple-store-panel {
  flex: 1;
}

.form-panel {
  display: flex;
  flex-direction: column;
  width: 20vw;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: var(--padding);
}

.edit-actions-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.edit-actions-row .button {
  flex: 1;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: var(--paddingHalf);
  padding: var(--padding);
  background-color: var(--secondaryColor);
  border-bottom: 2px solid var(--borderColor);
}

.search-wrapper input {
  width: 100%;
  border: none;
  background: transparent;
  outline: none;
  font-size: var(--base-size);
  font-style: italic;
}

.search-wrapper input::placeholder {
  color: #989898;
}
.search-wrapper svg {
  height: 24px;
  width: 24px;
  color: var(--mainFontColor);
}
.loading-state {

}

@media (max-width: 1100px) {
  .store-layout {
    flex-direction: column;
  }
}
</style>
