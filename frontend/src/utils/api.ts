import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.request.use(config => {
  const authStore = useAuthStore()
  if (authStore.token) {
    config.headers.Authorization = `Bearer ${authStore.token}`
  }
  return config
})

api.interceptors.response.use(
  response => response.data,
  error => {
    const message = error.response?.data?.message || '请求出错'
    ElMessage.error(message)
    
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
    }
    
    return Promise.reject(error)
  }
)

export default api
