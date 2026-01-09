<template>
  <div class="cms-management-page">
    <van-nav-bar
        title="科普患教"
        left-arrow
        fixed
        placeholder
        @click-left="onBack"
    >
      <template slot="right">
        <div
            @click="showAddOptions = true"
            v-if="cmsListVo.length > 0"
            class="add-button"
        >
          <img src="../../assets/cms_add.png" style="width: 15px;"/>
          添加
        </div>
      </template>
    </van-nav-bar>

    <div class="search-container">
      <van-search
          v-model="searchQuery"
          placeholder="请输入文章标题"
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
            :class="['status-tab', { active: pageParams.model.releaseStatus === 1 }]"
            @click="switchStatus(1)"
        >
          已发布
        </span>
        <span
            :class="['status-tab', { active: pageParams.model.releaseStatus === 0 }]"
            @click="switchStatus(0)"
        >
          草稿
        </span>
      </div>
    </div>

    <div class="cms-list-container">
      <!-- 空数据状态 -->
      <div v-if="!loadinging && cmsListVo.length === 0" class="empty-state">
        <div class="empty-icon">
          <img src="../../assets/empty_box.png" style="width: 160px;"/>
        </div>
        <div class="empty-text">暂无内容</div>
        <div
            class="cms-empty-add-btn"
            v-if="!searchQuery"
            @click="showAddOptions = true"
        >
          <img src="../../assets/cms_add.png" style="width: 20px;"/>
          添加
        </div>
      </div>

      <!-- 有数据时显示列表 -->
      <van-list
          v-else
          style="padding-bottom: 80px"
          v-model="loadingState"
          :finished="noMoreState"
          @load="onLoadMore"
      >
        <div
            v-for="(cms, index) in cmsListVo"
            :key="cms.id"
            class="cms-item"
            @click="onCmsClick(cms)"
        >
          <div class="cms-info">
            <!-- 第一行：文章标签、标题和删除按钮 -->
            <div class="cms-header">
              <div class="cms-type-tag">
                {{ cms.cmsType == 'CMS_TYPE_TEXT' ? '文章' : cms.cmsType == 'CMS_TYPE_VIDEO' ? '视频' : '音频' }}
              </div>
              <div class="cms-title">{{ cms.cmsTitle }}</div>
              <div class="cms-delete-btn" @click.stop="deleteCms(cms)">
                <img src="../../assets/cms_del.png" style="width: 16px; height: 16px;"/>
              </div>
            </div>

            <!-- 文章内容描述 -->
            <div v-if="cms.cmsType == 'CMS_TYPE_TEXT'" class="cms-description">
              <div v-html="cms.cmsContent"></div>
            </div>
            <div v-else class="cms-content_video">
              <div style="width: 55px; display: flex; align-items: center">
                <img v-if="cms.cmsType == 'CMS_TYPE_VIDEO'" src="../../assets/cms_add_video_1.png" style="width: 55px"/>
                <img v-else src="../../assets/cms_add_audio_1.png" style="width: 55px"/>
              </div>

              <div class="cms-content_video_text">
                <div class="cms-content_video_title">{{ cms.cmsFileTitle ? cms.cmsFileTitle : '' }}</div>
                <div class="cms-content_video_dsc">{{ cms.cmsFileType ? cms.cmsFileType : '' }} {{ formatFileSize(cms.cmsFileSize) }}</div>
              </div>
            </div>

            <!-- 底部：时间和置顶按钮 -->
            <div class="cms-footer">
              <div class="cms-date">{{ cms.createTime.substring(0, 10) }}</div>
              <div
                  v-if="pageParams.model.releaseStatus === 1"
                  class="top-button"
                  :class="{ 'top-button-disabled': cms.pinToTop == 1 }"
                  @click.stop="cms.pinToTop == 1 ? null : toTop(cms, index)">
                <img src="../../assets/cms_top.png" style="width: 18px; height: 18px;"/>
                {{ cms.pinToTop == 1 ? '已置顶' : '置顶' }}
                <div v-if="cms.pinToTop == 1" class="disabled-overlay"></div>
              </div>
              <div v-else class="top-button" @click.stop="sCms(cms, index)">
                <img src="../../assets/cms_send.png" style="width: 18px; height: 18px;"/>
                去发布
              </div>
            </div>
          </div>
        </div>
      </van-list>

      <!-- 底部提示文本 -->
      <div v-if="cmsListVo.length > 0 && pageParams.model.releaseStatus === 1" class="bottom-tip">
        此处排序将同步更新患者端看到的文章顺序
      </div>
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
  import {
    getCmsList,
    delCms,
    pinToTopCms,
    releaseCms,
    checkArticleUserExist
  } from '@/api/cms'
  import Vue from 'vue';
  import Api from '@/api/doctor.js'
  import marked from 'marked';
  import { Toast, Dialog, Overlay } from 'vant';

  Vue.use(Toast);
  Vue.use(Overlay);

  export default {
    name: 'CmsListPage',
    data() {
      return {
        doctorName: '',
        doctorId: localStorage.getItem("caring_doctor_id"),
        searchQuery: '',
        showAddOptions: false,
        doctorList: [],
        showDoctorSelect: false,
        cmsListVo: [],
        loadingState: false,
        loadinging: false,
        noMoreState: false,
        pageParams: {
          current: 1,
          model: {
            cmsTitle: null,
            cmsType: null,
            doctorId: localStorage.getItem("caring_doctor_id"), // 假设 user 模块在 Vuex 中
            releaseStatus: 1
          },
          size: 10
        },
        doctorMobile: '',
        articleImportUserId: '',
        debouncedPerformSearch: null
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

    mounted() {
      const releaseStatus = localStorage.getItem('cmsReleaseStatus')
      if (releaseStatus) {
        this.pageParams.model.releaseStatus = Number(releaseStatus)
      }
      this.queryCmsList()
      this.getInfo()
    },

    beforeDestroy() {
      clearTimeout(this._searchTimeout)
    },

    methods: {
      performSearch() {
        this.pageParams.model.cmsTitle = this.searchQuery
        this.searchCms()
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

      /**
       * 格式转换
       * @param content
       * @returns {*}
       */
      markdownToHtml(content) {
        return marked(content);
      },

      searchCms() {
        this.pageParams.current = 1
        this.cmsListVo = []
        this.noMoreState = false
        this.queryCmsList()
      },

      queryCmsList() {
        if (this.loadinging) {
          return
        }
        this.loadinging = true
        getCmsList(this.pageParams).then(res => {
          res.data.data.records.forEach(item => {
            this.cmsListVo.push(item)
          })
          if (res.data.data.pages <= this.pageParams.current) {
            this.noMoreState = true
          }
          this.pageParams.current++
          this.loadingState = false
          this.loadinging = false
        }).catch(() => {
          this.loadingState = false
          this.loadinging = false
        })
      },

      onLoadMore() {
        this.queryCmsList()
      },

      onSearch() {
        this.performSearch()
      },

      onClear() {
        this.searchQuery = ''
      },

      switchStatus(status) {
        this.pageParams.model.releaseStatus = status
        localStorage.setItem('cmsReleaseStatus', status)
        this.searchCms()
      },

      onBack() {
        this.$router.back()
      },

      onCmsClick(cms) {
        let routePath = '/studio/cms/textDetail'
        if (cms.cmsType === 'CMS_TYPE_VIDEO') {
          routePath = '/studio/cms/videoDetail'
        } else if (cms.cmsType === 'CMS_TYPE_VOICE') {
          routePath = '/studio/cms/voiceDetail'
        }

        this.$router.push({
          path: routePath,
          query: {
            cmsId: cms.id,
            doctorId: this.doctorId
          }
        })
      },

      deleteCms(cms) {
        const that = this
        Dialog.confirm({
          title: '确认删除',
          message: '确定要删除这条内容吗?',
          showCancelButton: true
        })
          .then(() => {
            delCms(cms.id).then(res => {
              Toast('删除成功')
              that.searchCms()
            })
          })
      },

      toTop(cms) {
        const that = this
        Dialog.confirm({
          title: '置顶提醒',
          message: '确定要置顶这条内容吗?',
          showCancelButton: true
        })
          .then(() => {
            pinToTopCms(cms.id, that.doctorId).then(res => {
              Toast('置顶成功')
              that.searchCms()
            })
          })
      },

      sCms(cms, index) {
        if (cms.cmsType === 'CMS_TYPE_TEXT' && !cms.cmsContent) {
          Toast('请添加文章内容')
          return
        }
        const that = this
        Dialog.confirm({
          title: '发布提醒',
          message: '内容发布完成后，患者即可查看?',
          showCancelButton: true
        })
          .then(() => {
            releaseCms(cms.id).then(res =>{
              Toast('发布成功')
              that.cmsListVo.splice(index, 1)
            })
          })
      },

      onAddSelect(action) {
        this.showAddOptions = false

        switch (action.value) {
          case 'text':
            this.goAddTextCms()
            break
          case 'voice':
            this.goAddVoiceCms()
            break
          case 'video':
            this.goAddVideoCms()
            break
          case 'articleImport':
            this.toArticleImport()
        }
      },

      toArticleImport() {
        this.$router.push({ path: '/studio/cms/articleImport', query: { articleUserId: this.articleImportUserId} })
      },

      goAddTextCms() {
        this.$router.push({ path: '/studio/cms/addText' })
      },

      goAddVideoCms() {
        this.$router.push({ path: '/studio/cms/addVideo' })
      },

      goAddVoiceCms() {
        this.$router.push({ path: '/studio/cms/addVoice' })
      },

      formatFileSize(bytes) {
        if (!bytes) {
          return ''
        }
        bytes = Number(bytes)
        if (bytes === 0) return ''
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

/deep/.van-field__left-icon {
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

.doctor-info {
  position: relative;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.doctor-info:hover {
  background-color: #f5f5f5;
}

.doctor-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.rotate-180 {
  transform: rotate(180deg);
  transition: transform 0.2s ease;
}

.doctor-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e5e5;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  min-width: 150px;
  max-height: 300px;
  overflow-y: auto;
}

.doctor-option {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s ease;
  width: 100%;
  font-size: 14px;
}

.doctor-option:last-child {
  border-bottom: none;
}

.doctor-option:hover {
  background-color: #f5f5f5;
}

.doctor-option.active {
  background-color: #e6f7ff;
  color: #1890ff;
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

.bottom-tip {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  padding: 10px 16px 15px 16px;
  background: #f7f8fa;
  color: #A6ADB7;
  font-size: 12px;
  z-index: 1;
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

.add-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px !important;
  font-size: 12px;
  background: #4562BA;
  border-radius: 30px;
  color: #FFF;
}

.cms-list-container {
  padding: 0 16px;
}

.cms-item {
  display: flex;
  align-items: flex-start;
  padding: 16px 16px 12px 16px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0px 2px 26px 0px rgba(3, 3, 3, 0.03);
  cursor: pointer;
}

.cms-info {
  flex: 1;
  min-width: 0;
}

.cms-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  gap: 8px;
}

.cms-type-tag {
  color: #4562BA;
  border: 1px solid #4562BA;
  font-size: 11px;
  padding: 1px 4px 0px 4px;
  border-radius: 4px;
  white-space: nowrap;
  flex-shrink: 0;
}

.cms-title {
  font-size: 16px;
  font-weight: 500;
  color: #1A1A1A;
  line-height: 1.4;
  flex: 1;
  min-width: 0;
  margin-top: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cms-delete-btn {
  flex-shrink: 0;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cms-delete-btn:hover {
  opacity: 0.7;
}

.cms-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cms-content_video {
  background: #FAFAFA;
  border-radius: 8px;
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 10px;
  margin-top: 12px;
  gap: 10px
}

.cms-content_video_text {
  flex: 1;
  min-width: 0;
}

.cms-content_video_title {
  font-size: 16px;
  color: #1A1A1A;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.cms-content_video_dsc {
  font-size: 12px;
  color: #46556B;
}

.cms-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #EBF2FF;
}

.cms-date {
  font-size: 14px;
  color: #999;
}

.top-button {
  background-color: transparent;
  color: #4562BA;
  font-size: 16px;
  padding: 0;
  gap: 1px;
  display: flex;
  align-items: center;
  position: relative;
}

.top-button-disabled {
  pointer-events: none;
  cursor: not-allowed;
}

.disabled-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 4px;
}

/deep/ .van-nav-bar .van-icon {
  color: #999;
  font-size: 20px;
}

:deep(.van-hairline--bottom:after) {
  border-bottom-width: 0px;
}

:deep(.van-nav-bar) {
  background: #f7f8fa;
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

/* 空数据状态样式 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 5px;
}

.empty-text {
  font-size: 18px;
  color: #999;
  margin-bottom: 32px;
}

.cms-empty-add-btn {
  width: 95%;
  height: 48px;
  border-radius: 24px;
  background: #4562BA;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #FFF;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  gap: 5px;
}

.cms-empty-add-btn :deep(.van-button__content) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/deep/ .van-nav-bar .van-icon{
  color: #000000 !important;
  font-size: 20px;
}
</style>
