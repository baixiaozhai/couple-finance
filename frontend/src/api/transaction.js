import request from './request'

// 添加记账记录
export const addTransaction = (data) => {
  return request.post('/transaction/add', data)
}

// 获取记账列表
export const getTransactions = () => {
  return request.get('/transaction/list')
}

// 删除记账记录
export const deleteTransaction = (id) => {
  return request.delete(`/transaction/delete/${id}`)
}
