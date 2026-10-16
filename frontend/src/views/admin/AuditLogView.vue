<template>
  <div class="space-y-6">
    <div class="flex items-center gap-3">
      <ClipboardDocumentListIcon class="h-7 w-7 text-blue-600" />
      <div>
        <h1 class="text-xl font-bold text-gray-800">Audit Log</h1>
        <p class="text-sm text-gray-500">
          Security trail: sign-ins and administrative changes. Entries cannot be edited or deleted.
        </p>
      </div>
    </div>

    <form
      class="flex flex-wrap items-end gap-3 rounded-xl border border-gray-200 bg-white p-4 shadow-sm"
      @submit.prevent="applyFilters"
    >
      <div>
        <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Action</label>
        <select v-model="filters.action" :class="inputClass">
          <option value="">All actions</option>
          <option v-for="a in ACTIONS" :key="a" :value="a">{{ label(a) }}</option>
        </select>
      </div>
      <div>
        <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Username</label>
        <input v-model.trim="filters.username" type="text" placeholder="Contains..." :class="inputClass" />
      </div>
      <div>
        <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">From</label>
        <input v-model="filters.from" type="date" :class="inputClass" />
      </div>
      <div>
        <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">To</label>
        <input v-model="filters.to" type="date" :class="inputClass" />
      </div>
      <button
        type="submit"
        class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500"
      >
        Apply
      </button>
      <button
        type="button"
        class="rounded-lg px-3 py-2 text-sm font-medium text-blue-600 hover:bg-blue-50"
        @click="clearFilters"
      >
        Clear
      </button>
      <span class="ml-auto text-sm text-gray-500">
        {{ result.totalElements }} entr{{ result.totalElements === 1 ? 'y' : 'ies' }}
      </span>
    </form>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading" class="text-sm text-gray-400">Loading audit log...</p>

    <div v-else class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
            <tr>
              <th class="px-5 py-3 font-semibold">When</th>
              <th class="px-5 py-3 font-semibold">Actor</th>
              <th class="px-5 py-3 font-semibold">Action</th>
              <th class="px-5 py-3 font-semibold">Details</th>
              <th class="px-5 py-3 font-semibold">IP address</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="log in result.items" :key="log.id" class="hover:bg-blue-50/40">
              <td class="whitespace-nowrap px-5 py-3 text-gray-500">{{ formatDate(log.createdAt) }}</td>
              <td class="px-5 py-3 font-medium text-gray-800">{{ log.actorUsername || 'system' }}</td>
              <td class="px-5 py-3">
                <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="actionStyle(log.action)">
                  {{ label(log.action) }}
                </span>
              </td>
              <td class="px-5 py-3 text-gray-600">{{ log.details || '—' }}</td>
              <td class="px-5 py-3 text-gray-500">{{ log.ipAddress || '—' }}</td>
            </tr>
            <tr v-if="result.items.length === 0">
              <td colspan="5" class="px-5 py-10 text-center text-gray-400">No matching audit entries.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between border-t border-gray-200 px-5 py-3 text-sm text-gray-500">
        <span>Page {{ result.page + 1 }} of {{ Math.max(result.totalPages, 1) }}</span>
        <div class="flex gap-2">
          <button
            class="rounded-lg border border-gray-200 px-3 py-1.5 font-semibold text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40"
            :disabled="result.page === 0"
            @click="goTo(result.page - 1)"
          >
            Previous
          </button>
          <button
            class="rounded-lg border border-gray-200 px-3 py-1.5 font-semibold text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40"
            :disabled="result.page + 1 >= result.totalPages"
            @click="goTo(result.page + 1)"
          >
            Next
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ClipboardDocumentListIcon } from '@heroicons/vue/24/outline'
import { getAuditLogs } from '@/api/audit'
import { errorMessage } from '@/api/errors'

// Matches AuditAction on the backend.
const ACTIONS = [
  'LOGIN', 'LOGOUT', 'LOGIN_FAILED',
  'USER_CREATED', 'USER_UPDATED', 'ROLE_CHANGED', 'USER_ACTIVATED', 'USER_DEACTIVATED', 'PASSWORD_RESET',
  'DEPARTMENT_CREATED', 'DEPARTMENT_UPDATED', 'DEPARTMENT_ACTIVATED', 'DEPARTMENT_DEACTIVATED',
  'DOCUMENT_CREATED', 'DOCUMENT_UPDATED', 'DOCUMENT_CANCELLED', 'DOCUMENT_VIEWED',
  'VERSION_UPLOADED', 'DOCUMENT_DOWNLOADED',
  'ROUTE_SUBMITTED',
  'STEP_RECEIVED', 'STEP_COMPLETED', 'STEP_REJECTED', 'STEP_ACKNOWLEDGED', 'ROUTE_COMPLETED',
  'ROUTE_RECALLED'
]

const inputClass =
  'mt-1.5 block rounded-lg border border-gray-300 px-3 py-2 text-sm text-gray-700 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'

const filters = reactive({ action: '', username: '', from: '', to: '' })
const result = ref({ items: [], page: 0, size: 25, totalElements: 0, totalPages: 0 })
const loading = ref(false)
const error = ref('')

function label(action) {
  const text = action.toLowerCase().replace(/_/g, ' ')
  return text.charAt(0).toUpperCase() + text.slice(1)
}

function actionStyle(action) {
  if (action === 'LOGIN_FAILED') return 'bg-rose-50 text-rose-600'
  if (['ROLE_CHANGED', 'PASSWORD_RESET', 'USER_DEACTIVATED', 'DEPARTMENT_DEACTIVATED'].includes(action)) {
    return 'bg-amber-50 text-amber-600'
  }
  return 'bg-gray-100 text-gray-600'
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  })
}

async function load(page = 0) {
  loading.value = true
  error.value = ''
  try {
    const params = { page, size: 25 }
    if (filters.action) params.action = filters.action
    if (filters.username) params.username = filters.username
    if (filters.from) params.from = filters.from
    if (filters.to) params.to = filters.to
    result.value = await getAuditLogs(params)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

const applyFilters = () => load(0)
const goTo = (page) => load(page)

function clearFilters() {
  Object.assign(filters, { action: '', username: '', from: '', to: '' })
  load(0)
}

onMounted(() => load(0))
</script>