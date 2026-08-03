<template>
  <div class="profile-page">
    <!-- 顶部个人信息卡片 -->
    <el-card shadow="never" class="profile-header-card">
      <div class="profile-header">
        <div class="profile-avatar-wrapper">
          <el-avatar :size="90" :src="avatarUrl" icon="UserFilled" class="profile-avatar" />
          <div class="avatar-status" :class="user.status === 1 ? 'online' : 'offline'"></div>
          <el-upload
            class="avatar-upload-btn"
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*"
          >
            <div class="upload-mask" v-loading="uploading">
              <el-icon><Camera /></el-icon>
            </div>
          </el-upload>
        </div>
        <div class="profile-header-info">
          <h2 class="profile-name">{{ user.realName || '未设置' }}</h2>
          <p class="profile-username">@{{ user.username }}</p>
          <div class="profile-tags">
            <el-tag :type="user.status === 1 ? 'success' : 'danger'" effect="light" round size="small">
              {{ user.status === 1 ? '在线' : '停用' }}
            </el-tag>
            <el-tag v-if="user.sex === 1" type="primary" effect="plain" round size="small">男</el-tag>
            <el-tag v-else-if="user.sex === 2" type="danger" effect="plain" round size="small">女</el-tag>
            <el-tag v-else type="info" effect="plain" round size="small">未设置</el-tag>
          </div>
        </div>
        <div class="profile-header-meta">
          <div class="meta-item">
            <el-icon><Phone /></el-icon>
            <span>{{ user.phone || '未绑定' }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Message /></el-icon>
            <span class="meta-email">{{ user.email || '未绑定' }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>{{ user.lastLoginTime || '暂无记录' }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="profile-body">
      <!-- 左侧：基本信息展示 -->
      <el-col :span="10">
        <el-card shadow="never" class="info-card">
          <template #header>
            <div class="card-title">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </div>
          </template>
          <div class="info-grid">
            <div class="info-row">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ user.username || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">真实姓名</span>
              <span class="info-value">{{ user.realName || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">手机号码</span>
              <span class="info-value">{{ user.phone || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">电子邮箱</span>
              <span class="info-value text-ellipsis">{{ user.email || '-' }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">性别</span>
              <span class="info-value">{{ sexText }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">账户状态</span>
              <span class="info-value">
                <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small" effect="light">
                  {{ user.status === 1 ? '正常' : '停用' }}
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span class="info-label">最后登录</span>
              <span class="info-value">{{ user.lastLoginTime || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：修改资料表单 -->
      <el-col :span="14">
        <el-card shadow="never" class="form-card">
          <template #header>
            <div class="card-title">
              <el-icon><Edit /></el-icon>
              <span>编辑资料</span>
            </div>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" label-position="right">
            <el-form-item label="用户名">
              <el-input :model-value="form.username" disabled prefix-icon="User" />
              <div class="form-hint">用户名不可修改</div>
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" prefix-icon="Postcard" clearable />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号码" prefix-icon="Phone" clearable maxlength="11" />
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入电子邮箱" prefix-icon="Message" clearable />
            </el-form-item>
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio-button :label="1">男</el-radio-button>
                <el-radio-button :label="2">女</el-radio-button>
                <el-radio-button :label="0">保密</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave" size="default">
                <el-icon style="margin-right: 4px"><Check /></el-icon>保存修改
              </el-button>
              <el-button @click="handleReset" size="default">
                <el-icon style="margin-right: 4px"><RefreshLeft /></el-icon>重置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadRequestOptions } from 'element-plus'
import {
  User, Edit, Phone, Message, Clock, Check, RefreshLeft, Postcard, Camera
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateUser } from '@/api/system'
import { getInfo } from '@/api/auth'
import request from '@/api/request'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const saving = ref(false)
const uploading = ref(false)

const user = ref<any>({})
const form = reactive({
  id: undefined as number | undefined,
  username: '',
  realName: '',
  phone: '',
  email: '',
  sex: 0,
  avatar: ''
})

/** 头像完整 URL */
const avatarUrl = computed(() => {
  if (user.value.avatar) {
    return '/api' + user.value.avatar
  }
  return ''
})

const rules: FormRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
}

const sexText = computed(() => {
  if (user.value.sex === 1) return '男'
  if (user.value.sex === 2) return '女'
  return '保密'
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
      sex: data.user?.sex ?? 0,
      avatar: data.user?.avatar || ''
    })
  } catch {
    // 错误已由请求拦截器统一提示
  }
}

/** 自定义头像上传 */
async function handleUpload(options: UploadRequestOptions) {
  const file = options.file as File
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res: any = await request({
      url: '/file/uploadAvatar',
      method: 'post',
      data: formData
    })
    const avatarPath = res.data
    // 更新用户头像到数据库
    await updateUser({ id: form.id, avatar: avatarPath })
    user.value.avatar = avatarPath
    form.avatar = avatarPath
    // 同步更新全局 store，右上角头像随之刷新
    if (userStore.userInfo) {
      userStore.userInfo.avatar = avatarPath
    }
    ElMessage.success('头像更新成功')
  } catch {
    // 错误已由请求拦截器统一提示
  } finally {
    uploading.value = false
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
.profile-page {
  padding: 20px;
}

/* 顶部头部卡片 */
.profile-header-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
  border: none;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 10px 0;
}

.profile-avatar-wrapper {
  position: relative;
  flex-shrink: 0;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: visible;
}

.profile-avatar {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
}

.avatar-status {
  position: absolute;
  bottom: 6px;
  right: 6px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 3px solid #fff;
  z-index: 2;
}

.avatar-status.online {
  background: #67c23a;
}

.avatar-status.offline {
  background: #f56c6c;
}

/* 头像上传按钮 */
.avatar-upload-btn {
  position: absolute;
  top: 0;
  left: 0;
  width: 90px;
  height: 90px;
}

.avatar-upload-btn :deep(.el-upload) {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
}

.upload-mask {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 20px;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.avatar-upload-btn:hover .upload-mask {
  opacity: 1;
}

.profile-header-info {
  flex: 1;
}

.profile-name {
  margin: 0 0 6px 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.profile-username {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #909399;
}

.profile-tags {
  display: flex;
  gap: 8px;
}

.profile-header-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 200px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.meta-item .el-icon {
  color: #c0c4cc;
  font-size: 15px;
}

.meta-email {
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 卡片标题 */
.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.card-title .el-icon {
  color: #409eff;
}

/* 信息列表 */
.info-grid {
  padding: 8px 0;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  width: 90px;
  flex-shrink: 0;
  font-size: 13px;
  color: #909399;
}

.info-value {
  flex: 1;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 表单卡片 */
.form-card :deep(.el-form-item) {
  margin-bottom: 22px;
}

.form-card :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.form-hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 4px;
  line-height: 1;
}

/* 按钮组 */
.form-card :deep(.el-button) {
  border-radius: 8px;
}
</style>
