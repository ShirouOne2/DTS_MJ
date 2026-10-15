import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://192.168.57.71:8082/api',
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true // sends the HttpOnly jwt cookie
})

// If the session expires mid-use, send the user back to the login page.
// /auth/login and /auth/me are excluded: a 401 there is an expected answer.
api.interceptors.response.use(
  (res) => res,
  (error) => {
    const url = error.config?.url || ''
    const isAuthCall = url.startsWith('/auth/login') || url.startsWith('/auth/me')
    if (error.response?.status === 401 && !isAuthCall && window.location.pathname !== '/login') {
      window.location.assign('/login?expired=1')
    }
    return Promise.reject(error)
  }
)

export default api