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
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="20" class="data-cards" v-loading="loading">
      <el-col :span="6">
        <div class="stat-card" style="border-left-color: #409eff;">
          <div class="stat-card-header">
            <span class="stat-label">客户总数</span>
            <el-tag type="primary" size="small">全部</el-tag>
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
            <span class="stat-label">线索转化率</span>
            <el-tag type="success" size="small">转化中</el-tag>
          </div>
          <div class="stat-value">{{ stats.clueConversionRate }}%</div>
          <div class="stat-footer">
            <span>已转化 <b style="color: #67c23a;">{{ stats.convertedClues }}</b> / {{ stats.totalClues }} 条</span>
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
        <div class="stat-card" style="border-left-color: #f56c6c;">
          <div class="stat-card-header">
            <span class="stat-label">待处理工单</span>
            <el-tag type="danger" size="small">待办</el-tag>
          </div>
          <div class="stat-value">{{ stats.pendingOrders }}</div>
          <div class="stat-footer">
            <span>已完成 <b style="color: #67c23a;">{{ stats.completedOrders }}</b> / {{ stats.totalOrders }} 个</span>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getOverview } from '@/api/report'

const userStore = useUserStore()
const loading = ref(false)

const stats = reactive({
  totalCustomers: 0,
  monthlyNewCustomers: 0,
  clueConversionRate: 0,
  convertedClues: 0,
  totalClues: 0,
  winRate: 0,
  wonOpportunities: 0,
  totalOpportunities: 0,
  pendingOrders: 0,
  completedOrders: 0,
  totalOrders: 0
})

async function loadStats() {
  loading.value = true
  try {
    const res = await getOverview()
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
      stats.completedOrders = data.completedOrders ?? 0
      stats.totalOrders = data.totalOrders ?? 0
      stats.pendingOrders = (data.totalOrders ?? 0) - (data.completedOrders ?? 0)
    }
  } catch (e) {
    console.error('加载工作台数据失败', e)
  } finally {
    loading.value = false
  }
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

onMounted(() => loadStats())
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
</style>
