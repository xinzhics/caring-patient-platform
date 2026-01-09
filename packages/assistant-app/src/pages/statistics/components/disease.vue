<template>
<div class="main">
  <div style="height: 13px;background: #F5F5F5"></div>
  <!--  诊断类型-->
  <div class="box">
    <div style="display: flex;align-items: center;padding-bottom: 18px;border-bottom: 1px solid #EEEEEE">
      <div style="width: 2px;height: 13px;background: #337EFF;border-radius: 4px;margin-right: 8px"></div>
      <div>{{title}}</div>
    </div>
    <div v-for="(item, index) in diagnosticTypeList.xname" :key="index">
      <div style="display: flex;justify-content: space-between;align-items: center;margin-top: 22px">
        <div style="font-size: 13px;color: #999999">{{ item }}</div>
        <div style="font-size: 12px;color: #333333">{{ diagnosticTypeList.ydataList[0].yData[index] }}人({{ diagnosticTypeList.ydataList[1].yData[index] }}%)</div>
      </div>
      <div style="margin-top: 9px">
        <van-progress :show-pivot="false" :style="{width: srceenWidth}" :percentage="diagnosticTypeList.ydataList[1].yData[index]" stroke-width="8"/>
      </div>
    </div>
  </div>
  <div>
    <!--  复诊率-->
    <div v-for="(item,index) in tenantStatisticsChartListData" :key="index" style="padding: 17px 14px;background: #FFFFFF;margin-top: 13px">
      <div style="display: flex;align-items: center;padding-bottom: 18px;border-bottom: 1px solid #EEEEEE;justify-content: space-between">
        <div style="display: flex;align-items: center;">
          <div style="width: 2px;height: 13px;background: #337EFF;border-radius: 4px;margin-right: 8px"></div>
          <div>{{ item.formFieldLabel }}{{item.statisticsDataType==='compliance_rate'?'达标率':item.statisticsDataType==='base_line_value'?'基线值':''}}</div>
        </div>
        <div @click="showList(index)" v-if="item.statisticsDataType==='return_rate'||item.statisticsDataType==='compliance_rate'" style="padding: 9px 12px 9px 23px;border: 1px solid #D9D9D9;border-radius: 2px;">
          <span style="font-size: 14px">{{item.yearData}}</span>
          <van-icon size="14px" name="notes-o" />
        </div>
      </div>
        <div v-if="item.statisticsDataType==='return_rate'||item.statisticsDataType==='compliance_rate'" class="dataTime">
          <van-tree-select
              v-if="showDatelist&&dateListIndex===index"
              ref="tree"
              :items="actions"
              @click.stop.native="()=> {}"
              @click-item="(data) => clickItem(data)"
              :active-id.sync="activeId"
              :main-active-index.sync="activeIndex"
          />
      </div>
      <div>
        <lineChart v-if="item.formFieldLabel==='复诊率'||item.chartType==='lineChart'" :yearData="dateListIndex===index&&!showDatelist?item.yearData:''" :chartsData="item" :indexNumber="index" ></lineChart>
        <barChart v-if="item.chartType==='histogram'" :chartsData="item" :showNumber="item.showNumber" :showPercentage="item.showPercentage" :indexNumber="index" ></barChart>
        <pieChart v-if="item.chartType==='fanChart'" :titleText="item.formFieldLabel" :statisticsDataType="item.statisticsDataType==='compliance_rate'?'达标率':item.statisticsDataType==='base_line_value'?'基线值':''" :chartsData="item" :indexNumber="index" ></pieChart>
      </div>
    </div>
  </div>

</div>
</template>

<script>
import {diagnosticTypeStatisticsNursing, tenantStatisticsChartList} from '@/api/statisticsDisease'
import { getHealthOrBaseInfoForm } from '@/api/formApi'
import Vue from 'vue'
import moment from 'moment'
import {Progress, TreeSelect} from 'vant'
import lineChart from './line'
import barChart from './bar'
import pieChart from './pie'
Vue.use(Progress)
Vue.use(TreeSelect)
export default {
  name: 'disease',
  components: {
    lineChart, barChart, pieChart
  },
  data () {
    return {
      nursingId: localStorage.getItem('caringNursingId'),
      diagnosticTypeList: [], // 诊断类型数据
      tenantStatisticsChartListData: [], // 项目配置统计图数据
      actions: [],
      activeId: -1,
      activeIndex: 0,
      yearData: '',
      showDatelist: false, // 显示折线图时间列表
      dateListIndex: undefined,
      title: '诊断类型',
      srceenWidth: (window.innerWidth - 34) + 'px'
    }
  },
  created () {
    this.getDiagnosticTypeList(this.nursingId)
    this.getTenantStatisticsChartList()
    this.getHealthOrBaseInfoForm()
    let date = new Date()
    let year = date.getFullYear()
    this.yearData = `${year}全年`
  },
  mounted () {
    this.$forceUpdate()
  },
  methods: {
    getHealthOrBaseInfoForm () {
      getHealthOrBaseInfoForm('0', 'HEALTH_RECORD')
        .then(res => {
          res.data.fieldList.forEach(item => {
            if (item.exactType === 'Diagnose') {
              if (item.label) {
                this.title = item.label
              }
            }
          })
        })
    },
    showList (index) {
      this.dateListIndex = index
      this.showDatelist = !this.showDatelist
    },
    /**
     * 点月份
     * @param data 该点击月份的数据
     */
    clickItem (data) {
      this.showDatelist = false
      this.oldYearData = this.yearData
      console.log('clickItem', data)
      if (data.id === 0) {
        this.tenantStatisticsChartListData[this.dateListIndex].yearData = data.text
      } else {
        this.tenantStatisticsChartListData[this.dateListIndex].yearData = this.actions[this.activeIndex].text.substr(0, 4) + '年 ' + data.text
      }
    },
    /**
     * 获取诊断类型数据
     * @param data 医助id
     */
    getDiagnosticTypeList (data) {
      diagnosticTypeStatisticsNursing(data).then(res => {
        if (res.code === 0) {
          console.log('=======', res.data)
          this.diagnosticTypeList = res.data
        }
      })
    },
    /**
     *  项目配置统计图的顺序类型宽度
     */
    getTenantStatisticsChartList () {
      tenantStatisticsChartList().then(res => {
        if (res.code === 0) {
          this.tenantStatisticsChartListData = res.data
          this.tenantStatisticsChartListData.forEach(item => {
            item.yearData = this.yearData
          })
          this.set()
        }
      })
    },
    getYear (data) {
      if (data === undefined) {
        let date = new Date()
        let year = date.getFullYear()
        return year + '全年'
      }
    },
    set () {
      let data = '1990-01-01 00:00:00'
      this.actions = []
      let projectYear = Number(moment(data).format('yyyy'))
      let year = Number(moment(new Date()).format('yyyy'))
      console.log('data', data, 'projectYear', projectYear, 'year', year)
      for (let i = year; i > projectYear - 1; i--) {
        this.actions.push({
          text: i + '全年',
          children: [
            {text: i + '全年', id: 0},
            {text: '1月', id: 1},
            {text: '2月', id: 2},
            {text: '3月', id: 3},
            {text: '4月', id: 4},
            {text: '5月', id: 5},
            {text: '6月', id: 6},
            {text: '7月', id: 7},
            {text: '8月', id: 8},
            {text: '9月', id: 9},
            {text: '10月', id: 10},
            {text: '11月', id: 11},
            {text: '12月', id: 12}
          ]
        })
      }
    }
  }
}
</script>

<style scoped lang="less">
.dataTime{
  position: relative;
}

.main {
  .box {
    padding: 17px 14px;
    background: #FFFFFF
  }
  ::-webkit-scrollbar {
    display: none !important;
    width: 0px !important;
    height: 0px !important;
  }

  ::v-deep .van-progress__portion {
    width: 100%;
  }
}

/deep/.van-tree-select {
  z-index: 99999;
  width: 200px;
  position: absolute;
  right: 0;
  box-shadow: 0px 1px 4px rgba(0,0,0,0.15);
  .van-sidebar{
    background: #FFFFFF;
    border-right: 1px solid #EBEBEB;
    .van-sidebar-item{
      text-align: center;
    }
  }
  .van-tree-select__content{
    background: #FFFFFF;
    flex: 1;
    width: 40px !important;
    .van-ellipsis{
      text-align: center;
      padding: 0 !important;
    }
  }
  .van-sidebar-item--select:before {
    background: #5995FD !important;
  }
  .van-tree-select__item--active {
    color: #5995FD !important;
  }
  .van-tree-select__selected{
    display: none;
  }
}
</style>
