<template>
  <div class="space-y-6">
    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading" class="text-sm text-gray-400">Loading document...</p>
    <p v-else-if="!document" class="text-sm text-gray-400">No document loaded yet.</p>

    <template v-else>
      <DocumentHeader
        :document="document"
        @archive="handleArchive"
        @receive="handleReceive"
        @forward="showForwardDialog = true"
        @return="handleReturn"
      />

      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div class="space-y-6 lg:col-span-2">
          <DocumentMetadata :metadata="document.metadata" />
          <UploadedFileCard v-if="document.file" :file="document.file" />
          <ActionsNeededCard :actions="document.actionsNeeded" />
          <RemarksSection
            :comments="document.comments"
            :posting="postingRemark"
            @add-comment="handleAddRemark"
          />
        </div>

        <div class="space-y-6 lg:col-span-1">
          <RecipientsCard :recipients="document.currentTransaction?.recipients || []" />
          <TransmittalTimeline :transmittals="document.history" />
        </div>
      </div>

      <ForwardDialog
        v-if="showForwardDialog"
        @close="showForwardDialog = false"
        @submit="handleForwardSubmit"
      />
    </template>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import DocumentHeader from '../components/document-detail/DocumentHeader.vue'
import DocumentMetadata from '../components/document-detail/DocumentMetadata.vue'
import UploadedFileCard from '../components/document-detail/UploadedFileCard.vue'
import ActionsNeededCard from '../components/document-detail/ActionsNeededCard.vue'
import RemarksSection from '../components/document-detail/RemarksSection.vue'
import TransmittalTimeline from '../components/document-detail/TransmittalTimeline.vue'
import RecipientsCard from '../components/document-detail/RecipientsCard.vue'
import ForwardDialog from '../components/document-detail/ForwardDialog.vue'
import { showUndoToast } from '../shared/toast'

const route = useRoute()

// TODO: connect to backend — populate from GET /documents/{id}
const document = ref(null)
const loading = ref(false)
const error = ref('')
const postingRemark = ref(false)
const showForwardDialog = ref(false)

// TODO: connect to backend — PATCH /documents/{id}/archive
async function handleArchive() {
  showUndoToast({
    message: 'Document archived.',
    onUndo: () => undoLastOperation()
  })
}

// TODO: connect to backend — POST /documents/{id}/receive
async function handleReceive() {
  showUndoToast({
    message: 'Document received.',
    onUndo: () => undoLastOperation()
  })
}

// TODO: connect to backend — POST /documents/{id}/forward
async function handleForwardSubmit(payload) {
  showForwardDialog.value = false
  showUndoToast({
    message: 'Document forwarded.',
    onUndo: () => undoLastOperation()
  })
}

// TODO: connect to backend — POST /documents/{id}/return
// (single-recipient "Return" flow the schema's routing rules require)
async function handleReturn() {
  showUndoToast({
    message: 'Document returned for revision.',
    onUndo: () => undoLastOperation()
  })
}

// TODO: connect to backend — POST /documents/{id}/remarks
async function handleAddRemark(message) {
  postingRemark.value = true
  postingRemark.value = false
}

// TODO: connect to backend — POST /undo/{id} per the schema's UNDO_LOG design
async function undoLastOperation() {
  console.log('Would call POST /undo/{id} here once the backend exists.')
}
</script>
