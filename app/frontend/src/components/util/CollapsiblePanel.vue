<template>
  <div class="collapsible-panel">
    <div class="panel-header" @click="togglePanel">
      <div class="header-left">
        <slot name="header"></slot>
      </div>

      <div class="help-icon">
        <CircleHelp />
      </div>
    </div>

    <div class="panel-body" :class="{ 'is-expanded': isOpen }">
      <div class="body-inner">
        <slot name="info"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { CircleHelp } from 'lucide-vue-next'

const isOpen = ref(false)

const togglePanel = () => {
  isOpen.value = !isOpen.value
}
</script>

<style scoped>
.collapsible-panel {
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.header-left {
  flex: 1;
  min-width: 0;
}

.help-icon svg {
  color: var(--accentColor);
  width: 24px;
  height: 24px;
}

.panel-body {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.panel-body.is-expanded {
  grid-template-rows: 1fr;
}

.body-inner {
  overflow: hidden;
  min-height: 0;
  background: var(--highlightColor);
  border-left: 4px solid var(--accentColor);
}
</style>
