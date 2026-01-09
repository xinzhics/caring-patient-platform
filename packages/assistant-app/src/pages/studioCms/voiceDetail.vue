<template>
  <div class="voice-detail">
    <!-- 顶部导航栏 -->
    <van-nav-bar
        title="音频"
        left-arrow
        @click-left="goBack"
    >
      <template slot="right">
        <img src="../../assets/cms_del.png" style="width: 18px; height: 18px;" @click="deleteCms"/>
      </template>
    </van-nav-bar>

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

    <!-- 底部操作按钮 -->
    <div class="footer-actions">
      <van-button
          class="edit-btn"
          v-if="!audioData.articleDataId"
          plain
          round
          @click="editContent"
      >
        修改内容
      </van-button>
      <van-button
          class="publish-btn"
          type="primary"
          round
          @click="publishNew"
      >
        再发布一篇
      </van-button>
    </div>
    <!-- 自定义添加选项弹窗 -->
    <van-overlay :show="showAddOptions" @click="showAddOptions = false">
      <div class="add-options-popup" @click.stop>
        <div class="add-options-grid">
          <div class="add-option-item" @click="onAddSelect({ value: 'video' })">
            <img src="../../assets/cms_add_video.png" style="width: 50px"/>
            <span class="add-option-text">添加视频</span>
          </div>
          <div class="add-option-item" @click="onAddSelect({ value: 'voice' })">
            <img src="../../assets/cms_add_audio.png" style="width: 48px; margin-bottom: 2px"/>
            <span class="add-option-text">添加音频</span>
          </div>
          <div class="add-option-item" @click="onAddSelect({ value: 'text' })">
            <img src="../../assets/cms_add_article.png" style="width: 48px; margin-bottom: 2px"/>
            <span class="add-option-text">添加文章</span>
          </div>
          <div v-if="articleImportUserId" class="add-option-item" @click="onAddSelect({ value: 'articleImport' })">
            <img src="../../assets/article_download_icon.png" style="width: 48px; margin-bottom: 2px"/>
            <span class="add-option-text">科普导入</span>
          </div>
        </div>
        <div class="add-options-cancel" @click="showAddOptions = false">
          取消
        </div>
      </div>
    </van-overlay>
  </div>
</template>

<script>
import { delCms, getCmsInfo, channelContentWithReply, checkArticleUserExist } from '@/api/cms'
import { doctorDetails } from '@/api/doctorApi'
import Vue from 'vue'
import { Toast, Dialog, NavBar, Button, Overlay } from 'vant'
Vue.use(Toast)
Vue.use(NavBar)
Vue.use(Button)
Vue.use(Overlay)
export default {
  name: 'CmsVoiceDetail',
  data () {
    return {
      audioData: {
        title: '',
        fileUrl: '',
        fileType: '',
        cmsFileRemark: '',
        fileSize: 0,
        fileName: ''
      },
      doctorMobile: '',
      isPlaying: false,
      currentAudio: null, // Audio 实例
      currentTime: 0,
      duration: 0,
      cmsId: this.$route.query.cmsId,
      doctorId: this.$route.query.doctorId,
      articleImportUserId: '',
      showAddOptions: false,
      addActions: [
        { name: '图文文章', value: 'text' },
        { name: '音频内容', value: 'voice' },
        { name: '视频内容', value: 'video' }
      ],
      windowHeight: window.innerHeight,
      navBarHeight: 46,
      footerHeight: 76,
      contentPadding: 30,
      commentCurrent: 1,
      commentSize: 50,
      contentReplies: [],
      loading: false,
      finished: false
    }
  },

  computed: {
    contentHeight () {
      return (
        this.windowHeight -
        this.navBarHeight -
        this.footerHeight -
        this.contentPadding
      )
    }
  },

  mounted () {
    if (!this.cmsId) {
      this.$router.replace('/studio/cms/list')
      return
    }
    this.queryCmsDetail()
    this.getInfo()
    window.addEventListener('resize', this.handleResize)
  },

  beforeDestroy () {
    // 清理音频资源
    if (this.currentAudio) {
      this.currentAudio.pause()
      this.currentAudio = null
    }
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    handleResize () {
      this.windowHeight = window.innerHeight
    },

    goBack () {
      this.$router.back()
    },

    getInfo () {
      doctorDetails(this.doctorId).then(res => {
        if (res.code === 0) {
          this.doctorMobile = res.data.mobile
          checkArticleUserExist(this.doctorMobile).then(res => {
            this.articleImportUserId = res.data
          })
        }
      })
    },

    formatTime (time) {
      if (!time || time <= 0) return '0:00'
      const minutes = Math.floor(time / 60)
      const seconds = Math.floor(time % 60)
      return `${minutes}:${seconds.toString().padStart(2, '0')}`
    },

    formatFileSize (bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },

    toggleAudio () {
      if (!this.audioData.fileUrl) {
        Toast('暂无音频文件')
        return
      }

      if (!this.currentAudio) {
        Toast('音频未加载完成')
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
        console.error('播放失败:', error)
        Toast('音频播放失败')
        this.isPlaying = false
      }
    },

    deleteCms () {
      const that = this
      Dialog.confirm({
        title: '确认删除',
        message: '确定要删除这条视频吗？',
        showCancelButton: true
      })
        .then(() => {
          delCms(this.cmsId)
          Toast('删除成功')
          that.$router.go(-1)
        })
    },

    queryCmsDetail () {
      const that = this
      try {
        getCmsInfo(this.cmsId).then(res => {
          const data = res.data
          that.audioData = {
            title: data.cmsTitle || '',
            fileUrl: data.cmsFileUrl || '',
            fileType: data.cmsFileType || '',
            fileSize: data.cmsFileSize || 0,
            fileName: data.cmsFileTitle || '',
            cmsFileRemark: data.cmsFileRemark || ''
          }

          // 如果已有 audio 实例，先清理
          if (that.currentAudio) {
            that.currentAudio.pause()
            that.currentAudio = null
          }

          // 创建新的 Audio 实例
          if (that.audioData.fileUrl) {
            const audio = new Audio(that.audioData.fileUrl)

            audio.addEventListener('loadedmetadata', () => {
              that.duration = audio.duration || 0
            })

            audio.addEventListener('timeupdate', () => {
              that.currentTime = audio.currentTime || 0
            })

            audio.addEventListener('ended', () => {
              that.isPlaying = false
              that.currentTime = 0
            })

            audio.addEventListener('error', () => {
              Toast('音频加载或播放失败')
              that.isPlaying = false
            })

            that.currentAudio = audio
          }
        })
      } catch (err) {
        console.error('获取音频详情失败:', err)
        Toast('获取内容失败')
      }
    },

    editContent () {
      this.$router.replace({
        path: '/studio/cms/addVoice',
        query: { cmsId: this.cmsId, doctorId: this.doctorId }
      })
    },

    publishNew () {
      this.showAddOptions = true
    },

    onAddSelect (action) {
      this.showAddOptions = false
      switch (action.value) {
        case 'text':
          this.$router.replace({
            path: '/studio/cms/addText',
            query: { doctorId: this.doctorId }
          })
          break
        case 'voice':
          this.$router.replace({
            path: '/studio/cms/addVoice',
            query: { doctorId: this.doctorId }})
          break
        case 'video':
          this.$router.replace({
            path: '/studio/cms/addVideo',
            query: { doctorId: this.doctorId }
          })
          break
        case 'articleImport':
          this.$router.replace({ path: '/studio/cms/articleImport', query: { articleUserId: this.articleImportUserId, doctorId: this.doctorId } })
      }
    },

    onLoad () {
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
      channelContentWithReply(params).then((res) => {
        if (res.code === 0) {
          const records = res.data && res.data.records ? res.data.records : []
          if (records && records.length > 0) {
            this.contentReplies = this.contentReplies.concat(records)
            this.commentCurrent = this.commentCurrent + 1
            this.loading = false
            if (res.data && this.commentCurrent > res.data.pages) {
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

/* 内容容器 */
.content-container {
  padding: 5px 12px 0px 12px; /* 增加底部padding为底部按钮留出空间 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
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
  font-size: 16px;
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
  min-height: 55px;
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
  padding-bottom: 6px;
}

.audio-name {
  font-size: 14px;
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

/* 自定义添加选项弹窗样式 */
.add-options-popup {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  background: white;
  border-radius: 16px 16px 0 0;
  padding: 24px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.1);
}

.add-options-grid {
  display: flex;
  justify-content: space-around;
  margin-bottom: 24px;
  margin-top: 20px;
}

.add-option-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.add-option-item:active {
  transform: scale(0.95);
}

.add-option-text {
  font-size: 14px;
  color: #1A1A1A;
  font-weight: 500;
  text-align: center;
}

.add-options-cancel {
  text-align: center;
  padding: 16px;
  font-size: 16px;
  color: #1A1A1A;
  font-weight: 500;
  cursor: pointer;
  border-top: 1px solid #f0f0f0;
  margin: 0 -24px -24px -24px;
  border-radius: 0 0 16px 16px;
  transition: background-color 0.2s ease;
}

.add-options-cancel:hover {
  background-color: #f8f9fa;
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
  line-height: 15px;
}

.appraiseCont .item img {
  width: 30px;
  height: 30px;
  margin-right: 10px;
  border-radius: 4px;
}

/deep/ .van-nav-bar .van-icon {
  color: #000;
  font-size: 20px;
}
</style>
