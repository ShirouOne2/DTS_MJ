<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <InformationCircleIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Document Information</h2>
    </div>

    <dl class="grid grid-cols-1 gap-x-6 gap-y-4 px-6 py-5 sm:grid-cols-2">
      <div v-for="field in fields" :key="field.label">
        <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">
          {{ field.label }}
        </dt>
        <dd class="mt-1 flex items-center gap-1.5 text-sm font-medium text-gray-800">
          <component :is="field.icon" class="h-4 w-4 shrink-0 text-gray-400" />
          {{ field.value }}
        </dd>
      </div>
    </dl>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  InformationCircleIcon,
  BuildingOffice2Icon,
  CalendarDaysIcon,
  ClockIcon,
  UserIcon,
  DocumentTextIcon,
  PaperAirplaneIcon
} from '@heroicons/vue/24/outline'

// Pass the document metadata fetched from GET /documents/{id}
const props = defineProps({
  metadata: {
    type: Object,
    required: true
  }
})

const fields = computed(() => {
  const list = [
    { label: 'Origin Department', value: props.metadata.originDepartment, icon: BuildingOffice2Icon },
    { label: 'Date Created', value: props.metadata.dateCreated, icon: CalendarDaysIcon },
    { label: 'Time Created', value: props.metadata.timeCreated, icon: ClockIcon },
    { label: 'Document Type', value: props.metadata.documentType, icon: DocumentTextIcon },
    { label: 'Uploaded By', value: props.metadata.uploadedBy, icon: UserIcon }
  ]

  if (props.metadata.forwardedBy) {
    list.push({ label: 'Forwarded By', value: props.metadata.forwardedBy, icon: PaperAirplaneIcon })
  }

  return list
})
</script>
