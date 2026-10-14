<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Departments</h1>
        <p class="mt-1 text-sm text-gray-500">Offices that create, receive and sign documents.</p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
        @click="openCreate"
      >
        <PlusIcon class="h-4 w-4" />
        Add department
      </button>
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading" class="text-sm text-gray-400">Loading departments...</p>

    <div v-else class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <table class="w-full text-left text-sm">
        <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
          <tr>
            <th class="px-6 py-3 font-semibold">Name</th>
            <th class="px-6 py-3 font-semibold">Code</th>
            <th class="px-6 py-3 font-semibold">Status</th>
            <th class="px-6 py-3 text-right font-semibold">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="d in departments" :key="d.id" class="hover:bg-blue-50/50">
            <td class="px-6 py-3 font-medium text-gray-800">{{ d.name }}</td>
            <td class="px-6 py-3 text-gray-600">{{ d.code }}</td>
            <td class="px-6 py-3">
              <span
                class="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                :class="d.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'"
              >
                {{ d.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-6 py-3 text-right">
              <button class="text-sm font-semibold text-blue-600 hover:underline" @click="openEdit(d)">Edit</button>
              <button
                class="ml-4 text-sm font-semibold hover:underline"
                :class="d.active ? 'text-rose-600' : 'text-emerald-600'"
                @click="toggleActive(d)"
              >
                {{ d.active ? 'Deactivate' : 'Activate' }}
              </button>
            </td>
          </tr>
          <tr v-if="departments.length === 0">
            <td colspan="4" class="px-6 py-12 text-center text-sm text-gray-400">No departments yet.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
      <form class="w-full max-w-md rounded-xl bg-white shadow-xl" @submit.prevent="save">
        <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 class="text-sm font-bold text-gray-800">{{ editingId ? 'Edit department' : 'Add department' }}</h2>
          <button type="button" class="text-gray-400 hover:text-gray-600" @click="showForm = false">
            <XMarkIcon class="h-5 w-5" />
          </button>
        </div>

        <div class="space-y-4 px-6 py-5">
          <p v-if="formError" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ formError }}</p>

          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Name</label>
            <input
              v-model.trim="form.name"
              type="text"
              maxlength="255"
              class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
          </div>

          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Code</label>
            <input
              v-model.trim="form.code"
              type="text"
              maxlength="20"
              placeholder="e.g. HR"
              class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm uppercase focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
            <p class="mt-1 text-xs text-gray-400">Short and unique. It will appear in reference numbers.</p>
          </div>
        </div>

        <div class="flex items-center justify-end gap-2 border-t border-gray-200 px-6 py-4">
          <button
            type="button"
            class="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
            @click="showForm = false"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
            :disabled="!form.name || !form.code || saving"
          >
            {{ saving ? 'Saving...' : 'Save' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { PlusIcon, XMarkIcon } from '@heroicons/vue/24/outline'
import {
  getAllDepartments,
  createDepartment,
  updateDepartment,
  setDepartmentActive
} from '@/api/departments'

const departments = ref([])
const loading = ref(false)
const error = ref('')

const showForm = ref(false)
const saving = ref(false)
const formError = ref('')
const editingId = ref(null)
const form = reactive({ name: '', code: '' })

function messageFrom(err) {
  const data = err.response?.data
  if (data?.details?.length) return data.details.join(', ')
  return data?.message || 'Something went wrong. Please try again.'
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    departments.value = await getAllDepartments()
  } catch (err) {
    error.value = messageFrom(err)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.name = ''
  form.code = ''
  formError.value = ''
  showForm.value = true
}

function openEdit(d) {
  editingId.value = d.id
  form.name = d.name
  form.code = d.code
  formError.value = ''
  showForm.value = true
}

async function save() {
  saving.value = true
  formError.value = ''
  try {
    const payload = { name: form.name, code: form.code }
    if (editingId.value) await updateDepartment(editingId.value, payload)
    else await createDepartment(payload)
    showForm.value = false
    await load()
  } catch (err) {
    formError.value = messageFrom(err)
  } finally {
    saving.value = false
  }
}

async function toggleActive(d) {
  error.value = ''
  try {
    await setDepartmentActive(d.id, !d.active)
    await load()
  } catch (err) {
    error.value = messageFrom(err)
  }
}

onMounted(load)
</script>