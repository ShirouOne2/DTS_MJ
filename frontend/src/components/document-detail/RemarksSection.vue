<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <ChatBubbleLeftRightIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Comments</h2>
    </div>

    <ul class="divide-y divide-gray-100">
      <li
        v-for="comment in comments"
        :key="comment.id"
        class="flex items-start gap-3 px-6 py-4"
      >
        <div
          class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-blue-600 to-blue-700 text-xs font-bold text-white"
        >
          {{ initials(comment.authorUsername) }}
        </div>
        <div class="min-w-0 flex-1">
          <div class="flex items-center gap-2">
            <p class="text-sm font-semibold text-gray-800">{{ comment.authorUsername }}</p>
            <p class="text-xs text-gray-400">{{ formatDate(comment.createdAt) }}</p>
          </div>
          <p class="mt-1 text-sm text-gray-600">{{ comment.message }}</p>
        </div>
      </li>

      <li v-if="comments.length === 0" class="px-6 py-10 text-center text-sm text-gray-400">
        No comments yet. Be the first to leave one.
      </li>
    </ul>

    <div class="border-t border-gray-200 px-6 py-4">
      <textarea
        v-model="draft"
        rows="3"
        placeholder="Add a comment..."
        class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
      ></textarea>
      <div class="mt-2 flex justify-end">
        <button
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="!draft.trim() || posting"
          @click="postComment"
        >
          <PaperAirplaneIcon class="h-4 w-4" />
          {{ posting ? 'Posting...' : 'Post Comment' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ChatBubbleLeftRightIcon, PaperAirplaneIcon } from '@heroicons/vue/24/outline'

// Backs COMMENTS — the ongoing thread, distinct from the single
// DOCUMENTS.remarks field set at upload (see StepDetails.vue for that one).
// comments: CommentDto[] from DocumentDetailDto.comments
defineProps({
  comments: {
    type: Array,
    default: () => []
  },
  posting: {
    type: Boolean,
    default: false
  }
})

// Parent should call addRemark(documentId, message) from src/api/documents.js —
// the endpoint itself is still named /remarks per the original contract,
// this component's job is just to present it as "Comments" to the user.
const emit = defineEmits(['add-comment'])

const draft = ref('')

function postComment() {
  if (!draft.value.trim()) return
  emit('add-comment', draft.value.trim())
  draft.value = ''
}

function initials(name) {
  if (!name) return '?'
  return name
    .split(' ')
    .map((part) => part.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
