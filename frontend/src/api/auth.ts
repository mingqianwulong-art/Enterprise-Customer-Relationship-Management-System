import request from './request'

/** 登录 */
export function login(data: { username: string; password: string }) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/** 获取当前用户信息（角色+权限+菜单） */
export function getInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

/** 退出登录 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/** 发送短信验证码 */
export function sendSmsCode(phone: string) {
  return request({
    url: '/auth/sms/send',
    method: 'post',
    data: { phone }
  })
}

/** 用户注册 */
export function register(data: {
  username: string
  password: string
  phone: string
  email?: string
  code: string
}) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/** 忘记密码（手机号验证码重置） */
export function forgotPassword(data: {
  phone: string
  code: string
  newPassword: string
}) {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data
  })
}

/** 修改当前用户密码 */
export function changePassword(data: { oldPassword: string; newPassword: string }) {
  return request({
    url: '/auth/change-password',
    method: 'post',
    data
  })
}

