import axiosApi from "./apiAxios.js";
import baseUrl from './baseUrl.js'

const apiList = {
  //【医生端】 查询随访任务的简介
  doctorFindFollowBriefIntroduction: {
    method: 'GET',
    url: baseUrl + `/nursing/followTask/doctorFindFollowBriefIntroduction`
  },
  // 【医生端】 查询全部随访任务的计划安排
  doctorFindFollowPlan: {
    method: 'GET',
    url: baseUrl + `/nursing/followTask/doctorFindFollowPlan`
  },
  // 【医生端】 查询学习计划
  doctorFindLearnPlan: {
    method: 'GET',
    url: baseUrl + `/nursing/followTask/doctorFindLearnPlan`
  },
  //患者标签列表
  getPatientTagQuery: {
    method: 'POST',
    url: baseUrl + `/nursing/tag/query`
  },
  // 【医生端】 查询其他计划
  doctorFindOtherPlan: {
    method: 'GET',
    url: baseUrl + `/nursing/followTask/doctorFindOtherPlan`
  },
  // 根据计划ID查询计划配置的表单
  getPlanForm:{
    method: 'GET',
    url: baseUrl + `/nursing/form/getPlanForm`
  },
  // 【医生端】查询随访统计的详细
  doctorQueryFollowCountDetail:{
    method: 'GET',
    url: baseUrl + `/nursing/followTask/doctorQueryFollowCountDetail`
  },
  // 【医生端】查询学习计划栏目下文章的统计数据
  doctorQueryCountLearnPlanPush:{
    method:'GET',
    url:baseUrl+`/nursing/followTask/doctorQueryCountLearnPlanPush`
  },
  // 【医生端】查询非学习计划的栏目的统计数据
  doctorQueryCountOtherPush:{
    method:'GET',
    url:baseUrl+`/nursing/followTask/doctorQueryCountOtherPush`
  }
}
export default {
  //【医生端】 查询随访任务的简介
  doctorQueryCountOtherPush(followTaskContentId) {
    let url = apiList.doctorQueryCountOtherPush.url + '?followTaskContentId=' + followTaskContentId
    return axiosApi({
      method: apiList.doctorQueryCountOtherPush.method,
      url: url
    })
  },
  //患者标签列表
  getPatientTagQuery(data) {
    return axiosApi({
      method: apiList.getPatientTagQuery.method,
      url: apiList.getPatientTagQuery.url,
    })
  },
  //【医生端】 查询随访任务的简介
  doctorFindFollowBriefIntroduction(tagIds) {
    let url = apiList.doctorFindFollowBriefIntroduction.url
    if (tagIds !== '') {
      url = apiList.doctorFindFollowBriefIntroduction.url + '?tagIds=' + tagIds
    }
    return axiosApi({
      method: apiList.doctorFindFollowBriefIntroduction.method,
      url: url
    })
  },
  // 【医生端】 查询全部随访任务的计划安排
  doctorFindFollowPlan(tagIds, currentPage) {
    let url = apiList.doctorFindFollowPlan.url + '?currentPage=' + currentPage
    if (tagIds !== '') {
      url = apiList.doctorFindFollowPlan.url + '?currentPage=' + currentPage + '&tagIds=' + tagIds
    }
    return axiosApi({
      method: apiList.doctorFindFollowPlan.method,
      url: url
    })
  },
  doctorFindLearnPlan(tagIds) {
    let url = apiList.doctorFindLearnPlan.url
    if (tagIds) {
      url = apiList.doctorFindLearnPlan.url + "?tagIds=" + tagIds
    }
    return axiosApi({
      method: apiList.doctorFindLearnPlan.method,
      url: url
    })
  },
  doctorFindOtherPlan(planId, currentPage) {
    const url = apiList.doctorFindOtherPlan.url + '?currentPage=' + currentPage + '&planId=' + planId
    return axiosApi({
      method: apiList.doctorFindOtherPlan.method,
      url: url
    })
  },
  /**
   * 根据计划ID查询计划配置的表单
   * @param planId
   * @returns {*|Promise|Promise<unknown>}
   */
  getPlanForm(planId){
    const url = apiList.getPlanForm.url + '?planId=' + planId
    return axiosApi({
      method: apiList.getPlanForm.method,
      url: url
    })
  },
  /**
   *【医生端】查询随访统计的详细
   * @returns {*|Promise|Promise<unknown>}
   */
  doctorQueryFollowCountDetail(){
    return axiosApi({
      method: apiList.doctorQueryFollowCountDetail.method,
      url: apiList.doctorQueryFollowCountDetail.url
    })
  },
  doctorQueryCountLearnPlanPush(){
    return axiosApi({
      method: apiList.doctorQueryCountLearnPlanPush.method,
      url: apiList.doctorQueryCountLearnPlanPush.url
    })
  }
}
