<template>
  <div>
    <div v-if="planListData.length == 0" style="display: flex; background: white; justify-content: center;height: 60vh">
      <van-empty :image="require('@/assets/my/no_data.png')" :description="active?'未来30天没有待执行计划':'近30天没有已执行计划'"/>
    </div>
    <div class="list" v-for="(item,index) in planListData" :key="item.id">
      <div style="display: flex;align-items: center;margin-bottom: 7px">
          <span><img :src="require('@/assets/my/panel-point.png')" style="width:13px;height: 13px;margin-right: 22px"
                     alt=""></span>
        <span style="color: #666666;font-size: 13px">{{ getDateTime(item.executionDate) }}</span>
      </div>
      <div class="list-item" v-for="(citem, key) in item.planDetails" :key='key'
           :style="{'border-left':index<planListData.length-1?'1px solid #CED1DA':'none' }">
        <!--学习计划-->
        <div v-if="citem.planType === 6" class="itemBox">
          <div style="width: 100%;padding-bottom: 13px">
            <div style="color:#333333;font-size: 15px">{{ citem.firstShowTitle }}</div>
            <div v-for="(dItem, dKey) in citem.planDetailTimeDTOS" :key="dKey"
                 style="display: flex; justify-content: space-between; margin-top: 5px" @click="goCMS(dItem)">
              <div
                  style="color:#999999;font-size: 13px; white-space: nowrap; width: 180px;overflow: hidden; text-overflow: ellipsis; margin-right: 15px;">
                {{ dItem.secondShowTitle }}
              </div>
              <div style="display: flex;align-items: center">
                  <span
                      :style="{color:active?'#3F86FF':dItem.cmsReadStatus===0?'#FF5555':'#999999','font-size':'13px'}">
                    {{ active ? getDate(dItem.planExecutionDate) : dItem.cmsReadStatus === 0 ? '未读' : '已读' }}
                  </span>
                <van-icon color="#C6C6C6" name="arrow"/>
              </div>
            </div>
          </div>
        </div>

        <!--其他计划-->
        <div v-else class="itemBox">
          <div style="display: flex;align-items: center;justify-content: space-between;margin-bottom: 10px"
               v-for="(ditem,dkey) in citem.planDetailTimeDTOS" :key="dkey" @click="goFrom(citem, dkey)">
            <div style="color:#333333;font-size: 15px">{{ citem.firstShowTitle }}</div>
            <span :style="{color:active?'#3F86FF':ditem.cmsReadStatus===0?'#FF5555':'#999999','font-size':'13px'}">
                {{ active ? getDate(ditem.planExecutionDate) : getPlanFinishStatus(ditem.cmsReadStatus) }}
                <van-icon color="#C6C6C6" name="arrow"/>
              </span>
          </div>
        </div>
      </div>
    </div>
    <!--      需要设置为page===current在显示-->

    <div style="text-align: center;padding: 35px 0 ;color: #B8B8B8;font-size: 13px;background: #F5F5F5">
      — 仅显示最近30天数据 —
    </div>
    <loading-dialog ref="loading"></loading-dialog>
  </div>
</template>

<script>
import Vue from 'vue';
import {Empty, Icon, List} from 'vant';
import Api from '@/api/followUp.js';

Vue.use(Empty);
Vue.use(Icon);
Vue.use(List);
export default {
  name: "scheduleList",
  components: {
    loadingDialog: () => import('./loadingDialog'),
  },
  data() {
    return {
      planListData: [],
      planList: [], //列表
      patientId: localStorage.getItem('userId')
    };
  },
  mounted() {
    this.getPatientQueryFollowPlanUnExecuted(this.planId)
  },
  watch: {
    planId: {
      handler: function (val, old) {
        this.getPatientQueryFollowPlanUnExecuted(val)
      }
    },
    active: {
      handler: function (val, old) {
        if (val === false) {
          // 已执行
          this.getPatientQueryFollowPlanExecuted()
        } else {
          // 待执行
          this.getPatientQueryFollowPlanUnExecuted(this.planId)
        }
      }
    }
  },
  props: {
    planId: {
      type: String,
      default: ''
    },
    active: {
      type: Boolean
    }
  },
  methods: {
    // 日期格式化
    getDateTime(time) {
      if (time) {
        return moment(time).format("YYYY年MM月DD日")
      } else {
        return moment().format("YYYY.MM.DD")
      }
    },
    getPlanFinishStatus(planFinishStatus) {
      if (planFinishStatus === 0) {
        return '未完成'
      } else {
        return '已完成'
      }
    },
    //跳转到cms
    goCMS(item) {
      //将文章设置成已读
      if (item.messageId) {
        Api.openArticle(item.messageId)
      }

      if (item.hrefUrl) {
        //此文章为外链，直接跳转到外链
        window.location.href = item.hrefUrl + '?time=' + ((new Date()).getTime());
      } else {
        //跳转到cms查看文章
        this.$router.push({path: '/cms/show', query: {id: item.cmsId, isComment: true}})
      }
    },
    /**
     * 跳转表单
     * @param item
     */
    goFrom(data, index) {

      if (data.planType == 2) {
        //血糖需要单独跳转到血糖界面
        this.$router.push({path: '/followUp/glucose'})
      } else if (data.planDetailTimeDTOS[index].hrefUrl) {
        //将文章设置成已读
        if (data.planDetailTimeDTOS[index].messageId) {
          Api.openArticle(data.planDetailTimeDTOS[index].messageId)
        }
        //配置有文章链接
        window.location.href = data.planDetailTimeDTOS[index].hrefUrl + '?time=' + ((new Date()).getTime());
      } else if (data.planDetailTimeDTOS[index].cmsId) {
        //将文章设置成已读
        if (data.planDetailTimeDTOS[index].messageId) {
          Api.openArticle(data.planDetailTimeDTOS[index].messageId)
        }
        //跳转到cms查看文章
        this.$router.push({path: '/cms/show', query: {id: data.planDetailTimeDTOS[index].cmsId,isComment: true}})
      } else {
        this.$router.push({
          path: '/followUp/scheduleForm',
          query: {
            planId: data.planId,
            palnName: data.firstShowTitle
          }
        })
      }
    },
    /**
     * // 【患者端】查询 随访的 待执行计划
     */
    getPatientQueryFollowPlanUnExecuted(planId) {
      this.planListData = []
      this.$nextTick(() => {
        if (this.$refs.loading !== undefined) {
          this.$refs.loading.openLoading()
        }
      })
      Api.patientQueryFollowPlanUnExecuted(this.patientId, planId).then(res => {
        if (res.data.code === 0) {
          this.planListData = res.data.data
          this.$nextTick(() => {
            this.$refs.loading.cancelLoading()
          })
        }
      })
    },
    /**
     * // 患者端 查询随访的已执行计划
     */
    getPatientQueryFollowPlanExecuted() {
      this.planListData = []
      this.$nextTick(() => {
        if (this.$refs.loading !== undefined) {
          this.$refs.loading.openLoading()
        }
      })
      Api.patientQueryFollowPlanExecuted(this.patientId, this.planId).then(res => {
        if (res.data.code === 0) {
          this.planListData = res.data.data
          this.$nextTick(() => {
            this.$refs.loading.cancelLoading()
          })
        }
      })
    },
    // 时间格式化
    getDate(time) {
      if (time) {
        return time.slice(0, time.length - 3)
      }
    }
  },
}
</script>

<style scoped lang="less">
.list {
  padding: 0px 27px 6px 27px;
  background: #FFFFFF;
  //margin-bottom: 6px;
  .list-item {

    margin-left: 5.5px;
    padding: 6px 0 6px 28px;
    //margin-bottom: 6px;

    .itemBox {
      margin-bottom: 13px;
      padding: 13px 13px 3px 13px;
      background: #F5F5F5;
      //display: flex;
      //justify-content: space-between;
      //align-items: center;
      //border: 1px solid rgba(63, 134, 255, 0.12);
      border-radius: 4px
    }
  }
}
</style>
