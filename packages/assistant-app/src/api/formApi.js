import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 获取诊断类型
   */
  getDiagnosis: {
    method: 'GET',
    url: baseUrl + `api/nursing/form/getDiagnosis`
  },

  /**
   * 根据类型获取患者的表单结果
   * 基本信息，健康档案
   */
  getHealthOrBaseInfoForm: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/getFromResultByCategory/`
  },
  /**
   * 新增表单
   */
  createFormResult: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult`
  },
  /**
   * 修改表单
   */
  updateFormResult: {
    method: 'PUT',
    url: baseUrl + `api/nursing/formResult`
  },
  /**
   * 获取自定义随访的表单结果。
   * path： patientId
   * path: planId
   * query: current
   * query: size
   */
  getCustomPlanForm: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/getCustomForm/`
  },
  /**
   * 检验数据或健康日志列表
   * path: patientId
   * query: current
   * query: size
   * query: type
   */
  pageFormResultByType: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult/getCheckData/`
  },
  /**
   * 获取表单结果的详情
   */
  getCheckDataFormResult: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult`
  },
  /**
   * 使用护理计划类型或者护理计划ID或者消息ID查询表单
   * 健康日志新增时查询表单字段
   */
  formOrFormResult: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/getPlanForm`
  },
  /**
   * 表单结果的修改记录
   */
  pageFormResultBackUp: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResultBackUp/page`
  },
  /**
   * 表单记录的修改结果详情
   */
  nursingFormResultBackUp: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResultBackUp/`
  },

  /**
   * app血压走势图
   */
  loadMyBloodPressureTrendData: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/loadMyBloodPressureTrendData/`
  },

  /**
   * 患者端血压走势图
   */
  loadPatientBloodPressureTrendData: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/getPatientBloodPress/`
  },
  /**
   * 监测数据的列表
   */
  pageMonitorFormResult: {
    method: 'POST',
    url: baseUrl + `nursing/formResult/monitorFormResult`
  },
  /**
   * 监测数据的表单
   */
  monitorLineChart: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult/monitorLineChart/`
  },

  /**
   * 监测数据折线图
   */
  getMonitorLineChart: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/monitorLineChart/`
  },

  /**
   * 监测数据列表倒序
   */
  getMonitorList: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult/monitorFormResult`
  },

  /**
   * 修改基线值
   */
  updateFieldReference: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult/updatePatientFormFieldReference`
  }

}

export function monitoringIndicatorsFormResult (data) {
  let apiUrlFormResult = baseUrl + `api/nursing/formResult/monitoringIndicators/${data.businessId}`
  return axiosApi({
    method: 'GET',
    url: apiUrlFormResult,
    data
  })
}

/**
 * 获取诊断类型
 */
export function getDiagnosis () {
  return axiosApi({
    ...apiList.getDiagnosis
  })
}

/**
 * 基本信息或者健康档案的表单结果
 * @param data
 * @returns {Promise | Promise<>}
 */
export function getHealthOrBaseInfoForm (patientId, formEnum) {
  const url = apiList.getHealthOrBaseInfoForm.url + patientId + '?formEnum=' + formEnum
  return axiosApi({
    method: apiList.getHealthOrBaseInfoForm.method,
    url: url
  })
}
/**
 * 新增表单
 */
export function createFormResult (data) {
  return axiosApi({
    ...apiList.createFormResult,
    data
  })
}

/**
 * 修改表单
 */
export function updateFormResult (data) {
  return axiosApi({
    ...apiList.updateFormResult,
    data
  })
}

/**
 * 自定义随访表单结果的列表
 * @param patientId
 * @param planId
 * @param current
 */
export function pageCustomForm (patientId, planId, current) {
  const url = apiList.getCustomPlanForm.url + patientId + '/' + planId + `?current=${current}&size=20`
  return axiosApi({
    method: apiList.getCustomPlanForm.method,
    url: url
  })
}

/**
 * 通过表单的结果的类型获取。
 *
 * @param patientId
 * @param current
 * @param type 3 复查提醒， 5 健康日志， 1 血压提醒
 */
export function pageFormResultByType (patientId, current, type) {
  const url = apiList.pageFormResultByType.url + patientId + `?current=${current}&size=10&type=` + type
  return axiosApi({
    method: apiList.pageFormResultByType.method,
    url: url
  })
}

/**
 * 获取表单结果的详情
 */
export function formResultDetail (id) {
  const url = apiList.getCheckDataFormResult.url + '/' + id
  return axiosApi({
    method: apiList.getCheckDataFormResult.method,
    url: url
  })
}

/**
 * 使用护理计划ID或者护理计划类型或者消息ID 查询一个表单或表单结果
 * @param planId
 * @param planType
 * @param messageId
 */
export function formOrFormResult (planId, planType, messageId) {
  const data = {planId: planId, planType: planType, messageId: messageId}
  return axiosApi({
    ...apiList.formOrFormResult,
    data
  })
}

/**
 * 表单结果的修改记录
 * @param data
 * @returns {Promise | Promise<>}
 */
export function pageFormResultBackUp (data) {
  return axiosApi({
    ...apiList.pageFormResultBackUp,
    data
  })
}

/**
 * 表单结果修改记录的结构
 */
export function nursingFormResultBackUp (id) {
  return axiosApi({
    method: apiList.nursingFormResultBackUp.method,
    url: apiList.nursingFormResultBackUp.url + id
  })
}

/**
 * 血压走势
 * @param patientId
 */
export function loadMyBloodPressureTrendData (patientId) {
  return axiosApi({
    method: apiList.loadMyBloodPressureTrendData.method,
    url: apiList.loadMyBloodPressureTrendData.url + patientId
  })
}

export function loadPatientBloodPressureTrendData (patientId) {
  return axiosApi({
    method: apiList.loadPatientBloodPressureTrendData.method,
    url: apiList.loadPatientBloodPressureTrendData.url + patientId
  })
}

/**
 * 监测数据的列表
 * @param data
 */
export function pageMonitorFormResult (data) {
  return axiosApi({
    ...apiList.pageMonitorFormResult,
    data
  })
}

/**
 * 监测数据
 * @param data
 * @param id
 * @returns {Promise | Promise<unknown>}
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
 * 监测数据
 * @param data
 * @param id
 * @returns {Promise | Promise<unknown>}
 */
export function getMonitorLineChart (businessId, monitorDateLineType) {
  let url = apiList.getMonitorLineChart.url + localStorage.getItem('patientId') + '?businessId=' + businessId + '&monitorDateLineType=' + monitorDateLineType
  return axiosApi({
    method: apiList.getMonitorLineChart.method,
    url: url
  })
}

/**
 * 监测数据列表
 * @param data
 */
export function getMonitorList (data) {
  return axiosApi({
    ...apiList.getMonitorList,
    data: data
  })
}

/**
 * 更新基线值
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function updateFieldReference (data) {
  return axiosApi({
    ...apiList.updateFieldReference,
    data
  })
}
