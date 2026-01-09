<template>
  <section class="main" v-if="oneQuestionPage!==1">
    <div class="content" v-if="oneQuestionPage!==1" v-html="mainData"></div>
    <div class="setbottom" v-if="oneQuestionPage!==1">
      <div class="header">
        <input class="checkBox" type="checkbox" name="已阅读并同意《服务协议》" :checked="isSelect" @change="changeSelect"
               style="vertical-align: middle;" id="">
        <span>已阅读并同意<span class="tip" @click="tomyshow()">《服务协议》</span></span>
      </div>
      <x-button :style="{width:'60%',color:'#fff',backgroundColor:isSelect?'#66728C':'#dbdbdb'}" @click.native="goItem">
        确定
      </x-button>
    </div>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import wx from "weixin-js-sdk";

export default {
  data() {
    return {
      alldata: {},
      mainData: '',
      show: false,
      isSelect: false,
      oneQuestionPage: 1,
      formHome: localStorage.getItem('formHome'),
      patientId: localStorage.getItem('userId')
    }
  },
  created() {
    this.getInfo()
  },
  mounted() {

  },
  methods: {
    getInfo() {
      if (this.formHome === 'true') {
        wx.closeWindow()
      }
      Api.regGuidegetGuide({}).then((res) => {
        if (res.data.code === 0) {
          localStorage.setItem('statusAll', JSON.stringify(res.data.data))
          if (res.data.data.enableIntro === 0) {
            this.alldata = res.data.data
            this.mainData = res.data.data.intro
            this.oneQuestionPage = 0
          } else {
            this.getchek()
          }
        }
      })
    },
    goItem() {
      if (this.isSelect) {
        Api.agreeAgreement(this.patientId).then(res => {
          if (res.data.data) {
            Api.getFormIntoTheGroup('BASE_INFO').then(ress => {
              if (ress.data.data === 1) {
                // 是一题一页
                this.$router.replace({path: '/questionnaire/editquestion', query: {status: 1, isGroup: 1}})
              } else {
                // 不是一题一页
                this.$router.replace({path: 'two'})
              }
            })
          }
        })
      } else {
        this.$vux.toast.text('请同意服务协议，以便进行后续操作！', 'center')
      }
    },
    getchek() {
      const params = {
        formEnum: 'BASE_INFO',
        patientId: localStorage.getItem('userId')
      }
      Api.getFormIntoTheGroup('BASE_INFO').then(ress => {
        if (ress.data.data === 1) {
          // 是一题一页
          this.$router.replace({path: '/questionnaire/editquestion', query: {status: 1, isGroup: 1}})
        } else {
          // 不是一题一页
          this.$router.replace({path: 'two'})
        }
      })
    },
    changeSelect() {
      this.isSelect = !this.isSelect
    },
    tomyshow() {
      this.$router.push('/shouintroduce')
    }
  },
  beforeRouteLeave(to, from, next) {
    if (to.path === '/home') {
      wx.closeWindow()
    } else {
      next()
    }
  }
}
</script>
<style lang="less" scoped>
@import '~vux/src/styles/close';

.main {
  .content {
    width: 100vw;
    padding: 0vw;
    padding-bottom: 110px;
    word-break: break-all;
    background-color: white;

    /deep/ img{
      max-width: 100%;
    }

    /deep/ .__logo__ img {
      width: 90% !important;
      height: auto !important;
      max-width: 120px;
      border-radius: 8px;
      max-height: 90% !important;
    }

    /deep/ .__title__ {
      margin-top: 8px;
      font-weight: 545;
      color: #ffffff;
      font-size: 20px;
    }

  }

  .setbottom {
    // position:f;
    position: fixed;
    left: 0;
    bottom: 0;
    height: 120px;
    width: 100vw;
    background: #fff;
    border-top: 1px solid rgba(102, 102, 102, 0.1);

    .header {
      width: 60%;
      margin: 10px auto 20px;

      span {
        .tip {
          color: #66728C;
        }
      }
    }
  }
}

.vux-close {
  margin-top: 8px;
  margin-bottom: 8px;
}

/deep/ .vux-header {
  height: 50px;
}
</style>
