import axiosApi from './apiAxios.js'
import apiUrl from './baseUrl.js'

const apiList = {

  screenshotPhoto: {
    method: 'GET',
    url: apiUrl + `api/file/screenshotTask/anno/getScreenshotPhoto?fileName=`
  }

}

/**
 * 获取视频封面图片
 * @param filename
 */
export function getScreenshotPhoto (filename) {
  let url = apiList.screenshotPhoto.url + filename
  return axiosApi({
    method: apiList.screenshotPhoto.method,
    url: url
  })
}

/**
 * 创建视频封面任务并返回封面链接
 * @param filename
 */
export const createScreenshotTaskAndReturnCover = (data) => {
  return axiosApi({
    method: 'POST',
    url: apiUrl + `api/file/screenshotTask/createScreenshotTaskAndReturnCover`,
    data
  })
}

/**
 * 查询华为云配置
 * @param filename
 */
export const getHuaweiObsPath = () => {
  return axiosApi({
    method: 'GET',
    url: apiUrl + `api/file/screenshotTask/getHuaweiObsPath`
  })
}
