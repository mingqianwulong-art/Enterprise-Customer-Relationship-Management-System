import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getInfo as getInfoApi, logout as logoutApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')

  // 刷新页面后从 localStorage 恢复用户信息（含 permissions/roles/menus）
  // 否则 hasPerm 会全部返回 false，导致左侧菜单只剩无 perms 的"工作台"
  const cachedUserInfo = localStorage.getItem('userInfo')
  let initialUserInfo: any = null
  let initialPermissions: string[] = []
  let initialRoles: string[] = []
  let initialMenus: any[] = []
  let initialDisabledPaths: string[] = []
  if (cachedUserInfo) {
    try {
      const parsed = JSON.parse(cachedUserInfo)
      initialUserInfo = parsed.user || null
      initialPermissions = parsed.permissions || []
      initialRoles = parsed.roles || []
      initialMenus = parsed.menus || []
      initialDisabledPaths = parsed.disabledPaths || []
    } catch {
      // 缓存损坏，忽略
    }
  }
  const userInfo = ref<any>(initialUserInfo)
  const permissions = ref<string[]>(initialPermissions)
  const roles = ref<string[]>(initialRoles)
  const menus = ref<any[]>(initialMenus)
  // 已停用的菜单路径集合（左侧导航栏点击时拦截并提示"该功能已被停用"）
  const disabledPaths = ref<string[]>(initialDisabledPaths)

  /** 登录 */
  async function loginAction(data: { username: string; password: string }) {
    const res: any = await loginApi(data)
    const loginData = res.data
    token.value = loginData.token
    localStorage.setItem('token', loginData.token)

    // 获取用户信息
    const infoRes: any = await getInfoApi()
    const infoData = infoRes.data
    userInfo.value = infoData.user
    permissions.value = infoData.permissions || []
    roles.value = infoData.roles || []
    menus.value = infoData.menus || []
    disabledPaths.value = infoData.disabledPaths || []
    localStorage.setItem('userInfo', JSON.stringify(infoData))
    return true
  }

  /** 拉取最新的用户信息（供路由守卫在 token 存在但 store 为空时调用） */
  async function fetchUserInfo() {
    const infoRes: any = await getInfoApi()
    const infoData = infoRes.data
    userInfo.value = infoData.user
    permissions.value = infoData.permissions || []
    roles.value = infoData.roles || []
    menus.value = infoData.menus || []
    disabledPaths.value = infoData.disabledPaths || []
    localStorage.setItem('userInfo', JSON.stringify(infoData))
  }

  /** 退出登录 */
  async function logoutAction() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      userInfo.value = null
      permissions.value = []
      roles.value = []
      menus.value = []
      disabledPaths.value = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
  }

  /** 判断指定菜单路径是否已停用 */
  function isMenuDisabled(path: string): boolean {
    return disabledPaths.value.includes(path)
  }

  return { token, userInfo, permissions, roles, menus, disabledPaths, loginAction, logoutAction, fetchUserInfo, isMenuDisabled }
})
