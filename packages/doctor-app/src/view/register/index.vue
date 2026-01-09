<template>
  <section class="content">
    <x-header style="margin-bottom:0px !important" :left-options="{backText: '',preventGoBack:true}"
              @on-click-back="goBack">{{ doctorName }}个人账号注册
    </x-header>
    <div class="box" :style="{height: innerHeight + 'px'}">

      <div
        style="display: flex; justify-content: center; flex-direction: column; align-items: center; margin-top: 30px; margin-bottom: 10px">
        <div style="display: flex; align-items: center; ">
          <div v-if="active == 0" class="titleStep">
            <div class="titleStepRound"></div>
          </div>
          <div v-else class="titleRound" :style="{background: active > 0 ? '#666' : '#B6BFCD'}">1</div>
          <div class="line" :style="{background: active === 0 ? '#10C6B2' : '#B6BFCD'}"></div>
          <div v-if="active == 1" class="titleStep">
            <div class="titleStepRound"></div>
          </div>
          <div v-else class="titleRound">2</div>
          <div v-if="doctorRegisterType === 0" class="line" :style="{background: active === 1 ? '#10C6B2' : '#B6BFCD'}"></div>
          <div v-if="doctorRegisterType === 0" class="titleRound">3</div>
        </div>

        <div :style="{width: doctorRegisterType === 0 ? '320px' : '220px'}"
          style="display: flex; align-items: center; justify-content: space-between; font-size: 12px; margin-top: 10px;">
          <van-row style="width: 100%">
            <van-col :span="doctorRegisterType === 0 ? '8' : '12'" class="titleText" :style="{color: active >= 0 ? '#191919' : '#B6BFCD'}">填写个人信息</van-col>
            <van-col :span="doctorRegisterType === 0 ? '8' : '12'" class="titleText" :style="{color: active >= 1 ? '#191919' : '#B6BFCD'}">
              {{ doctorRegisterType === 0 ? '注册完成' : '等待审核' }}
            </van-col>
            <van-col v-if="doctorRegisterType === 0" span="8" class="titleText"  style="color: #B6BFCD">进入系统</van-col>
          </van-row>
        </div>

      </div>


      <div style="flex: 1; ">
        <div style="position: relative;" v-if="active === 0">
          <div class="boxContent">
            <van-field v-model="name" placeholder="请输入您的姓名" :maxlength="10" @input="handleInput(0)">
              <div slot="label">
                <div class="title">
                  姓名 <span style="color: #E72B26">*</span>
                </div>
              </div>
            </van-field>
            <div style="position: relative">
              <div style="display: flex; align-items: center; padding: 17px 16px;">
                <div class="van-cell__title van-field__label">
                  <span style="color: #333">手机号</span> <span style="color: #E72B26">*</span>
                </div>
                <div class="van-cell__value van-field__value">
                  <div class="van-field__body" style="justify-content: right">
                    <input type="text" v-model="phone" maxlength="11" placeholder="请输入您的手机号"
                           @input="handleInput(1)"
                           style="border: 0px; font-size: 14px; color: #323233; text-align: right"/>
                  </div>
                </div>
              </div>
              <div :style="{borderBottom: isMobile ? '1px solid #FE0700' : '1px solid #ebedf0'}"
                   style="margin-right: 15px; transform: scaleY(.5);"></div>
              <div style="color: #FE0700; font-size: 12px; position: absolute; left: 15px; bottom: -20px; z-index: 999"
                   v-show="isMobile">该手机号已注册，请登录即可
              </div>

            </div>
            <van-field v-model="org" placeholder="请输入您的工作单位" @input="searchOrg()">
              <div slot="label">
                <div class="title">
                  医院 <span style="color: #E72B26">*</span>
                </div>
              </div>
            </van-field>
            <van-field v-model="password" placeholder="请输入登录密码" type="password" :maxlength="20">
              <div slot="label">
                <div class="title">
                  密码 <span style="color: #E72B26">*</span>
                </div>
              </div>
            </van-field>
            <van-field v-model="password2" placeholder="请再次输入登录密码" type="password" :maxlength="20">
              <div slot="label">
                <div class="title">
                  确认密码 <span style="color: #E72B26">*</span>
                </div>
              </div>
            </van-field>


          </div>

          <div style="font-weight: 500; font-size: 13px; margin-top: 10px; margin-left: 15px; color: #10C6B2; display: flex; align-items: center">
            <van-image
              width="14px"
              height="14px"
              :src="require('@/assets/my/register-tips.png')"
            />
            <span style="margin-left: 5px">注意：请输入正确的手机号</span>
          </div>


          <div style="position: absolute; bottom: 40px; display: flex; justify-content: center; width: 100%;">

          </div>
          <div style="margin-top: 150px; width: 100%; display: flex; justify-content: center">
            <van-button type="primary" round class="commit" @click="submit()">提 交</van-button>
          </div>
        </div>
        <div v-else-if="active === 1" style="padding: 30px 15px;">
          <div style="display: flex; align-items: center" v-if="doctorRegisterType === 0">
            <van-image
              width="100"
              height="100"
              :src="require('@/assets/my/register_success.png')"
            />
            <div style="margin-left: 15px;">
              <div style="font-weight: 600; font-size: 24px; color: #333333;">注册成功</div>
              <div style="font-size: 14px; color: #999;">后续可在个人资料内填充您的其他信息</div>
              <div style="font-size: 16px; color: #333;">该页面将在{{ countdown }}s后自动关闭</div>
            </div>
          </div>
          <div style="font-size: 14px; color: #999; margin-top: 15px; margin-left: 25px;"
               v-if="doctorRegisterType === 0">温馨提示: 系统将在24小时内激活，敬请期待
          </div>
          <div style="display: flex; align-items: center" v-if="doctorRegisterType === 1">
            <van-image
              width="100"
              height="100"
              :src="require('@/assets/my/register_success.png')"
            />
            <div style="margin-left: 15px;">
              <div style="font-weight: 600; font-size: 24px; color: #333333;">提交成功</div>
              <div style="font-size: 14px; color: #999;">请耐心等待管理员审核</div>
              <div style="font-size: 16px; color: #333;">该页面将在{{ countdown }}s后自动关闭</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <van-popup v-model="showHospital" :overlay="false" position="bottom" :safe-area-inset-bottom="true"
               :style="{ height: '40%' }" style="background: #F7F8FA" :close-on-click-overlay="false">
      <h3 style="text-align: center;color: #333;font-weight: 600;font-size: 18px;line-height: 30px;">请选择机构</h3>
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model="loading"
          :finished="finished"
          finished-text=""
          @load="onLoadHospital"
        >
          <div style="margin: 10px 0;">
            <div v-for="(hospital, index) in hospitalList"
                 style="background: white; padding: 8px 10px; border: solid 1px #DCDEE0; text-align: center"
                 @click="selectHospital(hospital)">
              <span>{{ hospital.hospitalName }}</span>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </van-popup>
    <van-dialog v-model="show" :showConfirmButton="false" width="170px">
      <div style="height: 130px; display: flex; flex-direction: column; align-items: center; justify-content: center">
        <van-loading type="spinner" color="#3F86FF" size="45px"/>
        <div style="color: #666; font-size: 14px; margin-top: 5px">
          {{ doctorRegisterType === 0 ? '正在注册，请稍等...' : '正在提交，请稍等...' }}
        </div>
      </div>
    </van-dialog>
  </section>
</template>

<script>

import Vue from 'vue';
import {Col, Row, Step, Steps, Field, CellGroup, Button, Image as VanImage, Dialog, Loading} from 'vant';
import doctorApi from "../../api/doctor";
import wx from "weixin-js-sdk";

Vue.use(Col);
Vue.use(Row);
Vue.use(Step);
Vue.use(Steps);
Vue.use(Field);
Vue.use(CellGroup);
Vue.use(Button);
Vue.use(VanImage);
Vue.use(Dialog);
Vue.use(Loading);

export default {
  name: "register",
  data() {
    return {
      active: 0,
      innerHeight: window.innerHeight - 50,
      doctorName: this.$getDictItem('doctor'),
      name: '',
      phone: '',
      org: '',
      searchOrgName: '',
      hospitalName: '',
      hospitalId: '',
      countdown: 3,
      current: 1,
      loading: false,
      finished: false,
      refreshing: false,
      isMobile: false,
      showHospital: false,
      hospitalList: [],
      password: '',
      password2: '',
      show: false,
      searchOrgStatus: false,
      doctorRegisterType: undefined
    }
  },
  created() {
    this.queryDoctorRegisterType()
  },
  mounted() {
    let info = localStorage.getItem('registerInfo')
    if (info) {
      this.name = JSON.parse(info).name
      this.phone = JSON.parse(info).phone
      this.org = JSON.parse(info).org
      this.hospitalName = JSON.parse(info).hospitalName
      this.hospitalId = JSON.parse(info).hospitalId
    }
  },
  methods: {
    queryDoctorRegisterType() {
      doctorApi.queryDoctorRegisterType().then(res => {
        this.doctorRegisterType = res.data.data
      })
    },
    searchOrg() {
      if (this.org) {
        if (this.org === this.searchOrgName) {
          return;
        }
        this.searchOrgName = this.org
        this.showHospital = true
        if (this.searchOrgStatus) {
          return
        }
        this.searchOrgStatus = true
        this.hospitalList = [];
        this.current = 1
        this.onLoadHospital(true)
      }
      let result = {
        name: this.name,
        phone: this.phone,
        org: this.org,
        hospitalName: this.hospitalName,
        hospitalId: this.hospitalId
      }
      localStorage.setItem('registerInfo', JSON.stringify(result))
    },

    isValidPassword(password) {
      // 校验密码长度
      if (password === null || password.length < 8) {
        return false;
      }

      // 定义正则表达式来校验是否包含至少一个字母和一个数字
      // [a-zA-Z] 匹配任意字母（不区分大小写），\d 匹配任意数字
      var regex = /^(?=.*[a-zA-Z])(?=.*\d).+$/;

      // 使用test方法检查密码字符串是否匹配正则表达式
      return regex.test(password);
    },

    handleInput(type) {
      if (type === 1) {
        this.phone = this.phone.replace(/[^0-9.]/g, "");
      }
      let result = {
        name: this.name,
        phone: this.phone,
        org: this.org,
        hospitalName: this.hospitalName,
        hospitalId: this.hospitalId
      }
      localStorage.setItem('registerInfo', JSON.stringify(result))
    },
    selectHospital(hospital) {
      this.hospitalName = hospital.hospitalName
      this.hospitalId = hospital.id
      this.showHospital = false
      this.org = hospital.hospitalName
      this.handleInput()
    },
    // 计步器选择
    selectStep(index) {
      this.currentStep = index;
    },
    // 后退
    goBack() {
      this.$router.replace({path: '/', query: this.$route.query})
    },
    submit() {
      if (this.name.length <= 1) {
        this.$vux.toast.text('请输入正确的姓名', 'center')
        return
      }

      let regex = /^1[3456789]\d{9}$/;
      if (this.phone.length !== 11 || !regex.test(this.phone)) {
        this.$vux.toast.text('请输入正确的手机号', 'center')
        return
      }

      if (this.org.length <= 1) {
        this.$vux.toast.text('请输入正确的工作单位', 'center')
        return
      }
      if (!this.password) {
        this.$vux.toast.text('密码不能为空', 'center')
        return
      }
      if (!this.password2) {
        this.$vux.toast.text('密码不能为空', 'center')
        return
      }
      if (!this.isValidPassword(this.password)) {
        this.$vux.toast.text('请输入字母+数字长度8位以上的密码', 'center')
        return;
      }
      if (this.password !== this.password2) {
        this.$vux.toast.text('两次密码不一致', 'center')
        return
      }

      this.show = true
      this.isMobileRegister()
    },
    // 校验手机号
    isMobileRegister() {
      doctorApi.isMobileRegister(this.phone)
        .then(res => {
          this.isMobile = res.data.data
          if (!this.isMobile) {
            // this.isMobile false 表示手机号未被占用
            if (this.doctorRegisterType === 0) {
              this.registerDoctor()
            } else {
              this.doctorAudit()
            }
          } else {
            this.show = false
          }
        })
    },
    /**
     * 提交医生审核信息
     */
    doctorAudit() {
      doctorApi.phoneAuditExist(this.phone).then(res => {
        if (res.data.data === true) {
          this.show = false
          this.$vux.toast.text('信息已经在审核，请勿重复提交', 'center')
        } else {
          const doctorApply = {
            name: this.name,
            openId: this.$route.query.openId,
            wxAppId: localStorage.getItem("wxAppId"),
            mobile: this.phone,
            hospitalId: this.hospitalId,
            password: this.password,
            hospitalName: this.hospitalName
          }
          doctorApi.submitDoctorAudit(doctorApply)
            .then((res) => {
              if (res.data.code === 0) {
                this.$vux.toast.text("提交信息成功", "center");
                this.show = false
                this.active = 1
                const countdownInterval = setInterval(() => {
                  if (this.countdown > 0) {
                    this.countdown--;
                  } else {
                    clearInterval(countdownInterval); // 停止倒计时
                    localStorage.removeItem('registerInfo')
                    wx.closeWindow()
                  }
                }, 1000); // 每秒执行一次
              }
            })
            .catch((res) => {
              this.show = false
              this.$vux.toast.text("提交信息失败", "center");
            });
        }
      })
    },
    onRefresh() {
      this.current = 1
      this.hospitalList = []
      this.finished = true;
      this.onLoadHospital();
    },
    /**
     * 加载医院
     */
    onLoadHospital(clear) {
      let params = {
        current: this.current,
        model: {
          hospitalName: this.org
        },
        size: 10
      }
      doctorApi.annoHospitalPage(params).then(res => {
        this.searchOrgStatus = false
        if (res.data.code === 0) {
          if (this.current === 1 || clear) {
            this.hospitalList = res.data.data.records
          } else {
            this.hospitalList = [...this.hospitalList, ...res.data.data.records]
          }
          this.refreshing = false;
          if (this.current >= res.data.data.pages) {
            this.finished = true;
          } else {
            this.finished = false;
          }
          this.current++
        }
        this.loading = false
      }).catch(res => {
        this.loading = false
      })
    },

    // 注册医生
    registerDoctor() {
      var s = window.location.href;
      var h = s.split(".")[0];
      // 获取域名
      var a = h.split("//")[1];
      let domain = location.hostname
      if (domain === 'localhost') {
        a = 'grfwh';
      }
      //a = 'kailing'
      let params = {
        domain: a,
        mobile: this.phone,
        name: this.name,
        openId: this.$route.query.openId,
        registerOrgName: this.org,
        hospitalId: this.hospitalId,
        hospitalName: this.hospitalName,
        password: this.password
      }
      doctorApi.registerDoctor(params)
        .then(res => {
          localStorage.setItem("LAST_LOGIN_ROLE", "doctor")
          localStorage.setItem('caring_doctor_id', res.data.data.userId)
          localStorage.setItem('doctortoken', res.data.data.token)
          this.show = false
          this.active = 1
          const countdownInterval = setInterval(() => {
            if (this.countdown > 0) {
              this.countdown--;
            } else {
              clearInterval(countdownInterval); // 停止倒计时
              localStorage.removeItem('registerInfo')
              this.$router.replace('/index')
            }
          }, 1000); // 每秒执行一次
        })
    }
  },
}
</script>

<style lang="less" scoped>

.titleText {
  display: flex;
  justify-content: center;
  font-weight: bold;
  color: #656565;
}

.titleStep {
  width: 28px;
  height: 28px;
  border-radius: 15px;
  background: rgba(31, 205, 182, 0.18);
  display: flex;
  align-items: center;
  justify-content: center;
}

.titleStepRound {
  width: 17px;
  height: 17px;
  border-radius: 15px;
  background: #10C6B2;
}

.titleRound {
  width: 25px;
  height: 25px;
  background: #B6BFCD;
  border-radius: 15px;
  font-size: 13px;
  color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
}

.line {
  width: 80px;
  height: 3px;
  background: #B6BFCD;
  border-radius: 3px;
  opacity: 0.42;
}

.boxContent {
  background: #FFF;
  margin: 15px;
  border-radius: 12px;
  padding: 10px 10px;
}

.content {
  height: 100vh;

  .box {
    display: flex;
    flex-direction: column;
    background: #F5F5F5;
    position: relative;

    .stpe {
      overflow: hidden;
      padding-top: 10px;

      .step__circle {
        display: block;
        width: 8px;
        height: 8px;
        background-color: #969799;
        border-radius: 50%;
      }

      .step__line {
        top: 8px;
        width: 100%;
        height: 1px;
      }

      .step__circle-container {
        top: -6px;
        background-color: #fff;
      }

      .stpe_content_title {
        display: flex;
        margin-top: 8px;

        .stpe_content_title_item {
          flex: 1;
          font-weight: 600;
          color: #999999;
          font-size: 14px;
          text-align: center;
        }

      }
    }

    .title {
      color: #333333;
      line-height: 22px;
    }

    .commit {
      width: 90vw;
      font-size: 16px;
      height: 50px;
      background: linear-gradient(to right, #0DC6B2 0%, #70D4B9 100%);
      border: 0px
    }

  }

  /* WebKit browsers */

  input::-webkit-input-placeholder {
    color: #9b9da9;
  }

  /* Mozilla Firefox 4 to 18 */

  input:-moz-placeholder {
    color: #9b9da9;
  }

  /* Mozilla Firefox 19+ */

  input::-moz-placeholder {
    color: #9b9da9;
  }

  /* Internet Explorer 10+ */

  input:-ms-input-placeholder {
    color: #9b9da9;
  }

  /deep/ .van-field__control::-webkit-input-placeholder {
    color: #9b9da9;
    text-align: right;
  }

}

/deep/ .van-field__control {
  text-align: right;
}

</style>
