import axiosApi from './apiAxios.js'
import apiUrl from './baseUrl.js'

// 获取数字人视频列表
export function userVoiceTaskPage (data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}api/ai/articleUserVoiceTask/page`,
    data
  })
}

// 获取文章列表
export function articleTaskPage (data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}api/ai/articleTask/draftPage`,
    data
  })
}

// 获取播客列表
export function podcastPage (data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}api/ai/podcast/creation/page`,
    data
  })
}

// 查询AI文章的所有
export function getArticleContentAll (taskId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}api/ai/articleTask/getArticleContentAll/${taskId}`
  })
}

// 查询播客
export function getPodcast (taskId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}api/ai/podcast/getPodcast?podcastId=${taskId}`
  })
}

// 查询视频
export function getVoiceTask (taskId) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}api/ai/articleUserVoiceTask/${taskId}`
  })
}
