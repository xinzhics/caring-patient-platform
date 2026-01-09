<template>
  <div style="height: 100%; position: relative">
    <div style="height: auto;">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh"
                        style="background-color: #f5f5f5; padding-bottom: 60px;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-if="list.length > 0">
            <van-swipe-cell  v-for="(i,k) in list" :key="k" style="margin-top: 15px;">
              <div style="background: white" @click="Goitem(i)">
                <div style="display: flex; align-items: center; padding: 15px; font-size: 16px; color: #333333">
                  <div style="display: flex; align-items: center">
                    <div
                      style="width: 3px; height: 21px; margin-right: 10px; border-radius: 10px; background: #66E0A7;"></div>
                    {{ getTime(i.createTime) }}
                  </div>
                  <div style="position: absolute; right: 15px; font-size: 14px;">
                    <span v-if="i.formErrorResult === 2" style="color: #FF5A5A">结果异常</span>
                    <span v-else style="color: #3BD26A">结果正常</span>
                    <!--                  <van-icon name="arrow" style="float: right"/>-->
                  </div>
                </div>
                <div style="margin-left: 15px; margin-right: 15px; height: 1px; background: #f5f5f5"></div>
                <div style="padding: 10px 0px 15px 0px">
                  <div style="padding: 10px 15px; font-size: 14px">
                    <div v-for="(item, index) in getArray(i.jsonContent)" :key="index">
                      <div style="display: flex; justify-content: space-between"
                           v-if="getFormValue(item).type === 'text'"
                           :style="{marginBottom: index === getArray(i.jsonContent).length - 1? '0' : '10px'}">
                        <div style="color: #999999; min-width: 100px;">{{ item.label }}：</div>
                        <div style="color: #333333">{{ getFormValue(item).value }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <template slot="right">
                <van-button style="height: 100%;"
                            @click="deleteFormResult(i.id)"
                            square type="danger" text="删除"/>
              </template>
            </van-swipe-cell>
          </div>
          <div v-if="!loading && list.length === 0" class="noData">
            <van-image :src="require('@/assets/my/noData.png')" width="70%"/>
            <div>{{patient}}未添加数据</div>
            <div style="margin-top: 5px;">点击
              <span @click="goRecommend"
                    style="background: #67E0A7; color: white; border-radius: 15px; padding: 3px 8px; margin: 0px 3px;">推荐功能</span>则可将该功能推送至{{patient}}填写
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;">
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #FFF" @click="putOrDeletePlan">
          <span>{{ subscribePLan ? '关闭' : '开启' }}提醒</span>
        </div>
      </van-col>
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add()">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
  </div>
</template>

<script>
import Vue from 'vue'
import {getPatientSubscribePlan, deletePatientSubscribePlan, putPatientSubscribePlan} from '@/api/plan.js'
import {pageFormResultByType} from '@/api/formApi.js'
import {deleteFormResult} from '@/api/formResult.js'
import {SwipeCell, Button, Empty, Dialog, CellGroup, PullRefresh, List} from 'vant'
import {getValue} from '@/components/utils/index'
import {Constants} from '@/api/constants.js'
import moment from 'moment'

Vue.use(SwipeCell)
Vue.use(Button)
Vue.use(Empty)
Vue.use(CellGroup)
Vue.use(PullRefresh)
Vue.use(List)
export default {
  data () {
    return {
      list: [],
      patientId: localStorage.getItem('patientId'),
      refreshing: false,
      current: 1,
      loading: false,
      finished: false,
      planType: 3,
      planId: undefined,
      subscribePLan: undefined,
      patient: this.$getDictItem('patient')
    }
  },
  mounted () {
    this.onRefresh()
    this.getPatientSubscribePlanFunction()
  },
  methods: {
    goRecommend () {
      this.$emit('recommendation', 'TEST_NUMBER')
    },
    add () {
      this.$router.push('/inspectionData/editor')
    },
    putOrDeletePlan () {
      console.log('=============', this.subscribePLan)
      if (this.subscribePLan) {
        Dialog.confirm({
          message: '关闭提醒后，将无法接收填写提醒，是否确认关闭？'
        })
          .then(() => {
            this.subscribePLan = false
            this.deletePatientSubscribePlanFunction()
          })
      } else {
        this.subscribePLan = true
        this.putPatientSubscribePlanFunction()
      }
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.current = 1
      // 重新加载数据
      this.onLoad()
    },
    onLoad () {
      console.log('=======onload', this.loading)
      if (this.loading) {
        return
      }
      this.loading = true
      if (this.current === 1) {
        this.list = []
        this.refreshing = false
      }
      pageFormResultByType(this.patientId, this.current, this.planType).then((res) => {
        this.loading = false
        if (res.code === 0) {
          this.list.push(...res.data.records)
          if (res.data.pages <= this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
      })
    },
    getTime (time) {
      return moment(time).format('YYYY年MM月DD日')
    },
    getArray (json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (i < 4) {
          list.push(jsonList[i])
        } else if (!flag && (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload || jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload)) {
          flag = true
          list.push(jsonList[i])
        }
      }
      return list
    },
    getFormValue (item) {
      return getValue(item)
    },
    /**
     * 订阅计划
     */
    putPatientSubscribePlanFunction () {
      putPatientSubscribePlan(this.patientId, this.planId, this.planType).then(res => {
        if (res.code === 0) {
          this.getPatientSubscribePlanFunction()
        }
      })
    },

    /**
     * 取消订阅
     */
    deletePatientSubscribePlanFunction () {
      deletePatientSubscribePlan(this.patientId, this.planId, this.planType).then(res => {
        if (res.code === 0) {
          this.getPatientSubscribePlanFunction()
        }
      })
    },

    /**
     * 获取订阅状态
     */
    getPatientSubscribePlanFunction () {
      getPatientSubscribePlan(this.patientId, this.planId, this.planType).then(res => {
        if (res.code === 0) {
          this.subscribePLan = res.data
        }
      })
    },
    // 跳转
    Goitem (item) {
      this.$router.push({
        path: '/inspectionData/showdata',
        query: {
          content: item.id
        }
      })
    },
    deleteFormResult (id) {
      deleteFormResult(id).then((res) => {
        if (res.code === 0) {
          this.onRefresh()
        }
      })
    }
  }
}

</script>

<style scoped lang="less">
.noData {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-size: 13px;
  color: #999;
  padding-top: 80px;
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
</style>
