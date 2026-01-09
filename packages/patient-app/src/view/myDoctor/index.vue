<template>
  <div>
    <navBar :pageTitle="$getDictItem('doctor')+'详情'"></navBar>
    <div style="display: flex;padding-top: 17px;padding-left: 16px;background: #fff;padding-bottom: 25px">
      <img v-if="doctorData.avatar && doctorData.avatar !== null" :src="doctorData.avatar"  style="height: 83px;width: 83px;padding-right: 13px" >
      <img v-else style="height: 83px;width: 83px;padding-right: 13px"src="../../images/head-portrait.png">
      <div>
        <p style="color: #333333;font-size: 19px">{{ doctorData.name }}</p>
        <div style="color: #989898;font-size: 15px;">
          <span>{{ doctorData.deptartmentName }}</span>
          <span v-if="doctorData.deptartmentName!==''&&doctorData.title!==''">|</span>
          <span>{{ doctorData.title }}</span>
          <div>{{ doctorData.hospitalName }}</div>
        </div>
      </div>
    </div>
    <div style="margin-top: 10px;padding-top: 15px;padding-left: 12px;background: #fff;padding-bottom: 27px;padding-right: 13px">
      <p style="color: #333333;padding-bottom: 15px;border-bottom: 1px solid #EBEBEB">专业特长</p>
      <p style="margin-top: 17px; color: #666;font-size: 15px">{{ getSpecialty() }}</p>
    </div>
    <div style="margin-top: 10px;padding-top: 15px;padding-left: 12px;background: #fff;padding-bottom: 27px;padding-right: 13px">
      <p style="color: #333333;padding-bottom: 15px;border-bottom: 1px solid #EBEBEB">详细介绍</p>
      <p style="margin-top: 17px; color: #666;font-size: 15px">{{ getItroduction() }}</p>
    </div>
  </div>
</template>

<script>
import Api from '../../api/Content'

export default {
  data() {
    return {
      loadingDoctor: false,
      doctorData: {},
    }
  },
  components:{
    navBar: () => import('@/components/headers/navBar'),
  },
  methods: {
    getSpecialty() {
      if (this.doctorData.extraInfo) {
        return JSON.parse(this.doctorData.extraInfo).Specialties
      }
    },
    getItroduction(){
      if (this.doctorData.extraInfo) {
        return JSON.parse(this.doctorData.extraInfo).Introduction
      }
    },
    getDoctorInfo() {
      let patientInfoJson = localStorage.getItem('myallInfo')
      let b = {
        id: JSON.parse(patientInfoJson).doctorId
      }
      Api.searchDoctor(b).then(res => {
        this.doctorData = res.data.data
      })
    }
  },
  mounted() {
    this.getDoctorInfo()
  }
}
</script>

<style scoped>

</style>
