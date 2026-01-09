<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left"
                      :showRightText="true"
                      rightText="常用语设置"
                      @showpop="toAddCommonWords"
                      title="选择常用语"
                      :leftSpanWidth="6"
                      :rightSpanWidth="6"
                      :centerSpanWidth="12"
                      @onBack="goback"></headNavigation>
    </van-sticky>
    <van-pull-refresh v-model='refreshing' @refresh='onRefresh'>
      <van-list
        v-if="commonWordList.length > 0"
        v-model='loading'
        :finished='finished'
        finished-text='没有更多了'
        @load='onLoad'>
        <div style="margin-top: 10px;padding: 0 5px 0 5px" >
          <div class="list" v-for="word in commonWordList" :key="word.id" @click="chooseWord(word)">
            <div class="box-left">{{ word.content }}</div>
            <div><van-icon name="arrow" /></div>
          </div>
        </div>
      </van-list>
      <div v-if="!loading && commonWordList.length === 0" style="text-align: center; margin-top: 50px">
        <van-image :src="require('@/assets/noNews.png')" width="70%"/>
        <div>暂无数据</div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Checkbox, CheckboxGroup, Tab, Tabs, Field, PullRefresh, List, Sticky} from 'vant'
import { pageCommonWord } from '@/api/commonWords.js'
Vue.use(Icon)
Vue.use(Col)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(Row)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Sticky)
Vue.use(Field)
export default {
  data () {
    return {
      nursingId: localStorage.getItem('caringNursingId'),
      commonWordList: [],
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      localMessageId: this.$route.query.localMessageId // 本地设置的群发信息的缓存 key
    }
  },
  created () {
    this.onRefresh()
  },
  methods: {
    goback () {
      this.$router.push({
        path: '/newsDispatch/content',
        query: {
          localMessageId: this.localMessageId
        }
      })
    },
    onLoad () {
      const params = {
        'current': this.current,
        'model': {
          'accountId': this.nursingId,
          'userType': 'NursingStaff'
        },
        'order': 'descending',
        'size': 20,
        'sort': 'id'
      }
      pageCommonWord(params).then(res => {
        if (res.data) {
          this.commonWordList.push(...res.data.records)
          if (res.data.pages === 0 || res.data.pages === this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
        this.loading = false
        this.refreshing = false
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.commonWordList = []
      this.current = 1
      this.loading = true
      this.onLoad()
    },
    /**
     * 选择常用语
     */
    chooseWord (word) {
      this.$router.push({
        path: '/newsDispatch/content',
        query: {
          localMessageId: this.localMessageId,
          wordContent: word.content
        }
      })
    },

    /**
     * 跳转到设置常用语
     */
    toAddCommonWords () {
      this.$router.push({
        path: '/newsDispatch/commonWordsList',
        query: {
          localMessageId: this.localMessageId
        }
      })
    }
  }
}
</script>

<style scoped>
.list {
  height: 52px;
  background: #ffffff;
  padding-left: 10px;
  padding-right: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  border-radius: 5px;
  font-size: 15px;
  color: #666666;
}

.box-left{
  width: 95%;
  color: #999999;
  display: -webkit-box; /** 对象作为伸缩盒子模型显示 **/
  overflow: hidden;  /* 超出隐藏 */
  text-overflow: ellipsis;  /* 超出部分省略号 */
  word-break: break-all;  /* break-all(允许在单词内换行。) */
  -webkit-box-orient: vertical; /** 设置或检索伸缩盒对象的子元素的排列方式 **/
  -webkit-line-clamp: 2; /** 显示的行数 **/
}
</style>
