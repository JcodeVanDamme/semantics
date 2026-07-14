<template>
  <BaseModal
    title="State Mediatization"
    :description="`Select a State to be mediatized by ${actingState}.`"
    sizeClass="found-modal"
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <div class="modal-form">
      <label class="select-header">Select State</label>
      <select v-model="form.consumed" class="base-input">
        <option value="" disabled selected>Select a State</option>
        <option v-for="opt in stateOptions" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </option>
      </select>

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
import BaseModal from './BaseModal.vue'
import { useDashboardStore } from '@/stores/dashboardStore'
import { api } from '@/scripts/api.ts'

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

watch(
  () => form.value.consumed,
  () => {
    errorMessage.value = null
  },
)

const actingStateURI = computed(() => {
  const state = store.allStates.find((s) => s.name === props.actingState)
  return state ? state.URI : props.actingState
})

const stateOptions = computed(() => {
  return store.activeStates
    .filter((s) => s.name !== props.actingState)
    .map((s) => ({ label: s.name, value: s.URI }))
})

const isValid = computed(() => form.value.consumed.length > 0)

async function handleSubmit() {
  errorMessage.value = null

  if (!isValid.value) return

  const targetState = store.allStates.find((s) => s.URI === form.value.consumed)

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
    // Call the API directly from the modal
    const res = await api.mediatizate({
      absorbed: targetState.URI,
      into: actingStateURI.value,
    })

    console.log('Med Res: ', res)
    emit('submit', res)
  } catch (err: any) {
    errorMessage.value = err.message || 'Encountered an unexpected Error.'
  } finally {
    isProcessing.value = false
  }
}
</script>
