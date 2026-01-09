/**
 * 医助的患者小组
 */
import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 小组
   */
  customGroupingQuery: {
    method: 'POST',
    url: baseUrl + `api/ucenter/customGrouping/query`
  },

  customGroupingQueryAll: {
    method: 'POST',
    url: baseUrl + `api/ucenter/customGrouping/queryAll/`
  },

  customGroupPatientList: {
    method: 'POST',
    url: baseUrl + `api/ucenter/customGrouping/findAllPatientBaseInfo/`
  }

}

/**
 * 医助所有小组列表统计小组内非取关患者数量
 *
 */
export function customGroupingQueryAll (data) {
  return axiosApi({
    method: apiList.customGroupingQueryAll.method,
    url: apiList.customGroupingQueryAll.url + data.userId
  })
}

/**
 * 医助自定义小组
 *
 */
export function customGroupingQuery (data) {
  return axiosApi({
    ...apiList.customGroupingQuery,
    data
  })
}

/**
 * 自定义小组下的患者列表
 * @param nursingId
 */
export function customGroupPatientList (groupId) {
  return axiosApi({
    method: apiList.customGroupPatientList.method,
    url: apiList.customGroupPatientList.url + groupId
  })
}
