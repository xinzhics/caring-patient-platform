<template>
  <section>
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" title="血糖监测" @onBack="() => $router.go(-1)"></headNavigation>
    </van-sticky>
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
          <van-icon name="plus" @click="edit(sugar)" v-if="!sugar.sugarValue" />
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
      </div>
      <div style="clear:both;"></div>
    </div>
    <van-dialog v-model="show" class="dialog-demo" show-confirm-button :showConfirmButton="false">
      <div style="line-height:40px;padding:10px;text-align:center">
        <span style="font-size:1.2rem;color:rgba(102,102,102,1);">填写/修改血糖值</span>
        <van-icon name="cross" size="20" @click="show=false" style="vertical-align: middle;margin-top: 6px;float:right" />
      </div>
      <div style="padding-bottom:20px">
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <van-field
            input-align="right"
            v-model="formData.sugarValue"
            style="background:#F5F5F5;"
            placeholder="请输入"
          >
            <template slot="label">
              <span style=" font-size: 16px;font-weight: 500">血糖</span>
            </template>
          </van-field>
          <span style="text-align: center;line-height: 44px;">mmol/L</span>
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <van-cell style="background:#F5F5F5;" :value="sugarTypes[formData.type]" >
            <template slot="title">
              <span style="font-size: 16px;font-weight: 500">测量点</span>
            </template>
          </van-cell>
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <van-cell style="background:#F5F5F5;" :value="formData.createTime" >
            <template slot="title">
              <span style="font-size: 16px;font-weight: 500">测量时间</span>
            </template>
          </van-cell>
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <van-cell style="background:#F5F5F5;" :value="formatDate(formData.createDate)" >
            <template slot="title">
              <span style="font-size: 16px;font-weight: 500">测量日期</span>
            </template>
          </van-cell>
        </div>
        <div
          style="width:86%;margin:10px auto;background:#F5F5F5;border-radius:6px;text-align:left;padding:10px;display:flex;justify-content: space-between;">
          <van-field
            input-align="right"
            v-model="formData.remarks"
            style="background:#F5F5F5;"
            label="备注"
            placeholder="请输入"
          >
            <template slot="label">
              <span style="font-size: 16px;font-weight: 500">备注</span>
            </template>
          </van-field>
        </div>
      </div>
    </van-dialog>
  </section>
</template>
<script>

import Vue from 'vue'
import { Dialog, Field, Icon, Cell } from 'vant'
import moment from 'moment'
Vue.use(Dialog)
Vue.use(Icon)
Vue.use(Cell)
Vue.use(Field)
export default {
  data () {
    return {
      biaozhun: require('@/assets/my/biaozhun (2).png'),
      biaozhun1: require('@/assets/my/biaozhun (1).png'),
      biaozhun2: require('@/assets/my/biaozhun.png'),
      showPicker: false,
      sugarTypes: [
        '凌晨',
        '空腹',
        '早餐后',
        '午餐前',
        '午餐后',
        '晚餐前',
        '晚餐后',
        '睡前'
      ],
      sugarType: 0,
      week: 0,
      list: [],
      show: false,
      formData: {},
      position: 0
    }
  },
  created () {
    this.initPage()
  },
  mounted () {
  },
  methods: {
    getDay (d) {
      if (!d) return
      let date = ''
      if (this.ifios()) {
        date = new Date(d.replace(/-/g, '/'))
      } else {
        date = new Date(d)
      }
      return date.getDate()
    },
    initPage () {
      // 计算本周时间， 设置数据
      let time = moment(moment()).startOf('week')
      let weekArr = []
      for (let i = 1; i < 8; i++) {
        let tempTime = moment(time).weekday(i)
        let tr = moment(tempTime).format('YYYY-MM-DD')
        tr = tr.replace('星期', '周')
        weekArr.push({createDate: tr + ' 00:00:00', sugars: []})
      }
      console.log('weekArr', weekArr)
      this.list = this.insertBlankData(weekArr)
      console.log('this.list', this.list)
    },
    insertBlankData: function (list) {
      if (list) {
        for (let i = 0; i < list.length; i++) {
          const ele = list[i]
          if (ele.sugars.length < 8) {
            for (let j = 0; j < 8; j++) {
              if (!ele.sugars[j] || ele.sugars[j].type > j) {
                let data = {}
                if (!ele.createDate) return
                if (this.ifios()) {
                  data.createDate = ele.createDate.replace(/-/g, '/')
                } else {
                  data.createDate = ele.createDate
                }
                data.type = j
                let date = new Date()
                let hours = date.getHours()
                let minutes = date.getMinutes()
                if (minutes < 10) {
                  minutes = '0' + minutes
                }
                data.createTime = hours + ':' + minutes
                ele.sugars.splice(j, 0, data)
              }
            }
          }
        }
        return list
      } else {
        return []
      }
    },
    convert: function (index) {
      let day = ''
      switch (index) {
        case 1:
          day = '周一'
          break
        case 2:
          day = '周二'
          break
        case 3:
          day = '周三'
          break
        case 4:
          day = '周四'
          break
        case 5:
          day = '周五'
          break
        case 6:
          day = '周六'
          break
        case 7:
          day = '周日'
          break
      }
      return day
    },
    edit: function (sugar) {
      this.formData = {
        sugarValue: '',
        type: sugar.type,
        createTime: moment(new Date()).format('HH:mm'),
        createDate: new Date(),
        remarks: ''
      }
      this.show = true
    },
    formatDate: function (d) {
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
      if (month < 10) {
        month = '0' + month
      }
      if (day < 10) {
        day = '0' + day
      }
      return year + '/' + month + '/' + day
    },
    measure: function (type) {
      let value = ''
      switch (type) {
        case 0:
          value = '凌晨'
          break
        case 1:
          value = '早餐前'
          break
        case 2:
          value = '早餐后'
          break
        case 3:
          value = '午餐前'
          break
        case 4:
          value = '午餐后'
          break
        case 5:
          value = '晚餐前'
          break
        case 6:
          value = '晚餐后'
          break
        case 7:
          value = '睡前'
          break
      }
      return value
    },
    rightColor: function (v) {
      if (v < 5) {
        return 'rightColor3'
      } else if (v >= 5 && v < 10) {
        return 'rightColor1'
      } else {
        return 'rightColor2'
      }
    },
    formatNumber: function (v) {
      if (parseInt(v) === parseFloat(v)) {
        return v + '.0'
      } else {
        return v
      }
    },
    ifios () {
      let result = false
      let browser = {
        versions: () => {
          let u = navigator.userAgent
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
        }
      }
      let isPhone = browser.versions.mobile || browser.versions.ios || browser.versions.android || browser.versions.iPhone || browser.versions.iPad
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
<style lang='less' scoped>
.list {
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
