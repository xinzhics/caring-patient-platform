<template>
  <section class="main">
    <div class="allget" v-if="status==2">
        <img src="@/assets/my/gotBj.png" alt="">
        <x-button style="width:70%;margin-top:40px;color:#fff;background:#3F86FF!important;font-size:16px;padding:8px 0px" @click.native="submit">查看{{patient}}档案</x-button>
        <div class="content">
            <p>
                <span class="label">{{patient}}姓名：</span> <span class="docs">{{allData.patientName}}</span>
            </p>
            <p>
                <span class="label">发起时间：</span> <span class="docs">{{allData.launchTime}}</span>
            </p>
            <p>
                <span class="label">发起{{name}}：</span><span class="docs">{{allData.launchDoctorName}}</span>
            </p>
            <p>
                <span class="label">转诊性质：</span> <span class="docs">{{allData.referralCategory==0?'单次转诊':'长期转诊'}}</span>
            </p>
        </div>
    </div>
    <div class="cencel" v-if="status==3">
        <img src="@/assets/my/gottranst.png" alt="">
        <div class="text">
            <p>该转诊码已被接收</p>
            <p>或转诊已取消</p>
        </div>
    </div>
    <!-- <x-button style="width:40%;margin-top:40px;color:#fff;background:#66728C!important" @click.native="submit">提交
    </x-button> -->
  </section>
</template>
<script>
  import Api from '@/api/Content.js'

  export default {
    data() {
      return {
          name: this.$getDictItem('doctor'),
          status:'',
        // headerimg:require('@/assets/my/remindImg.png'),
        value: '',
        allData: {
          subscribeList: []
        },
        patient: this.$getDictItem('patient'),
        pageTitle: localStorage.getItem('pageTitle'),
        myName: ['用药提醒', '血压监测', '血糖监测', '复查提醒', '健康日志'],
        styleData:JSON.parse(localStorage.getItem('styleDate'))
      }
    },
    mounted() {
          // var varperson =JSON.parse(localStorage.getItem("dictionaryItem"))
      // window.dictionaryItem = new Map()
      //     for (let index = 0; index < varperson.length; index++) {
      //       window.dictionaryItem.set(varperson[index].code, varperson[index].name)
      //     }
      this.getInfo()
      // if (window.dictionaryItem) {
      //   this.name = window.dictionaryItem.get('doctor')
      //   this.patient = window.dictionaryItem.get('patient')
      // }

    },
    methods: {
        getInfo() {
            const that = this
            const params = {
                acceptDoctorId:that.getQueryVariable('acceptDoctorId'),
                id: that.getQueryVariable('id')
            }
            Api.referral({id:that.getQueryVariable('id')}).then((resT) => {
                // that.$vux.toast.text(resT,'cneter')
                if (resT.data.code === 0) {
                    that.allData = resT.data.data
                    if(resT.data.data.referralStatus===0){
                        Api.acceptReferral(params).then((res) => {
                            if (res.data.code == 0) {
                                that.status = 2
                            }else{
                                that.$vux.toast.text(res.data.msg,'cneter')
                            }
                        })

                    }else{
                        that.status = 3
                    }

                }
            })

        },
        getQueryVariable(variable){
            var query = window.location.search.substring(1);
            var vars = query.split("&");
            for (var i=0;i<vars.length;i++) {
                    var pair = vars[i].split("=");
                    if(pair[0] == variable){return pair[1];}
            }
            return(false);
        },
      changeBtn(i, k) {
        this.$set(this.allData.subscribeList, k, i)
      },
      submit() {
        if (this.allData&&this.allData.patientId) {
          this.$router.push({path: '/mypatientHome', query: {id: this.allData.patientId}})
        }
      }
    }
  }
</script>
<style lang="less" scoped>
  .main {
    padding-bottom: 40px;
    .contentInner{
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
          .headerDocs{
              font-size: 13px;
              color: #fff;
              line-height: 24px;
              text-align: center;
          }
          .cont{
              width: 84vw;
              margin-top: 10px;
              background: #fff;
              border-radius: 16px;
              padding:20px 4vw;
              .userHeader{
                  display: flex;
                  justify-content: start;
                  padding-bottom: 10px;
                  border-bottom: 1px solid #EEEEEE;
                  img{
                      width: 50px;
                      height: 50px;
                      border-radius: 50%;
                  }
                  .right{
                      margin-left: 10px;
                      .name{
                          line-height: 30px;
                          font-size: 18px;
                          color: #666;
                      }
                      .detail{
                          line-height: 20px;
                          font-size: 13px;
                          color: #999;
                      }
                  }

              }
              .userContent{
                  .contentHeader,.text{
                      text-align: left;
                      color: #999999;
                      font-size: 13px;
                      line-height: 40px;
                  }
                  img{
                      width: 40vw;
                      margin: 50px auto;
                      display: block;
                  }
                  .text{
                      text-align: center;
                  }
              }
          }
        }

    }
    .cencel{
        img{
            width: 30vw;
            display: block;
            padding: 10px;
            margin: 80px auto 20px;
            background: #fff;
            text-align: center;
        }
        .text{
            text-align: center;
            font-size: 14px;
            color: #999;
        }
    }
    .allget{
        img{
            width: 100vw;
        }
        .content{
            position: absolute;
            top: 74vw;
            left: 12vw;
            width: 68vw;
            // background: #f5f5f5;
            padding: 4vw;
            border-radius: 4px;
            p{
                line-height: 30px;
                .label{
                    font-size: 14px;
                    color: #999;
                }
                .docs{
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
