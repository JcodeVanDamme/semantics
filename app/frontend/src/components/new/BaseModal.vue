<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-box card" :class="sizeClass">
      <div class="modal-header">
        <div class="top">
          <div class="text-wrapper">
            <h2>{{ title }}</h2>
            <p v-if="description">{{ description }}</p>
          </div>
          <button type="button" class="close-button button accent" @click="emit('close')">
            <X :size="18" />
          </button>
        </div>
        <div class="divider accent"></div>
      </div>

      <div class="modal-body">
        <slot />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { X } from 'lucide-vue-next'

defineProps<{
  title: string
  description?: string
  sizeClass?: 'large-modal' | 'found-modal' | ''
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()
</script>

<style>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-box {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  max-width: 90%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  flex-direction: column;
}

.top {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  flex: 1;
}

.close-button {
  font-size: var(--base-size);
  height: fit-content;
  padding: 5px;
  margin-left: var(--paddingDouble);
}

.text-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--paddingHalf);
}

.text-wrapper * {
  margin: 0;
}

/* --- FORM CONTAINER STACK --- */
.modal-form {
  display: flex;
  flex-direction: column;
  gap: 16px; /* Keeps the inputs beautifully spaced vertically */
  width: 100%;
}

/* --- REPLICATED INPUT STYLINGS --- */
.modal-form input {
  border: none;
  border-bottom: 2px solid var(--borderColor);
  background: transparent;
  padding: 12px 10px;
  font-size: 15px;
  outline: none;
  color: var(--mainFontColor);
  transition: border-color 0.2s;
}

.modal-form input:focus {
  border-color: var(--accentColor);
}

.modal-form input::placeholder {
  color: #999;
}

.modal-form h3 {
  margin-top: var(--padding);
  margin-bottom: 0;
}

.input-button-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--padding);
}
</style>
