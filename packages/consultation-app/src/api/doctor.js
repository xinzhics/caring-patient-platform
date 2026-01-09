import axiosApi from './apiAxios.js'

const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'https://dev-api.example.com/api' 
  : 'https://api.example.com/api'
//const apiUrl = 'http://192.168.1.133:8760'
const apiList = {
  //获取医生的基本信息

  getContent: {
    method: 'GET',
    url: apiUrl + `/ucenter/doctor/`
  },
  getContentCode: {
    method: 'GET',
    url: apiUrl + ` /ucenter/doctor/anno/`
  },
  //修改医生的基本信息
  putContent: {
    method: 'PUT',
    url: apiUrl + `/ucenter/doctor`
  },
  //获取患者的分类列表
  getstatisticPatientByDoctorId: {
    method: 'GET',
    url: apiUrl + `/ucenter/statistics/statisticPatientByDoctorId/`
  },
  //获取患者分类后的列表
  postquery: {
    method: 'POST',
    url: apiUrl + `/ucenter/patient/query`
  },
  //获取患者信息
  getContentPatient: {
    method: 'GET',
    url: apiUrl + `/ucenter/patient/`
  },
  //获取健康表单
  gethealthform: {
    method: 'GET',
    url: apiUrl + `/nursing/formResult/getFromResultByCategory/`
  },
  //医生登录获取验证码
  sendcheckNumber: {
    method: 'POST',
    url: apiUrl + `/ucenter/anno/doctor/send`
  },
  //医生登录
  sendlogin: {
    method: 'POST',
    url: apiUrl + `/ucenter/anno/doctor/login`
  },
//数据统计
  statisticDashboard: {
    method: 'GET',
    url: apiUrl + `/ucenter/statistics/statisticDashboard/`
  },
  //医生未读消息
  getCountDoctorMessage: {
    method: 'GET',
    url: apiUrl + `/msgs/im/countDoctorMsgNumber/`
  },

  //注册引导表单一
  regGuidegetGuide: {
    method: 'GET',
    url: apiUrl + `/wx/regGuide/getGuide`
  },
  //注册引导表单一
  geth5router: {
    method: 'GET',
    url: apiUrl + `/tenant/h5Router/anno/query/`
  },

}

export default {
  getContent(data) {
    const url = apiList.getContent.url + data.id
    return axiosApi({
      method: apiList.getContent.method,
      url: url
    })
  },
  getContentCode(data) {
    const url = apiList.getContent.url + data.id + '?tenantCode=' + data.code
    return axiosApi({
      method: apiList.getContent.method,
      url: url
    })
  },
  putContent(data) {
    return axiosApi({
      method: apiList.putContent.method,
      url: apiList.putContent.url,
      data: data
    })
  },
  getstatisticPatientByDoctorId(data) {
    const url = apiList.getstatisticPatientByDoctorId.url + data.id
    return axiosApi({
      method: apiList.getstatisticPatientByDoctorId.method,
      url: url,
      data: data
    })
  },
  postquery(data) {
    return axiosApi({
      method: apiList.postquery.method,
      url: apiList.postquery.url,
      data: data
    })
  },
  getContentPatient(data, custom = {}) {
    const url = apiList.getContentPatient.url + data.id
    return axiosApi({
      url: url,
      method: apiList.getContentPatient.method,
      custom
    })
  },
  gethealthform(data) {
    const url = apiList.gethealthform.url + data.patientId + '?formEnum=' + data.formEnum
    return axiosApi({
      method: apiList.gethealthform.method,
      url: url
    })
  },
  sendcheckNumber(data) {
    return axiosApi({
      method: apiList.sendcheckNumber.method,
      url: apiList.sendcheckNumber.url,
      data: data
    })
  },
  sendlogin(data) {
    return axiosApi({
      method: apiList.sendlogin.method,
      url: apiList.sendlogin.url,
      data: data
    })
  },
  statisticDashboard(data, custom = {}) {
    const url = apiList.statisticDashboard.url + data.id
    return axiosApi({
      url: url,
      method: apiList.statisticDashboard.method,
      custom
    })
  },
  getCountDoctorMessage() {
    const url = apiList.getCountDoctorMessage.url + localStorage.getItem("userImAccount")
    return axiosApi({
      method: apiList.getCountDoctorMessage.method,
      url: url
    })
  },
  regGuidegetGuide(data) {
    return axiosApi({
      ...apiList.regGuidegetGuide,
      data
    })
  },
  geth5router(data){
    const url = apiList.geth5router.url + data.code + '?userType=' + data.userType
    return axiosApi({
      method: apiList.geth5router.method,
      url: url
    })
  }
}
