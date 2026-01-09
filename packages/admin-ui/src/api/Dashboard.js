import axiosApi from './AxiosApi.js'

const apiList = {
  getVisitList: `/ucenter/statistics/userStatistics`
}

export default {
  getVisitList (data) {
    return axiosApi({
      method: 'GET',
      url: apiList.getVisitList,
      data
    })
  }
}
