<template>
  <section v-if="canshow">
    <navBar @searchMedicine="searchMedicine" @toHistoryPage="startScan" :rightIcon="scan" :showRightIcon="true"
            ref="medicineNavBar"
            :showMedicineInput="true"/>
    <div class="medicine-div-list" :style="{height: height + 'px'}">
      <div v-if="listData && listData.length >= 1 && statusAll.hasShowRecommendDrugs === 0 && !scanMedicineState && !searchMedicineState">
        <div style="display: flex; justify-content: center; background: #FFF; padding-top: 10px; padding-bottom: 10px">
          <van-button type="primary" round size="small" style="width: 120px; font-weight: bold">推荐用药</van-button>
        </div>
        <group style="background: #FFF; margin-top: 0px !important;">
          <cell-box is-link v-for="(i,k) in listData" :key="k" @click.native="goitem(i)">
            <div style="display: flex;justify-content: space-between;margin:10px 0px;">
              <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                <img v-if="i.icon" :src="i.icon" style="width:100%" alt="" srcset="">
              </div>
              <div style="margin-left:10px;line-height:24px">
                <p style="font-size:16px;color:rgba(102,102,102,1);"><span v-if="i.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{
                    i.name
                  }}
                </p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.genericName }}</p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.manufactor }}</p>
                <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ i.spec }}</p>
              </div>
            </div>
          </cell-box>
        </group>
      </div>
      <!-- 非搜索模式。 非扫码结果的全部药品数据 -->
      <div v-if="medicineList.length > 0 && !scanMedicineState && !searchMedicineState">
        <div style="display: flex; justify-content: center; background: #FFF">
          <van-button type="primary" round size="small" style="width: 120px; margin-top: 10px; margin-bottom: 10px">所有药品</van-button>
        </div>
        <group>
          <cell-box is-link v-for="(i,k) in medicineList" :key="k" @click.native="goitem(i)">
            <div style="display: flex;justify-content: space-between;margin:10px 0px;">
              <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                <img v-if="i.icon" :src="i.icon" style="width:100%" alt="" srcset="">
              </div>
              <div style="margin-left:10px;line-height:24px">
                <p style="font-size:16px;color:rgba(102,102,102,1);"><span v-if="i.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{
                    i.name
                  }}
                </p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.genericName }}</p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.manufactor }}</p>
                <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ i.spec }}</p>
              </div>
            </div>
          </cell-box>
        </group>
        <div v-if="current < pages" style="text-align:center;line-height:30px;font-size:16px;padding:5px 0px 25px"
             @click="getInfo(current+1)">
          <span>点击加载更多...</span>
        </div>
      </div>
      <!-- 当搜索或者扫码后 -->
      <div v-if="scanMedicineState && !loading">
        <p v-if="medicineList && medicineList.length > 0" style="padding:10px 16px 0">扫描结果</p>
        <div class="nodata" v-if="medicineList && medicineList.length === 0">
          <p style="color:#9d9d9d">暂无数据</p>
          <p style="color:#9d9d9d">您可点击下方按钮自定义添加</p>
          <p style="width:70%;margin:20px auto;padding:10px 0px;border-radius:30px;background:#ffbe8b;color:#fff;"
             @click="() => {isNoData = true, medicineName = ''}">没有我的药品？</p>
        </div>
        <group>
          <cell-box is-link v-for="(i,k) in medicineList" :key="k" @click.native="goitem(i)">
            <div style="display: flex;justify-content: space-between;margin:10px 0px;">
              <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                <img v-if="i.icon" :src="i.icon" style="width:100%" alt="" srcset="">
              </div>
              <div style="margin-left:10px;line-height:24px">
                <p style="font-size:16px;color:rgba(102,102,102,1);"><span v-if="i.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{
                    i.name
                  }}
                </p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.genericName }}</p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.manufactor }}</p>
                <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ i.spec }}</p>
              </div>
            </div>
          </cell-box>
        </group>
      </div>
      <div v-if="searchMedicineState && !loading">
        <p v-if="medicineList && medicineList.length >= 1" style="padding:10px 16px 0px">搜索药品</p>
        <div class="nodata" v-if="medicineList && medicineList.length < 1" style="position: relative; height: 60vh">
          <p style="color:#9d9d9d; font-size: 14px; margin-top: 20px; font-weight: bold">未搜索到您需要的药品</p>
          <p style="color:#9d9d9d;font-size: 14px; font-weight: bold">是否添加该药品</p>
          <div style="position: absolute; display: flex; justify-content: center; width: 100%;bottom: 0px">
            <van-button type="primary" round style="width: 90%; background: #67E0A7; border: 0px" @click="() => {isNoData = true, medicineName = ''}">添加该药品</van-button>
          </div>
        </div>
        <group>
          <cell-box is-link v-for="(i,k) in medicineList" :key="k" @click.native="goitem(i)">
            <div style="display: flex;justify-content: space-between;margin:10px 0px;">
              <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                <img v-if="i.icon" :src="i.icon" style="width:100%" alt="" srcset="">
              </div>
              <div style="margin-left:10px;line-height:24px">
                <p style="font-size:16px;color:rgba(102,102,102,1);"><span v-if="i.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{
                    i.name
                  }}
                </p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.genericName }}</p>
                <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ i.manufactor }}</p>
                <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ i.spec }}</p>
              </div>
            </div>
          </cell-box>
        </group>
        <div v-if="current < pages" style="text-align:center;line-height:30px;font-size:16px;padding:5px 0px 25px"
             @click="current = current+1; getMoreMedicine(current+1)">
          <span>点击加载更多...</span>
        </div>
      </div>
    </div>

    <van-dialog v-model="isNoData" title="添加药品" :show-cancel-button="false" :show-confirm-button="false" >
      <div style="display: flex; margin: 40px 20px;border-radius: 5px; border: 1px solid #EEEEEE; align-items: center">
        <van-field v-model="medicineName" placeholder="输入药名" style="" maxlength="50"/>
        <div style="padding-right: 10px;">{{medicineName.length}}/50</div>
      </div>
      <div style="padding-bottom: 20px; display: flex; align-items: center; justify-content: center">
        <van-button plain type="primary" round size="small" style="width: 100px" @click="isNoData = false">取消</van-button>
        <van-button type="primary" round size="small" style="margin-left: 30px; width: 100px" @click="dialogConfirm()">确认</van-button>
      </div>
    </van-dialog>
  </section>
</template>
<script>
import {Group, Cell, CellBox, XInput} from 'vux'
import Api from '@/api/Content.js'
import wx from 'weixin-js-sdk'
import {Base64} from 'js-base64'
import Vue from 'vue';
import {Button, Dialog, Field } from 'vant';

Vue.use(Button);
Vue.use(Dialog);
Vue.use(Field);

export default {
  components: {
    Group,
    Cell,
    CellBox,
    XInput,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      noData: require('@/assets/my/medicine_no_data.png'),
      scan: require('@/assets/my/saoma.png'),
      inputVal: '',
      statusAll: {},
      current: 1,
      total: 0,
      pages: 0,
      isNoData: false,
      loading: false,
      listData: [], // 放推荐药品
      medicineList: [],   // 放所有药品 扫码的结果。搜索的结果。
      searchMedicineState: false,
      scanMedicineState: false,
      canshow: false,
      medicineName: '',
      height: document.body.offsetHeight - 60
    }
  },
  mounted() {
    this.getInfo()
    this.getwxSignature()
    // hasShowRecommendDrugs 判断是否显示推荐用药
    this.getlist()
    if (localStorage.getItem('statusAll')) {
      this.statusAll = JSON.parse(localStorage.getItem('statusAll'))
    } else {
      this.getStatus()
    }
  },
  methods: {
    searchMedicine(e) {
      this.inputVal = e
      this.scanMedicineState = false
      this.medicineList = []
      this.searchMedicineState = true
      this.loading = true
      this.getInfo(1)
    },
    getStatus() {
      Api.regGuidegetGuide({}).then((res) => {
        if (res.data.code === 0) {
          this.statusAll = res.data.data
        }
      })
    },
    getMoreMedicine() {
      const params = {
        tenant: Base64.decode(localStorage.getItem('headerTenant')),
        current: this.current,
        map: {},
        model: {
          name: this.inputVal
        },
        order: "descending",
        size: 20,
        sort: "id"
      }
      Api.sysDrugsPage(params).then((res) => {
        if (res.data.code === 0) {
          this.medicineList = this.medicineList.concat(res.data.data.records)
          this.total = res.data.data.total
          this.canshow = true
        }
        this.loading = false
      })
    },
    getInfo(current, queryAll) {
      if (current) {
        this.current = current
      }
      if (queryAll) {
        this.searchMedicineState = false
        this.scanMedicineState = false
      }
      if (!this.inputVal || this.inputVal.length === 0) {
        this.scanMedicineState = false
        this.searchMedicineState = false
      }
      const params = {
        tenant: Base64.decode(localStorage.getItem('headerTenant')),
        current: this.current,
        map: {},
        model: {
          name: this.inputVal
        },
        order: "descending",
        size: 20,
        sort: "id"
      }
      Api.sysDrugsPage(params).then((res) => {
        if (res.data.code === 0) {
          this.medicineList = this.medicineList.concat(res.data.data.records)
        }
        this.total = res.data.data.total
        this.pages = res.data.data.pages
        this.canshow = true
        this.loading = false
      })
    },
    getlist() {
      const params = {
        code: Base64.decode(localStorage.getItem('headerTenant'))
      }
      Api.listRecommendDrugs(params).then((res) => {
        if (res.data.code === 0) {
          this.listData = res.data.data
          this.canshow = true
        }
      })
    },
    dialogConfirm() {
      if (this.$route.query && this.$route.query.medicineId) {
        this.$router.replace({
          path: '/medicine/addmedicine',
          query: {medicineName: this.medicineName, medicineId: this.$route.query.medicineId}
        })
      }else if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.replace({
          path: '/medicine/addmedicine',
          query: {medicineName: this.medicineName, imMessageId: this.$route.query.imMessageId}
        })
      }else {
        this.$router.replace({
          path: '/medicine/addmedicine',
          query: {medicineName: this.medicineName}
        })
      }
    },
    goitem(k) {
      if (this.$route.query && this.$route.query.medicineId) {
        this.$router.replace({
          path: '/medicine/addmedicine',
          query: {drugsId: k.id, medicineId: this.$route.query.medicineId}
        })
      }else if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.replace({
          path: '/medicine/addmedicine',
          query: {drugsId: k.id, imMessageId: this.$route.query.imMessageId}
        })
      }else {
        this.$router.replace({
          path: '/medicine/addmedicine',
          query: {drugsId: k.id}
        })
      }
    },
    //扫一扫
    // 判断系统版本是否是ios 14或以上
    isIos14AndMore() {
      // 判断ios手机版本号是否大于14，大于14就拿app.vue存储的url,否则拿当前页面的url
      try {
        const str = navigator.userAgent.toLowerCase()
        const ver = str.match(/cpu iphone os (.*?) like mac os/)
        if (!ver) {
          return false
        } else {
          return Number(ver[1].split('_')[0]) >= 14
        }
      } catch (e) {
        return false
      }
    },

    getwxSignature() {
      const params = {
        url: this.$getWxConfigSignatureUrl(),
        wxAppId: localStorage.getItem('wxAppId')
      }
      Api.wxSignature(params).then((res) => {
        if (res.data.code === 0) {
          wx.config({
            // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            debug: false,
            // 必填，公众号的唯一标识
            appId: res.data.data.appId,
            // 必填，生成签名的时间戳
            timestamp: res.data.data.timestamp,
            // 必填，生成签名的随机串
            nonceStr: res.data.data.nonceStr,
            // 必填，签名
            signature: res.data.data.signature,
            // 必填，需要使用的JS接口列表，所有JS接口列表
            jsApiList: ["scanQRCode"]
          });
          wx.ready(function () {
            console.log('配置成功')
          });
          wx.error(function () {
            console.log('配置不成功')
          });
        }
      })
    },
    startScan() {
      const that = this
      wx.scanQRCode({
        needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
        scanType: ["qrCode", 'barCode'], // 可以指定扫二维码还是一维码，默认二者都有
        success: (res) => {
          var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果/
          that.getMedice(result)
        }
      });
    },
    getMedice(k) {
      this.inputVal = ''
      this.loading = true
      this.medicineList = []
      this.searchMedicineState = false
      this.scanMedicineState = true
      const that = this
      const params = {
        tenant: Base64.decode(localStorage.getItem('headerTenant')),
        data: {
          "current": 1,
          "map": {},
          "model": {
            "code": that.getCaption(k),
          },
          "order": "descending",
          "size": 20,
          "sort": "id"
        }
      }
      Api.pageByTenant(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data.records) {
            that.medicineList = res.data.data.records
          }
          this.$vux.toast.text('扫描成功', 'center')
          this.canshow = true
        }
        this.loading = false
      })
    },
    getCaption(obj) {
      var index = obj.lastIndexOf("\,");
      obj = obj.substring(index + 1, obj.length);
      return obj;
    },
  }
}
</script>
<style lang="less" scoped>
.nodata {
  width: 100%;
  text-align: center;
  padding: 80px 0px;
  background: #f5f5f5;

  img {
    height: 100px;
  }
}

/deep/ .vux-header {
  height: 50px;
}

/deep/ .weui-cells {
  margin-top: 0px !important;
}

.medicine-div-list {
  overflow-y: auto;
}
</style>
