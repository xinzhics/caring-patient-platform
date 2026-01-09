<template>
  <section class="allContent">
    <van-sticky>
      <headNavigation v-if="!searchModel" leftIcon="arrow-left" title="常用语模板库" @onBack="back"></headNavigation>
      <div style="display: flex; background-color: white; align-items: center" v-if="!searchModel">
        <div class="common-template-search-button" @click="onSearch">搜索</div>
        <div style="width: calc(100% - 87px);">
          <van-search
            v-model="searchKeyWord"
            shape="round"
            :clearable="false"
            placeholder="请输入常用语的标题、常用语内容"
            @search="onSearch">
            <template slot="right-icon">
              <van-icon v-if="searchKeyWord" name="close" @click="onClear" />
            </template>
          </van-search>
        </div>
        <div style="width: 33px; display: flex;justify-content: center;align-items: center;" @click="openDialog()">
          <img style="width: 13px;height: 13px" :src="require('@//assets/common/common_expand.png')" alt="">
        </div>
      </div>
      <van-search
        v-if="searchModel"
        v-model="searchKeyWord"
        shape="round"
        :action-text="actionText"
        show-action
        :clearable="false"
        placeholder="请输入常用语的标题、常用语内容"
        @input="searchInput"
        @cancel="onCancelSearch"
        @search="onRefresh">
        <template slot="right-icon">
          <van-icon v-if="searchKeyWord" name="close" @click="onClear" />
        </template>
        <template slot="action">
          <div @click="searchButtonClick" style="padding: 0 15px;color: var(--caring-search-action); font-size: 13px; font-weight: 500; font-family: Source Han Sans CN, Source Han Sans CN;">{{actionText}}</div>
        </template>
      </van-search>
      <van-tabs v-if="!searchModel" swipeable :lazy-render="false" v-model="active" @change="clickTab" :ellipsis="false" title-active-color="var(--caring-tab-action-color)" color="var(--caring-tab-action-color)">
        <van-tab :title="type.typeName" v-for="(type, index) in typeList" :key="index"></van-tab>
      </van-tabs>
    </van-sticky>
    <div style="background: var(--caring-body-bg); ">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="overflow-y: auto; ">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          :offset="200"
          @load="getComMsgTemplateList"
        >
          <div v-for="(item, i) in list" :key="i" style="padding-top: 10px;" v-if="list && list.length > 0">
            <div style="background-color: var(--caring-common-bg-color)" :style="{    boxShadow: selectImportTemplateIds.includes(item.id) ? '0px 2px 4px #a9a9a9' : 'unset'}">
              <!-- 标题 -->
              <div :span="24">
                <div class="common-title" style="display: flex"  @click="selectTemplate(item.id, item.existed)">
                  <div style="margin-right: 8px; display: flex;align-items: center;">
                    <div v-if="item.existed === 1"
                         style="width: 15px; height: 15px; border-radius: 3px; display: flex; justify-content: center;
                          align-items: center; background-color: var(--caring-common-disable-select); border: 3px solid var(--caring-common-disable-select)">
                      <!-- 已经导入的禁止选择 -->
                      <van-icon name="success" color="#FFFFFF"/>
                    </div>
                    <div v-else-if="selectImportTemplateIds.indexOf(item.id) > -1"
                         style="width: 15px; height: 15px; border-radius: 3px; display: flex; justify-content: center;
                         align-items: center; background-color: var(--caring-common-title-icon-bg); border: 3px solid var(--caring-common-title-icon-bg)">
                      <!-- 选中时 -->
                      <van-icon name="success" color="#FFFFFF"/>
                    </div>
                    <div v-else style="width: 15px; height: 15px; border-radius: 3px; border: 3px solid var(--caring-common-no-select)">
                      <!-- 未选择时 -->
                    </div>
                  </div>
                  <div>
                    <div class="common-title-content">
                    <div class="common-title-icon"
                          :style="{backgroundColor: item.existed === 1 ? 'var(--caring-common-disable-select)'
                           : selectImportTemplateIds.indexOf(item.id) > -1 ? ''
                           : 'var(--caring-common-no-select)' }  ">
                      标题
                    </div>
                    <div style="margin-left: 13px" class="mate-content-highlight"
                          :style="{color: item.existed === 1 ? 'var(--caring-common-disable-select)'
                           : selectImportTemplateIds.indexOf(item.id) > -1 ? ''
                           : 'var(--caring-common-no-select)' }">
                      {{item.templateTitle}}
                    </div>
                  </div>
                  </div>
                </div>
              </div>
              <!-- 内容-->
              <div :span="24">
                <div style="padding: 12px 33px 20px 21px;">
                  <div class="common-content line-breaks-3 mate-content-highlight" :id="'content' + item.id"
                       :style="{color: item.existed === 1 ? 'var(--caring-common-disable-select)' : selectImportTemplateIds.indexOf(item.id) > -1 ? '' : 'var(--caring-common-no-select)',
                           webkitLineClamp: item.showOpenContent === undefined ? 3 : item.showOpenContent ? 3 : 'unset'}"  @click="selectTemplate(item.id, item.existed)">{{item.templateContent}}</div>
                  <div v-if="item.showOpenContent" @click="openAllContent(i)"
                       style="text-align: center; margin-top: 20px">
                    <span class="open-content-button-text"
                          :style="{color: item.existed === 1 ? 'var(--caring-common-disable-select)'
                           : selectImportTemplateIds.indexOf(item.id) > -1 ? ''
                           : 'var(--caring-common-no-select)' }">
                      展开查看全文
                    </span>
                    <van-icon style="margin-left: 5px" name="arrow-down" :color="item.existed === 1 ? 'var(--caring-common-disable-select)'
                           : selectImportTemplateIds.indexOf(item.id) > -1 ? 'var(--caring-common-open-content-a-text)'
                           : 'var(--caring-common-no-select)'" /> </div>
                </div>
              </div>
            </div>
          </div>
        </van-list>
        <div v-if="showNoData" class="noData">
          <div style="width:143px ;margin:132px auto">
            <img :src="require('@/assets/my/no_data.png')" width="100%">
            <div style="color: #999999;font-size: 15px;text-align: center;margin-top: 21px">暂无数据</div>
          </div>
        </div>
      </van-pull-refresh>
    </div>
    <div style="margin-top: 30px; margin-bottom: 20px; width: 100%;position: fixed;bottom: 0;" v-if="selectImportTemplateIds.length > 0">
      <van-button type="info"
                  round
                  @click="importTemplateSubmit"
                  style="display: block; width: 95%; height: 42px; margin: 0 auto; border-color: var(--caring-common-button-default-bg); background-color: var(--caring-common-button-default-bg)"
      >
        确认添加
      </van-button>
    </div>

    <round-dialog  v-if="!searchModel" ref="rDialog" @selectConfirm="selectDialogConfirm"
                  :data-list="typeList"></round-dialog>
  </section>
</template>

<script>
import Vue from 'vue'
import { Tab, Tabs, Search, Icon, Toast, Sticky, PullRefresh, List } from 'vant'
import {importTemplateCommon, getComMsgTemplateList, getComMsgTemplateType} from '@/api/commonWords.js'
import roundDialog from './components/roundCommonDialog'
Vue.use(Search)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(Sticky)
Vue.use(Icon)
Vue.use(Toast)

export default {
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    roundDialog // 弹窗
  },
  name: 'commonTemplate',
  data () {
    return {
      isCallBack: this.$route.query.isCallBack,
      searchKeyWord: undefined,
      nursingId: undefined,
      searchModel: false, // 点击搜索进入搜索模式。搜索模式下不显示分类相关元素，不显示页面的标题。 只显示搜索框，搜索结果，取消按钮和 确认添加
      typeList: [],
      templateTypeId: undefined,
      selectImportTemplateIds: [],
      actionText: '取消',
      active: 0,
      params: {
        current: 1,
        size: 10,
        model: {
          remarkUserSelect: localStorage.getItem('caringNursingId'),
          userType: 'NursingStaff',
          templateTypeId: '',
          templateRelease: 1
        }
      },
      showNoData: false,
      list: [],
      loading: false,
      finished: false,
      refreshing: false
    }
  },
  created () {
    this.nursingId = localStorage.getItem('caringNursingId')
    this.params.model.remarkUserSelect = localStorage.getItem('caringNursingId')
    this.onRefresh()
    this.getComMsgTemplateType()
  },
  methods: {
    // 搜索的关键词修改
    searchInput () {
      if (this.searchKeyWord) {
        this.actionText = '搜索'
      } else {
        this.actionText = '取消'
      }
    },
    // 搜索内容
    onSearch () {
      // 切换到搜索模式。
      this.searchModel = true
      if (!this.searchKeyWord) {
        this.actionText = '搜索'
      }
      // 搜索内容时 要求取消分类搜索
      if (this.searchKeyWord) {
        this.onRefresh()
      }
    },
    // 取消搜索模式
    onCancelSearch () {
      this.searchModel = false
      this.searchKeyWord = undefined
      this.onRefresh()
    },
    // 搜索按钮的点击
    searchButtonClick () {
      let that = this
      setTimeout(() => {
        if (that.actionText === '取消') {
          that.onCancelSearch()
        } else {
          that.onRefresh()
          that.actionText = '取消'
        }
      }, 200)
    },
    onClear () {
      this.searchKeyWord = undefined
      this.actionText = '取消'
    },
    /**
     * 匹配用户搜索的内容，对齐进行高亮设置
     */
    mateSearchKeyWordHighlight () {
      let searchKeyWord = this.searchKeyWord
      const Reg = new RegExp(searchKeyWord, 'ig')
      const replaceHtml = `<span style="color: var(--caring-search-text-highlight)">${searchKeyWord}</span>`
      this.$nextTick(() => {
        const contentList = document.getElementsByClassName('mate-content-highlight')
        if (contentList) {
          for (let i = 0; i < contentList.length; i++) {
            const innerText = contentList[i].innerText
            const replacedContent = innerText.replace(Reg, replaceHtml)
            contentList[i].innerHTML = replacedContent
          }
        }
      })
    },
    /**
     * 提交已经选择的要导入的常用语
     */
    importTemplateSubmit () {
      if (this.selectImportTemplateIds.length <= 0) {
        Toast({
          duration: 1000, // 持续展示 toast
          closeOnClick: true,
          message: '请选择常用语模版'
        })
        return
      }
      if (this.isCallBack) {
        this.$router.replace({
          path: '/common/commonAdd',
          query: {
            templateContentId: this.selectImportTemplateIds[0],
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            message: this.$route.query.message,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
        return
      }
      console.log(this.selectImportTemplateIds)
      const number = this.selectImportTemplateIds.length
      const message = `您已成功添加${number}条常用语`
      importTemplateCommon(this.nursingId, 'NursingStaff', this.selectImportTemplateIds).then(res => {
        if (res.code === 0) {
          Toast({
            duration: 1000, // 持续展示 toast
            closeOnClick: true,
            message: message
          })
          this.back()
        }
      })
    },

    /**
     * 选择或取消选择常用语
     * @param id
     */
    selectTemplate (id, existed) {
      if (existed === 1) {
        return
      }
      if (this.isCallBack) {
        if (this.selectImportTemplateIds[0] === id) {
          this.selectImportTemplateIds = []
        } else {
          this.selectImportTemplateIds = [id]
        }
      } else if (this.selectImportTemplateIds.indexOf(id) > -1) {
        this.selectImportTemplateIds.splice(this.selectImportTemplateIds.indexOf(id), 1)
      } else {
        this.selectImportTemplateIds.push(id)
      }
    },

    // 点击展示全部
    openAllContent (index) {
      this.list[index].showOpenContent = false
      this.$forceUpdate()
    },

    // 判断是否要显示这个id 对应的常用语 的 展示查看全文
    showOpenContentButton (item) {
      this.$nextTick(() => {
        if (item.showOpenContent === undefined) {
          const content = document.getElementById('content' + item.id)
          const lineHeight = 20
          if (content.scrollHeight > lineHeight * 3 && content.offsetHeight <= lineHeight * 3) {
            item.showOpenContent = true
          } else {
            item.showOpenContent = false
          }
          this.$forceUpdate()
        }
      })
    },

    // 选择常用语分类
    selectDialogConfirm (pos) {
      this.active = pos
      this.params.current = 1
      this.list = []
      if (pos === 0) {
        // 点击了全部按钮
        this.params.model.templateTypeId = ''
      } else {
        // 其他按钮
        this.templateTypeId = this.typeList[pos].id
        this.params.model.templateTypeId = this.typeList[pos].id
      }
      console.log('selectDialogConfirm')
      this.getComMsgTemplateList()
    },
    onRefresh () {
      this.finished = false
      this.loading = true
      this.list = []
      this.params.current = 1
      this.refreshing = false
      console.log('onRefresh')
      this.getComMsgTemplateList()
    },
    // 常用语列表
    getComMsgTemplateList () {
      let params = this.params
      // 搜索模式下。不添加分类条件
      if (this.searchModel) {
        params.model.templateTypeId = undefined
      } else {
        this.params.model.templateTypeId = this.templateTypeId
      }
      if (this.isCallBack) {
        this.params.model.remarkUserSelect = undefined
      }
      params.model.searchKey = this.searchKeyWord
      getComMsgTemplateList(params)
        .then(res => {
          this.list.push(...res.data.records)
          res.data.records.forEach(item => {
            this.showOpenContentButton(item)
          })
          if (this.params.current >= res.data.pages) {
            this.finished = true
          }
          this.loading = false
          this.refreshing = false
          if (this.list.length === 0) {
            this.showNoData = true
          } else {
            this.showNoData = false
          }
          if (this.searchKeyWord) {
            this.mateSearchKeyWordHighlight()
          }
          this.params.current++
        })
    },
    // 获取常用语分类
    getComMsgTemplateType () {
      let data = {
        userType: 'NursingStaff'
      }
      getComMsgTemplateType(data)
        .then(res => {
          this.typeList.push({typeName: '全部'})
          res.data.forEach(item => {
            this.typeList.push(item)
          })
        })
    },
    // 打开弹窗
    openDialog () {
      console.log('openDialog')
      this.$refs.rDialog.openDialog()
      this.$refs.rDialog.setPos(this.active)
    },
    // 标签点击
    clickTab (name, title) {
      if (title === '全部') {
        // 点击了全部按钮
        this.templateTypeId = undefined
        this.params.current = 1
        this.onRefresh()
      } else {
        // 其他按钮
        this.templateTypeId = this.typeList[name].id
        this.params.current = 1
        this.onRefresh()
      }
    },
    // 返回
    back () {
      if (this.isCallBack) {
        this.$router.replace({
          path: '/common/commonAdd',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            message: this.$route.query.message,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      } else {
        this.$router.replace({
          path: '/common/commonList',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            message: this.$route.query.message,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      }
    }
  }

}
</script>

<style lang='less' scoped  src="./commonClass.less">
</style>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #f5f5f5;

  .noData {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 150px;
  }
}

</style>
