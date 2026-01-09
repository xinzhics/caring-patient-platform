<template>
  <div class="main">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '智能提醒'" @onBack="$router.go(-1)"></headNavigation>
    </van-sticky>
    <img :src="imageUrl" alt="" style="width:100%" srcset="">
    <div class="content">
      <div class="item" v-for="(i,k) in allData.subscribeList" :key="k">
        <van-cell center v-if="i.disableSubscribe===0" :title="i.nursingPlanName">
          <template #right-icon>
            <van-switch size="mini" :active-value="1" :inactive-value="0" v-model="i.isSubscribe" @on-change="changeBtn(i,k)"/>
          </template>
        </van-cell>
        <div
          v-if="i.disableSubscribe===0 && i.nursingPlanName === '复查提醒'">
          <div style="display: flex; -webkit-box-align: center; align-items: center; padding: 10px 12px !important;
          border-top: 1px solid #f5f5f5 !important;" @click="showDate()">
            <div class="left">
              {{ i.customPlanTimes.length > 0 ? getDate(i.customPlanTimes[0].nextRemindTime) : '' }}
            </div>
            <div class="right">
              <van-icon name="arrow" size="20"/>
            </div>
          </div>
          <van-popup v-model="show" position="bottom">
            <van-datetime-picker
              :v-model="getDate('')"
              type="date"
              title="选择年月日"
              :min-date="minDate"
              :max-date="new Date(2050, 12, 31)"
              @confirm="(time) => dateConfirm(time)"
              @cancel="dateCancel()"
            />
          </van-popup>
          <van-popup v-model="showTime" position="bottom">
            <van-picker show-toolbar title="选择时间" :columns="columns"
                        @confirm="(value, index)=>timeConfirm(value, index)"
                        @cancel="timeCancel()"/>
          </van-popup>
        </div>
      </div>
    </div>
    <div style="text-align: center">
      <van-button round style="width:40%;margin-top:40px;color:#fff;background:#66728C!important" @click.native="submit">提交
      </van-button>
    </div>
  </div>
</template>
<script>
import { getMyNursingPlans, updateMyNursingPlans } from '@/api/plan.js'
import { getTenantUi } from '@/api/tenant.js'
import moment from 'moment'
import Vue from 'vue'
import {Popup, DatetimePicker, Picker, Row, Col, Button, Icon, Sticky, Switch, Cell, Toast} from 'vant'

Vue.use(Popup)
Vue.use(DatetimePicker)
Vue.use(Picker)
Vue.use(Row)
Vue.use(Col)
Vue.use(Button)
Vue.use(Icon)
Vue.use(Sticky)
Vue.use(Switch)
Vue.use(Cell)
export default {

  data () {
    return {
      value: '',
      imageUrl: '',
      allData: {
        subscribeList: []
      },
      minDate: new Date(moment().subtract(-1, 'days').format('YYYY-MM-DD')),
      show: false,
      showTime: false,
      date: '',
      pageTitle: localStorage.getItem('pageTitle'),
      myName: ['用药提醒', '血压监测', '血糖监测', '复查提醒', '健康日志'],
      columns: [
        // 第一列
        {
          values: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'],
          defaultIndex: this.getTimeHourIndex()
        },
        // 第二列
        {
          values: ['00', '30'],
          defaultIndex: 0
        }
      ]
    }
  },
  mounted () {
    this.getInfo()
    const styleDateJSON = localStorage.getItem('styleDate')
    if (styleDateJSON) {
      const styleDate = JSON.parse(styleDateJSON)
      this.setImageUrl(styleDate)
    } else {
      getTenantUi().then(res => {
        if (res.code === 0) {
          this.setImageUrl(res.data)
        }
      })
    }
  },
  methods: {
    /**
     * 设置背景图
     * @param styleDate
     */
    setImageUrl (styleDate) {
      this.imageUrl = styleDate.NURSING_BG.attribute1
    },
    getTimeHourIndex () {
      return moment().format('H') - 1
    },
    dateConfirm (value) {
      this.show = false
      this.date = moment(value).format('YYYY-MM-DD')
      this.showTime = true
    },
    dateCancel () {
      this.show = false
    },
    timeConfirm (value, index) {
      this.showTime = false
      this.date = this.date + ' ' + value[0] + ':' + value[1] + ':00'
      this.allData.subscribeList.forEach((item, index) => {
        if (item.nursingPlanName === '复查提醒' && this.allData.subscribeList[index].customPlanTimes.length > 0) {
          this.allData.subscribeList[index].customPlanTimes[0].nextRemindTime = this.date
          this.allData.subscribeList[index].customPlanTimes[0].frequency = 0
          this.allData.subscribeList[index].customPlanTimes[0].customizeStatus = 1
        } else {
          this.allData.subscribeList[index].customPlanTimes = [{
            nextRemindTime: this.date,
            frequency: 0,
            customizeStatus: 1
          }]
        }
      })
    },
    timeCancel () {
      this.showTime = false
    },
    getDate (time) {
      if (time) {
        return moment(time).format('YYYY-MM-DD HH:mm')
      } else {
        return moment().format('YYYY-MM-DD HH:mm')
      }
    },
    showDate () {
      this.show = true
    },
    getInfo () {
      const params = {
        patientId: localStorage.getItem('patientId')
      }
      getMyNursingPlans(params).then((res) => {
        if (res.code === 0) {
          this.allData = res.data
        }
      })
    },
    changeBtn (i, k) {
      this.$set(this.allData.subscribeList, k, i)
    },
    submit () {
      const params = this.allData
      const param = {
        patientId: localStorage.getItem('patientId'),
        content: params
      }
      updateMyNursingPlans(param).then((res) => {
        if (res.code === 0) {
          Toast({message: '提交成功', closeOnClick: true})
          this.$router.go(-1)
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
.main {
  padding-bottom: 40px;
  background: #f5f5f5;
  .content {
    .item {
      width: 92vw;
      margin: 3vw;
      padding: 0vw 1vw;
      border-radius: 8px;
      background: #fff;
      font-size: 18px;
      color: rgba(102, 102, 102, 1);
    }
  }

  .left {
    -webkit-box-flex: 1;
    flex: 1;
    font-size: 14px;
    color: #999999;
  }

  .right {
    font-size: 14px;
    color: #999999;
  }
}

</style>
