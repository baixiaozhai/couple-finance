import request from './request'

// 设置预算
export const setBudget = (data) => {
  return request.post('/budget/set', data)
}

// 获取预算列表
export const getBudgets = (month) => {
  return request.get('/budget/list', { params: { month } })
}

// 删除预算
export const deleteBudget = (month, category) => {
  return request.delete('/budget/delete', { params: { month, category } })
}
