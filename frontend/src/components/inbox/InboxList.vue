<template>
  <ul class="divide-y divide-gray-100">
    <li v-for="item in items" :key="item.stepId">
      <router-link
        :to="`/document/${item.documentId}`"
        class="flex flex-col gap-2 px-6 py-4 transition-colors hover:bg-blue-50/50 sm:flex-row sm:items-center sm:justify-between"
      >
        <div class="min-w-0">
          <p class="flex flex-wrap items-center gap-2">
            <span class="text-xs font-semibold uppercase tracking-wide text-blue-600">{{ item.referenceNo }}</span>
            <ActionCodeBadge :code="item.actionCode" />
            <span v-if="item.roundNo > 1" class="text-xs text-gray-400">Round {{ item.roundNo }}</span>
          </p>
          <p class="mt-0.5 truncate text-sm font-semibold text-gray-800">{{ item.title }}</p>
          <p class="text-xs text-gray-400">From {{ item.originDepartment }} &middot; {{ item.documentType }}</p>
          <p v-if="item.instruction" class="mt-1 text-sm text-gray-600">{{ item.instruction }}</p>
        </div>

        <div class="flex shrink-0 flex-wrap items-center gap-2">
          <span
            v-if="item.overdue"
            class="rounded-full bg-rose-50 px-2.5 py-0.5 text-xs font-semibold text-rose-600"
          >
            Overdue &middot; {{ item.dueDate }}
          </span>
          <span
            v-else-if="item.dueDate"
            class="rounded-full bg-amber-50 px-2.5 py-0.5 text-xs font-semibold text-amber-600"
          >
            Due {{ item.dueDate }}
          </span>
          <span class="rounded-full px-2.5 py-0.5 text-xs font-semibold" :class="nextStep(item).style">
            {{ nextStep(item).label }}
          </span>
        </div>
      </router-link>
    </li>
  </ul>
</template>

<script setup>
import ActionCodeBadge from '../../shared/ActionCodeBadge.vue'

defineProps({
  items: { type: Array, default: () => [] }
})

function nextStep(item) {
  if (item.actions.includes('RECEIVE')) return { label: 'Receive', style: 'bg-blue-600 text-white' }
  if (item.actions.includes('ACKNOWLEDGE')) return { label: 'Acknowledge', style: 'bg-blue-600 text-white' }
  if (item.actions.includes('COMPLETE')) {
    const label = item.requiresSignature ? 'Sign / approve'
        : item.fileKind === 'DRAFT_REPLY' ? 'Submit draft reply' : 'Mark done'
    return { label, style: 'bg-blue-600 text-white' }
    }
  return { label: 'Waiting for an Approver', style: 'bg-gray-100 text-gray-500' }
}
</script>