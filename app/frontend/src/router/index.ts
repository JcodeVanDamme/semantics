import { createRouter, createWebHistory } from 'vue-router'

import Dashboard from '../views/Dashboard.vue'
import TripleExplorer from '../views/TripleExplorer.vue'
import StoreManagement from '../views/StoreManagement.vue'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: Dashboard
  },
  {
    path: '/triple-explorer',
    name: 'tripleExplorer',
    component: TripleExplorer
  },
  {
  path: '/store-management',
  name: 'storeManagement',
  component: StoreManagement
}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
