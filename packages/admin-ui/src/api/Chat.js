import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/msgs/im/getPatientChat`,
  }
}

export default {
  page(data) {
    return axiosApi({
      ...apiList.page,
      data
    })
  }
}
