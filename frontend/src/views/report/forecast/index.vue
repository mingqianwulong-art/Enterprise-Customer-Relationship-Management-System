<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $route.meta.title }}</h2>
      <span class="page-subtitle">基于历史数据预测未来销售趋势与流失风险</span>
    </div>

    <!-- 销售趋势预测 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="chart-header">
          <span>销售趋势预测（基于近{{ historyMonths }}个月数据）</span>
          <div>
            <el-radio-group v-model="historyMonths" size="small" @change="loadForecast">
              <el-radio-button :value="3">3个月</el-radio-button>
              <el-radio-button :value="6">6个月</el-radio-button>
              <el-radio-button :value="12">12个月</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      <div ref="forecastChartRef" class="chart-box"></div>

      <el-row :gutter="16" class="forecast-summary" v-if="forecast">
        <el-col :span="8">
          <el-statistic title="下月预测合同金额" :value="forecast.forecastAmount" :precision="2" prefix="¥" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="下月预测新增客户" :value="forecast.forecastCustomers" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="下月预测新增商机" :value="forecast.forecastOpportunities" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 高流失风险客户 -->
    <el-card class="chart-card" shadow="hover" style="margin-top: 16px">
      <template #header>
        <div class="chart-header">
          <span>高流失风险客户识别</span>
          <div>
            <span class="risk-filter-label">阈值天数：</span>
            <el-select v-model="thresholdDays" size="small" style="width: 120px" @change="loadChurnRisk">
              <el-option :value="30" label="30天未跟进" />
              <el-option :value="60" label="60天未跟进" />
              <el-option :value="90" label="90天未跟进" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="churnRiskList" stripe style="width: 100%" v-loading="churnLoading">
        <el-table-column prop="customerName" label="客户名称" min-width="180" />
        <el-table-column label="客户等级" width="100">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.customerLevel)" size="small">
              {{ levelText(row.customerLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="负责人" width="120" />
        <el-table-column label="最后跟进时间" width="180">
          <template #default="{ row }">
            {{ row.lastFollowTime ? row.lastFollowTime : '从未跟进' }}
          </template>
        </el-table-column>
        <el-table-column prop="daysSinceLastFollow" label="未跟进天数" width="120" sortable>
          <template #default="{ row }">
            <span :style="{ color: dayColor(row.daysSinceLastFollow) }">{{ row.daysSinceLastFollow }} 天</span>
          </template>
        </el-table-column>
        <el-table-column label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="riskTagType(row.riskLevel)" size="small">{{ row.riskLevelText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="suggestedAction" label="建议挽留动作" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleRetain(row)" :loading="row.retaining">
              触发挽留
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSalesForecast, getChurnRiskCustomers, triggerRetention } from '@/api/report'

// ==================== 销售趋势预测 ====================
const forecastChartRef = ref<HTMLElement>()
let forecastChart: echarts.ECharts | null = null
const historyMonths = ref(6)
const forecast = ref<any>(null)
const forecastLoading = ref(false)

async function loadForecast() {
  forecastLoading.value = true
  try {
    const res = await getSalesForecast(1, historyMonths.value)
    forecast.value = res.data
    renderForecastChart()
  } catch (e) {
    console.error('加载预测数据失败', e)
  } finally {
    forecastLoading.value = false
  }
}

function renderForecastChart() {
  if (!forecastChartRef.value) return
  if (!forecastChart) {
    forecastChart = echarts.init(forecastChartRef.value)
  }

  const history = forecast.value?.history || []
  const months = history.map((h: any) => h.month)
  const amounts = history.map((h: any) => Number(h.contractAmount || 0))
  const customers = history.map((h: any) => Number(h.newCustomers || 0))

  // 追加预测月份
  months.push(forecast.value?.month + '(预测)')
  amounts.push(Number(forecast.value?.forecastAmount || 0))
  customers.push(Number(forecast.value?.forecastCustomers || 0))

  forecastChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['合同金额', '新增客户数'] },
    grid: { left: 60, right: 60, bottom: 40, top: 40 },
    xAxis: { type: 'category', data: months },
    yAxis: [
      { type: 'value', name: '金额(元)', position: 'left' },
      { type: 'value', name: '客户数', position: 'right' }
    ],
    series: [
      {
        name: '合同金额',
        type: 'line',
        smooth: true,
        yAxisIndex: 0,
        data: amounts,
        itemStyle: { color: '#409EFF' },
        markPoint: {
          data: [{ type: 'max', name: '最大值' }]
        }
      },
      {
        name: '新增客户数',
        type: 'bar',
        yAxisIndex: 1,
        data: customers,
        itemStyle: { color: '#67C23A' }
      }
    ]
  })
  forecastChart.resize()
}

// ==================== 高流失风险客户 ====================
const churnRiskList = ref<any[]>([])
const churnLoading = ref(false)
const thresholdDays = ref(60)

async function loadChurnRisk() {
  churnLoading.value = true
  try {
    const res = await getChurnRiskCustomers(thresholdDays.value)
    churnRiskList.value = (res.data || []).map((item: any) => ({ ...item, retaining: false }))
  } catch (e) {
    console.error('加载流失风险客户失败', e)
  } finally {
    churnLoading.value = false
  }
}

async function handleRetain(row: any) {
  try {
    await ElMessageBox.confirm(
      `确认向 "${row.customerName}" 的负责人 ${row.ownerName || ''} 发送挽留提醒？`,
      '挽留提醒',
      { type: 'warning' }
    )
    row.retaining = true
    await triggerRetention(row.customerId)
    ElMessage.success('挽留提醒已发送')
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e?.message || '发送失败')
    }
  } finally {
    row.retaining = false
  }
}

// ==================== 工具函数 ====================
function levelText(level: number) {
  return { 1: '普通', 2: '重要', 3: 'VIP' }[level] || '未知'
}

function levelTagType(level: number) {
  return ({ 1: 'info', 2: 'warning', 3: 'danger' } as const)[level] || 'info'
}

function riskTagType(level: number) {
  return ({ 1: 'info', 2: 'warning', 3: 'danger' } as const)[level] || 'info'
}

function dayColor(days: number) {
  if (days >= 90) return '#F56C6C'
  if (days >= 60) return '#E6A23C'
  return '#909399'
}

// ==================== 生命周期 ====================
onMounted(() => {
  nextTick(() => {
    loadForecast()
    loadChurnRisk()
  })
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  forecastChart?.dispose()
})

function handleResize() {
  forecastChart?.resize()
}
</script>

<style scoped>
.chart-box {
  height: 350px;
  width: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.risk-filter-label {
  margin-right: 8px;
  color: #606266;
  font-size: 14px;
}

.forecast-summary {
  margin-top: 20px;
  padding: 16px 0;
  border-top: 1px solid #ebeef5;
}
</style>
