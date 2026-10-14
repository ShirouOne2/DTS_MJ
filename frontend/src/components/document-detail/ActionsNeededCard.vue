<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <ClipboardDocumentCheckIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Actions Needed</h2>
    </div>

    <ul class="divide-y divide-gray-100">
      <li
        v-for="action in actions"
        :key="action.id"
        class="flex items-center gap-3 px-6 py-4"
      >
        <CheckCircleIconSolid
          v-if="action.status === 'done'"
          class="h-5 w-5 shrink-0 text-emerald-500"
        />
        <span
          v-else
          class="h-5 w-5 shrink-0 rounded-full border-2 border-blue-300"
        ></span>

        <div class="min-w-0 flex-1">
          <div class="flex flex-wrap items-center gap-2">
            <ActionCodeBadge :code="action.code" />
            <p
              class="text-sm font-medium"
              :class="action.status === 'done' ? 'text-gray-400 line-through' : 'text-gray-800'"
            >
              {{ action.label }}
            </p>
          </div>
          <p class="mt-0.5 text-xs text-gray-400">Assigned to {{ action.assignee }}</p>
        </div>

        <span
          v-if="action.status !== 'done'"
          class="shrink-0 rounded-full px-2.5 py-0.5 text-xs font-semibold"
          :class="dueStyles(action.dueDate)"
        >
          Due {{ action.dueDate }}
        </span>
      </li>

      <li v-if="actions.length === 0" class="px-6 py-10 text-center text-sm text-gray-400">
        No actions needed at this time.
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ClipboardDocumentCheckIcon } from '@heroicons/vue/24/outline'
import { CheckCircleIcon as CheckCircleIconSolid } from '@heroicons/vue/24/solid'
import ActionCodeBadge from '../../shared/ActionCodeBadge.vue'

// actionsNeeded comes from the document record fetched from GET /documents/{id}
defineProps({
  actions: {
    type: Array,
    default: () => []
  }
})

function dueStyles(dueDate) {
  // TODO: swap for a real date comparison (e.g. overdue = rose, due soon = amber)
  // once dueDate is a real ISO date coming from the backend.
  return 'bg-amber-50 text-amber-600'
}
</script>
