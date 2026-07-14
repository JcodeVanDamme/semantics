Code-Snippet
<template>
  <div class="form-panel card">
    <div class="icon-title accent">
      <PackagePlus v-if="!editMode" :size="20" />
      <Pencil v-else :size="20" />
      <h2 class="accent">
        {{ editMode ? 'EDIT TRIPLE' : 'CREATE NEW TRIPLE' }}
      </h2>
    </div>

    <div class="input-group">
      <label class="input-header">Subject</label>
      <input v-model="form.subject" type="text" placeholder="e.g. Example" />
    </div>

    <div class="input-group">
      <label class="input-header">Predicate</label>
      <input v-model="form.predicate" type="text" placeholder="e.g. Example" />
    </div>

    <div class="input-group">
      <label class="input-header">Object</label>
      <input v-model="form.object" type="text" placeholder="e.g. Example" />
    </div>

    <div class="form-actions">
      <button v-if="!editMode" class="create-btn" @click="$emit('create')">
        <ArrowDown :size="18" />
        CREATE TRIPLE
      </button>

      <template v-else>
        <button class="abort-btn" @click="$emit('cancel')">
          <Undo2 :size="18" />
          ABORT EDIT
        </button>

        <button class="commit-btn" @click="$emit('commit')">
          <ArrowDown :size="18" />
          COMMIT CHANGES
        </button>

        <button class="delete-btn" @click="$emit('delete')">
          <FileX2 :size="18" />
          DELETE TRIPLE
        </button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PackagePlus, Pencil, ArrowDown, Undo2, FileX2, Search } from 'lucide-vue-next'

// Structural interfaces for type safety
interface TripleForm {
  subject: string
  predicate: string
  object: string
}

// Props definition
defineProps<{
  editMode: boolean
  form: TripleForm
}>()

// Decoupled action emits
defineEmits<{
  (e: 'create'): void
  (e: 'cancel'): void
  (e: 'commit'): void
  (e: 'delete'): void
}>()
</script>

<style scoped>
.form-panel {
  display: flex;
  flex-direction: column;
}

.input-group {
  display: flex;
  flex-direction: column;
}

.input-header {
  margin: var(--paddingHalf);
  font-weight: lighter;
  font-size: var(--text-h4);
}

.input-group input {
  border: none;
  border-bottom: 2px solid var(--borderColor);
  background: transparent;
  padding: 12px 10px;
  font-size: 15px;
  outline: none;
  color: var(--mainFontColor);
  transition: border-color 0.2s;
}

.query-input-group input:focus {
  border-color: var(--accentColor);
}

.query-input-group input::placeholder {
  color: #999;
}
</style>
