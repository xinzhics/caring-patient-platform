import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  /**
   * 患者的药品记录和历史用药
   */
  patientDrugsListAndHistory: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientDrugs/patientDrugsListAndHistory/`
  },
  /**
   * 添加药品
   */
  addPatientDrugs: {
    method: 'POST',
    url: baseUrl + `api/nursing/patientDrugs/savePatientDrugs`
  },
  /**
   * 修改患者的药品
   */
  updatePatientDrugs: {
    method: 'POST',
    url: baseUrl + `api/nursing/patientDrugs/updatePatientDrugs`
  },

  /**
   * 药品详情
   */
  sysDrugsDetail: {
    method: 'GET',
    url: baseUrl + 'api/nursing/sysDrugs/'
  },

  /**
   * 推荐用药
   */
  listRecommendDrugs: {
    method: 'GET',
    url: baseUrl + `api/tenant/drugsConfig/listRecommendDrugs/`
  },

  /**
   * 药品分页查询
   */
  sysDrugsPage: {
    method: 'POST',
    url: baseUrl + `api/nursing/sysDrugs/pageByTenant/`
  },

  /**
   * 获取患者的药品详情
   */
  getPatientDrugs: {
    method: 'POST',
    url: baseUrl + `api/nursing/patientDrugs/getPatientDrugsDetails/`
  },
  /**
   * 用药历史
   */
  getMedicineHistory: {
    method: 'POST',
    url: baseUrl + `api/nursing/patientDrugsHistoryLog/app/historyDate/page`
  },

  /**
   * 用药数据
   */
  getPatientMedicationPlan: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientDrugsTime/queryByDay`
  },

  /**
   * 用药日历  查询当前用药和历史用药
   */
  patientDayDrugsCalendar: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientDayDrugs/calendar/`
  },

  // 删除用药提醒
  deletePatientDrugs: {
    method: 'DELETE',
    url: baseUrl + `api/nursing/patientDrugs`
  },

  clockIn: {
    method: 'POST',
    url: baseUrl + `api/nursing/patientDrugsTime/clockIn`
  },

  queryByDrugsTimestamp: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientDrugsTime/queryByDrugsTimestamp`
  },

  // 用药日历  查询周打卡
  patientDayDrugsTo7Day: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientDayDrugs/7Day/`
  }

}

/**
 * 查询周打卡
 * @param patientId
 * @returns {Promise | Promise<unknown>}
 */
export function patientDayDrugsTo7Day (patientId) {
  let url = apiList.patientDayDrugsTo7Day.url + patientId
  return axiosApi({
    method: apiList.patientDayDrugsTo7Day.method,
    url: url
  })
}
/**
 * 查询这个时间点吃药的药品
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function queryByDrugsTimestamp (data) {
  const url = apiList.queryByDrugsTimestamp.url + '?drugsTimestamp=' + data.drugsTimestamp
  return axiosApi({
    method: apiList.queryByDrugsTimestamp.method,
    url: url
  })
}

/**
 * 用药打卡
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function clockIn (data) {
  const url = apiList.clockIn.url + '?ids=' + data.ids
  return axiosApi({
    method: apiList.clockIn.method,
    url: url
  })
}

/**
 * 删除用药提醒
 * @param id
 * @returns {Promise | Promise<unknown>}
 */
export function deletePatientDrugs (id) {
  const url = apiList.deletePatientDrugs.url + '?ids[]=' + id
  return axiosApi({
    method: apiList.deletePatientDrugs.method,
    url: url
  })
}

/**
 * 患者的药品记录和历史用药
 */
export function patientDrugsListAndHistory (data) {
  const url = apiList.patientDrugsListAndHistory.url + data.patientId
  return axiosApi({
    method: apiList.patientDrugsListAndHistory.method,
    url: url
  })
}

/**
 * 修改患者的药品
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function updatePatientDrugs (data) {
  return axiosApi({
    ...apiList.updatePatientDrugs,
    data
  })
}

/**
 * 获取一个系统药品的详情
 * @param id
 */
export function getSysDrugsDetail (id) {
  const url = apiList.sysDrugsDetail.url + id
  return axiosApi({
    method: apiList.sysDrugsDetail.method,
    url: url
  })
}

/**
 * 推荐用药
 * @param data
 */
export function listRecommendDrugs (data) {
  const url = apiList.listRecommendDrugs.url + data.code
  return axiosApi({
    url: url,
    method: apiList.listRecommendDrugs.method
  })
}

/**
 * 药品库分页
 * @param data
 */
export function sysDrugsPage (data) {
  const url = apiList.sysDrugsPage.url + data.tenant
  return axiosApi({
    method: apiList.sysDrugsPage.method,
    url: url,
    data
  })
}

/**
 * 获取患者的药品
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function getPatientDrugs (data) {
  const url = apiList.getPatientDrugs.url + data.id
  return axiosApi({
    method: apiList.getPatientDrugs.method,
    url: url
  })
}

/**
 * 新增患者药品
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function addPatientDrugs (data) {
  return axiosApi({
    ...apiList.addPatientDrugs,
    data
  })
}

/**
 * 用药历史记录
 * @param current
 * @returns {Promise | Promise<unknown>}
 */
export function getMedicineHistory (current) {
  return axiosApi({
    method: apiList.getMedicineHistory.method,
    url: apiList.getMedicineHistory.url + '?patientId=' + localStorage.getItem('patientId') + '&page=' + current
  })
}

/**
 * 获取患者的用药数据
 */
export function getPatientMedicationPlan (day) {
  return axiosApi({
    method: apiList.getPatientMedicationPlan.method,
    url: apiList.getPatientMedicationPlan.url + '?patientId=' + localStorage.getItem('patientId') + '&drugsDay=' + day
  })
}

/**
 * 用药日历  查询当前用药和历史用药
 * @param data
 */
export function patientDayDrugsCalendar (data) {
  const url = apiList.patientDayDrugsCalendar.url + data.patientId + '?date=' + data.date
  return axiosApi({
    method: apiList.patientDayDrugsCalendar.method,
    url: url
  })
}
