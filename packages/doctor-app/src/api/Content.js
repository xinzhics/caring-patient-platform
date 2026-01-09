import axiosApi from './apiAxios.js'
import {Base64} from "js-base64";
import baseUrl from './baseUrl.js'

const apiList = {

  //获取患者的基本信息
  getContent: {
    method: 'GET',
    url: baseUrl + `/ucenter/patient/`
  },
  // 是否开启历史
  formHistoryRecord: {
    method: 'GET',
    url: baseUrl + `/wx/regGuide/formHistoryRecord`
  },
  //获取表单
  gethealthform: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/getFromResultByCategory/`
  },
  //新增表单
  gethealthformSub: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult`
  },
  //获取会诊管理历史消息
  getGroupMessageList: {
    method: 'POST',
    url: baseUrl + `/msgs/groupIm/getGroupImMessage/`
  },
  //发送会诊消息
  sendGroupMessage: {
    method: 'POST',
    url: baseUrl + `/ucenter/consultationGroup/sendGroupMessage/`
  },
  //修改表单
  gethealthformSubPut: {
    method: 'PUT',
    url: baseUrl + `/nursing/formResult`
  },
  //获取检验数据
  getCheckData: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/getCheckData/`
  },
  //获取检验数据结果
  getCheckDataformResult: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/`
  },

  getCustomPlanFormResult: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/getCustomFormDetail/`
  },
  //获取检验数据单条空的数据
  getPlanByType: {
    method: 'GET',
    url: baseUrl + `/nursing/plan/getPlanByType`
  },
  //获取检验数据单条表单
  getPlanByTypequery: {
    method: 'POST',
    url: baseUrl + `/nursing/form/query`
  },
  //获取血压走势数据
  loadMyBloodPressureTrendData: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/getPatientBloodPress/`
  },

  //获取血糖的列表
  findSugarByTime: {
    method: 'GET',
    url: baseUrl + `/nursing/sugarFormResult/findSugarByTime/`
  },
  //智能提醒
  getMyNursingPlans: {
    method: 'GET',
    url: baseUrl + `/nursing/patientNursingPlan/getMyNursingPlans/`
  },
  //智能提醒修改
  updateMyNursingPlans: {
    method: 'POST',
    url: baseUrl + `/nursing/patientNursingPlan/updateMyNursingPlans/`
  },

  //我的药箱  查询当前用药和历史用药
  patientDrugsListAndHistory: {
    method: 'GET',
    url: baseUrl + `/nursing/patientDrugs/patientDrugsListAndHistory/`
  },
  //我的药箱  整个药品库
  sysDrugsPage: {
    method: 'POST',
    url: baseUrl + `/nursing/sysDrugs/pageByTenant/`
  },
  //我的药箱  单个药品查询详情
  sysDrugsPagedetail: {
    method: 'POST',
    url: baseUrl + `/nursing/sysDrugs/query`
  },
  //我的药箱  患者添加用药
  addpatientDrugs: {
    method: 'POST',
    url: baseUrl + `/nursing/patientDrugs/savePatientDrugs`
  },
  //修改
  updatepatientDrugs: {
    method: 'POST',
    url: baseUrl + `/nursing/patientDrugs/updatePatientDrugs`
  },
  //获取单个药品详情
  getpatientDrugs: {
    method: 'POST',
    url: baseUrl + `/nursing/patientDrugs/getPatientDrugsDetails/`
  },

  //血糖走势
  loadMyBloodSugarTrendData: {
    method: 'GET',
    url: baseUrl + `/nursing/sugarFormResult/loadMyBloodSugarTrendData/`
  },


  //用药日历  查询当前用药和历史用药
  patientDayDrugsCalendar: {
    method: 'GET',
    url: baseUrl + `/nursing/patientDayDrugs/calendar/`
  },

  //提交血糖的结果
  sugarFormResult: {
    method: 'POST',
    url: baseUrl + `/nursing/sugarFormResult`
  },
  //修改血糖
  PutsugarFormResult: {
    method: 'PUT',
    url: baseUrl + `/nursing/sugarFormResult`
  },
  //注册引导表单一
  regGuidegetGuide: {
    method: 'GET',
    url: baseUrl + `/wx/regGuide/getGuide`
  },
  //cms导航栏
  channelpage: {
    method: 'POST',
    url: baseUrl + `/cms/channel/anno/page`
  },
  //cms内容
  channelContentpage: {
    method: 'POST',
    url: baseUrl + `/cms/channelContent/anno/page`
  },
  channelGroup: {
    method: 'GET',
    url: baseUrl + `/cms/channel/anno/getChannelGroup/`
  },
  //我的药箱  查单个药品
  sysDrugsSearch: {
    method: 'GET',
    url: baseUrl + `/nursing/sysDrugs/`
  },

  //获取im群组
  getPatientImGroup: {
    method: 'GET',
    url: baseUrl + `/ucenter/patient/anno/getPatientImGroup`
  },

  //获取历史消息
  getMessageList: {
    method: 'GET',
    url: baseUrl + `/msgs/im/anno/chat/history`
  },

  //发送消息
  sendMessage: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctor/anno/sendChat`
  },

  // 删除一条群发消息记录
  deleteMoreMessage: {
    method: 'DELETE',
    url: baseUrl + `/msgs/im/deleteGroupSend/`
  },

  // 新的群发消息
  sendMoreMessage: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctor/anno/sendMoreChat`
  },

  /**
   * 群发消息列表
   */
  moreMessageList: {
    method: 'POST',
    url: baseUrl + `/msgs/im/doctorGroupChatList`
  },

  //我的药箱  修改
  FixpatientDrugs: {
    method: 'POST',
    url: baseUrl + `/nursing/patientDrugs/updatePatientDrugs`
  },

  //上传图片
  updateImg: {
    method: 'POST',
    url: baseUrl + `/file/file/app/upload`
  },
  //微信获取分享扫一扫接口
  wxSignature: {
    method: 'POST',
    url: baseUrl + `/wx/config/jssdk/signature`
  },
  wxAnnoSignature: {
    method: 'POST',
    url: baseUrl + `/wx/config/jssdk/anno/signature`
  },
  //修改患者未读消息为已读
  setMsgStatus: {
    method: 'POST',
    url: baseUrl + `/msgs/im/chat/refreshDoctorAndPatientMsg/`
  },
  //查单个药品
  pageByTenant: {
    method: 'POST',
    url: baseUrl + `/nursing/sysDrugs/pageByTenant/`
  },
  //是否显示患者群组
  isImGroup: {
    method: 'PUT',
    url: baseUrl + `/ucenter/patient`
  },
  //获取推荐用要
  listRecommendDrugs: {
    method: 'GET',
    url: baseUrl + `/tenant/drugsConfig/listRecommendDrugs/`
  },

  //cms获取轮播图的接口
  channelContentquery: {
    method: 'POST',
    url: baseUrl + `/cms/channelContent/anno/query`
  },
  channelContentWithReply: {
    method: 'GET',
    url: baseUrl + `/cms/channelContent/anno/channelContentWithReply/`
  },
  updateHitCount: {
    method: 'PUT',
    url: baseUrl + `/cms/channelContent/anno/updateHitCount/`
  },
  contentReply: {
    method: 'POST',
    url: baseUrl + `/cms/contentReply`
  },
  contentReplylike: {
    method: 'POST',
    url: baseUrl + `/cms/contentReply/like`
  },
  contentCollect: {
    method: 'POST',
    url: baseUrl + `/cms/contentCollect`
  },
  contentCollectList: {
    method: 'POST',
    url: baseUrl + `/cms/contentCollect/page`
  },
  queryByDrugsTimestamp: {
    method: 'GET',
    url: baseUrl + `/nursing/patientDrugsTime/queryByDrugsTimestamp`
  },

  clockIn: {
    method: 'POST',
    url: baseUrl + `/nursing/patientDrugsTime/clockIn`
  },

  //获取医生聊天列表
  getDoctorMsgList: {
    method: 'POST',
    url: baseUrl + `/msgs/im/anno/doctorNewChatUser/shared`
  },
  //医生信息
  getDoctorInfo: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor`
  },
  //修改医生信息
  setDoctorInfo: {
    method: 'PUT',
    url: baseUrl + `/ucenter/doctor`
  },

  //获取医生预约数据
  getappointConfig: {
    method: 'GET',
    url: baseUrl + `/nursing/appointConfig/doctorId/`
  },
  //修改医生预约数据
  PutappointConfig: {
    method: 'PUT',
    url: baseUrl + `/nursing/appointConfig`
  },
  getappointment: {
    method: 'GET',
    url: baseUrl + `/nursing/appointment/statistics/day`
  },
  getappointmentweek: {
    method: 'GET',
    url: baseUrl + `/nursing/appointment/statistics/week`
  },

  doctorGetpatient: {
    method: 'POST',
    url: baseUrl + `/nursing/appointment/page`
  },
  putappointmentStatus: {
    method: 'PUT',
    url: baseUrl + `/nursing/appointment`
  },

  getConsultationGroupDetail: {
    method: 'GET',
    url: baseUrl + `/ucenter/consultationGroup/getConsultationGroupDetail/`
  },
  uploadServerId: {
    method: 'POST',
    url: baseUrl + `/wx/config/material/get_material`
  },
  //获转诊列表
  referralQuery: {
    method: 'POST',
    url: baseUrl + `/ucenter/referral/query`
  },

  //查询所有医生
  doctorQuery: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctor/query`
  },
  //发起转诊
  launchReferral: {
    method: 'POST',
    url: baseUrl + `/ucenter/referral/launchReferral`
  },
  //接受转诊
  acceptReferralId: {
    method: 'PUT',
    url: baseUrl + `/ucenter/referral/acceptReferral/`
  },
  //修改转诊状态
  referralPut: {
    method: 'PUT',
    url: baseUrl + `/ucenter/referral`
  },
  //删除转移
  DelreferralPut: {
    method: 'DELETE',
    url: baseUrl + `/ucenter/referral`
  },


  getExit: {
    method: 'GET',
    url: baseUrl + `/ucenter/anno/doctor/logout`
  },
  getGroupDoctor: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctorGroup/getGroupDoctor/baseInfo/readMsgStatus`
  },
  setGroupDoctor: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorGroup/updateDoctorNoReadGroupDoctor`
  },
  getPatientGroupDetail: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor/getPatientGroupDetail`
  },
  getPatientsByStatusGroupNew: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor/getPatientsByStatusGroupNew`
  },
  getPatientDoctorInfo: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor/getDoctorBaseInfoByPatientId/`
  },
  //im中查看图片
  getChatImage: {
    method: 'GET',
    url: baseUrl + `/msgs/im/anno/chat/image`
  },
  //接受患者
  acceptReferral: {
    method: 'PUT',
    url: baseUrl + `/ucenter/referral/acceptReferral/`
  },
  //查转诊详情
  referral: {
    method: 'GET',
    url: baseUrl + `/ucenter/referral/`
  },
  planCmsReminderLog: {
    method: 'POST',
    url: baseUrl + `/nursing/planCmsReminderLog/page`
  },
  getCustomPlanForm: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/getCustomForm/`
  },
  //获取监测计划列表
  getPatientMonitoringDataPlan: {
    method: 'GET',
    url: baseUrl + `/nursing/patientNursingPlan/patientPlanList/formResult`
  },
  //取消关注监测计划
  patientMonitoringCancelPlan: {
    method: 'DELETE',
    url: baseUrl + `/nursing/patientNursingPlan/patient/subscribe?patientId=`
  },
  //关注监测计划
  setPatientMonitoringSubscribePlan: {
    method: 'PUT',
    url: baseUrl + `/nursing/patientNursingPlan/subscribePlan?patientId=`
  },
  //监测数据列表倒序
  getMonitorList: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/monitorFormResult`
  },
  //监测数据折线图
  getMonitorLineChart: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/monitorLineChart/`
  },
  //用药数据
  getPatientMedicationPlan: {
    method: 'GET',
    url: baseUrl + `/nursing/patientDrugsTime/queryByDay`
  },
  getMedicineHistor: {
    method: 'POST',
    url: baseUrl + `/nursing/patientDrugsHistoryLog/app/historyDate/page`
  },
  updateFieldReference: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/updatePatientFormFieldReference`
  },
  // 字典
  getdictionaryItem: {
    method: 'POST',
    url: baseUrl + `/authority/dictionaryItem/anno/query`
  },
  //
  getH5UIQuery: {
    method: 'GET',
    url: baseUrl + `/tenant/h5Ui/anno/mapQuery/`
  },
  getNursingStaffInfo: {
    method: 'GET',
    url: baseUrl + `/ucenter/nursingStaff/`
  },
  getSystemMsg: {
    method: 'POST',
    url: baseUrl + `/ucenter/systemMsg/page`
  },
  getAnnoDoctorInfo: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor/anno/`
  },
  getByDomain: {
    method: 'GET',
    url: baseUrl + `/tenant/tenant/anno/getByDomain`
  },
  tenantStatisticsChartList: {
    method: 'GET',
    url: baseUrl + `/nursing/statisticsData/tenantStatisticsChartList`
  },
  getStatisticsChartDetail: {
    method: 'POST',
    url: baseUrl + `/nursing/statisticsData/tenantDataStatistics/doctor/`
  },
  getByCode: {
    method: 'GET',
    url: baseUrl + `/tenant/tenant/anno/getByCode/`
  },
  diagnosticTypeStatisticsDoctor: {
    method: 'GET',
    url: baseUrl + `/ucenter/statistics/diagnosticTypeStatisticsDoctor/`
  },
  monitorFormResult: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/monitorFormResult`
  },
  monitorLineChart: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/monitorLineChart/`
  },
  //  查询医生患者的列表并按首字母返回
  pageDoctorPatient: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/pageDoctorPatient`
  },
  // 新增小组患者
  saveDoctorGroup: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/saveDoctorGroup`
  },
  // 小组内患者的列表
  pageGroupPatient: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/pageGroupPatient`
  },
//  修改自定义小组
  updateDoctorGroup: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/updateDoctorGroup`
  },
  // 删除小组内的某个患者
  deleteDoctorGroupPatient: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/deleteDoctorGroupPatient`
  },
//  医生的小组列表
  doctorCustomGroup: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/list`
  },
//  删除小组
  deleteDoctorGroup: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctorCustomGroup/deleteDoctorGroup`
  },
  //删除我的列表中的消息
  deleteStatusChat: {
    method: 'PUT',
    url: baseUrl + `/msgs/im/updateDeleteStatusChat/`
  },
  //更新消息为撤回状态，并发送通知给群组成员
  withdrawChat: {
    method: 'PUT',
    url: baseUrl + `/msgs/im/withdrawChat/`
  },
  //获取消息记录中最新的信息
  getChatUserNewMsg: {
    method: 'PUT',
    url: baseUrl + `/msgs/im/getChatUserNewMsg/`
  },
  //获取当前角色和患者群组的消息列表记录
  findChatUserNewMsg: {
    method: 'GET',
    url: baseUrl + `/msgs/im/findChatUserNewMsg/`
  },
  // 【精准预约】 医助统计待审核预约数量
  approvalNumber: {
    method: 'GET',
    url: baseUrl + `/nursing/appointment/doctor/statistics/approvalNumber?doctorId=`
  },
  // 【精准预约】 医生待审核数据列表接口
  approval: {
    method: 'POST',
    url: baseUrl + `/nursing/appointment/doctor/appoint/approval`
  },
  // 【精准预约】 医生医助一键清除 已过期 预约数据
  clearAppoint: {
    method: 'PUT',
    url: baseUrl + `/nursing/appointment/clear/appoint?doctorId=`
  },
  // 【精准预约】 医生审批预约接口
  doctorApprove: {
    method: 'POST',
    url: baseUrl + `/nursing/appointment/doctorApprove`
  },
  // 【精准预约】 医生医助同意增加号源并对预约进行审批通过
  addDirectApproval: {
    method: 'POST',
    url: baseUrl + `/nursing/appointment/directApproval?appointmentId=`
  },
  // 表单结果历史记录分页查询
  formResultBackUp: {
    method: 'POST',
    url: baseUrl + `/nursing/formResultBackUp/page`
  },
  // 表单结果历史记录查询详情
  nursingFormResultBackUp: {
    method: 'GET',
    url: baseUrl + `/nursing/formResultBackUp/`
  },
  // 删除修改历史
  delFormResultBackUp: {
    method: 'DELETE',
    url: baseUrl + `/nursing/formResultBackUp`
  },
  // 医生设置退出聊天
  doctorExitChat: {
    method: 'PUT',
    url: baseUrl + `/ucenter/patient/doctorExitChat`
  },
  // 表单提交后。查询表单的成绩结果
  getFormResultScore:{
    method: 'GET',
    url: baseUrl+`/nursing/formResultScore/queryFormResultScore?formResultId=`
  },
  // 使用护理计划类型或者护理计划ID或者消息ID查询表单
  formOrFormResult: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/getPlanForm`
  },
  //获取患者是否订阅这个计划
  getPatientSubscribePlan: {
    method: 'GET',
    url: baseUrl + `/nursing/patientNursingPlan/patient/subscribe?patientId=`
  },
  //患者取消订阅这个计划
  deletePatientSubscribePlan: {
    method: 'DELETE',
    url: baseUrl + `/nursing/patientNursingPlan/patient/subscribe?patientId=`
  },
  //患者订阅这个计划
  putPatientSubscribePlan: {
    method: 'PUT',
    url: baseUrl + `/nursing/patientNursingPlan/subscribePlan?patientId=`
  },
  //用药日历  查询周打卡
  patientDayDrugsTo7Day: {
    method: 'GET',
    url: baseUrl + `/nursing/patientDayDrugs/7Day/`
  },
  //患者疾病信息列表
  healthFormResultList: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/healthFormResultList`
  },
  updateForDeleteFormResult:{
    method: 'PUT',
    url: baseUrl + `/nursing/formResult/updateForDeleteFormResult/`
  },
  patientGetForm:{
    method: 'GET',
    url: baseUrl + `/nursing/form/patientGetForm`
  },
  byCategory:{
    method: 'GET',
    url: baseUrl + `/nursing/formResult/byCategory/`
  },
  // 首次分阶段保存基本信息或疾病信息
  saveFormResultStage: {
    method: 'POST',
    url: baseUrl + `/nursing/formResult/saveFormResultStage`
  },
  //删除用药提醒
  deletePatientDrugs: {
    method: 'DELETE',
    url: baseUrl + `/nursing/patientDrugs`
  },
  getHealthRecordByMessageId:{
    method: 'GET',
    url: baseUrl + `/nursing/formResult/getHealthRecordByMessageId?messageId=`
  },
  // 分页获取血糖列表
  findSugarPage: {
    method: 'POST',
    url: baseUrl + `/nursing/sugarFormResult/findSugarByTime/monitorQueryDate`
  },
  // 通过消息ID查询数据
  getGlucoseMessageId:{
    method: 'GET',
    url: baseUrl+`/nursing/sugarFormResult/getByMessageId?messageId=`
  },
  // 通过消息ID查询数据
  getDoctorSeePatientGroup:{
    method: 'PUT',
    url: baseUrl+`/ucenter/patient/doctorSeePatientGroup?doctorId=`
  },
  // 删除表单结果
  deleteFormResult:{
    method: 'DELETE',
    url: baseUrl + `/nursing/formResult`
  },
  // 获取一个血糖记录
  getSugar: {
    method: 'GET',
    url: baseUrl + `/nursing/sugarFormResult/`
  },
}

export default {
  deleteFormResult(id) {
    let url = apiList.deleteFormResult.url + '?ids[]=' + id
    return axiosApi({
      method: apiList.deleteFormResult.method,
      url: url,
    })
  },
  getSugar(id) {
    const url = apiList.getSugar.url + id
    return axiosApi({
      method: apiList.getSugar.method,
      url: url
    })
  },
  getDoctorSeePatientGroup() {
    let url = apiList.getDoctorSeePatientGroup.url + localStorage.getItem('caring_doctor_id') + '&patientId=' + localStorage.getItem('patientId')
    return axiosApi({
      method: apiList.getDoctorSeePatientGroup.method,
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
  findSugarPage(data) {
    return axiosApi({
      method: apiList.findSugarPage.method,
      url: apiList.findSugarPage.url,
      data
    })
  },
  queryHealthRecordByMessageId(messageId) {
    let url = apiList.getHealthRecordByMessageId.url + messageId
    return axiosApi({
      method: apiList.getHealthRecordByMessageId.method,
      url: url
    })
  },
  deletePatientDrugs(id) {
    const url = apiList.deletePatientDrugs.url + '?ids[]=' + id
    return axiosApi({
      method: apiList.deletePatientDrugs.method,
      url: url
    })
  },
  saveFormResultStage(data) {
    return axiosApi({
      ...apiList.saveFormResultStage,
      data
    })
  },
  byCategory(formEnum) {
    let url = apiList.byCategory.url + localStorage.getItem('patientId')+'?formEnum=' +formEnum
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
  updateForDeleteFormResult(id) {
    return axiosApi({
      method: apiList.updateForDeleteFormResult.method,
      url: apiList.updateForDeleteFormResult.url+id
    })
  },
  healthFormResultList(data){
    return axiosApi({
      ...apiList.healthFormResultList,
      data
    })
  },
  patientDayDrugsTo7Day() {
    let url = apiList.patientDayDrugsTo7Day.url + localStorage.getItem('patientId')
    return axiosApi({
      method: apiList.patientDayDrugsTo7Day.method,
      url: url
    })
  },
  getPatientSubscribePlan(patientId, planId, planType) {
    let url = apiList.getPatientSubscribePlan.url + patientId
    if (planId) {
      url += `&planId=${planId}`
    }
    if (planType) {
      url += `&planType=${planType}`
    }
    return axiosApi({
      method: apiList.getPatientSubscribePlan.method,
      url: url,
    })
  },
  deletePatientSubscribePlan(patientId, planId, planType) {
    let url = apiList.deletePatientSubscribePlan.url + patientId
    if (planId) {
      url += `&planId=${planId}`
    }
    if (planType) {
      url += `&planType=${planType}`
    }
    return axiosApi({
      method: apiList.deletePatientSubscribePlan.method,
      url: url,
    })
  },
  putPatientSubscribePlan(patientId, planId, planType) {
    let url = apiList.putPatientSubscribePlan.url + patientId
    if (planId) {
      url += `&planId=${planId}`
    }
    if (planType) {
      url += `&planType=${planType}`
    }
    return axiosApi({
      method: apiList.putPatientSubscribePlan.method,
      url: url,
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
  getFormResultScore(formResultId) {
    return axiosApi({
      method: apiList.getFormResultScore.method,
      url: apiList.getFormResultScore.url + formResultId,
    })
  },
  doctorExitChat(data) {
    return axiosApi({
      method: apiList.doctorExitChat.method,
      url: apiList.doctorExitChat.url + '?patientId=' + data.patientId + '&exitChat=' + data.exitChat
    })
  },
  delFormResultBackUp(data) {
    return axiosApi({
      method: apiList.delFormResultBackUp.method,
      url: apiList.delFormResultBackUp.url + '?ids[]=' + data.id
    })
  },
  nursingFormResultBackUp(data) {
    return axiosApi({
      method: apiList.nursingFormResultBackUp.method,
      url: apiList.nursingFormResultBackUp.url + data
    })
  },
  addDirectApproval(appointmentId) {
    return axiosApi({
      method: apiList.addDirectApproval.method,
      url: apiList.addDirectApproval.url + appointmentId
    })
  },
  doctorApprove(data) {
    return axiosApi({
      method: apiList.doctorApprove.method,
      url: apiList.doctorApprove.url,
      data
    })
  },
  clearAppoint(doctorId) {
    return axiosApi({
      method: apiList.clearAppoint.method,
      url: apiList.clearAppoint.url + doctorId,
    })
  },
  approval(data) {
    return axiosApi({
      method: apiList.approval.method,
      url: apiList.approval.url,
      data
    })
  },
  approvalNumber(doctorId) {
    return axiosApi({
      method: apiList.approvalNumber.method,
      url: apiList.approvalNumber.url + doctorId,
    })
  },
  findChatUserNewMsg(receiverImAccount) {
    return axiosApi({
      method: apiList.findChatUserNewMsg.method,
      url: `${apiList.findChatUserNewMsg.url}${receiverImAccount}`,
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
      url: `${apiList.deleteStatusChat.url}${localStorage.getItem('caring_doctor_id')}/${chatId}`,
    })
  },
  deleteDoctorGroup(groupId) {
    return axiosApi({
      method: apiList.deleteDoctorGroup.method,
      url: `${apiList.deleteDoctorGroup.url}?groupId=${groupId}`,
    })
  },
  doctorCustomGroup(doctorId) {
    return axiosApi({
      method: apiList.doctorCustomGroup.method,
      url: `${apiList.doctorCustomGroup.url}?doctorId=${doctorId}`,
    })
  },
  deleteDoctorGroupPatient(groupId, patientId) {
    return axiosApi({
      method: apiList.deleteDoctorGroupPatient.method,
      url: `${apiList.deleteDoctorGroupPatient.url}?groupId=${groupId}&patientId=${patientId}`,
    })
  },
  updateDoctorGroup(data) {
    return axiosApi({
      ...apiList.updateDoctorGroup,
      data
    })
  },
  pageGroupPatient(data) {
    return axiosApi({
      ...apiList.pageGroupPatient,
      data
    })
  },
  saveDoctorGroup(data) {
    return axiosApi({
      ...apiList.saveDoctorGroup,
      data
    })
  },
  //  查询医生患者的列表并按首字母返回
  pageDoctorPatient(data) {
    return axiosApi({
      ...apiList.pageDoctorPatient,
      data
    })
  },
  monitorLineChart(data, id, patientId) {
    let url = apiList.monitorLineChart.url + patientId + '?businessId=' + id
    return axiosApi({
      method: 'POST',
      url: url,
      data
    })
  },
  monitorFormResult(data) {
    return axiosApi({
      method: apiList.monitorFormResult.method,
      url: apiList.monitorFormResult.url,
      data
    })
  },
  diagnosticTypeStatisticsDoctor() {
    return axiosApi({
      method: apiList.diagnosticTypeStatisticsDoctor.method,
      url: apiList.diagnosticTypeStatisticsDoctor.url + localStorage.getItem('caring_doctor_id'),
    })
  },
  getByCode() {
    return axiosApi({
      method: apiList.getByCode.method,
      url: apiList.getByCode.url + Base64.decode(localStorage.getItem('headerTenant')),
    })
  },
  getStatisticsChartDetail(data) {
    return axiosApi({
      method: apiList.getStatisticsChartDetail.method,
      url: apiList.getStatisticsChartDetail.url + localStorage.getItem('caring_doctor_id'),
      data
    })
  },
  tenantStatisticsChartList() {
    return axiosApi({
      ...apiList.tenantStatisticsChartList,
    })
  },
  getByDomain(domain) {
    return axiosApi({
      method: apiList.getByDomain.method,
      url: apiList.getByDomain.url + '?domain=' + domain
    })
  },
  getAnnoDoctorInfo(data) {
    return axiosApi({
      method: apiList.getAnnoDoctorInfo.method,
      url: apiList.getAnnoDoctorInfo.url + data.id + '?tenantCode=' + data.tenantCode
    })
  },
  monitoringIndicatorsFormResult(data) {
    let apiUrlFormResult = baseUrl + `/nursing/formResult/monitoringIndicators/${data.businessId}`;
    return axiosApi({
      method: "GET",
      url: apiUrlFormResult,
      data
    })
  },
  getSystemMsg(data) {
    return axiosApi({
      ...apiList.getSystemMsg,
      data
    })
  },
  getNursingStaffInfo(id) {
    return axiosApi({
      method: apiList.getNursingStaffInfo.method,
      url: apiList.getNursingStaffInfo.url + id
    })
  },
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
  getH5UIQuery() {
    let headerTenant = localStorage.getItem('headerTenant')
    return axiosApi({
      method: apiList.getH5UIQuery.method,
      url: apiList.getH5UIQuery.url + Base64.decode(headerTenant)
    })
  },
  updateFieldReference(data) {
    return axiosApi({
      ...apiList.updateFieldReference,
      data
    })
  },
  getdictionaryItem() {
    return axiosApi({
      method: apiList.getdictionaryItem.method,
      url: apiList.getdictionaryItem.url
    })
  },
  getMedicineHistor(current) {
    return axiosApi({
      method: apiList.getMedicineHistor.method,
      url: apiList.getMedicineHistor.url + '?patientId=' + localStorage.getItem('patientId') + '&page=' + current
    })
  },
  getPatientMedicationPlan(day) {
    return axiosApi({
      method: apiList.getPatientMedicationPlan.method,
      url: apiList.getPatientMedicationPlan.url + '?patientId=' + localStorage.getItem('patientId') + '&drugsDay=' + day
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
  getPatientMonitoringDataPlan(patientId) {
    let url = apiList.getPatientMonitoringDataPlan.url + '?patientId=' + patientId
    return axiosApi({
      method: apiList.getPatientMonitoringDataPlan.method,
      url: url
    })
  },
  getCustomPlanForm(data) {
    const url = apiList.getCustomPlanForm.url + data.patientId + '/' + data.planId + `?current=${data.current}&size=40`
    return axiosApi({
      method: apiList.getCustomPlanForm.method,
      url: url
    })
  },
  setPatientMonitoringSubscribePlan(patientId, planId, planType) {
    let url = ''
    if (planType) {
      url = apiList.setPatientMonitoringSubscribePlan.url + patientId + "&planId=" + planId + "&planType=" + planType
    }else {
      url = apiList.setPatientMonitoringSubscribePlan.url + patientId + "&planId=" + planId
    }
    return axiosApi({
      method: apiList.setPatientMonitoringSubscribePlan.method,
      url: url,
    })
  },
  patientMonitoringCancelPlan(patientId, planId, planType) {
    let url = ''
    if (planType) {
      url = apiList.patientMonitoringCancelPlan.url + patientId + "&planId=" + planId + "&planType=" + planType
    }else {
      url = apiList.patientMonitoringCancelPlan.url + patientId + "&planId=" + planId
    }
    return axiosApi({
      method: apiList.patientMonitoringCancelPlan.method,
      url: url,
    })
  },
  planCmsReminderLog(data) {
    return axiosApi({
      ...apiList.planCmsReminderLog,
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
  acceptReferral(params) {
    let url = apiList.acceptReferral.url + params.id + '?acceptDoctorId=' + params.acceptDoctorId
    return axiosApi({
      method: apiList.acceptReferral.method,
      url: url
    })
  },
  getChatImage(params) {
    let url = apiList.getChatImage.url + "?direction=" + params.direction + "&patientImAccount=" + params.patientImAccount + '&createTimeString=' + params.createTimeString + "&currentUserId=" + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getChatImage.method,
      url: url
    })
  },
  getPatientDoctorInfo(params) {
    let url = apiList.getPatientDoctorInfo.url + params.patientId
    return axiosApi({
      method: apiList.getPatientDoctorInfo.method,
      url: url
    })
  },
  getPatientsByStatusGroupNew(params) {
    let url = apiList.getPatientsByStatusGroupNew.url + '?userId=' + params.id + "&dimension=" + params.dimension
    return axiosApi({
      method: apiList.getPatientsByStatusGroupNew.method,
      url: url
    })
  },
  getPatientGroupDetail(patientId) {
    let doctorId = localStorage.getItem('caring_doctor_id')
    let url = apiList.getPatientGroupDetail.url + '?doctorId=' + doctorId + "&patientId=" + patientId
    return axiosApi({
      method: apiList.getPatientGroupDetail.method,
      url: url
    })
  },
  setGroupDoctor(data) {
    return axiosApi({
      ...apiList.setGroupDoctor,
      data
    })
  },
  getGroupDoctor() {
    let doctorId = localStorage.getItem('caring_doctor_id')
    const url = apiList.getGroupDoctor.url + '?doctorId=' + doctorId
    return axiosApi({
      method: apiList.getGroupDoctor.method,
      url: url
    })
  },
  getExit() {
    let doctorId = localStorage.getItem('caring_doctor_id')
    let headerTenant = localStorage.getItem('headerTenant')
    const url = apiList.getExit.url + '?doctorId=' + doctorId + '&tenantCode=' + Base64.decode(headerTenant)
    return axiosApi({
      method: apiList.getExit.method,
      url: url
    })
  },

  uploadServerId(data) {
    return axiosApi({
      ...apiList.uploadServerId,
      data
    })
  },
  getConsultationGroupDetail(consultationId) {
    const url = apiList.getConsultationGroupDetail.url + consultationId
    return axiosApi({
      method: apiList.getConsultationGroupDetail.method,
      url: url
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
  getContent(data) {
    const url = apiList.getContent.url + data.patientId
    return axiosApi({
      method: apiList.getContent.method,
      url: url
    })
  },
  gethealthform(data) {
    const url = apiList.gethealthform.url + data.patientId + '?formEnum=' + data.formEnum
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
  gethealthformSubPut(data) {
    return axiosApi({
      ...apiList.gethealthformSubPut,
      data
    })
  },
  getCheckData(data) {
    const url = apiList.getCheckData.url + data.patientId + '?current=' + data.current + '&size=30&type=' + data.type
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


  getPlanByType(data) {
    const url = apiList.getPlanByType.url + '?planType=' + data.planType
    return axiosApi({
      method: apiList.getPlanByType.method,
      url: url
    })
  },
  getPlanByTypequery(data) {
    return axiosApi({
      ...apiList.getPlanByTypequery,
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
  findSugarByTime(data) {
    const url = apiList.findSugarByTime.url + data.patientId
    return axiosApi({
      method: apiList.findSugarByTime.method,
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
  regGuidegetGuide(data) {
    return axiosApi({
      ...apiList.regGuidegetGuide,
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
    const url = apiList.getPatientImGroup.url + "?patientId=" + data.patientId
    return axiosApi({
      method: apiList.getPatientImGroup.method,
      url: url
    })
  },

  getMessageList(data) {
    let doctorId = localStorage.getItem('caring_doctor_id');
    let url = apiList.getMessageList.url + "?patientImAccount=" + data.patientImAccount + "&createTime=" + data.createTime + '&doctorId=' + doctorId;
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
    return axiosApi({
      ...apiList.sendMessage,
      data
    })
  },
  sendMoreMessage(data) {
    return axiosApi({
      ...apiList.sendMoreMessage,
      data
    })
  },
  // 删除群发消息
  deleteMoreMessage(id) {
    return axiosApi({
      method: apiList.deleteMoreMessage.method,
      url: apiList.deleteMoreMessage.url + id
    })
  },
  // 群发消息列表
  moreMessageList(data) {
    return axiosApi({
      ...apiList.moreMessageList,
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
  wxSignature(data) {
    return axiosApi({
      ...apiList.wxSignature,
      data
    })
  },
  wxAnnoSignature(data) {
    return axiosApi({
      ...apiList.wxAnnoSignature,
      data
    })
  },
  setMsgStatus(data) {
    let id = localStorage.getItem('caring_doctor_id')
    let url = apiList.setMsgStatus.url + id + "?groupIm=" + data.groupIm;
    return axiosApi({
      url: url,
      method: apiList.setMsgStatus.method,
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
  channelContentquery(data) {
    return axiosApi({
      ...apiList.channelContentquery,
      data
    })
  },
  getChannelGroup(data, custom = {}) {
    const url = apiList.channelGroup.url + data
    return axiosApi({
      method: 'GET',
      url: url,
      custom
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
  getDoctorMsgList(data) {
    const url = apiList.getDoctorMsgList.url
    return axiosApi({
      method: apiList.getDoctorMsgList.method,
      url: url,
      data
    })
  },
  getDoctorInfo(data) {
    const url = apiList.getDoctorInfo.url + '/' + data.id
    return axiosApi({
      method: apiList.getDoctorInfo.method,
      url: url
    })
  },
  setDoctorInfo(data) {
    const url = apiList.setDoctorInfo.url
    return axiosApi({
      method: apiList.setDoctorInfo.method,
      url: url,
      data
    })
  },
  getappointConfig(data) {
    const url = apiList.getappointConfig.url + data.doctorId
    return axiosApi({
      method: apiList.getappointConfig.method,
      url: url
    })
  },
  PutappointConfig(data) {
    return axiosApi({
      method: apiList.PutappointConfig.method,
      url: apiList.PutappointConfig.url,
      data
    })
  },
  getappointment(data) {
    const url = apiList.getappointment.url + '?day=' + data.day + '&doctorId=' + data.doctorId
    return axiosApi({
      method: apiList.getappointment.method,
      url: url,
      data
    })
  },
  getappointmentweek(data) {
    return axiosApi({
      method: apiList.getappointmentweek.method,
      url: apiList.getappointmentweek.url,
      data
    })
  },
  doctorGetpatient(data) {
    return axiosApi({
      method: apiList.doctorGetpatient.method,
      url: apiList.doctorGetpatient.url,
      data
    })
  },
  putappointmentStatus(data) {
    return axiosApi({
      method: apiList.putappointmentStatus.method,
      url: apiList.putappointmentStatus.url,
      data
    })
  },
  referralQuery(data) {
    return axiosApi({
      ...apiList.referralQuery,
      data
    })
  },
  doctorQuery(data) {
    return axiosApi({
      ...apiList.doctorQuery,
      data
    })
  },
  launchReferral(data) {
    return axiosApi({
      ...apiList.launchReferral,
      data
    })
  },
  acceptReferralId(data) {
    return axiosApi({
      method: apiList.acceptReferralId.method,
      url: apiList.acceptReferralId.url + data.id + '?acceptDoctorId=' + data.acceptDoctorId
    })
  },
  referralPut(data) {
    return axiosApi({
      ...apiList.referralPut,
      data
    })
  },
  DelreferralPut(data) {
    return axiosApi({
      method: apiList.DelreferralPut.method,
      url: apiList.DelreferralPut.url + '?ids[]=' + data.id
    })
  },
  formHistoryRecord() {
    return axiosApi({
      method: apiList.formHistoryRecord.method,
      url: apiList.formHistoryRecord.url
    })
  },
  formResultBackUp(data) {
    return axiosApi({
      ...apiList.formResultBackUp,
      data
    })
  }
}
