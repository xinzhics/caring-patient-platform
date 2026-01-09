<template>
  <section class="allContent">
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" :rightIcon="require('@/assets/my/follow-up-statistics.png')" @showpop="goStatistics" title="随访管理" @onBack="onBack"></headNavigation>
    </van-sticky>
    <div>
      <!--项目详情-->
      <div class="headerBox">
        <img v-if="showImg" :src="followUpData.url?followUpData.url:require('@/assets/my/follow_task_bj.png')"
             style="width: 100%; height: 100%"/>
        <div class="headerBoxDetail">
          <div class="detailTxt" style="font-size: 20px;margin-bottom: 10px;color: #FFFFFF">{{
              followUpData.showOutline === 1 ? followUpData.name : ''
            }}
          </div>
          <div v-if="followUpData.showOutline===1">
            <div class="detailTxt">启动时间：{{ getDate(followUpData.createTime) }}</div>
            <div class="detailTxt">持续时间：{{ followUpData.days }} 天</div>
            <div class="detailTxt">提醒项目：{{ followUpData.followProjectNumber }} 个</div>
            <div class="detailTxt">管理人数：{{ followUpData.managePatientNumber }} 人</div>
          </div>
        </div>
      </div>
      <!--患者范围-->
      <div class="patientScope">

        <div style="display: flex; position: relative; flex: 1; max-width: 85%;">
          <span class="patientRound">患者范围：</span>
          <div class="leftShadow" v-if="tagList.length > 1"></div>
          <div class="tagList">
            <div v-for="(item,index) in tagList" :key="index"
                 :style="{'margin-left': index === 0 && tagList.length > 1? '15px' : '0px', 'margin-right': index === tagList.length - 1 ? '40px' : '7px'}"
                 class="tagItem">
              {{ item.name }}
            </div>
          </div>
          <div class="rightShadow"></div>
        </div>
        <!--筛选按钮-->
        <div class="filter" @click="openDialog()">
          <div class="filterTxt">筛选</div>
          <img :src="require('@/assets/my/screening.png')" style="width: 20px; height: 20px"/>
        </div>
      </div>

      <!--计划类型列表-->
      <horizontal-list ref="horizontalList" :followTaskContentList="followTaskContentList" :tag-ids="tagIds"></horizontal-list>
      <!--列表-->
    </div>
    <round-dialog ref="rDialog" @selectConfirm="selectDialogConfirm" :is-multiple="true"
                  :data-list="patientTagList"></round-dialog>
  </section>
</template>

<script>
import {Tab, Tabs, Sticky} from 'vant'
import scheduleList from './components/scheduleList'
import horizontalList from './components/horizontalList'
import roundDialog from './components/RoundDialog'
import learningPlanList from './components/learningPlanList'
import {appFindFollowBriefIntroduction, getPatientTagQuery} from '@/api/followUp.js'
import moment from 'moment'
import Vue from 'vue'

Vue.use(Sticky)
export default {
  components: {
    scheduleList, // 其他+全部
    horizontalList, // 计划tab
    roundDialog, // 弹窗
    learningPlanList, // 学习计划列表
    [Tab.name]: Tab,
    [Tabs.name]: Tabs
  },
  name: 'index',
  data () {
    return {
      imageUrl: '',
      active: 0,
      tagIds: '',
      followUpData: [], // 随访任务的简介数据
      followTaskContentList: [], // 计划列表
      patientTagList: [], // 患者筛选范围
      tagList: [{name: '全部患者'}],
      showImg: false
    }
  },
  mounted () {
    this.getPatientTag()
    this.getDoctorFindFollowBriefIntroduction()
  },
  methods: {
    // 跳转到统计页面
    goStatistics () {
      this.$router.push({
        path: '/followUp/statistics'
      })
    },
    // 选择患者范围，可多选，  逗号间隔
    selectDialogConfirm (tagIds) {
      this.tagIds = tagIds
      if (!tagIds) {
        // 为空表示选择全部患者
        this.tagList = [{name: '全部患者'}]
      } else {
        // 通过,截取数据
        let tagArr = tagIds.split(',')
        this.tagList = []
        this.patientTagList.forEach(item => {
          tagArr.forEach(id => {
            if (item.id === id) {
              this.tagList.push(item)
            }
          })
        })
      }
      this.$refs.horizontalList.clickTab('', '全部')
      this.getDoctorFindFollowBriefIntroduction()
    },
    // 打开患者筛选dialog
    openDialog () {
      this.$refs.rDialog.openDialog()
    },
    /**
     * 获取随访任务的简介数据
     */
    getDoctorFindFollowBriefIntroduction () {
      appFindFollowBriefIntroduction(this.tagIds).then(res => {
        if (res.code === 0) {
          this.showImg = true
          this.followUpData = res.data
          this.followTaskContentList = res.data.followTaskContentList
          this.followTaskContentList.unshift({planName: '全部', showName: '全部'})
        }
      })
    },
    getDate (time) {
      if (time) {
        return moment(time).format('YYYY年MM月DD日')
      } else {
        return moment().format('YYYY年MM月DD日')
      }
    },
    getPatientTag () {
      getPatientTagQuery()
        .then(res => {
          console.log('getPatientTagQuery', res.data)
          this.patientTagList = res.data
          this.patientTagList.unshift({name: '全部患者'})
        })
    },
    onBack () {
      this.$h5Close()
    }
  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100%;
  height: 100vh;
  background: #F5F5F5;
  ::-webkit-scrollbar {
    display: none;
  }

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
      display: flex; flex: 1; overflow-x: auto;
    }

    .patientRound {
      display: flex;
      align-items: center;
      position: relative;
      font-size: 14px;
      color: #333;
    }

    .leftShadow {
      position: absolute;
      left: 67px;
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
      right: -6px;
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

/deep/.vux-header-right{
  top: 11px !important;
}
</style>
