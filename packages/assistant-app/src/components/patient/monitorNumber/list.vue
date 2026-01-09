<template>
  <div style="background: #FFF; border-top: 1px solid #f5f5f5">
    <div style="padding-bottom: 10px;">
      <div v-for="(item, index) in dataList" :key="index" style="padding: 10px;">
        <van-swipe-cell @open="openIndex(index)" @close="closeIndex()">
          <div class="box" ref="myDiv"
               :style="{background: getDataFeedBack(item.dataFeedBack) !== 1 ? '#FFF4F4' : '#F5FDFA', borderRadius: pos === index ? '0px' : '10px'}">
            <div style="position: absolute; right: 0; width: 50px; height: 100%; z-index: -1" v-if="pos === index"
                 :style="{background: item.subscribe ? 'rgb(255, 90, 90)' : 'rgb(102, 224, 167)'}"/>
            <div style="padding: 15px; width: 100%" @click="detailBtn(item)">
              <div style="display: flex; align-items: center; justify-content: space-between">
                <div style="display: flex; align-items: center">
                <span style="font-weight: 600; color: #333333; font-size: 16px;">
                  {{ item.name }}
                </span>
                  <span v-if="item.dataFeedBack"
                        :style="{background: getDataFeedBack(item.dataFeedBack) !== 1 ? '#FF5A5A' : '#66E0A7'}"
                        style="font-size: 10px; background: #66E0A7; color: #fff; padding: 0px 6px; border-radius: 8px; margin-left: 5px; font-weight: bold">
                  {{ getFeedName(item.dataFeedBack) }}
                </span>
                </div>
                <div style="color: #333333; font-size: 12px;">
                  查看更多数据
                </div>
              </div>

              <div
                v-if="getContent(item, 1) !== '-' && getContent(item, 2) !== '-'"
                style="display: flex; align-items: center; height: 75px; flex-direction: column; justify-content: center">
                <div
                  style="width: 100%; font-size: 16px; font-weight: bold; color: #333; display: flex; align-items: center; justify-content: center">
                  <span style="flex: 1; text-align: center">{{ getContent(item, 1) }}</span>
                  <div style="width: 2px; background: #999; height: 20px;"></div>
                  <span style="flex: 1; text-align: center">{{ getContent(item, 2) }}</span>

                  <div style="width: 2px; background: #999; height: 20px;" v-if="item.planType === 1"></div>
                  <span style="flex: 1; text-align: center" v-if="item.planType === 1">{{ getContent(item, 5) }}</span>
                </div>
                <div
                  style="width: 100%; font-size: 13px; color: #333; display: flex; align-items: center; justify-content: center; margin-top: 10px;">
                  <span style="flex: 1; text-align: center">{{ getContent(item, 3) }}</span>
                  <span style="flex: 1; text-align: center">{{ getContent(item, 4) }}</span>
                  <span style="flex: 1; text-align: center" v-if="item.planType === 1">记录时间</span>
                </div>
              </div>
              <div v-else
                   style="display: flex; align-items: center; height: 75px; flex-direction: column; justify-content: center; color: #333333; font-size: 11px;">
                暂未填写相关数据
              </div>
            </div>
          </div>
          <template slot="right">
            <van-button v-if="item.subscribe" style="height: 100%; width: 60px; padding: 0px 20px;" square type="danger" text="取消提醒"
                        @click="cancelBtn(item, index)"/>
            <van-button v-else style="height: 100%; width: 60px; padding: 0px 20px;" square type="primary" text="提醒我"
                        @click="subscribeBtn(item)"/>
          </template>
        </van-swipe-cell>
      </div>
    </div>
  </div>
</template>

<script>
import {getPatientMonitoringDataPlan, setPatientMonitoringSubscribePlan, patientMonitoringCancelPlan} from '@/api/formResult.js'
import Vue from 'vue'
import moment from 'moment'
import {SwipeCell, Button} from 'vant'

Vue.use(SwipeCell)
Vue.use(Button)
export default {
  name: 'list',
  data () {
    return {
      pageTitle: '',
      patientId: localStorage.getItem('patientId'),
      dataList: [],
      pos: -1,
      screenWidth: (window.innerWidth - 120) + 'px'
    }
  },
  mounted () {
    this.getData()
  },
  methods: {
    // 获取数据
    getData () {
      if (!this.patientId) {
        return
      }
      getPatientMonitoringDataPlan(this.patientId)
        .then(res => {
          if (res && res.code === 0) {
            this.dataList = res.data
          }
        })
    },
    // 取消关注
    cancelBtn (item, index) {
      patientMonitoringCancelPlan(this.patientId, item.planId, item.planType)
        .then(res => {
          if (res && res.code === 0) {
            this.getData()
          }
        })
    },
    // 关注
    subscribeBtn (item) {
      setPatientMonitoringSubscribePlan(this.patientId, item.planId, item.planType)
        .then(res => {
          if (res && res.code === 0) {
            this.getData()
          }
        })
    },
    // 侧滑打开的位置
    openIndex (val) {
      this.pos = val
    },
    // 侧滑关闭
    closeIndex () {
      this.pos = -1
    },
    // 判断背景
    getDataFeedBack (dataFeedBack) {
      if (dataFeedBack && JSON.parse(dataFeedBack).normalAnomaly) {
        return JSON.parse(dataFeedBack).normalAnomaly
      }
      return 1
    },
    // 跳转列表
    detailBtn (item) {
      if (item.planType === 1) { // 血压监测
        localStorage.setItem('pageTitle', item.name)
        this.$router.push({path: '/patient/monitor/pressure', query: {planId: item.planId}})
      } else if (item.planType === 2) { // 血糖监测
        localStorage.setItem('pageTitle', item.name)
        this.$router.push('/patient/monitor/glucose')
      } else {
        this.$router.push({path: '/patient/monitor/show', query: {planId: item.planId, title: item.name}})
      }
    },
    getFeedName (dataFeedBack) {
      return JSON.parse(dataFeedBack).normalAnomalyText
    },
    // 列表内容
    getContent (item, type) {
      if (item.planType === 1) { // 血压监测
        if (type === 1) {
          if (item.formFields && item.formFields.length > 0 && item.formFields[0].values) {
            let arr = JSON.parse(item.formFields[0].values)
            if (arr && arr.length > 0 && arr[0].attrValue) {
              return arr[0].attrValue
            } else {
              return '-'
            }
          } else {
            return '-'
          }
        }
        if (type === 2) {
          if (item.formFields && item.formFields.length > 1 && item.formFields[1].values) {
            let arr = JSON.parse(item.formFields[1].values)
            if (arr && arr.length > 0 && arr[0].attrValue) {
              return arr[0].attrValue
            } else {
              return '-'
            }
          } else {
            return '-'
          }
        }
        if (type === 3) {
          return '收缩压/mmHg'
        }
        if (type === 4) {
          return '舒张压/mmHg'
        }
        if (type === 5) {
          return moment(item.formResultCreateTime).format('YYYY-MM-DD')
        }
      } else if (item.planType === 2) { // 血糖监测
        if (type === 1) {
          if (item.sugarValue) {
            return item.sugarValue
          } else {
            return '-'
          }
        }
        if (type === 2) {
          if (item.createDay) {
            return moment.unix(item.createDay).format('YYYY-MM-DD')
          }
          return '-'
        }

        if (type === 3) {
          return '空腹血糖/mmol'
        }
        if (type === 4) {
          return '记录时间'
        }
      } else {
        if (type === 1) {
          if (item.scoreQuestionnaire === 1) {
            // 是表单
            if (item.showFormResultSumScore === 1) {
              // 返回总分
              return item.formResultSumScore !== undefined ? item.formResultSumScore : '-'
            } else if (item.showFormResultAverageScore === 1) {
              // 返回平均分
              return item.formResultAverageScore !== undefined ? item.formResultAverageScore : '-'
            } else {
              // 返回得分
              return '-'
            }
          } else {
            if (item.formFields && item.formFields.length > 0 && item.formFields[0].values) {
              let arr = JSON.parse(item.formFields[0].values)
              return arr[0].attrValue ? arr[0].attrValue : '-'
            } else {
              return '-'
            }
          }
        }

        if (type === 3) {
          if (item.scoreQuestionnaire === 1) {
            // 是表单
            if (item.showFormResultSumScore === 1) {
              // 返回总分
              return '总分'
            } else if (item.showFormResultAverageScore === 1) {
              // 返回平均分
              return '平均分'
            } else {
              // 返回得分
              return '-'
            }
          } else {
            if (item.formFields && item.formFields.length > 0 && item.formFields[0].rightUnit) {
              return item.formFields[0].rightUnit ? item.formFields[0].rightUnit : '-'
            } else {
              return '-'
            }
          }
        }

        if (type === 2) {
          return item.formResultCreateTime ? moment(item.formResultCreateTime).format('YYYY-MM-DD') : '-'
        }
        if (type === 4) {
          return '记录时间'
        }
      }
    }
  }
}

</script>

<style scoped lang="less">
.box {
  border-radius: 10px;
  background: #F5FDFA;
  display: flex;
  align-items: center;
  height: 120px;
  position: relative;
  justify-content: space-between;

  .add {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #FFF;
    background: #66E0A7;
    width: 80px;
    height: 100%;
    flex-direction: column;
    border-radius: 10px;
  }
}

.van-button--primary {
  background-color: #66E0A7;
  border: 1px solid #66E0A7;
}
</style>
