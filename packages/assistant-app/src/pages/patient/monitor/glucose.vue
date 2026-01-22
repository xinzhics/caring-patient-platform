<template>
  <div class="main">
    <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '血糖监测'"
                    @onBack="$router.go(-1)"></headNavigation>
    <div style="display: flex; background: #fff; padding: 15px 15px 0px 15px; margin-top: 1px">
      <div style="width: 50%">
        <div>
          <span style="font-size: 16px; font-weight: 500; color: #3D444D; height: 22px;">监测图表</span>
        </div>
        <div>
          <span style="font-size: 12px; font-weight: 400; color:#999; height: 17px">单位（mmol/L）</span>
        </div>
      </div>
      <div style="width: 50%; text-align: right">
        <select class="caring-select" style="width: 105px; " v-model="selectVal" @change="getSelect">
          <option v-for="(i,k) in sugarTypes" :key="k" :value="i.value">{{ i.name }}</option>
        </select>
      </div>
    </div>
    <div id="myChart" style="height:230px;background: #fff; padding-bottom: 15px" :style="{width: echartWidth + 'px'}"></div>
    <div style="display: flex; padding: 10px 15px 0 15px; margin-top: 15px">
      <div style="width: 50%">
        <span style="font-size: 16px; font-weight: 500; color: #3D444D">监测记录</span>
      </div>
      <div style="width: 50%; text-align: right">
        <select class="caring-select" style="width: 120px;" v-model="monitorQueryDate" @change="monitorQueryDateChange">
          <option v-for="(item, index) in monitorQueryDateList" :key="index" :value="item.code">{{ item.name }}</option>
        </select>
      </div>
    </div>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="padding-bottom: 60px">
      <van-list :finished="finished" finished-text="没有更多了" @load="onLoad">
        <div style="padding: 10px" v-for="(i, k) in listData" :key="k">
          <div style="background: white; padding: 10px 0" @click="toEdit(i.id)">
            <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
              <div style="display: flex; align-items: center">
                <div class="caring-form-div-icon"></div>
                <span style="margin-left: 11px; color: #333333; font-size: 14px; font-weight: 500; ">监测时间</span>
                <span style="margin-left: 11px; font-size: 14px; font-weight: 400; color: #666">
                    {{ formatDate(i.createDay) }} {{ i.time }}
                </span>
              </div>
              <div style="position: absolute; right: 20px" v-if="i.dataFeedBack">
                <div class="caring-form-result-status"
                     :style="{background: getDataFeedBack(i.dataFeedBack, 'status') == '1' ? '#66E0A7' : '#FF5A5A'}">
                  {{ getDataFeedBack(i.dataFeedBack, 'text') }}
                </div>
              </div>
            </div>
            <div style="padding: 10px 15px 0px 15px">
              <div style="border-radius: 10px; padding: 10px 6px;">
                <div style="padding: 5px 0">
                  <van-row>
                    <van-col span="12">
                      <div style="color: #333333; font-weight: 400; font-size: 14px">{{ getTypeText(i.type) }}：</div>
                    </van-col>
                    <van-col span="12" style="display: flex">
                      <div style="font-weight: 500; font-size: 14px; width: 90%; text-align: right; color: #333333">
                        {{ i.sugarValue }} mmol/L
                      </div>
                    </van-col>
                  </van-row>
                </div>
                <div style="padding: 5px 0">
                  <van-row>
                    <van-col span="12">
                      <div style="color: #333333; font-weight: 400; font-size: 14px">备注：</div>
                    </van-col>
                    <van-col span="12" style="display: flex">
                      <div style="font-weight: 500; font-size: 14px; width: 90%; text-align: right; color: #999999">
                        {{ i.remarks ? i.remarks : '未填写' }}
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
      <van-button type="primary" style="width: 90%; background: #67E0A7; border: none" round
                  @click="$router.push('/patient/monitor/glucoseEditor')">添加记录
      </van-button>
    </div>
  </div>
</template>

<script>
import {findSugarPage, loadMyBloodSugarTrendData} from '@/api/plan.js'
import Vue from 'vue'
import {Button, PullRefresh, List} from 'vant'
import * as echarts from 'echarts'

Vue.use(Button)
Vue.use(PullRefresh)
Vue.use(List)

export default {
  data () {
    return {
      pageTitle: '',
      bloodimg: require('@/assets/my/xuetang.png'),
      echartWidth: window.innerWidth,
      sugarTypes: [
        {name: '凌晨血糖', value: 0},
        {name: '空腹血糖', value: 1},
        {name: '早餐后血糖', value: 2},
        {name: '午餐前血糖', value: 3},
        {name: '午餐后血糖', value: 4},
        {name: '晚餐前血糖', value: 5},
        {name: '晚餐后血糖', value: 6},
        {name: '睡前血糖', value: 7},
        {name: '随机血糖', value: 8}
      ],
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
      selectVal: 0,
      listData: [],
      loading: false,
      finished: false,
      refreshing: false,
      current: 1,
      xdata: [],
      myChart: undefined,
      show: false,
      formData: {},
      datacharts: []
    }
  },
  mounted () {
    this.getInfo()
    this.getEchatData()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    getTypeText (type) {
      let text
      type = Number(type)
      switch (type) {
        case 0:
          text = '凌晨血糖'
          break
        case 1:
          text = '空腹血糖'
          break
        case 2:
          text = '早餐后2小时血糖'
          break
        case 3:
          text = '午餐前血糖'
          break
        case 4:
          text = '午餐后2小时血糖'
          break
        case 5:
          text = '晚餐前血糖'
          break
        case 6:
          text = '晚餐后2小时血糖'
          break
        case 7:
          text = '睡前血糖'
          break
        case 8:
          text = '随机血糖'
          break
      }
      return text
    },
    toEdit (id) {
      this.$router.push({
        path: '/patient/monitor/glucoseEditor',
        query: {
          id: id
        }
      })
    },
    /**
     * 获取数据反馈
     */
    getDataFeedBack (dataFeedBack, type) {
      const obj = JSON.parse(dataFeedBack)
      if (type === 'status') {
        return obj.normalAnomaly
      } else if (type === 'text') {
        return obj.normalAnomalyText
      }
    },
    onLoad () {
      if (this.refreshing) {
        this.list = []
        this.current = 1
        this.refreshing = false
      }
      this.getInfo()
    },
    getInfo () {
      const params = {
        'current': this.current,
        'model': {
          monitorQueryDate: this.monitorQueryDate,
          patientId: localStorage.getItem('patientId')
        },
        'size': 10,
        'sort': 'createTime'
      }
      if (this.finished) {
        return
      }
      if (this.loading) {
        return
      }
      this.loading = true
      findSugarPage(params).then((res) => {
        if (res.code === 0) {
          console.log(res, '=====================<')
          if (this.current >= res.data.pages) {
            this.finished = true
          }
          if (this.current === 1) {
            this.listData = res.data.records
          } else {
            this.listData.push(...res.data.records)
          }
          this.current++
          this.refreshing = false
          this.loading = false
        }
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.list = []
      this.onLoad()
    },
    getEchatData () {
      const params = {
        patientId: localStorage.getItem('patientId'),
        type: this.selectVal,
        week: 0
      }
      loadMyBloodSugarTrendData(params).then((res) => {
        if (res.code === 0) {
          if (res.data && res.data.length > 1) {
            this.datacharts = res.data
            console.log('loadMyBloodSugarTrendData', this.datacharts)
            this.makeLine()
          }
        }
      })
    },
    /**
     * 创建趋势图的 option
     *
     */
    createOptions (ydata) {
      return {
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
          boundaryGap: [0, '40%']
        },
        series: ydata
      }
    },

    // 全部线
    makeLine () {
      let xdata = [] // x数据
      let ydata = [] // y数据
      this.xdata = xdata
      let arr = {
        name: '血糖',
        type: 'line',
        data: []
      }
      this.datacharts.forEach(el => {
        xdata.push(el.day.substring(5, 10))
        ydata.push(el.value)
      })
      arr.data = ydata
      this.myChart = echarts.init(document.getElementById('myChart'))
      const option = this.createOptions([arr])
      option && this.myChart.setOption(option, true)
    },

    getSelect () {
      this.getEchatData()
    },
    monitorQueryDateChange () {
      this.finished = false
      this.refreshing = true
      this.list = []
      this.current = 1
      this.getInfo()
    },
    timestampToTime (timestamp) {
      let date = new Date(timestamp * 1000) // 时间戳为10位需*1000，时间戳为13位的话不需乘1000
      let Y = date.getFullYear() + '/'
      let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '/'
      let D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' '
      let h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':'
      let m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':'
      let s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds())
      return Y + M + D + h + m + s
    },
    formatDate (t) {
      let d = this.timestampToTime(t)
      if (!d) return
      let date = ''
      if (this.ifios()) {
        date = new Date(d.replace(/-/g, '/'))
      } else {
        date = new Date(d)
      }
      let year = date.getFullYear()
      let month = date.getMonth() + 1
      let day = date.getDate()
      return year + '-' + month + '-' + day
    },
    ifios () {
      let result = false
      var browser = {
        versions: (function () {
          var u = navigator.userAgent
          return {
            trident: u.indexOf('Trident') > -1, // IE内核
            presto: u.indexOf('Presto') > -1, // opera内核
            webKit: u.indexOf('AppleWebKit') > -1, // 苹果、谷歌内核
            gecko: u.indexOf('Firefox') > -1, // 火狐内核Gecko
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), // 是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), // ios
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, // android
            iPhone: u.indexOf('iPhone') > -1, // iPhone
            iPad: u.indexOf('iPad') > -1, // iPad
            webApp: u.indexOf('Safari') > -1 // Safari
          }
        }())
      }
      var isPhone = browser.versions.mobile || browser.versions.ios || browser.versions.android || browser.versions.iPhone || browser.versions.iPad
      if (isPhone && browser.versions.ios) {
        return true
      } else if ((isPhone && browser.versions.android) || (!isPhone)) {
        return false
      }
      return result
    }
  }
}
</script>
<style lang="less" scoped>
.main {
  position: relative;

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
      padding: 10px 0px 10px;
      color: rgba(102, 102, 102, 1);
      display: flex;
      justify-content: space-between;
      border-bottom: 1px solid #eaecef;

      div {
        img {
          width: 30px;
          vertical-align: middle;
        }
      }

    }
  }

  .headerContent {
    margin-top: 10px;
    padding-top: 10px;
    background: #fff;
    display: flex;
    justify-content: space-between;
    padding: 10px 5vw 0px;
    font-size: 12px;

    color: rgba(102, 102, 102, 0.85);

    span {
      line-height: 30px;
    }

    .selectCont {
      background: #f5f5f5;
      padding: 5px 20px;
      border: none !important;
      color: rgba(102, 102, 102, 0.85);
    }
  }
}

.caring-form-result-status {
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
