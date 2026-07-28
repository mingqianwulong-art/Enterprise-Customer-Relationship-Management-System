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
              <span class="feature-icon">📊</span>
              <span>智能数据看板</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🤝</span>
              <span>全流程客户管理</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">📈</span>
              <span>销售漏斗分析</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 右侧登录表单 -->
      <div class="login-form-wrapper">
        <div class="login-form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号和密码</p>
        </div>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              class="login-btn"
              type="primary"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

/** 登录处理 */
async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.loginAction(loginForm)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } catch (error: any) {
      ElMessage.error(error?.message || '登录失败，请检查账号和密码')
    } finally {
      loading.value = false
    }
  })
}
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
  min-height: 520px;
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

/* 右侧登录表单 */
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
  margin-bottom: 36px;
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

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}
</style>
