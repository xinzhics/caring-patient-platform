<template>
  <section style="position: relative; height: 100vh">
    <navBar backUrl="/home" :pageTitle="pageTitle !== undefined ? pageTitle : '健康日志'"/>
    <div v-if="allData.length == 0 && !loading" :style="{width: pageWidth + 'px', height: pageHeight + 'px'}">
      <div class="nodata">
        <img :src="noData" alt="" style="padding-top: 150px;">
        <p>暂未添加{{pageTitle ? pageTitle : '健康日志'}}</p>
        <p>请点击下方添加按钮进行添加</p>
      </div>
    </div>
    <div v-else :style="{width: pageWidth + 'px', height: pageHeight + 'px'}" style="overflow-y: auto">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #f5f5f5; padding-bottom: 50px;" >
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <group v-for="(i,k) in allData" :key="i.id" style="padding: 0px 10px 5px 10px">
            <div style="background: white; padding: 10px 0" @click="Goitem(i)">
              <div style="display: flex; align-items: center; padding: 5px 15px; font-size: 16px; color: #333333">
                <div style="display: flex; align-items: center">
                  <div class="caring-form-div-icon"></div>
                  <span style="margin-left: 10px; color: #666; font-size: 16px">
                    {{ getTime(i.createTime) }}
                  </span>
                </div>
                <div style="position: absolute; right: 38px">
                  <span class="caring-form-look-detail-button" style="font-size: 14px; color: #3BD26A">查看详情</span>
                </div>
              </div>
              <div style="padding: 10px 15px 15px 15px">
                <div style="border-radius: 10px; padding: 10px 6px;">
                  <div v-for="(item, index) in getArray(i.jsonContent)" :key="index" style="padding: 5px 0">
                    <van-row v-if="getFormValue(item).type === 'text'">
                      <van-col span="12">
                        <div style="color: #333333; font-weight: 400; font-size: 14px">{{ item.label }}：</div>
                      </van-col>
                      <van-col span="12" style="display: flex">
                        <div style="color: #333333; font-weight: 500; font-size: 14px; width: 90%; text-align: right">
                          {{ getFormValue(item).value }}
                        </div>
                        <div style="width: 10%; color: #999; align-items: center; display: flex; justify-content: center;">
                          <van-icon name="arrow"  />
                        </div>
                      </van-col>
                    </van-row>
                  </div>
                </div>
              </div>
            </div>
          </group>
        </van-list>
      </van-pull-refresh>
    </div>
    <van-row style="padding: 10px 0 0 0; position: fixed; bottom: 10px; width: 100%;">
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #FFF" @click="putOrDeletePlan">
          <span>{{subscribePLan ? '关闭' : '开启'}}提醒</span>
        </div>
      </van-col>
      <van-col span="12" style="padding: 0 20px">
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="add">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
  </section>
</template>
<script>
import {Group, Cell, CellBox} from 'vux'
import Api from '@/api/Content.js'
import {deletePatientSubscribePlan, putPatientSubscribePlan, getPatientSubscribePlan} from '@/api/plan.js'
import {getValue} from "@/components/utils/index"
import {Constants} from '@/api/constants.js'
import { Dialog } from 'vant'

export default {
  components: {
    Group,
    Cell,
    CellBox,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      pageTitle: '',
      noData: require('@/assets/my/medicineImg.png'),
      addImg: require('@/assets/my/add.png'),
      value: '',
      current: 1,
      patientId: localStorage.getItem('userId'),
      loading: false,
      finished: false,
      refreshing: false,
      planType: 5,
      planId: undefined,
      allData: [],
      subscribePLan: undefined,
      pageWidth: window.innerWidth,
      pageHeight: window.innerHeight - 46 - 33 - 10 - 20
    }
  },
  created() {
    this.onRefresh()
    this.getPatientSubscribePlanFunction()
  },
  mounted() {
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    getImage(values) {
      if (values) {
        if (values.length === 1) {
          values.push({attrValue: ""})
          values.push({attrValue: ""})
          values.push({attrValue: ""})
        }else if (values.length === 2) {
          values.push({attrValue: ""})
          values.push({attrValue: ""})
        }else if (values.length === 3) {
          values.push({attrValue: ""})
        }
      }
      return values
    },

    /**
     * 订阅计划
     */
    putPatientSubscribePlanFunction() {
      putPatientSubscribePlan(this.patientId, this.planId, this.planType).then(res => {
        if (res.data.code === 0) {
          this.getPatientSubscribePlanFunction()
        }
      })
    },

    /**
     * 取消订阅
     */
    deletePatientSubscribePlanFunction() {
      deletePatientSubscribePlan(this.patientId, this.planId, this.planType).then(res => {
        if (res.data.code === 0) {
          this.getPatientSubscribePlanFunction()
        }
      })
    },

    /**
     * 获取订阅状态
     */
    getPatientSubscribePlanFunction() {
      getPatientSubscribePlan(this.patientId, this.planId, this.planType).then(res => {
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

    getFormValue(item) {
      return getValue(item)
    },
    getTime(time) {
      return moment(time).format("YYYY年MM月DD日")
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
      return list
    },
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      this.current = 1;
      // 重新加载数据
      this.allData = []
      this.loading = true;
      this.onLoad();
    },
    onLoad() {
      const params = {
        type: 5,
        current: this.current,
        patientId: localStorage.getItem('userId')
      }
      Api.getCheckData(params).then((res) => {
        if (res.data.code === 0) {
          this.allData.push(...res.data.data.records)
          this.loading = false;
          this.refreshing = false;
          if (res.data.data.pages === 0 || res.data.data.pages === this.current) {
            this.finished = true;
          } else {
            this.finished = false
            this.current++
          }
        }
      })
    },
    add() {
      this.$router.push({
        path: '/healthCalendar/editor'
      })
    },
    Goitem(k) {
      this.$router.push({
        path: '/healthCalendar/form/result',
        query: {
          formResultId: k.id
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
    background-color: #fafafa;
  }

  .nodata {
    width: 100vw;
    height: 90vh;
    text-align: center;
    font-size: 14px;
    color: rgba(102, 102, 102, 0.85);
    background: #f5f5f5;
  }

  /deep/ .vux-header {
    margin-bottom: 0px;
    height: 50px;
    position: fixed;
    width: 100%;
    z-index: 999;
    top: 0;
    left: 0;
  }

</style>
