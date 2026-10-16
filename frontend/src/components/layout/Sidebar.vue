<template>
  <div
    id="nav-bar"
    class="fixed left-0 top-0 z-40 flex h-screen flex-col justify-between overflow-hidden border-r border-gray-200 bg-white shadow-sm transition-all duration-300"
    :class="isOpen ? 'w-56' : 'w-16'"
  >
    <nav class="flex h-full flex-col justify-between">
      <div>
        <router-link
          to="/"
          class="flex items-center gap-3 px-4 py-6 text-gray-800"
        >
          <img
            :src="pstmnIcon"
            alt="Philpost Icon"
            class="h-8 w-8 shrink-0"
          />

          <span
            class="whitespace-nowrap transition-opacity duration-200"
            :class="isOpen ? 'opacity-100' : 'opacity-0'"
          >
            <div class="italic font-bold leading-tight text-blue-500">
                PHL<span class="text-red-600">POST</span>
                <div class="italic text-[.65rem] font-normal text-black">
                    Document Tracking System
                </div>
            </div>
          </span>
        </router-link>

        <div class="mt-2 flex flex-col gap-1">
          <router-link
            to="/"
            class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'dashboard' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <Squares2X2Icon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Dashboard</span
            >
          </router-link>

          <router-link
            to="/inbox"
            class="relative flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'inbox' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <InboxArrowDownIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Awaiting Action</span
            >
            <span
              v-if="summary.awaitingAction > 0"
              class="ml-auto shrink-0 rounded-full px-1.5 py-0.5 text-[0.65rem] font-bold text-white transition-opacity duration-200"
              :class="[summary.overdue > 0 ? 'bg-rose-600' : 'bg-blue-600', isOpen ? 'opacity-100' : 'opacity-0']"
            >
              {{ summary.awaitingAction }}
            </span>
          </router-link>

          <router-link
            to="/create"
            class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'create' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <CloudArrowUpIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Create Document</span
            >
          </router-link>

          <router-link
            to="/mydocs"
            class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'mydocs' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <FolderIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >My Docs</span
            >
          </router-link>

          <router-link
            to="/transmittal"
            class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'transmittal' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <PaperAirplaneIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Transmittal</span
            >
          </router-link>

          <router-link
            to="/tracking"
            class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'tracking' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <MapPinIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Tracking</span
            >
          </router-link>

          <router-link
            to="/notifications"
            class="relative flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'notifications' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <BellIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Notifications</span
            >
            <span
              class="ml-auto shrink-0 rounded-full bg-blue-600 px-1.5 py-0.5 text-[0.65rem] font-bold text-white transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
            >
              0
            </span>
          </router-link>

          <router-link
            to="/archived"
            class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
            :class="activePage === 'archived' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
          >
            <ArchiveBoxIcon class="h-5 w-5 shrink-0" />
            <span
              class="whitespace-nowrap transition-opacity duration-200"
              :class="isOpen ? 'opacity-100' : 'opacity-0'"
              >Archived</span
            >
          </router-link>

          <!-- ADMIN ONLY SECTION -->
          <template v-if="isAdmin">
            <div class="my-1 border-t border-gray-100"></div>

            <router-link
              to="/admin/departments"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'departments' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <BuildingOffice2Icon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >Departments</span
              >
            </router-link>

            <router-link
              to="/admin/users"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'users' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <UsersIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >Users</span
              >
            </router-link>
            <router-link
              to="/admin/audit-log"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'audit-log' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <ClipboardDocumentListIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >Audit Log</span
              >
            </router-link>
            <router-link
              to="/"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'analytics' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <ChartBarIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >Analytics</span
              >
            </router-link>

            <router-link
              to="/file-integrity"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'file-integrity' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <ShieldCheckIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >File Integrity</span
              >
            </router-link>

            <router-link
              to="/"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'custom-viewer-groups' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <UserGroupIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >Viewer Groups</span
              >
            </router-link>

            <router-link
              to="/"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'settings' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <Cog6ToothIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >Settings</span
              >
            </router-link>

            <router-link
              to="/"
              class="flex items-center gap-3 border-l-4 border-transparent py-3 pl-5 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
              :class="activePage === 'devtools' ? 'rounded-r-full border-blue-600 bg-blue-50 pr-8 font-semibold text-blue-600' : 'pr-4'"
            >
              <WrenchScrewdriverIcon class="h-5 w-5 shrink-0" />
              <span
                class="whitespace-nowrap transition-opacity duration-200"
                :class="isOpen ? 'opacity-100' : 'opacity-0'"
                >DevTools</span
              >
            </router-link>
          </template>
        </div>
      </div>

      <router-link
        v-if="isAdmin"
        to="/"
        class="flex items-center gap-3 border-l-4 border-transparent py-4 pl-5 pr-4 text-gray-600 transition-colors duration-200 hover:rounded-r-full hover:bg-blue-50 hover:text-blue-600"
      >
        <ArrowRightOnRectangleIcon class="h-5 w-5 shrink-0" />
        <span
          class="whitespace-nowrap transition-opacity duration-200"
          :class="isOpen ? 'opacity-100' : 'opacity-0'"
          >Sign Out</span
        >
      </router-link>
    </nav>
  </div>
</template>

<script setup>
import pstmnIcon from '@/assets/pstmnIcon.png'
import { summary } from '@/stores/summary'

import {
  Squares2X2Icon,
  CloudArrowUpIcon,
  FolderIcon,
  PaperAirplaneIcon,
  MapPinIcon,
  BellIcon,
  ArchiveBoxIcon,
  ChartBarIcon,
  ShieldCheckIcon,
  UserGroupIcon,
  Cog6ToothIcon,
  WrenchScrewdriverIcon,
  ArrowRightOnRectangleIcon,
  BuildingOffice2Icon,
  UsersIcon,
  InboxArrowDownIcon
} from '@heroicons/vue/24/outline'

defineProps({
  activePage: {
    type: String,
    default: ''
  },
  isAdmin: {
    type: Boolean,
    default: false
  },
  isOpen: {
    type: Boolean,
    default: false
  }
})
</script>