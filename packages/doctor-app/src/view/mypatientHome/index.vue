<template>
  <div class="content" style="overflow-x: hidden">
    <navBar :pageTitle="patient + '详情'"></navBar>
    <div class="header" ref="header">
      <div class="headerImg" @click="toEditBaseInfo">
        <img v-if="patientInfo.avatar!==''" :src="patientInfo.avatar">
        <img v-else src="../../components/arrt/images/head-portrait.png" alt="">
        <van-icon name="edit"
                  style="position: absolute; right: 0px; font-size: 22px; bottom: 0px"/>
      </div>
      <div class="detail">
        <div class="detail-name">
          <div style="display: flex; align-items: center">
            <div>{{ patientInfo.name }}</div>
            <div style="font-size: 12px; color: #999; font-weight: normal" v-if="patientInfo.doctorRemark">
              ({{ patientInfo.doctorRemark }})
            </div>
            <p class="sexAge">{{ patientInfo.sex == 0 ? '男性' : patientInfo.sex == 1 ? '女性' : '' }}
              {{ (patientInfo.sex == 0 || patientInfo.sex == 1) && jsGetAge(patientInfo.birthday) ? ' ' : '' }}
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
      <test-number-list v-if="patientMyFile[active].dictItemType === 'MONITOR'" :patientId="patientId"/>
      <!--   检验数据     -->
      <inspection-data-list v-else-if="patientMyFile[active].dictItemType === 'TEST_NUMBER'" :patientId="patientId"
                            style="height: 100%;" @recommendation="recommendation"/>
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
      <!--   智能提醒     -->
      <remind v-else-if="patientMyFile[active].dictItemType === 'REMIND'"/>
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
import Api from '@/api/doctor.js'
import ContentApi from '@/api/Content.js'
import Vue from 'vue';
import {Icon, Tab, Tabs, Dialog, Field, Toast} from 'vant';

Vue.use(Icon);
Vue.use(Tab);
Vue.use(Tabs);
Vue.use(Field);
Vue.use(Toast);
export default {
  components: {
    medicineCalendar: () => import('@/components/patient/calendar/index'), // 用药日历
    inspectionDataList: () => import('@/components/patient/inspectionData/list'), // 检验数据
    testNumberList: () => import('@/components/patient/monitorNumber/list'), // 监测数据
    healthLogList: () => import('@/components/patient/healthLog/list'), // 健康日志
    customFollowUpList: () => import('@/components/patient/customFollowUp/list'), // 自定义随访
    diseaseInfoList: () => import('@/components/patient/diseaseInfo/list'), // 疾病信息
    baseInfo: () => import('@/components/patient/baseInfo/index'), // 疾病信息
    medicine: () => import('@/components/patient/medicine/index'), // 我的药箱
    remind: () => import('@/components/patient/remind/index'), // 我的药箱
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
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
      patient: this.$getDictItem('patient'),
    }
  },
  created() {
    if (this.$route.query && this.$route.query.id) {
      this.patientId = this.$route.query.id
      // 如果患者id不同, 则清空标签
      if (this.patientId !== localStorage.getItem('patientId')) {
        localStorage.removeItem('patientActive')
      }

      localStorage.setItem('patientId', this.$route.query.id)
      this.getPatientInfo()
      this.getPatientRouter()
      this.getDoctorSeePatientGroup()
    }
  },
  mounted() {
    // 屏幕高度减去 头部 在减去标题
    this.tabsHeight = (window.innerHeight - this.$refs.header.offsetHeight - 56) + 'px';
  },
  methods: {
    // 患者提交信息后
    baseInfoSubmit() {
      this.getPatientInfo()
    },
    recommendation() {
      let item = this.patientMyFile[this.active]
      this.sendRecommendIm(item)
    },
    getDoctorSeePatientGroup() {
      ContentApi.getDoctorSeePatientGroup()
        .then(res => {
          this.groupName = res.data.data.join(', ');
        })
    },
    tabChage() {
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
    setDoctorRemark() {
      if (this.remark == '') {
        this.$vux.toast.text('请输入会员备注', 'center')
        return
      }
      const params = {
        doctorRemark: this.remark,
        id: this.patientId
      }
      Api.setPatientInfo(params).then(res => {
        this.isShow = false
        this.patientInfo.doctorRemark = this.remark
        /*this.$data.message = ''*/
      })
    },
    getPatientRouter() {
      Api.getPatientNewHomeRouter().then(res => {
        if (res.data.code === 0) {
          const dataMenu = res.data.data

          if (dataMenu.patientBaseInfo) {
            this.patientBaseInfoMenu = dataMenu.patientBaseInfo
          }
          dataMenu.patientMyFile.forEach(item => {
            if (item.dictItemType !== 'OTHER' && item.dictItemType !== 'REMIND' && item.patientMenuDoctorStatus) {
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
        //this.tabChage()
      })
    },
    /**
     * 去基本信息的编辑页面
     */
    toEditBaseInfo() {
      console.log('============', this.patientBaseInfoMenu)
      // localStorage.setItem("pageTitle", this.patientBaseInfoMenu.name);
      this.$router.push({
        path: '/baseinfo/index'
      })
    },
    getCustomePlan(item, index) {
      let planId
      if (item.path.indexOf('calendar') > -1) {
        const str = item.path
        const regex = /^(.*\/)([^\/]+)(\/.*)$/;
        const match = str.match(regex);
        if (match) {
          planId = match[2]
        }
      } else {
        planId = item.path.substring(item.path.lastIndexOf("/") + 1)
      }
      Api.planDetail(planId).then(res => {
        if (res.data.code === 0 && res.data.data) {
          if (res.data.data.pushType === 2 && res.data.data.content) {
            // 自定义护理计划有链接
            this.patientMyFile[index].link = res.data.data.content
          }
        }
      })
    },
    // 获取患者基本信息
    getPatientInfo() {
      const params = {
        id: this.patientId
      }
      Api.getContentPatient(params).then(res => {
        if (res.data.code === 0) {
          this.patientInfo = res.data.data
          //this.patientInfo.doctorId === localStorage.getItem('caring_doctor_id') ? this.isMyDoctor = true : this.isMyDoctor = false
          localStorage.setItem('patientBaseInfo', JSON.stringify(this.patientInfo))
          localStorage.setItem('diseaseInformationStatus', res.data.data.diseaseInformationStatus)
        }
      })
    },
    /*根据出生日期算出年龄*/
    jsGetAge(strBirthday) {
      var returnAge = ''
      var strBirthdayArr = ''
      if (strBirthday) {
        if (strBirthday.indexOf('-') != -1) {
          strBirthdayArr = strBirthday.split('-')
        } else if (strBirthday.indexOf('/') != -1) {
          strBirthdayArr = strBirthday.split('/')
        }
        var birthYear = strBirthdayArr[0]
        var birthMonth = strBirthdayArr[1]
        var birthDay = strBirthdayArr[2]

        var d = new Date()
        var nowYear = d.getFullYear()
        var nowMonth = d.getMonth() + 1
        var nowDay = d.getDate()

        if (nowYear == birthYear) {
          returnAge = 0 //同年 则为0岁
        } else {
          var ageDiff = nowYear - birthYear //年之差
          if (ageDiff > 0) {
            if (nowMonth == birthMonth) {
              var dayDiff = nowDay - birthDay //日之差
              if (dayDiff < 0) {
                returnAge = ageDiff - 1
              } else {
                returnAge = ageDiff
              }
            } else {
              var monthDiff = nowMonth - birthMonth //月之差
              if (monthDiff < 0) {
                returnAge = ageDiff - 1
              } else {
                returnAge = ageDiff
              }
            }
          } else {
            returnAge = -1 //返回-1 表示出生日期输入错误 晚于今天
          }
        }
      } else {
        returnAge = ''
      }
      //如果结果为 -1 或者为 0， 则不显示年龄
      if (returnAge === -1 || returnAge === 0) {
        returnAge = ''
      }
      return returnAge //返回周岁年龄
    },

    // 推荐功能发送消息
    sendRecommendIm(item) {
      let message = '为了让医生更好的了解您的身体情况，请补充填写' + item.name + ',点击此对话即可填写'
      let recommendationFunctionParams = ''
      Toast.loading({
        message: '正在发送...',
        forbidClick: true,
      });
      if (item.dictItemType === 'CUSTOM_FOLLOW_UP') {
        // 自定义随访
        let planId;
        if (item.path.indexOf('calendar') > -1) {
          const str = item.path
          const regex = /^(.*\/)([^\/]+)(\/.*)$/;
          const match = str.match(regex);
          if (match) {
            planId = match[2]
          }
        } else {
          planId = item.path.substring(item.path.lastIndexOf("/") + 1)
        }
        Api.planDetail(planId).then(res => {
          if (res.data.code === 0 && res.data.data) {
            const planDetail = res.data.data
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
              receiverId: this.patientInfo.id,
              senderId: localStorage.getItem('caring_doctor_id'),
              type: 'text',
              content: message,
              chatAtRecords: [],
              recommendationFunction: item.dictItemType,
              recommendationFunctionParams: recommendationFunctionParams
            }
            ContentApi.sendMessage(params)
              .then(res => {
                Toast.clear();
                Toast({duration: 2000, message: '推荐成功'});
                // 增加推荐功能使用的热度
                this.imRecommendationHeat({
                  functionId: item.id,
                  functionType: item.dictItemType,
                  userId: localStorage.getItem('caring_doctor_id')
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
        senderId: localStorage.getItem('caring_doctor_id'),
        type: 'text',
        content: message,
        chatAtRecords: [],
        recommendationFunction: item.dictItemType,
        recommendationFunctionParams: recommendationFunctionParams
      }
      ContentApi.sendMessage(params)
        .then(res => {
          Toast.clear();
          Toast('发送成功');
          // 增加推荐功能使用的热度
          this.imRecommendationHeat({
            functionId: item.id,
            functionType: item.dictItemType,
            userId: localStorage.getItem('caring_doctor_id')
          })

        })
    },

    imRecommendationHeat(data) {
      Api.imRecommendationHeat(data)
    },
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
