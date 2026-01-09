<template>
  <section>
    <div v-if="setTime">
      <navBar pageTitle="添加药品"></navBar>
      <group>
        <cell class="myValcolor" required  is-link>
            <span slot="title">
              <span style="color:#666666">
                  药品图片
              </span>
              <span style="color:red;vertical-align: middle;">*</span>
            </span>
          <div style="width:30px;position: relative">
            <img :src="loaderImg" alt="" style="width:100%;display: table-cell;vertical-align: middle;" >
            <input class="iconup" type="file" style="width:100%;position: absolute;top:0;left:0;opacity: 0;" @change="uploaderfile($event)" accept="image/*"/>
          </div>
        </cell>
        <x-input required v-model="medicineName" placeholder-align="right" text-align="right" placeholder="请输入药品名称">
            <span slot="label">
              <span style="color:#666666">
                  药品名称
              </span>
              <span style="color:red;vertical-align: middle;">*</span>
            </span>
        </x-input>
      </group>
      <group>
        <x-number class="numberOfBoxes" title="添加数量（盒)"  v-model="formData.numberOfBoxes" :min='1'>
        </x-number>
      </group>
      <group>
        <popup-picker popup-title="用药频次" class="myValcolor" required :data="cedata" v-model="frequency"
                      value-text-align="right"
                      @on-shadow-change="ceChange"
                      @on-hide="setFrequencyData"
                      @on-show="getFrequencyData"
        >
          <span slot="title">
                        <span style="color:#666666">
                            用药频次
                        </span>
                        <span style="color:red;vertical-align: middle;">*</span>
                    </span>
        </popup-picker>
        <cell class="myValcolor" :class="frequency.length<1?'notClick':''" required is-link @click.native="setTime=!setTime">
                     <span slot="title">
                        <span style="color:#666666">
                            用药时间
                        </span>
                        <span style="color:red;vertical-align: middle;">*</span>
                    </span>
          <span slot="value" style="color:#333333">
                       {{ formData.drugsTime||frequency[1]=='小时'&&frequency[0]==1 ? "已配置" : '' }}
                      </span>
        </cell>
        <popup-picker class="myValcolor" required :data="dayHowList" v-model="formData.dose" value-text-align="right">
                    <span slot="title">
                        <span style="color:#666666">
                            每次剂量
                        </span>
                        <span style="color:red;vertical-align: middle;">*</span>
                    </span>
        </popup-picker>
        <popup-picker class="myValcolor" required :data="weekList" v-model="formData.cycleDay" value-text-align="right"
                      :columns="1"
                      show-name>
                     <span slot="title">
                        <span style="color:#666666">
                            用药周期
                        </span>
                        <span style="color:red;vertical-align: middle;">*</span>
                    </span>
        </popup-picker>
      </group>
      <div style="margin:0px auto;padding:40px 0px">
        <x-button style="width:40vw;background:#66728C;color:#fff;" @click.native="submit">
          保存
        </x-button>
      </div>
    </div>
    <div v-else style="height: 100vh;padding-bottom:30px">
      <x-header title="slot:overwrite-title" :left-options="{preventGoBack: true,backText: ''}"
                @on-click-back="setTime=true">
        设置时间
      </x-header>
      <div v-for="(item,index) in Number(frequency[2])" :key="index" style="background: #fff">
        <popup-picker v-if="frequency[1]!='小时'" style="margin-top: 10px" :title="`第${index+1}次用药`" :data="hourList"
                      v-model="selected[index]"
                      value-text-align="right" @on-change="changeTime" show-name></popup-picker>
        <popup-picker v-if="frequency[1]=='小时'&&frequency[0]>1" title="首次用药时间" :data="hourList"
                      v-model="selected[index]"
                      value-text-align="right" @on-change="changeTime" show-name>
        </popup-picker>

        <div v-if="frequency[1]=='小时'&&frequency[0]==1"
             style="display: flex;justify-content: space-between;height: 51px;line-height: 51px;padding-left: 13px;padding-right: 13px;color: #666">
          <div>用药时间</div>
          <div>整点</div>
        </div>
      </div>
      <div style="margin:0px auto;padding:40px 0px 60px">
        <x-button style="width:208px;background:#66728C;color:#fff;margin-top: 62px" @click.native="setMedicationTime">
          完成
        </x-button>
      </div>
    </div>
  </section>
</template>
<script>
import {Group, Cell, CellBox, XNumber, PopupPicker,XInput} from 'vux'
import Api from '@/api/Content.js'
import {Dialog} from "vant";

export default {
  components: {
    Group,
    Cell,
    CellBox,
    XNumber,
    PopupPicker,
    XInput,
    [Dialog.Component.name]: Dialog.Component,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      loaderImg:require('@/assets/my/medicineIconImg.png'),
      scan: require('@/assets/my/saoma.png'),
      alldata: [
        {interaction: ''}
      ],
      params: [],
      inputVal: '',
      id: '',
      dayList: [
        {
          name: '每日1次',
          value: "1",
          parent: 0
        },
        {
          name: '每日2次',
          value: "2",
          parent: 0
        },
        {
          name: '每日3次',
          value: "3",
          parent: 0
        },
        {
          name: '每日4次',
          value: "4",
          parent: 0
        }
      ],
      dayHowList: [
        ['0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0'],
        ['片', '粒', '丸', '滴', '袋', '支', '喷', '克', '毫克', '毫升']
      ],
      medicineName:'',
      weekList: [
        {
          name: '长期',
          value: '0'
        }
      ],
      hourList: [
        [
          {
            name: '01时',
            value: "01"
          },
          {
            name: '02时',
            value: "02"
          },
          {
            name: '03时',
            value: "03"
          },
          {
            name: '04时',
            value: "04"
          },

          {
            name: '05时',
            value: "05"
          },
          {
            name: '06时',
            value: "06"
          },
          {
            name: '07时',
            value: "07"
          },
          {
            name: '08时',
            value: "08"
          },
          {
            name: '09时',
            value: "09"
          },
          {
            name: '10时',
            value: "10"
          },

          {
            name: '11时',
            value: "11"
          },
          {
            name: '12时',
            value: "12"
          },
          {
            name: '13时',
            value: "13"
          },
          {
            name: '14时',
            value: "14"
          },
          {
            name: '15时',
            value: "15"
          },
          {
            name: '16时',
            value: "16"
          },
          {
            name: '17时',
            value: "17"
          },
          {
            name: '18时',
            value: "18"
          },
          {
            name: '19时',
            value: "19"
          },

          {
            name: '20时',
            value: "20"
          },
          {
            name: '21时',
            value: "21"
          },

          {
            name: '22时',
            value: "22"
          },
          {
            name: '23时',
            value: "23"
          },
          {
            name: '24时',
            value: "24"
          }
        ],
        [
          {
            name: '00分',
            value: "00"
          },
          {
            name: '15分',
            value: "15"
          },
          {
            name: '30分',
            value: "30"
          },
          {
            name: '45分',
            value: "45"
          }
        ]
      ],
      remindDrug: false,
      remindDay: -14,
      tempRemindDay: 14,
      setTime: true,
      formData: {
        numberOfDay: [],
        drugsTime: '',
        dose: [],
        cycleDay: [],
        numberOfBoxes: 1,
        buyDrugsReminderTime: moment().subtract(-14, "days").format("YYYY-MM-DD"),
      },
      selected: [
        ['08', '00'],
        ['20', '00'],
        ['20', '00'],
        ['20', '00'],
      ],
      drugData: {},
      show: false,
      drugsId: '',
      cedata: [],
      frequency: [],
      savefrequencydata: [],
      rowNumber: 2,
      submitStatus: false
    }
  },
  beforeMount() {
    this.ceshi()
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


    for (var i = 1; i <= 100; i++) {
      const obj = {}
      obj.name = i + "天"
      obj.value = String(i)
      that.weekList.push(obj);
    }
  },
  methods: {
    //用药时间完成按钮校验
    setMedicationTime() {
      let map = new Map
      if (this.frequency[1] == '小时' && this.frequency[0] == 1) {
        this.setTime = !this.setTime
        console.log(789)
      } else {
        // if (this.selected.length < this.frequency[2] && this.frequency[1] !== '小时' && this.frequency[0] !== 1) {
        //   this.$vux.toast.text('请设置用药时间', 'center')
        //   return
        // }
        let num=0
        if (this.frequency[2] >= 1) {

          this.selected.forEach(item => {
            map.set(item.toString(), item)
            num++
          })
          if (num<this.frequency[2]||num==0){
            this.$vux.toast.text('请设置用药时间', 'center')
            return
          }
          if (map.size < this.selected.length) {
            this.$vux.toast.text('用药时间重复，请重新设置', 'center')
            return
          }
          let a = ''
          if (map.size == this.selected.length) {
            this.formData.drugsTime = ''
            this.selected.forEach((item, b) => {
              item.forEach((i, index) => {
                if (index + 1 == 2 && b < this.selected.length && item.length < 3) {
                  this.formData.drugsTime += i + ','
                }
                if (index + 1 !== 2 && item[0].length < 3) {
                  this.formData.drugsTime += i + ':'
                } else if (index == 0 && item[0].length == 3) {
                  this.formData.drugsTime += i + ' | '
                }
                if (item.length == 3 && index == 1) {
                  this.formData.drugsTime += i + ':'
                }
                if (item.length == 3 && index > 1) {
                  this.formData.drugsTime += i + " "
                }
              })

            })
            // if (this.selected[0].length==3){
            //   this.formData.drugsTime=this.selected[0][0]+' | '+this.selected[0][1]+":"+this.selected[0][2]
            // }
            if (this.selected[0] && this.selected[0].length == 2) {
              this.formData.drugsTime = this.formData.drugsTime.substr(0, this.formData.drugsTime.length - 1)
            }
            this.setTime = !this.setTime
          }
        }
      }


    },
    //查看用药时间列表是否有重复时间
    isRepeat(v) {
      let obj = {}
      for (let i in v) {
        if (obj[v[i]]) {
          return true
        }
        obj[v[i]] = true
      }
      return false
    },
    ceshi() {
      let a = []
      let b = ['天', '小时', '周', '月']
      let c = []
      for (let i = 1; i <= 30; i++) {
        a.push(i.toString())
      }
      for (let i = 1; i <= 9; i++) {
        c.push(i.toString())
      }
      this.cedata.push(a)
      this.cedata.push(b)
      this.cedata.push(c)
      this.cedata.push(['次'])
    },
    tempRemindDayAdd() {
      if (this.tempRemindDay < 999) {
        this.tempRemindDay++;
      }
    },
    tempRemindDayReduce() {
      if (this.tempRemindDay > 1) {
        this.tempRemindDay--;
      }
    },
    //暂不提醒
    notTips() {
      this.remindDrug = !this.remindDrug
      this.formData.buyDrugsReminderTime = ''
    },
    //dialog取消
    cancelReminder() {
      this.remindDrug = !this.remindDrug
    },
    //dialog确认
    confirmReminder() {
      this.remindDrug = !this.remindDrug
      this.remindDay = -1 * this.tempRemindDay
      this.formData.buyDrugsReminderTime = this.getTime()
    },
    getTime(buyDrugsReminderTime) {
      if (buyDrugsReminderTime) {
        //格式化时间
        return moment(buyDrugsReminderTime).format("YYYY年MM月DD日")
      } else {
        //从今天计算往后14天
        return moment().subtract(this.remindDay, "days").format("YYYY-MM-DD")
      }
    },
    getDetail() {
      const that = this
      Api.getpatientDrugs({id: this.id}).then((el) => {
        if (el.data.code === 0) {
          console.log(el.data.data)
          if (el.data.data.cycleDuration !== null) {
            this.frequency[0] = el.data.data.cycleDuration.toString()
          }
          // this.frequency[]
          if (el.data.data.timePeriod == 'day') {
            this.frequency[1] = '天'
          } else if (el.data.data.timePeriod == 'hour') {
            this.frequency[1] = '小时'
          } else if (el.data.data.timePeriod == 'week') {
            this.frequency[1] = '周'
          } else if (el.data.data.timePeriod == 'moon') {
            this.frequency[1] = '月'
          }
          this.frequency[3] = '次'
          this.frequency[2] = el.data.data.numberOfDay.toString()
          this.savefrequencydata = this.frequency
          this.selected = []
          el.data.data.patientDrugsTimeSettingList.forEach((item, index) => {
            let arr = [item.triggerTimeOfTheDay.slice(0, 2), item.triggerTimeOfTheDay.slice(3, 5)]
            this.selected.push(arr)
            // a[index].push([item.triggerTimeOfTheDay,item.triggerTimeOfTheDay])
          })
          if (this.frequency[0] == 1 && this.frequency[1] !== '小时' && this.frequency[1] !== '天') {
            let arr = []
            this.hourList = [[
              {
                name: '01时',
                value: "01"
              },
              {
                name: '02时',
                value: "02"
              },
              {
                name: '03时',
                value: "03"
              },
              {
                name: '04时',
                value: "04"
              },

              {
                name: '05时',
                value: "05"
              },
              {
                name: '06时',
                value: "06"
              },
              {
                name: '07时',
                value: "07"
              },
              {
                name: '08时',
                value: "08"
              },
              {
                name: '09时',
                value: "09"
              },
              {
                name: '10时',
                value: "10"
              },

              {
                name: '11时',
                value: "11"
              },
              {
                name: '12时',
                value: "12"
              },
              {
                name: '13时',
                value: "13"
              },
              {
                name: '14时',
                value: "14"
              },
              {
                name: '15时',
                value: "15"
              },
              {
                name: '16时',
                value: "16"
              },
              {
                name: '17时',
                value: "17"
              },
              {
                name: '18时',
                value: "18"
              },
              {
                name: '19时',
                value: "19"
              },

              {
                name: '20时',
                value: "20"
              },
              {
                name: '21时',
                value: "21"
              },

              {
                name: '22时',
                value: "22"
              },
              {
                name: '23时',
                value: "23"
              },
              {
                name: '24时',
                value: "24"
              }
            ],
              [
                {
                  name: '00分',
                  value: "00"
                },
                {
                  name: '15分',
                  value: "15"
                },
                {
                  name: '30分',
                  value: "30"
                },
                {
                  name: '45分',
                  value: "45"
                }
              ]]
            let a = 0
            if (this.frequency[1] == '周') {
              a = 7
            }
            if (this.frequency[1] == '月') {
              a = 30
            }
            for (let i = 1; i <= a; i++) {
              arr.push({
                name: '第' + i + '天',
                value: '第' + i + '天'
              })

            }
            this.selected = []
            el.data.data.patientDrugsTimeSettingList.forEach((item, index) => {
              let arr = [`第${item.dayOfTheCycle}天`, item.triggerTimeOfTheDay.slice(0, 2), item.triggerTimeOfTheDay.slice(3, 5)]
              this.selected.push(arr)
            })
            console.log("===========<", this.selected)
            this.hourList.unshift(arr)

          }
          this.params = el.data.data
          let thsiArray = el.data.data
          Api.sysDrugsSearch({id: this.drugsId}).then((res) => {
            if (res.data.code === 0) {
              res.data.data.numberOfBoxes = thsiArray.numberOfBoxes
              res.data.data.form = 'index'
              res.data.data.numberOfDay = [String(thsiArray.numberOfDay)]
              res.data.data.drugsTime = thsiArray.drugsTime
              res.data.data.dose = [thsiArray.dose, thsiArray.unit]
              res.data.data.cycleDay = [String(thsiArray.cycleDay)]
              res.data.data.putId = thsiArray.id
              res.data.data.buyDrugsReminderTime = thsiArray.buyDrugsReminderTime,
                  that.formData = res.data.data
              that.changeDay(that.formData)
            }
          })

        }
      })
    },
    getInfo(i) {
      const params = {
        id: i,
      }
      Api.sysDrugsPagedetail(params).then((res) => {
        if (res.data.code === 0 && res.data.data && res.data.data.length > 0) {
          this.alldata = res.data.data[0]
        }
      })
    },
    gouserWay() {
      this.$router.push({
        path: '/medicine/showuser',
        query: {drugsId: this.drugsId}
      })
    },
    ceChange(i) {
      if (this.cedata[2] != 1 && (i[0] == 1 && i[1] == '小时') || this.cedata[2] != 1 && i[0] > 1) {
        this.cedata[2] = [1]
        this.$set(this.cedata, 2, [1])
      } else if (this.cedata[2] == 1 && i[0] == 1 && i[1] !== '小时') {
        let a = [1, 2, 3, 4, 5, 6, 7, 8, 9]
        this.$set(this.cedata, 2, a)
      }
    },
    //点击用药频次显示弹出层
    getFrequencyData() {
      this.savefrequencydata= this.frequency
      // console.log(this.frequency)
    },
    //设置用药时间列表
    setTimeList() {
      let arr = []

      if (this.frequency[0] > 1 && this.frequency[1] == '小时') {
        this.selected = [['08', '00']]
      }
      if (this.frequency[0] == 1 && this.frequency[1] !== '小时' && this.frequency[1] !== '天') {
        this.hourList = [[
          {
            name: '01时',
            value: "01"
          },
          {
            name: '02时',
            value: "02"
          },
          {
            name: '03时',
            value: "03"
          },
          {
            name: '04时',
            value: "04"
          },

          {
            name: '05时',
            value: "05"
          },
          {
            name: '06时',
            value: "06"
          },
          {
            name: '07时',
            value: "07"
          },
          {
            name: '08时',
            value: "08"
          },
          {
            name: '09时',
            value: "09"
          },
          {
            name: '10时',
            value: "10"
          },

          {
            name: '11时',
            value: "11"
          },
          {
            name: '12时',
            value: "12"
          },
          {
            name: '13时',
            value: "13"
          },
          {
            name: '14时',
            value: "14"
          },
          {
            name: '15时',
            value: "15"
          },
          {
            name: '16时',
            value: "16"
          },
          {
            name: '17时',
            value: "17"
          },
          {
            name: '18时',
            value: "18"
          },
          {
            name: '19时',
            value: "19"
          },

          {
            name: '20时',
            value: "20"
          },
          {
            name: '21时',
            value: "21"
          },

          {
            name: '22时',
            value: "22"
          },
          {
            name: '23时',
            value: "23"
          },
          {
            name: '24时',
            value: "24"
          }
        ],
          [
            {
              name: '00分',
              value: "00"
            },
            {
              name: '15分',
              value: "15"
            },
            {
              name: '30分',
              value: "30"
            },
            {
              name: '45分',
              value: "45"
            }
          ]]
        let a = 0
        if (this.frequency[1] == '周') {
          a = 7
        }
        if (this.frequency[1] == '月') {
          a = 30
        }
        for (let i = 1; i <= a; i++) {
          arr.push({
            name: '第' + i + '天',
            value: '第' + i + '天'
          })

        }
        this.selected.push([])

        this.hourList.unshift(arr)
      }
      if (this.frequency[0] == 1 && this.frequency[1] == '天' && this.frequency[2] <= 4) {
        this.hourList = [[
          {
            name: '01时',
            value: "01"
          },
          {
            name: '02时',
            value: "02"
          },
          {
            name: '03时',
            value: "03"
          },
          {
            name: '04时',
            value: "04"
          },

          {
            name: '05时',
            value: "05"
          },
          {
            name: '06时',
            value: "06"
          },
          {
            name: '07时',
            value: "07"
          },
          {
            name: '08时',
            value: "08"
          },
          {
            name: '09时',
            value: "09"
          },
          {
            name: '10时',
            value: "10"
          },

          {
            name: '11时',
            value: "11"
          },
          {
            name: '12时',
            value: "12"
          },
          {
            name: '13时',
            value: "13"
          },
          {
            name: '14时',
            value: "14"
          },
          {
            name: '15时',
            value: "15"
          },
          {
            name: '16时',
            value: "16"
          },
          {
            name: '17时',
            value: "17"
          },
          {
            name: '18时',
            value: "18"
          },
          {
            name: '19时',
            value: "19"
          },

          {
            name: '20时',
            value: "20"
          },
          {
            name: '21时',
            value: "21"
          },

          {
            name: '22时',
            value: "22"
          },
          {
            name: '23时',
            value: "23"
          },
          {
            name: '24时',
            value: "24"
          }
        ],
          [
            {
              name: '00分',
              value: "00"
            },
            {
              name: '15分',
              value: "15"
            },
            {
              name: '30分',
              value: "30"
            },
            {
              name: '45分',
              value: "45"
            }
          ]]
        let arr = [['08', '00'],
          ['11', '00'],
          ['16', '00'],
          ['20', '00'],]
        this.selected = arr.slice(0, this.frequency[2])
      }
      if (this.frequency[0] == 1 && this.frequency[1] == '天' && this.frequency[2] > 4) {
        this.selected = []
        this.hourList = [[
          {
            name: '01时',
            value: "01"
          },
          {
            name: '02时',
            value: "02"
          },
          {
            name: '03时',
            value: "03"
          },
          {
            name: '04时',
            value: "04"
          },

          {
            name: '05时',
            value: "05"
          },
          {
            name: '06时',
            value: "06"
          },
          {
            name: '07时',
            value: "07"
          },
          {
            name: '08时',
            value: "08"
          },
          {
            name: '09时',
            value: "09"
          },
          {
            name: '10时',
            value: "10"
          },

          {
            name: '11时',
            value: "11"
          },
          {
            name: '12时',
            value: "12"
          },
          {
            name: '13时',
            value: "13"
          },
          {
            name: '14时',
            value: "14"
          },
          {
            name: '15时',
            value: "15"
          },
          {
            name: '16时',
            value: "16"
          },
          {
            name: '17时',
            value: "17"
          },
          {
            name: '18时',
            value: "18"
          },
          {
            name: '19时',
            value: "19"
          },

          {
            name: '20时',
            value: "20"
          },
          {
            name: '21时',
            value: "21"
          },

          {
            name: '22时',
            value: "22"
          },
          {
            name: '23时',
            value: "23"
          },
          {
            name: '24时',
            value: "24"
          }
        ],
          [
            {
              name: '00分',
              value: "00"
            },
            {
              name: '15分',
              value: "15"
            },
            {
              name: '30分',
              value: "30"
            },
            {
              name: '45分',
              value: "45"
            }
          ]]
      }
      if(this.frequency[0] !== '1'){
        this.hourList = [[
          {
            name: '01时',
            value: "01"
          },
          {
            name: '02时',
            value: "02"
          },
          {
            name: '03时',
            value: "03"
          },
          {
            name: '04时',
            value: "04"
          },

          {
            name: '05时',
            value: "05"
          },
          {
            name: '06时',
            value: "06"
          },
          {
            name: '07时',
            value: "07"
          },
          {
            name: '08时',
            value: "08"
          },
          {
            name: '09时',
            value: "09"
          },
          {
            name: '10时',
            value: "10"
          },

          {
            name: '11时',
            value: "11"
          },
          {
            name: '12时',
            value: "12"
          },
          {
            name: '13时',
            value: "13"
          },
          {
            name: '14时',
            value: "14"
          },
          {
            name: '15时',
            value: "15"
          },
          {
            name: '16时',
            value: "16"
          },
          {
            name: '17时',
            value: "17"
          },
          {
            name: '18时',
            value: "18"
          },
          {
            name: '19时',
            value: "19"
          },

          {
            name: '20时',
            value: "20"
          },
          {
            name: '21时',
            value: "21"
          },

          {
            name: '22时',
            value: "22"
          },
          {
            name: '23时',
            value: "23"
          },
          {
            name: '24时',
            value: "24"
          }
        ],
          [
            {
              name: '00分',
              value: "00"
            },
            {
              name: '15分',
              value: "15"
            },
            {
              name: '30分',
              value: "30"
            },
            {
              name: '45分',
              value: "45"
            }
          ]]
      }
    },

    //用药频次弹出层点击确定或者取消
    setFrequencyData(data) {
      if (data == true) {
        this.selected = []
        this.formData.drugsTime = ""
        this.setTimeList()
      } else {

        this.frequency = this.savefrequencydata
      }
    },
    changeDay(i) {
      if (i[0] === '1') {
        let thisArray = this.formData.drugsTime.split(",")
        if (thisArray.length === 1 && thisArray[0]) {
          this.selected = [
            this.formData.drugsTime.split(":")
          ]
        } else {
          this.formData.drugsTime = "08:00"
          this.selected = [
            ['08', '00']
          ]
        }

      } else if (i[0] === '2') {
        let thisArray = this.formData.drugsTime.split(",")
        if (thisArray.length === 2) {
          this.selected = [
            thisArray[0].split(":"),
            thisArray[1].split(":"),
          ]
        } else {
          this.formData.drugsTime = "08:00,20:00"
          this.selected = [
            ['08', '00'],
            ['20', '00']
          ]
        }

      } else if (i[0] === '3') {
        let thisArray = this.formData.drugsTime.split(",")
        if (thisArray.length === 3) {
          this.selected = [
            thisArray[0].split(":"),
            thisArray[1].split(":"),
            thisArray[2].split(":"),
          ]
        } else {
          this.formData.drugsTime = "08:00,11:00,20:00"
          this.selected = [
            ['08', '00'],
            ['11', '00'],
            ['20', '00']
          ]
        }
      } else if (i[0] === '4') {
        let thisArray = this.formData.drugsTime.split(",")
        if (thisArray.length === 4) {
          this.selected = [
            thisArray[0].split(":"),
            thisArray[1].split(":"),
            thisArray[2].split(":"),
            thisArray[3].split(":"),
          ]
        } else {
          this.formData.drugsTime = "08:00,11:00,16:00,20:00"
          this.selected = [
            ['08', '00'],
            ['11', '00'],
            ['16', '00'],
            ['20', '00']
          ]
        }
      }
    },
    changeTime(i) {

      // const selectList = this.selected.slice(0, parseInt(this.formData.numberOfDay[0]))
      // let allString = ''
      // for (var i = 0; i < selectList.length; i++) {
      //   allString += selectList[i][0] + ':' + selectList[i][1] + ','
      // }
      // this.formData.drugsTime = allString.substring(0, allString.lastIndexOf(','))
    },
    uploaderfile(i){

      // console.log(i)
      let files = i.target.files[0]
      const isJPG = (files.type === 'image/jpeg' || files.type === 'image/png')
      if (!isJPG) {
        this.$vux.toast.text("上传图片只能为jpg或者png!",'center');
        return
      }
      this.$compressionImg.compressImg(files).then(f => {
        var formData = new FormData()
        formData.append('file', f)
        formData.append('folderId', 1308295372556206080)
        Api.updateImg(formData).then((res) => {
          if (res.data.code === 0) {
            this.$vux.toast.text("上传成功", 'bottom');
            this.loaderImg = res.data.data.url
          } else {
            this.$vux.toast.text("文件上传失败", 'bottom');
          }
        })
      })
    },
    submit() {
      if ((!this.formData.drugsTime || !this.formData.numberOfDay || !this.formData.dose[0] || !this.formData.dose[1] || !this.formData.cycleDay[0]||!this.loaderImg||!this.medicineName) && (this.frequency[1] !== '小时' && this.frequency[0] !== 1)) {
        // this.$toast.fail('您有表单项未完善，请检查完善后进行下一步');
        this.$vux.toast.text('您有表单项未完善，请检查完善后进行下一步', 'center')
        return
      }
      if (this.formData.id) {
        //1到30
        this.params.cycleDuration = this.frequency[0]
        //时间周期
        if (this.frequency[1] == '天') {
          this.params.timePeriod = 'day'
        }
        if (this.frequency[1] == '周') {
          this.params.timePeriod = 'week'
        }
        if (this.frequency[1] == '小时') {
          this.params.timePeriod = 'hour'
        }
        if (this.frequency[1] == '月') {
          this.params.timePeriod = 'moon'
        }
        //药品名字
        this.params.medicineName=this.medicineName
        //药品图标
        this.params.medicineIcon=this.loaderImg
        //用药周期
        this.params.cycleDay = this.formData.cycleDay[0] === "0" ? 0 : parseInt(this.formData.cycleDay[0])
        //用药剂量
        this.params.dose = this.formData.dose[0]
        this.params.drugsId = this.drugsId
        //用药时间
        this.params.drugsTime = this.formData.drugsTime
        //药品数量
        this.params.numberOfBoxes = this.formData.numberOfBoxes
        //每个周期用药次数
        this.params.numberOfDay = this.frequency[2]
        //用药时间
        this.params.patientDrugsTimeSettingList = []
        // if(this.selected[0].length>2){
        //
        // }
        if (this.selected !== []) {
          let obj = {}
          this.selected.forEach((item, index) => {
            if (this.selected[0].length == 2) {
              obj = {
                triggerTimeOfTheDay: item[0] + ":" + item[1] + ':00',
                theFirstTime: index + 1
              }
            }
            if (this.selected[0].length == 3) {
              obj = {
                dayOfTheCycle: Number(item[0].substring(1,2)),
                triggerTimeOfTheDay: item[1] + ":" + item[2] + ':00',
              }
            }

            this.params.patientDrugsTimeSettingList.push(obj)
          })
        }
        //用药单位
        this.params.unit = this.formData.dose[1]
        Api.FixpatientDrugs(this.params).then((res) => {
          if (res.data.code === 0) {
            console.log(res)
            this.$router.go(-1)
          }
        })

      } else {
        let params = {
          "buyDrugsReminderTime": this.formData.buyDrugsReminderTime,
          "checkinNum": 0,
          "cycle": this.formData.cycleDay[0] === "0" ? 0 : 1,
          "cycleDay": this.formData.cycleDay[0] === "0" ? 0 : parseInt(this.formData.cycleDay[0]),
          "cycleDuration": this.frequency[0],
          "dose": this.formData.dose[0],
          "drugsId": this.drugsId,
          "medicineIcon": this.loaderImg,
          "medicineName": this.medicineName,
          "numberOfBoxes": this.formData.numberOfBoxes,
          "numberOfDay": this.frequency[2],
          "patientDrugsTimeSettingList": [],
          "patientId": localStorage.getItem('userId'),
          "status": 0,
          "timePeriod": "",
          "unit": this.formData.dose[1],
          drugsTime: this.formData.drugsTime
        }
        if (this.selected !== []) {
          let obj = {}
          this.selected.forEach((item, index) => {
            if (this.selected[0].length == 2) {
              obj = {
                triggerTimeOfTheDay: item[0] + ":" + item[1] + ':00',
                theFirstTime: index + 1
              }
            }
            if (this.selected[0].length == 3) {
              obj = {
                dayOfTheCycle: Number(item[0].substring(1,2)),
                triggerTimeOfTheDay: item[1] + ":" + item[2] + ':00',
              }
            }
            params.patientDrugsTimeSettingList.push(obj)
          })
        }
        if (this.frequency[1] == '天') {
          params.timePeriod = 'day'
        }
        if (this.frequency[1] == '周') {
          params.timePeriod = 'week'
        }
        if (this.frequency[1] == '小时') {
          params.timePeriod = 'hour'
        }
        if (this.frequency[1] == '月') {
          params.timePeriod = 'moon'
        }
        console.log(params)
        if (this.submitStatus) {
          return;
        }
        this.submitStatus = true
        Api.addpatientDrugs(params).then((res) => {
          this.submitStatus = false
          if (res.data.code === 0) {
            console.log(res)
            this.$router.push('/medicine/index')
          }
        })
      }
    }
  }
}
</script>
<style lang="less" scoped>
@import '~vux/src/styles/close';
.notClick{
  pointer-events:none
}
.vux-close {
  margin-top: 8px;
  margin-bottom: 8px;
}

/deep/ .vux-header {
  height: 50px;
}

</style>
