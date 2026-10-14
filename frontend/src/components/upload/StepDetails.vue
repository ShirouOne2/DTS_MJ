<template>
  <div class="space-y-6">
    <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
        <DocumentTextIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Document Details</h2>
      </div>

      <div class="space-y-5 px-6 py-5">
        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Subject <span class="text-rose-500">*</span>
          </label>
          <input
            v-model="form.subject"
            type="text"
            placeholder="e.g. Q3 Regional Budget Report"
            class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          />
          <p class="mt-1 text-xs text-gray-400">This becomes the document's title across the system.</p>
        </div>

        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Document Type <span class="text-rose-500">*</span>
          </label>
          <select
            v-model="form.documentType"
            class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          >
            <option value="" disabled>Select a type...</option>
            <option v-for="type in documentTypes" :key="type" :value="type">{{ type }}</option>
          </select>
          <p class="mt-1 text-xs text-gray-400">Required — the DOCUMENTS table doesn't allow this to be blank.</p>
        </div>

        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Remarks
          </label>
          <textarea
            v-model="form.remarks"
            rows="4"
            placeholder="Add any remarks about this document..."
            class="mt-1.5 w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          ></textarea>
          <p class="mt-1 text-xs text-gray-400">Optional context anyone viewing this document will see first.</p>
        </div>

        <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
              Date Created
            </label>
            <div class="mt-1.5 flex items-center gap-2 rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-600">
              <CalendarDaysIcon class="h-4 w-4 text-gray-400" />
              {{ form.dateCreated }}
            </div>
            <p class="mt-1 text-xs text-gray-400">Automatically recorded</p>
          </div>
          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
              Time Created
            </label>
            <div class="mt-1.5 flex items-center gap-2 rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-600">
              <ClockIcon class="h-4 w-4 text-gray-400" />
              {{ form.timeCreated }}
            </div>
            <p class="mt-1 text-xs text-gray-400">Automatically recorded</p>
          </div>
        </div>
      </div>
    </div>

    <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
        <ClipboardDocumentCheckIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Actions Needed</h2>
      </div>

      <div class="px-6 pt-4">
        <p class="text-xs text-gray-400">Tell the receiving department what needs to happen and by when.</p>
      </div>

      <div class="px-6 pb-5 pt-3">
        <div class="flex flex-col gap-2 sm:flex-row sm:flex-wrap">
          <input
            v-model="newActionLabel"
            type="text"
            placeholder="e.g. Legal review of allocations"
            class="min-w-[10rem] flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
            @keyup.enter="addAction"
          />
          <ActionCodeSelect v-model="newActionCode" />
          <select
            v-model="newActionDepartmentId"
            class="w-40 shrink-0 rounded-lg border border-gray-200 px-2 py-2 text-sm text-gray-600 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          >
            <option value="" disabled>Assign to...</option>
            <option v-for="dept in departments" :key="dept.id" :value="dept.id">{{ dept.name }}</option>
          </select>
          <input
            v-model="newActionDue"
            type="date"
            title="Deadline for this specific action, not the document itself"
            class="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          />
          <button
            type="button"
            :disabled="!newActionLabel.trim() || !newActionCode || !newActionDepartmentId"
            class="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
            @click="addAction"
          >
            <PlusIcon class="h-4 w-4" />
            Add
          </button>
        </div>
        <p class="mt-1.5 text-xs text-gray-400">
          The date field is this action's deadline — leave it blank if there isn't one.
        </p>

        <ul class="mt-4 divide-y divide-gray-100">
          <li
            v-for="(action, index) in form.actionsNeeded"
            :key="action.id"
            class="flex items-center gap-3 py-3"
          >
            <span class="h-2 w-2 shrink-0 rounded-full bg-blue-600"></span>
            <ActionCodeBadge :code="action.code" />
            <span class="flex-1 text-sm text-gray-800">{{ action.label }}</span>
            <span class="shrink-0 text-xs text-gray-400">{{ departmentName(action.assignedDepartmentId) }}</span>
            <span
              v-if="action.dueDate"
              class="shrink-0 rounded-full bg-amber-50 px-2.5 py-0.5 text-xs font-semibold text-amber-600"
            >
              Due {{ action.dueDate }}
            </span>
            <button
              type="button"
              class="shrink-0 text-gray-400 transition-colors hover:text-rose-500"
              @click="removeAction(index)"
            >
              <XMarkIcon class="h-4 w-4" />
            </button>
          </li>

          <li v-if="form.actionsNeeded.length === 0" class="py-6 text-center text-sm text-gray-400">
            No actions added yet.
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  DocumentTextIcon,
  CalendarDaysIcon,
  ClockIcon,
  ClipboardDocumentCheckIcon,
  PlusIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import ActionCodeSelect from '../../shared/ActionCodeSelect.vue'
import ActionCodeBadge from '../../shared/ActionCodeBadge.vue'

const props = defineProps({
  form: {
    type: Object,
    required: true
  }
})

// Matches the DOCUMENTS.document_type examples from the schema doc.
// Move this to a real lookup endpoint if the list needs to grow beyond a
// fixed set — same reasoning as ACTION_CODES.
const documentTypes = ['Budget Report', 'Memo', 'Policy', 'Legal Filing', 'Correspondence']

// TODO: connect to backend — populate from GET /departments
const departments = ref([])

function departmentName(id) {
  return departments.value.find((d) => d.id === id)?.name || ''
}

const newActionLabel = ref('')
const newActionDue = ref('')
const newActionCode = ref('')
const newActionDepartmentId = ref('')

function addAction() {
  if (!newActionLabel.value.trim() || !newActionCode.value || !newActionDepartmentId.value) return
  props.form.actionsNeeded.push({
    id: Date.now(),
    label: newActionLabel.value.trim(),
    dueDate: newActionDue.value || '',
    code: newActionCode.value,
    assignedDepartmentId: newActionDepartmentId.value
  })
  newActionLabel.value = ''
  newActionDue.value = ''
  newActionCode.value = ''
  newActionDepartmentId.value = ''
}

function removeAction(index) {
  props.form.actionsNeeded.splice(index, 1)
}

onMounted(() => {
  if (!props.form.dateCreated || !props.form.timeCreated) {
    const now = new Date()
    props.form.dateCreated = now.toLocaleDateString('en-US', {
      month: 'long',
      day: '2-digit',
      year: 'numeric'
    })
    props.form.timeCreated = now.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    })
  }
})
</script>
