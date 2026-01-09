import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

// 获取cms列表
export function getCmsList (data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioCms/page`,
    data
  })
}


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

/**
 * 新增评论
 */
export function studioContentReplySave(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioContentReply`,
    data
  })
}

/**
 * 收藏医生cms
 */
export function studioCollectSave(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioCollect`,
    data
  })
}

/**
 * 我的收藏
 * @param data
 * @returns {Promise<any>}
 */
export function studioCollectPage(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioCollect/page`,
    data
  })
}

/**
 * 我评论的文章
 * @param data
 * @returns {Promise<any>}
 */
export function myStudioContentReply(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioContentReply/pageContent`,
    data
  })
}

/**
 * 文章的评论列表
 * @param data
 * @returns {Promise<any>}
 */
export function studioContentReplyPage(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/cms/studioContentReply/page`,
    data
  })
}

/**
 * 统计cms收藏和评论数量
 * @param userId
 * @returns {Promise<any>}
 */
export function countPatientCms(userId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}/cms/studioCms/countPatient?userId=${userId}`,
  })
}
