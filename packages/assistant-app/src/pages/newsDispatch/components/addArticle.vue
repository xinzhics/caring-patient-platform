<template>
  <div>
    <van-search
      v-model="title"
      shape="round"
      show-action
      background="#F5F5F9"
      placeholder="请输入文章标题关键字"
      @search="onSearch"
    >
      <template #action>
        <div @click="onSearch">搜索</div>
      </template>
    </van-search>
    <div>
      <van-tabs color="#3F86FF" title-active-color="#3F86FF" v-model="channelId">
        <van-tab v-for="channel in channelList" :key="channel.id" :name="channel.id" :title="channel.channelName">
          <van-pull-refresh v-model='channel.refreshing' @refresh='onRefresh(channel)'>
            <van-list
              v-model='channel.loading'
              :finished='channel.finished'
              finished-text='没有更多了'
              @load='pageContentList(channel)'>
              <div v-for="content in channel.contentList" :key="content.id" @click="goArticleDetails(content)"
                   style="border-top: 1px solid #E5E5E5;height: 61px;padding: 13px 17px 13px 14px;background: #ffffff">
                <div style="display: flex;justify-content: space-between;align-items: center">
                  <div>
                    <div style="color: #333333;font-size: 16px;margin-bottom: 9px" class="box-text">{{ content.title }}</div>
                    <div class="box-text">{{ content.summary }}</div>
                  </div>
                  <div>
                    <img :src="content.icon" style="width: 65px;height: 65px" alt="">
                  </div>
                </div>
              </div>
            </van-list>
          </van-pull-refresh>
        </van-tab>
      </van-tabs>
    </div>
    <Dialog @showPop="showPop" :localMessageId="localMessageId" :submit-message="submitMessage" :show="dialogShow" :people-number="peopleNumber"></Dialog>
  </div>
</template>

<script>
import Dialog from './Dialog'
import Vue from 'vue'
import {Search, Button, List, Col, Row, Icon, PullRefresh} from 'vant'
import { channelList, contentList } from '@/api/cms.js'

Vue.use(Search)
Vue.use(Button)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(Col)
Vue.use(Row)
Vue.use(Icon)
export default {
  components: {
    Dialog
  },
  data () {
    return {
      submitMessage: {},
      peopleNumber: 0,
      dialogShow: false,
      nursingId: localStorage.getItem('caringNursingId'),
      channelId: 'all',
      localMessageId: this.$route.query.localMessageId, // 本地设置的群发信息的缓存 key
      channelList: [{
        id: 'all',
        channelName: '全部',
        currentPage: 1,
        loading: false,
        finished: false,
        refreshing: false
      }],
      title: ''
    }
  },
  created () {
    this.queryChannelList()
  },
  methods: {
    queryChannelList () {
      const params = {
        'current': 1,
        'model': {
          'ownerType': 'TENANT',
          'doctorOwner': false
        },
        'size': 200
      }
      channelList(params).then(res => {
        console.log(res.data)
        if (res.data.records) {
          res.data.records.forEach(item => {
            item.currentPage = 1
            item.loading = false
            item.finished = false
            item.refreshing = false
          })
          this.channelList.push(...res.data.records)
        }
      })
    },

    /**
     * 搜索
     */
    onSearch () {
      console.log(this.channelId, this.title)
      let channel = this.channelList.find(item => item.id === this.channelId)
      channel.currentPage = 1
      this.onRefresh(channel)
    },
    /**
     * 加载对应栏目下的文章列表
     * @param channel
     * @param title
     */
    pageContentList (channel) {
      if (!channel.currentPage) {
        channel.currentPage = 1
      }
      const channelId = channel.id === 'all' ? undefined : channel.id
      const params = {
        'current': channel.currentPage,
        'model': {
          'channelType': 'Article',
          'title': this.title,
          'channelId': channelId,
          'doctorOwner': false
        },
        'size': 20
      }
      if (!channel.contentList) {
        channel.contentList = []
      }
      contentList(params).then(res => {
        console.log(res.data)
        channel.contentList.push(...res.data.records)
        if (res.data.pages === 0 || res.data.pages === channel.currentPage) {
          channel.finished = true
        } else {
          channel.finished = false
          channel.currentPage++
        }
        channel.loading = false
        channel.refreshing = false
      })
    },
    /**
     * 下载刷新。或者标题搜索
     * @param channel
     * @param title
     */
    onRefresh (channel) {
      channel.finished = false
      channel.contentList = []
      channel.currentPage = 1
      channel.loading = true
      this.pageContentList(channel)
    },

    showPop () {
      this.dialogShow = false
      console.log('关闭了窗口')
    },
    /**
     * 去文章详情页面
     * @param content
     */
    goArticleDetails (content) {
      if (content.link) {
        const localMessageJSONString = localStorage.getItem(this.localMessageId)
        const localMessage = JSON.parse(localMessageJSONString)
        const params = {}
        this.peopleNumber = localMessage.peopleNumber
        params.receiverId = localMessage.receiverId
        params.senderId = this.nursingId
        params.type = 'cms'
        let cms = {
          id: content.id,
          title: content.title,
          icon: content.icon,
          link: content.link,
          summary: content.summary
        }
        params.content = JSON.stringify(cms)
        this.submitMessage = params
        this.dialogShow = true
      } else {
        this.$router.push({
          path: '/newsDispatch/articleDetails',
          query: {
            localMessageId: this.localMessageId,
            contentId: content.id
          }
        })
      }
    }
  }
}
</script>

<style scoped>
/deep/ .van-search__content {
  background: #fff !important;
  border-radius: 4px !important;
}
.box-text{
  /*width: 20%;*/
  width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 13px;
  color: #999999;
}
</style>
