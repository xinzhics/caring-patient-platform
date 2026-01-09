import axiosApi from './apiAxios.js'

const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com' 
  : 'https://api.example.com'
//const apiUrl = 'http://192.168.1.133:8760'
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


  //获取表单
  gethealthform: {
    method: 'GET',
    url: apiUrl + `/api/nursing/formResult/getFromResultByCategory/`
  },
  //新增表单
  gethealthformSub: {
    method: 'POST',
    url: apiUrl + `/api/nursing/formResult`
  },
  //微信来宾认证
  guestLogin: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/anno/guest/login`
  },
  //获取医生信息
  getConsultationGroupMemeberInfo: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/consultationGroup/findConsultationGroupMember/`
  },
  postConsultationGroupMemeberInfo: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/consultationGroup/findConsultationGroupMember/`
  },
  //修改会诊组医生信息
  getJoinGroupFromQrCode: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/consultationGroup/joinGroupFromQrCode`
  },
  //获取会诊管理历史消息
  getGroupMessageList: {
    method: 'POST',
    url: apiUrl + `/api/msgs/groupIm/getGroupImMessage/`
  },
  //发送会诊消息
  sendGroupMessage: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/consultationGroup/sendGroupMessage/`
  },
  //修改表单
  gethealthformSubPut: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/formResult`
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
  //获取检验数据单条空的数据
  getPlanByType: {
    method: 'GET',
    url: apiUrl + `/api/nursing/plan/getPlanByType`
  },
  //获取检验数据单条表单
  getPlanByTypequery: {
    method: 'POST',
    url: apiUrl + `/api/nursing/form/query`
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

  //我的药箱  查询当前用药和历史用药
  patientDrugsListAndHistory: {
    method: 'GET',
    url: apiUrl + `/api/nursing/patientDrugs/patientDrugsListAndHistory/`
  },
//我的药箱  整个药品库
  sysDrugsPage: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sysDrugs/page`
  },
  //我的药箱  单个药品查询详情
  sysDrugsPagedetail: {
    method: 'POST',
    url: apiUrl + `/api/nursing/sysDrugs/query`
  },
  //我的药箱  患者添加用药
  addpatientDrugs: {
    method: 'POST',
    url: apiUrl + `/api/nursing/patientDrugs`
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
    url: apiUrl + `/api/cms/channel/page`
  },
  //cms内容
  channelContentpage: {
    method: 'POST',
    url: apiUrl + `/api/cms/channelContent/page`
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

  //发送消息
  sendMessage: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/doctor/anno/sendChat`
  },

  //给多人发送消息
  sendMoreMessage: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/doctor/anno/sendMoreChat`
  },

  //我的药箱  修改
  FixpatientDrugs: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/patientDrugs`
  },

  //上传图片
  updateImg: {
    method: 'POST',
    url: apiUrl + `/api/file/file/app/upload`
  },
  annoUpdateImg: {
    method: 'POST',
    url: apiUrl + `/api/file/file/anno/doctor/upload`
  },
  //微信获取分享扫一扫接口
  wxSignature: {
    method: 'POST',
    url: apiUrl + `/api/wx/config/jssdk/signature`
  },

  //患者Im在线
  setImLine: {
    method: 'PUT',
    url: apiUrl + `/api/ucenter/patient/anno/imOnline/`
  },

  //修改患者未读消息为已读
  setMsgStatus: {
    method: 'POST',
    path: apiUrl + `/api/msgs/im/chat/refreshMsgStatus/`,
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
  //
  getMsgCount: {
    method: 'GET',
    url: apiUrl + `/api/msgs/im/offline_msg_count/`
  },

  //cms获取轮播图的接口
  channelContentquery: {
    method: 'POST',
    url: apiUrl + `/api/cms/channelContent/query`
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

  //获取医生聊天列表
  getDoctorMsgList: {
    method: 'POST',
    url: apiUrl + `/api/msgs/im/anno/newChatUser`
  },
  //医生信息
  getDoctorInfo: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/doctor`
  },
  //修改医生信息
  setDoctorInfo: {
    method: 'PUT',
    url: apiUrl + `/api/ucenter/doctor`
  },

  //设置医生消息为已读
  setRefreshDoctorMsg: {
    method: 'POST',
    url: apiUrl + `/api/msgs/im/chat/refreshDoctorMsg/`
  },
  //获取医生预约数据
  getappointConfig: {
    method: 'GET',
    url: apiUrl + `/api/nursing/appointConfig/doctorId/`
  },
  //修改医生预约数据
  PutappointConfig: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/appointConfig`
  },
  getappointment: {
    method: 'GET',
    url: apiUrl + `/api/nursing/appointment/statistics/day`
  },
  getappointmentweek: {
    method: 'GET',
    url: apiUrl + `/api/nursing/appointment/statistics/week`
  },

  doctorGetpatient: {
    method: 'POST',
    url: apiUrl + `/api/nursing/appointment/page`
  },
  putappointmentStatus: {
    method: 'PUT',
    url: apiUrl + `/api/nursing/appointment`
  },
  getByTenant: {
    method: 'GET',
    url: apiUrl + `/api/tenant/appConfig/anno/getByTenant`
  },
  getConsultationGroupDetail: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/consultationGroup/getConsultationGroupDetail/`
  },
  getExitConsultation: {
    method: 'GET',
    url: apiUrl + `/api/ucenter/consultationGroup/guestLeaveConsultation/`
  },
  //获取字典
  getDictionary: {
    method: 'POST',
    url: apiUrl + `/api/authority/dictionaryItem/anno/query`
  },
}


export default {
  getDictionary(data) {
    return axiosApi({
      ...apiList.getDictionary,
      data
    })
  },
  getApiUrl() {
    return apiUrl
  },
  getExitConsultation(memberId) {
    const url = apiList.getExitConsultation.url + memberId
    return axiosApi({
      method: apiList.getExitConsultation.method,
      url: url
    })
  },
  getConsultationGroupDetail(consultationId, openId) {
    const url = apiList.getConsultationGroupDetail.url + consultationId + "?openId=" + openId
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
  getJoinGroupFromQrCode(data) {
    return axiosApi({
      ...apiList.getJoinGroupFromQrCode,
      data
    })
  },
  postConsultationGroupMemeberInfo(groupId, openId) {
    const param = { openId : openId}
    const url = apiList.postConsultationGroupMemeberInfo.url + groupId
    return axiosApi({
      method: apiList.postConsultationGroupMemeberInfo.method,
      url: url,
      data: param
    })
  },
  getConsultationGroupMemeberInfo(id) {
    const url = apiList.getConsultationGroupMemeberInfo.url + id
    return axiosApi({
      method: apiList.getConsultationGroupMemeberInfo.method,
      url: url
    })
  },
  guestLogin(data) {
    const url = apiList.guestLogin.url + "?appId=" + data.appId + "&code=" + data.code + "&groupId=" + data.groupId
    return axiosApi({
      method: apiList.guestLogin.method,
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
  getbaseContent(data) {
    return axiosApi({
      ...apiList.getbaseContent,
      data
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
    const url = apiList.getCheckData.url + data.patientId + '?current=1&size=40&type=' + data.type
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

  patientDrugsListAndHistory(data) {
    const url = apiList.patientDrugsListAndHistory.url + data.patientId
    return axiosApi({
      method: apiList.patientDrugsListAndHistory.method,
      url: url
    })
  },
  sysDrugsPage(data) {
    return axiosApi({
      ...apiList.sysDrugsPage,
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
    const url = apiList.getPatientImGroup.url + "?patientId=" + data.patientId
    return axiosApi({
      method: apiList.getPatientImGroup.method,
      url: url
    })
  },

  getMessageList(data) {
    const url = apiList.getMessageList.url + "?receiverImAccount=" + data.receiverImAccount + "&nursingStaffImAccount=" + data.nursingStaffImAccount +
      "&createTime=" + data.createTime;
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
  annoUpdateImg(data) {
    return axiosApi({
      ...apiList.annoUpdateImg,
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
    apiList.setImLine.url = apiList.setImLine.url + data.id;
    return axiosApi({
      ...apiList.setImLine,
      data
    })
  },
  setMsgStatus(data) {
    apiList.setMsgStatus.url = apiList.setMsgStatus.path + data.id;
    return axiosApi({
      ...apiList.setMsgStatus,
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
  setRefreshDoctorMsg(data) {
    const url = apiList.setRefreshDoctorMsg.url + localStorage.getItem("userId") + '?senderImAccount=' + data.senderImAccount
    return axiosApi({
      method: apiList.setRefreshDoctorMsg.method,
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
    const url = apiList.getappointmentweek.url + '?doctorId=' + data.doctorId + '&week=' + data.week
    return axiosApi({
      method: apiList.getappointmentweek.method,
      url: url,
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
  getByTenant(data) {
    return axiosApi({
      ...apiList.getByTenant,
      data
    })
  },
}
