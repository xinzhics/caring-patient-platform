<template>
  <div class="add-text-cms">
    <!-- 头部导航 -->
    <div class="header">
      <van-icon v-if="!cmsId" name="cross" class="close-btn" @click="goBack"/>
      <van-icon v-else name="arrow-left" class="close-btn" @click="goBack"/>
      <span class="title">{{ cmsId ? '' : '添加' }}文章</span>
      <van-button v-if="!cmsId" class="save-draft-btn" plain @click="saveDraft">保存草稿</van-button>
    </div>

    <!-- 内容区域 -->
    <div class="content">
      <!-- 标题输入框 -->
      <van-field
          v-model="cmsText.cmsTitle"
          placeholder="请输入标题"
          class="title-input"
          :border="false"
          maxlength="50"
      />

      <!-- 富文本编辑器 -->
      <div class="editor-container" ref="editorElem" :style="{ height: editorHeight + 'px' }">
        <div id="toolbar-container" style="display: none"></div>
        <div id="text-container" class="editor-content studiocms-content" style="z-index: 2000"></div>
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
  </div>
</template>
<script>
  import { getCmsInfo, saveTextCms, putTextCms } from '@/api/cms'
  import E from "wangeditor"
  import Vue from 'vue';
  import { Toast, Dialog } from 'vant';

  Vue.use(Toast);
  export default {
    name: 'CmsEditor',
    data() {
      return {
        editor: null,
        cmsId: this.$route.query.cmsId || '',
        releaseStatus: 0,
        cmsText: {
          id: this.$route.query.cmsId || '',
          cmsTitle: '',
          cmsType: 'CMS_TYPE_TEXT',
          cmsContent: '',
          doctorId: localStorage.getItem("caring_doctor_id"), // 假设 user 模块在 Vuex 中
          releaseStatus: 0
        },
        loadingState: false,
        editorHeight: 0,
        editorConfig: {
          placeholder: '请输入文章内容或在此处直接粘贴文章内容'
        }
      }
    },
    mounted() {
      if (this.cmsId) {
        this.queryCmsDetail()
      } else {
        this.initEdit()
      }
      this.calculateEditorHeight()
      window.addEventListener('resize', this.calculateEditorHeight)
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.calculateEditorHeight)
      this.editor.destroy()
      this.editor = null
    },
    methods: {
      initEdit() {
        this.$nextTick(() => {
          if (this.editor == null) {
            const editor = new E('#toolbar-container', '#text-container')
            editor.config.menus = []
            editor.config.placeholder = '点击（iOS 需双击）以编辑内容'
            editor.create()
            if (this.cmsText.cmsContent) {
              editor.txt.html(this.cmsText.cmsContent)
            }
            this.editor = editor

            const editableDiv = this.$refs.editorElem.querySelector('.editor-content')
            if (editableDiv) {
              editableDiv.classList.add('needsclick')
            }
          }
        })
      },

      // HTML 转纯文本
      htmlToText(html) {
        if (!html) return ''
        const tempDiv = document.createElement('div')
        tempDiv.innerHTML = html
        const textContent = tempDiv.textContent || tempDiv.innerText || ''
        return textContent.replace(/\s+/g, ' ').trim()
      },

      calculateEditorHeight() {
        this.$nextTick(() => {
          const windowHeight = window.innerHeight
          const headerHeight = document.querySelector('.header').clientHeight || 0
          const titleInputHeight = document.querySelector('.title-input').clientHeight || 0
          const footerHeight = document.querySelector('.footer').clientHeight || 0
          const contentMargin = 30

          const editorContainerHeight =
            windowHeight - headerHeight - footerHeight - contentMargin - titleInputHeight

          this.editorHeight = Math.max(editorContainerHeight, 200)
        })
      },

      goBack() {
        this.$router.go(this.cmsId ? -2 : -1)
      },

      saveDraft() {
        if (!this.cmsText.cmsTitle.trim()) {
          Toast('请输入标题')
          return
        }

        const textContent = this.editor.txt.html()
        if (!textContent) {
          this.cmsText.cmsContent = ''
        } else {
          this.cmsText.cmsContent = textContent
        }
        this.cmsText.releaseStatus = 0

        this.loadingState = true
        const api = this.cmsId ? putTextCms : saveTextCms

        api(this.cmsText)
          .then(() => {
            this.loadingState = false
            Toast('保存成功')
            this.$router.go(this.cmsId ? -2 : -1)
          })
          .catch(() => {
            this.loadingState = false
            Toast('保存失败，请重试')
          })
      },

      saveCms() {
        if (!this.cmsText.cmsTitle.trim()) {
          Toast('请输入标题')
          return
        }

        const textContent = this.editor.txt.html()
        if (!textContent) {
          Toast('请输入内容')
          return
        }
        this.cmsText.cmsContent = textContent
        this.cmsText.releaseStatus = 1
        this.loadingState = true
        const api = this.cmsId ? putTextCms : saveTextCms

        if (this.releaseStatus === 1) {
          api(this.cmsText)
            .then(() => {
              this.loadingState = false
              Toast('保存成功')
              this.$router.go(this.cmsId ? -2 : -1)
            })
            .catch(() => {
              this.loadingState = false
              Toast('保存失败，请重试')
            })
        } else {
          Dialog.confirm({
            title: '发布提醒',
            message: '内容发布完成后，患者即可查看',
            showCancelButton: true
          })
            .then(() => {
              api(this.cmsText)
                .then(() => {
                  this.loadingState = false
                  Toast('保存成功')
                  this.$router.go(this.cmsId ? -2 : -1)
                })
                .catch(() => {
                  this.loadingState = false
                  Toast('保存失败，请重试')
                })
            })
            .catch(() => {
              this.loadingState = false
            });

        }
      },

      queryCmsDetail() {
        this.loadingState = true
        try {
          getCmsInfo(this.cmsId).then(res => {
            const data = res.data.data
            this.cmsText.cmsTitle = data.cmsTitle
            this.cmsText.cmsContent = data.cmsContent
            this.cmsText.releaseStatus = data.releaseStatus
            this.releaseStatus = data.releaseStatus
            if (this.releaseStatus === 1) {
              this.calculateEditorHeight()
            }
            this.initEdit()
          })
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
.add-text-cms {
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
  background-color: #fff;
  margin: 0px 15px 10px 15px;
  box-shadow: 0px 2px 26px 0px rgba(3, 3, 3, 0.03);
  border-radius: 18px;
  display: flex;
  flex-direction: column;
}

.title-input {
  padding: 16px;
  border-top-left-radius: 18px;
  border-top-right-radius: 18px;
  border-bottom: 1px solid #f0f0f0;
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

/* TipTap 编辑器 */
.editor-container {
  border-bottom-left-radius: 18px;
  border-bottom-right-radius: 18px;
  background: #fff;
  display: flex;
  overflow: hidden;
}

.editor-content {
  width: 100%;
  height: 100%;
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  padding: 6px;
  overflow-y: auto;
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

/* 富文本编辑器 placeholder 样式 */
.editor-content :deep(.w-e-text-placeholder) {
    font-style: normal !important;
}
</style>
