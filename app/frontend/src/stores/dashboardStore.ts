import { defineStore } from 'pinia'
import { api } from '@/scripts/api'
import type { StateData } from '../scripts/type.ts'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    activeStates: [] as StateData[],
    allStates: [] as StateData[],
    activeStatesCount: 0,
    stateChangesFactor: 0,
    loading: false,
    errorMessage: null as string | null, // Simply null, no ref() needed here
  }),

  actions: {
    async fetchDashboardData() {
      // Use 'this' to access state in Options stores
      this.loading = true
      this.errorMessage = null

      try {
        const [activeRes, allRes, statsRes, changesRes] = await Promise.all([
          api.getStates(true),
          api.getStates(false),
          api.getActiveStateCount(),
          api.getStateChanges(),
        ])

        this.activeStates = activeRes.states
        this.allStates = allRes.states
        this.activeStatesCount = statsRes.count
        this.stateChangesFactor = changesRes.factor
      } catch (error) {
        this.errorMessage = 'Failed to load dashboard data. Please check your connection.'
        console.error('Failed to load dashboard data:', error)
      } finally {
        this.loading = false
      }
    },
  },
})
