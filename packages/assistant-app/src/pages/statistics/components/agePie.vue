<template>
<div style="width: 100%;height: 400px" id="agePie"/>
</template>
<script>
import * as echarts from 'echarts'

export default {
  name: 'agePie',
  data () {
    return {
      option: {
        color: ['#77AAFF', '#21C7C7', '#FFA902', '#FF6F01', '#FF94AA'],
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
                show: true
                // {b} : \n {c} \n ({d}%)
                // formatter: '{b} : {c} ({d}%)' //带当前图例名 + 百分比
              },
              labelLine: {show: true}
            },
            type: 'pie',
            radius: ['20%', '40%'],
            data: []
          }
        ]
      }
    }
  },
  watch: {
    agePieData: {
      handler: function (val, old) {
        if (val) {
          this.getAgePieData()
        }
      }
    }
  },
  mounted () {
  },
  props: {
    agePieData: {
      type: Array
    }
  },
  methods: {
    /**
     * 给饼图赋值
     */
    getAgePieData () {
      console.log(this.agePieData)
      this.agePieData.forEach(item => {
        let obj = {}
        obj = {
          value: item.count,
          name: item.name
        }
        this.option.series[0].data.push(obj)
      })
      console.log(456)
      this.option.series[0].itemStyle = {
        normal: {
          label: {
            formatter: function (params) {
              console.log(params)
              if (params.percent === undefined) {
                return '0%'
              } else {
                return params.percent + '%'
              }
            }
          }
        }
      }
      let chartDom = document.getElementById('agePie')
      let myChart = echarts.init(chartDom)
      this.option && myChart.setOption(this.option)
    }
  }
}
</script>

<style scoped>

</style>
