<template style="-webkit-text-size-adjust: 100%!important;">
  <div id="app">
    <keep-alive>
      <router-view v-if="$route.meta.keepAlive" />
    </keep-alive>
    <transition v-if="!$route.meta.keepAlive" :name="transitionName">
      <router-view></router-view>
    </transition>
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
import { Base64 } from "js-base64";
import wx from "weixin-js-sdk";

export default {
  name: "App",
  data: function () {
    return {
      transitionName: "",
      closePageRouter: [
        '/',      // 无路径
        '/home',  // 首页
        '/questionnaire/loginSuccessful', // 注册完成页面
        '/scanningReferral/index',  // 转诊页面
        '/loadapp/index', // app下载页面
        '/loadapp/index2',  // uniapp下载页面
        '/wxauthorize/refuse',  // 患者不存在页面
        '/rule/select',  // 身份选择页面
      ]
    };
  },
  watch: {
    //使用watch 监听$router的变化
    $route(to, from) {
      //如果to索引大于from索引,判断为前进状态,反之则为后退状态
      if (from.meta.pageName === "questionnaireIndex") {
        //设置动画名称
        this.transitionName = "vux-pop-in";
      }
      // 从路由中。找到当前最新的标题
      const routerData = localStorage.getItem("routerData");
      const path = to.path;
      if (path === '/home') {
        // 到首页的，，删除这个缓存
        localStorage.removeItem("isBannerTitle");
      }
      //console.log('==========', localStorage.getItem("isBannerTitle"))

      if (localStorage.getItem("isBannerTitle")) {
        localStorage.setItem("pageTitle", localStorage.getItem("isBannerTitle"));
      } else if (routerData && routerData.length > 0) {
        if (path === '/baseinfo/index/history' || path === '/baseinfo/index/history/detailsHistory') {
          return
        }
        const routerDataArray = JSON.parse(routerData);
        let patientMyFile = routerDataArray.patientMyFile
        let patientMyFeatures = routerDataArray.patientMyFeatures
        let tempTitle = undefined
        if (patientMyFile && patientMyFile.length > 0) {
          for (let i = 0; i < patientMyFile.length; i++) {
            if (path.indexOf(patientMyFile[i].path) > -1) {
              tempTitle = patientMyFile[i].name;
            }
          }
        }
        if (!tempTitle && patientMyFeatures && patientMyFeatures.length > 0) {
          for (let i = 0; i < patientMyFeatures.length; i++) {
            if (path.indexOf(patientMyFeatures[i].path) > -1) {
              tempTitle = patientMyFeatures[i].name;
            }
          }
        }
        if (tempTitle) {

          localStorage.setItem("pageTitle", tempTitle);
        }
      }
    },
  },

  beforeCreate() {
    if (localStorage.getItem("headerTenant")) {
      this.$initDict();
      this.$showHist();
    }
  },
  beforeDestroy() {
    // 关闭页面的时候。。删除这个缓存
    window.removeEventListener('popstate');
    localStorage.removeItem("isBannerTitle");
  },
  mounted() {
    console.log('window', window)
    window.localStorage.setItem('scanCodeUrl', location.href)
  },
  created: function () {
    let that = this
    let apiUrl = process.env.NODE_ENV === 'development' 
  ? "http://localhost:8760" 
  : "https://api.example.com";
    if (localStorage.getItem("headerTenant")) {
      let mycode = Base64.decode(localStorage.getItem("headerTenant"));
      localStorage.removeItem("pageTitle");
      axios.get(apiUrl + "/api/tenant/h5Router/anno/patientRouter/" + mycode + "?currentUserType=PATIENT")
        .then((zf) => {
          if (zf.data.code === 0) {
            localStorage.setItem('routerData', JSON.stringify(zf.data.data))
            let currentPath = localStorage.getItem('currentPathRouter')
            if (localStorage.getItem("isBannerTitle")) {
              localStorage.setItem("pageTitle", localStorage.getItem("isBannerTitle"));
            } else if (currentPath)  {
              const title = that.$pageTitleUtil.pageTitleUtil(currentPath)
              if (title) {
                console.log('设置缓存中的标题为', title)
                localStorage.setItem("pageTitle", title);
              }
            }
            console.log('打开了链接。没有pageTitle呢')
          }
        });
    }
    window.addEventListener('popstate', (event) => {
      const currentPathRouter = localStorage.getItem("currentPathRouter")
      console.log('onPopState', event, currentPathRouter)
      console.log('onPopState', that.closePageRouter.indexOf(currentPathRouter))
      // 如果物理返回时，页面的路径是下面这些路径时，
      if (that.closePageRouter.indexOf(currentPathRouter) > -1) {
        event.preventDefault();
        event.stopPropagation();
        wx.closeWindow()
      }
    })
  },
};
</script>

<style lang="less">
body {
  -webkit-text-size-adjust: 100% !important;
}
@import "~vux/src/styles/reset.less";
#app {
  background: #f5f5f5;
  min-height: 100vh;
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
  padding: 17px 16px !important;
}
.van-cell__title,
.vux-label,
.weui-label {
  color: #666;
  font-size: 15px !important;
}
.van-cell__value {
  font-size: 15px !important;
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
.weui-switch:checked {
  border-color: #5294f7 !important;
  background-color: #5294f7 !important;
}
.weui-grid__label {
  color: #666 !important;
}
.van-field__label {
  width: 9.2em;
}
.classTextarea > .van-cell > .van-cell__value {
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
  color: #999 !important;
}

.btn {
  background-color: rgba(102, 102, 102, 0.85) !important;
  border: 1px solid rgba(102, 102, 102, 0.85) !important;
}
</style>
<style>
.checkBox input[type="checkbox"]:checked {
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
  border: 1px solid rgba(0, 0, 0, 0.2) !important;
  /* // border: 1px solid rgba(102,102,102,0.1); */
}
.weui-btn:after {
  border: none !important;
}
.mypicker > .weui-cell {
  padding: 0px !important;
}
.reservationClass > .vux-no-group-title {
  margin-top: 0px !important;
}
.weui-cell_access .weui-cell__ft:after {
  border-width: 1px 1px 0 0 !important;
}
.numberOfBoxes div p {
  color: #666;
}
.numberOfBoxesDown div p label {
  color: #666;
}
.myValcolor div div div span,
.myValcolor .weui-cell__ft {
  color: #333 !important;
}
.myavatar > .van-image__img {
  border-radius: 50%;
}
.myBmi .van-cell .van-cell__value span {
  color: #0e0e0e !important;
}
/* .mycell>.van-cell__value{
    text-align: left;
  } */
.reservationClass .weui-cells:before {
  border-bottom: 1px solid #f5f5f5;
  border-top: 1px solid #f5f5f5;
}

/* 动画效果 */
.vux-pop-out-enter-active,
.vux-pop-out-leave-active,
.vux-pop-in-enter-active,
.vux-pop-in-leave-active {
  will-change: transform;
  transition: all 500ms;
  height: 100%;
  position: absolute;
  backface-visibility: hidden;
  perspective: 1000;
}
.vux-pop-out-enter {
  opacity: 0;
  /* transform: translate3d(-100%, 0, 0); */
}
.vux-pop-out-leave-active {
  opacity: 0;
  /* transform: translate3d(100%, 0, 0); */
}
.vux-pop-in-enter {
  opacity: 1;
  /* transform: translate3d(100%, 0, 0); */
}
.vux-pop-in-leave-active {
  opacity: 0;
  /* transform: translate3d(-100%, 0, 0); */
}

/deep/input[type="search"]{
  -webkit-appearance:none !important;
}

/* 设置滚动条宽度 */
::-webkit-scrollbar {
  width: 1px;
}
/* 设置滚动条轨道样式 */
::-webkit-scrollbar-track {
  background: #f1f1f1;
}
/* 设置滚动滑块样式 */
::-webkit-scrollbar-thumb {
  background: #888;
}
/* 添加滚动滑块hover效果 */
::-webkit-scrollbar-thumb:hover {
  background: #555;
}


.caring-form-div-icon{
  width: 3px;
  height: 21px;
  background: #66E0A7;
  border-radius: 0;
  opacity: 1;
}
.caring-form-look-detail-button{
  width: 48px;
  height: 17px;
  font-size: 12px;
  font-family: PingFang SC, PingFang SC;
  font-weight: 500;
  color: #66E0A7;
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

.caring-form-result-status{
  width: 61px;
  height: 21px;
  color: white;
  background: rgb(255, 90, 90);
  line-height: 21px;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
  border-radius: 16px;
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

</style>
