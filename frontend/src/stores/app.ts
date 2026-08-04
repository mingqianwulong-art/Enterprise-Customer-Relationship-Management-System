import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const activeMenu = ref('/dashboard')
  // 移动端抽屉式侧边栏开关（仅 <= 768px 生效）
  const mobileSidebarOpen = ref(false)

  function toggleSidebar() {
    // 移动端优先控制抽屉开合；桌面端控制折叠状态
    if (typeof window !== 'undefined' && window.innerWidth <= 768) {
      mobileSidebarOpen.value = !mobileSidebarOpen.value
    } else {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }
  }

  /** 移动端选中菜单后自动关闭抽屉 */
  function closeMobileSidebar() {
    mobileSidebarOpen.value = false
  }

  function setActiveMenu(path: string) {
    activeMenu.value = path
  }

  return { sidebarCollapsed, activeMenu, mobileSidebarOpen, toggleSidebar, closeMobileSidebar, setActiveMenu }
})
