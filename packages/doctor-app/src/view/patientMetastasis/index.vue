<template>
  <section class="allContent">
    <x-header style="margin-bottom:0px !important" :left-options="{backText: '', preventGoBack: true}" @on-click-back="back">{{patient}}转移
      <a slot="right" v-if="!show" @click="scanBtn" class="fa fa-exist">
        <img :src="headerIcon" width='20px' alt="">
      </a>
    </x-header>
    <div v-if="show" class="cover">
      <div class="label">
        <p @click="status=true" :class="status?'active':''">全部</p>
        <p @click="status=false" :class="status?'':'active'">按状态</p>
      </div>
      <div class="docs">
        <div class="group">默认</div>
        <div class="btnCont">
          <div :class="btnstatus == 6?'active':''" @click="selectBtn(6)">全部</div>
        </div>
        <div class="group">按状态</div>
        <div class="btnCont">
          <div :class="btnstatus==0?'active':''" @click="selectBtn(0)">未接收</div>
          <div :class="btnstatus==1?'active':''" @click="selectBtn(1)">已接收</div>
          <div :class="btnstatus==2?'active':''" @click="selectBtn(2)">已取消</div>
        </div>
      </div>
    </div>
    <div class="contant">

      <div class="tabsCont">
        <div class="left" @click="transtBtn(1)">
          <p :class="transtCan?'itemP':''">发起转诊</p>
          <p :class="transtCan?'item':''"></p>
        </div>
        <div class="right" @click="transtBtn(2)">
          <p :class="transtCan?'':'itemP'">接受转诊</p>
          <p :class="transtCan?'':'item'"></p>
        </div>
      </div>
      <div class="contentList">
        <launchList v-if="transtCan" :list="list" @getInfo="getList()" />
        <acceptList v-if="!transtCan" :list="acceptArray" @getInfo="acceptgetList()" />
      </div>
    </div>
    <div class="toBottomBtn">
      <div @click="$router.push('/patientMetastasis/transtPatient')">
        发起转诊
      </div>
    </div>
  </section>
</template>
<script>
  import {
    Swiper,
    SwiperItem,
    Drawer
  } from 'vux'
  import Api from '@/api/Content.js'
  import launchList from "./components/launchList";
  import acceptList from "./components/acceptList";

  export default {
    components: {
      Swiper,
      SwiperItem,
      Drawer,
      launchList,
      acceptList
    },
    data() {
      return {
        show: false,
        headerIcon: require('@/assets/my/screenIcon.png'),
        transtCan: true,
        status: true,
        btnstatus: 6,
        list: [],
        acceptArray: [],
        patient: this.$getDictItem('patient')

      }
    },
    mounted() {
      this.getList(6)
    },
    methods: {
      getList() {
        const params = {
          launchDoctorId: localStorage.getItem('caring_doctor_id'),
          referralStatus: this.btnstatus === 6 ? '' : (this.btnstatus === 0 ? this.btnstatus : (this.btnstatus ? this.btnstatus : ''))
        }
        Api.referralQuery(params).then((res) => {
          if (res.data.code === 0) {
            this.list = res.data.data
          } else {
            this.$vux.toast.text(res.data.msg, 'center')
          }
        })
      },
      back() {
        if (this.show) {
          this.show = false
        } else {
          this.$router.back()
        }
      },
      acceptgetList() {
        const params = {
          acceptDoctorId: localStorage.getItem('caring_doctor_id'),
          referralStatus: this.btnstatus == 6 ? '' : (this.btnstatus === 0 ? this.btnstatus : (this.btnstatus ? this.btnstatus : ''))
        }
        Api.referralQuery(params).then((res) => {
          if (res.data.code === 0) {
            console.log(res.data.data)
            this.acceptArray = res.data.data
          }
        })
      },
      selectBtn(i) {
        this.btnstatus = i
        this.show = false
        if (this.transtCan) {
          this.list = []
          this.getList()
        } else {
          this.acceptArray = []
          this.acceptgetList()
        }
      },
      scanBtn() {
        this.show = true
      },
      transtBtn(k) {
        if (k == 1) {
          this.transtCan = true
        } else {
          this.transtCan = false
        }
        if (this.transtCan) {
          this.list = []
          this.getList()
        } else {
          this.acceptArray = []
          this.acceptgetList()
        }


      }
    }
  }

</script>
<style lang="less" scoped>
  .allContent {
    .toBottomBtn {
      position: fixed;
      bottom: 0;
      padding: 30px 0px;
      width: 100%;

      div {
        width: 60%;
        background: #3F86FF;
        text-align: center;
        color: #fff;
        margin: 0px auto;
        border-radius: 60px;
        padding: 10px 0px;
      }
    }

    .bottomBtn {
      padding: 30px 0px;

      div {
        width: 60%;
        background: #3F86FF;
        text-align: center;
        color: #fff;
        margin: 0px auto;
        border-radius: 60px;
        padding: 10px 0px;
      }

    }

    .cover {
      width: 100%;
      height: 100%;
      position: fixed;
      z-index: 9;
      display: flex;

      .label {
        width: 30%;
        background: #f5f5f5;

        p {
          line-height: 40px;
          padding-left: 10px;
          font-size: 14px;
          color: #666666;
        }

        .active {
          background: #fff;
          color: #333333
        }
      }

      .docs {
        width: 70%;
        background: #fff;
        padding: 10px;

        .group {
          font-size: 12px;
          color: #999;

        }

        .btnCont {
          padding: 5px 0px;

          div {
            font-size: 13px;
            width: 20%;
            display: inline-block;
            text-align: center;
            padding: 5px 20px;
            line-height: 20px;
            border: 1px solid #B8B8B8;
            color: #666666;
            border-radius: 30px;
            margin: 0px 10% 10px 0px;
          }

          .active {
            color: #3F86FF;
            border: 1px solid #3F86FF;
          }
        }
      }
    }

    .contant {

      .tabsCont {
        display: flex;
        justify-content: space-between;
        background: #fff;
        text-align: center;
        font-size: 14px;
        padding: 5px 0px 2px;
        line-height: 30px;
        color: #666666;

        .left {
          width: 50%;

          .itemP {
            color: #3F86FF;
          }

          .item {
            width: 30px;
            height: 4px;
            border-radius: 4px;
            background: #3F86FF;
            margin: auto;
          }
        }

        .right {
          width: 50%;

          .itemP {
            color: #3F86FF;
          }

          .item {
            width: 30px;
            height: 4px;
            border-radius: 4px;
            background: #3F86FF;
            margin: auto;
          }
        }
      }
    }
  }

</style>
