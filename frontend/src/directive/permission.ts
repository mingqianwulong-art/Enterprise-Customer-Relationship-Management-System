import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

/**
 * 按钮级权限指令
 * 用法：
 *   v-permission="'system:user:add'"        // 单个权限
 *   v-permission="['system:user:add','system:user:edit']"  // 任意一个
 * 无权限时移除该 DOM 元素
 */
export const permission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value } = binding
    const userStore = useUserStore()

    if (!value) {
      return
    }

    // 管理员拥有所有权限
    if (userStore.roles?.includes('ADMIN')) {
      return
    }

    const perms = Array.isArray(value) ? value : [value]
    const hasPerm = perms.some((p: string) => userStore.permissions?.includes(p))

    if (!hasPerm) {
      // 无权限则移除元素
      el.parentNode?.removeChild(el)
    }
  }
}
