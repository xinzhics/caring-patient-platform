<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">详情</x-header>
    <div>
      <div
        style="padding-left: 15px; padding-right: 15px; padding-top: 12px; padding-bottom: 12px; display: flex; background: #FFF">

        <div style="display: flex; width: 100%; align-items: center; justify-content: space-between">
          <div style="display: flex;align-items: center">
            <img
                style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px"
                v-bind:src=loadingAvatar(patientInfo.avatar)>
              <div style="display: flex; font-size: 10px;">
                <span style="font-weight:600;max-width: 150px; font-size: 15px; color: #333333">{{patientInfo.doctorRemark && patientInfo.doctorId === doctorId ? patientInfo.name + '(' + patientInfo.doctorRemark + ')' : patientInfo.name}}</span>
              </div>
          </div>
          <div style="display: flex;align-items: center">
            <div style="margin-right: 10px;background: #F5F5F5; display: flex; align-items: center; justify-items: center; border-radius: 10px;
                margin-left: 5px; padding-left: 6px; padding-right: 6px; padding-top: 2px; padding-bottom: 2px;width: 24px;height: 20px">
              <div style="text-align: center; color: #999999; font-size: 11px">{{patient}}
              </div>
            </div>
            <div
                v-if="true"
                style="display: flex; margin-right: 5px; align-items: center; border: 1px solid #3f86ff; font-size:12px;
                color: #3f86ff; border-radius: 15px; padding-left: 15px; padding-right: 15px; height: 30px; text-align: center"
                @click="JumpPatient()">查看
            </div>
          </div>

        </div>

      </div>

      <div style="padding-left: 15px; padding-right: 15px; padding-top: 10px; padding-bottom: 10px;
            font-size: 12px; color: #999999">参与人员
      </div>

      <div v-for="(item,i) in doctorList" :key="i">
        <div
          style="padding-left: 15px; padding-right: 15px; padding-top: 12px; padding-bottom: 12px; display: flex; background: #FFF;
          border-bottom: 1px solid #f5f5f5">
              <div style="display: flex; font-size: 10px;align-items: center">
                <img
                    style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px"
                    v-bind:src=loadingAvatar(item.avatar)>
                <span style="font-weight:600; font-size: 15px; color: #333333;max-width: 150px">{{item.name}}</span>
                <div v-if="" style="background: #F5F5F5; display: flex; align-items: center; justify-items: center; border-radius: 10px;
                margin-left: 5px; padding-left: 6px; padding-right: 6px; padding-top: 2px; padding-bottom: 2px">
                  <div style="text-align: center; color: #999999; font-size: 11px">
                    {{ item.id === doctorId?'我':item.type==='NursingStaff'?$getDictItem('assistant'):$getDictItem('doctor')}}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

    <div style="display: flex; justify-content: center; padding-top: 40px; padding-bottom: 40px;">
      <van-button type="default" round style="width: 220px; color: red" v-if="doctorExitChat == 0" @click="()=> {isImCloseDialog = true}">关闭聊天</van-button>
      <van-button type="info" round style="width: 220px" v-else @click="()=> {isImOpenDialog = true}">开启聊天</van-button>
    </div>

    <!--  关闭聊天  -->
    <van-dialog v-model="isImCloseDialog" show-cancel-button confirmButtonColor="#2F8DEB"
                @confirm="imOpenOrClose(1)"
                confirmButtonText="确定关闭">
      <div style="display: flex; flex-direction: column; align-items: center; padding: 20px 30px">
        <img :src="require('@/assets/my/careful.png')" style="margin-left: 5px" width="60" height="60">
        <div
          style="font-size: 22px; color: #333333; font-weight: 500; margin-top: 12px; margin-bottom: 12px; margin-left: 5px">
          确定要关闭吗？
        </div>
        <div style="color: #666666; font-size: 12px; text-align: center">关闭沟通后，您将不会再收到该患者发送的消息
          但仍可以从聊天列表中找到该患者再次开启沟通
        </div>
      </div>
    </van-dialog>
    <!--  开启聊天  -->
    <van-dialog v-model="isImOpenDialog" :showConfirmButton="false" :showCancelButton="false"
    >
      <div style="padding: 15px 20px; display: flex; flex-direction: column; align-items: center">
        <div style="display: flex; justify-content: right; width: 100%">
          <van-icon name="cross" color="#B8B8B8" @click="() => {isImOpenDialog = false}"/>
        </div>

        <img :src="require('@/assets/my/im_chat_open_bj.png')" style="margin-left: 5px" width="230" height="180">
        <div
          style="font-size: 20px; color: #333333; font-weight: 500; margin-top: 12px; margin-bottom: 12px; margin-left: 5px">
          确定要开启吗？
        </div>
        <div style="color: #666666; font-size: 12px; text-align: center; width: 150px">开启沟通功能后可发送消息是否开启？</div>

        <div style="display: flex; align-items: center; padding: 10px 0px; margin-top: 15px">
          <van-button plain type="info" round style="width: 110px;" @click="() => {isImOpenDialog = false}">取消</van-button>
          <van-button type="info" round style="width: 110px; margin-left: 25px"  @click="imOpenOrClose(0)">确定开启</van-button>
        </div>
      </div>

    </van-dialog>
  </section>
</template>

<script>
  import Api from '@/api/Content.js'
  export default {
    name: "messageDetail",
    data() {
      return {
        patientInfo: {},
        doctorList: [],
        doctorId: localStorage.getItem('caring_doctor_id'),
        patient: this.$getDictItem('patient'),
        isImCloseDialog: false,
        isImOpenDialog: false,
        doctorExitChat: 0,
      }
    },

    methods: {
      // 开启或退出聊天
      imOpenOrClose(chatStat) {
        Api.doctorExitChat({
          exitChat: chatStat,
          patientId: this.patientInfo.id,
        })
          .then(res => {
            if (chatStat == 0) {
              this.isImOpenDialog = false
            }
            this.doctorExitChat = chatStat
            this.getGroupDetail();
          })
      },
      JumpPatient() {
        if (this.patientInfo) {
          this.$router.push({path: '/mypatientHome', query: {id: this.patientInfo.id}})
        }
      },

      getGroupDetail() {
        Api.getPatientGroupDetail(this.$route.query.id)
          .then((res) => {
              if (res.data.code === 0) {
                this.patientInfo = res.data.data.patient;
                this.doctorExitChat = res.data.data.patient.doctorExitChat
                this.doctorList = res.data.data.groupMembers;
                console.log(this.doctorList)
              }
          })
      },

      loadingAvatar(avatar) {
        return avatar ? avatar : require('@/assets/my/nursingstaff_avatar.png')
      },
    },

    created() {
      this.getGroupDetail();
    },
  }
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
  overflow: hidden;
}

</style>
