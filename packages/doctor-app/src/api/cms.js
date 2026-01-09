import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

/**
 * 获取cms详情
 * @param cmsId
 * @returns
 */
export function getCmsInfo(cmsId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}/cms/studioCms/${cmsId}`
  })
}

// 获取cms列表
export function getCmsList (data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioCms/page`,
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
    url: `${apiUrl}/cms/studioCms`,
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
    url: `${apiUrl}/cms/studioCms`,
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
    url: `${apiUrl}/cms/studioCms/release/${cmsId}`
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
    url: `${apiUrl}/cms/studioCms/pinToTop/${cmsId}?doctorId=${doctorId}`
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
    url: `${apiUrl}/cms/studioCms/release/${cmsId}`
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
    url: `${apiUrl}/cms/studioCms?ids[]=${ids}`
  })
}

/**
 * 检查科普创作用户是否存在
 * @returns {Promise | Promise<unknown>}
 */
export const checkArticleUserExist = (userAccount) => {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}/ai/articleUser/anno/getMobileUserId?userAccount=${userAccount}`
  })
}


/**
 * 查看cms评论
 * @param ids
 * @returns
 */
export const channelContentWithReply = (data) => {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioContentReply/page`,
    data
  })
}
