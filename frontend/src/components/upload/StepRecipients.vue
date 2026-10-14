<template>
  <div>
    <p class="mb-2 text-xs text-gray-400">Route this document to one office, or several at once.</p>
    <div class="inline-flex rounded-lg border border-gray-200 p-1">
      <button
        type="button"
        class="rounded-md px-3 py-1.5 text-sm font-semibold transition-colors"
        :class="mode === 'single' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-50'"
        @click="setMode('single')"
      >
        Single Department
      </button>
      <button
        type="button"
        class="rounded-md px-3 py-1.5 text-sm font-semibold transition-colors"
        :class="mode === 'multiple' ? 'bg-blue-600 text-white' : 'text-gray-600 hover:bg-gray-50'"
        @click="setMode('multiple')"
      >
        Multiple Departments
      </button>
    </div>

    <p class="mt-4 text-xs text-gray-400">
      {{ mode === 'single' ? 'Choose one department to receive this document.' : 'Choose one or more departments to receive this document.' }}
    </p>

    <p v-if="loading" class="mt-3 text-sm text-gray-400">Loading departments…</p>
    <p v-else-if="loadError" class="mt-3 text-sm text-rose-600">Couldn't load departments. Try again.</p>

    <div v-else class="mt-3 grid grid-cols-1 gap-2 sm:grid-cols-2">
      <button
        v-for="dept in departments"
        :key="dept.id"
        type="button"
        class="relative flex items-center gap-2 rounded-lg border px-3 py-2.5 text-left text-sm font-medium transition-colors"
        :class="modelValue.includes(dept.id)
          ? 'border-blue-600 bg-blue-50 text-blue-600'
          : 'border-gray-200 text-gray-600 hover:bg-gray-50'"
        @click="toggleDepartment(dept.id)"
      >
        <BuildingOffice2Icon class="h-4 w-4 shrink-0" />
        <span class="truncate">{{ dept.name }}</span>
        <CheckCircleIconSolid
          v-if="modelValue.includes(dept.id)"
          class="ml-auto h-4 w-4 shrink-0 text-blue-600"
        />
      </button>

      <p v-if="departments.length === 0" class="col-span-full text-sm text-gray-400">
        No departments loaded yet.
      </p>
    </div>

    <p class="mt-4 text-sm text-gray-500">
      <span v-if="modelValue.length === 0">No department selected yet.</span>
      <span v-else>
        <span class="font-semibold text-gray-800">{{ modelValue.length }}</span>
        department{{ modelValue.length > 1 ? 's' : '' }} selected: {{ selectedNames.join(', ') }}
      </span>
    </p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { BuildingOffice2Icon } from '@heroicons/vue/24/outline'
import { CheckCircleIcon as CheckCircleIconSolid } from '@heroicons/vue/24/solid'

const props = defineProps({
  // Array of selected department UUIDs
  modelValue: {
    type: Array,
    required: true
  },
  mode: {
    type: String,
    default: 'single' // 'single' | 'multiple'
  }
})

const emit = defineEmits(['update:modelValue', 'update:mode'])

// TODO: connect to backend — populate from GET /departments
const departments = ref([])
const loading = ref(false)
const loadError = ref(false)

const selectedNames = computed(() =>
  departments.value.filter((d) => props.modelValue.includes(d.id)).map((d) => d.name)
)

function setMode(mode) {
  emit('update:mode', mode)
  if (mode === 'single' && props.modelValue.length > 1) {
    emit('update:modelValue', [props.modelValue[0]])
  }
}

function toggleDepartment(id) {
  if (props.mode === 'single') {
    emit('update:modelValue', [id])
    return
  }
  const next = props.modelValue.includes(id)
    ? props.modelValue.filter((existing) => existing !== id)
    : [...props.modelValue, id]
  emit('update:modelValue', next)
}
</script>
