<template>
  <div class="content" style="overflow-x: hidden">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" rightText="备注" :title="patient + '详情'" @onBack="back"></headNavigation>
    </van-sticky>
    <div class="header" ref="header">
      <div class="headerImg" @click="toEditBaseInfo">
        <img v-if="patientInfo.avatar!==''" :src="patientInfo.avatar">
        <img v-else src="../../assets/avatar/head-portrait.png" alt="">
        <van-icon v-if="patientBaseInfoMenu.patientMenuNursingStatus" name="edit"
                  style="position: absolute; right: 0px; font-size: 22px; bottom: 0px"/>
      </div>
      <div class="detail">
        <div class="detail-name">
          <div style="display: flex; align-items: center">
            <div>{{ patientInfo.name }}</div>
            <div style="font-size: 12px; color: #999; font-weight: normal" v-if="patientInfo.remark">
              ({{ patientInfo.remark }})
            </div>
            <p class="sexAge">{{ patientInfo.sex === 0 ? '男性' : patientInfo.sex === 1 ? '女性' : '' }}
              {{ (patientInfo.sex === 0 || patientInfo.sex === 1) && jsGetAge(patientInfo.birthday) ? ' ' : '' }}
              {{ jsGetAge(patientInfo.birthday) ? jsGetAge(patientInfo.birthday) + '岁' : '' }}</p>
          </div>

          <div style="font-size: 14px; color: #3BD26A; font-weight: normal; display: flex; align-items: center"
               @click="isShow = true; remark = ''">
            <van-image :src="require('@/assets/my/patient_remark_icon.png')" style="width: 15px; height: 15px;"/>
            <span>备注</span>
          </div>
        </div>

        <div style="font-size: 14px; color: #999;">
          <span>分组：{{ groupName ? groupName : '-' }}</span>
        </div>
      </div>
    </div>

    <div style="display: flex; margin-top: 10px; background: #FFF; position: relative">
      <van-tabs v-model="active" style="padding-right: 30px; width: 100vh;"
                title-active-color="var(--caring-tab-action-color)"
                color="var(--caring-tab-action-color)" @click="tabChage()">
        <van-tab v-for="(item, index) in patientMyFile"
                 :title="item.name" :key="index"
                 :title-style="{marginRight: index === patientMyFile.length - 1 ? '40px' : '0px'}"
        />
      </van-tabs>
      <div
        style="position: absolute; right: 0px; ;height: 44px; display: flex; padding: 0px 5px; align-items: center; justify-content: center; background: #FFF">
        <van-image :src="require('@/assets/my/im_recommend_arrow.png')"
                   style="width: 15px; height: 12px; transform: rotate(180deg); "/>
      </div>
    </div>

    <div v-if="patientMyFile && patientMyFile.length > 0"
         :style="{height: (parseInt(tabsHeight) - 44) + 'px'}">
      <!--   监测计划     -->
      <test-number-list v-if="patientMyFile[active].dictItemType === 'MONITOR'"/>
      <!--   检验数据     -->
      <inspection-data-list v-else-if="patientMyFile[active].dictItemType === 'TEST_NUMBER'" style="height: 100%;"
                            @recommendation="recommendation"/>
      <!--   用药日历     -->
      <medicine-calendar v-else-if="patientMyFile[active].dictItemType === 'CALENDAR'"
                         @recommendation="recommendation"/>
      <!--   健康日志     -->
      <health-log-list v-else-if="patientMyFile[active].dictItemType === 'HEALTH_CALENDAR'"
                       @recommendation="recommendation"/>
      <!--   自定义随访   -->
      <custom-follow-up-list
        v-else-if="patientMyFile[active].dictItemType === 'CUSTOM_FOLLOW_UP' && !patientMyFile[active].link"
        :customData="patientMyFile[active]" ref="customFollowUpList" @recommendation="recommendation"/>
      <!--   疾病信息     -->
      <disease-info-list v-else-if="patientMyFile[active].dictItemType === 'HEALTH'"
                         :pageTitle="patientMyFile[active].name" @recommendation="recommendation"/>
      <!--   基本信息     -->
      <base-info v-else-if="patientMyFile[active].dictItemType === 'BASE_INFO'" :patientInfo="patientInfo"
                 @baseInfoSubmit="baseInfoSubmit"/>
      <!--   我的药箱     -->
      <medicine v-else-if="patientMyFile[active].dictItemType === 'MEDICINE'" @recommendation="recommendation"/>
    </div>

    <van-dialog v-model="isShow" :showConfirmButton="false" :showCancelButton="false" @confirm="setDoctorRemark">
      <div slot="title" style="display: flex; justify-content: center; position: relative; align-items: center">
        <div style="font-weight: bold">修改{{ patient }}备注</div>
        <van-icon name="cross" style="position: absolute; right: 20px" @click="isShow = false"/>
      </div>
      <div
        style="display: flex; justify-content: center; align-items: center; padding-top: 25px; padding-bottom: 25px;">
        <div style="display: flex; align-items: center; width: 80%; background: #f5f5f5; border-radius: 10px;">
          <van-image :src="require('@/assets/my/patient_remark_input_icon.png')"
                     style="width: 30px; height: 22px; margin-left: 10px;"/>
          <van-field v-model="remark" :placeholder="'请输入' + patient + '备注'"
                     style="background: #f5f5f5; border-radius: 10px;"
                     maxlength="15"/>
          <div style="font-size: 14px; color: #999; width: 40px; margin-right: 10px">{{ remark.length }}/15</div>
        </div>
      </div>

      <div style="display: flex; justify-content: center; align-items: center; margin-bottom: 20px;">
        <van-button plain hairline round type="primary" size="small" style="background: #FFF;
         border: 1px solid #67E0A7; color: #67E0A7; width: 110px;" @click="isShow = false">取消
        </van-button>
        <van-button type="primary" round size="small" @click="setDoctorRemark"
                    style="background: #67E0A7; width: 110px; border: 0px; margin-left: 30px;">确定
        </van-button>
      </div>
    </van-dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import {Icon, Tab, Tabs, Dialog, Field, Toast, Image} from 'vant'
import {
  patientDetail,
  getPatientNewHomeRouter,
  updatePatient,
  sendMessage,
  imRecommendationHeat,
  getDoctorSeePatientGroup
} from '@/api/patient.js'
import {planDetail} from '@/api/plan.js'
import {jsGetAge} from '@/utils/age.js'

Vue.use(Icon)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Field)
Vue.use(Image)
Vue.use(Dialog)
Vue.use(Toast)
export default {
  components: {
    medicineCalendar: () => import('@/components/patient/calendar/index'), // 用药日历
    inspectionDataList: () => import('@/components/patient/inspectionData/list'), // 检验数据
    testNumberList: () => import('@/components/patient/monitorNumber/list'), // 监测数据
    healthLogList: () => import('@/components/patient/healthLog/list'), // 健康日志
    customFollowUpList: () => import('@/components/patient/customFollowUp/list'), // 自定义随访
    diseaseInfoList: () => import('@/components/patient/diseaseInfo/list'), // 疾病信息
    baseInfo: () => import('@/components/patient/baseInfo/index'), // 疾病信息
    medicine: () => import('@/components/patient/medicine/index') // 我的药箱
  },
  data () {
    return {
      patientId: '',
      patientInfo: {},
      patientMyFile: [],
      active: 0,
      tabsHeight: '0px',
      isShow: false,
      remark: '',
      groupName: '',
      patientBaseInfoMenu: {},
      patient: this.$getDictItem('patient')
    }
  },
  created () {
    const patientId = this.$route.query.patientId
    if (this.$route.query.backUrl) {
      localStorage.setItem('backUrl', this.$route.query.backUrl)
    } else {
      this.backUrl = localStorage.getItem('backUrl')
    }
    if (patientId) {
      this.patientId = patientId
      if (patientId !== localStorage.getItem('patientId')) {
        localStorage.removeItem('patientActive')
      }
      localStorage.setItem('patientId', patientId)
    }
    this.getPatientInfo()
    this.getPatientRouter()
  },
  mounted () {
    // 屏幕高度减去 头部 在减去标题
    this.tabsHeight = (window.innerHeight - this.$refs.header.offsetHeight - 56) + 'px'
  },
  methods: {
    getDoctorSeePatientGroup (doctorId, patientId) {
      getDoctorSeePatientGroup(doctorId, patientId)
        .then(res => {
          this.groupName = res.data.join(', ')
        })
    },
    jsGetAge (datetime) {
      return jsGetAge(datetime)
    },
    /**
     * 页面上的返回
     */
    back () {
      if (this.backUrl) {
        this.$router.replace({
          path: this.backUrl
        })
      } else {
        // 返回到app去
        this.$h5Close()
      }
    },
    // 患者提交信息后
    baseInfoSubmit () {
      this.getPatientInfo()
    },
    recommendation () {
      if (this.patientInfo.nursingExitChat === 1) {
        Toast('已退出聊天，不可推送功能')
      } else {
        let item = this.patientMyFile[this.active]
        this.sendRecommendIm(item)
      }
    },
    tabChage () {
      // 修改患者标题
      if (this.patientMyFile[this.active].dictItemType === 'CUSTOM_FOLLOW_UP' && this.patientMyFile[this.active].link) {
        window.location.href = this.patientMyFile[this.active].link
      } else {
        if (this.patientMyFile[this.active].dictItemType === 'CUSTOM_FOLLOW_UP') {
          this.$nextTick(() => {
            if (this.$refs.customFollowUpList) {
              this.$refs.customFollowUpList.init(true)
            } else {
              setTimeout(() => {
                this.$refs.customFollowUpList.init(true)
              }, 500)
            }
          })
        }
        localStorage.setItem('patientActive', this.active)
        localStorage.setItem('pageTitle', this.patientMyFile[this.active].name)
      }
    },
    setDoctorRemark () {
      if (this.remark === '') {
        Toast('请输入会员备注')
        return
      }
      const params = {
        id: this.patientId,
        remark: this.remark
      }
      updatePatient(params).then(res => {
        this.isShow = false
        this.patientInfo.remark = this.remark
      })
    },
    getPatientRouter () {
      getPatientNewHomeRouter().then(res => {
        if (res.code === 0) {
          const dataMenu = res.data
          if (dataMenu.patientBaseInfo) {
            this.patientBaseInfoMenu = dataMenu.patientBaseInfo
          }
          dataMenu.patientMyFile.forEach(item => {
            if (item.dictItemType !== 'OTHER' && item.dictItemType !== 'REMIND' && item.patientMenuNursingStatus) {
              this.patientMyFile.push(item)
            }
          })
          this.patientMyFile.forEach((item, index) => {
            if (item.dictItemType === 'CUSTOM_FOLLOW_UP') {
              this.getCustomePlan(item, index)
            }
          })
          if (this.$route.query && this.$route.query.dictItemType) {
            this.patientMyFile.forEach((item, index) => {
              if (item.dictItemType === this.$route.query.dictItemType) {
                this.active = index
              }
            })
          } else if (localStorage.getItem('patientActive')) {
            // 如果患者有跳转标签， 然后跳转到该标签
            this.active = parseInt(localStorage.getItem('patientActive'))
            if (this.active >= this.patientMyFile.length) {
              this.active = 0
            }
          }
        }
        // 获取完数据之后走一下tabChage, 防止注射计划在第一个
        // this.tabChage()
      })
    },
    /**
     * 去基本信息的编辑页面
     */
    toEditBaseInfo () {
      if (this.patientBaseInfoMenu.patientMenuNursingStatus) {
        localStorage.setItem('pageTitle', this.patientBaseInfoMenu.name)
        const path = '/patient/' + this.patientBaseInfoMenu.path
        this.$router.push({
          path: path
        })
      }
    },
    getCustomePlan (item, index) {
      let planId = item.path.substring(item.path.lastIndexOf('/') + 1)
      if (planId === 'calendar') {
        // 注射日历
        planId = item.path.substring(0, item.path.lastIndexOf('/'))
        planId = planId.substring(planId.lastIndexOf('/') + 1)
      }
      planDetail(planId).then(res => {
        if (res.code === 0 && res.data) {
          if (res.data.pushType === 2 && res.data.content) {
            // 自定义护理计划有链接
            this.patientMyFile[index].link = res.data.content
          }
        }
      })
    },
    // 获取患者基本信息
    getPatientInfo () {
      patientDetail(localStorage.getItem('patientId')).then((res) => {
        if (res.code === 0) {
          this.patientInfo = res.data
          if (this.patientInfo.doctorId) {
            this.getDoctorSeePatientGroup(this.patientInfo.doctorId, localStorage.getItem('patientId'))
          }
          localStorage.setItem('patientBaseInfo', JSON.stringify(this.patientInfo))
          localStorage.setItem('diseaseInformationStatus', res.data.diseaseInformationStatus)
        }
      })
    },

    // 推荐功能发送消息
    sendRecommendIm (item) {
      let that = this
      let message = '为了让医生更好的了解您的身体情况，请补充填写' + item.name + ',点击此对话即可填写'
      let recommendationFunctionParams = ''
      Toast.loading({
        message: '正在发送...',
        forbidClick: true
      })
      if (item.dictItemType === 'CUSTOM_FOLLOW_UP') {
        // 自定义随访
        let planId = item.path.substring(item.path.lastIndexOf('/') + 1)
        if (planId === 'calendar') {
          // 注射日历
          planId = item.path.substring(0, item.path.lastIndexOf('/'))
          planId = planId.substring(planId.lastIndexOf('/') + 1)
        }
        planDetail(planId).then(res => {
          if (res.code === 0 && res.data) {
            const planDetail = res.data
            if (planDetail.pushType === 2 && planDetail.content) {
              recommendationFunctionParams = JSON.stringify({
                dictItemId: item.dictItemId,
                path: planDetail.content,
                name: item.name,
                isLink: true
              })
            } else {
              recommendationFunctionParams = JSON.stringify({
                dictItemId: item.dictItemId,
                path: item.path,
                name: item.name
              })
            }
            const params = {
              receiverId: that.patientInfo.id,
              senderId: localStorage.getItem('caringNursingId'),
              type: 'text',
              content: message,
              chatAtRecords: [],
              recommendationFunction: item.dictItemType,
              recommendationFunctionParams: recommendationFunctionParams
            }
            sendMessage(params)
              .then(res => {
                Toast.clear()
                Toast({duration: 2000, message: '推荐成功'})
                // 增加推荐功能使用的热度
                this.imRecommendationHeat({
                  functionId: item.id,
                  functionType: item.dictItemType,
                  userId: localStorage.getItem('caringNursingId')
                })
              })
          }
        })
        return
      }
      if (item.dictItemType === 'OTHER') {
        // 链接
        recommendationFunctionParams = JSON.stringify({
          dictItemId: item.dictItemId,
          path: item.path,
          name: item.name,
          isLink: true
        })
      } else if (item.dictItemType === 'MONITOR') {
        // 监测计划中，有dictItemId，则为 血压 血糖
        if (item.dictItemId) {
          recommendationFunctionParams = JSON.stringify({dictItemId: item.dictItemId, path: item.path, name: item.name})
        } else {
          // 自定义监测计划
          recommendationFunctionParams = JSON.stringify({id: item.id, name: item.name})
        }
      } else {
        recommendationFunctionParams = JSON.stringify({dictItemId: item.dictItemId, path: item.path, name: item.name})
      }

      const params = {
        receiverId: this.patientInfo.id,
        senderId: localStorage.getItem('caringNursingId'),
        type: 'text',
        content: message,
        chatAtRecords: [],
        recommendationFunction: item.dictItemType,
        recommendationFunctionParams: recommendationFunctionParams
      }
      sendMessage(params)
        .then(res => {
          Toast.clear()
          Toast('发送成功')
          // 增加推荐功能使用的热度
          this.imRecommendationHeat({
            functionId: item.id,
            functionType: item.dictItemType,
            userId: localStorage.getItem('caringNursingId')
          })
        })
    },

    imRecommendationHeat (data) {
      let msg = {action: 'sendRecommendationFunction'}
      window.parent.postMessage(msg, '*')
      imRecommendationHeat(data)
    }
  }
}

</script>

<style scoped lang="less">

.content {
  width: 100vw;
  height: 100vh;
  background: #f5f5f5;

  .header {
    display: flex;
    align-content: center;
    background: #FFF;
    padding: 10px;

    .detail {
      display: flex;
      flex-direction: column;
      justify-content: center;
      flex: 1;

      .detail-name {
        display: flex;
        align-items: center;
        justify-content: space-between;
        color: #333;
        font-weight: bold;

        .sexAge {
          font-size: 12px;
          margin-left: 10px;
          color: #999;
          font-weight: normal;
        }
      }
    }

    .headerImg {
      position: relative;
      width: 3.5rem;
      height: 3.5rem;
      //margin: 2.5rem auto 0.3rem;
      overflow: hidden;
      margin-right: 13px;
      //border: 1px solid rgba(255, 255, 255, 0.2);

      img {
        width: 94%;
        height: 94%;
        border-radius: 50%;
        background: #fff;
        margin: 3%;
      }
    }
  }
}
</style>
