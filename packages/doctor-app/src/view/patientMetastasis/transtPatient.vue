<template>
  <div class="content">
    <div v-if="!selectPatient&&!selectDoctor">
      <x-header style="margin-bottom:0px !important" :left-options="{backText: ''}">发起转诊</x-header>
      <div class="allcont">
        <div class="item">
          <div class="header">
            <div>
              转诊{{ patient }}
              <span style="color:red">*</span>
            </div>
            <div>
              <x-icon style="display:inline-block;vertical-align: middle;" class="icon" type="ios-arrow-right"
                      size="20"></x-icon>
            </div>
          </div>
          <div class="selectedList" v-if="selectedpatientList.name" @click="selectPatient=true">
            <div style="display:flex;justify-content: space-between;width:100%;align-items: center;padding:5px 10px;">
              <div style="display:flex;align-items: center;">
                <img :src="selectedpatientList.avatar" alt=""
                     style="border-radius:50%;width:2.2rem;height:2.2rem;vertical-align: middle;margin-right:0.5rem">
                <div>
                  <p style="font-size:14px;line-height:20px">{{
                      selectedpatientList.doctorRemark ? selectedpatientList.name + '(' + selectedpatientList.doctorRemark + ')' : selectedpatientList.name
                    }}</p>
                  <p style="font-size:14px;line-height:20px">
                    {{ selectedpatientList.sex ? (selectedpatientList.sex === 0 ? '男  |' : '女  |') : '' }}{{ selectedpatientList.age ? selectedpatientList.age + '岁' : '' }}</p>
                </div>
              </div>
            </div>
          </div>
          <div class="inner" v-if="!selectedpatientList.name" @click="selectPatient=true">
            <x-icon style="display:inline-block;vertical-align: middle;color:#999" type="ios-plus-outline"
                    size="28"></x-icon>
            <span style="line-height:30px">转诊{{ patient }}</span>
          </div>
        </div>
        <div class="item">
          <div class="header">
            <div class="left">
              接收{{ name }}
              <span style="color:red">*</span>
            </div>
            <div class="right">
              <x-icon style="display:inline-block;vertical-align: middle;" class="icon" type="ios-arrow-right"
                      size="20"></x-icon>
            </div>
          </div>
          <div class="selectedList" v-if="selecteddoctorList.name" @click="selectDoctor=true">
            <div style="display:flex;justify-content: space-between;width:100%;align-items: center;padding:5px 10px;">
              <div style="display:flex;align-items: center;">
                <img :src="selecteddoctorList.avatar" alt=""
                     style="border-radius:50%;width:2.2rem;height:2.2rem;vertical-align: middle;margin-right:0.5rem">
                <div>
                  <p style="font-size:14px;line-height:20px">{{ selecteddoctorList.name }}</p>
                  <p style="font-size:14px;line-height:20px">{{ selecteddoctorList.title }} </p>
                </div>
              </div>
              <div style="align-items: center;">
                {{ selecteddoctorList.hospitalName }}
              </div>
            </div>
          </div>
          <div class="inner" v-if="!selecteddoctorList.name" @click="selectDoctor=true">
            <x-icon style="display:inline-block;vertical-align: middle;color:#999" type="ios-plus-outline"
                    size="28"></x-icon>
            <span style="line-height:30px">接收{{ name }}</span>
          </div>
        </div>
        <div class="otherItem">
          <div class="label">转诊性质
            <span style="color:red">*</span>
          </div>
          <div class="option">
            <span :class="referralCategory==0?'active':''" @click="referralCategory=0;ifendCan()">单次转诊</span>
            <span :class="referralCategory==1?'active':''" @click="referralCategory=1;ifendCan()">长期转诊</span>
          </div>
        </div>
        <div class="docs">
          <img height="12" width="12" style="margin-right:5px" :src="require('@/assets/my/warning.png')"/>单次转诊仅下次就诊为转诊，{{ patient }}关系不转移，长期转诊{{ patient }}关系
          转移至接收{{ name }}处
        </div>
        <div class="bottomBtn">
          <div :class="endBtn?'active':'noclick'" @click="submit()">
            发起转诊
          </div>
        </div>
      </div>
    </div>
    <div v-if="selectPatient">
      <patientList @patientSelectBtn="patientSelectBtn"/>
    </div>
    <div v-if="selectDoctor">
      <doctorList @doctorSelectBtn="doctorSelectBtn"/>
    </div>
    <confirm v-model="delshow"
             @on-cancel="delshow=false"
             @on-confirm="delonConfirm">
      <div style="text-align:center;">
        <div style="text-align:center;font-size:14px;font-weight:bolder">
          转诊确认
        </div>
        <p style="color:#FF9848;font-size:12px;margin:10px 0px">请检查核对以下信息后确定发起本次转诊</p>
        <div style="text-align:left;background:#f5f5f5;font-size:14px;padding:5px">
          <p style="line-height:30px">
            <span style="color:#999">转诊{{ patient }}:</span>
            <span
              style="color:#666">{{ selectedpatientList.doctorRemark ? selectedpatientList.name + '(' + selectedpatientList.doctorRemark + ')' : selectedpatientList.name }}</span>
          </p>
          <p style="line-height:30px">
            <span style="color:#999">接收{{ name }}:</span>
            <span style="color:#666">{{ selecteddoctorList.name }}</span>
          </p>
          <p style="line-height:30px">
            <span style="color:#999">转诊性质:</span>
            <span style="color:#666">{{ referralCategory === 0 ? '单次转诊' : '长期转诊' }}</span>
          </p>
        </div>
      </div>
    </confirm>
  </div>
</template>
<script>
import patientList from "./components/patientList";
import doctorList from "./components/doctorList";
import {Icon, Confirm} from 'vux'
import Api from '@/api/Content.js'

export default {
  components: {
    Icon,
    Confirm,
    patientList,
    doctorList
  },
  data() {
    return {
      delshow: false,
      selectPatient: false,
      selectDoctor: false,
      selectedpatientList: {},
      selecteddoctorList: {},
      referralCategory: 3,
      endBtn: false,
      name: this.$getDictItem('doctor'),
      patient: this.$getDictItem('patient')
    }
  },
  methods: {
    patientSelectBtn(item) {
      this.selectPatient = false
      if (item.id) {
        this.selectedpatientList = item
      }
      this.ifendCan()

    },
    doctorSelectBtn(item) {
      this.selectDoctor = false
      if (item.id) {
        this.selecteddoctorList = item
      }
      this.ifendCan()
    },
    ifendCan() {
      if (this.referralCategory != 3 && this.selectedpatientList.id && this.selecteddoctorList.id) {
        this.endBtn = true
      }
    },
    delonConfirm() {
      const params = {
        "acceptDoctorId": this.selecteddoctorList.id,
        "launchDoctorId": localStorage.getItem('caring_doctor_id'),
        "patientId": this.selectedpatientList.id,
        "referralCategory": this.referralCategory
      }
      Api.launchReferral(params).then((res) => {
        if (res.data.code === 0) {
          this.$router.go(-1)
        } else {
          this.$vux.toast.text(res.data.msg, 'cneter')
        }
      })
    },
    submit() {
      this.delshow = true
    }
  }
}
</script>
<style lang="less" scoped>
.noclick {
  pointer-events: none
}

.content {
  div {
    .allcont {
      .item {
        background: #fff;
        padding: 0px 10px 10px 10px;
        margin-top: 10px;

        .header {
          border-bottom: 1px solid #f5f5f5;
          display: flex;
          justify-content: space-between;
          font-size: 14px;
          line-height: 30px;
          padding: 5px 0px;
          color: #666666;

        }

        .selectedList {
          padding: 5px 0px;
          border-radius: 6px;
          display: flex;
          justify-content: space-between;
          font-size: 14px;
          color: #333;
          background: #f5f5f5;
        }

        .inner {
          background: #f5f5f5;
          margin-top: 10px;
          text-align: center;
          color: #999;
          padding: 15px 0px;
        }
      }

      .otherItem {
        display: flex;
        justify-content: space-between;
        font-size: 14px;
        margin-top: 10px;
        padding: 10px;
        background: #fff;
        color: #666666;
        // .label{

        // }
        .option {
          span {
            padding: 2px 5px;
            border: 1px solid #B8B8B8;
            border-radius: 40px;
            font-size: 12px;
            margin-left: 10px;
            color: #999;
          }

          .active {
            color: #3F86FF;
            border: 1px solid #3F86FF;
          }
        }
      }

      .docs {
        padding: 10px;
        font-size: 10px;
        color: #999;
      }

      .bottomBtn {
        padding: 30px 0px;

        div {
          width: 60%;
          background: #ccc;
          text-align: center;
          color: #fff;
          margin: 0px auto;
          border-radius: 60px;
          padding: 10px 0px;
        }

        .active {
          background: #3F86FF;
        }

      }
    }
  }
}
</style>
