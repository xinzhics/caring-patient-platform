<template>
  <div>
    <div style="background: #ffffff">
      <div class="content" :style="{'border-bottom':show?'none':'1px solid #E4E4E4'}">
        <van-tabs type="card" swipeable :lazy-render="false" v-model="active" @change="clickTab" >
          <van-tab v-for="(item, index) in followTaskContentList" :title="item.showName" :key="index"
                   :title-style="{marginRight: index == followTaskContentList.length - 1? '8px' : '8px'}"/>
          <van-tab :title-style="{background:'#ffffff',width:'60px'}"/>
        </van-tabs>

        <div class="right-tab">
          <div class="right-tab-left" />
          <div style="background: #FFFFFF">
            <div class="right-tab-right" @click="openDialog()">
              <img style="width: 13px;height: 13px" :src="require('@//assets/my/tdicon.png')" alt="">
            </div>
          </div>
        </div>
      </div>
    </div>
    <div>
      <!--其他计划-->
      <scheduleList ref="scheduleList" :planId="planId" v-show="type == 0&&!show"  :tag-ids="tagIds"></scheduleList>
      <!--学习计划-->
      <learningPlanList ref="learningPlanList" v-show="type === 6&&!show" :tag-ids="tagIds"></learningPlanList>
    </div>

    <round-dialog ref="rDialog" :data-list="followTaskContentList" @selectConfirm="selectPlanList" :is-multiple="false"></round-dialog>
  </div>
</template>

<script>

import roundDialog from './RoundDialog'
import { Tab, Tabs } from 'vant'
import scheduleList from './scheduleList'
import learningPlanList from './learningPlanList'
export default {
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    scheduleList,
    learningPlanList,
    roundDialog // 弹窗
  },
  name: 'index',
  props: {
    followTaskContentList: {
      type: Array,
      default () {
        return []
      }
    },
    show: {
      type: Boolean
    },
    tagIds: {
      type: String,
      default () {
        return ''
      }
    }
  },
  data () {
    return {
      active: 0,
      type: 0, // 6 学习计划，0 为其他计划
      planId: '',
      planType: 0
    }
  },
  methods: {
    // 标签点击
    clickTab (name, title) {
      console.log('clickTab', name, title)
      if (title === '全部') {
        // 点击了全部按钮
        this.planId = ''
        this.active = 0
        this.$set(this, 'type', 0)
        this.$refs.scheduleList.updatePlanType(0)
      } else {
        this.$refs.scheduleList.updatePlanType(this.followTaskContentList[name].planType)
        // 其他按钮
        if (this.followTaskContentList[name].planType === 6) {
          // 学习计划
          this.$set(this, 'type', 6)
        } else {
          // 其他计划
          this.planId = this.followTaskContentList[name].planId
          this.$set(this, 'type', 0)
        }
      }
      if (this.show) {
        // 统计界面使用组件为true
        this.$emit('selectPlan', {planId: this.followTaskContentList[name].id, planType: this.type})
      }
    },
    // 打开弹窗
    openDialog () {
      this.$refs.rDialog.setPos(this.active)
      this.$refs.rDialog.openDialog()
    },
    // 弹窗选择计划
    selectPlanList (pos) {
      this.active = pos
      if (this.followTaskContentList[pos].planType === 6) {
        this.$set(this, 'type', 6)
        this.$refs.scheduleList.updatePlanType(6)
      } else {
        // 其他计划
        if (this.followTaskContentList[pos].planName === '全部') {
          this.planId = ''
          this.$refs.scheduleList.updatePlanType(0)
        } else {
          this.planId = this.followTaskContentList[pos].planId
          this.$refs.scheduleList.updatePlanType(this.followTaskContentList[pos].planType)
        }
        this.$set(this, 'type', 0)
      }
      if (this.show) {
        // 统计界面使用组件为true
        this.$emit('selectPlan', {planId: this.followTaskContentList[pos].id, planType: this.type})
      }
    }
  }
}
</script>

<style lang="less" scoped>

.content {
  border-bottom: 1px solid #E4E4E4;
  background: #ffffff;
  padding: 12px 0px;
  margin: 0 7px;
  position: relative;
  margin-top: 10px;

  .left-tab::-webkit-scrollbar {
    display: none;
  }

  .left-tab {
    display: flex;
    overflow-x: scroll;

    .item {
      min-width: 70px;
      height: 30px;
      border-radius: 5px;
      background: #F5F5F5;
      text-align: center;
      line-height: 30px;
    }
  }

  .right-tab {
    position: absolute;
    right: 0px;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    align-items: center;

    .right-tab-right {
      background: #F5F5F5;
      border-radius: 5px;
      width: 39px;
      height: 39px;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .right-tab-left {
      width: 33px;
      height: 38px;
      background: linear-gradient(89deg, rgba(255, 255, 255, 0) 0%, #FFFFFF 100%);
    }
  }
}
.listBox {
  background: #ffffff;
  padding: 0px 15px 15px 15px;
}
/deep/.van-tabs{
  .van-tabs__wrap{
    height: 37px;
    .van-tabs__nav{
      border: none !important;
      overflow: -moz-scrollbars-none;
      height: 37px;
      margin: 0;
      padding-right: 60px;

      .van-tab{
        height: 37px;
        color: #666666!important;
        border: none;
        border-radius: 7px;
        background: #F5F5F5;
        margin-right: 7px;
      }
      .van-tab--active{
        background: linear-gradient(90deg, #3F86FF 0%, #6EA8FF 100%);
        color: #FFFFFF !important;
      }
    }
  }
}
</style>
