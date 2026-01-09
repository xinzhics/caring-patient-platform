import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  /**
   * 我的医生
   * 小组和医生的列表
   */
  groupAndDoctorList: {
    method: 'GET',
    url: baseUrl + `api/ucenter/nursingStaff/getAppDoctor?nursingId=`
  },
  /**
   * 我的医生
   * 医生的分页列表
   */
  doctorListPage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/doctor/doctorPage`
  },

  /**
   * 我的医生
   * 医生的分页列表
   */
  doctorPage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/doctor/page`
  },
  /**
   * 我的医生
   * 医生的详情
   */
  doctorDetails: {
    method: 'GET',
    url: baseUrl + `api/ucenter/doctor/`
  },
  /**
   * 修改医生信息
   */
  updateDoctorInfo: {
    method: 'PUT',
    url: baseUrl + `api/ucenter/doctor`
  },
  /**
   * 创建医生
   */
  createDoctor: {
    method: 'POST',
    url: baseUrl + `api/ucenter/doctor`
  },
  /**
   * 删除医生
   */
  deleteDoctor: {
    method: 'DELETE',
    url: baseUrl + `api/ucenter/doctor/delete/`
  },
  /**
   * 独立医生的名字和id
   */
  independenceDoctor: {
    method: 'POST',
    url: baseUrl + `api/ucenter/doctor/findDoctorNameAndId`
  }
}

/**
 * 我的医生
 * 小组和医生的列表
 * @param nursingId 医助的ID
 * @returns {Promise<>}
 */
export function groupAndDoctorList (nursingId) {
  return axiosApi({
    method: apiList.groupAndDoctorList.method,
    url: apiList.groupAndDoctorList.url + nursingId
  })
}
/**
 * 我的医生
 * 小组内医生的列表
 * @param data 医生分页参数
 * @returns {Promise<>}
 */
export function doctorListPage (data) {
  return axiosApi({
    ...apiList.doctorListPage,
    data
  })
}

/**
 * 我的医生
 * @param data 医生分页参数
 * @returns {Promise<>}
 */
export function doctorPage (data) {
  return axiosApi({
    ...apiList.doctorPage,
    data
  })
}

/**
 * 我的医生
 * 医生的详情
 * @param doctorId 医生id
 * @returns {Promise<>}
 */
export function doctorDetails (doctorId) {
  return axiosApi({
    method: apiList.doctorDetails.method,
    url: apiList.doctorDetails.url + doctorId
  })
}

/**
 * 我的医生
 * 修改医生信息
 * @param doctor
 */
export function updateDoctorInfo (doctor) {
  return axiosApi({
    ...apiList.updateDoctorInfo,
    'data': doctor
  })
}

/**
 * 我的医生
 * 创建医生
 * @param doctor
 * @returns {Promise<>}
 */
export function createDoctor (doctor) {
  return axiosApi({
    ...apiList.createDoctor,
    'data': doctor
  })
}

/**
 * 我的医生
 * 删除医生
 * @param doctorId
 * @returns {Promise<>}
 */
export function deleteDoctor (doctorId) {
  return axiosApi({
    method: apiList.deleteDoctor.method,
    url: apiList.deleteDoctor.url + doctorId
  })
}

/**
 * 修改医生信息(预约审核开关)
 * @param doctorId
 * @returns {Promise<unknown>|*}
 */
export function setDoctorInfo (data) {
  const url = 'api/ucenter/doctor'
  return axiosApi({
    method: 'PUT',
    url: baseUrl + url,
    data
  })
}
// 【精准预约】 医助统计待审核预约数量
export function approvalNumber (doctorId, nursingId) {
  let url
  if (doctorId) {
    url = 'api/nursing/appointment/nursing/statistics/approvalNumber?nursingId=' + nursingId + '&doctorId=' + doctorId
  } else {
    url = 'api/nursing/appointment/nursing/statistics/approvalNumber?nursingId=' + nursingId
  }
  return axiosApi({
    method: 'GET',
    url: baseUrl + url
  })
}

// 【精准预约】 医助待审核数据列表接口
export function approval (data) {
  const url = 'api/nursing/appointment/nursing/appoint/approval'
  return axiosApi({
    method: 'POST',
    url: baseUrl + url,
    data
  })
}
// 【精准预约】 医生医助同意增加号源并对预约进行审批通过
export function directApproval (appointmentId) {
  const url = 'api/nursing/appointment/directApproval?appointmentId=' + appointmentId + '&nursing=true'
  return axiosApi({
    method: 'POST',
    url: baseUrl + url
  })
}
// 【精准预约】 医助审批预约接口
export function nursingApprove (data) {
  const url = 'api/nursing/appointment/nursingApprove'
  return axiosApi({
    method: 'POST',
    url: baseUrl + url,
    showToast: true, // 不显示异常弹出
    data
  })
}
// 【精准预约】 医生医助一键清除 已过期 预约数据
export function clearAppoint (doctorId, nursingId) {
  const url = 'api/nursing/appointment/clear/appoint?doctorId=' + '&nursingId=' + nursingId
  return axiosApi({
    method: 'PUT',
    url: baseUrl + url
  })
}
// 查询医助下医生的名字和id
export function findDoctorNameAndId (data) {
  const url = 'api/ucenter/doctor/findDoctorNameAndId'
  return axiosApi({
    url: baseUrl + url,
    method: 'POST',
    data
  })
}

// 精准预约】 医生，app 查询每日待就诊或已就诊数据
export function appointmentPage (data) {
  const url = 'api/nursing/appointment/page'
  return axiosApi({
    url: baseUrl + url,
    method: 'POST',
    data
  })
}

export function createDoctorNumberCheck () {
  const url = 'api/tenant/tenantOperate/createDoctorNumberCheck'
  return axiosApi({
    url: baseUrl + url,
    method: 'GET'
  })
}
