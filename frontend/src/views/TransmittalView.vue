<script setup>
import { ref } from 'vue'
import {
  MagnifyingGlassIcon,
  CalendarDaysIcon,
  PaperAirplaneIcon
} from '@heroicons/vue/24/outline'

// TODO: connect to backend — populate from GET /transmittals?fromDate=&toDate=
const transmittals = ref([])
const fromDate = ref('')
const toDate = ref('')
const loading = ref(false)

function clearFilter() {
  fromDate.value = ''
  toDate.value = ''
}

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

<template>
  <div class="min-h-screen bg-gray-50 px-8 py-8">
    <div class="mx-auto max-w-6xl">
      <div class="mb-6 flex items-center gap-3">
        <PaperAirplaneIcon class="h-7 w-7 text-blue-600" />
        <div>
          <h1 class="text-xl font-bold text-gray-800">Transmittal Log</h1>
          <p class="text-sm text-gray-500">
            Every routing event across every document, searchable by date
          </p>
        </div>
      </div>

      <div class="mb-6 rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
        <div class="flex flex-wrap items-center gap-3">
          <div class="flex items-center gap-2">
            <CalendarDaysIcon class="h-5 w-5 text-gray-400" />
            <span class="text-sm font-medium text-gray-600">Date range</span>
          </div>

          <div class="flex items-center gap-2">
            <label for="from-date" class="text-sm text-gray-500">From</label>
            <input
              id="from-date"
              v-model="fromDate"
              type="date"
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>

          <div class="flex items-center gap-2">
            <label for="to-date" class="text-sm text-gray-500">To</label>
            <input
              id="to-date"
              v-model="toDate"
              type="date"
              class="rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
            />
          </div>

          <span class="text-xs text-gray-400">(both optional)</span>

          <button
            v-if="fromDate || toDate"
            @click="clearFilter"
            class="rounded-lg px-3 py-2 text-sm font-medium text-blue-600 hover:bg-blue-50"
          >
            Clear
          </button>

          <div class="ml-auto flex items-center gap-1.5 text-sm text-gray-500">
            <MagnifyingGlassIcon class="h-4 w-4" />
            <span>{{ transmittals.length }} event{{ transmittals.length === 1 ? '' : 's' }}</span>
          </div>
        </div>
      </div>

      <div class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
            <tr>
              <th class="px-5 py-3 font-semibold">Reference No.</th>
              <th class="px-5 py-3 font-semibold">Event</th>
              <th class="px-5 py-3 font-semibold">From</th>
              <th class="px-5 py-3 font-semibold">To</th>
              <th class="px-5 py-3 font-semibold">Date</th>
              <th class="px-5 py-3 font-semibold">Remarks</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="t in transmittals"
              :key="t.id"
              class="cursor-pointer transition-colors hover:bg-blue-50/40"
              @click="$router.push(`/document/${t.documentId}`)"
            >
              <td class="px-5 py-3 font-medium text-blue-600">{{ t.documentReferenceNo }}</td>
              <td class="px-5 py-3">
                <span class="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-medium text-blue-600">
                  {{ t.action }}
                </span>
              </td>
              <td class="px-5 py-3 text-gray-700">{{ t.fromDepartmentName || 'System' }}</td>
              <td class="px-5 py-3 text-gray-700">{{ t.recipientSummary || '—' }}</td>
              <td class="px-5 py-3 text-gray-500">{{ formatDate(t.createdAt) }}</td>
              <td class="px-5 py-3 text-gray-500">{{ t.remarks || '—' }}</td>
            </tr>

            <tr v-if="!loading && transmittals.length === 0">
              <td colspan="6" class="px-5 py-10 text-center text-gray-400">
                No transmittal events found for the selected date range.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
