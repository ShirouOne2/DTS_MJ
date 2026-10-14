<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <PaperAirplaneIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Transmittal History</h2>
    </div>

    <ol class="px-6 py-5">
      <li
        v-for="(entry, index) in transmittals"
        :key="entry.id"
        class="relative flex gap-4 pb-6 last:pb-0"
      >
        <div class="flex flex-col items-center">
          <span
            class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full"
            :class="index === 0 ? 'bg-blue-600 text-white' : 'bg-blue-50 text-blue-600'"
          >
            <ArrowRightCircleIcon class="h-5 w-5" />
          </span>
          <span
            v-if="index !== transmittals.length - 1"
            class="mt-1 w-px flex-1 bg-gray-200"
          ></span>
        </div>

        <div class="min-w-0 flex-1 pt-1">
          <p class="text-sm font-semibold text-gray-800">
            {{ entry.fromDepartmentName || 'System' }}
            <template v-if="entry.recipientSummary">
              <span class="text-gray-400">&rarr;</span>
              {{ entry.recipientSummary }}
            </template>
          </p>
          <p class="mt-0.5 text-xs font-medium text-blue-600">{{ entry.action }}</p>
          <p class="mt-1 text-xs text-gray-400">{{ formatDate(entry.createdAt) }}</p>
          <p v-if="entry.remarks" class="mt-1 text-xs text-gray-500">{{ entry.remarks }}</p>
        </div>
      </li>

      <li v-if="transmittals.length === 0" class="py-10 text-center text-sm text-gray-400">
        No transmittal history yet.
      </li>
    </ol>
  </div>
</template>

<script setup>
import { PaperAirplaneIcon, ArrowRightCircleIcon } from '@heroicons/vue/24/outline'

// Pass TransmittalDto[] — from DocumentDetailDto.history or TrackingResultDto.history
defineProps({
  transmittals: {
    type: Array,
    default: () => []
  }
})

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
