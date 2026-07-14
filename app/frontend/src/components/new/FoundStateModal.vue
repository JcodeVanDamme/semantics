<template>
  <BaseModal
    title="STATE FOUNDING:"
    description="Fill out the Fields below in order for your new State to be created."
    @close="emit('close')"
  >
    <div class="modal-form">
      <h3>State-URI</h3>
      <input v-model="form.name" placeholder="Statename" />
      <h3>State-Label</h3>
      <input v-model="form.ruler" placeholder="Ruler" />
      <h3>Population</h3>
      <input v-model="form.population" placeholder="Population" />
      <h3>State-Type-URI</h3>
      <input v-model="form.type" placeholder="Statetype" />
      <h3>State-Type-Label</h3>
      <input v-model="form.type" placeholder="Statetype" />

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
import SuggestionInput from '@/components/new/SuggestionInput.vue'

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', payload: { name: string; ruler: string; population: string; type: string }): void
}>()

const form = ref({
  name: '',
  ruler: '',
  population: '',
  type: '',
})

const isValid = computed(() => {
  return form.value.name && form.value.ruler && form.value.population && form.value.type
})

function handleSubmit() {
  if (!isValid.value) return
  emit('submit', { ...form.value })
  // Reset form
  form.value = { name: '', ruler: '', population: '', type: '' }
}
</script>
