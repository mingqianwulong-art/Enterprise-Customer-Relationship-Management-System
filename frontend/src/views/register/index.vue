<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧品牌区 -->
      <div class="login-banner">
        <div class="banner-content">
          <h1 class="banner-title">企业客户关系管理系统</h1>
          <p class="banner-subtitle">Enterprise CRM</p>
          <div class="banner-features">
            <div class="feature-item">
              <span class="feature-icon">📝</span>
              <span>手机号注册验证</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🔐</span>
              <span>短信验证码保护</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">📧</span>
              <span>邮箱可选唯一绑定</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧注册表单 -->
      <div class="login-form-wrapper">
        <div class="login-form-header">
          <h2>用户注册</h2>
          <p>请填写以下信息完成注册</p>
        </div>
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="8-20位，含大小写字母、数字和特殊字符"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              :prefix-icon="Phone"
              maxlength="11"
            />
          </el-form-item>
          <el-form-item prop="code">
            <div class="code-row">
              <el-input
                v-model="registerForm.code"
                placeholder="请输入短信验证码"
                :prefix-icon="Message"
                maxlength="6"
              />
              <el-button
                type="primary"
                :disabled="countdown > 0"
                :loading="sendingCode"
                class="code-btn"
                @click="handleSendCode"
              >
                {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱（选填，但需唯一）"
              :prefix-icon="Promotion"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              class="login-btn"
              type="primary"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '注 册' }}
            </el-button>
          </el-form-item>
          <div class="register-footer">
            已有账号？<el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Phone, Message, Promotion } from '@element-plus/icons-vue'
import { sendSmsCode, register } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)
let timer: ReturnType<typeof setInterval> | null = null

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  code: '',
  email: ''
})

/** 确认密码校验 */
const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^\w\s]).{8,20}$/,
      message: '密码必须8-20位，且包含大小写字母、数字和特殊字符',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码为6位数字', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

/** 发送验证码 */
async function handleSendCode() {
  if (!registerFormRef.value) return
  // 单独校验手机号
  try {
    await registerFormRef.value.validateField('phone')
  } catch {
    return
  }
  sendingCode.value = true
  try {
    await sendSmsCode(registerForm.phone)
    ElMessage.success('验证码已发送，请查收短信')
    // 开始60秒倒计时
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        if (timer) {
          clearInterval(timer)
          timer = null
        }
      }
    }, 1000)
  } catch (error: any) {
    ElMessage.error(error?.message || '验证码发送失败')
  } finally {
    sendingCode.value = false
  }
}

/** 注册 */
async function handleRegister() {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await register({
        username: registerForm.username,
        password: registerForm.password,
        phone: registerForm.phone,
        email: registerForm.email || undefined,
        code: registerForm.code
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (error: any) {
      ElMessage.error(error?.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-wrapper {
  display: flex;
  width: 960px;
  min-height: 580px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

/* 左侧品牌区 */
.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.login-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -30%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
}

.login-banner::after {
  content: '';
  position: absolute;
  bottom: -20%;
  left: -10%;
  width: 260px;
  height: 260px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
}

.banner-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.banner-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.banner-subtitle {
  font-size: 16px;
  margin: 0 0 40px;
  opacity: 0.8;
  letter-spacing: 4px;
  text-transform: uppercase;
}

.banner-features {
  display: flex;
  flex-direction: column;
  gap: 18px;
  text-align: left;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  opacity: 0.9;
}

.feature-icon {
  font-size: 20px;
}

/* 右侧表单 */
.login-form-wrapper {
  flex: 1;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 50px;
}

.login-form-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-form-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px;
}

.login-form-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.register-form {
  width: 100%;
}

/* 验证码行 */
.code-row {
  display: flex;
  gap: 10px;
  width: 100%;
}

.code-btn {
  flex-shrink: 0;
  width: 130px;
}

.login-btn {
  width: 100%;
  margin-top: 4px;
}

/* 底部链接 */
.register-footer {
  text-align: center;
  font-size: 14px;
  color: #606266;
}
</style>
