import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 医助
   */
  referralPage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/referral/page`
  },
  /**
   * 删除
   */
  deleteReferral: {
    method: 'DELETE',
    url: baseUrl + `api/ucenter/referral`
  }
}

/**
 * 医助的基本信息
 * @param nursingId
 */
export function referralPage (data) {
  return axiosApi({
    ...apiList.referralPage,
    data
  })
}

/**
 * 删除转诊
 * @param id
 */
export function deleteReferral (id) {
  return axiosApi({
    method: apiList.deleteReferral.method,
    url: apiList.deleteReferral.url + '?ids[]=' + id
  })
}
