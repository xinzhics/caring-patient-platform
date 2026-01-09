<template>
  <section class="allContent">
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '监测数据'" backUrl="/home"></navBar>
    <div style="padding-bottom: 10px;">
      <div v-for="(item, index) in list" :key="index" style="margin: 10px;">
        <van-swipe-cell @open="openIndex(index)" @close="closeIndex()">
          <div class="box" ref="myDiv"
               :style="{background: getDataFeedBack(item.dataFeedBack) !== 1 ? '#FFF4F4' : '#F5FDFA'}">
            <div style="position: absolute; right: 0; width: 50px; height: 100%; z-index: -1" v-if="pos === index"
                 :style="{background: getDataFeedBack(item.dataFeedBack) !== 1 ? 'rgb(255, 90, 90)' : 'rgb(102, 224, 167)'}"/>
            <div style="padding: 10px;" :style="{width: screenWidth}" @click="detailBtn(item)">
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
              <div
                style="display: flex; align-items: center; height: 75px; flex-direction: column; justify-content: center">
                <div
                  style="width: 100%; font-size: 16px; font-weight: bold; color: #333; display: flex; align-items: center; justify-content: center">
                  <span style="width: 49%; text-align: center">{{ getContent(item, 1) }}</span>
                  <div style="width: 2px; background: #999; height: 20px;"></div>
                  <span style="width: 49%; text-align: center">{{ getContent(item, 2) }}</span>
                </div>
                <div
                  style="width: 100%; font-size: 13px; color: #333; display: flex; align-items: center; justify-content: center; margin-top: 10px;">
                  <span style="width: 50%; text-align: center">{{ getContent(item, 3) }}</span>
                  <span style="width: 50%; text-align: center">{{ getContent(item, 4) }}</span>
                </div>
              </div>
            </div>
            <div class="add" @click="addBtn(item)"
                 :style="{background: getDataFeedBack(item.dataFeedBack) !== 1 ? '#FF5A5A' : '#66E0A7'}">
              <van-icon name="add-o" size="30px"/>
              <span style="font-size: 12px; margin-top: 15px; font-weight: bold">添加记录</span>
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
      <div v-if="list.length===0&&showNoData" style="padding-top: 50vh;transform: translateY(-25%)">
        <van-empty :image="require('@/assets/my/no_data.png')" description="暂无数据"/>
      </div>
    </div>
  </section>


</template>

<script>
import Api from '@/api/Content.js'
import Vue from 'vue';
import moment from "moment";
import {SwipeCell, Button} from 'vant';

Vue.use(SwipeCell);
Vue.use(Button);

export default {
  components: {
    navBar: () => import('@/components/headers/navBar')
  },
  name: "index",
  data() {
    return {
      pageTitle: '',
      list: [],
      showNoData: false,
      noData: require('@/assets/my/no_data.png'),
      screenWidth: (window.innerWidth - 120) + 'px',
      pos: -1
    }
  },
  created() {
    this.getData()
  },
  mounted() {
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    // 获取数据
    getData() {
      Api.getPatientMonitoringDataPlan()
        .then(res => {
          if (res.data && res.data.code === 0) {
            this.showNoData = true
            this.list = res.data.data
          }
        })
    },
    // 侧滑打开的位置
    openIndex(val) {
      this.pos = val
    },
    // 侧滑关闭
    closeIndex() {
      this.pos = -1
    },
    // 取消关注
    cancelBtn(item, index) {
      Api.patientMonitoringCancelPlan(item.planId, item.planType)
        .then(res => {
          if (res.data && res.data.code === 0) {
            this.getData()
          }
        })
    },
    // 关注
    subscribeBtn(item) {
      Api.setPatientMonitoringSubscribePlan(item.planId, item.planType)
        .then(res => {
          if (res.data && res.data.code === 0) {
            this.getData()
          }
        })
    },
    // 判断背景
    getDataFeedBack(dataFeedBack) {
      if (dataFeedBack && JSON.parse(dataFeedBack).normalAnomaly) {
        return JSON.parse(dataFeedBack).normalAnomaly
      }
      return 1
    },
    getFeedName(dataFeedBack) {
      return JSON.parse(dataFeedBack).normalAnomalyText
    },
    // 列表内容
    getContent(item, type) {
      if (item.planType === 1) {//血压监测
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
          if (item.formFields && item.formFields.length > 1 &&  item.formFields[1].values) {
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

      } else if (item.planType === 2) {//血糖监测
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
              //返回总分
              return item.formResultSumScore !== undefined ? item.formResultSumScore : '-'
            } else if (item.showFormResultAverageScore === 1) {
              //返回平均分
              return item.formResultAverageScore !== undefined ? item.formResultAverageScore : '-'
            } else {
              //返回得分
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
              //返回总分
              return '总分'
            } else if (item.showFormResultAverageScore === 1) {
              //返回平均分
              return '平均分'
            } else {
              //返回得分
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
    },
    addBtn(item) {
      if (item.planType === 1) {//血压监测
        localStorage.setItem("pageTitle", item.name);
        this.$router.push({path: '/monitor/pressureEditor', query: {planId: item.id}})
      } else if (item.planType === 2) {//血糖监测
        localStorage.setItem("pageTitle", item.name);
        this.$router.push({path: '/monitor/glucoseEditor'})
      } else {
        this.$router.push({
          path: '/monitor/add',
          query: {
            id: item.planId, title: item.name
          }
        })
      }
    },
    detailBtn(item) {
      console.log(item)
      if (item.planType === 1) {//血压监测
        localStorage.setItem("pageTitle", item.name);
        this.$router.push({path: '/monitor/pressure', query: {planId: item.planId}})
      } else if (item.planType === 2) {//血糖监测
        localStorage.setItem("pageTitle", item.name);
        this.$router.push('/monitor/glucose')
      } else {
        this.$router.push({path: '/monitor/show', query: {id: item.planId, title: item.name}})
      }
    },
  }
}
</script>

<style lang="less" scoped>
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
