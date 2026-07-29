<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline @submit.prevent>
        <el-form-item label="部门名称">
          <el-input
            v-model="queryParams.deptName"
            placeholder="请输入部门名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 树形表格 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-button type="primary" :icon="Plus" @click="handleAdd()">新增部门</el-button>
        </div>
      </template>
      <el-table
        :data="filteredTree"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        default-expand-all
        style="width: 100%"
      >
        <el-table-column label="部门名称" prop="deptName" min-width="200" />
        <el-table-column label="排序" prop="orderNum" width="100" align="center" />
        <el-table-column label="状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="small">启用</el-tag>
            <el-tag v-else type="info" size="small">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" fixed="right" width="220">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Plus" @click="handleAdd(row)">新增子部门</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="父部门" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            placeholder="请选择父部门，顶级请选“顶级部门”"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getDeptTree, addDept, updateDept, deleteDept } from '@/api/system'

interface DeptNode {
  id: number
  parentId: number
  deptName: string
  orderNum: number
  status: number
  createTime?: string
  children?: DeptNode[]
  [key: string]: any
}

const loading = ref(false)
const treeData = ref<DeptNode[]>([])

const queryParams = reactive({
  deptName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  id: undefined as number | undefined,
  parentId: 0 as number,
  deptName: '',
  orderNum: 0,
  status: 1
})
const form = reactive(defaultForm())

const rules: FormRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择父部门', trigger: 'change' }]
}

/** el-tree-select 数据：在树根加一个“顶级部门”节点，value=0 */
const parentOptions = computed(() => [
  {
    value: 0,
    label: '顶级部门',
    children: normalizeForSelect(treeData.value)
  }
])

function normalizeForSelect(nodes: DeptNode[]): any[] {
  return (nodes || []).map((n) => ({
    value: n.id,
    label: n.deptName,
    children: n.children && n.children.length ? normalizeForSelect(n.children) : undefined
  }))
}

/** 按部门名称过滤树（保留匹配节点的父级链路） */
const filteredTree = computed(() => {
  const keyword = queryParams.deptName.trim()
  if (!keyword) return treeData.value
  return filterTree(treeData.value, keyword)
})

function filterTree(nodes: DeptNode[], keyword: string): DeptNode[] {
  const result: DeptNode[] = []
  for (const node of nodes) {
    const children = node.children ? filterTree(node.children, keyword) : []
    if (node.deptName.includes(keyword) || children.length > 0) {
      result.push({ ...node, children: children.length ? children : node.children })
    }
  }
  return result
}

/** 加载部门树 */
async function loadData() {
  loading.value = true
  try {
    const res: any = await getDeptTree()
    treeData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  // 前端过滤，无需重新请求
}

function resetQuery() {
  queryParams.deptName = ''
}

function handleAdd(row?: DeptNode) {
  isEdit.value = false
  dialogTitle.value = row ? '新增子部门' : '新增部门'
  Object.assign(form, defaultForm())
  form.parentId = row ? row.id : 0
  dialogVisible.value = true
}

function handleEdit(row: DeptNode) {
  isEdit.value = true
  dialogTitle.value = '编辑部门'
  Object.assign(form, defaultForm())
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId,
    deptName: row.deptName,
    orderNum: row.orderNum,
    status: row.status
  })
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    if (isEdit.value) {
      await updateDept(form)
      ElMessage.success('修改成功')
    } else {
      await addDept(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {
    // 错误已由请求拦截器统一提示
  }
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, defaultForm())
}

async function handleDelete(row: DeptNode) {
  try {
    await ElMessageBox.confirm(`确定要删除部门 "${row.deptName}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await deleteDept(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 错误已由请求拦截器统一提示
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
</style>
