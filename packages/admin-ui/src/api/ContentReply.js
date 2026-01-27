import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/cms/contentReply/page`,
  },
  query: {
    method: 'POST',
    url: `/cms/contentReply/query`,
  },
  update: {
    method: 'PUT',
    url: `/cms/contentReply`
  },
  save: {
    method: 'POST',
    url: `/cms/contentReply`
  },
  delete: {
    method: 'DELETE',
    url: `/cms/contentReply`
  },
  export: {
    method: 'POST',
    url: `/cms/contentReply/export`
  },
  preview: {
    method: 'POST',
    url: `/cms/contentReply/preview`
  },
  import: {
    method: 'POST',
    url: `/cms/contentReply/import`
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
