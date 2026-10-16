import api from './axios'

export const getInbox = () => api.get('/inbox').then((r) => r.data)