<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">{{ patient }}统计</x-header>
    <div style="padding-top: 50px">
      <div class="titleBar">
        <van-button :class="type == 1 ? 'chooseButton' : 'normalButton'" size="small"
                    @click="titleClick(1)">基础
        </van-button>
        <van-button :class="type == 2 ? 'chooseButton' : 'normalButton'" size="small"
                    @click="titleClick(2)">疾病
        </van-button>
      </div>
      <basics v-show="type == 1"/>
      <disease v-show="type == 2" ref="disease"/>
    </div>
  </section>
</template>
<script>
import basics from "./basics";
import disease from "./disease";

export default {
  name: 'index',
  components: {
    basics,
    disease,
  },
  data() {
    return {
      type: 1,
      patient: this.$getDictItem('patient'),
    }
  },
  created() {
    this.type = 1
  },
  methods: {
    titleClick(value) {
      if (this.type===value){
        return
      }else{
        this.type = value
        if (value == 2) {
          this.$refs.disease.tenantStatisticsChartList();
        }
      }
    }
  }
}
</script>
<style lang="less" scoped>
.allContent {
  width: 100vw;
  height: 100vh;
  background: #fafafa;

  .titleBar {
    background: white;
    display: flex;
    justify-content: center;
    align-items: center;
    padding-top: 12px;
    padding-bottom: 12px;

    .normalButton {
      border: #d9d9d9 solid 1px;
      background: white;
      width: 60px;
      color: #333333;
    }

    .chooseButton {
      border: #337EFF solid 1px;
      background: white;
      width: 60px;
      color: #337EFF;
    }
  }
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
