<template>
  <div class="space-y-6">
    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading && !doc" class="text-sm text-gray-400">Loading document...</p>

    <template v-else-if="doc">
      <p v-if="actionError" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ actionError }}</p>

      <div
        v-if="rejection"
        class="rounded-xl border border-rose-200 bg-rose-50 px-5 py-4"
      >
        <p class="text-sm font-semibold text-rose-600">Returned for revision by {{ rejection.by }}</p>
        <p class="mt-1 whitespace-pre-line text-sm text-rose-600">{{ rejection.remarks }}</p>
        <p class="mt-2 text-xs text-rose-500">Upload a corrected file, then resubmit.</p>
      </div>

      <div class="space-y-3">
        <button
          type="button"
          class="inline-flex items-center gap-1.5 text-sm font-medium text-gray-500 transition-colors hover:text-blue-600"
          @click="router.back()"
        >
          <ArrowLeftIcon class="h-4 w-4" />
          Back
        </button>

        <div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">{{ doc.referenceNo }}</p>
            <h1 class="mt-1 text-2xl font-bold text-gray-800">{{ doc.title }}</h1>
            <div class="mt-2 flex flex-wrap items-center gap-2">
              <StatusBadge :status="doc.status" />
              <span class="inline-flex items-center gap-1 text-xs text-gray-500">
                <BuildingOffice2Icon class="h-3.5 w-3.5" />
                {{ doc.departmentName }}
              </span>
            </div>
          </div>
          <div class="flex shrink-0 flex-wrap items-center gap-2">
            <button
              v-if="can('SUBMIT')"
              class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
              @click="showSubmit = true"
            >
              <PaperAirplaneIcon class="h-4 w-4" />
              {{ doc.status === 'FOR_REVISION' ? 'Resubmit' : 'Submit for routing' }}
            </button>
            <button
              v-if="can('RECALL')"
              class="inline-flex items-center gap-2 rounded-lg border border-amber-200 px-3 py-2 text-sm font-semibold text-amber-600 transition-colors hover:bg-amber-50"
              @click="recall"
            >
              <ArrowUturnLeftIcon class="h-4 w-4" />
              Recall
            </button>
            <button
              v-if="can('EDIT')"
              class="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50"
              @click="openEdit"
            >
              <PencilSquareIcon class="h-4 w-4" />
              Edit
            </button>
            <button
              v-if="can('CANCEL')"
              class="inline-flex items-center gap-2 rounded-lg border border-rose-200 px-3 py-2 text-sm font-semibold text-rose-600 transition-colors hover:bg-rose-50"
              @click="cancelDraft"
            >
              <XCircleIcon class="h-4 w-4" />
              Cancel draft
            </button>
          </div>
        </div>
      </div>

      <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
          <InformationCircleIcon class="h-5 w-5 text-blue-600" />
          <h2 class="text-sm font-bold text-gray-800">Document Information</h2>
        </div>
        <dl class="grid grid-cols-1 gap-x-6 gap-y-4 px-6 py-5 sm:grid-cols-2">
          <div v-for="field in fields" :key="field.label">
            <dt class="text-xs font-semibold uppercase tracking-wide text-gray-400">{{ field.label }}</dt>
            <dd class="mt-1 text-sm font-medium text-gray-800">{{ field.value }}</dd>
          </div>
        </dl>
      </div>
      
      <VersionsCard
        :document-id="doc.id"
        :versions="doc.versions"
        :can-upload="can('UPLOAD')"
        @uploaded="onUploaded"
      />

      <RouteProgressCard :routes="doc.routes" :origin-name="doc.departmentName" @act="onStepAct" />

      <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
          <ChatBubbleLeftRightIcon class="h-5 w-5 text-blue-600" />
          <h2 class="text-sm font-bold text-gray-800">Remarks</h2>
        </div>
        <p class="whitespace-pre-line px-6 py-5 text-sm text-gray-600">{{ doc.remarks || 'No remarks.' }}</p>
      </div>
    </template>

    <div v-if="showEdit" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
      <form class="w-full max-w-lg rounded-xl bg-white shadow-xl" @submit.prevent="saveEdit">
        <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 class="text-sm font-bold text-gray-800">Edit document</h2>
          <button type="button" class="text-gray-400 hover:text-gray-600" @click="showEdit = false">
            <XMarkIcon class="h-5 w-5" />
          </button>
        </div>

        <div class="space-y-4 px-6 py-5">
          <p v-if="formError" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ formError }}</p>

          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Subject</label>
            <input v-model="form.title" type="text" maxlength="500" :class="inputClass" />
          </div>
          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Document Type</label>
            <select v-model="form.documentTypeId" :class="inputClass">
              <option v-for="t in types" :key="t.id" :value="t.id">{{ t.name }}</option>
            </select>
          </div>
          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Remarks</label>
            <textarea v-model="form.remarks" rows="4" maxlength="5000" :class="[inputClass, 'resize-none']"></textarea>
          </div>
        </div>

        <div class="flex items-center justify-end gap-2 border-t border-gray-200 px-6 py-4">
          <button
            type="button"
            class="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
            @click="showEdit = false"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
            :disabled="!form.title.trim() || saving"
          >
            {{ saving ? 'Saving...' : 'Save' }}
          </button>
        </div>
      </form>
    </div>
    <SubmitRouteDialog
      v-if="showSubmit && doc"
      :document-id="doc.id"
      @close="showSubmit = false"
      @submitted="onSubmitted"
    />
    <StepActionDialog
      v-if="stepDialog && doc"
      :document-id="doc.id"
      :step="stepDialog.step"
      :action="stepDialog.action"
      @close="stepDialog = null"
      @done="onStepDone"
    />
  </div>
</template>


<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeftIcon,
  BuildingOffice2Icon,
  PencilSquareIcon,
  XCircleIcon,
  XMarkIcon,
  InformationCircleIcon,
  ChatBubbleLeftRightIcon,
  PaperAirplaneIcon
} from '@heroicons/vue/24/outline'
import StatusBadge from '../shared/StatusBadge.vue'
import VersionsCard from '../components/document-detail/VersionsCard.vue'
import RouteProgressCard from '../components/document-detail/RouteProgressCard.vue'
import SubmitRouteDialog from '../components/document-detail/SubmitRouteDialog.vue'
import StepActionDialog from '../components/document-detail/StepActionDialog.vue'
import { receiveStep, recallDocument } from '@/api/routing'
import { summary } from '@/stores/summary'
import { getDocument, updateDocument, cancelDocument, getDocumentTypes } from '@/api/documents'
import { errorMessage } from '@/api/errors'

const route = useRoute()
const router = useRouter()
const showSubmit = ref(false)


const inputClass =
  'mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100'

const doc = ref(null)
const loading = ref(false)
const error = ref('')

const showEdit = ref(false)
const saving = ref(false)
const formError = ref('')
const types = ref([])
const form = reactive({ title: '', documentTypeId: null, remarks: '' })

const can = (action) => doc.value?.allowedActions?.includes(action)

const actionError = ref('')
const stepDialog = ref(null)

const rejection = computed(() => {
  if (doc.value?.status !== 'FOR_REVISION') return null

  const step = doc.value.routes?.[0]?.steps?.find(
    (s) => s.status === 'REJECTED'
  )

  return step
    ? {
        by: step.departmentName,
        remarks: step.decisionRemarks
      }
    : null
})

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

function onSubmitted(detail) {
  doc.value = detail
  showSubmit.value = false
}

function onUploaded(detail) {
  doc.value = detail
}

const fields = computed(() => [
  { label: 'Document Type', value: doc.value.documentType },
  { label: 'Origin Department', value: doc.value.departmentName },
  { label: 'Created By', value: doc.value.createdByName },
  { label: 'Date Created', value: formatDate(doc.value.createdAt) },
  { label: 'Last Updated', value: formatDate(doc.value.updatedAt) }
])

async function recall() {
  if (!window.confirm('Recall this document? It goes back to Draft, and you can edit and resubmit it.')) return
  actionError.value = ''
  try {
    doc.value = await recallDocument(doc.value.id)
    summary.refresh()
  } catch (err) {
    actionError.value = errorMessage(err)
  }
}

async function onStepAct({ action, step }) {
  actionError.value = ''
  if (action === 'RECEIVE') {
    try {
      doc.value = await receiveStep(doc.value.id, step.id)
    } catch (err) {
      actionError.value = errorMessage(err)
    }
    return
  }
  stepDialog.value = { action, step }
}

function onStepDone(detail) {
  doc.value = detail
  stepDialog.value = null
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    doc.value = await getDocument(route.params.id)
  } catch (err) {
    doc.value = null
    error.value = err.response?.status === 404 ? 'Document not found.' : errorMessage(err)
  } finally {
    loading.value = false
  }
}

async function openEdit() {
  formError.value = ''
  Object.assign(form, {
    title: doc.value.title,
    documentTypeId: doc.value.documentTypeId,
    remarks: doc.value.remarks || ''
  })
  try {
    if (types.value.length === 0) types.value = await getDocumentTypes()
  } catch (err) {
    formError.value = errorMessage(err)
  }
  showEdit.value = true
}

async function saveEdit() {
  saving.value = true
  formError.value = ''
  try {
    doc.value = await updateDocument(doc.value.id, {
      title: form.title,
      documentTypeId: form.documentTypeId,
      remarks: form.remarks || null
    })
    showEdit.value = false
  } catch (err) {
    formError.value = errorMessage(err)
  } finally {
    saving.value = false
  }
}


async function cancelDraft() {
  actionError.value = ''

  if (!window.confirm('Cancel this draft? It will stay on record as Cancelled.')) {
    return
  }

  try {
    doc.value = await cancelDocument(doc.value.id)
  } catch (err) {
    actionError.value = errorMessage(err)
  }
}


watch(() => route.params.id, load, { immediate: true })
</script>