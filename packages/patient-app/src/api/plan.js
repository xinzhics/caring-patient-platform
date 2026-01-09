import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

/**
 * 获取患者是否订阅这个计划
 * @param filename
 */
export function getPatientSubscribePlan(patientId, planId, planType) {
  let url = `${apiUrl}/api/nursing/patientNursingPlan/patient/subscribe?patientId=${patientId}`
  if (planId) {
    url += `&planId=${planId}`
  }
  if (planType) {
    url += `&planType=${planType}`
  }
  return axiosApi({
    method: 'GET',
    url: url
  })
}

/**
 * 查询随访计划的名称和信息
 * @param planId
 * @returns {Promise<any>}
 */
export function queryPlanInfo(planId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}/api/nursing/plan/${planId}`
  })
}

/**
 * 患者取消订阅这个计划
 * @param filename
 */
export function deletePatientSubscribePlan(patientId, planId, planType) {
  let url = `${apiUrl}/api/nursing/patientNursingPlan/patient/subscribe?patientId=${patientId}`
  if (planId) {
    url += `&planId=${planId}`
  }
  if (planType) {
    url += `&planType=${planType}`
  }
  return axiosApi({
    method: 'DELETE',
    url: url
  })
}


/**
 * 患者订阅这个计划
 * @param filename
 */
export function putPatientSubscribePlan(patientId, planId, planType) {
  let url = `${apiUrl}/api/nursing/patientNursingPlan/subscribePlan?patientId=${patientId}`
  if (planId) {
    url += `&planId=${planId}`
  }
  if (planType) {
    url += `&planType=${planType}`
  }
  return axiosApi({
    method: 'PUT',
    url: url
  })
}

/**
 * 判断日期是否可以提交
 * @param planId
 * @param patientId
 * @param localDate
 * @returns {Promise<any>}
 */
export function checkPatientAbleAddFormResult(planId, patientId, localDate) {
  const data = {
    planId: planId,
    patientId: patientId,
    localDate: localDate
  }
  return axiosApi({
    method: 'PUT',
    url: `${apiUrl}/api/nursing/formResult/checkPatientAbleAddFormResult`,
    data
  })
}
