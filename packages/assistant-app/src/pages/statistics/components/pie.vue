<template>
  <div style="width: 100%;height: 400px" :id="'agePie'+indexNumber"/>
</template>
<script>
import * as echarts from 'echarts'
import { tenantDataStatistics } from '@/api/statisticsDisease'
export default {
  name: 'agePie',
  props: {
    indexNumber: {
      type: Number
    },
    chartsData: {
      type: Object,
      default: () => {
      }
    },
    titleText: {
      type: String
    },
    statisticsDataType: {
      type: String
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
      option: {
        color: [],
        graphic: {
          type: 'text',
          left: 'center',
          top: 'center',
          style: {
            text: '',
            textAlign: 'center',
            fontSize: '16px'
          }
        },
        legend: {
          y: 'bottom',
          x: 'center'
        },
        series: [
          {
            itemStyle: {
              borderWidth: 2, // 间距的宽度
              borderColor: '#fff', // 背景色
              label: {
                show: true,
                // {b} : \n {c} \n ({d}%)
                // formatter: '{b} : {c} ({d}%)' //带当前图例名 + 百分比
                formatter: '{d}% \n {b}' // 只要百分比
                // labelLine: {show: true}
              }
            },
            type: 'pie',
            radius: ['30%', '40%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center',
              normal: {
                show: true
              }
            },
            data: []
          }
        ]
      }
    }
  },
  mounted () {
    console.log('pie', this.chartsData)
    this.getChartsData()
    console.log(this.showNumber, this.showPercentage)
  },
  methods: {
    getChartsData () {
      tenantDataStatistics(this.chartsData).then(res => {
        if (res.code === 0) {
          res.data.fanCharts.forEach(item => {
            let arr = {
              value: item.total,
              name: item.name
            }
            this.option.color.push(item.color)
            this.option.series[0].data.push(arr)
          })
          const that = this
          this.option.series[0].label.normal.formatter = function (params) {
            if (that.chartsData.showNumber && that.chartsData.showPercentage) {
              return `${params.value}人 \n${params.percent ? params.percent : 0}%`
            } else if (that.chartsData.showNumber && !that.chartsData.showPercentage) {
              return `${params.value}人`
            } else if (!that.chartsData.showNumber && that.chartsData.showPercentage) {
              return `${params.percent ? params.percent : 0}%`
            } else {
              return params.name
            }
          }
          this.option.graphic.style.text = this.titleText + this.statisticsDataType
          console.log(this.option.series[0].data)
          let chartDom = document.getElementById('agePie' + this.indexNumber)
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
