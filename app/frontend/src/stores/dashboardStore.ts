import { defineStore } from 'pinia'
import { api } from '@/scripts/apiClient.ts'
import type { StateData } from '../scripts/type.ts'

interface DashboardState {
  activeStates: StateData[]
  allStates: StateData[]
  activeStatesCount: number
  stateChangesFactor: number
  loading: boolean
  errorMessage: string | null
}

export const useDashboardStore = defineStore('dashboard', {
  state: (): DashboardState => ({
    activeStates: [],
    allStates: [],
    activeStatesCount: 0,
    stateChangesFactor: 0,
    loading: false,
    errorMessage: null,
  }),

  actions: {
    async fetchDashboardData() {
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
      } finally {
        this.loading = false
      }
    },
  },
})
