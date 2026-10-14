<template>
  <div class="space-y-6">
    <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
        <PaperClipIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Upload File(s)</h2>
      </div>

      <div class="px-6 py-5">
        <div
          class="flex flex-col items-center justify-center gap-2 rounded-xl border-2 border-dashed px-6 py-10 text-center transition-colors"
          :class="isDragging ? 'border-blue-400 bg-blue-50/50' : 'border-gray-300'"
          @dragover.prevent="isDragging = true"
          @dragleave.prevent="isDragging = false"
          @drop.prevent="onDrop"
        >
          <CloudArrowUpIcon class="h-10 w-10 text-blue-600" />
          <p class="text-sm font-medium text-gray-800">
            Drag and drop files here, or
            <button type="button" class="font-semibold text-blue-600 hover:underline" @click="fileInput.click()">
              browse
            </button>
          </p>
          <p class="text-xs text-gray-400">You can select multiple files</p>
          <input
            ref="fileInput"
            type="file"
            multiple
            class="hidden"
            @change="onFileChange"
          />
        </div>

        <ul class="mt-4 divide-y divide-gray-100">
          <li
            v-for="(file, index) in form.files"
            :key="`${file.name}-${index}`"
            class="flex items-center gap-3 py-3"
          >
            <div class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-[0.65rem] font-bold uppercase text-blue-600">
              {{ extensionOf(file.name) }}
            </div>
            <div class="min-w-0 flex-1">
              <p class="truncate text-sm font-medium text-gray-800">{{ file.name }}</p>
              <p class="text-xs text-gray-400">{{ formatBytes(file.size) }}</p>
            </div>
            <button
              type="button"
              class="shrink-0 text-gray-400 transition-colors hover:text-rose-500"
              @click="removeFile(index)"
            >
              <XMarkIcon class="h-4 w-4" />
            </button>
          </li>

          <li v-if="form.files.length === 0" class="py-6 text-center text-sm text-gray-400">
            No files selected yet.
          </li>
        </ul>
      </div>
    </div>

    <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
        <ChatBubbleLeftRightIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Comments for Department</h2>
      </div>
      <div class="px-6 py-5">
        <textarea
          v-model="form.departmentComments"
          rows="3"
          placeholder="Add any notes for the receiving department..."
          class="w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
        ></textarea>
        <p class="mt-1 text-xs text-gray-400">Visible to the department(s) you selected in the previous step.</p>
      </div>
    </div>

    <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
        <InformationCircleIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Review Before Submitting</h2>
      </div>

      <dl class="grid grid-cols-1 gap-x-6 gap-y-4 px-6 py-5 sm:grid-cols-2">
        <div class="sm:col-span-2">
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Subject</dt>
          <dd class="mt-1 text-sm font-medium text-gray-800">{{ form.subject || '—' }}</dd>
        </div>

        <div>
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Document Type</dt>
          <dd class="mt-1 text-sm font-medium text-gray-800">{{ form.documentType || '—' }}</dd>
        </div>

        <div class="sm:col-span-2">
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Remarks</dt>
          <dd class="mt-1 text-sm text-gray-600">{{ form.remarks || '—' }}</dd>
        </div>

        <div>
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Date &amp; Time Created</dt>
          <dd class="mt-1 text-sm font-medium text-gray-800">{{ form.dateCreated }} &middot; {{ form.timeCreated }}</dd>
        </div>

        <div>
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Recipients</dt>
          <dd class="mt-1 text-sm font-medium text-gray-800">
            {{ recipientNames.length ? recipientNames.join(', ') : '—' }}
          </dd>
        </div>

        <div class="sm:col-span-2">
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Actions Needed</dt>
          <dd class="mt-1 space-y-1.5">
            <template v-if="form.actionsNeeded.length">
              <div
                v-for="action in form.actionsNeeded"
                :key="action.id"
                class="flex flex-wrap items-center gap-2 text-sm text-gray-600"
              >
                <ActionCodeBadge :code="action.code" />
                {{ action.label }}
                <span v-if="action.dueDate" class="text-gray-400">(due {{ action.dueDate }})</span>
              </div>
            </template>
            <p v-else class="text-sm text-gray-400">—</p>
          </dd>
        </div>

        <div class="sm:col-span-2">
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Files</dt>
          <dd class="mt-1 text-sm font-medium text-gray-800">
            {{ form.files.length ? form.files.map((f) => f.name).join(', ') : '—' }}
          </dd>
        </div>

        <div class="sm:col-span-2">
          <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">Comments for Department</dt>
          <dd class="mt-1 text-sm text-gray-600">{{ form.departmentComments || '—' }}</dd>
        </div>
      </dl>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  PaperClipIcon,
  CloudArrowUpIcon,
  XMarkIcon,
  ChatBubbleLeftRightIcon,
  InformationCircleIcon
} from '@heroicons/vue/24/outline'
import ActionCodeBadge from '../../shared/ActionCodeBadge.vue'

// TODO: connect to backend — populate from GET /departments
const departments = ref([])

const props = defineProps({
  form: {
    type: Object,
    required: true
  }
})

const recipientNames = computed(() =>
  departments.value.filter((d) => props.form.recipients.includes(d.id)).map((d) => d.name)
)

const fileInput = ref(null)
const isDragging = ref(false)

function addFiles(fileList) {
  Array.from(fileList).forEach((file) => props.form.files.push(file))
}

function onFileChange(event) {
  addFiles(event.target.files)
  event.target.value = ''
}

function onDrop(event) {
  isDragging.value = false
  addFiles(event.dataTransfer.files)
}

function removeFile(index) {
  props.form.files.splice(index, 1)
}

function extensionOf(name) {
  const parts = name.split('.')
  return parts.length > 1 ? parts.pop() : 'FILE'
}

function formatBytes(bytes) {
  if (bytes === 0) return '0 KB'
  const units = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${units[i]}`
}
</script>
