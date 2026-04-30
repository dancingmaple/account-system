import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import constantRoutes from './routes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes
})

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const hasToken = getToken()
  const userStore = useUserStore()

  if (hasToken) {
    if (to.path === '/login') {
      // If already logged in, redirect to dashboard
      next({ path: '/' })
    } else {
      // Check if user info is loaded
      const hasUserInfo = userStore.username
      if (hasUserInfo) {
        next()
      } else {
        try {
          // Get user info
          await userStore.getUserInfoAction()
          next()
        } catch (error) {
          // Token expired, redirect to login
          await userStore.logoutAction()
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else {
    // Not logged in
    if (to.path === '/login') {
      next()
    } else {
      // Redirect to login page with return url
      next(`/login?redirect=${to.path}`)
    }
  }
})

export default router