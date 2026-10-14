<template>
  <div class="mx-auto max-w-2xl space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">Track a Document</h1>
      <p class="mt-1 text-sm text-gray-500">Look up a document's current status by its reference number.</p>
    </div>

    <div class="flex gap-2">
      <input
        v-model="referenceNo"
        type="text"
        placeholder="e.g. REF-2026-00123"
        class="flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
        @keyup.enter="search"
      />
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
        :disabled="!referenceNo.trim() || loading"
        @click="search"
      >
        <MagnifyingGlassIcon class="h-4 w-4" />
        {{ loading ? 'Searching...' : 'Track' }}
      </button>
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>

    <div v-if="result" class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
      <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">{{ result.referenceNo }}</p>
      <h2 class="mt-1 text-lg font-bold text-gray-800">{{ result.subject }}</h2>
      <StatusBadge :status="result.status" class="mt-2" />

      <TransmittalTimeline v-if="result.history" :transmittals="result.history" class="mt-6" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { MagnifyingGlassIcon } from '@heroicons/vue/24/outline'
import TransmittalTimeline from '../components/document-detail/TransmittalTimeline.vue'
import StatusBadge from '../shared/StatusBadge.vue'

const referenceNo = ref('')
const result = ref(null)
const loading = ref(false)
const error = ref('')

// TODO: connect to backend — GET /tracking/{referenceNo}
async function search() {
  if (!referenceNo.value.trim()) return
  error.value = ''
  result.value = null
}
</script>
