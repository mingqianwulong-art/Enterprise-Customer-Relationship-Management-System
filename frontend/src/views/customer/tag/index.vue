<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增标签</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="标签名称" prop="tagName" min-width="180">
          <template #default="{ row }">
            <el-tag :color="row.tagColor" class="tag-item">{{ row.tagName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="颜色" prop="tagColor" width="160" align="center">
          <template #default="{ row }">
            <div class="color-cell">
              <span class="color-block" :style="{ backgroundColor: row.tagColor }"></span>
              <span>{{ row.tagColor }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="tagType" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" v-if="row.tagType === 1">系统</el-tag>
            <el-tag type="success" v-else>自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="170" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签颜色" prop="tagColor">
          <el-color-picker v-model="form.tagColor" />
          <span class="color-tip">{{ form.tagColor }}</span>
        </el-form-item>
        <el-form-item label="标签类型" prop="tagType">
          <el-select v-model="form.tagType" placeholder="请选择" style="width: 100%">
            <el-option label="系统" :value="1" />
            <el-option label="自定义" :value="2" />
          </el-select>
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
import { Plus } from '@element-plus/icons-vue'
import { getTagList, addTag, updateTag, deleteTag } from '@/api/customer'

const loading = ref(false)
const tableData = ref<any[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  tagName: '',
  tagColor: '#409EFF',
  tagType: 2
})
const rules: FormRules = {
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }],
  tagColor: [{ required: true, message: '请选择标签颜色', trigger: 'change' }],
  tagType: [{ required: true, message: '请选择标签类型', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getTagList()
    tableData.value = res.data || res.rows || []
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.tagName = ''
  form.tagColor = '#409EFF'
  form.tagType = 2
  formRef.value?.clearValidate()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增标签'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑标签'
  resetForm()
  form.id = row.id
  form.tagName = row.tagName
  form.tagColor = row.tagColor
  form.tagType = row.tagType
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateTag({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addTag({ ...form })
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
    await ElMessageBox.confirm(`确认删除标签【${row.tagName}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteTag(row.id)
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tag-item {
  color: #fff;
  border: none;
}
.color-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.color-block {
  display: inline-block;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
.color-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 13px;
}
</style>
