<template>
  <div class="myCenter">
    <div class="headerContent">
      <img :src="topbj" height="100%" width="100%">
    </div>
    <div class="content">
      <!--会议详情-->
      <div
        style="padding-left: 15px; padding-right: 15px; padding-top: 12px; padding-bottom: 12px; border-bottom: 1px solid #d6d6d6; display: flex">
        <img :src="groupAvatar ? groupAvatar : headerImgT" alt=""
             style="width:50px;border-radius:50%;height:50px;vertical-align: middle;margin-right:10px">
        <div style="display:inline-block;vertical-align: middle; width: 80%">
          <div style="display: flex; font-size: 10px;">
            <span
              style="font-weight:600; font-size: 17px; color: #333333; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{groupName}}</span>
          </div>
          <div>
            <p
              style="color: #666666; font-size: 12px; display: block; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
              {{groupDesc}}
            </p>
          </div>

        </div>
      </div>

      <div class="cell" style="margin-top: 30px">
        <div class="left-group">头像</div>
        <div class="mid-group">
          <van-uploader
            :after-read="onUpload"
            :multiple="false"
            :max-count="1"
          >
            <img :src="this.memberAvatar ? this.memberAvatar : headerImgT"
                 style="width:35px;border-radius:50%;height:35px;vertical-align: middle"/>
          </van-uploader>
        </div>
        <div class="right-group">
          <x-icon type="ios-arrow-right" size="20"></x-icon>
        </div>
      </div>

      <div class="cell" @click="setName">
        <div class="left-group">姓名</div>
        <div class="mid-group">
          <span style="display: block; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;"
                :style="this.name? 'color:#333333' : 'color:#999999'">{{this.name? this.name : "请输入"}}</span>
        </div>
        <div class="right-group">
          <x-icon type="ios-arrow-right" size="20"></x-icon>
        </div>
      </div>
      <div class="cell" @click="setMobile" v-if="officialAccountType === 'PERSONAL_SERVICE_NUMBER'">
        <div class="left-group">手机号</div>
        <div class="mid-group">
          <span style="display: block; white-space: nowrap;overflow: hidden;text-overflow: ellipsis;"
                :style="this.mobile? 'color:#333333' : 'color:#999999'">{{this.mobile? this.mobile : "请输入"}}</span>
        </div>
        <div class="right-group">
          <x-icon type="ios-arrow-right" size="20"></x-icon>
        </div>
      </div>

      <div class="cell" style="margin-bottom: 30px" @click="setRole">
        <div class="left-group">角色</div>
        <div class="mid-group" :style="this.roleName? 'color:#333333' : 'color:#999999'">{{this.roleName? this.roleName
          : "请选择"}}
        </div>
        <div class="right-group">
          <x-icon type="ios-arrow-right" size="20"></x-icon>
        </div>
      </div>
    </div>

    <x-button v-if="!this.showOne" class="btnConfirm" plain disabled>确定加入</x-button>
    <x-button v-else class="btnConfirm" type="primary" style="background: #66728b" @click.native="confirm">确定加入
    </x-button>


    <van-dialog v-model="inputShow" @confirm="onConfirm">
      <div style="margin-top: 20px">
        <van-field ref="mobile" label-width="45px" autofocus clearable required border v-if="isType === 4" v-model="mobile" label="手机号" placeholder="请输入手机号" maxlength="11" />
        <van-field ref="name" label-width="45px" autofocus clearable required border v-if="isType === 2" v-model="name" label="姓名" placeholder="请输入姓名" maxlength="8" />
        <van-field ref="roleName" label-width="45px" autofocus clearable required border v-if="isType === 3" v-model="roleName" label="角色" placeholder="请输入角色备注" maxlength="6" />
      </div>
    </van-dialog>


    <van-action-sheet
      v-model="sheetShow"
      :actions="actions"
      cancel-text="取消"
      close-on-click-action
      @select="onSelect"
    />

    <div v-transfer-dom>
      <x-dialog v-model="isMe" :dialog-style="{'max-width': '100%', width: '100%', height: '50%', 'background-color': 'transparent'}">
        <p style="color:#fff;text-align:center;">
          <span style="font-size:25px;">温馨提示</span>
        </p>
        <p style="color:#fff;text-align:center;">
          <span style="font-size:25px;">仅限扫描二维码进入</span>
        </p>
      </x-dialog>
    </div>
  </div>
</template>

<script>
  import Api from '@/api/Content.js'
  import ConsultationGroup from '@/api/consultationGroup.js'
  import lanoUploader from '@/components/upload/index'
  import {Confirm, Toast} from 'vux'
  import {ActionSheet, Dialog , Field  } from 'vant';
  import wx from 'weixin-js-sdk';
  import axios from "axios";

  export default {
    name: "index",
    components: {
      Confirm,
      lanoUploader,
      Toast,
      [ActionSheet.name]: ActionSheet,
      [Dialog.Component.name]: Dialog.Component,
      [Field.name]: Field
    },
    data() {
      return {
        groupId: "",
        group: {},
        showOne: false,
        topbj: require('@/assets/my/consultation_bj.png'),
        //userId: "1405450052054286336",
        userId: "",
        headerImgT: require('@/assets/my/default_avatar.png'),
        role: "",
        roleName: "",
        name: "",
        memberAvatar: "",
        inputTitle: "请输入",
        inputShow: false,
        isType: 1,
        actions: [{name: '医生'}, {name: '药师'}, {name: '中医师'}, {name: '康复师'}, {name: '营养师'}, {name: '其他'}],
        sheetShow: false,
        max: {
          default: 1
        }, // 文件数量上限
        memberOpenId: "",
        memberImAccount: "",
        memberStatus: -1,
        groupName: "",
        groupDesc: "",
        groupAvatar: "",
        isMe: false,
        officialAccountType: '',
        mobile: '',
        mobileShow: false
      }
    },
    mounted() {
      this.officialAccountType = localStorage.getItem("officialAccountType")
      this.groupId = this.$route.params.groupId
      localStorage.setItem('groupId', this.groupId)
      this.getConsultationDetail()
      this.setStatus()
    },

    methods: {
      //确认加入
      confirm() {
        const param = {
          consultationGroupId: this.groupId,
          memberAvatar: this.memberAvatar,
          memberName: this.name,
          memberOpenId: this.memberOpenId,
          memberRole: this.role,
          memberRoleRemarks: this.roleName,
          mobile: this.mobile
        }
        if (this.officialAccountType === 'CERTIFICATION_SERVICE_NUMBER') {
          Api.getJoinGroupFromQrCode(param).then((res) => {
            if (res.data.code === 0) {
              localStorage.setItem('memberRole', this.role)
              localStorage.setItem('consultationUserId', this.userId)
              localStorage.setItem('memberOpenId', this.memberOpenId)
              localStorage.setItem('memberImAccount', this.memberImAccount)
              this.$router.replace({
                path: '/consultation/chat',
                query: {groupId: this.groupId, pageTitle: this.groupName}
              })
            }
          })
        } else {
          // 个人服务号。添加用户信息，创建或更新用户，并生成授权
          ConsultationGroup.annoJoinGroupFromQrCode(param).then(res => {
            if (res.data.code === 0) {
              console.log('annoJoinGroupFromQrCode', res.data.data)
              const data = res.data.data
              const token = data.token
              const consultationGroupMember = data.ConsultationGroupMember
              localStorage.setItem('consultationtoken', token)
              localStorage.setItem('memberRole', this.role)
              localStorage.setItem('consultationUserId', data.userId)
              localStorage.setItem('memberMobile', this.mobile)
              localStorage.setItem('memberImAccount', consultationGroupMember.memberImAccount)
              this.$router.replace({
                path: '/consultation/chat',
                query: {groupId: this.groupId, pageTitle: this.groupName}
              })
            }
          })
        }
      },
      //上传头像
      onUpload(files) {
        if (files) {
          var formData = new FormData()
          formData.append('file', files.file)
          formData.append('folderId', 1308295372556206080)

          if (this.type === 'img') {
            const isJPG = (files.file.type === 'image/jpeg' || files.file.type === 'image/png')
            if (!isJPG) {
              this.$vux.toast.text("上传图片只能为jpg或者png", 'center');
              return
            }
          }
          const isLt2M = files.file.size / 1024 / 1024 < 5
          if (!isLt2M) {
            this.$vux.toast.text("上传图片大小不能超过 5MB", 'center');
            return
          }
          Api.annoUpdateImg(formData).then((res) => {
            if (res.data.code === 0) {
              this.$vux.toast.text("上传成功", 'center');
              this.memberAvatar = res.data.data.url
            } else {
              this.$vux.toast.text("文件上传失败", 'center');
            }
          })
        }
      },
      //判断是否可以点击加入按钮
      isBtnEnable() {
        if (this.roleName && this.name) {
          if (this.officialAccountType !== 'CERTIFICATION_SERVICE_NUMBER') {
            if (this.mobile) {
              this.showOne = true
            }
          } else {
            this.showOne = true
          }
        }
      },
      //input输入框
      onConfirm() {
        this.inputShow = false
        this.isBtnEnable()
      },
      //选择角色
      onSelect(item) {
        this.sheetShow = false
        if (item.name === '医生') {
          this.roleName = item.name
          this.role = "roleDoctor"
          this.isBtnEnable()
        } else if (item.name === '药师') {
          this.roleName = item.name
          this.role = "rolePharmacist"
          this.isBtnEnable()
        } else if (item.name === '中医师') {
          this.roleName = item.name
          this.role = "roleChineseDoctor"
          this.isBtnEnable()
        } else if (item.name === '康复师') {
          this.roleName = item.name
          this.role = "roleTherapists"
          this.isBtnEnable()
        } else if (item.name === '营养师') {
          this.roleName = item.name
          this.role = "roleDietitian"
          this.isBtnEnable()
        } else if (item.name === '其他') {
          this.role = "roleOther"   //选择其他时，角色类型
          this.inputShow = !this.inputShow
          this.isType = 3
          this.$nextTick(res => {
            this.$refs['roleName'].focus()
          })
        }
      },
      //名称
      setName() {
        this.inputShow = !this.inputShow
        this.isType = 2
        this.$nextTick(res => {
          this.$refs['name'].focus()
        })
      },
      setMobile() {
        this.inputShow = !this.inputShow
        this.isType = 4
        this.$nextTick(res => {
          this.$refs['mobile'].focus()
        })
      },
      //角色
      setRole() {
        this.sheetShow = !this.sheetShow
      },
      setStatus() {
        var testToken ='eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX3R5cGUiOiJOVVJTSU5HX1NUQUZGIiwibmFtZSI6IuWtmeWKqeeQhiIsInRva2VuX3R5cGUiOiJ0b2tlbiIsInVzZXJpZCI6IjIwMDEwMDAwMDAwMDAwMDAwMTAiLCJhY2NvdW50IjoiMTUxNzM3ODY1NTQiLCJleHAiOjE4MDAxNTQxMzEsIm5iZiI6MTc2OTA1MDEzMX0.Nl9ebv71Z1M6DfgQEjsAaPl41KqOOhFPWEc_JFMdLDk';
        var testUserId = '2001000000000000010';
        debugger;
        if (this.officialAccountType === 'CERTIFICATION_SERVICE_NUMBER') {
          //2026daxiong 调试注释
          // if (getQueryString('groupId')||1) {
            localStorage.setItem('consultationtoken', getQueryString('token') || testToken)
            this.userId = getQueryString('userId') || testUserId
            this.getDictionary()
            this.getConsultationGroupMemeberInfo()
          // } else {
          //   wxAuthorize()
          // }
        } else {
          this.getDictionary()
        }


        //未授权过患者进行授权
        function wxAuthorize() {
          var s = window.location.href;
          var h = s.split(".")[0];
          var a = h.split("//")[1];
          window.location.href = Api.getApiUrl() + '/api/wx/wxUserAuth/anno/getWxUserCode?domain=' + a + '&redirectUri='+ s + '&groupId=' + localStorage.getItem('groupId')
        }
        //获取跳转回来的连接参数
        function getQueryString(name) {
          var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
          var r = window.location.search.substr(1).match(reg);
          if (r != null) return unescape(r[2]);
          return null;
        }
      },
      getDictionary() {
        Api.getDictionary({})
          .then(res => {
            if (res.data && res.data.code == 0) {
              localStorage.setItem("dictionary", JSON.stringify(res.data.data))
            }
          })
      },

      getConsultationGroupMemeberInfo() {
        Api.getConsultationGroupMemeberInfo(this.userId).then((res) => {
          if (res.data.data.group) {
            this.memberOpenId = res.data.data.memberOpenId
            if (this.$route.query.OpenId === undefined || this.memberOpenId === this.$route.query.OpenId) {
              this.groupName = res.data.data.group.groupName
              this.groupDesc = res.data.data.group.groupDesc
              this.groupAvatar = res.data.data.group.memberAvatar
              this.memberAvatar = res.data.data.memberAvatar
              this.memberImAccount = res.data.data.memberImAccount
              this.memberStatus = res.data.data.memberStatus
              this.userId = res.data.data.id
              if (this.memberStatus === 1) {
                this.name = res.data.data.memberName
                this.roleName = res.data.data.memberRoleRemarks
                this.role = res.data.data.memberRole
              }
              this.getwxSignature()
              this.isBtnEnable()
              if (res.data.data.group.consultationStatus &&  res.data.data.group.consultationStatus === 'finish') {
                this.$vux.toast.text("此小组已结束，界面将在2秒后关闭！", 'center');
                setTimeout(() => {
                  wx.closeWindow();
                }, 2000);
              }else if (this.memberStatus === 1) { // -1 不可移除，0 扫码未加入 1 加入状态， 2 已移除
                localStorage.setItem('memberRole', this.role)
                localStorage.setItem('consultationUserId', this.userId)
                localStorage.setItem('memberOpenId', this.memberOpenId)
                localStorage.setItem('memberImAccount', this.memberImAccount)
                this.$router.replace({
                  path: '/consultation/chat',
                  query: {groupId: this.groupId, pageTitle: this.groupName}
                })
              }
            }else {
              this.isMe = true
            }
          }
        })
      },

      getConsultationDetail() {
        ConsultationGroup.annoGetGroup(this.groupId).then((res) => {
          if (res.data.code === 0) {
            this.groupName = res.data.data.groupName
            this.groupDesc = res.data.data.groupDesc
            this.groupAvatar = res.data.data.memberAvatar
          }
        })
      },
      getwxSignature() {
        const that = this
        let url = that.$getWxConfigSignatureUrl();
        axios.get(`${process.env.NODE_ENV === 'development' ? 'http://localhost:8760' : 'https://api.example.com'}/api/wx/config/anno/createJsapiSignature?url=`+encodeURIComponent(url)+'&appId='+localStorage.getItem('wxAppId')).then(res => {
          if (res.data.code === 0) {
            wx.config({
              // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
              debug: false,
              // 必填，公众号的唯一标识
              appId: localStorage.getItem('wxAppId'),
              // 必填，生成签名的时间戳
              timestamp: res.data.data.timestamp,
              // 必填，生成签名的随机串
              nonceStr: res.data.data.nonceStr,
              // 必填，签名
              signature: res.data.data.signature,
              // 必填，需要使用的JS接口列表，所有JS接口列表
              jsApiList: [
                'onMenuShareTimeline',
                'onMenuShareAppMessage',
                'onMenuShareQQ',
                'onMenuShareWeibo',
                'onMenuShareQZone'
              ]
            });
            wx.ready(function () {
              // 分享给好友
              wx.onMenuShareAppMessage({
                title: that.groupName, // 分享标题
                desc: that.groupDesc, // 分享描述
                link: url+"?OpenId="+that.memberOpenId, // 分享链接
                imgUrl: that.groupAvatar, // 分享图标
                success: function () {
                },
                cancel: function () {
                }
              })

              // 分享到朋友圈
              wx.onMenuShareTimeline({
                title: that.groupName, // 分享标题
                link: url+"?OpenId="+that.memberOpenId, // 分享链接
                imgUrl: that.groupAvatar, // 分享图标
                success: function () {
                },
                cancel: function () {
                }
              })
            });
            wx.error(function () {
              console.log('配置不成功')
            });
          }
        })
      },
    }
  }
</script>

<style lang="less" scoped>
  .myCenter {
    width: 100vw;
    height: 100vh;
    background: #FAFAFA;

    .headerContent {
      width: 100%;
      height: calc(25vh);
      background-size: 100% 100%;

      .contentTitle {
        left: 30%;
        position: absolute;
        justify-content: center;
        padding-top: calc(6vh);
        font-size: 20px;
        color: #FFFFFF;
        z-index: 1;
      }

      .headerImg {
        width: 4.5rem;
        height: 4.5rem;
        border-radius: 50%;
        margin: 2.5rem auto 0.3rem;
        overflow: hidden;
        border: 1px solid rgba(255, 255, 255, 0.2);

        img {
          width: 94%;
          height: 94%;
          border-radius: 50%;
          background: #fff;
          margin: 3%;
        }
      }

      .detail {
        text-align: center;
        color: rgba(255, 255, 255, 0.85);

        .name {
          font-size: 1.2rem;
          color: #fff;
          line-height: 1.5;
        }

        .sexAge {
          line-height: 1.5;
          color: #CED2DA;
        }
      }

    }

    .content {
      background: #fff;
      width: 80vw;
      position: absolute;
      left: 5%;
      top: 15vh;
      border-radius: 1rem;
      padding: 0.5rem 5vw;

      .weui-grids {
        &::before {
          border: none !important;
        }

        .weui-grid {
          &::after {
            border: none !important;
          }
        }
      }

      .cell {
        border: 1.5px solid #d6d6d6;
        margin-top: 30px;
        width: 100%;
        height: 50px;
        display: flex;
        border-radius: 10px;
        background: #F5F5F5;

        .left-group {
          width: 100px;
          align-items: center;
          display: flex;
          margin-left: 10px;
          font-size: 15px;
          color: #666666;
        }

        .right-group {
          margin-right: 10px;
          justify-content: flex-end;
          align-items: center;
          display: flex;
        }

        .mid-group {
          flex: 1;
          justify-content: flex-end;
          align-items: center;
          display: flex;
          font-size: 15px;
          color: #B8B8B8;
          white-space: nowrap;
          text-overflow: ellipsis;
          text-overflow: ellipsis;
          overflow: hidden;
        }
      }
    }

    .btnConfirm {
      border-radius: 20px;
      position: fixed;
      bottom: 40px;
      width: 200px;
      left: 50%;
      margin-left: -100px;
    }
  }

  .vux-x-icon {
    fill: #B8B8B8;
  }
</style>
