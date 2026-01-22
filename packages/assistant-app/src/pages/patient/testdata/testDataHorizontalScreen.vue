<template>
  <section class="allContent" ref="view">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '监测数据'" @onBack="() => $router.go(-1)"></headNavigation>
    </van-sticky>

    <div style="background:#fff;display:flex;justify-content: space-between;padding-top:21px">
      <div class="choosetime">
        <span>{{ params.monitorDayType === 'OneDay' ? today : stratTime + '-' + lastTime }}</span>
      </div>

      <ul>
        <li v-for="(i,index) in timeData" :key="index" @click="chooseTime(i.monitorDayType,index)" v-if="index==0"
            class="firstli" :class="params.monitorDayType==i.monitorDayType?'chooseli ul-li firstli':'ul-li firstli'">
          {{ i.name }}
        </li>
        <li v-if="index>0&&index<timeData.length-1" @click="chooseTime(i.monitorDayType,index)"
            v-for="(i,index) in timeData" :key="index"
            :class="params.monitorDayType==i.monitorDayType?'chooseli ul-li':'ul-li'">{{ i.name }}
        </li>
        <li v-for="(i,index) in timeData" :key="index" @click="chooseTime(i.monitorDayType,index)"
            v-if="index===timeData.length-1" class="lastli"
            :class="params.monitorDayType==i.monitorDayType?'chooseli ul-li lastli':'ul-li lastli'">{{ i.name }}
        </li>
      </ul>

    </div>

    <div style="background:#fff">
      <div id="myChart" style="height:250px;width:100%"></div>
    </div>

    <div style="background:#fff">
      <!-- <div id="myChart" style="height:230px;width:100%"></div> -->
      <div>
        <div class="ckqj" v-for="(item,index) in EchartsData.ydataList" :key="index">
          <div v-if="item.hasTrendReference!==false" class="ck">
            {{ item.trendReference == 'range' ? '参考区间:' : '参考值:' }}
          </div>
          <div style="color:#3f86ff;margin-left:10px"
               v-if="item.trendReference=='exact_value'&&item.trendReferenceExactValue!==null&&item.hasTrendReference!==false">
            {{ item.trendReferenceExactValue }}
          </div>
          <div style="color:#3f86ff;margin-left:10px"
               v-if="item.trendReference=='range'&&item.hasTrendReference!==false">
            {{ item.trendReferenceMin }}-{{ item.trendReferenceMax }}
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { getMonitorLineChart, monitorLineChart } from '@/api/formApi.js'
import Vue from 'vue'
import {DatetimePicker, Toast} from 'vant'
import moment from 'moment'
import * as echarts from 'echarts'

Vue.use(DatetimePicker)
Vue.use(Toast)
export default {
  name: 'TestDataHorizontalScreen',
  data () {
    return {
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
        }
      ],
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
      type: 'WEEK', // monitorDateLineType,可用值:DAY,WEEK,MONTH
      trendReferenceMax: 0,
      trendReferenceMin: 0,

      // 后加的
      params: {
        customizeEndDate: '',
        customizeStartDate: '',
        monitorDayType: 'Day30',
        defaultQuery: 0
      },
      EchartsData: [],
      Xdata: [],
      Ydata: [],
      stratTime: '',
      lastTime: '',
      options: {
        grid: {
          left: '2%',
          right: '5%',
          bottom: '10%',
          top: '20%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLabel: {
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
      baceImg:
        'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAAAXNSR0IArs4c6QAAAehJREFUaEPtWTtOAzEQHZuPOQA9BUgUFCDgBhyAhpNAm8QbZ5Ub5BY03IQgUVAgQZlUoctGkQd582ETZcnEixcveKU0kT2e955nPGMzBNiCCn/s3wAYNZs3yHnTiMW0lrtS3vsgHFmBpNV6BsaOU6cRX0S9flIpAKM4HqPW5fuMOOBJcrXTbndXLU5XQKlx+d5PV0QcCCn3qwzgSUh5XlkAqLXaiyJVCQCICIyxrK8fotc7hE5n4D0A47YBABkA37GfpnTqQZaUEMSbsu8VABv2vQJgw75XAGDDvT8LaG9iAE1AZtLMuuD1D8By+jSnb79/lJc+vQOQBvFSoqeo4M0WmhS5S4cYQQWvANio4BUAGxW8A7Cq3qlUMbey5/iJfmCo1HihRiyzu0Es3g/8VkuJAO9c69vdKHooVk7H8UJLKWq17TJFyFuLHsQBgBu9ggJueKVbDQrQuXIzMijghle61aAAnSs3I4MCbnilWw0K0LlyMzIo4IZXutW/q8Aoiq6RsTowdpbywfkiLbMXS8Q3hniX1/LRubQbmavAUKlXBnAwN5sHYHKZ8yikvLRzodgsOgDz7DN7+jFX4eb39XVFo3FRzBW72bkA0i3EeQMATuemswCmf667NbBziz6LHMR0k+WOrDyAT4hSeqAB3EL0AAAAAElFTkSuQmCC',
      timeNumber: 2,
      pageTitle: '',
      today: this.$route.query.today,
      myChart: null
    }
  },
  mounted () {
    this.resizeScreen()
    this.current = 1
    this.minDate = new Date(2000, 0, 1)
    this.getchartdata()
  },
  beforeMount () {
    if (this.$route.query.hasOneDay === 'true') {
      const arr = {
        name: '单日',
        monitorDayType: 'OneDay'
      }
      this.timeData.splice(1, 0, arr)
    }
    if (this.$route.query.title) {
      this.pageTitle = this.$route.query.title
      if (!this.params.monitorDayType === 'OneDay') {
        this.params.monitorDayType = this.$route.query.monitorDayType
      }
      console.log(this.params.monitorDayType)
      if (this.params.monitorDayType === 'OneDay') {
        this.params.defaultQuery = 1
        this.params.customizeStartDate = ''
        this.params.customizeEndDate = ''
        this.params.oneDayDate = this.$route.query.stratTime
      } else {
        this.params.customizeStartDate = this.$route.query.stratTime
        this.params.customizeEndDate = this.$route.query.lastTime
        this.chooseTime(this.$route.query.monitorDayType, this.$route.query.timeNumber)
      }
    }
  },
  methods: {
    touchStart (e) {
      console.log(e)
    },
    // 手机横屏
    resizeScreen () {
      const _this = this
      // 利用 CSS3 旋转 对根容器逆时针旋转 90 度
      const detectOrient = function () {
        let width = document.documentElement.clientWidth
        let height = document.documentElement.clientHeight
        let $wrapper = _this.$refs.view // 这里是页面最外层元素
        let style = ''
        if (width >= height) {
          // 横屏
          style += 'width:' + width + 'px;' // 注意旋转后的宽高切换
          style += 'height:' + height + 'px;'
          style += '-webkit-transform: rotate(0); transform: rotate(0);'
          style += '-webkit-transform-origin: 0 0;'
          style += 'transform-origin: 0 0;'
        } else {
          // 竖屏
          style += 'width:' + height + 'px;'
          style += 'height:' + width + 'px;'
          style += '-webkit-transform: rotate(90deg); transform: rotate(90deg);'
          // 注意旋转中点的处理
          style += '-webkit-transform-origin: ' + width / 2 + 'px ' + width / 2 + 'px;'
          style += 'transform-origin: ' + width / 2 + 'px ' + width / 2 + 'px;'
        }
        $wrapper.style.cssText = style
      }
      window.onresize = detectOrient
      detectOrient()
    },

    buttonClick (type) {
      this.type = type
      this.xList = []
      this.yList = []
      this.trendReferenceMax = 0
      this.trendReferenceMin = 0
      this.option.xAxis.data = []
      this.option.series = []
      this.getEchartsData()
    },
    // 折线图
    getEchartsData () {
      getMonitorLineChart(this.$route.query.id, this.type).then(res => {
        if (res.data && res.code === 0) {
          if (res.data.xdataList) {
            res.data.xdataList.forEach(item => {
              this.xList.push(this.getEchartsTime(item))
            })
          }
          if (res.data.ydataList) {
            res.data.ydataList.forEach(item => {
              if (
                item.hasTrendReference &&
                item.trendReference === 'range' &&
                item.trendReferenceMax &&
                item.trendReferenceMin
              ) {
                this.trendReferenceMax = item.trendReferenceMax
                this.trendReferenceMin = item.trendReferenceMin
              }
              this.option.series.push({
                name: item.label,
                type: 'line',
                data: item.ydata,
                markLine:
                  item.trendReference === 'exact_value'
                    ? {
                      lineStyle: {
                        color: '#999'
                      },
                      data: [
                        {
                          yAxis: item.trendReferenceExactValue
                        }
                      ]
                    }
                    : {}
              })
            })
          }

          // 延迟保证echart刷新
          setTimeout(() => {
            this.drawLine()
          }, 1000)
        }
      })
    },
    getEchartsTime (time) {
      // monitorDateLineType,可用值:DAY,WEEK,MONTH
      if (this.type === 'DAY') {
        return moment(time).format('H:m')
      } else if (this.type === 'WEEK') {
        return moment(time).format('M/D')
      } else if (this.type === 'MONTH') {
        return moment(time).format('D')
      }
    },
    drawLine () {
      this.option.xAxis.data = this.xList
      let myChart = echarts.init(document.getElementById('myChart'))
      document.getElementById('myChart').setAttribute('_echarts_instance_', '')
      // 绘制图表
      myChart.setOption(this.option)
      if (this.trendReferenceMin > 0 && this.trendReferenceMax > 0) {
        myChart.dispatchAction({
          type: 'brush',
          areas: [
            {
              brushType: 'lineY',
              coordRange: [this.trendReferenceMin, this.trendReferenceMax],
              xAxisIndex: 0
            }
          ]
        })
      }
    },
    closeTime () {
      this.show = false
      this.startOrEnd = 'start'
    },

    // 自定义时间确定按钮
    updata () {
      if (this.startOrEnd === 'start') {
        let value = this.$refs.value.getPicker().getValues()
        this.startTimes = value[0] + '-' + value[1] + '-' + value[2]
        this.stratTime = this.startTimes
        this.params.customizeStartDate = this.stratTime
        this.minDate2 = new Date(value[0] + ',' + value[1] + ',' + value[2])

        this.startOrEnd = 'end'
      } else {
        //
        let values = this.$refs.endtime.getPicker().getValues()
        this.lastTime = values[0] + '-' + values[1] + '-' + values[2]
        this.endTimes = this.lastTime
        this.params.customizeEndDate = this.endTimes
        this.show = false
        Toast.loading({
          message: '加载中...',
          forbidClick: true
        })
        this.getchartdata()
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
      if (this.$route.query.timeNumber === 4 && this.params.customizeStartDate === '') {
      } else {
        let patientId = localStorage.getItem('patientId')
        monitorLineChart(this.params, this.$route.query.id, patientId).then(res => {
          if (res.code === 0) {
            Toast.clear()
            this.params.defaultQuery = 0
            this.EchartsData = res.data
            console.log(this.EchartsData, '===================>')
            if (this.EchartsData.oneDayCurve === 1 && this.timeData.length === 4) {
              this.options.xAxis.type = 'time'
              this.params.monitorDayType = 'OneDay'
              this.lastTime = this.today
              this.stratTime = this.today
              this.timeNumber = 1
            }

            let a = []
            this.Xdata = []

            if (this.EchartsData.ydataList !== null && this.EchartsData.ydataList.length > 0) {
              if (this.EchartsData.ydataList[0].ydata && this.EchartsData.ydataList[0].ydata.length > 0) {
                this.EchartsData.ydataList[0].ydata.forEach(item => {
                  a.push({
                    value: item
                  })
                })
                this.Ydata = a
              }
            }

            this.EchartsData.xdataList.forEach(item => {
              let a = ''
              a = item.slice(5, 10)
              this.Xdata.push(a)
            })
            if (this.EchartsData.xdataList && this.EchartsData.xdataList.length > 0) {
              this.stratTime = this.EchartsData.xdataList[0].slice(0, 10)
              this.lastTime = this.EchartsData.xdataList[this.EchartsData.xdataList.length - 1].slice(0, 10)
            }
            if (this.Xdata.length > 7) {
              // 如果是大于7并且是偶数
              if (this.Xdata.length % 2 === 0) {
                console.log(456)
                this.options.xAxis.axisLabel.interval = parseInt(this.Xdata.length / 3 - 1)
                // console.log(this.options.xAxis.axisLabel.interval);
                if (this.Xdata.length % 3 === 0) {
                  this.options.xAxis.axisLabel.showMaxLabel = true
                } else {
                  this.options.xAxis.axisLabel.showMaxLabel = ''
                }
              } else if (this.Xdata.length % 2 !== 0) {
                // 大于7但不是偶数
                this.options.xAxis.axisLabel.interval = parseInt(this.Xdata.length / 4) - 1
                console.log(this.options.xAxis.axisLabel.interval)
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
                      // name: 'Morning Peak',
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
                    label: {show: false} // 是否在折线点上显示数字
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
              // &&
              // this.EchartsData.ydataList[0].trendReferenceExactValue > this.options.yAxis.min
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
                    label: {show: false} // 是否在折线点上显示数字
                  }
                }
              }
            }

            if (this.EchartsData.ymonitorEvent !== null && this.EchartsData.ymonitorEvent.length > 0) {
              // 添加小红旗
              this.EchartsData.ymonitorEvent[0].ydata.forEach((item, index) => {
                if (item !== null) {
                  this.Ydata[index].symbol = this.baceImg
                  this.Ydata[index].symbolSize = [10, 20]
                  this.Ydata[index].symbolOffset = [2, -8]
                }
              })
              this.options.series[0].data = this.Ydata

              setTimeout(() => {
                this.showEchatrts()
              }, 500)
            } else {
              if (this.EchartsData.ydataList && this.EchartsData.ydataList.length > 0) {
                this.options.series[0].data = this.EchartsData.ydataList[0].ydata
                setTimeout(() => {
                  this.showEchatrts()
                }, 500)
              }
            }
            setTimeout(() => {
              this.showEchatrts()
            }, 500)
          }
        })
      }
    },
    // 画图方法
    showEchatrts () {
      console.log(456)
      this.ydatas()
      if (this.EchartsData.showTrend === true) {
        let n = 0
        let arr = []
        if (
          (this.EchartsData.ydataList &&
            this.EchartsData.ydataList.length > 0 &&
            this.EchartsData.ydataList[0].ydata) ||
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
          console.log(sortArr, '12132131313')
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
                  console.log(456)
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
              console.log(Number(sortArr[0]) >= Number(this.EchartsData.ydataList[0].trendReferenceExactValue))

              if (Number(sortArr[0]) >= Number(this.EchartsData.ydataList[0].trendReferenceExactValue)) {
                this.options.yAxis.max = sortArr[0]
                this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))
              } else if (Number(sortArr[0]) <= Number(this.EchartsData.ydataList[0].trendReferenceExactValue)) {
                this.options.yAxis.interval = Number((this.options.yAxis.max / 5).toFixed(1))

                this.options.yAxis.max = this.EchartsData.ydataList[0].trendReferenceExactValue
              }
            } else if (this.EchartsData.ydataList[0].trendReference === 'range') {
              console.log(Number(sortArr[0]) <= Number(this.EchartsData.ydataList[0].trendReferenceMax))
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
      console.log(123)
      // 设置Y周最大值与间隔
      if (this.EchartsData.ydataList && this.EchartsData.ydataList.length > 0) {
        if (this.EchartsData.ydataList[0].yaxis) {
          this.options.yAxis.max = this.EchartsData.ydataList[0].yaxis.max
          this.options.yAxis.min = this.EchartsData.ydataList[0].yaxis.min
          this.options.yAxis.interval = Number(this.EchartsData.ydataList[0].yaxis.interval)
        }
      }
      if (this.params.monitorDayType === 'OneDay') {
        this.options.xAxis.type = 'time'
        this.options.xAxis.max = this.today + ' 23:59:59'
        this.options.xAxis.min = this.today + ' 00:00:00'
        this.options.xAxis.min = this.today + ' 00:00:00'
        this.options.xAxis.splitNumber = 8
        this.options.xAxis.axisLabel.formatter = '{HH}:{mm}'
        let arr = {}
        if (this.EchartsData.xdataList && this.EchartsData.xdataList.length > 0) {
          this.options.series[0].data = []
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
          this.options.yAxis.splitLine = {
            show: true
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
      }
      console.log(this.options)
      var chartDom = document.getElementById('myChart')
      this.myChart = echarts.init(chartDom)
      this.iskuanian()
      // if (
      //   this.EchartsData.ydataList[0].ydata.length < 1 ||
      //   (this.EchartsData.ydataList[0].ydata.length == 1 && this.EchartsData.ydataList[0].ydata[0] == null)
      // ) {
      //   this.options.yAxis.axisLabel.show = false

      // }

      this.options.xAxis.data = this.Xdata
      if (this.EchartsData.ydataList !== null && this.EchartsData.ydataList.lenght > 0) {
        this.options.series[0].name = this.EchartsData.ydataList[0].label
      }
      let ydata = this.EchartsData.ymonitorEvent
      this.options.tooltip = {
        borderColor: '#ffffff',
        trigger: this.params.monitorDayType === 'OneDay' ? 'item' : 'axis',
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
      console.log(this.options)
      var option = this.options
      this.myChart.setOption(option)
    },
    // 选择上面的时间区间
    chooseTime (i, index) {
      if (this.myChart) {
        this.myChart.dispatchAction({
          type: 'hideTip'
        })
      }
      if (i !== 'OneDay') {
        this.options.xAxis.type = 'category'
        this.options.xAxis.max = ''
        this.options.xAxis.min = ''
        this.options.xAxis.min = ''
        this.options.xAxis.splitNumber = ''
        this.options.xAxis.axisLabel.formatter = null
      }
      this.Xdata = []
      this.timeNumber = index
      this.params.monitorDayType = i

      if (i !== 'customize') {
        if (i === 'Day7') {
          this.options.xAxis.axisLabel.interval = 0
        }
        Toast.loading({
          message: '加载中...',
          forbidClick: true
        })
        // 如果切换到单日
        if (i === 'OneDay') {
          this.options.xAxis.type = 'time'
          this.params.customizeEndDate = ''
          this.params.customizeStartDate = ''
          this.params.oneDayDate = this.$route.query.today
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
      console.log(this.Ydata, 13213213213)
      // this.Ydata = this.Ydata.filter(n => n) //删除ydata里的null
      // let sortArr = this.Ydata.sort((a, b) => a - b)

      // this.options.yAxis.interval = (sortArr[sortArr.length - 1] - sortArr[0]) / 5
      // this.options.yAxis.max = Math.max.apply(null, this.Ydata)
      // this.options.yAxis.min = Math.min.apply(null, this.Ydata)
      let arr = []
      for (let i = this.Ydata.length - 1; i >= 0; i--) {
        if (this.Ydata[i].value != null) {
          this.Ydata[i].value = Number(this.Ydata[i].value)
          arr.push(this.Ydata[i].value)
        }
      }
      console.log(arr)
      if (arr.length === 1) {
        this.options.yAxis.min = 0
        this.options.yAxis.max = arr[0]
        this.options.yAxis.interval = (arr[0] - 0) / 5
        this.options.yAxis.axisLabel.show = true
      } else if (arr.length > 1) {
        let sortArr = arr.sort((a, b) => a - b)
        this.options.yAxis.interval = Number(((sortArr[sortArr.length - 1] - sortArr[0]) / 5).toString().slice(0, 3))
        this.options.yAxis.max = sortArr[sortArr.length - 1]
        this.options.yAxis.min = sortArr[0]
        this.options.yAxis.axisLabel.show = true
      } else if (arr.length < 1) {
        this.options.yAxis.axisLabel.show = false
      }
      console.log('===============>', this.options)
    },

    // 判断是否跨年
    iskuanian () {
      //  this.stratTime = this.EchartsData.xdataList[0].slice(0, 10)
      //     this.lastTime = this.EchartsData.xdataList[this.EchartsData.xdataList.length - 1].slice(0, 10)
      if (this.EchartsData.xdataList && this.EchartsData.xdataList.length > 0) {
        let a = this.EchartsData.xdataList[0].slice(0, 4)
        let b = this.EchartsData.xdataList[this.EchartsData.xdataList.length - 1].slice(0, 4)
        if (b - a > 0) {
          this.Xdata = []
          this.EchartsData.xdataList.forEach(item => {
            let a = ''
            a = item.slice(0, 10)
            this.Xdata.push(a)
          })
        }
      }
    }
  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  // position: fixed;
  width: 100%;
  // z-index: 999;
  top: 0;
  left: 0;
}

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fff;
}

.wrapper {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50%;
  z-index: 99999999999999;

  .block {
    margin-top: 150px;
    width: 292px;
    border-radius: 6px;
    background-color: #fff;

    .title {
      display: flex;
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
      .van-picker__columns {
        .van-hairline-unset--top-bottom {
          width: 99%;
          left: 0 !important;
          // top: 62% !important;
          background: rgba(250, 250, 250, 0.39);
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
      background: #3f86ff;
      color: #fff;
      border-radius: 42px;
      margin: 0 auto;
    }
  }
}

.choosetime {
  margin-left: 17px;
  // margin: 0px auto;
  margin-right: 13px;
  display: flex;
  justify-content: space-between;
  position: relative;

  span {
    font-size: 15px;
    color: #666666;
  }

  img {
    width: 12px;
    height: 12px;
    margin: 0;
    position: absolute;
    right: -20px;
    top: 25%;
    transform: translate(0, -30%);
  }
}

ul {
  margin-left: 13px;
  margin-right: 13px;

  display: flex;
  // border: 1px solid #b8b8b8;
  // margin: 0px auto;
  // padding-top: 21px;
  text-align: center;
  line-height: 33px;
  background: #fff;
  border-right: none;
  // border-right: 1px solid #b8b8b8;
  // border-left: 1px solid #b8b8b8;
  border-radius: 4px;
  // margin-bottom: 21px;

  .ul-li {
    width: 50px;
    height: 33px;
    font-size: 13px;
    color: #666666;
    border: 1px solid #b8b8b8;
    // border-left: none;
  }

  .firstli {
    //border-left: 1px solid #b8b8b8;
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

.ckqj {
  display: flex;
  position: relative;
  color: #999999;
  font-size: 14px;

  .qvjian {
    height: 12px;
    width: 13px;
    position: absolute;
    top: 50%;
    left: 17px;
    transform: translate(0, -50%);
  }

  .ck {
    margin-left: 17px;
  }

  .jzz {
    margin-left: 100px;
  }

  .mbz {
    margin-left: 19px;
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
</style>
