<template>
  <div>
    <x-header style="margin-bottom:0px!important;" :left-options="{preventGoBack:true,backText:''}"
              @on-click-back="close">预约失败
    </x-header>
    <div class="content">
      <div class="prohibit">
        <van-icon size="50" name="fail"/>
      </div>
      <div style="font-size: 20px;color: #FF5555;font-weight: 500">
        预约失败
      </div>
    </div>
    <div class="failure-Reason">
      <div style="color: #666666;font-size: 15px;margin-bottom: 17px;font-weight: 500">失败原因</div>
      <div>
        {{ReasonRejection}}
      </div>
    </div>
    <div class="btn-box" @click="goDoctorIndex">
      再次预约
    </div>
  </div>
</template>

<script>
import Vue from 'vue';
import {Icon} from 'vant';
import wx from 'weixin-js-sdk';
import Api from '@/api/Content.js'
Vue.use(Icon);
export default {
  name: "appointmentFailed",
  data(){
    return {
      ReasonRejection:'',
      doctorId:''
    }
  },
  beforeMount() {
    if (this.$route.query.appointmentId) {
      this.appointmentId = this.$route.query.appointmentId
      console.log(this.appointmentId)
      Api.appointment(this.$route.query.appointmentId).then(res=>{
        console.log(res)
        if (res.data.code === 0) {
          this.doctorId = res.data.data.doctorId
          if (res.data.data.auditFailReason === 'ABOUT_FULL') {
            this.ReasonRejection = '该时段已约满'
          } else {
            this.ReasonRejection = res.data.data.auditFailReasonDesc
          }
        }
      })
    }
  },
  methods: {
    close() {
      console.log(wx)
      wx.closeWindow(); //关闭浏览器
    },
    /**
     * 跳转到预约界面
     */
    goDoctorIndex(){
      this.$router.push({
        path:'/reservation/doctorIndex',
        query:{
          id:this.doctorId
        }
      })
    }
  },
}
</script>

<style scoped lang="less">
.content {
  background: #FFFFFF;
  padding: 43px 13px 26px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  border-bottom: 1px solid #EBEBEB;

  .prohibit {
    width: 87px;
    height: 87px;
    background: #FF5555;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #FFFFFF;
    margin-bottom: 26px;
  }
}

.failure-Reason {
  padding: 17px;
  background: #FFFFFF;
  color: #999999;
  font-size: 13px;
}

.btn-box {
  width: 66%;
  height: 43px;
  background: #66728C;
  border-radius: 43px;
  margin: 37px auto 0;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
