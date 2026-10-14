<template>
  <span
    class="group relative inline-flex shrink-0 cursor-help items-center rounded-full bg-blue-50 px-2 py-0.5 text-xs font-bold text-blue-600"
  >
    {{ code }}
    <span
      class="invisible absolute bottom-full left-1/2 z-20 mb-2 w-48 -translate-x-1/2 rounded-lg border border-gray-200 bg-white p-2 text-left text-xs font-normal normal-case text-gray-600 opacity-0 shadow-lg transition-opacity duration-150 group-hover:visible group-hover:opacity-100"
    >
      {{ label }}
    </span>
  </span>
</template>

<script setup>
import { computed } from 'vue'
import { ACTION_CODES } from './ActionCodes'

const props = defineProps({
  code: {
    type: String,
    required: true
  }
})

const label = computed(() => ACTION_CODES.find((c) => c.value === props.code)?.label || 'Unknown code')
</script>
