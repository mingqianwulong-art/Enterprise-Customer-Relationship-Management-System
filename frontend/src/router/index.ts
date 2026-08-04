import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/register/index.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/forgot-password',
      name: 'ForgotPassword',
      component: () => import('@/views/forgot-password/index.vue'),
      meta: { title: '找回密码' }
    },
    {
      path: '/',
      component: () => import('@/layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '工作台', icon: 'Odometer' }
        },
        // 系统管理
        {
          path: 'system/user',
          name: 'SystemUser',
          component: () => import('@/views/system/user/index.vue'),
          meta: { title: '用户管理', icon: 'UserFilled', perms: 'system:user:list' }
        },
        {
          path: 'system/role',
          name: 'SystemRole',
          component: () => import('@/views/system/role/index.vue'),
          meta: { title: '角色管理', icon: 'Avatar', perms: 'system:role:list' }
        },
        {
          path: 'system/menu',
          name: 'SystemMenu',
          component: () => import('@/views/system/menu/index.vue'),
          meta: { title: '菜单管理', icon: 'Menu', perms: 'system:menu:list' }
        },
        {
          path: 'system/dept',
          name: 'SystemDept',
          component: () => import('@/views/system/dept/index.vue'),
          meta: { title: '部门管理', icon: 'OfficeBuilding', perms: 'system:dept:list' }
        },
        {
          path: 'system/log',
          name: 'SystemLog',
          component: () => import('@/views/system/log/index.vue'),
          meta: { title: '操作日志', icon: 'List', perms: 'system:log:list' }
        },
        {
          path: 'system/message',
          name: 'SystemMessage',
          component: () => import('@/views/system/message/index.vue'),
          meta: { title: '消息中心', icon: 'Bell' }
        },
        {
          path: 'system/profile',
          name: 'SystemProfile',
          component: () => import('@/views/system/profile/index.vue'),
          meta: { title: '个人信息', hidden: true }
        },
        {
          path: 'system/password',
          name: 'SystemPassword',
          component: () => import('@/views/system/password/index.vue'),
          meta: { title: '修改密码', hidden: true }
        },
        // 市场获客
        {
          path: 'market/clue',
          name: 'MarketClue',
          component: () => import('@/views/market/clue/index.vue'),
          meta: { title: '线索池', icon: 'Aim', perms: 'market:clue:list' }
        },
        {
          path: 'market/channel',
          name: 'MarketChannel',
          component: () => import('@/views/market/channel/index.vue'),
          meta: { title: '渠道管理', icon: 'Share', perms: 'market:channel:list' }
        },
        {
          path: 'market/knowledge',
          name: 'MarketKnowledge',
          component: () => import('@/views/market/knowledge/index.vue'),
          meta: { title: '知识库', icon: 'Document', perms: 'market:knowledge:list' }
        },
        // 客户管理
        {
          path: 'customer/list',
          name: 'CustomerList',
          component: () => import('@/views/customer/list/index.vue'),
          meta: { title: '客户列表', icon: 'UserFilled', perms: 'customer:customer:list' }
        },
        {
          path: 'customer/detail/:id',
          name: 'CustomerDetail',
          component: () => import('@/views/customer/detail/index.vue'),
          meta: { title: '客户详情', hidden: true, perms: 'customer:customer:list' }
        },
        {
          path: 'customer/pool',
          name: 'CustomerPool',
          component: () => import('@/views/customer/pool/index.vue'),
          meta: { title: '公海池', icon: 'Box', perms: 'customer:customer:list' }
        },
        {
          path: 'customer/tag',
          name: 'CustomerTag',
          component: () => import('@/views/customer/tag/index.vue'),
          meta: { title: '标签管理', icon: 'PriceTag', perms: 'customer:customer:list' }
        },
        // 商机销售
        {
          path: 'business/opportunity',
          name: 'BizOpportunity',
          component: () => import('@/views/business/opportunity/index.vue'),
          meta: { title: '商机管理', icon: 'Trophy', perms: 'business:opportunity:list' }
        },
        {
          path: 'business/contract',
          name: 'BizContract',
          component: () => import('@/views/business/contract/index.vue'),
          meta: { title: '合同管理', icon: 'Document', perms: 'business:contract:list' }
        },
        {
          path: 'business/payment',
          name: 'BizPayment',
          component: () => import('@/views/business/payment/index.vue'),
          meta: { title: '回款管理', icon: 'Money', perms: 'business:payment:list' }
        },
        {
          path: 'business/funnel',
          name: 'BizFunnel',
          component: () => import('@/views/business/funnel/index.vue'),
          meta: { title: '销售漏斗', icon: 'TrendCharts', perms: 'business:opportunity:list' }
        },
        {
          path: 'business/sign-in',
          name: 'BizSignIn',
          component: () => import('@/views/business/sign-in/index.vue'),
          meta: { title: '外勤签到', icon: 'Location', perms: 'business:sign-in:list' }
        },
        // 售后服务
        {
          path: 'service/order',
          name: 'ServiceOrder',
          component: () => import('@/views/service/order/index.vue'),
          meta: { title: '工单管理', icon: 'Ticket', perms: 'service:order:list' }
        },
        {
          path: 'service/record',
          name: 'ServiceRecord',
          component: () => import('@/views/service/record/index.vue'),
          meta: { title: '售后记录', icon: 'Files', perms: 'service:record:list' }
        },
        // 数据分析
        {
          path: 'report/dashboard',
          name: 'ReportDashboard',
          component: () => import('@/views/report/dashboard/index.vue'),
          meta: { title: '数据看板', icon: 'DataAnalysis', perms: 'report:dashboard:list' }
        },
        {
          path: 'report/custom',
          name: 'ReportCustom',
          component: () => import('@/views/report/custom/index.vue'),
          meta: { title: '自定义报表', icon: 'Histogram', perms: 'report:custom:list' }
        },
        {
          path: 'report/forecast',
          name: 'ReportForecast',
          component: () => import('@/views/report/forecast/index.vue'),
          meta: { title: '预测分析', icon: 'MagicStick', perms: 'report:forecast:list' }
        }
      ]
    }
  ]
})

// 路由守卫：未登录跳登录页
router.beforeEach(async (to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password') {
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }
  if (!token) {
    next('/login')
    return
  }
  // token 存在但用户信息丢失（例如清过浏览器缓存）时，重新拉取一次
  // 避免 hasPerm 全部返回 false 导致左侧菜单只剩"工作台"
  const { useUserStore } = await import('@/stores/user')
  const userStore = useUserStore()
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      // token 失效，跳登录页
      next('/login')
      return
    }
  }
  // 拦截直接访问停用菜单的 URL（防绕过左侧导航栏点击）
  if (userStore.isMenuDisabled(to.path)) {
    ElMessage.warning('该功能已被停用')
    next('/dashboard')
    return
  }
  // 前端权限校验：路由声明了 meta.perms 时，校验当前用户是否具备
  const requiredPerms = to.meta?.perms as string | string[] | undefined
  if (requiredPerms) {
    const { hasPerm, hasAnyPerm } = await import('@/utils/permission')
    const ok = Array.isArray(requiredPerms) ? hasAnyPerm(requiredPerms) : hasPerm(requiredPerms)
    if (!ok) {
      ElMessage.error('无权访问该功能')
      next('/dashboard')
      return
    }
  }
  next()
})

export default router
