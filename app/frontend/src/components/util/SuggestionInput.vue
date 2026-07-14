<template>
  <div class="suggestion-wrapper" @focusout="handleFocusOut">
    <div class="input-row">

      <input :value="displayValue" @input="handleInput" placeholder="Type or select..." />
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
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ChevronDown } from 'lucide-vue-next'

interface SuggestionOption {
  label: string
  value: any
}

const props = defineProps<{
  modelValue: any
  options: SuggestionOption[]
}>()

const emit = defineEmits(['update:modelValue'])

const isOpen = ref(false)

// 1. Find the label for the current value to display in the input
const displayValue = computed(() => {
  const selected = props.options.find((o) => o.value === props.modelValue)
  return selected ? selected.label : props.modelValue
})

// 2. Handle manual typing (optional: emits the typed string directly)
const handleInput = (e: Event) => {
  const target = e.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

const handleFocusOut = (event: FocusEvent) => {
  const target = event.relatedTarget as Node
  if (event.currentTarget && (event.currentTarget as HTMLElement).contains(target)) return
  isOpen.value = false
}

// 3. Emit the 'value' (the ID) when an item is selected
const selectOption = (opt: SuggestionOption) => {
  emit('update:modelValue', opt.value)
  isOpen.value = false
}
</script>

<style scoped>
/* ... keep your existing CSS exactly as it was ... */
.suggestion-wrapper {
  position: relative;
  width: 100%;
}
.input-row {
  display: flex;
  align-items: center;
  position: relative;
}
input {
  flex: 1;
  border: none;
  border-bottom: 2px solid var(--borderColor);
  background: transparent;
  padding: 12px 10px;
  font-size: 15px;
  outline: none;
  color: var(--mainFontColor);
  transition: border-color 0.2s;
  padding-top: 0;
}
input:focus {
  border-color: var(--accentColor);
}
input::placeholder {
  color: #999;
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
