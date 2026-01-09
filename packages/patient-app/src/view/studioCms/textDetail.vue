<template>
  <div class="text-detail">
    <div v-if="showShareHint">
      <div
        style="position: fixed; background-color: black; opacity: 0.3; width: 100%; z-index: 900; height: 100vh;"></div>
      <div style="width: 100%; text-align: center; position: fixed; z-index: 9999">
        <img :src="share_hint" style="width: 85%; padding-left: 15px;">
        <img :src="i_know" style="width: 35%; margin-top: 80px;" @click="closeShareHint()">
      </div>
    </div>
    <navBar pageTitle="文章"></navBar>

    <!-- 文章内容区域 -->
    <div class="content-container">
      <div
        class="article-content">
        <div class="title">
          <h2>
            {{ articleData.title }}
          </h2>
        </div>
        <div class="studiocms-content" v-html="articleData.content"> </div>
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
        <x-input @on-focus="show=!show" class="inputCont" style="font-size:12px" size="mini"
                 placeholder="写评论..."></x-input>
        <div class="right">
          <img @click="saveBtn()" :src="articleData.collectStatus == 1 ? saveT:save" alt="">
          <img :src="share" alt="" @click="openShare()">
        </div>
      </div>
      <div v-if="!show" class="innerT">
        <div class="title">
          <span><img :src="close" alt="" @click="show=true"></span>
          <span style="margin-left: 30px; font-size: 16px">写评论</span>
          <span
            style="padding:6px 15px;border-radius:20px;color:#fff;font-size:14px; text-align: center; display: flex; align-items: center"
            :style="{background:inputContent?'#3F86FF':'#ccc'}" @click="submit">提交</span>
        </div>
        <x-textarea class="textareaCont" :max="200" :rows="5" :cols="30" placeholder="写评论..."
                    v-model="inputContent"></x-textarea>
      </div>
    </div>
  </div>
</template>


<script>
import {getCmsInfo, studioContentReplySave, studioCollectSave, studioContentReplyPage} from '@/api/cms'
import {XTextarea, XInput} from 'vux'
import Api from '@/api/Content.js'
import wx from 'weixin-js-sdk'
import Vue from 'vue'
import { PullRefresh, List } from 'vant'
import marked from "marked";
Vue.use(PullRefresh)
Vue.use(List)

export default {
  components: {
    navBar: () => import('@/components/headers/navBar'),
    [XTextarea.name]: XTextarea,
    XInput,
  },
  name: 'ArticleDetail',
  data() {
    return {
      articleData: {
        title: '',
        articleDataId: '',
        content: ''
      },
      save: require('@/assets/my/saveS.png'),
      saveT: require('@/assets/my/saveT.png'),
      share: require('@/assets/my/share.png'),
      share_hint: require('@/assets/my/share_hint.png'),
      i_know: require('@/assets/my/i_know.png'),
      cmsId: this.$route.query.cmsId,
      doctorId: this.$route.query.doctorId, // 虽未使用，但保留以对齐原逻辑
      windowHeight: window.innerHeight,
      navBarHeight: 46,
      contentPadding: 30,
      id: this.$route.query.cmsId,
      contentReplies: [],
      show: true,
      inputContent: '',
      isLoading: false,
      isLogin: !!localStorage.getItem('userId'),
      isComment: false,
      close: require('@/assets/my/close.png'),
      good: require('@/assets/my/good.png'),
      gooder: require('@/assets/my/gooder.png'),
      patientInfo: '',
      cancollct: true,
      showShareHint: false,
      commentCurrent: 1,
      commentSize: 50,
      loading: false,
      finished: false,
      refreshing: false
    }
  },

  computed: {
    contentHeight() {
      return this.windowHeight - this.navBarHeight - this.contentPadding
    }
  },

  mounted() {
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)

    // 校验参数并加载数据
    if (!this.cmsId) {
      this.$router.replace('/')
      return
    }
    this.queryCmsDetail()
    this.patientInfo = JSON.parse(localStorage.getItem('myallInfo'))
    this.getInfo()
    this.getwxSignature()
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
  },

  methods: {
    saveBtn() {
      const params = {
        cmsId: this.id,
        userId: localStorage.getItem('userId'),
        collectStatus: this.articleData.collectStatus == 1 ? 0 : 1
      }
      studioCollectSave(params).then((res) => {
        if (res.data.code === 0) {
          this.articleData.collectStatus = this.articleData.collectStatus == 1 ? 0 : 1
        }
      })

    },
    handleResize() {
      this.windowHeight = window.innerHeight
    },

    /**
     * 格式转换
     * @param content
     * @returns {*}
     */
    markdownToHtml(content) {
      return marked(content);
    },

    goBack() {
      this.$router.back()
    },

    queryCmsDetail() {
      getCmsInfo(this.cmsId).then(res => {
        const data = res.data.data
        this.articleData = {
          title: data.cmsTitle || '',
          content: data.cmsContent || '',
          articleDataId: data.articleDataId || '',
          collectStatus: data.collectStatus
        }
      })
    },
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
        ccmsId: this.id,
        creplierId: localStorage.getItem('userId'),
        ccontent: this.inputContent,
        replierAvatar: this.patientInfo.avatar,
        replierName: this.patientInfo.name
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
    openShare() {
      this.showShareHint = true;
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
              title: that.articleData.title,
              desc: '',
              link: url,
              imgUrl: that.articleData.icon,
              success: function () {
                that.showShareHint = false
              }
            });
            wx.updateTimelineShareData({
              title: that.articleData.title,
              desc: '',
              link: url,
              imgUrl: that.articleData.icon,
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
.title {
  font-weight: 500;
  font-size: 20px;
  color: #1A1A1A;
  border-bottom: 1px solid #DEDEDE;
  margin-bottom: 16px;
  padding-bottom: 8px;
}

.text-detail {
  min-height: 100vh;
  background-color: #FAFAFA;
  display: flex;
  flex-direction: column;
}

/* 导航栏样式 */
:deep(.van-nav-bar) {
  background-color: #fff;
  border-bottom: 1px solid #ebedf0;
  height: 46px;
  flex-shrink: 0;
}

:deep(.van-nav-bar__title) {
  font-size: 18px;
  font-weight: 500;
  color: #323233;
}

:deep(.van-nav-bar .van-icon) {
  color: #969799;
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

.tipCover {
  width: 94vw;
  padding: 10px 3vw 20px 3vw;
  background: #fff;
  position: fixed;
  bottom: 0;
  left: 0;
  border-top: 1px solid rgba(102, 102, 102, 0.1);
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
  border: 1px solid #D6D6D6;


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
</style>

<style>

  .studiocms-content {

  }

  .studiocms-content h1,
  .studiocms-content h2,
  .studiocms-content h3 {
    margin-top: 1.5em;
    margin-bottom: 0.8em;
    font-weight: 600;
  }
  .studiocms-content p {
    margin-bottom: 1em;
  }
  .studiocms-content a {
    color: #007bff;
    text-decoration: none;
  }
  .studiocms-content a:hover {
    text-decoration: underline;
  }
  .studiocms-content code {
    background-color: #f4f4f4;
    padding: 2px 6px;
    border-radius: 4px;
    font-family: monospace;
  }
  .studiocms-content pre {
    background-color: #2d2d2d;
    color: #f8f8f2;
    padding: 16px;
    border-radius: 6px;
    overflow-x: auto;
  }
  .studiocms-content pre code {
    background: none;
    padding: 0;
  }
  .studiocms-content ul, ol {
    padding-left: 2em;
  }
  .studiocms-content img {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
  }

  .studiocms-content blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

</style>
