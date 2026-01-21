import Vue from 'vue'
import Router from 'vue-router'
import {Base64} from 'js-base64'
import wx from "weixin-js-sdk";
import {debounce} from "../components/utils";
import { patientLogin } from '@/api/patientRegister.js'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  base: '/wx',
  routes: [{
    path: '/home',
    name: '首页',
    component: () => import('@/view/patient/index'),
    },
    {
      path: '/integrity',
      name: '信息完整度',
      component: () => import('@/view/Integrity/Integrity'),
      meta: {
        pageName: 'Integrity'
      },
    },
    {
      path: '/questionnaire/index',
      name: '问卷调查',
      meta: {
        pageName: 'questionnaireIndex'
      },
      component: () => import('@/view/questionnaire/index')
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
      path: '/questionnaire/loginSuccessful',
      name: '注册完成',
      component: () => import('@/view/questionnaire/loginSuccessful')
    },
    {
      path: '/questionnaire/editquestion',
      name: '问卷调查编辑',
      meta: {
        pageName: 'questionnaireIndex'
      },
      component: () => import('@/view/questionnaire/editquestion')
    },
    {
      path: '/baseinfo/index',
      name: '基础信息',
      component: () => import('@/view/baseinfo/index')
    },
    // 基础信息历史
    {
      path: "/baseinfo/index/history",
      name: '基础信息修改历史',
      component: () => import('@/view/baseHistory/baseHistroy')
    },
    {
      path: "/baseinfo/index/history/detailsHistory",
      name: '基础信息修改历史详情',
      component: () => import('@/view/detailsHistory/detailsHistory')
    },

    {
      path: '/scanningReferral/index',
      name: '患者转诊',
      component: () => import('@/view/scanningReferral/index')
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
      path: '/medicine/showuser',
      name: '药物使用说明',
      component: () => import('@/view/medicine/showuser')
    },
    {
      path: '/medicine/addmedicine',
      name: '添加药物',
      component: () => import('@/view//medicine/add')
    },

    {
      path: '/medicine/storeroom',
      name: '药品库存',
      component: () => import('@/view/medicine/storeroom')
    },
    {
      path: '/medicine/detail',
      name: '药品详情',
      component: () => import('@/view/medicine/detail'),
      meta: {
        keepAlive: false
      }
    },
    {
      path: '/medicine/show',
      name: '使用说明',
      component: () => import('@/view/medicine/show')
    },
    {
      path: '/custom/follow/:planId',
      name: '随访',
      component: () => import('@/view/customFollow/index')
    },
    {
      path: '/custom/follow/detail/result',
      name: '随访详情',
      component: () => import('@/view/customFollow/detail')
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
      path: '/custom/follow/:planId/editor/:messageId',
      name: '添加随访',
      component: () => import('@/view/customFollow/editor')
    },
    {
      path: '/custom/follow/:planId/calendar',
      name: '随访日历',
      component: () => import('@/view/customFollow/calendar')
    },
    {
      path: '/testNumber/index',
      name: '检测数据',
      component: () => import('@/view/testNumber/index')
    },
    {
      path: '/testNumber/editor',
      name: '检验数据',
      component: () => import('@/view/testNumber/editor')
    },
    {
      path: '/healthCalendar/index',
      name: '健康日志',
      component: () => import('@/view/healthCalendar/index')
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
      path: '/reservation/index',
      name: '就诊预约',
      meta: {
        pageName: 'reservation'
      },
      component: () => import('@/view/reservation/index')
    },
    {
      path: '/reservation/appointmentFailed',
      name: '预约失败',
      component: () => import('@/view/reservation/appointmentFailed')
    },
    {
      path: '/reservation/doctorIndex',
      name: '医生预约',
      meta: {
        pageName: 'reservation'
      },
      component: () => import('@/view/reservation/doctorIndex')
    },
    {
      path: '/reservation/myreservation',
      name: '我的预约',
      meta: {
        pageName: 'reservation'
      },
      component: () => import('@/view/reservation/myreservation')
    },
    {
      path: '/mydoctor/index',
      name: '我的医生',
      component: () => import('@/view/myDoctor/index')
    },
    {
      path: '/im/index',
      name: '聊天',
      component: () => import('@/view/im/index')
    },
    {
      path: '/im/messageDetail',
      name: '聊天详情',
      component: () => import('@/view/im/messageDetail')
    },
    {
      path: '/consultation/:groupId',
      name: '会诊聊天',
      component: () => import('@/view/consultation/index')
    },
    {
      path: '/consultationDetail',
      name: '会诊详情',
      component: () => import('@/view/consultation/consultationDetail')
    },
    {
      path: '/health/index',
      name: '健康档案',
      component: () => import('@/view/health/healthList')
    },
    {
      path: '/health/list',
      name: '疾病信息列表',
      component: () => import('@/view/health/index')
    },
    {
      path: '/health/imageList',
      name: '疾病信息图片列表',
      component: () => import('@/view/health/imageList')
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
        keepAlive: true
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
      path: '/hospital',
      name: '医院',
      component: () => import('@/components/arrt/hospital/hospitalList/hospitalList'),
    },
    //项目介绍
    {
      path: '/',
      name: '项目介绍第一页',
      meta: {
        pageName: 'one',
        keepAlive: true
      },
      component: () => import('@/view/introduce/one')
    },
    {
      path: '/two',
      name: '项目介绍第二页',
      component: () => import('@/view/introduce/two')
    },
    {
      path: '/shouintroduce',
      name: '服务协议',
      component: () => import('@/view/introduce/showintroduce')
    },
    {
      path: '/loadapp/index',
      name: '下载',
      meta: {
        pageName: 'load'
      },
      component: () => import('@/view/loadApp/index')
    },
    {
      path: '/loadapp/index2',
      name: 'UNI下载',
      meta: {
        pageName: 'load'
      },
      component: () => import('@/view/loadApp/index2')
    },
    {
      path: '/cmsz/show',
      name: 'cms文章详情',
      meta: {
        pageName: 'cmsz'
      },
      component: () => import('@/view/cms/chatCMSShow')
    },
    {
      path: '/wxauthorize/refuse',
      name: '微信授权拒绝',
      meta: {
        pageName: 'authorize'
      },
      component: () => import('@/view/wxAuthorize/index')
    },
    {
      path: '/monitor/index',
      name: '监测数据',
      component: () => import('@/view/testdata/index')
    },
    {
      path: '/monitor/show',
      name: '监测数据查看数据',
      component: () => import('@/view/testdata/show'),
    },
    {
      path: '/monitor/add',
      name: '监测数据添加数据',
      component: () => import('@/view/testdata/add')
    },
    {
      path: '/monitor/horizontalScreen',
      name: '监测数据横屏',
      component: () => import('@/view/testdata/TestDataHorizontalScreen')
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
      path: '/medicine/medicineReminder',
      name: '购药提醒',
      component: () => import('@/view/medicine/PurchaseMedicineReminder')
    },
    {
      path: '/medicine/history',
      name: '历史用药记录',
      component: () => import('@/view/medicine/medicineHistory')
    },
    {
      path: '/medicine/modify',
      name: '修改用药提醒',
      component: () => import('@/view/medicine/medicineModifyTips')
    },
    {
      path: '/monitor/editReference',
      name: '修改目标值和基本值',
      component: () => import('@/view/testdata/editReference')
    },
    {
      path: '/mine/invitation',
      name: '我的邀请',
      component: () => import('@/view/mine/invitation.vue')
    },
    {
      path: '/followUp',
      name: '随访管理',
      component: () => import('@/view/followUp/index')
    },
    {
      path: '/followUp/scheduleForm',
      name: '随访计划表单',
      component: () => import('@/view/followUp/components/scheduleForm')
    },
    {
      path: '/followUp/glucose',
      name: '随访管理',
      component: () => import('@/view/followUp/components/followShowGlucose')
    },
    {
      path: '/followUp/statistics',
      name: '随访统计',
      component: () => import('@/view/followUp/components/statistics')
    },
    {
      path: '/patient/systemMessage',
      name: '随访统计',
      component: () => import('@/view/patient/systemMessage/index')
    },
    {
      path: '/rule/select',
      name: '身份选择',
      component: () => import('@/view/rule/select')
    },
    {
      path: '/select/role',
      name: '个人服务号身份选择',
      component: () => import('@/view/rule/role_select')
    },
    {
      path: '/patient/login',
      name: '患者登录',
      component: () => import('@/view/rule/patient_login')
    },
    {
      path: '/patient/register',
      name: '患者注册',
      component: () => import('@/view/rule/patient_register')
    },
    {
      path: '/patient/resetPsd',
      name: '找回密码',
      component: () => import('@/view/rule/resetPsd')
    },
    {
      path: '/score/index',
      name: '评分列表',
      component: () => import('@/view/score/index')
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
      path: '/testNumber/detail',
      name: '检验数据结果表单',
      component: () => import('@/view/testNumber/detail')
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
    }
  ]
})


let apiUrl = process.env.NODE_ENV === 'development' 
  ? "http://localhost:8760/api" 
  : "https://api.example.com"
// 首次加载判断是否患者需要完成入组
let shouldCheckEnterGroupStatus = true
// 记录患者的入组状态
let patientCompleteEnterGroup = undefined

/**
 * 关于去 身份选择页面时的 业务逻辑。
 * @param to
 * @param from
 * @param next
 */
function ruleSelectFilter(userId, token, wxAppId, headerTenant,  to, from, next) {
  if (localStorage.getItem('projectInfo') && JSON.parse(localStorage.getItem('projectInfo')).name) {
    window.document.title = JSON.parse(localStorage.getItem('projectInfo')).name
  }

  // 用户ID已经存在。token已经生成。直接去首页
  if (userId && token) {
    next({
      path: "/"
    })
  } else
    // 如果有openId 则使用openId判断是去选择身份还是去首页。
  if (to.query && to.query.openId) {
    if (wxAppId && headerTenant) {
      // 使用to中的参数，判断用户是去哪里。
      checkOpenIdUserRole(to, next)
    }else {
      // 获取系统配置跳转
      initPage(() => {
        checkOpenIdUserRole(to, next)
      })
    }
  } else {
    //未授权患者后端授权
    initPage(() => {
      wxAuthorize()
    })
  }
}


/**
 * 去选择角色页面之前，先检查openId是否已经拥有身份，患者或医生
 * @param to
 * @param next
 */
function checkOpenIdUserRole(to, next) {
  const query = to.query
  if (query && query.openId) {
    axios.get(apiUrl + '/wx/config/anno/getWeiXinUserInfo' + '?openId=' + query.openId).then(res => {
      if (res.data.code === 0) {
        const result = res.data.data
        if (result.userRole === 'patient') {
          // 用户已经选择了患者身份，直接让他去home。
          next('/home')
        } else if (result.userRole === 'doctor') {
          let url = window.location.href.substring(0, window.location.href.indexOf('/'));
          window.location.href = url + '/doctor/?openId='+ query.openId +'&doctorLogin=true'
        } else {
          next()
        }
      }
    })
  }
}

/**
 * 去药箱或者去日历。或者继续走下去
 * @param to
 * @param next
 */
function toMedicine (to, next) {
  if (to.path === '/medicine/index') {
    // 如何是我的药品页面
    next({
      path: '/calendar/index',
      query: { type: '1'}
    })
    return
  }
  next()
}

/**
 * 未授权过患者进行授权
 */
function wxAuthorize(to, next) {
  const officialAccountType = localStorage.getItem('officialAccountType')
  // 项目是个人服务号的项目。直接去患者登录页面
  if (officialAccountType && officialAccountType === 'PERSONAL_SERVICE_NUMBER') {
    next({
      path: '/patient/login',
      query: {
        redirect: to.path,
        redirectQuery: JSON.stringify(to.query)
      }
    })
    return
  }
  // 开发环境自动登录配置 2026daxiong 测试用，正式环境注释
 if (process.env.NODE_ENV === 'development' || localStorage.getItem('AUTO_LOGIN_ENABLED')) {
      // 设置测试账户信息
      const testPhone = localStorage.getItem('TEST_PHONE') || '15969881998';
      const testPassword = localStorage.getItem('TEST_PASSWORD') || '123456';
      
      // 直接调用登录方法
      const phone = caringDecode(testPhone);
      const params = {
        phone: phone,
        password: testPassword
      };
      
      patientLogin(params).then((res) => {
        console.log('自动登录',res.data);
        if (res.data.code === 0) {
            localStorage.setItem("LAST_LOGIN_ROLE", "patient")
            localStorage.setItem('token', res.data.data.token)
            localStorage.setItem('userId', res.data.data.userId)
            localStorage.setItem('expiration', res.data.data.expiration)
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
  window.location.href = apiUrl + '/wx/wxUserAuth/anno/getWxUserCode?domain=' + a + '&redirectUri=' + encodeURIComponent(s)
}
/**
 * 对信息加密
 * @param str
 * @returns {Uint8Array}
 */
function  caringDecode(str) {
  str = 'caring_' + str
  const base64 = Base64.encode(str)
  const result = 'A' + base64 + 'B'
  return Base64.encode(result)
}
/**
 * 初始化字典。并存入缓存
 * @param tenantCode
 */
function tempInitDict(tenantCode) {
  axios.get(apiUrl + '/authority/dictionaryItem/anno/queryTenantDict' + '?tenantCode=' + tenantCode).then(res => {
    if (res.data.code === 0) {
      const dict = res.data.data
      if (dict && dict.length > 0) {
        localStorage.setItem("dictionaryItem", JSON.stringify(dict))
      }
    }
  })
}

/**
 * 初始化的函数  获取到headerTenant和wxAppId
 * @param callback
 */
function initPage(callback) {
  const s = window.location.href;
  const h = s.split(".")[0];
  let a = h.split("//")[1];
  // 测试环境时，localhost替换域名为kailing 去获取项目信息
  let domain = location.hostname
  if (domain === 'localhost') {
    a = 'kailing';
  }
  if (localStorage.getItem("headerTenant") && localStorage.getItem("wxAppId") && localStorage.getItem("projectInfo") &&
    localStorage.getItem("routerData") && localStorage.getItem("styleDate")) {
    window.document.title = JSON.parse(localStorage.getItem('projectInfo')).name
    callback()
  } else {
    axios.get(apiUrl + '/tenant/tenant/v2/anno/patient/getByDomain?domain=' + a).then(res => {
      if (res.data.code === 0) {
        console.log('router数据', res.data.data)
        localStorage.setItem('headerTenant', Base64.encode(res.data.data.code))
        localStorage.setItem('wxAppId', res.data.data.wxAppId)
        localStorage.setItem('officialAccountType', res.data.data.officialAccountType)
        localStorage.setItem('projectInfo', JSON.stringify(res.data.data))
        window.document.title = res.data.data.name
        localStorage.setItem('routerData', JSON.stringify(res.data.data.routerPatientDto))
        localStorage.setItem('styleDate', JSON.stringify(res.data.data.styleDate))
        localStorage.setItem('enableIntro', JSON.stringify(res.data.data.regGuide.enableIntro))
        localStorage.setItem('patientCompleteFormStatus', JSON.stringify(res.data.data.patientCompleteFormStatus))
        tempInitDict(res.data.data.code)
        callback()

      } else {
        console.log('初始化有问题啊啊啊啊！')
      }
    })
  }
}

/**
 * 患者还没有完成入组，检查患者的信息
 * @param patient
 */
function patientCompleteEnterGroupNoSuccess(patient, next) {
  const enableIntro = localStorage.getItem('enableIntro')
  const patientCompleteFormStatus = localStorage.getItem('patientCompleteFormStatus')
  if (enableIntro !== undefined && enableIntro === '0') {
    // 判断 患者是否已经 同意入组协议。
    if (!patient.agreeAgreement || patient.agreeAgreement === 0) {
      // 要去展示项目介绍。
      next({
        path: '/'
      })
      return
    }
  }
  if (patientCompleteFormStatus === '1') {
    // 去 一题一页入组
    next({path: '/questionnaire/editquestion', query: {status: 1, isGroup: 1}})
  } else {
    // 去 表单的入组
    next({
      path: '/two'
    })
  }
}

/**
 * 患者入组成功
 * @param toPath
 */
function patientCompleteEnterGroupSuccess(toPath, next) {
  if (toPath === '/home') {
    next({
      path: '/home'
    })
  } else {
    next()
  }
}


/**
 * 判断是否入组从而知道进入那些界面
 * @param userId
 * @param toPath
 * @param next
 */
function isGroup(userId, toPath, next) {
  // 患者已经在当前的router中拿到他入组成功的记录了。直接跳转
  //debugger
  if (patientCompleteEnterGroup) {
    patientCompleteEnterGroupSuccess(toPath, next)
    return
  }
  axios.get(apiUrl + '/ucenter/patient/' +userId, {
    params: {},
    headers: {
      Authorization: 'Basic Y2FyaW5nX3VpOmNhcmluZ191aV9zZWNyZXQ=',
      tenant: localStorage.getItem('headerTenant'),
      token: 'Bearer ' + localStorage.getItem('token')
    }
  }).then((el) => {
    if (el.data.code === 0) {
      shouldCheckEnterGroupStatus = false
      const patient = el.data.data
      if (patient) {
        localStorage.setItem('wxAppId', patient.wxAppId)
        localStorage.setItem('myallInfo', JSON.stringify(patient))
        if (toPath === '/im/index') {
          next()
        } else {
          if (patient.isCompleteEnterGroup === 1) {
            patientCompleteEnterGroup = true
            patientCompleteEnterGroupSuccess(toPath, next)
          } else if (patient.isCompleteEnterGroup === 0) {
            patientCompleteEnterGroup = false
            patientCompleteEnterGroupNoSuccess(patient, next)
          }
        }
      } else {
        localStorage.setItem('headerTenant', '')
        localStorage.setItem('wxAppId', '')
        localStorage.setItem('userId', '')
        localStorage.setItem('token', '')
        next({
          path: '/'
        })
      }
    }
  })
}

router.beforeEach((to, from, next) => {

  let headerTenant = localStorage.getItem('headerTenant') //获取请求头部的headerTenant
  let wxAppId = localStorage.getItem('wxAppId') //获取微信授权的wxAppId
  let userId = localStorage.getItem('userId') //获取用户的userId
  let token = localStorage.getItem('token') //获取请求头的token
  // 注意：不要在代码中硬编码token，应通过正常的登录流程获取
  localStorage.setItem('currentPathRouter', to.path)

  // 患者端的白名单页面，用户不需要注册，不需要入组就可以访问
  // 个人服务号 身份选择或患者登录。直接查询项目信息后进入页面
  if (to.meta.pageName === 'load' || to.meta.pageName === 'cms' || to.meta.pageName === 'authorize'
    || to.path === '/select/role' || to.path === '/patient/login' || to.path === '/patient/register' || to.path === '/patient/resetPsd') { //不需要授权直接进入的界面
    initPage(() => {
      toMedicine(to, next)
    })
    return
  }
  //debugger
  // 每次进入 身份选择页面时。
  if (to.path === '/rule/select') {
    ruleSelectFilter(userId, token, wxAppId, headerTenant, to, from, next)
  } else {

    let toPath = undefined
    if (to.path === '/') {
      toPath = '/home'
    } else {
      toPath = to.path
      // 如果去我的药箱，则跳转路径
      if (to.path === '/medicine/index') {
        toPath = '/calendar/index'
      }
    }
    //判断有没有患者id及token微信授权的wxAppId头部的headerTenant
    if (userId && token && wxAppId && headerTenant) {
      if (localStorage.getItem('projectInfo') && JSON.parse(localStorage.getItem('projectInfo')).name) {
        window.document.title = JSON.parse(localStorage.getItem('projectInfo')).name
      }
      // 不需要患者完成入组的功能。只需要登录就可以使用
      if (to.meta.pageName === 'reservation') {
        next()
      } else {
        // 系统每次打开时，判断患者的入组状态。 之后跳转的页面路由不再判断是否入组
        if (shouldCheckEnterGroupStatus) {
          isGroup(userId, toPath, next)
        } else {
          toMedicine(to, next)
        }
      }
    } else {
      //判断location.search是否有参数，以及参数中是否为 患者id 和 token
      if (to.query && to.query.userId && to.query.token) {
        localStorage.setItem('userId', to.query.userId)
        localStorage.setItem('token', to.query.token)
        initPage(() => {
          isGroup(to.query.userId, toPath, next)
        })
      } else {
        //未授权患者后端授权
        initPage(() => {
          wxAuthorize(to, next)
        })
      }
    }
  }
})

router.afterEach((to, from) => {
  if (!window.sessionStorage.firstUrl) {
    window.sessionStorage.firstUrl = window.location.href
  }
  window.scrollTo(0, 0);
})
export default router;
