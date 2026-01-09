<template>
  <div style="background: #f5f5f5;min-height: 100vh">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="$getDictItem('patient') + '转诊'" @onBack="back"></headNavigation>
    </van-sticky>
<!--    列表-->
    <div>
      <van-pull-refresh v-if="list.length > 0" v-model='refreshing' style="min-height: 90vh" @refresh='onRefresh'>
        <van-list
          v-model='loading'
          :finished='finished'
          @load='onLoad'>
          <div class="list" v-for="item in list" :key="item.id">
            <div style="display: flex;justify-content: space-between;align-items: center;padding-bottom: 11px;border-bottom: 1px solid #EEEEEE">
              <div>
                <div style="display: flex;align-items: center">
                  <div>
                    <van-image
                      round
                      width="43px"
                      height="43px"
                      :src="item.patientAvatar"
                    />
                  </div>
                  <div style="margin-left: 9px">
                    <div style="color: #333333">{{item.patientName}}</div>
                    <div style="color:#999999;font-size: 13px">{{item.patientSex === 0 ? '男' : item.patientSex === 1 ? '女' : ''}}
                      {{ (item.patientSex === 0 || item.patientSex === 1) && item.patientAge > 0 ? '|' : ''}}
                      {{item.patientAge ? item.patientAge + '岁' : ''}}</div>
                  </div>
                </div>
              </div>
              <div v-if="item.referralStatus<2" :class="item.referralStatus === 0?'yellow-text':'blue-text'"  style="font-size: 15px">{{item.referralStatus === 0 ? '待接收' : item.referralStatus === 1 ? '已接收' : item.referralStatus === 2 ? '已取消' : '' }}</div>
              <div v-else><van-button @click="deleteReferralFun(item.id)" color="#686868" style="width: 65px" plain  round size="small">删除</van-button></div>
            </div>
            <div style="padding-top: 13px;display: flex;justify-content: space-between;align-items: center" @click="toPatientCenter(item.patientId)">
              <div>
                <div style="margin-bottom: 6px"><span class="left">发起时间: </span><span class="right">{{item.launchTime.substring(0, 16)}}</span></div>
                <div style="margin-bottom: 6px"><span class="left">接收时间: </span><span class="right">{{item.acceptTime ? item.acceptTime.substring(0, 16) : '-'}}</span></div>
                <div style="margin-bottom: 6px" ><span class="left">发起{{$getDictItem('doctor')}}: </span><span class="right">{{item.launchDoctorName}}</span></div>
                <div style="margin-bottom: 6px" ><span class="left">接收{{$getDictItem('doctor')}}: </span><span class="right">{{item.acceptDoctorName}}</span></div>
                <div><span class="left">转诊性质: </span><span class="right">{{item.referralCategory === 0 ? '单次转诊' : item.referralCategory === 1 ? '长期转诊' : '-' }}</span></div>
              </div>
              <div>
                <van-icon v-if="item.referralStatus<2" size="20px" name="arrow" />
                <img v-else src="../../assets/cancle.png" style="width: 78px" alt="">
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
      <div v-if="!loading && list.length === 0">
        <div style="width: 152px;margin: 129px auto 0 auto">
          <img src="../../assets/referralNoData.png" style="width: 152px" alt="">
          <div style="color: #999999;font-size: 15px;text-align: center;margin-top: 9px">暂无数据</div>
        </div>
      </div>
    </div>
<!--    删除二次确认弹窗-->
    <van-dialog @confirm="confirm" @cancel="showDeleteDialog=false" v-model="showDeleteDialog"  show-cancel-button>
        <div style="width: 55px;margin: 18px auto">
          <img src="../../assets/careful.png" style="width: 100%" alt="">
        </div>
      <div style="text-align: center;color: #666666;font-size: 15px;margin-bottom: 20px">
        确定删除该记录吗?
      </div>
    </van-dialog>
  </div>
</template>
<script>
import Vue from 'vue'
import { referralPage, deleteReferral } from '@/api/referralApi.js'
import {Sticky, Dialog, Icon, Col, Row, PullRefresh, List, Image as VanImage, Button} from 'vant'
Vue.use(Sticky)
Vue.use(Dialog)
Vue.use(Icon)
Vue.use(Col)
Vue.use(Row)
Vue.use(PullRefresh)
Vue.use(List)
Vue.use(VanImage)
Vue.use(Button)
export default {
  name: 'referral',
  data () {
    return {
      list: [],
      nursingId: localStorage.getItem('caringNursingId'),
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      deleteReferralId: undefined,
      showDeleteDialog: false // 删除二次确认弹窗控制
    }
  },
  created () {
    this.onRefresh()
  },
  methods: {
    back () {
      this.$h5Close()
    },
    /**
     * 删除二次确认点击确认方法
     */
    confirm () {
      deleteReferral(this.deleteReferralId).then(res => {
        if (res.code === 0) {
          const index = this.list.findIndex(item => item.id === this.deleteReferralId)
          this.list.splice(index, 1)
          this.showDeleteDialog = false
        }
      })
    },
    /**
     * 去患者中心
     */
    toPatientCenter (patientId) {
      this.$router.push({
        path: '/patient/center',
        query: {
          patientId: patientId,
          backUrl: '/referral'
        }
      })
    },
    deleteReferralFun (id) {
      this.deleteReferralId = id
      this.showDeleteDialog = true
    },
    onLoad () {
      const params = {
        'current': this.current,
        'model': {
          'serviceId': this.nursingId
        },
        'order': 'descending',
        'size': 10,
        'sort': 'id'
      }
      referralPage(params).then(res => {
        console.log(res.data)
        if (res.data) {
          this.list.push(...res.data.records)
          if (res.data.pages === 0 || res.data.pages === this.current) {
            this.finished = true
          } else {
            this.finished = false
            this.current++
          }
        }
        this.loading = false
        this.refreshing = false
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.list = []
      this.current = 1
      this.loading = true
      this.onLoad()
    }
  }

}
</script>

<style scoped lang="less">
.top-box {
  /*display: flex;*/
  /*justify-content: space-between;*/
  padding: 48px 13px 13px 13px;
  color: #333;
  font-size: 19px;
  line-height: 1;
  background: #fff;
  text-align: center;
}
.list{
  background: #ffffff;
  padding: 13px;
  margin-top: 13px;
  .left{
    color: #999999;
    font-size: 15px;
  }
  .right{
    color: #666666;
    font-size: 15px;
  }
}
.yellow-text{
  color: #FF9848;
}
.blue-text{
  color: #3F86FF;
}
</style>
