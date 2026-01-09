<template>
  <div>
    <x-header :left-options="{ backText: '', }" @on-click-back="windowClose">{{name}}申请</x-header>
    <div class="image1">
      <div class="img1">
        <img src="../../assets/submitted.png" alt="">
      </div>

    </div>
    <div style="background-color: #fff; height:61vh">
      <div class="message">
        您的信息已提交成功
      </div>
      <div class="wite">
        请等待审核
      </div>
      <div style="margin-top:4rem; height:6rem; ">
        <van-button round block type="info" native-type="submit" @click="close">
          我知道了
        </van-button>
      </div>
    </div>

  </div>

</template>

<script>
import wx from 'weixin-js-sdk'
export default {
  data(){
    return{
      name: this.$getDictItem('doctor')
    }
  },
  mounted(){
  this.projectInfo = JSON.parse(localStorage.getItem('projectInfo'))
      // var varperson = JSON.parse(localStorage.getItem("dictionaryItem"))
      // window.dictionaryItem = new Map()
      // for (let index = 0; index < varperson.length; index++) {
      //   window.dictionaryItem.set(varperson[index].code, varperson[index].name)
      // }
      // if (window.dictionaryItem) {
      //   this.name = window.dictionaryItem.get('doctor')
      // }
  },
  methods: {
    windowClose() {
      wx.closeWindow()
    },
    close() {
      this.windowClose()
    }
  }
}
</script>

<style lang="less" scoped>
.message {
  width: 100%;
  text-align: center;
  font-weight: 500;
  font-size: 1.3rem;
}
.wite {
  width: 100%;
  text-align: center;
  margin-top: 0.5rem;
  color: #bfbcbf;
  font-size: 0.8rem;
}
.image1 {
  background-color: #fff;
}
.img1 {
  width: 50%;
  margin: 0 auto;
  margin-top: 1rem;
  background-color: #fff;
  img {
    width: 100%;
  }
}
/deep/.van-button--normal {
  width: 50%;
  margin-bottom: 2rem;
  margin: 0 auto;
}
</style>
