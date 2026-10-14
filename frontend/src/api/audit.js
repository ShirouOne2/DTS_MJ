import api from './axios'

export const getAuditLogs = (params) => api.get('/admin/audit-logs', { params }).then((r) => r.data)