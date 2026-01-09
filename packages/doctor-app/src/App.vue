<template>
  <div id="app">
    <keep-alive>
<router-view v-if="$route.meta.keepAlive"></router-view>
</keep-alive>
<router-view v-if="!$route.meta.keepAlive"></router-view>
  </div>
</template>

<script>
(function () {
  if (
    typeof WeixinJSBridge == "object" &&
    typeof WeixinJSBridge.invoke == "function"
  ) {
    handleFontSize();
  } else {
    if (document.addEventListener) {
      document.addEventListener("WeixinJSBridgeReady", handleFontSize, false);
    } else if (document.attachEvent) {
       console.log(999);
      document.attachEvent("WeixinJSBridgeReady", handleFontSize);
      document.attachEvent("onWeixinJSBridgeReady", handleFontSize);
    }
  }

  function handleFontSize() {
    // 设置网页字体为默认大小
    WeixinJSBridge.invoke("setFontSizeCallback", { fontSize: 0 });
    // 重写设置网页字体大小的事件
    WeixinJSBridge.on("menu:setfont", function () {
      WeixinJSBridge.invoke("setFontSizeCallback", { fontSize: 0 });
    });
  }
})();
  import ApiT from '@/api/doctor.js'
  import wx from "weixin-js-sdk";
  import {
    Base64
  } from 'js-base64'


  export default {
    name: 'App',
    data: function () {
      return {
        closePageRouter: [
          '/',
          '/register/index',
          '/scanningReferral/index'
        ]
      }
    },
    mounted() {
      window.localStorage.setItem('scanCodeUrl', location.href)
    },
    beforeDestroy() {
      localStorage.removeItem('cmsArticleStatus')
      localStorage.removeItem('cmsReleaseStatus')
    },
    created: function () {
      localStorage.removeItem('cmsReleaseStatus')
      localStorage.removeItem('cmsArticleStatus')
      let that = this
      if (localStorage.getItem('headerTenant')) {
        this.$initDict()
        this.$showHist()
        let headerTenant = localStorage.getItem('headerTenant')
        let mycode = Base64.decode(headerTenant)
        const params = {
          code: mycode,
          userType: 'DOCTOR'
        }
        ApiT.geth5router(params).then((res) => {
          if (res.data.code === 0) {
            localStorage.setItem('doctorRouterData', JSON.stringify(res.data.data))
          }
        })

        // 进入系统时。 如果医生ID存在，但是医生的IM账号没有缓存在本地。那么刷新本地缓存
        if (localStorage.getItem('caring_doctor_id') && localStorage.getItem('caring_doctor_id') !== 'undefined' && !localStorage.getItem('userImAccount')) {
          const doctorParams = {id: localStorage.getItem('caring_doctor_id')}
          ApiT.getContent(doctorParams).then(res => {
            if (res.data.code === 0) {
              if (res.data.data) {
                localStorage.setItem('userImAccount', res.data.data.imAccount)
              }
            }
          })
        }
      }

      /**
       * 监听页面的物理返回事件。 当是 closePageRouter 中的路径时，则直接关闭浏览器
       */
      window.addEventListener('popstate', (event) => {
        const currentPathRouter = localStorage.getItem("currentPathRouter")
        console.log('onPopState', event, currentPathRouter)
        console.log('onPopState', that.closePageRouter.indexOf(currentPathRouter))
        // 如果物理返回时，页面的路径是下面这些路径时，
        if (that.closePageRouter.indexOf(currentPathRouter) > -1 && currentPathRouter !== '/') {
          event.preventDefault();
          event.stopPropagation();
          wx.closeWindow()
        }
      })
    },
  }

</script>

<style lang="less">
  @import '~vux/src/styles/reset.less';
  // #app{
  // width:100vw;
  // height: 100vh;
  // background-color: #fafafa;

  // }
  body{
  -webkit-text-size-adjust: 100%!important;
}
  #app {
    min-height: 100vh;
    background: #f5f5f5;
  }

  :root{
    // 往下补充
    --caring-tab-action-color: #3BD26A;      //vant tab 组件的标签选中的 文字颜色和 文字下标签的颜色
    --caring-common-type-select-color: #3BD26A;      // 常用语分类选中时颜色
    --caring-common-title-icon-bg: #67E0A7;  // 常用语标题前面icon的背景色
    --caring-common-open-content-a-text: #3BD26A; // 常用语内容下打开全部的文字的颜色
    --caring-common-button-plain-text: #66E0A7;   // 常用语自定义添加按钮的文字颜色
    --caring-common-button-plain-border: #66E0A8; // 常用语自定义添加按钮边框颜色
    --caring-common-button-default-bg: #66E0A7;   // 常用语按钮默认背景色
    --caring-common-button-disable-bg: #999999;   // 常用语按钮默认背景色
    --caring-search-text-highlight: #2596E8;   // 常用语按钮默认背景色
    --caring-common-disable-select: #C3C2C2;  // 常用语模版。已经被用户选择导致的禁用状态
    --caring-common-no-select: #999999;  // 常用语模版。没有被用户选择时的状态
    --caring-common-no-select-text: #666666;  // 常用语模版。没有被用户选择时的状态
    --caring-common-search-result-bg: #F7F7F7; // 常用语搜索结果页面的背景色
    --caring-common-type-no-select-bg: #EFEFF2; //添加常用语分类未选择时背景色
    --caring-no-data-text: #666666; // 无数据时提示文字的颜色
    --caring-common-bg-color: #FFFFFF;    // 常用语卡片背景色
    --caring-first-title: #333;   // 一级主标题颜色
    --caring-first-title-bg: #3BD26A; // 一级主题文字背景填充色
    --caring-first-a-text: #3BD26A; // 一级主题文字背景填充色
    --caring-line: #DEDEDE; // 边框线条的颜色
    --caring-first-title-white: #FFFFFF; // 深色背景下的文字的颜色
    --caring-body-bg: #F5F5F5; // 页面内容背景底色
    --caring-search-action: #1D97FF; // 搜索按钮的蓝色
  }

  .weui-toast__content {
    color: #fff !important;
  }

  body {
    background-color: #fbf9fe;
  }

  .main {
    background-color: #f5f5f5;
  }

  .vux-header {
    border-bottom: 1px solid rgba(102, 102, 102, 0.1);
    margin-bottom: 10px;
  }

  .van-cell {
    padding: 17px 16px;
  }

  .van-cell__title,
  .vux-label,
  .weui-label {
    color: #666;
    font-size: 14px !important;

  }

  .van-cell__value,
  .weui-cell__primary {
    color: #999;
  }

  .weui-cells {
    margin-top: 10px !important;
    font-size: 14px !important;
  }

  .weui-cell {
    padding: 17px 16px !important;
  }

  .weui-btn {
    border-radius: 30px !important;

  }

  .vux-label,
  .weui-label {
    font-size: 14px !important;
  }

  .weui-switch:after {
    left: 6px !important;
  }

  //.weui-switch:checked {
  //
  //  border-color: #5294F7 !important;
  //  background-color: #5294F7 !important;
  //}

  .weui-grid__label {
    color: #666 !important;
  }

  .van-field__label {
    width: 9.2em;
  }

  .classTextarea>.van-cell>.van-cell__value {
    border: 1px solid rgba(102, 102, 102, 0.1);
    padding: 5px;
    text-align: left;
  }

  .textareaCont::before {
    border-top: none !important;
    // border: 1px solid rgba(102,102,102,0.1);
  }

  .van-button--primary {
    background-color: rgba(102, 102, 102, 0.85);
    border: 1px solid rgba(102, 102, 102, 0.85);
  }

  .vux-number-selector-sub {
    border-radius: 20px 0 0 20px !important;

  }

  .vux-number-selector-plus {
    border-radius: 0 20px 20px 0 !important;
  }

  .vux-number-input,
  .vux-popup-picker-value {
    color: #333 !important;
    font-size: 15px !important;
  }


  .btn {
    background-color: rgba(102, 102, 102, 0.85) !important;
    border: 1px solid rgba(102, 102, 102, 0.85) !important;
  }

</style>
<style>
  .weui-btn_primary {
    background-color: #3F86FF;
  }

  .checkBox input[type=checkbox]:checked {
    background-color: #999 !important;
  }

  .vue-directive-image-previewer-body img {
    cursor: pointer;
    top: 20% !important;
    left: 10% !important;
    width: 80% !important;
    height: auto !important;
    transition: all 0ms ease 0ms;
  }

  .setBnts {
    border: none !important;
    border: 1px solid rgba(0, 0, 0, 0.2) !important
      /* // border: 1px solid rgba(102,102,102,0.1); */
  }

  .weui-btn:after {
    border: none !important;
  }

  .mypicker>.weui-cell {
    padding: 0px !important
  }

  .caring-select{
    outline:none;
    width: 77px;
    height: 29px;
    border-radius: 5px 5px 5px 5px;
    border: 1px solid #999999;
    color: #666666;
    font-size: 14px;
    font-weight: 400;
  }

  .caring-select:active{
    border: #999999;
  }
  .caring-form-button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 34px;
    text-align: center;
    border-radius: 17px 17px 17px 17px;
    opacity: 1;
    border: 1px solid #66E0A8;
    font-weight: 500;
    color: #66E0A7;
    font-size: 14px;
  }


  .studiocms-content {

  }

  .studiocms-content h1,
  .studiocms-content h2,
  .studiocms-content h3 {
    margin-top: 1.5em;
    margin-bottom: 0.8em;
    font-weight: 600;
  }
  .studiocms-content p {
    margin-bottom: 1em;
  }
  .studiocms-content a {
    color: #007bff;
    text-decoration: none;
  }
  .studiocms-content a:hover {
    text-decoration: underline;
  }
  .studiocms-content code {
    background-color: #f4f4f4;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
  }
  .studiocms-content pre {
    background-color: #2d2d2d;
    color: #f8f8f2;
    padding: 16px;
    border-radius: 6px;
    overflow-x: auto;
  }
  .studiocms-content pre code {
    background: none;
    padding: 0;
  }
  .studiocms-content ul, ol {
    padding-left: 2em;
  }
  .studiocms-content img {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
  }

  .studiocms-content blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

</style>
