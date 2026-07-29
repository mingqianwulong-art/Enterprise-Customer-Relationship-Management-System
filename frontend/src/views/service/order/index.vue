<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="工单编号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入工单编号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="queryParams.priority" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增工单</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="工单编号" prop="orderNo" min-width="140" show-overflow-tooltip />
        <el-table-column label="客户名称" prop="customerName" min-width="140" show-overflow-tooltip />
        <el-table-column label="工单标题" prop="title" min-width="160" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" prop="source" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ sourceText(row.source) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优先级" prop="priority" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="priorityTagType(row.priority)">{{ priorityText(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理人" prop="assigneeName" width="120" show-overflow-tooltip />
        <el-table-column label="满意度" prop="satisfaction" width="160" align="center">
          <template #default="{ row }">
            <el-rate v-if="row.satisfaction" v-model="row.satisfaction" disabled />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" width="440" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleAssign(row)">分配</el-button>
            <el-button link type="warning" @click="handleStatusChange(row)">状态变更</el-button>
            <el-button link type="success" @click="handleSatisfaction(row)">满意度评价</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="联系人姓名" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入工单标题" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入问题描述" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-select v-model="form.source" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="form.priority" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配弹窗 -->
    <el-dialog v-model="assignDialogVisible" title="分配处理人" width="480px">
      <el-form label-width="100px">
        <el-form-item label="工单编号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="处理人ID">
          <el-input-number v-model="assignForm.assigneeId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="处理人姓名">
          <el-input v-model="assignForm.assigneeName" placeholder="请输入处理人姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态变更弹窗 -->
    <el-dialog v-model="statusDialogVisible" title="状态变更" width="480px">
      <el-form label-width="100px">
        <el-form-item label="工单编号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="statusTagType(currentOrder?.status)">{{ statusText(currentOrder?.status) }}</el-tag>
        </el-form-item>
        <el-form-item label="变更至">
          <el-select v-model="selectedStatus" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">确定</el-button>
      </template>
    </el-dialog>

    <!-- 满意度评价弹窗 -->
    <el-dialog v-model="satisfactionDialogVisible" title="满意度评价" width="480px">
      <el-form label-width="100px">
        <el-form-item label="工单编号">
          <span>{{ currentOrder?.orderNo }}</span>
        </el-form-item>
        <el-form-item label="满意度评分">
          <el-rate v-model="satisfactionForm.satisfaction" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="satisfactionForm.satisfactionComment" type="textarea" :rows="3" placeholder="请输入评价内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="satisfactionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSatisfaction">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getServiceOrderPage,
  addServiceOrder,
  updateServiceOrder,
  deleteServiceOrder,
  assignServiceOrder,
  changeOrderStatus,
  addSatisfaction
} from '@/api/service'

const typeOptions = [
  { value: 1, label: '售后咨询' },
  { value: 2, label: '投诉' },
  { value: 3, label: '维修' },
  { value: 4, label: '安装' },
  { value: 5, label: '退换货' },
  { value: 6, label: '其他' }
]
const sourceOptions = [
  { value: 1, label: '电话' },
  { value: 2, label: '微信' },
  { value: 3, label: '门店' },
  { value: 4, label: '邮件' },
  { value: 5, label: '其他' }
]
const priorityOptions = [
  { value: 1, label: '紧急' },
  { value: 2, label: '普通' },
  { value: 3, label: '低' }
]
const statusOptions = [
  { value: 0, label: '待处理' },
  { value: 1, label: '处理中' },
  { value: 2, label: '待反馈' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已关闭' },
  { value: 5, label: '已取消' }
]

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  type: undefined as number | undefined,
  status: undefined as number | undefined,
  priority: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  customerId: undefined as number | undefined,
  customerName: '',
  contactName: '',
  title: '',
  description: '',
  type: 1,
  source: 1,
  priority: 2
})
const rules: FormRules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  source: [{ required: true, message: '请选择来源', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
}

// 分配处理人
const assignDialogVisible = ref(false)
const currentOrder = ref<any>(null)
const assignForm = reactive({
  assigneeId: undefined as number | undefined,
  assigneeName: ''
})

// 状态变更
const statusDialogVisible = ref(false)
const selectedStatus = ref<number>(0)

// 满意度评价
const satisfactionDialogVisible = ref(false)
const satisfactionForm = reactive({
  satisfaction: 0,
  satisfactionComment: ''
})

function typeText(type?: number) {
  if (type === undefined || type === null) return '-'
  return typeOptions.find((item) => item.value === type)?.label || '-'
}

function typeTagType(type?: number): 'info' | 'success' | 'warning' | 'danger' {
  switch (type) {
    case 2:
      return 'danger' // 投诉
    case 3:
      return 'warning' // 维修
    case 4:
      return 'success' // 安装
    case 5:
      return 'warning' // 退换货
    default:
      return 'info' // 售后咨询、其他
  }
}

function sourceText(source?: number) {
  if (source === undefined || source === null) return '-'
  return sourceOptions.find((item) => item.value === source)?.label || '-'
}

function priorityText(priority?: number) {
  if (priority === undefined || priority === null) return '-'
  return priorityOptions.find((item) => item.value === priority)?.label || '-'
}

function priorityTagType(priority?: number): 'info' | 'warning' | 'danger' {
  switch (priority) {
    case 1:
      return 'danger' // 紧急
    case 2:
      return 'warning' // 普通
    default:
      return 'info' // 低
  }
}

function statusText(status?: number) {
  if (status === undefined || status === null) return '-'
  return statusOptions.find((item) => item.value === status)?.label || '-'
}

function statusTagType(status?: number): 'info' | 'warning' | 'success' | 'danger' {
  switch (status) {
    case 0:
      return 'info' // 待处理
    case 1:
      return 'warning' // 处理中
    case 2:
      return 'warning' // 待反馈
    case 3:
      return 'success' // 已完成
    case 4:
      return 'info' // 已关闭
    case 5:
      return 'danger' // 已取消
    default:
      return 'info'
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getServiceOrderPage(queryParams)
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
  queryParams.orderNo = ''
  queryParams.type = undefined
  queryParams.status = undefined
  queryParams.priority = undefined
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.customerId = undefined
  form.customerName = ''
  form.contactName = ''
  form.title = ''
  form.description = ''
  form.type = 1
  form.source = 1
  form.priority = 2
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增工单'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑工单'
  resetForm()
  form.id = row.id
  form.customerId = row.customerId
  form.customerName = row.customerName
  form.contactName = row.contactName
  form.title = row.title
  form.description = row.description
  form.type = row.type
  form.source = row.source
  form.priority = row.priority
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateServiceOrder({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addServiceOrder({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

function handleAssign(row: any) {
  currentOrder.value = row
  assignForm.assigneeId = row.assigneeId ?? undefined
  assignForm.assigneeName = row.assigneeName ?? ''
  assignDialogVisible.value = true
}

async function submitAssign() {
  if (!currentOrder.value) return
  if (!assignForm.assigneeId) {
    ElMessage.warning('请输入处理人ID')
    return
  }
  if (!assignForm.assigneeName) {
    ElMessage.warning('请输入处理人姓名')
    return
  }
  try {
    await assignServiceOrder(currentOrder.value.id, assignForm.assigneeId, assignForm.assigneeName)
    ElMessage.success('分配成功')
    assignDialogVisible.value = false
    loadData()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

function handleStatusChange(row: any) {
  currentOrder.value = row
  selectedStatus.value = row.status ?? 0
  statusDialogVisible.value = true
}

async function submitStatus() {
  if (!currentOrder.value) return
  try {
    await changeOrderStatus(currentOrder.value.id, selectedStatus.value)
    ElMessage.success('状态变更成功')
    statusDialogVisible.value = false
    loadData()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

function handleSatisfaction(row: any) {
  currentOrder.value = row
  satisfactionForm.satisfaction = row.satisfaction ?? 0
  satisfactionForm.satisfactionComment = row.satisfactionComment ?? ''
  satisfactionDialogVisible.value = true
}

async function submitSatisfaction() {
  if (!currentOrder.value) return
  if (!satisfactionForm.satisfaction) {
    ElMessage.warning('请选择满意度评分')
    return
  }
  try {
    await addSatisfaction(
      currentOrder.value.id,
      satisfactionForm.satisfaction,
      satisfactionForm.satisfactionComment
    )
    ElMessage.success('评价成功')
    satisfactionDialogVisible.value = false
    loadData()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除工单【${row.orderNo}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteServiceOrder(row.id)
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
