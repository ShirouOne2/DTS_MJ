<template>
  <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
    <div class="relative flex-1 sm:max-w-sm">
      <MagnifyingGlassIcon class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
      <input
        :value="search"
        type="text"
        placeholder="Search by title or owner..."
        class="w-full rounded-lg border border-gray-200 py-2 pl-9 pr-3 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
        @input="$emit('update:search', $event.target.value)"
      />
    </div>

    <div v-if="showStatusFilter" class="flex items-center gap-2">
      <FunnelIcon class="h-4 w-4 shrink-0 text-gray-400" />
      <select
        :value="status"
        class="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
        @change="$emit('update:status', $event.target.value)"
      >
        <option v-for="option in statuses" :key="option" :value="option">
          {{ option === 'All' ? 'All Statuses' : option }}
        </option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { MagnifyingGlassIcon, FunnelIcon } from '@heroicons/vue/24/outline'
import { DOCUMENT_STATUSES } from '../../shared/statusStyles'

defineProps({
  search: {
    type: String,
    default: ''
  },
  status: {
    type: String,
    default: 'All'
  },
  statuses: {
    type: Array,
    default: () => ['All', ...DOCUMENT_STATUSES]
  },
  showStatusFilter: {
    type: Boolean,
    default: true
  }
})

defineEmits(['update:search', 'update:status'])
</script>