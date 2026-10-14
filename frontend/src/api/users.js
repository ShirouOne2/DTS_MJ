import api from './axios'

export const getUsers = () => api.get('/admin/users').then((r) => r.data)
export const createUser = (payload) => api.post('/admin/users', payload).then((r) => r.data)
export const updateUser = (id, payload) => api.put(`/admin/users/${id}`, payload).then((r) => r.data)
export const setUserActive = (id, active) =>
  api.patch(`/admin/users/${id}/active`, { active }).then((r) => r.data)
export const resetUserPassword = (id, newPassword) =>
  api.post(`/admin/users/${id}/reset-password`, { newPassword })