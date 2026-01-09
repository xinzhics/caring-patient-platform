<template>
  <div>
    <headNavigation leftIcon="arrow-left" :title="doctorName" @onBack="goback"></headNavigation>
    <!--    在线预约开关-->
    <div style="margin-top: 11px;padding: 14px 13px;background: #ffffff">
      <div style="display: flex;align-items: center;justify-content: space-between;padding-bottom: 14px;border-bottom: 1px solid #EEEEEE">
        <div>
          <div style="color: #333333">在线预约</div>
          <div style="margin-top: 9px;font-size: 13px;color: #999999">关闭后将不再提供预约服务</div>
        </div>
        <div>
          <van-switch :loading="switchLoading" size="16px" v-model="checked" inactive-color="#EBEBEB"
                      active-color="#07c160" @change="switchChange"/>
        </div>
      </div>
      <div style="display: flex;align-items: center;justify-content: space-between;padding-top: 14px">
        <div>
          <div style="color: #333333">预约无需审核</div>
          <div style="margin-top: 9px;font-size: 13px;color: #999999">开启后，预约无需审核直接预约成功</div>
        </div>
        <div>
          <van-switch :loading="switchLoading" size="16px" v-model="appointmentReview" inactive-color="#EBEBEB" active-value="no_review" inactive-value="need_review"
                      active-color="#07c160" @change="changeAppointmentReview"/>
        </div>
      </div>
    </div>
    <div v-if="checked">
      <!--    设置部分-->
      <div style="padding: 18px 13px 0 13px;background: #ffffff;margin-top: 11px">
        <!--        每周号源设置-->
        <div
            style="border-bottom: 1px solid #EBEBEB;display: flex;justify-content: space-between ;padding-bottom: 18px">
          <div>每周号源设置</div>
          <div>
            <span><img src="../../../assets/careful.png" style="width: 12px" alt=""></span>
            <span style="color: #B8B8B8;font-size: 13px">点击星期可修改当天号源</span>
          </div>

        </div>
        <!--      日期选择-->
        <div style="padding: 18px 0;display: flex;justify-content: space-around;align-items: center">
          <div v-for="(item,index) in weekConfig" :key="index" @click="select(item)"
               :class="selectDay.id === item.id ?'date-numbers date-number':'date-number'">
            <div>{{ item.name }}</div>
            <div>{{ item.number }}人</div>
          </div>
        </div>
      </div>
      <!--    人数设置-->
      <div style="margin-top: 11px;background: #ffffff;padding: 0 13px">.
        <!--      可预约人数-->
        <div style="border-bottom: 1px solid #F5F5F5;display: flex;justify-content: space-between;padding-bottom: 20px">
          <div style="color: #333333">可约人数设置</div>
          <div style="color: #3F86FF">{{ selectDay.number }}人</div>
        </div>
        <!--      上午-->
        <div style="border-bottom: 1px solid #F5F5F5;display: flex;justify-content: space-between;padding: 20px 0">
          <div style="color: #333333">上午</div>
          <div style="color: #3F86FF" @click="clickStepper">
            <van-stepper :disable-plus="!!!selectDay.id" :disable-minus="!!!selectDay.id"
                         :disable-input="!!!selectDay.id" integer input-width="43px" max="10000" min="0" async-change
                         v-model="selectDay.morning" @change="morningChange"/>
          </div>
        </div>
        <!--      下午-->
        <div style="display: flex;justify-content: space-between;padding: 20px 0">
          <div style="color: #333333">下午</div>
          <div style="color: #3F86FF" @click="clickStepper">
            <van-stepper :disable-plus="!!!selectDay.id" :disable-minus="!!!selectDay.id"
                         :disable-input="!!!selectDay.id" integer input-width="43px" max="10000" min="0" async-change
                         v-model="selectDay.afternoon" @change="afterNoonChange"/>
          </div>
        </div>
      </div>
      <!--    提交按钮-->
      <div style="margin-top: 58px; padding-bottom: 20px">
        <div style="width: 286px;margin: 0 auto">
          <van-button :loading="loading" @click="submit" style="width: 100%;height: 53px" round type="info">确定
          </van-button>
        </div>
      </div>
      <!--      点击提交按钮后弹窗-->
      <van-dialog :showConfirmButton="false" :showCancelButton="false" v-model="show" show-cancel-button>
        <div style="width: 173px;margin: 0 auto;text-align: center">
          <img src="../../../assets/xiugai-sec.png" style="width: 173px" alt="">
          <div style="color: #333333;font-size: 17px;font-weight: 700">修改成功</div>
          <div style="font-size: 12px;color: #999999;margin-top: 18px;margin-bottom: 63px">本次修改将默认应用到以后每周</div>
          <div style="margin-bottom: 37px">
            <van-button @click="setUp" style="width: 173px;height: 43px" round type="info">我知道了</van-button>
          </div>
        </div>
      </van-dialog>
    </div>

  </div>
</template>
<script>
import Vue from 'vue'
import {Button, Col, Dialog, Icon, Row, Stepper, Switch, Toast} from 'vant'
import {appointConfig, updateAppointConfig} from '@/api/appointment.js'
import {setDoctorInfo, updateDoctorInfo} from '@/api/doctorApi.js'

Vue.use(Switch)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
Vue.use(Dialog)
Vue.use(Button)
Vue.use(Stepper)
Vue.use(Toast)
export default {
  name: 'reservation',
  data () {
    return {
      switchLoading: false,
      doctorId: this.$route.query.doctorId,
      status: Number(this.$route.query.status),
      doctorName: this.$route.query.doctorName,
      doctorAppointConfig: {},
      loading: false,
      checked: true,
      weekConfig: [],
      selectDay: {afternoon: 0, number: 0, morning: 0},
      value: 0,
      show: false, // 控制弹窗
      appointmentReview: this.$route.query.appointmentReview === 'need_review' ? 'need_review' : 'no_review'
    }
  },
  created () {
    this.appointConfig()
    if (this.status === 1) {
      this.checked = false
    } else {
      this.checked = true
    }
  },
  methods: {
    // 是否开启预约
    changeAppointmentReview () {
      const params = {
        appointmentReview: this.appointmentReview,
        id: this.doctorId
      }
      setDoctorInfo(params).then((res) => {
        if (res.data.code === 0) {
          Toast({message: '修改成功', position: 'bottom', closeOnClick: true})
        }
      })
    },
    clickStepper () {
      if (!this.selectDay.id) {
        Toast({message: '请选择要修改的预约日期', position: 'bottom', closeOnClick: true})
      }
    },
    /**
     * 获取医生的配置
     */
    appointConfig () {
      appointConfig(this.doctorId).then(res => {
        console.log(res.data)
        this.doctorAppointConfig = res.data
        this.weekConfig.push({
          id: 1,
          name: '周一',
          number: this.doctorAppointConfig.numOfMondayMorning + this.doctorAppointConfig.numOfMondayAfternoon,
          morning: this.doctorAppointConfig.numOfMondayMorning,
          afternoon: this.doctorAppointConfig.numOfMondayAfternoon
        })
        this.weekConfig.push({
          id: 2,
          name: '周二',
          number: this.doctorAppointConfig.numOfTuesdayMorning + this.doctorAppointConfig.numOfTuesdayAfternoon,
          morning: this.doctorAppointConfig.numOfTuesdayMorning,
          afternoon: this.doctorAppointConfig.numOfTuesdayAfternoon
        })
        this.weekConfig.push({
          id: 3,
          name: '周三',
          number: this.doctorAppointConfig.numOfWednesdayMorning + this.doctorAppointConfig.numOfWednesdayAfternoon,
          morning: this.doctorAppointConfig.numOfWednesdayMorning,
          afternoon: this.doctorAppointConfig.numOfWednesdayAfternoon
        })
        this.weekConfig.push({
          id: 4,
          name: '周四',
          number: this.doctorAppointConfig.numOfThursdayMorning + this.doctorAppointConfig.numOfThursdayAfternoon,
          morning: this.doctorAppointConfig.numOfThursdayMorning,
          afternoon: this.doctorAppointConfig.numOfThursdayAfternoon
        })
        this.weekConfig.push({
          id: 5,
          name: '周五',
          number: this.doctorAppointConfig.numOfFridayMorning + this.doctorAppointConfig.numOfFridayAfternoon,
          morning: this.doctorAppointConfig.numOfFridayMorning,
          afternoon: this.doctorAppointConfig.numOfFridayAfternoon
        })
        this.weekConfig.push({
          id: 6,
          name: '周六',
          number: this.doctorAppointConfig.numOfSaturdayMorning + this.doctorAppointConfig.numOfSaturdayAfternoon,
          morning: this.doctorAppointConfig.numOfSaturdayMorning,
          afternoon: this.doctorAppointConfig.numOfSaturdayAfternoon
        })
        this.weekConfig.push({
          id: 7,
          name: '周日',
          number: this.doctorAppointConfig.numOfSundayMorning + this.doctorAppointConfig.numOfSundayAfternoon,
          morning: this.doctorAppointConfig.numOfSundayMorning,
          afternoon: this.doctorAppointConfig.numOfSundayAfternoon
        })
      })
    },
    morningChange (val) {
      console.log('morningChange', val)
      if (this.selectDay.id) {
        this.selectDay.morning = val
        this.selectDay.number = val + this.selectDay.afternoon
        this.weekConfig[this.selectDay.id - 1].morning = val
        this.weekConfig[this.selectDay.id - 1].number = this.selectDay.number
      } else {
        Toast({message: '请选择要修改的预约日期', position: 'bottom', closeOnClick: true})
      }
    },
    afterNoonChange (val) {
      console.log('afterNoonChange', val)
      if (this.selectDay.id) {
        this.selectDay.afternoon = val
        this.selectDay.number = val + this.selectDay.morning
        this.weekConfig[this.selectDay.id - 1].afternoon = val
        this.weekConfig[this.selectDay.id - 1].number = this.selectDay.number
      } else {
        Toast({message: '请选择要修改的预约日期', position: 'bottom', closeOnClick: true})
      }
    },
    switchChange () {
      const params = {
        id: this.doctorId,
        closeAppoint: this.checked ? 0 : 1
      }
      this.switchLoading = true
      updateDoctorInfo(params).then(res => {
        if (res.code === 0) {
          Toast({message: this.checked ? '已开启' : '已关闭', position: 'bottom', closeOnClick: true})
        }
        this.switchLoading = false
      }).catch(res => {
        this.switchLoading = false
      })
      console.log(this.checked)
    },
    /**
     * 选中一个日期
     */
    select (item) {
      this.selectDay = {
        id: item.id,
        number: item.number,
        morning: item.morning,
        afternoon: item.afternoon
      }
    },
    submit () {
      // 星期一
      this.doctorAppointConfig.numOfMondayMorning = this.weekConfig[0].morning
      this.doctorAppointConfig.numOfMondayAfternoon = this.weekConfig[0].afternoon
      // 星期二
      this.doctorAppointConfig.numOfTuesdayMorning = this.weekConfig[1].morning
      this.doctorAppointConfig.numOfTuesdayAfternoon = this.weekConfig[1].afternoon
      // 星期三
      this.doctorAppointConfig.numOfWednesdayMorning = this.weekConfig[2].morning
      this.doctorAppointConfig.numOfWednesdayAfternoon = this.weekConfig[2].afternoon
      // 星期四
      this.doctorAppointConfig.numOfThursdayMorning = this.weekConfig[3].morning
      this.doctorAppointConfig.numOfThursdayAfternoon = this.weekConfig[3].afternoon
      // 星期五
      this.doctorAppointConfig.numOfFridayMorning = this.weekConfig[4].morning
      this.doctorAppointConfig.numOfFridayAfternoon = this.weekConfig[4].afternoon
      // 星期六
      this.doctorAppointConfig.numOfSaturdayMorning = this.weekConfig[5].morning
      this.doctorAppointConfig.numOfSaturdayAfternoon = this.weekConfig[5].afternoon
      // 星期日
      this.doctorAppointConfig.numOfSundayMorning = this.weekConfig[6].morning
      this.doctorAppointConfig.numOfSundayAfternoon = this.weekConfig[6].afternoon
      console.log(this.weekConfig)
      console.log(this.doctorAppointConfig)
      if (this.loading) {
        return
      }
      this.loading = true
      updateAppointConfig(this.doctorAppointConfig).then(res => {
        this.loading = false
        if (res.code === 0) {
          this.show = true
        }
      }).catch(res => {
        this.loading = false
      })
    },
    /**
     * 跳转到上一页
     */
    goback () {
      this.$router.replace({
        path: '/reservation/reservationSetUp'
      })
    },
    /**
     * 点击我知道了
     */
    setUp () {
      this.goback()
    }
  }
}
</script>

<style scoped lang="less">
.date-number {
  height: 65px;
  text-align: center;
  line-height: 2;
  padding: 0 5px;
}

.date-numbers {
  background: #337EFF;
  border-radius: 4px;
  color: #ffffff;
}

/deep/ .van-stepper {
  .van-stepper__minus {
    margin-right: 10px;
    background: #ffffff;
  }

  .van-stepper__input {
    border-radius: 4px;
  }

  .van-stepper__plus {
    margin-left: 10px;
    background: #ffffff;
  }
}
</style>
