<template>
  <div class="app-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="queryParams.category" placeholder="请选择" clearable style="width: 160px">
            <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
          <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading">
      <el-row :gutter="16" v-if="tableData.length > 0">
        <el-col :xs="24" :sm="12" :md="8" v-for="item in tableData" :key="item.id">
          <el-card class="knowledge-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-title" :title="item.title">{{ item.title }}</span>
                <el-tag :type="categoryTagType(item.category)" size="small">{{ item.category || '-' }}</el-tag>
              </div>
            </template>
            <div class="content-summary">{{ summary(item.content) }}</div>
            <div class="tag-list" v-if="item.tags">
              <el-tag
                v-for="(tag, idx) in splitTags(item.tags)"
                :key="idx"
                size="small"
                class="tag-item"
                effect="plain"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="card-footer">
              <div class="footer-left">
                <span class="view-count">
                  <el-icon><View /></el-icon>
                  {{ item.viewCount ?? 0 }}
                </span>
                <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
                  {{ item.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </div>
              <div class="footer-right">
                <el-button link type="primary" size="small" @click="handleView(item)">查看</el-button>
                <el-button link type="primary" size="small" @click="handleEdit(item)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDelete(item)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无数据" />
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[9, 12, 18, 24]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="知识详情" width="700px">
      <div v-if="detailData" class="detail-wrap">
        <h3 class="detail-title">{{ detailData.title }}</h3>
        <div class="detail-meta">
          <el-tag :type="categoryTagType(detailData.category)" size="small">{{ detailData.category }}</el-tag>
          <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small">
            {{ detailData.status === 1 ? '上架' : '下架' }}
          </el-tag>
          <span class="detail-view">
            <el-icon><View /></el-icon>
            {{ detailData.viewCount ?? 0 }} 次浏览
          </span>
        </div>
        <div class="detail-tags" v-if="detailData.tags">
          <el-tag
            v-for="(tag, idx) in splitTags(detailData.tags)"
            :key="idx"
            size="small"
            effect="plain"
            class="tag-item"
          >
            {{ tag }}
          </el-tag>
        </div>
        <el-divider />
        <div class="detail-content">{{ detailData.content }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔，如：话术,新客户,陌拜" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="正文内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入正文内容" />
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
import { Search, Refresh, Plus, View } from '@element-plus/icons-vue'
import {
  getKnowledgePage,
  getKnowledgeById,
  addKnowledge,
  updateKnowledge,
  deleteKnowledge
} from '@/api/market'

const categoryOptions = ['话术', '方案', '案例', '培训']

const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 9,
  title: '',
  category: ''
})

const detailVisible = ref(false)
const detailData = ref<any>(null)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  title: '',
  category: '',
  content: '',
  tags: '',
  status: 1
})
const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入正文内容', trigger: 'blur' }]
}

function categoryTagType(category: string): 'primary' | 'success' | 'warning' | 'info' {
  switch (category) {
    case '话术': return 'warning'
    case '方案': return 'primary'
    case '案例': return 'success'
    case '培训': return 'info'
    default: return 'info'
  }
}

function summary(content: string) {
  if (!content) return '暂无内容'
  return content.length > 100 ? content.slice(0, 100) + '...' : content
}

function splitTags(tags: string) {
  if (!tags) return []
  return tags.split(/[,，]/).map((t) => t.trim()).filter(Boolean)
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getKnowledgePage(queryParams)
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
  queryParams.title = ''
  queryParams.category = ''
  queryParams.pageNum = 1
  loadData()
}

function resetForm() {
  form.id = undefined
  form.title = ''
  form.category = ''
  form.content = ''
  form.tags = ''
  form.status = 1
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增知识'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑知识'
  resetForm()
  form.id = row.id
  form.title = row.title
  form.category = row.category
  form.content = row.content
  form.tags = row.tags
  form.status = row.status
  dialogVisible.value = true
}

async function handleView(row: any) {
  try {
    const res: any = await getKnowledgeById(row.id)
    detailData.value = res.data ?? row
    detailVisible.value = true
    // 重新加载列表以刷新浏览次数
    loadData()
  } catch (e) {
    // 错误已由请求拦截器统一提示
  }
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateKnowledge({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addKnowledge({ ...form })
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
    await ElMessageBox.confirm(`确认删除知识【${row.title}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteKnowledge(row.id)
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
.knowledge-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}
.content-summary {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  height: 96px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  margin-bottom: 10px;
}
.tag-list {
  margin-bottom: 10px;
  min-height: 4px;
}
.tag-item {
  margin-right: 6px;
  margin-bottom: 6px;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #ebeef5;
  padding-top: 10px;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #909399;
  font-size: 13px;
}
.view-count {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.footer-right {
  display: flex;
  align-items: center;
}
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
.detail-wrap {
  padding: 0 4px;
}
.detail-title {
  margin: 0 0 12px;
  font-size: 18px;
  color: #303133;
}
.detail-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #909399;
  font-size: 13px;
}
.detail-view {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.detail-tags {
  margin-top: 10px;
}
.detail-content {
  white-space: pre-wrap;
  color: #303133;
  font-size: 14px;
  line-height: 1.8;
}
</style>
