// Single source of truth for DOCUMENTS.status — matches the schema exactly.
// Update colors here; every badge in the app pulls from this one place.

export const DOCUMENT_STATUSES = [
  'Incoming',
  'Ongoing',
  'For Revise',
  'Forwarded',
  'Released',
  'Completed'
]

const STYLE_MAP = {
  Incoming: 'bg-sky-50 text-sky-600',
  Ongoing: 'bg-amber-50 text-amber-600',
  'For Revise': 'bg-rose-50 text-rose-600',
  Forwarded: 'bg-blue-50 text-blue-600',
  Released: 'bg-emerald-50 text-emerald-600',
  Completed: 'bg-gray-100 text-gray-600'
}

export function statusStyles(status) {
  return STYLE_MAP[status] || 'bg-gray-100 text-gray-600'
}
