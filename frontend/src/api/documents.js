import api from './axios'

export const getDocumentTypes = () => api.get('/document-types').then((r) => r.data)
export const getDocuments = (params) => api.get('/documents', { params }).then((r) => r.data)
export const getDocument = (id) => api.get(`/documents/${id}`).then((r) => r.data)
export const createDocument = (payload) => api.post('/documents', payload).then((r) => r.data)
export const updateDocument = (id, payload) => api.put(`/documents/${id}`, payload).then((r) => r.data)
export const cancelDocument = (id) => api.post(`/documents/${id}/cancel`).then((r) => r.data)

export const uploadVersion = (documentId, file, onProgress) => {
  const body = new FormData()
  body.append('file', file)
  return api
    .post(`/documents/${documentId}/versions`, body, {
      headers: { 'Content-Type': 'multipart/form-data' },
      onUploadProgress: (e) => e.total && onProgress?.(Math.round((e.loaded / e.total) * 100))
    })
    .then((r) => r.data)
}

// Downloads through axios so the login cookie is sent and errors can be shown in the page.
export async function downloadVersion(documentId, version) {
  try {
    const res = await api.get(`/documents/${documentId}/versions/${version.id}/download`, {
      responseType: 'blob'
    })
    const url = URL.createObjectURL(res.data)
    const a = document.createElement('a')
    a.href = url
    a.download = version.fileName
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)
  } catch (err) {
    // With responseType blob, an error body arrives as a Blob; turn it back into JSON.
    if (err.response?.data instanceof Blob) {
      try {
        err.response.data = JSON.parse(await err.response.data.text())
      } catch {
        /* keep as is */
      }
    }
    throw err
  }
}