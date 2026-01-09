<template>
  <section>
    <x-header :left-options="{showBack: false}">微信授权</x-header>
    <div style="width: 100%; height: 100%; background: white">
      <!-- 通用错误 -->
      <van-empty image="error" description="请重新扫码关注。" />
    </div>
  </section>
</template>


<script>

  import wx from 'weixin-js-sdk';

  export default {
    name: "index",
    mounted() {
      setTimeout(function () {
        wx.closeWindow();
      }, 5000)
    }

  }
</script>

<style scoped>

</style>
