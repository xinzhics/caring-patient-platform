import axiosApi from './AxiosApi.js'

const apiList = {
  captcha: `/ucenter/anno/captcha`,
  login: `/ucenter/anno/token`,
  router: `/ucenter/menu/router`,
  resource: `/ucenter/resource/visible`,
}

export default {
  getCaptcha (randomId) {
    return axiosApi({
      method: 'GET',
      url: apiList.captcha + `?key=${randomId}`,
      responseType: 'arraybuffer',
      meta: {
        "X-isToken": false
      }
    })
  },
  login (data) {
    return axiosApi({
      method: 'POST',
      url: apiList.login,
      data
    })
  },
  getRouter (data) {
    return axiosApi({
      method: 'GET',
      url: apiList.router,
      data: data || {}
    })
  },
  getResource (data) {
    return axiosApi({
      method: 'GET',
      url: apiList.resource,
      data: data || {}
    })
  }
}
