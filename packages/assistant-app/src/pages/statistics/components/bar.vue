<template>
  <div>
    <div :id="'barChart'+indexNumber" style="width: 100%;height: 300px"></div>
    <div style="display: flex;align-items: center;justify-content: center">
      <div v-for="(item, index) in fanCharts" :key="index" style="display: flex;align-items: center" >
        <div :style="{background:item.color,width:'25px',height:'13px','border-radius':'2px'}"></div>
        <div style="font-size: 13px;color:#333333;margin-left: 5px;margin-right: 10px">
          {{item.name}}
        </div>
      </div>
    </div>
  </div>
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
    showNumber: {
      type: Boolean
    },
    showPercentage: {
      type: Boolean
    }
  },
  data () {
    return {
      fanCharts: [],
      option: {
        xAxis: {
          type: 'category',
          data: []
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
            type: 'bar',
            itemStyle: {}
          }
        ],
        grid: {
          left: 40,
          right: 0,
          top: 30,
          bottom: 30
        }
      }
    }
  },
  mounted () {
    this.getChartsData()
  },
  methods: {
    getChartsData () {
      let date = new Date()
      let year = date.getFullYear()
      let month = date.getMonth() + 1
      let day = date.getDate()
      month = (month > 9) ? month : ('0' + month)
      day = (day < 10) ? ('0' + day) : day
      this.chartsData.endTime = year + '-' + month + '-' + day
      this.chartsData.startTime = year + '-01-01'
      console.log('bar', this.chartsData)
      tenantDataStatistics(this.chartsData).then(res => {
        if (res.code === 0) {
          this.fanCharts = res.data.fanCharts
          console.log(res.data)
          res.data.fanCharts.forEach(item => {
            this.option.xAxis.data.push(item.name)
            this.option.series[0].data.push(item.proportion)
          })
          const that = this
          this.option.series[0].itemStyle.normal = {
            color: function (params) {
              // 注意，如果颜色太少的话，后面颜色不会自动循环，最好多定义几个颜色
              let colorList = []
              res.data.fanCharts.forEach(item => {
                colorList.push(item.color)
              })
              return colorList[params.dataIndex]
            },
            label: {
              show: true,
              position: 'top',
              formatter: function (option) {
                console.log(option)
                if (that.chartsData.showNumber && that.chartsData.showPercentage) {
                  return `${res.data.fanCharts[option.dataIndex].total}\n${option.data}%`
                } else if (that.chartsData.showNumber && !that.chartsData.showPercentage) {
                  return `${res.data.fanCharts[option.dataIndex].total}`
                } else if (!that.chartsData.showNumber && that.chartsData.showPercentage) {
                  return `${option.data}%`
                }
              }
            }
          }
          console.log(this.option, 123)
          let chartDom = document.getElementById('barChart' + this.indexNumber)
          let myChart = echarts.init(chartDom)
          this.option && myChart.setOption(this.option)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
