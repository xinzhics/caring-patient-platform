<template>
  <div style="background: #F5F5F9 ;height: 100vh">
    <x-header @on-click-back="gobackpage" style="margin: 0 !important;" :left-options="{backText: '',preventGoBack:true}">{{title}}
      <div slot="right">
        <img :src="require('@/assets/my/updata.png')"
             @click="toUpDataList"
             width="20px" height="20px">
      </div>
    </x-header>

    <div>
      <group class="myheader">
        <cell v-for="(i,k) in alldata" :key="k+'z'" is-link @click.native="itemClick(i)">
          <div slot="title">
            <img v-if="i.avatar" :src="i.avatar ? i.avatar : ''" alt="" style="width:2.2rem;height:2.2rem;vertical-align: middle;">
            <img v-else src="../../components/arrt/images/head-portrait.png" alt="" style="width:2.2rem;height:2.2rem;vertical-align: middle;">
            {{ doctorId === i.doctorId && i.doctorRemark ? i.name + '('+i.doctorRemark + ')': i.name }}
          </div>
        </cell>
      </group>
    </div>
  </div>
</template>

<script>
import Api from '@/api/Content.js'
export default {
  data(){
    return{
      alldata:[],
      title:'',
      params:{
        "current": 1,
        "map": {},
        "model": {
          "doctorCustomGroupId": '',
          "doctorId": '',
        },
        "order": "descending",
        "size": 10,
        "sort": "id"
      },
      doctorId: localStorage.getItem('caring_doctor_id')
    }
  },
  mounted() {

    if(this.$route.query.id){
      this.params.model.doctorCustomGroupId=this.$route.query.id

    }
    if(this.$route.query.title){
      this.title=this.$route.query.title
    }
    this.params.model.doctorId=localStorage.getItem('caring_doctor_id')
    this.getPatientList()
  },
  methods:{
    gobackpage(){
      this.$router.push({
        path: '/mypatient',
      })
    },
    getPatientList(){
      Api.pageGroupPatient(this.params).then(res=>{
        console.log(res)
        if (res.data.code==0){
          this.alldata=res.data.data.records
        }
      })
    },
    itemClick(item) {
      const userName = localStorage.getItem("userImAccount");
      const password = "123456";
      let options = {
        apiUrl: WebIM.config.apiURL,
        user: userName,
        pwd: password,
        appKey: WebIM.config.appkey
      };
      //2026daxiong调试注释
    //WebIM.conn.open(options);WebIM.conn.open(options);
      Vue.$store.commit("updateDoctorMessageList", {
        receiverImAccount: item.imAccount,
      });
      Vue.$store.commit("clearMessageList", {});
      this.$router.push({path: '/im/index', query: {imAccount: item.imAccount, imPatientId: item.id}})
      //
      //this.$router.push({path: '/massSend/edit', query: {receiverImAccount: item.receiverImAccount, userId: item.patientId}})
    },
    toUpDataList(){
      this.$router.push({
        path: '/mypatient/addTeam',
        query:{
          id:this.$route.query.id,
          title:this.title
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
}

.myheader {
  .weui-cells__title {
    line-height: 20px;
  }
}

.slide {
  width: 100vw;
  position: absolute;
  z-index: 10;
  background: rgba(0, 0, 0, 0.5);
  overflow: hidden;
  max-height: 0;
  transition: max-height .5s cubic-bezier(0, 1, 0, 1) -.1s;
  display: flex;
  justify-content: space-between;

  .itemTitle {
    background: #F7F7F7;
    text-align: left;
    width: 50vw;

    .item {
      padding: 10px 0px;
      width: 45vw;
      padding-left: 5vw;
      font-size: 14px;
    }

  }

  .itemInner {
    background: #fff;
    text-align: left;
    width: 45vw;
    padding-left: 5vw;

    .item {
      padding: 10px 0px 9px;
      font-size: 14px;
      border-bottom: 1px solid #DDDDDD;
      display: flex;
      justify-content: space-between;
    }
  }
}

.animate {
  height: 100vh;
  max-height: 9999px;
  transition-timing-function: cubic-bezier(0.5, 0, 1, 0);
  transition-delay: 0s;
}
</style>
