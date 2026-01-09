<template>
  <div style="position: relative">
    <calendar ref="calendar" style="margin-top: 15px" v-show="injectionModel" @changeSelectDate="changeSelectDate"></calendar>
    <div>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh"
                        style="background-color: #f5f5f5; overflow-y: auto; padding-bottom: 50px;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-if="list.length > 0" v-for="(i,k) in list" :key="i.id" style="padding: 0; margin-top: 15px">
            <van-swipe-cell style="width: auto; background-color: #fff; ">
              <div @click="Goitem(i)">
              <div v-if="i.scoreQuestionnaire === 1" style="width: auto; background-color: #fff;">
                <div style="display: flex; font-size: 14px">
                  <div style="color:#323233; padding: 17px 16px; width: calc(100% - 122px)">
                    <div>{{ getTime(i.createTime, 'YYYY年MM月DD日') }}</div>
                    <div style="font-size: 12px; color: #666; margin-top: 8px" v-if="headerTenant !== 'MDQ0OQ=='">
                      {{ getEstimateTime(i) }}
                    </div>
                  </div>
                  <div style="width: 90px;color: #68E1A8;justify-content: right; align-items: center;display: flex; padding: 0 16px">
                    查看详情
                  </div>
                </div>
                <van-cell title="总得分" :border="false" v-if="i.showFormResultSumScore === 1"
                          style="border-radius: 12px">
                  <div style="color: #333">
                    {{i.formResultSumScore}}
                  </div>
                </van-cell>
                <van-cell title="平均分" v-if="i.showFormResultAverageScore === 1"
                          style="border-radius: 12px;" :style="{marginTop: i.showFormResultSumScore === 1 ? '-5px' : '0px'}">
                  <div style="color: #333">
                    {{i.formResultAverageScore}}
                  </div>
                </van-cell>
              </div>
              <div style="background: white; padding: 10px 0"  v-else>
                <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                  <div style="display: flex; align-items: center">
                    <div class="caring-form-div-icon"></div>
                    <span style="margin-left: 10px; color: #666; font-size: 16px">
                    {{ getTime(i.createTime, 'YYYY年MM月DD日 HH:mm') }}
                  </span>
                  </div>
                  <div style="position: absolute; right: 38px">
                    <span class="caring-form-look-detail-button" style="font-size: 14px; color: #3BD26A">查看详情</span>
                  </div>
                </div>
                <div style="padding: 10px 15px 15px 15px">
                  <div style="border-radius: 10px; padding: 10px 6px;">
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
              <template slot="right">
                <van-button style="height: 100%; width: 60px; padding: 0px 20px;" square type="danger" text="删除"
                            @click="deleteBtn(i, k)"/>
              </template>
            </van-swipe-cell>
          </div>
          <div v-if="showNoData" class="noData" >
            <van-image :src="require('@/assets/my/noData.png')" width="70%"/>
            <div>{{patient}}未添加数据</div>
            <div style="margin-top: 5px;">点击<span style="background: #67E0A7; color: white; border-radius: 15px; padding: 3px 8px; margin: 0px 3px;" @click="goRecommend">推荐功能</span>则可将该功能推送至{{patient}}填写
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;" v-if="injectionModel === false">
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
    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;" v-if="injectionModel">
      <van-col span="24" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
  </div>
</template>

<script>
import Vue from 'vue';
import ContentApi from '@/api/Content'
import {queryPlanInfo} from '@/api/plan.js'
import {findPatientInjectionCalendarFormResult} from '@/api/formResult.js'
import {SwipeCell, Button, Dialog} from 'vant';
import {getValue} from "@/components/utils/index"
import {Constants} from '@/api/constants.js'
Vue.use(SwipeCell);
Vue.use(Button);

export default {
  components: {
    calendar: () => import('@/components/patient/customFollowUp/calendar'), // 注射日历
  },
  props: {
    customData: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      refreshing: false,
      subscribePLan: undefined,
      list: [],
      patientId: localStorage.getItem('patientId'),
      loading: true,
      finished: false,
      showNoData: false,
      planId: '',
      patient: this.$getDictItem('patient'),
      headerTenant: localStorage.getItem('headerTenant'),
      // 注射日历模式
      injectionModel: undefined,
      sendDate: moment().format("YYYY-MM"), // datetime日期
      medicationDay: '',
      initStatus: false,
      dateType: 'month',  // 当注射模式时，数据列表展示某天的注射记录
    }
  },
  created() {
    this.injectionModel = undefined
    this.init()
  },
  methods: {
    // 外面切换菜单的时候。当是自定义随访时，刷新数据
    init(force) {
      // 强制初始化
      if (force) {
        this.initStatus = false
      }
      if (this.initStatus) {
        return
      }
      this.initStatus = true
      this.sendDate = moment().format("YYYY-MM") // datetime日期
      this.medicationDay = ''
      this.dateType = 'month'  // 当注射模式时，数据列表展示某天的注射记录
      const item = this.customData
      if (item.path.indexOf('calendar') > -1) {
        const str = item.path
        const regex = /^(.*\/)([^\/]+)(\/.*)$/;
        const match = str.match(regex);
        if (match) {
          this.planId = match[2]
        }
      } else {
        this.planId = item.path.substring(item.path.lastIndexOf("/") + 1)
      }
      this.injectionModel = undefined
      this.queryPlan(this.planId)
      this.getPatientSubscribePlanFunction()
    },
    /**
     * 注射日历的时间变化
     */
    changeSelectDate(data) {
      console.log('changeSelectDate', data)
      this.sendDate = data.sendDate
      this.dateType = data.dateType
      this.medicationDay = data.medicationDay
      this.onRefresh()
    },
    goRecommend() {
      this.$emit('recommendation', 'CUSTOM_FOLLOW_UP')
    },
    /**
     * 查询随访计划的设置
     */
    queryPlan(planId) {
      queryPlanInfo(planId).then(res => {
        const plan = res.data.data
        // 注射模式, 判断路径中是否包含日历
        if (plan.planModel === 1 && this.customData.path.indexOf('calendar') > -1) {
          // 初始化日历
          this.injectionModel = true
          this.$nextTick(() => {
            this.$refs.calendar.init(planId)
          })
        } else {
          this.injectionModel = false
        }
        this.onRefresh()
      })
    },
    deleteBtn(item, index) {
      ContentApi.deleteFormResult(item.id)
        .then(res => {
          this.list.splice(index, 1)
        })
    },
    /**
     * 订阅计划
     */
    putPatientSubscribePlanFunction() {
      ContentApi.putPatientSubscribePlan(this.patientId, this.planId)
        .then(res => {
          if (res.data.code === 0) {
            this.getPatientSubscribePlanFunction()
          }
        })
    },
    getEstimateTime(item) {
      const jsonContent = item.jsonContent
      const jsonArray = JSON.parse(jsonContent)
      let field = undefined;
      const date = this.Constants.CustomFormWidgetType.Date
      const checkTime = this.Constants.FieldExactType.CHECK_TIME
      for (let i = 0; i < jsonArray.length; i++) {
        console.log(jsonArray[i].widgetType, jsonArray[i].exactType)
        if (jsonArray[i].widgetType === date && jsonArray[i].exactType === checkTime) {
          field = jsonArray[i]
          break
        }
      }
      if (field) {
        if (field.values && field.values[0] && field.values[0].attrValue) {
          return ' 填写时间：' + item.firstSubmitTime
        }
      }
      return ''
    },
    /**
     * 取消订阅
     */
    deletePatientSubscribePlanFunction() {
      ContentApi.deletePatientSubscribePlan(this.patientId, this.planId)
        .then(res => {
          if (res.data.code === 0) {
            this.getPatientSubscribePlanFunction()
          }
        })
    },

    /**
     * 获取订阅状态
     */
    getPatientSubscribePlanFunction() {
      ContentApi.getPatientSubscribePlan(this.patientId, this.planId)
        .then(res => {
          if (res.data.code === 0) {
            this.subscribePLan = res.data.data
          }
        })
    },
    putOrDeletePlan() {
      if (this.subscribePLan) {
        Dialog.confirm({
          message: '关闭提醒后，将无法接收填写提醒，是否确认关闭？',
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
    onRefresh() {
      // 清空列表数据
      this.current = 1;
      this.finished = false;
      this.showNoData = false
      this.loading = false
      // 重新加载数据
      this.onLoad();
    },
    onLoad() {
      const params = {
        planId: this.planId,
        current: this.current,
        patientId: this.patientId
      }
      if (this.loading) {
        return
      }
      this.loading = true;
      if (this.current === 1) {
        this.list = []
        this.refreshing = false;
      }
      if (this.injectionModel) {
        let data = this.medicationDay
        if (this.dateType === 'month') {
          data = this.sendDate + '-01'
        }
        findPatientInjectionCalendarFormResult(this.patientId, this.planId, this.dateType, data).then(res => {
          console.log('findPatientInjectionCalendarFormResult', res)
          this.list = res.data.data
          this.loading = false;
          this.finished = true;
        })
      } else {
        ContentApi.getCustomPlanForm(params).then((res) => {
          if (res.data.code === 0) {
            this.list.push(...res.data.data.records)
            this.loading = false;
            this.refreshing = false;
            if (res.data.data.pages <= this.current) {
              this.finished = true;
            } else {
              this.finished = false
              this.current++
            }
            if (this.list.length === 0) {
              this.showNoData = true
            }
          }
        })
      }
    },
    add() {
      this.$router.push(`/custom/follow/${this.planId}/editor`)
    },
    Goitem(k) {
      if (k.scoreQuestionnaire === 1) {
        // 评分表单
        this.$router.push({
          path: '/score/show',
          query: {
            dataId: k.id,
            title: this.customData.name,
            id: this.planId,
          }
        })
      } else {
        this.$router.push({
          path: '/healthCalendar/editor',
          query: {
            content: k.id
          }
        })
      }
    },
    getTime(time, format) {
      return moment(time).format(format)
    },
    getArray(json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (i < 4) {
          list.push(jsonList[i])
        } else if (!flag && (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload)) {
          flag = true
          list.push(jsonList[i])
        }
      }

      return list
    },
    getFormValue(item) {
      return getValue(item)
    },
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
  padding-top: 3px;
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
