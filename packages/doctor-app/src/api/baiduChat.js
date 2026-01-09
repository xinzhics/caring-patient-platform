import axiosApi from "./apiAxios";
import baseUrl from "./baseUrl";

const apiList = {
  // 发送消息
  baiduBotSseChat: {
    method: "POST",
    url: baseUrl + `/msgs/baiduBotDoctorChat/sseChat`
  },
  // 获取医生和百度灵医最新的消息
  getBaiDulastNewMessage: {
    method: 'GET',
    url: baseUrl + `/msgs/baiduBotDoctorChat/lastNewMessage?doctorId=`
  },
  // 获取消息列表
  getAiChatListPage: {
    method: "GET",
    url: baseUrl + `/msgs/baiduBotDoctorChat/chatListPage`
  },
}


export default {
  getAiChatListPage(createTime) {
    let url = apiList.getAiChatListPage.url + "?doctorId=" + localStorage.getItem('caring_doctor_id') + '&createTimeString=' + createTime
    return axiosApi({
      method: apiList.getAiChatListPage.method,
      url: url,
    });
  },
  baiduBotSseChat(data) {
    return axiosApi({
      ...apiList.baiduBotSseChat,
      data,
    });
  },
  getBaiDulastNewMessage() {
    let url = apiList.getBaiDulastNewMessage.url + localStorage.getItem('caring_doctor_id')
    return axiosApi({
      method: apiList.getBaiDulastNewMessage.method,
      url: url,
    })
  },
}
