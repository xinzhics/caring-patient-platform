<template>
  <van-col span="24" class="attr-split-line">
    <div style="margin-top: 10px; margin-bottom: 10px;" :class="[field.dividerStyle === 'hairline' ? 'link-solid' : 'link-dashed', field.dividerWidth === 100 ? 'link-kuan100' : 'link-kuan80']"></div>
  </van-col>
</template>
<script>
export default {
  props: {
    field: {
      type: Object
    }
  },
  data: function () {
    return {
      // field: {
      //     dividerStyle: "hairline",
      //     dividerWidth: 100
      // }
    }
  }
}
</script>
<style lang="less" scoped>
.attr-split-line {
  background-color: #fff;
  // padding: 15px 0;
  .link-solid {
    height: 1px;
    border-top: solid #ebebeb 1px;
  }
  .link-kuan100 {
    width: 100%;
  }
  .link-kuan80 {
    margin-left:13px;
    margin-right: 20px;
  }
  .link-dashed {
    height: 1px;
    border-top: dashed #ebebeb 1px;
  }
}
</style>
