<template>
  <div class="content" :style="{height: screenHeight + 'px'}">
<!--    <van-image-->
<!--      width="100%"-->
<!--      :src="require('@/assets/my/login_bg_top.png')"-->
<!--    />-->

    <div style="padding: 100px 40px 20px 40px;">
      <div style="display: flex; align-items: center">
        <div :class="loginType === 1 ? 'loginText' : 'loginTextGreen'" style="padding-right: 10px"
             @click="setLoginType(0)">账号注册
        </div>
      </div>

      <div class="inputOne" @click.stop.prevent="inputPaentClick('input1')" style="margin-top: 35px">
        <input ref="input1" type="phone" v-model="telephone" placeholder="请输入手机号" maxlength="11"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px;" @change="telephoneChange">
      </div>
      <div class="psd">
        <input ref="input2" type="password" v-model="password" placeholder="请输入密码" maxlength="30"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; border: none; width: 70%">
      </div>
      <div class="psd">
        <input ref="input2" type="password" v-model="password2" placeholder="再次输入密码" maxlength="30"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; border: none; width: 70%">
      </div>

      <div v-if="isError" style="display: flex; align-items: center; margin-top: 10px">
        <van-image
          width="13px"
          height="13px"
          :src="require('@/assets/my/login-tips.png')"
        />
        <div style="font-size: 12px; color: #FD503E; margin-left: 5px">{{ errorMsg }}</div>
      </div>

      <div class="loginBtn" @click="submitBtn">注 册</div>
      <div style="width: 100%;margin-top: 20px;text-align: right;color: #b5b5b5;font-size: 14px;" @click="toLogin">有账号去登录</div>

      <van-overlay :show="showDialog" @click="showDialog = false">
        <div class="loginBtn" @click="toLogin">前往登录</div>
      </van-overlay>

    </div>
  </div>
</template>

<script>
  import ContentApi from '@/api/Content.js'
  import {patientRegister, checkMobileIsPatient} from '@/api/patientRegister.js'
  import Vue from 'vue';
  import {Image as VanImage, Dialog as VanDialog } from 'vant';

  Vue.use(VanImage);
  Vue.use(VanDialog);
  export default {
    name: 'Home',
    data() {
      return {
        loginType: 0,
        title: '',
        telephone: '',
        checkInput: true,
        show: false,
        isError: false,
        agreement: '',
        showOne: false,
        isTips: false,
        isConfirm: false,
        name: '',
        register: '注册',
        screenHeight: window.innerHeight,
        password: '',
        password2: '',
        errorMsg: '',
        showDialog: false
      }
    },
    created() {
      const phone = this.$route.query.phone
      if (phone) {
        this.telephone = phone
      }
      this.queryDictItem()
    },
    methods: {
      setLoginType(val) {
        this.loginType = val
        this.isError = false
      },
      queryDictItem() {
        ContentApi.getdictionaryItem().then(res => {
          let dictItem = new Map();
          if (res.data.code === 0) {
            const dict = res.data.data
            for (let i = 0; i < dict.length; i++) {
              dictItem.set(dict[i].code, dict[i].name)
            }
            let dictDoctorName = dictItem.get('patient')
            if (dictDoctorName) {
              this.title = dictItem.get('patient') + '登录'
              this.name = dictItem.get('patient')
            } else {
              this.title = '患者登录'
              this.name = '患者'
            }
          }
        })
      },


      inputPaentClick(refName) {
        this.$nextTick(() => {
          this.$refs[refName] && this.$refs[refName].focus();
        });
      },

      telephoneChange() {
        if (this.telephone.length === 11) {
          checkMobileIsPatient(this.telephone).then(res => {
            console.log('checkOpenIdIsPatient', res)
            if (res.data.code === 0) {
              if (res.data.data) {
                VanDialog.alert({
                  title: '提示',
                  message: '手机号已经注册，是否立即登录',
                }).then(() => {
                  this.toLogin()
                });
              }
            }
          })
        }

      },

      toLogin() {
        this.$router.push({
          path: '/patient/login',
          query: {
            phone: this.telephone
          }
        })
      },
      /**
       * 对信息加密
       * @param str
       * @returns {Uint8Array}
       */
      caringDecode(str) {
        str = 'caring_' + str
        const base64 = Base64.encode(str)
        const result = 'A' + base64 + 'B'
        return Base64.encode(result)
      },
      submitBtn() {
        if (!this.checkInput) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请认真阅读服务协议并同意'
          })
          return
        }
        if (!this.telephone) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请填写手机号'
          })
          return
        }
        if (!this.password || this.password.length < 6) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请输入6位以上密码'
          })
          return
        }
        if (this.password !== this.password2) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '两次密码不一致'
          })
          return
        }
        const phone = this.caringDecode(this.telephone)
        let params = {
          phone: phone,
          password: this.password
        }
        checkMobileIsPatient(this.telephone).then(res => {
          console.log('checkMobileIsPatient', res)
          if (res.data.code === 0) {
            if (res.data.data) {
              VanDialog.alert({
                title: '提示',
                message: '手机号已经注册，是否立即登录',
              }).then(() => {
                this.toLogin()
              });
              return
            } else {
              let that = this
              patientRegister(params).then((res) => {
                console.log('patientRegister', res)
                if (res.data.code === 0) {
                  localStorage.setItem('token', res.data.data.token)
                  localStorage.setItem('userId', res.data.data.userId)
                  localStorage.setItem('wxAppId', res.data.data.wxAppId)
                  localStorage.setItem('expiration', res.data.data.expiration)
                  this.$vux.toast.show({
                    type: 'text',
                    position: 'center',
                    text: '注册成功'
                  })
                  localStorage.setItem("LAST_LOGIN_ROLE", "patient")
                  this.$router.push('/')
                } else {
                  that.isError = true
                  that.errorMsg = that.loginType === 0 ? '手机号或密码错误' : '手机号或验证码错误'
                }
              }).catch(() => {
                that.isError = true
                that.errorMsg = that.loginType === 0 ? '手机号或密码错误' : '手机号或验证码错误'
              })
            }
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>

  .bottomBtn {
    display: flex;
    position: absolute;
    align-items: center;
    bottom: 100px;
    justify-content: center;
    left: 0;
    right: 0;
    font-size: 14px;
  }


  .loginBtn {
    height: 48px;
    background: linear-gradient(270deg, #70D4B9 0%, #09C5B2 100%), #30CCA3;
    border-radius: 50px;
    font-size: 16px;
    color: #FFFFFF;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 50px;
  }

  .psd {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #CCCCCC;
    margin-top: 20px
  }

  .inputOne {
    border-bottom: 1px solid #D6D6D6;

    input {
      border: none;
      width: 100%;
    }
  }

  .content {
    background: #FFFFFF;
    width: 100vw;
    position: relative;
  }

  .loginText {
    font-size: 18px;
    color: #969A9D;
    font-weight: 600;
  }

  .loginTextGreen {
    color: #10C6B2;
    font-size: 18px;
    font-weight: 600;
  }

  .tipWord {
    font-size: 14px;
    color: #1FCDB6;
    padding-right: 10px;
  }

</style>
