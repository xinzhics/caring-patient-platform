<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" title="注射日历"
                      @onBack="back"></headNavigation>
    </van-sticky>
    <calendar ref="calendar" style="margin-top: 15px" @changeSelectDate="changeSelectDate" :plan-id="planId"></calendar>
    <div>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh"
                        style="background-color: #f5f5f5; overflow-y: auto; padding-bottom: 50px;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-if="list.length > 0" v-for="(i,k) in list" :key="i.id" style="padding: 0 0 5px; margin-top: 15px">
            <van-swipe-cell style="width: auto; margin: 15px 0; background-color: #fff; ">
              <div @click="Goitem(i)">
                <div style="background: white; padding: 10px 0">
                  <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                    <div style="display: flex; align-items: center">
                      <div class="caring-form-div-icon"></div>
                      <span style="margin-left: 10px; color: #666; font-size: 16px">
                    {{ getTime(i.createTime, 'YYYY年MM月DD日 HH:mm') }}
                  </span>
                    </div>
                    <div style="position: absolute; right: 38px">
                      <span class="caring-form-look-detail-button"
                            style="font-size: 14px; color: #3BD26A">查看详情</span>
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
                            <div
                              style="color: #333333; font-weight: 500; font-size: 14px; width: 90%; text-align: right">
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
          <div v-if="showNoData" class="noData">
            <!--            <van-image :src="require('@/assets/my/noData.png')" width="70%"/>-->
            <div>{{ patient }}未添加数据</div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import moment from 'moment'
import {findPatientInjectionCalendarFormResult, deleteFormResult} from '@/api/formResult.js'
import {SwipeCell, Button, List, PullRefresh} from 'vant'
import {getValue} from '@/components/utils/index'
import {Constants} from '@/api/constants'

Vue.use(SwipeCell)
Vue.use(Button)
Vue.use(List)
Vue.use(PullRefresh)
export default {
  components: {
    calendar: () => import('./calendar') // 注射日历
  },
  data () {
    return {
      refreshing: false,
      loading: true,
      finished: false,
      planId: '',
      patient: this.$getDictItem('patient'),
      list: [],
      showNoData: false,
      // 注射日历模式
      injectionModel: true,
      sendDate: moment().format('YYYY-MM'), // datetime日期
      medicationDay: '',
      initStatus: false,
      dateType: 'month'
    }
  },
  created () {
    this.sendDate = moment().format('YYYY-MM') // datetime日期
    this.medicationDay = ''
    this.dateType = 'month'
    this.patientId = localStorage.getItem('patientId')
    this.planId = this.$route.query.planId
  },
  mounted () {
    if (this.planId) {
      this.onRefresh()
    }
  },
  methods: {
    /**
     * 注射日历的时间变化
     */
    changeSelectDate (data) {
      console.log('changeSelectDate', data)
      this.sendDate = data.sendDate
      this.dateType = data.dateType

      this.medicationDay = data.medicationDay
      this.onRefresh()
    },
    deleteBtn (item, index) {
      deleteFormResult(item.id)
        .then(res => {
          this.list.splice(index, 1)
        })
    },
    getEstimateTime (item) {
      const jsonContent = item.jsonContent
      const jsonArray = JSON.parse(jsonContent)
      let field = {}
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
    onRefresh () {
      // 清空列表数据
      this.current = 1
      this.finished = false
      this.showNoData = false
      this.loading = false
      // 重新加载数据
      this.onLoad()
    },
    onLoad () {
      if (this.loading) {
        return
      }
      this.loading = true
      if (this.current === 1) {
        this.list = []
        this.refreshing = false
      }
      let data = this.medicationDay
      if (this.dateType === 'month') {
        data = this.sendDate + '-01'
      }
      findPatientInjectionCalendarFormResult(this.patientId, this.planId, this.dateType, data).then(res => {
        this.list = res.data
        this.loading = false
        this.finished = true
      })
    },
    add () {
      this.$router.push(`/custom/follow/${this.planId}/editor`)
    },
    Goitem (k) {
    },
    getTime (time, format) {
      return moment(time).format(format)
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
    },
    back () {
      this.$router.replace({
        path: '/patient/center'
      })
    }
  }
}
</script>

<style scoped lang="less">

</style>
