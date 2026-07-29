import request from './request'

// ==================== 商机管理 ====================

/** 分页查询商机 */
export function getOpportunityPage(params: any) {
  return request({ url: '/business/opportunity/page', method: 'get', params })
}

/** 商机详情 */
export function getOpportunityById(id: number) {
  return request({ url: `/business/opportunity/${id}`, method: 'get' })
}

/** 新增商机 */
export function addOpportunity(data: any) {
  return request({ url: '/business/opportunity', method: 'post', data })
}

/** 修改商机 */
export function updateOpportunity(data: any) {
  return request({ url: '/business/opportunity', method: 'put', data })
}

/** 删除商机 */
export function deleteOpportunity(id: number) {
  return request({ url: `/business/opportunity/${id}`, method: 'delete' })
}

/** 修改商机阶段 */
export function changeOppStage(id: number, stage: number) {
  return request({ url: `/business/opportunity/${id}/stage`, method: 'put', params: { stage } })
}

/** 销售漏斗数据 */
export function getFunnelData() {
  return request({ url: '/business/opportunity/funnel', method: 'get' })
}

// ==================== 合同管理 ====================

/** 分页查询合同 */
export function getContractPage(params: any) {
  return request({ url: '/business/contract/page', method: 'get', params })
}

/** 合同详情 */
export function getContractById(id: number) {
  return request({ url: `/business/contract/${id}`, method: 'get' })
}

/** 新增合同 */
export function addContract(data: any) {
  return request({ url: '/business/contract', method: 'post', data })
}

/** 修改合同 */
export function updateContract(data: any) {
  return request({ url: '/business/contract', method: 'put', data })
}

/** 删除合同 */
export function deleteContract(id: number) {
  return request({ url: `/business/contract/${id}`, method: 'delete' })
}

/** 审批合同 */
export function approveContract(id: number, approverId: number) {
  return request({ url: `/business/contract/${id}/approve`, method: 'put', params: { approverId } })
}

// ==================== 回款管理 ====================

/** 分页查询回款 */
export function getPaymentPage(params: any) {
  return request({ url: '/business/payment/page', method: 'get', params })
}

/** 回款详情 */
export function getPaymentById(id: number) {
  return request({ url: `/business/payment/${id}`, method: 'get' })
}

/** 新增回款 */
export function addPayment(data: any) {
  return request({ url: '/business/payment', method: 'post', data })
}

/** 修改回款 */
export function updatePayment(data: any) {
  return request({ url: '/business/payment', method: 'put', data })
}

/** 删除回款 */
export function deletePayment(id: number) {
  return request({ url: `/business/payment/${id}`, method: 'delete' })
}

/** 确认回款 */
export function confirmPayment(id: number, actualAmount: number, actualDate: string) {
  return request({ url: `/business/payment/${id}/confirm`, method: 'put', params: { actualAmount, actualDate } })
}
