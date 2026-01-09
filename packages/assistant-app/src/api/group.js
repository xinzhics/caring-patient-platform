import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  groupDetail: {
    method: 'GET',
    url: baseUrl + `api/ucenter/group/`
  },
  updateGroup: {
    method: 'PUT',
    url: baseUrl + `api/ucenter/group/`
  },
  createGroup: {
    method: 'POST',
    url: baseUrl + `api/ucenter/group/`
  },
  deleteGroup: {
    method: 'DELETE',
    url: baseUrl + `api/ucenter/group/delete/`
  }
}

/**
 * 我的医生
 * 小组详情
 * @param groupId 小组ID
 * @returns {Promise<>}
 */
export function groupDetail (groupId) {
  return axiosApi({
    method: apiList.groupDetail.method,
    url: apiList.groupDetail.url + groupId
  })
}

/**
 * 我的医生
 * 修改小组信息
 * @param group 小组
 * @returns {Promise<>}
 */
export function updateGroup (group) {
  return axiosApi({
    ...apiList.updateGroup,
    data: group
  })
}

/**
 * 我的医生
 * 创建小组
 * @param group 小组
 */
export function createGroup (group) {
  return axiosApi({
    ...apiList.createGroup,
    data: group
  })
}

/**
 * 删除小组
 * @param groupId
 * @returns {Promise<>}
 */
export function deleteGroup (groupId) {
  return axiosApi({
    method: apiList.deleteGroup.method,
    url: apiList.deleteGroup.url + groupId
  })
}
