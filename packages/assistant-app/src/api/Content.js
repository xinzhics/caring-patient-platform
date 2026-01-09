import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 获取消息id
   */
  getHealthRecordByMessageId: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/getHealthRecordByMessageId?messageId=`
  },
  // 查询系统配置的表单
  patientGetForm: {
    method: 'GET',
    url: baseUrl + `api/nursing/form/patientGetForm`
  },
  byCategory: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/byCategory/`
  },
  // 获取检验数据结果
  getCheckDataformResult: {
    method: 'GET',
    url: baseUrl + `api/nursing/formResult/`
  },
  // 首次分阶段保存基本信息或疾病信息
  saveFormResultStage: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult/saveFormResultStage`
  },
  gethealthformSubPut: {
    method: 'PUT',
    url: baseUrl + `api/nursing/formResult`
  },
  gethealthformSub: {
    method: 'POST',
    url: baseUrl + `api/nursing/formResult`
  }
}

export function gethealthformSub (data) {
  return axiosApi({
    ...apiList.gethealthformSub,
    data
  })
}
export function gethealthformSubPut (data) {
  return axiosApi({
    ...apiList.gethealthformSubPut,
    data
  })
}

export function saveFormResultStage (data) {
  return axiosApi({
    ...apiList.saveFormResultStage,
    data
  })
}

/**
 * 获取消息id
 * @param messageId
 */
export function queryHealthRecordByMessageId (messageId) {
  let url = apiList.getHealthRecordByMessageId.url + messageId
  return axiosApi({
    method: apiList.getHealthRecordByMessageId.method,
    url: url
  })
}

export function patientGetForm () {
  return axiosApi({
    method: apiList.patientGetForm.method,
    url: apiList.patientGetForm.url
  })
}

export function byCategory (formEnum) {
  let url = apiList.byCategory.url + localStorage.getItem('patientId') + '?formEnum=' + formEnum
  return axiosApi({
    method: apiList.byCategory.method,
    url: url
  })
}

export function getCheckDataformResult (data) {
  let url = apiList.getCheckDataformResult.url + data.id
  return axiosApi({
    method: apiList.getCheckDataformResult.method,
    url: url
  })
}
