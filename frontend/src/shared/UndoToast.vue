<template>
  <Transition
    enter-active-class="transition ease-out duration-200"
    enter-from-class="opacity-0 translate-y-2"
    leave-active-class="transition ease-in duration-150"
    leave-to-class="opacity-0"
  >
    <div
      v-if="toastState.visible"
      class="fixed bottom-6 left-1/2 z-[100] flex -translate-x-1/2 items-center gap-4 rounded-xl bg-gray-900 px-5 py-3 text-sm text-white shadow-lg"
    >
      <span>{{ toastState.message }}</span>
      <button type="button" class="font-semibold text-blue-300 hover:text-blue-200" @click="triggerUndo">
        Undo ({{ toastState.secondsLeft }}s)
      </button>
      <button type="button" class="text-gray-400 hover:text-gray-200" @click="hideToast">
        <XMarkIcon class="h-4 w-4" />
      </button>
    </div>
  </Transition>
</template>

<script setup>
import { XMarkIcon } from '@heroicons/vue/24/outline'
import { toastState, triggerUndo, hideToast } from './toast'
</script>
