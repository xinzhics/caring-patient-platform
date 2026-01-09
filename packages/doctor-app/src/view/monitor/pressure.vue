<template>
  <div class="main">
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '血压监测'"/>
    <div style="display: flex; background: #fff; padding: 15px">
      <div style="width: 50%">
        <div>
          <span style="font-size: 16px; font-weight: 500; color: #3D444D; height: 22px;">监测图表</span>
        </div>
        <div>
          <span style="font-size: 12px; font-weight: 400; color:#999; height: 17px">单位（mmHg）</span>
        </div>
      </div>
      <div style="width: 50%; text-align: right">
        <select class="caring-select" v-model="showLine" @change="showLineChange">
          <option v-for="(item, index) in showLineList" :key="index">{{item}}</option>
        </select>
      </div>
    </div>
    <div id="myChart" style="height:230px;background: #fff" :style="{width: echartWidth + 'px'}"></div>
    <div style="display: flex; padding: 10px 15px 0 15px; margin-top: 15px">
      <div style="width: 50%">
        <span style="font-size: 16px; font-weight: 500; color: #3D444D">监测记录</span>
      </div>
      <div style="width: 50%; text-align: right">
        <select class="caring-select" style="width: 120px;" v-model="monitorQueryDate" @change="monitorQueryDateChange">
          <option v-for="(item, index) in monitorQueryDateList" :key="index" :value="item.code">{{item.name}}</option>
        </select>
      </div>
    </div>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="padding-bottom: 60px">
      <van-list :finished="finished" finished-text="没有更多了" @load="onLoad">
        <div style="padding: 10px" v-for="(i,k) in listData" :key="i.id">
          <div style="background: white; padding: 10px 0" @click="goItem(i)">
            <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
              <div style="display: flex; align-items: center">
                <div class="caring-form-div-icon"></div>
                <span style="margin-left: 11px; color: #333333; font-size: 14px; font-weight: 500; ">监测时间</span>
                <span style="margin-left: 11px; font-size: 14px; font-weight: 400; color: #666">
                    {{ getDay(i.createTime) }}
                </span>
              </div>
              <div style="position: absolute; right: 20px" v-if="i.dataFeedBack">
                <div class="caring-form-result-status" :style="{background: getDataFeedBack(i.dataFeedBack, 'status') == '1' ? '#66E0A7' : '#FF5A5A'}">
                  {{getDataFeedBack(i.dataFeedBack, 'text')}}
                </div>
              </div>
            </div>
            <div style="padding: 10px 15px 15px 15px">
              <div style="border-radius: 10px; padding: 10px 6px;">
                <div v-for="(item, index) in getArray(i.jsonContent)" :key="index" style="padding: 5px 0">
                  <van-row v-if="getFormValue(item).type === 'text'">
                    <van-col span="12">
                      <div style="color: #333333; font-weight: 400; font-size: 14px">{{ item.label }}：</div>
                    </van-col>
                    <van-col span="12" style="display: flex">
                      <div style="font-weight: 500; font-size: 14px; width: 90%; text-align: right" :style="{ color: thisGetValueStatus(item, i.dataFeedBack) === 1 ? '#333333' : '#FF5A5A' }">
                        {{ getFormValue(item).value }}
                      </div>
                    </van-col>
                  </van-row>
                </div>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
    <div style="display: flex; justify-content: center; position: fixed; bottom: 20px; width: 100%;">
      <van-button type="primary" style="width: 90%; background: #67E0A7; border: none" round @click="addData()">添加记录</van-button>
    </div>
  </div>
</template>

<script>
import Api from '@/api/Content.js'
import {Constants} from '@/api/constants.js'
import {getValue, getValueStatus} from "@/components/utils/index"
import Vue from "vue";
import {Button} from "vant";

Vue.use(Button)

export default {
  components: {
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      pageTitle: '',
      bloodimg: require('@/assets/my/xueya.png'),
      datacharts:[],
      echartWidth: window.innerWidth,
      planId: this.$route.query.planId,
      listData: [],
      loading: false,
      finished: false,
      refreshing: false,
      current:1,
      myChart: undefined,
      showLine: '全部',
      showLineList: ['全部'],
      monitorQueryDate: 'CURRENT_DAY',
      monitorQueryDateList: [
        {code: 'ALL', name: '全部记录'},
        {code: 'CURRENT_DAY', name: '当日记录'},
        {code: 'day7', name: '近7天的记录'},
        {code: 'day30', name: '近30天的记录'},
        {code: 'moon3', name: '近3个月的记录'},
        {code: 'moon6', name: '近6个月的记录'},
        {code: 'year', name: '近1年的记录'}
      ],
      xdata: [],
      legend: []
    }
  },
  mounted() {
    this.getInfo()
    this.loadMyBloodPressureTrendData()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    onLoad() {
      if (this.refreshing) {
        this.list = []
        this.current = 1
        this.refreshing = false
      }
      this.getInfo()
    },
    onRefresh() {
      this.list = []
      this.finished = false
      this.onLoad()
    },
    monitorQueryDateChange() {
      this.finished = false
      this.refreshing = true
      this.list = []
      this.current = 1
      this.getInfo()
    },
    getFormValue(item) {
      return getValue(item)
    },
    thisGetValueStatus(formField, formDataFeedBack) {
      if (formDataFeedBack) {
        return getValueStatus(formField)
      }
      return 1
    },
    addData() {
      this.$router.push({path: '/monitor/pressureEditor', query: {planId: this.$route.query.planId}})
    },
    getDay(time) {
      return moment(time).format("YYYY-MM-DD HH:mm")
    },
    showLineChange() {
      console.log('showLineChange', this.showLine)
      if (this.showLine === '全部') {
        let ydata=[]   // y数据
        this.legend.forEach((item,index)=>{
          let arr={
            name: '',
            type: 'line',
            data: []
          }
          arr.name=item
          this.datacharts.forEach(el=>{
            if(arr.name===el.type){
              arr.data.push(el.value)
            }
          })
          ydata.push(arr)
        })
        const option = this.createOptions(this.legend, ydata)
        this.myChart.setOption(option, true)
      } else {
        let ydata=[]   // y数据
        let arr={
          name: this.showLine,
          type: 'line',
          data: []
        }
        this.datacharts.forEach(el=>{
          if(arr.name===el.type){
            arr.data.push(el.value)
          }
        })
        ydata.push(arr)
        const option = this.createOptions([this.showLine] ,ydata)
        this.myChart.setOption(option, true)
      }
    },
    /**
     * 获取数据反馈
     */
    getDataFeedBack(dataFeedBack, type) {
      const obj = JSON.parse(dataFeedBack)
      if (type === 'status') {
        return obj.normalAnomaly
      } else if (type === 'text') {
        return obj.normalAnomalyText
      }
    },
    getArray(json) {
      let list = []
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (jsonList[i].widgetType === Constants.CustomFormWidgetType.Number) {
          list.push(jsonList[i])
        }
        if (jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiLineText) {
          list.push(jsonList[i])
        }
      }
      return list
    },
    /**
     * 创建趋势图的 option
     *
     */
    createOptions(legend, ydata) {
      return {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: legend
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.xdata
        },
        grid: {
          left: '3%',
          bottom: '0%',
          containLabel: true
        },
        yAxis: {
          type: 'value',
          boundaryGap: [0, '40%'],
          max: 180,
        },
        series:ydata
      }
    },

    // 全部线
    makeLine() {
      let legend=[]  // 三条线的名字
      let xdata=[]   // x数据
      let ydata=[]   // y数据
      this.datacharts.forEach(item=>{
        legend.push(item.type)
        xdata.push(item.day)
      })
      function unique(arr) {
        return Array.from(new Set(arr));
      }
      legend=unique(legend)
      this.showLineList = this.showLineList.concat(legend)
      console.log('this.showLineList',  this.showLineList)
      xdata=unique(xdata)
      this.legend = legend
      let arr1=[]
      xdata.forEach(item=>{
        arr1.push(item.slice(5))
      })
      xdata=arr1
      this.xdata = xdata
      //每条线的数据
      this.legend.forEach((item,index)=>{
        let arr={
          name: '',
          type: 'line',
          data: []
        }
        arr.name=item
        this.datacharts.forEach(el=>{
          if(arr.name===el.type){
            arr.data.push(el.value)
          }
        })
        ydata.push(arr)
      })
      this.myChart = echarts.init(document.getElementById('myChart'))
      const option = this.createOptions(legend, ydata)
      option && this.myChart.setOption(option, true);
    },
    /**
     * 查询趋势图的数据
     */
    loadMyBloodPressureTrendData() {
      Api.loadMyBloodPressureTrendData({patientId: localStorage.getItem('patientId')}).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data && res.data.data.length > 1) {
            this.datacharts = res.data.data
            this.makeLine()
          }
        }
      })
    },
    getInfo() {
      const params = {
        "current": this.current,
        "model": {
          monitorQueryDate: this.monitorQueryDate,
          "businessId": this.planId,
          "userId": localStorage.getItem('patientId')
        },
        "size": 10,
        "sort": "createTime"
      }
      if (this.finished) {
        return
      }
      if (this.loading) {
        return
      }
      this.loading = true
      Api.monitorFormResult(params).then((res) => {
        if (res.data.code === 0) {
          if (this.current >= res.data.data.pages) {
            this.finished = true
            console.log('monitorFormResult', this.finished)
          }
          if (this.current === 1) {
            this.listData = res.data.data.records
          } else {
            this.listData.push(...res.data.data.records)
          }
          this.refreshing = false
          this.loading = false
        }
      })
    },
    goItem(k) {
      this.$router.push({
        path: '/monitor/pressureEditor',
        query: {
          content: k.id,
          planId: this.$route.query.planId
        }
      })
    },
    // /**
    // * js将字符串转成日期格式，返回年月日
    // * @param dateStr 日期字符串
    // */
    getymd(dateStr) {
      var d = new Date(dateStr.replace(/-/g, '/'));
      var resDate = (d.getMonth() + 1) + '月' + d.getDate() + '日';
      return resDate;
    },

  }
}
</script>
<style lang="less" scoped>
.main {

  .caring-select{
    outline:none;
    width: 77px;
    height: 29px;
    border-radius: 5px 5px 5px 5px;
    border: 1px solid #999999;
    color: #666666;
    font-size: 14px;
    font-weight: 400;
    text-align: center;
  }

  .unit {
    background: #fff;
    font-size: 12px;
    color: rgba(102, 102, 102, 0.85);
    padding: 0px 4vw;
  }

  .item {
    margin-top: 10px;
    padding: 10px;
    background: #fff;

    .date {
      color: rgba(102, 102, 102, 0.85);
    }

    .docs {
      margin: 10px 0px 5px;
      color: rgba(102, 102, 102, 1);

      img {
        width: 30px;
        vertical-align: middle;
      }
    }
  }
}

.caring-form-result-status{
  width: 61px;
  height: 21px;
  color: white;
  background: rgb(255, 90, 90);
  line-height: 21px;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
  border-radius: 16px;
}

/deep/ .vux-header {
  height: 50px;
}

</style>

