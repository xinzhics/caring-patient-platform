<template>
  <section class="allContent">
    <navBar @toHistoryPage="goStatistics()" pageTitle="随访管理" :rightIcon="require('@/assets/my/follow-up-statistics.png')" :showRightIcon="true" />
    <div style="padding-top: 0;">
      <!--项目详情-->
      <div class="headerBox">
        <img v-if="showImge" :src="followUpData.url?followUpData.url:require('@/assets/my/follow_task_bj.png')"
             style="width: 100%; height: 100%"/>
        <div v-if="followUpData.showOutline===1" class="headerBoxDetail">
          <div class="detailTxt" style="font-size: 20px;margin-bottom: 10px;color: #FFFFFF">{{
              followUpData.name
            }}
          </div>
          <div v-if="followUpData.showOutline===1">
            <div class="detailTxt">启动时间：{{ getDate(followUpData.createTime) }}</div>
            <div class="detailTxt">持续时间：{{ followUpData.days }} 天</div>
            <div class="detailTxt">提醒项目：{{ followUpData.followProjectNumber }} 个</div>
          </div>
        </div>
      </div>
    </div>
    <horizontal-list :followTaskContentList="followTaskContentList"></horizontal-list>
    <!--    日历-->
    <!--    <calendar></calendar>-->
  </section>
</template>

<script>
import {Tab, Tabs} from 'vant'
import Api from '@/api/followUp.js'
import calendar from "./components/calendar";
import horizontalList from './components/horizontalList'

export default {
  components: {
    calendar,
    horizontalList,
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    navBar: () => import('@/components/headers/navBar'),
  },
  name: "index",
  data() {
    return {
      imageUrl: '',
      active: 0,
      followUpData: [], // 随访任务的简介数据
      followTaskContentList: [], // 计划列表
      showImge: false
    }
  },
  mounted() {
    this.patientQueryFollowUpInfo()
  },
  methods: {
    // 跳转到统计页面
    goStatistics() {
      this.$router.push({
        path: '/followUp/statistics'
      })
    },
    /**
     * 获取随访任务的简介数据
     */
    patientQueryFollowUpInfo() {
      Api.patientFollowUpInfo().then(res => {
        if (res.data.code === 0) {
          this.followUpData = res.data.data
          this.followTaskContentList = res.data.data.followTaskContentList
          this.followTaskContentList.unshift({planName: '全部'})
          this.showImge = true // 请求成功并且赋值完成后显示顶部图片(默认或后台配置的图片)
        }
      })
    },
    getDate(time) {
      if (time) {
        return moment(time).format("YYYY年MM月DD日")
      } else {
        return moment().format("YYYY年MM月DD日")
      }
    },
  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background: #F5F5F5;

  .headerBox {
    position: relative;
    height: 180px;

    .headerBoxDetail {
      position: absolute;
      top: 0;
      padding-left: 20px;
      display: flex;
      flex-direction: column;
      //width: 100%;
      height: 100%;
      justify-content: center

    }

    .detailTxt {
      font-size: 12px;
      color: #DAE8FF;
    }
  }


  .patientScope {
    background-color: white;
    padding: 0 13px;
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .tagList::-webkit-scrollbar {
      display: none;
    }

    .tagList {
      display: flex;
      flex: 1;
      overflow-x: auto;
    }

    .patientRound {
      display: flex;
      align-items: center;
      position: relative;
      width: 75px;
      font-size: 14px;
      color: #333;
    }

    .leftShadow {
      position: absolute;
      left: 0;
      width: 20px;
      height: 35px;
      background: linear-gradient(271deg, rgba(255, 255, 255, 0) 0%, #FFFFFF 100%);
      z-index: 999
    }

    .tagItem {
      padding: 4px;
      font-size: 13px;
      white-space: nowrap;
      background: #F0F5FF;
      margin-right: 7px;
      border-radius: 7px;
      border: 1px solid #B7D2FF;
      color: #3F86FF
    }

    .rightShadow {
      position: absolute;
      right: 0;
      width: 20px;
      height: 35px;
      background: linear-gradient(89deg, rgba(255, 255, 255, 0) 0%, #FFFFFF 100%);
      z-index: 999
    }

    .filter {
      display: flex;
      align-items: center;
      width: 60px;
      margin-left: 5px;
      justify-content: right;

      .filterTxt {
        margin-right: 7px;
        color: #666666;
        white-space: nowrap;
        font-size: 14px;
        color: #333;
      }
    }
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

///deep/ .van-tabs {
//  .van-tabs__wrap {
//    height: 26px;
//
//
//    .van-tabs__nav {
//      border: none !important;
//      overflow: -moz-scrollbars-none;
//      height: 26px;
//      margin: 0px 0px;
//
//      .van-tab {
//        height: 26px;
//        min-width: 65px;
//        color: #666666 !important;
//        border: none;
//        border-radius: 7px;
//        background: #F5F5F5;
//        margin-right: 7px;
//      }
//
//      .van-tab--active {
//        background: linear-gradient(90deg, #3F86FF 0%, #6EA8FF 100%);
//        color: #FFFFFF !important;
//      }
//    }
//  }
//}
///deep/.vux-header-right{
//  top: 11px !important;
//}
</style>
