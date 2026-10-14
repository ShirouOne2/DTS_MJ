<template>
  <router-view v-if="isPublicRoute" />

  <div v-else id="app" class="min-h-screen bg-gray-50">
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
import { auth } from './stores/auth'

const route = useRoute()
const router = useRouter()

const isPublicRoute = computed(() => route.meta.public === true)
const activePage = computed(() => route.name)

const currentUser = computed(() =>
  auth.user ? { name: auth.user.fullName, email: auth.user.username } : null
)
const isAdmin = computed(() => auth.isAdmin)

// TODO: connect to backend, populate from GET /notifications (filter unread)
const unreadNotifications = ref([])

// TODO: connect to backend, PATCH /notifications/read-all
async function handleMarkAllRead() {
  unreadNotifications.value = []
}

async function handleSignOut() {
  await auth.logout()
  router.push({ name: 'login' })
}

// Sidebar open/collapsed state, persisted across refreshes.
const sidebarOpen = ref(localStorage.getItem('sidebarOpen') === 'true')

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}

watch(sidebarOpen, (value) => {
  localStorage.setItem('sidebarOpen', String(value))
})
</script>