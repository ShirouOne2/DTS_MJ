<template>
  <div class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
          <tr>
            <th class="px-6 py-3 font-semibold">Reference</th>
            <th class="px-6 py-3 font-semibold">Title</th>
            <th class="px-6 py-3 font-semibold">Owner</th>
            <th class="px-6 py-3 font-semibold">Updated</th>
            <th class="px-6 py-3 font-semibold">Status</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr
            v-for="doc in documents"
            :key="doc.id"
            class="cursor-pointer transition-colors hover:bg-blue-50/50"
            @click="router.push(`/document/${doc.id}`)"
          >
            <td class="whitespace-nowrap px-6 py-3 font-medium text-blue-600">{{ doc.referenceNo }}</td>
            <td class="px-6 py-3">
              <p class="truncate font-medium text-gray-800">{{ doc.title }}</p>
              <p class="text-xs text-gray-400">{{ doc.documentType }}</p>
            </td>
            <td class="px-6 py-3 text-gray-600">
              {{ doc.ownerName }}
              <p class="text-xs text-gray-400">{{ doc.departmentName }}</p>
            </td>
            <td class="whitespace-nowrap px-6 py-3 text-gray-500">{{ formatDate(doc.updatedAt) }}</td>
            <td class="px-6 py-3"><StatusBadge :status="doc.status" /></td>
          </tr>

          <tr v-if="documents.length === 0">
            <td colspan="5" class="px-6 py-12 text-center text-sm text-gray-400">No documents found.</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import StatusBadge from '../../shared/StatusBadge.vue'

defineProps({
  documents: { type: Array, default: () => [] }
})

const router = useRouter()

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>