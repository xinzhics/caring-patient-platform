<template>
  <div class="add-video-cms">
    <!-- 顶部导航栏 -->
    <div class="header">
      <van-icon v-if="!cmsId" name="cross" class="close-btn" @click="goBack"/>
      <van-icon v-else name="arrow-left" class="close-btn" @click="goBack"/>
      <span class="title">{{ cmsId ? '' : '添加' }}视频</span>
      <van-button v-if="!cmsId" class="save-draft-btn" plain @click="saveDraft">保存草稿</van-button>
      <div v-else></div>
    </div>

    <!-- 内容区域 -->
    <div class="content">
      <!-- 标题输入框 -->
      <van-field
          v-model="cmsVideo.cmsTitle"
          placeholder="请输入视频标题"
          class="title-input"
          :border="false"
          maxlength="50"
      />

      <!-- 视频上传区域 -->
      <div class="video-section">
        <div>
          <div class="section-label">视频</div>
          <div class="format-tips">MP4、AVI、MOV、MKV、WMV 格式，小于200MB</div>
        </div>
        <div v-if="!cmsVideo.cmsFileUrl" class="video-upload-area">

          <van-uploader :before-read="handleFileSelect" accept="video/*">
            <div class="upload-placeholder">
              <img src="../../assets/cms_tip_add.png" style="width: 28px;"/>
              <div class="upload-text">点击添加视频</div>
            </div>
          </van-uploader>
        </div>
        <div v-else class="video-preview">
          <div class="video-player" style="background: #E8E8E8; border-radius: 12px">
            <video
                ref="videoPlayer"
                :src="cmsVideo.cmsFileUrl"
                class="video-player"
                :poster="cmsVideo.cmsVideoCover"
                @play="onVideoPlay"
                @pause="onVideoPause"
                @ended="onVideoEnded"
                @click="toggleVideoPlay"
            ></video>
            <!-- 悬浮播放按钮 -->
            <div v-if="!isPlaying" class="play-overlay" @click="toggleVideoPlay">
              <img src="@/assets/cms_video_play.png" alt="播放" class="play-btn-icon"/>
            </div>

            <div class="delete-btn">
              <van-uploader :before-read="handleFileSelect" accept="video/*">
                <img src="../../assets/cms_video_change.png" style="width: 18px"/>
                修改
              </van-uploader>
            </div>

          </div>

          <!-- 视频信息 -->
          <div class="video-info">
            <div class="video-name">{{ cmsVideo.cmsFileTitle }}</div>
            <div class="video-size">{{ formatFileSize(cmsVideo.cmsFileSize) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部发布按钮 -->
    <div class="footer">
      <van-button
          type="primary"
          class="publish-btn"
          @click="saveCms"
          :loading="loadingState">
        {{ releaseStatus == 1 ? '保存' : '发布内容' }}
      </van-button>
    </div>

    <!-- 上传进度弹窗 -->
    <van-overlay :show="showUploadProgress" :lock-scroll="true" z-index="2000">
      <div class="upload-progress-popup" @click.stop>
        <div class="upload-progress-header">
          <span class="upload-progress-title"></span>
<!--          <van-icon name="cross" class="upload-progress-close" @click="cancelUpload"/>-->
        </div>

        <div class="upload-progress-content">
          <div class="upload-progress-bar-container">
            <div class="upload-progress-bar">
              <div
                  class="upload-progress-bar-fill"
                  :style="{ width: uploadProgress + '%' }"
              ></div>
            </div>
          </div>

          <div class="upload-file-info">
            <div class="upload-file-size"> 上传中...</div>
          </div>
        </div>
      </div>
    </van-overlay>
  </div>
</template>

<script>
import {
  getCmsInfo,
  saveTextCms,
  putTextCms
} from '@/api/cms'
import { createScreenshotTaskAndReturnCover, getHuaweiObsPath } from '@/api/file'
import Vue from 'vue'
import { uploadHwFile } from '@/utils/hwOss'
import { Toast, Dialog, Overlay, Field, Uploader, Button } from 'vant'
Vue.use(Overlay)
Vue.use(Toast)
Vue.use(Field)
Vue.use(Uploader)
Vue.use(Button)
export default {
  name: 'VideoCmsEditor',
  data () {
    return {
      cmsId: this.$route.query.cmsId || '',
      releaseStatus: 0,
      selectFile: null,
      showUploadProgress: false,
      uploadProgress: 0,
      uploadFileName: '',
      uploadFileSize: 0,
      isPlaying: false,
      cmsVideo: {
        id: this.$route.query.cmsId || '',
        cmsTitle: '',
        cmsType: 'CMS_TYPE_VIDEO',
        cmsFileUrl: '',
        cmsFileTitle: '',
        cmsFileSize: 0,
        cmsFileType: '',
        cmsVideoCover: '',
        doctorId: this.$route.query.doctorId, // 假设 user 模块在 Vuex 中
        releaseStatus: 0
      },
      loadingState: false,
      hwIv: '',
      hwConfig: ''
    }
  },

  mounted () {
    if (this.cmsId) {
      this.queryCmsDetail()
    }
    getHuaweiObsPath().then(res => {
      const data = res.data
      this.hwIv = data.IV
      this.hwConfig = data.result
    })
  },

  methods: {
    goBack () {
      this.$router.go(this.cmsId ? -2 : -1)
    },

    selectVideo () {
      console.log('selectVideo')
      this.$refs['fileInput'].click()
    },

    toggleVideoPlay () {
      const player = this.$refs.videoPlayer
      if (!player) return

      if (this.isPlaying) {
        player.pause()
      } else {
        player.play().catch(err => {
          console.error('视频播放失败:', err)
          Toast('视频播放失败')
        })
      }
    },

    onVideoPlay () {
      this.isPlaying = true
    },

    onVideoPause () {
      this.isPlaying = false
    },

    onVideoEnded () {
      this.isPlaying = false
    },

    handleFileSelect (file) {
      const allowedTypes = [
        'video/mp4',
        'video/avi',
        'video/mov',
        'video/wmv',
        'video/mkv',
        'video/quicktime'
      ]

      if (!allowedTypes.includes(file.type)) {
        Toast('不支持的视频格式')
        return false
      }

      const maxSize = 200 * 1024 * 1024 // 200MB
      if (file.size > maxSize) {
        Toast('视频文件不能超过200MB')
        return false
      }

      this.cmsVideo.cmsFileTitle = file.name
      this.cmsVideo.cmsFileSize = file.size
      this.cmsVideo.cmsFileType = file.type.includes('/')
        ? file.type.split('/')[1]
        : file.type

      this.selectFile = file
      this.uploadVideo()
      return false
    },

    removeVideo () {
      const player = this.$refs.videoPlayer
      if (player && this.isPlaying) {
        player.pause()
      }

      this.cmsVideo.cmsFileUrl = ''
      this.cmsVideo.cmsFileTitle = ''
      this.cmsVideo.cmsFileSize = 0
      this.cmsVideo.cmsFileType = ''
      this.cmsVideo.cmsVideoCover = ''
      this.isPlaying = false

      // 重置 input
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    },

    formatFileSize (bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },

    cancelUpload () {
      this.showUploadProgress = false
      this.uploadProgress = 0
      this.loadingState = false
    },

    saveDraft () {
      if (!this.cmsVideo.cmsTitle.trim()) {
        Toast('请输入视频标题')
        return
      }
      if (!this.cmsVideo.cmsFileUrl) {
        Toast('请选择视频文件')
        return
      }

      if (this.loadingState) return

      this.loadingState = true
      this.cmsVideo.releaseStatus = 0

      const api = this.cmsId ? putTextCms : saveTextCms
      api(this.cmsVideo)
        .then(() => {
          Toast('保存成功')
          this.$router.go(this.cmsId ? -2 : -1)
        })
        .catch(() => {
          Toast('保存失败，请重试')
        })
        .finally(() => {
          this.loadingState = false
        })
    },

    saveCms () {
      if (!this.cmsVideo.cmsTitle.trim()) {
        Toast('请输入视频标题')
        return
      }
      if (!this.cmsVideo.cmsFileUrl) {
        Toast('请选择视频文件')
        return
      }

      if (this.loadingState) return

      this.loadingState = true
      this.cmsVideo.releaseStatus = 1

      const api = this.cmsId ? putTextCms : saveTextCms

      if (this.releaseStatus === 1) {
        api(this.cmsVideo)
          .then(() => {
            Toast('发布成功')
            this.$router.go(this.cmsId ? -2 : -1)
          })
          .catch(() => {
            Toast('发布失败，请重试')
          })
          .finally(() => {
            this.loadingState = false
          })
      } else {
        Dialog.confirm({
          title: '发布提醒',
          message: '内容发布完成后，患者即可查看',
          showCancelButton: true
        })
          .then(() => {
            api(this.cmsVideo)
              .then(() => {
                Toast('保存成功')
                this.$router.go(this.cmsId ? -2 : -1)
              })
              .catch(() => {
                Toast('保存失败，请重试')
              })
              .finally(() => {
                this.loadingState = false
              })
          })
          .catch(() => {
            this.loadingState = false
          })
      }
    },

    uploadVideo () {
      if (this.loadingState) return

      this.loadingState = true
      this.showUploadProgress = true
      this.uploadProgress = 0

      const that = this
      const progressCallback = (transferredAmount, totalAmount, totalSeconds) => {
        // 获取上传平均速率（KB/S）
        // console.log(transferredAmount * 1.0 / totalSeconds / 1024)
        // 获取上传进度百分比
        // console.log(transferredAmount * 100.0 / totalAmount)
        const speed = transferredAmount * 1.0 / totalSeconds / 1024
        const progress = transferredAmount * 100.0 / totalAmount
        let speedStr = speed + ''
        let progressStr = progress + ''
        if (speedStr.indexOf('.') > -1) {
          speedStr = speedStr.substring(0, speedStr.indexOf('.'))
        }
        if (progressStr.indexOf('.') > -1) {
          progressStr = progressStr.substring(0, progressStr.indexOf('.'))
        }
        if (progressStr === 100 || progressStr === '100') {
          that.uploadProgress = 99
          Toast('开始截取视频封面!')
        } else {
          that.uploadProgress = progressStr
        }
      }

      const resultCallback = (result, errorMessage) => {
        if (result) {
          that.cmsVideo.cmsFileUrl = result.fileHwUrl
          // 视频 需要去加载封面
          createScreenshotTaskAndReturnCover(result).then(res => {
            const coverData = res.data
            that.cmsVideo.cmsVideoCover = coverData.coverUrl
            that.uploadProgress = 100
            that.loadingState = false
            that.showUploadProgress = false
          })
        } else {
          that.showUploadProgress = false
          that.loadingState = false
          Toast(errorMessage)
        }
      }

      uploadHwFile(this.hwIv, this.hwConfig, this.selectFile, progressCallback, resultCallback)
    },

    queryCmsDetail () {
      this.loadingState = true
      try {
        getCmsInfo(this.cmsId).then(res => {
          const data = res.data
          this.cmsVideo.cmsTitle = data.cmsTitle || ''
          this.cmsVideo.cmsFileUrl = data.cmsFileUrl || ''
          this.cmsVideo.cmsFileTitle = data.cmsFileTitle || ''
          this.cmsVideo.cmsFileSize = data.cmsFileSize || 0
          this.cmsVideo.cmsFileType = data.cmsFileType || ''
          this.cmsVideo.cmsVideoCover = data.cmsVideoCover || ''
          this.cmsVideo.doctorId = data.doctorId || this.$store.state.user.userId
          this.cmsVideo.id = this.cmsId
          this.cmsVideo.releaseStatus = data.releaseStatus || 0
          this.releaseStatus = data.releaseStatus || 0
        })
      } catch (err) {
        Toast('获取视频信息失败')
      } finally {
        this.loadingState = false
      }
    }
  }
}
</script>

<style scoped>
.add-video-cms {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #FAFAFA;
}

/* 头部 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #FAFAFA;
  padding: 12px 16px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.close-btn {
  font-size: 20px;
  color: #333;
  cursor: pointer;
}

.title {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.save-draft-btn {
  font-size: 12px;
  color: #FFF;
  padding: 6px 12px;
  height: auto;
  background: #4562BA;
  border-radius: 16px;
}

/* 内容 */
.content {
  flex: 1;
  margin: 20px 15px 10px 15px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.title-input {
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0px 2px 26px 0px rgba(3, 3, 3, 0.03);
  min-height: 55px;
}

.title-input :deep(.van-field__control) {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.title-input :deep(.van-field__control::placeholder) {
  color: #c8c9cc;
}

/* 视频区域 */
.video-section {
  padding: 20px 16px 25px 16px;
  background: #FFF;
  margin-top: 15px;
  border-radius: 12px;
  box-shadow: 0px 2px 26px 0px rgba(3, 3, 3, 0.03);
}

.section-label {
  font-size: 16px;
  font-weight: 500;
  color: #1A1A1A;
}

.format-tips {
  font-size: 11px;
  color: #BABABA;
  margin-bottom: 16px;
}

.video-upload-area {
  border: 1px solid #DDDDDD;
  border-radius: 12px;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
  position: relative;
}

.video-upload-area:hover {
  border-color: #4562BA;
}

.upload-placeholder {
  display: flex;
  align-items: center;
  color: #969799;
  gap: 5px;
  padding: 30px;
}

.plus-icon {
  font-size: 20px;
}

.upload-text {
  font-size: 16px;
  color: #41464E;
}

.video-preview {
  width: 100%;
  height: 100%;
}

.video-player {
  width: 100%;
  height: 210px;
  object-fit: contain;
  cursor: pointer;
  position: relative;
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

.video-info {
  margin-top: 12px;
  padding: 0 8px;
}

.video-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
  word-break: break-all;
}

.video-size {
  font-size: 12px;
  color: #969799;
}

.delete-btn {
  position: absolute;
  bottom: 12px;
  right: 10px;
  font-size: 14px;
  color: #FFF;
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 20px;
  padding: 5px 10px;
  cursor: pointer;
}

/* 底部按钮 */
.footer {
  padding: 16px;
}

.publish-btn {
  width: 100%;
  height: 45px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  background: #4562BA;
  border: none;
  box-shadow: 0 2px 8px rgba(90, 125, 232, 0.3);
}

.publish-btn:active {
  transform: translateY(1px);
  box-shadow: 0 1px 4px rgba(90, 125, 232, 0.3);
}

/* 上传进度弹窗样式 */
.upload-progress-popup {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  background: #fff;
  border-radius: 16px 16px 0 0;
  padding: 0;
  z-index: 20;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
}

.upload-progress-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 20px 10px 20px;
}

.upload-progress-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.upload-progress-close {
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}

.upload-progress-content {
  padding: 20px 20px 40px 20px;
}

.upload-progress-bar-container {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0px 20px;
  margin-bottom: 16px;
}

.upload-progress-bar {
  flex: 1;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.upload-progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #4562BA 0%, #6B7FE8 100%);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.upload-file-info {
  text-align: center;
}

.upload-file-size {
  font-size: 14px;
  color: #999;
}

/deep/ .van-nav-bar .van-icon {
  color: #000000;
  font-size: 20px;
}
</style>
