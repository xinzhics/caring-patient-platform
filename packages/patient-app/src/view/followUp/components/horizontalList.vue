<template>
  <div>
    <div style="background: #ffffff">
      <div class="content" :style="{'border-bottom':show?'none':'1px solid #E4E4E4'}">
        <van-tabs :ellipsis="false" type="card" swipeable :lazy-render="false" v-model="active" @change="clickTab">
          <van-tab v-for="(item, index) in list" :title="item.planName" :key="index"
                   :title-style="{marginRight: index == followTaskContentList.length - 1? '8px' : '8px'}"/>
          <van-tab v-for="item in emptyBox" :key="item+'index'" :title-style="{background:'#ffffff',width:'60px'}"/>
        </van-tabs>

        <div class="right-tab">
          <div class="right-tab-left"/>
          <div style="background: #FFFFFF">
            <div class="right-tab-right" @click="openDialog()">
              <img style="width: 13px;height: 13px" :src="require('@//assets/my/tdicon.png')" alt="">
            </div>
          </div>

        </div>
      </div>
    </div>
    <listCalendar ref="listCalendar" v-if="subscribe!==0 && !show" :type="type" :planIds="planId"></listCalendar>
    <openTask @followList="followList" @openFollowUp="openFollowUp" :planIds="planId"
              v-if="subscribe===0 && !show"></openTask>
    <round-dialog ref="rDialog" :data-list="followTaskContentList" @selectConfirm="selectPlanList"
                  :is-multiple="false"></round-dialog>
  </div>
</template>

<script>

import {Tab, Tabs} from 'vant'

export default {
  components: {
    [Tab.name]: Tab,
    [Tabs.name]: Tabs,
    listCalendar: () => import('./listCalendar'),
    openTask: () => import('./openTask'),
    roundDialog: () => import('./roundDialog'), // 弹窗
  },
  watch: {
    followTaskContentList: {
      handler: function (val, old) {
        if (val) {
          this.list = val
          // 如果列表长度小于等于4 需要往列表添加空盒子(防止列表长度短导致单一tab过长)
          if (this.list !== [] && this.list.length <= 4) {
            this.emptyBox = Number(5 - this.list.length)
          }
        }
      }
    }
  },
  name: "index",
  props: {
    followTaskContentList: {
      type: Array,
      default() {
        return []
      }
    },
    show: {
      type: Boolean,
      default() {
        return false;
      }
    }
  },
  data() {
    return {
      active: 0,
      type: 0,  //6 学习计划，  0 为其他计划
      planId: '',
      subscribe: '',
      list: [],
      emptyBox: 1
    }
  },
  methods: {
    followList(listData) {
      this.list = listData
    },
    openFollowUp(k) {
      this.subscribe = k
    },
    //标签点击
    clickTab(name, title) {
      this.subscribe = this.followTaskContentList[name].subscribe
      if (this.$refs.listCalendar) {
        this.$refs.listCalendar.changeTab()
      }
      if (title === '全部') {
        this.$set(this, 'type', 0)
        //点击了全部按钮
        this.planId = ''
      } else {
        //其他按钮
        if (this.followTaskContentList[name].planType == 6) {
          //学习计划
          this.$set(this, 'type', 6)
        } else {
          // 其他计划
          this.$set(this, 'type', 0)
        }
        this.planId = this.followTaskContentList[name].planId
      }
      if (this.show) {
        //统计界面使用组件为true
        this.$emit('selectPlan', {planId: this.followTaskContentList[name].planId, planType: this.type})
      }

      if (this.$refs.listCalendar) {
        this.$refs.listCalendar.changeTab()
      }
    },
    //打开弹窗
    openDialog() {
      this.$refs.rDialog.setPos(this.active)
      this.$refs.rDialog.openDialog()
    },
    //弹窗选择计划
    selectPlanList(pos) {
      this.active = pos
      this.subscribe = this.followTaskContentList[pos].subscribe
      if (this.followTaskContentList[pos].planType == 6) {
        this.$set(this, 'type', 6)
      } else {
        // 其他计划
        this.$set(this, 'type', 0)
      }
      this.planId = this.followTaskContentList[pos].planId
      if (this.show) {
        //统计界面使用组件为true
        this.$emit('selectPlan', {planId: this.followTaskContentList[pos].planId, planType: this.type})
      }

      if (this.$refs.listCalendar) {
        this.$refs.listCalendar.changeTab()
      }
    },
  }
}
</script>

<style lang="less" scoped>

.content {
  background: #ffffff;
  padding: 12px 0px;
  margin: 0 7px;
  position: relative;

  /deep/ .van-tabs {
    .van-tabs__wrap {
      height: 37px;


      .van-tabs__nav {
        border: none !important;
        overflow: -moz-scrollbars-none;
        height: 37px;
        margin: 0px 6px;

        .van-tab {
          height: 37px;
          color: #666666 !important;
          border: none;
          border-radius: 7px;
          background: #F5F5F5;
          margin-right: 7px;
        }

        .van-tab--active {
          background: #66728B;
          color: #FFFFFF !important;
        }
      }
    }
  }

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
    top: 10px;
    display: flex;
    align-items: center;

    .right-tab-right {
      background: #f5f5f5;
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

</style>
