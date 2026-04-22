import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getCurrentUser } from '../api/auth'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const familyId = ref(localStorage.getItem('familyId') || '')

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const hasFamily = computed(() => !!familyId.value)

  // Actions
  const setUserInfo = (data) => {
    token.value = data.token || token.value
    userId.value = data.userId || userId.value
    username.value = data.username || username.value
    nickname.value = data.nickname || username.value
    familyId.value = data.familyId || ''
    
    // 持久化到localStorage
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('username', username.value)
    localStorage.setItem('nickname', nickname.value)
    if (familyId.value) {
      localStorage.setItem('familyId', familyId.value)
    } else {
      localStorage.removeItem('familyId')
    }
  }

  const clearUserInfo = () => {
    token.value = ''
    userId.value = ''
    username.value = ''
    nickname.value = ''
    familyId.value = ''
    
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('familyId')
  }

  const login = async (loginData) => {
    const res = await loginApi(loginData)
    if (res.code === 200) {
      setUserInfo(res.data)
      return { success: true }
    }
    return { success: false, message: res.message }
  }

  const register = async (registerData) => {
    const res = await registerApi(registerData)
    if (res.code === 200) {
      setUserInfo(res.data)
      return { success: true }
    }
    return { success: false, message: res.message }
  }

  const logout = () => {
    clearUserInfo()
  }

  const fetchUserInfo = async () => {
    const res = await getCurrentUser()
    if (res.code === 200) {
      setUserInfo(res.data)
      return true
    }
    return false
  }

  const setFamilyId = (id) => {
    familyId.value = id
    if (id) {
      localStorage.setItem('familyId', id)
    } else {
      localStorage.removeItem('familyId')
    }
  }

  return {
    token,
    userId,
    username,
    nickname,
    familyId,
    isLoggedIn,
    hasFamily,
    login,
    register,
    logout,
    fetchUserInfo,
    setFamilyId,
    setUserInfo
  }
})
