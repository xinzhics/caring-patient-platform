<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">系统消息</x-header>
    <div style="padding-top: 50px">
      <van-tabs @click="onTabClick" offset-top="50px" title-active-color="#03B668" color="#03B668"
                 title-inactive-color="#626463"  v-model="active" sticky>
        <van-tab>
          <template slot="title">
            <div style="font-weight: 600; font-size: 18px; display: flex; align-items: flex-start">待办提醒
              <van-tag v-if="doctorCountMessage > 0" round type="primary" color="#F74F51"
                       style="font-size: 10px; min-width: 10px; min-height: 10px; display: flex; align-items: center; justify-content: center">{{doctorCountMessage}}</van-tag>
            </div>
          </template>
          <todo @docCountMessage="doctorCountMsg" @doctorRefresh="docCountMessage()"/>
        </van-tab>
        <van-tab>
          <template slot="title">
            <div style="font-weight: 600; font-size: 18px; display: flex; align-items: flex-start">消息通知
              <van-tag v-if="patientCountMessage > 0" round type="primary" color="#F74F51"
                       style="font-size: 10px; min-width: 10px; min-height: 10px; display: flex; align-items: center; justify-content: center">{{patientCountMessage}}</van-tag>
            </div>
          </template>
          <dynamic @patCountMsg="patCountMsg" @patientRefresh="patCountMessage()"/>
        </van-tab>
      </van-tabs>
    </div>
  </section>
</template>

<script>
import Vue from 'vue';
import { Tab, Tabs, Tag } from 'vant';
import dynamic from "@/components/systemMessage/dynamics";
import todo from "@/components/systemMessage/toDoList";
import doctorApi from "@/api/doctor"

Vue.use(Tab);
Vue.use(Tabs);
Vue.use(Tag);
export default {
  name: "index",
  components: {
    dynamic,
    todo
  },
  data() {
    return {
      active: 0,
      doctorCountMessage: 0,
      patientCountMessage: 0,
    }
  },
  mounted() {
    this.docCountMessage();
    this.patCountMessage();
  },
  methods: {
    onTabClick() {
    },
    docCountMessage() {
      doctorApi.doctorCountMessage({doctorId: localStorage.getItem('caring_doctor_id')})
        .then(res => {
          this.doctorCountMessage = res.data.data;
      })
    },
    patCountMessage() {
      doctorApi.patientCountMessage({doctorId: localStorage.getItem('caring_doctor_id')})
        .then(res => {
          this.patientCountMessage = res.data.data;
        })
    },
    doctorCountMsg() {
      this.doctorCountMessage = this.doctorCountMessage - 1;
    },
    patCountMsg() {
      this.patientCountMessage = this.patientCountMessage - 1;
    }
  }
}
</script>

<style lang="less" scoped>

.titleClass {
  height: 100px;
  font-weight: 600;
  font-size: 18px;
  color: #03B668;
}

.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;
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
