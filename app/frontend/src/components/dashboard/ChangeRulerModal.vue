<template>
  <BaseModal
    title="CHANGE RULER"
    description="Select an existing ruler or create a new one."
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <div class="modal-form">
      <div class="toggle-labels">
        <span class="toggle-label" :class="{ active: mode === 'select' }" @click="mode = 'select'">
          SELECT RULER
        </span>
        <span class="toggle-divider">/</span>
        <span class="toggle-label" :class="{ active: mode === 'create' }" @click="mode = 'create'">
          CREATE RULER
        </span>
      </div>

      <div v-if="mode === 'select'" class="input-group">
        <select v-model="selectedRulerUri" class="base-input">
          <option value="" disabled selected>Select a Ruler</option>
          <option v-for="opt in rulerOptions" :key="opt.URI" :value="opt.URI">
            {{ opt.name }}
          </option>
        </select>
      </div>

      <div v-else class="input-group">
        <label class="select-header pad-top">Ruler Name</label>
        <input v-model="form.label" class="base-input" placeholder="e.g. Frederick the Great" />

        <label class="select-header">Title</label>
        <input v-model="form.title" class="base-input" placeholder="e.g. King of Prussia" />
      </div>

      <div class="input-button-wrapper">
        <button class="button accent" :disabled="!isValid || isProcessing" @click="handleSubmit">
          <Flag :size="18" />
          {{ isProcessing ? 'PROCESSING...' : 'CONFIRM CHANGE' }}
        </button>
      </div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Flag } from 'lucide-vue-next'
import { useDashboardStore } from '@/stores/dashboardStore'
import { api } from '@/scripts/apiClient.js'
import BaseModal from './BaseModal.vue'

// Imported Interfaces
import type { Ruler, StateData, ChangeRulerRequest, HistoryEvent } from '@/scripts/type.ts'

const props = defineProps<{
  stateUri: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: HistoryEvent[]): void
}>()

const store = useDashboardStore()
const errorMessage = ref<string | null>(null)
const isProcessing = ref(false)
const mode = ref<'select' | 'create'>('select')

const form = ref({
  label: '',
  title: '',
})
const selectedRulerUri = ref('')

// Reset form state when switching modes
watch(mode, () => {
  errorMessage.value = null
  selectedRulerUri.value = ''
  form.value = { label: '', title: '' }
})

// Compute unique rulers from the store, cast as Ruler array
const rulerOptions = computed<Ruler[]>(() => {
  const rulers = new Map<string, Ruler>()

  store.allStates.forEach((s: StateData) => {
    if (s.ruler?.URI && !rulers.has(s.ruler.URI)) {
      rulers.set(s.ruler.URI, s.ruler)
    }
  })

  return Array.from(rulers.values())
})

const isValid = computed(() => {
  if (mode.value === 'select') return selectedRulerUri.value.length > 0
  return form.value.label.length > 0 && form.value.title.length > 0
})

async function handleSubmit() {
  errorMessage.value = null
  isProcessing.value = true

  try {
    let payload: ChangeRulerRequest

    if (mode.value === 'select') {
      const selected = rulerOptions.value.find((o) => o.URI === selectedRulerUri.value)

      if (!selected) throw new Error('Ruler not found')

      payload = {
        state: props.stateUri,
        ruler: selected.URI,
        label: selected.name,
        title: selected.title,
      }
    } else {
      // Create Mode: Construct URI
      const formattedName = form.value.label.replace(/\s+/g, '_')
      payload = {
        state: props.stateUri,
        ruler: `http://semantics.rdf.system.data/Ruler_${formattedName}`,
        label: form.value.label,
        title: form.value.title,
      }
    }

    const res = await api.changeRuler(payload)
    emit('submit', res)
  } catch (err: any) {
    errorMessage.value = err.message || 'Failed to update ruler.'
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped>
/* --- LAYOUT & SPACING --- */
.pad-top {
  padding-top: var(--paddingDouble);
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: var(--paddingHalf);
}

.input-button-wrapper {
  margin-top: 1rem;
}

/* --- INTERACTIVE ELEMENTS --- */
.toggle-labels {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.toggle-label {
  cursor: pointer;
  color: var(--mutedFontColor);
  font-size: var(--text-h4);
  transition: color 0.2s;
}

.toggle-label:hover,
.toggle-label.active {
  color: var(--mainFontColor);
}

.toggle-divider {
  color: var(--mutedFontColor);
}
</style>
