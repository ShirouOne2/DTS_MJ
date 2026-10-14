<template>
  <div class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
          <tr>
            <th class="px-6 py-3 font-semibold">Document</th>
            <th class="px-6 py-3 font-semibold">Owner</th>
            <th class="px-6 py-3 font-semibold">Date</th>
            <th class="px-6 py-3 font-semibold">Status</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="doc in documents"
            :key="doc.id"
            class="cursor-pointer transition-colors hover:bg-blue-50/50"
            @click="goToDocument(doc)"
          >
            <td class="flex items-center gap-2 px-6 py-3 font-medium text-gray-800">
              <DocumentIcon class="h-4 w-4 shrink-0 text-gray-400" />
              <span class="truncate">{{ doc.name }}</span>
            </td>
            <td class="px-6 py-3 text-gray-600">{{ doc.owner }}</td>
            <td class="px-6 py-3 text-gray-500">{{ doc.date }}</td>
            <td class="px-6 py-3">
              <StatusBadge :status="doc.status" />
            </td>
          </tr>

          <tr v-if="documents.length === 0">
            <td colspan="4" class="px-6 py-12 text-center text-sm text-gray-400">
              No documents match your search.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { DocumentIcon } from '@heroicons/vue/24/outline'
import StatusBadge from '../../shared/StatusBadge.vue'

defineProps({
  documents: {
    type: Array,
    default: () => []
  }
})

const router = useRouter()

function goToDocument(doc) {
  router.push(`/document/${doc.id}`)
}

</script>
