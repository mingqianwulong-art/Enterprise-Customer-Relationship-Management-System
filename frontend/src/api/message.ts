import request from './request'

/** 未读消息列表 */
export function getUnreadMessages() {
  return request({ url: '/system/message/unread', method: 'get' })
}

/** 所有消息列表 */
export function getMessageList() {
  return request({ url: '/system/message/list', method: 'get' })
}

/** 未读消息数量 */
export function getUnreadCount() {
  return request({ url: '/system/message/unread-count', method: 'get' })
}

/** 标记消息已读 */
export function markMessageRead(id: number) {
  return request({ url: `/system/message/${id}/read`, method: 'put' })
}

/** 标记全部已读 */
export function markAllMessagesRead() {
  return request({ url: '/system/message/read-all', method: 'put' })
}
