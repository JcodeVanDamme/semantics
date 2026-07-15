<template>
  <BaseModal
    title="State History"
    description="View all logged State-Relevant-Actions."
    sizeClass="found-modal"
    :error-message="errorMessage"
    @close="emit('close')"
  >
    <!-- Loading State -->
    <div v-if="isLoading" class="loading-container">Loading History...</div>

    <!-- Data Content -->
    <div v-else-if="!errorMessage" class="history-container">
      <HistoryEntry v-for="(entry, index) in history" :key="index" :response="entry" />

      <div v-if="history.length === 0" class="empty-state">No historical records found.</div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '@/scripts/apiClient'
import type { HistoryEvent, HistoryApiResponse } from '@/scripts/type'
import BaseModal from './BaseModal.vue'
import HistoryEntry from './HistoryEntry.vue'

const emit = defineEmits<{ (e: 'close'): void }>()

const history = ref<HistoryEvent[]>([])
const isLoading = ref<boolean>(true)
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
/* --- LAYOUT --- */
.history-container {
  display: flex;
  flex-direction: column;
  gap: var(--padding);
  max-height: 70vh;
  overflow-y: auto;
}

/* --- STATE & FEEDBACK --- */
.loading-container {
  padding: 50px 200px;
  text-align: center;
  color: var(--mutedFontColor);
}

.empty-state {
  padding: var(--padding);
  text-align: center;
  color: var(--mutedFontColor);
}
</style>
