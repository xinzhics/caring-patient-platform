<template>
  <div>
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" :rightIcon="headerImg" :title="pageTitle ? pageTitle : alldata.name"
                      @onBack="$router.go(-1)" @showpop="toDrugsBox" ></headNavigation>
    </van-sticky>
    <div class="my-calendar">
      <div class="tip">
        <img :src="bloodimg" alt="" srcset=""><span>坚持用药有助于您的病情早日康复</span>
      </div>
      <div class="calendarTitle">
        <div class="inner">
          <p class="arrow_left" @click="editorCalendar(false)"></p>
          <span class="showDate">{{ sendDate }}</span>
          <p class="arrow_right" @click="editorCalendar(true)"></p>
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
              <div :class="{'item':true}">
                <div :class="{itemInner:true, 'smallchange': new Date().toDateString()==new Date(day.date).toDateString(), 'bigchange': choseFlag === day && new Date() > new Date(choseFlag.date)} ">
                  <span
                    :class="{'show-formal':true,'noShow':new Date()<new Date(day.date)}">{{ new Date(day.date).getDate()}}</span>
                  <span v-if="new Date()>=new Date(day.date)&& day.status===0" :class="{'dolt':true}"></span>
                  <span v-if="new Date()>=new Date(day.date)&&(day.status===1 || day.status===2)" :class="{'doltT':true}"></span>
                </div>
              </div>
            </span>
          </section>
        </div>
        <div class="endDesc">
          <span class="dolt"></span>
          <span>已打卡</span>
          <span class="doltT"></span>
          <span>未打卡</span>
        </div>
      </div>
    </div>
    <div style="background: #f5f5f5" v-if="list && list.length !== 0">
      <div style="margin-top: 15px; background: #FFF;display: block; padding: 15px;">
        <div style="margin-bottom: 15px;">
          <span style="font-size: 16px; color: #333333">用药安排</span>
        </div>
        <div style="display: flex; align-items: center; overflow:hidden;position: relative;" v-for="(i, k) in list" :key="k">
          <div style="margin-right: 5px;">{{getTime(i.drugsTime, 'HH:mm')}}</div>
          <div
            style="height: 100%; margin-left: 15px; margin-right: 20px; margin-bottom: -1000px; margin-top: -1000px; padding-top: 1000px; padding-bottom: 1000px; position: relative">
            <div
              style="display: flex; justify-content: center; align-items: center; top:50%;left:50%;transform:translate(-50%,-50%); height: 20px; width: 20px; position: absolute; border-radius: 15px;z-index:999"
              :style="'background:'+ (getContrastTime(i.drugsTime) ? '#B8B8B8' : '#66728B')">
              <div style="height: 10px; width: 10px; border-radius: 10px; background: white"></div>
            </div>
          </div>
          <van-row style="width: 100%; background: #f5f5f5; display:flex;
            justify-content:center;
            align-items:center;
           border-radius: 10px; padding: 10px; margin-top: 10px; margin-bottom: 10px;">
            <van-col :span="18">
              <div v-for="(item, key) in i.patientDrugsTimes" :key="key"
                   style="font-size: 13px;">
                {{item.medicineName}}({{item.drugsDose + item.unit}})
              </div>
            </van-col>
            <van-col :span="6" style="display: flex; justify-content: center;">
              <span v-if="i.status === 2 && getContrastTime(i.drugsTime)" style="color: white; background: #FF5555; border-radius: 15px; font-size: 12px; padding: 3px 10px;">漏服</span>
            </van-col>
          </van-row>
          <div v-if="k === 0"  :class="k === 0 && list.length <= 1 ? 'line2 line' : 'line line1'" ></div>
          <div v-if="k > 0 && k < list.length - 1" class="line" ></div>
          <div v-if="k === list.length - 1 && list.length > 1"  class="line line3" ></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { getPatientMedicationPlan, patientDayDrugsCalendar } from '@/api/drugsApi.js'
import moment from 'moment'
import Vue from 'vue'
import {Col, Icon, Row, Sticky, Toast, Button} from 'vant'
Vue.use(Sticky)
Vue.use(Icon)
Vue.use(Toast)
Vue.use(Row)
Vue.use(Col)
Vue.use(Button)
export default {
  data () {
    return {
      bloodimg: require('@/assets/patient/calender.png'),
      headerImg: require('@/assets/patient/yiyaoxiang.png'),
      weekList: ['一', '二', '三', '四', '五', '六', '日'],
      blankList: '', // 空白的格子数
      choseFlag: '', // 用来调整今天选中的样式
      dayCalender: [], // 日历数据
      sendDate: this.initialDateSet(), // datetime日期
      selected: '',
      monthNum: 0,
      show: false,
      allData: [],
      pageTitle: localStorage.getItem('pageTitle'),
      drugsList: [],
      medicationDay: '',
      list: []
    }
  },
  mounted () {
    this.getInfo()
    this.medicationDay = this.getTime('', 'YYYY-MM-DD')
    this.getMedicationPlan()
  },
  methods: {
    getContrastTime (time) {
      return moment(time).isBefore(new Date(), 'YYYY-MM-DD HH:mm')
    },
    getTime (time, format) {
      if (time) {
        return moment(time).format(format)
      } else {
        return moment().format(format)
      }
    },
    toDrugsBox () {
      this.$router.push('/patient/medicine/index')
    },
    getMedicationPlan () {
      getPatientMedicationPlan(this.medicationDay)
        .then(res => {
          if (res && res.code === 0) {
            this.list = res.data
          }
        })
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

    // 传一个日期，获取这个月的所有日期   2019-07 获取-->  2019-07-01-----2019-07-31
    getTimeSheet (date) { // 2019-01
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
      for (let k = 1; k <= dayLength; k++) {
        everyDate = year + '-' + month + '-' + k
        if (that.allData && that.allData.length > 0) {
          let myIndex = that.allData.findIndex((res) => that.changeFormat2(everyDate) === that.changeFormat3(res.createTime)[0])
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
            date: that.changeFormat2(everyDate)
          })
        }
      }
    },

    // 每月第一天周几
    getBlank (year, month) {
      let blankNum = new Date(year, month, '01') // 下一个月一号是周几
      if (blankNum.getDay() === 0) {
        return 7
      }
      return blankNum.getDay()
    },

    // 时间格式转化   2019-01-01  ---》  2019-01
    changeFormat3 (date) {
      const newDate = /\d{4}-\d{1,2}-\d{1,2}/g.exec(date)
      return newDate
    },
    // 时间格式转化   2019-1-1  ---》  2019-01-01
    changeFormat2 (date) {
      let arr1 = date.split('-')
      let year = arr1[0]
      let month = +arr1[1]
      let day = +arr1[2]
      month = month < 10 ? ('0' + month) : month // 判断日期是否大于10
      day = day < 10 ? ('0' + day) : day // 判断日期是否大于10
      return year + '-' + month + '-' + day
    },
    // 点击某天，显示样式
    clickDayDetail (val) {
      if (new Date() < new Date(val.date)) {
        Toast({ message: '不能查看当前时间之后的日历', closeOnClick: true, position: 'center' })
      } else {
        // this.show=true
        this.choseFlag = val // 获取当前日期
        this.medicationDay = val.date
        this.getMedicationPlan()
      }
    },
    editorCalendar (i) {
      if (i) {
        this.monthNum = 1
        if (new Date() < new Date(this.getNextMonth(true))) {
          Toast({ message: '不能查看当前时间之后的日历', closeOnClick: true, position: 'center' })
        } else {
          this.sendDate = this.getNextMonth(true)
          this.getTimeSheet(this.sendDate)
        }
      } else {
        this.monthNum = -1
        this.sendDate = this.getNextMonth(false)
        this.getTimeSheet(this.sendDate)
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
      return year2 + '-' + month2
    },
    getInfo () {
      const params = {
        date: this.sendDate,
        patientId: localStorage.getItem('patientId')
      }
      patientDayDrugsCalendar(params).then((res) => {
        if (res.code === 0) {
          this.allData = res.data.calendar
          this.getTimeSheet(this.sendDate)
        }
      })
    }
  },
  watch: {
    sendDate: {
      handler (newValue, oldValue) {
        this.getTimeSheet(newValue) // 监听sendDate变化，即时变化日历
        this.getInfo()
      },
      immediate: true, // 页面一进入就开始监听
      deep: false
    }
  }
}
</script>
<style lang="less" scoped>
.line1{
  transform: translateY(50%);
}
.line3{
   height: 50%!important;
}
.line{
  background: #ccc;
  width: 2px;
  height: 100%;
  position: absolute;
  left: 60px;
  top: 0;
}
.line2{
  display:none;
}
.main{
  background: #f5f5f5;
}
.my-calendar {
  padding-top: 20px;
  background: #fff;

  .tip {
    width: 85vw;
    //   height: 40px;
    line-height: 40px;
    font-weight: 600;
    color: rgba(102, 102, 102, 1);
    padding: 10px;
    margin: 0px auto 10px;
    border-radius: 6px;
    box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.1);

    img {
      width: 26px;
      margin-left: 12px;
      vertical-align: middle;
      margin-right: 10px;
    }

  }

  .calendarTitle {
    width: 100%;
    padding: 10px 0px 15px;
    border-bottom: 1px solid #f5f5f5;
    //   height:40px;
    background: #fff;

    .inner {
      width: 60%;
      margin: auto auto;
      text-align: center;

      .showDate {
        margin: 0 20%;
        color: rgba(102, 102, 102, 1);
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
    background: #F5F5F5;
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

      .triangle-down { //倒立三角形
        width: 0;
        height: 0;
        margin-left: 4px;
        border-left: 3.5px solid transparent;
        border-right: 3.5px solid transparent;
        border-top: 6px solid #CDCDCD;
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
          // line-height: 35px;
          text-align: center;
          height: 45px;
          margin-bottom: 4px;
        }

        .dayBar {
          position: relative;
          z-index: 100;

          .item {
            margin: auto auto;
            border-radius: 50%;

            .itemInner {
              padding: 5px;
              width: 25px;
              height: 25px;
              margin: auto auto;
              overflow: hidden;
              border-radius: 50%;

            }

            .bigchange {
              color: #000 !important;
              //background: #66728B;
              box-shadow: 0 0 0 1px #66728C;
            }

            .smallchange {
              border: 1px solid #66728C;
              background: #66728C;
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
          background-size: 35px 35px;
          color: #fff;
          background: #444;
        }

        .smallchange {
          border: 1px solid #444
        }

        .noShow {
          color: rgba(102, 102, 102, .85);
        }

        .show-formal {
          width: 20px;
          height: 10px;
          display: inline-block;
        }

        .dolt {
          width: 8px;
          height: 8px;
          background: #ffbe8b;
          display: block;
          border-radius: 50%;
          margin: auto auto;
        }

        .doltT {
          width: 8px;
          height: 8px;
          background: #66728C;
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
        background: #66728C;
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
}

</style>
