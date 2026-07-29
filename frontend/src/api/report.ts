import request from './request'

// ==================== 数据看板 ====================

/** 获取数据看板概览统计 */
export function getOverview() {
  return request({ url: '/report/overview', method: 'get' })
}

/** 获取趋势数据 */
export function getTrend(months: number = 6) {
  return request({ url: '/report/trend', method: 'get', params: { months } })
}

/** 获取工单状态分布 */
export function getOrderStatusStats() {
  return request({ url: '/report/order-status', method: 'get' })
}

/** 获取销售业绩排行 */
export function getSalesRanking() {
  return request({ url: '/report/sales-ranking', method: 'get' })
}

/** 获取客户行业分布 */
export function getCustomerIndustryStats() {
  return request({ url: '/report/customer-industry', method: 'get' })
}

/** 获取线索来源分布 */
export function getClueSourceStats() {
  return request({ url: '/report/clue-source', method: 'get' })
}

// ==================== 自定义报表 ====================

/** 自定义客户报表 */
export function getCustomCustomerReport(params: any) {
  return request({ url: '/report/custom/customer', method: 'get', params })
}

/** 自定义销售报表 */
export function getCustomSalesReport(params: any) {
  return request({ url: '/report/custom/sales', method: 'get', params })
}
