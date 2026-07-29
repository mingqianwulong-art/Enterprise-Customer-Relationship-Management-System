<template>
  <div class="app-container">
    <!-- 渠道效果统计卡片 -->
    <el-card class="stats-card">
      <template #header>
        <div class="card-header"><span>渠道效果统计</span></div>
      </template>
      <el-row :gutter="16" v-loading="statsLoading">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in statsData" :key="item.channelId || item.id">
          <el-card class="stat-item" shadow="hover">
            <div class="stat-title">{{ item.channelName }}</div>
            <div class="stat-row">
              <span class="stat-label">线索数：</span>
              <span class="stat-value">{{ item.clueCount ?? 0 }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">获客成本：</span>
              <span class="stat-value">¥{{ Number(item.cost ?? 0).toLocaleString() }}</span>
            </div>
            <div class="stat-row">
              <span class="stat-label">转化率：</span>
              <span class="stat-value">{{ formatRate(item.conversionRate) }}</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="24" v-if="!statsLoading && statsData.length === 0">
          <el-empty description="暂无统计数据" />
        </el-col>
      </el-row>
    </el-card>

    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="渠道名称">
          <el-input v-model="queryParams.channelName" placeholder="请输入渠道名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
          <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增渠道</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="渠道名称" prop="channelName" min-width="160" show-overflow-tooltip />
        <el-table-column label="渠道类型" prop="channelType" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.channelType)">{{ row.channelType || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="联系人" prop="contact" min-width="120" show-overflow-tooltip />
        <el-table-column label="联系电话" prop="phone" min-width="130" />
        <el-table-column label="获客成本" prop="cost" width="130" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.cost || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="渠道名称" prop="channelName">
          <el-input v-model="form.channelName" placeholder="请输入渠道名称" />
        </el-form-item>
        <el-form-item label="渠道类型" prop="channelType">
          <el-select v-model="form.channelType" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="获客成本" prop="cost">
          <el-input-number v-model="form.cost" :min="0" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getChannelPage,
  addChannel,
  updateChannel,
  deleteChannel,
  getChannelStats
} from '@/api/market'

const typeOptions = ['线上', '线下', '展会', '社交']

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  channelName: ''
})

const statsLoading = ref(false)
const statsData = ref<any[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  channelName: '',
  channelType: '',
  contact: '',
  phone: '',
  cost: 0,
  status: 1,
  remark: ''
})
const rules: FormRules = {
  channelName: [{ required: true, message: '请输入渠道名称', trigger: 'blur' }],
  channelType: [{ required: true, message: '请选择渠道类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function typeTagType(type: string): 'primary' | 'success' | 'warning' | 'info' {
  switch (type) {
    case '线上': return 'primary'
    case '线下': return 'success'
    case '展会': return 'warning'
    case '社交': return 'info'
    default: return 'info'
  }
}

function formatRate(rate: number | undefined | null) {
  const num = Number(rate ?? 0)
  if (Number.isNaN(num)) return '0%'
  return `${(num * 100).toFixed(1)}%`
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getChannelPage(queryParams)
    tableData.value = res.data?.records || res.data?.list || res.rows || []
    total.value = res.data?.total ?? res.total ?? 0
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
}

async function loadStats() {
  statsLoading.value = true
  try {
    const res: any = await getChannelStats()
    statsData.value = res.data?.records || res.data?.list || res.data || res.rows || []
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    statsLoading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  loadData()
}

function resetQuery() {
  queryParams.channelName = ''
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.channelName = ''
  form.channelType = ''
  form.contact = ''
  form.phone = ''
  form.cost = 0
  form.status = 1
  form.remark = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增渠道'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑渠道'
  resetForm()
  form.id = row.id
  form.channelName = row.channelName
  form.channelType = row.channelType
  form.contact = row.contact
  form.phone = row.phone
  form.cost = row.cost
  form.status = row.status
  form.remark = row.remark
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateChannel({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addChannel({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
      loadStats()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

async function handleStatusChange(row: any, val: number | boolean) {
  const newVal = Number(val)
  try {
    await updateChannel({ ...row, status: newVal })
    row.status = newVal
    ElMessage.success(newVal === 1 ? '已启用' : '已停用')
    loadStats()
  } catch (e) {
    // 失败时回退由表格重新加载保证一致性
    loadData()
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除渠道【${row.channelName}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteChannel(row.id)
    ElMessage.success('删除成功')
    loadData()
    loadStats()
  } catch (e) {
    // 用户取消或请求错误
  }
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.stats-card {
  margin-bottom: 16px;
}
.search-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.stat-item {
  margin-bottom: 16px;
}
.stat-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 10px;
}
.stat-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
  line-height: 24px;
}
.stat-value {
  color: #409eff;
  font-weight: 600;
}
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
