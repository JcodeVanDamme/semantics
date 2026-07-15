<template>
  <BaseModal
    title="State Mediatization"
    :description="`Select a State to be mediatized by ${actingState}.`"
    sizeClass="found-modal"
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <div class="modal-form">
      <!-- State Selection -->
      <label class="select-header">Select State</label>
      <select v-model="form.consumed" class="base-input">
        <option value="" disabled selected>Select a State</option>
        <option v-for="opt in stateOptions" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>

      <!-- Action Footer -->
      <div class="input-button-wrapper">
        <button class="button accent" :disabled="!isValid || isProcessing" @click="handleSubmit">
          <Flag :size="18" />
          {{ isProcessing ? 'PROCESSING...' : 'MEDIATIZATE' }}
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
import type { StateData, MediatizationRequest } from '@/scripts/type.ts'

/**
 * INTERFACES
 */
interface SelectOption {
  label: string
  value: string
}

// Props & Emits
const props = defineProps<{
  actingState: string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: any): void
}>()

const store = useDashboardStore()
const errorMessage = ref<string | null>(null)
const isProcessing = ref(false)
const form = ref({ consumed: '' })

// Reset errors on interaction
watch(
  () => form.value.consumed,
  () => {
    errorMessage.value = null
  },
)

/**
 * COMPUTED
 */
const actingStateURI = computed<string>(() => {
  const state = store.allStates.find((s: StateData) => s.name === props.actingState)
  return state ? state.URI : props.actingState
})

const stateOptions = computed<SelectOption[]>(() => {
  return store.activeStates
    .filter((s: StateData) => s.name !== props.actingState)
    .map((s: StateData) => ({ label: s.name, value: s.URI }))
})

const isValid = computed<boolean>(() => form.value.consumed.length > 0)

/**
 * METHODS
 */
async function handleSubmit() {
  errorMessage.value = null
  if (!isValid.value) return

  const targetState = store.allStates.find((s: StateData) => s.URI === form.value.consumed)

  // Validation Guard Clauses
  if (!targetState) {
    errorMessage.value = 'State not found.'
    return
  }

  if (targetState.URI === actingStateURI.value) {
    errorMessage.value = 'Cannot mediatize a state into itself.'
    return
  }

  isProcessing.value = true

  try {
    const payload: MediatizationRequest = {
      absorbed: targetState.URI,
      into: actingStateURI.value,
    }

    const res = await api.mediatizate(payload)
    emit('submit', res)
  } catch (err: any) {
    errorMessage.value = err.message || 'Encountered an unexpected Error.'
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped>
/* --- LAYOUT --- */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: var(--paddingHalf);
}

.input-button-wrapper {
  margin-top: var(--padding);
  display: flex;
  justify-content: flex-end;
}

/* --- TYPOGRAPHY --- */
.select-header {
  font-weight: bold;
  font-size: var(--text-small);
  color: var(--mutedFontColor);
}

/* --- FORM ELEMENTS --- */
.base-input {
  width: 100%;
}
</style>
