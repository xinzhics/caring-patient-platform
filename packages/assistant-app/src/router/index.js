import Vue from 'vue'
import Router from 'vue-router'
import {nursingBaseInfo} from '@/api/nursing.js'
import {tenantDetail} from '@/api/tenant.js'
Vue.use(Router)
const originalPush = Router.prototype.push
Router.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

const router = new Router({
  mode: 'history',
  base: '/assistant',
  routes: [
    {
      path: '/index',
      name: '首页',
      component: () => import('@/index')
    },
    {
      path: '/mydoctor',
      name: '我的医生',
      component: () => import('@/pages/myDoctor/myDoctor')
    },
    {
      path: '/mydoctor/groupDetails',
      name: '小组详情',
      component: () => import('@/pages/groupDetails/groupDetails')
    },
    {
      path: '/mydoctor/addDoctor',
      name: '创建医生',
      component: () => import('@/pages/myDoctor/components/adddoctor')
    },
    {
      path: '/mydoctor/doctorDetail',
      name: '医生详情页',
      component: () => import('@/pages/myDoctor/components/doctorDetail')
    },
    {
      path: '/mydoctor/editGroup',
      name: '新增编辑小组',
      component: () => import('@/pages/groupDetails/components/editGroup')
    },
    {
      path: '/newsDispatch',
      name: '消息群发',
      component: () => import('@/pages/newsDispatch/newsDispatch')
    },
    {
      path: '/newsDispatch/patientList',
      name: '消息群发患者列表',
      component: () => import('@/pages/newsDispatch/components/patientList')
    },
    {
      path: '/newsDispatch/content',
      name: '群发内容',
      component: () => import('@/pages/newsDispatch/components/content')
    },
    {
      path: '/newsDispatch/commonWords',
      name: '选择常用语',
      component: () => import('@/pages/newsDispatch/components/commonWords')
    },
    {
      path: '/newsDispatch/commonWordsList',
      name: '常用语列表',
      component: () => import('@/pages/newsDispatch/components/commonWordsList')
    },
    {
      path: '/newsDispatch/addCommonWords',
      name: '编辑/添加常用语',
      component: () => import('@/pages/newsDispatch/components/addCommonWords')
    },
    {
      path: '/newsDispatch/articleDetails',
      name: '文章详情页',
      component: () => import('@/pages/newsDispatch/components/articleDetails')
    },
    {
      path: '/reservation',
      name: '我的预约',
      component: () => import('@/pages/reservation/reservation')
    },
    {
      path: '/reservation/reservationSetUp',
      name: '预约设置',
      component: () => import('@/pages/reservation/components/reservationSetUp')
    },
    {
      path: '/reservation/docReservation',
      name: '医生预约设置',
      component: () => import('@/pages/reservation/components/docReservation')
    },
    {
      path: '/reservation/reviewedList',
      name: '医生待审核列表',
      component: () => import('@/pages/reservation/components/reviewedList')
    },
    {
      path: '/referral',
      name: '患者转诊',
      component: () => import('@/pages/referral/referral')
    },
    {
      path: '/statistics',
      name: '统计分析',
      component: () => import('@/pages/statistics/statistics')
    },
    {
      path: '/patient/center',
      name: '个人中心',
      component: () => import('@/pages/patient/index')
    },
    {
      path: '/patient/injectionCalendar',
      name: '注射日历',
      component: () => import('@/pages/patient/injectionCalendar/index')
    },
    {
      path: '/patient/baseinfo/index',
      name: '基础信息',
      component: () => import('@/pages/patient/baseInfo/index')
    },
    {
      path: '/patient/health/index',
      name: '健康档案',
      component: () => import('@/pages/patient/health/index')
    },
    {
      path: '/patient/healthCalendar/index',
      name: '健康日志',
      component: () => import('@/pages/patient/form/index')
    },
    {
      path: '/patient/form/editor',
      name: '表单编辑',
      component: () => import('@/pages/patient/form/editor')
    },
    {
      path: '/patient/testNumber/index',
      name: '检验数据',
      component: () => import('@/pages/patient/form/index')
    },
    {
      path: '/patient/custom/follow/:planId',
      name: '自定义表单',
      component: () => import('@/pages/patient/form/index')
    },
    {
      path: '/patient/monitor/index',
      name: '监测数据列表',
      component: () => import('@/pages/patient/testdata/index')
    },
    {
      path: '/patient/monitor/glucose',
      name: '血糖监测',
      component: () => import('@/pages/patient/monitor/glucose')
    },
    {
      path: '/patient/monitor/glucoseEditor',
      name: '血糖监测',
      component: () => import('@/pages/patient/monitor/glucoseEditor')
    },
    {
      path: '/patient/monitor/pressure',
      name: '血压监测',
      component: () => import('@/pages/patient/monitor/pressure')
    },
    {
      path: '/patient/monitor/pressureEditor',
      name: '血压数据',
      component: () => import('@/pages/patient/monitor/pressureEditor')
    },
    {
      path: '/patient/monitor/show',
      name: '自定义监测数据',
      component: () => import('@/pages/patient/testdata/customShow')
    },
    {
      path: '/patient/monitor/add',
      name: '添加自定义监测数据',
      component: () => import('@/pages/patient/testdata/customAdd')
    },
    {
      path: '/patient/monitor/horizontalScreen',
      name: '监测数据横向图',
      component: () => import('@/pages/patient/testdata/testDataHorizontalScreen')
    },
    {
      path: '/patient/monitor/editReference',
      name: '修改基准值',
      component: () => import('@/pages/patient/testdata/editReference')
    },
    {
      path: '/patient/medicine/index',
      name: '患者药箱',
      component: () => import('@/pages/patient/medicine/index')
    },
    {
      path: '/reservation/searchDoctor',
      name: '搜索医生',
      component: () => import('@/pages/reservation/components/searchDoctor')
    },
    {
      path: '/patient/medicine/storeroom',
      name: '药品列表',
      component: () => import('@/pages/patient/medicine/storeroom')
    },
    {
      path: '/patient/medicine/detail',
      name: '添加药品',
      component: () => import('@/pages/patient/medicine/detail')
    },
    {
      path: '/patient/medicine/showuser',
      name: '药品使用说明',
      component: () => import('@/pages/patient/medicine/showuser')
    },
    {
      path: '/patient/medicine/addMedicine',
      name: '添加其他药品',
      component: () => import('@/pages/patient/medicine/addmedicine')
    },
    {
      path: '/patient/medicine/history',
      name: '历史用药',
      component: () => import('@/pages/patient/medicine/medicineHistory')
    },
    {
      path: '/patient/calendar/index',
      name: '用药日历',
      component: () => import('@/pages/patient/calender/index')
    },
    {
      path: '/patient/remind/index',
      name: '智能提醒',
      component: () => import('@/pages/patient/remind/index')
    },
    {
      path: '/patient/update/remark',
      name: '修改备注',
      component: () => import('@/pages/patient/patientRemark')
    },
    // 基础信息历史
    {
      path: '/patient/baseinfo/index/history',
      name: '基础信息修改历史',
      component: () => import('@/pages/patient/baseHistory/baseHistory')
    },
    {
      path: '/patient/baseinfo/index/history/detailsHistory',
      name: '基础信息修改历史详情',
      component: () => import('@/pages/patient/detailsHistory/detailsHistory')
    },
    {
      path: '/followUp',
      meta: {
        keepAlive: true
      },
      name: '随访管理',
      component: () => import('@/pages/followUp/index')
    },
    {
      path: '/followUp/glucose',
      meta: {
        keepAlive: true
      },
      name: '随访管理',
      component: () => import('@/pages/followUp/components/followShowGlucose')
    },
    {
      path: '/followUp/statistics',
      meta: {
        keepAlive: true
      },
      name: '随访统计',
      component: () => import('@/pages/followUp/components/statistics')
    },
    {
      path: '/cms/show',
      meta: {
        keepAlive: true
      },
      name: 'cms文章详情',
      component: () => import('@/pages/cms/show')
    },
    {
      path: '/followUp/scheduleForm',
      meta: {
        keepAlive: true
      },
      name: '随访计划表单',
      component: () => import('@/pages/followUp/components/scheduleForm')
    },
    {
      path: '/score/result',
      name: '评分结果',
      component: () => import('@/pages/score/result')
    },
    {
      path: '/score/show',
      name: '评分结果表单',
      component: () => import('@/pages/score/show')
    },
    {
      path: '/health/detail',
      name: '疾病信息详情',
      component: () => import('@/pages/patient/health/index')
    },
    {
      path: '/health/editor',
      name: '添加疾病信息',
      component: () => import('@/pages/patient/health/editor')
    },
    {
      path: '/inspectionData/editor',
      name: '添加检验数据',
      component: () => import('@/pages/patient/inspectionData/editor')
    },
    {
      path: '/inspectionData/showdata',
      name: '查看检验数据',
      component: () => import('@/pages/patient/inspectionData/showdata')
    },
    {
      path: '/healthCalendar/form/result',
      name: '健康日志详情',
      component: () => import('@/pages/patient/healthCalendar/detail')
    },
    {
      path: '/healthCalendar/editor',
      name: '健康日志详情',
      component: () => import('@/pages/patient/healthCalendar/editor')
    },
    {
      path: '/common/commonList',
      name: '常用语列表',
      component: () => import('@/pages/commonWords/commonList')
    },
    {
      path: '/common/commonSetting',
      name: '设置常用语',
      component: () => import('@/pages/commonWords/commonSetting')
    },
    {
      path: '/common/commonSearchResult',
      name: '常用语搜索',
      component: () => import('@/pages/commonWords/commonSearchResult')
    },
    {
      path: '/common/commonTemplate',
      name: '常用语模板库',
      component: () => import('@/pages/commonWords/commonTemplate')
    },
    {
      path: '/common/commonAdd',
      name: '添加常用语',
      component: () => import('@/pages/commonWords/commonAdd')
    },
    {
      path: '/studio/cms/list',
      name: 'cmsList',
      component: () => import('@/pages/studioCms/list.vue')
    },
    {
      path: '/studio/cms/addText',
      name: 'cms-add-text',
      component: () => import('@/pages/studioCms/addTextCms.vue')
    },
    {
      path: '/studio/cms/textDetail',
      name: 'cms-text-detail',
      component: () => import('@/pages/studioCms/textDetail.vue')
    },
    {
      path: '/studio/cms/addVoice',
      name: 'cms-add-voice',
      component: () => import('@/pages/studioCms/addVoiceCms.vue')
    },
    {
      path: '/studio/cms/addVideo',
      name: 'cms-add-video',
      component: () => import('@/pages/studioCms/addVideoCms.vue')
    },
    {
      path: '/studio/cms/voiceDetail',
      name: 'cms-voice-detail',
      component: () => import('@/pages/studioCms/voiceDetail.vue')
    },
    {
      path: '/studio/cms/videoDetail',
      name: 'cms-video-detail',
      component: () => import('@/pages/studioCms/videoDetail.vue')
    },
    {
      path: '/studio/cms',
      name: 'CmsDoctorManager',
      component: () => import('@/pages/studioCms/CMSManagement.vue')
    },
    {
      path: '/studio/cms/articleImport',
      name: 'cms-import',
      component: () => import('@/pages/studioCms/articleList')
    },
    {
      path: '/studio/article/textDetail',
      name: 'article-text-detail',
      component: () => import('@/pages/studioCms/articleTextDetail')
    },
    {
      path: '/studio/article/voiceDetail',
      name: 'article-voice-detail',
      component: () => import('@/pages/studioCms/articleVoiceDetail')
    },
    {
      path: '/studio/article/videoDetail',
      name: 'article-video-detail',
      component: () => import('@/pages/studioCms/articleVideoDetail')
    }
  ]
})

router.beforeEach((to, form, next) => {
  var testToken=undefined;
  var testUserId=undefined;
  var testTenant=undefined;
   
   // 开发环境自动登录配置 2026daxiong 测试用，正式环境注释
 if (process.env.NODE_ENV === 'development' || localStorage.getItem('AUTO_LOGIN_ENABLED')) {
    testToken='eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX3R5cGUiOiJOVVJTSU5HX1NUQUZGIiwibmFtZSI6IuWtmeWKqeeQhiIsInRva2VuX3R5cGUiOiJ0b2tlbiIsInVzZXJpZCI6IjIwMDEwMDAwMDAwMDAwMDAwMTAiLCJhY2NvdW50IjoiMTUxNzM3ODY1NTQiLCJleHAiOjE4MDAxNTQxMzEsIm5iZiI6MTc2OTA1MDEzMX0.Nl9ebv71Z1M6DfgQEjsAaPl41KqOOhFPWEc_JFMdLDk'
    testUserId = '2001000000000000010'
    testTenant = 'TENANT_H5'
 }
  // 先取 路径上的 医助ID 。 路径上没有 ，看本地缓存。 本地缓存也没有，则使用测试数据
  // token 和租户同样
  let NET_WORK_STATUS = localStorage.getItem('NET_WORK_STATUS')
  const currentDevice = getQueryString('currentDevice')
  if (currentDevice) {
    localStorage.setItem('caringCurrentDevice', currentDevice)
  }
  // 如果网络状态是 无网络。则跳转进 无网络页面。
  console.log('beforeEach', to.path)
  if (NET_WORK_STATUS === 'none' && to.path !== '/index') {
    console.log('router NET_WORK_STATUS', NET_WORK_STATUS)
    window.parent.postMessage({action: 'networkErrorToPage'}, '*')
    const caringCurrentDevice = localStorage.getItem('caringCurrentDevice')
    if (caringCurrentDevice === 'weixin') {
    } else {
      return
    }
  }
  const userId = getQueryString('caringNursingId') || testUserId
  if (userId) {
    localStorage.setItem('caringNursingId', userId)
  }
  const token = getQueryString('caringToken') || testToken
  if (token) {
    localStorage.setItem('caringToken', token)
  }

  const tenant = getQueryString('tenantCode') || testTenant
  if (tenant) {
    localStorage.setItem('tenantCode', tenant)
  }
  if (!localStorage.getItem('nursingBaseInfo')) {
    nursingBaseInfo(localStorage.getItem('caringNursingId'))
  }
  if (!localStorage.getItem('projectInfo') && localStorage.getItem('tenantCode')) {
    tenantDetail().then(res => {
      localStorage.setItem('projectInfo', JSON.stringify(res.data))
      localStorage.setItem('projectCode', res.data.code)
      next()
    })
  } else {
    next()
  }
})

function getQueryString (name) {
  let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)')
  let r = decodeURI(window.location.search.substr(1)).match(reg)
  if (r != null) return unescape(r[2])
  return null
}
export default router
