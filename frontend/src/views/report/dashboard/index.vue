<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $route.meta.title }}</h2>
      <span class="page-subtitle">企业数据全景概览</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-card-inner">
            <div class="stat-info">
              <div class="stat-label">{{ card.label }}</div>
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-sub" v-if="card.sub">{{ card.sub }}</div>
            </div>
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="14">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>业务增长趋势（近{{ trendMonths }}个月）</span>
              <el-radio-group v-model="trendMonths" size="small" @change="loadTrend">
                <el-radio-button :value="6">6个月</el-radio-button>
                <el-radio-button :value="12">12个月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>客户行业分布</span>
            </div>
          </template>
          <div ref="industryChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="14">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>销售业绩排行</span>
            </div>
          </template>
          <div ref="rankingChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>工单状态分布</span>
            </div>
          </template>
          <div ref="orderStatusChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  UserFilled, TrendCharts, ShoppingBag, Money,
  Ticket, Service
} from '@element-plus/icons-vue'
import {
  getOverview, getTrend, getOrderStatusStats,
  getSalesRanking, getCustomerIndustryStats
} from '@/api/report'

const loading = ref(false)

// 统计卡片数据
const overview = ref<any>({})
const statCards = computed(() => {
  const o = overview.value
  if (!o || Object.keys(o).length === 0) return []
  return [
    {
      label: '客户总数',
      value: formatNum(o.totalCustomers),
      sub: `本月新增 ${o.monthlyNewCustomers} 家`,
      icon: UserFilled,
      color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    },
    {
      label: '线索转化率',
      value: o.clueConversionRate + '%',
      sub: `${o.convertedClues} / ${o.totalClues} 条`,
      icon: TrendCharts,
      color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
    },
    {
      label: '商机赢单率',
      value: o.winRate + '%',
      sub: `${o.wonOpportunities} / ${o.totalOpportunities} 个`,
      icon: ShoppingBag,
      color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
    },
    {
      label: '合同总金额',
      value: '¥ ' + formatAmount(o.totalContractAmount),
      sub: `已回款 ¥ ${formatAmount(o.totalReceivedAmount)}`,
      icon: Money,
      color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
    },
    {
      label: '工单处理',
      value: o.completedOrders + ' / ' + o.totalOrders,
      sub: `满意度 ${o.avgSatisfaction} 分`,
      icon: Ticket,
      color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
    },
    {
      label: '公海客户',
      value: formatNum(o.poolCustomers),
      sub: '等待认领中',
      icon: Service,
      color: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)'
    }
  ]
})

// 图表 refs
const trendChartRef = ref<HTMLElement>()
const industryChartRef = ref<HTMLElement>()
const rankingChartRef = ref<HTMLElement>()
const orderStatusChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let industryChart: echarts.ECharts | null = null
let rankingChart: echarts.ECharts | null = null
let orderStatusChart: echarts.ECharts | null = null

const trendMonths = ref(6)

function formatNum(val: any): string {
  if (val === null || val === undefined) return '0'
  return Number(val).toLocaleString()
}

function formatAmount(val: any): string {
  if (val === null || val === undefined) return '0'
  const n = Number(val)
  if (n >= 10000) {
    return (n / 10000).toFixed(1) + '万'
  }
  return n.toLocaleString()
}

// 加载概览数据
async function loadOverview() {
  try {
    const res = await getOverview()
    overview.value = res.data
  } catch (e) {
    console.error('加载概览数据失败', e)
  }
}

// 加载趋势图
async function loadTrend() {
  try {
    const res = await getTrend(trendMonths.value)
    const data = res.data || []
    const months = data.map((d: any) => d.month)
    const customers = data.map((d: any) => d.newCustomers)
    const opportunities = data.map((d: any) => d.newOpportunities)
    const contracts = data.map((d: any) => d.newContracts)

    if (trendChart) {
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['新增客户', '新增商机', '新增合同'], bottom: 0 },
        grid: { left: 60, right: 20, top: 20, bottom: 35 },
        xAxis: { type: 'category', data: months },
        yAxis: { type: 'value' },
        series: [
          {
            name: '新增客户', type: 'line', data: customers,
            smooth: true, lineStyle: { width: 3, color: '#667eea' },
            itemStyle: { color: '#667eea' }, symbol: 'circle', symbolSize: 6
          },
          {
            name: '新增商机', type: 'line', data: opportunities,
            smooth: true, lineStyle: { width: 3, color: '#4facfe' },
            itemStyle: { color: '#4facfe' }, symbol: 'circle', symbolSize: 6
          },
          {
            name: '新增合同', type: 'line', data: contracts,
            smooth: true, lineStyle: { width: 3, color: '#43e97b' },
            itemStyle: { color: '#43e97b' }, symbol: 'circle', symbolSize: 6
          }
        ]
      })
    }
  } catch (e) {
    console.error('加载趋势数据失败', e)
  }
}

// 加载行业分布图
async function loadIndustry() {
  try {
    const res = await getCustomerIndustryStats()
    const data = res.data || []
    if (industryChart) {
      industryChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [{
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: false,
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold' }
          },
          data: data.map((d: any) => ({ name: d.name, value: d.count }))
        }]
      })
    }
  } catch (e) {
    console.error('加载行业分布失败', e)
  }
}

// 加载销售排行图
async function loadRanking() {
  try {
    const res = await getSalesRanking()
    const data = res.data || []
    if (rankingChart) {
      rankingChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: ['商机数', '赢单数'], bottom: 0 },
        grid: { left: 60, right: 20, top: 20, bottom: 35 },
        xAxis: {
          type: 'category',
          data: data.map((d: any) => d.realName),
          axisLabel: { rotate: 30 }
        },
        yAxis: { type: 'value' },
        series: [
          {
            name: '商机数', type: 'bar', data: data.map((d: any) => d.opportunityCount),
            itemStyle: { color: '#4facfe', borderRadius: [4, 4, 0, 0] }, barMaxWidth: 32
          },
          {
            name: '赢单数', type: 'bar', data: data.map((d: any) => d.wonCount),
            itemStyle: { color: '#43e97b', borderRadius: [4, 4, 0, 0] }, barMaxWidth: 32
          }
        ]
      })
    }
  } catch (e) {
    console.error('加载销售排行失败', e)
  }
}

// 加载工单状态图
async function loadOrderStatus() {
  try {
    const res = await getOrderStatusStats()
    const data = res.data || []
    const statusMap: Record<number, string> = { 0: '待处理', 1: '处理中', 2: '待反馈', 3: '已完成', 4: '已关闭', 5: '已取消' }
    const colors = ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#909399', '#c0c4cc']
    if (orderStatusChart) {
      orderStatusChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center' },
        series: [{
          type: 'pie',
          radius: ['45%', '75%'],
          center: ['40%', '50%'],
          avoidLabelOverlap: false,
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold' }
          },
          data: data.map((d: any) => ({
            name: statusMap[d.status] || '未知',
            value: d.count,
            itemStyle: { color: colors[d.status] || '#909399' }
          }))
        }]
      })
    }
  } catch (e) {
    console.error('加载工单状态失败', e)
  }
}

// 初始化图表
function initCharts() {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
  }
  if (industryChartRef.value) {
    industryChart = echarts.init(industryChartRef.value)
  }
  if (rankingChartRef.value) {
    rankingChart = echarts.init(rankingChartRef.value)
  }
  if (orderStatusChartRef.value) {
    orderStatusChart = echarts.init(orderStatusChartRef.value)
  }

  window.addEventListener('resize', handleResize)
}

function handleResize() {
  trendChart?.resize()
  industryChart?.resize()
  rankingChart?.resize()
  orderStatusChart?.resize()
}

async function loadAll() {
  loading.value = true
  await loadOverview()
  await nextTick()
  initCharts()
  // 等待所有图表数据加载完毕再关闭loading
  await Promise.all([loadTrend(), loadIndustry(), loadRanking(), loadOrderStatus()])
  loading.value = false
}

onMounted(() => {
  loadAll()
})

onUnmounted(() => {
  trendChart?.dispose()
  industryChart?.dispose()
  rankingChart?.dispose()
  orderStatusChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: #303133;
}

.page-subtitle {
  font-size: 13px;
  color: #909399;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-card-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.stat-sub {
  font-size: 12px;
  color: #67c23a;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

/* 图表区域 */
.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  border-radius: 8px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart-box {
  width: 100%;
  height: 320px;
}
</style>
