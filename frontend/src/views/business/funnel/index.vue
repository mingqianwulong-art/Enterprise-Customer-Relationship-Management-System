<template>
  <div class="app-container">
    <el-card class="search-card">
      <div class="page-header">
        <h2>销售漏斗分析</h2>
        <el-button type="primary" @click="loadData"><el-icon><Refresh /></el-icon>刷新</el-button>
      </div>
    </el-card>

    <el-row :gutter="16">
      <!-- 左侧：漏斗图 -->
      <el-col :span="12">
        <el-card v-loading="loading">
          <template #header>
            <span>商机漏斗</span>
          </template>
          <div class="funnel-wrap">
            <div
              v-for="(item, index) in funnelData"
              :key="item.stage"
              class="funnel-stage"
              :style="funnelStyle(index)"
            >
              <div class="funnel-stage-name">{{ item.stageName }}</div>
              <div class="funnel-stage-info">
                {{ item.count }} 个 · ¥{{ Number(item.totalAmount || 0).toLocaleString() }}
              </div>
            </div>
            <el-empty v-if="!funnelData.length" description="暂无数据" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：统计卡片 -->
      <el-col :span="12">
        <el-card class="stat-card">
          <template #header>
            <span>统计概览</span>
          </template>
          <el-row :gutter="16">
            <el-col :span="12">
              <div class="stat-item">
                <div class="stat-label">总商机数</div>
                <div class="stat-value">{{ summary.totalCount }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="stat-item">
                <div class="stat-label">总预计金额</div>
                <div class="stat-value">¥{{ Number(summary.totalAmount).toLocaleString() }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="stat-item">
                <div class="stat-label">已赢单数</div>
                <div class="stat-value" style="color: #67c23a">{{ summary.wonCount }}</div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="stat-item">
                <div class="stat-label">赢单金额</div>
                <div class="stat-value" style="color: #67c23a">¥{{ Number(summary.wonAmount).toLocaleString() }}</div>
              </div>
            </el-col>
          </el-row>
          <el-divider />
          <div class="win-rate">
            <span class="stat-label">赢单率</span>
            <el-progress
              :percentage="summary.winRate"
              :color="'#67c23a'"
              :stroke-width="18"
              :text-inside="true"
            />
          </div>
          <el-divider />
          <div class="conversion-list">
            <div class="stat-label" style="margin-bottom: 12px">各阶段转化率</div>
            <div v-for="item in funnelData" :key="item.stage" class="conversion-item">
              <div class="conversion-name">{{ item.stageName }}</div>
              <el-progress
                :percentage="Number(item.conversionRate || 0)"
                :color="stageColor(funnelData.indexOf(item))"
                :stroke-width="14"
                :text-inside="true"
              />
            </div>
            <el-empty v-if="!funnelData.length" description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：阶段详细列表 -->
    <el-card style="margin-top: 16px">
      <template #header>
        <span>各阶段明细</span>
      </template>
      <el-table :data="funnelData" border style="width: 100%">
        <el-table-column label="阶段名称" prop="stageName" min-width="120" />
        <el-table-column label="商机数" prop="count" width="100" align="center" />
        <el-table-column label="预计金额" prop="totalAmount" width="160" align="right">
          <template #default="{ row }">
            ¥{{ Number(row.totalAmount || 0).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="占比" width="200" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="proportion(row.count)"
              :color="stageColor(funnelData.indexOf(row))"
              :stroke-width="14"
              :text-inside="true"
            />
          </template>
        </el-table-column>
        <el-table-column label="转化率" width="200" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="Number(row.conversionRate || 0)"
              :color="stageColor(funnelData.indexOf(row))"
              :stroke-width="14"
              :text-inside="true"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getFunnelData } from '@/api/business'

const loading = ref(false)
const funnelData = ref<any[]>([])

// 阶段颜色：从蓝到红渐变
const stageColors = ['#1890ff', '#447ad5', '#7064ab', '#9d4e81', '#c93857', '#f5222d']

function stageColor(index: number) {
  return stageColors[index] || '#409eff'
}

function funnelStyle(index: number) {
  const width = 100 - index * 15
  return {
    width: `${width}%`,
    backgroundColor: stageColor(index)
  }
}

const summary = computed(() => {
  const totalCount = funnelData.value.reduce((sum, item) => sum + Number(item.count || 0), 0)
  const totalAmount = funnelData.value.reduce((sum, item) => sum + Number(item.totalAmount || 0), 0)
  const wonItem = funnelData.value.find((item) => item.stage === 5)
  const wonCount = Number(wonItem?.count || 0)
  const wonAmount = Number(wonItem?.totalAmount || 0)
  const winRate = totalCount > 0 ? Math.round((wonCount / totalCount) * 100) : 0
  return { totalCount, totalAmount, wonCount, wonAmount, winRate }
})

function proportion(count: number) {
  if (summary.value.totalCount === 0) return 0
  return Math.round((Number(count || 0) / summary.value.totalCount) * 100)
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getFunnelData()
    funnelData.value = res.data || []
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
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
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}
.funnel-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 200px;
}
.funnel-stage {
  margin: 0 auto 4px;
  padding: 16px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  clip-path: polygon(8% 0, 92% 0, 84% 100%, 16% 100%);
  transition: all 0.3s;
}
.funnel-stage-name {
  font-size: 15px;
}
.funnel-stage-info {
  font-size: 13px;
  margin-top: 4px;
  opacity: 0.95;
}
.stat-card .stat-item {
  text-align: center;
  padding: 12px 0;
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}
.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}
.win-rate {
  margin-top: 8px;
}
.conversion-item {
  margin-bottom: 12px;
}
.conversion-name {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}
</style>
