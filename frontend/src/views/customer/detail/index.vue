<template>
  <div class="app-container">
    <div class="detail-header">
      <el-button @click="goBack"><el-icon><ArrowLeft /></el-icon>返回</el-button>
      <span class="title">客户详情</span>
    </div>

    <el-row :gutter="16" v-loading="loading">
      <!-- 左侧侧边栏：客户基础信息 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span class="card-title">客户基础信息</span>
          </template>
          <div class="info-list">
            <div class="info-row">
              <span class="label">客户名称：</span>
              <span class="value">{{ customer.name || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">统一信用代码：</span>
              <span class="value">{{ customer.creditCode || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">所属行业：</span>
              <span class="value">{{ customer.industry || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">所在区域：</span>
              <span class="value">{{ customer.region || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">客户等级：</span>
              <span class="value">
                <el-tag :type="levelTagType(customer.customerLevel)">{{ levelText(customer.customerLevel) }}</el-tag>
              </span>
            </div>
            <div class="info-row">
              <span class="label">负责人：</span>
              <span class="value">{{ customer.ownerName || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">累计成交金额：</span>
              <span class="value amount">¥{{ Number(customer.totalAmount || 0).toLocaleString() }}</span>
            </div>
            <div class="info-row">
              <span class="label">最后跟进时间：</span>
              <span class="value">{{ customer.lastFollowTime || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">创建时间：</span>
              <span class="value">{{ customer.createTime || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">备注：</span>
              <span class="value">{{ customer.remark || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：分栏标签页 -->
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="跟进记录" name="follow">
              <el-table :data="followRecords" border style="width: 100%">
                <el-table-column label="跟进类型" prop="type" width="120" />
                <el-table-column label="跟进内容" prop="content" min-width="200" show-overflow-tooltip />
                <el-table-column label="操作人" prop="operatorName" width="120" />
                <el-table-column label="下次跟进时间" prop="nextFollowTime" width="170" />
                <el-table-column label="跟进时间" prop="createTime" width="170" />
              </el-table>
              <el-empty v-if="!followRecords.length" description="暂无跟进记录" />
            </el-tab-pane>

            <el-tab-pane label="联系人" name="contact">
              <el-table :data="contacts" border style="width: 100%">
                <el-table-column label="姓名" prop="name" width="120" />
                <el-table-column label="职位" prop="position" width="140" />
                <el-table-column label="手机号" prop="phone" width="150" />
                <el-table-column label="邮箱" prop="email" min-width="180" show-overflow-tooltip />
                <el-table-column label="主要联系人" prop="isPrimary" width="120" align="center">
                  <template #default="{ row }">
                    <el-tag type="success" v-if="row.isPrimary === 1">是</el-tag>
                    <el-tag type="info" v-else>否</el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!contacts.length" description="暂无联系人" />
            </el-tab-pane>

            <el-tab-pane label="商机列表" name="opportunity">
              <el-table :data="opportunities" border style="width: 100%" v-loading="tabLoading">
                <el-table-column label="商机名称" prop="oppName" min-width="180" show-overflow-tooltip />
                <el-table-column label="阶段" prop="stage" width="120" align="center">
                  <template #default="{ row }">
                    <el-tag :type="oppStageTagType(row.stage)">{{ oppStageText(row.stage) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="预计金额" prop="expectedAmount" width="140" align="right">
                  <template #default="{ row }">¥{{ Number(row.expectedAmount || 0).toLocaleString() }}</template>
                </el-table-column>
                <el-table-column label="预计成交日期" prop="expectedCloseDate" width="140" />
                <el-table-column label="创建时间" prop="createTime" width="170" />
              </el-table>
              <el-empty v-if="!opportunities.length && !tabLoading" description="暂无商机" />
            </el-tab-pane>

            <el-tab-pane label="订单回款" name="payment">
              <el-table :data="payments" border style="width: 100%" v-loading="tabLoading">
                <el-table-column label="回款编号" prop="paymentNo" width="160" />
                <el-table-column label="合同编号" prop="contractNo" width="160" show-overflow-tooltip />
                <el-table-column label="计划金额" prop="planAmount" width="140" align="right">
                  <template #default="{ row }">¥{{ Number(row.planAmount || 0).toLocaleString() }}</template>
                </el-table-column>
                <el-table-column label="实际金额" prop="actualAmount" width="140" align="right">
                  <template #default="{ row }">¥{{ Number(row.actualAmount || 0).toLocaleString() }}</template>
                </el-table-column>
                <el-table-column label="计划日期" prop="planDate" width="130" />
                <el-table-column label="状态" prop="status" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag :type="paymentStatusTagType(row.status)">{{ paymentStatusText(row.status) }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-empty v-if="!payments.length && !tabLoading" description="暂无回款记录" />
            </el-tab-pane>

            <el-tab-pane label="售后工单" name="serviceOrder">
              <el-table :data="serviceOrders" border style="width: 100%" v-loading="tabLoading">
                <el-table-column label="工单编号" prop="orderNo" width="160" />
                <el-table-column label="标题" prop="title" min-width="180" show-overflow-tooltip />
                <el-table-column label="类型" prop="type" width="100" align="center">
                  <template #default="{ row }">{{ orderTypeText(row.type) }}</template>
                </el-table-column>
                <el-table-column label="优先级" prop="priority" width="90" align="center">
                  <template #default="{ row }">
                    <el-tag :type="priorityTagType(row.priority)">{{ priorityText(row.priority) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="处理人" prop="assigneeName" width="120" />
                <el-table-column label="状态" prop="status" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag :type="orderStatusTagType(row.status)">{{ orderStatusText(row.status) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="创建时间" prop="createTime" width="170" />
              </el-table>
              <el-empty v-if="!serviceOrders.length && !tabLoading" description="暂无工单" />
            </el-tab-pane>

            <el-tab-pane label="标签" name="tag">
              <div class="tag-list">
                <el-tag
                  v-for="tag in tags"
                  :key="tag.id"
                  :color="tag.tagColor"
                  class="customer-tag"
                >
                  {{ tag.tagName }}
                </el-tag>
                <el-empty v-if="!tags.length" description="暂无标签" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部快捷操作区 -->
    <div class="quick-actions">
      <el-button type="primary" @click="openFollowDialog">
        <el-icon><Phone /></el-icon>新增跟进
      </el-button>
      <el-button type="success" @click="goCreateOpportunity">
        <el-icon><Trophy /></el-icon>创建商机
      </el-button>
      <el-button type="warning" @click="goCreateServiceOrder">
        <el-icon><Tools /></el-icon>发起工单
      </el-button>
    </div>

    <!-- 新增跟进对话框 -->
    <el-dialog v-model="followDialogVisible" title="新增跟进记录" width="560px">
      <el-form :model="followForm" label-width="100px" ref="followFormRef" :rules="followRules">
        <el-form-item label="跟进类型" prop="type">
          <el-select v-model="followForm.type" placeholder="请选择" style="width: 100%">
            <el-option label="电话" value="电话" />
            <el-option label="上门拜访" value="上门拜访" />
            <el-option label="微信沟通" value="微信沟通" />
            <el-option label="邮件" value="邮件" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进内容" prop="content">
          <el-input v-model="followForm.content" type="textarea" :rows="4" placeholder="请输入跟进内容" />
        </el-form-item>
        <el-form-item label="下次跟进时间">
          <el-date-picker v-model="followForm.nextFollowTime" type="datetime" placeholder="选择下次跟进时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitFollow">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { ArrowLeft, Phone, Trophy, Tools } from '@element-plus/icons-vue'
import { getCustomerDetail, addFollowRecord } from '@/api/customer'
import { getOpportunityPage } from '@/api/business'
import { getPaymentPage } from '@/api/business'
import { getServiceOrderPage } from '@/api/service'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const tabLoading = ref(false)
const activeTab = ref('follow')
const customer = reactive<any>({})
const contacts = ref<any[]>([])
const tags = ref<any[]>([])
const followRecords = ref<any[]>([])
const opportunities = ref<any[]>([])
const payments = ref<any[]>([])
const serviceOrders = ref<any[]>([])

// 已加载过的标签页（避免重复加载）
const loadedTabs = ref<Set<string>>(new Set(['follow', 'contact', 'tag']))

// 跟进对话框
const followDialogVisible = ref(false)
const submitLoading = ref(false)
const followFormRef = ref<FormInstance>()
const followForm = reactive<any>({
  type: '电话',
  content: '',
  nextFollowTime: '',
  customerId: null
})
const followRules = {
  type: [{ required: true, message: '请选择跟进类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入跟进内容', trigger: 'blur' }]
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

function oppStageText(stage: number) {
  const map: Record<number, string> = { 1: '需求确认', 2: '方案报价', 3: '商务谈判', 4: '合同签订', 5: '已成交', 6: '已失败' }
  return map[stage] || '未知'
}

function oppStageTagType(stage: number): 'info' | 'warning' | 'success' | 'danger' {
  if (stage === 5) return 'success'
  if (stage === 6) return 'danger'
  if (stage >= 3) return 'warning'
  return 'info'
}

function paymentStatusText(status: number) {
  const map: Record<number, string> = { 0: '待回款', 1: '部分回款', 2: '已回款', 3: '已逾期' }
  return map[status] || '未知'
}

function paymentStatusTagType(status: number): 'info' | 'warning' | 'success' | 'danger' {
  if (status === 2) return 'success'
  if (status === 3) return 'danger'
  if (status === 1) return 'warning'
  return 'info'
}

function orderTypeText(type: number) {
  const map: Record<number, string> = { 1: '咨询', 2: '投诉', 3: '报修', 4: '退换货', 5: '安装' }
  return map[type] || '其他'
}

function orderStatusText(status: number) {
  const map: Record<number, string> = { 0: '待处理', 1: '处理中', 2: '待验收', 3: '已完成', 4: '已关闭' }
  return map[status] || '未知'
}

function orderStatusTagType(status: number): 'info' | 'warning' | 'success' | 'danger' {
  if (status === 3) return 'success'
  if (status === 4) return 'info'
  if (status === 0) return 'danger'
  return 'warning'
}

function priorityText(priority: number) {
  const map: Record<number, string> = { 1: '紧急', 2: '高', 3: '中', 4: '低' }
  return map[priority] || '普通'
}

function priorityTagType(priority: number): 'danger' | 'warning' | 'info' {
  if (priority === 1) return 'danger'
  if (priority === 2) return 'warning'
  return 'info'
}

function goBack() {
  router.push('/customer/list')
}

async function loadDetail() {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res: any = await getCustomerDetail(id)
    const data = res.data || res
    Object.assign(customer, data)
    contacts.value = data.contacts || []
    tags.value = data.tags || []
    followRecords.value = data.followRecords || []
    followForm.customerId = id
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
}

/** 标签页切换时懒加载 */
async function handleTabChange(tabName: string) {
  if (loadedTabs.value.has(tabName)) return
  tabLoading.value = true
  try {
    const customerId = Number(route.params.id)
    if (tabName === 'opportunity') {
      const res: any = await getOpportunityPage({ customerId, pageNum: 1, pageSize: 50 })
      opportunities.value = res.data?.records || res.records || []
    } else if (tabName === 'payment') {
      const res: any = await getPaymentPage({ customerId, pageNum: 1, pageSize: 50 })
      payments.value = res.data?.records || res.records || []
    } else if (tabName === 'serviceOrder') {
      const res: any = await getServiceOrderPage({ customerId, pageNum: 1, pageSize: 50 })
      serviceOrders.value = res.data?.records || res.records || []
    }
    loadedTabs.value.add(tabName)
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    tabLoading.value = false
  }
}

/** 打开新增跟进对话框 */
function openFollowDialog() {
  followForm.type = '电话'
  followForm.content = ''
  followForm.nextFollowTime = ''
  followDialogVisible.value = true
}

/** 提交跟进记录 */
async function submitFollow() {
  if (!followFormRef.value) return
  await followFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await addFollowRecord({
        customerId: followForm.customerId,
        type: followForm.type,
        content: followForm.content,
        nextFollowTime: followForm.nextFollowTime || null
      })
      ElMessage.success('新增跟进记录成功')
      followDialogVisible.value = false
      // 刷新跟进记录
      await loadDetail()
      loadedTabs.value.add('follow')
    } catch (e) {
      // 错误已由请求拦截器统一提示
    } finally {
      submitLoading.value = false
    }
  })
}

/** 跳转创建商机（携带客户ID） */
function goCreateOpportunity() {
  router.push({ path: '/business/opportunity', query: { customerId: route.params.id, action: 'create' } })
}

/** 跳转发起工单（携带客户ID） */
function goCreateServiceOrder() {
  router.push({ path: '/service/order', query: { customerId: route.params.id, action: 'create' } })
}

onMounted(() => loadDetail())
</script>

<style scoped>
.app-container {
  padding: 20px;
}
.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.detail-header .title {
  margin-left: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.card-title {
  font-weight: 600;
  color: #303133;
}
.info-list {
  display: flex;
  flex-direction: column;
}
.info-row {
  display: flex;
  padding: 8px 0;
  font-size: 14px;
  line-height: 1.6;
}
.info-row .label {
  width: 120px;
  flex-shrink: 0;
  color: #909399;
}
.info-row .value {
  flex: 1;
  color: #303133;
  word-break: break-all;
}
.info-row .value.amount {
  color: #f56c6c;
  font-weight: 600;
}
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  min-height: 60px;
  align-items: center;
}
.customer-tag {
  color: #fff;
  border: none;
}
.quick-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
