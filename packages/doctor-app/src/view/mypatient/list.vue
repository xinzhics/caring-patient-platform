<template>
  <section>
    <x-header :left-options="{backText: ''}" style="margin-bottom:0px!important;">
      {{ title }}
    </x-header>

    <div>
      <input style="width: calc(94% - 24px);margin: 10px 3% 0px;border: none;padding:4px 12px;border-radius: 5px;"
             @keyup.13="getInfo" @keyup.native="getInfo" v-model="patientName" placeholder="搜索"/>
      <group class="myheader">
        <cell v-for="(i,k) in alldata" :key="k+'z'" is-link @click.native="itemClick(i)">
          <div slot="title">
            <img v-if="i.avatar" :src="i.avatar ? i.avatar : ''" alt=""
                 style="width:2.2rem;height:2.2rem;vertical-align: middle; border-radius: 50%">
            <img v-else src="../../components/arrt/images/head-portrait.png" alt=""
                 style="width:2.2rem;height:2.2rem;vertical-align: middle;">
            {{ doctorId === i.doctorId && i.doctorRemark ? i.name + '('+i.doctorRemark + ')': i.name }}
          </div>
        </cell>
      </group>
    </div>
  </section>
</template>
<script>
import Api from '@/api/doctor.js'
import {Icon} from 'vux'

export default {
  components: {Icon},
  data() {
    return {
      alldata: [],
      doctorId: localStorage.getItem('caring_doctor_id'),
      patientName: '',
      title: this.$getDictItem('patient') + '列表',//患者列表
      params: {
        model: {
          diagnosisId: '',
          dimension: '',
          doctorId: '',
          status: 0,
          name: '',
        },
        current: 1,
        size: 50,
        patient: this.$getDictItem('patient')
      }
    }
  },
  created() {
    console.log(this.$route.query)
    if (this.$route.query) {
      if (this.$route.query.title) {
        this.title = this.$route.query.title
      }
      this.params.model.doctorId = this.$route.query.doctorId
      if (this.$route.query.tagId) {
        this.params.model.tagId = this.$route.query.tagId
      }
      this.params.model.dimension = this.$route.query.dimension
      this.params.model.status = this.$route.query.status
      this.params.model.diagnosisId = this.$route.query.diagnosisId
    }
    this.getInfo()
  },
  methods: {
    //聊天条目点击跳转
    itemClick(item) {
      const userName = localStorage.getItem("userImAccount");
      const password = "123456";
      let options = {
        apiUrl: WebIM.config.apiURL,
        user: userName,
        pwd: password,
        appKey: WebIM.config.appkey
      };
      WebIM.conn.open(options);
      Vue.$store.commit("updateDoctorMessageList", {
        receiverImAccount: item.imAccount,
      });
      Vue.$store.commit("clearMessageList", {});
      this.$router.push({path: '/im/index', query: {imAccount: item.imAccount, imPatientId: item.id}})
      //
      //this.$router.push({path: '/massSend/edit', query: {receiverImAccount: item.receiverImAccount, userId: item.patientId}})
    },
    getInfo() {
      this.params.model.name = this.patientName
      Api.postquery(this.params).then((res) => {
        if (res.data.code === 0) {
          this.alldata = res.data.data.records
        }
      })
    },
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

