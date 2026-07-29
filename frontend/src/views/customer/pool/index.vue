<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="客户名称">
          <el-input v-model="queryParams.name" placeholder="请输入客户名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="所属行业">
          <el-input v-model="queryParams.industry" placeholder="请输入所属行业" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>公海池</span>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="客户名称" prop="name" min-width="160" show-overflow-tooltip />
        <el-table-column label="统一信用代码" prop="creditCode" min-width="180" show-overflow-tooltip />
        <el-table-column label="所属行业" prop="industry" min-width="120" show-overflow-tooltip />
        <el-table-column label="所在区域" prop="region" min-width="120" show-overflow-tooltip />
        <el-table-column label="客户等级" prop="customerLevel" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.customerLevel)">{{ levelText(row.customerLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后跟进时间" prop="lastFollowTime" min-width="160" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleClaim(row)">领取</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getPoolPage, claimCustomer } from '@/api/customer'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  industry: ''
})

function levelText(level: number) {
  switch (level) {
    case 3: return 'VIP'
    case 2: return '重要'
    default: return '普通'
  }
}

function levelTagType(level: number): 'info' | 'warning' | 'danger' {
  switch (level) {
    case 3: return 'danger'
    case 2: return 'warning'
    default: return 'info'
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getPoolPage(queryParams)
    tableData.value = res.data?.records || res.data?.list || res.rows || []
    total.value = res.data?.total ?? res.total ?? 0
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  loadData()
}

function resetQuery() {
  queryParams.name = ''
  queryParams.industry = ''
  queryParams.pageNum = 1
  loadData()
}

async function handleClaim(row: any) {
  try {
    await ElMessageBox.confirm(`确认领取客户【${row.name}】吗？领取后该客户将归入您的名下。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await claimCustomer(row.id)
    ElMessage.success('领取成功')
    loadData()
  } catch (e) {
    // 用户取消或请求错误
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
