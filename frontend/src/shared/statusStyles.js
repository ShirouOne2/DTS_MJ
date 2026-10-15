const STATUSES = {
  DRAFT: { label: 'Draft', style: 'bg-gray-100 text-gray-600' },
  IN_ROUTING: { label: 'In Routing', style: 'bg-amber-50 text-amber-600' },
  FOR_REVISION: { label: 'For Revision', style: 'bg-rose-50 text-rose-600' },
  COMPLETED: { label: 'Completed', style: 'bg-emerald-50 text-emerald-600' },
  CANCELLED: { label: 'Cancelled', style: 'bg-gray-100 text-gray-400' }
}

export const DOCUMENT_STATUSES = Object.entries(STATUSES).map(([value, s]) => ({ value, label: s.label }))

export function statusLabel(status) {
  return STATUSES[status]?.label || status
}

export function statusStyles(status) {
  return STATUSES[status]?.style || 'bg-gray-100 text-gray-600'
}