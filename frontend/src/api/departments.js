import api from './axios'

export const getAllDepartments = () => api.get('/admin/departments').then((r) => r.data)
export const getActiveDepartments = () => api.get('/departments').then((r) => r.data)
export const createDepartment = (payload) => api.post('/admin/departments', payload).then((r) => r.data)
export const updateDepartment = (id, payload) => api.put(`/admin/departments/${id}`, payload).then((r) => r.data)
export const setDepartmentActive = (id, active) =>
  api.patch(`/admin/departments/${id}/active`, { active }).then((r) => r.data)