<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left"  title="药品库" @onBack="$router.go(-1)"></headNavigation>
      <van-search
        v-model="inputVal"
        show-action
        :clearable="true"
        placeholder="请输入药品名"
        @search="onSearch"
        @clear="onClear"
      >
        <template #action>
          <div @click="onSearch">搜索</div>
        </template>
      </van-search>
    </van-sticky>
    <div class="main">
      <div style="height: 100%">
        <div v-if="listData && listData.length >=1 && statusAll.hasShowRecommendDrugs === 0 && !searchStatus">
          <p style="padding:10px 16px">推荐用药</p>
          <div>
            <van-cell is-link v-for="(i,k) in listData" :key="k" @click.native="goItem(i)">
              <div style="display: flex;margin:10px 0px;">
                <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                  <img v-if="i.icon" :src="i.icon" style="width:100%" alt="" srcset="">
                </div>
                <div style="margin-left:10px;line-height:24px">
                  <p style="font-size:16px;color:rgba(102,102,102,1);"><span v-if="i.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{ i.name }}
                  </p>
                  <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.genericName }}</p>
                  <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.manufactor }}</p>
                  <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ i.spec }}</p>
                </div>
              </div>
            </van-cell>
          </div>
        </div>
      </div>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #fafafa;">
        <van-list
          v-model="loading"
          :finished="finished"
          offset="600"
          @load="onLoad">
          <div>
            <p v-if="allData && allData.length >= 1" style="padding:10px 16px">所有药品</p>
            <div class="nodata" v-if=" allData && allData.length < 1" >
              <img :src="noData" alt="" @click="onRefresh()">
              <p style="color:#9d9d9d">暂无数据,点击刷新</p>
              <p style="color:#9d9d9d">您可点击下方按钮自定义添加</p>
              <p style="width:70%;margin:20px auto;padding:10px 0px;border-radius:30px;background:#ffbe8b;color:#fff;" @click="$router.push('/patient/medicine/addMedicine')">没有我的药品？</p>
            </div>
            <van-cell is-link v-for="(i,k) in allData" :key="k" @click.native="goItem(i)">
              <div style="display: flex;margin:10px 0px;">
                <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                  <img v-if="i.icon" :src="i.icon" style="width:100%" alt="" srcset="">
                </div>
                <div style="margin-left:10px;line-height:24px">
                  <p style="font-size:16px;color:rgba(102,102,102,1);">
                    <span v-if="i.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{ i.name }}
                  </p>
                  <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.genericName }}</p>
                  <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.manufactor }}</p>
                  <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ i.spec }}</p>
                </div>
              </div>
            </van-cell>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>
<script>
import Vue from 'vue'
import {Search, Row, Col, Sticky, Icon, Cell, List, PullRefresh} from 'vant'
import {getRegGuide} from '@/api/regGuideApi.js'
import {listRecommendDrugs, sysDrugsPage} from '@/api/drugsApi.js'
import {Base64} from 'js-base64'

Vue.use(List)
Vue.use(PullRefresh)
Vue.use(Search)
Vue.use(Sticky)
Vue.use(Icon)
Vue.use(Row)
Vue.use(Col)
Vue.use(Cell)
export default {
  data () {
    return {
      noData: require('@/assets/patient/medicineImg.png'),
      scan: require('@/assets/patient/saoma.png'),
      inputVal: '',
      statusAll: {},
      allData: [], // 可见药品
      listData: [], // 推荐药品
      current: 1,
      searchStatus: false,
      loading: true,
      finished: false,
      refreshing: false
    }
  },
  mounted () {
    this.onRefresh()
    this.getList()
    this.getStatus()
  },
  methods: {
    getStatus () {
      getRegGuide().then((res) => {
        if (res.code === 0) {
          this.statusAll = res.data
        }
      })
    },
    onClear () {
      this.inputVal = ''
    },
    /**
     * 搜索状态控制
     */
    onSearch () {
      if (this.inputVal) {
        this.searchStatus = true
      } else {
        this.searchStatus = false
      }
      this.onRefresh()
    },
    onRefresh () {
      this.finished = false
      this.current = 1
      this.allData = []
      this.onLoad()
    },
    onLoad () {
      this.loading = true
      const code = Base64.decode(localStorage.getItem('tenantCode'))
      console.log('code', code)
      const params = {
        tenant: Base64.decode(localStorage.getItem('tenantCode')),
        current: this.current,
        model: {
          name: this.inputVal
        },
        size: 15
      }
      sysDrugsPage(params).then((res) => {
        this.loading = false
        this.refreshing = false
        if (res.code === 0) {
          this.allData.push(...res.data.records)
          if (res.data.pages === 0 || res.data.pages === this.current) {
            console.log(res.data.pages, this.current)
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
      })
    },
    getList () {
      const params = {
        code: Base64.decode(localStorage.getItem('tenantCode'))
      }
      listRecommendDrugs(params).then((res) => {
        if (res.code === 0) {
          this.listData = res.data
        }
      })
    },
    goItem (k) {
      if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.replace({
          path: '/patient/medicine/addmedicine',
          query: {drugsId: k.id, imMessageId: this.$route.query.imMessageId}
        })
      } else {
        this.$router.replace({
          path: '/patient/medicine/addmedicine',
          query: {drugsId: k.id}
        })
      }
    }
  }
}
</script>
<style lang="less" scoped>
.main {
  width: 100vw;
  background: #f5f5f5;
  p {
    padding: 0;
    margin: 0;
  }
}
.nodata {
  width: 100%;
  text-align: center;
  padding: 50px 0px;
  background: #f5f5f5;
}

</style>
