<template>
  <section style="height: 100vh; background: #fff; position: relative;">
    <van-nav-bar style="height:46px" z-index="99" :fixed="true" :placeholder="true" title="身份选择"/>
    <img :src="require('@/assets/my/role_select_bg.png')" style="width: 100%; height: auto; display: block;">
    <div style="padding: 25px 30px; text-align: left; position: absolute; top: 100px; left: 0; right: 0; bottom: 0;">
      <div style="font-size: 26px; font-weight: 600; color: #333333;">请选择您的身份</div>
      <div>
        <span style="font-size: 16px; color: #666666;">为保证您的服务质量，请根据您的实际情况选择身份，谨慎操作，一经选择无法更改。</span>
      </div>

      <div style="margin-top: 60px; display: flex; flex-direction: column; align-items: center; gap: 20px;">
        <div
          style="display: flex; align-items: center; width: 100%; padding: 15px 15px; border-radius: 12px; box-sizing: border-box; cursor: pointer; background: #F8FAFB"
          :style="{border: type === 'doctor' ? '2px solid #07c160' : '2px solid #F8FAFB'}"
          @click="select('doctor')"
        >
          <div :style="{
                 width: '20px', height: '20px',
                 borderRadius: '50%',
                 border: type === 'doctor' ? '2px solid #07c160' : '2px solid #cccccc',
                 marginRight: '10px',
                 display: 'flex', alignItems: 'center', justifyContent: 'center',
                 boxSizing: 'border-box'
               }">
            <div v-if="type === 'doctor'" :style="{
                   width: '10px', height: '10px',
                   borderRadius: '50%',
                   background: '#07c160'
                 }"></div>
          </div>
          <div style="font-weight: 600; font-size: 18px; flex-grow: 1; margin-right: 10px;"
               :style="{color: type === 'doctor' ? '#03B668' : '#141414' }">我是医生
          </div>
          <van-image
            width="70"  height="70" style="border-radius: 50%; flex-shrink: 0;"
            :src="require('@/assets/my/select_doctor.png')"
          />
        </div>

        <div
          style="display: flex; align-items: center; width: 100%; padding: 15px; border-radius: 12px; box-sizing: border-box; cursor: pointer; background: #F8FAFB; margin-top: 15px"
          :style="{border: type === 'patient' ? '2px solid #07c160' : '2px solid #F8FAFB'}"
          @click="select('patient')"
        >
          <div :style="{
                 width: '20px', height: '20px',
                 borderRadius: '50%',
                 border: type === 'patient' ? '2px solid #07c160' : '2px solid #cccccc',
                 marginRight: '10px',
                 display: 'flex', alignItems: 'center', justifyContent: 'center',
                 boxSizing: 'border-box'
               }">
            <div v-if="type === 'patient'" :style="{
                   width: '10px', height: '10px',
                   borderRadius: '50%',
                   background: '#07c160'
                 }"></div>
          </div>
          <div style="font-size: 18px; font-weight: 600; flex-grow: 1; margin-right: 10px;"
               :style="{color: type === 'patient' ? '#03B668' : '#141414' }">我是患者
          </div>
           <van-image
            width="70" height="70" style="border-radius: 50%; flex-shrink: 0;"
            :src="require('@/assets/my/select_patient.png')"
          />
        </div>
      </div>

      <!-- New Bottom Container for Button and Nursing Staff Text -->
      <div style="position: absolute; bottom: 30px; left: 30px; right: 30px; box-sizing: border-box; text-align: center;">
          <!-- Button Div (margin-top removed) -->
          <div style="display: flex; justify-content: center">
            <van-button type="primary" round style="width: 100%; max-width: 360px; font-size: 18px; height: 50px; background: #52D99A; border: 2px solid #52D99A"
                        :disabled="type === '' ? true : false" @click="setTouristUserRole()">确认选择
            </van-button>
          </div>

          <!-- Nursing Staff Div -->
          <div style="text-align: center; margin-top: 20px; display: flex; align-items: center; justify-content: center" @click="setNursingRole()">
            <van-image
              width="15" height="15" style="border-radius: 50%; flex-shrink: 0;"
              :src="require('@/assets/my/role_select_nursing.png')"
            />
              <div style="font-size: 16px; color: #52D99A; margin-left: 5px">我是医助
              </div>
          </div>
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
import {NavBar, Image as VanImage, Button, Dialog, Loading, Icon} from 'vant';

Vue.use(NavBar);
Vue.use(VanImage);
Vue.use(Button);
Vue.use(Dialog);
Vue.use(Loading);
Vue.use(Icon);

import Content from '@/api/Content.js'
export default {
  name: "select",
  data() {
    return {
      type: '',
      lastRole: undefined,
      show: false,
    }
  },
  created() {
    // 由于没有openId，不知道当前是谁，只能根据本地上一次登录成功后，缓存的角色，来进行跳转。

    // 表示用户要求必须重新选择角色。
    let reSelectRole = this.$route.query.reSelectRole
    if (!reSelectRole) {
      this.lastRole = localStorage.getItem("LAST_LOGIN_ROLE")
      if (this.lastRole === 'patient') {
        // 进入患者的登录页面，
        this.$router.replace('/patient/login')
      } else if (this.lastRole === 'doctor') {
        // 跳转到医生登录
        let url = window.location.href.substring(0, window.location.href.indexOf('/'));
        window.location.href = url + '/doctor/?gerenfuwhaoDoctorLogin=true'
      } else if (this.lastRole === 'NursingStaff') {
        // 跳转到医助登录
        let url = window.location.href.substring(0, window.location.href.indexOf('/'));
        window.location.href = url + '/assistantH5/#/pages/login/login'
      }
    }
  },
  methods: {
    select(val) {
      this.type = val
    },
    setNursingRole() {
      // 跳转到医助登录
      let url = window.location.href.substring(0, window.location.href.indexOf('/'));
      window.location.href = url + '/assistantH5/#/pages/login/login'
    },
    // 选择角色
    setTouristUserRole() {
      this.show = true
      if (this.type === 'patient') {
        // 进入患者的登录页面，
        this.lastRole = localStorage.getItem("LAST_LOGIN_ROLE")
        if (this.lastRole === 'patient') {
          this.$router.replace('/patient/login')
        } else {
          // 进入患者的注册页面
          this.$router.replace('/patient/register')
        }
      } else if (this.type === 'doctor') {
        // 跳转到医生登录
        let url = window.location.href.substring(0, window.location.href.indexOf('/'));
        window.location.href = url + '/doctor/?gerenfuwhaoDoctorLogin=true'
      }
    }
  }
}
</script>

<style lang="less" scoped>

.van-dialog {
  border-radius: 5px;
}

</style>
