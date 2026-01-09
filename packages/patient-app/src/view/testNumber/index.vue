<template>
  <section class="main">
    <navBar
            :pageTitle="pageTitle !== undefined ? pageTitle : '检验数据'"
            backUrl="/home"/>
    <div v-if="allData.length === 0&&show">
      <div class="nodata">
        <img :src="noData" alt="" style="padding-top: 150px;">
        <p>暂未添加{{ pageTitle ? pageTitle : '检验数据' }}</p>
        <p>请点击下方添加按钮进行添加</p>
      </div>
    </div>
    <div v-if="allData.length>0&&show" :style="{width: pageWidth + 'px', height: pageHeight + 'px'}" style="overflow-y: auto">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh" style="background-color: #f5f5f5; padding-bottom: 60px;">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad">
          <group v-for="(i,k) in allData" :key="k">
            <van-swipe-cell>
              <div style="background: white" @click="Goitem(i)">
                <div style="display: flex; align-items: center; padding: 15px; font-size: 16px; color: #333333">
                  <div style="display: flex; align-items: center">
                    <div style="width: 3px; height: 21px; margin-right: 10px; border-radius: 10px; background: #66E0A7;"></div>
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
                  <div style="padding: 10px 15px;">
                    <div v-for="(item, index) in getArray(i.jsonContent)" :key="index">
                      <div style="display: flex; justify-content: space-between"
                           v-if="getFormValue(item).type === 'text'"
                      :style="{marginBottom: index === getArray(i.jsonContent).length - 1? '0' : '10px'}">
                        <div style="color: #999999; min-width: 100px">{{ item.label }}：</div>
                        <div style="color: #333333">{{ getFormValue(item).value }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <template #right>
                <van-button style="height: 100%;"
                            @click="deleteFormResult(i.id)"
                            square type="danger" text="删除"/>
              </template>
            </van-swipe-cell>
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
        <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF;" @click="$router.push('/testNumber/editor')">
          <span>添加记录</span>
        </div>
      </van-col>
    </van-row>
  </section>
</template>
<script>
import {Group, Cell, CellBox} from 'vux'
import Api from '@/api/Content.js'
import Vue from 'vue';
import {getValue} from "@/components/utils/index"
import {Constants} from '@/api/constants.js'
import {SwipeCell, Button, Dialog} from 'vant';
import {deletePatientSubscribePlan, putPatientSubscribePlan, getPatientSubscribePlan} from '@/api/plan.js'

Vue.use(SwipeCell);
Vue.use(Button);
export default {
  components: {
    Group,
    Cell,
    CellBox,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      noData: require('@/assets/my/medicineImg.png'),
      addImg: require('@/assets/my/add.png'),
      value: '',
      pageTitle: '',
      allData: [],
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      subscribePLan: undefined,
      show: false,
      planType: 3, // 检验数据类型为3， 健康日志类型为5
      planId: undefined,
      pageWidth: window.innerWidth,
      pageHeight: window.innerHeight - 46 - 33 - 10 - 20,
      patientId: localStorage.getItem('userId'),
    }
  },
  created() {
    this.onRefresh()
  },
  mounted() {
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
    this.getPatientSubscribePlanFunction()
  },
  methods: {
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
    getTime(time) {
      return moment(time).format("YYYY年MM月DD日")
    },
    getArray(json) {
      let list = []
      let flag = false
      let jsonList = JSON.parse(json)
      for (let i = 0; i < jsonList.length; i++) {
        if (i < 4) {
          list.push(jsonList[i])
        } else if (!flag && (jsonList[i].widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || jsonList[i].widgetType === Constants.CustomFormWidgetType.MultiImageUpload)) {
          flag = true
          list.push(jsonList[i])
        }
      }

      return list
    },
    onRefresh() {
      // 清空列表数据
      this.finished = false;
      this.current = 1
      // 重新加载数据
      // 将 loading 设置为 true，表示处于加载状态
      this.allData = []
      this.loading = true;
      this.onLoad();
    },
    deleteFormResult(id) {
      Api.deleteFormResult(id).then((res) => {
        if (res.data.code === 0) {
          this.onRefresh()
        }
      })
    },
    onLoad() {
      const params = {
        type: 3,
        current: this.current,
        patientId: localStorage.getItem('userId')
      }
      this.show = false
      Api.getCheckData(params).then((res) => {
        if (res.data.code === 0) {
          this.allData.push(...res.data.data.records)
          this.loading = false;
          this.show = true
          if (res.data.data.pages === 0 || res.data.data.pages === this.current) {
            this.finished = true;
          } else {
            this.finished = false
            this.current++
          }
        }
      })
    },
    Goitem(k) {
      this.$router.push({
        path: '/testNumber/detail',
        query: {
          content: k.id
        }
      })
    }
  }
}
</script>


<style lang="less" scoped>
.main {
  width: 100vw;
  height: 100vh;
  background: #F5F5F5;
  position: relative;
}

.nodata {
  width: 100vw;
  height: 90vh;
  text-align: center;
  font-size: 14px;
  color: rgba(102, 102, 102, 0.85);
  background: #f5f5f5;
}

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
}
/deep/ .van-list__finished-text{
  background: #F5F5F5;
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

