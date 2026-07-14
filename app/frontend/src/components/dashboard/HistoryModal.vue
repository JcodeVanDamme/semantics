<template>
  <BaseModal
    title="State History"
    description="View all logged State-Relevant-Actions."
    sizeClass="found-modal"
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <div v-if="isLoading" class="loading-container">Loading History...</div>

    <div v-else-if="!errorMessage" class="history-container">
      <HistoryEntry v-for="(entry, index) in history" :key="index" :response="entry" />

      <div v-if="history.length === 0">No historical records found.</div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../scripts/api.ts'
import { type HistoryResponse } from '../../scripts/type.ts'
import BaseModal from './BaseModal.vue'
import HistoryEntry from './HistoryEntry.vue'

interface HistoryApiResponse {
  history: HistoryResponse[]
}

const emit = defineEmits<{ (e: 'close'): void }>()

const history = ref<HistoryResponse[]>([])
const isLoading = ref(true)
const errorMessage = ref<string | null>(null)

onMounted(async () => {
  try {
    const data = (await api.getHistory()) as HistoryApiResponse
    history.value = data.history
  } catch (err: any) {
    errorMessage.value = err.message || 'Failed to load history data.'
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
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

.history-container {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
  max-height: 70vh;
  overflow-y: auto;
}
</style>
