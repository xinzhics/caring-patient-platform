<template>
  <div class="content">
    <x-header :left-options="{ backText: '' }">密码设置</x-header>
    <van-image
      width="100%"
      :src="require('@/assets/my/rest_psd_bg.png')"
    />

    <div style="padding: 0px 40px 20px 40px;">
      <div>
        <div class="psd" @click.stop.prevent="inputPaentClick('input3')">
          <input ref="input3" :type="psdFlag1 ? 'text' : 'password'" v-model="password1" placeholder="请输入密码" maxlength="30" @input="handleInput1"
                 style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; border: none; width: 80%">
          <div style="display: flex; justify-content: flex-end" @click="psdFlag1 = !psdFlag1">
            <van-image
              width="20px"
              v-if="!psdFlag1"
              :src="require('@/assets/my/psd_no_look.png')"
            />
            <van-image
              width="20px"
              v-else
              :src="require('@/assets/my/psd_look.png')"
            />
          </div>
        </div>
        <div class="psd" @click.stop.prevent="inputPaentClick('input4')">
          <input ref="input4" :type="psdFlag2 ? 'text' : 'password'" v-model="password2" placeholder="请输入密码" maxlength="30" @input="handleInput2"
                 style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; border: none; width: 80%">
          <div style="display: flex; justify-content: flex-end" @click="psdFlag2 = !psdFlag2">
            <van-image
              width="20px"
              v-if="!psdFlag2"
              :src="require('@/assets/my/psd_no_look.png')"
            />
            <van-image
              width="20px"
              v-else
              :src="require('@/assets/my/psd_look.png')"
            />
          </div>
        </div>
        <div v-if="isPsdError" style="color: #FD503E; font-size: 12px; margin-top: 10px; display: flex; align-items: center">
          <van-image
            width="13px" height="13px"  style="padding-right: 3px"
            :src="require('@/assets/my/psd_red_tips.png')"
          />
          {{errorMsg}}</div>
      </div>


      <div class="loginBtn" @click="submitBtn">确 认</div>

    </div>
  </div>
</template>
<script>

import Api from '@/api/doctor.js'

export default {
  name: 'RestPsd',
  data() {
    return {
      type: 0,
      telephone: '',
      verifyCode: '',
      password1: '',
      password2: '',
      errorMsg: '',
      isPsdError: false,
      tipWord: '发送验证码',
      sendCheckNumberStatus: false,
      Cansend: true,
      psdFlag1: false,
      psdFlag2: false,
      isCodeError: false
    }
  },
  methods: {
    handleInput1() {
      this.password1 = event.target.value.replace(/[^a-zA-Z0-9]/g, '');
      this.isPsdError = false
    },
    handleInput2() {
      this.password2 = event.target.value.replace(/[^a-zA-Z0-9]/g, '');
      this.isPsdError = false
    },
    submitBtn() {
      if (this.password1.length < 6) {
        this.errorMsg = '请输入6位以上密码'
        this.isPsdError = true
        return
      }
      // 提交密码
      if (this.password1 != this.password2) {
        this.errorMsg = '两次输入的密码不同，请重新设置'
        this.isPsdError = true
        return
      }
      let userId = localStorage.getItem('caring_doctor_id')
      Api.updatePassword({
        password: this.password1,
        id: userId
      })
        .then(res => {
          if (res.data.code === 0) {
            this.$router.go(-1)
          }
        })
    },
    inputPaentClick(refName) {
      this.$nextTick(() => {
        this.$refs[refName] && this.$refs[refName].focus();
      });
    },
    sendCheckNumber() {
      if (this.isPoneAvailable(this.telephone)) {
        this.$vux.toast.show({
          type: 'text',
          position: 'center',
          text: '请输入正确的手机号码'
        })
      } else {
        if (this.sendCheckNumberStatus) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '验证码正在发送，请稍等'
          })
          return;
        }
        this.sendCheckNumberStatus = true
        if (!this.Cansend) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '验证码已发送，请稍等'
          })
          return
        }
        const params = {
          "code": '1',
          "mobile": this.telephone,
          "type": "RESET_PASSWORD"
        }
        Api.sendPsdCode(params).then((res) => {
          this.sendCheckNumberStatus = false
          if (res.data.code === 0 && res.data.data) {
            this.tipWord = '正在发送中'
            this.Cansend = false
            this.NumberTime()
            this.$vux.toast.show({
              type: 'text',
              position: 'center',
              text: '发送成功'
            })
          } else if (res.data.code === -10) {
            this.showOne = true
          }
        }).catch(res => {
          this.sendCheckNumberStatus = false
          this.Cansend = true;
        })
      }
    },
    isPoneAvailable(poneInput) {
      var myreg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
      if (!myreg.test(poneInput)) {
        return true;
      } else {
        return false;
      }
    },
    //倒计时
    NumberTime() {
      const that = this
      var num = 60
      var int = setInterval(() => {
        num > 0 ? num-- : clearInterval(int);
        that.tipWord = '还剩' + num + '秒'
        if (num === 0) {
          that.Cansend = true
          that.tipWord = '重新发送'
        }
      }, 1000);
    }
  },
}
</script>
<style lang="less" scoped>

.vux-header {
  margin-bottom: 0px;
}

.content {
  background: #FFFFFF;
  height: 100vh;
  width: 100vw;
  position: relative;
}

.inputOne {
  border-bottom: 1px solid #D6D6D6;

  input {
    border: none;
    width: 100%;
  }
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
  margin-top: 80px;
}

.psd {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #CCCCCC;
  margin-top: 20px
}

.tipWord {
  font-size: 14px;
  color: #1FCDB6;
  padding-right: 10px;
}

</style>
