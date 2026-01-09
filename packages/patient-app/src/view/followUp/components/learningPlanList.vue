<template>
  <div>
    <div class="list" v-for="(item,index) in learnPlanList" :key="index">
      <div>
        <div style="display: flex;align-items: center;margin-bottom: 17px">
          <div style="width: 3px;height: 13px;background: #66728B;border-radius: 4px;margin-right: 7px"/>
          <div style="color: #333333;font-size: 15px;font-weight: 500;">{{ item.planName }}（共{{
              item.learnCmsNumber
            }}篇）
          </div>
        </div>
        <!--        详细内容-->
        <div v-if="item.subscribe===0" style="padding-bottom: 64px;background: #FFFFFF">
          <openTask @openFollowUp="openFollowUp" :planIds="item.planId"></openTask>
        </div>
        <div v-if="item.subscribe!==0" v-for="(m,n) in item.planExecutionCycles" :key='n'
             style="padding: 13px;margin-bottom: 13px;border-radius: 4px;box-shadow: 0px 0px 4px 1px rgba(0,0,0,0.08)">
          <div>
            <div style="display: flex;align-items: center">
              <img :src="require('@/assets/my/flag.png')" style="width: 17px;height: 17px;margin-right: 7px" alt="">
              <span style="color:#999999;font-size: 13px">
              {{ getDate(m.localDate) }}
            </span>
            </div>
            <div v-for="(z,k) in m.planDetails" :key="k" style="color: #666666;font-size: 15px;margin-top: 11px;
            display: flex;align-items: center;justify-content: space-between" @click="goCms(z)">
              <div style="max-width: 70%">
                {{ z.firstShowTitle }}

              </div>
              <div style="display: flex;align-items: center">
                <div class="cms-status"
                     :style="{color:z.readStatus===1?'#999999':z.readStatus===0?'#FF5555':'#3F86FF'}">{{ getStatus(z) }}
                </div>
                <div>
                  <van-icon color="#C6C6C6" name="arrow"/>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div style="border-bottom: 1px solid #EBEBEB;"></div>
    </div>
    <!--      需要设置为page===current在显示-->
    <div style="text-align: center;padding: 35px 0 ;color: #B8B8B8;font-size: 13px;background: #F5F5F5">
      一 没有更多了 一
    </div>
    <loading-dialog ref="loading"></loading-dialog>
  </div>
</template>

<script>
import Api from '@/api/followUp.js'
import Vue from 'vue';
import {Icon, List} from 'vant';

Vue.use(Icon);
Vue.use(List);
export default {
  name: "scheduleList",
  components: {
    loadingDialog: () => import('./loadingDialog'),
    openTask: () => import('./openTask'),
  },
  data() {
    return {
      list: [],
      loading: false,
      finished: true,
      learnPlanList: [],
      patientId: localStorage.getItem('userId')
    };
  },
  mounted() {
    this.getPatientQueryLearnPlan(this.patientId)
  },
  methods: {
    openFollowUp(k) {
      this.getPatientQueryLearnPlan(this.patientId)
    },
    getDate(time) {
      if (time) {
        return moment(time).format("YYYY年MM月DD日")
      } else {
        return moment().format("YYYY.MM.DD")
      }
    },
    getStatus(data){
      if (data.readStatus===-1){
        return moment(data.planExecutionDate, "HH:mm:ss").format('HH:mm')
      } else if (data.readStatus===1) {
        return '已读'
      } else {
        return '未读'
      }
    },
    goCms(item) {
      //先判断messageId
      if (item.messageId) {
        console.log(item)
        Api.openArticle(item.messageId)
      }
      //在跳转
      if (item.hrefUrl) {
        //此文章为外链，直接跳转到外链
        window.location.href = item.hrefUrl + '?time=' + ((new Date()).getTime());
      } else {
        //跳转到cms查看文章
        this.$router.push({path: '/cms/show', query: {id: item.cmsId, isComment: true}})
      }
    },
    getPatientQueryLearnPlan(patientId) {
      this.$nextTick(()=>{
        if (this.$refs.loading !== undefined) {
          this.$refs.loading.openLoading()
        }
      })
      Api.patientQueryLearnPlan(patientId).then(res => {
        if (res.data.code === 0) {
          this.$refs.loading.cancelLoading()
          this.$refs.loading.cancelLoading()
          this.learnPlanList = res.data.data
          console.log(this.learnPlanList)
        }
      })
    }
  },
}
</script>

<style scoped lang="less">
.list {
  padding: 13px;
  //margin-bottom: 26px;
  background: #FFFFFF;

  .list-item {
    margin-left: 5.5px;
    padding: 7px 0 20px 28px
  }

  .cms-status {
    color: #3F86FF;
    font-size: 14px;
  }
}
</style>
