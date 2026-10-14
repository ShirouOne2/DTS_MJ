<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Users</h1>
        <p class="mt-1 text-sm text-gray-500">Accounts that can sign in and act on documents.</p>
      </div>
      <button
        type="button"
        class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-blue-500"
        @click="openCreate"
      >
        <PlusIcon class="h-4 w-4" />
        Add user
      </button>
    </div>

    <div class="relative sm:max-w-sm">
      <MagnifyingGlassIcon class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
      <input
        v-model="search"
        type="text"
        placeholder="Search by name, username or department..."
        class="w-full rounded-lg border border-gray-200 py-2 pl-9 pr-3 text-sm text-gray-800 placeholder:text-gray-400 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
      />
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>
    <p v-else-if="loading" class="text-sm text-gray-400">Loading users...</p>

    <div v-else class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
            <tr>
              <th class="px-6 py-3 font-semibold">Name</th>
              <th class="px-6 py-3 font-semibold">Department</th>
              <th class="px-6 py-3 font-semibold">Role</th>
              <th class="px-6 py-3 font-semibold">Status</th>
              <th class="px-6 py-3 font-semibold">Last login</th>
              <th class="px-6 py-3 text-right font-semibold">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="u in filteredUsers" :key="u.id" class="hover:bg-blue-50/50">
              <td class="px-6 py-3">
                <p class="font-medium text-gray-800">{{ u.firstName }} {{ u.lastName }}</p>
                <p class="text-xs text-gray-400">{{ u.username }}</p>
              </td>
              <td class="px-6 py-3 text-gray-600">{{ u.departmentName }}</td>
              <td class="px-6 py-3">
                <span class="rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-semibold text-blue-600">
                  {{ u.role }}
                </span>
              </td>
              <td class="px-6 py-3">
                <span
                  class="rounded-full px-2.5 py-0.5 text-xs font-semibold"
                  :class="u.active ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-500'"
                >
                  {{ u.active ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td class="px-6 py-3 text-gray-500">{{ formatDate(u.lastLoginAt) }}</td>
              <td class="whitespace-nowrap px-6 py-3 text-right">
                <button class="text-sm font-semibold text-blue-600 hover:underline" @click="openEdit(u)">Edit</button>
                <button class="ml-4 text-sm font-semibold text-blue-600 hover:underline" @click="openReset(u)">
                  Reset password
                </button>
                <button
                  v-if="!isSelf(u)"
                  class="ml-4 text-sm font-semibold hover:underline"
                  :class="u.active ? 'text-rose-600' : 'text-emerald-600'"
                  @click="toggleActive(u)"
                >
                  {{ u.active ? 'Deactivate' : 'Activate' }}
                </button>
              </td>
            </tr>
            <tr v-if="filteredUsers.length === 0">
              <td colspan="6" class="px-6 py-12 text-center text-sm text-gray-400">No users found.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create / edit -->
    <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
      <form class="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-xl bg-white shadow-xl" @submit.prevent="save">
        <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 class="text-sm font-bold text-gray-800">{{ editingId ? 'Edit user' : 'Add user' }}</h2>
          <button type="button" class="text-gray-400 hover:text-gray-600" @click="showForm = false">
            <XMarkIcon class="h-5 w-5" />
          </button>
        </div>

        <div class="space-y-4 px-6 py-5">
          <p v-if="formError" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ formError }}</p>

          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">First name</label>
              <input v-model.trim="form.firstName" type="text" maxlength="100" :class="inputClass" />
            </div>
            <div>
              <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Last name</label>
              <input v-model.trim="form.lastName" type="text" maxlength="100" :class="inputClass" />
            </div>
          </div>

          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Username</label>
            <input
              v-model.trim="form.username"
              type="text"
              maxlength="100"
              :disabled="!!editingId"
              :class="[inputClass, editingId ? 'bg-gray-50 text-gray-500' : '']"
            />
            <p class="mt-1 text-xs text-gray-400">
              {{ editingId ? 'Usernames cannot be changed.' : 'Letters, numbers, dot, dash and underscore. Stored in lowercase.' }}
            </p>
          </div>

          <div>
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Email (optional)</label>
            <input v-model.trim="form.email" type="email" maxlength="255" :class="inputClass" />
          </div>

          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Department</label>
              <select v-model="form.departmentId" :class="inputClass">
                <option :value="null" disabled>Select...</option>
                <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.name }}</option>
              </select>
            </div>
            <div>
              <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Role</label>
              <select v-model="form.role" :disabled="editingSelf" :class="inputClass">
                <option v-for="r in ROLES" :key="r.value" :value="r.value">{{ r.value }}</option>
              </select>
            </div>
          </div>
          <p class="-mt-2 text-xs text-gray-400">
            {{ roleHelp }}
            <span v-if="editingSelf"> You can't change your own role.</span>
          </p>

          <div v-if="!editingId">
            <label class="text-xs font-semibold uppercase tracking-wide text-gray-400">Starting password</label>
            <input v-model="form.password" type="text" maxlength="72" autocomplete="off" :class="inputClass" />
            <p class="mt-1 text-xs text-gray-400">At least 8 characters. Give it to the user in person or by phone.</p>
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
            :disabled="!canSave || saving"
          >
            {{ saving ? 'Saving...' : 'Save' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Reset password -->
    <div v-if="resetTarget" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
      <form class="w-full max-w-md rounded-xl bg-white shadow-xl" @submit.prevent="doReset">
        <div class="flex items-center justify-between border-b border-gray-200 px-6 py-4">
          <h2 class="text-sm font-bold text-gray-800">Reset password</h2>
          <button type="button" class="text-gray-400 hover:text-gray-600" @click="resetTarget = null">
            <XMarkIcon class="h-5 w-5" />
          </button>
        </div>
        <div class="space-y-3 px-6 py-5">
          <p class="text-sm text-gray-600">
            Set a new password for <span class="font-semibold">{{ resetTarget.firstName }} {{ resetTarget.lastName }}</span>.
          </p>
          <p v-if="resetError" class="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{{ resetError }}</p>
          <input v-model="resetPassword" type="text" maxlength="72" autocomplete="off" placeholder="New password" :class="inputClass" />
          <p class="text-xs text-gray-400">At least 8 characters.</p>
        </div>
        <div class="flex items-center justify-end gap-2 border-t border-gray-200 px-6 py-4">
          <button
            type="button"
            class="rounded-lg border border-gray-200 px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50"
            @click="resetTarget = null"
          >
            Cancel
          </button>
          <button
            type="submit"
            class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-50"
            :disabled="resetPassword.length < 8 || resetting"
          >
            {{ resetting ? 'Saving...' : 'Set password' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { PlusIcon, XMarkIcon, MagnifyingGlassIcon } from '@heroicons/vue/24/outline'
import { getUsers, createUser, updateUser, setUserActive, resetUserPassword } from '@/api/users'
import { getActiveDepartments } from '@/api/departments'
import { errorMessage } from '@/api/errors'
import { auth } from '@/stores/auth'

const ROLES = [
  { value: 'STAFF', help: 'Creates documents and acts on the steps routed to their department.' },
  { value: 'APPROVER', help: 'Department head: signs, approves or rejects on behalf of the department.' },
  { value: 'RECORDS', help: 'Records officer: can view all documents and manage archiving.' },
  { value: 'ADMIN', help: 'Manages users, departments and system settings.' }
]

const inputClass =
  'mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100'

const users = ref([])
const departments = ref([])
const loading = ref(false)
const error = ref('')
const search = ref('')

const showForm = ref(false)
const saving = ref(false)
const formError = ref('')
const editingId = ref(null)
const form = reactive({
  username: '', firstName: '', lastName: '', email: '',
  role: 'STAFF', departmentId: null, password: ''
})

const resetTarget = ref(null)
const resetPassword = ref('')
const resetting = ref(false)
const resetError = ref('')

const isSelf = (u) => auth.user?.id === u.id
const editingSelf = computed(() => !!editingId.value && auth.user?.id === editingId.value)
const roleHelp = computed(() => ROLES.find((r) => r.value === form.role)?.help || '')

const canSave = computed(() => {
  const base = form.firstName && form.lastName && form.role && form.departmentId
  if (editingId.value) return !!base
  return !!(base && form.username.length >= 3 && form.password.length >= 8)
})

const filteredUsers = computed(() => {
  const q = search.value.trim().toLowerCase()
  if (!q) return users.value
  return users.value.filter((u) =>
    `${u.firstName} ${u.lastName} ${u.username} ${u.departmentName}`.toLowerCase().includes(q)
  )
})

function formatDate(iso) {
  if (!iso) return 'Never'
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [u, d] = await Promise.all([getUsers(), getActiveDepartments()])
    users.value = u
    departments.value = d
  } catch (err) {
    error.value = errorMessage(err)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, {
    username: '', firstName: '', lastName: '', email: '',
    role: 'STAFF', departmentId: null, password: ''
  })
  formError.value = ''
  showForm.value = true
}

function openEdit(u) {
  editingId.value = u.id
  Object.assign(form, {
    username: u.username, firstName: u.firstName, lastName: u.lastName, email: u.email || '',
    role: u.role, departmentId: u.departmentId, password: ''
  })
  formError.value = ''
  showForm.value = true
}

async function save() {
  saving.value = true
  formError.value = ''
  try {
    const base = {
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email || null,
      role: form.role,
      departmentId: form.departmentId
    }
    if (editingId.value) await updateUser(editingId.value, base)
    else await createUser({ ...base, username: form.username, password: form.password })
    showForm.value = false
    await load()
  } catch (err) {
    formError.value = errorMessage(err)
  } finally {
    saving.value = false
  }
}

async function toggleActive(u) {
  error.value = ''
  try {
    await setUserActive(u.id, !u.active)
    await load()
  } catch (err) {
    error.value = errorMessage(err)
  }
}

function openReset(u) {
  resetTarget.value = u
  resetPassword.value = ''
  resetError.value = ''
}

async function doReset() {
  resetting.value = true
  resetError.value = ''
  try {
    await resetUserPassword(resetTarget.value.id, resetPassword.value)
    resetTarget.value = null
  } catch (err) {
    resetError.value = errorMessage(err)
  } finally {
    resetting.value = false
  }
}

onMounted(load)
</script>