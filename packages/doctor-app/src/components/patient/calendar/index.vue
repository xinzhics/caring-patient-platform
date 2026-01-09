<template>
  <div style="position: relative; height: 100%; background: #f5f5f5">
    <div class="my-calendar">
      <div class="calendar-content">
        <div style="display: flex; align-items: center; justify-content: space-between; padding: 0px 4% 10px 4%">
          <div style="font-size: 16px; color: #333; font-weight: bold">{{ getMonthName() }}</div>
          <div class="endDesc">
            <span class="dolt"></span>
            <span>已打卡</span>
            <span class="doltT" style="margin-left: 10px"></span>
            <span style="color:#333;">未打卡</span>
          </div>
        </div>

        <div class="calendar-wrap">
          <!--日历星期-->
          <section class="week" style="color: #999">
            <span v-for="(week, index ) in weekList" :key="'weekList' + index">{{ week }}</span>
          </section>
          <!--日历天数-->
          <section class="day" @touchstart="startTouch" @touchmove="moveTouch" @touchend="endTouch">
            <!--空白格子-->
            <template v-for="(blank, index ) in blankList">
              <span class="blank" :key="'blankList' + index" v-if="blankList!='7'">{{ blank }}</span>
            </template>

            <span class="dayBar" v-for="(day, index ) in dayCalender" :key="'dayCalender' + index"
                  @click.stop="clickDayDetail(day)" style="font-size: 16px; font-weight: bold">
                <div class="item">
                  <div>
                    <div
                      style="width: 25px; height: 25px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 5px"
                      :class="{'show-formal':true,'noShow':new Date()<new Date(day.date)}"
                      :style="{background: day.date === weekCurrentDate ? '#9CEBC6' : ''}">
                      {{ new Date(day.date).getDate() }}
                    </div>
                    <span v-if="new Date()>=new Date(day.date)&&day.status===0" class="dolt"></span>
                    <span v-else-if="new Date()>=new Date(day.date)&&(day.status===1 || day.status===2)"
                          class="doltT"></span>
                    <span v-else class="dolt" style="background: white"></span>
                  </div>
                </div>
              </span>
          </section>
          <div style="height: 30px; display: flex; align-items: center; justify-content: center"
               @click="isWeekOrCalendar()">
            <div style="width: 40px; height: 4px; border-radius: 5px; background: #C6C6C6"></div>
          </div>

        </div>
      </div>
      <div style="display: flex; justify-content: center; align-items: center; margin-top: 15px; margin-bottom: 15px" v-if="list && list.length !== 0">
        <van-button type="primary" size="small" style="width: 100px; font-weight: bold; background: #67E0A7; border: 0px" round>用药安排</van-button>
      </div>

      <div class="calendar-medication" v-if="list && list.length !== 0">
        <div class="van-steps van-steps--vertical" style="background: unset">
          <div class="van-steps__items">
            <div class="van-hairline van-step van-step--vertical"
                 v-for="(item, index) in list" :key="index">
              <div class="van-step__title">
                <div style="margin-bottom: 10px; font-weight: bold" :style="{color: stepActive === index ? '#40BD54' : '#333'}">
                  {{ getTime(item.drugsTime, 'HH:mm') }}
                </div>

                <div v-for="(drug, key) in item.patientDrugsTimes" :key="key"
                     :style="{marginBottom: key === item.patientDrugsTimes.length -1 ? '0' : '10px',
                      boxShadow: stepActive === index ? '2px 2px 4px rgba(0, 0, 0, 0.2)' : ''}"
                     style="display: flex; align-items: center; justify-content: space-between; background: rgb(255, 255, 255); padding: 10px; border-radius: 8px;">
                  <div style="color: rgb(51, 51, 51); font-weight: bold">
                    {{ drug.medicineName }} {{ '  ' + (drug.drugsDose === -1 ? '外用' : drug.drugsDose) + drug.unit }}
                  </div>
                  <div v-if="drug.status === 2 && compareTime(drug.drugsTime)"
                       :style="{background : !isToday(drug.drugsTime) ? '#999' : stepActive === index ? '#40BD54' : '#FF5A5A'}"
                       style="color: white; background: rgb(145, 203, 147); border-radius: 7px; font-size: 12px; padding: 3px 10px;">
                    {{!isToday(drug.drugsTime) ? '打卡' : stepActive === index ? '打卡' : '漏服'}}
                  </div>
                  <div v-else-if="drug.status === 1" style="font-size: 12px; color: #333">
                    已打卡
                  </div>
                </div>
              </div>
              <div class="van-step__circle-container"><i class="van-step__circle" style="width: 7px; height: 7px;"
                                                         :style="{ background: stepActive === index ? '#40BD54' : '#969799'}"></i>
              </div>
              <div class="van-step__line"
                   style="background: unset; height:90%; left: -16px; padding-top: 5px; width: 0px; padding-top: 2px; padding-bottom: 2px; "
                   :style="{border: stepActive === index ? '1px dashed #40BD54' : '1px dashed #969799'}"></div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="noData" >
        <van-image :src="require('@/assets/my/noData.png')" width="70%"/>
        <div>{{patient}}未添加数据</div>
        <div style="margin-top: 5px;">点击<span style="background: #67E0A7; color: white; border-radius: 15px; padding: 3px 8px; margin: 0px 3px;" @click="goRecommend">推荐功能</span>则可将该功能推送至{{patient}}填写
        </div>
      </div>
    </div>

    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;">
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #FFF" @click="medicineList()">
          <span>查看用药记录</span>
        </div>
      </van-col>
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="addMedication">
          <span>新增用药提醒</span>
        </div>
      </van-col>
    </van-row>


    <x-dialog v-model="show" class="dialog-demo">
      <div style="line-height:40px;padding:20px 10px 10px;text-align:center">
        <span style="font-size:1.2rem;font-weight:600;color:rgba(102,102,102,1);">选择已用药品</span>
        <x-icon @click="show=false" type="ios-close-empty" style="position: absolute;right: 10px;top: 10px;" size="30"></x-icon>
        <span style="width:30px;border-bottom:3px solid #FFBE8B;display:block;margin:0px auto;border-radius:3px"></span>
      </div>
      <div style="padding-bottom:30px">
        <div v-for="(i,k) in drugsList" :key="k" style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <div style="color:#666666;width: 80%;word-wrap: break-word;">{{i.medicineName}}</div>
          <van-checkbox v-model="i.checked" checked-color="#66728B" style="float: right; margin-right: 5px;" />
        </div>
        <div style="font-size:13px;color:#999;line-height:20px;margin:20px auto;">
          <p>养成良好的用药习惯，</p>
          <p>将有助于更好的管理自己的病情哦~</p>
        </div>

        <x-button style="width:70%;background:#66728C;color:#fff" @click.native="btn">确定打卡</x-button>
      </div>
    </x-dialog>

    <van-dialog v-model="recording" :title="recordingName" show-cancel-button confirmButtonText="确定补录"
                @confirm="confirmRecording" @cancel="cancelRecording" :beforeClose="beforeClose">
      <div style="padding: 15px; max-height: 400px;">
        <div v-for="(item, index) in recordingList" :key="index" v-if="item.status === 2 && index < 7"
             style="background: #f5f5f5;  padding: 10px 10px; border-radius: 5px; margin-top: 10px;">
          <div @click="clickItem(item)"
               style="display:flex; font-size: 12px;  text-overflow: ellipsis;justify-content: space-between">
            <div style="width: 90%">
              {{ (index + 1) + '. ' }}{{ item.medicineName }}({{ item.drugsDose + item.unit }})
            </div>
            <van-checkbox v-model="item.checked" checked-color="#66728B" style="float: right; margin-right: 5px;"/>
          </div>
        </div>

        <div
          style="display: flex; align-items: center; justify-content: center; font-size: 12px; color: #333333; margin-top: 12px;">
          下次用药后记得点击打卡哟~
        </div>

        <div
          style="display: flex; align-items: center; justify-content: center; font-size: 12px; color: #FF6F00; margin-top: 12px;"
          v-if="!isBeforeClose">
          请选择补录药品
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script>

import Api from '@/api/Content.js'
import Vue from 'vue';
import moment from 'moment';
import {Button, Step, Steps, Dialog} from 'vant';

Vue.use(Button);
Vue.use(Step);
Vue.use(Steps);

export default {
  components: {
    [Dialog.Component.name]: Dialog.Component,
  },
  data() {
    return {
      weekList: ['一', '二', '三', '四', '五', '六', '日'],
      dayCalender: [],
      blankList: [],
      isWeek: true,
      choseFlag: '', // 用来调整今天选中的样式
      sendDate: moment().format('yyyy-MM'),
      weekCurrentDate: moment().format('yyyy-MM-DD'),
      list: [],
      stepActive: -1,
      recording: false,
      recordingList: [],
      isBeforeClose: true,
      startX: 0,
      recordingName: '补录已用药品',
      currentX: 0,
      drugsList: [],
      show: false,
      patient: this.$getDictItem('patient'),
    }
  },
  mounted() {
    // 获取打卡记录
    this.getData()
    this.getMedicationPlan()
  },
  methods: {
    goRecommend() {
      this.$emit('recommendation', 'CALENDAR')
    },
    btn() {
      // if()
      const that = this
      let allids = []
      let cango = false
      let drugsTime = ''
      that.drugsList.forEach(element => {
        if (!element.checked) {
          cango = true
        }
        if (element.checked) {
          allids.push(element.id)
          drugsTime = element.drugsTime
        }
      })
      if (cango) {
        that.$vux.toast.text('请选择要打卡的药品', 'center')
        return
      }
      const params = {
        ids: allids
      }
      Api.clockIn(params).then(res => {
        if (res.data.code === 0) {
          that.$vux.toast.text('打卡成功', 'center')
          that.getMedicationPlan()
          that.show = false
          this.dayCalender.forEach(item => {
            if (item.date === moment(drugsTime).format('YYYY-MM-DD') && item.status === 0) {
              item.status = 1
            }
          })
        }
      })
    },
    // 获取打卡记录
    getDrugsTime(i) {
      const that = this
      const params = {
        drugsTimestamp: i
      }
      Api.queryByDrugsTimestamp(params).then(res => {
        if (res.data.code === 0) {
          console.log(res.data.data)
          this.drugsList = res.data.data
          res.data.data.forEach(element => {
            if (element.status === 1) {
              that.show = false
              that.$vux.toast.text('您已完成打卡！', 'center')
            } else {
              that.show = true
            }
          })
        }
      })
    },
    compareTime(time) {
      let day = moment().format('yyyy-MM-DD HH:mm')
      return !moment(day).isBefore(time)
    },
    // 开始移动
    startTouch(event) {
      this.startX = event.touches[0].clientX;
    },
    // 移动结束
    moveTouch(event) {
      this.currentX = event.touches[0].clientX;
    },
    // 移动结束
    endTouch(event) {
      if (this.startX === 0 || this.currentX === 0) {
        return
      }
      if (this.isWeek) {
        if (this.currentX - this.startX > 100) {
          // 左
          this.weekCurrentDate = this.dayCalender[0].date
          this.getDayCalender(this.sendDate)
        } else if (this.startX - this.currentX > 100) {
          // 右
          this.weekCurrentDate = moment(this.dayCalender[this.dayCalender.length - 1].date).add(6, 'days').format('YYYY-MM-DD')
          if (moment(moment(this.weekCurrentDate).subtract(1, 'day').format('YYYY-MM-DD')).isSameOrBefore(moment(), 'day')) {
            this.getDayCalender(this.sendDate)
          } else {
            this.$vux.toast.text('不能查看当前时间之后的日历', 'center')
          }
        }
      } else {
        if (this.currentX - this.startX > 100) {
          // 左
          this.monthNum = -1
          this.sendDate = this.getNextMonth(false)
          this.getDayCalender(this.sendDate)
        } else if (this.startX - this.currentX > 100) {
          // 右
          this.monthNum = 1
          if (new Date() < new Date(this.getNextMonth(true))) {
            this.$vux.toast.text('不能查看当前时间之后的日历', 'center')
          } else {
            this.sendDate = this.getNextMonth(true)
            this.getDayCalender(this.sendDate)
          }
        }
      }
      this.startX = 0
      this.currentX = 0
    },
    // 日期点击
    clickDayDetail(item) {
      console.log('clickDayDetail', item)
      let day = moment().format('yyyy-MM-DD')
      if (item.date > day) {
        this.$vux.toast.text('不能查看当前时间之后的日历', 'center')
        return
      }
      this.stepActive = -1
      this.weekCurrentDate = item.date
      this.getMedicationPlan()
    },
    // 获取用药列表
    getMedicationPlan() {
      Api.getPatientMedicationPlan(this.weekCurrentDate).then(res => {

        if (res.data && res.data.code === 0) {
          if (res.data.data) {
            this.list = res.data.data
            this.stepActive = -1
            for (let i = 0; i < this.list.length; i++) {
              if (this.getContrastTime(this.list[i].drugsTime)) {
                this.stepActive = i
              }
            }
          } else {
            this.list = []
          }
        }
      })
    },
    // 周月切换
    isWeekOrCalendar() {
      this.isWeek = !this.isWeek
      this.sendDate = moment().format('yyyy-MM'),
        this.weekCurrentDate = moment().format('yyyy-MM-DD'),
        this.getData()
    },
    getData() {
      if (this.isWeek) {
        // 周显示
        this.getWeekInfo()
      } else {
        // 月显示
        this.getMonthInfo()
      }
    },
    // 获取周数据
    getWeekInfo() {
      Api.patientDayDrugsTo7Day().then(res => {
        if (res.data.code === 0) {
          this.allData = res.data.data.calendar
          this.getDayCalender(this.sendDate)
        }
      })
    },
    // 获取月数据
    getMonthInfo() {
      const params = {
        date: this.sendDate,
        patientId: localStorage.getItem('patientId')
      }
      Api.patientDayDrugsCalendar(params).then(res => {
        if (res.data.code === 0) {
          this.allData = res.data.data.calendar
          this.getDayCalender(this.sendDate)
        }
      })
    },
    getDayCalender(date) {
      const that = this
      if (this.isWeek) {
        // 周数据
        this.blankList = []
        this.weekList = []
        this.dayCalender = []
        let sevenDaysAgo = moment(this.weekCurrentDate).add(1, 'day').clone().subtract(7, 'days')
        for (let i = 0; i < 7; i++) {
          let day = sevenDaysAgo.clone().add(i, 'days')
          this.weekList.push(this.getWeekName(day.format('dddd')))
          if (that.allData && that.allData.length > 0) {
            let myIndex = that.allData.findIndex(res => that.changeFormat3(res.createTime)[0] === day.format('YYYY-MM-DD'))
            if (myIndex !== -1) {
              that.dayCalender.push({
                date: day.format('YYYY-MM-DD'),
                condition: '',
                status: that.allData[myIndex].status
              })
            } else {
              that.dayCalender.push({
                date: day.format('YYYY-MM-DD'),
                condition: ''
              })
            }
          } else {
            that.dayCalender.push({
              date: day.format('YYYY-MM-DD'),
              condition: ''
            })
          }
        }
        this.sendDate = this.dayCalender[0].date
      } else {
        this.weekList = ['一', '二', '三', '四', '五', '六', '日']
        let nowMonth = date.slice(5, 7)
        let nowYear = date.slice(0, 4)
        // 月数据
        this.blankList = this.getBlank(nowYear, nowMonth - 1) - 1
        this.dayCalender = []
        let year = +date.split('-')[0]
        let month = +date.split('-')[1]
        let d = new Date(year, month, 0)
        let dayLength = d.getDate() // 得到这一年这一月的天数
        let everyDate = '' // 定义每一天的日期  2019-01-01
        for (let k = 1; k <= dayLength; k++
        ) {
          everyDate = year + '-' + month + '-' + k
          if (that.allData && that.allData.length > 0) {
            let myIndex = that.allData.findIndex(
              res => that.changeFormat2(everyDate) === that.changeFormat3(res.createTime)[0]
            )
            if (myIndex !== -1) {
              that.dayCalender.push({
                date: that.changeFormat2(everyDate),
                condition: '',
                status: that.allData[myIndex].status
              })
            } else {
              that.dayCalender.push({
                date: that.changeFormat2(everyDate),
                condition: ''
              })
            }
          } else {
            that.dayCalender.push({
              date: that.changeFormat2(everyDate),
              condition: ''
            })
          }
        }
      }

    },
    // 获取周名称
    getWeekName(week) {
      switch (week) {
        case 'Monday':
          return '一'
        case 'Tuesday':
          return '二'
        case 'Wednesday':
          return '三'
        case 'Thursday':
          return '四'
        case 'Friday':
          return '五'
        case 'Saturday':
          return '六'
        case 'Sunday':
          return '日'
      }
    },
    // 每月第一天周几
    getBlank(year, month) {
      let blankNum = new Date(year, month, '01') // 下一个月一号是周几
      if (blankNum.getDay() === 0) {
        return 7
      }
      return blankNum.getDay()
    },
    // 时间格式转化   2019-1-1  ---》  2019-01-01
    changeFormat2(date) {
      let arr1 = date.split('-')
      let year = arr1[0]
      let month = +arr1[1]
      let day = +arr1[2]
      month = month < 10 ? '0' + month : month // 判断日期是否大于10
      day = day < 10 ? '0' + day : day // 判断日期是否大于10
      return year + '-' + month + '-' + day
    },
    // 时间格式转化   2019-01-01  ---》  2019-01
    changeFormat3(date) {
      const newDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(date)
      return newDate
    },
    // 获取名字
    getMonthName() {
      return moment(this.sendDate).format('yyyy年M月')
    },
    getNextMonth(k) {
      var arr = this.sendDate.split('-')
      var year = arr[0] //获取当前日期的年份
      var month = arr[1] //获取当前日期的月份
      var day = arr[2] //获取当前日期的日
      var days = new Date(year, month, 0)
      days = days.getDate() //获取当前日期中的月的天数
      var year2 = year
      var month2 = parseInt(month) + this.monthNum
      if (month2 == 13 && k) {
        year2 = parseInt(year2) + 1
        month2 = 1
      }
      if (month2 == 0 && !k) {
        year2 = parseInt(year2) - 1
        month2 = 12
      }
      var day2 = day
      var days2 = new Date(year2, month2, 0)
      days2 = days2.getDate()
      if (day2 > days2) {
        day2 = days2
      }
      if (month2 < 10) {
        month2 = '0' + month2
      }

      var t2 = year2 + '-' + month2
      return t2
    },
    getTime(time, format) {
      if (time) {
        return moment(time).format(format)
      } else {
        return moment().format(format)
      }
    },
    getContrastTime(time) {
      if (this.isToday(time)) {
        // 当天, 并且为当前小时
        if (moment().format('HH:mm') === moment(time).format('HH:mm')) {
          return true
        }
      }
      return false
    },
    isToday(time) {
      if (moment().format('YYYY-MM-DD') === moment(time).format('YYYY-MM-DD')) {
        // 当天
        return true
      }
      return false
    },
    clickRecording(item, drugsTime) {
      if (moment(drugsTime).format('YYYY-MM-DD HH') === moment().format('YYYY-MM-DD HH')) {
        let time = moment(drugsTime).format('YYYY-MM-DD HH')
        this.getDrugsTime(moment(time).format('X'))
      } else {
        this.recording = true
        this.isBeforeClose = true
        this.recordingList = item
        this.recordingName = '补录已用药品'
      }
    },
    // 漏服打卡
    confirmRecording() {
      let ids = []
      let drugsTime = ''
      this.recordingList.forEach(item => {
        if (item.checked) {
          ids.push(item.id)
          drugsTime = item.drugsTime
        }
      })
      if (ids.length === 0) {
        this.isBeforeClose = false
        return
      }

      Api.postRecordingMedication(ids).then(res => {
        if (res.data && res.data.code === 0) {
          this.recording = false
          this.dayCalender.forEach(item => {
            if (item.date === moment(drugsTime).format('YYYY-MM-DD') && item.status === 0) {
              item.status = 1
            }
          })
          this.getMedicationPlan()
        }
      })
    },
    // 漏服取消
    cancelRecording() {
      this.isBeforeClose = true
      this.recordingList.forEach(item => {
        item.checked = false
      })
    },
    beforeClose(action, done) {
      return done(this.isBeforeClose)
    },
    /**
     * 点击item
     */
    clickItem(item) {
      item.checked = !item.checked
      this.$forceUpdate()
      console.log(item)
    },
    // 添加用药
    addMedication() {
      if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.push({path: '/medicine/addmedicine', query: {
            imMessageId: this.$route.query.imMessageId
          }})
      }else {
        this.$router.push('/medicine/addmedicine')
      }
    },
    // 历史记录
    medicineList() {
      this.$router.push('/medicine/index')
    }
  },
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
  padding-top: 30px;
  margin-bottom: 100px;
  background: #f5f5f5;
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

.my-calendar {
  padding: 15px 12px 15px 12px;
  border-radius: 8px;
  position: relative;

  .head-date {
    background: #f5f5f5;
    height: 48px;
    padding: 10px;

    .sheet-date-time {
      width: 90px;
      height: 28px;
      position: relative;
      background-color: white;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;

      .date-time-text {
        color: #333333;
        font-size: 12px;
      }

      .triangle-down {
        //倒立三角形
        width: 0;
        height: 0;
        margin-left: 4px;
        border-left: 3.5px solid transparent;
        border-right: 3.5px solid transparent;
        border-top: 6px solid #cdcdcd;
      }
    }
  }

  .calendar-content {
    //padding: 20px 0;
    padding-top: 25px;
    background-color: white;
    border-radius: 8px;

    .calendar-wrap {
      width: 100%;
      position: relative;
      left: 0;
      top: 0;
      font-size: 14px;

      .week {
        padding: 0 2%;
        display: flex;
        flex-wrap: nowrap;
        line-height: 35px;
        color: #333333;
        margin-bottom: 10px;
        font-family: PingFangSC-Semibold;
        font-weight: 600;
        background: #fff;

        span {
          flex: 1;
          text-align: center;
          margin-bottom: 4px;
        }
      }

      .day {
        padding: 0 2%;
        display: flex;
        flex-wrap: wrap;
        background: #fff;
        align-items: center;
        font-family: PingFangSC-Regular;
        font-weight: 400;

        span {
          flex: 1;
          flex-basis: 14.2%;
          flex-grow: 0;
          // line-height: 35px;
          text-align: center;
          margin-bottom: 4px;
        }

        .dayBar {
          position: relative;

          .item {
            margin: auto auto;
            border-radius: 50%;
            display: flex;
            justify-content: center;

            .bigchange {
              color: #000 !important;
              //background: #66728B;
              box-shadow: 0 0 0 1px #40BD54;
            }

            .smallchange {
              border: 1px solid #40BD54;
              background: #40BD54;
              color: #fff;
            }
          }

          i {
            position: absolute;
            bottom: 0;
            display: flex;
            width: 100%;

            .normal {
              width: 5px;
              height: 5px;
              border-radius: 50%;
              background: #2661ff;
              position: absolute;
              bottom: 0;
              right: 45%;
            }

            .abnormal {
              width: 5px;
              height: 5px;
              border-radius: 50%;
              background: #f9400b;
              position: absolute;
              bottom: 0;
              right: 45%;
            }
          }
        }

        .chose-span {
          // background: url("../../assets/images/circle.png") center
          // center no-repeat;
          background-size: 35px 35px;
          color: #fff;
          background: #444;
          /*pointer-events: none;//事件阻止*/
        }

        .smallchange {
          border: 1px solid #444;
        }

        .noShow {
          color: rgba(102, 102, 102, 0.85);
        }

        .show-formal {
          width: 20px;
          height: 10px;
          display: inline-block;
        }

        .dolt {
          width: 8px;
          height: 8px;
          background: #939393;
          display: block;
          border-radius: 50%;
          margin: auto auto;
        }

        .doltT {
          width: 8px;
          height: 8px;
          background: #40BD54;
          display: block;
          border-radius: 50%;
          margin: auto auto;
        }

        .show-today {
          color: #2196f3;
          background: #444;
        }

        .show-Weekend {
          color: #bebebe;
        }

        span.blank {
          color: transparent;
        }
      }
    }

    .endDesc {
      text-align: right;
      font-size: 12px;
      color: rgba(102, 102, 102, 0.6);

      .dolt {
        display: inline-block;
        width: 8px;
        height: 8px;
        background: #40BD54;
        border-radius: 50%;
        margin-right: 5px;
      }

      .doltT {
        display: inline-block;
        width: 8px;
        height: 8px;
        background: #939393;
        border-radius: 50%;
        margin-right: 5px;
      }
    }
  }

  .calendar-medication {
    background: #f5f5f5;
    padding-bottom: 50px;
  }
}

/*/deep/ .van-step--vertical .van-step__line {
  border: 1px dashed #EEE;
}*/
</style>
