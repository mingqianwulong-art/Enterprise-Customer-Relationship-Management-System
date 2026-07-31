import { createRouter, createWebHistory } from 'vue-router'

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
          meta: { title: '用户管理', icon: 'UserFilled' }
        },
        {
          path: 'system/role',
          name: 'SystemRole',
          component: () => import('@/views/system/role/index.vue'),
          meta: { title: '角色管理', icon: 'Avatar' }
        },
        {
          path: 'system/menu',
          name: 'SystemMenu',
          component: () => import('@/views/system/menu/index.vue'),
          meta: { title: '菜单管理', icon: 'Menu' }
        },
        {
          path: 'system/dept',
          name: 'SystemDept',
          component: () => import('@/views/system/dept/index.vue'),
          meta: { title: '部门管理', icon: 'OfficeBuilding' }
        },
        {
          path: 'system/log',
          name: 'SystemLog',
          component: () => import('@/views/system/log/index.vue'),
          meta: { title: '操作日志', icon: 'List' }
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
          meta: { title: '线索池', icon: 'Aim' }
        },
        {
          path: 'market/channel',
          name: 'MarketChannel',
          component: () => import('@/views/market/channel/index.vue'),
          meta: { title: '渠道管理', icon: 'Share' }
        },
        {
          path: 'market/knowledge',
          name: 'MarketKnowledge',
          component: () => import('@/views/market/knowledge/index.vue'),
          meta: { title: '知识库', icon: 'Document' }
        },
        // 客户管理
        {
          path: 'customer/list',
          name: 'CustomerList',
          component: () => import('@/views/customer/list/index.vue'),
          meta: { title: '客户列表', icon: 'UserFilled' }
        },
        {
          path: 'customer/detail/:id',
          name: 'CustomerDetail',
          component: () => import('@/views/customer/detail/index.vue'),
          meta: { title: '客户详情', hidden: true }
        },
        {
          path: 'customer/pool',
          name: 'CustomerPool',
          component: () => import('@/views/customer/pool/index.vue'),
          meta: { title: '公海池', icon: 'Box' }
        },
        {
          path: 'customer/tag',
          name: 'CustomerTag',
          component: () => import('@/views/customer/tag/index.vue'),
          meta: { title: '标签管理', icon: 'PriceTag' }
        },
        // 商机销售
        {
          path: 'business/opportunity',
          name: 'BizOpportunity',
          component: () => import('@/views/business/opportunity/index.vue'),
          meta: { title: '商机管理', icon: 'Trophy' }
        },
        {
          path: 'business/contract',
          name: 'BizContract',
          component: () => import('@/views/business/contract/index.vue'),
          meta: { title: '合同管理', icon: 'Document' }
        },
        {
          path: 'business/payment',
          name: 'BizPayment',
          component: () => import('@/views/business/payment/index.vue'),
          meta: { title: '回款管理', icon: 'Money' }
        },
        {
          path: 'business/funnel',
          name: 'BizFunnel',
          component: () => import('@/views/business/funnel/index.vue'),
          meta: { title: '销售漏斗', icon: 'TrendCharts' }
        },
        {
          path: 'business/sign-in',
          name: 'BizSignIn',
          component: () => import('@/views/business/sign-in/index.vue'),
          meta: { title: '外勤签到', icon: 'Location' }
        },
        // 售后服务
        {
          path: 'service/order',
          name: 'ServiceOrder',
          component: () => import('@/views/service/order/index.vue'),
          meta: { title: '工单管理', icon: 'Ticket' }
        },
        {
          path: 'service/record',
          name: 'ServiceRecord',
          component: () => import('@/views/service/record/index.vue'),
          meta: { title: '售后记录', icon: 'Files' }
        },
        // 数据分析
        {
          path: 'report/dashboard',
          name: 'ReportDashboard',
          component: () => import('@/views/report/dashboard/index.vue'),
          meta: { title: '数据看板', icon: 'DataAnalysis' }
        },
        {
          path: 'report/custom',
          name: 'ReportCustom',
          component: () => import('@/views/report/custom/index.vue'),
          meta: { title: '自定义报表', icon: 'Histogram' }
        },
        {
          path: 'report/forecast',
          name: 'ReportForecast',
          component: () => import('@/views/report/forecast/index.vue'),
          meta: { title: '预测分析', icon: 'MagicStick' }
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
  next()
})

export default router
