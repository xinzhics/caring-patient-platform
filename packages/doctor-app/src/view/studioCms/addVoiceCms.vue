<template>
  <div class="add-voice-page">
    <!-- 顶部导航栏 -->
    <div class="header">
      <van-icon v-if="!cmsId" name="cross" class="close-btn" @click="goBack"/>
      <van-icon v-else name="arrow-left" class="close-btn" @click="goBack"/>
      <span class="title">{{ cmsId ? '' : '添加' }}音频</span>
      <van-button v-if="!cmsId" class="save-draft-btn" plain @click="saveDraft">保存草稿</van-button>
    </div>

    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 标题输入 -->
      <div class="title-section">
        <van-field
          v-model="cmsVoice.cmsTitle"
          type="text"
          placeholder="请输入音频标题"
          class="title-input"
          maxlength="50"
        />
      </div>

      <!-- 音频上传区域 -->
      <div class="audio-section">
        <div class="section-label">
          <span>音频</span>
          <span class="file-info">MP3格式，小于100MB</span>
        </div>

        <div class="audio-upload-area" v-if="!cmsVoice.cmsFileUrl">
          <van-uploader :before-read="handleAudioSelect" accept="audio/mp3">
            <div class="upload-placeholder">
              <div class="upload-icon">
                <img src="../../assets/cms_tip_add.png" style="width: 100%"/>
              </div>
              <span class="upload-text">点击添加音频</span>
            </div>
          </van-uploader>
        </div>

        <!-- 音频预览区域 -->
        <div class="audio-preview" v-if="cmsVoice.cmsFileUrl">
          <div class="audio-card">
            <div class="audio-icon" @click="toggleAudio">
              <!-- 播放按钮 -->
              <svg v-if="!isPlaying" width="24" height="24" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="#41464E" stroke-width="2"/>
                <polygon points="10,8 16,12 10,16" fill="#41464E"/>
              </svg>
              <!-- 暂停按钮 -->
              <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none">
                <circle cx="12" cy="12" r="10" stroke="#666" stroke-width="2"/>
                <rect x="9" y="8" width="2" height="8" fill="#666"/>
                <rect x="13" y="8" width="2" height="8" fill="#666"/>
              </svg>
            </div>
            <div class="audio-info">
              <div class="audio-title">上传的音频</div>
            </div>
            <van-uploader :before-read="handleAudioSelect" accept="audio/mp3">
              <button class="edit-btn">
                <van-icon name="replay" size="18px" color="#41464E"/>
                修改
              </button>
            </van-uploader>
          </div>
        </div>
      </div>

      <!-- 描述输入 -->
      <div class="description-section">
        <textarea
          v-model="cmsVoice.cmsFileRemark"
          placeholder="请输入音频描述"
          class="description-input"
        ></textarea>
      </div>
    </div>

    <!-- 底部发布按钮 -->
    <div class="bottom-action">
      <button
        class="publish-btn"
        :class="{ 'loading': loadingState }"
        @click="saveCms"
        :disabled="loadingState"
      >
        {{ releaseStatus == 1 ? '保存' : '发布内容' }}
      </button>
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
  putTextCms,
} from '@/api/cms'
import Vue from 'vue';
import {getHuaweiObsPath} from '@/api/file'
import {Toast, Dialog} from 'vant';
import {uploadHwFile} from '@/service/hwOss'

Vue.use(Toast);
export default {
  name: 'VoiceCmsEditor',
  data() {
    return {
      cmsId: this.$route.query.cmsId || '',
      selectFile: null,
      uploadTask: null,
      isPlaying: false,
      currentAudio: null,
      uploadProgress: 0,
      showUploadProgress: false,
      releaseStatus: 0,
      cmsVoice: {
        id: this.$route.query.cmsId || '',
        cmsTitle: '',
        cmsType: 'CMS_TYPE_VOICE',
        cmsFileUrl: '',
        cmsFileTitle: '',
        cmsFileSize: 0,
        cmsFileType: '',
        cmsFileRemark: '',
        doctorId: localStorage.getItem("caring_doctor_id"),
        releaseStatus: 0
      },
      loadingState: false,
      hwIv: '',
      hwConfig: ''
    }
  },

  computed: {
    canPublish() {
      return this.cmsVoice.cmsTitle.trim() && this.cmsVoice.cmsFileUrl
    }
  },

  mounted() {
    if (this.cmsId) {
      this.queryCmsDetail()
    }
    getHuaweiObsPath().then(res => {
      const data = res.data.data
      this.hwIv = data.IV
      this.hwConfig = data.result
    })
  },
  beforeDestroy() {
    // 清理音频资源
    if (this.currentAudio) {
      this.currentAudio.pause()
      this.currentAudio = null
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },

    selectAudio() {
      this.$refs.audioInput.click()
    },

    cancelUpload() {
      this.showUploadProgress = false
      this.uploadProgress = 0
      this.loadingState = false
    },

    handleAudioSelect(file) {
      if (!file) return false

      if (!file.type.startsWith('audio/')) {
        Toast('请选择音频文件')
        return false
      }

      const maxSize = 200 * 1024 * 1024 // 200MB
      if (file.size > maxSize) {
        Toast('文件大小不能超过200MB')
        return false
      }

      this.cmsVoice.cmsFileTitle = file.name
      this.cmsVoice.cmsFileSize = file.size
      this.cmsVoice.cmsFileType = file.type
      this.selectFile = file
      this.uploadVideo()
      return false
    },

    saveDraft() {
      if (this.isPlaying && this.currentAudio) {
        this.currentAudio.pause()
        this.isPlaying = false
      }

      if (!this.cmsVoice.cmsTitle.trim()) {
        Toast('请输入音频标题')
        return
      }

      if (this.loadingState) return

      this.loadingState = true
      this.cmsVoice.releaseStatus = 0

      const api = this.cmsId ? putTextCms : saveTextCms
      api(this.cmsVoice)
        .then(() => {
          Toast('草稿保存成功')
          this.$router.go(this.cmsId ? -2 : -1)
        })
        .catch(() => {
          Toast('保存失败，请重试')
        })
        .finally(() => {
          this.loadingState = false
        })
    },

    toggleAudio() {
      if (!this.cmsVoice.cmsFileUrl) {
        Toast('暂无音频文件')
        return
      }

      if (this.isPlaying) {
        if (this.currentAudio) {
          this.currentAudio.pause()
        }
        this.isPlaying = false
        return
      }

      // 停止之前的播放
      if (this.currentAudio) {
        this.currentAudio.pause()
      }

      const audio = new Audio(this.cmsVoice.cmsFileUrl)
      this.currentAudio = audio

      audio.addEventListener('ended', () => {
        this.isPlaying = false
        this.currentAudio = null
      })

      audio.addEventListener('error', () => {
        console.error('音频播放失败')
        Toast('音频播放失败')
        this.isPlaying = false
        this.currentAudio = null
      })

      audio.play()
        .then(() => {
          this.isPlaying = true
        })
        .catch(err => {
          console.error('播放失败:', err)
          Toast('音频播放失败')
          this.isPlaying = false
          this.currentAudio = null
        })
    },

      saveCms() {
        if (this.isPlaying && this.currentAudio) {
          this.currentAudio.pause()
          this.isPlaying = false
        }
        if (!this.cmsVoice.cmsTitle.trim()) {
          Toast('请输入音频标题')
          return
        }
        if (!this.canPublish) {
          Toast('请完善音频信息')
          return
        }

      if (this.loadingState) return

      this.loadingState = true
      this.cmsVoice.releaseStatus = 1

      const api = this.cmsId ? putTextCms : saveTextCms

      if (this.releaseStatus === 1) {
        api(this.cmsVoice)
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
            api(this.cmsVoice)
              .then(() => {
                Toast('保存成功')
                // this.$store.commit('config/setCmsActive', 1)
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

    uploadVideo() {
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
        that.uploadProgress = progressStr
      }

      const resultCallback = (result, errorMessage) => {
        debugger
        if (result) {
          that.cmsVoice.cmsFileUrl = result.fileHwUrl
          that.loadingState = false
          that.showUploadProgress = false
          that.uploadProgress = 0
        } else {
          that.showUploadProgress = false
          that.loadingState = false
          Toast(errorMessage)
        }
      }

      uploadHwFile(this.hwIv, this.hwConfig, this.selectFile, progressCallback, resultCallback)
    },

    queryCmsDetail() {
      this.loadingState = true
      try {
        getCmsInfo(this.cmsId).then(res => {
          const data = res.data.data
          this.cmsVoice.cmsTitle = data.cmsTitle || ''
          this.cmsVoice.cmsFileUrl = data.cmsFileUrl || ''
          this.cmsVoice.cmsFileTitle = data.cmsFileTitle || ''
          this.cmsVoice.cmsFileSize = data.cmsFileSize || 0
          this.cmsVoice.cmsFileType = data.cmsFileType || ''
          this.cmsVoice.cmsFileRemark = data.cmsFileRemark || ''
          this.cmsVoice.doctorId = data.doctorId || this.$store.state.user.userId
          this.releaseStatus = data.releaseStatus || 0
        })
        this.cmsVoice.id = this.cmsId
      } catch (err) {
        Toast('获取内容失败')
      } finally {
        this.loadingState = false
      }
    }
  }
}
</script>

<style scoped>
.add-voice-page {
  min-height: 100vh;
  background-color: #FAFAFA;
  display: flex;
  flex-direction: column;
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


/* 内容区域 */
.content-area {
  flex: 1;
  padding: 0 16px 100px;
  display: flex;
  flex-direction: column;
}

/* 标题输入 */
.title-section {
  margin-bottom: 15px;
  margin-top: 10px;
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

/* 音频区域 */
.audio-section {
  margin-bottom: 15px;
  background: #FFF;
  padding: 20px 20px;
  border-radius: 12px;
}

.section-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.file-info {
  font-size: 12px;
  color: #BABABA;
  font-weight: normal;
}

.audio-upload-area {
  background-color: #fff;
  border-radius: 8px;
  text-align: center;
  border: 1px solid #ddd;
  cursor: pointer;
  padding: 15px 0px;
  transition: all 0.3s ease;
}


.upload-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 0px;
  gap: 8px;
}

.upload-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 25px;
}

.upload-text {
  margin-top: 1px;
  font-size: 16px;
  color: #41464E;
}

/* 音频预览 */
.audio-preview {
  background-color: #fff;
}

.audio-card {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 16px 12px;
  margin-top: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.audio-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}

.audio-icon:hover svg circle {
  stroke: #6366f1;
}

.audio-icon:hover svg polygon,
.audio-icon:hover svg rect {
  fill: #6366f1;
}

.audio-info {
  flex: 1;
  display: flex;
  align-items: center;
  padding-top: 1px;
}

.audio-title {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.edit-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1px solid #e9ecef;
  border-radius: 10px;
  background-color: #fff;
  font-size: 14px;
  color: #41464E;
}

/* 描述输入 */
.description-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 允许flex子项收缩 */
}

.description-input {
  flex: 1;
  padding: 16px;
  border: none;
  border-radius: 8px;
  background-color: #fff;
  font-size: 14px;
  color: #333;
  resize: none; /* 禁用手动调整大小 */
  font-family: inherit;
  box-shadow: 0px 2px 26px 0px rgba(3, 3, 3, 0.03);
  min-height: 200px; /* 设置最小高度 */
}

.description-input::placeholder {
  color: #999;
}

.description-input:focus {
  outline: none;
}

/* 底部发布按钮 */
.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  z-index: 10;
}

.publish-btn {
  width: 100%;
  height: 45px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  background: #4562BA;
  border: none;
  color: #FFF;
  box-shadow: 0 2px 8px rgba(90, 125, 232, 0.3);
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
</style>
