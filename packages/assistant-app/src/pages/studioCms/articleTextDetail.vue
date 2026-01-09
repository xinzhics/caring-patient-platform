<template>
  <div class="text-detail">
    <!-- 顶部导航栏 -->
    <van-nav-bar
        title="文案"
        left-arrow
        @click-left="goBack"
    >
    </van-nav-bar>

    <!-- 文章内容区域 -->
    <div class="content-container">
      <div
          class="article-content"
          :style="{ height: contentHeight + 'px' }">
        <div class="title">
          {{articleData.cmsTitle}}
        </div>
        <div class="studiocms-content" v-html="compiledMarkdown" style="font-size: 16px"> </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="footer-actions">
      <van-button
          class="edit-btn"
          plain
          round
          @click="importText(0)"
      >
        导入到草稿
      </van-button>
      <van-button
        class="publish-btn"
        type="primary"
        round
        @click="importText(1)"
      >
        导入并发布
      </van-button>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { getArticleContentAll } from '@/api/article'
import { saveTextCms } from '@/api/cms'
import { Toast, Button, NavBar } from 'vant'
import marked from 'marked'
Vue.use(Toast)
Vue.use(Button)
Vue.use(NavBar)

export default {
  name: 'ArticleTextDetail',
  data () {
    return {
      articleData: {
        cmsTitle: '',
        cmsContent: ''
      },
      doctorId: this.$route.query.doctorId,
      compiledMarkdown: '',
      takeId: this.$route.query.takeId,
      windowHeight: window.innerHeight,
      navBarHeight: 46, // van-nav-bar 默认高度
      footerHeight: 76, // 底部按钮区域高度
      contentPadding: 30,
      showAddOptions: false,
      loadingState: false
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
    this.queryCmsDetail()
    window.addEventListener('resize', this.handleResize)
  },

  beforeDestroy () {
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    handleResize () {
      this.windowHeight = window.innerHeight
    },

    goBack () {
      this.$router.back()
    },

    importText (type) {
      const cmsText = {
        cmsTitle: this.articleData.cmsTitle,
        cmsType: 'CMS_TYPE_TEXT',
        cmsContent: this.compiledMarkdown,
        doctorId: this.doctorId, // 假设 user 模块在 Vuex 中
        articleDataId: this.takeId
      }
      if (this.loadingState) {
        return
      }
      this.loadingState = true
      if (type === 0) {
        cmsText.releaseStatus = 0
      } else if (type === 1) {
        cmsText.releaseStatus = 1
      }
      saveTextCms(cmsText)
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

    queryCmsDetail () {
      const that = this
      try {
        getArticleContentAll(this.takeId).then(res => {
          const data = res.data
          const article = data.articleTask
          that.articleData.cmsTitle = article.title
          if (article.taskType !== 1) {
            that.articleData.cmsContent = article.content
          } else {
            if (article.imitativeWritingType && article.imitativeWritingType === 1) {
              that.articleData.cmsContent = article.content
            } else {
              that.articleData.cmsContent = data.articleOutline.articleContent
            }
          }
          this.compiledMarkdown = marked(that.articleData.cmsContent)
        })
      } catch (err) {
        console.error('获取文章详情失败:', err)
        Toast('获取内容失败')
      }
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

/deep/ .van-nav-bar .van-icon{
  color: #000000 !important;
  font-size: 20px;
}

/* 内容容器 */
.content-container {
  flex: 1;
  padding: 5px 12px 20px 12px;
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
  padding: 0px 12px 15px 12px;
  display: flex;
  gap: 12px;
  flex-shrink: 0;
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
</style>
