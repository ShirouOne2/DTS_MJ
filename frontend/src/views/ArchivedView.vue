<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">Archived</h1>
      <p class="mt-1 text-sm text-gray-500">Documents that have been archived and are no longer active.</p>
    </div>

    <DocumentsFilterBar :search="search" :show-status-filter="false" @update:search="search = $event" />

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

// TODO: connect to backend — populate from GET /documents/archived
const documents = ref([])
const loading = ref(false)
const error = ref('')

const filteredDocuments = computed(() => {
  return documents.value.filter(
    (doc) =>
      doc.name.toLowerCase().includes(search.value.toLowerCase()) ||
      doc.owner.toLowerCase().includes(search.value.toLowerCase())
  )
})
</script>
