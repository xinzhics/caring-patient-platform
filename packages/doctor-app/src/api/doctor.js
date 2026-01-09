import axiosApi from "./apiAxios.js";
import {Base64} from "js-base64";
import baseUrl from './baseUrl.js'

const apiList = {
  //获取医生的基本信息

  getContent: {
    method: "GET",
    url: baseUrl + `/ucenter/doctor/`
  },
  //修改医生的基本信息
  putContent: {
    method: "PUT",
    url: baseUrl + `/ucenter/doctor`
  },
  //获取患者分类后的列表
  postquery: {
    method: "POST",
    url: baseUrl + `/ucenter/patient/doctorPatientPage`
  },
  //获取患者信息
  getContentPatient: {
    method: "GET",
    url: baseUrl + `/ucenter/patient/`
  },
  //获取健康表单
  gethealthform: {
    method: "GET",
    url: baseUrl + `/nursing/formResult/getFromResultByCategory/`
  },
  gethealthformSubPut: {
    method: "PUT",
    url: baseUrl + `/nursing/formResult`
  },
  gethealthformSub: {
    method: "POST",
    url: baseUrl + `/nursing/formResult`
  },
  //医生登录获取验证码
  sendcheckNumber: {
    method: "POST",
    url: baseUrl + `/ucenter/anno/doctor/send`
  },
  checkPasswordIsInit: {
    method: "GET",
    url: baseUrl + `/ucenter/doctor/anno/checkPasswordIsInit`
  },
  //数据统计
  statisticDashboard: {
    method: "GET",
    url: baseUrl + `/ucenter/statistics/statisticDashboard/`
  },
  //医生未读消息
  getCountDoctorMessage: {
    method: "GET",
    url: baseUrl + `/msgs/im/countDoctorMsgNumber/`
  },

  //注册引导表单一
  regGuidegetGuide: {
    method: "GET",
    url: baseUrl + `/wx/regGuide/getGuide`
  },
  //注册引导表单一
  geth5router: {
    method: "GET",
    url: baseUrl + `/tenant/h5Router/anno/query/`
  },
  //修改患者信息
  setPatientInfo: {
    method: "PUT",
    url: baseUrl + `/ucenter/patient`
  },
  //添加常用语
  addCommonMsg: {
    method: "POST",
    url: baseUrl + `/ucenter/commonMsg`
  },
  //常用语列表
  commonList: {
    method: "POST",
    url: baseUrl + `/ucenter/commonMsg/page`
  },
  //删除常用语
  deleteCommon: {
    method: "DELETE",
    url: baseUrl + `/ucenter/commonMsg/delete/`
  },
  //编辑常用语
  updateCommonMsg: {
    method: "PUT",
    url: baseUrl + `/ucenter/commonMsg`
  },
  //查询文章栏目
  getChannelList: {
    method: "POST",
    url: baseUrl + `/cms/channel/page`
  },
  //查询文章栏目
  getArticleList: {
    method: "POST",
    url: baseUrl + `/cms/channelContent/page`
  },
  //新医生登录
  doctorlogin: {
    method: "POST",
    url: baseUrl + `/ucenter/anno/doctor/login/openId`
  },
  //首页医生微信绑定
  doctorBind: {
    method: "POST",
    url: baseUrl + `/ucenter/anno/doctor/bind_weixin/`
  },
  //删除openId的患者信息切换为医生标签
  changeRoleDeletePatient: {
    method: "GET",
    url: baseUrl + `/ucenter/doctor/anno/changeRoleDeletePatient/`
  },
  //查询是否为医生
  checkPatientExist: {
    method: "GET",
    url: baseUrl + `/ucenter/doctor/anno/checkPatientExist/`
  },
  //获取患者首页内容
  getPatientHomeRouter: {
    method: "GET",
    url: baseUrl + `/tenant/h5Router/anno/query/`
  },
  getPatientNewHomeRouter: {
    method: "GET",
    url: baseUrl + `/tenant/h5Router/getPatientRouter`
  },
  submitDoctorAudit: {
    method: "POST",
    url: baseUrl + `/ucenter/doctorAudit/anno/submitDoctorAudit`
  },
  existByMobile: {
    method: "GET",
    url: baseUrl + "/ucenter/doctor/anno/existByMobile"
  },
  uploadDoctorImage: {
    method: "POST",
    url: baseUrl + "/file/file/anno/doctor/upload"
  },
  // 分页查询省份
  listByIds: {
    method: "POST",
    url: baseUrl + `/authority/province/page`
  },
  // 无需授权分页查询
  annoQueryProvince: {
    method: "POST",
    url: baseUrl + `/authority/province/anno/query`
  },
  // 查询城市
  listByCity: {
    method: "POST",
    url: baseUrl + `/authority/city/page`
  },
  // 无授权查询城市
  annoQueryCity: {
    method: "POST",
    url: baseUrl + `/authority/city/anno/page`
  },
  // 分页查询医院
  hospitalPage: {
    method: "POST",
    url: baseUrl + `/authority/hospital/page`
  },
  // 无授权分页查询医院
  annoHospitalPage: {
    method: "POST",
    url: baseUrl + `/authority/hospital/anno/page`
  },
  //获取分页列表
  consultationGroupPage: {
    method: "POST",
    url: baseUrl + `/ucenter/consultationGroup/consultationGroupPage`
  },
  //接收或者拒绝会诊邀请
  consultatiAcceptOrReject: {
    method: "POST",
    url: baseUrl + `/ucenter/consultationGroup/acceptOrReject/invite`
  },
  //医生创建会诊
  addConsultationGroup: {
    method: "POST",
    url: baseUrl + `/ucenter/consultationGroup/doctor/create`
  },
  //获取医生列表
  getDoctorList: {
    method: "POST",
    url: baseUrl + `/ucenter/doctor/page`
  },
  //添加医生到群组
  addDoctorToGroup: {
    method: "POST",
    url: baseUrl + `/ucenter/consultationGroup/addMemberToGroup`
  },
  //移除讨论组中的医生
  removeDoctor: {
    method: "GET",
    url: baseUrl + `/ucenter/consultationGroup/removeMemberFormImGroup/`
  },
  //设置消息为已读
  doctorReadMessage: {
    method: "POST",
    url: baseUrl + `/msgs/groupIm/doctorReadMessage?`
  },
  //结束会诊
  endConsultation: {
    method: "GET",
    url: baseUrl + `/ucenter/consultationGroup/theEndConsultation/`
  },
  //不授权查询群组
  getAnnoConsultation: {
    method: "GET",
    url: baseUrl + `/ucenter/consultationGroup/anno/getGroup/`
  },
  //查询拒绝信息
  getConsultationMember: {
    method: "GET",
    url: baseUrl + `/ucenter/consultationGroup/groupMember/`
  },
  // 更新会诊的名字或描述
  updateConsultationNameOrDesc: {
    method: "PUT",
    url: baseUrl + `/ucenter/consultationGroup`
  },
  //删除拒绝的邀请
  deleteRejectConsultation: {
    method: "POST",
    url: baseUrl + `/ucenter/consultationGroup/deleteRejectMember`
  },
  //删除拒绝的邀请
  deleteConsultationGroup: {
    method: "DELETE",
    url: baseUrl + `/ucenter/consultationGroup?`
  },
  //删除拒绝的邀请
  getdoctorCustomGroup: {
    method: "POST",
    url: baseUrl + `/ucenter/doctorCustomGroup/list?`
  },
  //ai助手详情
  getAiInfo: {
    method: "GET",
    url: baseUrl + `/tenant/tenant/queryAiInfo`
  },
  //获取医生和GPT聊天列表最新20数据
  getAiChatListPage: {
    method: "GET",
    url: baseUrl + `/msgs/gptDoctorChat/chatListPage`
  },
  //医生是否可以继续发送消息 true 标识可以继续发送
  getDoctorSendMsgStatus: {
    method: "GET",
    url: baseUrl + `/msgs/gptDoctorChat/doctorSendMsgStatus`
  },
  //医生发送消息给GPT
  sendGPTMsg: {
    method: "POST",
    url: baseUrl + `/msgs/gptDoctorChat/sendMsg`
  },
  //sseChat
  sseChat: {
    method: "POST",
    url: baseUrl + `/msgs/gptDoctorChat/sseChat`
  },
  //sseChat
  baiduBotSseChat: {
    method: "POST",
    url: baseUrl + `/msgs/baiduBotDoctorChat/sseChat`
  },
  //医生登陆时的提醒
  getDoctorQueryTenantDay: {
    method: "GET",
    url: baseUrl + `/tenant/tenantOperate/doctorQueryTenantDay`
  },
  // 患者随访日历数据
  patientMenuFollow:{
    method: 'GET',
    url: baseUrl + `/nursing/patientNursingPlan/patientMenuFollow?doctorId=`
  },
  planDetail: {
    method: 'GET',
    url: baseUrl + `/nursing/planDetail/getPlanDetail/`
  },
  // 查询关键词并返回订阅状态
  getDocotrKeyWord: {
    method: 'GET',
    url: baseUrl + `/cms/cmsKeyWord/queryKeyWord?doctorId=`
  },
  // 检查医生是否有关键词
  getDoctorHasKeyWord: {
    method: 'GET',
    url: baseUrl + `/cms/cmsKeyWord/checkDoctorHasKeyWord?doctorId=`
  },
  // 接收医生订阅关键字后的文字消息，并返回一段取消订阅的描述
  getDoctorSubscribeReply: {
    method: 'PUT',
    url: baseUrl + `/msgs/gptDoctorChat/doctorSubscribeReply`
  },
  // 订阅关键词
  subscribeKeyword: {
    method: 'PUT',
    url: baseUrl + `/cms/cmsKeyWord/subscribeKeyword/`
  },
  // 无授权验证手机号是否已经注册医生
  isMobileRegister: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor/anno/existByMobile?mobile=`
  },
  // 医生注册
  registerDoctor: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctor/anno/registerDoctorAndCreateToken`
  },
  // 获取医生和GPT最新的消息
  getGTPlastNewMessage: {
    method: 'GET',
    url: baseUrl + `/msgs/gptDoctorChat/lastNewMessage?doctorId=`
  },
  // 医生推荐IM功能列表
  getDoctorImRecommend: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctor/doctorImRecommend?doctorId=`
  },
  // 增加推荐功能使用的热度
  imRecommendationHeat: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctor/imRecommendationHeat`
  },
  // 查询医生注册是否要审核
  queryDoctorRegisterType: {
    method: 'GET',
    url: baseUrl + `/tenant/tenant/anno/queryDoctorRegisterType`
  },
  phoneAuditExist: {
    method: 'GET',
    url: baseUrl + `/ucenter/doctorAudit/anno/phoneAuditExist`
  },
  // 查询医生的待办消息
  doctorToDoMessage: {
    method: 'POST',
    url: baseUrl + `/msgs/msgPatientSystemMessage/doctorQueryMessage`
  },
  // 医生查看某个待办消息
  doctorSeeMessage: {
    method: 'GET',
    url: baseUrl + `/msgs/msgPatientSystemMessage/doctorSeeMessage`
  },
  // 医生评论某个待办消息
  doctorCommentMessage: {
    method: 'POST',
    url: baseUrl + `/msgs/msgPatientSystemMessage/doctorCommentMessage`
  },
  // 医生删除某个待办消息评论
  doctorDelCommentMessage: {
    method: 'DELETE',
    url: baseUrl + `/msgs/msgPatientSystemMessage/doctorCommentMessage`
  },
  // 统计医生还没看的待办消息
  doctorCountMessage: {
    method: 'GET',
    url: baseUrl + `/msgs/msgPatientSystemMessage/doctorCountMessage`
  },
  // 统计医生未读的患者关注消息
  patientCountMessage: {
    method: 'GET',
    url: baseUrl + `/ucenter/systemMsg/countMessage`
  },
  // 医生删除待办事项
  doctorDelToDoMessage: {
    method: 'DELETE',
    url: baseUrl + `/msgs/msgPatientSystemMessage/doctorMessage`
  },
  // 检测表单结果是否还存在
  checkFormResultExist: {
    method: 'GET',
    url: baseUrl + `/nursing/formResult/checkFormResultExist`
  },
  // 删除患者系统消息
  systemDelMsg: {
    method: 'DELETE',
    url: baseUrl + `/ucenter/systemMsg`
  },
  // 设置消息已读
  setSystemMsgRead: {
    method: 'PUT',
    url: baseUrl + `/ucenter/systemMsg/setRead`
  },
  // 重置密码
  resetPassword: {
    method: 'POST',
    url: baseUrl + `/ucenter/doctor/anno/resetPassword`
  },
  // 修改密码
  updatePassword: {
    method: 'PUT',
    url: baseUrl + `/ucenter/doctor/updatePassword`
  },
  // 验证验证码
  verificationCode: {
    method: 'POST',
    url: baseUrl + `/msgs/verification/anno`
  },
  //医生修改密码获取验证码
  sendPsdCode: {
    method: "POST",
    url: baseUrl + `/msgs/verification/anno/send`
  },
};

export default {
  updatePassword(data) {
    return axiosApi({
      method: apiList.updatePassword.method,
      url: apiList.updatePassword.url,
      data
    })
  },
  sendPsdCode(data) {
    return axiosApi({
      method: apiList.sendPsdCode.method,
      url: apiList.sendPsdCode.url,
      data
    })
  },
  verificationCode(data) {
    return axiosApi({
      method: apiList.verificationCode.method,
      url: apiList.verificationCode.url,
      data
    })
  },
  resetPassword(data) {
    return axiosApi({
      method: apiList.resetPassword.method,
      url: apiList.resetPassword.url,
      data
    })
  },
  setSystemMsgRead(data) {
    return axiosApi({
      method: apiList.setSystemMsgRead.method,
      url: apiList.setSystemMsgRead.url,
      data
    })
  },
  systemDelMsg(ids) {
    let url = apiList.systemDelMsg.url + `?ids[]=` + ids;
    return axiosApi({
      method: apiList.systemDelMsg.method,
      url: url
    })
  },
  checkFormResultExist(data) {
    return axiosApi({
      method: apiList.checkFormResultExist.method,
      url: apiList.checkFormResultExist.url,
      data
    })
  },
  doctorDelToDoMessage(data) {
    return axiosApi({
      method: apiList.doctorDelToDoMessage.method,
      url: apiList.doctorDelToDoMessage.url,
      data
    })
  },
  patientCountMessage(data) {
    return axiosApi({
      method: apiList.patientCountMessage.method,
      url: apiList.patientCountMessage.url,
      data
    })
  },
  doctorCountMessage(data) {
    return axiosApi({
      method: apiList.doctorCountMessage.method,
      url: apiList.doctorCountMessage.url,
      data
    })
  },
  doctorDelCommentMessage(data) {
    return axiosApi({
      method: apiList.doctorDelCommentMessage.method,
      url: apiList.doctorDelCommentMessage.url,
      data
    })
  },
  doctorCommentMessage(data) {
    return axiosApi({
      method: apiList.doctorCommentMessage.method,
      url: apiList.doctorCommentMessage.url,
      data
    })
  },
  doctorSeeMessage(data) {
    return axiosApi({
      method: apiList.doctorSeeMessage.method,
      url: apiList.doctorSeeMessage.url,
      data
    })
  },
  doctorToDoMessage(data) {
    return axiosApi({
      method: apiList.doctorToDoMessage.method,
      url: apiList.doctorToDoMessage.url,
      data
    })
  },
  imRecommendationHeat(data) {
    return axiosApi({
      method: apiList.imRecommendationHeat.method,
      url: apiList.imRecommendationHeat.url,
      data
    })
  },
  getDoctorImRecommend() {
    let url = apiList.getDoctorImRecommend.url + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getDoctorImRecommend.method,
      url: url,
    })
  },
  getGTPLastNewMessage() {
    let url = apiList.getGTPlastNewMessage.url + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getGTPlastNewMessage.method,
      url: url,
    })
  },
  registerDoctor(data) {
    return axiosApi({
      method: apiList.registerDoctor.method,
      url: apiList.registerDoctor.url,
      data
    })
  },
  isMobileRegister(mobile) {
    return axiosApi({
      method: apiList.isMobileRegister.method,
      url: apiList.isMobileRegister.url + mobile
    })
  },
  subscribeKeyword(data) {
    let url = apiList.subscribeKeyword.url + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.subscribeKeyword.method,
      url: url,
      data
    })
  },
  getDoctorSubscribeReply(data) {
    return axiosApi({
      ...apiList.getDoctorSubscribeReply,
      data,
    })
  },
  getDoctorHasKeyWord(userName) {
    const url = apiList.getDoctorHasKeyWord.url + localStorage.getItem('caring_doctor_id') + `&imAccount=${userName}`
    return axiosApi({
      method: apiList.getDoctorHasKeyWord.method,
      url: url
    })
  },
  getDocotrKeyWord() {
    const doctorId = localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getDocotrKeyWord.method,
      url: apiList.getDocotrKeyWord.url + doctorId
    })
  },
  planDetail(planId) {
    if (planId === 'calendar') {
      return
    }
    return axiosApi({
      method: apiList.planDetail.method,
      url: apiList.planDetail.url + planId
    })
  },
  patientMenuFollow(doctorId,patientId) {
    let url = apiList.patientMenuFollow.url+doctorId+'&patientId='+patientId
    return axiosApi({
      method: apiList.patientMenuFollow.method,
      url: url,
    })
  },
  getDoctorQueryTenantDay(doctorId) {
    let url = apiList.getDoctorQueryTenantDay.url + "?userId=" + doctorId
    return axiosApi({
      method: apiList.getDoctorQueryTenantDay.method,
      url: url,
    });
  },
  sendGPTMsg(data) {
    return axiosApi({
      ...apiList.sendGPTMsg,
      data,
    });
  },
  sseChat(data) {
    return axiosApi({
      ...apiList.sseChat,
      data,
    });
  },
  baiduBotSseChat(data) {
    return axiosApi({
      ...apiList.baiduBotSseChat,
      data,
    });
  },
  getDoctorSendMsgStatus() {
    let url = apiList.getDoctorSendMsgStatus.url + "?doctorId=" + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getDoctorSendMsgStatus.method,
      url: url,
    });
  },
  getAiChatListPage(createTime) {
    let url = apiList.getAiChatListPage.url + "?doctorId=" + localStorage.getItem('caring_doctor_id') + '&createTimeString=' + createTime
    return axiosApi({
      method: apiList.getAiChatListPage.method,
      url: url,
    });
  },
  getAiInfo() {
    let url = apiList.getAiInfo.url + "?tenantCode=" + Base64.decode(localStorage.getItem('headerTenant'))
    return axiosApi({
      method: apiList.getAiInfo.method,
      url: url,
    });
  },
  getdoctorCustomGroup() {
    let url = apiList.getdoctorCustomGroup.url + "doctorId=" + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getdoctorCustomGroup.method,
      url: url
    });
  },
  deleteConsultationGroup(id) {
    let url = apiList.deleteConsultationGroup.url + "ids[]=" + id
    return axiosApi({
      method: apiList.deleteConsultationGroup.method,
      url: url
    });
  },
  deleteRejectConsultation(groupId) {
    let url = apiList.deleteRejectConsultation.url + "?doctorImAccount=" + localStorage.getItem('userImAccount') + '&groupId=' + groupId
    return axiosApi({
      method: apiList.deleteRejectConsultation.method,
      url: url
    });
  },
  getConsultationMember(id) {
    let url = apiList.getConsultationMember.url + id + "?imAccount=" + localStorage.getItem('userImAccount')
    return axiosApi({
      method: apiList.getConsultationMember.method,
      url: url
    });
  },
  getAnnoConsultation(id) {
    return axiosApi({
      method: apiList.getAnnoConsultation.method,
      url: apiList.getAnnoConsultation.url + id
    });
  },
  endConsultation() {
    return axiosApi({
      method: apiList.endConsultation.method,
      url: apiList.endConsultation.url + localStorage.getItem('groupId')
    });
  },
  doctorReadMessage(data) {
    let url = apiList.doctorReadMessage.url + 'groupId=' + localStorage.getItem('groupId') + '&&doctorId=' + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.doctorReadMessage.method,
      url: url
    });
  },
  removeDoctor(id) {
    return axiosApi({
      method: apiList.removeDoctor.method,
      url: apiList.removeDoctor.url + id
    });
  },
  addDoctorToGroup(data) {
    return axiosApi({
      ...apiList.addDoctorToGroup,
      data
    });
  },
  getDoctorList(data) {
    return axiosApi({
      ...apiList.getDoctorList,
      data
    });
  },
  addConsultationGroup(data) {
    return axiosApi({
      ...apiList.addConsultationGroup,
      data
    });
  },
  /**
   * 更新会诊的名称或描述
   * @param data
   * @returns {Promise | Promise<unknown>}
   */
  updateConsultationNameOrDesc(data) {
    return axiosApi({
      ...apiList.updateConsultationNameOrDesc,
      data
    });
  },
  consultatiAcceptOrReject(data) {
    return axiosApi({
      ...apiList.consultatiAcceptOrReject,
      data
    });
  },
  consultationGroupPage(data) {
    return axiosApi({
      ...apiList.consultationGroupPage,
      data
    });
  },
  // 查询医院
  hospitalPage(data) {
    return axiosApi({
      ...apiList.hospitalPage,
      data
    });
  },
  // 分页查询医院
  annoHospitalPage(data) {
    return axiosApi({
      ...apiList.annoHospitalPage,
      data
    });
  },
  // 查询城市
  listByCity(data) {
    return axiosApi({
      ...apiList.listByCity,
      data
    });
  },
  // 无授权查询城市
  annoQueryCity(data) {
    return axiosApi({
      ...apiList.annoQueryCity,
      data
    });
  },
  // 查询省份
  listByIds(data) {
    return axiosApi({
      ...apiList.listByIds,
      data
    });
  },
  annoQueryProvince(data) {
    return axiosApi({
      ...apiList.annoQueryProvince,
      data
    });
  },
  getPatientHomeRouter() {
    const url = apiList.getPatientHomeRouter.url + localStorage.getItem("Code");
    return axiosApi({
      method: apiList.getPatientHomeRouter.method,
      url: url
    });
  },
  getPatientNewHomeRouter() {
    const url = apiList.getPatientNewHomeRouter.url + "?currentUserType=DOCTOR";
    return axiosApi({
      method: apiList.getPatientNewHomeRouter.method,
      url: url
    });
  },
  checkPatientExist(id) {
    const url = apiList.checkPatientExist.url + id;
    return axiosApi({
      method: apiList.checkPatientExist.method,
      url: url
    });
  },
  changeRoleDeletePatient(id) {
    const url = apiList.changeRoleDeletePatient.url + id;
    return axiosApi({
      method: apiList.changeRoleDeletePatient.method,
      url: url
    });
  },
  doctorBind(code, doctorId) {
    const url =
      apiList.doctorBind.url +
      doctorId +
      "/" +
      localStorage.getItem("wxAppId") +
      "/" +
      code;
    return axiosApi({
      method: apiList.doctorBind.method,
      url: url
    });
  },
  doctorlogin(data) {
    return axiosApi({
      ...apiList.doctorlogin,
      data
    });
  },
  getArticleList(data) {
    return axiosApi({
      ...apiList.getArticleList,
      data
    });
  },
  getChannelList(data) {
    return axiosApi({
      ...apiList.getChannelList,
      data
    });
  },
  updateCommonMsg(data) {
    return axiosApi({
      ...apiList.updateCommonMsg,
      data
    });
  },
  deleteCommon(id) {
    const url = apiList.deleteCommon.url + id;
    return axiosApi({
      method: apiList.deleteCommon.method,
      url: url
    });
  },
  commonList(data) {
    return axiosApi({
      ...apiList.commonList,
      data
    });
  },
  addCommonMsg(data) {
    return axiosApi({
      ...apiList.addCommonMsg,
      data
    });
  },
  setPatientInfo(data) {
    return axiosApi({
      ...apiList.setPatientInfo,
      data
    });
  },
  getContent(data) {
    const url = apiList.getContent.url + data.id;
    return axiosApi({
      method: apiList.getContent.method,
      url: url
    });
  },
  putContent(data) {
    return axiosApi({
      method: apiList.putContent.method,
      url: apiList.putContent.url,
      data: data
    });
  },
  postquery(data) {
    return axiosApi({
      method: apiList.postquery.method,
      url: apiList.postquery.url,
      data: data
    });
  },
  getContentPatient(data, custom = {}) {
    const url = apiList.getContentPatient.url + data.id;
    return axiosApi({
      url: url,
      method: apiList.getContentPatient.method,
      custom
    });
  },
  gethealthform(data) {
    const url =
      apiList.gethealthform.url + data.patientId + "?formEnum=" + data.formEnum;
    return axiosApi({
      method: apiList.gethealthform.method,
      url: url
    });
  },
  gethealthformSubPut(data) {
    return axiosApi({
      ...apiList.gethealthformSubPut,
      data
    });
  },
  gethealthformSub(data) {
    return axiosApi({
      ...apiList.gethealthformSub,
      data
    });
  },
  sendcheckNumber(data) {
    return axiosApi({
      method: apiList.sendcheckNumber.method,
      url: apiList.sendcheckNumber.url,
      data: data
    });
  },
  checkPasswordIsInit(mobile) {
    return axiosApi({
      method: apiList.checkPasswordIsInit.method,
      url: apiList.checkPasswordIsInit.url + '?mobile=' +  mobile,
    });
  },
  statisticDashboard(data, custom = {}) {
    const url = apiList.statisticDashboard.url + data.id;
    return axiosApi({
      url: url,
      method: apiList.statisticDashboard.method,
      custom
    });
  },
  getCountDoctorMessage(caring_doctor_id) {
    const url =
      apiList.getCountDoctorMessage.url + caring_doctor_id;
    return axiosApi({
      method: apiList.getCountDoctorMessage.method,
      url: url
    });
  },
  regGuidegetGuide(data) {
    return axiosApi({
      ...apiList.regGuidegetGuide,
      data
    });
  },
  geth5router(data) {
    const url =
      apiList.geth5router.url + data.code + "?userType=" + data.userType;
    return axiosApi({
      method: apiList.geth5router.method,
      url: url
    });
  },
  submitDoctorAudit(data) {
    return axiosApi({
      method: apiList.submitDoctorAudit.method,
      url: apiList.submitDoctorAudit.url,
      data
    });
  },
  existByMobile(data) {
    return axiosApi({
      method: apiList.existByMobile.method,
      url: apiList.existByMobile.url,
      data
    });
  },
  uploadDoctorImage(data) {
    return axiosApi({
      ...apiList.uploadDoctorImage,
      data
    });
  },
  queryDoctorRegisterType() {
    return axiosApi({
      ...apiList.queryDoctorRegisterType,
    });
  },
  phoneAuditExist(phone) {
    return axiosApi({
      method: apiList.phoneAuditExist.method,
      url: apiList.phoneAuditExist.url + '?phone=' + phone,
    });
  }
};
