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
import LoginView from '../views/LoginView.vue'
import DepartmentsView from '../views/admin/DepartmentsView.vue'
import UsersView from '../views/admin/UsersView.vue'
import AuditLogView from '../views/admin/AuditLogView.vue'
import InboxView from '../views/InboxView.vue'
import { auth } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { public: true },
    },
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
    },
    {
      path: '/inbox',
      name: 'inbox',
      component: InboxView,
    },
    {
      path: '/admin/departments',
      name: 'departments',
      component: DepartmentsView,
      meta: { roles: ['ADMIN'] },
    },
    {
      path: '/admin/users',
      name: 'users',
      component: UsersView,
      meta: { roles: ['ADMIN'] },
    },
    {
      path: '/admin/audit-log',
      name: 'audit-log',
      component: AuditLogView,
      meta: { roles: ['ADMIN'] },
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
      meta: { roles: ['ADMIN'] },
    },
  ],
})

router.beforeEach(async (to) => {
  await auth.init()

  if (to.meta.public) {
    // Already signed in? Skip the login page.
    return auth.isLoggedIn && to.name === 'login' ? { name: 'dashboard' } : true
  }
  if (!auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.roles && !to.meta.roles.includes(auth.user.role)) {
    return { name: 'dashboard' }
  }
  return true
})

export default router
