import axiosApi from './apiAxios.js'
import apiUrl from './baseUrl.js'

/**
 * 查看注射日历
 * @param patientId
 * @param planId
 * @param localDate
 * @returns {Promise<any>}
 */
export function findPatientInjectionCalendar (patientId, planId, localDate) {
  let url = `${apiUrl}api/nursing/formResult/findPatientInjectionCalendar?patientId=${patientId}&planId=${planId}&localDate=${localDate}`
  return axiosApi({
    method: 'GET',
    url: url
  })
}

// 删除数据
export function deleteFormResult (ids) {
  let url = `${apiUrl}api/nursing/formResult?ids[]=${ids}`
  return axiosApi({
    method: 'DELETE',
    url: url
  })
}

/**
 * 查询数据列表
 * @param patientId
 * @param planId
 * @param localDate
 * @param dateType
 * @returns {Promise<any>}
 */
export function findPatientInjectionCalendarFormResult (patientId, planId, dateType, localDate) {
  let url = `${apiUrl}api/nursing/formResult/findPatientInjectionCalendarFormResult?patientId=${patientId}&planId=${planId}&localDate=${localDate}&type=${dateType}`
  return axiosApi({
    method: 'GET',
    url: url
  })
}

/**
 * 患者疾病信息列表
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function healthFormResultList (data) {
  let url = `${apiUrl}api/nursing/formResult/healthFormResultList`
  return axiosApi({
    method: 'POST',
    url: url,
    data
  })
}

/**
 * 更新疾病信息表单状态是 删除。
 * @param id
 * @returns {Promise | Promise<unknown>}
 */
export function updateForDeleteFormResult (id) {
  let url = `${apiUrl}api/nursing/formResult/updateForDeleteFormResult/` + id
  return axiosApi({
    method: 'PUT',
    url: url
  })
}

/**
 * 获取监测计划列表
 * @param patientId
 * @returns {Promise | Promise<unknown>}
 */
export function getPatientMonitoringDataPlan (patientId) {
  let url = `${apiUrl}api/nursing/patientNursingPlan/patientPlanList/formResult?patientId=${patientId}`
  return axiosApi({
    method: 'GET',
    url: url
  })
}

/**
 * 关注监测计划
 * @param patientId
 * @param planId
 * @param planType
 * @returns {Promise | Promise<unknown>}
 */
export function setPatientMonitoringSubscribePlan (patientId, planId, planType) {
  let url = `${apiUrl}api/nursing/patientNursingPlan/subscribePlan?patientId=`
  if (planType) {
    url += patientId + '&planId=' + planId + '&planType=' + planType
  } else {
    url += patientId + '&planId=' + planId
  }
  return axiosApi({
    method: 'PUT',
    url: url
  })
}

/**
 * 取消关注监测计划
 * @param patientId
 * @param planId
 * @param planType
 * @returns {Promise | Promise<unknown>}
 */
export function patientMonitoringCancelPlan (patientId, planId, planType) {
  let url = `${apiUrl}api/nursing/patientNursingPlan/patient/subscribe?patientId=`
  if (planType) {
    url += patientId + '&planId=' + planId + '&planType=' + planType
  } else {
    url += patientId + '&planId=' + planId
  }
  return axiosApi({
    method: 'DELETE',
    url: url
  })
}
