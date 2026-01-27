import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/ucenter/nursingStaff/page`,
  },
  query: {
    method: 'POST',
    url: `/ucenter/nursingStaff/query`,
  },
  update: {
    method: 'PUT',
    url: `/ucenter/nursingStaff`
  },
  save: {
    method: 'POST',
    url: `/ucenter/nursingStaff`
  },
  delete: {
    method: 'DELETE',
    url: `/ucenter/nursingStaff`
  },
  export: {
    method: 'POST',
    url: `/ucenter/nursingStaff/export`
  },
  preview: {
    method: 'POST',
    url: `/ucenter/nursingStaff/preview`
  },
  import: {
    method: 'POST',
    url: `/ucenter/nursingStaff/import`
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
