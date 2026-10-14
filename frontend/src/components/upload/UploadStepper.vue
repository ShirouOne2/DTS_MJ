<template>
  <ol class="flex items-center">
    <li
      v-for="(step, index) in steps"
      :key="step"
      class="flex flex-1 items-center last:flex-none"
    >
      <div class="flex flex-col items-center gap-2">
        <span
          class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-sm font-bold transition-colors"
          :class="circleClasses(index)"
        >
          <CheckIcon v-if="index + 1 < currentStep" class="h-5 w-5" />
          <span v-else>{{ index + 1 }}</span>
        </span>
        <span
          class="whitespace-nowrap text-xs font-semibold"
          :class="index + 1 <= currentStep ? 'text-gray-800' : 'text-gray-400'"
        >
          {{ step }}
        </span>
      </div>

      <div
        v-if="index !== steps.length - 1"
        class="mx-2 -mt-5 h-px flex-1 transition-colors"
        :class="index + 1 < currentStep ? 'bg-blue-600' : 'bg-gray-200'"
      ></div>
    </li>
  </ol>
</template>

<script setup>
import { CheckIcon } from '@heroicons/vue/24/outline'

const props = defineProps({
  steps: {
    type: Array,
    default: () => ['Document Details', 'Recipients', 'Files & Review']
  },
  currentStep: {
    type: Number,
    required: true
  }
})

function circleClasses(index) {
  if (index + 1 < props.currentStep) return 'bg-blue-600 text-white'
  if (index + 1 === props.currentStep) return 'bg-blue-600 text-white ring-4 ring-blue-100'
  return 'bg-gray-100 text-gray-400'
}
</script>