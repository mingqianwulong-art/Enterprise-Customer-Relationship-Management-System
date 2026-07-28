import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getInfo as getInfoApi, logout as logoutApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)
  const permissions = ref<string[]>([])
  const roles = ref<string[]>([])
  const menus = ref<any[]>([])

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
    localStorage.setItem('userInfo', JSON.stringify(infoData))
    return true
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
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    }
  }

  return { token, userInfo, permissions, roles, menus, loginAction, logoutAction }
})
