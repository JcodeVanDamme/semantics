<template>

  <div class="dashboard-container">
    <Sidebar />
    <div class="main-content">
      <slot />
    </div>
  </div>

</template>

<script setup>
import Sidebar from '../components/util/Sidebar.vue'
</script>
