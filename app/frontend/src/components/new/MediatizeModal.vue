<template>
  <BaseModal
    title="State Mediatization:"
    :description="`Select a State to be mediatized by ${actingState}.`"
    sizeClass="found-modal"
    @close="emit('close')"
  >
    <div
      v-if="status.message"
      :class="['banner', status.type === 'error' ? 'error-banner' : 'success-banner']"
    >
      {{ status.message }}
    </div>

    <div class="modal-form">
      <h3>Mediatized State</h3>

      <SuggestionInput v-model="form.consumed" :options="stateOptions" />

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
import SuggestionInput from './SuggestionInput.vue'

const props = defineProps<{
  actingState: string
  states: any[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: { actingState: string; consumedState: string }): void
}>()

const form = ref({ consumed: '' })
const isProcessing = ref(false)
const status = ref({ message: '', type: '' as 'error' | 'success' | '' })

watch(
  () => form.value.consumed,
  () => {
    status.value = { message: '', type: '' }
  },
)

const actingStateURI = computed(() => {
  const state = props.states.find((s) => s.name === props.actingState)
  return state ? state.URI : props.actingState
})

const stateOptions = computed(() => {
  return props.states
    .filter((s) => s.name !== props.actingState)
    .map((s) => ({ label: s.name, value: s.URI }))
})

const isValid = computed(() => form.value.consumed.length > 0)

function handleSubmit() {
  if (!isValid.value) return

  const targetState = props.states.find(
    (s) => s.URI === form.value.consumed || s.name === form.value.consumed,
  )

  if (!targetState) {
    status.value = {
      message: 'State not found. Please select a valid state from the list.',
      type: 'error',
    }
    return
  }

  if (targetState.URI === actingStateURI.value) {
    status.value = {
      message: 'Cannot mediatize a state into itself.',
      type: 'error',
    }
    return
  }

  isProcessing.value = true

  emit('submit', {
    actingState: actingStateURI.value,
    consumedState: targetState.URI, // Always emit the URI
  })
}

defineExpose({
  setError: (msg: string) => {
    isProcessing.value = false
    status.value = { message: msg, type: 'error' }
  },
})
</script>
