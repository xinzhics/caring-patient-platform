import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  /**
   * 按周统计
   */
  statisticsWeek: {
    method: 'GET',
    url: baseUrl + `api/nursing/appointment/accurateReservation/statistics/week`
  },
  /**
   * 按天统计
   */
  statisticsDay: {
    method: 'GET',
    url: baseUrl + `api/nursing/appointment/accurateReservation/statistics/day`
  },

  /**
   * 预约的列表
   */
  pageAppointment: {
    method: 'POST',
    url: baseUrl + `api/nursing/appointment/page`
  },

  /**
   * 预约签到
   */
  updateAppointment: {
    method: 'PUT',
    url: baseUrl + `api/nursing/appointment`
  },

  /**
   * 医生的预约配置
   */
  getAppointConfig: {
    method: 'GET',
    url: baseUrl + `api/nursing/appointConfig/doctorId/`
  },

  /**
   * 修改预约设置
   */
  updateAppointConfig: {
    method: 'PUT',
    url: baseUrl + `api/nursing/appointConfig`
  },

  /**
   * 新增预约设置
   */
  createAppointConfig: {
    method: 'POST',
    url: baseUrl + `api/nursing/appointConfig`
  },

  appointmentDoctorPage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/doctor/appointmentDoctorPage`
  }

}

/**
 * 按周统计
 * @param organId
 * @param week
 * @returns {Promise<>}
 */
export function statisticsWeek (nursingId, doctorId, week) {
  let url = apiList.statisticsWeek.url + '?nursingId=' + nursingId + '&week=' + week
  if (doctorId) {
    url += '&doctorId=' + doctorId
  }
  return axiosApi({
    method: apiList.statisticsWeek.method,
    url: url
  })
}

/**
 * 按日期统计
 * @param organId
 * @param day 2022-10-24
 */
export function statisticsDay (nursingId, doctorId, day) {
  let url = apiList.statisticsDay.url + '?nursingId=' + nursingId + '&day=' + day
  if (doctorId) {
    url += '&doctorId=' + doctorId
  }
  return axiosApi({
    method: apiList.statisticsDay.method,
    url: url
  })
}

/**
 * 分页查询预约记录
 * @param data
 * @returns {Promise<unknown>}
 */
export function pageAppointment (data) {
  return axiosApi({
    ...apiList.pageAppointment,
    data
  })
}

/**
 * 医生的配置查询
 * @param doctorId
 */
export function appointConfig (doctorId) {
  return axiosApi({
    method: apiList.getAppointConfig.method,
    url: apiList.getAppointConfig.url + doctorId
  })
}

/**
 * 更新预约设置
 * @param data
 * @returns {Promise<>}
 */
export function updateAppointConfig (data) {
  return axiosApi({
    ...apiList.updateAppointConfig,
    data
  })
}

/**
 * 创建预约设置
 * @param data
 * @returns {Promise<>}
 */
export function createAppointConfig (data) {
  return axiosApi({
    ...apiList.createAppointConfig,
    data
  })
}

/**
 * 预约签到
 * @param data
 * @returns {Promise<>}
 */
export function updateAppointment (data) {
  return axiosApi({
    ...apiList.updateAppointment,
    data
  })
}

/**
 * 预约设置中 医生列表
 * @param data
 * @returns {Promise | Promise<unknown>}
 */
export function appointmentDoctorPage (data) {
  return axiosApi({
    ...apiList.appointmentDoctorPage,
    data
  })
}
