<template>
  <section class="main" style="width: 100vw">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="'添加用药'" @onBack="() => $router.go(-1)"></headNavigation>
    </van-sticky>
    <div v-if="alldata.name"
         style="display: flex;justify-content: space-between; background: #FFF; padding: 15px; margin-bottom: 20px;">
      <div style="width:100px;height:100px;border:1px solid rgba(0,0,0,0.1);overflow:hidden">
        <img :src="alldata.icon?alldata.icon:formData.medicineIcon" style="width:100%" alt="" srcset="">
      </div>
      <div style="margin-left:20px; margin-right: 15px; width: calc(100% - 120px)">
        <div style="font-size:16px;color: #333333;line-height:26px;font-weight:600">
          {{ alldata.name ? alldata.name : '' }}</div>
        <div style="font-size:12px;color: #666666;line-height:20px">{{ alldata.genericName }}</div>
        <div style="font-size:12px;color: #666666;line-height:20px">{{ alldata.manufactor }}</div>
        <div style="font-size:12px;color: #666666;line-height:20px">{{ alldata.spec }}</div>
        <div style="font-size:12px;color: #999999;line-height:20px">
          <span v-if="alldata.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>
          {{ alldata.gyzz }}
        </div>
      </div>
    </div>
    <van-cell-group :style="{ width: screenWidth + 'px' }">
      <div style="display: flex; align-items: center; margin-left: 15px; padding-top: 10px;">
        <div style="width: 3px; height: 21px; background: #66E0A7;"></div>
        <span style="color: #333; font-size: 16px; font-weight: bold; margin-left: 10px;">提醒内容</span>
      </div>

      <van-cell title="药品名称：" is-link @click="goMedicine">
        <div>{{ formData.medicineName ? formData.medicineName : '请选择' }}</div>
      </van-cell>

      <van-cell v-if="alldata.name" title="使用说明" is-link @click="gouserWay"/>

      <van-cell title="添加数量（盒）">
        <div>
          <van-stepper v-model="formData.numberOfBoxes"/>
        </div>
      </van-cell>

      <van-cell title="用药频次" is-link @click="openPicker('frequency')">
        <div :style="{color: formData.frequencyData.length > 0 ? '' : '#3BD26A'}">
          {{ formData.frequencyData.length > 0 ? formData.frequencyData.map(item => item).join('') : '请选择' }}
        </div>
      </van-cell>
      <van-cell title="每日剂量" is-link @click="openPicker('dose')">
        <div>{{ formData.doseData.length > 0 ? formData.doseData.map(item => item).join('') : '请选择' }}</div>
      </van-cell>
    </van-cell-group>
    <van-cell-group style="margin-top: 20px;" :style="{ width: screenWidth + 'px' }">
      <div style="display: flex; align-items: center; margin-left: 15px; padding-top: 10px;">
        <div style="width: 3px; height: 21px; background: #66E0A7;"></div>
        <span style="color: #333; font-size: 16px; font-weight: bold; margin-left: 10px;">提醒设置</span>
      </div>

      <van-cell title="用药周期：" is-link @click="openPicker('cycle')">
        <div>{{ formData.cycleData.length > 0 ? formData.cycleData.map(item => item).join('') : '请选择' }}</div>
      </van-cell>

      <van-cell title="用药时间">
        <div :style="{color: formData.frequencyData.length > 0 ? '' : '#3BD26A'}">
          {{ formData.frequencyData.length > 0 ? formData.frequencyData.map(item => item).join('') : '请选择用药频次' }}
        </div>
      </van-cell>
    </van-cell-group>
    <div v-if="formData.frequencyData.length > 0" style="padding-bottom: 15px; padding-top: 15px; background: #FFF">
      <van-grid :gutter="10" :column-num="4">
        <van-grid-item v-for="(item, index) in formData.patientDrugsTimeSettingList" :key="index"
                       @click="setTime(index)">
          <div style="font-size: 12px;">
            {{ item.dayOfTheCycle ? '第' + item.dayOfTheCycle + '天' : getTimeName(index) }}
          </div>
          <div style="font-size: 16px;" v-if="item.triggerTimeOfTheDay">
            {{ getTime(item.triggerTimeOfTheDay) }}
          </div>
          <div v-else style="height: 16px">

          </div>
        </van-grid-item>
      </van-grid>
    </div>

    <div style="display: flex; justify-content: center">
      <van-button type="primary"
                  :style="{background: isSave ? '#67E0A7' : '#C4C4C4'}"
                  style="width: 80%; margin-top: 50px; border: none; margin-bottom: 30px;"
                  round @click="save()">保存
      </van-button>
    </div>

    <van-popup v-model="isFrequency" position="bottom">
      <van-picker v-if="pickerType === 'frequency'" show-toolbar title="用药频次" :columns="frequency"
                  @cancel="() => {isFrequency = false}" @confirm="onFrequencyConfirm" @change="onFrequencyChange"/>
      <van-picker v-if="pickerType === 'dose'" show-toolbar title="每次剂量" :columns="dose"
                  @cancel="() => {isFrequency = false}" @confirm="onDoseConfirm" @change="onDoseChange">
        <template slot="columns-bottom">
          <div style="width: 100%; display: flex; justify-content: center;padding-bottom: 15px; position: absolute; z-index: 999; bottom: 20px">
            <div style="width: 50%">
              <van-field v-model="doseCount" placeholder="请输入剂量" v-if="isDoseOther" type="number" maxlength="8"
                         style="border: 1px solid #EEE; padding: 10px !important; border-radius: 5px"/>
            </div>
          </div>
        </template>
      </van-picker>
      <van-picker v-if="pickerType === 'cycle'" show-toolbar title="用药周期" :columns="cycle"
                  @cancel="() => {isFrequency = false}" @confirm="onCycleConfirm"/>
      <van-picker v-if="pickerType === 'time'" show-toolbar title="用药时间" :columns="time"
                  @cancel="() => {isFrequency = false}" @confirm="onTimeConfirm"/>
    </van-popup>

  </section>
</template>

<script>
import Vue from 'vue'
import {sysDrugsPagedetail, getpatientDrugs, FixpatientDrugs, addpatientDrugs} from '@/api/medicine.js'
import {Button, Cell, CellGroup, Stepper, Popup, Picker, Grid, GridItem, Toast} from 'vant'
import moment from 'moment'

Vue.use(Cell)
Vue.use(Stepper)
Vue.use(CellGroup)
Vue.use(Button)
Vue.use(Picker)
Vue.use(Popup)
Vue.use(Grid)
Vue.use(GridItem)
export default {
  data () {
    return {
      alldata: {},
      screenWidth: window.innerWidth,
      isFrequency: false,
      pickerType: '',
      isSave: false,
      doseCount: '',
      isDoseOther: false,
      frequency: [
        // 第一列
        {
          values: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30'],
          defaultIndex: 0
        },
        // 第二列
        {
          values: ['天', '小时', '周', '月'],
          defaultIndex: 0
        },
        // 第三列
        {
          values: ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
          defaultIndex: 0
        },
        // 第四列
        {
          values: ['次'],
          defaultIndex: 0
        }
      ],
      dose: [
        {
          values: ['0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0'],
          defaultIndex: 0
        },
        {
          values: ['片', '粒', '丸', '滴', '袋', '支', '喷', '克', '毫克', '毫升'],
          defaultIndex: 0
        }
      ],
      cycle: [
        {
          values: [],
          defaultIndex: 0
        }
      ],
      time: [
        {
          values: ['00时', '01时', '02时', '03时', '04时', '05时', '06时', '07时', '08时', '09时', '10时', '11时', '12时', '13时', '14时', '15时', '16时', '17时', '18时', '19时', '20时', '21时', '22时', '23时'],
          defaultIndex: 0
        },
        {
          values: ['00分', '15分', '30分', '45分'],
          defaultIndex: 0
        }
      ],
      formData: {
        medicineName: '',
        numberOfBoxes: 1,
        frequencyData: [],
        doseData: [],
        cycleData: [],
        patientDrugsTimeSettingList: [],
        drugsTime: '',
        buyDrugsReminderTime: moment().subtract(-14, 'days').format('YYYY-MM-DD')
      },
      drugsId: '',
      currentTime: '',
      currentIndex: 0,
      medicineId: '',
      isLoading: false
    }
  },
  watch: {
    formData: {
      handler (newVal, oldVal) {
        this.isSave = this.isCanSave()
      },
      deep: true // 是否深度监听对象内部的属性，默认为false
    }
  },
  mounted () {
    this.cycle[0].values.push('长期')
    for (let i = 1; i < 100; i++) {
      this.cycle[0].values.push(i + '天')
    }
    if (this.$route.query && this.$route.query.medicineId) {
      this.medicineId = this.$route.query.medicineId
      this.getDetail()
    } else if (this.$route.query && this.$route.query.drugsId) {
      this.drugsId = this.$route.query.drugsId
      this.getDrugDetail(this.drugsId)
    } else if (this.$route.query && this.$route.query.medicineName) {
      this.formData.medicineName = this.$route.query.medicineName
    }
  },
  methods: {
    getDetail () {
      getpatientDrugs({id: this.medicineId})
        .then(res => {
          this.drugsId = res.data.drugsId
          if (this.drugsId) {
            this.getDrugDetail(this.drugsId)
          }
          this.formData.buyDrugsReminderTime = res.data.buyDrugsReminderTime
          this.formData.drugsTime = res.data.drugsTime
          this.formData.patientDrugsTimeSettingList = res.data.patientDrugsTimeSettingList
          this.formData.numberOfBoxes = res.data.numberOfBoxes
          this.formData.medicineName = res.data.medicineName
          this.formData.frequencyData = [res.data.cycleDuration, '', res.data.numberOfDay, '次']
          if (res.data.timePeriod === 'day') {
            this.formData.frequencyData[1] = '天'
          } else if (res.data.timePeriod === 'week') {
            this.formData.frequencyData[1] = '周'
          } else if (res.data.timePeriod === 'hour') {
            this.formData.frequencyData[1] = '小时'
          } else if (res.data.timePeriod === 'moon') {
            this.formData.frequencyData[1] = '月'
          }
          this.formData.doseData = [res.data.dose === -1 ? '外用' : res.data.dose, res.data.unit]
          this.formData.cycleData = [res.data.cycleDay === 0 ? '长期' : res.data.cycleDay + '天']
          this.isSave = this.isCanSave()
        })
    },
    // 判断是否可以保存
    isCanSave () {
      let flag = false
      if (!this.formData.medicineName || this.formData.frequencyData.length === 0 || this.formData.doseData.length === 0 || this.formData.cycleData.length === 0) {
        return flag
      }

      this.formData.patientDrugsTimeSettingList.forEach(item => {
        if (item.triggerTimeOfTheDay === '') {
          flag = true
        }
      })
      if (flag) {
        return false
      }
      return true
    },
    save () {
      if (!this.isSave) {
        return
      }
      if (!this.formData.medicineName) {
        Toast('请选择药品')
        return
      }

      if (this.formData.frequencyData.length === 0) {
        Toast('请选择用药频次')
        return
      }

      if (this.formData.doseData.length === 0) {
        Toast('请选择每日剂量')
        return
      }

      if (this.formData.cycleData.length === 0) {
        Toast('请选择用药周期')
        return
      }

      let flag = false
      this.formData.patientDrugsTimeSettingList.forEach(item => {
        if (item.triggerTimeOfTheDay === '') {
          flag = true
        }
      })
      if (flag) {
        Toast('请设置用药时间')
        return
      }

      // 系统用药
      let params = {}
      this.isLoading = true
      if (this.medicineId) {
        // 编辑用药
        params = {
          'buyDrugsReminderTime': this.formData.buyDrugsReminderTime,
          'checkinNum': 0,
          'cycle': this.formData.cycleData[0] === '长期' ? 0 : 1,
          'cycleDay': this.formData.cycleData[0] === '长期' ? 0 : parseInt(this.formData.cycleData[0]),
          'drugsId': this.drugsId,
          'numberOfBoxes': this.formData.numberOfBoxes,
          'medicineName': this.formData.medicineName,
          'patientId': localStorage.getItem('patientId'),
          'unit': this.formData.doseData[1],
          dose: this.formData.doseData[0] === '外用' ? -1 : this.formData.doseData[0],
          'status': 0,
          'id': this.medicineId,
          'timePeriod': '',
          'cycleDuration': this.formData.frequencyData[0],
          'numberOfDay': this.formData.frequencyData[2],
          'medicineIcon': this.alldata.icon,
          'patientDrugsTimeSettingList': this.formData.patientDrugsTimeSettingList,
          drugsTime: this.formData.drugsTime
        }
        // 时间周期
        if (this.formData.frequencyData[1] === '天') {
          params.timePeriod = 'day'
        }
        if (this.formData.frequencyData[1] === '周') {
          params.timePeriod = 'week'
        }
        if (this.formData.frequencyData[1] === '小时') {
          params.timePeriod = 'hour'
        }
        if (this.formData.frequencyData[1] === '月') {
          params.timePeriod = 'moon'
        }

        if (this.$route.query && this.$route.query.imMessageId) {
          params.messageId = this.$route.query.imMessageId
          // im的推荐消息，才传这个为1
          params.imRecommendReceipt = 1
        }

        FixpatientDrugs(params).then((res) => {
          this.isLoading = false
          if (res.code === 0) {
            this.$router.go(-1)
          }
        })
      } else {
        if (this.drugsId) {
          params = {
            'buyDrugsReminderTime': this.formData.buyDrugsReminderTime,
            'checkinNum': 0,
            'cycle': this.formData.cycleData[0] === '长期' ? 0 : 1,
            'cycleDay': this.formData.cycleData[0] === '长期' ? 0 : parseInt(this.formData.cycleData[0]),
            'drugsId': this.drugsId,
            'numberOfBoxes': this.formData.numberOfBoxes,
            'medicineName': this.formData.medicineName,
            'patientId': localStorage.getItem('patientId'),
            'unit': this.formData.doseData[1],
            dose: this.formData.doseData[0] === '外用' ? -1 : this.formData.doseData[0],
            'status': 0,
            'timePeriod': '',
            'cycleDuration': this.formData.frequencyData[0],
            'numberOfDay': this.formData.frequencyData[2],
            'medicineIcon': this.alldata.icon,
            'patientDrugsTimeSettingList': this.formData.patientDrugsTimeSettingList,
            drugsTime: this.formData.drugsTime
          }
          // 时间周期
          if (this.formData.frequencyData[1] === '天') {
            params.timePeriod = 'day'
          }
          if (this.formData.frequencyData[1] === '周') {
            params.timePeriod = 'week'
          }
          if (this.formData.frequencyData[1] === '小时') {
            params.timePeriod = 'hour'
          }
          if (this.formData.frequencyData[1] === '月') {
            params.timePeriod = 'moon'
          }
        } else {
          params = {
            'buyDrugsReminderTime': this.formData.buyDrugsReminderTime,
            'checkinNum': 0,
            'cycle': this.formData.cycleData[0] === '长期' ? 0 : 1,
            'cycleDay': this.formData.cycleData[0] === '长期' ? 0 : parseInt(this.formData.cycleData[0]),
            'drugsId': this.drugsId,
            'numberOfBoxes': this.formData.numberOfBoxes,
            'medicineName': this.formData.medicineName,
            'patientId': localStorage.getItem('patientId'),
            'unit': this.formData.doseData[1],
            dose: this.formData.doseData[0] === '外用' ? -1 : this.formData.doseData[0],
            'status': 0,
            'timePeriod': '',
            'cycleDuration': this.formData.frequencyData[0],
            'numberOfDay': this.formData.frequencyData[2],
            'patientDrugsTimeSettingList': this.formData.patientDrugsTimeSettingList,
            drugsTime: this.formData.drugsTime
          }
          // 时间周期
          if (this.formData.frequencyData[1] === '天') {
            params.timePeriod = 'day'
          }
          if (this.formData.frequencyData[1] === '周') {
            params.timePeriod = 'week'
          }
          if (this.formData.frequencyData[1] === '小时') {
            params.timePeriod = 'hour'
          }
          if (this.formData.frequencyData[1] === '月') {
            params.timePeriod = 'moon'
          }
        }

        if (this.$route.query && this.$route.query.imMessageId) {
          params.messageId = this.$route.query.imMessageId
          // im的推荐消息，才传这个为1
          params.imRecommendReceipt = 1
        }
        addpatientDrugs(params)
          .then((res) => {
            this.isLoading = false
            this.submitStatus = false
            this.$router.go(-1)
          })
      }
    },
    setTime (index) {
      this.pickerType = 'time'
      this.currentIndex = index
      // 赋值当前时间
      if (this.formData.frequencyData[1] === '周' || this.formData.frequencyData[1] === '月') {
        this.time[0].defaultIndex = parseInt(this.formData.patientDrugsTimeSettingList[index].dayOfTheCycle) - 1
        let arr = this.formData.patientDrugsTimeSettingList[index].triggerTimeOfTheDay.split(':')
        this.time[1].defaultIndex = parseInt(arr[0])
        if (parseInt(arr[1]) === 15) {
          this.time[2].defaultIndex = 1
        } else if (parseInt(arr[1]) === 30) {
          this.time[2].defaultIndex = 2
        } else if (parseInt(arr[1]) === 45) {
          this.time[2].defaultIndex = 3
        } else {
          this.time[2].defaultIndex = 0
        }
      } else {
        let arr = this.formData.patientDrugsTimeSettingList[index].triggerTimeOfTheDay.split(':')
        this.time[0].defaultIndex = parseInt(arr[0])
        if (parseInt(arr[1]) === 15) {
          this.time[1].defaultIndex = 1
        } else if (parseInt(arr[1]) === 30) {
          this.time[1].defaultIndex = 2
        } else if (parseInt(arr[1]) === 45) {
          this.time[1].defaultIndex = 3
        } else {
          this.time[1].defaultIndex = 0
        }
      }

      setTimeout(() => {
        this.isFrequency = true
      }, 100)
    },
    onTimeConfirm (values) {
      if (this.formData.frequencyData[1] === '周' || this.formData.frequencyData[1] === '月') {
        let arr = values[0].match(/\d+/g)
        let d = parseInt(arr[0])
        let h = parseInt(values[1])
        let m = parseInt(values[2])
        let time = (h > 9 ? h : '0' + h) + ':' + (m > 9 ? m : '0' + m) + ':00'
        let flag = false
        this.formData.patientDrugsTimeSettingList.forEach(item => {
          if (d === item.dayOfTheCycle && item.triggerTimeOfTheDay === time) {
            flag = true
          }
        })
        // 判断是否重复 如果是当前时间不提示，不是当前时间，则需要提示
        if (flag && !(this.formData.patientDrugsTimeSettingList[this.currentIndex].triggerTimeOfTheDay === time &&
          d === this.formData.patientDrugsTimeSettingList[this.currentIndex].dayOfTheCycle)) {
          Toast('不可设置重复的用药时间')
        } else {
          this.formData.patientDrugsTimeSettingList[this.currentIndex].triggerTimeOfTheDay = time
          this.formData.patientDrugsTimeSettingList[this.currentIndex].dayOfTheCycle = d
          this.isFrequency = false
          this.formData.drugsTime = ''
          this.formData.patientDrugsTimeSettingList.forEach(item => {
            if (this.formData.drugsTime) {
              this.formData.drugsTime = this.formData.drugsTime + ' ' + this.getTime(item.triggerTimeOfTheDay)
            } else {
              this.formData.drugsTime = '第' + d + '天' + ' | ' + this.getTime(item.triggerTimeOfTheDay)
            }
          })
        }
      } else {
        let h = parseInt(values[0])
        let m = parseInt(values[1])
        let time = (h > 9 ? h : '0' + h) + ':' + (m > 9 ? m : '0' + m) + ':00'
        let flag = false
        this.formData.patientDrugsTimeSettingList.forEach(item => {
          if (item.triggerTimeOfTheDay === time) {
            flag = true
          }
        })
        if (flag && !(this.formData.patientDrugsTimeSettingList[this.currentIndex].triggerTimeOfTheDay === time)) {
          Toast('不可设置重复的用药时间')
        } else {
          this.formData.patientDrugsTimeSettingList[this.currentIndex].triggerTimeOfTheDay = time
          this.isFrequency = false
          this.formData.drugsTime = ''
          this.formData.patientDrugsTimeSettingList.forEach(item => {
            if (this.formData.drugsTime) {
              this.formData.drugsTime = this.formData.drugsTime + ',' + this.getTime(item.triggerTimeOfTheDay)
            } else {
              this.formData.drugsTime = this.getTime(item.triggerTimeOfTheDay)
            }
          })
        }
      }
    },
    // 选择用药品
    goMedicine () {
      if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.replace({
          path: '/patient/medicine/storeroom',
          query: {
            imMessageId: this.$route.query.imMessageId
          }
        })
      } else {
        this.$router.replace('/patient/medicine/storeroom')
      }
    },
    // 选择弹窗
    openPicker (type) {
      if (!this.formData.medicineName) {
        Toast('请选择药品')
        return
      }
      if (type === 'dose') {
        this.isDoseOther = false
        this.doseCount = ''
        this.dose[0].defaultIndex = 0
        // 判断用药是其他、外用
        if (this.formData.doseData && this.formData.doseData.length === 2 && !this.formData.doseData[1]) {
          this.dose = [
            {
              values: ['0.25', '0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0', '外用', '其他'],
              defaultIndex: 0
            }
          ]
        } else {
          this.dose = [
            {
              values: ['0.25', '0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0', '外用', '其他'],
              defaultIndex: 0
            },
            {
              values: ['片', '粒', '丸', '滴', '袋', '支', '喷', '克', '毫克', '毫升'],
              defaultIndex: 0
            }
          ]
          this.dose[1].defaultIndex = 0
        }
        setTimeout(() => {
          if (this.formData.doseData.length > 0) {
            let doesVal = this.formData.doseData[0] + ''
            // 判断是否外用
            if (doesVal === '外用') {
              this.dose[0].defaultIndex = 21
            } else {
              // 判断是否带小数点
              doesVal = doesVal.indexOf('.') > -1 ? doesVal : doesVal + '.0'
              let valPos = this.dose[0].values.indexOf(doesVal)
              if (valPos > -1) {
                this.dose[0].defaultIndex = valPos
              } else if (valPos === -1 && doesVal) {
                this.isDoseOther = true
                this.dose[0].defaultIndex = this.dose[0].values.length - 1
                this.doseCount = this.formData.doseData[0]
              }
              if (this.formData.doseData[1]) {
                let valPos1 = this.dose[1].values.indexOf(this.formData.doseData[1])
                if (valPos1 > -1) {
                  this.dose[1].defaultIndex = valPos1
                }
              }
            }
          }
        }, 200)
      }
      this.pickerType = type
      this.isFrequency = true
    },
    onCycleConfirm (values) {
      this.formData.cycleData = values
      this.isFrequency = false
    },
    onDoseChange (picker, value, index) {
      if (value[0] === '其他' || value[0] === '外用') {
        if (value[0] === '其他') {
          this.isDoseOther = true
        } else {
          this.isDoseOther = false
        }
        this.dose = [
          {
            values: ['0.25', '0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0', '外用', '其他'],
            defaultIndex: value[0] === '其他' ? 22 : 21
          }
        ]
      } else {
        this.isDoseOther = false
        if (this.dose.length === 1) {
          let currentIndex = this.dose[0].values.findIndex(item => item === value[0])
          this.dose = [
            {
              values: ['0.25', '0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0', '外用', '其他'],
              defaultIndex: currentIndex
            },
            {
              values: ['片', '粒', '丸', '滴', '袋', '支', '喷', '克', '毫克', '毫升'],
              defaultIndex: 0
            }
          ]
        }
      }
    },
    onDoseConfirm (values) {
      if (values[0] === '其他' && !this.doseCount) {
        Toast('请输入剂量')
      } else {
        if (values[0] === '外用') {
          this.formData.doseData = ['外用', '']
        } else if (values[0] === '其他') {
          if (this.doseCount === '0' || this.doseCount === '0.') {
            Toast('请输入正确的剂量')
            return
          }
          this.formData.doseData = values
          this.formData.doseData[0] = this.doseCount
          this.formData.doseData[1] = ''
          this.doseCount = ''
        } else {
          this.formData.doseData = values
        }
        this.isFrequency = false
      }
    },
    // 用药频次弹窗确认
    onFrequencyConfirm (values) {
      this.formData.frequencyData = values
      this.isFrequency = false

      if (this.formData.frequencyData[1] === '周' || this.formData.frequencyData[1] === '月') {
        this.time = [
          {
            values: [],
            defaultIndex: 0
          },
          {
            values: ['00时', '01时', '02时', '03时', '04时', '05时', '06时', '07时', '08时', '09时', '10时', '11时', '12时', '13时', '14时', '15时', '16时', '17时', '18时', '19时', '20时', '21时', '22时', '23时'],
            defaultIndex: 0
          },
          {
            values: ['00分', '15分', '30分', '45分'],
            defaultIndex: 0
          }
        ]
        if (this.formData.frequencyData[1] === '周') {
          this.time[0].values = ['第1天', '第2天', '第3天', '第4天', '第5天', '第6天', '第7天', '第8天', '第9天']
        } else {
          for (let i = 1; i < 31; i++) {
            this.time[0].values.push('第' + i + '天')
          }
        }
        this.formData.patientDrugsTimeSettingList = []
        for (let i = 0; i < Number(this.formData.frequencyData[2]); i++) {
          this.formData.patientDrugsTimeSettingList.push({
            'triggerTimeOfTheDay': '',
            'dayOfTheCycle': i + 1
          })
        }
      } else {
        this.time = [
          {
            values: ['00时', '01时', '02时', '03时', '04时', '05时', '06时', '07时', '08时', '09时', '10时', '11时', '12时', '13时', '14时', '15时', '16时', '17时', '18时', '19时', '20时', '21时', '22时', '23时'],
            defaultIndex: 0
          },
          {
            values: ['00分', '15分', '30分', '45分'],
            defaultIndex: 0
          }
        ]
      }

      if (this.formData.frequencyData[1] === '周' || this.formData.frequencyData[1] === '月') {
        this.formData.patientDrugsTimeSettingList = []
        for (let i = 0; i < Number(this.formData.frequencyData[2]); i++) {
          this.formData.patientDrugsTimeSettingList.push({
            'triggerTimeOfTheDay': '',
            'dayOfTheCycle': i + 1
          })
        }
      } else if (Number(this.formData.frequencyData[2]) === 1) {
        this.formData.patientDrugsTimeSettingList = [{
          'triggerTimeOfTheDay': '08:00:00',
          'theFirstTime': 1
        }]
      } else if (Number(this.formData.frequencyData[2]) === 2) {
        this.formData.patientDrugsTimeSettingList = [{
          'triggerTimeOfTheDay': '08:00:00',
          'theFirstTime': 1
        }, {
          'triggerTimeOfTheDay': '11:00:00',
          'theFirstTime': 2
        }]
      } else if (Number(this.formData.frequencyData[2]) === 3) {
        this.formData.patientDrugsTimeSettingList = [{
          'triggerTimeOfTheDay': '08:00:00',
          'theFirstTime': 1
        },
        {
          'triggerTimeOfTheDay': '11:00:00',
          'theFirstTime': 2
        },
        {
          'triggerTimeOfTheDay': '16:00:00',
          'theFirstTime': 3
        }]
      } else if (Number(this.formData.frequencyData[2]) === 4) {
        this.formData.patientDrugsTimeSettingList = [{
          'triggerTimeOfTheDay': '08:00:00',
          'theFirstTime': 1
        }, {
          'triggerTimeOfTheDay': '11:00:00',
          'theFirstTime': 2
        }, {
          'triggerTimeOfTheDay': '16:00:00',
          'theFirstTime': 3
        }, {
          'triggerTimeOfTheDay': '20:00:00',
          'theFirstTime': 4
        }]
      } else {
        this.formData.patientDrugsTimeSettingList = []
        for (let i = 0; i < Number(this.formData.frequencyData[2]); i++) {
          this.formData.patientDrugsTimeSettingList.push({
            'triggerTimeOfTheDay': '',
            'theFirstTime': i + 1
          })
        }
      }
    },
    // 用药频次修改
    onFrequencyChange (picker, values) {
      let Indexes = picker.getIndexes()
      if (Indexes[1] === 1) {
        this.$set(this.frequency, 2, {
          values: ['1'],
          defaultIndex: 0
        })
      } else if (Indexes[0] !== 0 && Indexes[1] !== 1) {
        this.$set(this.frequency, 2, {
          values: ['1'],
          defaultIndex: 0
        })
      } else {
        this.$set(this.frequency, 2, {
          values: ['1', '2', '3', '4', '5', '6', '7', '8', '9'],
          defaultIndex: 0
        })
      }
    },
    // 使用说明
    gouserWay () {
      if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.push({
          path: '/patient/medicine/showuser',
          query: {drugsId: this.drugsId, imMessageId: this.$route.query.imMessageId}
        })
      } else {
        this.$router.push({
          path: '/patient/medicine/showuser',
          query: {drugsId: this.drugsId}
        })
      }
    },
    // 跳转到详情页面
    getDrugDetail (i) {
      sysDrugsPagedetail({id: i}).then((res) => {
        if (res.code === 0 && res.data && res.data.length > 0) {
          this.alldata = res.data[0]
          this.formData.medicineName = this.alldata.name
        }
      })
    },
    // 用药第几次
    getTimeName (index) {
      switch (index) {
        case 0:
          return '第一次'
        case 1:
          return '第二次'
        case 2:
          return '第三次'
        case 3:
          return '第四次'
        case 4:
          return '第五次'
        case 5:
          return '第六次'
        case 6:
          return '第七次'
        case 7:
          return '第八次'
        case 8:
          return '第九次'
      }
    },
    getTime (time) {
      if (time) {
        return time.substring(0, 2) + time.substring(2, 5)
      }
      return ''
    }
  }
}

</script>

<style scoped lang="less">
/deep/ .van-grid-item__content {
  background: #BAE9B9;
  border-radius: 9px;
  border: 0px;
}

/deep/ .van-cell__title {
  color: #333;
}

/deep/ .van-cell__value {
  color: #333;
}

/deep/ .van-cell {
  padding: 17px 16px;
}

/deep/ .van-icon {
  display: flex;
  align-items: center;
}
</style>
