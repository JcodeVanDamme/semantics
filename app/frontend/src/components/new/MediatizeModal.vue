<template>
  <BaseModal
    title="MEDIATIZATION:"
    description="Select the target State this one will be mediatized into"
    sizeClass="large-modal"
    @close="emit('close')"
  >
    <input
      v-model="searchQuery"
      class="modal-search"
      placeholder="Search by Statename, Ruler or Statetype"
    />

    <table class="modal-table">
      <thead>
        <tr>
          <th>NAME</th>
          <th>RULER</th>
          <th>MED. STATES</th>
          <th>REGIONS</th>
          <th>POPULATION</th>
          <th>STATE TYPE</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-v-for="(state, index) in filteredStates"
          :key="index"
          @click="selectedTarget = state"
          :class="{ selected: selectedTarget?.name === state.name }"
        >
          <td>{{ state.name }}</td>
          <td>{{ state.ruler }}</td>
          <td>{{ state.mediatizedStates }}</td>
          <td>{{ state.regions }}</td>
          <td>{{ state.population }}</td>
          <td>{{ state.type }}</td>
        </tr>
      </tbody>
    </table>

    <button class="modal-action-btn" :disabled="!selectedTarget" @click="handleSubmit">
      <Flag :size="18" />
      MEDIATIZATE
    </button>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Flag } from 'lucide-vue-next'
import BaseModal from './BaseModal.vue'

const props = defineProps<{
  states: any[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', targetState: any): void
}>()

const searchQuery = ref('')
const selectedTarget = ref<any>(null)

const filteredStates = computed(() => {
  return props.states.filter((state) =>
    state.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

function handleSubmit() {
  if (!selectedTarget.value) return
  emit('submit', selectedTarget.value)
}
</script>
