import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/ucenter/group/page`,
  },
  query: {
    method: 'POST',
    url: `/ucenter/group/query`,
  },
  update: {
    method: 'PUT',
    url: `/ucenter/group`
  },
  save: {
    method: 'POST',
    url: `/ucenter/group`
  },
  delete: {
    method: 'DELETE',
    url: `/ucenter/group`
  },
  export: {
    method: 'POST',
    url: `/ucenter/group/export`
  },
  preview: {
    method: 'POST',
    url: `/ucenter/group/preview`
  },
  import: {
    method: 'POST',
    url: `/ucenter/group/import`
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
