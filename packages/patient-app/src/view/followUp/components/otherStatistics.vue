<template>
  <div class="content">
    <!--标题-->
    <cell style=" border-bottom: 1px solid #E8E8E8">
      <div slot="title" style="display: flex; align-items: center;">
        <div style="width: 4px; height: 16px; background: #3F86FF; border-radius: 6px"></div>
        <span style="font-size: 20px; color: #333333; margin-left: 5px">{{ dataInfo.planName }}</span>
      </div>
    </cell>
    <!--推送次数-->
    <div
      style="height: 50px; background: #F5F5F5; border-radius: 6px; margin-top: 10px; display: flex; align-items: center">
      <img :src="require('@/assets/my/follow_task_statistics_push_icon.png')"
           style="width: 20px; height: 20px; margin: 0px 5px 0px 15px;"/>
      <span style="color: #6B6B6B; font-size: 16px">推送次数：</span>
      <span style="color: #3F86FF; font-size: 18px; font-weight: bolder">{{ dataInfo.pushNumber }}</span>
    </div>
    <div style="height: 280px" id="push"></div>
    <loading-dialog ref="loading"></loading-dialog>
  </div>
</template>

<script>
import Api from '@/api/followUp.js'
import loadingDialog from './loadingDialog'
export default {
  name: "otherStatistics",
  props: {
    planId: {
      type: String,
      default() {
        return ''
      }
    },
  },
  components: {
    loadingDialog: () => import('./loadingDialog'),
  },
  watch: {
    planId: {
      handler: function (val, old) {
        console.log(val)
        this.patientPlanStatistics(val)
      }
    }
  },
  data() {
    return {
      dataInfo: {},
    }
  },
  methods: {
    patientPlanStatistics(planId) {
      if (!planId) {
        return
      }
      this.$refs.loading.openLoading()
      Api.patientPlanStatistics(planId)
        .then(res => {
          this.dataInfo = res.data.data
          this.pushChart()
        })
    },
    //设置推送饼图
    pushChart() {
      let option = {
        title: {
          text: '完成率',
          left: 'center',
          bottom: '38%',
          textStyle: {
            color: '#999',
            fontWeight: 'normal',
            fontSize: 13,
            lineHeight: 10
          },
          subtext:this.dataInfo.completeRate+'%',
          //副标题文本样式
          subtextStyle: {
            fontSize: 24,
            lineHeight: 20,
            color: '#3F86FF',
            fontWeight: 600
          }
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: ['38%', '49%'],
            hoverAnimation:false,
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            emphasis: {
              disabled: true,
              focus: 'none'
            },
            data: [
              { value: 0,itemStyle:{color:'#f5f5f5',itemStyle:{borderWidth:100,borderColor:'#ffffff'}}}
            ]
          },
          {
            startAngle:90,
            stillShowZeroSum:false,
            hoverAnimation: false,
            name: 'Access From',
            type: 'pie',
            emphasis: {
              disabled: true,
              focus: 'none'
            },
            radius: ['38%', '49%'],
            label: {
              normal: {
                //下面三条语句设置了让文字显示在标线上
                formatter:function(params){
                  console.log(params)
                  return `{a|${params.name}}\n{b|${params.value}}`
                },
                rich:{
                  a:{
                    color:'#333333',
                    lineHeight: 20
                  },
                  b:{
                    color:'#999999',
                    lineHeight: 20
                  }
                },
                padding: [0,-40],
                alignTo: "labelLine",
                textStyle: {
                  color: "#000", // 改变标示文字的颜色
                },
              },
              show: true,
              position: 'outside',
            },
            labelLine:{//指示线样式设置
              normal: {
                length: 20,//设置指示线的长度
                length2: 100,
                lineStyle: {
                  color: "#EBEBEB"  // 设置标示线的颜色
                }
              }
            },
            data: [
              { value: this.dataInfo.completeNumber, name: '已完成', itemStyle:{
                  color: new echarts.graphic.LinearGradient(0.5, 0.5, 1, 0.5, [
                    {offset: 0, color: '#6EA8FF'},
                    {offset: 1, color: '#3F86FF'}
                  ]),borderRadius:50,onZeroAxisIndex:999} },
              { value: this.dataInfo.noCompleteNumber, name: '未完成', itemStyle:{color:'#f5f5f5'}}
            ]
          },
        ]
      };
      // 如果不等于0 切两个值相等 设置指示线长度
      if (this.dataInfo.pushNumber!==0&&this.dataInfo.completeNumber===this.dataInfo.noCompleteNumber){
        option.series[1].labelLine.normal.length=80
        option.series[1].labelLine.normal.length2=0
      }
      // 都等于0的情况
      if (this.dataInfo.pushNumber===0&&this.dataInfo.completeNumber===0){
        option.series[1].label={
          padding:[0,0],
          normal: {
            //下面三条语句设置了让文字显示在标线上
            formatter:function(params){
              return `{a|${params.name}} {b|${params.value}}`
            },
            rich:{
              a:{
                color:'#333333',
                lineHeight: 20
              },
              b:{
                color:'#999999',
                lineHeight: 20,
              }
            },
            padding: [0, 0],
            alignTo: "labelLine",
            textStyle: {
              color: "#000", // 改变标示文字的颜色
              width: 100
            },
          },
          textStyle: {
            color: "#000", // 改变标示文字的颜色
            width: 100
          }
        }
        option.series[1].labelLine.normal.length=40
        option.series[1].labelLine.normal.length2=40
      }
      var pushChart = echarts.init(document.getElementById("push"));
      // 绘制饼图
      console.log(option)
      pushChart.setOption(option);
      this.$refs.loading.cancelLoading()
    }
  }
}
</script>

<style lang="less" scoped>

.content {
  margin-top: 10px;
  background: white;
  padding: 0px 15px;
}

/deep/ .weui-cell {
  padding: 12px 10px 12px 0!important;
}

</style>
