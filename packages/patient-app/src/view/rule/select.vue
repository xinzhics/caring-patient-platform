<template>
  <section style="height: 100vh; background: #f5f5f5">
    <van-nav-bar style="height:46px" z-index="99" :fixed="true" :placeholder="true" title="身份选择"/>
    <div style="padding: 15px;">
      <div style="font-size: 26px; font-weight: 600; color: #333333;">身份选择</div>
      <div>
        <span style="font-size: 16px; color: #999999;">请根据您的实际情况进行选择，请谨慎操作，</span><span
          style="font-size: 16px; color: #3F86FF;">一经选择则无法修改。</span>
      </div>

      <div style="display: flex; align-items: center; justify-content: center; margin-top: 150px">
        <div style="display: flex; flex-direction: column; align-items: center" @click="select('doctor')">
          <van-image
              width="100"
              height="100"
              style="border-radius: 80px;"
              :style="{border: type === 'doctor' ? '2px solid #3F86FF' : '2px solid #f5f5f5'}"
              :src="require('@/assets/my/select_doctor.png')"
          />
          <div style="font-weight: 600; font-size: 16px; margin-top: 10px"
               :style="{color: type === 'doctor' ? '#333' : '#999' }">我是{{doctorName}}
          </div>
        </div>

        <div style="display: flex; flex-direction: column; align-items: center; margin-left: 50px"
             @click="select('patient')">
          <van-image
              width="100"
              height="100"
              style="border-radius: 80px;"
              :style="{border: type === 'patient' ? '2px solid #3F86FF' : '2px solid #f5f5f5'}"
              :src="require('@/assets/my/select_patient.png')"
          />
          <div style="font-size: 16px; font-weight: 600; margin-top: 10px"
               :style="{color: type === 'patient' ? '#333' : '#999' }">我是{{patientName}}
          </div>
        </div>
      </div>
      <div style="display: flex; margin-top: 70px; justify-content: center">
        <van-button type="primary" round
                    style="width: 260px; font-size: 18px; height: 50px; background: #3F86FF; border: 2px solid #3F86FF"
                    :disabled="type === '' ? true : false" @click="setTouristUserRole()">确认选择
        </van-button>
      </div>

    </div>

    <van-dialog v-model="show" :showConfirmButton="false" width="170px">
      <div style="height: 130px; display: flex; flex-direction: column; align-items: center; justify-content: center">
        <van-loading type="spinner" color="#3F86FF" size="45px"/>
        <div style="color: #666; font-size: 14px; margin-top: 5px">正在设置，请稍等...</div>
      </div>
    </van-dialog>
  </section>
</template>

<script>

import Vue from 'vue';
import {NavBar, Image as VanImage, Button, Dialog, Loading} from 'vant';

Vue.use(NavBar);
Vue.use(VanImage);
Vue.use(Button);
Vue.use(Dialog);
Vue.use(Loading);

import Content from '@/api/Content.js'

export default {
  name: "select",
  data() {
    return {
      type: '',
      show: false,
      doctorName: '',
      patientName: '',
    }
  },
  created() {
    setTimeout(() => {
      this.doctorName = this.$getDictItem('doctor')
      this.patientName = this.$getDictItem('patient')
      console.log('==========', this.patientName)
    }, 600)
  },
  methods: {
    select(val) {
      this.type = val
    },
    getContent(userId) {
      Content.getContent({id: userId})
          .then(res => {
            let patient = res.data.data
            let enableIntro = localStorage.getItem('enableIntro')
            let patientCompleteFormStatus = localStorage.getItem('patientCompleteFormStatus')
            if (enableIntro !== undefined && enableIntro === '0') {
              // 判断 患者是否已经 同意入组协议。
              if (!patient.agreeAgreement || patient.agreeAgreement === 0) {
                console.log('我要去项目介绍')
                // 要去展示项目介绍。
                this.$router.replace('/')
              }
            }
            if (patientCompleteFormStatus === '1') {
              // 去 一题一页入组
              this.$router.replace({path: '/questionnaire/editquestion', query: {status: 1, isGroup: 1}})
            } else {
              // 去 表单的入组
              this.$router.replace('/two')
            }
          })
    },
    // 患者登录
    getWxUserLogin() {
      Content.getWxUserLogin(this.type, {
        openId: this.$route.query.openId,
        unionId: this.$route.query.unionId,
      })
          .then(res => {
            let userInfo = res.data.data
            localStorage.setItem('userId', userInfo.userId)
            localStorage.setItem('token', userInfo.token)
            localStorage.setItem('myallInfo', JSON.stringify(userInfo))
            this.getContent(userInfo.userId)
          })
    },
    // 选择角色
    setTouristUserRole() {
      let params = {
        openId: this.$route.query.openId,
        unionId: this.$route.query.unionId,
        userRole: this.type
      }
      this.show = true
      Content.setTouristUserRole(params)
          .then(res => {
            if (res.data.data) {
              if (this.type === 'patient') {
                this.getWxUserLogin()
              } else {
                // 跳转到医生登录
                let url = window.location.href.substring(0, window.location.href.indexOf('/'));
                window.location.href = url + '/doctor/?openId=' + this.$route.query.openId + '&doctorLogin=true'
              }
            }
          })
    }
  }
}
</script>

<style lang="less" scoped>

.van-dialog {
  border-radius: 5px;
}

</style>
