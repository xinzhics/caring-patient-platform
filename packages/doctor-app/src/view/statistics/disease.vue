<template>
  <div @click="() => {
    if (showPopover) {
      showPopover = false
    }
  }">
    <div id="container" v-for="(item, pos) in list" :key="pos"
         style="padding: 10px; background: white; margin-top: 10px">
      <div class="title">
        <div class="titleBar">{{ item.formFieldLabel }}{{ getTitle(item) }}</div>
        <div class="titlechoose" @click.stop="selectDate(pos)" ref="choose" v-if="item.chartType === 'lineChart' || item.statisticsDataType === 'return_rate'">
          <span style="margin-right: 5px">{{ getTime(item) }}</span>
          <img :src="require('@/assets/my/statistics_calendar_icon.png')" width="15px" height="15px"/>
        </div>
        <div class="dataTime" v-if="position === pos && showPopover">
          <van-tree-select
              ref="tree"
              :items="actions"
              @click.stop.native="()=> {}"
              @click-item="(data) => clickItem(position, data)"
              @click-nav="(index) => clickNav(position, index)"
              :active-id.sync="activeId"
              :main-active-index.sync="activeIndex"
          />
        </div>

      </div>
      <div style="border-bottom: #337EFF solid 1px; width: 80px"></div>
      <div class="content" v-if="item.chartType === 'Diagnose'" style="padding-top: 15px; padding-left: 10px; padding-right: 10px;">
        <div v-for="(item, key) in item.fanCharts" :key="key" style="margin-bottom: 10px;">
          <div style="display: flex; justify-content: space-between; align-items: center; font-size: 13px;">
            <div style="color: #666666">{{item.name}}</div>
            <div>{{item.total}}人({{item.proportion}}%)</div>
          </div>

          <x-progress :percent="item.proportion" :show-cancel="false" style="margin-top: 5px; margin-bottom: 15px;"></x-progress>
        </div>


      </div>
      <div class="content" v-show="item.chartType !== 'Diagnose'">
        <div :id="'myChart'+pos" style="min-height:230px;"></div>
        <div v-if="item.chartType === 'fanChart'" style="padding-left: 15px; padding-right: 15px;
         font-size: 13px; margin-bottom: 10px; color: #444444;">
          <van-row style="margin-bottom: 10px; margin-top: 10px;">
            <van-col span="14">{{item.formFieldLabel}}</van-col>
            <van-col span="5" v-if="item.showPercentage">
              <div style="text-align: center">占比</div>
            </van-col>
            <van-col span="5" v-if="item.showNumber">
              <div style="text-align: center">人数</div>
            </van-col>
          </van-row>
          <div v-for="(fItem, key) in item.fanCharts" :key="key" style="margin-top: 5px; font-size: 12px">
            <van-row>
              <van-col span="14"><span>{{ fItem.name }}</span><span
                  style="color: #666666">{{ '  <' + fItem.maxValue + '-' + fItem.minValue }}</span></van-col>
              <van-col span="5" v-if="item.showPercentage">
                <div style="text-align: center">{{ fItem.proportion }}%</div>
              </van-col>
              <van-col span="5" v-if="item.showNumber">
                <div style="text-align: center">{{ fItem.total }}</div>
              </van-col>
            </van-row>
          </div>
        </div>
      </div>
      <div v-if="item.chartType === 'histogram'" style="display: flex;align-items: center;justify-content: center">
        <div v-for="(item,index) in item.fanCharts" style="display: flex;align-items: center">
          <div :style="{background:item.color,width:'25px',height:'13px','border-radius':'2px'}" ></div>
          <div style="font-size: 13px;color:#333333;margin-left: 5px;margin-right: 10px">
            {{item.name}}
          </div>
        </div>
      </div>
    </div>

  </div>

</template>

<script>
import ContentApi from "@/api/Content";
import {TreeSelect, Toast} from 'vant';
import { XProgress } from 'vux'
import doctorApi from '@/api/doctor.js'

export default {
  name: "BasicsStatistics",
  components: {
    [TreeSelect.name]: TreeSelect,
    [Toast.name]: Toast,
    XProgress
  },
  data() {
    return {
      list: [],
      actions: [],
      showPopover: false,
      position: 0,
      activeId: -1,
      activeIndex: 0,
      color: ['#77AAFF','#21C7C7','#FFA902','#FF6F01','#FF94AA','#FF00AA','#F9F4B5','#F5F94A','#CC94AA'],
      screenWidth: document.body.clientWidth,     // 屏幕宽
      screenHeight: document.body.clientHeight,     // 屏幕高
      toast: null,
      changecolor: 0,
    }
  },
  created() {
    ContentApi.getByCode()
        .then(res => {
          if (res.data.code === 0) {
            this.actions = []
            let projectYear = Number(moment(res.data.data.createTime).format("yyyy"));
            let year = Number(moment(new Date()).format("yyyy")) + 1;
            for (let i = projectYear; i < year; i++) {
              this.actions.push({
                text: i + '全年',
                children: [
                  {text: i + '全年', id: 0,},
                  {text: '1月', id: 1,},
                  {text: '2月', id: 2,},
                  {text: '3月', id: 3,},
                  {text: '4月', id: 4,},
                  {text: '5月', id: 5,},
                  {text: '6月', id: 6,},
                  {text: '7月', id: 7,},
                  {text: '8月', id: 8,},
                  {text: '9月', id: 9,},
                  {text: '10月', id: 10,},
                  {text: '11月', id: 11,},
                  {text: '12月', id: 12,},
                ]
              })
            }
          }
        })
  },
  methods: {
    selectDate(pos) {
      this.activeId = -1
      this.activeIndex = 0
      this.showPopover = !this.showPopover
      this.position = pos
    },
    clickNav(pos, index) {
      this.activeIndex = index
      this.activeId = -1
    },
    clickItem(pos, data) {
      this.showPopover = false
      let year = this.actions[this.activeIndex].text.substring(0, this.actions[this.activeIndex].text.length - 2)
      let month = ''
      if (data.id == 0) {
        this.list[pos].chooseType = 'year'
        this.list[pos].startTime = moment(year).startOf('year').format('YYYY-MM-DD')
        this.list[pos].endTime = moment(year).endOf('year').format('YYYY-MM-DD')
      } else {
        this.list[pos].chooseType = 'month'
        month = data.id < 10 ? '0' + data.id : data.id
        this.list[pos].startTime = moment(year + "-" + month).startOf('month').format('YYYY-MM-DD')
        this.list[pos].endTime = moment(year + "-" + month).endOf('month').format('YYYY-MM-DD')
      }

      ContentApi.getStatisticsChartDetail(this.list[pos])
          .then(res => {
            if (res.data.code === 0) {
              let myChart = echarts.init(document.getElementById('myChart' + pos))
              let option = {
                xAxis: {
                  type: 'category',
                  data: []
                },
                yAxis: {
                  type: 'value'
                },
                series: []
              };

              if (res.data.data.xdata) {
                res.data.data.xdata.forEach(item => {
                  if (this.list[pos].chooseType == 'month') {
                    option.xAxis.data.push(moment(item).format("D"))
                  } else {
                    option.xAxis.data.push(moment(item).format("M")+'月')
                  }
                })
              }
              if (res.data.data.ydata) {
                res.data.data.ydata.forEach(item => {
                  let seriesData = {
                    data: item.yData,
                    type: 'line',
                    smooth: true,
                    name: item.name,
                  }
                  option.series.push(seriesData)
                })
              }

              this.showPopover = false
              myChart.setOption(option);
            }
          })
    },
    getProportion(fanCharts, proportion) {
      let fanChartsCount = 0;
      fanCharts.forEach(item => {
        fanChartsCount = fanChartsCount + item.proportion
      })
      fanChartsCount = 15
      proportion = 10
      if (!proportion || fanChartsCount == 0 || proportion == 0) {
        return 0
      } else {
        return parseInt(proportion / fanChartsCount * 100) + '%'
      }

    },
    getTime(item) {
      if (item.chooseType == 'month') {
        return moment(item.startTime).format('yyyy-MM') + '月'
      } else {
        return moment(item.startTime).format('yyyy') + '年全年'
      }
    },
    tenantStatisticsChartList() {
      this.toast = Toast.loading({
        message: '加载中...',
        forbidClick: true,
        loadingType: 'spinner',
      })
      this.list = []
      ContentApi.tenantStatisticsChartList()
          .then(res => {
            if (res.data && res.data.code == 0) {
              this.getInfo(res.data.data)
            }
          })
    },
    getInfo(chartData) {
      const params = {
        formEnum: 'HEALTH_RECORD',
        patientId: 0,
      }
      doctorApi.gethealthform(params).then((res) => {
        if (res.data.code === 0) {
          res.data.data.fieldList.forEach(item => {
            if (item.exactType === 'Diagnose') {
              ContentApi.diagnosticTypeStatisticsDoctor()
                  .then(res => {
                    this.toast && this.toast.clear()
                    if (res.data.code === 0) {
                      let fanCharts = []
                      if (res.data.data.ydataList && res.data.data.ydataList.length > 0) {
                        for (let i = 0; i < res.data.data.ydataList[0].yData.length; i++) {
                          fanCharts.push({
                            proportion: res.data.data.ydataList[1].yData[i],
                            total: res.data.data.ydataList[0].yData[i],
                            name: res.data.data.xname[i],
                            color: this.color[i],
                          })
                        }
                      }

                      let value = {
                        formFieldLabel: item.label,
                        chartType: 'Diagnose',
                        showNumber: true,
                        showPercentage: true,
                        fanCharts: fanCharts,
                      }

                      this.list.push(value)
                      chartData.forEach(item => {
                        this.list.push({...item})
                      })

                      //延迟保证echart刷新
                      setTimeout(() => {
                        for (let i = 0; i < this.list.length; i++) {

                          if (this.list[i].chartType === 'fanChart') {//扇形图
                            this.drawPie(i)
                          } else if (this.list[i].chartType === 'histogram' || this.list[i].chartType === 'Diagnose') {//柱状图
                            this.drawBar(i)
                          } else {
                            this.drawLine(i)
                          }
                        }
                      }, 1000)
                    }
                  })
            }
          })

        }
      })
    },
    drawLine(pos) {
      this.list[pos].chooseType = 'year'
      let param = {
        ...this.list[pos],
        startTime: moment(new Date()).format("yyyy") + '-01-01',
        endTime: moment(new Date()).format("yyyy") + '-12-31',
      }
      ContentApi.getStatisticsChartDetail(param)
          .then(res => {
            if (res.data.code === 0) {
              let myChart = echarts.init(document.getElementById('myChart' + pos))
              let option = {
                xAxis: {
                  type: 'category',
                  data: []
                },
                yAxis: {
                  type: 'value',
                  axisLabel: {
                    //y轴设置为%
                    show: true,
                    formatter: '{value}%',
                    fontSize: 10, // 增加字体大小
                  },
                  max: 100, //最大值
                  min: 0
                },
                dataZoom: { // 放大和缩放
                  type: 'inside'
                },
                tooltip: {
                  // 鼠标悬浮提示框显示Y 轴数据
                  trigger: 'axis',
                  backgroundColor: 'rgba(32, 33, 36,.7)',
                  borderColor: 'rgba(32, 33, 36,0.20)',
                  borderWidth: 1,
                  textStyle: {
                    // 文字提示样式
                    color: '#fff',
                    fontSize: '12'
                  },
                  formatter: function (params) {
                    let name = `${params[0].name.indexOf('月') === -1 ? params[0].name + '日' : params[0].name}<br/>`
                    var content = ''
                    for (let index = 0; index < res.data.data.ydata.length; index++) {
                      let yydata = res.data.data.ydata[index]
                      content += `${params[0].marker}<span></span><span"></span><span>${yydata.name}: ${yydata.yData[params[0].dataIndex] ? yydata.yData[params[0].dataIndex] : 0}${index == res.data.data.ydata.length - 1 ? '%' : '人'}</span><br>`}
                    return name + content
                  }
                },
                series: []
              };
              res.data.data.xdata.forEach(item => {
                option.xAxis.data.push(moment(item).format("M") + '月')
              })

              for (let i = 0; i < res.data.data.ydata.length; i++) {
                if (i == res.data.data.ydata.length - 1) {
                  let seriesData = {
                    data: res.data.data.ydata[i].yData,
                    type: 'line',
                    smooth: true,
                    name: res.data.data.ydata[i].name,
                  }
                  option.series.push(seriesData)
                }
              }
              myChart.setOption(option);
            }
          })
    },
    drawBar(pos) {
      if (this.list[pos].chartType === 'Diagnose') {
        let myChart = echarts.init(document.getElementById('myChart' + pos))
        let option = {
          xAxis: {
            type: 'category',
            data: [],
            axisLabel: {
              color: '#333',
              //  让x轴文字方向为竖向
              interval: 0,
              formatter: function(value) {
                return value.split('').join('\n')
              }
            }
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              //y轴设置为%
              show: true,
              formatter: '{value}%',
              color: '#333', // 设置标签文本颜色
              fontSize: 12, // 设置标签字体大小
            },
            max: 100, //最大值
            min: 0
          },
          series: [{
            type: 'bar',
            data: [],
          }]
        }
        if (this.list[pos].fanCharts) {
          let that = this
          this.list[pos].fanCharts.forEach(item => {
            option.series[0].data.push({
              value: item.proportion,
              itemStyle: {
                color: item.color
              },
              label: {
                normal: {
                  show: true,
                  fontSize: 12,
                  fontWeight: 'bold',
                  // color: '#46F7FB',
                  position: 'top',
                  // 自定义顶部文字写判断
                  formatter: function (val) {
                    if (that.list[pos].showNumber && that.list[pos].showPercentage) {
                      let str = item.total + '\r\n' + item.proportion + '%'
                      return `${str}`
                    } else if (that.list[pos].showNumber) {
                      return `${item.total}`
                    } else if (that.list[pos].showPercentage) {
                      return `${item.proportion}%`
                    }

                  }
                }
              }
            })
            option.xAxis.data.push(item.name)
          })
        }
        myChart.setOption(option);
      }else {
        ContentApi.getStatisticsChartDetail(this.list[pos])
            .then(res => {
              if (res.data.code === 0) {
                this.list[pos].fanCharts=res.data.data.fanCharts
                let myChart = echarts.init(document.getElementById('myChart' + pos))
                let option = {
                  xAxis: {
                    type: 'category',
                    data: [],
                    axisLabel: {
                      color: '#333',
                      //  让x轴文字方向为竖向
                      interval: 0,
                      formatter: function(value) {
                        return value.split('').join('\n')
                      }
                    }
                  },
                  yAxis: {
                    type: 'value',
                    axisLabel: {
                      //y轴设置为%
                      show: true,
                      formatter: '{value}%',
                      fontSize: 10, // 设置标签字体大小
                    },
                    max: 100, //最大值
                    min: 0
                  },
                  series: [{
                    type: 'bar',
                    data: [],
                  }]
                }
                if (res.data.data.fanCharts) {
                  let that = this
                  res.data.data.fanCharts.forEach(item => {
                    option.series[0].data.push({
                      value: item.proportion,
                      itemStyle: {
                        color: item.color
                      },
                      label: {
                        normal: {
                          show: true,
                          fontSize: 10,
                          fontWeight: 'bold',
                          // color: '#46F7FB',
                          position: 'top',
                          // 自定义顶部文字写判断
                          formatter: function (val) {
                            if (that.list[pos].showNumber && that.list[pos].showPercentage) {
                              let str = item.total + '\r\n' + item.proportion + '%'
                              return `${str}`
                            } else if (that.list[pos].showNumber) {
                              return `${item.total}`
                            } else if (that.list[pos].showPercentage) {
                              return `${item.proportion}%`
                            }

                          }
                        }
                      }
                    })
                    option.xAxis.data.push(item.name)
                  })
                }
                myChart.setOption(option);
              }
            })
      }

    },
    drawPie(pos) {
      let myChart = echarts.init(document.getElementById('myChart' + pos))
      ContentApi.getStatisticsChartDetail(this.list[pos])
          .then(res => {
            if (res.data.code === 0) {
              let option = {
                tooltip: {
                  trigger: 'item',
                  formatter: function (params) {
                    return params.name + "<br/>" + params.value + "%";
                  }
                },
                legend: {
                  top: 'bottom'
                },

                series: [
                  {
                    type: 'pie',
                    radius: ['40%', '70%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                      borderRadius: 10,
                      borderColor: '#fff',
                      borderWidth: 2,
                      normal:{
                        color: function(params) {
                          return res.data.data.fanCharts[params.dataIndex].color
                        }
                      }
                    },
                    label: {
                      show: false,
                      position: 'center'
                    },
                    emphasis: {
                      label: {
                        show: true,
                        fontSize: '12',
                        fontWeight: 'bold'
                      }
                    },
                    labelLine: {
                      show: false
                    },
                    data: []
                  }
                ]
              }
              if (res.data.data) {
                this.list[pos].fanCharts = res.data.data.fanCharts
                this.$forceUpdate()
                res.data.data.fanCharts
                    .forEach(item => {
                      option.series[0].data.push({
                        value: item.proportion,
                        name: item.name,
                      })
                    })
              }
              myChart.setOption(option);
            }
          })
    },
    getTitle(item) {
      if (item.statisticsDataType === 'compliance_rate') {
        return '达标率'
      } else if (item.statisticsDataType === 'base_line_value') {
        return '基线值'
      } else {
        return ''
      }
    }
  }
}
</script>

<style lang="less" scoped>
.title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  background: white;
  position: relative;

  .titleBar {
    min-width: 60px;
    font-size: 14px;
    color: #333333;
  }

  .titlechoose {
    border: #D9D9D9 solid 1px;
    display: flex;
    align-items: center;
    padding: 5px 10px 5px 10px;
    color: #666666;
    font-size: 12px;
  }
}

.content {
  border-top: #D9D9D9 solid 1px;
}

.dataTime {
  overflow: auto;
  padding-top: 16px;
  box-shadow: -5px 0px 8px rgba(0, 0, 0, 0.15);
  height: 288px;
  z-index: 999;
  position: absolute;
  right: 5px;
  top: 40px;
  padding: 0;
  background: #fff;
  li {
    list-style: none;
    line-height: 30px;
    width: 100px;
    height: 30px;
    text-align: left;
    padding-left: 14px;
    background: #fff;
  }
}

.ul2 {
  padding-top: 16px;
  height: 288px;
  z-index: 999999;
  position: absolute;
  right: 50px;
  top: 40px;
  padding: 0;
  overflow: auto;
  box-shadow: 5px 0px 8px rgba(0, 0, 0, 0.15);
  border-left: 1px solid #ccc;
  li {
    list-style: none;
    width: 100px;
    height: 30px;
    line-height: 30px;
    // border: 1px solid #ccc;
    border-top: none;
    // border-left: none;
    text-align: center;
    background: #fff;
  }
  li:hover {
    color: #1890ff;
    background: #f6fbff;
    border: none;
  }
}

/deep/ .van-sidebar-item__text {
  font-size: 12px;
}

/deep/ .van-tree-select {
  font-size: 12px;
}

/deep/ .weui-progress__inner-bar{
  background-color: #337EFF;
}
</style>
