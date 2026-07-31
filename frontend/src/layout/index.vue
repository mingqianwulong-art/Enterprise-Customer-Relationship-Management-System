<template>
  <el-container class="layout-container">
    <!-- 左侧边栏 -->
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="layout-aside">
      <div class="aside-logo" @click="appStore.toggleSidebar()">
        <el-icon v-if="!appStore.sidebarCollapsed" :size="28" color="#409eff"><DataAnalysis /></el-icon>
        <span v-if="!appStore.sidebarCollapsed" class="logo-title">CRM 系统</span>
        <span v-else class="logo-mini">CRM</span>
      </div>

      <el-menu
        :default-active="appStore.activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        class="aside-menu"
        @select="handleMenuSelect"
      >
          <template v-for="item in menuList" :key="item.path">
            <!-- 有子菜单 -->
            <el-sub-menu v-if="item.children && item.children.length" :index="item.path">
              <template #title>
                <el-icon v-if="item.meta?.icon">
                  <component :is="item.meta.icon" />
                </el-icon>
                <span>{{ item.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                :key="child.path"
                :index="child.path"
              >
                <el-icon v-if="child.meta?.icon">
                  <component :is="child.meta.icon" />
                </el-icon>
                <span>{{ child.meta?.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <!-- 无子菜单 -->
            <el-menu-item v-else :index="item.path">
              <el-icon v-if="item.meta?.icon">
                <component :is="item.meta.icon" />
              </el-icon>
              <span>{{ item.meta?.title }}</span>
            </el-menu-item>
          </template>
        </el-menu>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-button
            class="collapse-btn"
            text
            @click="appStore.toggleSidebar()"
          >
            <el-icon :size="20">
              <Fold v-if="!appStore.sidebarCollapsed" />
              <Expand v-else />
            </el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute?.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 消息铃铛 -->
          <el-popover
            placement="bottom-end"
            :width="360"
            trigger="click"
            @show="loadUnreadMessages"
          >
            <template #reference>
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="bell-badge">
                <el-icon :size="20" class="bell-icon"><Bell /></el-icon>
              </el-badge>
            </template>
            <div class="msg-panel">
              <div class="msg-header">
                <span class="msg-title">消息通知</span>
                <el-button v-if="unreadCount > 0" link type="primary" @click="handleMarkAllRead">全部已读</el-button>
              </div>
              <el-scrollbar max-height="320px">
                <div v-if="messageList.length === 0" class="msg-empty">暂无消息</div>
                <div
                  v-for="msg in messageList"
                  :key="msg.id"
                  class="msg-item"
                  :class="{ unread: msg.isRead === 0 }"
                  @click="handleMessageClick(msg)"
                >
                  <div class="msg-item-title">{{ msg.title }}</div>
                  <div class="msg-item-content">{{ msg.content }}</div>
                  <div class="msg-item-time">{{ formatTime(msg.createTime) }}</div>
                </div>
              </el-scrollbar>
            </div>
          </el-popover>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="user-name">
                {{ userStore.userInfo?.realName || userStore.userInfo?.username || '未登录' }}
              </span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, watch, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Fold, Expand, ArrowDown, DataAnalysis, Bell } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { hasPerm } from '@/utils/permission'
import { getUnreadCount, getUnreadMessages, markMessageRead, markAllMessagesRead } from '@/api/message'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const currentRoute = computed(() => route)

/** 监听路由变化，更新当前激活菜单 */
watch(
  () => route.path,
  (path) => {
    appStore.setActiveMenu(path)
  },
  { immediate: true }
)

/** 全部菜单定义（含权限标识 perms） */
const allMenus = [
  {
    path: '/dashboard',
    meta: { title: '工作台', icon: 'Odometer' }
  },
  {
    path: '/system',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      { path: '/system/user', meta: { title: '用户管理', icon: 'UserFilled' }, perms: 'system:user:list' },
      { path: '/system/role', meta: { title: '角色管理', icon: 'Avatar' }, perms: 'system:role:list' },
      { path: '/system/menu', meta: { title: '菜单管理', icon: 'Menu' }, perms: 'system:menu:list' },
      { path: '/system/dept', meta: { title: '部门管理', icon: 'OfficeBuilding' }, perms: 'system:dept:list' },
      { path: '/system/log', meta: { title: '操作日志', icon: 'List' }, perms: 'system:log:list' }
    ]
  },
  {
    path: '/market',
    meta: { title: '市场获客', icon: 'Aim' },
    children: [
      { path: '/market/clue', meta: { title: '线索池', icon: 'Aim' }, perms: 'market:clue:list' },
      { path: '/market/channel', meta: { title: '渠道管理', icon: 'Share' }, perms: 'market:channel:list' },
      { path: '/market/knowledge', meta: { title: '知识库', icon: 'Document' }, perms: 'market:knowledge:list' }
    ]
  },
  {
    path: '/customer',
    meta: { title: '客户管理', icon: 'User' },
    children: [
      { path: '/customer/list', meta: { title: '客户列表', icon: 'UserFilled' }, perms: 'customer:customer:list' },
      { path: '/customer/pool', meta: { title: '公海池', icon: 'Box' }, perms: 'customer:customer:list' },
      { path: '/customer/tag', meta: { title: '标签管理', icon: 'PriceTag' }, perms: 'customer:customer:list' }
    ]
  },
  {
    path: '/business',
    meta: { title: '商机销售', icon: 'TrendCharts' },
    children: [
      { path: '/business/opportunity', meta: { title: '商机管理', icon: 'Trophy' }, perms: 'business:opportunity:list' },
      { path: '/business/contract', meta: { title: '合同管理', icon: 'Document' }, perms: 'business:contract:list' },
      { path: '/business/payment', meta: { title: '回款管理', icon: 'Money' }, perms: 'business:payment:list' },
      { path: '/business/funnel', meta: { title: '销售漏斗', icon: 'TrendCharts' }, perms: 'business:opportunity:list' }
    ]
  },
  {
    path: '/service',
    meta: { title: '售后服务', icon: 'Service' },
    children: [
      { path: '/service/order', meta: { title: '工单管理', icon: 'Ticket' }, perms: 'service:order:list' },
      { path: '/service/record', meta: { title: '售后记录', icon: 'Files' }, perms: 'service:record:list' }
    ]
  },
  {
    path: '/report',
    meta: { title: '数据分析', icon: 'DataAnalysis' },
    children: [
      { path: '/report/dashboard', meta: { title: '数据看板', icon: 'DataAnalysis' }, perms: 'report:dashboard:list' },
      { path: '/report/custom', meta: { title: '自定义报表', icon: 'Histogram' }, perms: 'report:custom:list' }
    ]
  }
]

/** 按权限过滤后的菜单列表 */
const menuList = computed(() => {
  return allMenus
    .map((item) => {
      // 工作台等无 perms 的菜单直接显示
      if (!item.children) {
        return item
      }
      // 过滤子菜单：有权限或无 perms 标识的保留
      const visibleChildren = item.children.filter((child) => !child.perms || hasPerm(child.perms))
      return { ...item, children: visibleChildren }
    })
    // 父菜单无子菜单时隐藏（工作台除外）
    .filter((item) => !item.children || item.children.length > 0)
})

/** 菜单点击导航 */
function handleMenuSelect(index: string) {
  router.push(index)
}

/** 处理用户下拉菜单命令 */
function handleCommand(command: string) {
  if (command === 'logout') {
    userStore.logoutAction()
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    router.push('/system/profile')
  } else if (command === 'password') {
    router.push('/system/password')
  }
}

// ==================== 消息通知 ====================
interface SysMessage {
  id: number
  title: string
  content: string
  type: number
  refId: number | null
  refType: string
  isRead: number
  createTime: string
}

const unreadCount = ref(0)
const messageList = ref<SysMessage[]>([])
let pollTimer: ReturnType<typeof setInterval> | null = null

/** 加载未读消息列表 */
async function loadUnreadMessages() {
  try {
    const res: any = await getUnreadMessages()
    messageList.value = res.data || []
  } catch (e) {
    // 忽略
  }
}

/** 刷新未读数量 */
async function refreshUnreadCount() {
  try {
    const res: any = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (e) {
    // 忽略
  }
}

/** 点击消息：标记已读并跳转业务页 */
async function handleMessageClick(msg: SysMessage) {
  if (msg.isRead === 0) {
    try {
      await markMessageRead(msg.id)
      msg.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (e) {
      // 忽略
    }
  }
  // 根据关联类型跳转
  if (msg.refType === 'customer' && msg.refId) {
    router.push('/customer/list')
  } else if (msg.refType === 'opportunity' && msg.refId) {
    router.push('/business/opportunity')
  }
}

/** 全部已读 */
async function handleMarkAllRead() {
  try {
    await markAllMessagesRead()
    messageList.value.forEach((m) => (m.isRead = 1))
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    // 忽略
  }
}

/** 格式化时间 */
function formatTime(time: string) {
  if (!time) return ''
  return time
}

onMounted(() => {
  refreshUnreadCount()
  // 每60秒轮询一次未读数量
  pollTimer = setInterval(refreshUnreadCount, 60000)
})

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* 侧边栏 */
.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.aside-logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.logo-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.logo-mini {
  font-size: 18px;
  font-weight: 700;
  color: #409eff;
}

/* 菜单 */
.aside-menu {
  border-right: none;
  height: calc(100vh - 56px);
  overflow-y: auto;
  overflow-x: hidden;
}

/* 顶部栏 */
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px !important;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  font-size: 20px;
}

/* 面包屑 */
:deep(.el-breadcrumb) {
  line-height: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 消息铃铛 */
.bell-badge {
  cursor: pointer;
}

.bell-icon {
  color: #606266;
  transition: color 0.2s;
}

.bell-icon:hover {
  color: #409eff;
}

/* 消息面板 */
.msg-panel {
  margin: -4px -8px;
}

.msg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #ebeef5;
}

.msg-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.msg-empty {
  padding: 32px 0;
  text-align: center;
  color: #909399;
  font-size: 13px;
}

.msg-item {
  padding: 10px 12px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: background 0.2s;
}

.msg-item:hover {
  background: #f5f7fa;
}

.msg-item.unread {
  background: #ecf5ff;
}

.msg-item.unread .msg-item-title::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f56c6c;
  margin-right: 6px;
  vertical-align: middle;
}

.msg-item-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.msg-item-content {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.msg-item-time {
  font-size: 11px;
  color: #909399;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-name {
  font-size: 14px;
  color: #303133;
}

/* 内容区 */
.layout-main {
  background: #f0f2f5;
  min-height: calc(100vh - 56px);
  padding: 0;
}
</style>
