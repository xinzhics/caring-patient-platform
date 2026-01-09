<template>
  <section class="main">
    <navBar :pageTitle="pageTitle ? pageTitle : '智能提醒'"></navBar>
    <img :src="styleData.NURSING_BG.attribute1" alt="" style="width:100%" srcset="">
    <div class="content">
      <div class="item" v-for="(i,k) in allData.subscribeList" :key="k">
        <x-switch :value-map="[0,1]" v-if="i.disableSubscribe===0" :title="i.nursingPlanName" min
                  v-model="i.isSubscribe" @on-change="changeBtn(i,k)"></x-switch>
        <div
          v-if="i.disableSubscribe===0 && i.nursingPlanName === '复查提醒'">
          <div style="display: flex; -webkit-box-align: center; align-items: center; padding: 10px 12px !important;
          border-top: 1px solid #f5f5f5 !important;" @click="showDate()">
            <div class="left">
              {{ i.customPlanTimes.length > 0 ? getDate(i.customPlanTimes[0].nextRemindTime) : '' }}
            </div>
            <div class="right">
              <x-icon type="ios-arrow-right" size="20"></x-icon>
            </div>
          </div>

          <van-popup v-model="show" position="bottom">
            <van-datetime-picker
              :v-model="getDate('')"
              type="date"
              title="选择年月日"
              :min-date="minDate"
              :max-date="new Date(2050, 12, 31)"
              @confirm="(time) => dateConfirm(time)"
              @cancel="dateCancel()"
            />
          </van-popup>

          <van-popup v-model="showTime" position="bottom">
            <van-picker show-toolbar title="选择时间" :columns="columns"
                        @confirm="(value, index)=>timeConfirm(value, index)"
                        @cancel="timeCancel()"/>
          </van-popup>


        </div>
      </div>
    </div>
    <x-button style="width:40%;margin-top:40px;color:#fff;background:#66728C!important" @click.native="submit">提交
    </x-button>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import {Popup, DatetimePicker, Picker} from 'vant';

export default {
  components: {
    [Popup.name]: Popup,
    [Picker.name]: Picker,
    [DatetimePicker.name]: DatetimePicker,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      value: '',
      allData: {
        subscribeList: []
      },
      minDate: new Date(moment().subtract(-1, "days").format("YYYY-MM-DD")),
      show: false,
      showTime: false,
      date: '',
      pageTitle: '',
      myName: ['用药提醒', '血压监测', '血糖监测', '复查提醒', '健康日志'],
      styleData: JSON.parse(localStorage.getItem('styleDate')),
      submitting: false,
      columns: [
        // 第一列
        {
          values: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24'],
          defaultIndex: this.getTimeHourIndex(),
        },
        // 第二列
        {
          values: ['00', '30'],
          defaultIndex: 0,
        },
      ],
    }
  },
  mounted() {
    this.getInfo()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    getTimeHourIndex() {
      return moment().format('H') - 1
    },
    dateConfirm(value) {
      this.show = false
      this.date = moment(value).format("YYYY-MM-DD")
      this.showTime = true
    },
    dateCancel() {
      this.show = false
    },
    timeConfirm(value, index) {
      this.showTime = false
      this.date = this.date + " " + value[0] + ":" + value[1] + ":00"
      this.allData.subscribeList.forEach((item, index) => {
        if (item.nursingPlanName === "复查提醒" && this.allData.subscribeList[index].customPlanTimes.length > 0) {
          this.allData.subscribeList[index].customPlanTimes[0].nextRemindTime = this.date
          this.allData.subscribeList[index].customPlanTimes[0].frequency = 0
          this.allData.subscribeList[index].customPlanTimes[0].customizeStatus = 1
        } else {
          this.allData.subscribeList[index].customPlanTimes = [{
            nextRemindTime: this.date,
            frequency: 0,
            customizeStatus: 1,
          }]
        }
      })
    },
    timeCancel() {
      this.showTime = false
    },
    getDate(time) {
      if (time) {
        return moment(time).format("YYYY-MM-DD HH:mm")
      } else {
        return moment().format("YYYY-MM-DD HH:mm")
      }
    },
    showDate() {
      this.show = true
    },
    getInfo() {
      const params = {
        patientId: localStorage.getItem('userId')
      }
      Api.getMyNursingPlans(params).then((res) => {
        if (res.data.code === 0) {
          this.allData = res.data.data
        }
      })
    },
    changeBtn(i, k) {
      this.$set(this.allData.subscribeList, k, i)
    },
    submit() {
      if (this.submitting) {
        return
      }
      this.submitting = true
      const params = this.allData
      const param = {
        patientId: localStorage.getItem('userId'),
        content: params
      }
      Api.updateMyNursingPlans(param).then((res) => {
        if (res.data.code === 0) {
          this.$router.replace("/home")
        } else {
          this.submitting = false
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
.main {
  padding-bottom: 40px;

  .content {
    .item {
      width: 92vw;
      margin: 3vw;
      padding: 0vw 1vw;
      border-radius: 8px;
      background: #fff;
      font-size: 18px;
      color: rgba(102, 102, 102, 1);
    }
  }

  .left {
    -webkit-box-flex: 1;
    flex: 1;
    font-size: 14px;
    color: #999999;
  }

  .right {
    font-size: 14px;
    color: #999999;
  }
}

/deep/ .vux-header {
  height: 50px;
}

</style>
