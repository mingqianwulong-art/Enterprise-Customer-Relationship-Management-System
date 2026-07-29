<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-button type="primary" @click="handleAdd()"><el-icon><Plus /></el-icon>新增菜单</el-button>
        </div>
      </template>
      <el-table
        :data="tableData"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        style="width: 100%"
      >
        <el-table-column label="菜单名称" prop="name" min-width="180" />
        <el-table-column label="图标" prop="icon" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.icon" class="icon-text">{{ row.icon }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="orderNum" width="80" align="center" />
        <el-table-column label="权限标识" prop="perms" min-width="180" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleAdd(row)">新增子菜单</el-button>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="父菜单" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="menuOptions"
            node-key="id"
            :props="{ label: 'name', children: 'children' }"
            check-strictly
            :render-after-expand="false"
            placeholder="请选择父菜单"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">目录</el-radio>
            <el-radio :label="2">菜单</el-radio>
            <el-radio :label="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="form.type !== 3" label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item v-if="form.type === 2" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="perms">
          <el-input v-model="form.perms" placeholder="请输入权限标识，如 system:user:list" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="是否可见" prop="visible">
          <el-radio-group v-model="form.visible">
            <el-radio :label="1">可见</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
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
import { Plus } from '@element-plus/icons-vue'
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/api/system'

const loading = ref(false)
const tableData = ref<any[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const form = reactive({
  id: undefined as number | undefined,
  parentId: 0,
  name: '',
  path: '',
  component: '',
  perms: '',
  icon: '',
  type: 1,
  orderNum: 0,
  visible: 1,
  status: 1
})
const rules: FormRules = {
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

// 父菜单下拉树：顶级菜单 + 实际菜单树
const menuOptions = computed(() => [
  { id: 0, name: '顶级菜单', children: tableData.value }
])

function typeText(type: number) {
  switch (type) {
    case 1: return '目录'
    case 2: return '菜单'
    case 3: return '按钮'
    default: return '-'
  }
}

function typeTagType(type: number): 'primary' | 'success' | 'warning' {
  switch (type) {
    case 1: return 'primary'
    case 2: return 'success'
    default: return 'warning'
  }
}

async function loadData() {
  loading.value = true
  try {
    const res: any = await getMenuTree()
    tableData.value = res.data || res.rows || []
  } catch (e) {
    // 错误已由请求拦截器统一提示
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.parentId = 0
  form.name = ''
  form.path = ''
  form.component = ''
  form.perms = ''
  form.icon = ''
  form.type = 1
  form.orderNum = 0
  form.visible = 1
  form.status = 1
  formRef.value?.clearValidate()
}

function handleAdd(row?: any) {
  isEdit.value = false
  dialogTitle.value = '新增菜单'
  resetForm()
  if (row) {
    form.parentId = row.id
  }
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'
  resetForm()
  form.id = row.id
  form.parentId = row.parentId ?? 0
  form.name = row.name
  form.path = row.path
  form.component = row.component
  form.perms = row.perms
  form.icon = row.icon
  form.type = row.type
  form.orderNum = row.orderNum
  form.visible = row.visible
  form.status = row.status
  dialogVisible.value = true
}

async function submitForm() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateMenu({ ...form })
        ElMessage.success('修改成功')
      } else {
        await addMenu({ ...form })
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
    await ElMessageBox.confirm(`确认删除菜单【${row.name}】吗？删除后不可恢复。`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMenu(row.id)
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
  justify-content: flex-start;
  align-items: center;
}
</style>
