<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <PencilSquareIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Continue a Draft</h2>
    </div>

    <ul v-if="drafts.length" class="divide-y divide-gray-100">
      <li
        v-for="draft in drafts"
        :key="draft.id"
        class="flex cursor-pointer items-center justify-between px-6 py-3 transition-colors hover:bg-blue-50/40"
        @click="resume(draft)"
      >
        <div class="min-w-0">
          <p class="truncate text-sm font-medium text-gray-800">
            {{ draft.subjectPreview || 'Untitled document' }}
          </p>
          <p class="text-xs text-gray-400">Last saved {{ formatDate(draft.lastSavedAt) }}</p>
        </div>
        <ArrowRightIcon class="h-4 w-4 shrink-0 text-gray-400" />
      </li>
    </ul>

    <p v-else class="px-6 py-6 text-sm text-gray-400">No drafts in progress.</p>
  </div>
</template>

<script setup>
import { PencilSquareIcon, ArrowRightIcon } from '@heroicons/vue/24/outline'
import { useRouter } from 'vue-router'

// drafts: DraftSummaryDto[] from GET /drafts
defineProps({
  drafts: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()

function resume(draft) {
  router.push({ path: '/create', query: { draftId: draft.id } })
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
