<template>
  <section :class="isScrollContainer ? 'cms-scroll-container' : ''">
    <div v-if="showShareHint">
      <div
          style="position: fixed; background-color: black; opacity: 0.3; width: 100%; z-index: 900; height: 800px;"></div>
      <div style="width: 100%; text-align: center; position: fixed; z-index: 9999">
        <img :src="share_hint" style="width: 85%; padding-left: 15px;">
        <img :src="i_know" style="width: 35%; margin-top: 80px;" @click="closeShareHint()">
      </div>
    </div>
    <navBar :isCmsIndex="true" pageTitle="文章详情"></navBar>
    <div class="content" style="overflow-x: hidden; overflow-y: auto">
      <p class="title">{{ allData.title }}</p>
      <p class="time">{{ allData.updateTime }}</p>
      <div class="cms-content-detail" v-html="allData.content"></div>
      <!--<div>
        <video controls="controls"  style="width: 100%;">
          <source src="http://caring.obs.cn-north-4.myhuaweicloud.com/cms/111111111D1658219915004.mp4" type="video/mp4">
        </video>
      </div>-->
    </div>
    <div class="appraiseCont">
      <div class="appraiseTitle">
        <span>评论({{ allData.messageNum ? allData.messageNum : 0 }})</span>
        <span>{{ allData.hitCount ? allData.hitCount : 0 }}人阅读</span>
      </div>
      <template v-if="allData.contentReplies">
        <div class="item" v-for="(i,k) in allData.contentReplies" :key="k">
          <img :src="i.replierAvatar" alt="">
          <div class="itemCont">
            <div class="contentItem">
              <p class="name">{{ i.replierWxName }}</p>
              <div>
                <img :src="i.hasLike?gooder:good" style="width:17px;height:17px;border-radius:0px;margin-right:0px"
                     alt="" @click="setgoodsay(i)">
                <span style="line-height:20px;font-size:13px;color:#666">{{ i.likeCount }}</span>
              </div>
            </div>
            <p>{{ i.content }}</p>
          </div>
        </div>
      </template>
    </div>
    <div class="tipCover" v-if="isLogin && allData && allData.ownerType && allData.ownerType.code === 'TENANT' && !isComment">
      <div v-if="show" class="inner">
        <x-input v-show="allData.canComment === 1" @on-focus="show=!show" class="inputCont" style="font-size:12px"
                 size="mini"
                 placeholder="写评论..."></x-input>
        <div class="right">
          <img @click="saveBtn()" :src="allData.contentCollect?saveT:save" alt="">
          <img :src="share" alt="" @click="openShare()">
        </div>
      </div>
      <div v-if="!show" class="innerT">
        <div class="title">
          <span><img :src="close" alt="" @click="show=true"></span>
          <span>写评论</span>
          <span style="padding:0px 10px;border-radius:20px;color:#fff;font-size:14px"
                :style="{background:inputContent?'#3F86FF':'#ccc'}" @click="submit">提交</span>
        </div>
        <x-textarea class="textareaCont" :max="200" :rows="5" :cols="30" placeholder="写评论..."
                    v-model="inputContent"></x-textarea>
      </div>
    </div>
  </section>
</template>
<script>
import {XButton, XTextarea, XInput} from "vux";
import wx from 'weixin-js-sdk';
import Api from '@/api/Content.js'
import {getScreenshotPhoto} from '@/api/file.js'
import {ImagePreview} from "vant";

export default {
  components: {
    [XButton.name]: XButton,
    XInput,
    [XTextarea.name]: XTextarea,
    [ImagePreview.Component.name]: ImagePreview.Component,
    navBar: () => import('@/components/headers/navBar'),
  },
  created() {
    // 有消息ID，说明是点击模板消息打开的此页面。
    if (this.$route.query && this.$route.query.messageId) {
      this.openMessage(this.$route.query.messageId)
    }
    if (this.$route.query && this.$route.query.isComment) {
      this.isComment = true
    }

    if (this.$route.query && this.$route.query.isScrollContainer) {
      this.isScrollContainer = true
    }

    if (this.$route.query && this.$route.query.id) {
      this.id = this.$route.query.id
      this.getInfo()
      this.setgood()
    }
    if (!localStorage.getItem('userId')) {
      this.isLogin = false
    }

  },
  mounted() {

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
      isLogin: true,
      isComment: false,
      isIphone: Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig)), // 是否是苹果浏览器
      isIpad: Boolean(navigator.userAgent.match(/ipad/ig)), // 是否是 IPad 浏览器
      isLoading: false,
      isScrollContainer: false,
    }
  },
  methods: {
    saveBtn() {
      const params = {
        contentId: this.id,
        userId: localStorage.getItem('userId'),
        roleType: 'patient'
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
    /**
     * 当文章是被学习计划推送过来的时候，设置消息推送记录中的状态为文章被打开。
     * @param messageId
     */
    openMessage(messageId) {
      Api.annoReminderLogSubmitSuccess(messageId)
    },
    setgood() {
      const params = {
        id: this.id
      }
      Api.updateHitCount(params)
    },
    getInfo() {
      const params = {
        id: this.id,
        userId: localStorage.getItem('userId'),
        roleType: 'patient'
      }
      Api.channelContentWithReply(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data && res.data.data.link) {
            window.location.href = res.data.data.link;
            return
          }
          this.allData = res.data.data
          this.$nextTick(function(){
            this.setVideoImage()
            this.addImageClick()
            this.listenerAudio()
          });
          this.getwxSignature()
        }
      })
    },
    /**
     * 给文章内的image添加点击预览事件
     */
    addImageClick() {
      const images = document.querySelectorAll('img');
      if (images && images.length > 0) {
        for (let i = 0; i < images.length; i++) {
          images[i].addEventListener('click', function(event) {
            // 获取被点击图片的src属性
            const imageSrc = event.target.src;
            console.log('Clicked image URL:', imageSrc);
            ImagePreview({
              images: [imageSrc],
              closeable: true,
            });
          });
        }
      }
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
          video.setAttribute('x5-video-orientation', 'portraint');
          video.setAttribute('x-webkit-airplay', 'allow');
          video.setAttribute('x5-video-player-fullscreen', 'true');
          video.setAttribute('webkit-playsinline', 'true');
          video.loop = true;
          video.playsinline=true;
          if (!video.poster) {
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
            } else {
              video.poster ="https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png";
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
      if (this.isLoading) {
        return;
      }
      this.isLoading = true
      const params = {
        contentId: this.id,
        replierId: localStorage.getItem('userId'),
        content: this.inputContent,
        roleType: "patient"
      }
      Api.contentReply(params).then((res) => {
        if (res.data.code === 0) {
          // this.allData = res.data.data
          this.inputContent = undefined
          this.show = true
          this.isLoading = false
          this.getInfo()
        }
      }).catch(() => {
        this.isLoading = false
      })
    },
    setgoodsay(i) {
      const params = {
        replyId: i.id,
        userId: localStorage.getItem('userId'),
        operation: i.hasLike ? 2 : 1,
        roleType: "patient"
      }
      Api.contentReplylike(params).then((res) => {
        if (res.data.code === 0) {
          this.getInfo()
        }
      })
    },
    getwxSignature() {
      const that = this
      let url = location.href
      const params = {
        url: that.$getWxConfigSignatureUrl(),
        wxAppId: localStorage.getItem('wxAppId')
      }
      Api.annoWxSignature(params).then(res => {
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
            console.log('分享的文章标题', that.allData.title)
            wx.updateAppMessageShareData({
              title: that.allData.title, // 分享标题
              desc: that.allData.summary ? that.allData.summary : '', // 分享描述
              link: url, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
              imgUrl: that.allData.icon, // 分享图标
              success: function () {
                // 设置成功
                that.showShareHint = false
              }
            });
            wx.updateTimelineShareData({
              title: that.allData.title, // 分享标题
              desc: that.allData.summary ? that.allData.summary : '', // 分享描述
              link: url, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
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
  },
}
</script>

<style>

.cms-scroll-container {
  height: 100vh;
  overflow-y: scroll; /* 启用垂直滚动 */
}


.cms-scroll-container::-webkit-scrollbar {
  width: 12px;
}

.cms-scroll-container::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 10px;
}
</style>

<style lang="less" scoped>

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
  margin-bottom: 60px;

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
  background-color: white;
}
.cms-content-detail div {
  width: 100%;
  word-wrap:break-word
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
}

.cms-content-detail ol {
  margin: 10px 0 10px 10px;
}

.cms-content-detail img {
  max-width: 100%;
}

</style>
