<template>
  <section>
    <x-header :left-options="{backText: ''}">血糖监测</x-header>
    <div class="list">
      <div class="title box van-hairline--bottom">
        <span class="van-hairline--right left-content">检测日期</span>
        <span class="flex-1 van-hairline--right">凌晨</span>
        <span class="flex-2 van-hairline--right two-floors">
                    <span class="top">早餐</span>
                    <div class="box van-hairline--top bottom">
                        <span class="flex-1 van-hairline--right">空腹</span>
                        <span class="flex-1">餐后</span>
                    </div>
                </span>
        <span class="flex-2 van-hairline--right two-floors">
                    <span class="top">午餐</span>
                    <div class="box van-hairline--top bottom">
                        <span class="flex-1 van-hairline--right">餐前</span>
                        <span class="flex-1">餐后</span>
                    </div>
                </span>
        <span class="flex-2 van-hairline--right two-floors">
                    <span class="top">晚餐</span>
                    <div class="box van-hairline--top bottom">
                        <span class="flex-1 van-hairline--right">餐前</span>
                        <span class="flex-1">餐后</span>
                    </div>
                </span>
        <span class="flex-1 big-span">睡前</span>
      </div>
      <div class="content box van-hairline--bottom" v-for="(sugarDTO,index) in list" :key="index">
                <span class="left-content van-hairline--right">
                    <div class="wei-font" v-text="getDay(sugarDTO.createDate)"></div>
                    <span v-text="convert(index+1)"></span>
                </span>
        <span class="flex-1 van-hairline--right" v-for="(sugar,i) in sugarDTO.sugars" :key="i">
                    <span
                      v-text="formatNumber(sugar.sugarValue)"
                      v-if="sugar.sugarValue"
                      :class="rightColor(sugar.sugarValue)"
                      @click="edit(sugar, index)"
                    />
          <!-- <i class="iconfont iconicon-test-copy" @click="edit(sugar)" v-else></i> -->
                    <x-icon type="ios-plus-empty" size="20" @click="edit(sugar, index)" v-if="!sugar.sugarValue"></x-icon>
                </span>
      </div>
    </div>

    <div>
      <div style="float: right;margin: 10px 10px 30px">
                <span style="padding:5px;margin-right: 5px;color:#00FF00">
                    <img :src="biaozhun" style="vertical-align: middle;width:15px" alt="">达标
                </span>
        <span style="padding:5px;margin-right: 5px;color:#7232dd">
                     <img :src="biaozhun1" style="vertical-align: middle;width:15px" alt="">偏高
                </span>
        <span style="padding:5px;margin-right: 5px;color:#FF0000">
                     <img :src="biaozhun2" style="vertical-align: middle;width:15px" alt="">偏低
                </span>
        <!-- <van-tag plain color="#00FF00" style="padding:5px;margin-right: 5px"><x-icon type="ios-circle-filled" size="30"></x-icon>达标</van-tag>
        <van-tag plain color="#7232dd" style="padding:5px;margin-right: 5px"><x-icon type="ios-circle-filled" size="30"></x-icon>偏高</van-tag>
        <van-tag plain color="#FF0000" style="padding:5px;"><x-icon type="ios-circle-filled" size="30"></x-icon>偏低</van-tag> -->
      </div>
      <div style="clear:both;"></div>
    </div>
    <van-dialog v-model="show" class="dialog-demo" :show-confirm-button="false">
      <div style="line-height:40px;padding:10px;text-align:center">
        <span style="font-size:1.2rem;color:rgba(102,102,102,1);">填写/修改血糖值</span>
        <x-icon @click="show=false" type="ios-close-empty" style="vertical-align: middle;margin-top: 6px;float:right"
                size="30"></x-icon>
        <!-- <div style="width:40%;height:4px;background:#FFBE8B;margin:5px calc(100% - 30px)"></div> -->
      </div>
      <div style="padding-bottom:20px">
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <x-input title="血糖" v-model="formData.sugarValue" @input="checkprice" text-align="right"
                   style="padding: 5px 16px!important;" placeholder="输入值">
            <span slot="right">mmol/L</span>
          </x-input>
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <x-input title="测量点" :value="sugarTypes[formData.type]" text-align="right" disabled
                   style="padding: 5px 16px!important;" placeholder="输入值"></x-input>
          <!-- <cell title="测量点" style="padding: 5px 16px!important;" value-align="right">{{changeTeps(formData.type)}}</cell> -->
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <popup-picker style="width:100%;padding: 0px!important;" title="测量时间"
                        :popup-style="{ 'z-index':99999,'padding': '0px!important'}" value-text-align="right"
                        :data="hourList" v-model="formData.createTime" @on-change="changeTime" show-name></popup-picker>
          <!-- <x-input title="测量时间" :value="formData.createTime" text-align="right" style="padding: 5px 16px!important;" placeholder="输入值"></x-input> -->
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <x-input title="测量日期" :value="formatDate(formData.createDate)" disabled text-align="right"
                   style="padding: 5px 16px!important;" placeholder="输入值"></x-input>
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <x-input title="备注" v-model="formData.remarks" text-align="right" style="padding: 5px 16px!important;"
                   placeholder="请输入"></x-input>
        </div>

      </div>
    </van-dialog>
  </section>
</template>
<script>
// import SugarMonitorService from "@/api/nursingplan/monitorSugar";
// import { Cell, PopupPicker} from 'vux'

import {Cell, PopupPicker} from 'vux'
import Vue from 'vue'
import { Dialog } from 'vant';
Vue.use(Dialog)
export default {
  components: {
    Cell,
    PopupPicker
  },
  data() {
    return {
      biaozhun: require('@/assets/my/biaozhun (2).png'),
      biaozhun1: require('@/assets/my/biaozhun (1).png'),
      biaozhun2: require('@/assets/my/biaozhun.png'),
      currentSelectedSugarType: "凌晨血糖趋势",
      showPicker: false,
      sugarTypes: [
        "凌晨",
        "空腹",
        "早餐后",
        "午餐前",
        "午餐后",
        "晚餐前",
        "晚餐后",
        "睡前"
      ],
      sugarType: 0,
      currentChartType: "table",
      changeBtnTitle: "查看血糖趋势图",
      headerConfig: {
        title: "血糖趋势"
      },
      jiancelist: [],
      week: 0,
      dateStr: "",
      list: [],
      show: false,
      formData: {},
      lineChartData: {
        datas: [],
        yData: {}

      },
      hourList: [
        [], []
      ],
      submitStatus: false,
      position: 0
    };
  },
  mounted() {
    // this.hourList[0].push()
    for (let j = 0; j < 24; j++) {
      if (j < 10) {
        j = '0' + j
      }
      this.hourList[0].push({name: String(j + '时'), value: String(j)})
    }
    for (let z = 0; z < 60; z++) {
      if (z < 10) {
        z = '0' + z
      }
      this.hourList[1].push({name: String(z + '分'), value: String(z)})
    }

  },
  methods: {
    // 血糖只能输入一位小数
    checkprice(e) {
      if (e && e.match) {
        e = e.match(/^\d*(\.?\d{0,1})/g)[0] || null
        this.$nextTick(() => {
          this.formData.sugarValue = e;
        });
      }
    },

    getDay(d) {

      // let dateT = parseInt(new Date(d).getTime()/1000);
      // console.log(dateT+'   55')
      if (!d) return;
      let date = ''
      if (this.ifios()) {
        date = new Date(d.replace(/-/g, '/'))
      } else {
        date = new Date(d)
      }
      return date.getDate();
    },
    clickLeft: function () {
      this.week--;
      this.initPage(true, false);
    },
    clickRight: function () {
      if (this.week < 0) {
        this.week++;
        this.initPage(true, true);
      }
    },
    initPage: function (flag, add) {
      //计算本周时间， 设置数据
      let self = this;
      let time = moment(moment()).startOf("week");
      let weekArr = []
      for (let i = 1; i < 8; i++) {
        let tempTime = moment(time).weekday(i);
        let tr = moment(tempTime).format("YYYY-MM-DD");
        tr = tr.replace("星期", "周");
        weekArr.push({createDate: tr + " 00:00:00", sugars: []});
      }
      self.list = this.insertBlankData(weekArr);
      this.initSugarTrendChart();
    },
    insertBlankData: function (list) {
      if (list) {
        for (let i = 0; i < list.length; i++) {
          const ele = list[i];
          if (ele.sugars.length < 8) {
            for (let j = 0; j < 8; j++) {
              if (!ele.sugars[j] || ele.sugars[j].type > j) {
                let data = {};
                // console.log(ele.createDate)
                if (!ele.createDate) return;
                if (this.ifios()) {
                  data.createDate = ele.createDate.replace(/-/g, '/');
                } else {
                  data.createDate = ele.createDate
                }
                data.type = j;
                let date = new Date();
                let hours = date.getHours();
                let minutes = date.getMinutes();
                if (minutes < 10) {
                  minutes = '0' + minutes
                }
                data.createTime = hours + ":" + minutes;
                ele.sugars.splice(j, 0, data);
              }
            }
          }
        }
        return list;
      } else {
        return [];
      }
    },
    changeTime(i) {

    },
    convert: function (index) {
      let day = "";
      switch (index) {
        case 1:
          day = "周一";
          break;
        case 2:
          day = "周二";
          break;
        case 3:
          day = "周三";
          break;
        case 4:
          day = "周四";
          break;
        case 5:
          day = "周五";
          break;
        case 6:
          day = "周六";
          break;
        case 7:
          day = "周日";
          break;
      }
      return day;
    },
    edit: function (sugar, i) {
      let d1 = new Date();
      console.log(i+"==========")
      this.position = i
      let year1 = d1.getFullYear();
      let month1 = d1.getMonth();
      let date11 = d1.getDate();
      let d2 = ''
      if (sugar.id) {
        if (this.ifios()) {
          d2 = new Date(sugar.createTime.replace(/-/g, '/'));
        } else {
          d2 = new Date(sugar.createTime);
        }

      } else {
        if (this.ifios()) {
          d2 = new Date(sugar.createDate.replace(/-/g, '/'));
        } else {
          d2 = new Date(sugar.createDate);
        }
      }
      let year2 = d2.getFullYear();
      let month2 = d2.getMonth();
      let date12 = d2.getDate();
      if (
        year1 > year2 ||
        (year1 == year2 && month1 > month2) ||
        (year1 == year2 && month1 == month2 && date11 >= date12)
      ) {
        this.show = true

        this.formData = {
          sugarValue: sugar.id ? sugar.sugarValue : '',
          type: sugar.type,
          createTime: sugar.id ? sugar.time : sugar.createTime,
          createDate: sugar.id ? timestampToTime(sugar.createDay) : sugar.createDate,
          remarks: sugar.id ? sugar.remarks : '',
          setTime: sugar.id ? timestampToTime(sugar.createDay) : sugar.createDate,
          id: sugar.id ? sugar.id : ''
        }
        this.formData.createTime = this.formData.createTime.split(':')
        this.formData.setTime = Date.parse(this.formatDate(this.formData.setTime) + ' ' + this.formData.createTime[0] + ':' + this.formData.createTime[1]).toString().substr(0, 10)
        //  console.log(this.formData.setTime)
        this.formData.time = this.formData.createTime[0] + ':' + this.formData.createTime[1]

        function timestampToTime(timestamp) {
          var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
          var Y = date.getFullYear() + '/';
          var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '/';
          var D = (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' ';
          var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
          var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
          var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
          return Y + M + D + h + m + s;
        }

      } else {
        // this.$toast.fail("不能在未来时间记录血糖值");
        this.$vux.toast.text('不能在未来时间记录血糖值', 'center')
      }
    },
    formatDate: function (d) {
      if (!d) return;
      let date = ''
      if (this.ifios()) {
        date = new Date(d.replace(/-/g, '/'));
      } else {
        date = new Date(d);
      }
      let year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day = date.getDate();
      if (month < 10) {
        month = '0' + month
      }
      if (day < 10) {
        day = '0' + day
      }
      return year + "/" + month + "/" + day;
    },
    measure: function (type) {
      let value = "";
      switch (type) {
        case 0:
          value = "凌晨";
          break;
        case 1:
          value = "早餐前";
          break;
        case 2:
          value = "早餐后";
          break;
        case 3:
          value = "午餐前";
          break;
        case 4:
          value = "午餐后";
          break;
        case 5:
          value = "晚餐前";
          break;
        case 6:
          value = "晚餐后";
          break;
        case 7:
          value = "睡前";
          break;
      }
      return value;
    },
    showFlag: function (array) {
      let flag = false;
      for (let i = 0; i < array.length; i++) {
        const ele = array[i];
        if (ele.id) {
          flag = true;
          break;
        }
      }
      return flag;
    },
    rightColor: function (v) {
      if (v < 5) {
        return "rightColor3";
      } else if (v >= 5 && v < 10) {
        return "rightColor1";
      } else {
        return "rightColor2";
      }
    },
    formatNumber: function (v) {
      if (parseInt(v) == parseFloat(v)
      ) {
        return v + '.0';
      } else {
        return v;
      }
    },
    changeChart() {
      // 血糖趋势图与血糖录入切换
      if (this.currentChartType == "table") {
        this.currentChartType = "Trend";
        this.changeBtnTitle = "录入血糖检查数据";
      } else {
        this.currentChartType = "table";
        this.changeBtnTitle = "查看血糖趋势图";
      }
    },
    onChangeSugarTypes(picker, value) {
      this.currentSelectedSugarType = picker;
      this.sugarType = value;
      this.showPicker = false;
      this.initSugarTrendChart();
    },
    initSugarTrendChart() {
      const my = {
        "datas": [
          "0",
          "0",
          "0",
          "0",
          "0",
          "0",
          "0"
        ],
        "xDatas": [
          1606060800369,
          1606147200369,
          1606233600369,
          1606320000369,
          1606406400369,
          1606492800369,
          1606579200369
        ]
      }

      this.lineChartData.xData = my.xDatas;
      this.lineChartData.yData.datas = my.datas;
    },
    getInfo() {
      const that = this
    },
    changeTeps(type) {
      let thisAll = this.sugarTypes[type]
      return thisAll
    },
    putResult() {
      console.log(this.position)
      this.list[this.position].sugars[this.formData.type].sugarValue = this.formData.sugarValue
      this.show = false
    },
    ifios() {
      let result = false
      var browser = {
        versions: function () {
          var u = navigator.userAgent;
          return {
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Firefox') > -1, //火狐内核Gecko
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android
            iPhone: u.indexOf('iPhone') > -1, //iPhone
            iPad: u.indexOf('iPad') > -1, //iPad
            webApp: u.indexOf('Safari') > -1 //Safari
          };
        }()
      }
      var isPhone = browser.versions.mobile || browser.versions.ios || browser.versions.android || browser.versions.iPhone || browser.versions.iPad
      if (isPhone && browser.versions.ios) {
        return result = true
      } else if ((isPhone && browser.versions.android) || (!isPhone)) {
        return result = false
      }
      return result
    }
  },

  created() {
    this.initPage();
  }
};
</script>
<style lang="less" scoped>
// .vux-x-icon {
//   fill: #F70968;
// }
.list {
  // background: #f6f6f6;
  margin-top: 10px
}

.box {
  display: flex;
  color: rgba(102, 102, 102, 0.85);;
  justify-content: center;
  background: #fff;

  ::v-deep .van-hairline--right {
    &::after {
      border-right: 1px solid #ccc;
    }
  }
}

.flex-1 {
  flex: 1;
  height: 100%;
  text-align: center;
  align-items: center;
  display: flex;
  justify-content: center;
}

.flex-2 {
  flex: 2;
  height: 100%;
  text-align: center;
}

.list {
  ::v-deep .van-hairline--bottom::after,
  ::v-deep .van-hairline--top::after,
  ::v-deep .van-hairline--right::after {
    border-right: 1px solid #ccc;
  }

}

/deep/ .weui-cell {
  padding: 5px 15px !important;
}

.title {
  height: 60px;
  overflow: hidden;

  .left-content {
    font-size: 16px;
  }
}

.two-floors {
  .top {
    height: 30px;
    display: block;
    line-height: 30px;
  }

  .bottom {
    height: 30px;
    line-height: 30px;
  }
}

.content {
  height: 50px;
}

.left-content {
  width: 42px;
  text-align: center;
  font-size: 12px;

  .wei-font {
    font-weight: 500;
    font-size: 20px;
  }
}

.rightColor1 {
  color: #00ff00;
}

.rightColor2 {
  color: #7232dd;
}

.rightColor3 {
  color: #ff0000;
}

/deep/ .vux-header {
  height: 50px;
}
</style>
