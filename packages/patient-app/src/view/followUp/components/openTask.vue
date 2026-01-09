<template>
  <div>
    <div style="width: 100%;margin: 108px auto 0">
      <div style="display: flex;align-items: center;justify-content: center">
        <img src="@/assets/my/open-task.png" width="152px" alt="">

      </div>
      <div style="text-align: center;font-size: 15px;color: #999999">
        <div style="margin-bottom: 3px;width: 100%">
          <p style="padding: 0">您暂未开启该随访任务</p>
        </div>
        <div>
          <p style="padding: 0">点击下方按钮可开启</p>
        </div>
      </div>
    </div>
    <div style="display: flex;justify-content: center">
      <van-button class="my-button" @click="openFollowUp" round type="info" plain color="#3F86FF">开启随访</van-button>
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import {Button, Toast} from 'vant';
import Api from '@/api/followUp.js'

Vue.use(Toast);
Vue.use(Button);
export default {
  name: "openTask",
  data() {
    return {
      patientId: localStorage.getItem('userId'),
      followTaskContentList: []
    }
  },
  props: {
    planIds: {
      type: String
    }
  },
  methods: {
    /**
     * 开启计划
     */
    openFollowUp() {
      console.log(this.planIds)
      Api.subscribePlan(this.patientId, this.planIds).then(res => {
        console.log(res)
        if (res.data.code === 0 && res.data.data) {
          Toast('计划已开启');
          this.patientQueryFollowUpInfo()
        }
      })
    },
    patientQueryFollowUpInfo() {
      Api.patientFollowUpInfo().then(res => {
        if (res.data.code === 0) {
          this.followUpData = res.data.data
          this.followTaskContentList = res.data.data.followTaskContentList
          this.followTaskContentList.unshift({planName: '全部'})
          this.$emit('followList', this.followTaskContentList)
          this.$emit('openFollowUp', 1)
        }
      })
    },
  }
}
</script>

<style scoped lang="less">
/deep/ .my-button {
  background: #FFFFFF !important;
  width: 173px;
  margin: 26px auto 0;
  height: 43px;
  font-weight: 500;
  font-size: 16px;
}
</style>
