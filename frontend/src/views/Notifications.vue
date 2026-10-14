<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Notifications</h1>
        <p class="mt-1 text-sm text-gray-500">Document activity and comments that involve you.</p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50"
        @click="markAllRead"
      >
        <CheckIcon class="h-4 w-4" />
        Mark all as read
      </button>
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading" class="text-sm text-gray-400">Loading notifications...</p>

    <div v-else class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <ul class="divide-y divide-gray-100">
        <li
          v-for="notif in notifications"
          :key="notif.id"
          class="flex cursor-pointer items-start gap-3 px-6 py-4 transition-colors hover:bg-blue-50/50"
          :class="!notif.read ? 'bg-blue-50/40' : ''"
          @click="handleClick(notif)"
        >
          <span
            class="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full"
            :class="notif.type === 'document' ? 'bg-blue-50 text-blue-600' : 'bg-amber-50 text-amber-600'"
          >
            <DocumentIcon v-if="notif.type === 'document'" class="h-4 w-4" />
            <ChatBubbleLeftIcon v-else class="h-4 w-4" />
          </span>

          <div class="min-w-0 flex-1">
            <div class="flex items-center gap-2">
              <p class="text-sm font-semibold text-gray-800">{{ notif.title }}</p>
              <span v-if="!notif.read" class="h-2 w-2 shrink-0 rounded-full bg-blue-600"></span>
            </div>
            <p class="mt-0.5 text-sm text-gray-600">{{ notif.message }}</p>
            <p class="mt-1 text-xs text-gray-400">{{ notif.time }}</p>
          </div>
        </li>

        <li v-if="notifications.length === 0" class="px-6 py-16 text-center text-sm text-gray-400">
          <BellSlashIcon class="mx-auto mb-3 h-10 w-10 opacity-40" />
          No notifications yet.
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { CheckIcon, DocumentIcon, ChatBubbleLeftIcon, BellSlashIcon } from '@heroicons/vue/24/outline'

const router = useRouter()

// TODO: connect to backend — populate from GET /notifications
const notifications = ref([])
const loading = ref(false)
const error = ref('')

// TODO: connect to backend — PATCH /notifications/read-all
async function markAllRead() {
  notifications.value.forEach((n) => (n.read = true))
}

// TODO: connect to backend — PATCH /notifications/{id}/read
async function handleClick(notif) {
  if (!notif.read) {
    notif.read = true
  }
  if (notif.documentId) {
    router.push(`/document/${notif.documentId}`)
  }
}
</script>
