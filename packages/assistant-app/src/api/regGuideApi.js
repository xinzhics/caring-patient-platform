import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  getRegGuide: {
    method: 'GET',
    url: baseUrl + `api/wx/regGuide/getGuide`
  }
}

/**
 * 获取配置
 */
export function getRegGuide () {
  return axiosApi({
    ...apiList.getRegGuide
  })
}
