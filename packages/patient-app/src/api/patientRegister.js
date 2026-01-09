import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'


/**
 * 获取视频封面图片
 * @param filename
 */
export function checkMobileIsPatient(phone) {
  return axiosApi({
    method: 'GET',
    url: `${apiUrl}/ucenter/patient/anno/checkMobile?phone=${phone}`,
  })
}


/**
 * 发送验证码
 * @param data
 * @returns {Promise<unknown>}
 */
export function sendPsdCode(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/msgs/verification/anno/send`,
    data
  })
}


/**
 * 验证验证码
 * @param data
 * @returns {Promise<unknown>}
 */
export function verification(data) {
  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/msgs/verification/anno`,
    data
  })
}

export function resetPassword(data) {

  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/ucenter/patient/anno/resetPassword`,
    data
  })
}

/**
 * 注册
 * {
	"openId": "",
	"password": "",
	"phone": ""
  }
 * @param data
 * @returns {Promise<unknown>}
 */
export function patientRegister(data) {

  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/ucenter/anno/patient/register`,
    data
  })

}

/**
 * 登录
 * {
	"openId": "",
	"password": "",
	"phone": ""
  }
 */
export function patientLogin(data) {

  return axiosApi({
    method: 'POST',
    url: `${apiUrl}/ucenter/anno/patient/login`,
    data
  })

}
