import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/ucenter/doctor/page`,
  },
  query: {
    method: 'POST',
    url: `/ucenter/doctor/query`,
  },
  update: {
    method: 'PUT',
    url: `/ucenter/doctor`
  },
  save: {
    method: 'POST',
    url: `/ucenter/doctor`
  },
  delete: {
    method: 'DELETE',
    url: `/ucenter/doctor`
  },
  export: {
    method: 'POST',
    url: `/ucenter/doctor/export`
  },
  preview: {
    method: 'POST',
    url: `/ucenter/doctor/preview`
  },
  import: {
    method: 'POST',
    url: `/ucenter/doctor/import`
  }
}

export default {
  page (data, custom = {}) {
    return axiosApi({
      ...apiList.page,
      data,
      custom
    })
  },
  query (data, custom = {}) {
    return axiosApi({
      ...apiList.query,
      data,
      custom
    })
  },
  save (data, custom = {}) {
    return axiosApi({
      ...apiList.save,
      data,
      custom
    })
  },
  update (data, custom = {}) {
    return axiosApi({
      ...apiList.update,
      data,
      custom
    })
  },
  delete (data, custom = {}) {
    return axiosApi({
      ...apiList.delete,
      data,
      custom
    })
  },
  export (data, custom = {}) {
    return axiosApi({
      ...apiList.export,
      responseType: "blob",
      data,
      custom
    })
  },
  preview (data, custom = {}) {
    return axiosApi({
      ...apiList.preview,
      data,
      custom
    })
  },
  import (data, custom = {}) {
    return axiosApi({
      ...apiList.import,
      data,
      custom
    })
  }
}
