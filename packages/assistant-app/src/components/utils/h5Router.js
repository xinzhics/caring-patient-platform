/**
 * 关闭H5页面的方法
 */
import {Toast} from 'vant'
import Vue from 'vue'
Vue.use(Toast)

export function h5Close (commonMsg) {
  if (commonMsg) {
    window.parent.postMessage({action: 'goBack', commonMsg: commonMsg, backType: 'patientChat'}, '*')
  } else if (localStorage.getItem('CARING_H5_BACK')) {
    const h5Back = localStorage.getItem('CARING_H5_BACK')
    localStorage.removeItem('CARING_H5_BACK')
    window.parent.postMessage({action: 'goBack', backType: h5Back}, '*')
  } else {
    window.parent.postMessage({action: 'goBack'}, '*')
  }
}

/**
 * 告知uni。h5需要预览打开外链
 * @param cmsUrl
 */
export function previewWeiXinCms (cmsUrl) {
  console.log('previewWeiXinCms', cmsUrl)
  window.parent.postMessage({action: 'previewWeiXinCms', data: cmsUrl}, '*')
}

/**
 * 预约审批变化
 */
export function reservationApproval () {
  window.parent.postMessage({action: 'reservationApprovalChange'}, '*')
}

/**
 * 精准预约跳转到uni im聊天
 * 约定只有预约可以进入im。 从im返回也固定到预约审核列表
 * @param id 患者id
 */
export function jumpPatientChat (id) {
  window.parent.postMessage({action: 'jumpPatientChat', data: id}, '*')
}

/**
 * 表单回退到患者管理平台
 */
export function backPatientManage () {
  window.parent.postMessage({action: 'backPatientManage'}, '*')
}
