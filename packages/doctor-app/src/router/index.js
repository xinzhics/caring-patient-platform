import Vue from 'vue'
import Router from 'vue-router'
import {Base64} from 'js-base64'
import axios from 'axios' // 添加axios导入，因为后面用到了
import baseUrl from '../api/baseUrl.js'
import Api from '../api/doctor.js'
import doctorInfoDetail from '@/view/infoDetail/index'
Vue.use(Router)

// 定义路由数组
const routes = [{
  path: '/',
  name: '登录界面',
  component: () => import('@/view/login/index')
},
  {
    path: '/index',
    name: '首页',
    component: () => import('@/view/Home')
  },
  {
    path: '/scanningReferral/index',
    name: '扫描二维码转诊',
    component: () => import('@/view/scanningReferral/index')
  },
  {
    path: '/doctorInfoDetail',
    name: '个人资料',
    component: doctorInfoDetail
  },
  {
    path: '/patientMetastasis/index',
    name: '患者转移',
    component: () => import('@/view/patientMetastasis/index')
  },
  {
    path: '/patientMetastasis/transtPatient',
    name: '患者转移',
    component: () => import('@/view/patientMetastasis/transtPatient')
  },
  {
    path: '/mypatient',
    name: '我的患者',
    component: () => import('@/view/mypatient/index')
  },
  {
    path: '/mypatient/GroupIdPatientList',
    name: '小组患者列表',
    component: () => import('@/view/mypatient/GroupIdPatientList')
  },
  {
    path: '/mypatient/list',

    name: '我的患者列表',
    component: () => import('@/view/mypatient/list')
  },
  {
    path: '/mypatient/addteam',
    name: '新建分组',
    component: () => import('@/view/mypatient/addTeam')
  },
  {
    path: '/mypatient/patientList',
    name: '患者分组',
    component: () => import('@/view/mypatient/patientList')
  },
  {
    path: '/massSend/selectPerson',
    name: '群发消息',
    component: () => import('@/view/massSend/index')
  },
  {
    path: '/massSend',
    name: '群发消息历史',
    component: () => import('@/view/massSend/index')
  },
  {
    path: '/massSend/edit',
    name: '群发消息编辑',
    component: () => import('@/view/massSend/edit')
  },
  {
    path: '/scanImg',
    name: '分享二维码',
    component: () => import('@/view/scanImg/index')
  },
  {
    path: '/mypatientHome',
    name: '患者主页',
    component: () => import('@/view/mypatientHome/index')
  },
  {
    path: '/statistics',
    name: '统计',
    component: () => import('@/view/statistics/index')
  },

  {
    path: '/reservation/index',
    name: '我的预约',
    component: () => import('@/view/reservation/index')
  },
  {
    path: '/reservation/set',
    name: '预约设置',
    component: () => import('@/view/reservation/set')
  },

  {
    path: '/baseinfo/index',
    name: '基础信息',
    component: () => import('@/view/baseinfo/index')
  },
  {
    path: "/baseinfo/index/history",
    name: '基础信息修改历史',
    component: () => import('@/view/baseHistory/baseHistroy.vue')
  },
  {
    path: "/baseinfo/index/history/detailsHistory",
    name: '基础信息修改历史详情',
    component: () => import('@/view/detailsHistory/detailsHistory.vue')
  },
  {
    path: '/calendar/index',
    name: '用药日历',
    component: () => import('@/view/calendar/index')
  },
  {
    path: '/remind/index',
    name: '智能提醒',
    component: () => import('@/view/remind/index')
  },
  {
    path: '/medicine/index',
    name: '我的药箱',
    component: () => import('@/view/medicine/index')
  },
  {
    path: '/medicine/storeroom',
    name: '药品库存',
    component: () => import('@/view/medicine/storeroom')
  },
  {
    path: '/medicine/detail',
    name: '药品详情',
    component: () => import('@/view/medicine/detail')
  },
  {
    path: '/medicine/show',
    name: '使用说明',
    component: () => import('@/view/medicine/show')
  },
  {
    path: '/medicine/showuser',
    name: '药物使用说明',
    component: () => import('@/view/medicine/showuser')
  },
  {
    path: '/medicine/addmedicine',
    name: '添加药物',
    component: () => import('@/view/medicine/add')
  },
  {
    path: '/testNumber/index',
    name: '检测数据',
    component: () => import('@/view/testNumber/index')
  },
  {
    path: '/testNumber/showdata',
    name: '单条数据详情',
    component: () => import('@/view/testNumber/showdata')
  },
  {
    path: '/testNumber/editor',
    name: '单条数据编辑',
    component: () => import('@/view/testNumber/editor')
  },
  {
    path: '/monitor/horizontalScreen',
    name: '监测数据横屏',
    component: () => import('@/view/testdata/TestDataHorizontalScreen')
  },
  {
    path: '/healthCalendar/index',
    name: '健康日志',
    component: () => import('@/view/healthCalendar/index')
  },
  {
    path: '/healthCalendar/showdata',
    name: '健康日志详情',
    component: () => import('@/view/healthCalendar/showdata')
  },
  {
    path: '/healthCalendar/editor',
    name: '健康日志编辑',
    component: () => import('@/view/healthCalendar/editor')
  },
  {
    path: '/healthCalendar/form/result',
    name: '健康日志详情',
    component: () => import('@/view/healthCalendar/detail')
  },
  {
    path: '/im/index',
    name: '聊天',
    component: () => import('@/view/im/index')
  },

  {
    path: '/im/messageList',
    name: '医患互动',
    component: () => import('@/view/im/messageList')
  },

  {
    path: '/im/messageSetting',
    name: '聊天设置',
    component: () => import('@/view/im/messageSetting')
  },
  {
    path: '/im/messageDetail',
    name: '聊天详情',
    component: () => import('@/view/im/messageDetail')
  },
  {
    path: '/health/index',
    name: '健康档案',
    component: () => import('@/view/health/index')
  },

  {
    path: '/health/editor',
    name: '健康档案编辑',
    component: () => import('@/view/health/editor')
  },

  {
    path: '/cms/index',
    name: 'cms文章显示',
    meta: {
      pageName: 'cms',
      keepAlive:true
    },
    component: () => import('@/view/cms/index')
  },
  {
    path: '/cms/show',
    name: 'cms文章详情',
    meta: {
      pageName: 'cms'
    },
    component: () => import('@/view/cms/show')
  },
  {
    path: '/cms/save',
    name: '我的收藏',
    meta: {
      pageName: 'cms'
    },
    component: () => import('@/view/cms/save')
  },
  {
    path: '/common/commonList',
    name: '常用语列表',
    component: () => import('@/view/commonWords/commonList')
  },
  {
    path: '/common/commonSetting',
    name: '设置常用语',
    component: () => import('@/view/commonWords/commonSetting')
  },
  {
    path: '/common/commonSearchResult',
    name: '常用语搜索',
    component: () => import('@/view/commonWords/commonSearchResult')
  },
  {
    path: '/common/commonTemplate',
    name: '常用语模板库',
    component: () => import('@/view/commonWords/commonTemplate')
  },
  {
    path: '/cms/cmsPatientList',
    name: '患教文章',
    component: () => import('@/view/patientCMS/cmsPatientList')
  },
  {
    path: '/cms/cmsPatientDetail',
    name: '患教文章详情',
    component: () => import('@/view/patientCMS/cmsPatientDetail')
  },
  {
    path: '/common/commonAdd',
    name: '添加常用语',
    component: () => import('@/view/commonWords/commonAdd')
  },
  {
    path: '/custom/follow/:planId',
    name: '随访',
    component: () => import('@/view/customFollow/index')
  },
  {
    path: '/followUp/scheduleForm',
    name: '随访计划表单',
    component: () => import('@/view/followUp/components/scheduleForm')
  },
  {
    path:'/followUp/statistics',
    name:'随访统计',
    component: () => import('@/view/followUp/components/statistics')
  },
  {
    path: '/custom/follow/:planId/editor',
    name: '添加随访',
    component: () => import('@/view/customFollow/editor')
  },
  {
    path: '/custom/follow/:planId/calendar/editor',
    name: '添加随访',
    component: () => import('@/view/customFollow/editor')
  },
  {
    path: '/historyPage/index',
    name: '历史推送',
    meta: {
      pageName: 'historyPage'
    },
    component: () => import('@/view/history/index')
  },
  {
    path: '/monitor/index',
    name: '监测数据',
    component: () => import('@/view/testdata/index')
  },
  {
    path: '/monitor/show',
    name: '监测数据查看数据',
    component: () => import('@/view/testdata/show')
  }, {
    path: '/monitor/editReference',
    name: '修改目标值和基本值',
    component: () => import('@/view/testdata/editReference')
  },
  {
    path: '/monitor/add',
    name: '监测数据编辑',
    component: () => import('@/view/testdata/add')
  },
  {
    path: '/monitor/pressure',
    name: '血压监测',
    component: () => import('@/view/monitor/pressure')
  },
  {
    path: '/monitor/pressureEditor',
    name: '血压监测编辑',
    component: () => import('@/view/monitor/pressureEditor')
  },
  {
    path: '/monitor/glucose',
    name: '血糖监测主页',
    component: () => import('@/view/monitor/glucose')
  },
  {
    path: '/monitor/glucoseEditor',
    name: '血糖监测编辑',
    component: () => import('@/view/monitor/glucoseEditor')
  },
  {
    path: '/saasDoctorApply',
    name: '医生申请',
    component: () => import('@/view/apply/doctorApply')
  },
  {
    path: "/submitted/index",
    name: "提交申请",
    component: () => import('@/view/submitted/index')
  },
  {
    path: '/medicine/history',
    name: '历史用药记录',
    component: () => import('@/view/medicine/medicineHistory')
  },
  {
    path: '/consultation/index/:state',
    name: '病例讨论首页',
    component: () => import('@/view/consultation/index')
  },
  {
    path: '/consultation/add',
    name: '病例讨论添加',
    component: () => import('@/view/consultation/add')
  },
  {
    path: '/consultation/patient',
    name: '病例讨论添加患者',
    component: () => import('@/view/consultation/patientList')
  },
  {
    path: '/consultation/message',
    name: '病例讨论消息列表',
    component: () => import('@/view/consultation/messageList')
  },
  {
    path: '/consultation/detail',
    name: '病例讨论详情',
    component: () => import('@/view/consultation/detail')
  },
  {
    path: '/consultation/qrCode',
    name: '病例讨论二维码',
    component: () => import('@/view/consultation/qrCode')
  },
  {
    path: '/consultation/doctorList',
    name: '病例讨论选择医生',
    component: () => import('@/view/consultation/doctorList')
  },
  {
    path: '/consultation/member',
    name: '病例讨论选择医生',
    component: () => import('@/view/consultation/consultationMember')
  },
  {
    path: '/sysMessage',
    name: '系统消息',
    component: () => import('@/view/system/index')
  },{
    path: '/aiMessage',
    name: 'AI助手',
    component: () => import('@/view/ai/aiMessage')
  },{
    path: '/baiduAiMessage',
    name: 'AI助手',
    component: () => import('@/view/ai/baiduAiMessage')
  },{
    path: '/followUp',
    name: '随访首页',
    component: () => import('@/view/followUp/index')
  },{
    path: '/followUp/glucose',
    name: '随访管理',
    component: () => import('@/view/followUp/components/followShowGlucose')
  },
  {
    path:'/reservation/reviewedList',
    name:'医生待审核列表',
    component:() => import('@/view/reservation/reviewedList')
  },
  {
    path:'/register/index',
    name:'医生注册',
    component:() => import('@/view/register/index')
  },
  {
    path: '/score/result',
    name: '评分结果',
    component: () => import('@/view/score/result')
  },
  {
    path: '/score/show',
    name: '评分结果表单',
    component: () => import('@/view/score/show')
  },
  {
    path: '/health/detail',
    name: '疾病信息列表',
    component: () => import('@/view/health/index')
  },
  {
    path: '/doctorInfoDetail/resetPsd',
    name: '重置密码',
    component: () => import('@/view/infoDetail/resetPsd')
  },
  {
    path: '/doctorInfoDetail/updatePsd',
    name: '修改密码',
    component: () => import('@/view/infoDetail/updatePsd')
  },
  {
    path: '/studio/cms',
    name: 'cms-list',
    component: () => import('@/view/studioCms/list'),
  },
  {
    path: '/studio/cms/articleImport',
    name: 'cms-import',
    component: () => import('@/view/studioCms/articleList'),
  },
  {
    path: '/studio/cms/addText',
    name: 'cms-add-text',
    component: () => import('@/view/studioCms/addTextCms'),
  },
  {
    path: '/studio/cms/addVoice',
    name: 'cms-add-voice',
    component: () => import('@/view/studioCms/addVoiceCms'),
  },
  {
    path: '/studio/cms/addVideo',
    name: 'cms-add-video',
    component: () => import('@/view/studioCms/addVideoCms'),
  },
  {
    path: '/studio/cmsList',
    name: 'cmsList',
    component: () => import('@/view/studioCms/list'),
  },
  {
    path: '/studio/cms/textDetail',
    name: 'cms-text-detail',
    component: () => import('@/view/studioCms/textDetail'),
  },
  {
    path: '/studio/cms/voiceDetail',
    name: 'cms-voice-detail',
    component: () => import('@/view/studioCms/voiceDetail'),
  },
  {
    path: '/studio/cms/videoDetail',
    name: 'cms-video-detail',
    component: () => import('@/view/studioCms/videoDetail'),
  },
  {
    path: '/studio/article/textDetail',
    name: 'article-text-detail',
    component: () => import('@/view/studioCms/articleTextDetail'),
  },
  {
    path: '/studio/article/voiceDetail',
    name: 'article-voice-detail',
    component: () => import('@/view/studioCms/articleVoiceDetail'),
  },
  {
    path: '/studio/article/videoDetail',
    name: 'article-video-detail',
    component: () => import('@/view/studioCms/articleVideoDetail'),
  },
]

const router = new Router({
  mode: 'history',
  base: '/doctor',
  routes: routes  // 使用定义好的路由数组
})


/**
     * 对信息加密
     * @param str
     * @returns {Uint8Array}
     */
 function caringDecode(str) {
      str = 'caring_' + str
      const base64 = Base64.encode(str)
      const result = 'A' + base64 + 'B'
      return Base64.encode(result)
  }
//未授权过患者进行授权
function wxAuthorize(onComplete) {
  const officialAccountType = localStorage.getItem('officialAccountType')
  // 项目是个人服务号的项目。直接去患者登录页面
  if (officialAccountType && officialAccountType === 'PERSONAL_SERVICE_NUMBER') {
    if (onComplete) onComplete();
    return
  }
 // 开发环境自动登录配置 2026daxiong 测试用，正式环境注释
 if (process.env.NODE_ENV === 'development' || localStorage.getItem('AUTO_LOGIN_ENABLED')) {
      // 设置测试账户信息
      const testPhone = localStorage.getItem('TEST_PHONE') || '18608106801';
      const testPassword = localStorage.getItem('TEST_PASSWORD') || '123456';
      
      // 直接调用登录方法
      const phone = caringDecode(testPhone);
      const params = {
        mobile: phone,
        decode: true,
        isToast: false, // 不显示登录提示
        password: testPassword,
        openId: 'wx_demo_app_id_001'
      };
      
      Api.doctorlogin(params).then((res) => {
        if (res.data.code === 0) {
          localStorage.setItem("LAST_LOGIN_ROLE", "doctor");
          localStorage.setItem('caring_doctor_id', res.data.data.userId);
          localStorage.setItem('doctortoken', res.data.data.token);
          window.location.reload();
        } else {
          console.log('自动登录失败，回退到普通登录流程');
        }
      }).catch((e) => {
          debugger;
        // 自动登录出错，回退到普通登录流程
        console.error('自动登录出错，回退到普通登录流程',e);
      });
       return;
  }
  var s = window.location.href;
  var h = s.split(".")[0];
  var a = h.split("//")[1];
  let domain = location.hostname
  if (domain === 'localhost') {
    a = 'kailing';
  }
  const redirectUri = `${baseUrl}/wx/wxUserAuth/anno/getWxUserCode?domain=${a}&redirectUri=${encodeURIComponent(s)}`;
  window.location.href = redirectUri
}


/**
 * 初始化字典。并存入缓存
 * @param tenantCode
 */
function tempInitDict(tenantCode) {
  axios.get(baseUrl + '/authority/dictionaryItem/anno/queryTenantDict' + '?tenantCode=' + tenantCode).then(res => {
    if (res.data.code === 0) {
      const dict = res.data.data
      if (dict && dict.length > 0) {
        localStorage.setItem("dictionaryItem", JSON.stringify(dict))
      }
    }
  })
}

//初始化的函数  获取到headerTenant和wxAppId
function initPage(callback) {
  let s = window.location.href;
  let h = s.split(".")[0];
  let a = h.split("//")[1];
  let domain = location.hostname
  if (domain === 'localhost') {
    a = 'kailing';
  }

  axios.get(baseUrl + '/tenant/tenant/anno/getByDomain?domain=' + a).then(res => {
    if (res.data.code === 0) {
      console.log(res)
      window.document.title = res.data.data.name
      localStorage.setItem('Code', res.data.data.code)
      localStorage.setItem('pageTitle', res.data.data.name)
      localStorage.setItem('headerTenant', Base64.encode(res.data.data.code))
      localStorage.setItem('wxAppId', res.data.data.wxAppId)
      localStorage.setItem('officialAccountType', res.data.data.officialAccountType)
      localStorage.setItem('projectInfo', JSON.stringify(res.data.data))
     
      window.document.title = res.data.data.name
        try {
          tempInitDict(res.data.data.code) 
        } catch (e) { }
        axios.get(baseUrl + '/tenant/h5Router/anno/query/' + res.data.data.code + '?userType=DOCTOR').then(zf => {
            if (zf.data.code === 0) {
          localStorage.setItem('doctorRouterData', JSON.stringify(zf.data.data))
          callback()
        }
      }).catch(err => {
        callback()
      })
    } else {
      console.log('initPage 初始化有问题！')
    }
  })
}


router.afterEach((to, from) => {
  localStorage.setItem('currentPathRouter', to.path)
})


router.beforeEach((to, from, next) => {
  // //获取跳转回来的连接参数
  let headerTenant = localStorage.getItem('headerTenant')//获取请求头部的headerTenant
  let wxAppId = localStorage.getItem('wxAppId')//获取微信授权的wxAppId
  let userId = localStorage.getItem('caring_doctor_id')//获取用户的userId
  let token = localStorage.getItem('doctortoken')//获取请求头的token
  if (userId === 'undefined' || token === 'undefined') {
    userId = null;
    token = null;
  }
  const query = to.query
  // 当缓存中有这几个信息的时候，说明已经授权过了。可以直接进入页面
  if (headerTenant && wxAppId && userId && token) {
    next()
    return;
  }

  //跳转到病例讨论、医生申请、医生二维码
  if (localStorage.getItem('projectInfo')) {
    let a =JSON.parse(localStorage.getItem('projectInfo'))
    window.document.title=a.name
  }

  // 白名单：无需登录
  const publicPaths = [
    '/cms/index',
    '/cms/show',
    '/register/index',
    '/doctorInfoDetail/resetPsd',
    '/consultation/qrCode',
    '/submitted/index',
    '/scanImg'
  ];
  if (publicPaths.some(p => to.path.startsWith(p))) {
    initPage(() => next());
    return;
  }

  // 去注册医生页面。如果没有openId 就直接重定向到后端。
  if (to.path.indexOf('saasDoctorApply') > -1) {
    // 去医生申请页面。需要拿到openId
    // 携带openID前往医生申请页面
    if (!query.openId) {
      initPage(() => {
        wxAuthorize(() => next()); // ✅ 关键：个人服务号靠这个 next() 放行
      })
    } else {
      initPage(() => next());
    }
    return;
  }

  // 参数有 userId 和token 。可以直接进入页面。
  if (query.userId && query.token) {
    localStorage.setItem('caring_doctor_id', query.userId)
    localStorage.setItem('doctortoken', query.token)
    initPage(() => next());
    return;
  }

  // 个人服务号选择身份后，或者 后端重定向回来要求登录。并携带openId和doctorLogin标识
  if (to.query.handled) {
    next()
    return;
  }
  if (query.gerenfuwhaoDoctorLogin || (query.openId && query.doctorLogin)) {
    initPage(() => {
      if (to.path === '/') {
        next()
      } else {
        next({
          path: '/',
          query: { ...to.query, handled: 1, redirectUri: to.fullPath, isRedirectUri: true }
        });
      }
    });
    return;
  }

  // 登录页且有 openId：允许进入
  if (to.path === '/' && query.openId) {
    initPage(() => next());
    return;
  }

  // 默认：去微信授权
  initPage(() => {
    wxAuthorize(() => next()); // ✅ 关键：个人服务号靠这个 next() 放行
  });
})

export default router;