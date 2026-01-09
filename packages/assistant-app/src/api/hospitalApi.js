import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'
const apiList = {
  // 分页查询省份
  queryProvince: {
    method: 'POST',
    url: baseUrl + `api/authority/province/page`
  },
  // 分页查询城市
  queryCity: {
    method: 'POST',
    url: baseUrl + `api/authority/city/page`
  },
  // 分页查询医院
  queryHospital: {
    method: 'POST',
    url: baseUrl + `api/authority/hospital/page`
  }
}
export function queryProvince (data) {
  return axiosApi({
    ...apiList.queryProvince,
    data
  })
}

export function queryCity (data) {
  return axiosApi({
    ...apiList.queryCity,
    data
  })
}

export function queryHospital (data) {
  return axiosApi({
    ...apiList.queryHospital,
    data
  })
}
