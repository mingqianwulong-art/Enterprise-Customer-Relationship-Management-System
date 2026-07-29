import request from './request'

// ==================== 客户管理 ====================

/** 分页查询客户 */
export function getCustomerPage(params: any) {
  return request({ url: '/customer/page', method: 'get', params })
}

/** 客户详情（含联系人、标签、跟进记录） */
export function getCustomerDetail(id: number) {
  return request({ url: `/customer/${id}`, method: 'get' })
}

/** 新增客户 */
export function addCustomer(data: any) {
  return request({ url: '/customer', method: 'post', data })
}

/** 修改客户 */
export function updateCustomer(data: any) {
  return request({ url: '/customer', method: 'put', data })
}

/** 删除客户 */
export function deleteCustomer(id: number) {
  return request({ url: `/customer/${id}`, method: 'delete' })
}

/** 领取客户（从公海） */
export function claimCustomer(id: number) {
  return request({ url: `/customer/${id}/claim`, method: 'put' })
}

/** 退回公海 */
export function releaseCustomer(id: number) {
  return request({ url: `/customer/${id}/release`, method: 'put' })
}

/** 公海池分页查询 */
export function getPoolPage(params: any) {
  return request({ url: '/customer/pool/page', method: 'get', params })
}

// ==================== 标签管理 ====================

/** 查询全部标签 */
export function getTagList() {
  return request({ url: '/customer/tag/list', method: 'get' })
}

/** 新增标签 */
export function addTag(data: any) {
  return request({ url: '/customer/tag', method: 'post', data })
}

/** 修改标签 */
export function updateTag(data: any) {
  return request({ url: '/customer/tag', method: 'put', data })
}

/** 删除标签 */
export function deleteTag(id: number) {
  return request({ url: `/customer/tag/${id}`, method: 'delete' })
}
