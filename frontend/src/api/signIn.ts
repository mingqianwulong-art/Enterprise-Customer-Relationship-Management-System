import request from './request'

/** 签到类型 */
export type SignType = 1 | 2 | 3  // 1上午签到 2下午签退 3拜访签到

/** 签到记录 */
export interface SignIn {
  id?: number
  userId?: number
  customerId?: number | null
  customerName?: string | null
  signType: SignType
  latitude?: number | null
  longitude?: number | null
  address?: string | null
  remark?: string | null
  signTime?: string
  createTime?: string
}

/** 签到参数 */
export interface SignInParams {
  signType: SignType
  latitude?: number
  longitude?: number
  address?: string
  customerId?: number
  customerName?: string
  remark?: string
}

/** 签到打卡 */
export function signIn(params: SignInParams) {
  return request({ url: '/business/sign-in', method: 'post', data: params })
}

/** 查询今日签到记录 */
export function getTodaySignIn() {
  return request({ url: '/business/sign-in/today', method: 'get' })
}

/** 查询签到记录 */
export function getSignInList(startDate?: string, endDate?: string) {
  return request({ url: '/business/sign-in', method: 'get', params: { startDate, endDate } })
}
