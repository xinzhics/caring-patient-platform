import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

const apiList = {
  //【患者端】 查询随访任务的简介
  patientFollowUpInfo: {
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryFollowUpInfo/`
  },
  //【患者端】查询 随访的 日历
  patientFollowPlanCalendar: {
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryFollowPlanCalendar/`
  },
  // 【患者端】查询 随访的 待执行计划
  patientQueryFollowPlanUnExecuted: {
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryFollowPlanUnExecuted/`
  },
  // 患者端 查询随访的已执行计划
  patientQueryFollowPlanExecuted:{
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryFollowPlanExecuted/`
  },
  // 【患者端】 查询随访统计的简介
  patientFollowCountDetail:{
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryFollowCountDetail`
  },
  // 【患者端】 统计学习计划推送阅读情况
  patientLearnPlanStatistics:{
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientLearnPlanStatistics/`
  },
  // 根据计划ID查询计划配置的表单
  getPlanForm:{
    method: 'GET',
    url: apiUrl + `/api/nursing/form/getPlanForm`
  },
  // 【患者端】查询 学习计划 执行计划
  patientQueryLearnPlan:{
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryLearnPlan/`
  },
  // 【患者端】 统计其他计划的推送打卡情况
  patientPlanStatistics:{
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientFollowPlanStatistics/`
  },
  // 患者单独订阅某护理计划
  subscribePlan:{
    method: 'PUT',
    url: apiUrl + `/api/nursing/patientNursingPlan/subscribePlan?patientId=`
  },
  //【患者端】查询 日历 某天的随访任务
  patientCalendarDayPlanDetail:{
    method: 'GET',
    url: apiUrl + `/api/nursing/followTask/patientQueryFollowPlanCalendarDayPlanDetail/`
  },
  // 用户打开了文章
  openArticle:{
    method: 'GET',
    url: apiUrl + `/api/nursing/reminderLog/submitSuccess?messageId=`
  }
}
export default {
  openArticle(messageId) {
    let url = apiList.openArticle.url + messageId
    return axiosApi({
      method: apiList.openArticle.method,
      url: url
    })
  },
  //【患者端】查询 日历 某天的随访任务
  patientCalendarDayPlanDetail(queryMonthly, planId ,learnPlan) {
    let url
    if (learnPlan == 1) {
      url = apiList.patientCalendarDayPlanDetail.url + localStorage.getItem('userId') + "?queryMonthly=" + queryMonthly + "&learnPlan=1"
    }else {
      url = apiList.patientCalendarDayPlanDetail.url + localStorage.getItem('userId') + "?queryMonthly=" + queryMonthly + "&planId=" + planId
    }
    return axiosApi({
      method: apiList.patientCalendarDayPlanDetail.method,
      url: url
    })
  },
  // 【患者端】 统计学习计划推送阅读情况
  patientPlanStatistics(planId) {
    let url = apiList.patientPlanStatistics.url + localStorage.getItem('userId') + "?planId="+planId
    return axiosApi({
      method: apiList.patientPlanStatistics.method,
      url: url
    })
  },
  // 【患者端】 统计学习计划推送阅读情况
  patientLearnPlanStatistics() {
    let url = apiList.patientLearnPlanStatistics.url + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.patientLearnPlanStatistics.method,
      url: url
    })
  },
  // 【患者端】 查询随访统计的简介
  patientFollowCountDetail() {
    let url = apiList.patientFollowCountDetail.url + '?patientId=' + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.patientFollowCountDetail.method,
      url: url
    })
  },
  //【患者端】 查询随访任务的简介
  patientFollowUpInfo() {
    let url = apiList.patientFollowUpInfo.url + localStorage.getItem('userId')
    return axiosApi({
      method: apiList.patientFollowUpInfo.method,
      url: url
    })
  },
  //【患者端】 查询随访任务的简介
  patientFollowPlanCalendar(queryMonthly, planId, learnPlan) {
    let url
    if (learnPlan == 1) {
      url = apiList.patientFollowPlanCalendar.url + localStorage.getItem('userId') + "?queryMonthly=" + queryMonthly + "&planId=" + "&learnPlan=1"
    }else {
      url = apiList.patientFollowPlanCalendar.url + localStorage.getItem('userId') + "?queryMonthly=" + queryMonthly + "&planId=" + planId
    }
    return axiosApi({
      method: apiList.patientFollowUpInfo.method,
      url: url
    })
  },
  // 【患者端】查询 随访的 待执行计划
  patientQueryFollowPlanUnExecuted(patientId,planId){
    let url = apiList.patientQueryFollowPlanUnExecuted.url+patientId
    if (planId){
      url=url+'?planId='+planId
    }
    return axiosApi({
      method: apiList.patientQueryFollowPlanUnExecuted.method,
      url: url
    })
  },
  patientQueryFollowPlanExecuted(patientId,planId){
    let url = apiList.patientQueryFollowPlanExecuted.url+patientId
    if (planId){
      url=url+'?planId='+planId
    }
    return axiosApi({
      method: apiList.patientQueryFollowPlanExecuted.method,
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
  patientQueryLearnPlan(patientId){
    const url = apiList.patientQueryLearnPlan.url + patientId
    return axiosApi({
      method: apiList.patientQueryLearnPlan.method,
      url: url
    })
  },
  subscribePlan(patientId,planId){
    const url = apiList.subscribePlan.url+patientId+'&planId='+planId
    return axiosApi({
      method: apiList.subscribePlan.method,
      url: url
    })
  }
}
