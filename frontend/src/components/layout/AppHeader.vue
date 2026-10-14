<template>
  <header
    id="header"
    class="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-gray-200 bg-white px-4"
  >
    <button
      class="text-blue-700 transition-colors hover:text-blue-600"
      @click="$emit('toggle-sidebar')"
    >
      <Bars3Icon class="h-7 w-7" />
    </button>

    <div class="ml-auto flex items-center gap-4">
      <!-- Notification Bell -->
      <div class="relative" ref="notifBellRef">
        <button
          class="relative text-blue-700 transition-colors hover:text-blue-600"
          @click.stop="onBellClick"
        >
          <BellIcon class="h-7 w-7" />
          <span
            v-show="unreadCount > 0"
            class="absolute -right-2 -top-2 flex h-[18px] min-w-[18px] animate-pulse items-center justify-center rounded-full bg-gradient-to-br from-blue-600 to-blue-700 text-[0.65rem] font-bold text-white shadow"
          >
            {{ unreadCount }}
          </span>
        </button>
        <Transition
        enter-active-class="transition duration-150 ease-out"
        enter-from-class="opacity-0 scale-95"
        enter-to-class="opacity-100 scale-100"
        leave-active-class="transition duration-100 ease-in"
        leave-from-class="opacity-100 scale-100"
        leave-to-class="opacity-0 scale-95"
        >
        <div
                v-show="notifDropdownOpen"
                class="absolute right-0 mt-2 flex max-h-[500px] w-96 flex-col overflow-hidden rounded-lg bg-white shadow-xl"
                >
                <div
                    class="flex items-center justify-between border-b border-gray-200 px-5 py-4"
                >
                    <h6 class="m-0 text-sm font-bold text-gray-800">Notifications</h6>
                    <button
                    class="rounded px-2 py-1 text-xs font-semibold text-blue-600 transition-colors hover:bg-blue-50"
                    @click="$emit('mark-all-read')"
                    >
                    Mark all as read
                    </button>
                </div>

                <div class="max-h-[400px] overflow-y-auto">
                    <ul v-if="notifications.length" class="divide-y divide-gray-100">
                      <li
                        v-for="notif in notifications"
                        :key="notif.id"
                        class="px-5 py-3 text-sm text-gray-700"
                      >
                        {{ notif.message }}
                      </li>
                    </ul>
                    <div
                    v-else
                    class="flex flex-col items-center px-8 py-12 text-center text-gray-400"
                    >
                    <BellSlashIcon class="mb-4 h-12 w-12 opacity-50" />
                    <p>No notifications yet</p>
                    </div>
                </div>

                <div class="border-t border-gray-200 px-5 py-3 text-center">
                    <router-link
                    to="/notifications"
                    class="text-sm font-semibold text-blue-600 hover:underline"
                    >
                    View all notifications
                    </router-link>
                </div>
                </div>
        </Transition>
        
      </div>

      <!-- User Account -->
      <div class="relative" ref="userAccountRef">
        <button
          class="flex items-center gap-2 rounded-lg px-2 py-1 transition-colors hover:bg-blue-50/50"
          @click.stop="onUserClick"
        >
          <span class="text-sm font-semibold text-gray-800">{{ user?.name || 'Account' }}</span>
          <div
            class="flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-blue-600 to-blue-700 font-bold text-white shadow-md"
          >
            {{ userInitial }}
          </div>
        </button>
        <Transition
        enter-active-class="transition duration-150 ease-out"
        enter-from-class="opacity-0 scale-95"
        enter-to-class="opacity-100 scale-100"
        leave-active-class="transition duration-100 ease-in"
        leave-from-class="opacity-100 scale-100"
        leave-to-class="opacity-0 scale-95"
        >
            <div
            v-show="userDropdownOpen"
            class="absolute right-0 mt-2 flex min-w-[250px] flex-col overflow-hidden rounded-lg bg-white shadow-xl"
            >
            <div class="flex items-center gap-2 border-b border-gray-200 px-5 py-4">
                <UserCircleIcon class="h-5 w-5 text-blue-600" />
                <h6 class="m-0 text-sm font-bold text-gray-800">{{ user?.name || 'Account' }}</h6>
            </div>

            <div class="py-2">
                <router-link
                to="/"
                class="flex items-center gap-3 px-6 py-3 text-gray-800 transition-colors hover:bg-gray-50"
                >
                <Cog6ToothIcon class="h-5 w-5 text-gray-500" />
                <span>Account Settings</span>
                </router-link>

                <router-link
                to="/"
                class="flex items-center gap-3 px-6 py-3 text-gray-800 transition-colors hover:bg-gray-50"
                >
                <BookOpenIcon class="h-5 w-5 text-gray-500" />
                <span>Knowledge Base</span>
                </router-link>

                <!-- Theme Toggle -->
                <div
                class="flex cursor-pointer items-center justify-between px-6 py-3 transition-colors hover:bg-gray-50"
                @click="toggleThemeFromSlider"
                >
                <div class="flex items-center gap-3 text-sm text-gray-800">
                    <MoonIcon class="h-5 w-5 text-gray-500" />
                    <span>Dark Mode</span>
                </div>
                <label
                    class="relative inline-flex cursor-pointer items-center"
                    @click.stop
                >
                    <input
                    type="checkbox"
                    class="peer sr-only"
                    v-model="isDark"
                    @change="applyTheme"
                    />
                    <div
                    class="h-6 w-11 rounded-full bg-gray-300 transition-colors duration-300 peer-checked:bg-gradient-to-br peer-checked:from-blue-600 peer-checked:to-blue-700"
                    ></div>
                    <div
                    class="absolute left-1 top-1 h-4 w-4 rounded-full bg-white shadow transition-transform duration-300 peer-checked:translate-x-5"
                    ></div>
                </label>
                </div>

                <div class="my-2 h-px bg-gray-200"></div>
                <button
                type="button"
                class="flex w-full items-center gap-3 px-6 py-3 text-left text-blue-600 transition-colors hover:bg-gray-50"
                @click="$emit('sign-out')"
                >
                <ArrowRightOnRectangleIcon class="h-5 w-5" />
                <span>Sign Out</span>
                </button>
            </div>
            </div>
        </transition>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  Bars3Icon,
  BellIcon,
  BellSlashIcon,
  UserCircleIcon,
  Cog6ToothIcon,
  BookOpenIcon,
  MoonIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/vue/24/outline'

// user: pass the authenticated user, e.g. { name, email }, once auth is wired up.
// notifications: pass unread notifications from GET /notifications.
const props = defineProps({
  user: {
    type: Object,
    default: null
  },
  notifications: {
    type: Array,
    default: () => []
  }
})

defineEmits(['toggle-sidebar', 'mark-all-read', 'sign-out'])

const unreadCount = computed(() => props.notifications.length)
const userInitial = computed(() => (props.user?.name ? props.user.name.charAt(0).toUpperCase() : '?'))

const notifDropdownOpen = ref(false)
const userDropdownOpen = ref(false)
const isDark = ref(false)

const notifBellRef = ref(null)
const userAccountRef = ref(null)

function onBellClick() {
  userDropdownOpen.value = false
  notifDropdownOpen.value = !notifDropdownOpen.value
}

function onUserClick() {
  notifDropdownOpen.value = false
  userDropdownOpen.value = !userDropdownOpen.value
}

function onClickOutside(e) {
  if (notifBellRef.value && !notifBellRef.value.contains(e.target)) {
    notifDropdownOpen.value = false
  }
  if (userAccountRef.value && !userAccountRef.value.contains(e.target)) {
    userDropdownOpen.value = false
  }
}

function applyTheme() {
  document.body.classList.toggle('dark-mode', isDark.value)
}

function toggleThemeFromSlider(event) {
  if (!event.target.closest('label')) {
    isDark.value = !isDark.value
    applyTheme()
  }
}

onMounted(() => {
  document.addEventListener('click', onClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', onClickOutside)
})
</script>
