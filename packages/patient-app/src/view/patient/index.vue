<template>
  <section class="content">
    <div>
      <first-page ref="first" v-if="active === 0"></first-page>
      <message ref="message" v-if="active === 1" @setCount="setCount"></message>
      <mine ref="mine" v-if="active === 2"></mine>
    </div>
    <van-tabbar v-model="active" @change="onChange" :placeholder="true" active-color="#67E0A7">
      <van-tabbar-item>
        首页
        <template slot="icon">
          <van-image
              width="20"
              height="20"
              :src="active === 0 ? require('@/assets/my/patient_home_select.png') : require('@/assets/my/patient_home_normal.png')"
          />
        </template>
      </van-tabbar-item>
      <van-tabbar-item icon="search" v-if="count === 0">
        消息
        <template slot="icon">
          <van-image
              width="20"
              height="20"
              :src="active === 1 ? require('@/assets/my/patient_message_select.png') : require('@/assets/my/patient_message_normal.png')"
          />
        </template>
      </van-tabbar-item>
      <van-tabbar-item icon="search" v-else :badge="count > 99 ? '99+' : count">
        消息
        <template slot="icon">
          <van-image
              width="20"
              height="20"
              :src="active === 1 ? require('@/assets/my/patient_message_select.png') : require('@/assets/my/patient_message_normal.png')"
          />
        </template>
      </van-tabbar-item>
      <van-tabbar-item icon="friends-o">
        我的
        <template slot="icon">
          <van-image
              width="20"
              height="20"
              :src="active === 2 ? require('@/assets/my/patient_mine_select.png') : require('@/assets/my/patient_mine_normal.png')"
          />
        </template>
      </van-tabbar-item>
    </van-tabbar>
  </section>
</template>

<script>

import Vue from 'vue';
import {Tabbar, TabbarItem} from 'vant';

Vue.use(Tabbar);
Vue.use(TabbarItem);
import firstPage from "./components/home/firstPage";
import message from "./components/home/message";
import mine from "./components/home/mine";
import ContentApi from "../../api/Content";
import wx from "weixin-js-sdk";
import Api from '@/api/Content.js'
import {mapGetters} from "vuex";

export default {
  name: "index",
  components: {
    firstPage,
    message,
    mine,
  },
  data() {
    return {
      active: -1,
      count: 0,
    }
  },
  computed: {
    ...mapGetters({
      updateMessage: "onUpdateMessage",
    }),
  },
  watch: {
    updateMessage: function (val, old) {
      this.getCountMessage()
    }
  },
  mounted() {
    this.getCountMessage()
    setTimeout(() => {
      this.getPatientInfo()
    }, 200)
    // 替换当前历史状态，清除浏览器历史记录
    window.history.replaceState({}, document.title, window.location.href);
    let editquestion = localStorage.getItem('editquestion')
    // 如果从基本信息页进入的，则需要延时加载页面
    if (editquestion) {
      localStorage.removeItem('editquestion')
      setTimeout(() => {
        this.active = this.$store.getters.getPageActive
      }, 800)
    } else {
      this.active = this.$store.getters.getPageActive
    }
  },
  methods: {
    // 获取患者im状态，以及im账号，登录im账号
    getPatientInfo() {
      if (!localStorage.getItem('userId')) {
        return
      }
      Api.getContent({ id: localStorage.getItem('userId') }).then((res) => {
        const userName = res.data.data.imAccount;
        const password = "123456";
        var options = {
          apiUrl: WebIM.config.apiURL,
          user: userName,
          pwd: password,
          appKey: WebIM.config.appkey
        };
        WebIM.conn.open(options);
      })
    },
    closeWXWindow() {
      wx.closeWindow()
    },
    setCount(val) {
      this.count = val
    },
    // 导航栏切换监听
    onChange(index) {
      this.$store.commit('setPageActive', {pageActive: index});
    },
    getCountMessage() {
      ContentApi.getCountMessage()
          .then(res => {
            this.count = res.data.data
          })
    },
  },
}
</script>

<style lang="less" scoped>

.content {
  height: 100vh;
  width: 100vw;
  background: #f7f7f7;
}

</style>
