<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
    <form class="w-full max-w-md rounded-xl bg-white shadow-xl" @submit.prevent="submit">
      <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
        <h2 class="text-sm font-bold text-gray-800">{{ title }}</h2>
        <button type="button" class="text-gray-400 hover:text-gray-600" @click="$emit('close')">
          <XMarkIcon class="h-5 w-5" />
        </button>
      </div>

      <div class="space-y-4 px-6 py-5">
        <p class="text-sm text-gray-600">{{ intro }}</p>
        <p v-if="error" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ error }}</p>

        <div v-if="showFile">
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Signed copy (optional)</label>
          <input
            type="file"
            :accept="ACCEPT"
            class="mt-1.5 block w-full text-sm text-gray-600 file:mr-3 file:rounded-lg file:border-0 file:bg-blue-50 file:px-3 file:py-2 file:text-sm file:font-semibold file:text-blue-600 hover:file:bg-blue-100"
            @change="file = $event.target.files[0] || null"
          />
          <p class="mt-1 text-xs text-gray-400">If attached, it becomes the current version for the next office.</p>
        </div>

        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            {{ isReject ? 'Reason' : 'Remarks (optional)' }}
            <span v-if="isReject" class="text-rose-500">*</span>
          </label>
          <textarea
            v-model="remarks"
            rows="4"
            maxlength="1000"
            :placeholder="isReject ? 'What needs to be corrected?' : 'Add a note...'"
            class="mt-1.5 w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          ></textarea>
        </div>
      </div>

      <div class="flex items-center justify-end gap-2 border-t border-gray-200 px-6 py-4">
        <button
          type="button"
          class="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
          @click="$emit('close')"
        >
          Cancel
        </button>
        <button
          type="submit"
          class="rounded-lg px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-50"
          :class="isReject ? 'bg-rose-600 hover:bg-rose-500' : 'bg-blue-600 hover:bg-blue-500'"
          :disabled="busy || (isReject && !remarks.trim())"
        >
          {{ busy ? 'Saving...' : confirmLabel }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'
import { completeStep, rejectStep, acknowledgeStep } from '@/api/routing'
import { errorMessage } from '@/api/errors'

const ACCEPT = '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.png,.jpg,.jpeg,.txt,.csv'
const MAX_BYTES = 50 * 1024 * 1024

const props = defineProps({
  documentId: { type: Number, required: true },
  step: { type: Object, required: true },
  action: { type: String, required: true } // COMPLETE | REJECT | ACKNOWLEDGE
})
const emit = defineEmits(['close', 'done'])

const remarks = ref('')
const file = ref(null)
const busy = ref(false)
const error = ref('')

const isReject = computed(() => props.action === 'REJECT')
const showFile = computed(() => props.action === 'COMPLETE' && props.step.requiresSignature)

const title = computed(() => {
  if (props.action === 'REJECT') return 'Reject and return for revision'
  if (props.action === 'ACKNOWLEDGE') return 'Acknowledge'
  return props.step.requiresSignature ? 'Sign / approve' : 'Mark as done'
})

const confirmLabel = computed(() => {
  if (props.action === 'REJECT') return 'Reject'
  if (props.action === 'ACKNOWLEDGE') return 'Acknowledge'
  return props.step.requiresSignature ? 'Sign / approve' : 'Mark done'
})

const intro = computed(() => {
  const what = `${props.step.departmentName}, ${props.step.actionLabel}.`
  if (props.action === 'REJECT') {
    return `${what} The document goes back to the origin department as For Revision, and the remaining steps are cancelled.`
  }
  return what
})

async function submit() {
  error.value = ''
  if (file.value && file.value.size > MAX_BYTES) return (error.value = 'File exceeds the 50 MB limit.')
  busy.value = true
  try {
    const id = props.documentId
    const sid = props.step.id
    let detail
    if (props.action === 'REJECT') detail = await rejectStep(id, sid, remarks.value.trim())
    else if (props.action === 'ACKNOWLEDGE') detail = await acknowledgeStep(id, sid, remarks.value.trim() || null)
    else detail = await completeStep(id, sid, remarks.value.trim(), file.value)
    emit('done', detail)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    busy.value = false
  }
}
</script>