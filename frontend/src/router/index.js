import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import DocumentView from '../views/DocumentView.vue'
import createdocs from '../views/UploadPage.vue'
import mydocs from '../views/MyDocumentsPage.vue'
import notifications from '../views/Notifications.vue'
import archived from '../views/ArchivedView.vue'
import TransmittalView from '../views/TransmittalView.vue'
import Tracking from '../views/Tracking.vue'
import FileIntegrityView from '../views/FileIntegrityView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
    },
    {
      path: '/document/:id',
      name: 'documentview',
      component: DocumentView,
      props: true,
    },
    {
      path: '/create',
      name: 'create',
      component: createdocs,
    },
    {
      path: '/mydocs',
      name: 'mydocs',
      component: mydocs,
    },
    {
      path: '/transmittal',
      name: 'transmittal',
      component: TransmittalView,
    },
    {
      path: '/tracking',
      name: 'tracking',
      component: Tracking,
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: notifications,
    },
    {
      path: '/archived',
      name: 'archived',
      component: archived,
    },
    {
      path: '/file-integrity',
      name: 'file-integrity',
      component: FileIntegrityView,
    },
  ],
})

export default router
