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
