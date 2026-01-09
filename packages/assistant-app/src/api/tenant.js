import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 项目详情
   */
  tenantDetail: {
    method: 'GET',
    url: baseUrl + `api/tenant/tenant/getTenantDetail/noCode`
  }

}

export function tenantDetail () {
  return axiosApi({
    ...apiList.tenantDetail
  })
}

export function getTenantUi () {
  if (localStorage.getItem('projectCode')) {
    return axiosApi({
      method: 'GET',
      url: baseUrl + `api/tenant/h5Ui/anno/mapQuery/` + localStorage.getItem('projectCode')
    })
  }
}

export function queryOfficialAccountType () {
  if (localStorage.getItem('projectCode')) {
    return axiosApi({
      method: 'GET',
      url: baseUrl + `api/tenant/tenant/queryOfficialAccountType?tenantCode=` + localStorage.getItem('projectCode')
    })
  }
}
