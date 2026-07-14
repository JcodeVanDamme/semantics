<template>
  <BaseModal
    title="STATE FOUNDING"
    description="Fill out the fields below to create your new State."
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <div class="modal-form">
      <BaseInput label="State Name" v-model="form.name" placeholder="Kingdom of..." />

      <label class="select-header">Ruler (URI)</label>
      <select v-model="form.ruler" class="base-input">
        <option value="" disabled selected>Select a Ruler URI</option>
        <option v-for="uri in rulerOptions" :key="uri" :value="uri">
          {{ uri }}
        </option>
      </select>

      <BaseInput label="Population" v-model="form.population" placeholder="Number" type="number" />
      <BaseInput label="State Type" v-model="form.type" placeholder="Kingdom, Duchy..." />

      <div class="input-button-wrapper">
        <button class="button accent" :disabled="!isValid" @click="handleSubmit">
          <Flag :size="18" />
          FOUND STATE
        </button>
      </div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Flag } from 'lucide-vue-next'
import BaseModal from './BaseModal.vue'
import BaseInput from '../util/BaseInput.vue'
import { useDashboardStore } from '@/stores/dashboardStore'
const store = useDashboardStore()
import { api, DATA_URI } from '@/scripts/api.ts'

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: any): void
}>()
const errorMessage = ref<string | null>(null)

const form = ref({
  name: '',
  ruler: '',
  population: 0,
  type: '',
})

const rulerOptions = computed(() => {
  if (!store.allStates) return []
  const uniqueUris = new Set<string>()
  store.allStates.forEach((state) => {
    if (state.ruler?.URI) {
      uniqueUris.add(state.ruler.URI)
    }
  })

  return Array.from(uniqueUris)
})

const isValid = computed(() => {
  return (
    form.value.name.length > 0 &&
    form.value.ruler.length > 0 &&
    form.value.type.length > 0 &&
    Number(form.value.population) > 0
  )
})

async function handleSubmit() {
  errorMessage.value = null
  const formattedName = form.value.name.replace(/\s+/g, '_')
  const stateUri = `${DATA_URI}${formattedName}`

  try {
    const res = await api.foundState({
      state: stateUri,
      label: form.value.name,
      ruler: form.value.ruler,
      population: Number(form.value.population),
      type: form.value.type,
    })

    console.log("Found Res: ", res)
    emit('submit', res)

  } catch (err: Any) {
    errorMessage.value = err.message || 'Encountered an unexpected Error.'
  }
}
</script>
