<template>
  <MainLayout>

    <!-- HEADER -->

    <div class="overview-section">
      <h1>Store Management</h1>
      <p>
        Lorem ipsum dolor sit amet, consetetur sadipscing elitr,
        sed diam nonumy eirmod tempor invidunt ut labore et dolore.
      </p>
    </div>

    <!-- STORE LAYOUT -->
    <div class="store-layout">
      <!-- LEFT PANEL -->
      <div class="store-form-panel">
        <!-- TITLE -->
        <div class="store-form-title">
          <div class="title-left">
            
            <PackagePlus v-if="!editMode" :size="20"/>
            <Pencil v-else :size="20"/>
            <span>
              {{ editMode ? 'EDIT TRIPLE' : 'CREATE NEW TRIPLE' }}
            </span>

          </div>
        </div>

        <!-- FORM -->
        <div class="store-input-group">
          <label>Subject</label>
          <input v-model="form.subject" type="text" placeholder="e.g. Example"/>
        </div>

        <div class="store-input-group">
          <label>Predicate</label>
          <input v-model="form.predicate" type="text" placeholder="e.g. Example"/>
        </div>

        <div class="store-input-group">
          <label>Object</label>
          <input v-model="form.object" type="text" placeholder="e.g. Example"/>
        </div>

        <!-- CREATE MODE -->
        <button v-if="!editMode" class="create-btn" @click="createTriple">
          <ArrowDown :size="18" />
          CREATE TRIPLE
        </button>

        <!-- EDIT MODE -->
        <template v-else>

          <button class="abort-btn" @click="cancelEdit">
            <Undo2 :size="18" />
            ABORT EDIT
          </button>

          <button class="commit-btn" @click="commitChanges">
            <ArrowDown :size="18" />
            COMMIT CHANGES
          </button>

          <button class="delete-btn" @click="deleteTriple">
            <FileX2 :size="18" />
            DELETE TRIPLE
          </button>

        </template>
      </div>

      <!-- RIGHT PANEL -->
      <div class="triple-store-panel">

        <!-- HEADER -->
        <div class="triple-store-header">
          <Database :size="18" />
          <span>TRIPLE STORE</span>
        </div>

        <!-- SEARCH -->
        <div class="search-wrapper">
          <Search :size="16" />
          <input v-model="search" type="text" placeholder="Search by Subject, Predicate or Object"/>
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
            <tr
              v-for="triple in filteredTriples"
              :key="triple.id"
              :class="{ selected: selectedTripleId === triple.id }"
              @click="selectTriple(triple)"
            >
              <td>{{ triple.subject }}</td>
              <td>{{ triple.predicate }}</td>
              <td>{{ triple.object }}</td>
            </tr>
          </tbody>
        </table>

      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">

import {
  ref,
  reactive,
  computed
} from 'vue'

import MainLayout from '../layouts/MainLayout.vue'

import {
  Database,
  Search,
  ArrowDown,
  Undo2,
  FileX2,
  Pencil,
  PackagePlus
} from 'lucide-vue-next'

import { api } from '../services/api'

import {
  useSemanticStore,
  type Triple
} from '../store/semanticStore'

/* =========================================
   GLOBAL STORE
========================================= */

const store = useSemanticStore()

const triples = store.triples

/* =========================================
   FORM
========================================= */

const form = reactive({

  id: 0,

  subject: '',

  predicate: '',

  object: ''
})

/* =========================================
   UI STATE
========================================= */

const editMode = ref(false)

const selectedTripleId =
  ref<number | null>(null)

const search = ref('')

/* =========================================
   FILTERED TABLE
========================================= */

const filteredTriples = computed(() => {

  return triples.value.filter((triple) => {

    const text =

      `${triple.subject}
       ${triple.predicate}
       ${triple.object}`

        .toLowerCase()

    return text.includes(
      search.value.toLowerCase()
    )
  })
})

/* =========================================
   CREATE
========================================= */

function createTriple() {

  if (

    !form.subject ||

    !form.predicate ||

    !form.object
  ) {
    return
  }

  api.createTriple({

    subject: form.subject,

    predicate: form.predicate,

    object: form.object
  })

  clearForm()
}

/* =========================================
   SELECT TRIPLE
========================================= */

function selectTriple(triple: Triple) {

  selectedTripleId.value = triple.id

  editMode.value = true

  form.id = triple.id

  form.subject = triple.subject

  form.predicate = triple.predicate

  form.object = triple.object
}

/* =========================================
   COMMIT CHANGES
========================================= */

function commitChanges() {

  if (
    selectedTripleId.value === null
  ) {
    return
  }

  api.updateTriple({

    id: form.id,

    subject: form.subject,

    predicate: form.predicate,

    object: form.object
  })

  cancelEdit()
}

/* =========================================
   DELETE
========================================= */

function deleteTriple() {

  if (
    selectedTripleId.value === null
  ) {
    return
  }

  api.deleteTriple(
    selectedTripleId.value
  )

  cancelEdit()
}

/* =========================================
   CANCEL EDIT
========================================= */

function cancelEdit() {

  editMode.value = false

  selectedTripleId.value = null

  clearForm()
}

/* =========================================
   CLEAR FORM
========================================= */

function clearForm() {

  form.id = 0

  form.subject = ''

  form.predicate = ''

  form.object = ''
}

</script>