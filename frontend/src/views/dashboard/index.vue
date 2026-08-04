<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2>工作台</h2>
    </div>

    <!-- 欢迎信息 -->
    <div class="welcome-card">
      <div class="welcome-text">
        <h3>欢迎回来，{{ userStore.userInfo?.realName || userStore.userInfo?.username || '管理员' }}</h3>
        <p>今天是 {{ today }}，祝您工作愉快！</p>
      </div>
      <div class="welcome-badge" v-if="unreadMessages > 0">
        <el-badge :value="unreadMessages">
          <el-button text @click="goMessage" style="color: #fff;">
            <el-icon :size="20"><Bell /></el-icon> 消息通知
          </el-button>
        </el-badge>
      </div>
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="20" class="data-cards" v-loading="loading">
      <!-- 管理者卡片：全公司新增客户量、总回款额 -->
      <template v-if="isManager">
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #409eff;">
            <div class="stat-card-header">
              <span class="stat-label">全公司客户总数</span>
              <el-tag type="primary" size="small">全部</el-tag>
            </div>
            <div class="stat-value">{{ stats.totalCustomers }}</div>
            <div class="stat-footer">
              <span>本月新增 <b style="color: #409eff;">{{ stats.monthlyNewCustomers }}</b> 家</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #f56c6c;">
            <div class="stat-card-header">
              <span class="stat-label">总回款额</span>
              <el-tag type="danger" size="small">累计</el-tag>
            </div>
            <div class="stat-value">¥{{ formatAmount(stats.totalReceivedAmount) }}</div>
            <div class="stat-footer">
              <span>合同总额 <b>¥{{ formatAmount(stats.totalContractAmount) }}</b></span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #e6a23c;">
            <div class="stat-card-header">
              <span class="stat-label">商机赢单率</span>
              <el-tag type="warning" size="small">跟踪中</el-tag>
            </div>
            <div class="stat-value">{{ stats.winRate }}%</div>
            <div class="stat-footer">
              <span>赢单 <b style="color: #e6a23c;">{{ stats.wonOpportunities }}</b> / {{ stats.totalOpportunities }} 个</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #67c23a;">
            <div class="stat-card-header">
              <span class="stat-label">待处理工单</span>
              <el-tag type="success" size="small">待办</el-tag>
            </div>
            <div class="stat-value">{{ stats.pendingOrders }}</div>
            <div class="stat-footer">
              <span>满意度 <b style="color: #67c23a;">{{ stats.avgSatisfaction }}</b> 分</span>
            </div>
          </div>
        </el-col>
      </template>

      <!-- 销售卡片：今日待跟进客户数、本月业绩完成率 -->
      <template v-else>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #409eff;">
            <div class="stat-card-header">
              <span class="stat-label">我的客户总数</span>
              <el-tag type="primary" size="small">个人</el-tag>
            </div>
            <div class="stat-value">{{ stats.totalCustomers }}</div>
            <div class="stat-footer">
              <span>本月新增 <b style="color: #409eff;">{{ stats.monthlyNewCustomers }}</b> 家</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #67c23a;">
            <div class="stat-card-header">
              <span class="stat-label">今日待跟进</span>
              <el-tag type="success" size="small">今日</el-tag>
            </div>
            <div class="stat-value">{{ todayPending }}</div>
            <div class="stat-footer">
              <span>请及时跟进客户，避免遗漏</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #e6a23c;">
            <div class="stat-card-header">
              <span class="stat-label">我的商机数</span>
              <el-tag type="warning" size="small">进行中</el-tag>
            </div>
            <div class="stat-value">{{ stats.activeOpportunities }}</div>
            <div class="stat-footer">
              <span>已赢单 <b style="color: #e6a23c;">{{ stats.wonOpportunities }}</b> 个</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="border-left-color: #f56c6c;">
            <div class="stat-card-header">
              <span class="stat-label">我的合同额</span>
              <el-tag type="danger" size="small">累计</el-tag>
            </div>
            <div class="stat-value">¥{{ formatAmount(stats.totalContractAmount) }}</div>
            <div class="stat-footer">
              <span>已回款 <b>¥{{ formatAmount(stats.totalReceivedAmount) }}</b></span>
            </div>
          </div>
        </el-col>
      </template>
    </el-row>

    <!-- 快捷入口 -->
    <div class="quick-entry">
      <div class="quick-entry-title">快捷入口</div>
      <div class="quick-entry-list">
        <div class="quick-item" @click="router.push('/market/clue')">
          <el-icon :size="28" color="#409eff"><Aim /></el-icon>
          <span>线索录入</span>
        </div>
        <div class="quick-item" @click="router.push('/customer/list')">
          <el-icon :size="28" color="#67c23a"><UserFilled /></el-icon>
          <span>客户跟进</span>
        </div>
        <div class="quick-item" @click="router.push('/business/opportunity')">
          <el-icon :size="28" color="#e6a23c"><Trophy /></el-icon>
          <span>商机推进</span>
        </div>
        <div class="quick-item" @click="router.push('/system/message')">
          <el-icon :size="28" color="#f56c6c"><Bell /></el-icon>
          <span>消息中心</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Aim, UserFilled, Trophy } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getOverview } from '@/api/report'
import { getUnreadCount } from '@/api/message'
import { getTodayPendingFollow } from '@/api/customer'

const userStore = useUserStore()
const router = useRouter()
const loading = ref(false)
const todayPending = ref(0)
const unreadMessages = ref(0)

// 判断是否为管理者（admin 或 部门经理角色，或拥有报表看板权限）
const isManager = computed(() => {
  const roles = userStore.roles || []
  return roles.includes('admin') || roles.includes('manager')
})

const stats = reactive({
  totalCustomers: 0,
  monthlyNewCustomers: 0,
  clueConversionRate: 0,
  convertedClues: 0,
  totalClues: 0,
  winRate: 0,
  wonOpportunities: 0,
  totalOpportunities: 0,
  activeOpportunities: 0,
  pendingOrders: 0,
  completedOrders: 0,
  totalOrders: 0,
  totalContractAmount: 0,
  totalReceivedAmount: 0,
  avgSatisfaction: 0
})

function formatAmount(val: number) {
  return Number(val || 0).toLocaleString()
}

async function loadStats() {
  loading.value = true
  try {
    const res: any = await getOverview()
    const data = res.data
    if (data) {
      stats.totalCustomers = data.totalCustomers ?? 0
      stats.monthlyNewCustomers = data.monthlyNewCustomers ?? 0
      stats.clueConversionRate = data.clueConversionRate ?? 0
      stats.convertedClues = data.convertedClues ?? 0
      stats.totalClues = data.totalClues ?? 0
      stats.winRate = data.winRate ?? 0
      stats.wonOpportunities = data.wonOpportunities ?? 0
      stats.totalOpportunities = data.totalOpportunities ?? 0
      stats.activeOpportunities = data.activeOpportunities ?? 0
      stats.completedOrders = data.completedOrders ?? 0
      stats.totalOrders = data.totalOrders ?? 0
      stats.pendingOrders = (data.totalOrders ?? 0) - (data.completedOrders ?? 0)
      stats.totalContractAmount = data.totalContractAmount ?? 0
      stats.totalReceivedAmount = data.totalReceivedAmount ?? 0
      stats.avgSatisfaction = data.avgSatisfaction ?? 0
    }
  } catch (e) {
    console.error('加载工作台数据失败', e)
  } finally {
    loading.value = false
  }
}

async function loadTodayPending() {
  try {
    const res: any = await getTodayPendingFollow()
    todayPending.value = (res.data || []).length
  } catch (e) {
    // 忽略错误
  }
}

async function loadUnreadCount() {
  try {
    const res: any = await getUnreadCount()
    unreadMessages.value = res.data || 0
  } catch (e) {
    // 忽略错误
  }
}

function goMessage() {
  router.push('/system/message')
}

/** 今天的日期 */
const today = computed(() => {
  const date = new Date()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  const y = date.getFullYear()
  const m = date.getMonth() + 1
  const d = date.getDate()
  const w = weekDays[date.getDay()]
  return `${y}年${m}月${d}日 星期${w}`
})

onMounted(() => {
  loadStats()
  loadTodayPending()
  loadUnreadCount()
})
</script>

<style scoped>
.dashboard-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

/* 欢迎卡片 */
.welcome-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 8px;
  padding: 24px 28px;
  margin-bottom: 20px;
  color: #fff;
}

.welcome-text h3 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

.welcome-badge :deep(.el-button) {
  color: #fff;
}

/* 数据卡片 */
.data-cards {
  margin: 0 !important;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 12px;
}

.stat-footer {
  font-size: 13px;
  color: #909399;
}

/* 快捷入口 */
.quick-entry {
  margin-top: 24px;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.quick-entry-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.quick-entry-list {
  display: flex;
  gap: 24px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.quick-item:hover {
  background: #f5f7fa;
}

.quick-item span {
  font-size: 13px;
  color: #606266;
}
</style>
