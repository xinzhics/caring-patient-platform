<template>
  <div :class="showoverlay?'zhezhao':''">
    <!--顶部组件-->
    <van-sticky>
      <headNavigation :style="{'pointer-events':showoverlay?'none':'auto'}" leftIcon="arrow-left" rightIcon="add-o" @showpop="addDoc" :title="pageTitle"
                      @onBack="onBack"></headNavigation>
    </van-sticky>
    <!--    下拉刷新-->
    <van-pull-refresh ref="doctorList" style="margin-top: 13px"
                      v-model="refreshing" @refresh="onRefresh">
      <van-list
          :style="{}"
          v-model="loading"
          offset="500"
          :finished="finished"
          @load="onLoad">
        <div slot="finished">
          <span v-if="groupList.length > 0 || doctorList.length > 0">没有更多了</span>
        </div>
        <!--    中间列表内容  >-->
        <div v-if="show">
          <div v-for="group in groupList" :key="group.id">
            <div class="list" @click="toGroupOrDoctorDetails('group', group.id)">
              <!-- 左边头像-->
              <div style="display: flex;justify-content: space-between;align-content: center">
                <img src="../../assets/g-head.png" style="width: 52px;height:52px;margin-right: 13px" alt=""/>
                <!-- 中间组名  会员数 医生数-->
                <div>
                  <div style="color: #333">{{ group.name }}</div>
                  <div style="color: #999999 ;font-size: 13px;margin-top: 7px">
                    <span>会员: {{ group.patientCount }}人</span>
                    <span style="margin-left: 30px">{{ $getDictItem('doctor') }}: {{ group.doctorCount }}人</span>
                  </div>
                </div>
              </div>
              <!--          右边箭头-->
              <div>
                <van-icon name="arrow"/>
              </div>
            </div>
          </div>
          <div v-for="doctor in doctorList" :key="doctor.id">
            <div class="list" @click="toGroupOrDoctorDetails('doctor', doctor.id)">
              <!-- 左边头像-->
              <div style="display: flex;justify-content: space-between;align-content: center">
                <img :src="doctor.avatar ? doctor.avatar : $getDoctorDefaultAvatar()"
                     style="width: 52px;height:52px;margin-right: 13px;border-radius: 50%" alt=""/>
                <!-- 中间组名  会员数 医生数-->
                <div>
                  <div style="color: #333">{{ doctor.name }}
                    <span v-if="officialAccountType === 'PERSONAL_SERVICE_NUMBER'"
                          style="margin-left: 5px" :style="doctor.doctorLeader === 1 ?
                          'border: 1px solid rgb(54 189 232);'+
                          'background: #37c2f7;'+
                          'color: white;' :
                          'border: 1px solid #b6b6b6;'">
                          组长
                        </span>
                  </div>
                  <div style="color: #999999 ;font-size: 13px;margin-top: 7px">
                    <span>{{ $getDictItem('patient') }}: {{
                        doctor.totalPatientCount ? doctor.totalPatientCount : 0
                      }}人</span>
                  </div>
                </div>
              </div>
              <div>
                <!--  右边箭头-->
                <van-icon name="arrow"/>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
    <!--    没有数据-->
    <div v-if="!show && !loading">
      <div style="width:143px ;margin:132px auto">
        <img src="../../assets/doc-bg.png" alt="" style="width:100%">
        <div style="color: #999999;font-size: 15px;text-align: center;margin-top: 21px">
          您暂时没有{{ $getDictItem('doctor') }}
        </div>
      </div>
    </div>
    <van-popup closeable :style="{ width: '80%' }" v-model="showPop">
      <div style="width: 100%;margin: 18px auto;text-align: center;color: #333;font-size: 17px;
        font-weight: 700;border-bottom: 1px solid #eee;padding-bottom: 17px">请选择
      </div>
      <div @click="adddoctor"
           style="font-size: 17px;color: #666;margin: 30px auto;width: 173px;height: 43px;line-height: 43px;text-align: center;border: 1px solid #B8B8B8;border-radius: 43px;">
        添加{{ $getDictItem('doctor') }}
      </div>
      <div
          @click="addGroup"
          style="font-size: 17px;color: #666;margin: 21px auto;width: 173px;height: 43px;line-height: 43px;text-align: center;border: 1px solid #B8B8B8;border-radius: 43px;">
        添加小组
      </div>
      <div
          style="width: 100%;margin: 0 auto;display: flex;justify-content: space-around;margin-top: 47px;line-height: 1">
        <div style="display: flex;align-items: center">
          <img src="../../assets/tips.png" style="width: 17px;" alt="">
          <div style="color: #FF6F00;font-size: 13px;margin-left: 8px">
            {{ '小组内' + $getDictItem('doctor') + ' 将共享' + $getDictItem('patient') }}
          </div>
        </div>
      </div>
    </van-popup>
    <!--不能创建医生-->
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
    <!--    引导遮罩-->
    <van-overlay :show="showoverlay" @click="showoverlay = false">
      <van-image
          width="99%"
          style="position: relative; "
          :style="{top: isWeiXin ? '2px' : '36px'}"
          fit="cover"
          :src="require('../../assets/my/doc-yindao.png')"
      />
    </van-overlay>
  </div>
</template>

<script>
import Vue from 'vue'
import {Cell, Col, Icon, Image as VanImage, List, Overlay, Popup, PullRefresh, Row} from 'vant'
import {groupAndDoctorList, createDoctorNumberCheck} from '@/api/doctorApi.js'
import { queryOfficialAccountType } from '@/api/tenant.js'
Vue.use(Popup)
Vue.use(VanImage)
Vue.use(Overlay)
Vue.use(Icon)
Vue.use(Col)
Vue.use(Row)
Vue.use(List)
Vue.use(Cell)
Vue.use(PullRefresh)
export default {
  data () {
    return {
      height: window.innerHeight,
      nursingId: localStorage.getItem('caringNursingId'),
      groupList: [],
      doctorList: [],
      pageTitle: '我的' + this.$getDictItem('doctor'),
      show: false,
      loading: false,
      finished: false,
      refreshing: false,
      showPop: false,
      vanPullRefreshHeight: window.innerHeight - 85.6,
      showoverlay: false,
      isWeiXin: false,
      showDialog: false,
      officialAccountType: 'CERTIFICATION_SERVICE_NUMBER'
    }
  },
  created () {
    const device = localStorage.getItem('caringCurrentDevice')
    if (device === 'weixin') {
      this.isWeiXin = true
    }
    this.onLoad()
  },
  mounted () {
    // 查询项目的服务号类型
    queryOfficialAccountType().then(res => {
      console.log('queryOfficialAccountType', res.data)
      this.officialAccountType = res.data
      if (this.officialAccountType === 'CERTIFICATION_SERVICE_NUMBER') {
        if (!localStorage.getItem('doc_yindao')) {
          const device = localStorage.getItem('caringCurrentDevice')
          if (device !== 'windows' && device !== 'macos') {
            this.showoverlay = true
            window.parent.postMessage({action: 'doctorYinDao'}, '*')
          }
          localStorage.setItem('doc_yindao', true)
        }
      } else {
        window.parent.postMessage({action: 'doctorYinDao'}, '*')
        localStorage.setItem('doc_yindao', true)
      }
    })
  },
  methods: {
    onBack () {
      this.$h5Close()
    },
    onLoad () {
      this.loading = true
      if (this.loading) {
        groupAndDoctorList(this.nursingId).then(res => {
          if (res.data.groupList && res.data.groupList.length > 0) {
            this.groupList = res.data.groupList
            console.log('groupList', this.groupList)
            this.show = true
          }
          if (res.data.doctorList && res.data.doctorList.length > 0) {
            this.doctorList = res.data.doctorList
            console.log('doctorList', this.doctorList)
            this.show = true
          }
          if (this.groupList.length === 0 && this.doctorList.length === 0) {
            this.vanPullRefreshHeight = 50
          } else {
            this.vanPullRefreshHeight = window.innerHeight - 85.6
          }
          this.finished = true
          this.loading = false
          this.refreshing = false
          console.log('加载完毕', this.loading)
        })
      }
    },
    onRefresh () {
      // 清空列表数据
      this.finished = false
      this.onLoad()
    },
    /**
     * 点击列表跳转到小组详情或者医生的详情
     * @param type
     * @param id
     */
    toGroupOrDoctorDetails (type, id) {
      if (type === 'group') {
        this.$router.push({
          path: '/mydoctor/groupDetails',
          query: {groupId: id}}
        )
      }
      if (type === 'doctor') {
        this.$router.push({
          path: '/mydoctor/doctorDetail',
          query: {doctorId: id}}
        )
      }
    },
    addDoc (val) {
      // 如果是个人服务号的， 直接去添加医生。
      if (this.officialAccountType === 'PERSONAL_SERVICE_NUMBER') {
        this.adddoctor()
      } else {
        this.showPop = val
      }
    },
    adddoctor () {
      createDoctorNumberCheck().then(res => {
        console.log(res)
        if (res.code === 0) {
          if (res.data === -1) {
            this.showPop = false
            setTimeout(() => {
              this.showDialog = true
            }, 200)
          } else {
            this.$router.push({
              path: '/mydoctor/addDoctor',
              query: {
                pathUrl: '/mydoctor'
              }
            })
          }
        }
      })
    },
    addGroup () {
      this.$router.push({
        path: '/mydoctor/editGroup',
        query: {}
      })
    }
  }
}
</script>

<style scoped lang="less">
.zhezhao{
  overflow: hidden;
  position:fixed;
  height: 100%;
  width: 100%;
  //pointer-events: none
}
.list {
  height: 37px;
  background: #fff;
  margin-bottom: 13px;
  padding: 17px 13px 17px 13px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/deep/ .van-popup {
  border-radius: 7px !important;
  padding: 0 12px 32px 14px;
}

</style>
