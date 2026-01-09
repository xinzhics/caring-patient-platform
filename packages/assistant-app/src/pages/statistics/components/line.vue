<template>
  <div :id="'main'+indexNumber" style="width: 100%;height: 300px"></div>
</template>

<script>
import * as echarts from 'echarts'
import { tenantDataStatistics } from '@/api/statisticsDisease'
export default {
  props: {
    indexNumber: {
      type: Number
    },
    chartsData: {
      type: Object,
      default: () => {
      }
    },
    yearData: {
      type: String
    }
  },
  watch: {
    yearData: {
      handler: function (val, old) {
        if (val) {
          let reg = /\s+/g
          val = val.replace(reg, '')
          if (val.slice(-2) === '全年') {
            let year = val.substring(0, 4)
            this.chartsData.endTime = year + '-12-31'
            this.chartsData.startTime = year + '-01-01'
          } else {
            let month = 0 + val.substring(5, 6)
            // 取当前的年份
            let year = val.substring(0, 4)
            let startDate = new Date(year, month, 1)
            let currentdate = `${year}-${month}-${new Date(startDate.getTime() - 1000 * 60 * 60 * 24).getDate()}`
            console.log(currentdate)
            this.chartsData.endTime = currentdate
            this.chartsData.startTime = year + '-' + month + '-01'
          }
          this.getChartsData()
        }
      },
      deep: true
    }
  },
  data () {
    return {
      option: {
        tooltip: {
          extraCssText: 'z-index:10',
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: [],
          boundaryGap: false,
          axisLabel: {
            type: 'category'
          }
        },
        grid: {
          left: 40,
          right: 20,
          top: 30,
          bottom: 30
        },
        yAxis: {
          type: 'value',
          min: 0, // 最小百分比
          max: 100,
          axisLabel: {
            show: true,
            formatter: '{value}%' // y轴数值，带百分号
          }
        },
        series: [
          {
            data: [],
            type: 'line'
          }
        ]
      }
    }
  },
  mounted () {
    let date = new Date()
    let year = date.getFullYear()
    let month = 12
    month = (month > 9) ? month : ('0' + month)
    this.chartsData.endTime = `${year}-${month}-31`
    this.chartsData.startTime = year + '-01-01'
    this.getChartsData()
  },
  methods: {
    /**
     * 请求图标数据并画图
     */
    getChartsData () {
      tenantDataStatistics(this.chartsData).then(res => {
        console.log(res)
        if (res.code === 0) {
          this.getXdata(res.data.xdata)
          this.option.series[0].data = res.data.ydata[res.data.ydata.length - 1].yData
          this.option.tooltip.formatter = function (params) {
            let name = `${params[0].name.indexOf('月') === -1 ? params[0].name + '日' : params[0].name}<br/>`
            var content = ''
            for (let index = 0; index < res.data.ydata.length; index++) {
              let yydata = res.data.ydata[index]
              content += `${params[0].marker}<span></span><span"></span><span>${yydata.name}: ${yydata.yData[params[0].dataIndex] ? yydata.yData[params[0].dataIndex] : 0}${index === res.data.ydata.length - 1 ? '%' : '人'}</span><br>`
            }
            return name + content
          }
          let chartDom = document.getElementById('main' + this.indexNumber)
          let myChart = echarts.init(chartDom)
          this.option && myChart.setOption(this.option)
        }
      })
    },
    /**
     * 给图表横坐标赋值
     * @param data
     */
    getXdata (data) {
      console.log(data)
      this.option.xAxis.data = []
      data.forEach(item => {
        if (data.length <= 12) {
          this.option.xAxis.data.push(item.substr(5, 2) + '月')
        } else {
          this.option.xAxis.data.push(item.substr(8, 2))
        }
      })
      console.log(this.option.xAxis.data)
    }
  }
}
</script>

<style scoped>

</style>
