<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
    <div class="max-h-[85vh] w-full max-w-lg overflow-y-auto rounded-xl bg-white shadow-xl">
      <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
        <h2 class="text-sm font-bold text-gray-800">Forward Document</h2>
        <button type="button" class="text-gray-400 hover:text-gray-600" @click="$emit('close')">
          <XMarkIcon class="h-5 w-5" />
        </button>
      </div>

      <div class="space-y-6 px-6 py-5">
        <RecipientPicker v-model="recipients" v-model:mode="mode" />

        <div>
          <h3 class="mb-2 text-xs font-semibold uppercase tracking-wide text-gray-400">Actions Needed (optional)</h3>
          <div v-for="(action, i) in actionsNeeded" :key="action.tempId" class="mb-2 flex flex-wrap items-center gap-2">
            <ActionCodeSelect v-model="action.code" class="w-32 shrink-0" />
            <input
              v-model="action.label"
              type="text"
              placeholder="Describe the action..."
              class="min-w-[10rem] flex-1 rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
            <select
              v-model="action.assignedDepartmentId"
              class="w-40 shrink-0 rounded-lg border border-gray-200 px-2 py-2 text-sm text-gray-600 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
            >
              <option value="" disabled>Assign to...</option>
              <option v-for="dept in departments" :key="dept.id" :value="dept.id">{{ dept.name }}</option>
            </select>
            <button type="button" class="text-gray-400 hover:text-rose-500" @click="removeAction(i)">
              <TrashIcon class="h-4 w-4" />
            </button>
          </div>
          <button
            type="button"
            class="mt-1 inline-flex items-center gap-1.5 text-sm font-semibold text-blue-600 hover:text-blue-500"
            @click="addAction"
          >
            <PlusIcon class="h-4 w-4" />
            Add action
          </button>
        </div>

        <div>
          <h3 class="mb-2 text-xs font-semibold uppercase tracking-wide text-gray-400">Remarks (optional)</h3>
          <textarea
            v-model="remarks"
            rows="3"
            placeholder="Add a note about this forward..."
            class="w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
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
          type="button"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="recipients.length === 0 || submitting"
          @click="submit"
        >
          <PaperAirplaneIcon class="h-4 w-4" />
          {{ submitting ? 'Forwarding...' : 'Forward' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { XMarkIcon, PlusIcon, TrashIcon, PaperAirplaneIcon } from '@heroicons/vue/24/outline'
import RecipientPicker from '../../shared/RecipientPicker.vue'
import ActionCodeSelect from '../../shared/ActionCodeSelect.vue'

const emit = defineEmits(['close', 'submit'])

const mode = ref('single')
const recipients = ref([])
const actionsNeeded = ref([])
const remarks = ref('')
const submitting = ref(false)
const departments = ref([])

onMounted(async () => {
  try {
    departments.value = await getDepartments()
  } catch (err) {
    console.error(err)
  }
})

function addAction() {
  actionsNeeded.value.push({ tempId: Date.now(), label: '', code: '', assignedDepartmentId: '' })
}

function removeAction(i) {
  actionsNeeded.value.splice(i, 1)
}

async function submit() {
  if (recipients.value.length === 0) return
  submitting.value = true
  try {
    // Matches ForwardRequest exactly: { recipients, actionsNeeded, remarks }
    await emit('submit', {
      recipients: recipients.value,
      actionsNeeded: actionsNeeded.value
        .filter((a) => a.label.trim() && a.assignedDepartmentId)
        .map((a) => ({
          label: a.label,
          code: a.code || null,
          assignedDepartmentId: a.assignedDepartmentId,
          dueDate: null
        })),
      remarks: remarks.value || null
    })
  } finally {
    submitting.value = false
  }
}
</script>
