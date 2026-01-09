<template>
  <div style="min-height: 100vh;background: #f5f5f5">
    <x-header style="margin: 0 !important;" :left-options="{backText: ''}">
      随访统计
    </x-header>

    <!--详情-->
    <div style="background: #FFFFFF">
      <div class="content">
        <div class="data-range">
          <div>
            <van-icon name="notes-o"/>
            数据范围
          </div>
          <div>
            {{ getDate(statisticsData.startTime) }}-{{ getDate(statisticsData.endTime) }}
          </div>
        </div>

      </div>
      <horizontal-list @selectPlan="selectPlan"
                       :show="true"
                       :followTaskContentList="statisticsData.followTaskContentList"></horizontal-list>
    </div>

    <!--其他计划-->
    <otherStatistics v-if="type == 0" :plan-id="planId"></otherStatistics>
    <!--学习计划-->
    <learningStatistics v-else-if="type == 6"></learningStatistics>
  </div>
</template>

<script>
import Api from '@/api/followUp.js'
import horizontalList from './horizontalList'
import learningStatistics from './learningStatistics'
import otherStatistics from "./otherStatistics";

export default {
  name: "statistics",
  components: {horizontalList, learningStatistics, otherStatistics},
  data() {
    return {
      statisticsData: {},
      type: -1,
      planId: '',
    }
  },
  created() {
    this.getDoctorQueryFollowCountDetail()
  },
  methods: {
    //选择了某个计划
    selectPlan(item) {
      console.log(item)
      this.type = item.planType
      setTimeout(() => {
        this.planId = item.planId
      },100)
    },
    /**
     * 【医生端】获取随访统计的详细
     */
    getDoctorQueryFollowCountDetail() {
      Api.doctorQueryFollowCountDetail().then(res => {
        if (res.data.code === 0) {
          this.statisticsData = res.data.data
          //首次进入加载数据，获取第一位数据的计划id与计划类型
          if (this.statisticsData.followTaskContentList && this.statisticsData.followTaskContentList.length > 0) {
            if (this.statisticsData.followTaskContentList[0].planType === 6) {
              this.type = 6
            }else {
              this.type = 0
            }
            //先需要根据 type 显示列表，， 然后修改planId 来加载数据
            setTimeout(() => {
              this.planId = this.statisticsData.followTaskContentList[0].id
            },100)
          }
        }
      })
    },
    getDate(time) {
      if (time) {
        return moment(time).format("YYYY.MM.DD")
      } else {
        return moment().format("YYYY.MM.DD")
      }
    },
  }
}
</script>

<style scoped lang="less">
.content {
  padding: 10px 7px 0 7px;
  background: #FFFFFF;

  .data-range {
    background: linear-gradient(to right, #6EA8FF, #5292FF);
    padding: 24px 13px;
    border-radius: 7px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #ffffff;
  }
}

.list {
  margin-top: 13px;
}
</style>
