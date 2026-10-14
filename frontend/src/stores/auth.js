import { reactive } from 'vue'
import api from '@/api/axios'

let initPromise = null

export const auth = reactive({
  user: null,

  get isLoggedIn() {
    return this.user !== null
  },
  get isAdmin() {
    return this.user?.role === 'ADMIN'
  },

  // Runs once per page load: asks the server who we are, using the cookie.
  init() {
    if (!initPromise) {
      initPromise = api
        .get('/auth/me')
        .then((res) => { this.user = res.data })
        .catch(() => { this.user = null })
    }
    return initPromise
  },

  async login(username, password) {
    const res = await api.post('/auth/login', { username, password })
    this.user = res.data
    initPromise = Promise.resolve()
  },

  async logout() {
    try {
      await api.post('/auth/logout')
    } finally {
      this.user = null
      initPromise = Promise.resolve()
    }
  }
})