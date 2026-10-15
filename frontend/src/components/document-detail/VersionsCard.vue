<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
      <div class="flex items-center gap-2">
        <PaperClipIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Files &amp; Versions</h2>
      </div>

      <div v-if="canUpload">
        <input ref="fileInput" type="file" class="hidden" :accept="ACCEPT" @change="onFileChange" />
        <button
          type="button"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="uploading"
          @click="fileInput.click()"
        >
          <ArrowUpTrayIcon class="h-4 w-4" />
          {{ uploading ? `Uploading ${progress}%` : versions.length ? 'Upload new version' : 'Upload file' }}
        </button>
      </div>
    </div>

    <p v-if="error" class="mx-6 mt-4 rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ error }}</p>

    <ul class="divide-y divide-gray-100">
      <li
        v-for="(v, index) in versions"
        :key="v.id"
        class="flex flex-col gap-3 px-6 py-4 sm:flex-row sm:items-center sm:justify-between"
      >
        <div class="flex min-w-0 items-center gap-3">
          <div
            class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-blue-50 text-[0.65rem] font-bold uppercase text-blue-600"
          >
            {{ extensionOf(v.fileName) }}
          </div>
          <div class="min-w-0">
            <p class="flex flex-wrap items-center gap-2 text-sm font-semibold text-gray-800">
              <span class="truncate">{{ v.fileName }}</span>
              <span class="rounded-full bg-gray-100 px-2 py-0.5 text-xs font-semibold text-gray-600">
                v{{ v.versionNo }}
              </span>
              <span
                v-if="index === 0"
                class="rounded-full bg-emerald-50 px-2 py-0.5 text-xs font-semibold text-emerald-600"
              >
                Current
              </span>
            </p>
            <p class="text-xs text-gray-400">
              {{ kindLabel(v.kind) }} &middot; {{ formatBytes(v.sizeBytes) }} &middot; {{ v.uploadedByName }}
              &middot; {{ formatDate(v.uploadedAt) }}
            </p>
            <p class="mt-0.5 font-mono text-xs text-gray-400" :title="v.sha256">
              SHA-256 {{ v.sha256.slice(0, 16) }}&hellip;
            </p>
          </div>
        </div>

        <button
          type="button"
          class="inline-flex shrink-0 items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm font-semibold text-gray-600 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="downloadingId === v.id"
          @click="download(v)"
        >
          <ArrowDownTrayIcon class="h-4 w-4" />
          {{ downloadingId === v.id ? 'Downloading...' : 'Download' }}
        </button>
      </li>

      <li v-if="versions.length === 0" class="px-6 py-10 text-center text-sm text-gray-400">
        No file uploaded yet.
      </li>
    </ul>

    <p v-if="versions.length" class="border-t border-gray-100 px-6 py-3 text-xs text-gray-400">
      The SHA-256 fingerprint is unique to each file's contents. If a file is ever altered, its fingerprint changes.
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { PaperClipIcon, ArrowUpTrayIcon, ArrowDownTrayIcon } from '@heroicons/vue/24/outline'
import { uploadVersion, downloadVersion } from '@/api/documents'
import { errorMessage } from '@/api/errors'

const ACCEPT = '.pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.png,.jpg,.jpeg,.txt,.csv'
const MAX_BYTES = 50 * 1024 * 1024

const props = defineProps({
  documentId: { type: Number, required: true },
  versions: { type: Array, default: () => [] },
  canUpload: { type: Boolean, default: false }
})

const emit = defineEmits(['uploaded'])

const fileInput = ref(null)
const uploading = ref(false)
const progress = ref(0)
const downloadingId = ref(null)
const error = ref('')

async function onFileChange(event) {
  const file = event.target.files[0]
  event.target.value = '' // lets the same file be picked again later
  if (!file) return
  error.value = ''
  if (file.size === 0) return (error.value = 'That file is empty.')
  if (file.size > MAX_BYTES) return (error.value = 'File exceeds the 50 MB limit.')

  uploading.value = true
  progress.value = 0
  try {
    const detail = await uploadVersion(props.documentId, file, (p) => (progress.value = p))
    emit('uploaded', detail)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    uploading.value = false
  }
}

async function download(version) {
  error.value = ''
  downloadingId.value = version.id
  try {
    await downloadVersion(props.documentId, version)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    downloadingId.value = null
  }
}

const KINDS = { ORIGINAL: 'Original', REVISION: 'Revision', SIGNED: 'Signed copy' }
const kindLabel = (kind) => KINDS[kind] || kind

function extensionOf(name) {
  const i = name.lastIndexOf('.')
  return i < 0 ? 'FILE' : name.slice(i + 1)
}

function formatBytes(bytes) {
  if (bytes < 1024) return `${bytes} B`
  const units = ['KB', 'MB', 'GB']
  const i = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)) - 1, units.length - 1)
  return `${(bytes / Math.pow(1024, i + 1)).toFixed(1)} ${units[i]}`
}

function formatDate(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}
</script>