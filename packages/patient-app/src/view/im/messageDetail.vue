<template>
  <section class="allContent">
    <navBar pageTitle="详情"></navBar>
    <div style="margin-top: 10px">
      <div
        style="padding-left: 15px; padding-right: 15px; padding-top: 12px; padding-bottom: 12px; display: flex; background: #FFF">
        <img
             style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px"
             v-bind:src=loadingAvatar(patientInfo.avatar)>
        <div style="display: flex; width: 100%; align-items: center; justify-content: center">
          <div style="display: flex; flex: 1">
              <div style="display: flex; font-size: 10px;">
                <span style="font-weight:600; font-size: 15px; color: #333333">{{this.patientInfo.name}}</span>
                <div style="background: #F5F5F5; display: flex; align-items: center; justify-items: center; border-radius: 10px;
                margin-left: 5px; padding-left: 6px; padding-right: 6px; padding-top: 2px; padding-bottom: 2px">
                  <div style="text-align: center; color: #999999; font-size: 11px">我
                  </div>
                </div>
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
          border-bottom: 1px solid #f5f5f5;align-items: center">
          <img
            style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px"
            v-bind:src=loadingAvatar(item.avatar)>
          <div style="display: flex; width: 100%; align-items: center; justify-content: center">
            <div style="display: flex; flex: 1;align-items: center">
                <span style="font-weight:600; font-size: 15px; color: #333333">{{item.name}}</span>
                <div style="text-align: center; color: #999999; font-size: 11px;background: #F5F5F5;border-radius: 10px;padding: 2px 6px;margin-left: 5px">{{item.type==='NursingStaff'?$getDictItem('assistant'):$getDictItem('doctor')}}</div>
            </div>
          </div>

        </div>


      </div>
    </div>

  </section>
</template>

<script>
  import Api from '@/api/Content.js'


  export default {
    name: "messageDetail",
    components:{
      navBar: () => import('@/components/headers/navBar'),
    },
    data() {
      return {
        patientInfo: {},
        doctorList: []

      }
    },

    methods: {
      getGroupDetail() {
        Api.getPatientGroupDetail(this.$route.query.id)
          .then((res) => {
              if (res.data.code === 0) {
                this.patientInfo = res.data.data.patient;
                this.doctorList = res.data.data.groupMembers;
              }
          })
      },

      loadingAvatar(avatar) {
        return avatar ? avatar : require('@/assets/my/nursingstaff_avatar.png')
      },
    },

    created() {
      this.getGroupDetail();
    }
  }
</script>

<style scoped>

</style>
