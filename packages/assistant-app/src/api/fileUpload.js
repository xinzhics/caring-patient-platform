import axiosApi from './apiAxios.js'
import baseUrl from './baseUrl.js'

const apiList = {
  /**
   * 上传图片
   */
  fileUpload: {
    method: 'POST',
    url: baseUrl + `api/file/file/app/upload`
  }

}
/**
 * 图片上传
 * @returns {Promise<>}
 */
export function fileUpload (data) {
  return axiosApi({
    ...apiList.fileUpload,
    data
  })
}
