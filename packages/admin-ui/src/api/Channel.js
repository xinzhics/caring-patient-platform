import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/cms/channel/page`,
  },
  query: {
    method: 'POST',
    url: `/cms/channel/query`,
  },
  update: {
    method: 'PUT',
    url: `/cms/channel`
  },
  save: {
    method: 'POST',
    url: `/cms/channel`
  },
  delete: {
    method: 'DELETE',
    url: `/cms/channel`
  },
  export: {
    method: 'POST',
    url: `/cms/channel/export`
  },
  preview: {
    method: 'POST',
    url: `/cms/channel/preview`
  },
  import: {
    method: 'POST',
    url: `/cms/channel/import`
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
