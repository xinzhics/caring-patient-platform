import axiosApi from './apiAxios.js'

const apiUrl = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:8760' 
  : 'https://api.example.com'
const apiList = {

  annoJoinGroupFromQrCode: {
    method: 'POST',
    url: apiUrl + `/api/ucenter/consultationGroup/anno/joinGroupFromQrCode`
  }

}

export default {

  /**
   * 个人服务号的项目。扫码打开病例讨论端。要求用户填写手机号。
   * 使用手机号完成注册授权
   * @param data
   * @returns {Promise<unknown>}
   */
  annoJoinGroupFromQrCode(data) {
    return axiosApi({
      ...apiList.annoJoinGroupFromQrCode,
      data
    })
  },


  annoGetGroup(id) {
    return axiosApi({
      method: 'GET',
      url: apiUrl + `/api/ucenter/consultationGroup/anno/getGroup/${id}`

    })
  }




}
