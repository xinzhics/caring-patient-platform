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
        <div style="width: 1px; height: 14px; background: #CCCCCC;"></div>
        <div :class="loginType === 0 ? 'loginText' : 'loginTextGreen'" style="padding-left: 10px"
             @click="setLoginType(1)">短信登录
        </div>
      </div>

      <div class="inputOne" @click.stop.prevent="inputPaentClick('input1')" style="margin-top: 35px">
        <input ref="input1" type="phone" v-model="telephone" placeholder="请输入手机号" maxlength="11"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px;" @input="changePhone">
      </div>
      <div v-if="loginType === 1" class="psd" @click.stop.prevent="inputPaentClick('input2')">
        <input ref="input2" type="text" v-model="verifyCode" placeholder="请输入验证码" maxlength="6"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; width: 60%; border: none">
        <span class="tipWord" @click="sendCheckNumber">{{ tipWord }}</span>
      </div>
      <div v-else class="psd">
        <input ref="input2" type="password" v-model="password" placeholder="请输入密码" maxlength="30"
               style="padding-top: 10px; padding-bottom: 10px; font-size: 16px; border: none; width: 70%">
        <div style="color: #1FCDB6; font-size: 14px" @click="toPsd()">忘记密码</div>
      </div>

      <div v-if="passwordIsUpdated" style="display: flex; align-items: center; margin-top: 10px">
        <van-image
          width="13px"
          height="13px"
          :src="require('@/assets/my/login-tips1.png')"
        />
        <div style="font-size: 12px; color: #10C6B2; margin-left: 5px">初始密码：doctor+手机号后4位</div>
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

    <div class="bottomBtn" >
      <div @click="checkInput = !checkInput" style="margin-right: 5px; display: flex; align-items: center; justify-content: center">
        <van-image
          width="15px"
          height="15px"
          v-if="checkInput"
          :src="require('@/assets/my/login_select.png')"
        />
        <van-image
          v-else
          width="15px"
          height="15px"
          :src="require('@/assets/my/login_no_select.png')"
        />
      </div>
<!--      <input type="checkbox" name="" id="" v-model="checkInput">-->
      <span @click="checkInput = !checkInput" style="color: #BBBBBB">我已阅读并同意</span>
      <span @click="show=!show" style="color: #0AC6B2;">《服务协议》</span>
    </div>

    <van-dialog v-model="show" class="dialog-demo" :dialog-style="{height:'80vh'}" :show-confirm-button="false" close-on-click-overlay @close="show=false">
      <div style="padding:10px;text-align:left;height: 80vh;overflow-y: scroll;" v-html="agreement">
      </div>
    </van-dialog>
    <van-dialog v-model="showOne" class="dialog-demo" :dialog-style="{borderRadius:'10px'}" :showConfirmButton="false">
      <div style="margin:0px;line-height:30px;text-align:center;padding-top:20px">
        <img :src="warn" alt="" width="45px">
      </div>
      <div>
        <p style="margin:0px;line-height:30px;text-align:center">您尚未{{ register }}本项目{{ name }}</p>
        <p style="margin:0px;line-height:30px;text-align:center">请检查手机号或点击下方“立即注册”</p>
      </div>
      <x-button style="color:#fff;background:#3f86ff;width:40vw;margin:20px auto" @click.native="showOne=false">我知道了
      </x-button>
    </van-dialog>

    <van-dialog v-model="isTips" title="温馨提示" :showConfirmButton="false"
                :showCancelButton="false">
      <div style="margin-left: 25px; margin-right: 25px;">
        <div style="text-align: center; font-size: 14px; margin-top: 5px; margin-top: 10px;">
          您已是{{ patient }}身份，确定后{{ patient }}账号将自动删除。
        </div>
        <div style="display: flex; padding-right: 15px; padding-left: 15px;">
          <div style="background: #3f86ff; width: 100%; color: #FFF; padding-left: 15px; padding-right: 15px; border-radius: 10px;
            padding-top: 7px; padding-bottom: 7px; text-align: center; font-size: 14px; margin-top: 20px; "
               @click="tipsConfirm()">登录{{ name }}端
          </div>
        </div>

        <div style="display: flex; padding-right: 15px; padding-left: 15px; margin-bottom: 20px;">
          <div style="background: #999999; width: 100%; color: #ffffff; padding-left: 15px; padding-right: 15px; border-radius: 10px;
            padding-top: 7px; padding-bottom: 7px; text-align: center; font-size: 14px; margin-top: 20px; border: 1px solid #777777"
               @click="tipsCancel()">一会再说
          </div>
        </div>

      </div>
    </van-dialog>

    <van-dialog v-model="isConfirm" :showConfirmButton="false" :showCancelButton="false">
      <div style="margin-left: 25px; margin-right: 25px;">
        <div style="width: 100%; display: flex; justify-content: center; margin-top: 20px;">
          <img
            :src="require('@/assets/my/blue_yes.png')"
            width="70px" height="70px">
        </div>

        <div style="text-align: center; font-size: 14px; margin-top: 5px; margin-top: 10px;">
          已为您切换为{{ name }},微信菜单将在24小时内更新。
        </div>
        <div style="display: flex; padding-right: 15px; padding-left: 15px; margin-bottom: 20px;">
          <div style="background: #3f86ff; width: 100%; color: #FFF; padding-left: 15px; padding-right: 15px; border-radius: 10px;
            padding-top: 7px; padding-bottom: 7px; text-align: center; font-size: 14px; margin-top: 20px; "
               @click="isConfirm = false">我知道了
          </div>
        </div>

      </div>
    </van-dialog>
  </div>
</template>

<script>
import Api from '@/api/doctor.js'
import baseUrl from '../../api/baseUrl.js'
import ContentApi from '@/api/Content.js'
import wx from 'weixin-js-sdk';
import Vue from 'vue';
import {Dialog, Image as VanImage} from 'vant';
import height from "../../components/arrt/height";

Vue.use(VanImage);

export default {
  name: 'Home',
  computed: {
    height() {
      return height
    }
  },
  components: {
    [Dialog.Component.name]: Dialog.Component,
  },
  data() {
    return {
      loginType: 1,
      title: '',
      telephone: '',
      tipWord: '发送验证码',
      Cansend: true,
      verifyCode: '',
      checkInput: false,
      thisCode: '',
      show: false,
      isError: false,
      agreement: '',
      showOne: false,
      warn: require('@/assets/drawable-xhdpi/warn.png'),
      isTips: false,
      isConfirm: false,
      name: '',
      patient: '',
      register: '注册',
      screenHeight: window.innerHeight,
      sendCheckNumberStatus: false,
      CodeToPassword: false,
      password: '',
      errorMsg: '',
      passwordIsUpdated: false,
    }
  },
  created() {
    if (localStorage.getItem('doctortoken') && localStorage.getItem('caring_doctor_id') && localStorage.getItem('headerTenant')) {
      // 开发完成后需要释放下面注释
      this.$router.push('/index')
    } else {
      if (this.$route.query && this.$route.query.userId && this.$route.query.token) {
        localStorage.setItem('caring_doctor_id', this.$route.query.userId)
        localStorage.setItem('doctortoken', this.$route.query.token)
        this.$router.push('/index')
      } else if (this.$route.query && ( this.$route.query.openId || this.$route.query.gerenfuwhaoDoctorLogin)) {
        this.setStatus()
      } else {
        //未授权去后端授权
        this.wxAuthorize()
      }
    }
    this.queryDictItem()
  },
  methods: {
    toPsd() {
      this.$router.replace({path: '/doctorInfoDetail/resetPsd', query: this.$route.query})
    },
    setLoginType(val) {
      this.loginType = val
      this.isError = false
      this.passwordIsUpdated = false
      if (val === 0) {
        this.changePhone()
      }
    },
    getBtnBack() {
      if (this.CodeToPassword) {
        if (this.password.length > 6 && this.checkInput) {
          return '#3F86FF'
        }
        return '#D6D6D6'
      } else {
        if (this.verifyCode.length === 6 && this.checkInput) {
          return '#3F86FF'
        }
        return '#D6D6D6'
      }
    },
    // 跳转到注册
    jumpRegister() {
      if (!this.checkInput) {
        this.$vux.toast.show({
          type: 'text',
          position: 'center',
          text: '请先勾选下方服务协议'
        })
        return
      }
      this.$router.replace({path: '/register/index', query: this.$route.query})
    },
    queryDictItem() {
      ContentApi.getdictionaryItem().then(res => {
        let dictItem = new Map();
        if (res.data.code === 0) {
          const dict = res.data.data
          for (let i = 0; i < dict.length; i++) {
            dictItem.set(dict[i].code, dict[i].name)
          }
          let dictDoctorName = dictItem.get('doctor')
          if (dictDoctorName) {
            this.title = dictItem.get('doctor') + '登录'
            this.name = dictItem.get('doctor')
            this.patient = dictItem.get('patient')
          } else {
            this.title = '医生登录'
            this.name = '医生'
            this.patient = '患者'
          }
        }
      })
    },
    GetQueryString(name) {
      var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
      var r = decodeURI(window.location.search.substr(1)).match(reg);
      if (r != null) return unescape(r[2]);
      return null;
    },
    //未授权过患者进行授权
    wxAuthorize() {

      const officialAccountType = localStorage.getItem('officialAccountType')
      // 项目是个人服务号的项目。直接去患者登录页面
      if (officialAccountType && officialAccountType === 'PERSONAL_SERVICE_NUMBER') {
        return
      }
      var s = window.location.href;
      var h = s.split(".")[0];
      var a = h.split("//")[1];
      let domain = location.hostname
      if (domain === 'localhost') {
        a = 'kailing';
      }
      window.location.href = baseUrl + '/wx/wxUserAuth/anno/getWxUserCode?domain=' + a + '&redirectUri=' + encodeURIComponent(s)
    },
    tipsCancel() {
      this.isTips = false
      wx.closeWindow();
    },
    tipsConfirm() {
      this.isTips = false;
      Api.changeRoleDeletePatient(this.$route.query.openId)
        .then(res => {
          if (res.data.code === 0) {
            this.isConfirm = true;
          }
        })
    },
    inputPaentClick(refName) {
      this.$nextTick(() => {
        this.$refs[refName] && this.$refs[refName].focus();
      });
    },
    setStatus() {
      //获取配置的参数
      //判断是否为患者，是患者  则提示切换为医生
      if (this.$route.query && this.$route.query.role && this.$route.query.role === 'patient' && this.$route.query.openId) {
        Api.checkPatientExist(this.$route.query.openId)
          .then(res => {
            if (res.data.code === 0 && res.data.data) {
              this.isTips = true
            }
          })
      }
      this.regGuidegetGuide()
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
          "type": "DOCTOR_LOGIN"
        }
        Api.sendcheckNumber(params).then((res) => {
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

    /**
     * 检查手机号对应的密码是否是默认密码。
     */
    checkPasswordIsInit() {
      Api.checkPasswordIsInit(this.telephone).then(res => {
        if (res.data.code === 0) {
          console.log('checkPasswordIsInit', res)
          if (res.data.data) {
            // 是默认密码
            this.passwordIsUpdated = true
          } else {
            // 默认密码已经被修改
            this.passwordIsUpdated = false
          }
        }
      })
    },

    regGuidegetGuide() {
      var s = window.location.href;
      var h = s.split(".")[0];
      var a = h.split("//")[1];
      let domain = location.hostname
      if (domain === 'localhost') {
        a = 'kailing';
      }
      axios.get(baseUrl + '/tenant/tenant/anno/getGuideByDomain?domain=' + a).then(res => {
        // //console.log(res)
        if (res.data.code === 0) {
          window.document.title = res.data.data.name
          localStorage.setItem('pageTitle', res.data.data.name)
          if (res.data.data.doctorAgreement) {
            this.agreement = res.data.data.doctorAgreement
          }
        }
      })
    },
    isPoneAvailable(poneInput) {
      var myreg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
      if (!myreg.test(poneInput)) {
        return true;
      } else {
        return false;
      }
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
      } else {
        if (!this.verifyCode || this.verifyCode.length !== 6) {
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '请填写六位数验证码'
          })
          return
        }
      }

      const phone = this.caringDecode(this.telephone)
      let params = {}
      if (this.loginType === 0) {
        // 密码登录
        params = {
          mobile: phone,
          decode: true,
          isToast: true,
          password: this.password,
          openId: this.$route.query.openId
        }
      } else {
        params = {
          mobile: phone,
          decode: true,
          isToast: true,
          verifyCode: this.verifyCode,
          openId: this.$route.query.openId
        }
      }
      let that = this
      Api.doctorlogin(params).then((res) => {
        if (res.data.code === 0) {
          localStorage.setItem("LAST_LOGIN_ROLE", "doctor")
          localStorage.setItem('caring_doctor_id', res.data.data.userId)
          localStorage.setItem('doctortoken', res.data.data.token)
          this.$vux.toast.show({
            type: 'text',
            position: 'center',
            text: '登录成功'
          })
          //如果是登录 则路径是 /
          if (this.$route.query && this.$route.query.redirectUri && this.$route.query.redirectUri !== '/') {
            this.$router.push(this.$route.query.redirectUri)
          } else {
            this.$router.push('/index')
          }
        } else {
          that.isError = true
          that.errorMsg = that.loginType === 0 ? '手机号或密码错误' : '手机号或验证码错误'
        }
      }).catch(() => {
        that.isError = true
        that.errorMsg = that.loginType === 0 ? '手机号或密码错误' : '手机号或验证码错误'
      })
    },
    //倒计时
    NumberTime() {
      const that = this
      var num = 60
      var int = setInterval(() => {
        num > 0 ? num-- : clearInterval(int);
        that.tipWord = '还剩' + num + '秒'
        if (num === 0) {
          that.sendCheckNumberStatus = false
          that.Cansend = true
          that.tipWord = '重新发送'
          if (that.loginType === 1) {
            that.isError = true
            that.errorMsg = '未收到验证码时，请用密码登录'
          }
        }
      }, 1000);
    },
    changePhone() {
      if(!this.isPoneAvailable(this.telephone) && this.loginType === 0) {
        this.checkPasswordIsInit()
      } else {
        this.passwordIsUpdated = false
      }
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
