<template>
  <div class="mx-auto max-w-3xl space-y-6">
    <div v-if="!submitted">
      <div class="flex items-start justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Create Document</h1>
          <p class="mt-1 text-sm text-gray-500">
            Step {{ currentStep }} of 3 &middot; {{ stepLabels[currentStep - 1] }}
          </p>
        </div>
      </div>

      <div class="mt-6">
        <UploadStepper :steps="stepLabels" :current-step="currentStep" />
      </div>

      <div class="mt-6">
        <StepDetails v-if="currentStep === 1" :form="form" />
        <StepRecipients v-else-if="currentStep === 2" :form="form" />
        <StepFilesReview v-else :form="form" />
      </div>

      <p v-if="submitError" class="mt-4 text-sm text-rose-600">{{ submitError }}</p>

      <div class="mt-6 flex items-center justify-between">
        <button
          type="button"
          class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40"
          :disabled="currentStep === 1"
          @click="goBack"
        >
          <ArrowLeftIcon class="h-4 w-4" />
          Back
        </button>

        <button
          v-if="currentStep < 3"
          type="button"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="!canProceed"
          @click="goNext"
        >
          Next
          <ArrowRightIcon class="h-4 w-4" />
        </button>

        <button
          v-else
          type="button"
          class="inline-flex items-center gap-2 rounded-lg bg-gradient-to-br from-blue-600 to-blue-700 px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:from-blue-500 hover:to-blue-600 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="!canSubmit || submitting"
          @click="submit"
        >
          <CheckIcon class="h-4 w-4" />
          {{ submitting ? 'Submitting...' : 'Submit Document' }}
        </button>
      </div>
    </div>

    <div v-else class="flex flex-col items-center rounded-xl border border-gray-200 bg-white px-6 py-16 text-center shadow-sm">
      <CheckCircleIconSolid class="h-14 w-14 text-emerald-500" />
      <h2 class="mt-4 text-xl font-bold text-gray-800">Document Submitted</h2>
      <p class="mt-1 max-w-sm text-sm text-gray-500">
        "{{ form.subject }}" has been sent to {{ form.recipients.length }} department{{ form.recipients.length === 1 ? '' : 's' }}.
      </p>
      <button
        type="button"
        class="mt-6 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
        @click="resetForm"
      >
        Upload Another Document
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ArrowLeftIcon, ArrowRightIcon, CheckIcon } from '@heroicons/vue/24/outline'
import { CheckCircleIcon as CheckCircleIconSolid } from '@heroicons/vue/24/solid'
import UploadStepper from '../components/upload/UploadStepper.vue'
import StepDetails from '../components/upload/StepDetails.vue'
import StepRecipients from '../components/upload/StepRecipients.vue'
import StepFilesReview from '../components/upload/StepFilesReview.vue'

const stepLabels = ['Document Details', 'Recipients', 'Files & Review']
const currentStep = ref(1)
const submitted = ref(false)
const submitting = ref(false)
const submitError = ref('')

function emptyForm() {
  return {
    subject: '',
    documentType: '',
    remarks: '',
    dateCreated: '',
    timeCreated: '',
    actionsNeeded: [],
    recipientMode: 'single',
    recipients: [],
    files: [],
    departmentComments: ''
  }
}

const form = reactive(emptyForm())

const canProceed = computed(() => {
  if (currentStep.value === 1) return form.subject.trim().length > 0 && form.documentType.length > 0
  if (currentStep.value === 2) return form.recipients.length > 0
  return true
})

const canSubmit = computed(() => form.files.length > 0)

function goNext() {
  if (canProceed.value && currentStep.value < 3) currentStep.value += 1
}

function goBack() {
  if (currentStep.value > 1) currentStep.value -= 1
}

// TODO: connect to backend
//   1. POST /documents            -> create the document record
//   2. POST /documents/{id}/files -> upload attached files (multipart/form-data)
async function submit() {
  if (!canSubmit.value) return
  submitting.value = true
  submitError.value = ''
  try {
    submitted.value = true
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  Object.assign(form, emptyForm())
  currentStep.value = 1
  submitted.value = false
}
</script>
