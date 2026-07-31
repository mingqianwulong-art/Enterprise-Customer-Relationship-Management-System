<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="客户名称">
          <el-input v-model="queryParams.customerName" placeholder="请输入客户名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="记录类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增记录</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="记录标题" prop="title" min-width="160" show-overflow-tooltip />
        <el-table-column label="客户名称" prop="customerName" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联工单编号" prop="orderNo" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.orderNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="处理人" prop="handlerName" width="120" show-overflow-tooltip />
        <el-table-column label="处理时间" prop="handleTime" min-width="160" />
        <el-table-column label="处理结果" prop="result" min-width="160" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="关联工单ID">
          <el-input-number v-model="form.orderId" :min="1" controls-position="right" style="width: 100%" placeholder="可选" />
        </el-form-item>
        <el-form-item label="关联工单编号">
          <el-input v-model="form.orderNo" placeholder="可选，请输入关联工单编号" />
        </el-form-item>
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="记录标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入记录标题" />
        </el-form-item>
        <el-form-item label="记录内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入记录内容" />
        </el-form-item>
        <el-form-item label="处理结果" prop="result">
          <el-input v-model="form.result" type="textarea" :rows="2" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="处理人姓名" prop="handlerName">
          <el-input v-model="form.handlerName" placeholder="请输入处理人姓名" />
        </el-form-item>
        <el-form-item label="处理时间" prop="handleTime">
          <el-date-picker
            v-model="form.handleTime"
            type="datetime"
            format="YYYY年MM月DD日 HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择处理时间"
            style="width: 100%"
          />
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
  getServiceRecordPage,
  addServiceRecord,
  updateServiceRecord,
  deleteServiceRecord
} from '@/api/service'

const typeOptions = [
  { value: 1, label: '保修' },
  { value: 2, label: '安装' },
  { value: 3, label: '退换货' },
  { value: 4, label: '维修' },
  { value: 5, label: '咨询' },
  { value: 6, label: '其他' }
]

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  customerName: '',
  type: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  orderId: undefined as number | undefined,
  orderNo: '',
  customerId: undefined as number | undefined,
  customerName: '',
  type: 1,
  title: '',
  content: '',
  result: '',
  handlerName: '',
  handleTime: ''
})
const rules: FormRules = {
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入记录标题', trigger: 'blur' }]
}

function typeText(type?: number) {
  if (type === undefined || type === null) return '-'
  return typeOptions.find((item) => item.value === type)?.label || '-'
}

function typeTagType(type?: number): 'info' | 'success' | 'warning' | 'danger' {
  switch (type) {
    case 2:
      return 'success' // 安装
    case 3:
      return 'danger' // 退换货
    case 4:
      return 'warning' // 维修
    case 1:
      return 'warning' // 保修
    default:
      return 'info' // 咨询、其他
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getServiceRecordPage(queryParams)
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
  queryParams.customerName = ''
  queryParams.type = undefined
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.orderId = undefined
  form.orderNo = ''
  form.customerId = undefined
  form.customerName = ''
  form.type = 1
  form.title = ''
  form.content = ''
  form.result = ''
  form.handlerName = ''
  form.handleTime = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增记录'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑记录'
  resetForm()
  form.id = row.id
  form.orderId = row.orderId
  form.orderNo = row.orderNo
  form.customerId = row.customerId
  form.customerName = row.customerName
  form.type = row.type
  form.title = row.title
  form.content = row.content
  form.result = row.result
  form.handlerName = row.handlerName
  form.handleTime = row.handleTime
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateServiceRecord({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addServiceRecord({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除记录【${row.title}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteServiceRecord(row.id)
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
