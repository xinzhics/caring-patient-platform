import axiosApi from './AxiosApi.js'

const apiList = {
  page: {
    method: 'POST',
    url: `/ucenter/patient/page`,
  },
  //获取用户信息
  getContent: {
    method: 'GET',
    url: `/ucenter/patient/`
  },
  //获取健康表单
  gethealthform: {
    method: 'GET',
    url: `/nursing/formResult/getFromResultByCategory/`
  },
  query: {
    method: 'POST',
    url: `/ucenter/patient/query`,
  },
  update: {
    method: 'PUT',
    url: `/ucenter/patient`
  },
  save: {
    method: 'POST',
    url: `/ucenter/patient`
  },
  delete: {
    method: 'DELETE',
    url: `/ucenter/patient`
  },
  export: {
    method: 'POST',
    url: `/ucenter/patient/export`
  },
  preview: {
    method: 'POST',
    url: `/ucenter/patient/preview`
  },
  import: {
    method: 'POST',
    url: `/ucenter/patient/import`
  },
  getCheckData: {
    method: 'POST',
    url: `/nursing/formResult/getCheckData/`
  },
  //我的药箱  查询当前用药和历史用药
  patientDrugsListAndHistory: {
    method: 'GET',
    url: `/nursing/patientDrugs/patientDrugsListAndHistory/`
  },
  //智能提醒
  getMyNursingPlans: {
    method: 'GET',
    url: `/nursing/patientNursingPlan/getMyNursingPlans/`
  },
   //获取检验数据单条空的数据
  getPlanByType: {
    method: 'GET',
    url: `/nursing/plan/getPlanByType`
  },
  //获取血压数据
  getformResultquery: {
    method: 'POST',
    url:`/nursing/formResult/query`
  },
  //获取血糖的列表
  findSugarByTime: {
    method: 'GET',
    url: `/nursing/sugarFormResult/findSugarByTime/`
  },
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
  },
  getContent(data, custom = {}) {
    const url = apiList.getContent.url + data.id
    return axiosApi({
      url:url,
      method:apiList.getContent.method,
      custom
    })
  },
  gethealthform(data) {
    const url = apiList.gethealthform.url + data.patientId + '?formEnum=' + data.formEnum
    return axiosApi({
      method: apiList.gethealthform.method,
      url: url
    })
  },
  getCheckData(data) {
    const url = apiList.getCheckData.url + data.patientId + '?current=1&size=40&type=' + data.type
    return axiosApi({
      method: apiList.getCheckData.method,
      url: url
    })
  },
  patientDrugsListAndHistory(data) {
    const url = apiList.patientDrugsListAndHistory.url + data.patientId
    return axiosApi({
      method: apiList.patientDrugsListAndHistory.method,
      url: url
    })
  },
  getMyNursingPlans(data) {
    const url = apiList.getMyNursingPlans.url + data.patientId
    return axiosApi({
      method: apiList.getMyNursingPlans.method,
      url: url
    })
  },
  getPlanByType(data) {
    const url = apiList.getPlanByType.url + '?planType=' + data.planType
    return axiosApi({
      method: apiList.getPlanByType.method,
      url: url
    })
  },
  getformResultquery(data) {
    return axiosApi({
      ...apiList.getformResultquery,
      data
    })
  },
  findSugarByTime(data) {
    const url = apiList.findSugarByTime.url + data.patientId
    return axiosApi({
      method: apiList.findSugarByTime.method,
      url: url
    })
  },
}
