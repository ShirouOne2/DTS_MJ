<template>
  <div class="space-y-6">
    <div class="flex items-center gap-3">
      <ShieldCheckIcon class="h-7 w-7 text-blue-600" />
      <div>
        <h1 class="text-xl font-bold text-gray-800">File Integrity</h1>
        <p class="text-sm text-gray-500">
          Security and access trail — who viewed, downloaded, uploaded, or edited what, and when.
        </p>
      </div>
    </div>

    <div class="flex flex-wrap items-center gap-3 rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
      <select
        v-model="actionFilter"
        class="rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
      >
        <option value="">All actions</option>
        <option v-for="action in ACTIONS" :key="action" :value="action">{{ action }}</option>
      </select>
      <span class="ml-auto text-sm text-gray-500">
        {{ filteredLogs.length }} entr{{ filteredLogs.length === 1 ? 'y' : 'ies' }}
      </span>
    </div>

    <div class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <table class="w-full text-left text-sm">
        <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
          <tr>
            <th class="px-5 py-3 font-semibold">Actor</th>
            <th class="px-5 py-3 font-semibold">Action</th>
            <th class="px-5 py-3 font-semibold">Entity</th>
            <th class="px-5 py-3 font-semibold">IP Address</th>
            <th class="px-5 py-3 font-semibold">When</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="log in filteredLogs" :key="log.id" class="hover:bg-blue-50/40">
            <td class="px-5 py-3 font-medium text-gray-800">{{ log.actorUsername }}</td>
            <td class="px-5 py-3">
              <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="actionStyle(log.action)">
                {{ log.action }}
              </span>
            </td>
            <td class="px-5 py-3 text-gray-600">{{ log.entityType }}</td>
            <td class="px-5 py-3 text-gray-500">{{ log.ipAddress || '—' }}</td>
            <td class="px-5 py-3 text-gray-500">{{ formatDate(log.createdAt) }}</td>
          </tr>

          <tr v-if="!loading && filteredLogs.length === 0">
            <td colspan="5" class="px-5 py-10 text-center text-gray-400">No matching audit entries.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ShieldCheckIcon } from '@heroicons/vue/24/outline'

// Matches AuditLog.Action on the backend exactly.
const ACTIONS = ['login', 'logout', 'viewed', 'downloaded', 'uploaded', 'edited', 'archived']

// TODO: connect to backend — populate from GET /audit-logs (admin-only)
const logs = ref([])
const loading = ref(false)
const actionFilter = ref('')

const filteredLogs = computed(() =>
  actionFilter.value ? logs.value.filter((l) => l.action === actionFilter.value) : logs.value
)

function actionStyle(action) {
  const risky = ['downloaded', 'archived', 'edited']
  return risky.includes(action) ? 'bg-amber-50 text-amber-600' : 'bg-gray-100 text-gray-600'
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
