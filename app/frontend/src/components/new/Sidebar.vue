<template>
  <div class="sidebar">
    <div class="logo-box">
      <div class="logo-icon">
        <Map />
      </div>

      <h2 class="logo-text">
        Semantics
        <br />
        RDF System
      </h2>
    </div>

    <div class="divider"></div>

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
        <li @click="navigate" :class="{ active: isActive }">
          <Terminal />
          Query Builder
        </li>
      </router-link>

      <router-link to="/store-management" custom v-slot="{ navigate, isActive }">
        <li @click="navigate" :class="{ active: isActive }">
          <Database />
          Store Management
        </li>
      </router-link>
    </ul>

    <div class="divider"></div>
  </div>
</template>

<script setup lang="ts">
import { Map, GitBranch, Terminal, Database } from 'lucide-vue-next'
</script>

<style scoped>
.sidebar {
  position: relative;
  background: var(--primaryColor);
  border-right: 2px solid var(--borderColor);
  padding: var(--padding);
}

.logo-box {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
  padding: var(--paddingHalf);
}

.logo-icon {
  width: 66px;
  height: 86px;
  background: #1c2c78;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  margin-right: 15px;
}

.logo-icon svg {
  width: 50px;
  height: 50px;
}

.logo-text {
  line-height: 1;
  text-align: center;
  font-family: var(--fancyFontStyle);
  font-weight: normal;
  color: var(--accentColor);
  text-transform: uppercase;
  letter-spacing: 0.1rem;
}

.menu {
  list-style: none;
  padding-left: 0;
}

.menu li {
  padding: var(--paddingDouble);
  color: #555;
  font-size: var(--text-h3);
  font-family: var(--fancyFontStyle);
  cursor: pointer;
  display: flex;
  gap: var(--paddingHalf);
  border-left: 4px solid transparent;
}

.menu li svg {
  width: 24px;
  height: 24px;
}

.menu li.active {
  background: var(--secondaryColor);
  border-left: 4px solid var(--accentColor);
  color: var(--darkFontColor);
}

.menu li:hover {
  background: var(--highlightColor);
}
</style>
