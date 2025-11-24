import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { User } from '@/types'
import api from '@/utils/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)
  const router = useRouter()

  const isAuthenticated = computed(() => !!token.value)

  const login = async (username: string, password: string) => {
    const response = await api.post('/auth/login', { username, password })
    token.value = response.data.token
    user.value = response.data.user
    localStorage.setItem('token', token.value)
    router.push('/')
  }

  const register = async (username: string, email: string, password: string) => {
    await api.post('/auth/register', { username, email, password })
    router.push('/login')
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  const fetchUser = async () => {
    if (!token.value) return
    const response = await api.get('/auth/user')
    user.value = response.data
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    register,
    logout,
    fetchUser
  }
})
