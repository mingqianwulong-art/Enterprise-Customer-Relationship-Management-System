<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="合同名称">
          <el-input v-model="queryParams.contractName" placeholder="请输入合同名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增合同</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="合同编号" prop="contractNo" min-width="140" show-overflow-tooltip />
        <el-table-column label="合同名称" prop="contractName" min-width="160" show-overflow-tooltip />
        <el-table-column label="客户名称" prop="customerName" min-width="140" show-overflow-tooltip />
        <el-table-column label="合同金额" prop="amount" width="140" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.amount || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="签订日期" prop="signedDate" min-width="120" />
        <el-table-column label="合同期限" min-width="200">
          <template #default="{ row }">
            {{ row.startDate || '-' }} ~ {{ row.endDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审批时间" prop="approveTime" min-width="160" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleApprove(row)">审批</el-button>
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

    <!-- 新增/编辑/查看弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="viewMode">
        <el-form-item v-if="isEdit" label="合同编号">
          <el-input v-model="form.contractNo" readonly />
        </el-form-item>
        <el-form-item v-else label="合同编号">
          <el-input value="系统自动生成" readonly disabled style="background-color: #f5f7fa" />
        </el-form-item>
        <el-form-item label="合同名称" prop="contractName">
          <el-input v-model="form.contractName" placeholder="请输入合同名称" />
        </el-form-item>
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="关联商机ID" prop="oppId">
          <el-input-number v-model="form.oppId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="合同金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="签订日期" prop="signedDate">
          <el-date-picker v-model="form.signedDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择签订日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择开始日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择结束日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ viewMode ? '关闭' : '取消' }}</el-button>
        <el-button v-if="!viewMode" type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getContractPage,
  addContract,
  updateContract,
  deleteContract,
  approveContract
} from '@/api/business'

const statusOptions = [
  { value: 0, label: '待审批' },
  { value: 1, label: '已审批' },
  { value: 2, label: '已签订' },
  { value: 3, label: '已作废' }
]

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  contractName: '',
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const viewMode = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  contractNo: '',
  contractName: '',
  customerId: undefined as number | undefined,
  customerName: '',
  oppId: undefined as number | undefined,
  amount: 0,
  signedDate: '',
  startDate: '',
  endDate: '',
  status: 0,
  approverId: undefined as number | undefined,
  ownerId: undefined as number | undefined,
  remark: ''
})
const rules: FormRules = {
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入合同金额', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

function statusText(status: number) {
  return statusOptions.find((item) => item.value === status)?.label || '-'
}

function statusTagType(status: number): 'info' | 'warning' | 'success' | 'danger' {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'info'
    case 2:
      return 'success'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getContractPage(queryParams)
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
  queryParams.contractName = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.contractNo = ''
  form.contractName = ''
  form.customerId = undefined
  form.customerName = ''
  form.oppId = undefined
  form.amount = 0
  form.signedDate = ''
  form.startDate = ''
  form.endDate = ''
  form.status = 0
  form.approverId = undefined
  form.ownerId = undefined
  form.remark = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  viewMode.value = false
  dialogTitle.value = '新增合同'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  viewMode.value = false
  dialogTitle.value = '编辑合同'
  resetForm()
  Object.assign(form, {
    id: row.id,
    contractNo: row.contractNo,
    contractName: row.contractName,
    customerId: row.customerId,
    customerName: row.customerName,
    oppId: row.oppId,
    amount: row.amount,
    signedDate: row.signedDate,
    startDate: row.startDate,
    endDate: row.endDate,
    status: row.status,
    approverId: row.approverId,
    ownerId: row.ownerId,
    remark: row.remark
  })
  dialogVisible.value = true
}

function handleView(row: any) {
  isEdit.value = true
  viewMode.value = true
  dialogTitle.value = '合同详情'
  resetForm()
  Object.assign(form, {
    id: row.id,
    contractNo: row.contractNo,
    contractName: row.contractName,
    customerId: row.customerId,
    customerName: row.customerName,
    oppId: row.oppId,
    amount: row.amount,
    signedDate: row.signedDate,
    startDate: row.startDate,
    endDate: row.endDate,
    status: row.status,
    approverId: row.approverId,
    ownerId: row.ownerId,
    remark: row.remark
  })
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateContract({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addContract({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

async function handleApprove(row: any) {
  try {
    const { value } = await ElMessageBox.prompt(`请输入审批人ID，确认审批合同【${row.contractName}】`, '合同审批', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入审批人ID（数字）',
      inputValidator: (val) => {
        if (val === null || val === undefined || val === '') return '请输入审批人ID'
        if (!/^\d+$/.test(val)) return '审批人ID必须为数字'
        return true
      }
    })
    await approveContract(row.id, Number(value))
    ElMessage.success('审批成功')
    loadData()
  } catch (e) {
    // 用户取消或请求错误
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除合同【${row.contractName}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteContract(row.id)
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
