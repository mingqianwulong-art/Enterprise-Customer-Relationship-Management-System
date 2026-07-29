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
        <el-form-item label="客户等级">
          <el-select v-model="queryParams.customerLevel" placeholder="请选择" clearable style="width: 160px">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="VIP" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="所在区域">
          <el-input v-model="queryParams.region" placeholder="请输入所在区域" clearable @keyup.enter="handleQuery" />
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增客户</el-button>
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
        <el-table-column label="累计成交金额" prop="totalAmount" width="140" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.totalAmount || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleRelease(row)">退回公海</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="客户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="统一信用代码" prop="creditCode">
          <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
        <el-form-item label="所属行业" prop="industry">
          <el-input v-model="form.industry" placeholder="请输入所属行业" />
        </el-form-item>
        <el-form-item label="所在区域" prop="region">
          <el-input v-model="form.region" placeholder="请输入所在区域" />
        </el-form-item>
        <el-form-item label="客户等级" prop="customerLevel">
          <el-select v-model="form.customerLevel" placeholder="请选择" style="width: 100%">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="VIP" :value="3" />
          </el-select>
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
import { useRouter } from 'vue-router'
import {
  getCustomerPage,
  addCustomer,
  updateCustomer,
  deleteCustomer,
  releaseCustomer
} from '@/api/customer'

const router = useRouter()

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  industry: '',
  customerLevel: undefined as number | undefined,
  region: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  name: '',
  creditCode: '',
  industry: '',
  region: '',
  customerLevel: 1,
  remark: ''
})
const rules: FormRules = {
  name: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  customerLevel: [{ required: true, message: '请选择客户等级', trigger: 'change' }]
}

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
    const res: any = await getCustomerPage(queryParams)
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
  queryParams.customerLevel = undefined
  queryParams.region = ''
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.name = ''
  form.creditCode = ''
  form.industry = ''
  form.region = ''
  form.customerLevel = 1
  form.remark = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增客户'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑客户'
  resetForm()
  form.id = row.id
  form.name = row.name
  form.creditCode = row.creditCode
  form.industry = row.industry
  form.region = row.region
  form.customerLevel = row.customerLevel
  form.remark = row.remark
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateCustomer({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addCustomer({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

function handleDetail(row: any) {
  router.push(`/customer/detail/${row.id}`)
}

async function handleRelease(row: any) {
  try {
    await ElMessageBox.confirm(`确认将客户【${row.name}】退回公海吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await releaseCustomer(row.id)
    ElMessage.success('已退回公海')
    loadData()
  } catch (e) {
    // 用户取消或请求错误
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除客户【${row.name}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCustomer(row.id)
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
