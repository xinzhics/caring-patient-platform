<template>
  <div>
    <div style="margin-top: 15px; background: #FFF; border-radius: 10px; padding: 10px;" v-if="currentList.length > 0">
      <div style="display: flex; align-items: center">
        <div style="width: 3px; height: 21px; background: #66E0A7;"></div>
        <span style="color: #333; font-size: 16px; font-weight: bold; margin-left: 10px;">当前用药</span>
      </div>
      <div style="margin-left: 15px; margin-right: 15px">
        <div v-for="(item, index) in currentList" :key="index" style="margin-top: 10px;" >
          <van-swipe-cell @open="openSwipe('current', index)" @close="closeSwipe('current', index)"
                          :style="{ backgroundColor: openCurrentIndex !== index? '#fff' : '#ee0a24',
                           borderTopRightRadius: openCurrentIndex !== index? '0px' : '10px',
                           borderBottomRightRadius: openCurrentIndex !== index? '0px' : '10px'}">
            <div class="box" :style="{ borderRadius: openCurrentIndex === index ? '0px' : '10px' }">
              <div style="width: 80%" @click="goItem(item)">
                <div style="font-size: 16px; color: #333; font-weight: bold">
                  {{ item.medicineName }} &nbsp; &nbsp; {{(item.dose === -1 ? '外用' : item.dose) + item.unit }}
                </div>
                <div style="font-size: 13px; color: #333; margin-top: 10px;">
                  {{ item.cycle === 0 ? '长期' : item.cycleDay + '天' }} 、{{ item.cycleDuration + getFrequency(item.timePeriod) + item.numberOfDay + '次' }}
                </div>
                <div style="display: flex; flex-flow: row wrap; align-items: flex-start;">
                  <div v-for="(itemDate, key) in item.patientDrugsTimeSettingList" :key="key"
                       style="margin-right: 10px; width: 55px; text-align: center; margin-top: 10px; font-size: 13px; color: #333; background: #66E0A7; color: #FFF; border-radius: 5px;">
                    {{ itemDate.dayOfTheCycle ? '第' + itemDate.dayOfTheCycle + '天' : '' }} {{ getTime(itemDate.triggerTimeOfTheDay) }}
                  </div>
                </div>
              </div>
              <div style="width: 20%; display: flex; justify-content: right">
                <van-switch :value="item.status" :active-color="'#66E0A7'" size="25px" :active-value="0"
                            :inactive-value="1" @input="currentSwitch(item)" />
              </div>
            </div>
            <template slot="right">
              <van-button square type="danger" text="删除" @click="deleteItem(item)"
                          style="height: 100%; border-top-right-radius: 10px; border-bottom-right-radius: 10px; margin-left: 2px"/>
            </template>
          </van-swipe-cell>
        </div>
      </div>
    </div>
    <div style="margin-top: 15px; background: #FFF; border-radius: 10px; padding: 10px;" v-if="historyList.length > 0">
      <div style="display: flex; align-items: center">
        <div style="width: 3px; height: 21px; background: #66E0A7;"></div>
        <span style="color: #333; font-size: 16px; font-weight: bold; margin-left: 10px;">历史用药</span>
      </div>
      <div style="margin-left: 15px; margin-right: 15px">
        <div v-for="(item, index) in historyList" :key="index" style="margin-top: 10px;">
          <van-swipe-cell @open="openSwipe('history', index)" @close="closeSwipe('history', index)"
                          :style="{ backgroundColor: openHistoryIndex !== index? '#fff' : '#ee0a24',
                           borderTopRightRadius: openHistoryIndex !== index? '0px' : '10px',
                           borderBottomRightRadius: openHistoryIndex !== index? '0px' : '10px'}">
            <div class="box" style="background: #EAEAEA" :style="{ borderRadius: openHistoryIndex === index ? '0px' : '10px' }">
              <div style="width: 80%">
                <div style="font-size: 16px; color: #333; font-weight: bold">
                  {{ item.medicineName }} &nbsp; &nbsp; {{(item.dose === -1 ? '外用' : item.dose) + item.unit }}
                </div>
                <div style="font-size: 13px; color: #333; margin-top: 10px;">
                  {{ item.cycle === 0 ? '长期' : item.cycleDay + '天' }}、{{ item.cycleDuration + getFrequency(item.timePeriod) + item.numberOfDay + '次' }}
                </div>
                <div style="display: flex; flex-flow: row wrap; align-items: flex-start;">
                  <div v-for="(itemDate, key) in item.patientDrugsTimeSettingList" :key="key"
                       style="margin-right: 10px; margin-top: 10px; font-size: 13px; color: #333; background: #B9B9B9; color: #FFF; border-radius: 5px; width: 55px; text-align: center;">
                    {{ getTime(itemDate.triggerTimeOfTheDay) }}
                  </div>
                </div>
              </div>
              <div style="width: 20%; display: flex; justify-content: right">
                <van-switch :value="item.status" :inactive-color="'#999999'" size="25px" :active-value="0"
                            :inactive-value="1" @input="historySwitch(item)" active-color="#34C759"/>
              </div>
            </div>
            <template slot="right">
              <van-button square type="danger" text="删除" @click="deleteItem(item)"
                          style="height: 100%; border-top-right-radius: 10px; border-bottom-right-radius: 10px; margin-left: 2px"/>
            </template>
          </van-swipe-cell>
        </div>
      </div>
    </div>
    <div v-if="!loading && currentList.length === 0 && historyList.length === 0" class="noData">
      <van-image :src="require('@/assets/my/noData.png')" width="70%"/>
      <div>{{patient}}未添加数据</div>
      <div style="margin-top: 5px;">点击<span style="background: #67E0A7; color: white; border-radius: 15px; padding: 3px 8px; margin: 0px 3px;" @click="goRecommend">推荐功能</span>则可将该功能推送至{{patient}}填写
      </div>
    </div>
    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;">
      <van-col span="24" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="addMedication">
          <span>新增用药提醒</span>
        </div>
      </van-col>
    </van-row>
    <van-dialog v-model="stopTips" style="border-radius: 20px;" :hide-on-blur="true" :showConfirmButton="false">
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
        <div style="color: #B8B8B8; font-size: 12px; text-align: center">
          <div>你可以反馈关闭原因</div>
          <div>帮我们更加智能的安排用药提醒</div>
        </div>

        <div style="margin-top: 15px; display: flex; flex-direction: column; align-items: center">
          <van-row wrap="wrap" :gutter="0">
            <van-col v-for="(i, k) in stopList" :key="k" span="12">
              <div style="margin: 8px 15px;" @click="clickStopReason(i)">
                <div
                  style="font-size: 12px; display: flex; justify-content: center; padding: 8px 0px; border-radius: 25px;"
                  :style="'background:'+ (Btnobj.stopReason === i.key ? '#66728B' : '#EEEEEE') +';color:'+ (Btnobj.stopReason === i.key ? '#ffffff' : '#999999')">
                  {{ i.label }}
                </div>
              </div>
            </van-col>
          </van-row>

          <div style="width: 90%; margin-top: 10px;"
               v-if="Btnobj.stopReason && Btnobj.stopReason === 'other'">
            <van-field
              v-model="stopReasonRemark"
              autosize
              rows="2"
              :style="{border: isMust ? '1px solid #F56C6C' : ''}"
              style="border-radius: 10px; background: #EEEEEE;"
              type="textarea"
              :maxlength="150"
              placeholder="请补充说明停药原因"
              show-word-limit
            />
          </div>

          <van-button mini type="primary" action-type="button"
                      style="height:44px; background: #66728B; width: 200px; margin-top: 20px; border: 0px !important;"
                      :class="Btnobj.stopReason==''||(Btnobj.stopReason=='other'&&stopReasonRemark=='')?'no-btn':''"
                      :disabled="Btnobj.stopReason!==''?false:true" round
                      @click.native="stopDrug()">确定
          </van-button>
        </div>
      </div>
    </van-dialog>
  </div>
</template>

<script>
import { patientDrugsListAndHistory, updatePatientDrugs, deletePatientDrugs } from '@/api/drugsApi.js'
import Vue from 'vue'
import {Switch, Field, SwipeCell, Button} from 'vant'
import moment from 'moment'

Vue.use(Switch)
Vue.use(Field)
Vue.use(SwipeCell)
Vue.use(Button)
export default {
  data () {
    return {
      historyList: [],
      currentList: [],
      isMust: false,
      stopTips: false,
      loading: false,
      Btnobj: {},
      patient: this.$getDictItem('patient'),
      openCurrentIndex: -1,
      openHistoryIndex: -1,
      stopReasonRemark: '',
      stopList: [
        {label: '病好了', key: 'recover'},
        {label: '疗效不好', key: 'bad_curative_effect'},
        {label: '出现不良反应', key: 'adverse_reactions'},
        {label: '检验指标异常', key: 'abnormal_test_index'},
        {label: '经济原因', key: 'economic_reasons'},
        {label: this.$getDictItem('doctor') + '换药', key: 'doctor_changing_medicine'},
        {label: '不想吃了', key: 'do_not_want_to_eat'},
        {label: '其他', key: 'other'}
      ]
    }
  },
  mounted () {
    this.getData()
  },
  methods: {
    goRecommend () {
      this.$emit('recommendation', 'MEDICINE')
    },
    deleteItem (item) {
      deletePatientDrugs(item.id)
        .then(() => {
          this.getData()
        })
    },
    addMedication () {
      if (this.$route.query && this.$route.query.imMessageId) {
        this.$router.push({path: '/patient/medicine/addmedicine', query: { imMessageId: this.$route.query.imMessageId }})
      } else {
        this.$router.push('/patient/medicine/addmedicine')
      }
    },
    goItem (item) {
      this.$router.push({
        path: '/patient/medicine/addmedicine',
        query: { medicineId: item.id }
      })
    },
    // 打开
    openSwipe (type, index) {
      if (type === 'current') {
        this.openCurrentIndex = index
      } else {
        this.openHistoryIndex = index
      }
    },
    // 关闭
    closeSwipe (type, index) {
      if (type === 'current') {
        this.openCurrentIndex = -1
      } else {
        this.openHistoryIndex = -1
      }
    },
    getData () {
      const params = {
        patientId: localStorage.getItem('patientId')
      }
      if (this.loading) {
        return
      }
      this.loading = true
      patientDrugsListAndHistory(params).then((res) => {
        if (res.code === 0) {
          this.currentList = res.data.medicines
          this.historyList = res.data.historyMedicines
        }
        this.loading = false
      })
    },
    getTime (time) {
      return moment(moment().format('yyyy-MM-DD') + ' ' + time).format('HH:mm')
    },
    currentSwitch (i) {
      this.Btnobj = i
      this.Btnobj.stopReason = ''
      this.stopTips = true
      this.stopReasonRemark = ''
      this.isMust = false
    },
    historySwitch (i) {
      let data = JSON.parse(JSON.stringify(i))
      data.status = 0
      updatePatientDrugs(data).then((res) => {
        if (res.code === 0) {
          this.getData()
        }
      })
    },
    getFrequency (timePeriod) {
      if (timePeriod === 'day') {
        return '天'
      } else if (timePeriod === 'week') {
        return '周'
      } else if (timePeriod === 'hour') {
        return '小时'
      } else if (timePeriod === 'moon') {
        return '月'
      }
    },
    clickStopReason (i) {
      this.Btnobj.stopReason = i.key
    },
    stopDrug () {
      if (this.Btnobj.stopReason === 'other') {
        if (!this.stopReasonRemark) {
          this.isMust = true
          return
        }
        this.Btnobj.stopReasonRemark = this.stopReasonRemark
      } else {
        this.Btnobj.stopReasonRemark = ''
      }
      this.Btnobj.status = 1
      this.stopTips = false
      updatePatientDrugs(this.Btnobj)
        .then((res) => {
          if (res.code === 0) {
            this.getData()
          }
        })
    }
  }
}

</script>

<style scoped lang="less">
.box {
  display: flex;
  align-items: center;
  background: #F2F8FD;
  padding: 15px;
  border-radius: 10px;
}
.caring-form-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 34px;
  text-align: center;
  border-radius: 17px 17px 17px 17px;
  opacity: 1;
  border: 1px solid #66E0A8;
  font-weight: 500;
  color: #66E0A7;
  font-size: 14px;
}

.noData {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-size: 13px;
  color: #999;
  padding-top: 80px;
}
</style>
