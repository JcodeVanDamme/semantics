<template>
  <BaseModal
    title="RULER CHANGE:"
    description="Select the new Ruler for your selected State"
    @close="emit('close')"
  >
    <input v-model="searchQuery" class="modal-search" placeholder="Search by Name or Title" />

    <div class="modal-list">
      <div
        v-v-for="(ruler, index) in filteredRulers"
        :key="index"
        class="modal-row"
        :class="{ selected: selectedRuler?.name === ruler.name }"
        @click="selectedRuler = ruler"
      >
        <div>{{ ruler.name }}</div>
        <small>{{ ruler.title }}</small>
      </div>
    </div>

    <button class="modal-action-btn" :disabled="!selectedRuler" @click="handleSubmit">
      <Crown :size="18" />
      CHANGE RULER
    </button>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Crown } from 'lucide-vue-next'
import BaseModal from './BaseModal.vue'

interface Ruler {
  name: string
  title: string
}

const props = defineProps<{
  rulers: Ruler[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'submit', ruler: Ruler): void
}>()

const searchQuery = ref('')
const selectedRuler = ref<Ruler | null>(null)

const filteredRulers = computed(() => {
  return props.rulers.filter((ruler) =>
    ruler.name.toLowerCase().includes(searchQuery.value.toLowerCase()),
  )
})

function handleSubmit() {
  if (!selectedRuler.value) return
  emit('submit', selectedRuler.value)
}
</script>
