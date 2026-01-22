<template>
  <div class="content" :style="{height: screenHeight + 'px'}">
    <van-image
      width="100%"
      :src="require('@/assets/my/login_bg_top.png')"
    />

    <div style="padding: 0px 40px 20px 40px;">
      <div style="display: flex; align-items: center">
        <div :class="loginType === 1 ? 'loginText' : 'loginTextGreen'" style="padding-right: 10px"
             @click="setLoginType(0)">密码登录
        </div>
      </div>

      <div class="inputOne" @click.stop.prevent="inputPaentClick('input1')" style="margin-top: 35px">
        <input ref="input1" type="phone" v-model="telephone" placeholder="请输入手机号" maxlength="11"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px;" @change="telephoneChange">
      </div>
      <div class="psd">
        <input ref="input2" type="password" v-model="password" placeholder="请输入密码" maxlength="30"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; border: none; width: 70%">
        <div style="color: #1FCDB6; font-size: 14px" @click="toPsd()">忘记密码</div>
      </div>

      <div v-if="isError" style="display: flex; align-items: center; margin-top: 10px">
        <van-image
          width="13px"
          height="13px"
          :src="require('@/assets/my/login-tips.png')"
        />
        <div style="font-size: 12px; color: #FD503E; margin-left: 5px">{{ errorMsg }}</div>
      </div>

      <div class="loginBtn" @click="submitBtn">登 录</div>

      <div style="color: #969A9D; text-align: center; margin-top: 20px; font-size: 14px"
           @click="jumpRegister()">注册账号</div>
    </div>
  </div>
</template>

<script>
  import ContentApi from '@/api/Content.js'
  import {patientLogin, checkMobileIsPatient} from '@/api/patientRegister.js'
  import Vue from 'vue';
  import {Dialog as VanDialog, Image as VanImage} from 'vant';

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
        errorMsg: ''
      }
    },
    created() {
      this.queryDictItem()
      const phone = this.$route.query.phone
      if (phone) {
        this.telephone = phone
      }
      let userId = localStorage.getItem('userId') //获取用户的userId
      let token = localStorage.getItem('token') //获取请求头的token
      if (userId && token) {
        let expiration = localStorage.getItem('expiration')
        if (expiration) {
          const timeout = new Date(expiration)
          // 如果refreshExpiration的已经过期，那么临时用户就清除掉用户信息，重新生产新用户。
          if (new Date() < timeout) {
            this.$router.replace({path: '/home'})
          }
        }
      }
    },
    methods: {
      toPsd() {
        let query = {}
        if (this.$route.query) {
          query = this.$route.query
        }
        this.$router.push({
          path: '/patient/resetPsd',
          query: query
        })
      },
      jumpRegister() {
        this.$router.push({
          path: '/patient/register',
          query: {
            phone: this.telephone
          }
        })
      },
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


      telephoneChange() {
        if (this.telephone.length === 11) {
          checkMobileIsPatient(this.telephone).then(res => {
            console.log('checkOpenIdIsPatient', res)
            if (res.data.code === 0) {
              if (!res.data.data) {
                VanDialog.alert({
                  title: '提示',
                  message: '手机号未注册，是否立即注册',
                }).then(() => {
                  this.jumpRegister()
                });
              }
            }
          })
        }

      },


      inputPaentClick(refName) {
        this.$nextTick(() => {
          this.$refs[refName] && this.$refs[refName].focus();
        });
      },

      patientCompleteEnterGroupNoSuccess(patient) {
        const enableIntro = localStorage.getItem('enableIntro')
        const patientCompleteFormStatus = localStorage.getItem('patientCompleteFormStatus')
        if (enableIntro !== undefined && enableIntro === '0') {
          // 判断 患者是否已经 同意入组协议。
          if (!patient.agreeAgreement || patient.agreeAgreement === 0) {
            console.log('我要去项目介绍')
            // 要去展示项目介绍。
            this.$router.replace("/")
          }
        }
        if (patientCompleteFormStatus === '1') {
          // 去 一题一页入组
          this.$router.replace({path: '/questionnaire/editquestion', query: {status: 1, isGroup: 1}})
        } else {
          // 去 表单的入组
          this.$router.replace('/two')
        }
      },

      //判断是否入组从而知道进入那些界面
      isGroup(userId) {
        ContentApi.getContent({id: userId}).then(res => {
          if (res.data.code === 0) {
            const patient = res.data.data
            if (patient.isCompleteEnterGroup === 1) {
              //如果是登录 则路径是 /
              if (this.$route.query && this.$route.query.redirect) {
                let query = {}
                if (this.$route.query.redirectQuery) {
                  query = JSON.parse(this.$route.query.redirectQuery)
                }
                this.$router.push({
                  path: this.$route.query.redirect,
                  query: query
                })
              } else {
                this.$router.push('/home')
              }
            } else {
              this.patientCompleteEnterGroupNoSuccess(patient)
            }
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
        if (this.loginType == 0) {
          if (!this.password || this.password.length < 6) {
            this.$vux.toast.show({
              type: 'text',
              position: 'center',
              text: '请输入6位以上密码'
            })
            return
          }
        }

        const phone = this.caringDecode(this.telephone)
        let params = {}
        if (this.loginType === 0) {
          // 密码登录
          params = {
            phone: phone,
            password: this.password
          }
        }
        let that = this
        patientLogin(params).then((res) => {
          console.log('patientRegister', res)
          if (res.data.code === 0) {
            localStorage.setItem("LAST_LOGIN_ROLE", "patient")
            localStorage.setItem('token', res.data.data.token)
            localStorage.setItem('userId', res.data.data.userId)
            // localStorage.setItem('wxAppId', res.data.data.wxAppId) 这里不会返回appid
            localStorage.setItem('expiration', res.data.data.expiration)
            this.$vux.toast.show({
              type: 'text',
              position: 'center',
              text: '登录成功'
            })
            const userId = res.data.data.userId
            this.isGroup(userId)
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
