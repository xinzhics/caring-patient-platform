<template>
  <div class="dialog" v-if="isVisible" @click.stop="isVisible = false">
    <div class="content-box" @click.stop="">
      <div style="display: flex; justify-content: center; margin-top: -25px">
        <van-image
          width="80px"
          height="80px"
          :src="require('@/assets/my/system-tip-del.png')"
        />

      </div>

      <div style="font-weight: 600; font-size: 18px; color: #000000; display: flex; justify-content: center; margin-top: 15px">温馨提示</div>

      <div style="padding: 15px">
        <div style="font-size: 15px; color: #343532; width: 100%; text-align: center">{{comment}}</div>
        <div style="display: flex; align-items: center; justify-content: center; margin-top: 35px; margin-bottom: 5px">
          <div class="btn2" @click="close">我知道了</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import Vue from 'vue';
import { Button } from 'vant';

Vue.use(Button);

export default {
  name: "index",
  data() {
    return {
      isVisible: false,
      comment: '该条信息已删除',

    }
  },
  methods: {
    open(comment) {
      this.isVisible = true;
      if (comment){
        this.comment = comment
      }
    },
    close() {
      this.isVisible = false;
    }
  }
}
</script>

<style lang="less" scoped>

.dialog {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  z-index: 1001;
}

.content-box {
  width: 85%;
  min-height: 200px;
  background: linear-gradient( 180deg, #E0FFF1 0%, #FFFFFF 30%, #FFFFFF 100%), #FFFFFF;
  box-shadow: 4px 10px 18px 0px rgba(16, 81, 218, 0.12);
  border-radius: 12px;
  position: absolute;
  top: 28%;
}

.btn2 {
  border-radius: 39px;
  background: #24C789;
  font-size: 14px;
  color: #FFF;
  height: 45px;
  width: 60%;
  display: flex;
  align-items: center;
  justify-content: center;
}

</style>
