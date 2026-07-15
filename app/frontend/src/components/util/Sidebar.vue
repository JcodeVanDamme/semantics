<template>
  <aside class="sidebar">
    <div class="logo-box">
      <div class="logo-icon">
        <Map />
      </div>
      <h2 class="logo-text">Semantics<br />RDF System</h2>
    </div>

    <div class="divider"></div>

    <ul class="menu">
      <router-link
        v-for="item in menuItems"
        :key="item.path"
        :to="item.path"
        custom
        v-slot="{ navigate, isActive }"
      >
        <li @click="navigate" :class="{ active: isActive }">
          <component :is="item.icon" />
          {{ item.label }}
        </li>
      </router-link>
    </ul>

    <div class="divider"></div>
  </aside>
</template>

<script setup lang="ts">
import { markRaw, type Component } from 'vue'
import { Map, GitBranch, Terminal, Database } from 'lucide-vue-next'

interface MenuItem {
  label: string
  path: string
  icon: Component
}

// Configuration driven menu
const menuItems: MenuItem[] = [
  { label: 'Dashboard', path: '/', icon: markRaw(Map) },
  { label: 'Triple Explorer', path: '/triple-explorer', icon: markRaw(GitBranch) },
  { label: 'Query Builder', path: '/query-builder', icon: markRaw(Terminal) },
  { label: 'Store Management', path: '/store-management', icon: markRaw(Database) },
]
</script>

<style scoped>
/* --- Sidebar Container --- */
.sidebar {
  position: relative;
  background: var(--primaryColor);
  border-right: 2px solid var(--borderColor);
  padding: var(--padding);
}

/* --- Logo Section --- */
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

.logo-text {
  line-height: 1;
  text-align: center;
  font-family: var(--fancyFontStyle);
  font-weight: normal;
  color: var(--accentColor);
  text-transform: uppercase;
  letter-spacing: 0.1rem;
}

/* --- Menu List --- */
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
  align-items: center;
  gap: var(--paddingHalf);
  border-left: 4px solid transparent;
  transition: all 0.2s ease;
}

.menu li svg {
  width: 24px;
  height: 24px;
}

/* --- Menu States --- */
.menu li.active {
  background: var(--secondaryColor);
  border-left: 4px solid var(--accentColor);
  color: var(--darkFontColor);
}

.menu li:hover {
  background: var(--highlightColor);
}
</style>
