import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {

  /**
   * 删除常用语
   */
  deleteCommonWord: {
    method: 'DELETE',
    url: baseUrl + `api/ucenter/commonMsg/delete/`
  },
  /**
   * 清除所有常用语
   */
  clearCommonWord: {
    method: 'DELETE',
    url: baseUrl + `api/ucenter/commonMsg/delete/all/`
  },
  /**
   * 常用语的分页查询
   */
  pageCommonWord: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsg/page`
  },
  // 医生自己的常用语列表
  newPageCommonList: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsg/pageCommonMsg`
  },
  /**
   * 创建一个常用语
   */
  createCommonWord: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsg`
  },
  /**
   * 更新常用语
   */
  updateCommonWord: {
    method: 'PUT',
    url: baseUrl + `api/ucenter/commonMsg`
  },
  /**
   * 获取一个常用语详情
   */
  getCommonWord: {
    method: 'GET',
    url: baseUrl + `api/ucenter/commonMsg/`
  },

  // 系统分类和医生自己的分类
  commonTypeAll: {
    method: 'GET',
    url: baseUrl + `api/ucenter/commonMsg/queryAllType/`
  },

  // 常用语模板分类
  getComMsgTemplateType: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsgTemplateType/query`
  },

  // 常用语模版列表
  getComMsgTemplate: {
    method: 'GET',
    url: baseUrl + `api/ucenter/commonMsgTemplateContent/`
  },

  // 常用语模版列表
  getComMsgTemplateList: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsgTemplateContent/page`
  },

  // 新增和保存常用语和分类
  doctorSaveOrUpdateCommon: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsg/saveOrUpdateCommonMsgAndType`
  },

  // 查询自己的常用语分类
  queryMyType: {
    method: 'GET',
    url: baseUrl + `api/ucenter/commonMsg/queryMyType/`
  },

  // 导入模板库的常用语到自己的库
  importTemplateCommon: {
    method: 'POST',
    url: baseUrl + `api/ucenter/commonMsg/importCommonMsgTemplate/`
  }

}

// 将选择的常用语模版导入到自己的常用语
export function importTemplateCommon (userId, userType, data) {
  return axiosApi({
    method: apiList.importTemplateCommon.method,
    url: apiList.importTemplateCommon.url + userId + '/' + userType,
    data
  })
}

/**
 * 新的常用语列表
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function newPageCommonList (data) {
  return axiosApi({
    method: apiList.newPageCommonList.method,
    url: apiList.newPageCommonList.url,
    data
  })
}

export function getComMsgTemplateType (data) {
  return axiosApi({
    method: apiList.getComMsgTemplateType.method,
    url: apiList.getComMsgTemplateType.url,
    data
  })
}

export function getComMsgTemplate (id) {
  return axiosApi({
    method: apiList.getComMsgTemplate.method,
    url: apiList.getComMsgTemplate.url + id
  })
}

export function getComMsgTemplateList (data) {
  return axiosApi({
    method: apiList.getComMsgTemplateList.method,
    url: apiList.getComMsgTemplateList.url,
    data
  })
}

// 查询所有的分类，系统的和自己的
export function commonTypeAll (userId, userType) {
  return axiosApi({
    method: apiList.commonTypeAll.method,
    url: apiList.commonTypeAll.url + userId + '/' + userType
  })
}

// 新增和保存常用语和分类
export function saveOrUpdateCommonMsgAndType (data) {
  return axiosApi({
    method: apiList.doctorSaveOrUpdateCommon.method,
    url: apiList.doctorSaveOrUpdateCommon.url,
    data
  })
}

/**
 * 删除一个常用语
 * @param commonWordId
 */
export function deleteCommonWord (commonWordId) {
  return axiosApi({
    method: apiList.deleteCommonWord.method,
    url: apiList.deleteCommonWord.url + commonWordId
  })
}

export function commonTypeMy (userId, userType) {
  return axiosApi({
    method: apiList.queryMyType.method,
    url: apiList.queryMyType.url + userId + '/' + userType
  })
}

/**
 * 清除常用语
 * @param nursingId 医助的ID
 */
export function clearCommonWord (nursingId) {
  return axiosApi({
    method: apiList.clearCommonWord.method,
    url: apiList.clearCommonWord.url + nursingId
  })
}

/**
 * 常用语的分页查询
 * @param data
 */
export function pageCommonWord (data) {
  return axiosApi({
    ...apiList.pageCommonWord,
    data
  })
}

/**
 * 创建一条常用语
 * @param data
 */
export function createCommonWord (data) {
  return axiosApi({
    ...apiList.createCommonWord,
    data
  })
}

/**
 * 修改常用语
 * @param data
 */
export function updateCommonWord (data) {
  return axiosApi({
    ...apiList.updateCommonWord,
    data
  })
}

/**
 * 获取常用语详细
 * @param id
 */
export function getCommonWord (id) {
  return axiosApi({
    method: apiList.getCommonWord.method,
    url: apiList.getCommonWord.url + id
  })
}
