<template>
  <div class="triple-input-group">
    <div class="input-header">
      <h3>{{ label }}</h3>

      <div v-if="hasToggle" class="toggle-labels">
        <span
          class="toggle-label"
          :class="{ active: mode === 'literal' }"
          @click="mode = 'literal'"
        >
          Literal
        </span>
        <span class="toggle-divider">/</span>
        <span class="toggle-label" :class="{ active: mode === 'uri' }" @click="mode = 'uri'">
          URI
        </span>
      </div>
    </div>

    <div class="inputs-stack">
      <input v-model="value" type="text" :placeholder="placeholderValue" />

      <input
        v-model="uri"
        type="text"
        :placeholder="placeholderUri || 'Optional: Custom URI Namespace'"
        class="uri-input"
        :class="{ 'invisible-spacer': hasToggle && mode !== 'uri' }"
      />

      <div v-if="showPreview" class="uri-preview">
        <span class="preview-label">Final {{ label }}:</span>
        <span class="preview-string">{{ uriPreview }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { DEFAULT_NAMESPACE } from '../../utils/util.ts'

// Component Properties
defineProps<{
  label: string
  placeholderValue?: string
  placeholderUri?: string
  hasToggle?: boolean
}>()

const value = defineModel<string>('value', { default: '' })
const uri = defineModel<string>('uri', { default: '' })
const mode = defineModel<'literal' | 'uri'>('mode', { default: 'literal' })

const uriPreview = computed(() => {
  const cleanToken = value.value.replace(/\s+/g, '') // Entfernt alle Whitespaces
  if (!cleanToken) return ''

  const cleanBase = uri.value.trim() || DEFAULT_NAMESPACE
  const needsSlash = !cleanBase.endsWith('/') && !cleanBase.endsWith('#')

  return `${cleanBase}${needsSlash ? '/' : ''}${cleanToken}`
})

const showPreview = computed(() => {
  return value.value.trim().length > 0
})
</script>

<style scoped>
.triple-input-group {
  display: flex;
  flex-direction: column;
}

.input-header {
  display: flex;
  gap: var(--padding);
  align-items: center;
  justify-content: space-between;
}

.input-header h3 {
  margin: var(--paddingHalf);
}

/* --- INPUT TEXT ELEMENTS --- */
.inputs-stack {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.triple-input-group input {
  border: none;
  border-bottom: 2px solid var(--borderColor);
  background: transparent;
  padding: 12px 10px;
  font-size: 15px;
  outline: none;
  color: var(--mainFontColor);
  transition: border-color 0.2s;
}

.triple-input-group input:focus {
  border-color: var(--accentColor);
}

.triple-input-group input::placeholder {
  color: #999;
}

.triple-input-group .uri-input {
  font-size: 13.5px;
  font-style: italic;
  border-bottom-style: dashed; /* Softly differentiates standard string vs URI specs */
}

.invisible-spacer {
  visibility: hidden;
  pointer-events: none; /* Verhindert, dass der Nutzer das unsichtbare Feld per Tab/Klick fokussiert */
}

.uri-preview {
  padding-top: var(--padding);
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.preview-label {
  font-weight: bolder;
  padding-right: var(--paddingHalf);
}

.preview-string {
  font-weight: bolder;
  color: var(--accentColor);
  line-break: anywhere;
}
</style>
