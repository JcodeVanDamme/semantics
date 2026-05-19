<template>
  
  <div class="sidebar">
    <!-- LOGO -->
    <div class="logo-box">
      
      <div class="logo-icon">
        <Map />
      </div>

      <div class="logo-text">
        SOME TITLE IS
        <br />
        PLACED HERE
      </div>

    </div>

    <!-- MENU -->
    <ul class="menu">
      
      <router-link to="/" custom v-slot="{ navigate, isActive }">
        <li @click="navigate" :class="{ active: isActive }">
            <Map />
            Dashboard
        </li>
      </router-link>

      <router-link to="/triple-explorer" custom v-slot="{ navigate, isActive }">
        <li @click="navigate" :class="{ active: isActive }">
            <GitBranch />
            Triple Explorer
        </li>
      </router-link>

      <router-link to="/query-builder" custom v-slot="{ navigate, isActive }">
        <li @click="navigate" :class="{ active: isActive }" >
            <Terminal />
            Query Builder
        </li>
      </router-link>

      <router-link to="/store-management" custom v-slot="{ navigate, isActive }" >
        <li @click="navigate" :class="{ active: isActive }">
            <Database />
            Store Management
        </li>
      </router-link>
    </ul>
    
    <div class="sidebar-line"></div>

  </div>
</template>

<script setup lang="ts">
import {
  Map,
  GitBranch,
  Terminal,
  Database
} from 'lucide-vue-next'
</script>