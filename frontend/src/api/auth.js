import request from './request'

// 登录
export const login = (data) => {
  return request.post('/auth/login', data)
}

// 注册
export const register = (data) => {
  return request.post('/auth/register', data)
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return request.get('/auth/me')
}
