import api from './axios'

export const getActionCodes = () => api.get('/action-codes').then((r) => r.data)
export const submitRoute = (documentId, payload) =>
  api.post(`/documents/${documentId}/route`, payload).then((r) => r.data)

const stepUrl = (docId, stepId) => `/documents/${docId}/steps/${stepId}`

export const receiveStep = (docId, stepId) =>
  api.post(`${stepUrl(docId, stepId)}/receive`).then((r) => r.data)

export const completeStep = (docId, stepId, remarks, file) => {
  const body = new FormData()
  if (remarks) body.append('remarks', remarks)
  if (file) body.append('file', file)
  return api
    .post(`${stepUrl(docId, stepId)}/complete`, body, { headers: { 'Content-Type': 'multipart/form-data' } })
    .then((r) => r.data)
}

export const rejectStep = (docId, stepId, remarks) =>
  api.post(`${stepUrl(docId, stepId)}/reject`, { remarks }).then((r) => r.data)

export const acknowledgeStep = (docId, stepId, remarks) =>
  api.post(`${stepUrl(docId, stepId)}/acknowledge`, { remarks }).then((r) => r.data)