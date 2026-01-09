import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  getStatistics: {
    method: 'GET',
    url: baseUrl + `api/ucenter/statistics/`
  }
}

/**
 * 统计分析(基础页面)
 * @param nursingId 医助ID
 */
export function getStatistics (nursingId) {
  return axiosApi({
    method: apiList.getStatistics.method,
    url: apiList.getStatistics.url + nursingId
  })
}
