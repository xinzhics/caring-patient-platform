<template>
  <div
    class="contentbody"
    :style="`background-image:url(${info.posterUrl});background-size:${width}px ${height}px`"
  >
    <navBar :yaoQing="true" @toHistoryPage="goback" pageTitle="我的邀请"/>
    <div
      class="righttips"
      @click="toRule"
      v-if="info.activityRuleSwitch == 2"
      :style="`margin-left: ${width - 94}px;`"
    >
      <div  style="text-align: center;width: 100%;color: #fff">活动规则 <van-icon name="arrow" /></div>
    </div>
    <div
      v-if="timepass"
      class="timebox"
      :style="`margin-top:${(height - 245)/2}px;margin-left: ${
        (width - 140) / 2
      }px`"
    >
      <img src="../../assets/my/nodata.png"  />
      <p >活动已过期</p>
    </div>
    <div
      class="codeimg"
      :style="`margin-top:${info.activityRuleSwitch == 1?(height - 205-((width-158)/2)):(height - 274-((width-158)/2))}px;background-image:url(${info.url});margin-left: ${(width - 158) / 2}px;`"
      v-if="!timepass"
    ></div>
  </div>
</template>
<script >
// import appHeader from "@/components/headers/com-header";
import Api from "@/api/Content";
export default {
  components: {
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      info: {},
      height: 0,
      width: 0,
      src: "",
      timepass: false,
    };
  },
  mounted() {

    let id = localStorage.getItem("userId") || "";
    Api.generatePatientInvitationQRcode(id).then((res) => {
      console.log(res)
      if (res.data.code == 0) {
        this.height = window.innerHeight;
        this.width = window.innerWidth;
         this.info = res.data.data||{};
      }
    }).catch(()=>{

        this.height = window.innerHeight;
        this.width = window.innerWidth;
        this.timepass = true;
    });
  },
  methods: {
    toRule() {
      window.location.href = this.info.activityRuleUrl;
    },
    goback() {
      console.log(123)
      return
    }
  },
};
</script>
<style lang="less" scoped>
.contentbody {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  .righttips {
    width: 94px;
    height: 32px;
    background: rgba(0, 0, 0, 0.36);
    border-radius: 40px 0px 0px 40px;
    margin-top: 37px;
    color: red;
    font-size: 14px;
    display: flex;
    align-items: center;
    .cell {
      background-color: transparent;
      color: red;
      font-size: 14px;
      padding: 5px 7px 3px 13px !important;
      div {
        color: red;
      }
    }
  }
  .codeimg {
    width: 158px;
    height: 158px;
    background-color: white;
    background-size: cover;
  }
  .timebox{
    width: 140px;
    text-align: center;
    img{
width: 130px; height: 130px
    }
    p{
margin-top:10px;color:#9D9D9D;font-size: 18px;
    }
  }
}
</style>>
