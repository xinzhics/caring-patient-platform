<template>
  <section class="allContent" style="overflow-y: hidden">
    <navBar pageTitle="创建"></navBar>
    <div>
      <van-cell is-link :value="patientName ? patientName : '请选择'"
                @click="selectPatient()">
        <span slot="title">患者<span style="color: red">*</span></span>
      </van-cell>
      <van-cell is-link :value="groupName ? groupName : '请输入'"
                @click="() => {
                  type = 1,
                  isShow = true
                }">
        <span slot="title">名称<span style="color: red">*</span></span>
      </van-cell>

      <van-cell title="描述" is-link :value="groupDesc ? groupDesc : '请输入'"
                @click="() => {
                  type = 2,
                  isShow = true
                }">
      </van-cell>
      <div style="display: flex; justify-content: center">
        <x-button mini type="primary" action-type="button"
                  style="height:44px; width: 200px; margin-top: 80px;"
                  :style="{background: patientId && groupName ? '#66728B' : '#D6D6D6'}"
        @click.native="save()">确定
        </x-button>
      </div>
    </div>

    <van-dialog v-model="isShow" style="border-radius: 20px;" :hide-on-blur="true" :show-confirm-button="false">
      <div style="padding: 15px 0px;">
        <div
          style="position: relative; width: 100%; display: flex; align-items: center; margin-bottom: 10px;">
          <div style="display: flex; width: 100%; justify-content: center;">
            <div
              style=" max-width: 120px; text-overflow:ellipsis;overflow:hidden;white-space:nowrap; color: #333333; font-size: 16px;">
              请输入
            </div>
          </div>
          <van-icon name="cross" style="position: absolute; right: 0; margin-right: 15px;" size="15px;" color="#B8B8B8"
                    @click="isShow = !isShow"/>
        </div>
        <div style="display: flex; justify-content: center;">
          <div style="width: 80px; height: 5px;background: #FFBE8B; opacity: 1;border-radius: 8px;"></div>
        </div>

        <div style="margin: 0px 15px; background: #F5F5F5; border-radius: 5px; border: 1px solid #EBEBEB;
        margin-top: 20px; margin-bottom: 20px;">
          <van-field
            v-model="groupName"
            rows="4"
            v-if="type == 1"
            autosize
            style="background: #F5F5F5;"
            type="textarea"
            maxlength="120"
            placeholder="请输入内容"
            show-word-limit
          />
          <van-field
            v-model="groupDesc"
            rows="4"
            v-else
            autosize
            style="background: #F5F5F5;"
            type="textarea"
            maxlength="120"
            placeholder="请输入内容"
            show-word-limit
          />
        </div>

        <div style="text-align: center">
          <van-button mini type="primary" round
                    style="height:44px; background: #66728B; width: 200px; margin-top: 10px;"
                    @click="()=> {isShow = false}">确定
          </van-button>
        </div>
      </div>
    </van-dialog>
  </section>
</template>

<script>
import Api from "@/api/doctor";
import { Dialog } from 'vant';
import navBar from "@/components/headers/navBar";
export default {
  components: {
    Dialog,
    navBar
  },
  name: "add",
  data() {
    return {
      patientId: '',
      patientName: '',
      groupName: '',
      groupDesc: '',
      memberAvatar: '',
      submitStatus: false,
      isShow: false,
      type: 1,
    }
  },
  mounted() {

    this.patientId = this.$store.state.consultation.addData.patientId
    this.patientName = this.$store.state.consultation.addData.patientName
    this.memberAvatar = this.$store.state.consultation.addData.memberAvatar
    this.groupName = this.$store.state.consultation.addData.groupName
    this.groupDesc = this.$store.state.consultation.addData.groupDesc
  },
  methods: {
    save() {
      if (this.patientId && this.groupName) {
        if (this.submitStatus) {
          return
        }
        this.submitStatus = true
        let param = {
          doctorId: localStorage.getItem('caring_doctor_id'),
          groupName: this.groupName,
          groupDesc: this.groupDesc,
          memberAvatar: this.memberAvatar,
          stringPatientIds: this.patientId,
        }
        Api.addConsultationGroup(param)
          .then(res => {
            this.submitStatus = false
            if (res.data && res.data.code == 0) {
                this.$router.back()
              }
          })
      }
    },
    selectPatient() {
      Vue.$store.commit('addData', {
        patientId: this.patientId,
        patientName: this.patientName,
        groupName: this.groupName,
        groupDesc: this.groupDesc,
        memberAvatar: this.memberAvatar
      })
      this.$router.push('/consultation/patient')
    },

  }
}
</script>

<style lang="less" scoped>

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
}

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}
</style>
