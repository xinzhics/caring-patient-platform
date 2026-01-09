import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 查询监测计划
   */
  getPatientMonitoringDataPlan: {
    method: 'GET',
    url: baseUrl + `api/nursing/plan/PatientMonitoringDataPlan`
  },
  /**
   * 获取血糖的列表
   */
  findSugarByTime: {
    method: 'GET',
    url: baseUrl + `api/nursing/sugarFormResult/findSugarByTime/`
  },

  /**
   * 血糖走势
   */
  loadMyBloodSugarTrendData: {
    method: 'GET',
    url: baseUrl + `api/nursing/sugarFormResult/loadMyBloodSugarTrendData/`
  },
  // 提交血糖的结果
  sugarFormResult: {
    method: 'POST',
    url: baseUrl + `api/nursing/sugarFormResult`
  },
  /**
   * 修改血糖
   */
  putSugarFormResult: {
    method: 'PUT',
    url: baseUrl + `api/nursing/sugarFormResult`
  },

  /**
   * 获取患者的护理计划关注状态
   */
  getMyNursingPlans: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientNursingPlan/getMyNursingPlans/`
  },

  /**
   * 更新患者的护理计划
   */
  updateMyNursingPlans: {
    method: 'POST',
    url: baseUrl + `api/nursing/patientNursingPlan/updateMyNursingPlans/`
  },
  planDetail: {
    method: 'GET',
    url: baseUrl + `api/nursing/planDetail/getPlanDetail/`
  },
  findSugarPage: {
    method: 'POST',
    url: baseUrl + `api/nursing/sugarFormResult/findSugarByTime/monitorQueryDate`
  },
  // 获取患者是否订阅这个计划
  getPatientSubscribePlan: {
    method: 'GET',
    url: baseUrl + `api/nursing/patientNursingPlan/patient/subscribe?patientId=`
  },
  // 患者取消订阅这个计划
  deletePatientSubscribePlan: {
    method: 'DELETE',
    url: baseUrl + `api/nursing/patientNursingPlan/patient/subscribe?patientId=`
  },
  // 患者订阅这个计划
  putPatientSubscribePlan: {
    method: 'PUT',
    url: baseUrl + `api/nursing/patientNursingPlan/subscribePlan?patientId=`
  },
  // 通过消息ID查询数据
  getGlucoseMessageId: {
    method: 'GET',
    url: baseUrl + `api/nursing/sugarFormResult/getByMessageId?messageId=`
  },
  // 获取一个血糖记录
  getSugar: {
    method: 'GET',
    url: baseUrl + `api/nursing/sugarFormResult/`
  },
  // 自定义监测计划折线图
  monitorLineChart: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult/monitorLineChart/`
  }
}

/**
 * 提交血糖的结果
 */
export function monitorLineChart (data, id, patientId) {
  let url = apiList.monitorLineChart.url + patientId + '?businessId=' + id
  return axiosApi({
    method: 'POST',
    url: url,
    data
  })
}
/**
 * 提交血糖的结果
 */
export function sugarFormResult (data) {
  return axiosApi({
    ...apiList.sugarFormResult,
    data: data
  })
}
/**
 * 获取一个血糖记录
 */
export function getSugar (id) {
  let url = apiList.getSugar.url + id
  return axiosApi({
    method: apiList.getSugar.method,
    url: url
  })
}

/**
 * 通过消息ID查询数据
 */
export function getGlucoseMessageId (imMessageId) {
  let url = apiList.getGlucoseMessageId.url + imMessageId
  return axiosApi({
    method: apiList.getGlucoseMessageId.method,
    url: url
  })
}

/**
 * 查询随访计划的名称和信息
 * @param planId
 * @returns {Promise<any>}
 */
export function queryPlanInfo (planId) {
  return axiosApi({
    method: 'GET',
    url: `${baseUrl}api/nursing/plan/${planId}`
  })
}

/**
 * 查询护理计划的推送设置
 * @param planId
 * @returns {Promise | Promise<unknown>}
 */
export function planDetail (planId) {
  return axiosApi({
    method: apiList.planDetail.method,
    url: apiList.planDetail.url + planId
  })
}

/**
 * 查询项目的监测计划
 * @returns {Promise | Promise<unknown>}
 */
export function listPatientMonitoringDataPlan () {
  return axiosApi({
    method: apiList.getPatientMonitoringDataPlan.method,
    url: apiList.getPatientMonitoringDataPlan.url
  })
}

/**
 * 获取血糖的列表
 * @param patientId
 * @returns {Promise | Promise<unknown>}
 */
export function findSugarByTime (patientId) {
  const url = apiList.findSugarByTime.url + patientId
  return axiosApi({
    method: apiList.findSugarByTime.method,
    url: url
  })
}

export function findSugarPage (data) {
  return axiosApi({
    method: apiList.findSugarPage.method,
    url: apiList.findSugarPage.url,
    data
  })
}

/**
 * 血糖的走势图
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function loadMyBloodSugarTrendData (data) {
  const url = apiList.loadMyBloodSugarTrendData.url + data.patientId + '?type=' + data.type + '&week=' + data.week
  return axiosApi({
    method: apiList.loadMyBloodSugarTrendData.method,
    url: url
  })
}

/**
 * 提交血糖结果
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function putSugarFormResult (data) {
  return axiosApi({
    ...apiList.putSugarFormResult,
    data
  })
}

/**
 * 获取患者的护理计划
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function getMyNursingPlans (data) {
  const url = apiList.getMyNursingPlans.url + data.patientId
  return axiosApi({
    method: apiList.getMyNursingPlans.method,
    url: url
  })
}

/**
 * 更新患者的护理计划
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function updateMyNursingPlans (data) {
  const url = apiList.updateMyNursingPlans.url + data.patientId
  return axiosApi({
    method: apiList.updateMyNursingPlans.method,
    url: url,
    data: data.content
  })
}

/**
 * 查询患者对护理计划的订阅状态
 * @param patientId
 * @param planId
 * @param planType
 * @returns {Promise | Promise<unknown>}
 */
export function getPatientSubscribePlan (patientId, planId, planType) {
  let url = apiList.getPatientSubscribePlan.url + patientId
  if (planId) {
    url += `&planId=${planId}`
  }
  if (planType) {
    url += `&planType=${planType}`
  }
  return axiosApi({
    method: apiList.getPatientSubscribePlan.method,
    url: url
  })
}

export function deletePatientSubscribePlan (patientId, planId, planType) {
  let url = apiList.deletePatientSubscribePlan.url + patientId
  if (planId) {
    url += `&planId=${planId}`
  }
  if (planType) {
    url += `&planType=${planType}`
  }
  return axiosApi({
    method: apiList.deletePatientSubscribePlan.method,
    url: url
  })
}

export function putPatientSubscribePlan (patientId, planId, planType) {
  let url = apiList.putPatientSubscribePlan.url + patientId
  if (planId) {
    url += `&planId=${planId}`
  }
  if (planType) {
    url += `&planType=${planType}`
  }
  return axiosApi({
    method: apiList.putPatientSubscribePlan.method,
    url: url
  })
}
