<template>
  <section class="allContent">
    <div>
      <van-sticky>
        <headNavigation  leftIcon="arrow-left" title="常用语设置" @onBack="back" rightIcon="add-o" @showpop="Jump"></headNavigation>
      </van-sticky>
      <div style="padding-top: 60px">
        <div v-if="list.length > 0">
          <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
            <van-list
              v-model="loading"
              :finished="finished"
              offset="50"
              finished-text="没有更多了"
              @load="getData">
              <van-cell v-for="(item, i) in list" :key="i" :value="item.content" @click="JumpEdit(item)"/>
            </van-list>
          </van-pull-refresh>
        </div>
        <div v-else>
          <van-empty description="暂无常用语"/>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import {List, Cell, Empty} from 'vant'
import {pageCommonWord} from '@/api/commonWords.js'

export default {
  name: 'commonSetting',
  components: {
    [List.name]: List,
    [Cell.name]: Cell,
    [Empty.name]: Empty
  },
  data () {
    return {
      list: [],
      loading: false,
      finished: false,
      refreshing: false,
      count: 1
    }
  },
  methods: {
    // 后退
    back () {
      this.$router.replace({
        path: '/common/commonList',
        query: {
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
          formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
        }
      })
    },
    // 跳转到添加
    Jump () {
      this.$router.replace({
        path: '/common/commonAdd',
        query: {
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
          formPage: this.$route.query.formPage ? this.$route.query.formPage : ''
        }
      })
    },
    // 跳转到编辑
    JumpEdit (item) {
      this.$router.replace({
        path: '/common/commonEdit',
        query: {
          id: item.id,
          content: item.content,
          imAccount: this.$route.query.imAccount,
          imPatientId: this.$route.query.imPatientId,
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
      this.count = 1
      this.getData()
    },
    getData () {
      let params = {
        model: {
          accountId: localStorage.getItem('caringNursingId'),
          userType: 'NursingStaff'
        },
        current: this.count,
        size: 30
      }

      pageCommonWord(params)
        .then(res => {
          if (res.records) {
            setTimeout(() => {
              if (this.refreshing) {
                this.list = []
                this.refreshing = false
              }
              res.records.forEach(item => {
                this.list.push(item)
              })
              this.loading = false
              if (res.records.length !== 30) {
                this.finished = true
              } else {
                this.count++
              }
            }, 100)
          }
        })
    }
  },
  created () {
    this.getData()
  }
}
</script>

<style scoped lang='less'>

  .allContent {
    width: 100vw;
    height: 100vh;
    background-color: #fafafa;
  }

</style>
