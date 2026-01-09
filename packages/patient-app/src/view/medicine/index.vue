<template>
  <section class="main">
    <navBar @toHistoryPage="$router.push('/medicine/history')" :pageTitle="pageTitle !== undefined ? pageTitle : '我的药箱'"
            :rightIcon="dateImg"
            backUrl="/home"
            :showRightIcon="true" />
    <div class="content">
      <p>当前用药({{ list.length }})</p>
      <div class="nodata" v-if="list.length===0">
        <img :src="noData" alt="">
        <p>您暂未添加药品</p>
        <p>请点击下方添加按钮进行添加</p>
      </div>
      <div class="item" v-for="(i,k) in list" :key="k">
        <cell is-link @click.native="goitem(i)">
                    <span slot="title">
                        <img
                            style="width:35px;height:35px;border:1px solid rgba(102,102,102,0.1);vertical-align: middle;margin-right:10px"
                            :src="i.medicineIcon" alt="" srcset="">
                        <span style="line-height:40px;color:rgba(102,102,102,0.85)">{{ i.medicineName }}</span>
                    </span>
        </cell>
        <div
            style="width:calc(100% - 50px);color:rgba(102,102,102,0.85);box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);padding:20px 10px;display: flex;justify-content: space-between;text-align:center;margin:10px 15px 0px;border-radius:6px">
          <div
              style="width:33.33%;border-right:1px solid rgba(0, 0, 0, 0.1);display: flex;justify-content: space-between;padding:0px 8px"
              @click="getdoseInfo(i)"><span>{{ i.dose }}/{{ i.unit }}</span> <span
              style=" width: 0;height: 0;border-top: 5px solid transparent;border-left: 10px solid #bbb;border-bottom: 5px solid transparent;display: inline-block;margin-top: 8px;"></span>
          </div>
          <div
              style="width:33.33%;border-right:1px solid rgba(0, 0, 0, 0.1);display: flex;justify-content: space-between;padding:0px 8px"
          ><span>{{i.cycleDuration}}{{i.timePeriod=='day'?'天':i.timePeriod=='hour'?'小时':i.timePeriod=='week'?'周':'月'}}{{ i.numberOfDay }}次</span> <span
              style=" width: 0;height: 0;border-top: 5px solid transparent;border-left: 10px solid #EDEDED;border-bottom: 5px solid transparent;display: inline-block;margin-top: 8px;"></span>
          </div>
          <div style="width:33.33%;display: flex;justify-content: space-between;padding:0px 8px"
               @click="getcycleDayInfo(i)"><span>{{ i.cycle === 0 ? '长期' : i.cycleDay + '天' }}</span> <span
              style=" width: 0;height: 0;border-top: 5px solid transparent;border-left: 10px solid #bbb;border-bottom: 5px solid transparent;display: inline-block;margin-top: 8px;"></span>
          </div>
        </div>
        <div style="text-align:right;margin-top:15px;margin-right:15px">
          <x-button mini class="setBnts" style="width:28%;background:#fff;color:rgba(102,102,102,0.85);"
                    v-if="i.status===0" @click.native="stopSelect(i)">停止用药
          </x-button>

        </div>
      </div>

      <p v-if="historyList.length>=1">历史用药({{ historyList.length }})</p>
      <div v-if="historyList.length>=1">
        <div class="item" v-for="(z,y) in historyList" :key="y+'T'">
          <cell is-link>
                        <span slot="title">
                            <img
                                style="width:35px;height:35px;border:1px solid rgba(102,102,102,0.1);vertical-align: middle;margin-right:10px"
                                :src="z.medicineIcon" alt="" srcset="">
                            <span style="line-height:40px;color:rgba(102,102,102,0.85)">{{ z.medicineName }}</span>
                        </span>
            <x-button mini class="setBnts" style="background:#fca130;color:#fff;border:#fca130!important"
                      v-if="z.status===1" @click.native="recoveryDrug(z,0)">恢复用药
            </x-button>
          </cell>
        </div>
      </div>

    </div>
    <x-button style="background:#66728C;width:40%;color:#fff;margin:15vw auto 0px;"
              @click.native="$router.push('/medicine/storeroom')">添加用药
    </x-button>

    <div v-if="showOne">
      <popup-picker :show="showOne" @on-hide="showOne=false" :data="dayHowList" v-model="Numberdose"
                    @on-change="changeNumber"></popup-picker>
    </div>
    <div v-if="showTwo">
      <popup-picker :show="showTwo" :data="dayList" @on-hide="showTwo=false" v-model="myNumber" @on-change="changeDay"
                    :columns="1"
                    show-name></popup-picker>
    </div>
    <div v-if="showThree">
      <popup-picker :show="showThree" @on-hide="showThree=false" :data="weekList" v-model="weekVal"
                    @on-change="changeWeek" :columns="1"
                    show-name></popup-picker>
    </div>
    <confirm v-model="Btnshow"
             :title="btnTitle"
             @on-cancel="onCancel"
             @on-confirm="onConfirm">
    </confirm>

    <x-dialog v-model="stopTips" style="border-radius: 20px;" :hide-on-blur="true">
      <div style="padding: 15px 0px;">
        <div
            style="position: relative; width: 100%; display: flex; align-items: center; margin-bottom: 10px;">
          <div style="display: flex; width: 100%; justify-content: center;">
            <div
                style=" max-width: 120px; text-overflow:ellipsis;overflow:hidden;white-space:nowrap; color: #333333; font-size: 16px;">
              是否关闭提醒
            </div>
          </div>
          <van-icon name="cross" style="position: absolute; right: 0; margin-right: 15px;" size="15px;" color="#B8B8B8"
                    @click="stopTips = !stopTips"/>
        </div>
        <div style="color: #B8B8B8; font-size: 12px;">
          <div>你可以反馈关闭原因</div>
          <div>帮我们更加智能的安排用药提醒</div>
        </div>

        <div style="margin-top: 15px;">
          <flexbox wrap="wrap" :gutter="0">
            <flexbox-item :span="1/2" v-for="(i, k) in stopList" :key="k">
              <div style="margin: 8px 15px;" @click="clickStopReason(i)">
                <div
                    style="font-size: 12px; display: flex; justify-content: center; padding: 8px 0px; border-radius: 25px;"
                    :style="'background:'+ (Btnobj.stopReason === i.key ? '#66728B' : '#EEEEEE') +';color:'+ (Btnobj.stopReason === i.key ? '#ffffff' : '#999999')">
                  {{ i.label }}
                </div>
              </div>
            </flexbox-item>
          </flexbox>

          <div style="padding-left: 15px; padding-right: 15px; margin-top: 10px;"
               v-if="Btnobj.stopReason && Btnobj.stopReason === 'other'">
            <van-field
                v-model="stopReasonRemark"
                autosize
                rows="2"
                style="border-radius: 10px; background: #EEEEEE;"
                type="textarea"
                :maxlength="150"
                placeholder="请补充说明停药原因"
                show-word-limit
            />
          </div>

          <x-button mini type="primary" action-type="button"
                    style="height:44px; background: #66728B; width: 200px; margin-top: 20px;"
                    :class="Btnobj.stopReason==''||(Btnobj.stopReason=='other'&&stopReasonRemark=='')?'no-btn':''"
                    :disabled="Btnobj.stopReason!==''?false:true"
                    @click.native="stopDrug()">确定
          </x-button>
        </div>
      </div>
    </x-dialog>
  </section>
</template>
<script>
import {Group, Cell, CellBox, PopupPicker, Confirm, XDialog, Flexbox, FlexboxItem} from 'vux'
import Api from '@/api/Content.js'
import {Field} from 'vant';

export default {
  components: {
    Group,
    Cell,
    CellBox,
    PopupPicker,
    Confirm,
    XDialog,
    Flexbox,
    FlexboxItem,
    [Field.name]: Field,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      name:"",
      noData: require('@/assets/my/medicineImg.png'),
      dateImg: require('@/assets/my/historyMedicine.png'),
      emptyData: true,
      list: [],
      pageTitle: '',
      historyList: [],
      showOne: false,
      selectObj: {},
      numberList: [
        [1, 2, 3, 'X'],
        [4, 5, 6, '0'],
        [7, 8, 9, '.'],
      ],
      showTwo: false,
      showThree: false,
      myNumber: [],
      weekVal: [],
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
      weekList: [
        {
          name: '长期',
          value: '0'
        }
      ],
      btnTitle: '是否停止用药？',
      Btnshow: false,
      Btnobj: {},
      BtnStatus: '',
      dayHowList: [
        ['0.5', '1.0', '1.5', '2.0', '2.5', '3.0', '3.5', '4.0', '4.5', '5.0', '5.5', '6.0', '6.5', '7.0', '7.5', '8.0', '8.5', '9.0', '9.5', '10.0'],
        ['片', '粒', '丸', '滴', '袋', '支', '喷', '克', '毫克', '毫升']
      ],
      Numberdose: [],
      stopTips: false,
      stopList: [{label: '病好了', key: 'recover',}, {label: '疗效不好', key: 'bad_curative_effect',}, {
        label: '出现不良反应',
        key: 'adverse_reactions',
      },
        {label: '检验指标异常', key: 'abnormal_test_index',}, {label: '经济原因', key: 'economic_reasons',}, {
          label: this.$getDictItem('doctor')+'换药',
          key: 'doctor_changing_medicine',
        },
        {label: '不想吃了', key: 'do_not_want_to_eat',}, {label: '其他', key: 'other'}],
      stopReasonRemark: '',
    }
  },


  mounted() {
    this.name = this.$getDictItem('doctor')
    this.getInfo()
    let that = this
    for (var i = 1; i <= 100; i++) {
      const obj = {}
      obj.name = i + "天"
      obj.value = String(i)
      that.weekList.push(obj);
    }
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    stopSelect(i) {
      this.Btnobj = i
      this.Btnobj.stopReason = ''
      this.stopTips = true
      this.stopReasonRemark = ''
    },
    clickStopReason(i) {
      this.Btnobj.stopReason = i.key
      console.log(this.Btnobj.stopReason)
    },
    getInfo() {
      const params = {
        patientId: localStorage.getItem('userId')
      }
      Api.patientDrugsListAndHistory(params).then((res) => {
        if (res.data.code === 0) {
          // console.log(res)

          // res.data.data.medicines.forEach(element => {
          //   element.Numberdose = [[element.dose], [element.unit]]
          // });
          this.list = res.data.data.medicines
          this.historyList = res.data.data.historyMedicines
          // console.log(this.historyList)

        }
      })
    },
    stopDrug() {
      this.Btnobj.status = 1
      this.stopTips = false
      if (this.Btnobj.stopReason === 'other') {
        this.Btnobj.stopReasonRemark = this.stopReasonRemark
      } else {
        this.Btnobj.stopReasonRemark = ''
      }

      console.log(this.Btnobj)
      Api.FixpatientDrugs(this.Btnobj)
          .then((res) => {
            if (res.data.code === 0) {
              this.getInfo()
            }
          })
    },
    recoveryDrug(k, z) {
      this.btnTitle = '是否恢复用药？'
      this.BtnStatus = z
      this.Btnobj = k
      this.Btnshow = true
    },
    getdoseInfo(i) {
      this.selectObj = i
      if ((i.dose + '').indexOf(".") === -1) {
        this.Numberdose = [i.dose + '.0', i.unit]
      } else {
        this.Numberdose = [i.dose + '', i.unit]
      }
      this.showOne = true
    },
    getnumberOfDayInfo(i) {
      this.selectObj = i
      this.showTwo = true
      this.myNumber = [i.numberOfBoxes]
    },
    getcycleDayInfo(i) {
      console.log(i)
      this.selectObj = i
      this.showThree = true
    },
    hitBtn(i) {
      if (i === 'X') {
        this.selectObj.dose = ''
      } else {
        if (i === '0') {
          i = '0.'
        }
        this.selectObj.dose = this.selectObj.dose + String(i)
      }
    },
    goitem(k) {
      const params = {id: k.drugsId}
      if (k.drugsId) {
        this.$router.push({
          path: '/medicine/detail',
          query: {drugsId: k.drugsId, id: k.id}
        })
      } else {
        this.$router.push({
          path: '/medicine/addmedicine',
          query: {content: JSON.stringify(k)}
        })
      }


    },
    changeDay() {
      this.showTwo = false
      this.selectObj.numberOfDay = this.myNumber[0]
      this.putInfo()
    },
    changeNumber() {
      this.showOne = false

      this.selectObj.dose = this.Numberdose[0]
      this.selectObj.unit = this.Numberdose[1]
      console.log(this.selectObj)
      this.putInfo()
    },
    changeWeek() {
      this.showThree = false
      this.selectObj.cycle = parseInt(this.weekVal[0]) === 0 ? 0 : 1
      this.selectObj.cycleDay = parseInt(this.weekVal[0])
      // FixpatientDrugs
      this.putInfo()
    },
    putInfo() {
      console.log(this.selectObj)
      const params = {
        cycle: this.selectObj.cycle,
        cycleDay: this.selectObj.cycleDay,
        dose: this.selectObj.dose,
        drugsId: this.selectObj.drugsId,
        drugsTime: this.selectObj.drugsTime,
        medicineIcon: this.selectObj.medicineIcon,
        medicineName: this.selectObj.medicineName,
        numberOfBoxes: this.selectObj.numberOfBoxes,
        numberOfDay: this.selectObj.numberOfDay,
        patientId: this.selectObj.patientId,
        status: 0,
        unit: this.selectObj.unit,
        id: this.selectObj.id,
        patientDrugsTimeSettingList:this.selectObj.patientDrugsTimeSettingList
      }
      Api.FixpatientDrugs(this.selectObj).then((res) => {
        if (res.data.code === 0) {
          this.$vux.toast.text('修改成功！', 'center')
          this.getInfo()
        }
      })
    },
    onCancel() {
    },
    onConfirm() {
      if (this.Btnobj) {
        this.Btnobj.status = this.BtnStatus
        Api.FixpatientDrugs(this.Btnobj).then((res) => {
          if (res.data.code === 0) {
            this.getInfo()
          }
        })
      }

    },

  }
}
</script>
<style lang="less" scoped>
.no-btn{
  background:#dddddd !important;
  pointer-events:none
}
.main {
  width: 100vw;
  // padding-bottom: 40px;
  height: 100vh;
  .content {
    margin-top: 10px;
    .nodata {
      background: #fff;
      width: 100vw;
      height: 30vh;
      text-align: center;
      padding-top: 20vw;
      font-size: 14px;
      color: rgba(102, 102, 102, 0.85);
      background: #f5f5f5;
    }

    .item {
      background: #fff;
      padding: 10px 0px 20px;
      margin-top: 10px;
    }

    p {
      padding-left: 10px
    }
  }
}

/deep/ .vux-header {
  height: 50px;
}

/deep/ .van-cell {
  padding: 10px 15px !important;
}

</style>
