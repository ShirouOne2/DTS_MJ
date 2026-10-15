<template>
  <div class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
    <form class="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl bg-white shadow-xl" @submit.prevent="submit">
      <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
        <h2 class="text-sm font-bold text-gray-800">Submit for routing</h2>
        <button type="button" class="text-gray-400 hover:text-gray-600" @click="$emit('close')">
          <XMarkIcon class="h-5 w-5" />
        </button>
      </div>

      <div class="space-y-5 px-6 py-5">
        <p v-if="error" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ error }}</p>
        <p v-if="loading" class="text-sm text-gray-400">Loading...</p>

        <template v-else>
          <div>
            <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">Routing type</p>
            <div class="mt-1.5 grid grid-cols-1 gap-2 sm:grid-cols-2">
              <button
                v-for="t in TYPES"
                :key="t.value"
                type="button"
                class="rounded-lg border p-3 text-left transition-colors"
                :class="routeType === t.value ? 'border-blue-600 bg-blue-50' : 'border-gray-200 hover:bg-gray-50'"
                @click="routeType = t.value"
              >
                <p class="text-sm font-semibold" :class="routeType === t.value ? 'text-blue-600' : 'text-gray-800'">
                  {{ t.label }}
                </p>
                <p class="mt-0.5 text-xs text-gray-500">{{ t.help }}</p>
              </button>
            </div>
          </div>

          <!-- For Action: ordered steps -->
          <div v-if="routeType === 'ACTION'">
            <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">Steps, in order</p>
            <ol class="mt-1.5 space-y-3">
              <li v-for="(step, i) in steps" :key="step.tempId" class="rounded-lg border border-gray-200 p-3">
                <div class="flex items-center justify-between">
                  <span class="text-xs font-bold text-blue-600">Step {{ i + 1 }}</span>
                  <div class="flex items-center gap-1">
                    <button type="button" class="rounded p-1 text-gray-400 hover:bg-gray-100 disabled:opacity-30" :disabled="i === 0" @click="move(i, -1)">
                      <ChevronUpIcon class="h-4 w-4" />
                    </button>
                    <button type="button" class="rounded p-1 text-gray-400 hover:bg-gray-100 disabled:opacity-30" :disabled="i === steps.length - 1" @click="move(i, 1)">
                      <ChevronDownIcon class="h-4 w-4" />
                    </button>
                    <button type="button" class="rounded p-1 text-gray-400 hover:text-rose-500 disabled:opacity-30" :disabled="steps.length === 1" @click="steps.splice(i, 1)">
                      <TrashIcon class="h-4 w-4" />
                    </button>
                  </div>
                </div>

                <div class="mt-2 grid grid-cols-1 gap-2 sm:grid-cols-2">
                  <select v-model="step.departmentId" :class="inputClass">
                    <option :value="null" disabled>Department...</option>
                    <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
                  </select>
                  <select v-model="step.actionCode" :class="inputClass">
                    <option value="" disabled>Action...</option>
                    <option v-for="c in actionOptions" :key="c.code" :value="c.code">{{ c.code }} - {{ c.label }}</option>
                  </select>
                  <input v-model="step.instruction" type="text" maxlength="500" placeholder="Instruction (optional)" :class="[inputClass, 'sm:col-span-2']" />
                  <div class="sm:col-span-2">
                    <label class="text-xs text-gray-400">Due date (optional)</label>
                    <input v-model="step.dueDate" type="date" :min="today" :class="inputClass" />
                  </div>
                </div>
              </li>
            </ol>
            <button type="button" class="mt-3 inline-flex items-center gap-1.5 text-sm font-semibold text-blue-600 hover:text-blue-500" @click="addStep">
              <PlusIcon class="h-4 w-4" />
              Add step
            </button>
          </div>

          <!-- For Information: pick departments -->
          <div v-else>
            <p class="text-xs font-semibold uppercase tracking-wide text-gray-400">Send to</p>
            <div class="mt-1.5 grid grid-cols-1 gap-2 sm:grid-cols-2">
              <button
                v-for="d in otherDepartments"
                :key="d.id"
                type="button"
                class="flex items-center gap-2 rounded-lg border px-3 py-2.5 text-left text-sm font-medium transition-colors"
                :class="advisoryDepartments.includes(d.id) ? 'border-blue-600 bg-blue-50 text-blue-600' : 'border-gray-200 text-gray-600 hover:bg-gray-50'"
                @click="toggleAdvisory(d.id)"
              >
                <BuildingOffice2Icon class="h-4 w-4 shrink-0" />
                <span class="truncate">{{ d.name }}</span>
                <CheckCircleIcon v-if="advisoryDepartments.includes(d.id)" class="ml-auto h-4 w-4 shrink-0 text-blue-600" />
              </button>
            </div>
            <div class="mt-3">
              <label class="text-xs text-gray-400">Acknowledge by (optional)</label>
              <input v-model="advisoryDue" type="date" :min="today" :class="inputClass" />
            </div>
          </div>

          <p v-if="flowSummary" class="rounded-lg bg-gray-50 px-3 py-2 text-sm text-gray-600">{{ flowSummary }}</p>

          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Remarks (optional)</label>
            <textarea v-model="remarks" rows="3" maxlength="1000" placeholder="A note for the receiving departments..." :class="[inputClass, 'resize-none']"></textarea>
          </div>
        </template>
      </div>

      <div class="flex items-center justify-end gap-2 border-t border-gray-200 px-6 py-4">
        <button type="button" class="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50" @click="$emit('close')">
          Cancel
        </button>
        <button
          type="submit"
          class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="!canSubmit || submitting"
        >
          <PaperAirplaneIcon class="h-4 w-4" />
          {{ submitting ? 'Submitting...' : 'Submit' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  XMarkIcon, PlusIcon, TrashIcon, ChevronUpIcon, ChevronDownIcon,
  PaperAirplaneIcon, BuildingOffice2Icon
} from '@heroicons/vue/24/outline'
import { CheckCircleIcon } from '@heroicons/vue/24/solid'
import { getActiveDepartments } from '@/api/departments'
import { getActionCodes, submitRoute } from '@/api/routing'
import { errorMessage } from '@/api/errors'
import { auth } from '@/stores/auth'

const props = defineProps({
  documentId: { type: Number, required: true }
})
const emit = defineEmits(['close', 'submitted'])

const TYPES = [
  { value: 'ACTION', label: 'For Action', help: 'One department after another: sign, approve, comment... Returns to you when done.' },
  { value: 'ADVISORY', label: 'For Information', help: 'Sent to several departments at once. Each one acknowledges. No approval needed.' }
]

const inputClass =
  'mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100'

const today = new Date().toLocaleDateString('en-CA') // YYYY-MM-DD in local time

const departments = ref([])
const actionCodes = ref([])
const loading = ref(true)
const submitting = ref(false)
const error = ref('')

const routeType = ref('ACTION')
const remarks = ref('')
const advisoryDepartments = ref([])
const advisoryDue = ref('')

let nextId = 1
const newStep = () => ({ tempId: nextId++, departmentId: null, actionCode: '', instruction: '', dueDate: '' })
const steps = ref([newStep()])

const actionOptions = computed(() => actionCodes.value.filter((c) => !c.advisory))
const otherDepartments = computed(() => departments.value.filter((d) => d.id !== auth.user?.departmentId))

const canSubmit = computed(() => {
  if (routeType.value === 'ADVISORY') return advisoryDepartments.value.length > 0
  return steps.value.length > 0 && steps.value.every((s) => s.departmentId && s.actionCode)
})

const deptName = (id) => departments.value.find((d) => d.id === id)?.name

const flowSummary = computed(() => {
  const origin = auth.user?.departmentName
  if (routeType.value === 'ADVISORY') {
    const names = advisoryDepartments.value.map(deptName).filter(Boolean)
    return names.length ? `Sent to ${names.join(', ')} at the same time. Completes when each has acknowledged.` : ''
  }
  const names = steps.value.map((s) => deptName(s.departmentId)).filter(Boolean)
  return names.length ? `${origin} \u2192 ${names.join(' \u2192 ')} \u2192 back to ${origin}` : ''
})

function addStep() {
  steps.value.push(newStep())
}

function move(index, delta) {
  const list = steps.value
  const target = index + delta
  ;[list[index], list[target]] = [list[target], list[index]]
}

function toggleAdvisory(id) {
  const list = advisoryDepartments.value
  advisoryDepartments.value = list.includes(id) ? list.filter((x) => x !== id) : [...list, id]
}

async function submit() {
  submitting.value = true
  error.value = ''
  try {
    const payload =
      routeType.value === 'ADVISORY'
        ? {
            routeType: 'ADVISORY',
            steps: advisoryDepartments.value.map((id) => ({
              departmentId: id, actionCode: null, instruction: null, dueDate: advisoryDue.value || null
            })),
            remarks: remarks.value || null
          }
        : {
            routeType: 'ACTION',
            steps: steps.value.map((s) => ({
              departmentId: s.departmentId,
              actionCode: s.actionCode,
              instruction: s.instruction || null,
              dueDate: s.dueDate || null
            })),
            remarks: remarks.value || null
          }
    const detail = await submitRoute(props.documentId, payload)
    emit('submitted', detail)
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  try {
    const [d, c] = await Promise.all([getActiveDepartments(), getActionCodes()])
    departments.value = d
    actionCodes.value = c
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
})
</script>