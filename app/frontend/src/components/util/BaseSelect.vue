<template>
  <div class="input-group">
    <label v-if="label">{{ label }}</label>

    <div class="suggestion-wrapper" @focusout="handleFocusOut">
      <div class="input-row">
        <input
          :value="displayValue"
          @input="handleInput"
          :placeholder="placeholder"
          class="base-input"
        />
        <button @click="isOpen = !isOpen" class="toggle-btn" type="button">
          <ChevronDown :size="18" />
        </button>
      </div>

      <ul v-if="isOpen" class="expander-list">
        <li v-for="opt in options" :key="opt.value" @mousedown.prevent="selectOption(opt)">
          {{ opt.label }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChevronDown } from 'lucide-vue-next'

export interface SuggestionOption {
  label: string
  value: any
}

const props = defineProps<{
  modelValue: string | number
  label?: string
  placeholder?: string
  options: SuggestionOption[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void
}>()

const isOpen = ref(false)

const displayValue = computed(() => {
  const selected = props.options.find((o) => o.value === props.modelValue)
  return selected ? selected.label : props.modelValue
})

const handleInput = (e: Event) => {
  const target = e.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

const handleFocusOut = (event: FocusEvent) => {
  const target = event.relatedTarget as Node
  if (event.currentTarget && (event.currentTarget as HTMLElement).contains(target)) return
  isOpen.value = false
}

const selectOption = (opt: SuggestionOption) => {
  emit('update:modelValue', opt.value)
  isOpen.value = false
}
</script>

<style scoped>
/* Reuse the styles from BaseInput for consistency */
.input-group {
  display: flex;
  flex-direction: column;
  gap: var(--paddingHalf, 8px);
  margin-bottom: var(--padding, 16px);
}

label {
  font-size: var(--text-h4);
  color: var(--mainFontColor, #333);
  text-transform: uppercase;
}

.base-input {
  border: none;
  border-bottom: 2px solid var(--borderColor, #ccc);
  background: transparent;
  color: var(--mainFontColor, #333);
  font-family: var(--baseFontStyle), monospace;
  transition: border-color 0.2s ease;
  width: 100%;
}

/* Suggestion Specific Layout */
.suggestion-wrapper {
  position: relative;
  width: 100%;
}
.input-row {
  display: flex;
  align-items: center;
  position: relative;
}
.toggle-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--accentColor);
  padding: 5px;
  margin-left: -30px;
}
.expander-list {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: var(--secondaryColor);
  border: 1px solid var(--borderColor);
  list-style: none;
  padding: 5px 0;
  margin: 5px 0 0 0;
  z-index: 100;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
.expander-list li {
  padding: 10px;
  cursor: pointer;
  color: var(--mainFontColor);
}
.expander-list li:hover {
  background-color: var(--highlightColor, #f0f0f0);
}
</style>
