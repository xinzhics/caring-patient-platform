<template>
  <div class="video-detail">
    <!-- 顶部导航栏 -->
    <van-nav-bar
        title="视频"
        left-arrow
        @click-left="goBack"
    >
    </van-nav-bar>

    <!-- 视频内容区域 -->
    <div class="content-container">
      <div class="video-content">
        <!-- 视频标题 -->
        <div class="title">
          {{ videoData.cmsTitle }}
        </div>

        <!-- 视频播放区域 -->
        <div class="video-player-container">
          <video
              ref="videoPlayer"
              v-if="videoData.cmsFileUrl"
              :src="videoData.cmsFileUrl"
              :poster="videoData.cmsVideoCover ? videoData.cmsVideoCover : ''"
              class="video-player"
              preload="metadata"
              @play="onVideoPlay"
              @pause="onVideoPause"
              @ended="onVideoEnded"
              @click="toggleVideoPlay"
          >
            您的浏览器不支持视频播放
          </video>
          <div v-else class="video-placeholder">
            <img src="../../assets/cms_add_video.png" class="placeholder-icon"/>
            <div class="placeholder-text">暂无视频内容</div>
          </div>

          <!-- 悬浮播放按钮 -->
          <div v-if="!isPlaying" class="play-overlay" @click="toggleVideoPlay">
            <img src="@/assets/cms_video_play.png" alt="播放" class="play-btn-icon"/>
          </div>
        </div>

      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="footer-actions">
      <van-button
          class="edit-btn"
          plain
          round
          @click="importData(0)"
      >
        导入到草稿
      </van-button>
      <van-button
          class="publish-btn"
          type="primary"
          round
          @click="importData(1)"
      >
        导入并发布
      </van-button>
    </div>
  </div>
</template>

<script>
  import { saveTextCms } from '@/api/cms'
  import { getVoiceTask } from '@/api/article'
  import Vue from 'vue';
  import { Toast, Dialog } from 'vant';
  Vue.use(Toast);
  export default {
    name: 'ArticleVideoDetail',
    data() {
      return {
        videoData: {
          cmsTitle: '',
          cmsFileUrl: '',
          cmsFileSize: 0,
          fileName: '',
          cmsVideoCover: ''
        },
        isPlaying: false,
        windowHeight: window.innerHeight,
        navBarHeight: 46,
        footerHeight: 76,
        contentPadding: 30,
        showAddOptions: false,
        loadingState: false,
        takeId: this.$route.query.takeId
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
      this.queryCmsDetail()
      window.addEventListener('resize', this.handleResize)
    },

    beforeDestroy() {
      window.removeEventListener('resize', this.handleResize)
    },

    methods: {
      handleResize() {
        this.windowHeight = window.innerHeight
      },
      goBack() {
        this.$router.back()
      },

      toggleVideoPlay() {
        const video = this.$refs.videoPlayer
        if (!video) return

        if (this.isPlaying) {
          video.pause()
        } else {
          video.play().catch((error) => {
            console.error('视频播放失败:', error)
            Toast('视频播放失败')
          })
        }
      },

      onVideoPlay() {
        this.isPlaying = true
      },

      onVideoPause() {
        this.isPlaying = false
      },

      onVideoEnded() {
        this.isPlaying = false
      },

      deleteCms() {
        const that = this
        Dialog.confirm({
          title: '确认删除',
          message: '确定要删除这条视频吗？',
          showCancelButton: true
        })
          .then(() => {
            delCms(this.cmsId)
            Toast('删除成功');
            that.$router.go(-1)
          })
      },

      queryCmsDetail() {
        const that = this
        try {
          getVoiceTask(this.takeId).then(res => {
            const data = res.data.data
            const fileType = data.generateAudioUrl.substring(data.generateAudioUrl.lastIndexOf(".") + 1)
            that.videoData = {
              cmsTitle: data.taskName || '',
              cmsFileUrl: data.generateAudioUrl || '',
              cmsFileType: fileType || '',
              cmsFileSize: 0,
              cmsVideoCover: data.humanVideoCover || '',
            }
          })
        } catch (err) {
          console.error('获取视频详情失败:', err)
          Toast('获取内容失败')
        }
      },

      importData(type) {
        if (this.loadingState) {
          return
        }
        this.loadingState = true
        this.videoData.cmsType = 'CMS_TYPE_VIDEO'
        this.videoData.cmsFileTitle = this.videoData.cmsTitle
        this.videoData.doctorId = localStorage.getItem("caring_doctor_id")
        this.videoData.articleDataId = this.takeId
        if (type === 0) {
          this.videoData.releaseStatus = 0
        } else if (type === 1) {
          this.videoData.releaseStatus = 1
        }
        saveTextCms(this.videoData)
          .then(() => {
            this.loadingState = false
            Toast('导入成功')
            this.$router.go(-1)
          })
          .catch(() => {
            this.loadingState = false
            Toast('保存失败，请重试')
          })
      },

      formatFileSize(bytes) {
        if (bytes === 0) return '0 B'
        const k = 1024
        const sizes = ['B', 'KB', 'MB', 'GB']
        const i = Math.floor(Math.log(bytes) / Math.log(k))
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
      }
    }
  }
</script>


<style scoped>
.video-detail {
  min-height: 100vh;
  background-color: #FAFAFA;
  display: flex;
  flex-direction: column;
}

/* 内容容器 */
.content-container {
  flex: 1;
  padding: 5px 12px 80px 12px; /* 增加底部padding为底部按钮留出空间 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/deep/ .van-nav-bar .van-icon{
  color: #000000 !important;
  font-size: 20px;
}

.video-content {
  background-color: #fff;
  padding: 12px 15px;
  border-radius: 8px;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
}

/* 标题样式 */
.title {
  font-weight: 500;
  font-size: 16px;
  color: #1A1A1A;
  padding-bottom: 8px;
}

/* 视频播放器容器 */
.video-player-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
  min-height: 200px;
  position: relative;
}

.video-player {
  width: 100%;
  height: 100%;
  max-height: 300px;
  object-fit: contain;
}

/* 悬浮播放按钮 */
.play-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
  transition: all 0.3s ease;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}


.play-btn-icon {
  width: 60px;
  height: 60px;
}


.video-placeholder {
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

/* 视频信息 */
.video-info {
  padding: 12px 0;
  border-top: 1px solid #EBF2FF;
}

.video-name {
  font-size: 16px;
  color: #1A1A1A;
  font-weight: 500;
  margin-bottom: 4px;
  word-break: break-all;
}

.video-details {
  font-size: 12px;
  color: #46556B;
}

/* 自定义滚动条样式 */
.video-content::-webkit-scrollbar {
  width: 4px;
}

.video-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.video-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.video-content::-webkit-scrollbar-thumb:hover {
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
</style>
