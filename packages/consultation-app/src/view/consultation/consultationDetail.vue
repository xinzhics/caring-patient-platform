<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">详情<a slot="right" class="fa fa-exist"/>
    </x-header>
    <div>
      <group>
        <cell title="名称" :value="this.groupName"></cell>
        <cell title="描述" :value="this.groupDesc"></cell>
      </group>

      <group>
        <cell title="姓名" :value="this.patientName"></cell>
        <cell title="角色" :value="this.role"></cell>
        <cell title="加入时间" :value="this.patientAddTime"></cell>
      </group>

      <div style="padding-left: 15px; padding-right: 15px; padding-top: 10px; padding-bottom: 10px;
            font-size: 12px; color: #999999">参与人员
      </div>

      <div style="background: #FFFFFF">
        <div
          style="border-bottom:1px solid #F5F5F5;  width: 100%;"
          v-for="(item,i) in this.groupMembers" :key="i">
          <div
            style="padding-left: 15px; padding-right: 15px; padding-top: 12px; padding-bottom: 12px; display: flex;">
            <img v-if="item.memberAvatar" :src="item.memberAvatar" alt=""
                 style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
            <img v-if="!item.memberAvatar" :src="headerImgT" alt=""
                 style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
            <div style="display: flex; width: 100%; align-items: center; justify-content: center">
              <div style="display: flex; flex: 1">
                <div>
                  <div style="display: flex; font-size: 10px;">
                    <span style="font-weight:600; font-size: 15px; color: #333333">{{ item.memberName }}</span>
                    <div style="background: #F5F5F5; display: flex; align-items: center; justify-items: center; border-radius: 10px;
                margin-left: 5px; padding-left: 6px; padding-right: 6px; padding-top: 2px; padding-bottom: 2px">
                      <div style="text-align: center; color: #999999; font-size: 11px">{{getRole(i, item)}}
                      </div>
                    </div>

                  </div>
                  <p style="color: #666666; font-size: 12px">
                    {{ getData(item.updateTime) }}
                  </p>
                </div>
              </div>
            </div>

          </div>
        </div>

      </div>

      <div style="margin-top: 30px; padding-bottom: 20px; ">
        <x-button style="background: #FFF; color: #f9400b; width: 250px;" @click.native="confirm">
          退出
        </x-button>
      </div>

      <van-dialog v-model="dialogShow"
                  confirmButtonText="确定"
                  show-cancel-button @confirm="exitConsutation">
        <div style="display: grid; justify-items: center; margin-top: 15px; margin-bottom: 15px; padding-top: 10px;">
          <img height="60" width="60" :src="require('@/assets/my/warning.png')"/>
          <span style="margin-top: 20px">确定结束？</span>
        </div>
      </van-dialog>

    </div>


  </section>
</template>

<script>

  import Api from '@/api/Content.js'
  import moment from "moment/moment";
  import {Dialog} from 'vant';
  import wx from 'weixin-js-sdk';
  import {getDictionary} from '../../components/utils/index'

  export default {
    components: {
      [Dialog.Component.name]: Dialog.Component,
    },
    name: "consultationDetail",
    data() {
      return {
        groupMembers: [],
        headerImgT: require('@/assets/my/default_avatar.png'),
        patientName: "",
        patientAddTime: "",
        userId: "",
        role: "",
        groupName: "",
        groupDesc: "",
        dialogShow: false,
        window: window,
        dictionaryList: {}
      }
    },
    methods: {
      getMyRole(role) {
        if (role === '患者') {
          return this.dictionaryList.get('patient')
        }else if (role === '医生') {
          return this.dictionaryList.get('doctor')
        }else if (role === '专员') {
          return this.dictionaryList.get('assistant')
        }else {
          return role
        }
      },
      getRole(i, item){
        if (i === 0){
          return "我"
        }else {
          if (item.memberRoleRemarks === '患者') {
            return this.dictionaryList.get('patient')
          }else if (item.memberRoleRemarks === '医生') {
            return this.dictionaryList.get('doctor')
          }else if (item.memberRoleRemarks === '专员') {
            return this.dictionaryList.get('assistant')
          }else {
            return item.memberRoleRemarks
          }
        }
      },
      confirm() {
        this.dialogShow = true;
      },
      exitConsutation() {
        Api.getExitConsultation(this.userId).then((res) => {
          if (res.data.code === 0) {
            wx.closeWindow();
          }
        })
      },
      JumpPatient(id) {
        this.$router.push({path: '/mypatientHome', query: {id: id}})
      },
      getConsultationDetail() {
        this.userId = localStorage.getItem('consultationUserId')
        let openId = localStorage.getItem('memberOpenId')
        Api.getConsultationGroupDetail(this.$route.query.id, openId).then((res) => {
          if (res.data.code === 0) {
            this.groupName = res.data.data.groupName
            this.groupDesc = res.data.data.groupDesc
            this.groupMembers = res.data.data.consultationGroupMembers
            if (this.groupMembers.length > 0) {
              this.role = this.getMyRole(this.groupMembers[0].memberRoleRemarks)
              this.patientName = this.groupMembers[0].memberName;
              this.patientAddTime = moment(this.groupMembers[0].updateTime).format("YYYY.MM.DD HH:mm");
            }
          }
        })
      },
      getData(data) {
        return moment(data).format("YYYY.MM.DD HH:mm");
      },
    },
    created() {
      let dict = JSON.parse(localStorage.getItem("dictionary"))
      this.dictionaryList = new Map()
      if (dict) {
        for (let i = 0; i < dict.length; i++) {
          this.dictionaryList.set(dict[i].code, dict[i].name)
        }
      }else {
        this.dictionaryList.set('doctor', '医生')
        this.dictionaryList.set('patient', '患者')
        this.dictionaryList.set('assistant', '医助')
        this.dictionaryList.set('register', '注册')
        this.dictionaryList.set('notregister', '未注册')
        this.dictionaryList.set('activation', '激活')
        this.dictionaryList.set('notactivation', '未激活')
        this.dictionaryList.set('registerrate', '注册转化率')
        this.dictionaryList.set('unfollowrate', '会员取关率')
        this.dictionaryList.set('diagnostictype', '诊断类型')
      }
      this.getConsultationDetail()
    }
  }
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
  position: relative;
}

</style>



