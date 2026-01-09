<template>
  <section>
    <!--日历-->
    <div class="my-calendar">
      <div class="calendarTitle">
        <div class="inner">
          <p class="arrow_left" @click="editorCalendar(false)"></p>
          <span class="showDate">{{ sendDate }}</span>
          <p class="arrow_right" @click="editorCalendar(true)"></p>
        </div>

      </div>
      <div style="padding: 0px 15px">
        <div style="border-bottom: 1px solid #f5f5f5;"></div>
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

            <span class="dayBar" v-for="(day, index ) in dayCalender" :key="'dayCalender' + index"
                  @click="clickDayDetail(day)">
                <div :class="{'item':true}">
                  <div
                      :class="{itemInner:true, 'smallchange': new Date().toDateString()==new Date(day.date).toDateString(), 'bigchange': choseFlag === day}">
                    <span :class="{'show-formal':true}" :style="{color: day.disabled ?
                    new Date().toDateString()==new Date(day.date).toDateString() ? '#FFF' : '#333' : '#B8B8B8'}">{{
                        new Date(day.date).getDate()
                      }}</span>
                    <!--有随访任务点-->
                    <span v-if="day.status === 1" :class="{'doltT':true}" :style="{background: new Date().toDateString()==new Date(day.date).toDateString() ? '#FFF' : '#3F86FF' }"></span>
                  </div>
                </div>
              </span>
          </section>
        </div>
        <div class="endDesc">
          <span class="dolt"></span>
          <span>选中</span>
          <span class="doltT"></span>
          <span>随访任务</span>
        </div>
      </div>
    </div>

    <!--列表头-->
    <div v-if="planList && planList.length > 0">
      <div>
        <div class="listTitle">
          <div class="title_header">
            <div class="title_icon"></div>
            <div class="title_txt">随访任务</div>
          </div>
          <div class="title_time">{{ currentDay }}</div>
        </div>
        <div style="padding: 0px 15px 15px 15px; background: white; ">
          <div style="border-bottom: 1px solid #f5f5f5;"></div>
        </div>
      </div>
      <!--列表-->
      <div v-for="(item, index) in planList" :key="index" style="background: white; padding: 0px 15px 15px 15px;">
        <!--已执行完成-->
        <div style="padding: 15px; background: #f5f5f5; border-radius: 8px;" v-if="item.planFinishStatus == 1">
          <!--已执行 ---- 学习计划-->
          <div v-if="item.planType == 6">
            <div style="color:#333333;font-size: 16px">{{ item.firstShowTitle }}</div>
            <div v-for="(cItem, key) in item.planDetailTimeDTOS" :key="key"
                 @click="goPage(item, key)"
                 style="color: #666666; font-size: 13px; display: flex;align-items: center;justify-content: space-between">
              <div style="max-width: 70%">
                {{ cItem.secondShowTitle }}
              </div>
              <div style="font-size: 14px;" :style="{color:cItem.cmsReadStatus === 1 ? '#999999' : '#FF5555'}">
                {{ cItem.cmsReadStatus === 1 ? '已读' : '未读' }}
                <van-icon color="#C6C6C6" name="arrow"/>
              </div>
            </div>
          </div>

          <!--已执行 ---- 其他计划-->
          <div v-for="(cItem, key) in item.planDetailTimeDTOS" :key="key" v-else>
            <div @click="goPage(item, key)" style="display: flex; justify-content: space-between; align-items: center">
              <div style="color: #333333; font-size: 16px;">{{ item.firstShowTitle }}</div>
              <div style="font-size: 14px;" :style="{color:cItem.cmsReadStatus === 1 ? '#999999' : '#FF5555'}">
                {{ cItem.cmsReadStatus === 1 ? '已读' : '未读' }}
                <van-icon color="#C6C6C6" name="arrow"/>
              </div>
            </div>
          </div>
        </div>
        <!--未执行-->
        <div style="padding: 15px; background: #F7FAFF; border-radius: 8px; border: 1px solid #D9E7FF;position: relative" v-else>
          <!--未执行 ---- 学习计划-->
          <div v-if="item.planType == 6">
            <div style="color:#333333;font-size: 16px">{{ item.firstShowTitle }}</div>
            <div @click="goPage(item, key)" v-for="(cItem, key) in item.planDetailTimeDTOS" :key="key"
                 style="color: #666666; font-size: 13px; display: flex;align-items: center;justify-content: space-between">
              <div style="max-width: 70%;line-height: 2">
                {{ cItem.secondShowTitle }}
              </div>
              <div style="display: flex;align-items: center;position: absolute;right: 15px;top: 50%;transform: translateY(-50%)">
                <div style="color: #3F86FF; font-size: 13px;margin-right: 7px">{{ getDate(cItem.planExecutionDate) }}
                </div>
                <div>
                  <van-icon color="#C6C6C6" name="arrow"/>
                </div>
              </div>
            </div>
          </div>

          <!--未执行 ---- 其他计划-->
          <div v-for="(cItem, key) in item.planDetailTimeDTOS" :key="key" v-else>
            <div @click="goPage(item, key)" style="display: flex; justify-content: space-between; align-items: center">
              <div style="color: #333333; font-size: 16px;">{{ item.firstShowTitle }}</div>
              <div style="color: #3F86FF; font-size: 13px">{{ getDate(cItem.planExecutionDate) }}
                <van-icon color="#C6C6C6" name="arrow"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else style="display: flex; background: white; justify-content: center;height: 35vh; margin-top: 10px;">
      <van-empty :image="require('@/assets/my/no_data.png')" description="暂无数据"/>
    </div>
    <loading-dialog ref="loading"></loading-dialog>
  </section>
</template>

<script>

import Api from '@/api/followUp.js'
import loadingDialog from "./loadingDialog";

export default {
  name: "calendar",
  components: {
    loadingDialog
  },
  props: {
    planId: {
      type: String,
      default: ''
    },
    planType: {
      type: Number,
      default: 0
    }
  },
  watch: {
    planId: {
      handler: function (val, old) {
        // this.$refs.loading.openLoading()
        this.patientFollowPlanCalendar(val)
        this.patientCalendarDayPlanDetail(moment().format('yyyy-MM-DD'), val)
      }
    },
  },
  data() {
    return {
      sendDate: this.initialDateSet(), // datetime日期
      monthNum: 0,
      blankList: '', // 空白的格子数
      dayCalender: [], // 日历数据
      weekList: ['一', '二', '三', '四', '五', '六', '日'],
      choseFlag: '', // 用来调整今天选中的样式
      allData: [],
      currentDay: moment().format('yyyy年MM月DD日'),
      planList: [],
      startTime: moment(JSON.parse(localStorage.getItem('myallInfo')).completeEnterGroupTime).format('yyyy-MM-DD'),
      endTime: moment().add(1, 'Y').format("yyyy-MM-DD"),
    }
  },
  mounted() {
    this.patientFollowPlanCalendar(this.planId)
    this.patientCalendarDayPlanDetail(moment().format('yyyy-MM-DD'), this.planId)
  },
  methods: {
    goPage(data, index) {
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
        this.$router.push({path: '/cms/show', query: {id: data.planDetailTimeDTOS[index].cmsId, isComment: true}})
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
    //学习计划解析时间
    getDate(time) {
      return moment(time, 'HH:mm:ss').format('HH:mm')
    },
    //查询 日历 某天的随访任务
    patientCalendarDayPlanDetail(date, planId) {
      //learnPlan 是否为学习计划, 学习计划传1 否则不穿
      let learnPlan = 0
      if (this.planType == 6) {
        learnPlan = 1
      }
      Api.patientCalendarDayPlanDetail(date, planId, learnPlan)
          .then(res => {
            this.planList = res.data.data.planDetails
            this.$refs.loading.cancelLoading()
          })
    },
    // 查询 随访的 日历
    patientFollowPlanCalendar(planId) {
      this.$refs.loading.openLoading()
      //learnPlan 是否为学习计划, 学习计划传1 否则不穿
      let learnPlan = 0
      if (this.planType == 6) {
        learnPlan = 1
      }
      Api.patientFollowPlanCalendar(this.sendDate + "-01", planId, learnPlan)
          .then(res => {
            this.allData = res.data.data
            this.getTimeSheet(this.sendDate)
          })
    },
    // 点击某天，显示样式, 获取点击当天数据
    clickDayDetail(val) {
      if (!val.disabled) {
        return
      }
      this.choseFlag = val // 获取当前日期
      this.currentDay = moment(val.date).format('yyyy年MM月DD日')
      this.$refs.loading.openLoading()
      this.patientCalendarDayPlanDetail(moment(val.date).format('yyyy-MM-DD'), this.planId)
    },
    // 切换日期
    editorCalendar(i) {
      if (i) {
        this.monthNum = 1
        if (moment(moment(this.endTime).format('yyyy-MM')).diff(this.sendDate, 'months') <= 0) {
          this.$vux.toast.text('不能查看超过一年之后的日期', 'center')
        } else {
          this.sendDate = this.getNextMonth(true)
          this.patientFollowPlanCalendar(this.planId)
        }
      } else {
        this.monthNum = -1
        if (moment(moment(this.startTime).format('yyyy-MM')).diff(this.sendDate, 'months') >= 0) {
          this.$vux.toast.text('不能查看入组之前的日期', 'center')
        } else {
          this.sendDate = this.getNextMonth(false)
          this.patientFollowPlanCalendar(this.planId)
        }
      }

    },
    // 获取当前年份和月份
    initialDateSet() {
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
    getTimeSheet(date) {

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
      for (let k = 1; k <= dayLength; k++) {
        everyDate = year + '-' + month + '-' + k
        if (that.allData && that.allData.length > 0) {
          let myIndex = that.allData.findIndex(item => {
            return that.changeFormat2(everyDate) === item
          })

          if (myIndex !== -1) {
            that.dayCalender.push({
              date: that.changeFormat2(everyDate),
              condition: '',
              status: 1,
              disabled: true,
            })
          } else {
            that.dayCalender.push({
              date: that.changeFormat2(everyDate),
              condition: '',
              status: 0,
              disabled: true,
            })
          }
        } else {
          that.dayCalender.push({
            date: that.changeFormat2(everyDate),
            condition: '',
            status: 0,
            disabled: true,
          })
        }
      }
      //判断当前是否为开始月或者结束月
      if (moment(moment(this.startTime).format('yyyy-MM')).diff(this.sendDate, 'months') == 0) {
        //开始月
        that.dayCalender.forEach(item => {
          if (moment(this.startTime).diff(item.date, 'day') > 0) {
            item.disabled = false
          }
        })
      } else if (moment(moment(this.endTime).format('yyyy-MM')).diff(this.sendDate, 'months') == 0) {
        //结束月
        that.dayCalender.forEach(item => {
          if (moment(this.endTime).diff(item.date, 'day') < 0) {
            item.disabled = false
          }
        })
      }
      this.$refs.loading.cancelLoading()
    },

    // 计算上一月
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
  }
}
</script>

<style lang="less" scoped>

.my-calendar {
  padding-top: 20px;
  background: #fff;

  .calendarTitle {
    width: 100%;
    padding: 0px 0px 15px;
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
    padding-top: 15px;
    padding-bottom: 15px;
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
            }

            .bigchange {
              color: #000 !important;
              //background: #66728B;
              box-shadow: 0 0 0 1px #66728c;
            }

            .smallchange {
              border: 1px solid #66728c;
              background: #66728c;
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

        .show-formal {
          width: 20px;
          height: 10px;
          display: inline-block;
        }

        .dolt {
          width: 8px;
          height: 8px;
          background: #66728B;
          display: block;
          border-radius: 50%;
          margin: auto auto;
        }

        .doltT {
          width: 6px;
          height: 6px;
          background: #3F86FF;
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
        background: #66728c;
        border-radius: 50%;
        margin-right: 5px;
      }

      .doltT {
        display: inline-block;
        width: 8px;
        height: 8px;
        background: #3F86FF;
        border-radius: 50%;
        margin-right: 5px;
      }
    }
  }

  .calendar-medication {
    background: #f5f5f5;
  }
}

.listTitle {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  padding: 12px 15px;
  background: white;

  .title_header {
    display: flex;
    align-items: center;
  }

  .title_icon {
    width: 4px;
    height: 14px;
    background: #66728B;
    border-radius: 8px 8px 8px 8px;
  }

  .title_txt {
    font-size: 16px;
    font-weight: 500;
    color: #333333;
    margin-left: 10px
  }

  .title_time {
    font-size: 12px;
    color: #666666;
    display: flex;
    align-items: center
  }
}

</style>
