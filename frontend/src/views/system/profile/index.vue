<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span>个人信息</span></template>
          <div class="user-avatar">
            <el-avatar :size="80" icon="UserFilled" />
          </div>
          <ul class="user-info-list">
            <li><label>用户名：</label><span>{{ user.username }}</span></li>
            <li><label>姓名：</label><span>{{ user.realName }}</span></li>
            <li><label>手机号：</label><span>{{ user.phone || '-' }}</span></li>
            <li><label>邮箱：</label><span>{{ user.email || '-' }}</span></li>
            <li><label>性别：</label><span>{{ sexText }}</span></li>
            <li><label>状态：</label><el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">{{ user.status === 1 ? '启用' : '停用' }}</el-tag></li>
            <li><label>最后登录：</label><span>{{ user.lastLoginTime || '-' }}</span></li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="never">
          <template #header><span>修改资料</span></template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 500px">
            <el-form-item label="用户名">
              <el-input :model-value="form.username" disabled />
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
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUser } from '@/api/system'
import { getInfo } from '@/api/auth'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const saving = ref(false)

const user = ref<any>({})
const form = reactive({
  id: undefined as number | undefined,
  username: '',
  realName: '',
  phone: '',
  email: '',
  sex: 0
})

const rules: FormRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
}

const sexText = computed(() => {
  if (user.value.sex === 1) return '男'
  if (user.value.sex === 2) return '女'
  return '未知'
})

async function loadInfo() {
  try {
    const res: any = await getInfo()
    const data = res.data
    user.value = data.user || {}
    Object.assign(form, {
      id: data.user?.id,
      username: data.user?.username,
      realName: data.user?.realName,
      phone: data.user?.phone || '',
      email: data.user?.email || '',
      sex: data.user?.sex ?? 0
    })
  } catch {
    // 错误已由请求拦截器统一提示
  }
}

async function handleSave() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      await updateUser({ ...form })
      ElMessage.success('修改成功')
      loadInfo()
    } catch {
      // 错误已由请求拦截器统一提示
    } finally {
      saving.value = false
    }
  })
}

function handleReset() {
  formRef.value?.clearValidate()
  loadInfo()
}

onMounted(() => loadInfo())
</script>

<style scoped>
.user-avatar {
  text-align: center;
  padding: 20px 0;
}
.user-info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.user-info-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}
.user-info-list label {
  color: #909399;
}
</style>
