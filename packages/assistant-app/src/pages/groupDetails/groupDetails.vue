<template>
  <div>
    <van-sticky>
      <div class='top'>
          <van-row class="top-box-group-detail">
            <van-col style="text-align: left;" :style="{paddingTop: isWeiXin ? '2px' : '35px'}" span="8" @click="goback">
              <van-icon size="20" name="arrow-left" />
            </van-col>
            <van-col span="8" :style="{paddingTop: isWeiXin ? '2px' : '35px'}">
              <div class="top-title" style="color: #FFFFFF;">小组详情</div>
            </van-col>
            <van-col span='8' :style="{paddingTop: isWeiXin ? '2px' : '35px'}">
              <div style='font-size: 16px; text-align: right; line-height: 20px' @click="goEditGroup">
                <van-icon size="16"  :name='editImg'/>
              </div>
            </van-col>
          </van-row>
        <div
          style='margin-top: 35px;padding-left: 22px;align-items: center;padding-right: 22px;padding-bottom: 25px;display: flex;justify-content: space-between'>
          <div style='display: flex;justify-content: space-between;align-items: center;'>
            <img src='../../assets/g-head.png' style='width: 61px;height: 61px;' alt=''>
            <div style='margin-left:15px '>
              <div class="group-name" style='font-size: 19px'>{{group.name}}</div>
              <div style='font-size: 13px'>{{$getDictItem('doctor')}}: {{ group.doctorCount ? group.doctorCount : 0}}人</div>
            </div>
          </div>
          <div
            @click='addDoctor'
            style='border-radius: 15px;height: 30px;min-width: 95px;border: 1px solid #fff;line-height: 30px;text-align: center'>
            + 添加{{$getDictItem('doctor')}}
          </div>
        </div>
      </div>
    </van-sticky>
    <div
      style='margin-top: 19px;display: flex;justify-content: space-around;align-items: center;padding-left: 20px;padding-right: 20px'>
      <div style='height: 1px;width: 63px;background: #B8B8B8 '></div>
      <div style='font-size: 12px;color: #999999'>该小组下的{{$getDictItem('doctor')}}将共享{{$getDictItem('patient')}}</div>
      <div style='height: 1px;width: 63px;background: #B8B8B8 '></div>
    </div>
    <!--    没有医生的时候-->
    <div v-if='!show'>
      <div style='width:143px ;margin:177px auto'>
        <img src='../../assets/doc-bg.png' alt='' style='width:100%'>
        <div style='color: #999999;font-size: 15px;text-align: center;margin-top: 21px'>您暂时没有{{$getDictItem('doctor')}}</div>
      </div>
    </div>
    <!--    有医生的时候-->
    <van-pull-refresh v-if='show' :style="{height: vanPullRefreshHeight + 'px'}" v-model='refreshing' @refresh='onRefresh'>
      <van-list
        v-model='loading'
        :finished='finished'
        finished-text='没有更多了'
        @load='onLoad'>
        <!--    中间列表内容  >-->
        <div>
          <div style='background: #F5F5F5' @click="() => goDoctorDetails(doctor.id)" v-for='doctor in doctorList' :key='doctor.id'>
            <div class='list'>
              <!-- 左边头像-->
              <div style='display: flex;justify-content: space-between;align-content: center'>
                <img :src="doctor.avatar ? doctor.avatar : $getDoctorDefaultAvatar()" style='width: 52px;height:52px;margin-right: 13px;border-radius: 50%' alt=''/>
                <!-- 中间组名  会员数 医生数-->
                <div>
                  <div style='color: #333'>{{doctor.name}}</div>
                  <div style='color: #999999 ;font-size: 13px;margin-top: 7px'>
                   <span>{{$getDictItem('patient')}}: {{doctor.totalPatientCount ? doctor.totalPatientCount : 0}}人</span>
                  </div>
                </div>
              </div>
              <!--          右边箭头-->
              <div>
                <van-icon name='arrow'/>
              </div>
            </div>
          </div>
        </div>

      </van-list>
    </van-pull-refresh>
    <van-popup :style="{ width: '80%' }" v-model="showDialog">
      <div style="display: flex;justify-content: center;margin: 30px 0">
        <img width="55px" height="55px" src="../../assets/my/warning.png" alt="">
      </div>
      <div style="text-align: center;color: #666666">
        <div style="margin-bottom: 5px">当前{{ $getDictItem('doctor') }}数量已达到可添加上限</div>
        <div>如需继续添加,请联系管理员</div>
      </div>
      <div @click="showDialog = false"
           style="font-size: 17px;color: #fff;margin: 30px auto 0 auto;width: 173px;height: 43px;line-height: 43px;text-align: center;border-radius: 43px;background-color: #3F86FF">
        我知道了
      </div>
    </van-popup>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, List, Cell, PullRefresh, Sticky, Popup} from 'vant'
import { groupDetail } from '@/api/group.js'
import { doctorListPage, createDoctorNumberCheck } from '@/api/doctorApi.js'
Vue.use(Sticky)
Vue.use(Icon)
Vue.use(List)
Vue.use(Cell)
Vue.use(PullRefresh)
Vue.use(Col)
Vue.use(Row)
Vue.use(Popup)
export default {
  data () {
    return {
      editImg: require('@/assets/group_edit.png'),
      group: {},
      doctorList: [],
      show: true,
      groupId: this.$route.query.groupId,
      nursingId: localStorage.getItem('caringNursingId'),
      current: 1,
      showDialog: false,
      loading: false,
      finished: false,
      refreshing: false,
      isWeiXin: false, // 当是微信端的时候， 控制标题的高度
      vanPullRefreshHeight: window.innerHeight - 207 - 16
    }
  },
  created () {
    const device = localStorage.getItem('caringCurrentDevice')
    if (device === 'weixin') {
      this.isWeiXin = true
    }
    this.getGroupDetail()
  },
  methods: {
    // 跳转到编辑小组
    goEditGroup () {
      this.$router.push({
        path: '/mydoctor/editGroup',
        query: {
          groupId: this.groupId
        }
      })
    },
    // 跳转到医生页
    goDoctorDetails (doctorId) {
      this.$router.push({
        path: '/mydoctor/doctorDetail',
        query: {
          groupId: this.groupId,
          doctorId: doctorId
        }
      })
    },
    onLoad () {
      const params = {
        'current': this.current,
        'map': {},
        'model': {
          'groupId': this.groupId,
          'nursingId': this.nursingId
        },
        'order': 'descending',
        'size': 10,
        'sort': 'id'
      }
      doctorListPage(params).then(res => {
        console.log(res.data.records.length < 1)
        if (res.data.records.length < 1) {
          this.show = false
          this.finished = true
        } else {
          if (res.data) {
            this.doctorList.push(...res.data.records)
            if (res.data.pages === 0 || res.data.pages === this.current) {
              this.finished = true
            } else {
              this.finished = false
              this.current++
            }
          }
        }
        this.loading = false
        this.refreshing = false
      })
    },
    getGroupDetail () {
      groupDetail(this.groupId).then(res => {
        this.group = res.data
      })
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.doctorList = []
      this.current = 1
      this.loading = true
      this.onLoad()
    },
    goback () {
      this.$router.replace({
        path: '/mydoctor'
      })
    },
    addDoctor () {
      createDoctorNumberCheck().then(res => {
        console.log(res)
        if (res.code === 0) {
          if (res.data === -1) {
            setTimeout(() => {
              this.showDialog = true
            }, 200)
          } else {
            this.$router.push({
              path: '/mydoctor/addDoctor',
              query: {
                groupId: this.groupId,
                pathUrl: '/mydoctor/groupDetails'
              }
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.top {
  background: url('../../assets/g-bj.png');
  background-repeat: no-repeat;
  background-size: cover;
  color: #ffffff;
}

.top-box-group-detail {
  display: flex;
  justify-content: space-between;
  padding: 13px 13px 13px 13px;
  font-size: 19px;
  line-height: 1;
  text-align: center;
}

.list {
  height: 37px;
  background: #fff;
  padding: 17px 13px 17px 13px;
  margin-top: 13px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.group-name{
  width: 100px;
  overflow: hidden;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
/deep/ .van-popup {
  border-radius: 7px !important;
  padding: 0 12px 32px 14px;
}
</style>
