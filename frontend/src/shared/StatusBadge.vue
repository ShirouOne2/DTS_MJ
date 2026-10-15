<template>
  <span
    class="inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold"
    :class="statusStyles(status)"
  >
    {{ statusLabel(status) }}
  </span>
</template>

<script setup>
import { statusStyles, statusLabel } from './statusStyles'

defineProps({
  status: { type: String, required: true }
})
</script>