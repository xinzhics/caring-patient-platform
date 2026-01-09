// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import 'default-passive-events'
import { Constants } from '@/api/constants'
import 'vant/lib/index.css'
import constantItem from './api/constantItem.js' // error log
import { compressionImg } from '@/components/utils/compressImg.js'
import { h5Close, previewWeiXinCms, jumpPatientChat, backPatientManage, reservationApproval } from '@/components/utils/h5Router.js'
import headNavigation from '@/components/headNavigation/headNavigation'
import { cleanFieldRepeat } from './components/utils/formResultField'
import 'vant/lib/icon/local.css'

Vue.config.productionTip = false
Vue.prototype.Constants = Constants
Vue.prototype.$initDict = constantItem.initDict
Vue.prototype.$getDictItem = constantItem.getDictItem
Vue.prototype.$showHist = constantItem.showHist
Vue.prototype.$getShowHist = constantItem.getShowHist
Vue.prototype.$getDoctorDefaultAvatar = constantItem.getDoctorDefaultAvatar
Vue.prototype.$formatPatientName = constantItem.formatPatientName
Vue.prototype.$compressionImg = compressionImg
Vue.prototype.$cleanFieldRepeat = cleanFieldRepeat
Vue.prototype.$h5Close = h5Close
Vue.prototype.$backPatientManage = backPatientManage
Vue.prototype.$previewWeiXinCms = previewWeiXinCms
Vue.prototype.$jumpPatientChat = jumpPatientChat
Vue.prototype.$reservationApproval = reservationApproval
Vue.config.ignoredElements.push('uni')
Vue.component('headNavigation', headNavigation)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
