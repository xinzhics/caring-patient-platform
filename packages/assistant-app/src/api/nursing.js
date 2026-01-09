import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 医助
   */
  getNursingStaff: {
    method: 'GET',
    url: baseUrl + `api/ucenter/nursingStaff/`
  }
}

/**
 * 医助的基本信息
 * @param nursingId
 */
export function nursingBaseInfo (nursingId) {
  if (nursingId) {
    axiosApi({
      method: apiList.getNursingStaff.method,
      url: apiList.getNursingStaff.url + nursingId
    }).then(res => {
      localStorage.setItem('nursingBaseInfo', JSON.stringify(res.data))
    })
  }
}
