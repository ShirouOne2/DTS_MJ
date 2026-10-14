<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-50 px-4">
    <div class="w-full max-w-sm">
      <div class="mb-6 flex flex-col items-center text-center">
        <img :src="pstmnIcon" alt="PHLPOST" class="h-12 w-12" />
        <div class="mt-3 italic font-bold leading-tight text-blue-500">
          PHL<span class="text-red-600">POST</span>
        </div>
        <p class="text-xs italic text-gray-500">Document Tracking System</p>
      </div>

      <form class="rounded-xl border border-gray-200 bg-white p-6 shadow-sm" @submit.prevent="submit">
        <h1 class="text-lg font-bold text-gray-800">Sign in</h1>
        <p class="mt-1 text-sm text-gray-500">Use your office account to continue.</p>

        <p v-if="expired && !error" class="mt-4 rounded-lg bg-amber-50 px-3 py-2 text-sm text-amber-600">
          Your session expired. Please sign in again.
        </p>
        <p v-if="error" class="mt-4 rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">
          {{ error }}
        </p>

        <div class="mt-5">
          <label for="username" class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Username
          </label>
          <input
            id="username"
            v-model.trim="username"
            type="text"
            autocomplete="username"
            autofocus
            class="mt-1.5 w-full rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
          />
        </div>

        <div class="mt-4">
          <label for="password" class="text-xs font-semibold uppercase tracking-wide text-gray-400">
            Password
          </label>
          <div class="relative mt-1.5">
            <input
              id="password"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              autocomplete="current-password"
              class="w-full rounded-lg border border-gray-200 py-2 pl-3 pr-10 text-sm text-gray-800 focus:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-100"
            />
            <button
              type="button"
              class="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              @click="showPassword = !showPassword"
            >
              <EyeSlashIcon v-if="showPassword" class="h-5 w-5" />
              <EyeIcon v-else class="h-5 w-5" />
            </button>
          </div>
        </div>

        <button
          type="submit"
          class="mt-6 w-full rounded-lg bg-gradient-to-br from-blue-600 to-blue-700 px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:from-blue-500 hover:to-blue-600 disabled:cursor-not-allowed disabled:opacity-50"
          :disabled="!username || !password || loading"
        >
          {{ loading ? 'Signing in...' : 'Sign in' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { EyeIcon, EyeSlashIcon } from '@heroicons/vue/24/outline'
import pstmnIcon from '@/assets/pstmnIcon.png'
import { auth } from '@/stores/auth'

const route = useRoute()
const router = useRouter()

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')
const expired = computed(() => route.query.expired === '1')

async function submit() {
  if (!username.value || !password.value || loading.value) return
  loading.value = true
  error.value = ''
  try {
    await auth.login(username.value, password.value)
    // Only follow redirects that stay inside the app.
    const target = route.query.redirect
    const safe = typeof target === 'string' && target.startsWith('/') && !target.startsWith('//')
    router.push(safe ? target : '/')
  } catch (err) {
    error.value = err.response?.data?.message || 'Unable to reach the server. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>