import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import MainLayout from '@/layouts/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/LoginPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/RegisterPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/pages/DashboardPage.vue')
      },
      {
        path: 'editor',
        name: 'Editor',
        component: () => import('@/pages/EditorPage.vue')
      },
      {
        path: 'articles',
        name: 'Articles',
        component: () => import('@/pages/ArticlesPage.vue')
      },
      {
        path: 'platforms',
        name: 'Platforms',
        component: () => import('@/pages/PlatformsPage.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/pages/SettingsPage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  
  if (requiresAuth && !authStore.token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && authStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
