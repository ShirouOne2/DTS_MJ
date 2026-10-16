<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">My Docs</h1>
        <p class="mt-1 text-sm text-gray-500">Documents you can see, newest activity first.</p>
      </div>
      <router-link
        to="/create"
        class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
      >
        <PlusIcon class="h-4 w-4" />
        New document
      </router-link>
    </div>

    <DocumentsFilterBar
      :search="search"
      :status="statusFilter"
      @update:search="search = $event"
      @update:status="statusFilter = $event"
    />

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading && result.items.length === 0" class="text-sm text-gray-400">Loading documents...</p>

    <template v-else>
      <DocumentsTable :documents="result.items" />

      <div class="flex items-center justify-between text-sm text-gray-500">
        <span>
          {{ result.totalElements }} document{{ result.totalElements === 1 ? '' : 's' }}
          &middot; page {{ result.page + 1 }} of {{ Math.max(result.totalPages, 1) }}
        </span>
        <div class="flex gap-2">
          <button
            class="rounded-lg border border-gray-200 px-3 py-1.5 font-semibold text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40"
            :disabled="result.page === 0"
            @click="load(result.page - 1)"
          >
            Previous
          </button>
          <button
            class="rounded-lg border border-gray-200 px-3 py-1.5 font-semibold text-gray-600 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40"
            :disabled="result.page + 1 >= result.totalPages"
            @click="load(result.page + 1)"
          >
            Next
          </button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { PlusIcon } from '@heroicons/vue/24/outline'
import DocumentsFilterBar from '../components/documents/DocumentsFilterBar.vue'
import DocumentsTable from '../components/documents/DocumentsTable.vue'
import { getDocuments } from '@/api/documents'
import { errorMessage } from '@/api/errors'
import { useRoute } from 'vue-router'

const search = ref('')
const route = useRoute()
const statusFilter = ref(route.query.status || 'All')
const result = ref({ items: [], page: 0, size: 20, totalElements: 0, totalPages: 0 })
const loading = ref(false)
const error = ref('')

let requestId = 0 // ignore answers that arrive after a newer request

async function load(page = 0) {
  const myRequest = ++requestId
  loading.value = true
  error.value = ''
  try {
    const params = { page, size: 20 }
    if (search.value.trim()) params.search = search.value.trim()
    if (statusFilter.value !== 'All') params.status = statusFilter.value
    const data = await getDocuments(params)
    if (myRequest === requestId) result.value = data
  } catch (err) {
    if (myRequest === requestId) error.value = errorMessage(err)
  } finally {
    if (myRequest === requestId) loading.value = false
  }
}

let timer = null
watch([search, statusFilter], () => {
  clearTimeout(timer)
  timer = setTimeout(() => load(0), 300)
})

onMounted(() => load(0))
</script>