<template>
  <section style="background: #F7F7F7; height: 100vh; position: relative">
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '随访'" :backUrl="backUrl" />

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
                    <span :class="{'show-formal':true,'isToday': today === day.date}">{{ getToday(day.date) ? '今' : new Date(day.date).getDate() }}</span>
                    <span v-if="day.status===2" :class="{'dolt':true}"></span>
                    <span v-if="day.status===1" :class="{'doltT':true}"></span>
                  </div>
                </div>
              </span>
          </section>
        </div>
      </div>
    </div>

    <div v-if="!loading && allData.length === 0" style="background:#f5f5f5" :style="{width: pageWidth + 'px'}">
      <div>
        <div class="nodata" style="padding-top:100px;">
          <img :src="noData" alt="">
          <p>您暂未添加{{ pageTitle ? pageTitle : '随访' }}</p>
          <p>请点击下方添加按钮进行添加</p>
        </div>
      </div>
    </div>
    <div v-else style="width: 100%;">
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad">
        <div v-for="(i, k) in allData" :key="k" @click="Goitem(i)">
          <div>
            <van-swipe-cell style="width: auto; margin: 15px 10px 0px 10px; background-color: #fff; border-radius: 12px;">
              <div>
                <div style="background: white; padding: 10px 0">
                  <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                    <div style="display: flex; align-items: center">
                      <div class="caring-form-div-icon"></div>
                      <span style="margin-left: 10px; font-weight: 500;font-size: 16px;color: #000000;line-height: 24px;text-align: left;font-style: normal;">
                        {{ '注射时间' + getTime(i.createTime, 'YYYY-MM-DD HH:mm:ss') }}
                      </span>
                    </div>
                  </div>
                  <div style="padding: 10px">
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
                            <div style="width: 10%; color: #999;  align-items: center; display: flex; justify-content: center;">
                              <van-icon name="arrow" />
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
        </div>
      </van-list>
    </div>


    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;">
      <van-col span="24" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
  </section>
</template>
<script>
  import Vue from 'vue'
  import { Group} from 'vux'
  import Api from '@/api/Content.js'
  import {queryPlanInfo} from '@/api/plan.js'
  import {findPatientInjectionCalendar, findPatientInjectionCalendarFormResult} from '@/api/formResult.js'
  import {getValue} from "@/components/utils/index"
  import {Constants} from '@/api/constants.js'
  import { Cell, Button, SwipeCell} from 'vant'
  import moment from "moment";
  Vue.use(Cell)
  Vue.use(Button)
  Vue.use(SwipeCell)

  export default {
    components: {
      Group,
      navBar: () => import('@/components/headers/navBar'),
    },
    data() {
      return {
        planId: this.$route.params.planId,
        pageTitle: '',
        noData: require('@/assets/my/medicineImg.png'),
        value: '',
        current: 1,
        dateType: 'month',  // 当注射模式时，数据列表展示某天的注射记录
        sendDate: this.initialDateSet(), // datetime日期
        weekList: ['一', '二', '三', '四', '五', '六', '日'],
        blankList: '', // 空白的格子数
        choseFlag: {}, // 用来调整今天选中的样式
        dayCalender: [], // 日历数据
        patientId: localStorage.getItem('userId'),
        today: '',
        allData: [],
        loading: false,
        finished: true,
        refreshing: false,
        allInjectionDate: {},
        subscribePLan: undefined,
        pageWidth: window.innerWidth,
        injectionModel: undefined,  // 注射模式需要展示日历和隐藏取关
        pageHeight: window.innerHeight - 46 - 33 - 10 - 20
      }
    },
    created() {
      this.today = moment().locale('zh-cn').format('YYYY-MM-DD')
      this.queryPlan(this.planId)
      console.log('this.$route.query.from ', this.$route.query.from )
      if (this.$route.query.from && this.$route.query.from === 'index') {
        this.backUrl = '/custom/follow/' +  this.planId
      } else {
        this.backUrl = '/home'
      }
    },
    watch: {
      sendDate: {
        handler(newValue, oldValue) {
          this.getTimeSheet(newValue) // 监听sendDate变化，即时变化日历
        },
        immediate: true, // 页面一进入就开始监听
        deep: false
      }
    },
    methods: {
      getToday(day) {
        if (day === moment().format('YYYY-MM-DD')) {
          return true
        }
        return false
      },
      // 查询随访计划的设置
      queryPlan(planId) {
        queryPlanInfo(planId).then(res => {
          const plan = res.data.data
          // 固定名称
          this.pageTitle = '注射日历'
          localStorage.setItem('pageTitle', this.pageTitle)
          this.injectionModel = true
          const date = moment().locale('zh-cn').format('YYYY-MM-DD')
          this.choseFlag.date = date
          this.medicationDay = date
          this.onRefresh()
          this.findPatientInjectionCalendar(date)
          // 注射模式
        })
      },
      /**
       * 查询注射的日历。
       */
      findPatientInjectionCalendar(data) {
        findPatientInjectionCalendar(this.patientId, this.planId, data).then(res => {
          this.allInjectionDate = res.data.data
          this.getTimeSheet(this.sendDate)
        })
      },
      // 每月第一天周几
      getBlank(year, month) {
        let blankNum = new Date(year, month, '01') // 下一个月一号是周几
        if (blankNum.getDay() === 0) {
          return 7
        }
        return blankNum.getDay()
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
      // 点击某天，显示样式
      clickDayDetail(val) {
        this.choseFlag = val // 获取当前日期
        this.medicationDay = val.date
        this.today = val.date
        console.log('============', this.today)
        this.dateType = 'day'
        this.onRefresh()
      },
      editorCalendar(i) {
        if (i) {
          this.monthNum = 1
          this.sendDate = this.getNextMonth(true)
          this.dateType = 'month'
          this.choseFlag = {}
          this.findPatientInjectionCalendar(this.sendDate + '-01')
          this.onRefresh()
        } else {
          this.monthNum = -1
          this.choseFlag = {}
          this.dateType = 'month'
          this.sendDate = this.getNextMonth(false)
          this.findPatientInjectionCalendar(this.sendDate + '-01')
          this.onRefresh()
        }
      },
      getNextMonth(k) {
        let arr = this.sendDate.split('-')
        let year = arr[0] //获取当前日期的年份
        let month = arr[1] //获取当前日期的月份
        let day = arr[2] //获取当前日期的日
        let days = new Date(year, month, 0)
        days = days.getDate() //获取当前日期中的月的天数
        let year2 = year
        let month2 = parseInt(month) + this.monthNum
        if (month2 == 13 && k) {
          year2 = parseInt(year2) + 1
          month2 = 1
        }
        if (month2 == 0 && !k) {
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
      deleteBtn(item, index) {
        Api.deleteFormResult(item.id)
          .then(res => {
            this.allData.splice(index, 1)
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

      getImage(values) {
        if (values) {
          if (values.length === 1) {
            values.push({attrValue: ""})
            values.push({attrValue: ""})
            values.push({attrValue: ""})
          } else if (values.length === 2) {
            values.push({attrValue: ""})
            values.push({attrValue: ""})
          } else if (values.length === 3) {
            values.push({attrValue: ""})
          }
        }
        return values
      },
      getFormValue(item) {
        return getValue(item)
      },
      getTime(time, format) {
        return moment(time).format(format)
      },
      getArray(json) {
        let list = []
        let jsonList = JSON.parse(json)
        for (let i = 0; i < jsonList.length; i++) {
          if (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload
            || jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload
            || jsonList[i].widgetType === Constants.CustomFormWidgetType.SplitLine
            || jsonList[i].widgetType === Constants.CustomFormWidgetType.Page
            || jsonList[i].widgetType === Constants.CustomFormWidgetType.Avatar
            || jsonList[i].widgetType === Constants.CustomFormWidgetType.Desc) {
            continue
          } else {
            list.push(jsonList[i])
          }
          if (list.length === 3) {
            break
          }
        }
        console.log('getArray', list)
        return list
      },
      onRefresh() {
        // 清空列表数据
        // 重新加载数据
        // 将 loading 设置为 true，表示处于加载状态
        this.finished = false;
        this.loading = true;
        this.list = [];
        this.current = 1;
        this.refreshing = false;
        this.onLoad();
      },
      onLoad() {
        this.getInfo()
      },
      getInfo() {
        let data = this.medicationDay
        if (this.dateType === 'month') {
          data = this.sendDate + '-01'
        }
        findPatientInjectionCalendarFormResult(this.patientId, this.planId, this.dateType, data).then(res => {
          this.allData = res.data.data
          this.loading = false;
          this.finished = true;
        })
      },
      add() {
        this.$router.push({
          path: `/custom/follow/${this.planId}/editor`,
          query: {
            pageTitle: this.pageTitle,
            from: 'calendar'
          }
        })
      },
      Goitem(k) {
        this.$router.push({
          path: `/custom/follow/detail/result`,
          query: {
            formId: k.id,
            planId: this.planId,
            pageTitle: this.pageTitle,
            from: 'calendar'
          }
        })
      }
    }
  }

</script>


<style lang="less" scoped>

  .allContent {
    width: 100vw;
    background-color: #fafafa;
  }

  .nodata {
    width: 100vw;
    text-align: center;
    font-size: 14px;
    color: rgba(102, 102, 102, 0.85);
    background: #f5f5f5 !important;
  }

  .my-calendar {
    padding-top: 20px;
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

