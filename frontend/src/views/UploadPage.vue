<template>
  <div class="mx-auto max-w-2xl space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">Create Document</h1>
      <p class="mt-1 text-sm text-gray-500">
        Start a draft. The reference number is assigned automatically.
      </p>
    </div>

    <form class="rounded-xl border border-gray-200 bg-white shadow-sm" @submit.prevent="submit">
      <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
        <DocumentTextIcon class="h-5 w-5 text-blue-600" />
        <h2 class="text-sm font-bold text-gray-800">Document Details</h2>
      </div>

      <div class="space-y-5 px-6 py-5">
        <p v-if="error" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ error }}</p>

        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Subject <span class="text-rose-500">*</span>
          </label>
          <input
            v-model="form.title"
            type="text"
            maxlength="500"
            placeholder="e.g. Q3 Regional Budget Report"
            class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          />
        </div>

        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Document Type <span class="text-rose-500">*</span>
          </label>
          <select
            v-model="form.documentTypeId"
            class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          >
            <option :value="null" disabled>Select a type...</option>
            <option v-for="t in types" :key="t.id" :value="t.id">{{ t.name }}</option>
          </select>
        </div>

        <div>
          <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Remarks</label>
          <textarea
            v-model="form.remarks"
            rows="4"
            maxlength="5000"
            placeholder="Optional context for anyone who opens this document..."
            class="mt-1.5 w-full resize-none rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          ></textarea>
        </div>
      </div>

      <div class="flex items-center justify-end gap-2 border-t border-gray-200 px-6 py-4">
        <router-link
          to="/mydocs"
          class="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
        >
          Cancel
        </router-link>
        <button
          type="submit"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="!form.title.trim() || !form.documentTypeId || saving"
        >
          <CheckIcon class="h-4 w-4" />
          {{ saving ? 'Creating...' : 'Create draft' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DocumentTextIcon, CheckIcon } from '@heroicons/vue/24/outline'
import { getDocumentTypes, createDocument } from '@/api/documents'
import { errorMessage } from '@/api/errors'

const router = useRouter()
const types = ref([])
const saving = ref(false)
const error = ref('')
const form = reactive({ title: '', documentTypeId: null, remarks: '' })

onMounted(async () => {
  try {
    types.value = await getDocumentTypes()
  } catch (err) {
    error.value = errorMessage(err)
  }
})

async function submit() {
  saving.value = true
  error.value = ''
  try {
    const doc = await createDocument({
      title: form.title,
      documentTypeId: form.documentTypeId,
      remarks: form.remarks || null
    })
    router.push(`/document/${doc.id}`)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    saving.value = false
  }
}
</script>