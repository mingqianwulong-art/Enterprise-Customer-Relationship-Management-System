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
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="user-name">
                {{ userStore.userInfo?.nickname || userStore.userInfo?.username || '未登录' }}
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
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Fold, Expand, ArrowDown, DataAnalysis } from '@element-plus/icons-vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

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

/** 菜单列表 */
const menuList = computed(() => [
  {
    path: '/dashboard',
    meta: { title: '工作台', icon: 'Odometer' }
  },
  {
    path: '/system',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      { path: '/system/user', meta: { title: '用户管理', icon: 'UserFilled' } },
      { path: '/system/role', meta: { title: '角色管理', icon: 'Avatar' } },
      { path: '/system/menu', meta: { title: '菜单管理', icon: 'Menu' } },
      { path: '/system/dept', meta: { title: '部门管理', icon: 'OfficeBuilding' } },
      { path: '/system/log', meta: { title: '操作日志', icon: 'List' } }
    ]
  },
  {
    path: '/market',
    meta: { title: '市场获客', icon: 'Aim' },
    children: [
      { path: '/market/clue', meta: { title: '线索池', icon: 'Aim' } },
      { path: '/market/channel', meta: { title: '渠道管理', icon: 'Share' } },
      { path: '/market/knowledge', meta: { title: '知识库', icon: 'Document' } }
    ]
  },
  {
    path: '/customer',
    meta: { title: '客户管理', icon: 'User' },
    children: [
      { path: '/customer/list', meta: { title: '客户列表', icon: 'UserFilled' } },
      { path: '/customer/pool', meta: { title: '公海池', icon: 'Box' } },
      { path: '/customer/tag', meta: { title: '标签管理', icon: 'PriceTag' } }
    ]
  },
  {
    path: '/business',
    meta: { title: '商机销售', icon: 'TrendCharts' },
    children: [
      { path: '/business/opportunity', meta: { title: '商机管理', icon: 'Trophy' } },
      { path: '/business/contract', meta: { title: '合同管理', icon: 'Document' } },
      { path: '/business/payment', meta: { title: '回款管理', icon: 'Money' } },
      { path: '/business/funnel', meta: { title: '销售漏斗', icon: 'TrendCharts' } }
    ]
  },
  {
    path: '/service',
    meta: { title: '售后服务', icon: 'Service' },
    children: [
      { path: '/service/order', meta: { title: '工单管理', icon: 'Ticket' } },
      { path: '/service/record', meta: { title: '售后记录', icon: 'Files' } }
    ]
  },
  {
    path: '/report',
    meta: { title: '数据分析', icon: 'DataAnalysis' },
    children: [
      { path: '/report/dashboard', meta: { title: '数据看板', icon: 'DataAnalysis' } },
      { path: '/report/custom', meta: { title: '自定义报表', icon: 'Histogram' } }
    ]
  }
])

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
    ElMessage.info('个人信息页面开发中')
  } else if (command === 'password') {
    ElMessage.info('修改密码页面开发中')
  }
}
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
