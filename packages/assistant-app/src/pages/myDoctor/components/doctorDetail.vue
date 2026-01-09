<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :rightIcon="editImg" @showpop="goEditDoctor" :title="$getDictItem('doctor') + '详情'" @onBack="goback"></headNavigation>
    </van-sticky>
    <div class="doctor-top-title">
        <div style="width: 95%;height: 200px;margin: 0 auto;border-radius: 7px;background: #fff;padding: 26px 0 26px 0;text-align: center">
            <div style="width: 65px;margin: 0 auto"><img :src="doctor.avatar ? doctor.avatar : $getDoctorDefaultAvatar()" style="width: 100%;border-radius: 50%" alt=""></div>
            <div style="margin-top: 17px;color: #333333;font-size: 17px;font-weight: 700">{{doctor.name}}</div>
<!--            <div style="margin-top: 13px;color: #666;font-size: 13px">时间 注册</div>-->
            <div @click="showQr" style="padding: 10px 13px;width: 125px;background: #3F86FF;border-radius: 43px;margin: 0 auto;margin-top: 17px;justify-content: center;display: flex;align-items: center;color: #fff">
              <div style="margin-right: 9px"><van-icon name="qr" /></div>
              {{$getDictItem('doctor')}}二维码
            </div>
        </div>
    </div>
    <div style="padding-left: 20px;padding-right: 20px;background: #F5F5F5; padding-bottom: 20px">
      <div style="padding: 17px 13px 18px 14px;background: #ffffff">
        <div class="list-box">
          <span class="box-left">姓名: </span>
          <span class="box-right">{{doctor.name}}</span>
        </div>
        <div class="list-box">
          <span class="box-left">手机号: </span>
          <span class="box-right">{{doctor.mobile}}</span>
          <span style="position: relative" @click="tipsTime()"><van-icon name="question-o" /></span>
        </div>
        <div class="list-box">
          <span class="box-left">医院: </span>
          <span class="box-right">{{doctor.hospitalName}}</span>
        </div>
        <div class="list-box">
          <div class="box-left">科室: </div>
          <div class="box-right">{{doctor.deptartmentName}}</div>
        </div>
        <div class="list-box">
          <span class="box-left">职称: </span>
          <span class="box-right">{{doctor.title}}</span>
        </div>
        <div style="padding-top: 10px;padding-bottom: 10px;" class="list-box">
          <span class="box-left">专业特长: </span>
          <span class="box-right1">{{ specialitys }}</span>
        </div>
        <div class="list-box" style="border: none;padding-top: 10px;padding-bottom: 10px;">
          <span class="box-left">详细介绍: </span>
          <span class="box-right1">{{ introduces }}</span>
        </div>
      </div>
    </div>
    <van-popup closeable v-model="showShareQrCode">
      <div style="width: 300px; height: 520px; border-radius: 15px; position: relative">
        <div style="width: 100%; height: 87%; margin: 0 auto">
          <img :src="doctor.businessCardQrCode" style="width: 100%; height: 100%; object-fit: cover">
        </div>
        <div style="display: flex;justify-content: space-between;align-items: center; position: relative; bottom: 0">
          <div>
            <div style="text-align: center;color: #999999;font-size: 14px; padding-left: 30px" @click="shareDoctorQrCode">
              <img src="../../../assets/wechat.png" style="width: 26px" alt="">
              <div style="margin-top: 6px">分享到微信</div>
            </div>
          </div>
          <div style="text-align: center;color: #999999;font-size: 14px; padding-right: 30px">
            <img src="../../../assets/save.png" style="width: 26px" alt="" @click="downloadDoctorQrCode">
            <div  style="margin-top: 6px">保存到相册</div>
          </div>
        </div>
      </div>
    </van-popup>
<!--    二维码弹出层-->
    <van-popup id="van-popup" v-model="show">
      <div class="qr-pop">
         <div style="color: #fff;text-align: center;line-height: 30px">{{ doctor.name }}</div>
          <div style="margin-top: 20px;padding: 40px">
              <div style="width: 180px;height: 180px;margin: 0 auto">
                <img :src="doctor.qrCode" style="width: 180px;height: 180px; object-fit: cover" alt="">
              </div>
              <div style="text-align: center;margin-top: 20px;color: #dddddd;font-size: 13px">用微信扫一扫,关注后注册</div>
          </div>
          <div style="display: flex;justify-content: space-between;align-items: center" v-if="showQrCodeButton">
            <div>
              <div style="text-align: center;color: #999999;font-size: 14px" @click="shareDoctorQrCode">
                  <img src="../../../assets/wechat.png" style="width: 26px" alt="">
                <div style="margin-top: 6px">分享到微信</div>
              </div>
            </div>
            <div style="text-align: center;color: #999999;font-size: 14px">
              <img src="../../../assets/save.png" style="width: 26px" alt="" @click="downloadDoctorQrCode">
              <div  style="margin-top: 6px">保存到相册</div>
            </div>
          </div>
      </div>
    </van-popup>

    <van-overlay :show="showWxDialog" @click.stop="showWxDialog = false">
      <div  style="display: flex; justify-content: center; padding-top: 100px" @click.stop>
        <van-image
          width="80%"
          :src="doctor.downLoadQrcode"
        />
      </div>
    </van-overlay>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Popup, Toast, Sticky, Overlay, Image as VanImage} from 'vant'
import { doctorDetails } from '@/api/doctorApi.js'
import { queryOfficialAccountType } from '@/api/tenant.js'
Vue.use(Icon)
Vue.use(Sticky)
Vue.use(Popup)
Vue.use(Col)
Vue.use(Row)
Vue.use(Toast)
Vue.use(Overlay)
Vue.use(VanImage)

export default {
  data () {
    return {
      editImg: require('@/assets/caring_edit.png'),
      show: false,
      doctor: {},
      groupId: this.$route.query.groupId,
      doctorId: this.$route.query.doctorId,
      backPathUrl: this.$route.query.pathUrl,
      // 特长数据
      specialitys: '',
      // 详细介绍数据
      introduces: '',
      showQrCodeButton: false,
      showShareQrCode: false,
      showWxDialog: false,
      officialAccountType: ''
    }
  },
  created () {
    this.getDoctorDetail()
    console.log('$getDictItem', this.$getDictItem('doctor'))
    window.scrollTo(0, 0)
    const device = localStorage.getItem('caringCurrentDevice')
    if (device !== 'windows' && device !== 'macos') {
      this.showQrCodeButton = true
    }
    queryOfficialAccountType().then(res => {
      console.log('queryOfficialAccountType', res.data)
      this.officialAccountType = res.data
    })
  },
  methods: {
    /**
     * 展示二维码
     */
    showQr () {
      const device = localStorage.getItem('caringCurrentDevice')
      console.log('================', device)
      if (device && device === 'weixin') {
        this.showShareQrCode = true
      } else {
        this.show = true
      }
    },
    /**
     * 获取医生的详情
     */
    getDoctorDetail () {
      doctorDetails(this.doctorId).then(res => {
        this.doctor = res.data
        if (this.doctor.extraInfo) {
          const extraInfo = JSON.parse(this.doctor.extraInfo)
          this.specialitys = extraInfo.Specialties
          this.introduces = extraInfo.Introduction
        }
        console.log('doctorDetail', this.doctor)
      })
    },
    /**
     * 发送 分享医生码到微信的事件
     */
    shareDoctorQrCode () {
      let msg = {action: 'shareDoctorQrCode', data: {code: this.doctor.businessCardQrCode, name: this.doctor.name}}
      window.parent.postMessage(msg, '*')
    },
    /**
     * 发送 下载医生码到微信的事件
     */
    downloadDoctorQrCode () {
      let isWx = localStorage.getItem('caringCurrentDevice') === 'weixin'
      if (isWx) {
        this.show = false
        this.showShareQrCode = false
        this.showWxDialog = true
        Toast('微信中请长按下载图片', {duration: 5000})
      } else {
        let msg = {action: 'downloadDoctorQrCode', data: this.doctor.downLoadQrcode}
        window.parent.postMessage(msg, '*')
      }
    },
    goback () {
      if (this.groupId) {
        this.$router.replace({
          path: '/mydoctor/groupDetails',
          query: {
            groupId: this.groupId
          }
        })
      } else {
        this.$router.replace({
          path: '/mydoctor'
        })
      }
    },
    goEditDoctor () {
      this.$router.push({
        path: '/mydoctor/addDoctor',
        query: {
          pathUrl: '/mydoctor/doctorDetail',
          doctorId: this.doctorId,
          groupId: this.groupId
        }
      })
    },
    /**
     * 提示一下医生的注册时间和
     * 上线时间
     */
    tipsTime () {
      let message = this.$getDictItem('doctor') + '添加时间:' + this.doctor.createTime.substring(0, 10) +
        '\n首次登录时间:' + (this.doctor.firstLoginTime ? this.doctor.createTime.substring(0, 10) : '-')
      Toast(
        {
          message: message,
          position: 'bottom',
          closeOnClick: true,
          className: 'doctor-mobile-toast'
        })
    }
  }
}
</script>

<style scoped lang="less">
.doctor-top-title{
  height: 256px;
  padding: 13px;
  background: #F5F5F5;
  background-image: url("../../../assets/doc-bg1.png");
  background-size: 100%;
  background-repeat: no-repeat;
}
.list-box{
  font-size: 16px;
  min-height: 51px;
  border-bottom: 1px solid #ddd;
  line-height: 51px;
  display: flex;
  align-items: center;
  /*justify-content: space-between;*/
}
.box-left{
  color: #999;
  width: 30%;
  align-items: center;
}
.box-right{
  color: #666;
  width: 55%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-align: left;
}
.box-right1{
  color: #666;
  width: 55%;
  line-height: 2;
}
/deep/.van-icon-question-o{
  color: #FFBA85 !important;
}
/deep/.van-popup#van-popup{
  border-radius: 10px !important;
  padding: 13px 20px 13px 20px;
  background-image: url("../../../assets/qr-bg.png");
  background-size: 100%;
}
.qr-pop{
  width: 250px;
  height: 400px;
  border-radius: 8px;
}
/deep/.van-popup__close-icon {
  color: #fff !important;
}

</style>
