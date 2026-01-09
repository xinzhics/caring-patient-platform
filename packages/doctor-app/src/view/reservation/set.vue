<template>
  <section class="allContent">
    <x-header style="margin-bottom:0px !important" :left-options="{backText: ''}">预约设置</x-header>
    <group>
      <x-switch :class="stringValue===1?'close':'open'" title="在线预约" inline-desc="关闭后将不再接收新的预约患者" :value-map="[1, 0]"
                v-model="stringValue"
                @on-change="setreservation"></x-switch>
      <x-switch :class="appointmentReview===1?'close':'open'" title="预约无需审核" inline-desc="开启后，新的预约患者无需审核直接预约成功" :value-map="[1, 0]" v-model="appointmentReview"
                @on-change="changeAppointmentReview"></x-switch>
    </group>
    <div v-if="stringValue===0">
      <div class="setContent">
        <p class="title">
          <span class="one">每周号源设置</span>
          <span>点击日期可修改当天号源</span>
        </p>


        <div class="setDate">
          <div :class="selectNumber===1?'getDate mydate':'mydate'" @click="selectWeek(1)">
            <p class="date">周一</p>
            <p class="number">{{ alldata.numOfMondayMorning + alldata.numOfMondayAfternoon }}人</p>
          </div>
          <div :class="selectNumber===2?'getDate mydate':'mydate'" @click="selectWeek(2)">
            <p class="date">周二</p>
            <p class="number">{{ alldata.numOfTuesdayMorning + alldata.numOfTuesdayAfternoon }}人</p>
          </div>
          <div :class="selectNumber===3?'getDate mydate':'mydate'" @click="selectWeek(3)">
            <p class="date">周三</p>
            <p class="number">{{ alldata.numOfWednesdayMorning + alldata.numOfWednesdayAfternoon }}人</p>
          </div>
          <div :class="selectNumber===4?'getDate mydate':'mydate'" @click="selectWeek(4)">
            <p class="date">周四</p>
            <p class="number">{{ alldata.numOfThursdayMorning + alldata.numOfThursdayAfternoon }}人</p>
          </div>
          <div :class="selectNumber===5?'getDate mydate':'mydate'" @click="selectWeek(5)">
            <p class="date">周五</p>
            <p class="number">{{ alldata.numOfFridayMorning + alldata.numOfFridayAfternoon }}人</p>
          </div>
          <div :class="selectNumber===6?'getDate mydate':'mydate'" @click="selectWeek(6)">
            <p class="date">周六</p>
            <p class="number">{{ alldata.numOfSaturdayMorning + alldata.numOfSaturdayAfternoon }}人</p>
          </div>
          <div :class="selectNumber===7?'getDate mydate':'mydate'" @click="selectWeek(7)">
            <p class="date">周日</p>
            <p class="number">{{ alldata.numOfSundayMorning + alldata.numOfSundayAfternoon }}人</p>
          </div>
        </div>
      </div>

      <div v-if="selectNumber===1" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfMondayMorning + alldata.numOfMondayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfMondayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfMondayAfternoon" :fillable="true"></x-number>
      </div>

      <div v-if="selectNumber===2" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfTuesdayMorning + alldata.numOfTuesdayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfTuesdayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfTuesdayAfternoon" :fillable="true"></x-number>
      </div>
      <div v-if="selectNumber===3" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfWednesdayMorning + alldata.numOfWednesdayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfWednesdayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfWednesdayAfternoon" :fillable="true"></x-number>
      </div>
      <div v-if="selectNumber===4" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfThursdayMorning + alldata.numOfThursdayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfThursdayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfThursdayAfternoon" :fillable="true"></x-number>
      </div>
      <div v-if="selectNumber===5" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfFridayMorning + alldata.numOfFridayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfFridayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfFridayAfternoon" :fillable="true"></x-number>
      </div>
      <div v-if="selectNumber===6" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfSaturdayMorning + alldata.numOfSaturdayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfSaturdayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfSaturdayAfternoon" :fillable="true"></x-number>
      </div>
      <div v-if="selectNumber===7" class="canset">
        <p class="title">
          <span class="one">可约人数设置</span>
          <span>{{ alldata.numOfSundayMorning + alldata.numOfSundayAfternoon }}人</span>
        </p>
        <x-number name="上午" title="上午" :min="0" v-model="alldata.numOfSundayMorning" :fillable="true"></x-number>
        <x-number name="下午" title="下午" :min="0" v-model="alldata.numOfSundayAfternoon" :fillable="true"></x-number>
      </div>
      <x-button style="width:60%;background:#3F86FF;color:#fff;margin-top:30px" @click.native="sureBtn">确定</x-button>
    </div>
    <van-dialog v-model="showOne" class="dialog-demo" :show-confirm-button="false" :className="'reservationDialog'">
      <div
          @click="showOne=false"
          style="background:#3F86FF;color: #fff;height: 40px;width: 50%;text-align: center;
        line-height: 40px;position:absolute;bottom: 40px;left: 50%;transform: translateX(-50%);border-radius: 20px"
      >
        知道了
      </div>
    </van-dialog>
  </section>
</template>
<script>
import {Cell, Group, XButton, XNumber, XSwitch} from 'vux'
import Api from '@/api/Content.js'
import {Dialog} from 'vant'

export default {
  components: {
    XSwitch,
    Group,
    Cell,
    XNumber,
    XButton,
    Dialog
  },
  data() {
    return {
      stringValue: 1,
      appointmentReview: 1,
      selectNumber: 1,
      alldata: {},
      morning: 0,
      afternoon: 0,
      putData: {},
      listId: '',
      showOne: false,
      setGood: require('@/assets/drawable-xhdpi/setGood.jpg'),
    }
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    changeAppointmentReview() {
      const params = {
        appointmentReview: this.appointmentReview === 1 ? 'need_review' : 'no_review',
        id: localStorage.getItem('caring_doctor_id')
      }
      console.log(params)
      Api.setDoctorInfo(params).then((res) => {
        if (res.data.code === 0) {
        }
      })
    },
    getInfo() {
      const params = {
        doctorId: localStorage.getItem('caring_doctor_id'),
      }
      Api.getappointConfig(params).then((res) => {
        if (res.data.code === 0) {
          // this.$vux.toast.text('修改成功', 'center')
          this.alldata = res.data.data
          this.putData = res.data.data
          this.listId = res.data.data.id
          if (this.selectNumber === 1) {
            this.morning = this.alldata.numOfMondayMorning
            this.afternoon = this.alldata.numOfMondayAfternoon
          }
        }
      })
      const paramsT = {
        id: localStorage.getItem('caring_doctor_id'),
      }
      Api.getDoctorInfo(paramsT).then((res) => {
        if (res.data.code === 0) {
          console.log(res.data.data.appointmentReview)
          this.stringValue = res.data.data.closeAppoint
          this.appointmentReview = res.data.data.appointmentReview === 'need_review' ? 1 : 0
        }
      })

    },
    setreservation() {
      const params = {
        closeAppoint: this.stringValue,
        id: localStorage.getItem('caring_doctor_id')
      }
      Api.setDoctorInfo(params).then((res) => {
        if (res.data.code === 0) {
          // this.$vux.toast.text('修改成功', 'center')
        }
      })
    },
    selectWeek(i) {
      this.selectNumber = i
    },
    sureBtn() {
      const params = {
        "id": this.listId,
        "numOfMondayMorning": this.alldata.numOfMondayMorning,
        "numOfMondayAfternoon": this.alldata.numOfMondayAfternoon,
        "numOfTuesdayMorning": this.alldata.numOfTuesdayMorning,
        "numOfTuesdayAfternoon": this.alldata.numOfTuesdayAfternoon,
        "numOfWednesdayMorning": this.alldata.numOfWednesdayMorning,
        "numOfWednesdayAfternoon": this.alldata.numOfWednesdayAfternoon,
        "numOfThursdayMorning": this.alldata.numOfThursdayMorning,
        "numOfThursdayAfternoon": this.alldata.numOfThursdayAfternoon,
        "numOfFridayMorning": this.alldata.numOfFridayMorning,
        "numOfFridayAfternoon": this.alldata.numOfFridayAfternoon,
        "numOfSaturdayAfternoon": this.alldata.numOfSaturdayAfternoon,
        "numOfSaturdayMorning": this.alldata.numOfSaturdayMorning,
        "numOfSundayAfternoon": this.alldata.numOfSundayAfternoon,
        "numOfSundayMorning": this.alldata.numOfSundayMorning,
      }
      Api.PutappointConfig(params).then((res) => {
        if (res.data.code === 0) {
          this.showOne = true
          this.getInfo()
        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
.allContent {
  width: 100vw;
  height: 100vh;
}

/deep/ .open {
  .weui-switch {
    width: 44px;
    height: 22px;
    background: #FFFFFF;
  }

  .weui-switch:after {
    width: 17px;
    height: 17px;
    background: #55CA99;
    transform: translate(16px, 1px);
  }
}
/deep/ .close{
  .weui-switch {
    width: 44px;
    height: 22px;
    background: #EBEBEB;
  }
  .weui-switch:before {
    width: 0;
    height:0;
    background: #EBEBEB;
  }
  .weui-switch:after {
    width: 17px;
    height: 17px;
    background: #FFFFFF;
    transform: translate(-4px, 1px);
  }
}
.setContent {
  background: #fff;
  margin-top: 10px;
  padding: 0px 20px;

  .title {
    padding: 10px 0px;
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: #666;
    border-bottom: 1px solid #d9d9d9;

    .one {
      font-size: 14px;
      color: #333;
    }
  }

  .setDate {
    display: flex;
    justify-content: space-between;
    padding: 20px 0px;

    .mydate {
      width: 12.8%;
      text-align: center;
      padding: 5px 0px;

      .date {
        font-size: 14px;
        line-height: 30px;
      }

      .number {
        font-size: 13px;
        color: #999;
      }
    }

    .getDate {
      background: #3F86FF;
      border-radius: 6px;

      .date {
        color: #fff;
      }

      .number {
        color: #fff;
      }
    }
  }
}

.reservationDialog {
  height: 95.28776vw;
  border-radius: 10px;
  width: 80%;
  background-image: url('../../assets/drawable-xhdpi/setGood.jpg');
  background-size: 100% 100%
}

.canset {
  background: #fff;
  margin-top: 10px;

  .title {
    padding: 10px 20px;
    display: flex;
    justify-content: space-between;
    // font-size:14px ;
    color: #3F86FF;

    .one {
      // font-size: 14px;
      color: #333;
    }
  }
}
</style>
