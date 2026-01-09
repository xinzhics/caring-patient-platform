import Vue from 'vue';
import { ToastPlugin } from 'vux'
import VueRouter from 'vue-router'
import { Toast } from 'vant'
import router from '../router/index'
import apiUrl from "./baseUrl";

Vue.use(ToastPlugin)
Vue.use(VueRouter)
let vm = new Vue({router})
// 请求添加条件，如token
axios.interceptors.request.use(
  config => {
    config.headers['Authorization']  = 'Basic enVpaG91X3VpOnp1aWhvdV91aV9zZWNyZXQ='
    config.headers['tenant']  = localStorage.getItem('headerTenant')
    config.headers['token']  = 'Bearer '+localStorage.getItem('token');
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

function handleError(error, reject) {
  if (error.code === 'ECONNABORTED') {
    Toast('请求超时!')
  } else if (error.response && error.response.data) {
    if (error.response.data.code) {
      const code = error.response.data.code
      if (code === 40005 || code === 40006 || code === 40007 ||
        code === 40008 || code === 40000 ||
        code === 40001 || code === 40002 ||
        code === 40003 || code === 401 || code === 40004) {
        wxAuthorize()
        return
      }
    }
    if (error.response.data.msg) {
      Toast(error.response.data.msg)
    } else if (error.response.data.message) {
      Toast(error.response.data.message)
    }
  } else if (error.message) {
    if (error.message !== 'Network Error') {
      Toast(error.message)
    }
  }
  reject(error)
}

//未授权过患者进行授权
function wxAuthorize() {

  // 项目是个人服务号的项目。直接去患者登录页面
  const officialAccountType = localStorage.getItem('officialAccountType')
  if (officialAccountType && officialAccountType === 'PERSONAL_SERVICE_NUMBER') {
    vm.$router.replace('/patient/login')
    return
  }

  const s = window.location.href;
  const h = s.split(".")[0];
  const a = h.split("//")[1];
  localStorage.removeItem('userId')
  localStorage.removeItem('token')
  window.location.href = apiUrl + '/wx/wxUserAuth/anno/getWxUserCode?domain=' + a + '&redirectUri=' + encodeURIComponent(s)
}

function handleSuccess(res, resolve) {

  if (res.data.isError) {
    // 未登录
    // debugger
    Toast.clear
    if (res.data.code === 40000 || res.data.code === 40001 ||
      res.data.code === 40002 || res.data.code === 40003 ||
      res.data.code === 40005 || res.data.code === 40006 ||
      res.data.code === 40008) {
        Toast(res.data.msg)
        vm.$vux.alert.show({
          content: res.data.msg,
          buttonText: '确定',
          maskZIndex: 100,
          onShow () {
            localStorage.setItem('headerTenant', '')
            localStorage.setItem('wxAppId', '')
            localStorage.setItem('userId', '')
            localStorage.setItem('token', '')
            localStorage.setItem('renovate', '')
            localStorage.setItem('routerData', '')
            localStorage.setItem('styleDate', '')
            vm.$router.replace('/')
          },
          onHide () {

          }
        })
    } else {
      //当患者不存在时重新授权
      if (res.data.msg === '患者不存在') {
        wxAuthorize()
      }else {
        Toast(res.data.msg)
      }
    }
  }
  resolve(res)
}

// http请求
const httpServer = (opts) => {
  // 公共参数
  const publicParams = {
    ts: Date.now()
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
    timeout: 20000
  }
  const dataRequest = ['PUT', 'POST', 'PATCH']
  if (method.includes('PUT') && opts.data && Array.isArray(opts.data.ids)) {
    httpDefaultOpts.data = opts.data.ids ? opts.data.ids : []
  }else if (dataRequest.includes(method)) {
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

  const promise = new Promise((resolve, reject) => {
    axios(httpDefaultOpts).then(response => {
      handleSuccess(response, resolve)
    }).catch(error => {
      handleError(error, reject)
    })
  })
  return promise
}

export default httpServer
