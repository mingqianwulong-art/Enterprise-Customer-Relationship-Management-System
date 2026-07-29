import request from './request'

// ==================== 线索管理 ====================

/** 分页查询线索 */
export function getCluePage(params: any) {
  return request({ url: '/market/clue/page', method: 'get', params })
}

/** 线索详情 */
export function getClueById(id: number) {
  return request({ url: `/market/clue/${id}`, method: 'get' })
}

/** 新增线索 */
export function addClue(data: any) {
  return request({ url: '/market/clue', method: 'post', data })
}

/** 修改线索 */
export function updateClue(data: any) {
  return request({ url: '/market/clue', method: 'put', data })
}

/** 删除线索 */
export function deleteClue(id: number) {
  return request({ url: `/market/clue/${id}`, method: 'delete' })
}

/** 分配线索 */
export function assignClue(clueId: number, userId: number) {
  return request({ url: `/market/clue/${clueId}/assign`, method: 'put', params: { userId } })
}

/** 抢单 */
export function claimClue(clueId: number) {
  return request({ url: `/market/clue/${clueId}/claim`, method: 'put' })
}

/** 转化为客户 */
export function convertClue(clueId: number, customerId: number) {
  return request({ url: `/market/clue/${clueId}/convert`, method: 'put', params: { customerId } })
}

// ==================== 渠道管理 ====================

/** 分页查询渠道 */
export function getChannelPage(params: any) {
  return request({ url: '/market/channel/page', method: 'get', params })
}

/** 查询所有启用渠道 */
export function getChannelList() {
  return request({ url: '/market/channel/list', method: 'get' })
}

/** 渠道效果统计 */
export function getChannelStats() {
  return request({ url: '/market/channel/stats', method: 'get' })
}

/** 新增渠道 */
export function addChannel(data: any) {
  return request({ url: '/market/channel', method: 'post', data })
}

/** 修改渠道 */
export function updateChannel(data: any) {
  return request({ url: '/market/channel', method: 'put', data })
}

/** 删除渠道 */
export function deleteChannel(id: number) {
  return request({ url: `/market/channel/${id}`, method: 'delete' })
}

// ==================== 知识库 ====================

/** 分页查询知识库 */
export function getKnowledgePage(params: any) {
  return request({ url: '/market/knowledge/page', method: 'get', params })
}

/** 知识库详情 */
export function getKnowledgeById(id: number) {
  return request({ url: `/market/knowledge/${id}`, method: 'get' })
}

/** 新增知识库 */
export function addKnowledge(data: any) {
  return request({ url: '/market/knowledge', method: 'post', data })
}

/** 修改知识库 */
export function updateKnowledge(data: any) {
  return request({ url: '/market/knowledge', method: 'put', data })
}

/** 删除知识库 */
export function deleteKnowledge(id: number) {
  return request({ url: `/market/knowledge/${id}`, method: 'delete' })
}
