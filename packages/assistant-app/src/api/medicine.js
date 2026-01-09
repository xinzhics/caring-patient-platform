import axiosApi from './apiAxios.js'
import apiUrl from './baseUrl.js'

const apiList = {
  sysDrugsPagedetail: {
    method: 'POST',
    url: apiUrl + `api/nursing/sysDrugs/query`
  },
  // 获取单个药品详情
  getpatientDrugs: {
    method: 'POST',
    url: apiUrl + `api/nursing/patientDrugs/getPatientDrugsDetails/`
  },
  // 我的药箱  患者添加用药
  addpatientDrugs: {
    method: 'POST',
    url: apiUrl + `api/nursing/patientDrugs/savePatientDrugs`
  },
  // 我的药箱  修改
  FixpatientDrugs: {
    method: 'POST',
    url: apiUrl + `api/nursing/patientDrugs/updatePatientDrugs`
  }
}

export function addpatientDrugs (data) {
  return axiosApi({
    ...apiList.addpatientDrugs,
    data
  })
}

export function FixpatientDrugs (data) {
  return axiosApi({
    ...apiList.FixpatientDrugs,
    data
  })
}

export function getpatientDrugs (data) {
  const url = apiList.getpatientDrugs.url + data.id
  return axiosApi({
    method: apiList.getpatientDrugs.method,
    url: url
  })
}

export function sysDrugsPagedetail (data) {
  return axiosApi({
    ...apiList.sysDrugsPagedetail,
    data
  })
}
