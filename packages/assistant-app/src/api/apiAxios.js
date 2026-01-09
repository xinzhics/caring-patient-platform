import Vue from 'vue'
import axios from 'axios'
import { Toast } from 'vant'
import router from '../router'

Vue.use(Toast)

// 请求添加条件，如token
axios.interceptors.request.use(
  config => {
    config.headers['Authorization'] = 'Basic enVpaG91X3VpOnp1aWhvdV91aV9zZWNyZXQ='
    config.headers['tenant'] = localStorage.getItem('tenantCode')
    config.headers['token'] = 'Bearer ' + localStorage.getItem('caringToken')
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 接口返回处理
axios.interceptors.response.use(
  response => {
    return response
  },
  error => {
    return Promise.reject(error)
  }
)

function handleError (error, resolve) {
  // return
  // 为false 界面不显示异常
  if (error.code === 'ECONNABORTED') {
    // debugger
  } else if (error.response && error.response.data && error.config.showToast) {
    if (error.response.data.msg) {
      Toast.fail({message: error.response.data.msg, position: 'bottom'})
    } else if (error.response.data.message) {
      Toast.fail({message: error.response.data.message, position: 'bottom'})
    }
  } else if (error.message && error.config.showToast) {
    if (error.message !== 'Network Error') {
      Toast.fail({message: error.message, position: 'bottom'})
    }
  }
  resolve(error.response.data)
}

function handleSuccess (res, resolve) {
  if (res.data.isError) {
    // 未登录
    if (res.data.code === 40000 || res.data.code === 40001 ||
      res.data.code === 40002 || res.data.code === 40003 ||
      res.data.code === 40005 || res.data.code === 40006 ||
      res.data.code === 40008) {
      window.parent.postMessage({action: 'tokenTimeOut'}, '*')
    } else if (res.data.code === -10) {
      resolve(res)
    } else {
      console.log(123)
      return
    }
  }
  resolve(res.data)
}

// http请求
const httpServer = (opts) => {
  // 公共参数
  const publicParams = {
    ts: Date.now()
  }
  let NET_WORK_STATUS = localStorage.getItem('NET_WORK_STATUS')
  // 如果网络状态是 无网络。则跳转进 无网络页面。
  if (NET_WORK_STATUS === 'none') {
    router.push({
      path: '/index',
      query: {
        form: 'api',
        network: '1'
      }
    })
    return new Promise((resolve) => {
      handleSuccess({data: {code: -1}}, resolve)
    })
  }
  // http默认配置
  const method = opts.method.toUpperCase()
  // baseURL
  // 开发环境： /api                 // 开发环境在 vue.config.js 中有 devServer.proxy 代理
  // 生产环境： http://IP:PORT/api   // 生产环境中 代理失效， 故需要配置绝对路径
  const httpDefaultOpts = {
    method,
    baseURL: process.env.VUE_APP_PROD_REQUEST_DOMAIN_PREFIX + process.env.VUE_APP_BASE_API,
    url: opts.url,
    responseType: opts.responseType || '',
    timeout: 20000,
    showToast: !opts.showToast
  }
  const dataRequest = ['PUT', 'POST', 'PATCH']
  if (dataRequest.includes(method)) {
    httpDefaultOpts.data = opts.data || {}
  } else {
    httpDefaultOpts.params = {
      ...publicParams,
      ...(opts.data || {})
    }
  }

  // formData转换
  if (opts.formData) {
    httpDefaultOpts.transformRequest = [data => {
      const formData = new FormData()
      if (data) {
        Object.entries(data).forEach(item => {
          formData.append(item[0], item[1])
        })
      }
      return formData
    }]
  }

  const promise = new Promise((resolve) => {
    axios(httpDefaultOpts).then(response => {
      // console.log(response,'成功进入判断')
      handleSuccess(response, resolve)
    }).catch(error => {
      // console.log(error,'失败进入判断')
      handleError(error, resolve)
    })
  })
  return promise
}

export default httpServer
