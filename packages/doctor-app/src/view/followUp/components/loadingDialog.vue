<template>
  <div>
    <van-dialog v-model:show="show" width="120px" :showConfirmButton="false" :showCancelButton="false">
      <div style="height: 120px; width: 120px; display: flex; justify-content: center; flex-direction: column; align-items: center">
        <van-loading type="spinner" color="#1989fa" vertical text-color="#666666" >加载中...</van-loading>
      </div>
    </van-dialog>
  </div>
</template>

<script>

import {Dialog, Loading} from 'vant';
import Vue from "vue";
Vue.use(Dialog);
Vue.use(Loading);

export default {
  name: "loadingDialog",
  data() {
    return {
      show: true,
    }
  },
  methods: {
    //打开loading
    openLoading() {
      this.show = true
    },
    //关闭loading
    cancelLoading() {
      this.show = false
    },
  }
}
</script>

<style scoped>

</style>
