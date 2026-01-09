import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

/**
 * 查询随访计划的名称和信息
 * @param planId
 * @returns {Promise<any>}
 */
export function queryPlanInfo(planId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}/nursing/plan/${planId}`
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
    url: `${apiUrl}/nursing/formResult/checkPatientAbleAddFormResult`,
    data
  })
}

