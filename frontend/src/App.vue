<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <Sidebar :active-page="activePage" :is-admin="isAdmin" :is-open="sidebarOpen" />

    <div
      class="flex min-h-screen flex-col transition-all duration-300"
      :class="sidebarOpen ? 'pl-56' : 'pl-16'"
    >
      <AppHeader
        :user="currentUser"
        :notifications="unreadNotifications"
        @toggle-sidebar="toggleSidebar"
        @mark-all-read="handleMarkAllRead"
        @sign-out="handleSignOut"
      />

      <main class="flex-1 p-6 pb-16">
        <router-view />
      </main>

      <AppFooter />
    </div>

    <UndoToast />
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from './components/layout/Sidebar.vue'
import AppHeader from './components/layout/AppHeader.vue'
import AppFooter from './components/layout/AppFooter.vue'
import UndoToast from './shared/UndoToast.vue'

// TODO: replace with real auth state once you wire up Spring Security / login.
// currentUser could come from a Pinia/auth store populated after login.
const currentUser = ref(null)
const isAdmin = ref(false)

// TODO: connect to backend — populate from GET /notifications (filter unread)
const unreadNotifications = ref([])

// TODO: connect to backend — PATCH /notifications/read-all
async function handleMarkAllRead() {
  unreadNotifications.value = []
}

const router = useRouter()
function handleSignOut() {
  // TODO: clear auth token / call a logout endpoint, then redirect.
  router.push('/')
}

const route = useRoute()
const activePage = computed(() => route.name)

// Sidebar open/collapsed state, persisted across refreshes.
const sidebarOpen = ref(localStorage.getItem('sidebarOpen') === 'true')

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}

watch(sidebarOpen, (value) => {
  localStorage.setItem('sidebarOpen', String(value))
})
</script>
