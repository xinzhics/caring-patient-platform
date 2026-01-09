<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="setTime ? '设置时间' : '添加药品'"
                      @onBack="setTime ? setTime = false : $router.go(-1)" ></headNavigation>
    </van-sticky>
    <div v-if="setTime" style="min-height: 40vh;padding-bottom:15px; margin-top: 15px">
      <van-popup v-model="showTime" position="bottom" @opened="openedTime">
        <van-picker show-toolbar :columns="hourList" @cancel="showTime = false" @confirm="onSelectTime" ref="timePicker" />
      </van-popup>
      <div v-for="(item,index) in Number(frequency[2])" :key="index" style="background: #fff" @click="openTimePicker(index)">
        <van-cell v-if="frequency[1] ==='周' || frequency[1] ==='月'" :title="`第${index+1}次用药`" :ref="'frequencyValue' + index"
                  style="margin-top: 10px">
          {{getTimeSelected(index, 3)}}
        </van-cell>
        <van-cell v-else-if="frequency[1] ==='天'" :title="`第${index+1}次用药`" :ref="'frequencyValue' + index"
          style="margin-top: 10px">
          {{getTimeSelected(index, 2)}}
        </van-cell>
        <van-cell v-else-if="frequency[1] === '小时' && frequency[0] > 1"
                  title="首次用药时间" :ref="'frequencyValue' + index"
                  :value="getTimeSelected(index, 2)" is-link>
        </van-cell>
        <div v-if="frequency[1] === '小时' && frequency[0] == 1"
             style="display: flex;justify-content: space-between;height: 51px;line-height: 51px;padding-left: 13px;padding-right: 13px;color: #666">
          <div>用药时间</div>
          <div>整点</div>
        </div>
      </div>
      <div style="margin:0px auto; text-align: center">
        <van-button round style="width:208px;background:#66728C;color:#fff;margin-top: 32px" @click.native="setMedicationTime">
          完成
        </van-button>
      </div>
    </div>
    <div v-else style="min-height: 40vh;padding-bottom:30px; margin-top: 15px">
      <van-cell >
        <div style="display: flex;justify-content: space-between;margin:0px;">
          <div style="width:100px;height:100px;border:1px solid rgba(0,0,0,0.1);overflow:hidden">
            <img :src="alldata.icon ? alldata.icon : formData.medicineIcon" style="width:100%" alt="" srcset="">
          </div>
          <div style="margin-left:20px;">
            <p style="font-size:16px;color: #333333;line-height:26px;font-weight:600;padding: 0;margin: 0"> {{ alldata.name? alldata.name:formData.medicineName}}</p>
            <p style="font-size:12px;color: #666666;line-height:20px;padding: 0;margin: 0">{{ alldata.genericName }}</p>
            <p style="font-size:12px;color: #666666;line-height:20px;padding: 0;margin: 0">{{ alldata.manufactor }}</p>
            <p style="font-size:12px;color: #666666;line-height:20px;padding: 0;margin: 0">{{ alldata.spec }}</p>
            <p style="font-size:12px;color: #999999;line-height:20px;padding: 0;margin: 0">
              <span v-if="alldata.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{alldata.gyzz }}
            </p>
          </div>
        </div>
      </van-cell>
      <van-cell-group style="margin-top: 15px">
        <van-cell title="添加数量（盒)">
          <van-stepper min="1" integer v-model="formData.numberOfBoxes" />
        </van-cell>
        <van-cell class="numberOfBoxesDown" title="使用说明" is-link @click.native="gouserWay"></van-cell>
      </van-cell-group>
      <van-popup v-model="showFrequencyData" position="bottom">
        <van-picker show-toolbar :columns="frequencyData" @cancel="showFrequencyData = false" @confirm="onFrequencyConfirm"  />
      </van-popup>
      <van-cell-group style="margin-top: 15px">
        <van-cell is-link @click="showFrequencyData = true" :value="frequency.length > 1 ? frequency[0] + frequency[1] + frequency[2] + frequency[3] : ''">
          <span slot="title">
            <span style="color:#666666">
                用药频次
            </span>
            <span style="color:red;vertical-align: middle;">*</span>
          </span>
        </van-cell>
        <van-cell class="myValcolor" is-link :value="patientDrugsTimeSettingList.length > 0 || (this.frequency[1] === '小时' && this.frequency[0] == 1) ? '已配置' : ''"
                  @click.native="setTime=!setTime">
           <span slot="title">
              <span style="color:#666666">
                  用药时间
              </span>
              <span style="color:red;vertical-align: middle;">*</span>
            </span>
        </van-cell>
        <van-popup v-model="showDose" position="bottom">
          <van-picker show-toolbar :columns="dayHowList"  @cancel="showDose = false" @confirm="onDoseUnitConfirm" />
        </van-popup>
        <van-cell is-link @click="setDoseInfo" :value="formData.dose && formData.unit ? formData.dose + '/' + formData.unit : ''">
          <span slot="title">
            <span style="color:#666666">
                每次剂量
            </span>
            <span style="color:red;vertical-align: middle;">*</span>
          </span>
        </van-cell>
        <van-popup v-model="showCycleDay" position="bottom">
          <van-picker show-toolbar :columns="weekList"  @cancel="showCycleDay = false" @confirm="changeWeek" />
        </van-popup>
        <van-cell is-link @click="setCycleDayInfo" :value="formData.cycle === 0 ? '长期' : formData.cycleDay ?  formData.cycleDay + '天' : ''">
          <span slot="title">
            <span style="color:#666666">
                用药周期
            </span>
            <span style="color:red;vertical-align: middle;">*</span>
          </span>
        </van-cell>
      </van-cell-group>
      <div style="margin:0px auto;padding:40px 0px; text-align: center">
        <van-button round style="width:40vw;background:#66728C;color:#fff;" @click.native="submit">
          保存
        </van-button>
      </div>
    </div>
  </div>
</template>
<script>
import { getPatientDrugs, getSysDrugsDetail, updatePatientDrugs, addPatientDrugs } from '@/api/drugsApi.js'
import {Col, Dialog, Toast, Icon, Row, Cell, Sticky, CellGroup, Popup, Picker, Field, Button, Stepper} from 'vant'
import Vue from 'vue'

Vue.use(Sticky)
Vue.use(Icon)
Vue.use(Stepper)
Vue.use(Dialog)
Vue.use(Toast)
Vue.use(Row)
Vue.use(CellGroup)
Vue.use(Popup)
Vue.use(Picker)
Vue.use(Button)
Vue.use(Field)
Vue.use(Col)
Vue.use(Cell)
export default {
  data () {
    return {
      alldata: [
        {interaction: ''}
      ],
      id: '',
      dayList: [
        {
          name: '每日1次',
          value: '1',
          parent: 0
        },
        {
          name: '每日2次',
          value: '2',
          parent: 0
        },
        {
          name: '每日3次',
          value: '3',
          parent: 0
        },
        {
          name: '每日4次',
          value: '4',
          parent: 0
        }
      ],
      dayHowList: [
        {
          values: ['0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0'],
          defaultIndex: 0
        },
        {
          values: ['片', '粒', '丸', '滴', '袋', '支', '喷', '克', '毫克', '毫升'],
          defaultIndex: 0
        }
      ],
      weekList: ['长期'],
      hourList: [],
      setTime: false,
      formData: {
        numberOfDay: [],
        drugsTime: '',
        dose: '',
        unit: '',
        cycle: '',
        cycleDay: 0,
        numberOfBoxes: 1
      },
      selected: [],
      drugsId: '',
      frequencyData: [],
      showFrequencyData: false, // 打开用药频次的面板
      frequency: [],
      patientDrugsTimeSettingList: [], // 用药时间设置后，点完成 更新这里的数据
      showDose: false, // 设置剂量的面板
      showCycleDay: false, // 设置周期的面板
      showTime: false, // 设置用药时间的面板
      setTimeIndex: undefined // 设置的第几个用药的时间
    }
  },
  created () {
    this.frequencyDataF()
  },
  beforeMount () {
    const that = this
    if (that.$route.query && that.$route.query.id && that.$route.query.drugsId) {
      that.id = that.$route.query.id
      that.drugsId = that.$route.query.drugsId
      that.getDetail()
      if (that.$route.query.form) {
        that.formData = that.$route.query
      }
    }
    if (that.$route.query && that.$route.query.drugsId) {
      that.drugsId = that.$route.query.drugsId
      that.getInfo(that.drugsId)
    }
    for (let i = 1; i <= 100; i++) {
      that.weekList.push(i + '天')
    }
  },
  methods: {
    /**
     * 打开剂量的设置面板
     */
    setDoseInfo () {
      const dose = this.formData.dose + ''
      const unit = this.formData.unit + ''
      const doseIndex = this.dayHowList[0].values.findIndex(item => item === dose)
      const unitIndex = this.dayHowList[1].values.findIndex(item => item === unit)
      this.dayHowList[0].defaultIndex = doseIndex
      this.dayHowList[1].defaultIndex = unitIndex
      this.showDose = true
    },
    /**
     * 用药频率的确认
     */
    onFrequencyConfirm (value) {
      console.log(value)
      this.frequency = value
      this.formData.cycleDuration = value[0]
      this.formData.numberOfDay = value[2]
      if (value[1] === '天') {
        this.formData.timePeriod = 'day'
      }
      if (value[1] === '小时') {
        this.formData.timePeriod = 'hour'
      }
      if (value[1] === '周') {
        this.formData.timePeriod = 'week'
      }
      if (value[1] === '月') {
        this.formData.timePeriod = 'moon'
      }
      this.setTimeList()
      this.showFrequencyData = false
    },
    /**
     * 确定剂量的设置
     */
    onDoseUnitConfirm (value) {
      console.log(value)
      this.formData.dose = value[0]
      this.formData.unit = value[1]
      this.showDose = false
    },
    /**
     * 打开修改周期的弹窗
     */
    setCycleDayInfo () {
      this.cycleDayIndex = 0
      if (this.formData.cycle === 0) {
        this.cycleDayIndex = 0
      } else {
        this.cycleDayIndex = this.formData.cycleDay
      }
      this.showCycleDay = true
    },
    /**
     * 修改周期
     * @param value
     */
    changeWeek (value) {
      this.showCycleDay = false
      const index = this.weekList.findIndex(item => value === item)
      if (index === 0) {
        this.formData.cycle = 0
        this.formData.cycleDay = 0
      } else {
        this.formData.cycle = 1
        this.formData.cycleDay = index
      }
    },
    // 用药时间完成按钮校验
    setMedicationTime () {
      let map = new Map()
      if (this.frequency[1] === '小时' && Number(this.frequency[0]) === 1) {
        this.patientDrugsTimeSettingList = []
        this.setTime = !this.setTime
      } else {
        let num = 0
        if (this.frequency[2] >= 1) {
          this.selected.forEach(item => {
            if (item && item.length > 0) {
              map.set(item.toString(), item)
              num++
            }
          })
          console.log('this.selected', this.selected)
          console.log('this.selected map', map)
          if (num < this.frequency[2] || num === 0) {
            Toast({ message: '请设置用药时间', closeOnClick: true, position: 'center' })
            return
          }
          if (map.size < this.selected.length) {
            Toast({ message: '用药时间重复，请检查设置', closeOnClick: true, position: 'center' })
            return
          }
          this.patientDrugsTimeSettingList = []
          for (let i = 0; i < this.selected.length; i++) {
            const values = this.selected[i]
            if (values.length === 3) {
              let hour = values[1].substring(0, 2)
              let minute = values[2].substring(0, 2)
              this.patientDrugsTimeSettingList.push({ patientDrugsId: this.id, theFirstTime: i + 1, dayOfTheCycle: values[0].substring(1, 2), triggerTimeOfTheDay: hour + ':' + minute + ':00' })
            } else {
              let hour = values[0].substring(0, 2)
              let minute = values[1].substring(0, 2)
              this.patientDrugsTimeSettingList.push({ patientDrugsId: this.id, theFirstTime: i + 1, triggerTimeOfTheDay: hour + ':' + minute + ':00' })
            }
          }
          console.log('patientDrugsTimeSettingList', this.patientDrugsTimeSettingList)
          this.setTime = !this.setTime
        }
      }
    },
    /**
     * 初始化频率的数据
     */
    frequencyDataF () {
      let c = []
      for (let i = 1; i <= 9; i++) {
        c.push({text: i + '', children: [{text: '次'}]})
      }
      const one = {
        text: '1',
        children: [
          {text: '天', children: c},
          {text: '小时', children: [{text: '1', children: [{text: '次'}]}]},
          {text: '周', children: c},
          {text: '月', children: c}
        ]
      }
      let a = [one]
      const twoData = [
        {text: '天', children: [{text: '1', children: [{text: '次'}]}]},
        {text: '小时', children: [{text: '1', children: [{text: '次'}]}]},
        {text: '周', children: [{text: '1', children: [{text: '次'}]}]},
        {text: '月', children: [{text: '1', children: [{text: '次'}]}]}
      ]
      for (let i = 2; i <= 30; i++) {
        a.push({text: i + '', children: twoData})
      }
      this.frequencyData.push(...a)
    },
    /**
     * 选择好时间之后。
     */
    onSelectTime (value) {
      console.log('values', this.setTimeIndex, value)
      this.selected[this.setTimeIndex] = value
      console.log('selected', this.selected)
      this.showTime = false
    },

    /**
     * 打开窗口后，设置默认值
     */
    openedTime () {
      const values = this.selected[this.setTimeIndex]
      if (values.length > 0) {
        console.log('回显的时间', values)
        if (this.$refs['timePicker']) {
          this.$refs['timePicker'].setValues(values)
        }
        console.log('this.$refs', this.$refs['timePicker'])
        // this.$refs.timePicker.setValues(values)
      }
    },
    /**
     * 打开picker时，根据select中的时间设置。
     * 设置弹窗的回显
     */
    openTimePicker (index) {
      this.setTimeIndex = index
      this.showTime = true
    },
    /**
     * 回显
     */
    getTimeSelected (index, depth) {
      const values = this.selected[index]
      if (depth === 3) {
        if (values.length === 3) {
          return values[0] + ' ' + values[1] + ' ' + values[2]
        } else {
          return ''
        }
      } else {
        if (values.length === 2) {
          return values[0] + ' ' + values[1]
        } else {
          return ''
        }
      }
    },
    /**
     * 获取患者药品的详情
     */
    getDetail () {
      getPatientDrugs({id: this.id}).then((el) => {
        if (el.code === 0) {
          const patientDurg = el.data
          this.formData = patientDurg
          if (patientDurg.cycleDuration !== null) {
            this.frequency[0] = patientDurg.cycleDuration.toString()
          }
          if (patientDurg.timePeriod === 'day') {
            this.frequency[1] = '天'
          } else if (patientDurg.timePeriod === 'hour') {
            this.frequency[1] = '小时'
          } else if (patientDurg.timePeriod === 'week') {
            this.frequency[1] = '周'
          } else if (patientDurg.timePeriod === 'moon') {
            this.frequency[1] = '月'
          }
          this.frequency[2] = patientDurg.numberOfDay.toString()
          this.frequency[3] = '次'
          this.setTimeList()
          this.patientDrugsTimeSettingList = []
          this.patientDrugsTimeSettingList.push(...this.formData.patientDrugsTimeSettingList)
          this.selected = []
          const frequency0 = Number(this.frequency[0])
          if (frequency0 > 1 && this.frequency[1] === '小时') {
            this.selected = [['08时', '00分']]
          } else if (frequency0 === 1 && this.frequency[1] === '小时') {
            // 整点
          } else {
            console.log('patientDrugsTimeSettingList', this.patientDrugsTimeSettingList)
            for (let i = 0; i < this.patientDrugsTimeSettingList.length; i++) {
              const dayOfTheCycle = this.patientDrugsTimeSettingList[i].dayOfTheCycle
              const hour = this.patientDrugsTimeSettingList[i].triggerTimeOfTheDay.substring(0, 2)
              const minute = this.patientDrugsTimeSettingList[i].triggerTimeOfTheDay.substring(3, 5)
              console.log(dayOfTheCycle, hour, minute)
              if (patientDurg.timePeriod === 'week' || patientDurg.timePeriod === 'moon') {
                this.selected.push([ '第' + dayOfTheCycle + '天', hour + '时', minute + '分' ])
              } else {
                this.selected.push([ hour + '时', minute + '分' ])
              }
            }
            console.log('selected', this.selected)
          }
          console.log(this.formData)
        }
      })
    },
    /**
     * 获取此系统用药的详情
     * @param i
     */
    getInfo (i) {
      getSysDrugsDetail(i).then((res) => {
        if (res.code === 0 && res.data) {
          this.alldata = res.data
        }
      })
    },
    /**
     * 去查看使用说明
     */
    gouserWay () {
      this.$router.push({
        path: '/patient/medicine/showuser',
        query: {drugsId: this.drugsId}
      })
    },
    // 设置用药时间列表
    setTimeList () {
      let arr = []
      this.selected = []
      this.patientDrugsTimeSettingList = []
      const frequency0 = Number(this.frequency[0])
      if (frequency0 > 1 && this.frequency[1] === '小时') {
        this.selected = [['08时', '00分']]
      } else {
        const number = this.frequency[2]
        for (let i = 0; i < number; i++) {
          this.selected.push([])
        }
      }
      if (frequency0 === 1 && this.frequency[1] !== '小时' && this.frequency[1] !== '天') {
        this.hourList = []
        let a = 0
        if (this.frequency[1] === '周') {
          a = 7
        }
        if (this.frequency[1] === '月') {
          a = 30
        }
        for (let i = 1; i <= a; i++) {
          arr.push('第' + i + '天')
        }
        let hourList = []
        for (let i = 0; i <= 23; i++) {
          if (i < 10) {
            hourList.push('0' + i + '时')
          } else {
            hourList.push(i + '时')
          }
        }
        // 生产一个三列的数据
        // 第几天， 几点， 几分
        const list = [
          {
            values: arr,
            defaultIndex: 0
          },
          // 第二列
          {
            values: hourList,
            defaultIndex: 0
          },
          // 第二列
          {
            values: ['00分', '15分', '30分', '45分'],
            defaultIndex: 0
          }
        ]
        this.hourList.push(...list)
        console.log('年月时', this.hourList)
      }
      if (frequency0 === 1 && this.frequency[1] === '天' && this.frequency[2] <= 4) {
        let hourList = []
        this.hourList = []
        for (let i = 0; i <= 23; i++) {
          if (i < 10) {
            hourList.push('0' + i + '时')
          } else {
            hourList.push(i + '时')
          }
        }
        const list = [
          // 第二列
          {
            values: hourList,
            defaultIndex: 0
          },
          // 第二列
          {
            values: ['00分', '15分', '30分', '45分'],
            defaultIndex: 0
          }
        ]
        this.hourList.push(...list)
        let arr = [
          ['08时', '00分'],
          ['11时', '00分'],
          ['16时', '00分'],
          ['20时', '00分']
        ]
        this.selected = arr.slice(0, this.frequency[2])
        console.log('1天四次', this.hourList)
      }
      if (frequency0 === 1 && this.frequency[1] === '天' && this.frequency[2] > 4) {
        this.hourList = []
        let hourList = []
        for (let i = 0; i <= 23; i++) {
          if (i < 10) {
            hourList.push('0' + i + '时')
          } else {
            hourList.push(i + '时')
          }
        }
        const list = [
          // 第二列
          {
            values: hourList,
            defaultIndex: 0
          },
          // 第二列
          {
            values: ['00分', '15分', '30分', '45分'],
            defaultIndex: 0
          }
        ]
        this.hourList.push(...list)
        console.log('一天四次以上', this.hourList)
      }
      if (frequency0 !== 1) {
        let hourList = []
        for (let i = 0; i <= 23; i++) {
          if (i < 10) {
            hourList.push('0' + i + '时')
          } else {
            hourList.push(i + '时')
          }
        }
        const list = [
          // 第二列
          {
            values: hourList,
            defaultIndex: 0
          },
          // 第二列
          {
            values: ['00分', '15分', '30分', '45分'],
            defaultIndex: 0
          }
        ]
        this.hourList.push(...list)
      }
    },
    /**
     * 提交药品信息
     */
    submit () {
      if (!this.formData.numberOfBoxes) {
        Toast({ message: '请设置用药数量', closeOnClick: true, position: 'center' })
        return
      }
      if (this.frequency.length === 0) {
        Toast({ message: '请设置用药频次', closeOnClick: true, position: 'center' })
        return
      }
      if (this.frequency[1] !== '小时' && this.frequency[0] !== 1 && this.patientDrugsTimeSettingList.length === 0) {
        Toast({ message: '请设置用药时间', closeOnClick: true, position: 'center' })
        return
      }
      if (!this.formData.dose || !this.formData.unit) {
        Toast({ message: '请设置用药剂量', closeOnClick: true, position: 'center' })
        return
      }
      if (this.formData.cycle === undefined || this.formData.cycle === '') {
        Toast({ message: '请设置用药周期', closeOnClick: true, position: 'center' })
        return
      }
      this.formData.drugsId = this.drugsId
      this.formData.patientDrugsTimeSettingList = this.patientDrugsTimeSettingList
      if (this.formData.id) {
        console.log('formData', this.formData)
        updatePatientDrugs(this.formData).then((res) => {
          if (res.code === 0) {
            console.log(res)
            this.$router.go(-1)
          }
        })
      } else {
        this.formData.medicineIcon = this.alldata.icon
        this.formData.medicineName = this.alldata.name
        this.formData.checkinNum = 0
        this.formData.patientId = localStorage.getItem('patientId')
        this.formData.status = 0
        console.log('formData', this.formData)
        addPatientDrugs(this.formData).then((res) => {
          if (res.code === 0) {
            console.log(res)
            this.$router.go(-1)
          }
        })
      }
    }
  }
}
</script>
<style lang='less' scoped>

</style>
