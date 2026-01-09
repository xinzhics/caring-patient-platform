import axiosApi from "./apiAxios.js";
import baseUrl from './baseUrl.js'

const apiList = {
  //常用语模板分类
  getComMsgTemplateType: {
    method: 'POST',
    url: baseUrl + `/ucenter/commonMsgTemplateType/query`
  },

  //常用语模版列表
  getComMsgTemplate: {
    method: 'GET',
    url: baseUrl + `/ucenter/commonMsgTemplateContent/`
  },

  //常用语模版列表
  getComMsgTemplateList: {
    method: 'POST',
    url: baseUrl + `/ucenter/commonMsgTemplateContent/page`
  },

  // 医生自己的常用语列表
  doctorCommonList: {
    method: 'POST',
    url: baseUrl + `/ucenter/commonMsg/pageCommonMsg`
  },

  // 获取医生的常用语
  getDoctorCommon: {
    method: 'GET',
    url: baseUrl + `/ucenter/commonMsg/`
  },

  // 新增和保存常用语和分类
  doctorSaveOrUpdateCommon: {
    method: 'POST',
    url: baseUrl + `/ucenter/commonMsg/saveOrUpdateCommonMsgAndType`
  },

  // 系统分类和医生自己的分类
  commonTypeAll: {
    method: 'GET',
    url: baseUrl + `/ucenter/commonMsg/queryAllType/`
  },

  // 查询自己的常用语分类
  queryMyType: {
    method: 'GET',
    url: baseUrl + `/ucenter/commonMsg/queryMyType/`
  },

  // 导入模板库的常用语到自己的库
  importTemplateCommon: {
    method: 'POST',
    url: baseUrl + `/ucenter/commonMsg/importCommonMsgTemplate/`
  }

}

export default {
  getComMsgTemplateType(data) {
    return axiosApi({
      method: apiList.getComMsgTemplateType.method,
      url: apiList.getComMsgTemplateType.url,
      data
    })
  },

  getComMsgTemplate(id) {
    return axiosApi({
      method: apiList.getComMsgTemplate.method,
      url: apiList.getComMsgTemplate.url + id,
    })
  },

  getComMsgTemplateList(data) {
    return axiosApi({
      method: apiList.getComMsgTemplateList.method,
      url: apiList.getComMsgTemplateList.url,
      data
    })
  },

  // 新增和保存常用语和分类
  saveOrUpdateCommonMsgAndType(data) {
    return axiosApi({
      method: apiList.doctorSaveOrUpdateCommon.method,
      url: apiList.doctorSaveOrUpdateCommon.url,
      data
    })
  },
  // 查询医生的常用语详情
  getDoctorCommon(id) {
    return axiosApi({
      method: apiList.getDoctorCommon.method,
      url: apiList.getDoctorCommon.url + id,
    })
  },

  // 医生常用语分页查询
  doctorCommonList(data) {
    return axiosApi({
      method: apiList.doctorCommonList.method,
      url: apiList.doctorCommonList.url,
      data
    })
  },

  //查询所有的分类，系统的和自己的
  commonTypeAll(userId, userType) {
    return axiosApi({
      method: apiList.commonTypeAll.method,
      url: apiList.commonTypeAll.url + userId + '/' + userType,
    })
  },

  // 医生自己的常用语分类
  commonTypeMy(userId, userType) {
    return axiosApi({
      method: apiList.queryMyType.method,
      url: apiList.queryMyType.url + userId + '/' + userType,
    })
  },

  // 将选择的常用语模版导入到自己的常用语
  importTemplateCommon(userId, userType, data) {
    return axiosApi({
      method: apiList.importTemplateCommon.method,
      url: apiList.importTemplateCommon.url + userId + '/' + userType,
      data
    })
  }


}
