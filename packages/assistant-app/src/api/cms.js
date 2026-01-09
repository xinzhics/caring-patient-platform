import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  /**
   * 栏目列表api
   */
  channelList: {
    method: 'POST',
    url: baseUrl + `api/cms/channel/page`
  },

  /**
   * 内容列表api
   */
  contentList: {
    method: 'POST',
    url: baseUrl + `api/cms/channelContent/page`
  },

  contentDetail: {
    method: 'GET',
    url: baseUrl + `api/cms/channelContent/getWithoutTenant/`
  },
  contentDetailNoContent: {
    method: 'GET',
    url: baseUrl + `api/cms/channelContent/getByIdWithoutTenantNoContent/`
  },
  channelContentWithReply: {
    method: 'POST',
    url: baseUrl + `api/cms/studioContentReply/page`
  }
}

/**
 * 查看cms评论
 * @param ids
 * @returns
 */
export const channelContentWithReply = (data) => {
  return axiosApi({
    ...apiList.channelContentWithReply,
    data
  })
}

/**
 * 栏目列表
 * @param data
 */
export function channelList (data) {
  return axiosApi({
    ...apiList.channelList,
    data
  })
}

/**
 * 文章列表
 * @param data
 */
export function contentList (data) {
  return axiosApi({
    ...apiList.contentList,
    data
  })
}

/**
 * 文章详情
 * @param id
 */

export function contentDetail (id) {
  return axiosApi({
    method: apiList.contentDetail.method,
    url: apiList.contentDetail.url + id
  })
}

export function contentDetailNoContent (id) {
  return axiosApi({
    method: apiList.contentDetailNoContent.method,
    url: apiList.contentDetailNoContent.url + id
  })
}

/**
 * 获取cms详情
 * @param cmsId
 * @returns
 */
export function getCmsInfo (cmsId) {
  return axiosApi({
    method: 'GET',
    url: `${baseUrl}api/cms/studioCms/${cmsId}`
  })
}

// 获取cms列表
export function getCmsList (data) {
  return axiosApi({
    method: 'POST',
    url: `${baseUrl}api/cms/studioCms/page`,
    data
  })
}

/**
 * 新增文章
 * @param data 新增
 * @returns
 */
export const saveTextCms = (data) => {
  return axiosApi({
    method: 'POST',
    url: `${baseUrl}api/cms/studioCms`,
    data
  })
}

/**
 * 修改文章
 * @param data 新增
 * @returns
 */
export const putTextCms = (data) => {
  return axiosApi({
    method: 'PUT',
    url: `${baseUrl}api/cms/studioCms`,
    data
  })
}

/**
 * 发布cms
 * @param data 新增
 * @returns
 */
export const sendCms = (cmsId) => {
  return axiosApi({
    method: 'PUT',
    url: `${baseUrl}api/cms/studioCms/release/${cmsId}`
  })
}

/**
 * 置顶cms
 * @param cmsId
 * @param doctorId
 * @returns
 */
export const pinToTopCms = (cmsId, doctorId) => {
  return axiosApi({
    method: 'PUT',
    url: `${baseUrl}api/cms/studioCms/pinToTop/${cmsId}?doctorId=${doctorId}`
  })
}

/**
 * 发布或取消发布
 * @param cmsId
 * @returns
 */
export const releaseCms = (cmsId) => {
  return axiosApi({
    method: 'PUT',
    url: `${baseUrl}api/cms/studioCms/release/${cmsId}`
  })
}

/**
 * 删除cms
 * @param ids
 * @returns
 */
export const delCms = (ids) => {
  return axiosApi({
    method: 'DELETE',
    url: `${baseUrl}api/cms/studioCms?ids[]=${ids}`
  })
}

/**
 * 检查科普创作用户是否存在
 * @returns {Promise | Promise<unknown>}
 */
export const checkArticleUserExist = (userAccount) => {
  return axiosApi({
    method: 'GET',
    url: `${baseUrl}api/ai/articleUser/anno/getMobileUserId?userAccount=${userAccount}`
  })
}
