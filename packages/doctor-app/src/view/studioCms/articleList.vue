<template>
  <div class="cms-management-page">
    <van-nav-bar
        title="科普创作作品集"
        left-arrow
        @click-left="onBack"
    >
    </van-nav-bar>

    <div class="search-container">
      <van-search
          v-model="searchQuery"
          placeholder="请输入标题"
          shape="round"
          style="font-size: 14px"
          @search="onSearch"
          @clear="onClear"
      >
      </van-search>
    </div>

    <div class="filter-container">
      <div class="status-tabs">
        <span
            :class="['status-tab', { active: queryType === 1 }]"
            @click="switchStatus(1)"
        >
          我的文案
        </span>
        <span
            :class="['status-tab', { active: queryType ===  2 }]"
            @click="switchStatus(2)"
        >
          我的视频
        </span>
        <span
          :class="['status-tab', { active: queryType ===  3 }]"
          @click="switchStatus(3)"
        >
          我的播客
        </span>
      </div>
    </div>

    <div class="tab-content">
      <!-- 视频列表 -->
      <div v-if="queryType === 2" class="content-panel">
        <van-list
          v-model:loading="loadingState"
          :finished="noMoreState"
          @load="onLoadMore"
        >
          <div v-if="dataList.length > 0" class="video-list">
            <div v-for="(item, index) in dataList" :key="index" class="video-item" @click="onCmsClick(item)">
              <div class="video-cover">
                <img
                  v-if="item.humanVideoCover"
                  :src="item.humanVideoCover"
                  class="cover-image"
                />
                <div v-else class="default-cover">
                  <img src="../../assets/video-default.png" class="default-icon"/>
                </div>
                <div v-if="item.humanVideoCover" class="play-button">
                  <img src="../../assets/addimagelib-play.png" class="play-icon"/>
                </div>
              </div>
              <div class="video-info">
                <div class="video-title">{{ item.taskName }}</div>
                <div class="video-time">{{ formatTime(item.createTime) }}</div>
              </div>
            </div>
          </div>
          <van-empty v-else :image="imgSrc" image-size="110" description="暂无视频作品"
                     style="margin-top: 90px"/>
        </van-list>
      </div>

      <!-- 文案列表 -->
      <div v-if="queryType === 1" class="content-panel">
        <van-list
          v-model:loading="loadingState"
          :finished="noMoreState"
          @load="onLoadMore">
          <div v-if="dataList.length > 0" class="podcast-list">
            <div
              v-for="(item, index) in dataList"
              :key="index"
              class="podcast-item"
              @click="onCmsClick(item)"
            >
              <div class="podcast-content" style="justify-content: left">
                <div
                  style="font-size: 12px; color: #1D65FF; border: 1px solid #1D65FF; margin-right: 5px; text-align: center; border-radius: 3px; ; width: 33px">
                  {{ item.creativeApproach == 2 ? '仿写' : '原创' }}
                </div>
                <div class="podcast-title">{{ item.title || item.imitativeWritingMaterial }}</div>
                <div :style="{color: item.taskStatus === 'NOT_FINISHED' ? '#f22e2e' : '#16C25F'}"
                     class="status-tag">
                  <van-icon name="arrow" style="width: 16px; height: 16px;" />
                </div>
              </div>

              <div class="podcast-content" style="margin-top: 6px">
                <div class="podcast-time">{{ getCreateName(item.taskType) }} {{ item.createTime }}</div>

                <div class="action-buttons">
                </div>
              </div>
            </div>
          </div>
          <van-empty v-else :image="imgSrc" image-size="110" description="暂无文案作品"
                     style="margin-top: 90px"/>
        </van-list>
      </div>

      <!-- 播客列表 -->
      <div v-if="queryType === 3" class="content-panel">
        <van-list
          v-model:loading="loadingState"
          :finished="noMoreState"
          @load="onLoadMore"
        >
          <div v-if="dataList.length > 0" class="podcast-list">
            <div
              v-for="(item, index) in dataList"
              :key="index"
              class="podcast-item"
              @click="onCmsClick(item)"
            >
              <div class="podcast-content">
                <div class="podcast-title">{{ item.podcastName }}</div>
                <div :style="{color: item.taskStatus != 'TASK_FINISH' ? '#f22e2e' : '#16C25F'}"
                     class="status-tag">
                    <van-icon name="arrow" style="width: 16px; height: 16px;" />
                </div>
              </div>

              <div class="podcast-content" style="margin-top: 6px">
                <div class="podcast-time">{{ item.createTime }}</div>

                <div class="action-buttons">
                </div>
              </div>
            </div>
          </div>
          <van-empty v-else :image="imgSrc" image-size="110" description="暂无播客作品"
                     style="margin-top: 90px"/>
        </van-list>
      </div>
    </div>
  </div>
</template>


<script>
  import { userVoiceTaskPage, articleTaskPage, podcastPage } from '@/api/article'
  import Vue from 'vue';
  import { Toast, Dialog, Overlay, Empty } from 'vant';
  import emptyDefault from "@/assets/empty-default.png";
  Vue.use(Toast);
  Vue.use(Overlay);
  Vue.use(Empty);

  export default {
    name: 'CmsListPage',
    data() {
      return {
        imgSrc: emptyDefault,
        doctorId: localStorage.getItem("caring_doctor_id"),
        searchQuery: '',
        queryType: 1, // 默认查文章， 2 查视频， 3 查播客
        showAddOptions: false,
        loadingState: false,
        noMoreState: false,
        dataList: [],
        // 视频分页
        voicePageParams: {
          current: 1,
          model: {
            taskName: '',  // 视频任务名称
            taskStatus: 'SUCCESS',
            userId: this.$route.query.articleUserId, // 假设 user 模块在 Vuex 中
          },
          size: 10
        },
        // 文章分页
        articlePageParams: {
          current: 1,
          model: {
            title: '', // 文章名称
            taskType: 'ARTICLE',
            userId: this.$route.query.articleUserId, // 假设 user 模块在 Vuex 中
          },
          size: 10
        },
        // 播客分页
        podcastPageParams: {
          current: 1,
          model: {
            podcastName: '', // 播客标题
            taskStatus: 'FINISHED',
            userId: this.$route.query.articleUserId, // 假设 user 模块在 Vuex 中
          },
          size: 10
        }
      }
    },

    watch: {
      searchQuery: {
        handler(newVal) {
          if (newVal === '') {
            this.performSearch()
          } else {
            clearTimeout(this._searchTimeout)
            this._searchTimeout = setTimeout(() => {
              this.performSearch()
            }, 800)
          }
        },
        immediate: false
      }
    },

    created() {
      const queryType = localStorage.getItem('cmsArticleStatus')
      if (queryType) {
        this.queryType = Number(queryType);
      }
    },

    mounted() {
      this.performSearch()
    },

    beforeDestroy() {
      clearTimeout(this._searchTimeout)
    },

    methods: {
      performSearch() {
        if (this.queryType === 1) {
          // 查文章
          this.articlePageParams.model.title = this.searchQuery
          this.articlePageParams.current = 1
          this.noMoreState = false
          this.dataList = []
          this.queryArticleList()

        } else if (this.queryType === 2) {
          // 查视频
          this.voicePageParams.model.taskName = this.searchQuery
          this.voicePageParams.current = 1
          this.dataList = []
          this.noMoreState = false

          this.queryVoiceList()
        } else {
          // 查播客
          this.podcastPageParams.model.podcastName = this.searchQuery
          this.podcastPageParams.current = 1
          this.dataList = []
          this.noMoreState = false

          this.queryPodcastList()
        }
      },

      // 格式化时间
      formatTime (time) {
        return moment(time).format('YYYY/MM/DD HH:mm')
      },

      getCreateName (type) {
        if (type == 2) {
          return '社交媒体文案'
        } else if (type == 3) {
          return '短视频口播稿'
        }
        return '深度科普文章'
      },

      /**
       * 查询播客
       */
      queryPodcastList() {

        this.loadingState = true
        podcastPage(this.podcastPageParams).then(res => {
          const data = res.data.data
          data.records.forEach(item => {
            this.dataList.push(item)
          })
          if (data.pages <=  this.podcastPageParams.current) {
            this.noMoreState = true
          }
          this.podcastPageParams.current++
          this.loadingState = false
        })

      },


      switchStatus(status) {
        this.queryType = status
        localStorage.setItem('cmsArticleStatus', status)
        this.performSearch()
      },

      /**
       * 查视频
       */
      queryVoiceList() {

        this.loadingState = true
        userVoiceTaskPage(this.voicePageParams).then(res => {
          const data = res.data.data
          data.records.forEach(item => {
            this.dataList.push(item)
          })
          if (data.pages <=  this.voicePageParams.current) {
            this.noMoreState = true
          }
          this.voicePageParams.current++
          this.loadingState = false
        })

      },

      /**
       * 查询文章
       */
      queryArticleList() {
        this.loadingState = true
        articleTaskPage(this.articlePageParams).then(res => {
          const data = res.data.data
          data.records.forEach(item => {
            this.dataList.push(item)
          })
          if (data.pages <=  this.articlePageParams.current) {
            this.noMoreState = true
          }
          this.articlePageParams.current++
          this.loadingState = false
        })
      },

      onLoadMore() {
        if (this.queryType === 1) {
          // 查文章
          this.queryArticleList()
        } else if (this.queryType === 2) {
          // 查视频
          this.queryVoiceList()
        } else {
          // 查播客
          this.queryPodcastList()
        }
      },

      onSearch() {
        this.performSearch()
      },

      onClear() {
        this.searchQuery = ''
      },

      onBack() {
        this.$router.back()
      },

      onCmsClick(data) {
        let routePath = ''
        if (this.queryType === 1) {
          // 查文章详情
          routePath = '/studio/article/textDetail'
        } else if (this.queryType === 2) {
          // 查视频详情
          routePath = '/studio/article/videoDetail'
        } else {
          // 查播客详情
          routePath = '/studio/article/voiceDetail'
        }
        this.$router.push({
          path: routePath,
          query: {
            takeId: data.id,
            queryType: this.queryType
          }
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
.cms-management-page {
  background-color: #f7f8fa;
  min-height: 100vh;
}

.search-container {
  padding: 10px 16px 0px 16px;
}

:deep(.van-field__left-icon) {
  margin-right: 10px;
}

:deep(.van-search) {
  padding: 7px 0px;
  border-radius: 10px;
}

:deep(.van-search__content) {
  background: #FFF;
}

.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
}

.status-tabs {
  display: flex;
  gap: 50px;
  justify-content: center;
  width: 100%;
  padding-bottom: 5px;
}

/deep/ .van-nav-bar .van-icon{
  color: #000000 !important;
  font-size: 20px;
}

:deep(.van-hairline--bottom:after) {
  border-bottom-width: 0px;
}

:deep(.van-list__finished-text) {
  font-size: 12px;
  color: #969799;
  text-align: center;
  padding: 16px;
}

:deep(.van-tag) {
  border-radius: 12px;
}


.status-tabs {
  display: flex;
  gap: 50px;
  justify-content: center;
  width: 100%;
  padding-bottom: 5px;
}

.status-tab {
  font-size: 16px;
  color: #999;
  cursor: pointer;
  position: relative;
  padding-bottom: 3px;
  font-weight: 400;
}

.status-tab.active {
  color: #4562BA;
  font-weight: 600;
}

.status-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 14px;
  height: 2px;
  background-color: #4562BA;
  border-radius: 1px;
}

/* Tab内容区域 */
.tab-content {
  flex: 1;
  overflow-y: auto;
  background: linear-gradient(90deg, #F7FAFD 0%, #F7F9FC 100%);
}

.content-panel {
  height: 100%;
  width: 100%;
}

.content {
  background: linear-gradient(180deg, #DDEAFB 0%, #E4EDFB 28%, #EDF1FA 65%, #ECEFFA 100%);
  box-sizing: border-box;
  min-height: 100vh;
}

/* 顶部导航栏样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  top: 0;
  z-index: 100;
  position: sticky;
  background: #DFEBFB;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  height: 32px;
  width: auto;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 发布按钮hover效果 */
.publish-btn {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 6px;
  border: 1px solid #1D65FF;
  font-size: 12px;
  line-height: 20px;
  color: #1D65FF;
  padding: 0px 5px;
  display: flex;
  align-items: center;
}

.beans-container {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f8f9fa;
  padding: 6px 12px;
  border-radius: 6px;
}

.beans-icon {
  width: 20px;
  height: 20px;
}

.beans-count {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
}

.notice-container {
  position: relative;
  padding: 4px;
}

.notice-icon {
  width: 24px;
  height: 24px;
  cursor: pointer;
}

.notice-dot {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #ff4757;
  border-radius: 50%;
  border: 2px solid white;
  box-sizing: border-box;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .tab-item {
    padding: 14px 8px;
    font-size: 14px;
  }

  .empty-icon {
    width: 100px;
    height: 100px;
  }

  .empty-text {
    font-size: 15px;
  }
}

/* 搜索容器 */
.search-container {
  background: white;
  border-bottom: 1px solid #f0f0f0;
}

/* 视频列表样式 */
.video-list {
  padding: 12px 12px 100px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.video-item {
  width: calc(50% - 6px);
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.video-cover {
  position: relative;
  width: 100%;
  height: 120px;
  flex-shrink: 0;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #E4EAF4;
}

.default-cover {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.default-icon {
  width: 40px;
  height: 40px;
  opacity: 0.5;
}

.play-button {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 30px;
  height: 30px;
}

.play-icon {
  width: 100%;
  height: 100%;
}

.status-badge {
  position: absolute;
  top: 0;
  left: 0;
  background-color: rgba(255, 255, 255, 0.78);
  color: #1D243F;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 0 0 10px 0;
  z-index: 2;
}


.video-info {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.video-title {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.video-time {
  font-size: 11px;
  color: #999;
}

.video-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 视频弹窗样式 */
.video-popup {
  width: 100%;
  height: 100%;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .video-item {
    margin-bottom: 12px;
  }

  .video-cover {
    height: 70px;
  }

  .video-info {
    padding: 10px;
  }

  .video-title {
    font-size: 13px;
  }
}

/* 播客列表样式 */
.podcast-list {
  padding: 0 12px;
  margin-bottom: 100px;
}

.podcast-item {
  padding: 14px;
  border-radius: 10px;
  margin-top: 12px;
  background: white;
  box-shadow: 3px 4px 9px 0px rgba(29, 101, 255, 0.04);
  transition: background-color 0.2s;
}

.podcast-content {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.podcast-title {
  font-weight: 600;
  font-size: 16px;
  color: #27264D;
  line-height: 1.4;
  flex: 1;
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.podcast-time {
  font-size: 12px;
  color: #8C90A5;
  line-height: 1.2;
}

.podcast-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.status-tag {
  border-radius: 4px;
  font-size: 12px;
  width: 60px;
  text-align: right;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-icon {
  font-size: 18px;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 26px 20px;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.modal-body {
  padding: 0 20px 20px 20px;
}

.create-type-item {
  display: flex;
  align-items: center;
  padding: 0px 16px;
  border-radius: 12px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
  height: 110px;
}


.create-type-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.create-type-item:last-child {
  margin-bottom: 0;
}

.type-icon {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 6px;
  position: relative;
  flex-shrink: 0;
}

.type-content {
  flex: 1;
}

.type-title {
  font-weight: 600;
  font-size: 18px;
  color: #1D243F;
  margin: 0 0 4px 0;
}

.type-desc {
  font-size: 14px;
  color: #9091A8;
  margin: 0;
  line-height: 1.4;
}

.card-image {
  width: 100%;
  height: 100%;
}
</style>
