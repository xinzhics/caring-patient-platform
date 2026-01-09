<template>
  <section class="allContent">
    <navBar pageTitle="我的二维码" backUrl="index"/>
    <div class="swiperContent">
      <div class="btnswiper">
      </div>
      <van-image
        fit="contain"
        style="width:80vw;margin-top:15px;"
        :src="QrCode"
      >
      </van-image>
      <div class="btnswiper">
      </div>
    </div>


    <div class="bannerDown" v-if="showButton">
      <div class="saveImg" @click="saveBtn">
        保存图片
      </div>
      <div class="shareImg" @click="show=true">
        分享到微信
      </div>
    </div>
    <!-- 图片弹窗 -->
    <div class="dialogShow" v-if="show">
      <img :src="showCover" alt="" width="100%" @click="show=false">
    </div>
    <!-- 图片弹窗 -->
    <div class="dialogShow" v-if="showOne">
      <div @click="showOne=false"
           style="position: absolute;width:30px;height:30px;top:7vw;right:2vw;background:#fff;border-radius:50%">
        <x-icon type="ios-close-empty" size="30"></x-icon>
      </div>
      <img :src="OneImg" alt="" style="width:90vw;margin:10vw 5vw">
    </div>
    <!-- 图片弹窗 -->
    <div class="dialogShow" v-if="showTwo">
      <div @click="showTwo=false"
           style="position: absolute;width:30px;height:30px;top:7vw;right:2vw;background:#fff;border-radius:50%">
        <x-icon type="ios-close-empty" size="30"></x-icon>
      </div>
      <img :src="TwoImg" alt="" style="width:90vw;margin:10vw 5vw">
    </div>
  </section>
</template>
<script>
import doctorApi from '@/api/doctor.js'
import contentApi from '@/api/Content.js'
import wx from 'weixin-js-sdk'
import {Swiper, SwiperItem} from 'vux'
export default {
  components: {
    Swiper,
    SwiperItem,
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      headerImg: require('@/assets/my/nursingstaff_avatar.png'),
      showCover: require('@/assets/drawable-xhdpi/cover.png'),
      show: false,
      baseInfo: {
        avatar: '',
        businessCardQrCode: ''
      },
      projectInfo: {
        logo: ''
      },
      loading: false,
      swiperIndex: 0,
      OneImg: require('@/assets/drawable-xhdpi/loading.gif'),
      canfalse: false,
      TwoImg: require('@/assets/drawable-xhdpi/loading.gif'),
      canset: [false, false, false],
      showOne: false,
      showTwo: false,
      doctorName: '',
      showButton: false,
      QrCode: ''
    }
  },
  created() {
    if (localStorage.getItem('doctortoken') && localStorage.getItem('caring_doctor_id')) {
      this.showButton = true
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.doctorId) {
      var s = window.location.href;
      var h = s.split(".")[0];
      var a = h.split("//")[1];
      contentApi.getByDomain(a)
        .then(res => {
          if (res.data.code === 0) {
            this.projectInfo = res.data.data
            window.document.title = res.data.data.name
            let tenantCode = res.data.data.code
            contentApi.getAnnoDoctorInfo({
              id: this.$route.query.doctorId,
              tenantCode: tenantCode,
            })
              .then((res) => {
                if (res.data.code === 0) {
                  this.baseInfo = res.data.data
                  this.QrCode = res.data.data.businessCardQrCode
                  this.doctorName = res.data.data.name
                  this.getwxAnnoSignature(tenantCode)
                }
              })
          }
        })
    } else {
      this.getInfo()
      this.projectInfo = JSON.parse(localStorage.getItem('projectInfo'))
    }

  },
  methods: {
    getInfo() {
      const params = {
        id: localStorage.getItem('caring_doctor_id')
      }
      this.loading = true
      doctorApi.getContent(params).then((res) => {
        this.loading = false
        if (res.data.code === 0) {
          this.baseInfo = res.data.data
          this.doctorName = res.data.data.name
          this.getwxSignature()
        }
      }).catch(() => {
        this.loading = false
      })
    },
    saveBtn() {
      this.$vux.toast.show({
        type: 'text',
        position: 'center',
        text: '请长按二维码保存到相册'
      })
      this.QrCode = this.baseInfo.downLoadQrcode
    },
    next(k) {
      console.log(this.swiperIndex)
      if (k) {
        if (this.swiperIndex === 1) {
          return this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '已是最后一张'
          })
        }
        this.swiperIndex = 1
      } else {
        if (this.swiperIndex === 0) {
          return this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '已是最后一张'
          })
        }
        this.swiperIndex = 0
      }
    },
    wxSignature(res, title, avatar) {
      if (res.data.code === 0) {
        const link = 'http://' + document.domain + '/doctor/scanImg?doctorId=' + localStorage.getItem('caring_doctor_id') // 分享链接
        wx.config({
          // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
          debug: false,
          // 必填，公众号的唯一标识
          appId: res.data.data.appId,
          // 必填，生成签名的时间戳
          timestamp: res.data.data.timestamp,
          // 必填，生成签名的随机串
          nonceStr: res.data.data.nonceStr,
          // 必填，签名
          signature: res.data.data.signature,
          // 必填，需要使用的JS接口列表，所有JS接口列表
          jsApiList: [
            'onMenuShareTimeline',
            'onMenuShareAppMessage',
            'onMenuShareQQ',
            'onMenuShareWeibo',
            'onMenuShareQZone'
          ]
        });
        wx.ready(function () {
          // 分享给好友
          wx.onMenuShareAppMessage({
            title: title, // 分享标题
            desc: '', // 分享描述
            link: link, // 分享链接
            imgUrl: avatar ? avatar : 'https://caing-test.obs.cn-north-4.myhuaweicloud.com/23a176879c0c5675556687e5718cc82.png', // 分享图标
            success: function () {
              console.log(111)
            },
            cancel: function () {
              console.log(222)
            }
          })

          // 分享到朋友圈
          wx.onMenuShareTimeline({
            title: title, // 分享标题
            link: link, // 分享链接
            imgUrl: avatar ? avatar : 'https://caing-test.obs.cn-north-4.myhuaweicloud.com/23a176879c0c5675556687e5718cc82.png', // 分享图标
            success: function () {
              console.log(111)
            },
            cancel: function () {
              console.log(222)
            }
          })
        });
        wx.error(function () {
          console.log('配置不成功')
        });
      }
    },
    getwxAnnoSignature(tenantCode) {
      const that = this
      const params = {
        url: that.$getWxConfigSignatureUrl(),
        tenantCode: tenantCode,
        wxAppId: localStorage.getItem('wxAppId')
      }
      let title
      if (that.doctorName && that.doctorName.length > 0 ) {
        title = '我是' + that.doctorName + '，点这里扫码加入服务'; // 分享标题
      } else {
        title = '您好，请点这里扫码加入服务'; // 分享标题
      }
      contentApi.wxAnnoSignature(params).then((res) => {
        that.wxSignature(res, title, that.baseInfo.avatar)
      })

    },
    getwxSignature() {
      const that = this
      const params = {
        url: that.$getWxConfigSignatureUrl(),
        wxAppId: localStorage.getItem('wxAppId')
      }
      let title
      if (that.doctorName && that.doctorName.length > 0 ) {
        title = '我是' + that.doctorName + '，点这里扫码加入服务'; // 分享标题
      } else {
        title = '您好，请点这里扫码加入服务'; // 分享标题
      }
      contentApi.wxSignature(params).then((res) => {
        that.wxSignature(res, title, that.baseInfo.avatar)
      })
    },
  }
}
</script>
<style lang="less" scoped>
/deep/ .van-image__img {
  border-radius: 10px;
}

.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;

  .swiperContent {
    display: flex;
    justify-content: space-between;

    .btnswiper {
      /*flex 布局*/
      display: flex;
      /*实现垂直居中*/
      align-items: center;
      /*实现水平居中*/
      justify-content: center;
      // line-height: 80vh;
    }

    padding-bottom: 80px;
  }

  .inner {
    width: 80vw;
    position: absolute;
    // margin: 20px 10vw 20px;
    border-radius: 10px;
    background: #fff;
    overflow: hidden;
    // box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    z-index: 0;
    // top: 50px;
    left: 0;

    .titleTwo {
      line-height: 20px;
      text-align: center;
      margin: 30px 0;
    }

    .headerInner {
      padding: 5vw;
      background: #5292FF;

      .title {
        color: #fff;
        font-size: 12px;
        line-height: 20px;
      }

      .imgInner {
        width: 4rem;
        height: 4rem;
        border-radius: 50%;
        overflow: hidden;
        border: 1px solid rgb(255, 255, 255);
        margin: 0 auto;
        img {
          width: 94%;
          height: 94%;
          border-radius: 50%;
          background: #fff;
          margin: 3%;
        }
      }

      .docsInner {
        margin-top: 10px;
        color: #fff;
        text-align: center;

        .name {
          font-size: 15px;
          font-weight: 600;
          color: #fff;
        }

        .item {
          font-size: 12px;
          color: #fff;
        }
      }
    }

    .content {
      font-size: 13px;
      color: #B8B8B8;
      text-align: center;

      p {
        margin: 20px;
      }

      img {
        width: 70%;
      }

      .footer {
        padding: 5vw;
        text-align: left;
        border-top: 1px dashed #ccc;
        // border-top-left-radius: 5px;
        // border-top-right-radius: 5px;
        .docsInner {
          vertical-align: middle;
          display: inline-block;

          .name {
            font-size: 15px;
            font-weight: 600;
            color: #333;
            line-height: 30px;
            margin: 0px;
          }

          .item {
            font-size: 12px;
            color: #666;
            line-height: 20px;
            margin: 0px;
          }
        }
      }
    }
  }

  .bannerDown {
    width: 100vw;
    border-top: 1px solid rgba(102, 102, 102, 0.1);
    position: fixed;
    left: 0;
    bottom: 0;
    display: flex;
    justify-content: space-between;
    color: #B8B8B8;
    z-index: 999;
    background: #fff;

    .saveImg {
      width: 50%;
      border-right: 1px solid rgba(102, 102, 102, 0.1);
      text-align: center;
      padding: 15px 0px;
    }

    .shareImg {
      width: 50%;
      text-align: center;
      padding: 15px 0px;
    }
  }

  .dialogShow {
    width: 100%;
    height: 100%;
    position: fixed;
    left: 0;
    top: 0;
    background: rgba(0, 0, 0, 0.6);
    z-index: 99999;

  }
}
</style>
