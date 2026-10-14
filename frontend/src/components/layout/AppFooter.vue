<template>
  <footer
    class="fixed inset-x-0 bottom-0 border-t border-gray-200 bg-white py-3 text-center text-sm text-gray-500"
  >
    &copy; {{ currentYear }} - PHLPOST Document Depository | Version 1.0
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>