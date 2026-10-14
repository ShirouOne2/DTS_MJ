<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <BuildingOffice2Icon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Recipients</h2>
    </div>

    <ul v-if="recipients.length" class="divide-y divide-gray-100">
      <li v-for="r in recipients" :key="r.id" class="flex items-center justify-between px-6 py-3">
        <span class="text-sm font-medium text-gray-700">{{ r.departmentName }}</span>
        <span class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-semibold" :class="ladderStyle(r.status)">
          <component :is="ladderIcon(r.status)" class="h-3.5 w-3.5" />
          {{ ladderLabel(r.status) }}
        </span>
      </li>
    </ul>

    <p v-else class="px-6 py-6 text-sm text-gray-400">No active recipients on this document.</p>
  </div>
</template>

<script setup>
import {
  BuildingOffice2Icon,
  ClockIcon,
  TruckIcon,
  EyeIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'

// recipients: TransactionRecipientDto[] from DocumentDetailDto.currentTransaction.recipients
defineProps({
  recipients: {
    type: Array,
    default: () => []
  }
})

// pending -> delivered -> viewed -> received, matches TRANSACTION_RECIPIENTS.status exactly
const LADDER = {
  pending: { label: 'Pending', icon: ClockIcon, style: 'bg-gray-100 text-gray-500' },
  delivered: { label: 'Delivered', icon: TruckIcon, style: 'bg-sky-50 text-sky-600' },
  viewed: { label: 'Viewed', icon: EyeIcon, style: 'bg-amber-50 text-amber-600' },
  received: { label: 'Received', icon: CheckCircleIcon, style: 'bg-emerald-50 text-emerald-600' }
}

function ladderLabel(status) {
  return LADDER[status]?.label || status
}
function ladderIcon(status) {
  return LADDER[status]?.icon || ClockIcon
}
function ladderStyle(status) {
  return LADDER[status]?.style || 'bg-gray-100 text-gray-500'
}
</script>
