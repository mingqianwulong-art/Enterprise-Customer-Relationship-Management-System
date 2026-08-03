<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $route.meta.title }}</h2>
      <span class="page-subtitle">按条件筛选并导出业务数据</span>
    </div>

    <!-- 筛选区 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="queryForm.startDate"
            type="date"
            placeholder="选择开始日期"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            style="width: 160px"
            @change="onStartDateChange"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="queryForm.endDate"
            type="date"
            placeholder="选择结束日期"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            style="width: 160px"
            :disabled-date="disabledEndDate"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 报表切换 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <el-tabs v-model="activeTab" @tab-change="handleQuery">
          <el-tab-pane label="客户报表" name="customer" />
          <el-tab-pane label="销售报表" name="sales" />
        </el-tabs>
      </template>

      <!-- 客户报表 -->
      <el-table v-if="activeTab === 'customer'" :data="tableData" border stripe v-loading="loading" empty-text="暂无数据">
        <el-table-column prop="customerName" label="客户名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="industry" label="所属行业" min-width="120" />
        <el-table-column prop="region" label="所在区域" min-width="100" />
        <el-table-column prop="customerLevel" label="客户等级" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.customerLevel === 'VIP'" type="danger" size="small">VIP</el-tag>
            <el-tag v-else-if="row.customerLevel === '重要'" type="warning" size="small">重要</el-tag>
            <el-tag v-else type="info" size="small">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="opportunityCount" label="关联商机" width="100" align="center" />
        <el-table-column prop="totalAmount" label="成交金额" min-width="130" align="right">
          <template #default="{ row }">
            ¥ {{ formatAmount(row.totalAmount) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 销售报表 -->
      <el-table v-if="activeTab === 'sales'" :data="tableData" border stripe v-loading="loading" empty-text="暂无数据">
        <el-table-column prop="realName" label="销售人员" width="120" />
        <el-table-column prop="opportunityCount" label="商机数" width="100" align="center" />
        <el-table-column prop="wonCount" label="赢单数" width="100" align="center" />
        <el-table-column prop="winRate" label="赢单率" width="100" align="center">
          <template #default="{ row }">
            {{ row.winRate }}%
          </template>
        </el-table-column>
        <el-table-column prop="contractCount" label="合同数" width="100" align="center" />
        <el-table-column prop="contractAmount" label="合同金额" min-width="140" align="right">
          <template #default="{ row }">
            ¥ {{ formatAmount(row.contractAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="receivedAmount" label="已回款金额" min-width="140" align="right">
          <template #default="{ row }">
            ¥ {{ formatAmount(row.receivedAmount) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getCustomCustomerReport, getCustomSalesReport } from '@/api/report'

const loading = ref(false)
const activeTab = ref('customer')
const tableData = ref<any[]>([])

const queryForm = ref({
  startDate: '',
  endDate: '',
  ownerId: null as number | null
})

function formatAmount(val: any): string {
  if (val === null || val === undefined) return '0'
  const n = Number(val)
  if (n >= 10000) {
    return (n / 10000).toFixed(1) + '万'
  }
  return n.toLocaleString()
}

async function handleQuery() {
  loading.value = true
  try {
    const params: any = {}
    if (queryForm.value.startDate) params.startDate = queryForm.value.startDate
    if (queryForm.value.endDate) params.endDate = queryForm.value.endDate

    if (activeTab.value === 'customer') {
      const res = await getCustomCustomerReport(params)
      tableData.value = res.data || []
    } else {
      const res = await getCustomSalesReport(params)
      tableData.value = res.data || []
    }
  } catch (e) {
    console.error('查询报表失败', e)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.value = { startDate: '', endDate: '', ownerId: null }
  handleQuery()
}

// 开始日期变化时清空结束日期，避免结束日期早于开始日期
function onStartDateChange() {
  queryForm.value.endDate = ''
}

// 结束日期不能早于开始日期
function disabledEndDate(time: Date) {
  if (!queryForm.value.startDate) return false
  const start = new Date(queryForm.value.startDate)
  start.setHours(0, 0, 0, 0)
  return time.getTime() < start.getTime()
}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
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

.filter-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.filter-card :deep(.el-card__body) {
  padding: 16px 20px 0;
}

.filter-card .el-form-item {
  margin-bottom: 16px;
}

.table-card {
  border-radius: 8px;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.table-card :deep(.el-card__header) {
  padding: 0 20px;
  border-bottom: none;
}

.table-card :deep(.el-tabs__header) {
  margin-bottom: 0;
}
</style>
