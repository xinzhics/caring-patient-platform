<template>
  <div style="position: relative">
    <van-sticky>
      <headNavigation leftIcon="arrow-left"  title="文章详情" @onBack="goback"></headNavigation>
    </van-sticky>
      <van-loading size="24px" v-if="loading">加载中...</van-loading>
      <div class="content" v-else>
        <p class="title">{{ content.title }}</p>
        <p class="time">{{ content.updateTime }}</p>
        <div style="margin-bottom: 50px" class="cms-content-detail" v-html="content.content"></div>
      </div>
    <div v-if="localMessageId" style="width: 100%;display: flex;justify-content: center;background: #ffffff; padding-top: 8px; padding-bottom: 8px; position: fixed;bottom: 0" >
      <van-button @click="submit" style="width: 217px" round type="info">发送</van-button>
    </div>
    <Dialog @showPop="showPop" :localMessageId="localMessageId" :submit-message="submitMessage" :show="dialogShow" :people-number="peopleNumber"></Dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import Dialog from './Dialog'
import {Col, Row, Icon, Checkbox, CheckboxGroup, Sticky, Loading, Button, Toast} from 'vant'
import { contentDetail } from '@/api/cms.js'
import {getScreenshotPhoto} from '@/api/file.js'

Vue.use(Icon)
Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Button)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Loading)
export default {
  components: {
    Dialog
  },
  data () {
    return {
      nursingId: localStorage.getItem('caringNursingId'),
      peopleNumber: 0,
      loading: true,
      dialogShow: false,
      submitMessage: {},
      contentId: this.$route.query.contentId,
      content: {},
      localMessageId: this.$route.query.localMessageId // 本地设置的群发信息的缓存 key
    }
  },
  mounted () {
    this.contentDetail()
  },
  methods: {
    contentDetail () {
      contentDetail(this.contentId).then(res => {
        if (res.data) {
          this.content = res.data
          this.$nextTick(function () {
            setTimeout(() => {
              this.setVideoImage()
              this.listenerAudio()
            }, 200)
          })
          this.loading = false
        } else {
          Toast('文章不存在')
          this.$router.go(-1)
        }
      })
    },
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
    submit () {
      const localMessageJSONString = localStorage.getItem(this.localMessageId)
      const localMessage = JSON.parse(localMessageJSONString)
      const params = {}
      this.peopleNumber = localMessage.peopleNumber
      params.receiverId = localMessage.receiverId
      params.senderId = this.nursingId
      params.type = 'cms'
      let cms = {
        id: this.content.id,
        title: this.content.title,
        icon: this.content.icon,
        summary: this.content.summary
      }
      params.content = JSON.stringify(cms)
      this.submitMessage = params
      console.log('submitMessage', this.submitMessage)
      this.dialogShow = true
    },
    showPop () {
      this.dialogShow = false
    },
    goback () {
      if (this.localMessageId) {
        this.$router.replace({
          path: '/newsDispatch/content',
          query: {
            localMessageId: this.localMessageId,
            active: 2
          }
        })
      } else {
        this.$router.replace({
          path: '/newsDispatch'
        })
      }
    }
  }
}
</script>

<style scoped>
.top-box {
  /*display: flex;*/
  /*justify-content: space-between;*/
  padding: 48px 13px 13px 13px;
  color: #333;
  font-size: 19px;
  line-height: 1;
  background: #fff;
  text-align: center;
}

.content {
  padding: 20px 15px;
  background: #fff;
}

.content .title {
  font-size: 18px;
  font-weight: 800;
  color: rgb(52, 52, 52);
}

.content .time {
  font-size: 13px;
  line-height: 20px;
  color: #999;
  margin-top: 5px;
  margin-bottom: 20px;
}

.cms-content-detail {
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
  list-style-position: inside;
}

.cms-content-detail ol {
  margin: 10px 0 10px 10px;
  list-style-type: decimal;
  list-style-position: inside;
}
</style>
