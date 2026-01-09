/**
 * HTTP请求封装
 */
import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || process.env.VITE_AXIOS_BASE_URL,
  timeout: 15000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json; charset=utf-8'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加认证token
    const token = localStorage.getItem('caring-token')
    if (token) {
      config.headers.token = `Bearer ${token}`
    }
    
    // 添加客户端认证
    config.headers['Authorization'] = `Basic Y2FyaW5nX3VpOmNhcmluZ191aV9zZWNyZXQ=`
    
    // 添加租户信息
    const tenant = localStorage.getItem('caring-tenant')
    if (tenant) {
      config.headers.tenant = tenant
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { data } = response
    
    // 处理业务成功状态
    if (data.code === 200 || response.status === 200) {
      return data
    }
    
    // 处理业务错误
    switch (data.code) {
      case 401:
        // 登录失效
        localStorage.clear()
        window.location.href = '/login'
        break
      case 403:
        // 权限不足
        console.error('权限不足')
        break
      default:
        break
    }
    
    return Promise.reject(new Error(data.msg || '请求失败'))
  },
  error => {
    const { response } = error
    
    if (response) {
      switch (response.status) {
        case 401:
          localStorage.clear()
          window.location.href = '/login'
          break
        case 403:
          console.error('权限不足')
          break
        case 404:
          console.error('资源不存在')
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error('网络错误')
          break
      }
    }
    
    return Promise.reject(error)
  }
)

// 请求方法封装
const http = {
  get(url, params = {}) {
    return request.get(url, { params })
  },
  
  post(url, data = {}) {
    return request.post(url, data)
  },
  
  put(url, data = {}) {
    return request.put(url, data)
  },
  
  delete(url, params = {}) {
    return request.delete(url, { params })
  },
  
  upload(url, formData) {
    return request.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export default http