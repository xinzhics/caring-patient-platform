import Vue from 'vue';
import {ToastPlugin} from 'vux'
import axios from 'axios'
import VueRouter from 'vue-router'
// import { Message, MessageBox } from 'element-ui'
import db from '@/api/localstorage'
// import { Base64 } from 'js-base64'
import router from '../router/index'

Vue.use(ToastPlugin)
Vue.use(VueRouter)
let vm = new Vue({router})

// 请求添加条件，如token
axios.interceptors.request.use(
  config => {
    // const isToken = config.headers['X-isToken'] === false ? config.headers['X-isToken'] : true
    // const token = db.get('TOKEN', '')
    // if (token && isToken) {
    //   config.headers.token = 'Bearer ' + token
    // }

    // const clientId = process.env.VUE_APP_CLIENT_ID
    // const clientSecret = process.env.VUE_APP_CLIENT_SECRET
    // config.headers['Authorization'] = `Basic ${Base64.encode(`${clientId}:${clientSecret}`)}`
    config.headers['Authorization'] = 'Basic enVpaG91X3VpOnp1aWhvdV91aV9zZWNyZXQ='
    config.headers['tenant'] = localStorage.getItem('headerTenant')
    config.headers['token'] = 'Bearer ' + localStorage.getItem('consultationtoken');
    // console.log(config)
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
    // debugger
    vm.$vux.toast.text('请求超时', 'cneter');
  } else if (error.response && error.response.data) {
    if (error.response.data.msg) {
      vm.$vux.toast.text(error.response.data.msg, 'cneter');

    } else if (error.response.data.message) {
      vm.$vux.toast.text(error.response.data.message, 'cneter');
    }
  } else if (error.message) {
    vm.$vux.toast.text(error.message, 'cneter');
  }
  reject(error)
}

function handleSuccess(res, resolve) {
  if (res.data.isError) {
    // 未登录
    // debugger
    if (res.data.code === 40000 || res.data.code === 40001 ||
      res.data.code === 40002 || res.data.code === 40003 ||
      res.data.code === 40005 || res.data.code === 40006 ||
      res.data.code === 40008) {
      // vm.$vux.toast.text(res.data.msg,'cneter');
      vm.$vux.alert.show({
        content: res.data.msg,
        buttonText: '确定',
        maskZIndex: 100,
        onShow() {
          localStorage.setItem('headerTenant', '')
          localStorage.setItem('consultationUserId', '')
          localStorage.setItem('consultationtoken', '')
          let groupId = localStorage.getItem("groupId")
          vm.$router.replace('/consultation/index/' + groupId)
        },
        onHide() {

        }
      })
      console.log('token 过期')
    } else if (res.data.code === -10) {
      resolve(res)
    } else {
      vm.$vux.toast.text(res.data.msg, 'top');
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

  const promise = new Promise((resolve, reject) => {
    axios(httpDefaultOpts).then(response => {
      // console.log(response,'成功进入判断')
      handleSuccess(response, resolve)
    }).catch(error => {
      // console.log(error,'失败进入判断')
      handleError(error, reject)
    })
  })
  return promise
}

export default httpServer
