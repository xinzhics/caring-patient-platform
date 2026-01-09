<template>
  <div class="text-detail">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      title="文章"
      left-arrow
      @click-left="goBack"
    >
      <template slot="right">
        <img src="../../assets/cms_del.png" style="width: 18px; height: 18px;" @click="deleteCms"/>
      </template>
    </van-nav-bar>

    <!-- 文章内容区域 -->
    <div class="content-container">
      <div
        class="article-content">
        <div class="title">
          <h2>
            {{ articleData.title }}
          </h2>
        </div>
        <div class="studiocms-content" v-html="articleData.content" style="font-size: 16px"> </div>
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
import Vue from 'vue'
import {delCms, getCmsInfo, channelContentWithReply, checkArticleUserExist} from '@/api/cms'
import { doctorDetails } from '@/api/doctorApi'
import {Toast, Dialog, Button, NavBar, Overlay} from 'vant'
import marked from 'marked'

Vue.use(Toast)
Vue.use(Button)
Vue.use(NavBar)
Vue.use(Overlay)

export default {
  name: 'CmsTextDetail',
  data () {
    return {
      articleData: {
        title: '',
        content: '',
        articleDataId: ''
      },
      cmsId: this.$route.query.cmsId,
      doctorMobile: '',
      articleImportUserId: '',
      doctorId: this.$route.query.doctorId,
      windowHeight: window.innerHeight,
      navBarHeight: 46, // van-nav-bar 默认高度
      footerHeight: 76, // 底部按钮区域高度
      contentPadding: 30,
      showAddOptions: false,
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
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    handleResize () {
      this.windowHeight = window.innerHeight
    },

    /**
     * 格式转换
     * @param content
     * @returns {*}
     */
    markdownToHtml (content) {
      return marked(content)
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
          that.articleData = {
            title: data.cmsTitle || '',
            content: data.cmsContent || '',
            articleDataId: data.articleDataId
          }
        })
      } catch (err) {
        console.error('获取文章详情失败:', err)
        Toast('获取内容失败')
      }
    },

    editContent () {
      this.$router.push({
        path: '/studio/cms/addText',
        query: {cmsId: this.cmsId, doctorId: this.doctorId}
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
            query: {doctorId: this.doctorId}
          })
          break
        case 'voice':
          this.$router.replace({
            path: '/studio/cms/addVoice',
            query: {doctorId: this.doctorId}
          })
          break
        case 'video':
          this.$router.replace({
            path: '/studio/cms/addVideo',
            query: {doctorId: this.doctorId}
          })
          break
        case 'articleImport':
          this.$router.push({ path: '/studio/cms/articleImport', query: { articleUserId: this.articleImportUserId, doctorId: this.doctorId } })
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
.title {
  font-weight: 500;
  font-size: 16px;
  color: #1A1A1A;
  border-bottom: 1px solid #EBF2FF;
  margin-bottom: 16px;
  padding-bottom: 8px;
}

.text-detail {
  min-height: 100vh;
  background-color: #FAFAFA;
  display: flex;
  flex-direction: column;
}

/* 内容容器 */
.content-container {
  padding: 5px 12px 0px 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.article-content {
  background-color: #fff;
  padding: 12px 15px;
  border-radius: 8px;
  line-height: 1.6;
  font-size: 16px;
  color: #323233;
  overflow-y: auto;
  overflow-x: hidden;
}

/* 自定义滚动条样式 */
.article-content::-webkit-scrollbar {
  width: 4px;
}

.article-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.article-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.article-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* HTML内容样式 */
.article-content :deep(p) {
  margin-bottom: 16px;
  text-align: justify;
}

.article-content :deep(strong) {
  font-weight: 600;
  color: #1989fa;
}

.article-content :deep(p:last-child) {
  margin-bottom: 0;
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

:deep(.van-nav-bar .van-icon) {
  color: #000000;
  font-size: 20px;
}

:deep(.van-hairline--bottom:after) {
  border-bottom-width: 0px;
}

:deep(.van-nav-bar) {
  background: #FAFAFA;
  border-bottom: 0px;
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
  color: #000;
  font-size: 20px;
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
  color: #000000;
  font-size: 20px;
}

</style>
