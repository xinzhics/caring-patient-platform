<template>
  <div class="my-calendar">
    <div class="calendarTitle">
      <div class="inner">
        <div style="width: 30px; height: 30px;" @click="editorCalendar(false)">
          <p class="arrow_left"></p>
        </div>
        <div>
          <span class="showDate">{{ sendDate }}</span>
        </div>
        <div style="width: 30px; height: 30px;" @click="editorCalendar(true)">
          <p class="arrow_right"></p>
        </div>
      </div>
      <div class="footnote">
        <span class="icon" style="background-color: #66E0A7"></span>
        <span class="text">已打针</span>
        <span class="icon" style="background-color: #FEBE37; margin-left: 9px;"></span>
        <span class="text">待打针</span>
      </div>
    </div>
    <div class="calendar-content">
      <div class="calendar-wrap">
        <!--日历星期-->
        <section class="week">
          <span v-for="(week, index ) in weekList" :key="'weekList' + index">{{ week }}</span>
        </section>
        <!--日历天数-->
        <section class="day">
          <!--空白格子-->
          <template v-for="(blank, index ) in blankList">
            <span class="blank" :key="'blankList' + index" v-if="blankList!='7'">{{ blank }}</span>
          </template>
          <span class="dayBar" v-for="(day, index ) in dayCalender" :key="'dayCalender' + index" @click="clickDayDetail(day)">
                <div class="item">
                  <div :class="{itemInner:true, 'isToday': today === day.date} ">
                    <span :class="{'show-formal':true,'isToday': today === day.date}">{{ today === day.date ? '今' : new Date(day.date).getDate() }}</span>
                    <span v-if="day.status===2" :class="{'dolt':true}"></span>
                    <span v-if="day.status===1" :class="{'doltT':true}"></span>
                  </div>
                </div>
              </span>
        </section>
      </div>
    </div>
  </div>
</template>
<script>
import Vue from 'vue'
import {findPatientInjectionCalendar} from '@/api/formResult.js'
import {getValue} from '@/components/utils/index'
import {Cell, Button, SwipeCell} from 'vant'
import moment from 'moment'
Vue.use(Cell)
Vue.use(Button)
Vue.use(SwipeCell)

export default {
  data () {
    return {
      dateType: 'month', // 当注射模式时，数据列表展示某天的注射记录
      sendDate: this.initialDateSet(), // datetime日期
      weekList: ['一', '二', '三', '四', '五', '六', '日'],
      blankList: '', // 空白的格子数
      choseFlag: {}, // 用来调整今天选中的样式
      dayCalender: [], // 日历数据
      patientId: localStorage.getItem('patientId'),
      today: '',
      planId: undefined,
      allInjectionDate: {}
    }
  },
  methods: {
    init (planId) {
      this.planId = planId
      console.log('planId init', planId)
      const date = moment().locale('zh-cn').format('YYYY-MM-DD')
      this.today = date
      this.choseFlag.date = date
      this.medicationDay = date
      this.findPatientInjectionCalendar(date)
    },
    /**
     * 查询注射的日历。
     */
    findPatientInjectionCalendar (data) {
      findPatientInjectionCalendar(this.patientId, this.planId, data).then(res => {
        this.allInjectionDate = res.data
        this.getTimeSheet(this.sendDate)
      })
    },
    // 每月第一天周几
    getBlank (year, month) {
      let blankNum = new Date(year, month, '01') // 下一个月一号是周几
      if (blankNum.getDay() === 0) {
        return 7
      }
      return blankNum.getDay()
    },
    // 获取当前年份和月份
    initialDateSet () {
      let thisDate = ''
      let nowDate = new Date()
      let nowYear = nowDate.getFullYear()
      let nowMonth = nowDate.getMonth() + 1 // 月份0-11
      if (nowMonth < 10) {
        thisDate = nowYear + '-' + '0' + nowMonth
      } else {
        thisDate = nowYear + '-' + nowMonth
      }
      return thisDate
    },
    // 点击某天，显示样式
    clickDayDetail (val) {
      this.choseFlag = val // 获取当前日期
      this.medicationDay = val.date
      this.dateType = 'day'
      console.log('clickDayDetail', val)
      this.$emit('changeSelectDate', {sendDate: this.sendDate, dateType: this.dateType, medicationDay: this.medicationDay})
    },
    editorCalendar (i) {
      if (i) {
        this.monthNum = 1
        this.sendDate = this.getNextMonth(true)
        this.dateType = 'month'
        this.choseFlag = {}
        this.findPatientInjectionCalendar(this.sendDate + '-01')
        this.$emit('changeSelectDate', {sendDate: this.sendDate, dateType: this.dateType, medicationDay: this.medicationDay})
      } else {
        this.monthNum = -1
        this.choseFlag = {}
        this.dateType = 'month'
        this.sendDate = this.getNextMonth(false)
        this.findPatientInjectionCalendar(this.sendDate + '-01')
        this.$emit('changeSelectDate', {sendDate: this.sendDate, dateType: this.dateType, medicationDay: this.medicationDay})
      }
    },
    getNextMonth (k) {
      let arr = this.sendDate.split('-')
      let year = arr[0] // 获取当前日期的年份
      let month = arr[1] // 获取当前日期的月份
      let day = arr[2] // 获取当前日期的日
      let year2 = year
      let month2 = parseInt(month) + this.monthNum
      if (month2 === 13 && k) {
        year2 = parseInt(year2) + 1
        month2 = 1
      }
      if (month2 === 0 && !k) {
        year2 = parseInt(year2) - 1
        month2 = 12
      }
      let day2 = day
      let days2 = new Date(year2, month2, 0)
      days2 = days2.getDate()
      if (day2 > days2) {
        day2 = days2
      }
      if (month2 < 10) {
        month2 = '0' + month2
      }

      let t2 = year2 + '-' + month2
      return t2
    },
    // 传一个日期，获取这个月的所有日期   2019-07 获取-->  2019-07-01-----2019-07-31
    getTimeSheet (date) {
      // 2019-01
      const that = this
      let nowMonth = date.slice(5, 7)
      let nowYear = date.slice(0, 4)
      this.blankList = this.getBlank(nowYear, nowMonth - 1) - 1
      this.dayCalender = []
      let year = +date.split('-')[0]
      let month = +date.split('-')[1]
      let d = new Date(year, month, 0)
      let dayLength = d.getDate() // 得到这一年这一月的天数
      let everyDate = '' // 定义每一天的日期  2019-01-01

      const injectionDateList = this.allInjectionDate.localDateList
      const nextRemindDate = this.allInjectionDate.nextRemindDate

      for (let k = 1; k <= dayLength; k++) {
        everyDate = year + '-' + month + '-' + k
        let myIndex = -1
        if (injectionDateList) {
          myIndex = injectionDateList.findIndex(
            res => that.changeFormat2(everyDate) === res
          )
        }
        if (myIndex !== -1) {
          that.dayCalender.push({
            date: that.changeFormat2(everyDate),
            condition: '',
            status: 1 // 表示已经填写了。
          })
        } else {
          // 判断是否未来的提醒日期
          if (nextRemindDate) {
            if (that.changeFormat2(everyDate) === nextRemindDate) {
              that.dayCalender.push({
                date: that.changeFormat2(everyDate),
                condition: '',
                status: 2 // 下次提醒日期
              })
              continue
            }
          }
          that.dayCalender.push({
            date: that.changeFormat2(everyDate),
            condition: ''
          })
        }
      }
      this.$forceUpdate()
      console.log(that.dayCalender)
    },
    // 时间格式转化   2019-1-1  ---》  2019-01-01
    changeFormat2 (date) {
      let arr1 = date.split('-')
      let year = arr1[0]
      let month = +arr1[1]
      let day = +arr1[2]
      month = month < 10 ? '0' + month : month // 判断日期是否大于10
      day = day < 10 ? '0' + day : day // 判断日期是否大于10
      return year + '-' + month + '-' + day
    },

    getFormValue (item) {
      return getValue(item)
    },

    getTime (time, format) {
      return moment(time).format(format)
    }
  }
}
</script>

<style lang="less" scoped>

  .my-calendar {
    background: #fff;

    .calendarTitle {
      width: 100%;
      padding: 10px 0px 15px;
      border-bottom: 1px solid #f5f5f5;
      background: #fff;
      display: flex;

      .footnote{

        width: 45%;
        text-align: center;
        background: #F7F7FB;
        border-radius: 25px;
        display: flex;
        align-items: center;
        justify-content: center;

        .icon{
          width: 7px;
          height: 7px;
          line-height: 0;
          display: inline-block;
          border-radius: 3px;
        }

        .text{
          color: #999999;
          margin-left: 5px;
        }

      }
      .inner {
        width: 50%;
        text-align: center;
        display: flex;
        justify-content: center;

        .showDate {
          margin: 0 15px;
          color: #666666;
          font-weight: bold;
          font-size: 19px;
        }

        .arrow_left {
          width: 0;
          height: 0;
          border-left: 6px solid transparent;
          border-right: 6px solid transparent;
          border-bottom: 10px solid #c0bebe;
          font-size: 0;
          line-height: 0;
          transform: rotate(270deg);
          display: inline-block;
        }

        .arrow_right {
          width: 0;
          height: 0;
          border-left: 6px solid transparent;
          border-right: 6px solid transparent;
          border-bottom: 10px solid #c0bebe;
          font-size: 0;
          line-height: 0;
          transform: rotate(90deg);
          display: inline-block;
        }
      }
    }

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
      padding-bottom: 30px;
      background-color: white;

      .calendar-wrap {
        width: 100%;
        position: relative;
        left: 0;
        top: 0;
        font-size: 14px;

        .week {
          padding: 0 4%;
          display: flex;
          flex-wrap: nowrap;
          margin-bottom: 10px;
          font-family: SourceHanSansCN, SourceHanSansCN;
          background: #fff;

          font-weight: 400;
          font-size: 11px;
          color: #A6A6A6;
          line-height: 17px;
          text-align: center;
          font-style: normal;

          span {
            flex: 1;
            text-align: center;
            margin-bottom: 4px;
          }
        }

        .day {
          padding: 0 4%;
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
            text-align: center;
            height: 45px;
            margin-bottom: 4px;
          }

          .dayBar {
            position: relative;

            .item {
              margin: auto auto;
              border-radius: 50%;

              .itemInner {
                padding: 15%;
                width: 25px;
                height: 25px;
                margin: auto auto;
                overflow: hidden;
                border-radius: 50%;
                font-weight: bold;
                font-size: 15px;
                color: #000000;
                line-height: 17px;
                letter-spacing: 0;
                text-align: center;
                font-style: normal;
              }

              .isToday{
                background-color: #66E0A7;
                font-weight: 600;
                font-size: 16px;
                color: #FFFFFF;
                line-height: 21px;
                letter-spacing: 0px;
                text-align: center;
                font-style: normal;
              }

              .bigchange {
                color: #000 !important;
                box-shadow: 0 0 0 1px #66728c;
              }

              .smallchange {
                border: 1px solid #66728c;
                color: #66728c;
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
            background-size: 35px 35px;
            color: #fff;
            background: #444;
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
            background: #FEBE37;
            display: block;
            border-radius: 50%;
            margin-top: 6px;
            margin-left: auto;
            margin-right: auto;
          }

          .doltT {
            width: 8px;
            height: 8px;
            background: #66E0A7;
            display: block;
            border-radius: 50%;
            margin-top: 6px;
            margin-left: auto;
            margin-right: auto;
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
        padding-right: 20px;
        font-size: 12px;
        color: rgba(102, 102, 102, 0.6);

        span {
          margin-right: 10px;
        }

        .dolt {
          display: inline-block;
          width: 8px;
          height: 8px;
          background: #66728c;
          border-radius: 50%;
          margin-right: 5px;
        }

        .doltT {
          display: inline-block;
          width: 8px;
          height: 8px;
          background: #ffbe8b;
          border-radius: 50%;
          margin-right: 5px;
        }
      }
    }

    .calendar-medication {
      background: #f5f5f5;
    }
  }

</style>
