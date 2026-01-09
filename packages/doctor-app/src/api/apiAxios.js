import Vue from 'vue';
import {ToastPlugin} from 'vux'
import VueRouter from 'vue-router'
import router from '../router/index'

Vue.use(ToastPlugin)
Vue.use(VueRouter)
let vm = new Vue({router})

// 请求添加条件，如token
axios.interceptors.request.use(
  config => {
    // const clientId = process.env.VUE_APP_CLIENT_ID
    // const clientSecret = process.env.VUE_APP_CLIENT_SECRET
    // config.headers['Authorization'] = `Basic ${Base64.encode(`${clientId}:${clientSecret}`)}`
    config.headers['Authorization']  = 'Basic enVpaG91X3VpOnp1aWhvdV91aV9zZWNyZXQ='
    config.headers['tenant']  = localStorage.getItem('headerTenant')
    config.headers['token']  = 'Bearer '+ localStorage.getItem('doctortoken');
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
    vm.$vux.toast.text('请求超时','cneter');
  } else if (error.response && error.response.data) {
    if (error.response.data.msg) {
      // 医生token 过期校验
      if (error.response.data.code === 40000 || error.response.data.code === 40001 ||
        error.response.data.code === 40002 || error.response.data.code === 40003 ||
        error.response.data.code === 40005 || error.response.data.code === 40006 ||
        error.response.data.code === 40008) {
        // vm.$vux.toast.text(res.data.msg,'cneter');
        vm.$vux.alert.show({
          content: error.response.data.msg,
          buttonText: '确定',
          maskZIndex: 100,
          onShow () {
            localStorage.removeItem('caring_doctor_id')
            localStorage.removeItem('doctortoken')
            vm.$router.replace('/')
          },
          onHide () {

          }
        })
      } else {
        let data = JSON.parse(error.response.config.data)
        if (!data || !data.isToast) {
          vm.$vux.toast.text(error.response.data.msg,'cneter');
        }
      }

    } else if (error.response.data.message) {
      vm.$vux.toast.text(error.response.data.message,'cneter');
    }
  } else if (error.message) {
    if (error.message !== 'Network Error') {
      vm.$vux.toast.text(error.message,'cneter');
    }
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
          onShow () {
            localStorage.removeItem('caring_doctor_id')
            localStorage.removeItem('doctortoken')
            vm.$router.replace('/')
          },
          onHide () {

          }
        })
    } else if(res.data.code === -10){
      resolve(res)
    }else{
      vm.$vux.toast.text(res.data.msg,'top');
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
  const httpDefaultOpts = {
    method,
    baseURL: process.env.VUE_APP_PROD_REQUEST_DOMAIN_PREFIX + process.env.VUE_APP_BASE_API,
    url: opts.url,
    responseType: opts.responseType || '',
    timeout: 20000
  }
  const dataRequest = ['PUT', 'POST', 'PATCH']
  // 请求参数处理
  // 方法为PUT或 DELETE 且有ids参数时，单独处理ids参数, 将ids参数转为数组形式
  if (method.includes('PUT') && opts.data && Array.isArray(opts.data.ids)) {
    httpDefaultOpts.data = opts.data.ids ? opts.data.ids : []
  }else if (method.includes('DELETE') && opts.data && Array.isArray(opts.data.ids)) {
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

  return new Promise((resolve, reject) => {
    axios(httpDefaultOpts).then(response => {
      // console.log(response,'成功进入判断')
      handleSuccess(response, resolve)
    }).catch(error => {
      // console.log(error,'失败进入判断')
      handleError(error, reject)
    })
  })
}

export default httpServer
