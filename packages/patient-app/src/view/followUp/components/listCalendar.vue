<template>
  <div>
    <div class="content">
      <div>
        <van-tabs @change="active1=true" v-model="active">
          <van-tab :title-style="{'font-size':'16px'}" title="列表">
            <div v-if="type!==6" class="plan-list">
              <div class="plan-tab">
                <div @click="changeActive('noFinish')" :class="active1?'chooseTba tab1':'tab1'">待执行计划</div>
                <div @click="changeActive('finish')" :class="!active1?'chooseTba tab1':'tab1'">已执行计划</div>
              </div>
            </div>
            <div>
              <scheduleList :active="active1" :planId="planIds" v-if="active===0&&type!==6"></scheduleList>
              <learningPlanList ref="learningPlanList" v-if="type === 6"></learningPlanList>
            </div>
          </van-tab>
          <van-tab :title-style="{'font-size':'16px'}" title="日历">
            <!--    日历-->
            <calendar :planId="planIds" :plan-type="type"></calendar>
          </van-tab>
        </van-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import {Tab, Tabs} from 'vant';
Vue.use(Tab);
Vue.use(Tabs);
export default {
  name: "listCalendar",
  props: {
    planIds: {
      type: String,
      default: ''
    },
    type: {
      type: Number,
      default: null
    }
  },
  components: {
    calendar: () => import('./calendar'),
    scheduleList: () => import('./scheduleList'),
    learningPlanList: () => import('./learningPlanList'),
  },
  data() {
    return {
      active: 0,
      active1: true,
    }
  },
  watch: {
    planIds: {
      handler: function (val, old) {
        //上标签切换时候，下面标签为初始
        this.active1 = true
      }
    },
  },
  methods: {
    /**
     * tab切换
     * @param isFinish
     */
    changeActive(isFinish) {
      if (isFinish === 'noFinish') {
        this.active1 = true
      } else {
        this.active1 = false
      }
    },
    changeTab(){
      this.active = 0
    }
  }
}
</script>

<style scoped lang="less">
.content {
  background: #FFFFFF;
  margin-top: 12px;

  /deep/ .van-tabs__line {
    background: #66728B !important;
  }

  /deep/ .van-tabs__wrap {
    padding-bottom: 4px;
    border-bottom: 1px solid #F5F5F5;
  }

  .plan-list {

    padding: 27px;
    background: #FFFFFF;

    .plan-tab {
      background: #F5F5F5;
      display: flex;
      justify-content: space-between;
      border-radius: 43px;
      align-items: center;
      text-align: center;
      border: 1px solid #f5f5f5;
      //padding: 11px 0;
      .tab1 {
        flex: 1;
        border: 1px solid #f5f5f5;
        color: #666666;
      }

      .chooseTba {
        padding: 5px 0;
        color: #66728B;
        background: #FFFFFF;
        border: 1px solid #66728B;
        border-radius: 43px;
      }
    }
  }
}
</style>
