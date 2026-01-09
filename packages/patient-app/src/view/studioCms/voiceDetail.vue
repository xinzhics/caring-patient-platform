<template>
  <div class="voice-detail">
    <div v-if="showShareHint">
      <div style="position: fixed; background-color: black; opacity: 0.3; width: 100%; z-index: 900; height: 100vh;"></div>
      <div style="width: 100%; text-align: center; position: fixed; z-index: 9999">
        <img :src="share_hint" style="width: 85%; padding-left: 15px;">
        <img :src="i_know" style="width: 35%; margin-top: 80px;" @click="closeShareHint()">
      </div>
    </div>
    <navBar pageTitle="音频"></navBar>

    <!-- 音频内容区域 -->
    <div class="content-container">
      <div class="voice-content">

        <!-- 音频标题 -->
        <div class="title">
          {{ audioData.title }}
        </div>

        <!-- 音频播放区域 -->
        <div class="audio-player-container">
          <div v-if="audioData.fileUrl" class="audio-player">
            <!-- 播放按钮 -->
            <div class="play-button" @click="toggleAudio">
              <!-- 播放图标 -->
              <svg v-if="!isPlaying" width="30" height="30" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="#41464E" stroke-width="2"/>
                <polygon points="10,8 16,12 10,16" fill="#41464E"/>
              </svg>
              <!-- 暂停图标 -->
              <svg v-else width="30" height="30" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="#666" stroke-width="2"/>
                <rect x="9" y="8" width="2" height="8" fill="#666"/>
                <rect x="13" y="8" width="2" height="8" fill="#666"/>
              </svg>
            </div>

            <!-- 进度条 -->
            <div class="progress-container">
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{ width: duration > 0 ? (currentTime / duration * 100) + '%' : '0%' }"
                ></div>
              </div>
            </div>

            <!-- 时间显示 -->
            <div class="time-display">
              {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
            </div>
          </div>

          <div v-else class="audio-placeholder">
            <img src="../../assets/cms_add_audio.png" class="placeholder-icon"/>
            <div class="placeholder-text">暂无音频内容</div>
          </div>
        </div>

        <!-- 音频信息 -->
        <div class="audio-info" v-if="audioData.cmsFileRemark">
          <div class="audio-name">{{ audioData.cmsFileRemark }}</div>
        </div>
      </div>
    </div>

    <div class="appraiseCont">
      <div class="appraiseTitle">
        <span>评论({{ contentReplies.length }})</span>
      </div>
      <van-list
        v-model="loading"
        :finished="finished"
        @load="onLoad"
      >
        <template v-if="contentReplies && contentReplies.length">
          <div class="item" v-for="(i,k) in contentReplies" :key="k">
            <img :src="i.replierAvatar" alt="">
            <div class="itemCont">
              <div class="contentItem">
                <p class="name">{{ i.replierName }}</p>
              </div>
              <p>{{ i.ccontent }}</p>
            </div>
          </div>
        </template>
      </van-list>
    </div>

    <div class="tipCover" v-if="isLogin && !isComment">
      <div v-if="show" class="inner">
        <x-input @on-focus="show=!show" class="inputCont" style="font-size:12px" size="mini" placeholder="写评论..."></x-input>
        <div class="right">
          <img @click="saveBtn()" :src="audioData.collectStatus == 1 ? saveT:save" alt="">
          <img :src="share" alt="" @click="openShare()">
        </div>
      </div>
      <div v-if="!show" class="innerT">
        <div class="title">
          <span><img :src="close" alt="" @click="show=true"></span>
          <span style="margin-left: 30px; font-size: 16px">写评论</span>
          <span style="padding:6px 15px;border-radius:20px;color:#fff;font-size:14px; text-align: center; display: flex; align-items: center" :style="{background:inputContent?'#3F86FF':'#ccc'}" @click="submit">提交</span>
        </div>
        <x-textarea class="textareaCont" :max="200" :rows="5" :cols="30" placeholder="写评论..." v-model="inputContent"></x-textarea>
      </div>
    </div>

  </div>
</template>


<script>
  import { getCmsInfo, studioContentReplySave, studioCollectSave, studioContentReplyPage } from '@/api/cms'
  import Vue from 'vue';
  import { Toast } from 'vant';
  import { PullRefresh, List } from 'vant'
  import { XTextarea, XInput } from 'vux'
  import Api from '@/api/Content.js'
  import wx from 'weixin-js-sdk'

  Vue.use(Toast);
  Vue.use(PullRefresh)
  Vue.use(List)
  export default {
    name: 'AudioDetail',
    components: {
      navBar: () => import('@/components/headers/navBar'),
      [XTextarea.name]: XTextarea,
      XInput,
    },
    data() {
      return {
        audioData: {
          title: '',
          fileUrl: '',
          fileType: '',
          cmsFileRemark: '',
          fileSize: 0,
          fileName: '',
          collectStatus: 0,
          icon: ''
        },
        isPlaying: false,
        currentAudio: null, // Audio 实例
        currentTime: 0,
        duration: 0,
        cmsId: this.$route.query.cmsId,
        doctorId: this.$route.query.doctorId, // 保留但未使用
        windowHeight: window.innerHeight,
        navBarHeight: 46,
        footerHeight: 76,
        contentPadding: 30,
        contentReplies: [],
        commentCurrent: 1,
        commentSize: 20,
        loading: false,
        finished: false,
        refreshing: false,
        show: true,
        inputContent: '',
        isLoading: false,
        isLogin: !!localStorage.getItem('userId'),
        isComment: false,
        close: require('@/assets/my/close.png'),
        save: require('@/assets/my/saveS.png'),
        saveT: require('@/assets/my/saveT.png'),
        share: require('@/assets/my/share.png'),
        share_hint: require('@/assets/my/share_hint.png'),
        i_know: require('@/assets/my/i_know.png'),
        showShareHint: false,
        cancollct: true,
        patientInfo: ''
      }
    },

    computed: {
      contentHeight() {
        return (
          this.windowHeight -
          this.navBarHeight -
          this.footerHeight -
          this.contentPadding
        )
      }
    },

    mounted() {
      window.addEventListener('resize', this.handleResize)

      if (!this.cmsId) {
        this.$router.replace('/home')
        return
      }
      this.queryCmsDetail()
      this.patientInfo = JSON.parse(localStorage.getItem('myallInfo'))
      this.getInfo()
      this.getwxSignature()
    },

    beforeDestroy() {
      window.removeEventListener('resize', this.handleResize)

      // 清理音频资源
      if (this.currentAudio) {
        this.currentAudio.pause()
        this.currentAudio = null
      }
    },

    methods: {
      handleResize() {
        this.windowHeight = window.innerHeight
      },

      goBack() {
        this.$router.back()
      },

      formatTime(time) {
        if (!time || time < 0) return '0:00'
        const minutes = Math.floor(time / 60)
        const seconds = Math.floor(time % 60)
        return `${minutes}:${seconds.toString().padStart(2, '0')}`
      },

      formatFileSize(bytes) {
        if (bytes === 0) return '0 B'
        const k = 1024
        const sizes = ['B', 'KB', 'MB', 'GB']
        const i = Math.floor(Math.log(bytes) / Math.log(k))
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
      },

      toggleAudio() {
        if (!this.audioData.fileUrl) {
          Toast('暂无音频文件')
          return
        }

        if (!this.currentAudio) {
          Toast('音频尚未加载完成')
          return
        }

        try {
          if (this.isPlaying) {
            this.currentAudio.pause()
            this.isPlaying = false
          } else {
            this.currentAudio.play()
            this.isPlaying = true
          }
        } catch (error) {
          console.error('音频播放失败:', error)
          Toast('音频播放失败')
          this.isPlaying = false
        }
      },

      queryCmsDetail() {
       getCmsInfo(this.cmsId).then(res => {
         const data = res.data.data
         this.audioData = {
           title: data.cmsTitle || '',
           fileUrl: data.cmsFileUrl || '',
           fileType: data.cmsFileType || '',
           fileSize: data.cmsFileSize || 0,
           fileName: data.cmsFileTitle || '',
           cmsFileRemark: data.cmsFileRemark || '',
           collectStatus: data.collectStatus || 0,
           icon: data.cmsIcon || ''
         }
         // 如果已有实例，先清理
         if (this.currentAudio) {
           this.currentAudio.pause()
           this.currentAudio = null
         }
         // 创建新的 Audio 实例并绑定事件
         if (this.audioData.fileUrl) {
           const audio = new Audio(this.audioData.fileUrl)

           audio.addEventListener('loadedmetadata', () => {
             this.duration = audio.duration || 0
           })

           audio.addEventListener('timeupdate', () => {
             this.currentTime = audio.currentTime || 0
           })

           audio.addEventListener('ended', () => {
             this.isPlaying = false
             this.currentTime = 0
           })

           audio.addEventListener('error', () => {
             Toast('音频加载或播放失败')
             this.isPlaying = false
           })
           this.currentAudio = audio
         }
       })
      }
    ,
    getInfo() {
      this.finished = false
      this.loading = true
      const params = {
        current: this.commentCurrent,
        size: this.commentSize,
        model: {
          ccmsId: this.cmsId
        }
      }
      studioContentReplyPage(params).then((res) => {
        if (res.data.code === 0) {
          const records = res.data.data && res.data.data.records ? res.data.data.records : []
          this.contentReplies = records
          this.commentCurrent = this.commentCurrent + 1
          this.loading = false
          if (res.data.data && this.commentCurrent > res.data.data.pages) {
            this.finished = true
          }
        } else {
          this.loading = false
        }
      }).catch(() => {
        this.loading = false
      })
    },
    onLoad() {
      if (this.refreshing) {
        this.refreshing = false
      }
      if (this.finished) {
        this.loading = false
        return
      }
      const params = {
        current: this.commentCurrent,
        size: this.commentSize,
        model: {
          ccmsId: this.cmsId
        }
      }
      studioContentReplyPage(params).then((res) => {
        if (res.data.code === 0) {
          const records = res.data.data && res.data.data.records ? res.data.data.records : []
          if (records && records.length > 0) {
            this.contentReplies = this.contentReplies.concat(records)
            this.commentCurrent = this.commentCurrent + 1
            this.loading = false
            if (res.data.data && this.commentCurrent > res.data.data.pages) {
              this.finished = true
            }
          } else {
            this.finished = true
            this.loading = false
          }
        } else {
          this.loading = false
        }
      }).catch(() => {
        this.loading = false
      })
    },
    onRefresh() {
      this.finished = false
      this.commentCurrent = 1
      this.contentReplies = []
      this.loading = true
      this.onLoad()
    },
    submit() {
      if (!this.inputContent) {
        return
      }
      if (this.isLoading) {
        return
      }
      this.isLoading = true
      const params = {
        auditStatus: 1,
        ccmsId: this.cmsId,
        creplierId: localStorage.getItem('userId'),
        ccontent: this.inputContent,
        replierAvatar: this.patientInfo && this.patientInfo.avatar ? this.patientInfo.avatar : '',
        replierName: this.patientInfo && this.patientInfo.name ? this.patientInfo.name : ''
      }
      studioContentReplySave(params).then((res) => {
        if (res.data.code === 0) {
          this.inputContent = undefined
          this.show = true
          this.isLoading = false
        } else {
          this.isLoading = false
        }
        this.onRefresh()
      }).catch(() => {
        this.isLoading = false
      })
    },
    saveBtn() {
      const params = {
        cmsId: this.cmsId,
        userId: localStorage.getItem('userId'),
        collectStatus: this.audioData.collectStatus == 1 ? 0 : 1
      }
      studioCollectSave(params).then((res) => {
        if (res.data.code === 0) {
          this.audioData.collectStatus = this.audioData.collectStatus == 1 ? 0 : 1
        }
      })
    },
    openShare() {
      this.showShareHint = true
    },
    closeShareHint() {
      this.showShareHint = false
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
            debug: false,
            appId: localStorage.getItem('wxAppId'),
            timestamp: res.data.data.timestamp,
            nonceStr: res.data.data.nonceStr,
            signature: res.data.data.signature,
            jsApiList: [
              'updateTimelineShareData',
              'updateAppMessageShareData',
            ]
          });
          wx.ready(function () {
            wx.updateAppMessageShareData({
              title: that.audioData.title,
              desc: '',
              link: url,
              imgUrl: that.audioData.icon,
              success: function () {
                that.showShareHint = false
              }
            });
            wx.updateTimelineShareData({
              title: that.audioData.title,
              desc: '',
              link: url,
              imgUrl: that.audioData.icon,
              success: function () {
                that.showShareHint = false
              }
            })
          });
        }
      })
    }
    }
  }
</script>

<style scoped>
.voice-detail {
  min-height: 100vh;
  background-color: #FAFAFA;
  display: flex;
  flex-direction: column;
}

/* 导航栏样式 */
:deep(.van-nav-bar) {
  background: #FAFAFA;
  border-bottom: 0px;
  height: 46px;
  flex-shrink: 0;
}

:deep(.van-nav-bar__title) {
  font-size: 18px;
  font-weight: 500;
  color: #323233;
}

:deep(.van-nav-bar .van-icon) {
  color: #000000;
  font-size: 20px;
}

:deep(.van-hairline--bottom:after) {
  border-bottom-width: 0px;
}

/* 内容容器 */
.content-container {
  padding: 5px 12px 0px 12px; /* 增加底部padding为底部按钮留出空间 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
}

.voice-content {
  background-color: #fff;
  padding: 12px 15px;
  border-radius: 8px;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  height: 100%; /* 占满父容器高度 */
}

/* 标题样式 */
.title {
  font-weight: 500;
  font-size: 20px;
  color: #1A1A1A;
  padding-bottom: 8px;
}

/* 音频播放器容器 */
.audio-player-container {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 40px;
  overflow: hidden;
  margin-bottom: 16px;
  padding: 10px 16px;
}

.audio-player {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 400px;
}

.play-button {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
  flex-shrink: 0;
}

.progress-container {
  flex: 1;
  display: flex;
  align-items: center;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background-color: #e0e0e0;
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #4562BA;
  transition: width 0.1s ease;
}

.time-display {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  flex-shrink: 0;
}

.audio-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #969799;
  padding: 40px 20px;
}

.placeholder-icon {
  width: 60px;
  height: 60px;
  margin-bottom: 12px;
  opacity: 0.6;
}

.placeholder-text {
  font-size: 14px;
  color: #969799;
}

/* 音频信息 */
.audio-info {
  padding-bottom: 6px 0 12px 0;
}

.audio-name {
  font-size: 16px;
  color: #1A1A1A;
  font-weight: 500;
  margin-bottom: 4px;
  word-break: break-all;
}

.audio-details {
  font-size: 12px;
  color: #46556B;
}

/* 自定义滚动条样式 */
.voice-content::-webkit-scrollbar {
  width: 4px;
}

.voice-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.voice-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.voice-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 底部操作按钮 */
.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 12px 15px 12px;
  display: flex;
  gap: 12px;
  background-color: #FAFAFA;
  z-index: 100;
}

.edit-btn {
  flex: 1;
  height: 44px;
  border: 1px solid #4562BA;
  color: #4562BA;
}

.publish-btn {
  flex: 1;
  height: 44px;
  background-color: #4562BA;
  border: none;
}

:deep(.van-button--round) {
  border-radius: 22px;
}

:deep(.van-button__content) {
  font-size: 16px;
  font-weight: 500;
}
.tipCover {
  width: 94vw;
  padding: 10px 3vw 20px 3vw;
  background: #fff;
  position: fixed;
  bottom: 0;
  left: 0;
  border-top: 1px solid rgba(102, 102, 102, 0.1);
  z-index: 200;
}

.tipCover .inner {
  display: block;
  justify-content: space-between;
}

.tipCover .inner .inputCont {
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
}

.right img {
  vertical-align: middle;
  width: 20px;
  margin-right: 2vw;
  margin-top: 10px;
}

.tipCover .innerT {
  padding-bottom: 10px;
}

.tipCover .innerT .title {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
  align-items: center;
}

.tipCover .innerT .title span img {
  width: 10px;
  vertical-align: middle;
}

.tipCover .innerT .textareaCont {
  border: 1px solid rgba(102, 102, 102, 0.1);
}

.appraiseCont {
  margin: 10px;
  margin-bottom: 70px;
}

.appraiseCont .appraiseTitle {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  line-height: 30px;
  color: #999;
}

.appraiseCont .item {
  display: flex;
  justify-content: flex-start;
  line-height: 16px;
  font-size: 12px;
  padding: 10px 0px;
}

.appraiseCont .item .itemCont {
  width: 100%;
}

.appraiseCont .item .itemCont .contentItem {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.appraiseCont .item .itemCont .contentItem .name {
  margin: 0px !important;
  color: #999;
  margin-bottom: 6px;
  font-size: 13px;
  line-height: 20px;
}

.appraiseCont .item img {
  width: 30px;
  height: 30px;
  margin-right: 10px;
  border-radius: 4px;
}
</style>
