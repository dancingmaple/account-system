import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { useRouter } from 'vue-router'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const username = ref('')
  const nickname = ref('')
  const avatar = ref('')
  const userId = ref(null)
  const roles = ref([])
  const permissions = ref([])

  async function loginAction(loginData) {
    const res = await login(loginData)
    token.value = res.data.accessToken
    setToken(res.data.accessToken)
    return res.data
  }

  async function getUserInfoAction() {
    if (!token.value) {
      throw new Error('Token is required')
    }
    try {
      const res = await getUserInfo()
      if (res.data) {
        username.value = res.data.username || ''
        nickname.value = res.data.nickname || ''
        avatar.value = res.data.avatar || ''
        userId.value = res.data.userId || null
      }
      return res.data
    } catch (error) {
      logoutAction()
      throw error
    }
  }

  async function logoutAction() {
    try {
      await logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      token.value = ''
      username.value = ''
      nickname.value = ''
      avatar.value = ''
      userId.value = null
      roles.value = []
      permissions.value = []
      removeToken()
    }
  }

  function resetToken() {
    token.value = ''
    removeToken()
  }

  return {
    token,
    username,
    nickname,
    avatar,
    userId,
    roles,
    permissions,
    loginAction,
    getUserInfoAction,
    logoutAction,
    resetToken
  }
})