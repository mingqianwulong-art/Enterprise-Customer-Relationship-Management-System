<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="操作用户">
          <el-input v-model="queryParams.username" placeholder="请输入操作用户" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="操作内容">
          <el-input v-model="queryParams.operation" placeholder="请输入操作内容" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="params-box">
              <div class="params-label">请求参数：</div>
              <pre class="params-content">{{ row.params || '无' }}</pre>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作用户" prop="username" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作内容" prop="operation" min-width="200" show-overflow-tooltip />
        <el-table-column label="请求方法" prop="method" min-width="280" show-overflow-tooltip />
        <el-table-column label="IP" prop="ip" width="140" />
        <el-table-column label="耗时" prop="costTime" width="110" align="right">
          <template #default="{ row }">
            {{ row.costTime ?? 0 }} ms
          </template>
        </el-table-column>
        <el-table-column label="操作时间" prop="createTime" min-width="170" />
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
import { Search, Refresh } from '@element-plus/icons-vue'
import { getLogPage } from '@/api/system'

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  operation: ''
})

async function loadData() {
  loading.value = true
  try {
    const res: any = await getLogPage(queryParams)
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
  queryParams.username = ''
  queryParams.operation = ''
  queryParams.pageNum = 1
  loadData()
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
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.params-box {
  padding: 12px 20px;
  background-color: #fafafa;
}
.params-label {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.params-content {
  margin: 0;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-all;
  color: #606266;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
}
</style>
