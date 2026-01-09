<template>
  <section>
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" title="文章详情" @onBack="() => $router.go(-1)"></headNavigation>
    </van-sticky>
    <div class="content">
      <p class="title">{{allData.title}}</p>
      <p class="time">{{allData.updateTime}}</p>
      <div class="cms-content-detail" v-html="allData.content"></div>
    </div>
  </section>
</template>
<script>
import {getScreenshotPhoto} from '@/api/file.js'
import {contentDetail} from '@/api/cms.js'
import {Toast} from 'vant'
import Vue from 'vue'

Vue.use(Toast)

export default {
  activated () {
    const that = this
    if (that.$route.query && that.$route.query.id) {
      that.id = that.$route.query.id
      that.getInfo()
      window.scrollTo(0, 0)
    }
  },
  data () {
    return {
      id: '',
      allData: {},
      content: '',
      isIphone: Boolean(navigator.userAgent.match(/iphone|ipod|iOS/ig)), // 是否是苹果浏览器
      isIpad: Boolean(navigator.userAgent.match(/ipad/ig)) // 是否是 IPad 浏览器
    }
  },
  methods: {
    listenerAudio () {
      // 获取页面上所有的audio元素
      let audioElements = document.getElementsByTagName('audio')
      console.log('listenerAudio', audioElements)
      // 为每个audio元素添加点击事件监听器
      for (let i = 0; i < audioElements.length; i++) {
        audioElements[i].addEventListener('play', function () {
          // 暂停所有正在播放的audio
          console.log('addEventListener', this)
          for (let j = 0; j < audioElements.length; j++) {
            if (audioElements[j] !== this && !audioElements[j].paused) {
              audioElements[j].pause()
            }
          }
          // 播放当前点击的audio
          if (this.paused) {
            this.play()
          }
        })
      }
    },
    setVideoImage () {
      const videoList = document.getElementsByTagName('video')
      if (videoList && videoList.length > 0) {
        for (let i = 0; i < videoList.length; i++) {
          let video = videoList[i]
          video.muted = true
          video.setAttribute('x5-video-orientation', 'portraint')
          video.setAttribute('x-webkit-airplay', 'allow')
          video.setAttribute('x5-video-player-fullscreen', 'true')
          video.setAttribute('webkit-playsinline', 'true')
          video.loop = true
          video.playsinline = true
          if (!video.poster) {
            if (this.isIphone || this.isIpad) {
              if (video.src.indexOf('myhuaweicloud.com/cms/') > -1) {
                let scr = video.src
                let filename
                if (scr.indexOf('?') > -1) {
                  filename = scr.substring(video.src.indexOf('myhuaweicloud.com/cms/') + 22, video.src.indexOf('?') + 1)
                } else {
                  filename = scr.substring(video.src.indexOf('myhuaweicloud.com/cms/') + 22)
                }
                getScreenshotPhoto(filename).then(res => {
                  if (res.code === 0) {
                    if (res.data) {
                      video.poster = res.data
                    } else {
                      video.poster = 'https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png'
                    }
                  } else {
                    video.poster = 'https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png'
                  }
                })
                continue
              }
              video.poster = 'https://caring.obs.cn-north-4.myhuaweicloud.com/output/screenshotPhoto.png'
            } else {
              video.src += '#t=0.5'
            }
          }
          video.preload = 'auto'
        }
      }
    },
    getInfo () {
      contentDetail(this.id).then((res) => {
        if (res.code === 0) {
          if (res.data && res.data.link) {
            this.$previewWeiXinCms(res.data.link)
          } else {
            if (res.data) {
              this.allData = res.data
              console.log('contentDetail', res.data)
              this.$nextTick(function () {
                setTimeout(() => {
                  this.setVideoImage()
                  this.listenerAudio()
                }, 200)
              })
            } else {
              Toast('文章不存在')
              this.$router.go(-1)
            }
          }
        }
      })
    }
  }
}
</script>
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
    position: fixed;
    bottom: 0;
    left: 0;

    border-top: 1px solid rgba(102, 102, 102, 0.1);

    .inner {
      background: #fff;
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
      background: #fff;
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
  }
  .cms-content-detail div {
    width: 100%;
    word-wrap:break-word
  }
  .cms-content-detail p {
    font-size: 16px;
    color: #393939;
    text-align: justify;
  }
  .cms-content-detail span {
    font-size: 16px;
    color: #393939;
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
