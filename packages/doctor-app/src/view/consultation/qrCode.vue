<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">讨论组</x-header>
    <div style="position: relative; padding-top: 60px">
      <div class="blueBJ">
        <div class="blueTitle" style="padding-top: 20px;">
          <img style="width: 50px; height: 25px;" :src="require('@/assets/my/dc_left_line.png')">
          <span style="color: white; font-size: 25px; margin-left: 15px; margin-right: 15px">讨论组</span>
          <img style="width: 50px; height: 25px; " :src="require('@/assets/my/dc_right_line.png')">
        </div>
        <span class="blueTitle" style="text-align: center; font-size: 14px; color: white">
          请扫描二维码加入
        </span>
      </div>

      <div class="qrBox">
        <div class="qrBoxContent" id="captureId">
          <div style="display: flex; align-items: center">
            <img :src="data.memberAvatar ? data.memberAvatar : require('@/assets/my/touxiang.png')" alt=""
                 style="width:55px;border-radius:50%;height:55px;vertical-align: middle;margin-right:10px">
            <div style="display:flex; flex-direction: column; justify-content: center">
              <div style="font-size: 16px; color: #333333; ">
                {{ data.groupName }}
              </div>
              <div style="font-size: 12px; color: #666666; padding-right: 10px;">
                {{ data.groupDesc ? data.groupDesc : '' }}
              </div>
            </div>
          </div>
          <div style="border-bottom:2px solid #F5F5F5; margin-top: 20px; margin-bottom: 20px"></div>
          <div style="display: flex; justify-content: center; margin-top: 40px; margin-bottom: 40px">
            <qrcode :value="data.consultationEntrance" :size="200" type="img"></qrcode>
          </div>
        </div>
      </div>
    </div>

    <div class="bannerDown" v-if="!($route.query && $route.query.groupId)">
      <div class="saveImg" @click="saveBtn">
        保存图片
      </div>
      <div style="border-right: 1px solid rgba(102, 102, 102, 0.1); height: 30px"></div>
      <div class="shareImg" @click="show=true">
        分享到微信
      </div>
    </div>

    <!-- 图片弹窗 -->
    <div class="dialogShow" v-if="show">
      <img :src="showCover" alt="" width="100%" @click="show=false">
    </div>
  </section>
</template>
<script>
import doctorApi from '@/api/doctor.js'
import ApiT from '@/api/Content.js'
import wx from 'weixin-js-sdk'
import {Swiper, SwiperItem, Qrcode} from 'vux'

export default {
  components: {
    Swiper,
    SwiperItem,
    Qrcode
  },
  data() {
    return {
      data: {},
      projectInfo: {},
      show: false,
      groupId: localStorage.getItem('groupId'),
      showCover: require('@/assets/drawable-xhdpi/cover.png'),
      headerImg: require('@/assets/my/nursingstaff_avatar.png'),
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.groupId) {
      this.groupId = this.$route.query.groupId
      this.getAnnoConsultation()
    }else {
      this.getConsultationDetail()
      this.projectInfo = JSON.parse(localStorage.getItem('projectInfo'))
      this.getwxSignature()
    }

  },
  methods: {
    saveBtn() {
      this.$vux.toast.show({
        type: 'text',
        position: 'center',
        text: '请长按二维码保存到相册'
      })
    },
    getwxSignature() {
      const that = this
      const params = {
        url: that.$getWxConfigSignatureUrl(),
        wxAppId: localStorage.getItem('wxAppId')
      }
      ApiT.wxSignature(params).then((res) => {
        if (res.data.code === 0) {
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
              link: 'http://' + document.domain + '/doctor/consultation/qrCode?groupId=' + that.groupId,  // 分享链接
              title: that.data.groupName, // 分享标题
              desc: '点击查看详情',
              imgUrl: that.projectInfo.logo, // 分享图标
              success: function () {
                console.log(111)
              },
              cancel: function () {
                console.log(222)
              }
            })

            // 分享到朋友圈
            wx.onMenuShareTimeline({
              title: that.data.groupName, // 分享标题
              link: 'http://' + document.domain + '/doctor/consultation/qrCode?groupId=' + that.groupId,  // 分享链接
              imgUrl: that.projectInfo.logo, // 分享图标
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
      })
    },
    getConsultationDetail() {
      ApiT.getConsultationGroupDetail(this.groupId).then((res) => {
        if (res.data.code === 0) {
          this.data = res.data.data
        }
      })
    },
    getAnnoConsultation() {
      doctorApi.getAnnoConsultation(this.groupId).then((res) => {
        if (res.data.code === 0) {
          this.data = res.data.data
        }
      })
    },
  }
}
</script>
<style lang="less" scoped>
.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
  position: relative;

  .qrBox {
    position: absolute;
    top: 160px;
    display: flex;
    justify-content: center;
    width: 100vw;
    padding-bottom: 80px;

    .qrBoxContent {
      width: 75vw;
      padding: 15px;
      background: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
    }
  }

  .bannerDown {
    width: 100vw;
    position: fixed;
    left: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    color: #B8B8B8;
    z-index: 999;

    .saveImg {
      width: 49%;
      text-align: center;
      padding: 15px 0px;
    }

    .shareImg {
      width: 49%;
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

  .blueBJ {
    height: 150px;
    background: -webkit-linear-gradient(left, #3F86FF, #6EA8FF,);

    .blueTitle {
      display: flex;
      align-items: center;
      width: 100%;
      justify-content: center;
    }
  }
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}
</style>
