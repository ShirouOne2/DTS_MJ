export function errorMessage(err) {
  const data = err.response?.data
  if (data?.details?.length) return data.details.join(', ')
  return data?.message || 'Something went wrong. Please try again.'
}