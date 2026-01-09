<template>
  <section class="main" style="width: 100vw;">
    <navBar @toHistoryPage="$router.push('/medicine/history')"
            :rightIcon="dateImg" :showRightIcon="true"
            :pageTitle="pageTitle !== undefined ? pageTitle : '用药日历'"
            :backUrl="backUrl"/>
    <van-tabs v-model="active" :color="'#40BD54'" :title-active-color="'#40BD54'">
      <van-tab title="用药日历">
        <calendar />
      </van-tab>
      <van-tab title="我的药箱">
        <medication/>
      </van-tab>
    </van-tabs>
  </section>
</template>
<script>
import Vue from 'vue';
import { Tab, Tabs } from 'vant';
import calendar from "./component/calendar.vue";
import medication from "./component/medication.vue";

Vue.use(Tab);
Vue.use(Tabs);
export default {

  components: {
    calendar,
    medication,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      headerImg: require('@/assets/my/yiyaoxiang.png'),
      dateImg: require('@/assets/my/historyMedicine.png'),
      pageTitle: '',
      active: 0,
      backUrl: '/home'
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = '/im/index'
    }
    if (this.$route.query && this.$route.query.type) {
      this.active = Number(this.$route.query.type)
    }
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  }
}
</script>
<style lang="less" scoped>

</style>
