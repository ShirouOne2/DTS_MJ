<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <ChatBubbleLeftRightIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Comments &amp; Remarks</h2>
    </div>

    <ul class="divide-y divide-gray-100">
      <li
        v-for="comment in comments"
        :key="comment.id"
        class="flex items-start gap-3 px-6 py-4 transition-colors hover:bg-blue-50/50"
      >
        <div
          class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-blue-600 to-blue-700 text-xs font-bold text-white"
        >
          {{ initials(comment.author) }}
        </div>
        <div class="min-w-0 flex-1">
          <p class="text-sm text-gray-800">
            <span class="font-semibold">{{ comment.author }}</span>
            commented on
            <span class="font-medium text-blue-600">{{ comment.document }}</span>
          </p>
          <p class="mt-1 line-clamp-2 text-sm text-gray-600">"{{ comment.message }}"</p>
          <p class="mt-1 text-xs text-gray-400">{{ comment.time }}</p>
        </div>
      </li>

      <li v-if="comments.length === 0" class="px-6 py-10 text-center text-sm text-gray-400">
        No comments yet.
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ChatBubbleLeftRightIcon } from '@heroicons/vue/24/outline'

// Pass comments fetched from GET /remarks/recent
defineProps({
  comments: {
    type: Array,
    default: () => []
  }
})

function initials(name) {
  return name
    .split(' ')
    .map((part) => part.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2)
}
</script>
