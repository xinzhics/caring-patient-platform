import axiosApi from "./apiAxios.js";
import apiUrl from './baseUrl.js'

const apiList = {

  screenshotPhoto:{
    method: 'GET',
    url: apiUrl + `/file/screenshotTask/anno/getScreenshotPhoto?fileName=`
  }

}

/**
 * 获取视频封面图片
 * @param filename
 */
export function getScreenshotPhoto(filename) {
  let url = apiList.screenshotPhoto.url + filename
  return axiosApi({
    method: apiList.screenshotPhoto.method,
    url: url
  })
}
