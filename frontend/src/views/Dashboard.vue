<template>
  <div class="space-y-8">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">Welcome back</h1>
      <p class="mt-1 text-sm text-gray-500">
        Here's what's happening across your document tracker today.
      </p>
    </div>

    <p v-if="error" class="text-sm text-rose-600">{{ error }}</p>

    <RecentDocumentsTable :documents="recentDocuments" />

    <div class="grid grid-cols-1 gap-6 lg:grid-cols-2">
      <DraftsList :drafts="drafts" />
      <LatestUpdatesList :updates="latestUpdates" />
    </div>

    <RecentCommentsList :comments="recentComments" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RecentDocumentsTable from '../components/dashboard/RecentDocumentsTable.vue'
import LatestUpdatesList from '../components/dashboard/LatestUpdatesList.vue'
import RecentCommentsList from '../components/dashboard/RecentCommentsList.vue'
import DraftsList from '../components/dashboard/DraftsList.vue'

// TODO: connect to backend
//   recentDocuments <- GET /documents/recent
//   latestUpdates   <- GET /activity/recent
//   recentComments  <- GET /remarks/recent
//   drafts          <- GET /drafts
const recentDocuments = ref([])
const latestUpdates = ref([])
const recentComments = ref([])
const drafts = ref([])
const error = ref('')
</script>
