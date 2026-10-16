<template>
  <div>
    <div class="flex flex-wrap items-center gap-2">
      <span class="text-sm font-bold text-gray-800">Round {{ route.roundNo }}</span>
      <span class="rounded-full bg-blue-50 px-2 py-0.5 text-xs font-semibold text-blue-600">
        {{ route.routeType === 'ADVISORY' ? 'For Information' : 'For Action' }}
      </span>
      <span class="rounded-full px-2 py-0.5 text-xs font-semibold" :class="ROUTE_STATUS[route.status].style">
        {{ ROUTE_STATUS[route.status].label }}
      </span>
    </div>
    <p class="mt-1 text-xs text-gray-400">
      Submitted by {{ route.submittedByName }} &middot; {{ formatDate(route.startedAt) }} &middot; file v{{ route.versionNo }}
    </p>
    <p v-if="route.remarks" class="mt-2 rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-600">{{ route.remarks }}</p>

    <ol class="mt-4 space-y-4">
      <li v-for="step in route.steps" :key="step.id" class="flex gap-3">
        <span
          class="mt-0.5 flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-xs font-bold"
          :class="STEP_STATUS[step.status].dot"
        >
          {{ route.routeType === 'ADVISORY' ? '\u2022' : step.stepOrder }}
        </span>
        <div class="min-w-0 flex-1">
          <div class="flex flex-wrap items-center gap-2">
            <span class="text-sm font-semibold text-gray-800">{{ step.departmentName }}</span>
            <ActionCodeBadge :code="step.actionCode" />
            <span class="rounded-full px-2 py-0.5 text-xs font-semibold" :class="STEP_STATUS[step.status].style">
              {{ stepLabel(step) }}
            </span>
          </div>
          <p v-if="step.instruction" class="mt-0.5 text-sm text-gray-600">{{ step.instruction }}</p>
          <p
            v-if="step.dueDate && (step.status === 'ACTIVE' || step.status === 'WAITING')"
            class="mt-0.5 text-xs"
            :class="step.overdue ? 'font-semibold text-rose-600' : 'text-amber-600'"
            >
            {{ step.overdue ? 'Overdue, was due' : 'Due' }} {{ step.dueDate }}
          </p>
          <p v-if="step.receivedAt" class="mt-0.5 text-xs text-gray-400">
            Received by {{ step.receivedByName }} &middot; {{ formatDate(step.receivedAt) }}
          </p>
          <p v-if="step.decidedAt" class="mt-0.5 text-xs text-gray-400">
            {{ decisionVerb(step) }} by {{ step.decidedByName }} &middot; {{ formatDate(step.decidedAt) }}
          </p>
          <p v-if="step.decisionRemarks" class="mt-1 rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-600">
            {{ step.decisionRemarks }}
          </p>
          <p v-if="step.resultVersionNo" class="mt-1 text-xs font-medium text-emerald-600">
            {{ RESULT_LABELS[step.resultKind] || 'File' }} attached (v{{ step.resultVersionNo }})
            </p>

          <div v-if="step.allowedActions?.length" class="mt-2 flex flex-wrap gap-2">
            <button
                v-for="action in step.allowedActions"
                :key="action"
                type="button"
                class="rounded-lg px-3 py-1.5 text-xs font-semibold transition-colors"
                :class="action === 'REJECT'
                ? 'border border-rose-200 text-rose-600 hover:bg-rose-50'
                : 'bg-blue-600 text-white hover:bg-blue-500'"
                @click="$emit('act', { action, step })"
            >
                {{ actionLabel(action, step) }}
            </button>
          </div>
        </div>
      </li>

      <li v-if="route.routeType === 'ACTION'" class="flex gap-3">
        <span class="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-gray-100 text-gray-400">
          <ArrowUturnLeftIcon class="h-4 w-4" />
        </span>
        <p class="pt-1 text-sm text-gray-500">Returns to {{ originName }}</p>
      </li>
    </ol>
  </div>
</template>

<script setup>
import { ArrowUturnLeftIcon } from '@heroicons/vue/24/outline'
import ActionCodeBadge from '../../shared/ActionCodeBadge.vue'

defineProps({
  route: { type: Object, required: true },
  originName: { type: String, default: '' }
})

defineEmits(['act'])

function actionLabel(action, step) {
  if (action === 'RECEIVE') return 'Receive'
  if (action === 'ACKNOWLEDGE') return 'Acknowledge'
  if (action === 'REJECT') return 'Reject'
  if (step.requiresSignature) return 'Sign / approve'
  if (step.fileKind === 'DRAFT_REPLY') return 'Submit draft reply'
  return 'Mark done'
}

const ROUTE_STATUS = {
  ACTIVE: { label: 'In progress', style: 'bg-amber-50 text-amber-600' },
  COMPLETED: { label: 'Completed', style: 'bg-emerald-50 text-emerald-600' },
  REJECTED: { label: 'Rejected', style: 'bg-rose-50 text-rose-600' },
  RECALLED: { label: 'Recalled', style: 'bg-gray-100 text-gray-500' }
}

const STEP_STATUS = {
  WAITING: { label: 'Waiting', style: 'bg-gray-100 text-gray-500', dot: 'bg-gray-100 text-gray-400' },
  ACTIVE: { label: 'With department', style: 'bg-amber-50 text-amber-600', dot: 'bg-amber-50 text-amber-600' },
  DONE: { label: 'Done', style: 'bg-emerald-50 text-emerald-600', dot: 'bg-emerald-50 text-emerald-600' },
  REJECTED: { label: 'Rejected', style: 'bg-rose-50 text-rose-600', dot: 'bg-rose-50 text-rose-600' },
  SKIPPED: { label: 'Skipped', style: 'bg-gray-100 text-gray-400', dot: 'bg-gray-100 text-gray-400' }
}

const RESULT_LABELS = {
  SIGNED: 'Signed copy', EDITED: 'Edited copy', DRAFT_REPLY: 'Draft reply', ATTACHMENT: 'Attachment'
}

function stepLabel(step) {
  if (step.status === 'DONE' && step.decision === 'ACKNOWLEDGED') return 'Acknowledged'
  if (step.status === 'DONE') return step.requiresSignature ? 'Signed' : 'Done'
  if (step.status === 'ACTIVE' && step.receivedAt) return 'Received'
  return STEP_STATUS[step.status].label
}

function decisionVerb(step) {
  if (step.decision === 'ACKNOWLEDGED') return 'Acknowledged'
  if (step.decision === 'REJECTED') return 'Rejected'
  return step.requiresSignature ? 'Signed' : 'Completed'
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>