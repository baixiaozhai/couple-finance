import request from './request'

// 获取月度统计
export const getMonthlyStatistics = (month) => {
  return request.get('/statistics/month', { params: { month } })
}
