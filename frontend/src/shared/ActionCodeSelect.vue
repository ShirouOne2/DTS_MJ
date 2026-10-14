<template>
  <div class="flex items-center gap-2">
    <select
      :value="modelValue"
      class="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
      @change="$emit('update:modelValue', $event.target.value)"
    >
      <option value="" disabled>Action Code</option>
      <option v-for="code in ACTION_CODES" :key="code.value" :value="code.value">
        {{ code.value }}
      </option>
    </select>

    <div class="group relative inline-flex">
      <InformationCircleIcon class="h-5 w-5 shrink-0 cursor-help text-gray-400 transition-colors hover:text-blue-600" />
      <div
        class="invisible absolute left-1/2 top-full z-20 mt-2 w-72 -translate-x-1/2 rounded-lg border border-gray-200 bg-white p-3 text-left opacity-0 shadow-lg transition-opacity duration-150 group-hover:visible group-hover:opacity-100"
      >
        <p class="mb-2 text-xs font-bold uppercase tracking-wide text-gray-400">
          Action Code Legend
        </p>
        <dl class="space-y-1">
          <div v-for="code in ACTION_CODES" :key="code.value" class="flex gap-2">
            <dt class="w-14 shrink-0 text-xs font-semibold text-blue-600">{{ code.value }}</dt>
            <dd class="text-xs text-gray-600">{{ code.label }}</dd>
          </div>
        </dl>
      </div>
    </div>
  </div>
</template>

<script setup>
import { InformationCircleIcon } from '@heroicons/vue/24/outline'
import { ACTION_CODES } from './ActionCodes'

defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

defineEmits(['update:modelValue'])
</script>
