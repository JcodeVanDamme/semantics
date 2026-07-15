<template>
  <BaseModal
    title="STATE FOUNDING"
    description="Fill out the fields below to create your new State."
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <div class="modal-form">
      <BaseInput label="State Name" v-model="form.name" placeholder="Kingdom of..." />

      <div class="input-group">
        <label class="select-header">Ruler</label>
        <select v-model="form.ruler" class="base-input">
          <option value="" disabled selected>Select a Ruler URI</option>
          <option v-for="uri in rulerOptions" :key="uri" :value="uri">
            {{ uri }}
          </option>
        </select>
      </div>

      <BaseInput
        label="Population"
        v-model.number="form.population"
        placeholder="Number"
        type="number"
      />
      <BaseInput label="State Type" v-model="form.type" placeholder="Kingdom, Duchy..." />

      <div class="input-button-wrapper">
        <button class="button accent" :disabled="!isValid" @click="handleSubmit">
          <Flag :size="18" />
          {{ isProcessing ? 'FOUNDING...' : 'FOUND STATE' }}
        </button>
      </div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Flag } from 'lucide-vue-next'
import { useDashboardStore } from '@/stores/dashboardStore'
import { api, DATA_URI } from '@/scripts/apiClient.js'
import BaseModal from './BaseModal.vue'
import BaseInput from '../util/BaseInput.vue'
import type { StateData, FoundStateRequest } from '../../scripts/type.ts'

interface FoundStateForm {
  name: string
  ruler: string
  population: number
  type: string
}

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: StateData): void
}>()

const store = useDashboardStore()
const errorMessage = ref<string | null>(null)
const isProcessing = ref(false)

const form = ref<FoundStateForm>({
  name: '',
  ruler: '',
  population: 0,
  type: '',
})

const rulerOptions = computed<string[]>(() => {
  if (!store.allStates) return []
  // Using a Set to ensure unique URIs
  return Array.from(new Set(store.allStates.map((s) => s.ruler?.URI).filter(Boolean) as string[]))
})

const isValid = computed(() => {
  return (
    form.value.name.length > 0 &&
    form.value.ruler.length > 0 &&
    form.value.type.length > 0 &&
    form.value.population > 0
  )
})

async function handleSubmit() {
  errorMessage.value = null
  isProcessing.value = true

  const formattedName = form.value.name.replace(/\s+/g, '_')

  const payload: FoundStateRequest = {
    state: `${DATA_URI}${formattedName}`,
    label: form.value.name,
    ruler: form.value.ruler,
    population: form.value.population,
    type: form.value.type,
  }

  try {
    const res = await api.foundState(payload)
    emit('submit', res)
  } catch (err: any) {
    errorMessage.value = err.message || 'Encountered an unexpected error.'
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped>
/* --- FORM LAYOUT --- */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: var(--paddingHalf);
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: var(--paddingHalf);
}

/* --- BUTTONS --- */
.input-button-wrapper {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
}
</style>
