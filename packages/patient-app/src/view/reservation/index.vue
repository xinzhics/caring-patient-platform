<template>
  <section>
    <navBar pageTitle="精准预约" backUrl="/home" ></navBar>
    <div class="headerContent">
      <img :src="headerimg" alt="">
    </div>
    <group style="margin-top:0px!important" class="reservationClass">
      <cell v-for="(i,k) in list" :key="k" is-link @click.native="goitem(i)">
        <div slot="title">
          <img v-if="i.avatar" :src="i.avatar" alt=""
               style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
          <!-- <span style="vertical-align: middle;">{{i.name}}</span> -->
          <img v-if="!i.avatar" :src="headerImgT" alt=""
               style="width:35px;border-radius:50%;height:35px;vertical-align: middle;margin-right:10px">
          <div style="display:inline-block;vertical-align: middle;">
            <span style="color:#333;font-weight:600">{{ i.name }}</span>
            <p
              style="width:70vw;font-size:12px;color: #999;overflow: hidden;text-overflow:ellipsis;white-space: nowrap;">
              {{ i.deptartmentName }}{{ i.title && i.deptartmentName ? '&nbsp;|&nbsp;' : '' }}{{ i.title }}</p>
          </div>
        </div>
      </cell>
    </group>
    <div class="mybtn" @click="mysubmit">
      <img width="100%" :src="btnImg" alt="">
    </div>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import {Group} from 'vux'

export default {
  components: {
    Group,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      headerimg: require('@/assets/my/resertion.png'),
      headerImgT: require('@/assets/my/touxiang.png'),
      btnImg: require('@/assets/my/btnimg.png'),
      myDoctorId: undefined,
      list: [],
      organId: '',
      current: 1,
    }
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    getInfo() {
      const params = {
        id: localStorage.getItem('userId')
      }
      Api.getContent(params).then((res) => {
        if (res.data.code === 0) {
          if (res.data.data) {
            // console.log(res)
            this.current = 1;
            this.organId = res.data.data.organId;
            this.myDoctorId = res.data.data.doctorId;
            this.getDoctorList();
          }
        }
      })
    },
    getDoctorList() {
      const paramsT = {
        "current": this.current,
        "map": {},
        "model": {
          "organId": this.organId,
          "doctorId": this.myDoctorId
        },
        "order": "descending",
        "size": 20,
        "sort": "id"
      }
      Api.getappointDoctor(paramsT).then((lt) => {
        console.log(lt)
        if (lt.data.code === 0) {
          this.list = [...this.list, ...lt.data.data.records];
          // console.log(this.list)
          console.log()
          if (this.list.length < lt.data.data.total) {
            this.current++;
            this.getDoctorList();
          }
        }
      })
    },
    goitem(k) {
      const params = {id: k.id}
      this.$router.push({
        path: '/reservation/doctorIndex',
        query: {id: k.id}
      })

    },
    mysubmit() {
      // const params = {id: k.id}
      this.$router.push('/reservation/myreservation')

    },
  }
}
</script>
<style lang="less" scoped>
.headerContent {
  width: 100%;
  margin: 0px;
  padding: 0px;
  line-height: 0;

  img {
    width: 100%;
    margin: 0px;
    padding: 0px;
  }
}

.mybtn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 80px;
  height: 80px;
  // padding: 10px;
  border-radius: 50%;
  // background: #fff;
  // border:1px solid #f5f5f5;
  // text-align: center;
  // line-height: 20px;
  // color: #666666;
  // font-size: 15px;
  // box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  z-index: 9999;
}
</style>
