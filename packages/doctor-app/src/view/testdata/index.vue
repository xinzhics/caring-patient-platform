<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">{{pageTitle ? pageTitle : '监测数据'}}</x-header>

    <div style="padding-top: 50px; padding-bottom: 10px;">
    <div v-for="(item, index) in list" :key="index">
      <div class="item" @click="detailBtn(item, index)">
        <div>
          <p class="title">{{item.name}}</p>
          <p class="content">{{item.planDesc}}</p>
          <x-button class="watchBtn" mini>查看</x-button>
        </div>

        <div class="icon" style="width: 170px; height: 170px">
          <div style="position: relative;">
            <van-image width="170" height="170" fit="cover"
                       :src="require('@/assets/my/test_data_bj.png')"/>
            <van-image fit="cover" width="45px" height="45px"
                       style="position: absolute; right: 0; bottom: 0; margin-right: 25px; margin-bottom: 25px;"
                       :src="item.planIcon ? item.planIcon : item.planType === 1 ? require('@/assets/my/test_data_xueya.png') :  item.planType === 2 ?
         require('@/assets/my/test_data_xuetang.png') : require('@/assets/my/test_data_other.png')"/>
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
    name: "index",
    data() {
      return {
        pageTitle: localStorage.getItem('pageTitle'),
        list: [],
      }
    },
    created() {
      Api.getPatientMonitoringDataPlan()
        .then(res => {
          if (res.data && res.data.code === 0) {
            this.list = res.data.data
          }
        })
    },
    methods: {
      detailBtn(item ,index) {
        if (item.planType === 1) {//血压监测
          this.$router.push({path: '/monitor/pressure', query: {planId: item.id}})
        }else if(item.planType === 2) {//血糖监测
          this.$router.push('/monitor/glucose')
        }else {
          this.$router.push({path: '/monitor/show', query: {id: item.id, title: item.name}})
        }

      }
    }
  }
</script>

<style lang="less" scoped>

  .item {
    width: calc(84vw);
    padding: 4vw;
    border-radius: 5px;
    margin: 4vw;
    background: #fff;
    position: relative;
    overflow: hidden;

    .title {
      font-size: 17px;
      font-weight: 600;
      margin-bottom: 15px;
      font-weight: 600;
      color: #333333;
    }

    .content {
      width: 80%;
      font-size: 13px;
      color: #666666;
      // float: left;
    }

    .watchBtn {
      margin-top: 30px;
      // text-align: left;
      width: 80px;
      margin-left: 0px;
      margin-right: 0px;
      background: #fff;
      border: 1px solid rgba(102, 102, 102, 0.6);
      color: #666;
    }

    .icon {
      position: absolute;
      bottom: 0px;
      right: 0px;
      width: 46%;
      text-align: right;
      // float: right;
    }
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
