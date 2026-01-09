import axiosApi from "./apiAxios.js";
import baseUrl from './baseUrl.js'

const apiList = {
  // 查询项目的标签列表
  getTagListAndCountPatientNumber: {
    method: 'POST',
    url: baseUrl + `/nursing/tag/getTagListAndCountPatientNumber`
  }
}

export default {
  getTagListAndCountPatientNumber(data) {
    let url = apiList.getTagListAndCountPatientNumber.url
    return axiosApi({
      method: apiList.getTagListAndCountPatientNumber.method,
      url: url,
      data
    })
  }
}

