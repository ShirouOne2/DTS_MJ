<template>
  <div class="space-y-3">
    <button
      type="button"
      class="inline-flex items-center gap-1.5 text-sm font-medium text-gray-500 transition-colors hover:text-blue-600"
      @click="goBack"
    >
      <ArrowLeftIcon class="h-4 w-4" />
      Back
    </button>

    <div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">
          {{ document.referenceNo }}
        </p>
        <h1 class="mt-1 text-2xl font-bold text-gray-800">{{ document.title }}</h1>
        <div class="mt-2 flex flex-wrap items-center gap-2">
          <StatusBadge :status="document.status" />
          <span
            v-if="routingSummary"
            class="inline-flex items-center gap-1 rounded-full bg-gray-50 px-2 py-0.5 text-xs font-medium text-gray-500"
          >
            <PaperAirplaneIcon class="h-3 w-3" />
            {{ routingSummary }}
          </span>
          <span class="inline-flex items-center gap-1 text-xs text-gray-500">
            <BuildingOffice2Icon class="h-3.5 w-3.5" />
            {{ document.department }}
          </span>
        </div>
      </div>

      <div class="flex shrink-0 flex-wrap items-center gap-2">
        <button
          class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50"
          @click="$emit('archive')"
        >
          <ArchiveBoxIcon class="h-4 w-4" />
          Archive
        </button>
        <button
          class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50"
          @click="$emit('receive')"
        >
          <InboxArrowDownIcon class="h-4 w-4" />
          Receive
        </button>
        <button
          v-if="canReturn"
          class="inline-flex items-center gap-2 rounded-lg border border-rose-200 px-3 py-2 text-sm font-semibold text-rose-600 transition-colors hover:bg-rose-50"
          @click="$emit('return')"
        >
          <ArrowUturnLeftIcon class="h-4 w-4" />
          Return
        </button>
        <button
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
          @click="$emit('forward')"
        >
          <PaperAirplaneIcon class="h-4 w-4" />
          Forward
        </button>
        <button
          class="inline-flex items-center gap-2 rounded-lg bg-gradient-to-br from-blue-600 to-blue-700 px-3 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:from-blue-500 hover:to-blue-600"
          @click="$emit('download')"
        >
          <ArrowDownTrayIcon class="h-4 w-4" />
          Download
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowLeftIcon,
  BuildingOffice2Icon,
  ArchiveBoxIcon,
  ArrowDownTrayIcon,
  ArrowUturnLeftIcon,
  InboxArrowDownIcon,
  PaperAirplaneIcon
} from '@heroicons/vue/24/outline'
import StatusBadge from '../../shared/StatusBadge.vue'

const router = useRouter()

function goBack() {
  router.back()
}

// document is the full DocumentDetailDto from GET /documents/{id} — needs
// currentTransaction.recipientCount for the two computeds below.
const props = defineProps({
  document: {
    type: Object,
    required: true
  }
})

defineEmits(['archive', 'receive', 'forward', 'return', 'download'])

// Return is only ever valid on a single-recipient (chain) transaction —
// a multi-recipient broadcast is one-way per the schema's routing rules.
const canReturn = computed(() => props.document.currentTransaction?.recipientCount === 1)

// Secondary indicator next to the status badge: who this is currently
// routed to, and whether that's one department or several.
const routingSummary = computed(() => {
  const recipients = props.document.currentTransaction?.recipients
  if (!recipients || recipients.length === 0) return ''
  if (recipients.length === 1) return `→ ${recipients[0].departmentName}`
  return `→ ${recipients.length} departments`
})
</script>
