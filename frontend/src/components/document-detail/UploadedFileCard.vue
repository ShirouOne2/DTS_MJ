<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <PaperClipIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Uploaded File</h2>
    </div>

    <div class="flex flex-col gap-4 px-6 py-5 sm:flex-row sm:items-center sm:justify-between">
      <div class="flex items-center gap-3 min-w-0">
        <div
          class="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-xs font-bold uppercase text-blue-600"
        >
          {{ extension }}
        </div>
        <div class="min-w-0">
          <p class="truncate text-sm font-semibold text-gray-800">{{ file.name }}</p>
          <p class="text-xs text-gray-400">{{ file.size }} &middot; uploaded {{ file.uploadedAt }}</p>
        </div>
      </div>

      <div class="flex shrink-0 items-center gap-2">
        <a
          v-if="file.viewUrl"
          :href="file.viewUrl"
          target="_blank"
          rel="noopener"
          class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50"
        >
          <EyeIcon class="h-4 w-4" />
          View
        </a>
        <a
          v-if="file.downloadUrl"
          :href="file.downloadUrl"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
        >
          <ArrowDownTrayIcon class="h-4 w-4" />
          Download
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { PaperClipIcon, EyeIcon, ArrowDownTrayIcon } from '@heroicons/vue/24/outline'

// Pass a file object from the document record, e.g.
// { name, size, uploadedAt, viewUrl, downloadUrl }
const props = defineProps({
  file: {
    type: Object,
    required: true
  }
})

const extension = computed(() => {
  const parts = props.file.name.split('.')
  return parts.length > 1 ? parts.pop() : 'FILE'
})
</script>
