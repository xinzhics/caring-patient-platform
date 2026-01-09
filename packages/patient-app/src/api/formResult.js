import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

/**
 * 查看注射日历
 * @param patientId
 * @param planId
 * @param localDate
 * @returns {Promise<any>}
 */
export function findPatientInjectionCalendar(patientId, planId, localDate) {
  let url = `${apiUrl}/api/nursing/formResult/findPatientInjectionCalendar?patientId=${patientId}&planId=${planId}&localDate=${localDate}`
  return axiosApi({
    method: 'GET',
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
export function findPatientInjectionCalendarFormResult(patientId, planId, dateType, localDate) {
  let url = `${apiUrl}/api/nursing/formResult/findPatientInjectionCalendarFormResult?patientId=${patientId}&planId=${planId}&localDate=${localDate}&type=${dateType}`
  return axiosApi({
    method: 'GET',
    url: url
  })

}
