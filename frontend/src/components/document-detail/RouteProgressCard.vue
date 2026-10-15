<template>
  <div class="rounded-xl border border-gray-200 bg-white shadow-sm">
    <div class="flex items-center gap-2 border-b border-gray-200 px-6 py-4">
      <PaperAirplaneIcon class="h-5 w-5 text-blue-600" />
      <h2 class="text-sm font-bold text-gray-800">Routing</h2>
    </div>

    <p v-if="routes.length === 0" class="px-6 py-10 text-center text-sm text-gray-400">
      Not routed yet. Submit the document when the file is ready.
    </p>

    <template v-else>
      <div class="px-6 py-5">
        <RouteBlock
        :route="routes[0]"
        :origin-name="originName"
        @act="$emit('act', $event)"
        />
      </div>

      <details v-if="routes.length > 1" class="border-t border-gray-100">
        <summary class="cursor-pointer px-6 py-3 text-sm font-semibold text-gray-500 hover:text-blue-600">
          Earlier rounds ({{ routes.length - 1 }})
        </summary>
        <div
        v-for="r in routes.slice(1)"
        :key="r.id"
        class="border-t border-gray-100 px-6 py-5"
        >
        <RouteBlock
            :route="r"
            :origin-name="originName"
            @act="$emit('act', $event)"
        />
        </div>
      </details>
    </template>
  </div>
</template>

<script setup>
import { PaperAirplaneIcon } from '@heroicons/vue/24/outline'
import RouteBlock from './RouteBlock.vue'

defineProps({
  routes: { type: Array, default: () => [] },
  originName: { type: String, default: '' }
})

defineEmits(['act'])

function actionLabel(action, step) {
  if (action === 'RECEIVE') return 'Receive'
  if (action === 'ACKNOWLEDGE') return 'Acknowledge'
  if (action === 'REJECT') return 'Reject'
  return step.requiresSignature ? 'Sign / approve' : 'Mark done'
}
</script>