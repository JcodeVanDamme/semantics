<template>
  <BaseModal
    title="State History"
    description="View all logged State-Relevant-Actions."
    sizeClass="found-modal"
    @close="emit('close')"
  >
    <div v-if="isLoading" class="loading-container">Loading History...</div>

    <div v-else class="history-container">
      <HistoryEntry v-for="(entry, index) in history" :key="index" :response="entry" />

      <div v-if="history.length === 0" class="empty-state">No historical records found.</div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api, type HistoryResponse } from '../../services/api'
import BaseModal from './BaseModal.vue'
import HistoryEntry from './HistoryEntry.vue'

const emit = defineEmits<{ (e: 'close'): void }>()

const history = ref<HistoryResponse[]>([])
const isLoading = ref(true)

onMounted(async () => {
  try {
    const data = await api.getHistory()
    history.value = data
  } catch (err) {
    console.error('Failed to load history.')
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.modal-body {
  border: none !important;
}

.history-container {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
  max-height: 70vh;
  overflow-y: auto;
}

.loading-container {
  padding: 50px 200px;
}
</style>
