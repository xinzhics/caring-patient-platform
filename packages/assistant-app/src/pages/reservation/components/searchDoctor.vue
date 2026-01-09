<template>
  <div style="min-height: 100vh;background: #FFFFFF">
    <div style="display: flex;align-items: center;justify-content: space-between;background: #FFFFFF;border-bottom: 1px solid #F4F4F4" :style="{padding: isWeiXin ? '10px 13px 5px 13px' : '40px 13px 5px 13px'}">
      <van-icon @click="()=>{this.$router.go(-1)}" size="26" color="#333333" name="arrow-left" />
      <van-search @search="getDoctorNameAndId" :clearable="false" shape="round" v-model="params.doctorName" placeholder="请输入" />
      <div @click="getDoctorNameAndId" style="color: #337EFF">搜索</div>
    </div>
    <div style="padding: 0 13px">
      <div @click="goDetails(item)" v-if="doctorList.length>0" style="padding: 17px 0;border-bottom: 1px solid #EBEBEB" v-for="(item, index) in doctorList" :key="index">
        <van-icon name="search" style="margin-right: 8px" />
        {{item.name}}
      </div>
<!--   无数据-->
      <div v-if="doctorList.length===0&&showNoDataImg" style="display: flex;align-items: center;flex-direction: column;margin-top: 50vh;transform: translateY(-50%)">
        <img width="152px" height="152px" :src="noDataImg" alt="">
        <div style="margin-top: 9px;color: #999999">暂无搜索内容</div>
      </div>
    </div>
<!-- 医生列表 -->

  </div>
</template>

<script>
import {findDoctorNameAndId} from '@/api/doctorApi.js'
import Vue from 'vue'
import {Search, Toast} from 'vant'
Vue.use(Search)
Vue.use(Toast)
export default {
  name: 'searchDoctor',
  data () {
    return {
      doctorList: [],
      showNoDataImg: false,
      isWeiXin: false,
      params: {
        doctorName: '',
        nursingId: localStorage.getItem('caringNursingId')
      },
      noDataImg: require('@/assets/my/noSearchData.png')
    }
  },
  created () {
    const device = localStorage.getItem('caringCurrentDevice')
    if (device === 'weixin') {
      this.isWeiXin = true
    }
  },
  methods: {
    goDetails (item) {
      if (item.name === '全部') {
        this.$router.push({
          path: '/reservation'
        })
      } else {
        this.$router.push({
          path: '/reservation',
          query: {
            appointmentReview: item.appointmentReview,
            doctorName: item.name,
            doctorId: item.id
          }
        })
      }
    },
    getDoctorNameAndId () {
      if (this.params.doctorName === '') {
        Toast('请输入搜索内容')
        return
      }
      findDoctorNameAndId(this.params).then(res => {
        if (res.code === 0) {
          this.$nextTick(() => {
            this.showNoDataImg = true
            this.doctorList = res.data
            if (res.data.length > 0) {
              this.doctorList.unshift({name: '全部'})
            }
            console.log(this.doctorList)
          })
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
/deep/.van-search{
  padding: 0 !important;
  width: 76%;
  height: 43px;
}

</style>
