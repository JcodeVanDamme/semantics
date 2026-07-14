<template>
  <div class="input-group">
    <label v-if="label">{{ label }}</label>
    <input
      :value="modelValue"
      :placeholder="placeholder"
      :type="type || 'text'"
      @input="updateValue"
      class="base-input"
    />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  modelValue: string | number
  label?: string
  placeholder?: string
  type?: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void
}>()

const updateValue = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>
