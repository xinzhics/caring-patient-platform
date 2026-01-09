<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '监测数据'" @onBack="back"></headNavigation>
    </van-sticky>
    <div style='margin-top: 13px'>
      <div v-for="(item, index) in list" :key="index">
        <div class="item" @click="detailBtn(item, index)">
          <div>
            <p class="title">{{item.name}}</p>
            <p class="content">{{item.planDesc}}</p>
            <van-button class="watchBtn" round size="small">查看</van-button>
          </div>
          <div class="icon" style="width: 170px; height: 170px">
            <div style="position: relative;">
              <van-image width="170" height="170" fit="cover" lazy-load
                         :src="require('@/assets/patient/test_data_bj.png')"/>
              <van-image fit="cover" width="45px" height="45px"
                         style="position: absolute; right: 0; bottom: 0; margin-right: 25px; margin-bottom: 25px;"
                         lazy-load
                         :src="item.planIcon ? item.planIcon :   item.planType === 1 ?
                          require('@/assets/patient/test_data_xueya.png') :  item.planType === 2 ?
                          require('@/assets/patient/test_data_xuetang.png') : require('@/assets/patient/test_data_other.png')"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listPatientMonitoringDataPlan } from '@/api/plan.js'
import {Row, Col, Image, Toast, Sticky, Icon, Button, Lazyload} from 'vant'
import Vue from 'vue'
Vue.use(Toast)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Image)
Vue.use(Col)
Vue.use(Icon)
Vue.use(Lazyload)
Vue.use(Button)
export default {
  data () {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      list: []
    }
  },
  created () {
    listPatientMonitoringDataPlan()
      .then(res => {
        this.list = []
        if (res && res.code === 0) {
          this.list.push(...res.data)
        }
      })
  },
  methods: {
    back () {
      this.$router.replace({
        path: '/patient/center'
      })
    },
    detailBtn (item) {
      if (item.planType === 1) { // 血压监测
        this.$router.push({
          path: '/patient/monitor/pressure',
          query: {
            planId: item.id,
            pageTitle: item.name
          }
        })
      } else if (item.planType === 2) { // 血糖监测
        this.$router.push({
          path: '/patient/monitor/glucose',
          query: {
            planId: item.id,
            pageTitle: item.name
          }
        })
      } else {
        this.$router.push({path: '/patient/monitor/show',
          query: {
            planId: item.id,
            title: item.name
          }
        })
      }
    }
  }
}
</script>

<style lang="less" scoped>
  .item {
    width: calc(84vw);
    padding: 4vw;
    border-radius: 5px;
    margin: 4vw;
    background: #fff;
    position: relative;
    overflow: hidden;

    .title {
      font-size: 17px;
      font-weight: 600;
      margin-bottom: 15px;
      font-weight: 600;
      color: #333333;
    }

    .content {
      width: 80%;
      font-size: 13px;
      color: #666666;
      // float: left;
    }

    .watchBtn {
      margin-top: 30px;
      width: 80px;
      margin-left: 0px;
      margin-right: 0px;
      background: #fff;
      border: 1px solid rgba(102, 102, 102, 0.6);
      color: #666;
    }

    .icon {
      position: absolute;
      bottom: 0px;
      right: 0px;
      width: 46%;
      text-align: right;
      // float: right;
    }
  }
</style>
