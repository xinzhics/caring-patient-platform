<template>
  <div class="myCenter">
    <x-header :left-options="{backText: ''}">设置</x-header>
    <div style="background: white">
      <van-cell center title="消息提醒">
        <template #right-icon>
          <van-switch v-model="msgChecked" size="24" @click="setMsgChecked()"/>
        </template>
      </van-cell>
    </div>

    <div style="background: white;margin-top: 10px">
      <van-cell center title="医患互动开关">
        <template #right-icon>
          <van-switch v-model="doctorMsgChecked" size="24" @click="setDoctorMsgChecked()"/>
        </template>
      </van-cell>
    </div>

    <div style="color: #999999; font-size: 12px; padding-left: 20px; padding-right: 20px; margin-top: 10px">
      医患互动功能关闭后，您将不参与医-患-助健康服务小组，{{patient}}也看不到 您在其中，仅可与{{assistant}}进行线上沟通。
    </div>

    <div style="color: #666; font-size: 15px; padding-left: 15px; margin-top: 10px; margin-bottom: 10px">
      {{patient}}筛选:
    </div>
    <div v-for="(item,i) in doctorList" :key="i">
      <div style="background: white; border-bottom: 1px solid #f5f5f5" v-if="item.name === '我'">
       <!-- <x-switch :title="'我的'+patient" :value="true" disabled></x-switch>-->
        <van-cell center :title="'我的'+patient">
          <template #right-icon>
            <van-switch disabled v-model="item.readMsgStatus" size="24"/>
          </template>
        </van-cell>
      </div>
      <div style="background: white; border-bottom: 1px solid #f5f5f5" v-else>
        <!--<x-switch :title="item.name + '的'+patient" v-model="item.readMsgStatus" @on-click="setDoctorList(i)"></x-switch>-->
        <van-cell center :title="item.name + '的'+patient">
          <template #right-icon>
            <van-switch v-model="item.readMsgStatus" size="24" @click="setDoctorList(i)"/>
          </template>
        </van-cell>
      </div>
    </div>

  </div>
</template>

<script>
  import Api from '@/api/Content.js'
  import {Switch} from 'vant';
  export default {
    name: "messageSetting",
    components: {
      [Switch.name]: Switch
    },
    data() {
      return {
        msgChecked: false,
        doctorMsgChecked: false,
        data: {},
        doctorList: [],
        patient: this.$getDictItem('patient'),
        assistant: this.$getDictItem('assistant')
      };
    },
    methods: {
      setMsgChecked() {
        console.log(this.msgChecked)
        this.data.imWxTemplateStatus = this.msgChecked ? 1 : 0
        this.setDoctorInfo(this.data)
      },
      setDoctorMsgChecked() {
        this.data.imMsgStatus = this.doctorMsgChecked ? 1 : 0
        this.setDoctorInfo(this.data)
      },
      setDoctorInfo(params) {
        Api.setDoctorInfo(params).then((res) => {
        })
      },
      //获取医生信息
      getDoctorInfo() {
        const params = {
          id: localStorage.getItem('caring_doctor_id')
        }
        Api.getDoctorInfo(params).then((res) => {
          if (res.data.data) {
            this.data = res.data.data
            if (res.data.data.imWxTemplateStatus === 1) {
              this.msgChecked = true;
            } else {
              this.msgChecked = false;
            }
            if (res.data.data.imMsgStatus === 1) {
              this.doctorMsgChecked = true;
            } else {
              this.doctorMsgChecked = false;
            }
          }
        })
      },
      //获取同小组下的医生
      getDoctorList() {
        Api.getGroupDoctor().then((res) => {
          if (res.data.code === 0) {
            this.doctorList = res.data.data
          }
        })
      },
      setDoctorList(index) {
        let params = {
          doctorId: localStorage.getItem('caring_doctor_id'),
          noReadGroupDoctorId: this.doctorList[index].doctorId
        }
        Api.setGroupDoctor(params)
          .then((res) => {

          })
      },
    },
    created() {
      this.getDoctorInfo();
      this.getDoctorList();
    },
     mounted(){
      //    var varperson =JSON.parse(localStorage.getItem("dictionaryItem"))
      // window.dictionaryItem = new Map()
      //     for (let index = 0; index < varperson.length; index++) {
      //       window.dictionaryItem.set(varperson[index].code, varperson[index].name)
      //     }
      // if (window.dictionaryItem) {
      //   this.patient = window.dictionaryItem.get('patient')
      //   this.assistant = window.dictionaryItem.get('assistant')
      // }
    }
  }
</script>

<style scoped>
  .myCenter {
    width: 100vw;
    height: 100vh;
    background: #FAFAFA;
  }


</style>
