<template>
  <div>
    <van-overlay :show="show">
      <div class="wrapper">
        <van-loading type="spinner" color="#FFF" size="60px"/>
      </div>
    </van-overlay>
  </div>
</template>

<script>
  export default {
    name: "ChrysanthemumLoading",
    props: {
      show: false
    },
  }
</script>

<style scoped>
  .wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }

  .block {
    width: 120px;
    height: 120px;
  }
</style>
