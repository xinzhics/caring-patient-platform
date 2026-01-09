import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  // 【app】 查询随访任务的简介
  appFindFollowBriefIntroduction: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/appFindFollowBriefIntroduction`
  },
  // 【app】 查询全部随访任务的计划安排
  appFindFollowPlanAll: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/findFollowPlanAll`
  },
  // 【app】 查询学习计划
  appFindLearnPlan: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/findLearnPlan`
  },
  // 患者标签列表
  getPatientTagQuery: {
    method: 'POST',
    url: baseUrl + `api/nursing/tag/query`
  },
  // 【app】 查询其他计划
  appFindOtherPlan: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/findOtherPlan`
  },
  // 根据计划ID查询计划配置的表单
  getPlanForm: {
    method: 'GET',
    url: baseUrl + `api/nursing/form/getPlanForm`
  },
  // 【app】查询随访统计的详细
  appQueryFollowCountDetail: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/queryFollowCountDetail`
  },
  // 【医生端】查询学习计划栏目下文章的统计数据
  appQueryCountLearnPlanPush: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/queryCountLearnPlanPush`
  },
  // 【app】查询非学习计划的栏目的统计数据
  appQueryCountOtherPush: {
    method: 'GET',
    url: baseUrl + `api/nursing/followTask/queryCountOtherPush`
  }
}

// 【app】查询非学习计划的栏目的统计数据
export function appQueryCountOtherPush (followTaskContentId) {
  let url = apiList.appQueryCountOtherPush.url + '?followTaskContentId=' + followTaskContentId
  return axiosApi({
    method: apiList.appQueryCountOtherPush.method,
    url: url
  })
}

// 患者标签列表
export function getPatientTagQuery (data) {
  return axiosApi({
    method: apiList.getPatientTagQuery.method,
    url: apiList.getPatientTagQuery.url
  })
}

// 【app】 查询随访任务的简介
export function appFindFollowBriefIntroduction (tagIds) {
  let url = apiList.appFindFollowBriefIntroduction.url
  if (tagIds !== '') {
    url = apiList.appFindFollowBriefIntroduction.url + '?tagIds=' + tagIds
  }
  return axiosApi({
    method: apiList.appFindFollowBriefIntroduction.method,
    url: url
  })
}

// 【app】 查询全部随访任务的计划安排
export function appFindFollowPlanAll (tagIds, currentPage) {
  let url = apiList.appFindFollowPlanAll.url + '?currentPage=' + currentPage
  if (tagIds !== '') {
    url = apiList.appFindFollowPlanAll.url + '?currentPage=' + currentPage + '&tagIds=' + tagIds
  }
  return axiosApi({
    method: apiList.appFindFollowPlanAll.method,
    url: url
  })
}

// App 查询学习计划安排
export function appFindLearnPlan (tagIds) {
  let url = apiList.appFindLearnPlan.url
  if (tagIds) {
    url = apiList.appFindLearnPlan.url + '?tagIds=' + tagIds
  }
  return axiosApi({
    method: apiList.appFindLearnPlan.method,
    url: url
  })
}

export function appFindOtherPlan (planId, currentPage) {
  const url = apiList.appFindOtherPlan.url + '?currentPage=' + currentPage + '&planId=' + planId
  return axiosApi({
    method: apiList.appFindOtherPlan.method,
    url: url
  })
}

/**
 * 根据计划ID查询计划配置的表单
 * @param planId
 * @returns {*|Promise|Promise<unknown>}
 */
export function getPlanForm (planId) {
  const url = apiList.getPlanForm.url + '?planId=' + planId
  return axiosApi({
    method: apiList.getPlanForm.method,
    url: url
  })
}

/**
 *【app】查询随访统计的详细
 * @returns {*|Promise|Promise<unknown>}
 */
export function appQueryFollowCountDetail () {
  return axiosApi({
    method: apiList.appQueryFollowCountDetail.method,
    url: apiList.appQueryFollowCountDetail.url
  })
}

export function appQueryCountLearnPlanPush () {
  return axiosApi({
    method: apiList.appQueryCountLearnPlanPush.method,
    url: apiList.appQueryCountLearnPlanPush.url
  })
}
