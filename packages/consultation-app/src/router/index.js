import Vue from 'vue'
import Router from 'vue-router'
import axios from 'axios'
import {Base64} from 'js-base64'

import consultation from '../view/consultation/index'//群组首页
import consultationImIndex from '@/view/consultation/consultationChat'//会诊聊天
import consultationDetail from '@/view/consultation/consultationDetail'//会诊聊天详情

Vue.use(Router)

const router = new Router({
  mode: 'history',
  base:  '/consultationDoctor',
  routes: [
    {
      path: '/consultation/index/:groupId',
      name: '会诊入口',
      component: consultation
    },
    {
      path: '/consultation/chat',
      name: '会诊聊天',
      component: consultationImIndex
    },
    {
      path: '/consultationDetail',
      name: '会诊详情',
      component: consultationDetail
    },
  ]
})
router.beforeEach((to, from, next) => {
  console.log(to.name, from.name)
  if(!localStorage.getItem('headerTenant') || !localStorage.getItem('pageTitle')){
    initPage(()=>{
      next()
    })
  }else{
    if(localStorage.getItem('pageTitle')) {
      window.document.title = localStorage.getItem('pageTitle')
    }
    next()
  }


//初始化的函数  获取到headerTenant和wxAppId
  function initPage(callback) {
    var s = window.location.href;
    var h = s.split(".")[0];
    var a = h.split("//")[1];
    axios.get(`${process.env.NODE_ENV === 'development' ? 'https://dev-api.example.com' : 'https://api.example.com'}/api/tenant/tenant/anno/getByDomain?domain=`+ a).then(res => {
      if (res.data.code === 0) {
        if (res.data.data) {
          localStorage.setItem('Code', res.data.data.code)
          localStorage.setItem('officialAccountType', res.data.data.officialAccountType)
          localStorage.setItem('headerTenant', Base64.encode(res.data.data.code))
          localStorage.setItem('wxAppId', res.data.data.wxAppId)
          localStorage.setItem('pageTitle', res.data.data.name)
        }
        callback()
      } else {
        console.log('初始化有问题！')
      }
    })
  }
})

export default router;
