import { reactive } from 'vue'
import api from '@/api/axios'

const EMPTY = { awaitingAction: 0, overdue: 0, drafts: 0, inRouting: 0, forRevision: 0 }

export const summary = reactive({
  ...EMPTY,

  async refresh() {
    try {
      const { data } = await api.get('/dashboard/summary')
      Object.assign(this, data)
    } catch {
      // A badge is a convenience: if it fails, don't break the page.
    }
  },

  reset() {
    Object.assign(this, EMPTY)
  }
})