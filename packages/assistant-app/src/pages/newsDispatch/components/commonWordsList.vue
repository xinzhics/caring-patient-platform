<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" rightIcon="add-o" @showpop="toAddCommonWords" title="常用语" @onBack="goback"></headNavigation>
    </van-sticky>
    <div style="margin-top: 10px;padding: 0 5px 0 5px">
      <van-pull-refresh v-model='refreshing' @refresh='onRefresh'>
        <van-list
          v-if="commonWordList.length > 0"
          v-model='loading'
          :finished='finished'
          finished-text='没有更多了'
          style="min-height: 500px;"
          @load='onLoad'>
          <van-swipe-cell right-width="60" v-for="(word, index) in commonWordList" :key="word.id" >
            <div class="list"  @click="editCommonWord(word)">
              <div class="box-left">{{ word.content }}</div>
              <div><van-icon name="arrow" /></div>
            </div>
            <template #right>
              <div style="display: flex;align-items: center;justify-content: center;height: 52px;margin-left: 24px">
                <div @click="deleteCommonWord(word, index)"  style="width: 30px;height: 30px;background: #fff;border-radius: 50%;text-align: center;line-height: 30px;color: #FF5555">
                  <van-icon name="delete" />
                </div>
              </div>
            </template>
          </van-swipe-cell>
        </van-list>
        <div v-if="!loading && commonWordList.length === 0" style="text-align: center; margin-top: 50px">
          <van-image :src="require('@/assets/noNews.png')" width="50%"/>
          <div>暂无数据</div>
        </div>
      </van-pull-refresh>
    </div>
    <div style="width: 90%;margin:0 auto ;position: fixed;bottom: 60px;left: 50%;transform: translateX(-50%)" >
      <van-button round  style="width: 100%;color:#FF5555 " type="default" @click="showClearCommonWord=true">清空</van-button>
    </div>
<!--    清空弹窗-->
    <van-dialog  confirm-button-color="#337EFF" @confirm="clearCommonWord"  v-model="showClearCommonWord"  show-cancel-button>
        <div style="padding: 35px 0 ;color:#666;text-align: center">是否清空常用语</div>
    </van-dialog>
<!--    删除弹窗-->
    <van-dialog @confirm="chooseOk" confirm-button-color="#337EFF"  v-model="show"  show-cancel-button>
        <div style="padding-top: 18px;text-align: center;color: #666666 ;font-size: 15px;">
          <img src="../../../assets/careful.png" style="width: 55px;margin-bottom: 17px" alt="">
          <div style="margin-bottom: 10px">确定删除吗？</div>
          <div style="margin-bottom: 21px">删除后内容不可恢复！</div>
        </div>
    </van-dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import {
  Col,
  Row,
  Icon,
  Checkbox,
  CheckboxGroup,
  Tab,
  Tabs,
  Field,
  SwipeCell,
  Dialog,
  Toast,
  PullRefresh,
  List,
  Button
} from 'vant'
import { pageCommonWord, deleteCommonWord, clearCommonWord } from '@/api/commonWords.js'
Vue.use(Icon)
Vue.use(Col)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(Row)
Vue.use(Button)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Field)
Vue.use(SwipeCell)
Vue.use(Dialog)
export default {
  data () {
    return {
      nursingId: localStorage.getItem('caringNursingId'),
      commonWordList: [],
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      localMessageId: this.$route.query.localMessageId, // 本地设置的群发信息的缓存 key
      deleteWordId: '',
      commonWordIndex: -1,
      active: 0,
      message: '',
      show: false,
      showClearCommonWord: false // 清空确认弹窗
    }
  },
  created () {
    this.onRefresh()
  },
  methods: {
    goback () {
      this.$router.replace({
        path: '/newsDispatch/commonWords',
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
     * 删除一个常用语
     */
    deleteCommonWord (word, index) {
      this.show = true
      this.commonWordIndex = index
      this.deleteWordId = word.id
    },
    /**
     * 跳转到设置常用语
     */
    toAddCommonWords () {
      this.$router.replace({
        path: '/newsDispatch/addCommonWords',
        query: {
          localMessageId: this.localMessageId
        }
      })
    },
    /**
     * 编辑常用语
     */
    editCommonWord (word) {
      this.$router.replace({
        path: '/newsDispatch/addCommonWords',
        query: {
          commonWordId: word.id,
          localMessageId: this.localMessageId
        }
      })
    },
    /**
     * 清空常用语
     */
    clearCommonWord () {
      clearCommonWord(this.nursingId).then(res => {
        if (res.code === 0) {
          this.commonWordList = []
        }
      })
    },
    /**
     * 弹窗点击确定
     */
    chooseOk () {
      deleteCommonWord(this.deleteWordId).then(res => {
        if (res.code === 0) {
          Toast({message: '删除成功', closeOnClick: true, duration: 1500})
        }
        this.deleteWordId = ''
        this.show = false
        if (this.commonWordIndex > -1) {
          this.commonWordList.splice(this.commonWordIndex, 1)
          this.commonWordIndex = -1
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.list{
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
/deep/.van-dialog{
  border-radius: 7px !important;
}
</style>
