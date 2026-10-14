<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">My Docs</h1>
      <p class="mt-1 text-sm text-gray-500">All documents you've uploaded or currently own.</p>
    </div>

    <DocumentsFilterBar
      :search="search"
      :status="statusFilter"
      @update:search="search = $event"
      @update:status="statusFilter = $event"
    />

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading" class="text-sm text-gray-400">Loading documents...</p>
    <DocumentsTable v-else :documents="filteredDocuments" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import DocumentsFilterBar from '../components/documents/DocumentsFilterBar.vue'
import DocumentsTable from '../components/documents/DocumentsTable.vue'

const search = ref('')
const statusFilter = ref('All')

// TODO: connect to backend — populate from GET /documents
const documents = ref([])
const loading = ref(false)
const error = ref('')

const filteredDocuments = computed(() => {
  return documents.value.filter((doc) => {
    const matchesSearch =
      doc.name.toLowerCase().includes(search.value.toLowerCase()) ||
      doc.owner.toLowerCase().includes(search.value.toLowerCase())
    const matchesStatus = statusFilter.value === 'All' || doc.status === statusFilter.value
    return matchesSearch && matchesStatus
  })
})
</script>
