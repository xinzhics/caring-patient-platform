<template>
  <div style="padding-top: 10px;background: #ffffff">
    <van-list
        v-model="loading"
        :finished="finished"
    >
      <div class="list" v-for="(item,index) in learnPlanList" :key="index">
        <div style="display: flex;align-items: center;margin-bottom: 17px">
          <div style="width: 3px;height: 13px;background: #3F86FF;border-radius: 4px;margin-right: 7px"/>
          <div style="color: #333333;font-size: 15px">{{ item.planName }}（共{{item.learnCmsNumber}}篇）</div>
        </div>
<!--        详细内容-->
        <div v-for="(m,n) in item.planExecutionCycles" :key='n' style="padding: 13px;margin-bottom: 13px;border-radius: 4px;box-shadow: 0px 0px 4px 1px rgba(0,0,0,0.08)">
          <div>
            <div style="display: flex;align-items: center">
              <img :src="require('@/assets/my/flag.png')" style="width: 17px;height: 17px;margin-right: 7px" alt="">
              <span style="color:#999999;font-size: 13px">执行计划第 <span style="color: #3F86FF">{{m.planExecutionDay}}</span> 天</span>
            </div>
            <div v-for="(z,k) in m.planDetails" :key="k" style="color: #666666;font-size: 15px;margin-top: 11px;
            display: flex;align-items: center;justify-content: space-between" @click="goCms(z)">
              <div style="max-width: 70%">
                {{ z.firstShowTitle }}
              </div>
              <div style="display: flex;align-items: center">
                <div style="color: #3F86FF;font-size: 14px;margin-right: 7px">{{ getDate(z)}}</div>
                <div><van-icon color="#C6C6C6" name="arrow" /></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </van-list>
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
import { List,Icon } from 'vant';

Vue.use(Icon);
Vue.use(List);
import loadingDialog from "./loadingDialog";

export default {
  name: "scheduleList",
  components: {
    loadingDialog
  },
  data() {
    return {
      list: [],
      loading: false,
      finished: true,
      learnPlanList:[]
    };
  },
  props:{
    tagIds:{
      type:String,
      default:''
    }
  },
  watch:{
    tagIds:{
      handler(val,old){
        this.$refs.loading.openLoading()
        this.getDoctorFindLearnPlan(val)
      }
    }
  },
  mounted() {
    this.$refs.loading.openLoading()
    this.getDoctorFindLearnPlan(this.tagIds)
  },
  methods: {
    //学习计划解析时间
    getDate(data) {
      return moment(data.planExecutionDate, 'HH:mm:ss').format('HH:mm')
    },
    goCms(item) {
      if (item.hrefUrl) {
        //此文章为外链，直接跳转到外链
        if (item.hrefUrl.startsWith("http")) {
          //头部有http协议，不需要拼接
          window.location.href = item.hrefUrl + '?time=' + ((new Date()).getTime());
        }else {
          window.location.href = 'http://' + item.hrefUrl + '?time=' + ((new Date()).getTime());
        }
      } else {
        //跳转到cms查看文章
        this.$router.push({path: '/cms/show', query: {id: item.cmsId, isComment: true}})
      }
    },
    getDoctorFindLearnPlan(tagIds){
      Api.doctorFindLearnPlan(tagIds).then(res=>{
        if (res.data.code === 0){
          if (res.data.code === 0){
            this.$refs.loading.cancelLoading()
            this.learnPlanList = res.data.data
          }
        }
      })
    }
  },
}
</script>

<style scoped lang="less">
.list{
  margin: 24px 13px 13px;
  //margin-bottom: 26px;
  padding-bottom: 13px;
  background: #FFFFFF;
  border-bottom: 1px solid #EBEBEB;
  .list-item{
    margin-left: 5.5px;
    padding: 7px 0 20px 28px
  }
}
</style>
