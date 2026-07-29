import request from './request'

// ==================== 工单管理 ====================

/** 分页查询工单 */
export function getServiceOrderPage(params: any) {
  return request({ url: '/service/order/page', method: 'get', params })
}

/** 工单详情 */
export function getServiceOrderById(id: number) {
  return request({ url: `/service/order/${id}`, method: 'get' })
}

/** 新增工单 */
export function addServiceOrder(data: any) {
  return request({ url: '/service/order', method: 'post', data })
}

/** 修改工单 */
export function updateServiceOrder(data: any) {
  return request({ url: '/service/order', method: 'put', data })
}

/** 删除工单 */
export function deleteServiceOrder(id: number) {
  return request({ url: `/service/order/${id}`, method: 'delete' })
}

/** 分配工单 */
export function assignServiceOrder(id: number, assigneeId: number, assigneeName: string) {
  return request({ url: `/service/order/${id}/assign`, method: 'put', params: { assigneeId, assigneeName } })
}

/** 变更工单状态 */
export function changeOrderStatus(id: number, status: number) {
  return request({ url: `/service/order/${id}/status`, method: 'put', params: { status } })
}

/** 满意度评价 */
export function addSatisfaction(id: number, satisfaction: number, satisfactionComment: string) {
  return request({ url: `/service/order/${id}/satisfaction`, method: 'put', params: { satisfaction, satisfactionComment } })
}

// ==================== 售后记录 ====================

/** 分页查询售后记录 */
export function getServiceRecordPage(params: any) {
  return request({ url: '/service/record/page', method: 'get', params })
}

/** 售后记录详情 */
export function getServiceRecordById(id: number) {
  return request({ url: `/service/record/${id}`, method: 'get' })
}

/** 新增售后记录 */
export function addServiceRecord(data: any) {
  return request({ url: '/service/record', method: 'post', data })
}

/** 修改售后记录 */
export function updateServiceRecord(data: any) {
  return request({ url: '/service/record', method: 'put', data })
}

/** 删除售后记录 */
export function deleteServiceRecord(id: number) {
  return request({ url: `/service/record/${id}`, method: 'delete' })
}

/** 按工单查询记录 */
export function listByOrderId(orderId: number) {
  return request({ url: `/service/record/order/${orderId}`, method: 'get' })
}

/** 按客户查询记录 */
export function listByCustomerId(customerId: number) {
  return request({ url: `/service/record/customer/${customerId}`, method: 'get' })
}
