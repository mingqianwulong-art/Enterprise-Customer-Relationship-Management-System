<template>
  <div class="app-container">
    <!-- 停滞预警提示 -->
    <el-alert
      v-if="stagnantCount > 0"
      class="stagnant-alert"
      type="warning"
      show-icon
      :closable="false"
    >
      <template #title>
        发现 {{ stagnantCount }} 个停滞商机（超过15天未推进阶段），请及时跟进！
        <el-button link type="primary" @click="showStagnantDialog = true">查看详情</el-button>
      </template>
    </el-alert>

    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="商机名称">
          <el-input v-model="queryParams.oppName" placeholder="请输入商机名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="商机阶段">
          <el-select v-model="queryParams.stage" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in stageOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增商机</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="商机名称" prop="oppName" min-width="160" show-overflow-tooltip />
        <el-table-column label="客户名称" prop="customerName" min-width="140" show-overflow-tooltip />
        <el-table-column label="预计成交金额" prop="estimatedAmount" width="140" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.estimatedAmount || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="商机阶段" prop="stage" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="stageTagType(row.stage)">{{ stageText(row.stage) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成交概率" prop="probability" width="100" align="center">
          <template #default="{ row }">
            {{ row.probability ?? 0 }}%
          </template>
        </el-table-column>
        <el-table-column label="预计成交日期" prop="expectedCloseDate" min-width="120" />
        <el-table-column label="来源" prop="source" width="100" align="center" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleStagePromote(row)">阶段推进</el-button>
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
        <el-form-item label="商机名称" prop="oppName">
          <el-input v-model="form.oppName" placeholder="请输入商机名称" />
        </el-form-item>
        <el-form-item label="客户ID" prop="customerId">
          <el-input-number v-model="form.customerId" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="预计成交金额" prop="estimatedAmount">
          <el-input-number v-model="form.estimatedAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商机阶段" prop="stage">
          <el-select v-model="form.stage" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in stageOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="成交概率" prop="probability">
          <el-input-number v-model="form.probability" :min="0" :max="100" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计成交日期" prop="expectedCloseDate">
          <el-date-picker
            v-model="form.expectedCloseDate"
            type="date"
            format="YYYY年MM月DD日"
            value-format="YYYY-MM-DD"
            placeholder="请选择日期"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" placeholder="请输入来源" />
        </el-form-item>
        <el-form-item label="商机描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商机描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 阶段推进弹窗 -->
    <el-dialog v-model="stageDialogVisible" title="阶段推进" width="480px">
      <el-form label-width="100px">
        <el-form-item label="商机名称">
          <span>{{ currentOpp?.oppName }}</span>
        </el-form-item>
        <el-form-item label="当前阶段">
          <el-tag :type="stageTagType(currentOpp?.stage)">{{ stageText(currentOpp?.stage) }}</el-tag>
        </el-form-item>
        <el-form-item label="推进至" v-if="nextStageOptions.length">
          <el-radio-group v-model="selectedStage">
            <el-radio v-for="item in nextStageOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!nextStageOptions.length">
          <el-alert type="info" :closable="false" title="当前阶段已无可推进的下一阶段" show-icon />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stageDialogVisible = false">取消</el-button>
        <el-button type="primary" :disabled="!nextStageOptions.length" @click="submitStage">确定</el-button>
      </template>
    </el-dialog>

    <!-- 停滞预警详情弹窗 -->
    <el-dialog v-model="showStagnantDialog" title="停滞商机预警" width="800px">
      <el-table :data="stagnantList" border max-height="400">
        <el-table-column label="商机名称" prop="oppName" min-width="160" show-overflow-tooltip />
        <el-table-column label="客户名称" prop="customerName" min-width="140" show-overflow-tooltip />
        <el-table-column label="阶段" prop="stage" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="stageTagType(row.stage)">{{ stageText(row.stage) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="阶段变更时间" prop="stageChangeTime" min-width="160" />
        <el-table-column label="预计金额" prop="estimatedAmount" width="120" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.estimatedAmount || 0).toLocaleString() }}
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="showStagnantDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import {
  getOpportunityPage,
  addOpportunity,
  updateOpportunity,
  deleteOpportunity,
  changeOppStage,
  getStagnantOpportunities
} from '@/api/business'

const stageOptions = [
  { value: 1, label: '需求确认' },
  { value: 2, label: '方案报价' },
  { value: 3, label: '商务谈判' },
  { value: 4, label: '合同签订' },
  { value: 5, label: '已赢单' },
  { value: 6, label: '已输单' }
]

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  oppName: '',
  stage: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  oppName: '',
  customerId: undefined as number | undefined,
  customerName: '',
  contactId: undefined as number | undefined,
  estimatedAmount: 0,
  stage: 1,
  probability: 0,
  expectedCloseDate: '',
  ownerId: undefined as number | undefined,
  source: '',
  description: ''
})
const rules: FormRules = {
  oppName: [{ required: true, message: '请输入商机名称', trigger: 'blur' }],
  customerId: [{ required: true, message: '请输入客户ID', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  stage: [{ required: true, message: '请选择商机阶段', trigger: 'change' }]
}

// 禁用今天之前的日期
function disabledDate(time: Date) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime()
}

// 阶段推进
const stageDialogVisible = ref(false)
const currentOpp = ref<any>(null)
const selectedStage = ref<number>(1)
const nextStageOptions = computed(() => {
  const cur = currentOpp.value?.stage
  if (!cur || cur >= 5) return []
  return stageOptions.filter((item) => item.value > cur)
})

function stageText(stage?: number) {
  if (stage === undefined || stage === null) return '-'
  return stageOptions.find((item) => item.value === stage)?.label || '-'
}

function stageTagType(stage?: number): 'info' | 'warning' | 'danger' | 'success' {
  switch (stage) {
    case 2:
    case 3:
      return 'warning'
    case 4:
      return 'danger'
    case 5:
      return 'success'
    default:
      return 'info'
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getOpportunityPage(queryParams)
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
  queryParams.oppName = ''
  queryParams.stage = undefined
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.oppName = ''
  form.customerId = undefined
  form.customerName = ''
  form.contactId = undefined
  form.estimatedAmount = 0
  form.stage = 1
  form.probability = 0
  form.expectedCloseDate = ''
  form.ownerId = undefined
  form.source = ''
  form.description = ''
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增商机'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑商机'
  resetForm()
  form.id = row.id
  form.oppName = row.oppName
  form.customerId = row.customerId
  form.customerName = row.customerName
  form.contactId = row.contactId
  form.estimatedAmount = row.estimatedAmount
  form.stage = row.stage
  form.probability = row.probability
  form.expectedCloseDate = row.expectedCloseDate
  form.ownerId = row.ownerId
  form.source = row.source
  form.description = row.description
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateOpportunity({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addOpportunity({ ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由请求拦截器统一提示
    }
  })
}

function handleStagePromote(row: any) {
  currentOpp.value = row
  const next = stageOptions.find((item) => item.value > row.stage)
  selectedStage.value = next ? next.value : row.stage
  stageDialogVisible.value = true
}

async function submitStage() {
  if (!currentOpp.value) return
  try {
    await changeOppStage(currentOpp.value.id, selectedStage.value)
    ElMessage.success('阶段推进成功')
    stageDialogVisible.value = false
    loadData()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确认删除商机【${row.oppName}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteOpportunity(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消或请求错误
  }
}

// 停滞预警
const stagnantList = ref<any[]>([])
const stagnantCount = computed(() => stagnantList.value.length)
const showStagnantDialog = ref(false)

async function loadStagnant() {
  try {
    const res: any = await getStagnantOpportunities(15)
    stagnantList.value = res.data || []
  } catch (e) {
    // 忽略预警加载错误
  }
}

onMounted(() => {
  loadData()
  loadStagnant()
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 16px;
}
.stagnant-alert {
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
