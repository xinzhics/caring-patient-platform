<template>
  <section>
    <div v-if="showShareHint">
      <div style="position: fixed; background-color: black; opacity: 0.3; width: 100%; z-index: 900; height: 800px;"></div>
      <div style="width: 100%; text-align: center; position: fixed; z-index: 9999">
        <img :src="share_hint" style="width: 85%; padding-left: 15px;">
        <img :src="i_know" style="width: 35%; margin-top: 80px;" @click="closeShareHint()">
      </div>
    </div>
    <x-header style="margin-bottom:0px !important" :left-options="{backText: '', preventGoBack: true}"
              @on-click-back="back()">文章详情</x-header>
    <div class="content" style="padding-top: 50px">
      <p class="title">{{allData.title}}</p>
      <p class="time">{{allData.updateTime}}</p>
      <div class="cms-content-detail" v-html="allData.content"></div>
      <div class="appraiseCont">

      </div>
    </div>
    <div class="tipCover">
      <x-button style="color:#fff;background:#3f86ff;width:50vw; margin-bottom: 10px; margin-top: 15px" @click.native="send()">发送
      </x-button>
    </div>
  </section>
</template>
<script>
  import {XButton, XTextarea} from "vux";
  import wx from 'weixin-js-sdk';
  import Api from '@/api/Content.js'
  import {getScreenshotPhoto} from '@/api/file.js'

  export default {
    name: "cmsPatientDetail",
    components: {
      [XButton.name]: XButton,
      [XTextarea.name]: XTextarea

    },
    mounted() {
      const that = this
      if (that.$route.query && that.$route.query.id) {
        that.id = that.$route.query.id
        that.getInfo()
      }
    },
    data() {
      return {
        id: '',
        allData: {},
        content: '',
        save: require('@/assets/my/saveS.png'),
        saveT: require('@/assets/my/saveT.png'),
        share: require('@/assets/my/share.png'),
        share_hint: require('@/assets/my/share_hint.png'),
        i_know: require('@/assets/my/i_know.png'),
        close: require('@/assets/my/close.png'),
        good: require('@/assets/my/good.png'),
        gooder: require('@/assets/my/gooder.png'),
        appraiseList: [
          {}, {}, {}, {}, {}, {}, {}, {}
        ],
        show: true,
        inputContent: '',
        cancollct: true,
        showShareHint: false,
        isIphone: Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig)), // 是否是苹果浏览器
        isIpad: Boolean(navigator.userAgent.match(/ipad/ig)), // 是否是 IPad 浏览器
      }
    },
    methods: {
      send() {
        let params = {
          icon: this.allData.icon,
          title: this.allData.title,
          summary: this.allData.summary,
          id: this.allData.id
        }
        this.$router.replace({
          path: '/im/index',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            cms: JSON.stringify(params),
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      },
      //后退
      back() {
        this.$router.replace({
          path: '/cms/cmsPatientList',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      },
      saveBtn() {
        const params = {
          contentId: this.id,
          userId: localStorage.getItem('caring_doctor_id'),
          roleType: 'doctor'
        }
        if (this.cancollct) {
          this.cancollct = false
          Api.contentCollect(params).then((res) => {
            if (res.data.code === 0) {
              this.cancollct = true
              this.getInfo()
            }
          })
        }
      },
      getInfo() {
        const params = {
          id: this.id,
          userId: localStorage.getItem('caring_doctor_id'),
          roleType: 'doctor'
        }
        Api.channelContentWithReply(params).then((res) => {
          if (res.data.code === 0) {
            this.allData = res.data.data
            this.$nextTick(function(){
              this.setVideoImage()
              this.listenerAudio()
            });
            this.getwxSignature()
          }
        })
      },
      listenerAudio() {
        // 获取页面上所有的audio元素
        let audioElements = document.getElementsByTagName('audio');
        console.log('listenerAudio', audioElements)
        // 为每个audio元素添加点击事件监听器
        for (let i = 0; i < audioElements.length; i++) {
          audioElements[i].addEventListener('play', function() {
            // 暂停所有正在播放的audio
            console.log('addEventListener', this)
            for (let j = 0; j < audioElements.length; j++) {
              if (audioElements[j] !== this && !audioElements[j].paused) {
                audioElements[j].pause();
              }
            }
            // 播放当前点击的audio
            if (this.paused) {
              this.play();
            }
          });
        }
      },
      setVideoImage() {
        const videoList = document.getElementsByTagName('video')
        if (videoList && videoList.length > 0) {
          for (let i = 0; i < videoList.length; i++) {
            let video = videoList[i]
            video.muted = true;
            video.setAttribute('x5-video-orientation', 'portraint');
            video.setAttribute('x-webkit-airplay', 'allow');
            video.setAttribute('x5-video-player-fullscreen', 'true');
            video.setAttribute('webkit-playsinline', 'true');
            video.loop = true;
            video.playsinline=true;
            if (!video.poster) {
              if (this.isIphone || this.isIpad) {
                if (video.src.indexOf('myhuaweicloud.com/cms/') > -1) {
                  let scr = video.src;
                  let filename
                  if (scr.indexOf("?") > -1) {
                    filename = scr.substring(video.src.indexOf('myhuaweicloud.com/cms/') + 22, video.src.indexOf("?") + 1)
                  } else {
                    filename = scr.substring(video.src.indexOf('myhuaweicloud.com/cms/') + 22)
                  }
                  getScreenshotPhoto(filename).then(res => {
                    if (res.data.code === 0) {
                      if (res.data.data) {
                        video.poster = res.data.data;
                      } else {
                        video.poster ="https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png";
                      }
                    } else {
                      video.poster ="https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png";
                    }
                  })
                  continue
                }
                video.poster ="https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png";
              } else {
                video.src += "#t=0.5"
              }
            }
            video.preload='auto';
          }
        }
      },
      submit() {
        if (!this.inputContent) {
          this.$vux.toast.text('请输入内容', 'center')
          return
        }
        const params = {
          contentId: this.id,
          replierId: localStorage.getItem('caring_doctor_id'),
          content: this.inputContent,
          roleType: "doctor"
        }
        Api.contentReply(params).then((res) => {
          if (res.data.code === 0) {
            // this.allData = res.data.data
            this.inputContent = undefined
            this.show = true
            this.getInfo()
          }
        })
      },
      setgoodsay(i) {
        const params = {
          replyId: i.id,
          userId: localStorage.getItem('caring_doctor_id'),
          operation: i.hasLike ? 2 : 1,
          roleType: "doctor"
        }
        Api.contentReplylike(params).then((res) => {
          if (res.data.code === 0) {
            this.getInfo()
          }
        })

      },
      getwxSignature() {
        const that = this
        const params = {
          url: that.$getWxConfigSignatureUrl(),
          wxAppId: localStorage.getItem('wxAppId')
        }
        Api.wxSignature(params).then(res => {
          if (res.data.code === 0) {
            wx.config({
              // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
              debug: false,
              // 必填，公众号的唯一标识
              appId: localStorage.getItem('wxAppId'),
              // 必填，生成签名的时间戳
              timestamp: res.data.data.timestamp,
              // 必填，生成签名的随机串
              nonceStr: res.data.data.nonceStr,
              // 必填，签名
              signature: res.data.data.signature,
              // 必填，需要使用的JS接口列表，所有JS接口列表
              jsApiList: [
                'updateTimelineShareData',
                'updateAppMessageShareData',
              ]
            });
            wx.ready(function (res) {
              wx.updateAppMessageShareData({
                title: that.allData.title, // 分享标题
                desc: that.allData.summary ?  that.allData.summary : '', // 分享描述
                link: url , // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                imgUrl: that.allData.icon, // 分享图标
                success: function () {
                  // 设置成功
                  that.showShareHint = false
                }
              });
              wx.updateTimelineShareData({
                title: that.allData.title, // 分享标题
                desc: that.allData.summary ?  that.allData.summary : '', // 分享描述
                link: url , // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
                imgUrl: that.allData.icon, // 分享图标
                success: function () {
                  // 设置成功
                  that.showShareHint = false
                }
              })
            });
          }
        })
      },
      openShare() {
        this.showShareHint = true;
      },
      closeShareHint() {
        console.log('关闭提示')
        this.showShareHint = false
      }
    }
  }
</script>
<style lang="less" scoped>

  /deep/ .vux-header {
    margin-bottom: 0px;
    height: 50px;
    position: fixed;
    width: 100%;
    z-index: 999;
    top: 0;
    left: 0;
  }

  .content {
    padding: 20px 15px;
    background: #fff;

    .title {
      font-size: 18px;
      font-weight: 800;
      color: rgb(52, 52, 52);
    }

    .time {
      font-size: 13px;
      line-height: 20px;
      color: #999;
      margin-top: 5px;
      margin-bottom: 20px;
    }
  }

  .tipCover {
    width: 94vw;
    padding: 1vw 3vw 20px;
    background: #fff;
    position: fixed;
    bottom: 0;
    left: 0;

    border-top: 1px solid rgba(102, 102, 102, 0.1);

    .inner {
      display: block;
      justify-content: space-between;

      .inputCont {
        float: left;
        width: 70%;
        background: #fafafa;
        border-radius: 60px;
        height: 26px;
        padding: 5px 15px !important;
        border: 1px solid #D6D6D6
      }

      .right {
        float: right;
        img {
          vertical-align: middle;
          width: 20px;
          margin-right: 2vw;
          margin-top: 10px;
        }
      }
    }

    .innerT {
      padding: 10px 0px;

      .title {
        display: flex;
        justify-content: space-between;
        padding: 10px 0px;

        span {
          img {
            width: 10px;
            vertical-align: middle;
          }
        }
      }

      .textareaCont {
        border: 1px solid rgba(102, 102, 102, 0.1);
      }
    }

  }

  .appraiseCont {
    margin: 10px;
    padding-bottom: 60px;

    .appraiseTitle {
      display: flex;
      justify-content: space-between;
      border-bottom: 1px solid rgba(102, 102, 102, 0.1);
      font-size: 14px;
      line-height: 30px;
      color: #999;
    }

    .item {
      display: flex;
      justify-content: flex-start;
      line-height: 16px;
      font-size: 12px;
      padding: 10px 0px;

      .itemCont {
        width: 100%;

        .contentItem {
          display: flex;
          justify-content: space-between;
          width: 100%;

          .name {
            margin: 0px !important;
            color: #999;
            margin-bottom: 6px;
            font-size: 13px;
            line-height: 20px;
          }
        }
      }
      img {
        width: 30px;
        height: 30px;
        margin-right: 10px;
        border-radius: 4px;
      }
    }
  }
</style>

<style lang="less">
  .cms-content-detail {
    padding-bottom: 40px;
  }
  .cms-content-detail p {
    font-size: 16px;
    color: #393939;
    text-align: justify;
  }
  .cms-content-detail span {
    font-size: 16px;
    text-align: justify;
  }

  .cms-content-detail table {
    border-top: 1px solid #ccc;
    border-left: 1px solid #ccc;
  }
  .cms-content-detail table td,
  table th {
    border-bottom: 1px solid #ccc;
    border-right: 1px solid #ccc;
    padding: 3px 5px;
  }
  .cms-content-detail table th {
    border-bottom: 2px solid #ccc;
    text-align: center;
  }

  /* blockquote 样式 */
  .cms-content-detail blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

  /* code 样式 */
  .cms-content-detail code {
    display: inline-block;
    *display: inline;
    *zoom: 1;
    background-color: #f1f1f1;
    border-radius: 3px;
    padding: 3px 5px;
    margin: 0 3px;
  }
  .cms-content-detail pre code {
    display: block;
  }

  /* ul ol 样式 */
  .cms-content-detail ul {
    margin: 10px 0 10px 10px;
    list-style: disc;
    list-style-position:inside;
  }
  .cms-content-detail ol {
    margin: 10px 0 10px 10px;
    list-style-type:decimal;
    list-style-position:inside;
  }
</style>
