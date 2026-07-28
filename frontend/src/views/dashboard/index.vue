<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2>工作台</h2>
    </div>

    <!-- 欢迎信息 -->
    <div class="welcome-card">
      <div class="welcome-text">
        <h3>欢迎回来，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员' }}</h3>
        <p>今天是 {{ today }}，祝您工作愉快！</p>
      </div>
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="20" class="data-cards">
      <el-col :span="6">
        <div class="stat-card" style="border-left-color: #409eff;">
          <div class="stat-card-header">
            <span class="stat-label">待跟进客户</span>
            <el-tag type="warning" size="small">待处理</el-tag>
          </div>
          <div class="stat-value">128</div>
          <div class="stat-footer">
            <span>较昨日 <i class="up">↑ 12%</i></span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="border-left-color: #67c23a;">
          <div class="stat-card-header">
            <span class="stat-label">本月新增客户</span>
            <el-tag type="success" size="small">增长中</el-tag>
          </div>
          <div class="stat-value">56</div>
          <div class="stat-footer">
            <span>较上月 <i class="up">↑ 8%</i></span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="border-left-color: #e6a23c;">
          <div class="stat-card-header">
            <span class="stat-label">进行中商机</span>
            <el-tag type="primary" size="small">跟踪中</el-tag>
          </div>
          <div class="stat-value">35</div>
          <div class="stat-footer">
            <span>预计成交额 <b>¥ 286 万</b></span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="border-left-color: #f56c6c;">
          <div class="stat-card-header">
            <span class="stat-label">待处理工单</span>
            <el-tag type="danger" size="small">紧急</el-tag>
          </div>
          <div class="stat-value">9</div>
          <div class="stat-footer">
            <span>其中紧急工单 <b style="color: #f56c6c;">3</b> 个</span>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

/** 今天的日期 */
const today = computed(() => {
  const date = new Date()
  const weekDays = ['日', '一', '二', '三', '四', '五', '六']
  const y = date.getFullYear()
  const m = date.getMonth() + 1
  const d = date.getDate()
  const w = weekDays[date.getDay()]
  return `${y}年${m}月${d}日 星期${w}`
})
</script>

<style scoped>
.dashboard-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.page-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  border-radius: 8px;
  padding: 24px 28px;
  margin-bottom: 20px;
  color: #fff;
}

.welcome-text h3 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

/* 数据卡片 */
.data-cards {
  margin: 0 !important;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 12px;
}

.stat-footer {
  font-size: 13px;
  color: #909399;
}

.stat-footer .up {
  color: #67c23a;
  font-style: normal;
}
</style>
