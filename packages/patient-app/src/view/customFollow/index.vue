<template>
  <section style="background: #F7F7F7; height: 100vh; position: relative">
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '随访'" backUrl="/home" :showRightIcon="injectionModel"
            :rightIcon="injectionIcon" @toHistoryPage="toCalendar()"/>

    <div v-if="!loading && allData.length === 0" style="background:#f5f5f5"
         :style="{width: pageWidth + 'px', height: pageHeight + 'px'}">
      <div>
        <div class="nodata" style="padding-top:100px;">
          <img :src="noData" alt="">
          <p>您暂未添加{{ pageTitle ? pageTitle : '随访' }}</p>
          <p>请点击下方添加按钮进行添加</p>
        </div>
      </div>
    </div>
    <div v-else :style="{width: pageWidth + 'px', height: pageHeight + 'px'}" style="overflow-y: auto">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #f5f5f5;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <div v-for="(i, k) in allData" :key="k" @click="Goitem(i)">
            <van-swipe-cell style="width: auto; margin: 15px 10px; background-color: #fff; border-radius: 12px;">
              <div v-if="i.scoreQuestionnaire === 1">
                <div style="display: flex; font-size: 14px">
                  <div style="color:#323233; padding: 17px 16px; width: calc(100% - 122px)">
                    <div>{{ getTime(i.createTime, 'YYYY年MM月DD日') }}</div>
                    <div style="font-size: 12px; color: #666; margin-top: 8px" v-if="headerTenant !== 'MDQ0OQ=='">
                      {{ getEstimateTime(i) }}
                    </div>
                  </div>
                  <div
                    style="width: 90px;color: #68E1A8;justify-content: right; align-items: center;display: flex; padding: 0 16px">
                    查看详情
                  </div>
                </div>
                <div style="border-bottom: 1px solid #ebedf0"></div>
                <van-cell title="总得分" :border="false" v-if="i.showFormResultSumScore === 1"
                          style="border-radius: 12px">
                  <div style="color: #333">
                    {{ i.formResultSumScore }}
                  </div>
                </van-cell>
                <van-cell title="平均分" v-if="i.showFormResultAverageScore === 1"
                          style="border-radius: 12px;"
                          :style="{marginTop: i.showFormResultSumScore === 1 ? '-5px' : '0px'}">
                  <div style="color: #333">
                    {{ i.formResultAverageScore }}
                  </div>
                </van-cell>
              </div>
              <div v-else style="padding: 5px 10px">
                <div>
                  <div style="background: white; padding: 10px 0">
                    <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                      <div style="display: flex; align-items: center">
                        <div class="caring-form-div-icon"></div>
                        <span style="margin-left: 10px">
                          {{ getTime(i.createTime, 'YYYY-MM-DD HH:mm') }}
                        </span>
                      </div>
                      <div style="position: absolute; right: 20px">
                        <span class="caring-form-look-detail-button">查看详情</span>
                      </div>
                    </div>
                    <div style="padding: 10px">
                      <div style="border-radius: 10px; padding: 10px 6px;">
                        <div v-for="(item, index) in getArray(i.jsonContent)" :key="index" style="padding: 5px 0">
                          <van-row v-if="getFormValue(item).type === 'text'">
                            <van-col span="12">
                              <div style="color: #333333; font-weight: 400; font-size: 14px">{{ item.label }}：</div>
                            </van-col>
                            <van-col span="12" style="display: flex">
                              <div
                                style="color: #333333; font-weight: 500; font-size: 14px; width: 90%; text-align: right">
                                {{ getFormValue(item).value }}
                              </div>
                              <div
                                style="width: 10%; color: #999;  align-items: center; display: flex; justify-content: center;">
                                <van-icon name="arrow"/>
                              </div>
                            </van-col>
                          </van-row>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <template slot="right">
                <van-button style="height: 100%; width: 60px; padding: 0px 20px;" square type="danger" text="删除"
                            @click="deleteBtn(i, k)"/>
              </template>
            </van-swipe-cell>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>

    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;"
             v-if="injectionModel === false && headerTenant !== 'MDQ0OQ=='">
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" @click="putOrDeletePlan">
          <span>{{ subscribePLan ? '关闭' : '开启' }}提醒</span>
        </div>
      </van-col>
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
    <!--  敏宁日记、注射记录关闭提醒 -->
    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;" v-else>
      <van-col span="24" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>

  </section>
</template>
<script>
import Vue from 'vue'
import {Group} from 'vux'
import Api from '@/api/Content.js'
import {
  deletePatientSubscribePlan,
  putPatientSubscribePlan,
  getPatientSubscribePlan,
  queryPlanInfo
} from '@/api/plan.js'
import {getValue} from "@/components/utils/index"
import {Constants} from '@/api/constants.js'
import {Cell, Dialog, Button, SwipeCell} from 'vant'

Vue.use(Cell)
Vue.use(Button)
Vue.use(SwipeCell)

export default {
  components: {
    Group,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      planId: this.$route.params.planId,
      pageTitle: '',
      noData: require('@/assets/my/medicineImg.png'),
      injectionIcon: require('@/assets/my/calendar.png'),
      value: '',
      current: 1,
      patientId: localStorage.getItem('userId'),
      headerTenant: localStorage.getItem('headerTenant'),
      allData: [],
      loading: false,
      finished: true,
      refreshing: false,
      subscribePLan: undefined,
      injectionModel: undefined,
      pageWidth: window.innerWidth,
      pageHeight: window.innerHeight - 46 - 33 - 10 - 20
    }
  },
  created() {
    const routerData = localStorage.getItem("routerData")
    const path = this.$route.path
    if (routerData && routerData.length > 0) {
      const routerDataArray = JSON.parse(routerData)
      for (let i = 0; i < routerDataArray.length; i++) {
        if (path.indexOf(routerDataArray[i].path) > -1) {
          localStorage.setItem('pageTitle', routerDataArray[i].name)
        }
      }
    }
    this.queryPlan(this.planId)
  },
  methods: {
    // 查询随访计划的设置
    queryPlan(planId) {
      queryPlanInfo(planId).then(res => {
        console.log('queryPlanInfo', res)
        const plan = res.data.data
        this.pageTitle = plan.name
        if (plan.planModel === 1) {
          this.injectionModel = true
        } else {
          this.injectionModel = false
        }
        this.onRefresh()
        this.getPatientSubscribePlanFunction()
      })
    },
    deleteBtn(item, index) {
      Api.deleteFormResult(item.id)
        .then(res => {
          this.allData.splice(index, 1)
        })
    },
    getEstimateTime(item) {
      const jsonContent = item.jsonContent
      const jsonArray = JSON.parse(jsonContent)
      let field = undefined;
      const date = this.Constants.CustomFormWidgetType.Date
      const checkTime = this.Constants.FieldExactType.CHECK_TIME
      for (let i = 0; i < jsonArray.length; i++) {
        console.log(jsonArray[i].widgetType, jsonArray[i].exactType)
        if (jsonArray[i].widgetType === date && jsonArray[i].exactType === checkTime) {
          field = jsonArray[i]
          break
        }
      }
      if (field) {
        if (field.values && field.values[0] && field.values[0].attrValue) {
          return ' 填写时间：' + item.firstSubmitTime
        }
      }
      return ''
    },
    /**
     * 订阅计划
     */
    putPatientSubscribePlanFunction() {
      putPatientSubscribePlan(this.patientId, this.planId).then(res => {
        if (res.data.code === 0) {
          this.getPatientSubscribePlanFunction()
        }
      })
    },

    /**
     * 取消订阅
     */
    deletePatientSubscribePlanFunction() {
      deletePatientSubscribePlan(this.patientId, this.planId).then(res => {
        if (res.data.code === 0) {
          this.getPatientSubscribePlanFunction()
        }
      })
    },

    /**
     * 获取订阅状态
     */
    getPatientSubscribePlanFunction() {
      getPatientSubscribePlan(this.patientId, this.planId).then(res => {
        if (res.data.code === 0) {
          this.subscribePLan = res.data.data
        }
      })
    },

    putOrDeletePlan() {
      if (this.subscribePLan) {
        Dialog.confirm({
          message: '关闭提醒后，将无法接收填写提醒，是否确认关闭？',
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

    getImage(values) {
      if (values) {
        if (values.length === 1) {
          values.push({attrValue: ""})
          values.push({attrValue: ""})
          values.push({attrValue: ""})
        } else if (values.length === 2) {
          values.push({attrValue: ""})
          values.push({attrValue: ""})
        } else if (values.length === 3) {
          values.push({attrValue: ""})
        }
      }
      return values
    },

    getFormValue(item) {
      return getValue(item)
    },

    getTime(time, format) {
      return moment(time).format(format)
    },

    getArray(json) {
      let list = []
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.SplitLine
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.Page
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.Avatar
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.Desc) {
          continue
        } else {
          list.push(jsonList[i])
        }
        if (list.length === 3) {
          break
        }
      }
      console.log('getArray', list)
      return list
    },

    onRefresh() {
      // 清空列表数据
      this.finished = false;
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.loading = true;
      this.list = [];
      this.current = 1;
      this.refreshing = false;
      this.onLoad();
    },
    onLoad() {
      this.getInfo()
    },
    getInfo() {
      const params = {
        planId: this.planId,
        current: this.current,
        patientId: localStorage.getItem('userId')
      }
      Api.getCustomPlanForm(params).then((res) => {
        if (res.data.code === 0) {
          console.log(res);
          if (this.current === 1) {
            this.allData = res.data.data.records
          } else {
            this.allData.push(...res.data.data.records)
          }
          this.loading = false;
          if (this.current >= res.data.data.pages) {
            this.finished = true;
          }
          this.current++; // 当前页加1
        }
      })
    },
    add() {
      this.$router.push({
        path: `/custom/follow/${this.planId}/editor`,
        query: {
          pageTitle: this.pageTitle,
        }
      })
    },
    toCalendar() {
      this.$router.push({
        path: `/custom/follow/${this.planId}/calendar`,
        query: {
          from: 'index'
        }
      })
    },
    Goitem(k) {
      if (k.scoreQuestionnaire === 1) {
        // 评分表单
        this.$router.push({
          path: '/score/show',
          query: {
            dataId: k.id,
            title: this.pageTitle,
            id: this.planId,
          }
        })
      } else {
        this.$router.push({
          path: `/custom/follow/detail/result`,
          query: {
            formId: k.id,
            planId: this.planId,
            pageTitle: this.pageTitle,
            injectionModel: this.injectionModel
          }
        })
      }
    }
  }
}

</script>


<style lang="less" scoped>

.allContent {
  width: 100vw;
  background-color: #fafafa;
}

.nodata {
  width: 100vw;
  text-align: center;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5 !important;
}

</style>

