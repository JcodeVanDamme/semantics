<template>
  <BaseModal
    title="CHANGE RULER:"
    description="Select an existing ruler or provide details to create a new one."
    @close="emit('close')"
  >
    <div class="modal-form">
      <p v-if="form.isNewRuler" class="note-banner">
        New ruler detected. Please provide label and title to register.
      </p>

      <h3>Ruler-URI</h3>
      <SuggestionInput v-model="form.uri" :options="rulerOptions" />

      <h3>Ruler-Label</h3>
      <input
        v-model="form.label"
        :disabled="!form.isNewRuler"
        placeholder="e.g. Frederick the Great"
      />

      <h3>Ruler-Title</h3>
      <input v-model="form.title" :disabled="!form.isNewRuler" placeholder="e.g. King of Prussia" />

      <div class="input-button-wrapper">
        <button class="button accent" :disabled="!isValid" @click="handleSubmit">
          <Flag :size="18" />
          CONFIRM CHANGE
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
  states: any[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: any): void
}>()

const rulerOptions = computed(() => {
  const map = new Map()
  props.states.forEach((s) => {
    if (s.ruler?.URI) {
      map.set(s.ruler.URI, s.ruler.name)
    }
  })
  return Array.from(map.entries()).map(([value, label]) => ({ label, value }))
})

const form = ref({
  uri: '',
  label: '',
  title: '',
  isNewRuler: false,
})

watch(
  () => form.value.uri,
  (newUri) => {
    if (!newUri) {
      form.value.isNewRuler = false
      return
    }
    alert(newUri)

    const exists = rulerOptions.value.find((o) => o.value === newUri)
    form.value.isNewRuler = !exists
  },
)

const isValid = computed(() => {
  if (form.value.isNewRuler) {
    return form.value.uri && form.value.label && form.value.title
  }
  return form.value.uri.length > 0
})

function handleSubmit() {
  if (!isValid.value) return
  emit('submit', { ...form.value })
  form.value = { uri: '', label: '', title: '', isNewRuler: false }
}
</script>

<style scoped>

.modal-form input:disabled {
  opacity: 0.5;
  border-bottom: 2px solid #ccc;
}

</style>
