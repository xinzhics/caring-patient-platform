<template>
  <div class="dialog" v-if="isVisible" @click.stop="isVisible = false">
    <div class="content-box" @click.stop="">
      <div style="font-weight: 600; font-size: 18px; color: #000000; display: flex; justify-content: center; margin-top: 15px">评论</div>

      <div style="padding: 15px">
        <van-field
          v-model="comment"
          rows="5"
          :autosize="{ maxHeight: 150, minHeight: 100 }"
          style="background: #F7F7F7; border-radius: 8px; font-size: 14px"
          type="textarea"
          placeholder="请输入留言"
        />

        <div style="display: flex; align-items: center; justify-content: space-between; margin-top: 25px; margin-bottom: 5px">
          <div class="btn1" @click="isVisible = false">取消</div>
          <div class="btn2" @click="submit">确认</div>
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
      comment: '',
    }
  },
  methods: {
    open() {
      this.comment = '';
      this.isVisible = true;
    },
    close() {
      this.isVisible = false;
    },
    submit() {
      if (!this.comment) {
        this.$toast('请输入评论内容')
        return;
      }
      this.$emit('submit', this.comment);
      this.close();
      this.comment = '';
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

.btn1 {
  border-radius: 39px;
  border: 1px solid #24C789;
  font-size: 14px;
  color: #03B668;
  height: 45px;
  width: 48%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn2 {
  border-radius: 39px;
  background: #24C789;
  font-size: 14px;
  color: #FFF;
  height: 45px;
  width: 48%;
  display: flex;
  align-items: center;
  justify-content: center;
}

</style>
