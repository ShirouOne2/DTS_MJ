<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Awaiting Action</h1>
        <p class="mt-1 text-sm text-gray-500">
          Documents currently with {{ auth.user?.departmentName }}.
        </p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50 disabled:opacity-50"
        :disabled="loading"
        @click="load"
      >
        <ArrowPathIcon class="h-4 w-4" />
        Refresh
      </button>
    </div>

    <div class="flex flex-wrap gap-2">
      <button
        v-for="f in FILTERS"
        :key="f.value"
        type="button"
        class="rounded-full px-3 py-1.5 text-sm font-semibold transition-colors"
        :class="filter === f.value ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 ring-1 ring-gray-200 hover:bg-gray-50'"
        @click="filter = f.value"
      >
        {{ f.label }} ({{ counts[f.value] }})
      </button>
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading && items.length === 0" class="text-sm text-gray-400">Loading...</p>

    <div v-else class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <InboxList :items="filtered" />
      <p v-if="filtered.length === 0" class="px-6 py-12 text-center text-sm text-gray-400">
        Nothing is waiting for your department.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowPathIcon } from '@heroicons/vue/24/outline'
import InboxList from '../components/inbox/InboxList.vue'
import { getInbox } from '@/api/dashboard'
import { errorMessage } from '@/api/errors'
import { auth } from '@/stores/auth'
import { summary } from '@/stores/summary'

const FILTERS = [
  { value: 'all', label: 'All' },
  { value: 'overdue', label: 'Overdue' },
  { value: 'new', label: 'Not received yet' }
]

const items = ref([])
const loading = ref(false)
const error = ref('')
const filter = ref('all')

const counts = computed(() => ({
  all: items.value.length,
  overdue: items.value.filter((i) => i.overdue).length,
  new: items.value.filter((i) => !i.receivedAt).length
}))

const filtered = computed(() => {
  if (filter.value === 'overdue') return items.value.filter((i) => i.overdue)
  if (filter.value === 'new') return items.value.filter((i) => !i.receivedAt)
  return items.value
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    items.value = await getInbox()
    summary.refresh()
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>