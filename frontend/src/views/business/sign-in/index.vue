<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ $route.meta.title }}</h2>
      <span class="page-subtitle">移动端外勤签到打卡</span>
    </div>

    <!-- 今日签到状态卡片 -->
    <el-card class="status-card" shadow="hover">
      <div class="status-card-inner">
        <div class="status-info">
          <div class="status-date">{{ todayStr }}</div>
          <div class="status-records">
            <el-tag v-if="todayRecords.length === 0" type="info">今日暂无签到</el-tag>
            <el-tag v-for="record in todayRecords" :key="record.id" :type="signTagType(record.signType)" style="margin-right: 8px">
              {{ signTypeText(record.signType) }} {{ record.signTime?.substring(11, 16) }}
            </el-tag>
          </div>
        </div>
        <div class="status-location">
          <el-icon :size="20"><Location /></el-icon>
          <span>{{ currentAddress || '正在获取位置...' }}</span>
        </div>
      </div>
    </el-card>

    <!-- 签到操作区 -->
    <el-card class="action-card" shadow="hover" style="margin-top: 16px">
      <template #header>
        <span>签到打卡</span>
      </template>

      <el-form :model="signForm" label-width="100px">
        <el-form-item label="签到类型">
          <el-radio-group v-model="signForm.signType">
            <el-radio-button :value="1">上午签到</el-radio-button>
            <el-radio-button :value="2">下午签退</el-radio-button>
            <el-radio-button :value="3">拜访签到</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="关联客户" v-if="signForm.signType === 3">
          <el-select v-model="signForm.customerId" filterable placeholder="选择拜访客户" style="width: 100%">
            <el-option v-for="c in customers" :key="c.id" :value="c.id" :label="c.name" />
          </el-select>
        </el-form-item>

        <el-form-item label="签到位置">
          <el-input v-model="signForm.address" placeholder="自动获取，可手动修改" />
          <el-button type="primary" link @click="getLocation" style="margin-top: 4px">
            <el-icon><Aim /></el-icon> 重新定位
          </el-button>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="signForm.remark" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleSignIn" :loading="signing" style="width: 100%">
            <el-icon><Check /></el-icon> 确认签到
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 签到历史 -->
    <el-card class="history-card" shadow="hover" style="margin-top: 16px">
      <template #header>
        <span>签到记录</span>
      </template>

      <el-timeline>
        <el-timeline-item
          v-for="record in historyList"
          :key="record.id"
          :timestamp="record.signTime"
          :type="signTimelineType(record.signType)"
        >
          <div class="timeline-item">
            <strong>{{ signTypeText(record.signType) }}</strong>
            <span v-if="record.customerName"> · {{ record.customerName }}</span>
            <div class="timeline-address" v-if="record.address">位置：{{ record.address }}</div>
            <div class="timeline-remark" v-if="record.remark">备注：{{ record.remark }}</div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Location, Aim, Check } from '@element-plus/icons-vue'
import { signIn, getTodaySignIn, getSignInList, type SignInParams } from '@/api/signIn'
import { getCustomerPage } from '@/api/customer'

const todayStr = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
const todayRecords = ref<any[]>([])
const historyList = ref<any[]>([])
const customers = ref<any[]>([])
const signing = ref(false)
const currentAddress = ref('')

const signForm = ref<SignInParams>({
  signType: 1,
  latitude: null,
  longitude: null,
  address: '',
  customerId: null,
  customerName: '',
  remark: ''
})

// 获取地理位置
function getLocation() {
  if (!navigator.geolocation) {
    ElMessage.warning('您的浏览器不支持定位功能')
    return
  }
  ElMessage.info('正在获取位置...')
  navigator.geolocation.getCurrentPosition(
    (position) => {
      signForm.value.latitude = position.coords.latitude
      signForm.value.longitude = position.coords.longitude
      currentAddress.value = `经度 ${position.coords.latitude.toFixed(6)}, 纬度 ${position.coords.longitude.toFixed(6)}`
      signForm.value.address = currentAddress.value
      ElMessage.success('定位成功')
    },
    (error) => {
      ElMessage.error('定位失败：' + error.message)
      // 失败时允许手动输入
      currentAddress.value = '定位失败，请手动输入地址'
    },
    { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
  )
}

// 签到
async function handleSignIn() {
  signing.value = true
  try {
    // 拜访签到时，带上客户名称
    if (signForm.value.signType === 3 && signForm.value.customerId) {
      const customer = customers.value.find(c => c.id === signForm.value.customerId)
      signForm.value.customerName = customer?.name || ''
    }
    await signIn(signForm.value)
    ElMessage.success('签到成功')
    // 刷新数据
    await Promise.all([loadTodayRecords(), loadHistory()])
    // 重置表单
    signForm.value.remark = ''
    signForm.value.customerId = null
  } catch (e: any) {
    ElMessage.error(e?.message || '签到失败')
  } finally {
    signing.value = false
  }
}

async function loadTodayRecords() {
  try {
    const res = await getTodaySignIn()
    todayRecords.value = res.data || []
  } catch (e) {
    console.error('加载今日签到失败', e)
  }
}

async function loadHistory() {
  try {
    const res = await getSignInList()
    historyList.value = (res.data || []).slice(0, 20)  // 最近20条
  } catch (e) {
    console.error('加载签到记录失败', e)
  }
}

async function loadCustomers() {
  try {
    const res = await getCustomerPage({ page: 1, size: 100 })
    customers.value = res.data?.records || []
  } catch (e) {
    console.error('加载客户失败', e)
  }
}

// 工具函数
function signTypeText(type: number) {
  return { 1: '上午签到', 2: '下午签退', 3: '拜访签到' }[type] || '未知'
}

function signTagType(type: number) {
  return ({ 1: 'success', 2: 'warning', 3: 'primary' } as const)[type] || 'info'
}

function signTimelineType(type: number) {
  return ({ 1: 'success', 2: 'warning', 3: 'primary' } as const)[type] || 'info'
}

onMounted(() => {
  loadTodayRecords()
  loadHistory()
  loadCustomers()
  getLocation()
})
</script>

<style scoped>
.status-card-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-date {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.status-location {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #909399;
  font-size: 14px;
}

.timeline-item strong {
  font-size: 15px;
}

.timeline-address, .timeline-remark {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .status-card-inner {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  :deep(.el-radio-group) {
    display: flex;
    width: 100%;
  }

  :deep(.el-radio-button) {
    flex: 1;
  }
}
</style>
