<template>
  <div style="position: relative">
    <div>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh"
                        style="background-color: #f5f5f5; overflow-y: auto; padding-bottom: 50px;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-if="list.length > 0"  style="padding: 0 10px 5px 10px; margin-top: 15px">
            <div style="background: white; padding: 10px 0" v-for="i in list" :key="i.id" @click="Goitem(i)">
              <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                <div style="display: flex; align-items: center">
                  <div class="caring-form-div-icon"></div>
                  <span style="margin-left: 10px; color: #666; font-size: 16px">
                    {{ getTime(i.createTime) }}
                  </span>
                </div>
                <div style="position: absolute; right: 38px">
                  <span class="caring-form-look-detail-button" style="font-size: 14px; color: #3BD26A">查看详情</span>
                </div>
              </div>
              <div style="padding: 10px 15px 0px 15px">
                <div style="border-radius: 10px;">
                  <div v-for="(item, index) in getArray(i.jsonContent)" :key="index" style="padding: 5px 0">
                    <van-row v-if="getFormValue(item).type === 'text'">
                      <van-col span="12">
                        <div style="color: #333333; font-weight: 400; font-size: 14px">{{ item.label }}：</div>
                      </van-col>
                      <van-col span="12" style="display: flex">
                        <div style="color: #333333; font-weight: 500; font-size: 14px; width: 90%; text-align: right">
                          {{ getFormValue(item).value }}
                        </div>
                        <div
                          style="width: 10%; color: #999; align-items: center; display: flex; justify-content: center;">
                          <van-icon name="arrow"/>
                        </div>
                      </van-col>
                    </van-row>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="!loading && list.length === 0" class="noData" >
            <van-image :src="require('@/assets/my/noData.png')" width="70%"/>
            <div>{{patient}}未添加数据</div>
            <div style="margin-top: 5px;">点击<span style="background: #67E0A7; color: white; border-radius: 15px; padding: 3px 8px; margin: 0px 3px;" @click="goRecommend">推荐功能</span>则可将该功能推送至{{patient}}填写
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;">
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #FFF" @click="putOrDeletePlan">
          <span>{{ subscribePLan ? '关闭' : '开启' }}提醒</span>
        </div>
      </van-col>
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
  </div>
</template>

<script>
import Vue from 'vue'
import {getPatientSubscribePlan, deletePatientSubscribePlan, putPatientSubscribePlan} from '@/api/plan.js'
import {pageFormResultByType} from '@/api/formApi.js'
import moment from 'moment'
import {SwipeCell, Button, Dialog} from 'vant'
import {getValue} from '@/components/utils/index'
import {Constants} from '@/api/constants.js'
Vue.use(SwipeCell)
Vue.use(Button)

export default {
  data () {
    return {
      refreshing: false,
      subscribePLan: undefined,
      list: [],
      patientId: localStorage.getItem('patientId'),
      loading: false,
      finished: false,
      planType: 5,
      planId: undefined,
      patient: this.$getDictItem('patient')
    }
  },
  mounted () {
    this.onRefresh()
    this.getPatientSubscribePlanFunction()
  },
  methods: {
    goRecommend () {
      this.$emit('recommendation', 'HEALTH_CALENDAR')
    },
    /**
     * 订阅计划
     */
    putPatientSubscribePlanFunction () {
      putPatientSubscribePlan(this.patientId, this.planId, this.planType)
        .then(res => {
          if (res.data.code === 0) {
            this.getPatientSubscribePlanFunction()
          }
        })
    },

    /**
     * 取消订阅
     */
    deletePatientSubscribePlanFunction () {
      deletePatientSubscribePlan(this.patientId, this.planId, this.planType)
        .then(res => {
          if (res.data.code === 0) {
            this.getPatientSubscribePlanFunction()
          }
        })
    },

    /**
     * 获取订阅状态
     */
    getPatientSubscribePlanFunction () {
      getPatientSubscribePlan(this.patientId, this.planId, this.planType)
        .then(res => {
          if (res.data.code === 0) {
            this.subscribePLan = res.data.data
          }
        })
    },
    putOrDeletePlan () {
      if (this.subscribePLan) {
        Dialog.confirm({
          message: '关闭提醒后，将无法接收填写提醒，是否确认关闭？'
        })
          .then(() => {
            this.subscribePLan = false
            this.deletePatientSubscribePlanFunction()
          })
      } else {
        this.subscribePLan = true
        this.putPatientSubscribePlanFunction()
      }
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.current = 1
      // 重新加载数据
      this.list = []
      this.onLoad(1)
    },
    onLoad (clearList) {
      if (this.loading) {
        return
      }
      this.loading = true
      if (this.current === 1) {
        this.list = []
        this.refreshing = false
      }
      pageFormResultByType(this.patientId, this.current, this.planType).then((res) => {
        if (res.code === 0) {
          this.list.push(...res.data.records)
          this.loading = false
          this.refreshing = false
          if (res.data.pages <= this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
      })
    },
    add () {
      this.$router.push({
        path: '/healthCalendar/editor'
      })
    },
    Goitem (k) {
      this.$router.push({
        path: '/healthCalendar/form/result',
        query: {
          formResultId: k.id
        }
      })
    },
    getTime (time) {
      return moment(time).format('YYYY年MM月DD日')
    },
    getArray (json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (i < 4) {
          list.push(jsonList[i])
        } else if (!flag && (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload || jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload)) {
          flag = true
          list.push(jsonList[i])
        }
      }
      return list
    },
    getFormValue (item) {
      return getValue(item)
    }
  }
}

</script>

<style scoped lang="less">

.noData {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-size: 13px;
  color: #999;
  padding-top: 80px;
}

.caring-form-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 34px;
  text-align: center;
  border-radius: 17px 17px 17px 17px;
  opacity: 1;
  border: 1px solid #66E0A8;
  font-weight: 500;
  color: #66E0A7;
  font-size: 14px;
}
</style>
