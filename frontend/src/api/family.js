import request from './request'

// 创建家庭
export const createFamily = (data) => {
  return request.post('/family/create', data)
}

// 加入家庭
export const joinFamily = (data) => {
  return request.post('/family/join', data)
}

// 获取家庭信息
export const getFamilyInfo = () => {
  return request.get('/family/info')
}

// 退出家庭
export const leaveFamily = () => {
  return request.post('/family/leave')
}
