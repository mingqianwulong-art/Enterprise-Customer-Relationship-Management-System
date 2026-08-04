<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" inline @submit.prevent>
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input
            v-model="queryParams.realName"
            placeholder="请输入真实姓名"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column label="用户名" prop="username" min-width="100" />
        <el-table-column label="真实姓名" prop="realName" min-width="100" />
        <el-table-column label="部门" prop="deptName" min-width="110" />
        <el-table-column label="手机号" prop="phone" min-width="120" />
        <el-table-column label="邮箱" prop="email" min-width="180" show-overflow-tooltip />
        <el-table-column label="性别" prop="sex" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.sex === 1" type="primary" size="small">男</el-tag>
            <el-tag v-else-if="row.sex === 2" type="danger" size="small">女</el-tag>
            <el-tag v-else type="info" size="small">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="最后登录时间" prop="lastLoginTime" min-width="160" />
        <el-table-column label="操作" fixed="right" width="290">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" :icon="User" @click="handleAssignRole(row)">分配角色</el-button>
            <el-button link type="warning" :icon="Key" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="form.deptId"
            :data="deptOptions"
            placeholder="请选择部门"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
            <el-radio :label="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="420px">
      <el-form label-width="80px">
        <el-form-item label="新密码">
          <el-input
            v-model="resetPwdForm.password"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleVisible" title="分配角色" width="480px">
      <div style="margin-bottom: 12px; color: #606266;">
        为用户 <b>{{ roleForm.username }}</b> 分配角色：
      </div>
      <el-checkbox-group v-model="roleForm.roleIds" v-loading="roleLoading">
        <div v-for="role in roleOptions" :key="role.id" style="padding: 6px 0;">
          <el-checkbox :label="role.id">
            {{ role.roleName }}
            <el-tag size="small" type="info" style="margin-left: 8px;">{{ role.roleCode }}</el-tag>
          </el-checkbox>
        </div>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key, User } from '@element-plus/icons-vue'
import {
  getUserPage,
  addUser,
  updateUser,
  deleteUser,
  resetPwd,
  changeUserStatus,
  getDeptTree,
  getRoleList,
  getUserRoles,
  assignUserRoles
} from '@/api/system'

interface UserRow {
  id: number
  deptId: number
  deptName?: string
  username: string
  realName: string
  phone: string
  email: string
  sex: number
  status: number
  lastLoginTime?: string
  [key: string]: any
}

const loading = ref(false)
const tableData = ref<UserRow[]>([])
const total = ref(0)
const deptOptions = ref<any[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  phone: '',
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = () => ({
  id: undefined as number | undefined,
  deptId: undefined as number | undefined,
  username: '',
  realName: '',
  phone: '',
  email: '',
  sex: 0,
  status: 1,
  password: ''
})
const form = reactive(defaultForm())

const rules = computed<FormRules>(() => ({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  // 编辑用户时密码非必填（留空则不修改密码）
  password: isEdit.value ? [] : [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
}))

// 重置密码
const resetPwdVisible = ref(false)
const resetPwdForm = reactive({ userId: undefined as number | undefined, password: '' })

/** 加载用户列表 */
async function loadData() {
  loading.value = true
  try {
    const res: any = await getUserPage(queryParams)
    const data = res.data || {}
    tableData.value = data.list || data.records || []
    total.value = data.total ?? 0
  } finally {
    loading.value = false
  }
}

/** 将部门树转换为 el-tree-select 默认结构 { value, label, children } */
function normalizeDept(nodes: any[]): any[] {
  return (nodes || []).map((n) => ({
    value: n.id,
    label: n.deptName,
    children: n.children && n.children.length ? normalizeDept(n.children) : undefined
  }))
}

/** 加载部门树 */
async function loadDeptTree() {
  try {
    const res: any = await getDeptTree()
    deptOptions.value = normalizeDept(res.data || [])
  } catch {
    deptOptions.value = []
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  loadData()
}

function resetQuery() {
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.phone = ''
  queryParams.status = undefined
  queryParams.pageNum = 1
  loadData()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  Object.assign(form, defaultForm())
  dialogVisible.value = true
}

function handleEdit(row: UserRow) {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, defaultForm())
  Object.assign(form, {
    id: row.id,
    deptId: row.deptId,
    username: row.username,
    realName: row.realName,
    phone: row.phone,
    email: row.email,
    sex: row.sex,
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
      const { password, ...rest } = form
      await updateUser(rest)
      ElMessage.success('修改成功')
    } else {
      await addUser(form)
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

async function handleDelete(row: UserRow) {
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 错误已由请求拦截器统一提示
  }
}

async function handleStatusChange(row: UserRow) {
  try {
    await changeUserStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已启用' : '已停用')
  } catch {
    // 失败回滚状态
    row.status = row.status === 1 ? 0 : 1
  }
}

function handleResetPwd(row: UserRow) {
  resetPwdForm.userId = row.id
  resetPwdForm.password = ''
  resetPwdVisible.value = true
}

async function submitResetPwd() {
  if (!resetPwdForm.password) {
    ElMessage.warning('请输入新密码')
    return
  }
  try {
    await resetPwd(resetPwdForm.userId as number, resetPwdForm.password)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch {
    // 错误已由请求拦截器统一提示
  }
}

// 分配角色
const roleVisible = ref(false)
const roleLoading = ref(false)
const roleOptions = ref<any[]>([])
const roleForm = reactive({
  userId: undefined as number | undefined,
  username: '',
  roleIds: [] as number[]
})

async function handleAssignRole(row: UserRow) {
  roleForm.userId = row.id
  roleForm.username = row.username
  roleForm.roleIds = []
  roleVisible.value = true
  roleLoading.value = true
  try {
    // 并行加载角色列表 + 用户已分配角色
    const [roleRes, userRoleRes]: any[] = await Promise.all([getRoleList(), getUserRoles(row.id)])
    roleOptions.value = (roleRes.data || []).filter((r: any) => r.status === 1)
    roleForm.roleIds = (userRoleRes.data || []).map((id: any) => Number(id))
  } catch {
    // 错误已由请求拦截器统一提示
  } finally {
    roleLoading.value = false
  }
}

async function submitAssignRole() {
  if (!roleForm.userId) return
  try {
    await assignUserRoles(roleForm.userId, roleForm.roleIds)
    ElMessage.success('角色分配成功')
    roleVisible.value = false
  } catch {
    // 错误已由请求拦截器统一提示
  }
}

onMounted(() => {
  loadDeptTree()
  loadData()
})
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
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
