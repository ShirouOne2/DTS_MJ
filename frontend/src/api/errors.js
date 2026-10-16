export function errorMessage(err) {
  const data = err.response?.data
  if (data?.details?.length) return data.details.join(', ')
  return data?.message || 'Something went wrong. Please try again.'
}

// A duplicate-file warning is a 409 whose body says duplicate: true.
export function duplicateWarning(err) {
  const data = err.response?.data
  return err.response?.status === 409 && data?.duplicate ? data : null
}