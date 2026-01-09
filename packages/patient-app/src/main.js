// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import FastClick from 'fastclick'
import VueRouter from 'vue-router'
import App from './App'
import store from "./webIM/store";
import router from './router/index'
import './assets/css/weui/weui.css'
import i18n from './langurage/i18n.js'

import Tools from './service/common/tools'
import { VueCropper } from 'vue-cropper'
import { Constants } from '@/api/constants';
import dictionaryItem from './components/utils/dictItem' // error log
import sassWxJs from './components/utils/wxConfigUrl' // error log
import compressionImg from '@/components/utils/compressImg'
import lang from '@/langurage/lang'
import { cleanFieldRepeat } from './components/utils/formResultField'
import pageTitleUtil from '@/components/utils/pageTitleUtils'
import 'vant/lib/index.css';
import 'vant/lib/icon/local.css';
import { Checkbox, CheckboxGroup, Icon, Uploader, Image as VanImage, Col, Row, ImagePreview, PullRefresh, Toast, Overlay, Loading, Empty, List} from 'vant';
import { LoadingPlugin, ToastPlugin, AlertPlugin,GroupTitle,Cell, Grid,GridItem,XHeader,Popup,TransferDom,XSwitch, Tabbar, TabbarItem,Flexbox, FlexboxItem,XDialog, XButton } from 'vux'
Vue.use(VueCropper);
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Icon)
Vue.use(PullRefresh)
Vue.use(VanImage)
Vue.use(Uploader)
Vue.use(Col)
Vue.use(Toast)
Vue.use(Row)
Vue.use(LoadingPlugin)
Vue.use(AlertPlugin)
Vue.use(ToastPlugin)
Vue.use(ImagePreview)
Vue.use(Loading)
Vue.use(Overlay)
Vue.use(Empty)
Vue.use(List)
Vue.prototype.$initDict = dictionaryItem.initDict
Vue.prototype.$getDictItem = dictionaryItem.getDictItem
Vue.prototype.$showHist = dictionaryItem.showHist
Vue.prototype.$getShowHist = dictionaryItem.getShowHist
Vue.prototype.$cleanFieldRepeat = cleanFieldRepeat
Vue.prototype.$getWxConfigSignatureUrl = sassWxJs.getWxConfigSignatureUrl
Vue.prototype.$compressionImg = compressionImg
Vue.prototype.$lang = lang.lang
Vue.prototype.$pageTitleUtil = pageTitleUtil
Vue.config.ignoredElements.push('wx-open-launch-weapp');
Vue.component('x-header', XHeader)
Vue.component('grid', Grid)
Vue.component('grid-item', GridItem)
Vue.component('group-title', GroupTitle)
Vue.component('cell', Cell)
Vue.directive('transfer-dom', TransferDom)
Vue.component('x-switch', XSwitch)
Vue.component('tabbar', Tabbar)
Vue.component('tabbar-item', TabbarItem)

Vue.component('popup', Popup)
Vue.component('x-dialog', XDialog)
Vue.component('x-button', XButton)

Vue.component('flexbox', Flexbox)
Vue.component('flexbox-item', FlexboxItem)
import wx from 'weixin-js-sdk'


Vue.use(VueRouter)

// util 直接注入到Vue上
Vue.use(Tools)
Vue.use(wx)

//注册AbScroller
//注册AbCustDialog
FastClick.attach(document.body)
FastClick.prototype.focus = function(targetElement) {
  let length
  if (targetElement.setSelectionRange && targetElement.type.indexOf('date') !== 0 && targetElement.type !== 'time' && targetElement.type !== 'month') {
    length = targetElement.value.length
    targetElement.focus()
    targetElement.setSelectionRange(length, length)
  } else {
    targetElement.focus()
  }
}
Vue.config.productionTip = false
Vue.prototype.Constants = Constants;
// Vue.prototype.$axios = axios
/* eslint-disable no-new */

const Base64 = require('js-base64').Base64

// 防抖处理 -- 最后执行
const on = Vue.prototype.$on
Vue.prototype.$on = function (event, func) {
  let timer
  let newFunc = func
  if (event === 'click') {
    newFunc = function () {
      clearTimeout(timer)
      timer = setTimeout(function () {
        func.apply(this, arguments)
      }, 500)
    }
  }
  // console.log('防抖')
  on.call(this, event, newFunc)
}

window.Vue = new Vue({
  router,
  store,
  Base64,
  i18n,
  render: h => h(App)
}).$mount('#app-box')
