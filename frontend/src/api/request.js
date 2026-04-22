import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'

// API基础URL - 生产环境使用完整URL
const API_BASE_URL = import.meta.env.VITE_API_URL || '/api'

// 创建axios实例
const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的code不是200，说明有错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 如果是401，清除token并跳转到登录页
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message))
    }
    
    return res
  },
  (error) => {
    const message = error.response?.data?.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
