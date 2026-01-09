<template>
  <div class="video-detail">
    <!-- 顶部导航栏 -->
    <van-nav-bar
        title="视频"
        left-arrow
        @click-left="goBack"
    >
      <template slot="right">
        <img src="../../assets/cms_del.png" style="width: 18px; height: 18px;" @click="deleteCms"/>
      </template>
    </van-nav-bar>

    <!-- 视频内容区域 -->
    <div class="content-container">
      <div class="video-content">
        <!-- 视频标题 -->
        <div class="title">
          {{ videoData.title }}
        </div>

        <!-- 视频播放区域 -->
        <div class="video-player-container">
          <video
              ref="videoPlayer"
              v-if="videoData.fileUrl"
              :src="videoData.fileUrl"
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

    <div class="footer-actions">
      <van-button
          class="edit-btn"
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
    <van-overlay :show="showAddOptions" @click="showAddOptions = false" zIndex="999">
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
  import Vue from 'vue';
  import Api from '@/api/doctor.js'
  import { Toast, Dialog } from 'vant';
  Vue.use(Toast);
  export default {
    name: 'CmsVideoDetail',
    data() {
      return {
        videoData: {
          title: '',
          fileUrl: '',
          fileType: '',
          fileSize: 0,
          fileName: '',
          cmsVideoCover: '',
          articleDataId: ''
        },
        isPlaying: false,
        windowHeight: window.innerHeight,
        navBarHeight: 46,
        footerHeight: 76,
        contentPadding: 30,
        showAddOptions: false,
        cmsId: this.$route.query.cmsId,
        doctorMobile: '',
        articleImportUserId: '',
        addActions: [
          { name: '图文文章', value: 'text' },
          { name: '音频内容', value: 'voice' },
          { name: '视频内容', value: 'video' }
        ],
        commentCurrent: 1,
        commentSize: 50,
        contentReplies: [],
        loading: false,
        finished: false
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
      if (!this.cmsId) {
        this.$router.replace('/cms')
        return
      }
      this.queryCmsDetail()
      this.getInfo()
      window.addEventListener('resize', this.handleResize)
    },

    beforeDestroy() {
      window.removeEventListener('resize', this.handleResize)
    },

    methods: {
      handleResize() {
        this.windowHeight = window.innerHeight
      },

      getInfo() {
        const params = {
          id: localStorage.getItem('caring_doctor_id')
        }
        if (!params.id) {
          return
        }
        Api.getContent(params).then(res => {
          if (res.data.code === 0) {
            this.doctorMobile = res.data.data.mobile
            checkArticleUserExist(this.doctorMobile).then(res => {
              this.articleImportUserId = res.data.data
            })
          }
        })
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
          getCmsInfo(this.cmsId).then(res => {
            const data = res.data.data
            that.videoData = {
              title: data.cmsTitle || '',
              fileUrl: data.cmsFileUrl || '',
              fileType: data.cmsFileType || '',
              fileSize: data.cmsFileSize || 0,
              cmsVideoCover: data.cmsVideoCover || '',
              fileName: data.cmsFileTitle || '',
              articleDataId: data.articleDataId
            }
          })
        } catch (err) {
          console.error('获取视频详情失败:', err)
          Toast('获取内容失败')
        }
      },

      editContent() {
        this.$router.push({
          path: '/studio/cms/addVideo',
          query: { cmsId: this.cmsId }
        })
      },

      publishNew() {
        this.showAddOptions = true
      },

      onAddSelect(action) {
        this.showAddOptions = false
        switch (action.value) {
          case 'text':
            this.$router.replace({ path: '/studio/cms/addText' })
            break
          case 'voice':
            this.$router.replace({ path: '/studio/cms/addVoice' })
            break
          case 'video':
            this.$router.replace({ path: '/studio/cms/addVideo' })
            break
          case 'articleImport':
            this.$router.push({ path: '/studio/cms/articleImport', query: { articleUserId: this.articleImportUserId} })

        }
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
        channelContentWithReply(params).then((res) => {
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
  padding: 5px 12px 0px 12px; /* 增加底部padding为底部按钮留出空间 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
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
  padding: 8px 12px 12px 12px;
  display: flex;
  gap: 12px;
  background-color: #FAFAFA;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
  z-index: 100;
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
/deep/ .van-nav-bar .van-icon {
  color: #000000 !important;
  font-size: 20px;
}
</style>
