<template>
  <section class="main">
    <navBar pageTitle="我的转诊卡" :closeWindow="true"></navBar>
    <div class="contentInner" v-if="allData&&allData.referralStatus==0">
      <img src="@/assets/my/scanbg.png" alt="" style="width:100%" srcset="">
      <div class="content">
        <div class="headerName">转诊卡</div>
        <p class="headerDocs">请出示给接诊{{name}}</p>
        <div class="cont">
          <div class="userHeader">
            <img :src="allData.patientAvatar" alt="">
            <div class="right">
              <p class="name">{{allData.patientName}}</p>
              <p class="detail">{{ allData.patientSex === null ? '' : allData.patientSex === 0 ? '男' : '女'}}
                {{ allData.patientSex !== null && allData.patientAge !== null ? '丨' : '' }}
                {{ allData.patientAge !== null ? allData.patientAge + '岁' : '' }}</p>
            </div>
          </div>
          <div class="userContent">
            <div class="contentHeader">接收{{name}}：{{allData.acceptDoctorName}} {{allData.acceptDoctorHospitalName && allData.acceptDoctorHospitalName.length > 0 ? "(" + allData.acceptDoctorHospitalName + ")" : "" }}
            </div>
            <img :src="allData.qrUrl" alt="">
            <div class="text">{{name}}微信扫码接收转诊</div>
          </div>
        </div>
      </div>
    </div>
    <div class="cencel" v-if="allData&&allData.referralStatus&&allData.referralStatus!=0">
      <img src="@/assets/my/gottranst.png" alt="">
      <div class="text">
        <p>该转诊码已被接收</p>
        <p>或转诊已取消</p>
      </div>
    </div>
  </section>
</template>
<script>
  import Api from '@/api/Content.js'
  export default {
    components: {
      navBar: () => import('@/components/headers/navBar'),
    },
    data() {
      return {
        status: 1,
        allData: {},
        myName: ['用药提醒', '血压监测', '血糖监测', '复查提醒', '健康日志'],
        name: ""
      }
    },
    mounted() {
      this.name = this.$getDictItem('doctor')
      this.getInfo()
    },
    methods: {
      getInfo() {
        console.log(this.getQueryVariable('id'))
        const params = {
          id: this.getQueryVariable('id')
        }
        Api.referral(params).then((res) => {
          if (res.data.code === 0) {
            this.allData = res.data.data
          }
        })
      },
      getQueryVariable(variable) {
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i = 0; i < vars.length; i++) {
          var pair = vars[i].split("=");
          if (pair[0] == variable) {
            return pair[1];
          }
        }
        return (false);
      },
    }
  }
</script>
<style lang="less" scoped>
  .main {
    padding-bottom: 40px;

    .contentInner {
      position: relative;

      .content {
        position: absolute;
        top: 0;
        left: 0;
        width: 92vw;
        // height: 90vh;
        padding: 20px 4vw;
        // background: #fff;

        .headerName {
          font-size: 22px;
          color: #fff;
          text-align: center;
        }

        .headerDocs {
          font-size: 13px;
          color: #fff;
          line-height: 24px;
          text-align: center;
        }

        .cont {
          width: 84vw;
          margin-top: 10px;
          background: #fff;
          border-radius: 16px;
          padding: 20px 4vw;

          .userHeader {
            display: flex;
            justify-content: start;
            padding-bottom: 10px;
            border-bottom: 1px solid #EEEEEE;

            img {
              width: 50px;
              height: 50px;
              border-radius: 50%;
            }

            .right {
              margin-left: 10px;

              .name {
                line-height: 30px;
                font-size: 18px;
                color: #666;
              }

              .detail {
                line-height: 20px;
                font-size: 13px;
                color: #999;
              }
            }

          }

          .userContent {
            .contentHeader, .text {
              text-align: left;
              color: #999999;
              font-size: 13px;
              line-height: 40px;
            }

            img {
              width: 40vw;
              margin: 50px auto;
              display: block;
            }

            .text {
              text-align: center;
            }
          }
        }
      }

    }

    .cencel {
      img {
        width: 30vw;
        display: block;
        padding: 10px;
        margin: 80px auto 20px;
        background: #fff;
        text-align: center;
      }

      .text {
        text-align: center;
        font-size: 14px;
        color: #999;
      }
    }

    .allget {
      img {
        width: 100vw;
      }

      .content {
        position: absolute;
        top: 88vw;
        left: 12vw;
        width: 68vw;
        background: #f5f5f5;
        padding: 4vw;
        border-radius: 4px;

        p {
          line-height: 30px;

          .label {
            font-size: 14px;
            color: #999;
          }

          .docs {
            font-size: 15px;
            color: #666;
          }
        }
      }
    }

  }

  /deep/ .vux-header {
    height: 50px;
  }

</style>
