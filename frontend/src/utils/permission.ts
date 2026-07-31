import { useUserStore } from '@/stores/user'

/**
 * 权限工具函数
 * - 管理员角色（ADMIN）拥有所有权限
 * - 其他用户检查 permissions 数组是否包含指定权限标识
 */

/** 判断是否拥有指定权限 */
export function hasPerm(perms: string): boolean {
  const userStore = useUserStore()
  // 管理员拥有所有权限
  if (userStore.roles?.includes('ADMIN')) {
    return true
  }
  return userStore.permissions?.includes(perms) ?? false
}

/** 判断是否拥有指定角色 */
export function hasRole(role: string): boolean {
  const userStore = useUserStore()
  return userStore.roles?.includes(role) ?? false
}

/** 判断是否拥有任意一个权限 */
export function hasAnyPerm(perms: string[]): boolean {
  const userStore = useUserStore()
  if (userStore.roles?.includes('ADMIN')) {
    return true
  }
  return perms.some((p) => userStore.permissions?.includes(p))
}
