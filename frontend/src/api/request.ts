import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 清理请求体中的空字符串（转为 undefined，避免后端唯一约束冲突）
function cleanEmptyStrings(data: any): any {
  if (!data || typeof data !== 'object' || Array.isArray(data)) return data
  const cleaned = { ...data }
  for (const key of Object.keys(cleaned)) {
    if (cleaned[key] === '') {
      delete cleaned[key]
    }
  }
  return cleaned
}

// 请求拦截器：自动带 token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // POST/PUT 请求体清理空字符串（跳过 FormData 文件上传）
    if (config.data instanceof FormData) {
      // FormData 由浏览器自动设置带 boundary 的 Content-Type
      delete config.headers['Content-Type']
    } else if (config.data && typeof config.data === 'object') {
      config.data = cleanEmptyStrings(config.data)
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg))
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error('网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
