<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <ClockIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Latest Updates</h2>
    </div>

    <ul class="divide-y divide-gray-100">
      <li
        v-for="update in updates"
        :key="update.id"
        class="flex items-start gap-3 px-6 py-4 transition-colors hover:bg-blue-50/50"
      >
        <span
          class="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-full"
          :class="iconBg(update.type)"
        >
          <component :is="iconFor(update.type)" class="h-4 w-4" :class="iconColor(update.type)" />
        </span>
        <div class="min-w-0 flex-1">
          <p class="text-sm text-gray-800">
            <span class="font-semibold">{{ update.actor }}</span>
            {{ update.message }}
          </p>
          <p class="mt-0.5 text-xs text-gray-400">{{ update.time }}</p>
        </div>
      </li>

      <li v-if="updates.length === 0" class="px-6 py-10 text-center text-sm text-gray-400">
        No recent updates.
      </li>
    </ul>
  </div>
</template>

<script setup>
import {
  ClockIcon,
  ArrowUpTrayIcon,
  PencilSquareIcon,
  ShareIcon,
  TrashIcon,
  DocumentIcon
} from '@heroicons/vue/24/outline'

// Pass updates fetched from GET /activity/recent (type: 'upload' | 'edit' | 'share' | 'delete')
defineProps({
  updates: {
    type: Array,
    default: () => []
  }
})

const icons = {
  upload: ArrowUpTrayIcon,
  edit: PencilSquareIcon,
  share: ShareIcon,
  delete: TrashIcon
}

const bg = {
  upload: 'bg-blue-50',
  edit: 'bg-amber-50',
  share: 'bg-violet-50',
  delete: 'bg-rose-50'
}

const color = {
  upload: 'text-blue-600',
  edit: 'text-amber-600',
  share: 'text-violet-600',
  delete: 'text-rose-600'
}

function iconFor(type) {
  return icons[type] || DocumentIcon
}
function iconBg(type) {
  return bg[type] || 'bg-gray-100'
}
function iconColor(type) {
  return color[type] || 'text-gray-500'
}
</script>
