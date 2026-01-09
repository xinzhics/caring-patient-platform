import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 患者详情
   */
  patientDetail: {
    method: 'GET',
    url: baseUrl + `api/ucenter/patient/`
  },

  /**
   * 患者的ui
   */
  patientRoute: {
    method: 'POST',
    url: baseUrl + `api/tenant/h5Router/query/`
  },

  /**
   * 修改患者信息
   */
  updatePatient: {
    method: 'PUT',
    url: baseUrl + `api/ucenter/patient`
  },

  getPatientNewHomeRouter: {
    method: 'GET',
    url: baseUrl + `api/tenant/h5Router/getPatientRouter`
  },

  // 患者随访日历数据
  patientMenuFollow: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientNursingPlan/patientMenuFollow?doctorId=`
  },

  // 表单提交后。查询表单的成绩结果
  getFormResultScore: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResultScore/queryFormResultScore?formResultId=`
  },

  // 发送消息
  sendMessage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/nursingStaff/sendChat`
  },

  // 增加推荐功能使用的热度
  imRecommendationHeat: {
    method: 'POST',
    url: baseUrl + `api/ucenter/doctor/imRecommendationHeat`
  },
  // 通过消息ID查询数据
  getDoctorSeePatientGroup: {
    method: 'PUT',
    url: baseUrl + `api/ucenter/patient/doctorSeePatientGroup?doctorId=`
  }
}

export function getDoctorSeePatientGroup (doctorId, patientId) {
  let url = apiList.getDoctorSeePatientGroup.url + doctorId + '&patientId=' + patientId
  return axiosApi({
    method: apiList.getDoctorSeePatientGroup.method,
    url: url
  })
}

/**
 * 获取患者的详情
 * @param id
 */
export function getFormResultScore (id) {
  const url = apiList.getFormResultScore.url + id
  return axiosApi({
    method: apiList.getFormResultScore.method,
    url: url
  })
}

/**
 * 获取患者的详情
 * @param id
 */
export function patientDetail (id) {
  const url = apiList.patientDetail.url + id
  return axiosApi({
    method: apiList.patientDetail.method,
    url: url
  })
}

export function getPatientNewHomeRouter () {
  const url = apiList.getPatientNewHomeRouter.url + '?currentUserType=NURSING_STAFF'
  return axiosApi({
    method: apiList.getPatientNewHomeRouter.method,
    url: url
  })
}

export function patientMenuFollow (doctorId, patientId) {
  return axiosApi({
    method: apiList.patientMenuFollow.method,
    url: apiList.patientMenuFollow.url + doctorId + '&patientId=' + patientId
  })
}

/**
 * 获取患者的路由
 */
export function patientRoute () {
  const data = {}
  const url = apiList.patientRoute.url + localStorage.getItem('projectCode')
  return axiosApi({
    method: apiList.patientRoute.method,
    url: url,
    data
  })
}

/**
 * 修改患者的信息
 * @param data
 */
export function updatePatient (data) {
  return axiosApi({
    ...apiList.updatePatient,
    data
  })
}

export function sendMessage (data) {
  return axiosApi({
    ...apiList.sendMessage,
    data
  })
}

export function imRecommendationHeat (data) {
  return axiosApi({
    method: apiList.imRecommendationHeat.method,
    url: apiList.imRecommendationHeat.url,
    data
  })
}
