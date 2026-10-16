<template>
  <div class="space-y-8">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">Welcome back, {{ firstName }}</h1>
      <p class="mt-1 text-sm text-gray-500">Here's what needs your attention.</p>
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>

    <div class="grid grid-cols-2 gap-4 lg:grid-cols-5">
      <router-link
        v-for="card in cards"
        :key="card.label"
        :to="card.to"
        class="rounded-xl border border-gray-200 bg-white p-4 shadow-sm transition-colors hover:border-blue-300"
      >
        <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">{{ card.label }}</p>
        <p class="mt-2 text-3xl font-bold" :class="card.value > 0 ? card.tone : 'text-gray-300'">
          {{ card.value }}
        </p>
      </router-link>
    </div>

    <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
        <div class="flex items-center gap-2">
          <InboxArrowDownIcon class="h-5 w-5 text-blue-600" />
          <h2 class="text-sm font-bold text-gray-800">Awaiting your action</h2>
        </div>
        <router-link to="/inbox" class="text-xs font-semibold text-blue-600 hover:underline">View all</router-link>
      </div>
      <InboxList :items="inbox.slice(0, 5)" />
      <p v-if="inbox.length === 0" class="px-6 py-8 text-center text-sm text-gray-400">
        Nothing is waiting for your department.
      </p>
    </div>

    <div>
      <div class="mb-3 flex items-center justify-between">
        <h2 class="text-sm font-bold text-gray-800">Recent documents</h2>
        <router-link to="/mydocs" class="text-xs font-semibold text-blue-600 hover:underline">View all</router-link>
      </div>
      <DocumentsTable :documents="recent" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { InboxArrowDownIcon } from '@heroicons/vue/24/outline'
import InboxList from '../components/inbox/InboxList.vue'
import DocumentsTable from '../components/documents/DocumentsTable.vue'
import { getInbox } from '@/api/dashboard'
import { getDocuments } from '@/api/documents'
import { errorMessage } from '@/api/errors'
import { auth } from '@/stores/auth'
import { summary } from '@/stores/summary'

const inbox = ref([])
const recent = ref([])
const error = ref('')

const firstName = computed(() => auth.user?.fullName?.split(' ')[0] || '')

const cards = computed(() => [
  { label: 'Awaiting action', value: summary.awaitingAction, to: '/inbox', tone: 'text-blue-600' },
  { label: 'Overdue', value: summary.overdue, to: '/inbox', tone: 'text-rose-600' },
  { label: 'Drafts', value: summary.drafts, to: '/mydocs?status=DRAFT', tone: 'text-gray-700' },
  { label: 'In routing', value: summary.inRouting, to: '/mydocs?status=IN_ROUTING', tone: 'text-amber-600' },
  { label: 'For revision', value: summary.forRevision, to: '/mydocs?status=FOR_REVISION', tone: 'text-rose-600' }
])

onMounted(async () => {
  try {
    const [items, docs] = await Promise.all([getInbox(), getDocuments({ size: 5 }), summary.refresh()])
    inbox.value = items
    recent.value = docs.items
  } catch (err) {
    error.value = errorMessage(err)
  }
})
</script>