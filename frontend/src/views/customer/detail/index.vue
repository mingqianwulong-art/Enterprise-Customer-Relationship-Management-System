<template>
  <div class="app-container">
    <div class="detail-header">
      <el-button @click="goBack"><el-icon><ArrowLeft /></el-icon>返回</el-button>
      <span class="title">客户详情</span>
    </div>

    <el-row :gutter="16" v-loading="loading">
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
              <span class="label">累计成交金额：</span>
              <span class="value amount">¥{{ Number(customer.totalAmount || 0).toLocaleString() }}</span>
            </div>
            <div class="info-row">
              <span class="label">最后跟进时间：</span>
              <span class="value">{{ customer.lastFollowTime || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="label">备注：</span>
              <span class="value">{{ customer.remark || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getCustomerDetail } from '@/api/customer'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const activeTab = ref('follow')
const customer = reactive<any>({})
const contacts = ref<any[]>([])
const tags = ref<any[]>([])
const followRecords = ref<any[]>([])

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

function goBack() {
  router.push('/customer/list')
}

async function loadDetail() {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res: any = await getCustomerDetail(id)
    const data = res.data || {}
    Object.assign(customer, data.customer || {})
    contacts.value = data.contacts || []
    tags.value = data.tags || []
    followRecords.value = data.followRecords || []
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
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
</style>
