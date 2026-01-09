<template>
  <div style="padding-bottom: 30px">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :showRightText="true" rightText="确定" @showpop="determine" title="选择联系人" @onBack="goback"></headNavigation>
    </van-sticky>

<!--    所有患者0-1-->
    <div style="border-top: 1px solid #ddd">
      <div style="display: flex;justify-content: space-between;padding: 14px 13px 14px 13px;background: #fff;">
        <van-checkbox style="width: 80%" @click="allCheckedChoose" v-model="allChecked">所有{{$getDictItem('patient')}}({{allPatientList.length}})</van-checkbox>
        <div>
          <van-icon :name="allPatientShow?'arrow-up':'arrow-down'" @click="allPatientShow = !allPatientShow" />
        </div>
      </div>
      <div v-if="allPatientShow" style="padding-left: 20px;background: #fff">
        <van-checkbox style="border-bottom: 1px solid #F5F6F7;padding: 7px 0 6px 0"
                      v-model="patient.checked" v-for="(patient, index) in allPatientList" :key="'allPatient' + index + '_' + patient.id">
          <div style="display: flex;align-items: center">
            <span><img :src="patient.avatar" style="width: 45px; border-radius: 50%" alt=""></span>
            <span style="margin-left: 10px">{{ $formatPatientName(patient.name, patient.remark) }}</span>
          </div>
        </van-checkbox>
      </div>
    </div>
<!--    患者状态2-3-->
    <div style="padding: 11px 0 11px 15px;background: #F5F5F5;border-top: 1px solid #ddd;font-size: 13px">按{{$getDictItem('patient')}}状态</div>
    <div style="border-top: 1px solid #ddd">
      <div style="display: flex;justify-content: space-between;padding: 14px 13px 14px 13px;background: #fff;">
        <van-checkbox style="width: 80%" @click="saoMaCheckedChoose" v-model="saoMaPatientChecked">扫码{{$getDictItem('patient')}}({{saoMaPatientList.length}})</van-checkbox>
        <div>
          <van-icon :name="saoMaPatientShow?'arrow-up':'arrow-down'" @click="saoMaPatientShow = !saoMaPatientShow" />
        </div>
      </div>
      <div v-if="saoMaPatientShow" style="padding-left: 20px;background: #fff">
        <van-checkbox v-for="(patient, index) in saoMaPatientList" :key="'saoMa_' + index + '_' + patient.id" style="border-bottom: 1px solid #F5F6F7;padding: 7px 0 6px 0" v-model="patient.checked" >
          <div style="display: flex;align-items: center">
            <span><img :src="patient.avatar" style="width: 45px; border-radius: 50%" alt=""></span>
            <span style="margin-left: 10px">{{patient.name}}</span>
            <span v-if="patient.remark">({{patient.remark}})</span>
          </div>
        </van-checkbox>
      </div>
    </div>
    <div style="border-top: 1px solid #ddd">
      <div style="display: flex;justify-content: space-between;padding: 14px 13px 14px 13px;background: #fff;">
        <van-checkbox style="width: 80%" @click="zhucePatientChoose" v-model="zhuCePatientChecked">
          {{$getDictItem('register')}}{{$getDictItem('patient')}}({{zhuCePatientList.length}})</van-checkbox>
        <div>
          <van-icon  :name="zhuCePatientShow?'arrow-up':'arrow-down'" @click="zhuCePatientShow = !zhuCePatientShow" />
        </div>
      </div>
      <div v-if="zhuCePatientShow" style="padding-left: 20px;background: #fff">
        <van-checkbox v-for="(patient, index) in zhuCePatientList" :key="'zhuCe_' + index + '_' + patient.id" style="border-bottom: 1px solid #F5F6F7;padding: 7px 0 6px 0" v-model="patient.checked" >
          <div style="display: flex;align-items: center">
            <span><img :src="patient.avatar" style="width: 45px; border-radius: 50%" alt=""></span>
            <span style="margin-left: 10px">{{patient.name}}</span>
            <span v-if="patient.remark">({{patient.remark}})</span>
          </div>
        </van-checkbox>
      </div>
    </div>
<!--    诊断类型4-5-->
    <div style="padding: 11px 0 11px 15px;background: #F5F5F5;border-top: 1px solid #ddd;font-size: 13px">按{{$getDictItem('diagnostictype')}}</div>
    <div style="border-top: 1px solid #ddd" v-for="diagnosis in diagnosisList" :key="diagnosis.id">
      <div style="display: flex;justify-content: space-between;padding: 14px 13px 14px 13px;background: #fff;">
        <van-checkbox style="width: 300px" @click="diagnosisChoose(diagnosis.id, diagnosis.checked)" v-model="diagnosis.checked">{{ diagnosis.name + '(' + getDiagnosisPatientList(diagnosis.id).length + ')'}}</van-checkbox>
        <div>
          <van-icon :name="diagnosis.show?'arrow-up':'arrow-down'" @click="diagnosis.show = !diagnosis.show" />
        </div>
      </div>
      <div v-if="diagnosis.show" style="padding-left: 20px;background: #fff">
        <van-checkbox  v-for="(patient, index) in getDiagnosisPatientList(diagnosis.id)" :key="diagnosis.id + '_' + index + '_' +  patient.id"
                       v-model="patient.checked"
                       style="border-bottom: 1px solid #F5F6F7;padding: 7px 0 6px 0">
          <div style="display: flex;align-items: center">
            <span><img :src="patient.avatar" style="width: 45px; border-radius: 50%" alt=""></span>
            <span style="margin-left: 10px">{{ patient.name }}</span>
            <span v-if="patient.remark">({{patient.remark}})</span>
          </div>
        </van-checkbox>
      </div>
    </div>
    <!--    自定义小组6-7-->
    <div style="padding: 11px 0 11px 15px;background: #F5F5F5;border-top: 1px solid #ddd;font-size: 13px">按自定义分组</div>
    <div style="border-top: 1px solid #ddd; " v-for="group in customGroupList" :key="group.id">
      <div style="display: flex;justify-content: space-between;padding: 14px 13px 14px 13px;background: #fff;">
        <van-checkbox style="width: 80%" @click="groupChoose(group)" v-model="group.checked">{{ group.name + '(' + group.patientCount +')' }}</van-checkbox>
        <div>
          <van-icon  :name="group.show?'arrow-up':'arrow-down'" @click="openOrCloseGroup(group)" />
        </div>
      </div>
      <div v-if="group.show" style="padding-left: 20px;background: #fff">
        <van-checkbox  v-for="(patient, index) in getGroupPatientList(group.id)" :key="group.id + '_' + index + '_' + patient.id"
                       v-model="patient.checked"
                       style="border-bottom: 1px solid #F5F6F7;padding: 7px 0 6px 0">
          <div style="display: flex;align-items: center">
            <span><img :src="patient.avatar" style="width: 45px; border-radius: 50%" alt=""></span>
            <span style="margin-left: 10px">{{ patient.name }}</span>
            <span v-if="patient.remark">({{patient.remark}})</span>
          </div>
        </van-checkbox>
      </div>
    </div>
    <van-overlay :show="loading">
      <div class="wrapper" @click="loading = false">
        <van-loading size="24px" vertical>数据加载中，请稍等</van-loading>
      </div>
    </van-overlay>
    <SelfDialog @showPop="showPop" :submit-message="submitData" :show="dialogShow" :people-number="peopleNumber"></SelfDialog>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Checkbox, CheckboxGroup, Toast, Sticky, Overlay, Loading} from 'vant'
import { getDiagnosis } from '@/api/formApi.js'
import { imGroupPatientPage } from '@/api/massMessaging.js'
import { customGroupingQueryAll, customGroupPatientList } from '@/api/customGroup.js'
import SelfDialog from './Dialog'

Vue.use(Icon)
Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Overlay)
Vue.use(Loading)
export default {
  components: {
    SelfDialog
  },
  data () {
    return {
      loading: false,
      dialogShow: false,
      peopleNumber: 0,
      submitData: {},
      nursingId: localStorage.getItem('caringNursingId'),
      // 疾病类型列表
      diagnosisList: [],
      diagnosisPatientMap: new Map(),
      groupPatientMap: new Map(),
      initDiagnosisStatus: false,
      // 自定义患者小组列表
      customGroupList: [],
      initCustomGroupStatus: false,
      // 全部患者列表
      allPatientList: [],
      // 患者分页的页码
      patientCurrent: 1,
      patientInterval: undefined,
      // 所有患者的选中状态
      allChecked: false,
      allPatientShow: false,
      receiverIdsList: [],
      saoMaPatientList: [],
      saoMaPatientChecked: false,
      saoMaPatientShow: false,
      zhuCePatientList: [],
      zhuCePatientChecked: false,
      zhuCePatientShow: false,
      sentType: this.$route.query.sentType, // 群发类型
      continueSentMessageInfo: this.$route.query.continueSentMessageInfo, // 继续群发的记录ID
      localMessageId: this.$route.query.localMessageId // 本地设置的群发信息的缓存 key
    }
  },
  created () {
    this.initPageData()
  },
  methods: {
    initCheckPatient () {
      if (this.localMessageId) {
        const localMessageJSONString = localStorage.getItem(this.localMessageId)
        const localMessage = JSON.parse(localMessageJSONString)
        this.diagnosisList = localMessage.diagnosisList
        this.customGroupList = localMessage.customGroupList
        this.allChecked = localMessage.allChecked
        this.allPatientShow = localMessage.allPatientShow
        this.saoMaPatientChecked = localMessage.saoMaPatientChecked
        this.saoMaPatientShow = localMessage.saoMaPatientShow
        this.zhuCePatientChecked = localMessage.zhuCePatientChecked
        this.zhuCePatientShow = localMessage.zhuCePatientShow
        this.receiverIdsList = localMessage.receiverIdsList
        if (this.receiverIdsList) {
          this.allPatientList.forEach(item => {
            if (this.receiverIdsList.indexOf(item.id) > -1) {
              item.checked = true
            }
          })
        }
        if (this.customGroupList) {
          for (let i = 0; i < this.customGroupList.length; i++) {
            if (this.customGroupList[i].loadPatient) {
              this.loadingGroupPatient(this.customGroupList[i])
            }
          }
        }
        this.sentType = 'add'
      }
    },

    /**
     * 初始化页面的数据
     */
    initPageData () {
      this.loading = true
      this.initDiagnosis()
      this.initCustomGroup()
      const that = this
      let patientInterval = window.setInterval(() => {
        if (this.initDiagnosisStatus && this.initCustomGroupStatus) {
          if (patientInterval) {
            window.clearInterval(patientInterval)
            that.initPatientInfo()
          }
        }
      }, 150)
      // 计时器等待 疾病和小组数据舒适化完毕。然后加载患者数据
    },

    /**
     * 加载患者数据
     */
    initPatientInfo () {
      const params = {
        'current': this.patientCurrent,
        'model': {
          'serviceAdvisorId': this.nursingId
        },
        'size': 300
      }
      imGroupPatientPage(params).then(res => {
        console.log(res)
        if (res.data) {
          res.data.records.forEach(item => { item.checked = false })
          this.allPatientList.push(...res.data.records)
          this.setPatientList(res.data.records)
          if (res.data.pages === 0 || res.data.pages === this.patientCurrent) {
            this.loading = false
            this.initCheckPatient()
            console.log('数据没有了。')
          } else {
            this.patientCurrent++
            this.initPatientInfo()
          }
        }
      })
    },
    /**
     * 吧患者信息添加到 对应的栏目中去
     */
    setPatientList (patientList) {
      patientList.forEach(item => {
        if (item.status === 1) {
          this.zhuCePatientList.push(item)
        }
        if (item.status === 0) {
          this.saoMaPatientList.push(item)
        }
        if (item.diagnosisId) {
          let patientList = this.diagnosisPatientMap.get(item.diagnosisId)
          if (patientList === undefined) {
            patientList = []
          }
          patientList.push(item)
          this.diagnosisPatientMap.set(item.diagnosisId, patientList)
        }
      })
    },
    /**
     * 初始化疾病的分组
     */
    initDiagnosis () {
      getDiagnosis().then(res => {
        console.log('疾病信息', res.data)
        res.data.forEach(item => {
          item.show = false
          item.checked = false
        })
        this.diagnosisList = res.data
        this.initDiagnosisStatus = true
      })
    },
    /**
     * 初始化自定义小组
     */
    initCustomGroup () {
      const params = {
        'roleType': 'NursingStaff',
        'userId': this.nursingId
      }
      customGroupingQueryAll(params).then(res => {
        console.log('小组数据', res.data)
        res.data.forEach(item => {
          item.show = false
          item.checked = false
        })
        this.customGroupList = res.data
        this.initCustomGroupStatus = true
      })
    },
    /**
     * 诊断类型下的患者数据
     */
    getDiagnosisPatientList (diagnosisId) {
      const patientList = this.diagnosisPatientMap.get(diagnosisId)
      if (patientList) {
        return patientList
      } else {
        return []
      }
    },
    /**
     * 本地map中拿到患者列表
     */
    getGroupPatientList (groupId) {
      const patientList = this.groupPatientMap.get(groupId)
      if (patientList) {
        return patientList
      } else {
        return []
      }
    },
    /**
     * 确定
     */
    determine () {
      // 继续发送群发
      // 找到选中了多少人
      console.log('this.allPatientList', this.allPatientList)
      let checkedPatientIds = new Set()
      for (let i = 0; i < this.allPatientList.length; i++) {
        if (this.allPatientList[i].checked) {
          checkedPatientIds.add(this.allPatientList[i].id)
        }
      }
      if (checkedPatientIds.size === 0) {
        Toast({message: '请选择' + this.$getDictItem('patient'), closeOnClick: true, duration: 1500})
        return
      }
      let receiverIdsList = []
      checkedPatientIds.forEach(item => { receiverIdsList.push(item) })
      const receiverId = receiverIdsList.join(',')

      console.log('sentType', this.sentType)
      if (this.sentType === 'againSend') {
        const params = JSON.parse(this.continueSentMessageInfo)
        params.receiverId = receiverId
        this.submitData = params
        this.peopleNumber = checkedPatientIds.size
        this.dialogShow = true
        console.log(params)
        return
      }
      // 创建一天群发
      if (this.sentType === 'add') {
        // 把本页面的数据封存到缓存中
        let time = new Date().getTime()
        if (!this.localMessageId) {
          this.localMessageId = 'messageId' + time
        }
        const params = {
          'diagnosisList': this.diagnosisList,
          'customGroupList': this.customGroupList,
          receiverIdsList: receiverIdsList,
          peopleNumber: checkedPatientIds.size,
          'receiverId': receiverId,
          'allChecked': this.allChecked,
          'allPatientShow': this.allPatientShow,
          'saoMaPatientChecked': this.saoMaPatientChecked,
          'saoMaPatientShow': this.saoMaPatientShow,
          'zhuCePatientChecked': this.zhuCePatientChecked,
          'zhuCePatientShow': this.zhuCePatientShow
        }
        const localMessage = JSON.stringify(params)
        localStorage.setItem(this.localMessageId, localMessage)
        // 前往设置文本等信息
        this.$router.replace({
          path: '/newsDispatch/content',
          query: {
            localMessageId: this.localMessageId
          }
        })
      }
    },
    goback () {
      this.$router.replace({
        path: '/newsDispatch'
      })
    },
    /**
     * 全部患者的全选方法
     * @param a 组件默认参数
     */
    allCheckedChoose () {
      if (this.allChecked) {
        this.allPatientList.forEach((item) => { item.checked = true })
      } else {
        this.allPatientList.forEach((item) => { item.checked = false })
      }
    },
    showPop () {
      this.dialogShow = false
      console.log('提交啦', this.submitData)
    },
    /**
     * 扫码患者的全选或者取消全选
     */
    saoMaCheckedChoose () {
      if (this.saoMaPatientChecked) {
        this.saoMaPatientList.forEach((item) => { item.checked = true })
      } else {
        this.saoMaPatientList.forEach((item) => { item.checked = false })
      }
    },
    /**
     * 注册患者的全选或者取消全选
     */
    zhucePatientChoose () {
      if (this.zhuCePatientChecked) {
        this.zhuCePatientList.forEach((item) => { item.checked = true })
      } else {
        this.zhuCePatientList.forEach((item) => { item.checked = false })
      }
    },
    /**
     * 诊断类型下的患者 全选
     */
    diagnosisChoose (id, status) {
      let patientList = this.getDiagnosisPatientList(id)
      console.log('diagnosisChoose', id, status, patientList)
      if (status) {
        patientList.forEach((item) => { item.checked = true })
      } else {
        patientList.forEach((item) => { item.checked = false })
      }
    },
    /**
     * 选择小组
     */
    groupChoose (group) {
      if (!group.loadPatient) {
        group.loadPatient = true
        this.loadingGroupPatient(group, group.checked)
      } else {
        let patientList = this.getGroupPatientList(group.id)
        if (group.checked) {
          patientList.forEach((item) => { item.checked = true })
        } else {
          patientList.forEach((item) => { item.checked = false })
        }
      }
    },
    /**
     * 打开或者关闭小组
     * @param group
     */
    openOrCloseGroup (group) {
      if (!group.loadPatient) {
        group.loadPatient = true
        this.loadingGroupPatient(group)
      }
      group.show = !group.show
    },
    /**
     * 加载小组下的患者数据
     * @param group
     */
    loadingGroupPatient (group, checked) {
      customGroupPatientList(group.id).then(res => {
        console.log('group', res.data)
        if (res.data) {
          const groupPatientList = res.data
          let patientList = []
          for (let i = 0; i < groupPatientList.length; i++) {
            let patient = this.allPatientList.find(item => { return item.id === groupPatientList[i].id })
            if (patient) {
              if (checked) {
                patient.checked = checked
              }
              patientList.push(patient)
            }
          }
          this.groupPatientMap.set(group.id, patientList)
          this.$forceUpdate()
        }
      })
    }
  }
}
</script>

<style scoped>

.wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

</style>
