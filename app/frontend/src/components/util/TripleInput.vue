<template>
  <div class="triple-input-group">
    <div class="input-header">
      <h3>{{ label }}</h3>

      <div v-if="hasToggle" class="toggle-group" role="group" aria-label="Input mode">
        <button
          type="button"
          class="toggle-btn"
          :class="{ active: mode === 'literal' }"
          @click="mode = 'literal'"
        >
          Literal
        </button>
        <span class="toggle-divider">/</span>
        <button
          type="button"
          class="toggle-btn"
          :class="{ active: mode === 'uri' }"
          @click="mode = 'uri'"
        >
          URI
        </button>
      </div>
    </div>

    <div class="inputs-stack">
      <input
        v-model="value"
        type="text"
        :placeholder="placeholderValue || 'Enter value...'"
        aria-label="Value"
      />

      <input
        v-model="uri"
        type="text"
        :placeholder="placeholderUri || 'Namespace (Optional)'"
        class="uri-input"
        :class="{ 'invisible-spacer': hasToggle && mode !== 'uri' }"
        aria-label="Namespace URI"
      />

      <div v-if="showPreview" class="uri-preview">
        <span class="preview-label">Generated:</span>
        <span class="preview-string">{{ uriPreview }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { DEFAULT_NAMESPACE } from '../../utils/util.ts'

const props = defineProps<{
  label: string
  placeholderValue?: string
  placeholderUri?: string
  hasToggle?: boolean
}>()

const value = defineModel<string>('value', { default: '' })
const uri = defineModel<string>('uri', { default: '' })
const mode = defineModel<'literal' | 'uri'>('mode', { default: 'literal' })

const uriPreview = computed(() => {
  const cleanToken = value.value.trim()
  const cleanBase = uri.value.trim() || DEFAULT_NAMESPACE

  const isUriMode = props.hasToggle ? mode.value === 'uri' : true

  if (!isUriMode) {
    return cleanToken
  }

  const needsSlash = !cleanBase.endsWith('/') && !cleanBase.endsWith('#')
  const fullUri = `${cleanBase}${needsSlash ? '/' : ''}${cleanToken}`

  return cleanToken ? fullUri : cleanBase
})

const showPreview = computed(() => value.value.trim().length > 0)
</script>

<style scoped>
.triple-input-group {
  display: flex;
  flex-direction: column;
  margin-bottom: var(--padding);
}

.input-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--paddingHalf);
}

.input-header h3 {
  margin: 0;
  font-size: var(--text-h4);
  color: var(--mainFontColor);
}

/* --- Toggle Buttons --- */
.toggle-group {
  display: flex;
  gap: 6px;
  font-size: 12px;
}

.toggle-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--mutedFontColor);
  padding: 0;
  font-size: calc(var(--base-size) * 1.1);
  text-transform: uppercase;
}

.toggle-btn.active {
  color: var(--accentColor);
  font-weight: bold;
}

/* --- Inputs --- */
.inputs-stack {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

input {
  border: none;
  border-bottom: 2px solid var(--borderColor);
  background: transparent;
  padding: 12px 10px;
  font-size: 15px;
  outline: none;
  color: var(--mainFontColor);
  transition: border-color 0.2s;
}

input:focus {
  border-color: var(--accentColor);
}

.uri-input {
  font-size: 13.5px;
  font-style: italic;
  border-bottom-style: dashed;
}

.invisible-spacer {
  visibility: hidden;
  height: 0;
  padding-top: 0;
  padding-bottom: 0;
  border: none;
}

/* --- Preview --- */
.uri-preview {
  margin-top: 4px;
  font-size: 13px;
}

.preview-label {
  color: var(--mutedFontColor);
  margin-right: var(--paddingHalf);
}

.preview-string {
  color: var(--accentColor);
  font-family: monospace;
  word-break: break-all;
}
</style>
