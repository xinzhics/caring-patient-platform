import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/cms/channelContent/page`,
  },
  channelpage: {
    method: 'POST',
    url: `/cms/channel/query`,
  },
  query: {
    method: 'POST',
    url: `/cms/channelContent/query`,
  },
  update: {
    method: 'PUT',
    url: `/cms/channelContent`
  },
  save: {
    method: 'POST',
    url: `/cms/channelContent`
  },
  delete: {
    method: 'DELETE',
    url: `/cms/channelContent`
  },
  export: {
    method: 'POST',
    url: `/cms/channelContent/export`
  },
  preview: {
    method: 'POST',
    url: `/cms/channelContent/preview`
  },
  import: {
    method: 'POST',
    url: `/cms/channelContent/import`
  },
  getTypeid: {
    method: 'POST',
    url: `/cms/channel/query`
  },
  getdetail: {
    method: 'GET',
    url: `/cms/channelContent/`
  },
  updateImg: {
    method: 'POST',
    url: `/file/file/upload`
  },
  copyContent: {
    method: 'POST',
    url: `/cms/channelContent/copyContent`
  },
  systemChannel: {
    method: 'GET',
    url: `/cms/channel/getSystemChannel`
  },
  systemContent: {
    method: 'POST',
    url: `/cms/channelContent/getNoImportMyContent`
  },
  pageChannel: {
    method: 'POST',
    url: `/tenant/cmsConfig/channel/page`
  },
}

export default {
  pageChannel(data) {
    return axiosApi({
      ...apiList.pageChannel,
      data
    })
  },
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
  },
  getTypeid(data, custom = {}) {
    return axiosApi({
      ...apiList.getTypeid,
      data,
      custom
    })
  },
  getdetail(data, custom = {}) {
    const url = apiList.getdetail.url + data.id
    return axiosApi({
      method:apiList.getdetail.method,
      url:url,
      custom
    })
  },
  updateImg(data) {
    return axiosApi({
      ...apiList.updateImg,
      data
    })
  },
  channelpage(data) {
    return axiosApi({
      ...apiList.channelpage,
      data
    })
  },
  copyContent(data) {
    return axiosApi({
      ...apiList.copyContent,
      data
    })
  },
  systemChannel() {
    return axiosApi({
      ...apiList.systemChannel
    })
  },
  systemContent(data) {
    return axiosApi({
      ...apiList.systemContent,
      data
    })
  }

}
