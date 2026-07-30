<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-label">总回款金额</div>
            <div class="stat-value">¥{{ Number(stat.totalAmount).toLocaleString() }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-label">已回款金额</div>
            <div class="stat-value" style="color: #67c23a">¥{{ Number(stat.paidAmount).toLocaleString() }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-label">待回款金额</div>
            <div class="stat-value" style="color: #e6a23c">¥{{ Number(stat.pendingAmount).toLocaleString() }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-label">逾期金额</div>
            <div class="stat-value" style="color: #f56c6c">¥{{ Number(stat.overdueAmount).toLocaleString() }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="合同编号">
          <el-input v-model="queryParams.contractNo" placeholder="请输入合同编号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="客户名称">
          <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable @keyup.enter="handleQuery" />
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增回款</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="回款编号" prop="paymentNo" min-width="140" show-overflow-tooltip />
        <el-table-column label="合同编号" prop="contractNo" min-width="140" show-overflow-tooltip />
        <el-table-column label="客户名称" prop="customerName" min-width="140" show-overflow-tooltip />
        <el-table-column label="回款阶段" prop="paymentStage" width="100" align="center" />
        <el-table-column label="计划回款日期" prop="planDate" min-width="120" />
        <el-table-column label="计划回款金额" prop="planAmount" width="140" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.planAmount || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="实际回款日期" prop="actualDate" min-width="120" />
        <el-table-column label="实际回款金额" prop="actualAmount" width="140" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.actualAmount || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleConfirm(row)">确认回款</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" :disabled="viewMode">
        <el-form-item label="回款编号" v-if="isEdit || viewMode">
          <el-input v-model="form.paymentNo" readonly />
        </el-form-item>
        <el-form-item label="回款编号" v-else>
          <el-input value="系统自动生成" readonly disabled style="background-color: #f5f7fa" />
        </el-form-item>
        <el-form-item label="合同ID" prop="contractId">
          <el-input-number v-model="form.contractId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="合同编号" prop="contractNo">
          <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
        </el-form-item>
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="回款阶段" prop="paymentStage">
          <el-select v-model="form.paymentStage" placeholder="请选择" style="width: 100%">
            <el-option label="首付款" value="首付款" />
            <el-option label="进度款" value="进度款" />
            <el-option label="尾款" value="尾款" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划回款日期" prop="planDate">
          <el-date-picker v-model="form.planDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择计划回款日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="计划回款金额" prop="planAmount">
          <el-input-number v-model="form.planAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
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

    <!-- 确认回款弹窗 -->
    <el-dialog v-model="confirmDialogVisible" title="确认回款" width="480px">
      <el-form ref="confirmFormRef" :model="confirmForm" :rules="confirmRules" label-width="110px">
        <el-form-item label="回款编号">
          <span>{{ currentRow?.paymentNo }}</span>
        </el-form-item>
        <el-form-item label="计划回款金额">
          <span>¥{{ Number(currentRow?.planAmount || 0).toLocaleString() }}</span>
        </el-form-item>
        <el-form-item label="实际回款金额" prop="actualAmount">
          <el-input-number v-model="confirmForm.actualAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际回款日期" prop="actualDate">
          <el-date-picker v-model="confirmForm.actualDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择实际回款日期" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConfirm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getPaymentPage,
  addPayment,
  updatePayment,
  deletePayment,
  confirmPayment
} from '@/api/business'

const statusOptions = [
  { value: 0, label: '待回款' },
  { value: 1, label: '部分回款' },
  { value: 2, label: '已回款' },
  { value: 3, label: '已逾期' }
]

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  contractNo: '',
  customerName: '',
  status: undefined as number | undefined
})

// 统计卡片（基于当前页数据汇总）
const stat = computed(() => {
  let totalAmount = 0
  let paidAmount = 0
  let pendingAmount = 0
  let overdueAmount = 0
  tableData.value.forEach((row) => {
    const plan = Number(row.planAmount || 0)
    const actual = Number(row.actualAmount || 0)
    totalAmount += plan
    paidAmount += actual
    if (row.status === 3) {
      overdueAmount += plan - actual
    } else if (row.status !== 2) {
      pendingAmount += plan - actual
    }
  })
  return { totalAmount, paidAmount, pendingAmount, overdueAmount }
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const viewMode = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  contractId: undefined as number | undefined,
  contractNo: '',
  customerId: undefined as number | undefined,
  customerName: '',
  paymentNo: '',
  planDate: '',
  planAmount: 0,
  paymentStage: '首付款',
  remark: ''
})
const rules: FormRules = {
  contractId: [{ required: true, message: '请输入合同ID', trigger: 'blur' }],
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  paymentStage: [{ required: true, message: '请选择回款阶段', trigger: 'change' }],
  planDate: [{ required: true, message: '请选择计划回款日期', trigger: 'change' }],
  planAmount: [{ required: true, message: '请输入计划回款金额', trigger: 'blur' }]
}

// 确认回款
const confirmDialogVisible = ref(false)
const confirmFormRef = ref<FormInstance>()
const currentRow = ref<any>(null)
const confirmForm = reactive({
  actualAmount: 0,
  actualDate: ''
})
const confirmRules: FormRules = {
  actualAmount: [{ required: true, message: '请输入实际回款金额', trigger: 'blur' }],
  actualDate: [{ required: true, message: '请选择实际回款日期', trigger: 'change' }]
}

function statusText(status: number) {
  return statusOptions.find((item) => item.value === status)?.label || '-'
}

function statusTagType(status: number): 'info' | 'warning' | 'success' | 'danger' {
  switch (status) {
    case 0:
      return 'info'
    case 1:
      return 'warning'
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
    const res: any = await getPaymentPage(queryParams)
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
  queryParams.contractNo = ''
  queryParams.customerName = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.contractId = undefined
  form.contractNo = ''
  form.customerId = undefined
  form.customerName = ''
  form.paymentNo = ''
  form.planDate = ''
  form.planAmount = 0
  form.paymentStage = '首付款'
  form.remark = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  viewMode.value = false
  dialogTitle.value = '新增回款'
  resetForm()
  dialogVisible.value = true
}

function fillForm(row: any) {
  Object.assign(form, {
    id: row.id,
    contractId: row.contractId,
    contractNo: row.contractNo,
    customerId: row.customerId,
    customerName: row.customerName,
    paymentNo: row.paymentNo,
    planDate: row.planDate,
    planAmount: row.planAmount,
    paymentStage: row.paymentStage,
    remark: row.remark
  })
}

function handleEdit(row: any) {
  isEdit.value = true
  viewMode.value = false
  dialogTitle.value = '编辑回款'
  resetForm()
  fillForm(row)
  dialogVisible.value = true
}

function handleView(row: any) {
  isEdit.value = true
  viewMode.value = true
  dialogTitle.value = '回款详情'
  resetForm()
  fillForm(row)
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updatePayment({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addPayment({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

function handleConfirm(row: any) {
  currentRow.value = row
  confirmForm.actualAmount = Number(row.planAmount || 0)
  confirmForm.actualDate = ''
  confirmDialogVisible.value = true
}

async function submitConfirm() {
  if (!confirmFormRef.value) return
  await confirmFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await confirmPayment(currentRow.value.id, confirmForm.actualAmount, confirmForm.actualDate)
      ElMessage.success('确认回款成功')
      confirmDialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除回款记录【${row.paymentNo}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePayment(row.id)
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
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  text-align: center;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
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
