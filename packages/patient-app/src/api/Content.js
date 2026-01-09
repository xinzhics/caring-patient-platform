import axiosApi from './apiAxios.js'
import {Base64} from "js-base64";
import apiUrl from './baseUrl.js'

const apiList = {

  //获取患者的基本信息
  getContent: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/patient/`
  },
  //获取基本信息
  getbaseContent: {
    method: 'GET',
    url: apiUrl + `/api/wx/regGuide/getGuide`
  },
  //获取历史推文
  planCmsReminderLog: {
    method: 'POST',
    url: apiUrl + `/api/nursing/planCmsReminderLog/page`
  },


  //获取表单
  gethealthform: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getFromResultByCategory/`
  },
  gethealthformSub: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResult`
  },
  /**
   * 更新表单结果
   */
  gethealthformSubPut: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/formResult`
  },
  /**
   * 获取自定义随访的表单列表
   * planId
   */
  getCustomPlanForm: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getCustomForm/`
  },
  /**
   * 获取自定义随访的表单详情
   * planId
   */
  getCustomPlanFormResult: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getCustomFormDetail/`
  },
  /**
   * 获取自定义随访的表单结果。
   * path： patientId
   * path: planId
   * query: current
   * query: size
   */
  pageCustomPlanForm: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getCustomForm/`
  },
  //获取检验数据
  getCheckData: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResult/getCheckData/`
  },
  //获取检验数据结果
  getCheckDataformResult: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/`
  },
  // 根据护理计划类型获取护理计划
  getPlanByType: {
    method: 'GET',
    url: apiUrl + `/api/nursing/plan/getPlanByType`
  },
  //获取血压数据
  getformResultquery: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResult/query`
  },
  //获取血压走势数据
  loadMyBloodPressureTrendData: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getPatientBloodPress/`
  },

  //获取血糖的列表
  findSugarByTime: {
    method: 'GET',
    url: apiUrl + `/api/nursing/sugarFormResult/findSugarByTime/`
  },
  // 分页获取血糖列表
  findSugarPage: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sugarFormResult/findSugarByTime/monitorQueryDate`
  },
  // 获取一个血糖记录
  getSugar: {
    method: 'GET',
    url: apiUrl + `/api/nursing/sugarFormResult/`
  },
  //获取血糖一周的列表v
  findSugarByTimeList: {
    method: 'GET',
    url: apiUrl + `/api/nursing/sugarFormResult/list/`
  },

  //智能提醒
  getMyNursingPlans: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientNursingPlan/getMyNursingPlans/`
  },
  //智能提醒修改
  updateMyNursingPlans: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientNursingPlan/updateMyNursingPlans/`
  },
  //智能提醒修改
  deletePatientDrugs: {
    method: 'DELETE',
    url: apiUrl + `/api/nursing/patientDrugs`
  },
  //我的药箱  查询当前用药和历史用药
  patientDrugsListAndHistory: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDrugs/patientDrugsListAndHistory/`
  },
//我的药箱  整个药品库
  sysDrugsPage: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sysDrugs/pageByTenant/`
  },
  //我的药箱  单个药品查询详情
  sysDrugsPagedetail: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sysDrugs/query`
  },
  getDrugsConditionMonitoring: {
    method: 'GET',
    url: apiUrl + `/api/nursing/drugsConditionMonitoring/getDrugsConditionMonitoring?drugsId=`
  },
  //我的药箱  患者添加用药
  addpatientDrugs: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientDrugs/savePatientDrugs`
  },
  //修改
  updatepatientDrugs: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/patientDrugs`
  },
  //获取单个药品详情
  getpatientDrugs: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDrugs/`
  },

  //血糖走势
  loadMyBloodSugarTrendData: {
    method: 'GET',
    url: apiUrl + `/api/nursing/sugarFormResult/loadMyBloodSugarTrendData/`
  },


  //用药日历  查询当前用药和历史用药
  patientDayDrugsCalendar: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDayDrugs/calendar/`
  },

  //用药日历  查询周打卡
  patientDayDrugsTo7Day: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDayDrugs/7Day/`
  },

  //提交血糖的结果
  sugarFormResult: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sugarFormResult`
  },
  //修改血糖
  PutsugarFormResult: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/sugarFormResult`
  },

  //微信登陆
  wxlogin: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/anno/wx/login`
  },
  //注册引导表单一
  regGuidegetGuide: {
    method: 'GET',
    url: apiUrl + `/api/wx/regGuide/getGuide`
  },
  //完成入组
  completeEnterGroup: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/patient/completeEnterGroup`
  },
//cms导航栏
  channelpage: {
    method: 'POST',
    url: apiUrl + `/api/cms/channel/anno/page`
  },
  //cms内容
  channelContentpage: {
    method: 'POST',
    url: apiUrl + `/api/cms/channelContent/anno/page`
  },
  channelGroup: {
    method: 'GET',
    url: apiUrl + `/api/cms/channel/anno/getChannelGroup/`
  },
  //我的药箱  查单个药品
  sysDrugsSearch: {
    method: 'GET',
    url: apiUrl + `/api/nursing/sysDrugs/`
  },
  // /api/nursing/sysDrugs/{id}

  //获取im群组
  getPatientImGroup: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/patient/anno/getPatientImGroup`
  },

  //获取历史消息
  getMessageList: {
    method: 'GET',
    url: apiUrl + `/api/msgs/im/anno/patient/chat/history`
  },

  //获取会诊管理历史消息
  getGroupMessageList: {
    method: 'POST',
    url: apiUrl + `/api/msgs/groupIm/getGroupImMessage/`
  },
  //发送消息
  sendMessage: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/patient/anno/sendChat`
  },
  //发送会诊消息
  sendGroupMessage: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/consultationGroup/sendGroupMessage/`
  },

  //我的药箱  修改
  FixpatientDrugs: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientDrugs/updatePatientDrugs
`
  },

  //上传图片
  updateImg: {
    method: 'POST',
    url: apiUrl + `/file/file/upload`
  },
  //微信获取分享扫一扫接口
  wxSignature: {
    method: 'POST',
    url: apiUrl + `/api/wx/config/jssdk/signature`
  },
  annoWxSignature: {
    method: 'POST',
    url: apiUrl + `/api/wx/config/jssdk/anno/signature`
  },

  //患者Im在线
  setImLine: {
    method: 'PUT',
    url: apiUrl + `/api/ucenter/patient/anno/imOnline/`
  },

  //修改患者未读消息为已读
  setMsgStatus: {
    method: 'POST',
    url: apiUrl + `/api/msgs/im/chat/refreshMsgStatus/`
  },
  //查单个药品
  pageByTenant: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sysDrugs/pageByTenant/`
  },
  //是否显示患者群组
  isImGroup: {
    method: 'PUT',
    url: apiUrl + `/api/ucenter/patient`
  },
  // /api/tenant/drugsConfig/listRecommendDrugs/
  //获取推荐用要
  listRecommendDrugs: {
    method: 'GET',
    url: apiUrl + `/api/tenant/drugsConfig/listRecommendDrugs/`
  },
  //cms获取轮播图的接口
  channelContentquery: {
    method: 'POST',
    url: apiUrl + `/api/cms/channelContent/anno/query`
  },
  getcmsdetail: {
    method: 'GET',
    url: apiUrl + `/api/cms/channelContent/`
  },
  // /api/nursing/patientDrugsTime/queryByDrugsTimestamp
  queryByDrugsTimestamp: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDrugsTime/queryByDrugsTimestamp`
  },

  clockIn: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientDrugsTime/clockIn`
  },

  getappointDoctor: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/doctor/appointDoctor`
  },
  searchDoctor: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/doctor`
  },
  getDoctorStatus: {
    method: 'GET',
    url: apiUrl + `/api/nursing/appointment/patient/appoint`
  },
  postappointment: {
    method: 'POST',
    url: apiUrl + `/api/nursing/appointment`
  },
  postappoint: {
    method: 'POST',
    url: apiUrl + `/api/nursing/appointment/patient/appoint`
  },
  putappoint: {
    method: 'PUT',
    url: apiUrl + `/nursing/appointment/patient/cancel/appoint?appointmentId=`
  },
  channelContentWithReply: {
    method: 'GET',
    url: apiUrl + `/api/cms/channelContent/anno/channelContentWithReply/`
  },
  updateHitCount: {
    method: 'PUT',
    url: apiUrl + `/api/cms/channelContent/anno/updateHitCount/`
  },
  contentReply: {
    method: 'POST',
    url: apiUrl + `/api/cms/contentReply`
  },
  contentReplylike: {
    method: 'POST',
    url: apiUrl + `/api/cms/contentReply/like`
  },
  contentCollect: {
    method: 'POST',
    url: apiUrl + `/api/cms/contentCollect`
  },
  contentCollectList: {
    method: 'POST',
    url: apiUrl + `/api/cms/contentCollect/page`
  },
  // 获取页面配置
  createIfNotExist: {
    method: 'GET',
    url: apiUrl + `/api/tenant/h5Router/createIfNotExist`
  },
  // 获取页面ui配置
  uicreateIfNotExist: {
    method: 'GET',
    url: apiUrl + `/api/tenant/h5Ui/createIfNotExist`
  },
  getConsultationGroupDetail: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/consultationGroup/getConsultationGroupDetail/`
  },
  uploadServerId: {
    method: 'POST',
    url: apiUrl + `/api/wx/config/material/get_material`
  },
  queryUserName: {
    method: 'POST',
    url: apiUrl + `/api/tenant/appConfig/queryUserName/`
  },

  referral: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/referral/`
  },
  // 获取聊天小组详细成员2.4
  getPatientGroupDetail: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/patient/anno/getPatientGroupDetail`
  },
  getPatientDoctorInfo: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/doctor/getDoctorBaseInfoByPatientId/`
  },
  //im中查看图片
  getChatImage: {
    method: 'GET',
    url: apiUrl + `/api/msgs/im/anno/chat/image`
  },
  //查看@患者的记录
  getATPatientList: {
    method: 'GET',
    url: apiUrl + `/api/msgs/im/anno/chatAtPatientRecordList/`
  },
  //获取监测计划列表
  getPatientMonitoringDataPlan: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientNursingPlan/patientPlanList/formResult`
  },
  //取消关注监测计划
  patientMonitoringCancelPlan: {
    method: 'DELETE',
    url: apiUrl + `/api/nursing/patientNursingPlan/patient/subscribe?patientId=`
  },
  //关注监测计划
  setPatientMonitoringSubscribePlan: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/patientNursingPlan/subscribePlan?patientId=`
  },
  //监测数据列表倒序
  getMonitorList: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResult/monitorFormResult`
  },
  //监测数据折线图
  getMonitorLineChart: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/monitorLineChart/`
  },
  //用药数据
  getPatientMedicationPlan: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDrugsTime/queryByDay`
  },
  postRecordingMedication: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientDrugsTime/reClockIn/`
  },
  getMedicineHistor: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientDrugsHistoryLog/app/historyDate/page`
  },
  getCurrentMedicine: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDrugs/patientDrugsList/`
  },
  putMedicine: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/patientDrugs/patientDrugsList`
  },
  // 字典
  getdictionaryItem: {
    method: 'POST',
    url: apiUrl + `/api/authority/dictionaryItem/anno/query`
  },
  // 是否显示修改历史
  openFormHistoryRecord: {
    method: 'GET',
    url: apiUrl + `/api/wx/regGuide/openFormHistoryRecord/`
  },
  // 表单结果历史记录分页查询
  formResultBackUp: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResultBackUp/page`
  },
  // 表单结果历史记录查询详情
  nursingFormResultBackUp: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResultBackUp/`
  },
  // 是否开启历史
  formHistoryRecord: {
    method: 'GET',
    url: apiUrl + `/api/wx/regGuide/formHistoryRecord`
  },
  // 分页查询省份
  listByIds: {
    method: 'POST',
    url: apiUrl + `/api/authority/province/page`
  },
  // 查询城市
  listByCity: {
    method: 'POST',
    url: apiUrl + `/api/authority/city/page`
  },
  // 分页查询医院
  hospitalPage: {
    method: 'POST',
    url: apiUrl + `/api/authority/hospital/page`
  },
  hospitalQuery: {
    method: 'POST',
    url: apiUrl + `/api/authority/hospital/query`
  },
  // 使用护理计划类型或者护理计划ID或者消息ID查询表单
  formOrFormResult: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getPlanForm`
  },
  // 信息完整度
  findIncompleteInformation: {
    method: 'GET',
    url: apiUrl + `/api/nursing/completenessInformation/findIncompleteInformation`
  },
  // 信息完整度提交
  updateIncompleteInformationField: {
    method: 'POST',
    url: apiUrl + `/api/nursing/completenessInformation/updateIncompleteInformationField`
  },
  //分页查询血压数据
  monitorFormResult: {
    method: 'POST',
    url: apiUrl + `/nursing/formResult/monitorFormResult/`
  },
  // 监测数据折线图新版-含监测事件
  monitorLineChart: {
    method: 'POST',
    url: apiUrl + `/nursing/formResult/monitorLineChart/`
  },
  updateFieldReference: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResult/updatePatientFormFieldReference`
  },
  //删除我的列表中的消息
  deleteStatusChat: {
    method: 'PUT',
    url: apiUrl + `/api/msgs/im/updateDeleteStatusChat/`
  },
  //更新消息为撤回状态，并发送通知给群组成员
  withdrawChat: {
    method: 'PUT',
    url: apiUrl + `/api/msgs/im/withdrawChat/`
  },
  //获取消息记录中最新的信息
  getChatUserNewMsg: {
    method: 'PUT',
    url: apiUrl + `/api/msgs/im/getChatUserNewMsg/`
  },
  // 对消息完成打卡
  reminderLogSubmitSuccess: {
    method: 'GET',
    url: apiUrl + `/api/nursing/reminderLog/submitSuccess`
  },
  // 对消息完成打卡
  annoReminderLogSubmitSuccess: {
    method: 'GET',
    url: apiUrl + `/api/nursing/reminderLog/anno/submitSuccess`
  },
  // 设置消息被打卡 (随访的表单)
  reminderLogOpenMessage: {
    method: 'GET',
    url: apiUrl + `/api/nursing/reminderLog/openMessage`
  },
  // 【精准预约】 患者删除预约
  deleteAppoint: {
    method: 'DELETE',
    url: apiUrl + `/nursing/appointment/patient/delete/appoint?appointmentId=`
  },
  // 查询患者预约详情
  appointment: {
    method: 'GET',
    url: apiUrl + `/nursing/appointment/`
  },
  //患者个人中心2.0根据域名获取项目信息
  getByDomain: {
    method: 'GET',
    url: apiUrl + `/tenant/tenant/v2/anno/patient/getByDomain?domain=`
  },
  // 患者随访日历数据
  patientMenuFollow: {
    method: 'GET',
    url: apiUrl + `/nursing/patientNursingPlan/v_4_3/patientMenuFollow?patientId=`
  },
  //患者同意入组协议
  agreeAgreement: {
    method: 'POST',
    url: apiUrl + `/ucenter/patient/agreeAgreement?patientId=`
  },
  //查询表单是否一题一页填写,1：是 。 2否
  getFormIntoTheGroup: {
    method: 'GET',
    url: apiUrl + `/nursing/form/getFormIntoTheGroup?category=`
  },
  // 首次分阶段保存基本信息或疾病信息
  saveFormResultStage: {
    method: 'POST',
    url: apiUrl + `/nursing/formResult/saveFormResultStage`
  },
  // 患者基本信息完成后的菜单
  getPatientJoinGroupAfterMenu: {
    method: 'GET',
    url: apiUrl + `/tenant/h5Router/getPatientJoinGroupAfterMenu`
  },
  planDetail: {
    method: 'GET',
    url: apiUrl + `/nursing/planDetail/getPlanDetail/`
  },
  // 首页 患者文章评论列表
  pageContent: {
    method: 'POST',
    url: apiUrl + `/cms/contentReply/pageContent`
  },
  // 患者待办中直接打开文章
  patientSubmitCms: {
    method: 'GET',
    url: apiUrl + `/nursing/reminderLog/submitCms`
  },
  // 查询患者首页的文章
  getPatientHomeContent: {
    method: 'POST',
    url: apiUrl + `/cms/channelContent/queryPatientHomeContent?patientHomeRegion=`
  },
  // 消息角标上的未读消息总和
  getCountMessage: {
    method: 'GET',
    url: apiUrl + `/msgs/msgPatientSystemMessage/countMessage?patientId=`
  },
  // 患者消息页面的所有数据
  getMessagePageData: {
    method: 'GET',
    url: apiUrl + `/msgs/msgPatientSystemMessage/messagePageData?patientId=`
  },
  // 系统消息列表
  getPatientSystemMessage: {
    method: 'POST',
    url: apiUrl + `/msgs/msgPatientSystemMessage/page`
  },
  countPatient:{
    method: 'GET',
    url: apiUrl + `/cms/channelContent/countPatient?roleType=patient&userId=`
  },
  //患者疾病信息列表
  healthFormResultList: {
    method: 'POST',
    url: apiUrl + `/nursing/formResult/healthFormResultList`
  },
  formResult:{
    method: 'GET',
    url: apiUrl + `/nursing/formResult/`
  },
  deleteFormResult:{
    method: 'DELETE',
    url: apiUrl + `/nursing/formResult`
  },
  // 设置系统消息已读
  setSystemMessageStatus:{
    method: 'PUT',
    url: apiUrl+`/msgs/msgPatientSystemMessage/setMessageStatus`
  },
  // 设置患者聊天消息已读
  setPatientMessageStatus:{
    method: 'POST',
    url: apiUrl + `/api/msgs/im/chat/refreshMsgStatus/`
  },
  patientGetForm:{
    method: 'GET',
    url: apiUrl + `/nursing/form/patientGetForm`
  },
  getHealthRecordByMessageId:{
    method: 'GET',
    url: apiUrl + `/nursing/formResult/getHealthRecordByMessageId?messageId=`
  },
  byCategory:{
    method: 'GET',
    url: apiUrl + `/nursing/formResult/byCategory/`
  },
  updateForDeleteFormResult:{
    method: 'PUT',
    url: apiUrl+`/nursing/formResult/updateForDeleteFormResult/`
  },
  // 设置游客选择的身份
  setTouristUserRole:{
    method: 'GET',
    url: apiUrl+`/wx/config/anno/setTouristUserRole?openId=`
  },
  // 游客选择患者身份后，登录
  getWxUserLogin:{
    method: 'POST',
    url: apiUrl+`/ucenter/anno/anno/wxPatient/login?userType=`
  },
  // 表单提交后。查询表单的成绩结果
  getFormResultScore:{
    method: 'GET',
    url: apiUrl+`/api/nursing/formResultScore/queryFormResultScore?formResultId=`
  },
  // 查看患者首页轮播图是否打开
  getBannerSwitch:{
    method: 'POST',
    url: apiUrl+`/api/cms/bannerSwitch/query`
  },
  // 查询首页轮播图
  getBannerList:{
    method: 'POST',
    url: apiUrl+`/api/cms/banner/query`
  },
  // 通过消息ID查询数据
  getGlucoseMessageId:{
    method: 'GET',
    url: apiUrl+`/api/nursing/sugarFormResult/getByMessageId?messageId=`
  },
  // AI 转人工
  sendManualTemplate:{
    method: 'GET',
    url: apiUrl+`/api/ucenter/patient/sendManualTemplate?patientId=`
  },
}


export default {
  sendManualTemplate() {
    let url = apiList.sendManualTemplate.url + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.sendManualTemplate.method,
      url: url,
    })
  },
  getGlucoseMessageId(imMessageId) {
    let url = apiList.getGlucoseMessageId.url + imMessageId
    return axiosApi({
      method: apiList.getGlucoseMessageId.method,
      url: url,
    })
  },
  setPatientMonitoringSubscribePlan(planId, planType) {
    let url = ''
    if (planType) {
      url = apiList.setPatientMonitoringSubscribePlan.url + localStorage.getItem('userId') + "&planId=" + planId + "&planType=" + planType
    }else {
      url = apiList.setPatientMonitoringSubscribePlan.url + localStorage.getItem('userId') + "&planId=" + planId
    }
    return axiosApi({
      method: apiList.setPatientMonitoringSubscribePlan.method,
      url: url,
    })
  },
  patientMonitoringCancelPlan(planId, planType) {
    let url = ''
    if (planType) {
      url = apiList.patientMonitoringCancelPlan.url + localStorage.getItem('userId') + "&planId=" + planId + "&planType=" + planType
    }else {
      url = apiList.patientMonitoringCancelPlan.url + localStorage.getItem('userId') + "&planId=" + planId
    }
    return axiosApi({
      method: apiList.patientMonitoringCancelPlan.method,
      url: url,
    })
  },
  deleteFormResult(id) {
    let url = apiList.deleteFormResult.url + '?ids[]=' + id
    return axiosApi({
      method: apiList.deleteFormResult.method,
      url: url,
    })
  },
  getBannerList(data) {
    return axiosApi({
      method: apiList.getBannerList.method,
      url: apiList.getBannerList.url,
      data
    })
  },
  getBannerSwitch(data) {
    return axiosApi({
      method: apiList.getBannerSwitch.method,
      url: apiList.getBannerSwitch.url,
      data
    })
  },
  getFormResultScore(formResultId) {
    return axiosApi({
      method: apiList.getFormResultScore.method,
      url: apiList.getFormResultScore.url + formResultId,
    })
  },
  getWxUserLogin(userRole, data) {
    return axiosApi({
      method: apiList.getWxUserLogin.method,
      url: apiList.getWxUserLogin.url + userRole,
      data
    })
  },
  setTouristUserRole(data) {
    let url = apiList.setTouristUserRole.url + data.openId + '&userRole=' + data.userRole + '&unionId=' + data.unionId
    return axiosApi({
      method: apiList.setTouristUserRole.method,
      url: url
    })
  },
  updateForDeleteFormResult(id) {
    return axiosApi({
      method: apiList.updateForDeleteFormResult.method,
      url: apiList.updateForDeleteFormResult.url+id
    })
  },
  byCategory(formEnum, messageId) {
    let url = apiList.byCategory.url + localStorage.getItem('userId')+'?formEnum=' +formEnum
    if (messageId) {
      url = url + '&messageId=' + messageId
    }
    return axiosApi({
      method: apiList.byCategory.method,
      url: url
    })
  },
  patientGetForm() {
    return axiosApi({
      method: apiList.patientGetForm.method,
      url: apiList.patientGetForm.url
    })
  },

  queryHealthRecordByMessageId(messageId) {
    return axiosApi({
      method: apiList.getHealthRecordByMessageId.method,
      url: apiList.getHealthRecordByMessageId.url + messageId
    })
  },

  formResult(id) {
    return axiosApi({
      method: apiList.formResult.method,
      url: apiList.formResult.url + id
    })
  },
  healthFormResultList(data){
    return axiosApi({
      ...apiList.healthFormResultList,
      data
    })
  },
  setPatientMessageStatus() {
    return axiosApi({
      method: apiList.setPatientMessageStatus.method,
      url: apiList.setPatientMessageStatus.url + localStorage.getItem('userId')
    })
  },
  setSystemMessageStatus(data) {
    return axiosApi({
      method: apiList.setSystemMessageStatus.method,
      url: apiList.setSystemMessageStatus.url,
      data
    })
  },
  getPatientSystemMessage(data){
    return axiosApi({
      ...apiList.getPatientSystemMessage,
      data
    })
  },
  getMessagePageData(imAccount) {
    return axiosApi({
      method: apiList.getMessagePageData.method,
      url: apiList.getMessagePageData.url + localStorage.getItem('userId') + '&patientImAccount=' + imAccount
    })
  },
  getCountMessage() {
    return axiosApi({
      method: apiList.getCountMessage.method,
      url: apiList.getCountMessage.url + localStorage.getItem('userId')
    })
  },
  getPatientHomeContent(patientHomeRegion) {
    return axiosApi({
      method: apiList.getPatientHomeContent.method,
      url: apiList.getPatientHomeContent.url + patientHomeRegion
    })
  },
  patientSubmitCms(messageId, planDetailTimeId) {
    let url = apiList.patientSubmitCms.url + '?patientId=' + localStorage.getItem('userId')
    if (messageId) {
      url = url + '&messageId=' + messageId
    }
    if (planDetailTimeId) {
      url = url + '&planDetailTimeId=' + planDetailTimeId
    }
    return axiosApi({
      method: apiList.patientSubmitCms.method,
      url: url
    })
  },
  pageContent(data) {
    return axiosApi({
      ...apiList.pageContent,
      data
    })
  },
  countPatient() {
    return axiosApi({
      method: apiList.countPatient.method,
      url: apiList.countPatient.url + localStorage.getItem('userId')
    })
  },
  planDetail(planId) {
    return axiosApi({
      method: apiList.planDetail.method,
      url: apiList.planDetail.url + planId
    })
  },
  getPatientJoinGroupAfterMenu() {
    return axiosApi({
      method: apiList.getPatientJoinGroupAfterMenu.method,
      url: apiList.getPatientJoinGroupAfterMenu.url,
    })
  },
  saveFormResultStage(data) {
    return axiosApi({
      ...apiList.saveFormResultStage,
      data
    })
  },
  getFormIntoTheGroup(category) {
    return axiosApi({
      method: apiList.getFormIntoTheGroup.method,
      url: apiList.getFormIntoTheGroup.url+category,
    })
  },
  agreeAgreement(patientId) {
    return axiosApi({
      method: apiList.agreeAgreement.method,
      url: apiList.agreeAgreement.url+patientId,
    })
  },
  patientMenuFollow() {
    return axiosApi({
      method: apiList.patientMenuFollow.method,
      url: apiList.patientMenuFollow.url + localStorage.getItem('userId'),
    })
  },
  getByDomain(domain) {
    return axiosApi({
      method: apiList.getByDomain.method,
      url: apiList.getByDomain.url+domain,
    })
  },
  appointment(appointmentId) {
    return axiosApi({
      method: apiList.appointment.method,
      url: apiList.appointment.url+appointmentId,
    })
  },
  deleteAppoint(appointmentId) {
    return axiosApi({
      method: apiList.deleteAppoint.method,
      url: apiList.deleteAppoint.url+appointmentId,
    })
  },
  getChatUserNewMsg(userNewMsgId) {
    return axiosApi({
      method: apiList.getChatUserNewMsg.method,
      url: `${apiList.getChatUserNewMsg.url}${userNewMsgId}`,
    })
  },
  withdrawChat(patientId, chatId) {
    return axiosApi({
      method: apiList.withdrawChat.method,
      url: `${apiList.withdrawChat.url}${patientId}/${chatId}`,
    })
  },
  deleteStatusChat(chatId) {
    return axiosApi({
      method: apiList.deleteStatusChat.method,
      url: `${apiList.deleteStatusChat.url}${localStorage.getItem('userId')}/${chatId}`,
    })
  },
  updateFieldReference(data) {
    return axiosApi({
      ...apiList.updateFieldReference,
      data
    })
  },
  monitorLineChart(data, id, patientId) {
    let url = apiList.monitorLineChart.url + patientId +'?businessId=' + id
    return axiosApi({
      method: 'POST',
      url: url,
      data
    })
  },
  updateIncompleteInformationField (data) {
    return axiosApi({
      ...apiList.updateIncompleteInformationField,
      data: data
    })
  },
  findIncompleteInformation(data) {
    return axiosApi({
      ...apiList.findIncompleteInformation,
      data
    })
  },
  // 批量查询医院
  hospitalQuery(data) {
    return axiosApi({
      ...apiList.hospitalQuery,
      data
    })
  },
  // 查询医院
  hospitalPage(data) {
    return axiosApi({
      ...apiList.hospitalPage,
      data
    })
  },
  // 查询城市
  listByCity(data) {
    return axiosApi({
      ...apiList.listByCity,
      data
    })
  },

  // 查询省份
  listByIds(data) {
    return axiosApi({
      ...apiList.listByIds,
      data
    })
  },
  formHistoryRecord () {
    return axiosApi({
      method: apiList.formHistoryRecord.method,
      url: apiList.formHistoryRecord.url
    })
  },
  nursingFormResultBackUp (data) {
    return axiosApi({
      method: apiList.nursingFormResultBackUp.method,
      url: apiList.nursingFormResultBackUp.url+data
    })
  },
  formResultBackUp (data) {
    return axiosApi({
      ...apiList.formResultBackUp,
      data
    })
  },
  openFormHistoryRecord () {
    return axiosApi({
      method: apiList.openFormHistoryRecord.method,
      url: apiList.openFormHistoryRecord.url
    })
  },
  putMedicine(data) {
    return axiosApi({
      ...apiList.putMedicine,
      data
    })
  },
  getdictionaryItem () {
    return axiosApi({
      method: apiList.getdictionaryItem.method,
      url: apiList.getdictionaryItem.url
    })
  },
  getCurrentMedicine() {
    return axiosApi({
      method: apiList.getCurrentMedicine.method,
      url: apiList.getCurrentMedicine.url + localStorage.getItem('userId')
    })
  },
  getMedicineHistor(current) {
    return axiosApi({
      method: apiList.getMedicineHistor.method,
      url: apiList.getMedicineHistor.url + '?patientId=' + localStorage.getItem('userId') + '&page=' + current
    })
  },
  postRecordingMedication(ids, messageId) {
    let url = apiList.postRecordingMedication.url + localStorage.getItem('userId') + '?ids=' + ids
    if (messageId) {
      url += '&messageId=' + messageId
    }
    return axiosApi({
      method: apiList.postRecordingMedication.method,
      url: url
    })
  },
  getPatientMedicationPlan(day) {
    return axiosApi({
      method: apiList.getPatientMedicationPlan.method,
      url: apiList.getPatientMedicationPlan.url + '?patientId=' + localStorage.getItem('userId') + '&drugsDay=' + day
    })
  },
  getMonitorLineChart(businessId, data, patientId) {
    let url = apiList.getMonitorLineChart.url + patientId + '?businessId=' + businessId
    return axiosApi({
      method: apiList.getMonitorLineChart.method,
      url: url,
      data
    })
  },
  getMonitorList(data) {
    return axiosApi({
      ...apiList.getMonitorList,
      data: data
    })
  },
  getPatientMonitoringDataPlan() {
    let url = apiList.getPatientMonitoringDataPlan.url + '?patientId=' + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.getPatientMonitoringDataPlan.method,
      url: url
    })
  },
  getATPatientList() {
    let url = apiList.getATPatientList.url + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.getATPatientList.method,
      url: url
    })
  },
  getChatImage(params) {
    let url = apiList.getChatImage.url + "?direction=" + params.direction + "&patientImAccount=" + params.patientImAccount
        + '&createTimeString=' + params.createTimeString + '&currentUserId=' + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.getChatImage.method,
      url: url
    })
  },
  getPatientDoctorInfo(params) {
    let url = apiList.getPatientDoctorInfo.url + params.id
    return axiosApi({
      method: apiList.getPatientDoctorInfo.method,
      url: url
    })
  },
  getPatientGroupDetail() {
    let patientId = localStorage.getItem('userId')
    let url = apiList.getPatientGroupDetail.url + '?patientId=' + patientId
    return axiosApi({
      method: apiList.getPatientGroupDetail.method,
      url: url
    })
  },
  queryUserName() {
    let headerTenant = localStorage.getItem('headerTenant')
    let mycode = Base64.decode(headerTenant)
    const url = apiList.queryUserName.url + mycode;
    return axiosApi({
      method: apiList.queryUserName.method,
      url: url
    })
  },
  uploadServerId(data) {
    return axiosApi({
      ...apiList.uploadServerId,
      data
    })
  },
  sendGroupMessage(senderImAccount, groupId, data) {
    const url = apiList.sendGroupMessage.url + senderImAccount + "/" + groupId;
    return axiosApi({
      method: apiList.sendGroupMessage.method,
      url: url,
      data
    })
  },
  getGroupMessageList(data) {
    const url = apiList.getGroupMessageList.url + data.consultationId + "?createTime=" + data.createTime;
    return axiosApi({
      method: apiList.getGroupMessageList.method,
      url: url
    })
  },
  getConsultationGroupDetail(consultationId) {
    const url = apiList.getConsultationGroupDetail.url + consultationId
    return axiosApi({
      method: apiList.getConsultationGroupDetail.method,
      url: url
    })
  },
  getContent(data) {
    const url = apiList.getContent.url + data.id
    return axiosApi({
      method: apiList.getContent.method,
      url: url
    })
  },
  getbaseContent(data) {
    return axiosApi({
      ...apiList.getbaseContent,
      data
    })
  },
  gethealthform(data) {
    const url = apiList.gethealthform.url + data.patientId + '?formEnum=' + data.formEnum+'&completeEnterGroup='+data.completeEnterGroup
    return axiosApi({
      method: apiList.gethealthform.method,
      url: url
    })
  },
  gethealthformSub(data) {
    return axiosApi({
      ...apiList.gethealthformSub,
      data
    })
  },
  planCmsReminderLog(data) {
    return axiosApi({
      ...apiList.planCmsReminderLog,
      data
    })
  },
  gethealthformSubPut(data) {
    return axiosApi({
      ...apiList.gethealthformSubPut,
      data
    })
  },
  getCustomPlanForm(data) {
    const url = apiList.getCustomPlanForm.url + data.patientId + '/' + data.planId + `?current=${data.current}&size=10`
    return axiosApi({
      method: apiList.getCustomPlanForm.method,
      url: url
    })
  },
  // 查询随访表单或表单结果
  getCustomPlanFormResult(planId, messageId) {
    let url = apiList.getCustomPlanFormResult.url + planId
    if (messageId) {
      url = url + `?messageId=${messageId}`
    }
    return axiosApi({
      method: apiList.getCustomPlanForm.method,
      url: url
    })
  },
  getCheckData(data) {
    const url = apiList.getCheckData.url + data.patientId + `?current=${data.current}&size=8&type=` + data.type
    return axiosApi({
      method: apiList.getCheckData.method,
      url: url
    })
  },
  getCheckDataformResult(data) {
    const url = apiList.getCheckDataformResult.url + data.id
    return axiosApi({
      method: apiList.getCheckDataformResult.method,
      url: url
    })
  },
  /*
   * 根据护理计划ID或计划类型或消息ID获取表单结果
   */
  formOrFormResult(planId, planType, messageId) {
    const data = {planId: planId, planType: planType, messageId: messageId}
    return axiosApi({
      method: apiList.formOrFormResult.method,
      url:  apiList.formOrFormResult.url,
      data
    })
  },

  getPlanByType(data) {
    const url = apiList.getPlanByType.url + '?planType=' + data.planType
    return axiosApi({
      method: apiList.getPlanByType.method,
      url: url
    })
  },
  /**
   * 获取随访对应表单。 支持获取自定义护理计划，自定义监测计划，护理计划的form结果。
   * @param data
   * @returns {promise|Promise<any>|(function(*=): any)|(function(*=): *)|(function(*=, *=): *)|(function(*=): any)|*}
   */
  customPlanFormResult(data) {
    let apiUrlFormResult = apiUrl + `/api/nursing/formResult/getCustomFormDetail/${data.businessId}`;
    if (data.messageId) {
      apiUrlFormResult = apiUrlFormResult + '?messageId=' + data.messageId
    }
    return axiosApi({
      method: "GET",
      url: apiUrlFormResult
    })
  },
  monitorFormResult(data) {
    return axiosApi({
      method: apiList.monitorFormResult.method,
      url: apiList.monitorFormResult.url,
      data
    })
  },
  monitoringIndicatorsFormResult(data) {
    let apiUrlFormResult = apiUrl + `/api/nursing/formResult/monitoringIndicators/${data.businessId}`;
    return axiosApi({
      method: "GET",
      url: apiUrlFormResult,
      data
    })
  },
  getformResultquery(data) {
    return axiosApi({
      ...apiList.getformResultquery,
      data
    })
  },
  loadMyBloodPressureTrendData(data) {
    const url = apiList.loadMyBloodPressureTrendData.url + data.patientId
    return axiosApi({
      method: apiList.loadMyBloodPressureTrendData.method,
      url: url
    })
  },
  getMyNursingPlans(data) {
    const url = apiList.getMyNursingPlans.url + data.patientId
    return axiosApi({
      method: apiList.getMyNursingPlans.method,
      url: url
    })
  },
  updateMyNursingPlans(data) {
    const url = apiList.updateMyNursingPlans.url + data.patientId
    return axiosApi({
      method: apiList.updateMyNursingPlans.method,
      url: url,
      data: data.content
    })
  },
  deletePatientDrugs(id) {
    const url = apiList.deletePatientDrugs.url + '?ids[]=' + id
    return axiosApi({
      method: apiList.deletePatientDrugs.method,
      url: url
    })
  },
  patientDrugsListAndHistory(data) {
    const url = apiList.patientDrugsListAndHistory.url + data.patientId
    return axiosApi({
      method: apiList.patientDrugsListAndHistory.method,
      url: url
    })
  },
  sysDrugsPage(data) {
    const url = apiList.sysDrugsPage.url + data.tenant
    return axiosApi({
      method: apiList.sysDrugsPage.method,
      url: url,
      data
    })
  },
  sysDrugsPagedetail(data) {
    return axiosApi({
      ...apiList.sysDrugsPagedetail,
      data
    })
  },
  getMedicineMonitoringSetting(id) {
    return axiosApi({
      method: apiList.getDrugsConditionMonitoring.method,
      url: apiList.getDrugsConditionMonitoring.url + id
    })
  },
  addpatientDrugs(data) {
    return axiosApi({
      ...apiList.addpatientDrugs,
      data
    })
  },
  updatepatientDrugs(data) {
    return axiosApi({
      ...apiList.updatepatientDrugs,
      data
    })
  },
  loadMyBloodSugarTrendData(data) {
    const url = apiList.loadMyBloodSugarTrendData.url + data.patientId + '?type=' + data.type + '&week=' + data.week
    return axiosApi({
      method: apiList.loadMyBloodSugarTrendData.method,
      url: url
    })
  },
  patientDayDrugsCalendar(data) {
    const url = apiList.patientDayDrugsCalendar.url + data.patientId + '?date=' + data.date
    return axiosApi({
      method: apiList.patientDayDrugsCalendar.method,
      url: url
    })
  },
  patientDayDrugsTo7Day() {
    let url = apiList.patientDayDrugsTo7Day.url + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.patientDayDrugsTo7Day.method,
      url: url
    })
  },
  findSugarByTime(data) {
    const url = apiList.findSugarByTime.url + data.patientId
    return axiosApi({
      method: apiList.findSugarByTime.method,
      url: url
    })
  },
  findSugarPage(data) {
    return axiosApi({
      method: apiList.findSugarPage.method,
      url: apiList.findSugarPage.url,
      data
    })
  },
  getSugar(id) {
    const url = apiList.getSugar.url + id
    return axiosApi({
      method: apiList.getSugar.method,
      url: url
    })
  },
  findSugarByTimeList(data) {
    const url = apiList.findSugarByTimeList.url + data.patientId + '?week=' + data.week
    return axiosApi({
      method: apiList.findSugarByTimeList.method,
      url: url
    })
  },
  sugarFormResult(data) {
    return axiosApi({
      ...apiList.sugarFormResult,
      data
    })
  },
  PutsugarFormResult(data) {
    return axiosApi({
      ...apiList.PutsugarFormResult,
      data
    })
  },
  wxlogin(data) {
    const url = apiList.wxlogin.url + "?appId=" + data.appId + "&code=" + data.code
    return axiosApi({
      method: apiList.wxlogin.method,
      url: url
    })
  },
  regGuidegetGuide(data) {
    return axiosApi({
      ...apiList.regGuidegetGuide,
      data
    })
  },
  completeEnterGroup(data) {
    return axiosApi({
      ...apiList.completeEnterGroup,
      data
    })
  },
  channelpage(data) {
    return axiosApi({
      ...apiList.channelpage,
      data
    })
  },
  channelContentpage(data) {
    return axiosApi({
      ...apiList.channelContentpage,
      data
    })
  },
  sysDrugsSearch(data) {
    const url = apiList.sysDrugsSearch.url + data.id
    return axiosApi({
      method: apiList.sysDrugsSearch.method,
      url: url
    })
  },
  getPatientImGroup(data) {
    const url = apiList.getPatientImGroup.url + "?patientId=" + data.id
    return axiosApi({
      method: apiList.getPatientImGroup.method,
      url: url
    })
  },

  getMessageList(data) {
    let url = apiList.getMessageList.url + "?receiverImAccount=" + data.receiverImAccount + "&createTime=" + data.createTime;
    if (data.size) {
      url += '&size=' + data.size
    } else {
      url += '&size=' + 20
    }
    return axiosApi({
      method: apiList.getMessageList.method,
      url: url
    })
  },
  sendMessage(data) {
    let url = apiList.sendMessage.url
    if (data.forcedManualReply) {
      url = apiList.sendMessage.url + "?forcedManualReply=" + data.forcedManualReply
    }

    return axiosApi({
      method: apiList.sendMessage.method,
      url: url,
      data
    })
  },
  FixpatientDrugs(data) {
    return axiosApi({
      ...apiList.FixpatientDrugs,
      data
    })
  },
  updateImg(data) {
    return axiosApi({
      ...apiList.updateImg,
      data
    })
  },
  annoWxSignature(data) {
    return axiosApi({
      ...apiList.annoWxSignature,
      data
    })
  },
  wxSignature(data) {
    return axiosApi({
      ...apiList.wxSignature,
      data
    })
  },
  setImLine(data) {
    const url = apiList.setImLine.url + data.id;
    return axiosApi({
      method: apiList.setImLine.method,
      url: url,
      data
    })
  },
  setMsgStatus(data) {
    let url = apiList.setMsgStatus.url + data.id;
    return axiosApi({
      method: apiList.setMsgStatus.method,
      url: url,
      data
    })
  },
  isImGroup(data) {
    return axiosApi({
      ...apiList.isImGroup,
      data
    })
  },
  pageByTenant(data) {
    const url = apiList.pageByTenant.url + data.tenant;
    return axiosApi({
      url: url,
      method: apiList.pageByTenant.method,
      data: data.data
    })
  },
  listRecommendDrugs(data) {
    const url = apiList.listRecommendDrugs.url + data.code;
    return axiosApi({
      url: url,
      method: apiList.listRecommendDrugs.method
    })
  },
  getMsgCount(imAccount) {
    const url = apiList.getMsgCount.url + imAccount;
    return axiosApi({
      url: url,
      method: apiList.getMsgCount.method
    })
  },
  channelContentquery(data) {
    return axiosApi({
      ...apiList.channelContentquery,
      data
    })
  },
  getcmsdetail(data, custom = {}) {
    const url = apiList.getcmsdetail.url + data.id
    return axiosApi({
      method: apiList.getcmsdetail.method,
      url: url,
      custom
    })
  },
  getChannelGroup(data, custom = {}) {
    const url = apiList.channelGroup.url + data
    return axiosApi({
      method: apiList.channelGroup.method,
      url: url,
      custom
    })
  },
  getpatientDrugs(data) {
    const url = apiList.getpatientDrugs.url + data.id
    return axiosApi({
      method: apiList.getpatientDrugs.method,
      url: url
    })
  },
  queryByDrugsTimestamp(data) {
    const url = apiList.queryByDrugsTimestamp.url + '?drugsTimestamp=' + data.drugsTimestamp
    return axiosApi({
      method: apiList.queryByDrugsTimestamp.method,
      url: url
    })
  },
  clockIn(data) {
    const url = apiList.clockIn.url + '?ids=' + data.ids
    return axiosApi({
      method: apiList.clockIn.method,
      url: url
    })
  },
  getappointDoctor(data) {
    return axiosApi({
      method: apiList.getappointDoctor.method,
      url: apiList.getappointDoctor.url,
      data
    })
  },
  searchDoctor(data) {
    const url = apiList.searchDoctor.url + '/' + data.id
    return axiosApi({
      method: apiList.searchDoctor.method,
      url: url
    })
  },
  getDoctorStatus(data) {
    const url = apiList.getDoctorStatus.url + '/' + data.id +' ?patientId='+data.patientId
    return axiosApi({
      method: apiList.getDoctorStatus.method,
      url: url
    })
  },
  postappointment(data) {
    return axiosApi({
      method: apiList.postappointment.method,
      url: apiList.postappointment.url,
      data
    })
  },
  postappoint(data) {
    return axiosApi({
      method: apiList.postappoint.method,
      url: apiList.postappoint.url,
      data
    })
  },
  putappoint(appointmentId) {
    const url = apiList.putappoint.url+appointmentId
    return axiosApi({
      method: apiList.putappoint.method,
      url: url,
    })
  },
  channelContentWithReply(data) {
    const url = apiList.channelContentWithReply.url + data.id
    return axiosApi({
      method: apiList.channelContentWithReply.method,
      url: url,
      data
    })
  },
  updateHitCount(data) {
    const url = apiList.updateHitCount.url + data.id
    return axiosApi({
      method: apiList.updateHitCount.method,
      url: url,
      data
    })
  },
  contentReply(data) {
    return axiosApi({
      method: apiList.contentReply.method,
      url: apiList.contentReply.url,
      data
    })
  },
  contentReplylike(data) {
    return axiosApi({
      method: apiList.contentReplylike.method,
      url: apiList.contentReplylike.url,
      data
    })
  },
  contentCollect(data) {
    return axiosApi({
      method: apiList.contentCollect.method,
      url: apiList.contentCollect.url,
      data
    })
  },
  contentCollectList(data) {
    return axiosApi({
      method: apiList.contentCollectList.method,
      url: apiList.contentCollectList.url,
      data
    })
  },
  createIfNotExist(data) {
    return axiosApi({
      method: apiList.createIfNotExist.method,
      url: apiList.createIfNotExist.url + '/' + data.code,
      data
    })
  },
  uicreateIfNotExist(data) {
    return axiosApi({
      method: apiList.uicreateIfNotExist.method,
      url: apiList.uicreateIfNotExist.url + '/' + data.code,
      data
    })
  },
  referral(data) {
    return axiosApi({
      method: apiList.referral.method,
      url: apiList.referral.url + '/' + data.id,
      data
    })
  },
  //查询患者拉新配置
  generatePatientInvitationQRcode(id) {
    return axiosApi({
      method:'GET',
      url: `/api/ucenter/patientRecommendRelationship/generatePatientInvitationQRcode?patientId=${id}`,
    })
  },
  // 设置文章被打开。
  reminderLogSubmitSuccess(messageId) {
    return axiosApi({
      method: apiList.reminderLogSubmitSuccess.method,
      url: apiList.reminderLogSubmitSuccess.url + "?messageId=" + messageId,
    })
  },
  // 无授权，设置文章被打开
  annoReminderLogSubmitSuccess(messageId) {
    return axiosApi({
      method: apiList.annoReminderLogSubmitSuccess.method,
      url: apiList.annoReminderLogSubmitSuccess.url + "?messageId=" + messageId,
    })
  },
  // 设置消息被打卡 (随访的表单)
  reminderLogOpenMessage(messageId) {
    return axiosApi({
      method: apiList.reminderLogOpenMessage.method,
      url: apiList.reminderLogOpenMessage.url + "?messageId=" + messageId,
    })
  }

}
