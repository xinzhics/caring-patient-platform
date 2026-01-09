<template>
  <section class="allContent">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" title="常用语" @onBack="back"></headNavigation>
      <van-search
        v-model="searchKeyWord"
        shape="round"
        show-action
        :clearable="false"
        placeholder="请输入常用语的标题、常用语内容">
        <template slot="right-icon">
          <van-icon v-if="searchKeyWord" name="close" @click="onClear"/>
        </template>
        <template slot="action">
          <div @click="searchButtonClick"
               style="padding: 0 15px;color: var(--caring-search-action); font-size: 13px; font-weight: 500; font-family: Source Han Sans CN, Source Han Sans CN;">
            搜索
          </div>
        </template>
      </van-search>
      <van-tabs v-model="typeAction" @click="clickCommonType" title-active-color="var(--caring-tab-action-color)"
                color="var(--caring-tab-action-color)">
        <van-tab :title="type.title" v-for="(type, index) in commonTypeList" :name="type.id" :key="index"></van-tab>
      </van-tabs>
    </van-sticky>
    <div>
      <div style="background: var(--caring-body-bg); ">
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh"
                          style="overflow-y: auto; ">
          <van-list
            v-if="list && list.length > 0 || loading"
            v-model="loading"
            :finished="finished"
            offset="500"
            finished-text="没有更多了"
            @load="getData">
            <div v-for="(item, i) in list" :key="i" style="padding-top: 10px;">
              <van-swipe-cell>
                <van-row style="background-color: var(--caring-common-bg-color)">
                  <!-- 标题 -->
                  <van-col :span="24">
                    <div class="common-title">
                      <div class="common-title-icon">标题</div>
                      <div style="margin-left: 13px;display: block; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
                           class="mate-content-highlight">
                        {{ item.title ? item.title : '请输入标题' }}
                      </div>
                      <div @click="editJump(item)" style="display: flex; align-items: center; margin-left: 5px;">
                        <img :src="require('@/assets/common/commonEdit.png')" style="width: 18px; height: 18px;">
                      </div>
                    </div>
                  </van-col>
                  <!-- 内容-->
                  <van-col :span="24">
                    <div style="padding: 12px 33px 20px 21px;">
                      <div class="common-content line-breaks-3 mate-content-highlight" :id="'content' + item.id"
                           :style="{webkitLineClamp: item.showOpenContent === undefined ? 3 : item.showOpenContent ? 3 : 'unset'}"
                           @click="itemClick(item)">{{ item.content }}
                      </div>
                      <div v-if="item.showOpenContent" @click="openAllContent(i)"
                           style="text-align: center; margin-top: 20px">
                        <span class="open-content-button-text">展开查看全文</span>
                        <van-icon style="margin-left: 5px" name="arrow-down"
                                  color="var(--caring-common-open-content-a-text)"/>
                      </div>
                    </div>
                  </van-col>
                </van-row>

                <template slot="right">
                  <div style="height: 100%; display: flex; align-items: center">
                    <div class="delBox" @click="delCom(item, i)">
                      <van-icon name="delete-o" color="red" size="18"/>
                    </div>
                  </div>
                </template>
              </van-swipe-cell>
            </div>
          </van-list>
          <div class="noData" v-else-if="list.length === 0 && !loading">
            <div style="width:143px ;margin:132px auto">
              <van-image :src="require('@/assets/my/no_data.png')" width="70%"/>
              <div style="color: #999999;font-size: 15px;text-align: center;margin-top: 21px">暂无数据</div>
            </div>
          </div>

        </van-pull-refresh>
      </div>
      <div class="botbtn">
        <van-button type="info"
                    round
                    plain
                    style="width: 50%; height: 34px; margin: 0 10px; color: var(--caring-common-button-plain-text); border-color: var(--caring-common-button-plain-border)"
                    @click="addJump()">
          自定义常用语
        </van-button>
        <van-button type="info"
                    round
                    style="width: 50%; height: 34px; margin: 0 10px; border-color: var(--caring-common-button-default-bg); background-color: var(--caring-common-button-default-bg)"
                    @click="templateJump()">
          从模板库添加
        </van-button>
      </div>
    </div>
    <!--  删除  -->
    <van-dialog v-model="isDeleteDialog" show-cancel-button confirmButtonColor="#2F8DEB"
                @confirm="confirmDelete()"
                @cancel="() => {isDeleteDialog = false}"
                confirmButtonText="确定">
      <div style="display: flex; flex-direction: column; align-items: center; padding: 20px 30px">
        <img :src="require('@/assets/my/careful.png')" style="margin-left: 5px" width="60" height="60">
        <div
          style="font-size: 18px; color: #333333; margin-top: 12px; margin-bottom: 5px; margin-left: 5px">
          确定删除吗？
        </div>
        <div style="color: #333; font-size: 18px; text-align: center">
          删除后内容不可恢复！
        </div>
      </div>
    </van-dialog>
  </section>
</template>

<script>
import Vue from 'vue'
import {List, Cell, Empty, SwipeCell, Dialog, Search, Tabs, Tab, Sticky, Toast, Button, PullRefresh, Image} from 'vant'
import {newPageCommonList, deleteCommonWord, commonTypeMy} from '@/api/commonWords.js'

Vue.use(Search)
Vue.use(Sticky)
Vue.use(PullRefresh)
Vue.use(Button)
Vue.use(Image)
Vue.use(Tabs)
Vue.use(Tab)
Vue.use(Toast)
Vue.use(Dialog)
export default {
  name: 'commonList',
  components: {
    [List.name]: List,
    [Cell.name]: Cell,
    [Empty.name]: Empty,
    [SwipeCell.name]: SwipeCell
  },
  data () {
    return {
      list: [],
      actionText: '搜索', // 搜索结束后，变为取消。 搜索关键词变化时，变为搜索
      searchKeyWord: null, // 用户输入的搜索名称
      commonTypeList: [{id: undefined, title: '全部'}], // 用户常用语的分类
      typeAction: undefined, // 分类选择的下标
      loading: false,
      finished: false,
      refreshing: false,
      count: 1,
      isDeleteDialog: false,
      commonId: '',
      pos: 0,
      nursingId: undefined,
      userType: 'NursingStaff'
    }
  },
  created () {
    this.refreshing = true
    this.nursingId = localStorage.getItem('caringNursingId')
    this.getData()
    this.queryDoctorCommonTypeList()
  },
  methods: {
    // 搜索按钮的点击
    searchButtonClick () {
      if (this.searchKeyWord) {
        this.$router.push({
          path: '/common/commonSearchResult',
          query: {
            imAccount: this.$route.query.imAccount,
            imPatientId: this.$route.query.imPatientId,
            searchKeyWord: this.searchKeyWord,
            message: this.$route.query.message,
            formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
          }
        })
      } else {
        this.onRefresh()
      }
    },
    // 清空搜索框内容
    onClear () {
      this.searchKeyWord = undefined
      console.log('onClear', this.searchKeyWord)
    },
    // 点击常用语的分类
    clickCommonType () {
      this.onRefresh()
    },
    // 查询医生的常用语模版分类
    queryDoctorCommonTypeList () {
      if (!this.nursingId) {
        return
      }
      commonTypeMy(this.nursingId, this.userType).then(res => {
        if (res.code === 0) {
          if (res.data) {
            this.commonTypeList.push(...res.data)
          }
        }
      })
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

    // 删除
    confirmDelete () {
      deleteCommonWord(this.commonId)
        .then(res => {
          Toast({message: '删除成功', closeOnClick: true, position: 'bottom'})
          this.$delete(this.list, this.pos)
        })
    },
    // 跳转到模板库
    templateJump () {
      this.$router.replace({
        path: '/common/commonTemplate',
        query: {
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
          message: this.$route.query.message,
          formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
        }
      })
    },
    // 删除常用语
    delCom (item, index) {
      if (item.id === '') {
        Toast({message: '未查询到该常用语', closeOnClick: true, position: 'bottom'})
        return
      }
      this.commonId = item.id
      this.pos = index
      this.isDeleteDialog = true
    },
    // 返回界面
    itemClick (item) {
      // this.$router.replace({
      //   path: '/im/index',
      //   query: {
      //     imAccount: this.$route.query.imAccount,
      //     imPatientId: this.$route.query.imPatientId,
      //     content: this.$route.query.message ? this.$route.query.message + item.content : item.content,
      //     formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
      //   }
      // })
      this.$h5Close(item.content)
    },

    // 后退
    back () {
      this.$h5Close()
      // this.$router.replace({
      //   path: '/im/index',
      //   query: {
      //     imAccount: this.$route.query.imAccount,
      //     imPatientId: this.$route.query.imPatientId,
      //     formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
      //   }
      // })
    },
    // 跳转到添加
    addJump () {
      this.$router.replace({
        path: '/common/commonAdd',
        query: {
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
          message: this.$route.query.message,
          formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
        }
      })
    },
    // 跳转到编辑
    editJump (item) {
      this.$router.replace({
        path: '/common/commonAdd',
        query: {
          id: item.id,
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
          message: this.$route.query.message,
          formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
        }
      })
    },
    // 下拉刷新
    onRefresh () {
      // 清空列表数据
      this.finished = false
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true
      this.refreshing = false
      this.list = []
      this.count = 1
      this.getData()
    },
    getData () {
      let params = {
        model: {
          typeId: this.typeAction === 0 ? undefined : this.typeAction,
          content: this.searchKeyWord,
          accountId: this.nursingId,
          userType: this.userType
        },
        current: this.count,
        size: 10
      }
      newPageCommonList(params)
        .then(res => {
          if (res.data.records) {
            res.data.records.forEach(item => {
              this.list.push(item)
              this.showOpenContentButton(item)
            })
            if (this.count >= res.data.pages) {
              this.finished = true
            }
            this.loading = false
            this.refreshing = false
            if (this.searchKeyWord) {
              this.mateSearchKeyWordHighlight()
            }
            this.count++
          }
        })
    }
  }
}
</script>

<style lang='less' scoped src="./commonClass.less">
</style>
<style scoped lang='less'>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #f5f5f5;

  .noData {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    font-size: 13px;
    color: #999;
    padding-top: 80px;
  }

  .botbtn {
    position: fixed;
    bottom: 20px;
    display: flex;
    justify-content: center;
    width: 100%
  }

  .delBox {
    width: 50px;
    height: 50px;
    background: #fff;
    margin-left: 10px;
    border-radius: 25px;
    display: flex;
    justify-content: center;
    align-items: center
  }
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}

/deep/ .van-cell__value {
  margin-right: 10px;
}

/deep/ .van-cell {
  border-radius: 8px;
}

</style>
