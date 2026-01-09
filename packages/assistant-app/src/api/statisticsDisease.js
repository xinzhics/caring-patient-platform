import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  diagnosticTypeStatisticsNursing: {
    method: 'GET',
    url: baseUrl + `api/ucenter/statistics/diagnosticTypeStatisticsNursing/`
  },
  tenantStatisticsChartList: {
    method: 'GET',
    url: baseUrl + `api/nursing/statisticsData/tenantStatisticsChartList`
  },
  tenantDataStatistics: {
    method: 'POST',
    url: baseUrl + `api/nursing/statisticsData/tenantDataStatistics/nursing/`
  }
}

/**
 * 统计分析(疾病分类)
 * 获取诊断类型数据
 * @param nursingId 医助ID
 */
export function diagnosticTypeStatisticsNursing (nursingId) {
  return axiosApi({
    method: apiList.diagnosticTypeStatisticsNursing.method,
    url: apiList.diagnosticTypeStatisticsNursing.url + nursingId
  })
}

/**
 * 项目配置统计图的顺序类型宽度
 * @returns {Promise | Promise<unknown>}
 */
export function tenantStatisticsChartList () {
  return axiosApi({
    method: apiList.tenantStatisticsChartList.method,
    url: apiList.tenantStatisticsChartList.url
  })
}

/**
 * app根据统计要求统计数据
 * @param data 图标数据
 * @returns {Promise | Promise<unknown>}
 */
export function tenantDataStatistics (data) {
  const url = apiList.tenantDataStatistics.url + localStorage.getItem('caringNursingId')
  return axiosApi({
    method: apiList.tenantDataStatistics.method,
    url: url,
    data
  })
}
