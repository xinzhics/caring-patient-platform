<template>
<div class="main">
  <van-sticky>
    <headNavigation leftIcon="arrow-left" title="统计分析" @onBack="onBack"></headNavigation>
    <div style="background: #FFFFFF;border-top:1px solid #EBEBEB;border-bottom:1px solid #EBEBEB;z-index: 9999999999">
      <van-tabs color="#337EFF" v-model="active">
        <van-tab title="基础">
          <div style="background: #F5F5F5">
          </div>
        </van-tab>
        <van-tab title="疾病">
          <div style="background: #F5F5F5">
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </van-sticky>
  <basics v-if="active===0"></basics>
  <disease v-if="active===1"></disease>
</div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Sticky, Tab, Tabs, Icon} from 'vant'
import basics from './components/basics'
import disease from './components/disease'
Vue.use(Col)
Vue.use(Sticky)
Vue.use(Icon)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Row)
export default {
  name: 'statistics',
  components: {
    basics, disease
  },
  data () {
    return {
      active: 0
    }
  },
  methods: {
    onBack () {
      this.$h5Close()
    }
  }
}
</script>

<style scoped lang="less">
/deep/.van-tabs__wrap{
  .van-tab__pane{
    background: #F5F5F5;
  }
}

.main {
  ::-webkit-scrollbar {
    display: none !important;
    width: 0px !important;
    height: 0px !important;
  }
}
</style>
