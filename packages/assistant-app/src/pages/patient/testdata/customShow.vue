<template>
  <section ref="mianscroll" class="allContent">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '监测数据'" @onBack="() => $router.go(-1)"></headNavigation>
    </van-sticky>
    <div class="choosetime" v-if="EchartsData.showTrend">
      <span style="font-size: 16px; font-weight: 500; color: #3D444D; height: 22px;">监测图表</span>
      <img class="quanping" style="padding-right: 15px" :src="require('@/assets/my/test_data_enlarge.png')"
           @click="HorizontalScreen"/>
    </div>

    <div v-show="(list.length > 0 || monitorQueryDate !== 'All') && EchartsData.showTrend==true"
         style="background:#fff;padding-bottom:12px">
      <div id="myChart" :style="{width: windowWidth + 'px', height: '230px'}"></div>
      <div>
        <div class="ckqj" v-for="(item,index) in EchartsData.ydataList" :key="index"
             style="display: flex;justify-content: space-between;padding-right: 24px">
          <div style="display: flex">
            <div v-if="item.hasTrendReference!==false" class="ck">
              {{ item.trendReference == 'range' ? '参考区间:' : '参考值:' }}
            </div>
            <div style="color:#3f86ff;margin-left:10px;min-width:60px" class="cknum"
                 v-if="item.trendReference=='exact_value'&&item.trendReferenceExactValue!==null&&item.hasTrendReference!==false">
              {{ item.trendReferenceExactValue }}
            </div>
            <div style="color:#3f86ff;margin-left:10px"
                 v-if="item.trendReference=='range'&&item.hasTrendReference!==false">
              {{ item.trendReferenceMin }}-{{ item.trendReferenceMax }}
            </div>
          </div>
          <div style="display: flex" v-if="item.showReferenceTargetValue===true">
            <div class="jzz">基线值: <span
              style="color: #5B97FF;">{{ item.referenceValue }}</span></div>
            <div class="mbz">目标值: <span
              style="color: #5B97FF;">{{ item.targetValue }}</span></div>
            <div class="updata">
              <img id="updata" src="@/assets/updata.png" @click="editReference(item)" alt="">
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="list.length > 0 || monitorQueryDate !== 'All'"
      style="padding-top: 12px; display: flex; align-items: center; padding-left: 15px; padding-right: 15px; justify-content: space-between">
      <div style="font-size: 16px;font-weight: 500;color: #3D444D;">
        监测记录
      </div>
      <select class="caring-select" style="width: 120px;" v-model="monitorQueryDate" @change="monitorQueryDateChange">
        <option v-for="(item, index) in monitorQueryDateList" :key="index" :value="item.code">{{ item.name }}</option>
      </select>
    </div>

    <div v-if="list.length === 0 && !loading">
      <div class="nodata">
        <img :src="require('@/assets/my/noData.png')" alt="" style="width: 80%">
        <div>暂无数据</div>
      </div>
    </div>

    <div v-if="list.length > 0">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" :class="EchartsData.showTrend==false?'jiange':''"
                        style="background-color: #f5f5f5; padding-bottom: 100px;">
        <van-list :finished="finished" :finished-text=finishedText() @load="onLoad">
          <div v-for="(i, k) in list" :key="k" @click="addData(i)">
            <div v-if="i.scoreQuestionnaire === 1"
                 style="width: auto; margin: 15px 10px; background-color: #fff; border-radius: 12px;">
              <div style="display: flex; font-size: 14px">
                <div style="color:#323233; padding: 17px 16px; width: calc(100% - 122px)">
                  <div>
                    <div slot="title" style="display: flex; align-items: center">
                      <div
                        style="background: #08BDC0; width: 3px;border-radius: 4px; height: 16px; margin-right: 6px;"/>
                      <span
                        style="color: #333333; font-size: 16px; font-weight: bold; margin-right: 10px; width: 70px;"> 监测时间 </span>
                      <span style="width: 130px;">{{ getTime(i.createTime, 'YYYY-MM-DD HH:mm') }}</span>
                    </div>
                  </div>
                  <div style="font-size: 12px; color: #666; margin-top: 8px">
                    {{ getEstimateTime(i) }}
                  </div>
                </div>
                <div
                  style="width: 90px;color: #68E1A8;justify-content: right; align-items: center;display: flex; padding: 0 16px">
                    <span v-if="i.dataFeedBack"
                          :style="{background: getDataFeedBack(i.dataFeedBack) !== 1 ? '#FF5A5A' : '#66E0A7'}"
                          style="font-size: 12px; background: #66E0A7; color: #fff; padding: 5px 15px; border-radius: 20px;">
                      {{ getFeedName(i.dataFeedBack) }}
                    </span>
                  <span v-else style="color: #68E1A8">查看详情</span>
                </div>
              </div>
              <van-cell title="总得分" :border="false" v-if="i.showFormResultSumScore === 1"
                        style="border-radius: 12px">
                <div style="color: #333">
                  {{ i.formResultSumScore }}
                </div>
              </van-cell>
              <van-cell title="平均分" v-if="i.showFormResultAverageScore === 1"
                        style="border-radius: 12px;"
                        :style="{marginTop: i.showFormResultSumScore === 1 ? '-5px' : '0px'}">
                <div style="color: #333">
                  {{ i.formResultAverageScore }}
                </div>
              </van-cell>
            </div>
            <div v-else style="background: white; marginTop: 15px">

              <div style="display: flex; align-items: center; padding: 10px 15px; font-size: 16px; color: #333333">
                <div style="display: flex; align-items: center">
                  <div class="caring-form-div-icon"></div>
                  <span style="margin-left: 11px; color: #333333; font-size: 14px; font-weight: 500; ">监测时间</span>
                  <span style="margin-left: 11px; font-size: 14px; font-weight: 400; color: #666">
                    {{  getTime(i.createTime, 'YYYY-MM-DD HH:mm') }}
                </span>
                </div>
                <div style="position: absolute; right: 20px" v-if="i.dataFeedBack">
                  <div class="caring-form-result-status"
                       :style="{background: getDataFeedBack(i.dataFeedBack, 'status') == '1' ? '#66E0A7' : '#FF5A5A'}">
                    {{ getFeedName(i.dataFeedBack) }}
                  </div>
                </div>
              </div>

              <div style="padding: 0px 15px 15px 15px">
                <div style="border-radius: 10px; padding-left: 15px; font-size: 14px;">
                  <div v-for="(item, index) in getArray(i.jsonContent)" :key="index" style="margin-bottom: 8px;">
                    <div v-if="i.isOpen ? true : index < 6">
                      <div style="display: flex;" v-if="getFormValue(item).type === 'text'">
                        <div style="color: #333333; min-width: 80px;">{{ item.label }}：</div>
                        <div style="color: #333333; font-weight: bold; flex: 1; text-align: right; margin-left: 5px;
                    text-overflow:ellipsis;overflow:hidden;white-space:nowrap;">
                          {{ getFormValue(item).value }} {{ getFormValue(item).unit }}
                        </div>
                      </div>
                      <div v-if="getFormValue(item).type !== 'text'&&item.widgetType!=='Desc'" style="display: flex;">
                        <div style="display: flex">
                          <div style="color: #999999; min-width: 80px;">{{ item.label }}：</div>
                          <div style="margin-top: 5px;">
                            <div v-for="(i, k) in getImage(item.values)" :key="k" v-if="k < 3">
                              <div class="flex-demo" v-if="i.attrValue">
                                <van-image v-if="k < 3" width="4rem" height="4rem" style="margin-right: 10px;"
                                           :src="i.attrValue"/>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div style="display: flex; align-items: center; justify-content: center; color: #929292"
                       @click.stop="openContent(k)">
                    <span v-if="!i.isOpen">展开</span>
                    <span v-else>收缩</span>
                    <van-icon v-if="i.isOpen" name="arrow-up"/>
                    <van-icon v-else name="arrow-down"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

    <div style="display: flex; justify-content: center; position: fixed; bottom: 20px; width: 100%;">
      <van-button type="primary" style="width: 90%; background: #67E0A7; border: none" round @click="addData()">
        添加记录
      </van-button>
    </div>

    <!-- 自定义弹出层 -->
    <van-overlay :show="show" @click="show = false">
      <div class="wrapper" @click.stop>
        <div class="block">
          <div class="title">
            <div style="text-align: center;margin: 0 auto">请选择日期</div>
            <img style="position: absolute" @click="closeTime" src="@/assets/close.png" alt="">
          </div>
          <div class="timeBox" :style="{visibility:this.params.monitorDayType==='customize'?'visible':'hidden',
          'margin-bottom':this.params.monitorDayType==='customize'?'25px':'0px'}">
            <div :class="startOrEnd=='start'?'startTime timeBlue':'startTime'">{{ startTimes }}</div>
            <div>至</div>
            <div :class="startOrEnd=='end'?'endTime timeBlue':'endTime'" class="endTime">{{ endTimes }}</div>
          </div>

          <!-- 时间选择器 -->
          <div class="timeChoose" v-if="startOrEnd=='start'">
            <van-datetime-picker visible-item-count="4" ref="value" v-model="currentDate" type="date"
                                 :show-toolbar=false :min-date="minDate" :max-date="maxDate"/>
          </div>
          <div class="timeChoose" v-else>
            <van-datetime-picker visible-item-count="4" ref="endtime" v-model="currentDate2" type="date"
                                 :show-toolbar=false :min-date="minDate2" :max-date="maxDate"/>
          </div>

          <!-- 确认按钮 -->
          <div class="TJ-btn" style="margin-top:33px">
            <div @click="updata">确定</div>
          </div>
        </div>
      </div>
    </van-overlay>
  </section>
</template>

<script>
import { monitorLineChart } from '@/api/plan.js'
import { getMonitorList } from '@/api/formApi.js'
import {getValue} from '@/components/utils/index'
import Vue from 'vue'
import {DatetimePicker, Toast, Overlay, PullRefresh, List, Image as VanImage, Cell, Popover, Button} from 'vant'
import moment from 'moment'
import * as echarts from 'echarts'

Vue.use(DatetimePicker)
Vue.use(PullRefresh)
Vue.use(Toast)
Vue.use(Overlay)
Vue.use(List)
Vue.use(VanImage)
Vue.use(Cell)
Vue.use(Button)
Vue.use(Popover)
export default {
  name: 'show',
  data () {
    return {
      windowWidth: window.innerWidth,
      timeNumber: 2,
      pageTitle: '监测数据查看',
      list: [],
      loading: false,
      finished: false,
      refreshing: false,
      showTrend: true,
      current: 1,
      type: 'WEEK', // monitorDateLineType,可用值:DAY,WEEK,MONTH
      option: {
        xAxis: {
          type: 'category',
          data: []
        },
        tooltip: {
          trigger: 'axis'
        },
        yAxis: {
          type: 'value'
        },
        series: [],
        // 设置 刷数据 的配置
        brush: {
          toolbox: [''],
          seriesIndex: 'all',
          xAxisIndex: 'all',
          yAxisIndex: 'all',
          brushLink: 'all',
          transformable: false,
          brushStyle: {
            borderWidth: 1,
            color: 'rgba(51, 126, 255, 0.15)',
            borderColor: 'rgba(51, 126, 255, 1)'
          },
          removeOnClick: false,
          inBrush: {},
          outOfBrush: {}
        }
      },
      xList: [],
      yList: [],
      unit: '',
      trendReferenceMax: 0,
      trendReferenceMin: 0,
      params: {
        customizeEndDate: '',
        customizeStartDate: '',
        monitorDayType: 'Day30',
        defaultQuery: 1
      },
      EchartsData: [],
      Xdata: [],
      Ydata: [],
      stratTime: '',
      lastTime: '',
      options: {
        grid: {
          left: '3%',
          right: '10%',
          bottom: '10%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {
            rotate: 0,
            interval: 10,
            // 加入axisLabel字段,interval后面加你想要间隔的个数
            textStyle: {
              color: '#999999'
            },
            showMaxLabel: true
          },
          boundaryGap: true,
          axisTick: {
            show: false
          },
          axisLine: {
            show: false, // 是否显示轴线
            lineStyle: {
              color: '#ccc' // 刻度线的颜色
            }
          }
        },
        tooltip: {},
        yAxis: {
          splitNumber: 6,
          scale: true,
          type: 'value',
          axisLabel: {
            // y轴文字的配置
            textStyle: {
              color: '#999999' // Y轴内容文字颜色
            }
          },
          axisLine: {
            show: false, // 是否显示轴线
            lineStyle: {
              color: '#EBEBEB' // 刻度线的颜色
            }
          }
        },
        series: [
          {
            name: '',
            data: [],
            type: 'line',
            connectNulls: true,
            showAllSymbol: true,
            symbolSize: 8,
            lineStyle: {
              color: '#337EFF',
              width: 2
            }
          }
        ]
      },
      yMin: '',
      ymax: '',
      show: false,
      startOrEnd: 'start',
      startTimes: '', // 自定义开始时间
      endTimes: '', // 自定义默认时间
      minDate: undefined,
      maxDate: new Date(),
      currentDate: new Date(),
      currentDate2: new Date(),
      minDate2: undefined,
      today: '',
      hasOneDay: false,
      myChart: null,
      showPopover: false,
      monitorQueryDate: 'CURRENT_DAY',
      timeData: [
        {
          name: '全部',
          monitorDayType: 'All'
        },
        {
          name: '近7天',
          monitorDayType: 'Day7'
        },
        {
          name: '近30天',
          monitorDayType: 'Day30'
        },
        {
          name: '近90天',
          monitorDayType: 'Day90'
        },
        {
          name: '自定义',
          monitorDayType: 'customize'
        }
      ],
      monitorQueryDateList: [
        {code: 'ALL', name: '全部记录'},
        {code: 'day7', name: '近7天的记录'},
        {code: 'day30', name: '近30天的记录'},
        {code: 'moon3', name: '近3个月的记录'}
      ],
      isFirst: true, // 首次加载
      baceImg:
        'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAAehJREFUaEPtWTtOAzEQHZuPOQA9BUgUFCDgBhyAhpNAm8QbZ5Ub5BY03IQgUVAgQZlUoctGkQd582ETZcnEixcveKU0kT2e955nPGMzBNiCCn/s3wAYNZs3yHnTiMW0lrtS3vsgHFmBpNV6BsaOU6cRX0S9flIpAKM4HqPW5fuMOOBJcrXTbndXLU5XQKlx+d5PV0QcCCn3qwzgSUh5XlkAqLXaiyJVCQCICIyxrK8fotc7hE5n4D0A47YBABkA37GfpnTqQZaUEMSbsu8VABv2vQJgw75XAGDDvT8LaG9iAE1AZtLMuuD1D8By+jSnb79/lJc+vQOQBvFSoqeo4M0WmhS5S4cYQQWvANio4BUAGxW8A7Cq3qlUMbey5/iJfmCo1HihRiyzu0Es3g/8VkuJAO9c69vdKHooVk7H8UJLKWq17TJFyFuLHsQBgBu9ggJueKVbDQrQuXIzMijghle61aAAnSs3I4MCbnilWw0K0LlyMzIo4IZXutW/q8Aoiq6RsTowdpbywfkiLbMXS8Q3hniX1/LRubQbmavAUKlXBnAwN5sHYHKZ8yikvLRzodgsOgDz7DN7+jFX4eb39XVFo3FRzBW72bkA0i3EeQMATuemswCmf667NbBziz6LHMR0k+WOrDyAT4hSeqAB3EL0AAAAAElFTkSuQmCC'
    }
  },
  created () {
    this.getToday()
    if (this.$route.query.title) {
      this.pageTitle = this.$route.query.title
    }
    if (this.$route.query.params) {
      this.params = this.$route.query.params
      this.chooseTime(this.$route.query.params.monitorDayType, this.$route.query.timeNumber)
    }
    this.current = 1
    // 创建一个日期对象
    if (localStorage.getItem('patientBaseInfo')) {
      let time = JSON.parse(localStorage.getItem('patientBaseInfo')).completeEnterGroupTime
      if (time) {
        // 获取年份
        const year = moment(time).get('year')
        // 获取月份
        const month = moment(time).get('month') // 返回 2
        // 获取日期
        const day = moment(time).format('DD') // 返回 22
        this.minDate = new Date(year, month, day)
      }
    }

    this.type = 'DAY'
  },
  mounted () {
    Toast.loading({
      message: '加载中...',
      forbidClick: true
    })
    this.getchartdata()
    window.scrollTo(0, 0)
  },
  methods: {
    /**
     * 是否显示填写时间
     * @param item
     * @returns {string}
     */
    getEstimateTime (item) {
      const jsonContent = item.jsonContent
      const jsonArray = JSON.parse(jsonContent)
      let field
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
    // 是否展开
    openContent (index) {
      this.list[index].isOpen = !this.list[index].isOpen
    },
    // 判断背景
    getDataFeedBack (dataFeedBack) {
      if (dataFeedBack) {
        return JSON.parse(dataFeedBack).normalAnomaly
      }
      return 1
    },
    getFeedName (dataFeedBack) {
      return JSON.parse(dataFeedBack).normalAnomalyText
    },
    monitorQueryDateChange () {
      this.refreshing = true
      let index = -1
      if (this.monitorQueryDate === 'ALL' || this.monitorQueryDate === 'day7' ||
        this.monitorQueryDate === 'day30' || this.monitorQueryDate === 'moon3') {
        // 全部
        if (this.monitorQueryDate === 'ALL') {
          index = this.timeData.findIndex(item => item.monitorDayType === 'All')
        } else if (this.monitorQueryDate === 'day7') {
          index = this.timeData.findIndex(item => item.monitorDayType === 'Day7')
        } else if (this.monitorQueryDate === 'day30') {
          index = this.timeData.findIndex(item => item.monitorDayType === 'Day30')
        } else if (this.monitorQueryDate === 'moon3') {
          index = this.timeData.findIndex(item => item.monitorDayType === 'Day90')
        }

        if (index > -1) {
          this.chooseTime(this.timeData[index], index)
        }
        this.onRefresh()
      } else if (this.monitorQueryDate === 'CURRENT_DAY') {
        // 当日记录
        this.params.monitorDayType = 'OneDay'
        this.show = true
      } else if (this.monitorQueryDate === 'CUSTOM_DATE') {
        // 当日记录
        this.params.monitorDayType = 'customize'
        this.show = true
      }
    },
    // 判断是ios 还是安卓
    getIos () {
      var u = navigator.userAgent
      var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)
      console.log(isiOS, '是苹果还是安卓')
      if (isiOS) {
        return true
      }
      return false
    },
    // 修改基线值
    editReference (item) {
      this.$router.push({
        path: '/patient/monitor/editReference',
        query: {
          businessId: this.$route.query.planId,
          referenceValue: item.referenceValue,
          targetValue: item.targetValue,
          fieldId: item.fieldId
        }
      })
    },
    getToday () {
      let time = new Date() // 创建一个Date对象
      let year = time.getFullYear() // 获取年份
      let month = time.getMonth() + 1 // 获取月份，注意要加1
      let day = time.getDate() // 获取日期
      this.today = `${year}-${month >= 10 ? month : '0' + month}-${day >= 10 ? day : '0' + day}`
      console.log(this.today)
    },
    // 点击自定义的x
    closeTime () {
      this.show = false
      this.startOrEnd = 'start'
    },

    // 自定义时间确定按钮
    updata () {
      console.log(this.$refs.value)
      if (this.startOrEnd === 'start') {
        const value = this.$refs.value.getPicker().getValues()
        if (this.params.monitorDayType !== 'customize') {
          this.params.customizeStartDate = ''
          this.params.customizeEndDate = ''
          this.params.oneDayDate = value[0] + '-' + value[1] + '-' + value[2]
          this.today = this.params.oneDayDate
          this.params.monitorDayType = 'OneDay'
          this.Xdata = []
          this.onRefresh()
          this.show = false
        } else {
          this.startTimes = value[0] + '-' + value[1] + '-' + value[2]
          this.stratTime = this.startTimes
          this.params.customizeStartDate = this.stratTime
          if (this.getIos()) {
            this.minDate2 = new Date(moment(value[0] + '/' + value[1] + '/' + value[2]).format('YYYY/MM/DD'))
            // this.minDate2 = new Date(value[0] + '/' + value[1] + '/' + value[2])
            console.log(this.minDate2)
          } else {
            this.minDate2 = new Date(value[0] + ',' + value[1] + ',' + value[2])
          }
          this.startOrEnd = 'end'
        }
      } else if (this.startOrEnd === 'end' && this.params.monitorDayType === 'customize') {
        const value = this.$refs.endtime.getPicker().getValues()
        this.lastTime = value[0] + '-' + value[1] + '-' + value[2]
        this.endTimes = this.lastTime
        this.params.customizeEndDate = this.endTimes
        if (this.params.oneDayDate) {
          delete this.params.oneDayDate
        }
        this.show = false
        this.getchartdata()
        this.onRefresh()
        this.Xdata = []
        this.startOrEnd = 'start'
      }
    },
    // // 获取当前日期
    getCurTime () {
      var date = new Date()
      let time = `${date.getFullYear()}${date.getMonth() + 1 >= 10 ? '-' : '-0'}${date.getMonth() + 1}${
        date.getDate() >= 10 ? '-' : '-0'
      }${date.getDate()} `
      this.startTimes = time
    },
    getchartdata () {
      let patientId = localStorage.getItem('patientId')
      let that = this
      this.options.series[0].data = []
      monitorLineChart(this.params, this.$route.query.planId, patientId).then(res => {
        if (res.code === 0) {
          this.EchartsData = res.data
          // 如果有单日曲线并且是首次加载
          if (this.EchartsData.oneDayCurve === 1 && this.params.defaultQuery === 1) {
            this.hasOneDay = true
            this.params.monitorDayType = 'OneDay'
            this.lastTime = this.today
            this.stratTime = this.today
            const arr = {
              name: '单日',
              monitorDayType: 'OneDay'
            }
            const temp = {
              code: 'CURRENT_DAY', name: '单日记录'
            }
            this.monitorQueryDateList.splice(1, 0, temp)
            this.timeData.splice(1, 0, arr)
            this.timeNumber = 1
          }
          // 首次进入页面 并且 oneDayCurve 标识为空， 则列表数据加载最近30天
          if (this.isFirst) {
            if (this.EchartsData.oneDayCurve !== 1) {
              that.monitorQueryDate = 'day30'
            }
            this.isFirst = false
            that.getData()
          }

          if (this.EchartsData.showTrend === true) {
            let result = this.monitorQueryDateList.find(item => item.code === 'CUSTOM_DATE')
            if (!result) {
              // 是否包含自定义时间
              this.monitorQueryDateList.push({
                code: 'CUSTOM_DATE', name: '自定义时间'
              })
            }

            if (
              this.EchartsData.ydataList !== null &&
              this.EchartsData.ydataList.length > 0 &&
              this.EchartsData.ydataList[0].ydata
            ) {
              let yList = []
              this.EchartsData.ydataList[0].ydata.forEach(item => {
                yList.push({
                  value: item
                })
              })
              this.Ydata = yList
            }
            if (this.EchartsData.xdataList !== null) {
              this.EchartsData.xdataList.forEach(item => {
                this.Xdata.push(item.slice(5, 10))
              })
              if (this.EchartsData.xdataList && this.EchartsData.xdataList.length > 0) {
                this.stratTime = this.EchartsData.xdataList[0].slice(0, 10)
                this.lastTime = this.EchartsData.xdataList[this.EchartsData.xdataList.length - 1].slice(0, 10)
              }
              if (this.Xdata.length > 7) {
                // 如果是大于7并且是偶数
                if (this.Xdata.length % 2 === 0) {
                  this.options.xAxis.axisLabel.interval = parseInt(this.Xdata.length / 3 - 1)
                  if (this.Xdata.length % 3 === 0) {
                    this.options.xAxis.axisLabel.showMaxLabel = true
                  } else {
                    this.options.xAxis.axisLabel.showMaxLabel = ''
                  }
                } else if (this.Xdata.length % 2 !== 0) {
                  // 大于7但不是偶数
                  this.options.xAxis.axisLabel.interval = parseInt(this.Xdata.length / 4) - 1
                  if (this.Xdata.length % 4 === 0) {
                    this.options.xAxis.axisLabel.showMaxLabel = true
                  } else {
                    this.options.xAxis.axisLabel.showMaxLabel = ''
                  }
                }
              } else {
                // 小于7
                this.options.xAxis.axisLabel.interval = 0
              }
            }
            // 设置y轴区间
            if (
              this.EchartsData.ydataList &&
              this.EchartsData.ydataList.length > 0 &&
              this.EchartsData.ydataList[0].trendReference === 'range' &&
              this.EchartsData.ydataList[0].hasTrendReference === true &&
              this.EchartsData.ydataList[0].trendReferenceMin > 0 &&
              this.EchartsData.ydataList[0].trendReferenceMax > 0
            ) {
              this.options.series[0].markArea = {
                itemStyle: {
                  color: 'rgba(51, 126, 255, 0.06)',
                  type: 'dashed'
                },
                data: [
                  [
                    {
                      yAxis: this.EchartsData.ydataList[0].trendReferenceMin
                    },
                    {
                      yAxis: this.EchartsData.ydataList[0].trendReferenceMax
                    }
                  ]
                ]
              }
              this.options.series[0].markLine = {
                silent: true,
                symbol: 'none',
                lineStyle: {
                  color: '#337EFF'
                },
                data: [
                  {
                    yAxis: this.EchartsData.ydataList[0].trendReferenceMin
                  },
                  {
                    yAxis: this.EchartsData.ydataList[0].trendReferenceMax
                  }
                ],
                itemStyle: {
                  normal: {
                    // color: "#e8961a", //折线点的颜色
                    label: {
                      show: false
                    } // 是否在折线点上显示数字
                  }
                }
              }
            }
            if (
              this.EchartsData.ydataList &&
              this.EchartsData.ydataList.length > 0 &&
              this.EchartsData.ydataList[0].trendReference === 'exact_value' &&
              this.EchartsData.ydataList[0].hasTrendReference === true &&
              this.EchartsData.ydataList[0].referenceValue > 0 &&
              this.EchartsData.ydataList[0].targetValue > 0
            ) {
              this.options.series[0].markLine = {
                silent: true,
                symbol: 'none',
                lineStyle: {
                  color: '#337EFF'
                },
                data: [
                  {
                    yAxis: this.EchartsData.ydataList[0].trendReferenceExactValue
                  }
                ],
                itemStyle: {
                  normal: {
                    // color: "#e8961a", //折线点的颜色
                    label: {
                      show: false
                    } // 是否在折线点上显示数字
                  }
                }
              }
            }

            if (
              this.EchartsData.ymonitorEvent !== null &&
              this.EchartsData.ymonitorEvent.length > 0 &&
              this.Ydata.length > 0
            ) {
              // 添加小红旗
              this.EchartsData.ymonitorEvent[0].ydata.forEach((item, index) => {
                if (item !== null) {
                  this.Ydata[index].symbol = this.baceImg
                  this.Ydata[index].symbolSize = [10, 20]
                  this.Ydata[index].symbolOffset = [2, -8]
                }
              })
              this.options.series[0].data = this.Ydata
            } else {
              if (this.EchartsData.ydataList && this.EchartsData.ydataList.length > 0) {
                this.options.series[0].data = this.EchartsData.ydataList[0].ydata
              }
            }

            setTimeout(() => {
              this.showEchatrts()
            }, 500)
          }

          Toast.clear()
        }
      })
    },
    // 画图方法
    showEchatrts () {
      this.ydatas()
      if (this.EchartsData.showTrend === true) {
        let n = 0
        let arr = []
        if (
          (this.EchartsData.ydataList.length > 0 && this.EchartsData.ydataList[0].ydata) ||
          (this.params.monitorDayType === 'OneDay' && !this.EchartsData.ydataList[0].ydata)
        ) {
          this.options.yAxis.axisLabel.show = true
          if (this.EchartsData.ydataList[0].ydata) {
            this.EchartsData.ydataList[0].ydata.forEach(item => {
              if (item !== null) {
                arr.push(item)
                n++
              }
            })
          }
          let sortArr = arr.sort((a, b) => a - b)
          if (n === 0 || this.EchartsData.ydataList[0].ydata == null) {
            // 如果都是null
            this.options.yAxis.min = 0
            this.options.yAxis.splitLine = {
              show: false
            }
            if (this.EchartsData.ydataList[0].hasTrendReference === true) {
              this.options.yAxis.splitLine = {
                show: true
              }
              this.options.yAxis.axisLabel.show = true
              if (this.EchartsData.ydataList[0].trendReference === 'exact_value') {
                this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceExactValue * 2
                this.options.yAxis.interval = Number(
                  ((this.EchartsData.ydataList[0].trendReferenceExactValue * 2) / 5).toFixed(1)
                )
              }
              if (this.EchartsData.ydataList[0].trendReference === 'range') {
                this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceMax
                this.options.yAxis.interval = Number((this.EchartsData.ydataList[0].trendReferenceMax / 5).toFixed(1))
              }
            } else {
              this.options.yAxis.axisLabel.show = false
              this.options.yAxis.splitLine = {
                show: false
              }
            }
          } else if (n > 1) {
            this.options.yAxis.splitLine = {
              show: true
            }
            if (this.EchartsData.ydataList[0].hasTrendReference === true) {
              // ydataList不为空切有值的时候
              if (this.EchartsData.ydataList[0].trendReference === 'exact_value') {
                this.options.series[0].markLine = {
                  silent: true,
                  symbol: 'none',
                  lineStyle: {
                    color: '#337EFF'
                  },
                  data: [
                    {
                      yAxis: this.EchartsData.ydataList[0].trendReferenceExactValue
                    }
                  ],
                  itemStyle: {
                    normal: {
                      label: {
                        show: false
                      }
                    }
                  }
                }
                // 精确数值
                if (this.EchartsData.ydataList[0].trendReferenceExactValue <= sortArr[0]) {
                  // 如果精确数值小于数据最小值
                  this.options.yAxis.min = 0
                  this.options.yAxis.max = sortArr[sortArr.length - 1]
                  this.options.yAxis.interval = Number((sortArr[sortArr.length - 1] / 5).toFixed(1))
                  console.log(this.options.yAxis.min)
                }

                if (
                  Number(this.EchartsData.ydataList[0].trendReferenceExactValue) >= Number(sortArr[sortArr.length - 1])
                ) {
                  // 如果Y轴最大值小于精确数值
                  this.options.yAxis.min = sortArr[0]
                  this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceExactValue
                  this.options.yAxis.interval = Number(
                    ((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1)
                  )
                }
                if (
                  this.EchartsData.ydataList[0].trendReferenceExactValue > sortArr[0] &&
                  Number(sortArr[sortArr.length - 1]) > Number(this.EchartsData.ydataList[0].trendReferenceExactValue)
                ) {
                  this.options.yAxis.min = sortArr[0]
                  this.options.yAxis.max = sortArr[sortArr.length - 1]
                  this.options.yAxis.interval = Number(
                    ((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1)
                  )
                }
                if (this.EchartsData.ydataList[0].trendReferenceExactValue <= sortArr[0]) {
                  this.options.yAxis.min = 0
                  this.options.yAxis.max = sortArr[sortArr.length - 1]
                  this.options.yAxis.interval = Number(
                    ((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1)
                  )
                }
              }

              if (this.EchartsData.ydataList[0].trendReference === 'range') {
                this.options.series[0].markLine = {
                  silent: true,
                  symbol: 'none',
                  lineStyle: {
                    color: '#337EFF'
                  },
                  data: [
                    {
                      yAxis: this.EchartsData.ydataList[0].trendReferenceMax
                    },
                    {
                      yAxis: this.EchartsData.ydataList[0].trendReferenceMin
                    }
                  ],
                  itemStyle: {
                    normal: {
                      label: {
                        show: false
                      }
                    }
                  }
                }
                this.options.series[0].markArea = {
                  itemStyle: {
                    color: 'rgba(51, 126, 255, 0.06)',
                    type: 'dashed'
                  },
                  data: [
                    [
                      {
                        yAxis: this.EchartsData.ydataList[0].trendReferenceMax
                      },
                      {
                        yAxis: this.EchartsData.ydataList[0].trendReferenceMin
                      }
                    ]
                  ]
                }
                if (
                  this.EchartsData.ydataList[0].trendReferenceMax > sortArr[sortArr.length - 1] && // 如果区间最大值大于y轴最大值并且区间最小值小于y轴最小值
                  this.EchartsData.ydataList[0].trendReferenceMin < sortArr[0]
                ) {
                  this.options.yAxis.min = 0
                  this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceMax
                  this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
                }
                if (
                  this.EchartsData.ydataList[0].trendReferenceMax < sortArr[sortArr.length - 1] && // 如果区间最大值小于y轴最大值并且区间最小值小于y轴最小值
                  this.EchartsData.ydataList[0].trendReferenceMin > sortArr[0]
                ) {
                  this.options.yAxis.min = sortArr[0]
                  this.options.yAxis.max = sortArr[sortArr.length - 1]
                  this.options.yAxis.interval = Number(
                    ((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1)
                  )
                }
                if (
                  this.EchartsData.ydataList[0].trendReferenceMax > sortArr[sortArr.length - 1] && // 如果区间最大值大于y轴最大值并且区间最小值大于y轴最小值
                  this.EchartsData.ydataList[0].trendReferenceMin > sortArr[0]
                ) {
                  this.options.yAxis.min = sortArr[0]
                  this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceMax
                  this.options.yAxis.max = sortArr[sortArr.length - 1]
                  this.options.yAxis.interval = Number(
                    ((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1)
                  )
                }

                if (
                  this.EchartsData.ydataList[0].trendReferenceMax < sortArr[sortArr.length - 1] && // 如果区间最大值小于y轴最大值并且区间最小值大于y轴最小值
                  this.EchartsData.ydataList[0].trendReferenceMin > sortArr[0]
                ) {
                  this.options.yAxis.max = sortArr[sortArr.length - 1]
                  this.options.yAxis.min = 0
                  this.options.yAxis.interval = Number(
                    ((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1)
                  )
                }
              }
            } else if (this.EchartsData.ydataList[0].hasTrendReference === false) {
              this.options.yAxis.min = sortArr[0]
              this.options.yAxis.max = sortArr[sortArr.length - 1]
              this.options.yAxis.interval = Number(((this.options.yAxis.max - this.options.yAxis.min) / 5).toFixed(1))
              if (this.options.series[0].markArea) {
                this.options.series[0].markArea.data = []
              }
              if (this.options.series[0].markLine) {
                this.options.series[0].markLine.data = []
              }
              this.options.yAxis.axisLabel.showMaxLabel = true
            }
          } else if (n === 1 && this.EchartsData.ydataList[0].hasTrendReference === false) {
            this.options.yAxis.splitLine = {
              show: true
            }
            this.options.yAxis.min = 0
            this.options.yAxis.max = arr[0] * 2

            if (this.options.series[0].markArea) {
              this.options.series[0].markArea.data = []
            }
            if (this.options.series[0].markLine) {
              this.options.series[0].markLine.data = []
            }
            this.options.yAxis.axisLabel.showMaxLabel = true
            this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
          } else if (n === 1 && this.EchartsData.ydataList[0].hasTrendReference === true) {
            this.options.yAxis.min = 0

            // 只有一条数据而且有参考值
            if (this.EchartsData.ydataList[0].trendReference === 'exact_value') {
              if (Number(sortArr[0]) >= Number(this.EchartsData.ydataList[0].trendReferenceExactValue)) {
                this.options.yAxis.max = sortArr[0]
                this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
              } else if (Number(sortArr[0]) <= Number(this.EchartsData.ydataList[0].trendReferenceExactValue)) {
                this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
                this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceExactValue
              }
            } else if (this.EchartsData.ydataList[0].trendReference === 'range') {
              if (Number(sortArr[0]) >= Number(this.EchartsData.ydataList[0].trendReferenceMax)) {
                this.options.yAxis.max = sortArr[0]
              } else if (Number(sortArr[0]) <= Number(this.EchartsData.ydataList[0].trendReferenceMin)) {
                this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceMax
              } else if (
                Number(sortArr[0]) <= Number(this.EchartsData.ydataList[0].trendReferenceMax) &&
                Number(sortArr[0]) >= Number(this.EchartsData.ydataList[0].trendReferenceMin)
              ) {
                this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceMax
              } else if (sortArr[0] >= Number(this.EchartsData.ydataList[0].trendReferenceMax)) {
                this.options.yAxis.max = sortArr[0]
              }
            }
            this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
          }
          if (sortArr.length > 1 && sortArr[0] === sortArr[sortArr.length - 1]) {
            this.options.yAxis.min = 0
            this.options.yAxis.max = sortArr[0] * 2
            this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
          }
        }
      }
      // 单日曲线
      this.params.defaultQuery = 0
      // 设置Y周最大值与间隔
      if (this.EchartsData.ydataList && this.EchartsData.ydataList.length > 0 && this.EchartsData.ydataList[0].yaxis) {
        this.options.yAxis.max = this.EchartsData.ydataList[0].yaxis.max
        this.options.yAxis.min = this.EchartsData.ydataList[0].yaxis.min
        this.options.yAxis.interval = Number(this.EchartsData.ydataList[0].yaxis.interval)
      }
      // 设置单日曲线
      if (this.EchartsData.oneDayCurve === 1 || this.params.monitorDayType === 'OneDay') {
        this.options.xAxis.type = 'time'
        this.options.xAxis.max = this.today + ' 23:59:59'
        this.options.xAxis.min = this.today + ' 00:00:00'
        this.options.xAxis.splitNumber = 3
        this.options.xAxis.axisLabel.formatter = '{HH}:{mm}'
        let arr = {}
        if (this.EchartsData.xdataList && this.EchartsData.xdataList.length > 0) {
          this.options.series[0].data = []
          // 设置单日小红旗
          this.EchartsData.xdataList.forEach((item, index) => {
            arr = {
              value: [item, this.EchartsData.ydataList[0].ydata[index]]
            }
            if (
              this.EchartsData.ymonitorEvent &&
              this.EchartsData.ymonitorEvent.length > 0 &&
              this.EchartsData.ymonitorEvent[0].ydata
            ) {
              if (this.EchartsData.ymonitorEvent[0].ydata[index] !== null) {
                arr.symbol = this.baceImg
                arr.symbolSize = [10, 20]
                arr.symbolOffset = [2, -8]
              }
            }
            this.options.series[0].data.push(arr)
          })
        }
        if (
          this.EchartsData.ydataList &&
          this.EchartsData.ydataList.length > 0 &&
          !this.EchartsData.ydataList[0].ydata &&
          !this.EchartsData.ydataList[0].hasTrendReference
        ) {
          this.options.yAxis.max = 100
          this.options.yAxis.min = 0
          this.options.yAxis.interval = 100 / 5
          this.options.yAxis.axisLabel.show = true
          this.options.yAxis.splitLine.show = true
        }
      }
      // 设置范围或参考值
      if (
        this.EchartsData.ydataList &&
        this.EchartsData.ydataList.length > 0 &&
        this.EchartsData.ydataList[0].hasTrendReference
      ) {
        // 精确数值
        if (this.EchartsData.ydataList[0].trendReference === 'exact_value') {
          this.options.series[0].markLine = {
            silent: true,
            symbol: 'none',
            lineStyle: {
              color: '#337EFF'
            },
            data: [
              {
                yAxis: this.EchartsData.ydataList[0].trendReferenceExactValue
              }
            ],
            itemStyle: {
              normal: {
                label: {
                  show: false
                }
              }
            }
          }
          // 区间范围
        } else {
          this.options.series[0].markLine = {
            silent: true,
            symbol: 'none',
            lineStyle: {
              color: '#337EFF'
            },
            data: [
              {
                yAxis: this.EchartsData.ydataList[0].trendReferenceMax
              },
              {
                yAxis: this.EchartsData.ydataList[0].trendReferenceMin
              }
            ],
            itemStyle: {
              normal: {
                label: {
                  show: false
                }
              }
            }
          }
        }
      }
      this.iskuanian()
      this.options.xAxis.data = this.Xdata
      if (this.EchartsData.ydataList !== null && this.EchartsData.ydataList.lenght > 0) {
        this.options.series[0].name = this.EchartsData.ydataList[0].label
      }
      let ydata = this.EchartsData.ymonitorEvent
      this.options.tooltip = {
        position: params => {
          if (params[0] > 100) {
            return [params[0] - 100, params[1]]
          } else {
            return params
          }
        },
        trigger: this.params.monitorDayType === 'OneDay' ? 'item' : 'axis',
        alwaysShowContent: false,
        borderColor: '#ffffff',
        axisPointer: {
          type: 'line',
          lineStyle: {
            color: '#B8B8B8',
            width: 1,
            type: 'solid'
          }
        },
        textStyle: {
          // color: '#337EFF' ,//设置文字颜色
          fontSize: 9,
          fontWeight: 'normal'
        },
        formatter: params => {
          console.log(params)
          let itemData = ''
          let result = ''
          let seriesName = ''
          let dotHtml =
            '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:4px;height:4px;background-color:#337EFF"></span>'

          let twoYuan =
            '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:4px;height:4px;background-color:red"></span>'
          if (params.length > 0) {
            //   除了单日
            if (this.EchartsData.ydataList !== [] && params.data !== null) {
              seriesName = `<span style="color:#666666;margin-right:5px;max-width: 50px">${this.EchartsData.ydataList[0].label}</span>`
            }
            itemData = `<span style="color:#337EFF ;font-weight: bold;margin-right:7px">${
              this.EchartsData.ydataList[0].ydata[params[0].dataIndex] == null
                ? '-'
                : this.EchartsData.ydataList[0].ydata[params[0].dataIndex]
            }</span>`
            params.forEach(function (item) {
              if (item.data !== null && item.data.symbol) {
                result +=
                  item.axisValueLabel +
                  '</br>' +
                  dotHtml +
                  seriesName +
                  itemData +
                  '</br>' +
                  twoYuan +
                  `<span style="white-space: normal">${ydata[0].ydata[item.dataIndex]}</span>`
              } else {
                result += item.axisValueLabel + '</br>' + dotHtml + seriesName + itemData
              }
            })
          } else {
            //  单日
            if (this.EchartsData.ydataList !== [] && params.data !== null) {
              seriesName = `<span style="color:#666666;margin-right:5px;max-width: 50px">${this.EchartsData.ydataList[0].label}</span>`
            }
            itemData = `<span style="color:#337EFF ;font-weight: bold;margin-right:7px">${
              this.EchartsData.ydataList[0].ydata[params.dataIndex] == null
                ? '-'
                : this.EchartsData.ydataList[0].ydata[params.dataIndex]
            }</span>`
            // params.forEach(function (item) {
            if (params.data !== null && params.data.symbol) {
              result +=
                this.EchartsData.xdataList[params.dataIndex] +
                '</br>' +
                dotHtml +
                seriesName +
                itemData +
                '</br>' +
                twoYuan +
                `<span style="white-space: normal">${ydata[0].ydata[params.dataIndex]}</span>`
            } else {
              result += this.EchartsData.xdataList[params.dataIndex] + '</br>' + dotHtml + seriesName + itemData
            }
            // })
          }

          return `<div style="max-width: 250px">${result}</div>`
        }
      }
      const chartDom = document.getElementById('myChart')
      this.myChart = echarts.init(chartDom)
      this.myChart.setOption(this.options)
      this.myChart.resize()
    },
    // 选择上面的时间区间
    chooseTime (i, index) {
      if (i.name !== '单日') {
        this.options.xAxis.type = 'category'
        this.options.xAxis.max = ''
        this.options.xAxis.min = ''
        this.options.xAxis.min = ''
        this.options.xAxis.splitNumber = ''
        this.options.xAxis.axisLabel.formatter = null
        this.options.series[0].data = []
        if (this.params.oneDayDate) {
          delete this.params.oneDayDate
        }
      }
      this.Xdata = []
      this.timeNumber = index
      if (this.params.monitorDayType === 'OneDay' && i.monitorDayType === 'customize') {
        this.params.customizeEndDate = this.today
        this.params.customizeStartDate = this.today
        this.params.monitorDayType = 'customize'
        Toast.loading({
          message: '加载中...',
          forbidClick: true
        })
        this.getchartdata()
      }
      console.log(this.params)
      this.params.monitorDayType = i.monitorDayType
      console.log(this.params.monitorDayType)

      if (i.monitorDayType !== 'customize') {
        if (i.monitorDayType === 'Day7') {
          this.options.xAxis.axisLabel.interval = 0
        }
        Toast.loading({
          message: '加载中...',
          forbidClick: true
        })
        // 如果切换到单日
        if (i.monitorDayType === 'OneDay') {
          this.options.xAxis.type = 'time'
          this.params.customizeEndDate = ''
          this.params.customizeStartDate = ''
          this.stratTime = this.today
          this.params.oneDayDate = this.today
        }
        this.getchartdata()
      } else {
        this.getCurTime()
        this.endTimes = ''
        this.currentDate = new Date()
        // this.show = true
      }
    },

    // 计算Y轴坐标值
    ydatas () {
      let arr = []
      for (let i = this.Ydata.length - 1; i >= 0; i--) {
        if (this.Ydata[i].value != null) {
          this.Ydata[i].value = Number(this.Ydata[i].value)
          arr.push(this.Ydata[i].value)
        }
      }
    },
    // 判断是否跨年
    iskuanian () {
      let a = ''
      let b = ''
      if (this.EchartsData.xdataList[0]) {
        a = this.EchartsData.xdataList[0].slice(0, 4)
      }
      if (this.EchartsData.xdataList[this.EchartsData.xdataList.length - 1]) {
        b = this.EchartsData.xdataList[this.EchartsData.xdataList.length - 1].slice(0, 4)
      }
      if (b - a > 0) {
        this.Xdata = []
        this.EchartsData.xdataList.forEach(item => {
          let a = ''
          a = item.slice(0, 10)
          this.Xdata.push(a)
        })
      }
    },
    getArray (json) {
      return JSON.parse(json)
    },
    getImage (values) {
      if (values) {
        if (values.length === 1) {
          values.push({
            attrValue: ''
          })
          values.push({
            attrValue: ''
          })
          values.push({
            attrValue: ''
          })
        } else if (values.length === 2) {
          values.push({
            attrValue: ''
          })
          values.push({
            attrValue: ''
          })
        } else if (values.length === 3) {
          values.push({
            attrValue: ''
          })
        }
      }
      return values
    },
    getFormValue (item) {
      return getValue(item)
    },
    finishedText () {
      if (this.list.length > 4) {
        return '没有更多了'
      } else {
        return ''
      }
    },
    HorizontalScreen () {
      this.$router.push({
        path: '/patient/monitor/horizontalScreen',
        query: {
          id: this.$route.query.planId,
          title: this.pageTitle,
          monitorDayType: this.params.monitorDayType,
          timeNumber: this.timeNumber,
          stratTime: this.stratTime,
          lastTime: this.lastTime,
          today: this.today,
          hasOneDay: this.hasOneDay
        }
      })
    },
    drawLine () {
      this.option.xAxis.data = this.xList
    },
    getTime (time, format) {
      return moment(time).format(format)
    },
    getData () {
      let params = {
        current: this.current,
        size: 10,
        model: {
          businessId: this.$route.query.planId,
          monitorQueryDate: this.monitorQueryDate,
          userId: localStorage.getItem('patientId'),
          oneDayDate: this.monitorQueryDate === 'CURRENT_DAY' ? this.params.oneDayDate ? this.params.oneDayDate : moment().format('YYYY-MM-DD') : '',
          customizeStartDate: this.monitorQueryDate === 'CUSTOM_DATE' ? this.params.customizeStartDate ? this.params.customizeStartDate : moment().format('YYYY-MM-DD') : '',
          customizeEndDate: this.monitorQueryDate === 'CUSTOM_DATE' ? this.params.customizeEndDate ? this.params.customizeEndDate : moment().format('YYYY-MM-DD') : ''
        }
      }
      if (this.finished) {
        return
      }
      if (this.loading) {
        return
      }
      this.loading = true
      getMonitorList(params).then(res => {
        if (res.data && res.code === 0) {
          if (this.current >= res.data.pages) {
            this.finished = true
            console.log('findSugarPage', this.finished)
          }
          this.loading = false
          res.data.records.forEach(item => {
            this.list.push({...item, isOpen: false})
          })
          this.current++
        }
      })
    },
    addData (item) {
      if (item && item.id) {
        if (item.scoreQuestionnaire === 1) {
          // 评分表单
          this.$router.push({
            path: '/score/show',
            query: {
              dataId: item.id,
              title: this.$route.query.title,
              id: this.$route.query.planId
            }
          })
        } else {
          this.$router.push({
            path: '/patient/monitor/add',
            query: {
              dataId: item.id,
              title: this.$route.query.title,
              planId: this.$route.query.planId
            }
          })
        }
      } else {
        this.$router.push({
          path: '/patient/monitor/add',
          query: {
            planId: this.$route.query.planId,
            title: this.$route.query.title
          }
        })
      }
    },
    onLoad () {
      if (this.refreshing) {
        this.list = []
        this.current = 1
        this.refreshing = false
      }
      this.getData()
    },
    onRefresh () {
      this.list = []
      this.finished = false
      this.onLoad()
    }
  }
}
</script>

<style lang="less" scoped>
/deep/ .van-picker-column {
  position: relative;
  z-index: 1;

  &:before {
    width: 100%;
    height: 100%;
    position: absolute;
    top: 0;
    left: 0;
    content: '';
  }

  & > ul {
    z-index: -1;
    position: relative;
  }
}

.jiange {
  padding-top: 0px;
}

/deep/ .van-overlay {
  z-index: 9999;
}

.wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  z-index: 99999999999999;

  .block {
    width: 292px;
    height: 446px;
    border-radius: 6px;
    background-color: #fff;

    .title {
      //display: flex;
      margin-top: 27px;
      position: relative;

      div {
        // width: 133px;
        font-size: 17px;
        color: #333333;
        margin-left: 79px;
        margin-right: 45px;
      }

      img {
        height: 21px;
        width: 21px;
        position: absolute;
        top: 50%;
        right: 14px;
        transform: translateY(-50%);
      }
    }
  }

  .timeBox {
    line-height: 33px;
    display: flex;
    margin-top: 33px;
    text-align: center;
    line-height: 33px;
    margin-bottom: 25px;
    color: #666666;

    .startTime {
      margin-left: 25px;
      height: 33px;
      width: 98px;
      border: 1px solid #b8b8b8;
      border-radius: 4px;
      margin-right: 17px;
      // padding: 10px 15px 10px 15px;
    }

    .endTime {
      margin-left: 17px;
      height: 33px;
      width: 98px;
      border: 1px solid #b8b8b8;
      border-radius: 4px;
    }

    .timeBlue {
      background: rgba(51, 126, 255, 0.08);
      border: 1px solid #337eff;
      color: #337eff;
    }
  }

  /deep/ .timeChoose {
    .van-picker {
      height: 211px;

      .van-picker__columns {
        //height: 211px !important;

        .van-hairline-unset--top-bottom {
          width: 99%;
          left: 0 !important;
          //top: 62% !important;
          //background: rgba(250, 250, 250, 0.39);
          border: 1px solid #d6d6d6;
          border-radius: 4px;
        }
      }
    }
  }

  .TJ-btn {
    div {
      width: 167px;
      height: 42px;
      text-align: center;
      line-height: 42px;
      background: #66728b;
      color: #fff;
      border-radius: 42px;
      margin: 0 auto;
    }
  }
}

.caring-select {
  outline: none;
  width: 77px;
  height: 29px;
  border-radius: 5px 5px 5px 5px;
  border: 1px solid #999999;
  color: #666666;
  font-size: 14px;
  font-weight: 400;
  text-align: center;
}

.ckqj {
  display: flex;
  position: relative;
  color: #999999;
  font-size: 14px;

  .cknum {
    width: 60px;
  }

  .qvjian {
    height: 12px;
    width: 20px;
    position: absolute;
    top: 50%;
    left: 17px;
    transform: translate(0, -50%);
  }

  .ck {
    // width:80px;
    margin-left: 17px;
  }

  .jzz {
    margin-right: 20px;
  }

  .mbz {
    margin-right: 20px;
  }

  .updata {
    #updata {
      height: 12px;
      width: 13px;
      position: absolute;
      top: 50%;
      right: 17px;
      transform: translate(0, -50%);
    }
  }
}

.choosetime {
  padding: 10px 13px 10px 13px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  background: #FFF;

  p {
    font-size: 15px;
    color: #666666;

    img {
      width: 12px;
      height: 12px;
    }
  }

  .quanping {
    right: 0;
    width: 25px;
    height: 25px;
    margin: 0;
    position: absolute;
    top: 50%;
    transform: translate(0, -50%);
  }
}

ul {
  margin-left: 13px;
  margin-right: 13px;

  display: flex;
  // border: 1px solid #b8b8b8;
  // margin: 0px auto;
  padding-top: 21px;
  text-align: center;
  line-height: 33px;
  background: #fff;
  border-right: none;
  // border-right: 1px solid #b8b8b8;
  // border-left: 1px solid #b8b8b8;
  border-radius: 4px;
  margin-bottom: 21px;

  .ul-li {
    width: 100%;
    //height: 33px;
    font-size: 13px;
    color: #666666;
    border: 1px solid #b8b8b8;
    // border-left: none;
  }

  .firstli {
    //border-left: none;
    border-radius: 4px 0 0 4px;
    border-right: none;
  }

  .lastli {
    border-radius: 0 4px 4px 0;
  }

  .chooseli {
    color: #337eff;
    background: rgba(51, 126, 255, 0.05);
    border: 1px solid #337eff;
  }
}

body {
  height: 100vh !important;
  background-color: #fafafa;
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}

/deep/ .weui-cells:after {
  border-bottom: unset;
}

.allContent {
  width: 100vw;
  position: relative;
  height: 100vh;
}

.nodata {
  width: 100vw;
  height: 50vh;
  text-align: center;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5;
}

/deep/ .van-pull-refresh {
  z-index: 1;
}
</style>
