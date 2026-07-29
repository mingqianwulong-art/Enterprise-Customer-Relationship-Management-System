<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="线索名称">
          <el-input v-model="queryParams.clueName" placeholder="请输入线索名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="线索来源">
          <el-select v-model="queryParams.source" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in sourceOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="线索等级">
          <el-select v-model="queryParams.level" placeholder="请选择" clearable style="width: 160px">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 160px">
            <el-option label="待分配" :value="0" />
            <el-option label="已分配" :value="1" />
            <el-option label="已转化" :value="2" />
            <el-option label="已废弃" :value="3" />
          </el-select>
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增线索</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="线索名称" prop="clueName" min-width="140" show-overflow-tooltip />
        <el-table-column label="公司名称" prop="company" min-width="160" show-overflow-tooltip />
        <el-table-column label="联系电话" prop="phone" min-width="130" />
        <el-table-column label="来源" prop="source" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="sourceTagType(row.source)">{{ row.source || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="行业" prop="industry" min-width="120" show-overflow-tooltip />
        <el-table-column label="区域" prop="region" min-width="120" show-overflow-tooltip />
        <el-table-column label="等级" prop="level" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.level)">{{ levelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleAssign(row)">分配</el-button>
            <el-button link type="warning" @click="handleClaim(row)">抢单</el-button>
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
        <el-form-item label="线索名称" prop="clueName">
          <el-input v-model="form.clueName" placeholder="请输入线索名称" />
        </el-form-item>
        <el-form-item label="公司名称" prop="company">
          <el-input v-model="form.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="线索来源" prop="source">
          <el-select v-model="form.source" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in sourceOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属行业" prop="industry">
          <el-input v-model="form.industry" placeholder="请输入所属行业" />
        </el-form-item>
        <el-form-item label="所在区域" prop="region">
          <el-input v-model="form.region" placeholder="请输入所在区域" />
        </el-form-item>
        <el-form-item label="线索等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择" style="width: 100%">
            <el-option label="低" :value="1" />
            <el-option label="中" :value="2" />
            <el-option label="高" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="需求描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入需求描述" />
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
  getCluePage,
  addClue,
  updateClue,
  deleteClue,
  assignClue,
  claimClue
} from '@/api/market'

const sourceOptions = ['展会', '门店', '抖音', '微信', '官网', '其他']

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  clueName: '',
  source: '',
  level: undefined as number | undefined,
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  clueName: '',
  company: '',
  phone: '',
  email: '',
  source: '',
  industry: '',
  region: '',
  level: 1,
  description: ''
})
const rules: FormRules = {
  clueName: [{ required: true, message: '请输入线索名称', trigger: 'blur' }],
  source: [{ required: true, message: '请选择线索来源', trigger: 'change' }],
  level: [{ required: true, message: '请选择线索等级', trigger: 'change' }]
}

function levelText(level: number) {
  switch (level) {
    case 3: return '高'
    case 2: return '中'
    default: return '低'
  }
}

function levelTagType(level: number): 'info' | 'warning' | 'danger' {
  switch (level) {
    case 3: return 'danger'
    case 2: return 'warning'
    default: return 'info'
  }
}

function statusText(status: number) {
  switch (status) {
    case 1: return '已分配'
    case 2: return '已转化'
    case 3: return '已废弃'
    default: return '待分配'
  }
}

function statusTagType(status: number): 'info' | 'warning' | 'success' | 'danger' {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'danger'
    default: return 'info'
  }
}

function sourceTagType(source: string): 'primary' | 'success' | 'warning' | 'danger' | 'info' {
  switch (source) {
    case '展会': return 'primary'
    case '门店': return 'success'
    case '抖音': return 'warning'
    case '微信': return 'success'
    case '官网': return 'danger'
    default: return 'info'
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getCluePage(queryParams)
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
  queryParams.clueName = ''
  queryParams.source = ''
  queryParams.level = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.clueName = ''
  form.company = ''
  form.phone = ''
  form.email = ''
  form.source = ''
  form.industry = ''
  form.region = ''
  form.level = 1
  form.description = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增线索'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑线索'
  resetForm()
  form.id = row.id
  form.clueName = row.clueName
  form.company = row.company
  form.phone = row.phone
  form.email = row.email
  form.source = row.source
  form.industry = row.industry
  form.region = row.region
  form.level = row.level
  form.description = row.description
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateClue({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addClue({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

async function handleAssign(row: any) {
  try {
    const { value } = await ElMessageBox.prompt(`请输入要分配给的负责人ID`, '分配线索', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入用户ID（数字）',
      inputValidator: (val) => {
        if (val === null || val === undefined || val === '') return '请输入用户ID'
        if (!/^\d+$/.test(val)) return '用户ID必须为数字'
        return true
      }
    })
    await assignClue(row.id, Number(value))
    ElMessage.success('分配成功')
    loadData()
  } catch (e) {
    // 用户取消或请求错误
  }
}

async function handleClaim(row: any) {
  try {
    await ElMessageBox.confirm(`确认抢单【${row.clueName}】吗？抢单后该线索将归入您的名下。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await claimClue(row.id)
    ElMessage.success('抢单成功')
    loadData()
  } catch (e) {
    // 用户取消或请求错误
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除线索【${row.clueName}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteClue(row.id)
    ElMessage.success('删除成功')
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
