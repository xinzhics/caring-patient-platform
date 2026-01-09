<template>
  <div>
    <div v-if="planList.length == 0" style="display: flex; background: white; justify-content: center;height: 60vh">
      <van-empty :image="require('@/assets/my/no_data.png')" description="暂无随访记录"/>
    </div>
    <van-list
      v-if="planList.length > 0"
      v-model="loading"
      :finished="finished"
      @load="onLoad"
      style="padding-top: 26px;background: #FFFFFF"
    >
      <div class="list" v-for="(item,index) in planList" :key="item.id">
        <div style="display: flex;align-items: center;margin-bottom: 7px">
          <span><img :src="require('@/assets/my/panel-point.png')" style="width:13px;height: 13px;margin-right: 22px"
                     alt=""></span>
          <span style="color: #666666;font-size: 13px">执行计划第 <span style="color: #3F86FF">{{
              item.planExecutionDay
            }}</span> 天</span>
        </div>
        <div class="list-item" v-for="(citem, key) in item.planDetails" :key='key'
             :style="{'border-left':index<planList.length-1?'1px solid #C1D8FF':'none' }">
          <!--学习计划-->
          <div v-if="citem.planType === 6" class="itemBox">
            <div style="width: 100%;">
              <div style="color:#333333;font-size: 15px">{{ citem.firstShowTitle }}</div>
              <div v-for="(dItem, dKey) in citem.planDetailTimeDTOS" :key="dKey"
                   style="display: flex; justify-content: space-between; margin-top: 5px" @click="goCMS(dItem)">
                <div
                  style="color:#999999;font-size: 13px; white-space: nowrap; width: 180px;overflow: hidden; text-overflow: ellipsis; margin-right: 15px;">
                  {{ dItem.secondShowTitle }}
                </div>
                <div style="display: flex;align-items: center">
                  <span style="color: #3F86FF;font-size: 13px">{{ getDate(dItem.planExecutionDate) }}</span>
                  <van-icon color="#C6C6C6" name="arrow"/>
                </div>
              </div>
            </div>
          </div>

          <!--其他计划-->
          <div v-else class="itemBox" @click="goFrom(citem)">
            <div style="color:#333333;font-size: 15px">{{ citem.firstShowTitle }}</div>
            <div style="display: flex;align-items: center">
              <span style="color: #3F86FF;font-size: 13px">{{ getDate(citem.planExecutionDate) }}</span>
              <van-icon color="#C6C6C6" name="arrow"/>
            </div>
          </div>
        </div>
      </div>
      <!--      需要设置为page===current在显示-->

      <div v-if="finished" style="text-align: center;padding: 35px 0 ;color: #B8B8B8;font-size: 13px">
        {{ getYears(planListData.maxPlanDay) }}
      </div>
    </van-list>
    <loading-dialog ref="loading"></loading-dialog>
  </div>
</template>

<script>
import Vue from 'vue';
import {List, Icon, Empty} from 'vant';
import Api from '@/api/followUp.js';
import loadingDialog from "./loadingDialog";

Vue.use(Empty);
Vue.use(Icon);
Vue.use(List);
export default {
  name: "scheduleList",
  components: {
    loadingDialog
  },
  data() {
    return {
      loading: false,
      finished: false,
      current: 1, //页数
      planListData: {},
      planList: [], //列表
    };
  },
  mounted() {
    this.$refs.loading.openLoading()
    this.getDoctorFindFollowPlan()
  },
  watch: {
    planId: {
      handler: function (val, old) {
        this.current = 1
        this.$refs.loading.openLoading()
        this.finished=false
        if (val !== '') {
          this.getDoctorFindOtherPlan()
        } else {
          this.getDoctorFindFollowPlan()
        }
      }
    },
    tagIds: {
      handler(val, old) {

        if (this.planId === '') {
          this.$refs.loading.openLoading()
          this.current = 1
          this.planListData=[]
          this.finished = false
          this.getDoctorFindFollowPlan(val)
        }
      }
    }
  },
  props: {
    planId: {
      type: String,
      delete: ''
    },
    tagIds: {
      type: String,
      default: ''
    }
  },
  methods: {
    //跳转到cms
    goCMS(item) {
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
    /**
     * 跳转表单
     * @param item
     */
    goFrom(item) {
      if (item.planType === 2) {
        //血糖需要单独跳转到血糖界面
        this.$router.push({path: '/followUp/glucose'})
      } else {
        if (item.hrefUrl) {
          //此文章为外链，直接跳转到外链
          if (item.hrefUrl.startsWith("http")) {
            //头部有http协议，不需要拼接
            window.location.href = item.hrefUrl + '?time=' + ((new Date()).getTime());
          }else {
            window.location.href = 'http://' + item.hrefUrl + '?time=' + ((new Date()).getTime());
          }
        }else if (item.cmsId) {
          //跳转到cms查看文章
          this.$router.push({path: '/cms/show', query: {id: item.cmsId, isComment: true}})
        }else {
          this.$router.push({
            path: '/followUp/scheduleForm',
            query: {
              planId: item.planId,
              palnName: item.firstShowTitle
            }
          })
        }

      }
    },
    onLoad() {
      // 异步更新数据
      // setTimeout 仅做示例，真实场景中一般为 ajax 请求
      setTimeout(() => {
        // 加载状态结束
        this.loading = true;
        // 如果是空就是全部选项 否则其他计划
        if (this.planId === '') {
          this.getDoctorFindFollowPlan()
        } else {
          this.getDoctorFindOtherPlan()
        }
      }, 300);
    },
    /**
     * 【医生端】 查询全部随访任务的计划安排
     */
    getDoctorFindFollowPlan() {
      Api.doctorFindFollowPlan(this.tagIds, this.current).then(res => {
        if (res.data.code === 0) {
          if (this.current === 1) {
            this.$refs.loading.cancelLoading()
            this.planListData = res.data.data
            this.planList = res.data.data.planExecutionCycles
          } else {
            this.planList.push(...res.data.data.planExecutionCycles)
          }
          // 数据全部加载完成
          this.current++
          if (res.data.data.planExecutionCycles && res.data.data.planExecutionCycles.length == 0) {
            this.finished = true
          }
          this.loading = false;

        }
      })
    },
    /**
     * // 【医生端】 查询其他计划
     */
    getDoctorFindOtherPlan() {
      console.log(this.finished,'======')
      Api.doctorFindOtherPlan(this.planId, this.current).then(res => {
        if (res.data.code === 0) {
          if (this.current === 1) {
            this.$refs.loading.cancelLoading()
            this.planListData = res.data.data
            this.planList = res.data.data.planExecutionCycles
          } else {
            this.planList.push(...res.data.data.planExecutionCycles)
          }
          // 数据全部加载完成
          this.current++
          if (res.data.data.planExecutionCycles && res.data.data.planExecutionCycles.length == 0) {
            this.finished = true
          }
          this.loading = false;

        }
      })
    },
    getDate(time) {
      if (time) {
        return time.slice(0, time.length - 3)
      }
    },
    getYears(date) {
      if (date <= 30) {
        return '— 仅显示最近1月 —'
      } else if (date <= 60) {
        return '— 仅显示最近2月 —'
      } else if (date <= 90) {
        return '— 仅显示最近3月 —'
      } else if (date <= 180) {
        return '— 仅显示最近半年 —'
      } else if (date <= 365) {
        return '— 仅显示最近1年 —'
      } else if (date <= 730) {
        return '— 仅显示最近2年 —'
      } else if (date <= 1095) {
        return '— 仅显示最近3年 —'
      }
    }
  },
}
</script>

<style scoped lang="less">
.list {
  padding: 0px 13px 6px 13px;
  background: #FFFFFF;
  //margin-bottom: 6px;
  .list-item {

    margin-left: 5.5px;
    padding: 6px 0 6px 28px;
    //margin-bottom: 6px;

    .itemBox {
      margin-bottom: 13px;
      padding: 13px;
      background: #F7FAFF;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border: 1px solid rgba(63, 134, 255, 0.12);
      border-radius: 4px
    }
  }
}
</style>
