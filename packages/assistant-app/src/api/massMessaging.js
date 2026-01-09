import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  /**
   * 群发消息的分页列表
   */
  massMessagePage: {
    method: 'POST',
    url: baseUrl + `api/msgs/im/getChatGroupSend`
  },
  /**
   * 发送一个群发消息
   */
  sentMessage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/nursingStaff/sendGroupChat`
  },
  /**
   * 群发消息时的患者列表
   */
  imGroupPatientPage: {
    method: 'POST',
    url: baseUrl + `api/ucenter/patient/imGroupPage`
  },
  /**
   * 删除群发消息
   */
  deleteMessage: {
    method: 'DELETE',
    url: baseUrl + `api/msgs/im/deleteGroupSend/`
  }
}

/**
 * 群发消息的分页
 * @param data
 */
export function massMessagePage (data) {
  return axiosApi({
    ...apiList.massMessagePage,
    data
  })
}

/**
 * 发送群发消息
 * @param data
 */
export function sentMessage (data) {
  return axiosApi({
    ...apiList.sentMessage,
    data
  })
}

/**
 * 群发消息选择联系人页面的患者列表
 */
export function imGroupPatientPage (data) {
  return axiosApi({
    ...apiList.imGroupPatientPage,
    data
  })
}

/**
 * 删除一条群发消息
 */
export function deleteMessage (id) {
  return axiosApi({
    method: apiList.deleteMessage.method,
    url: apiList.deleteMessage.url + id
  })
}
