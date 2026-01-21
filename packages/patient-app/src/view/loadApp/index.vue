<template>
  <section>
    <div
      :style="{width:'100vw',height:'100vh',background:'url('+bgimg+')',overflowY: 'scroll',backgroundSize: '100% 100%'}">
      <div style="    position: relative">
        <div style="position: absolute;width: 20%;top: 50%;left: 40%;">
          <img :src="logo" style="width:100%" alt="">
        </div>
        <p
          style="position: absolute;top: 63%;left:0%;font-size:16px;font-weight:600;text-align:center; width: 100%;color:#666">
          {{name}}</p>
        <img :src="logoimg" style="width:100%;" alt="">
      </div>
    </div>
    <img :src="btnimg" @click="setDilag" alt="" style="width: 100vw;position: fixed;bottom: 0px;left: 0px;">
    <div class="coverMain" v-if="coverCan" @click="coverCan=false">
      <img :src="headerimg" alt="" style="width:100vw">
    </div>
  </section>
</template>
<script>

  export default {
    name: 'loadapp',
    data() {
      return {
        browserInfo: {},
        huanjing: 'Android',
        intro: {},
        headerimg: require('@/assets/my/cover.png'),
        bgimg: require('@/assets/my/icon_9jcfxagfgyo/bg.jpg'),
        logoimg: require('@/assets/my/icon_9jcfxagfgyo/phone.png'),
        btnimg: require('@/assets/my/icon_9jcfxagfgyo/btn.png'),
        iosLogoimg: require('@/assets/my/icon_9jcfxagfgyo/iosPhone.png'),
        iosBtnimg: require('@/assets/my/icon_9jcfxagfgyo/iosBtn.png'),
        logo: '',
        name: '',
        coverCan: false
      }
    },
    created() {
      this.setLiuLanQi()
      this.setImage()
    },
    mounted() {
      this.getInfo()
    },
    methods: {
      setImage() {
        if (this.browserInfo.isAndroid) {

        } else if (this.browserInfo.isIphone || this.browserInfo.isIpad) {
          this.logoimg = this.iosLogoimg
          this.btnimg = this.iosBtnimg
        }
      },
      setLiuLanQi() {
        this.browserInfo = {
          isAndroid: Boolean(navigator.userAgent.match(/android/ig)), // 是否是Android浏览器
          isIphone: Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig)), // 是否是苹果浏览器
          isIpad: Boolean(navigator.userAgent.match(/ipad/ig)), // 是否是 IPad 浏览器
          isWeixin: Boolean(navigator.userAgent.match(/MicroMessenger/ig)), // 是否是微信平台浏览器
          isAli: Boolean(navigator.userAgent.match(/AlipayClient/ig)), // 是否是支付宝平台浏览器
          isPhone: Boolean(/(iPhone|iPad|iPod|iOS|Android)/i.test(navigator.userAgent)) // 是否是手机端
        }
      },
      // 判断浏览器环境
      isWeixin() {
        let result = false
        let ua = navigator.userAgent.toLowerCase();
        let isWeixin = ua.indexOf('micromessenger') != -1;
        if (isWeixin) {
          result = true;
        } else {
          result = false;
        }
        return result
      },
      setDilag() {
        // content为要复制的内容
        // 创建元素用于复制
        const projectInfo = JSON.parse(localStorage.getItem('projectInfo'))
        const ele = document.createElement('input')
        // 设置元素内容
        ele.setAttribute('value', projectInfo.code)
        // 将元素插入页面进行调用
        document.body.appendChild(ele)
        // 复制内容
        ele.select()
        // 将内容复制到剪贴板
        document.execCommand('copy')
        // 删除创建元素
        document.body.removeChild(ele)
        if (this.isWeixin()) {
          this.coverCan = true
        } else {
          if (this.browserInfo.isIphone || this.browserInfo.isIpad) {
            window.location.href = 'itms-apps://itunes.apple.com/app/id1658245749?action=write-review'
          } else {
            const that = this
            if (that.$route.query && that.$route.query.apkUrl) {
              location.href = that.$route.query.apkUrl
            }
          }
        }
      },
      getInfo() {
        var s = window.location.href;
        var h = s.split(".")[0];
        var a = h.split("//")[1];
        axios.get(`${process.env.NODE_ENV === 'development' ? 'http://localhost:8760' : 'https://api.example.com'}/api/tenant/tenant/anno/getGuideByDomain?domain=` + a).then(res => {
          console.log(res.data)
          if (res.data.code === 0) {
            this.intro = res.data.data
            this.logo = res.data.data.logo
            this.name = res.data.data.name
            window.document.title = res.data.data.name
          }
        })
      }
    }
  }
</script>
<style lang="less" scoped>
  .content {
    width: 100vw;
    padding: 0vw;
    background-color: #fff;
    word-break: break-all;

    /deep/ .__logo__ img {
      width: 90% !important;
      height: auto !important;
      max-width: 120px;
      border-radius: 8px;
      max-height: 90% !important;
    }

    /deep/ .__title__ {
      margin-top: 8px;
      font-weight: 545;
      color: #ffffff;
      font-size: 20px;
    }

  }

  .coverMain {
    position: fixed;
    width: 100vw;
    height: 100vh;
    z-index: 999;
    top: 0;
    left: 0;
    background: rgba(0, 0, 0, 0.6);
  }
</style>
