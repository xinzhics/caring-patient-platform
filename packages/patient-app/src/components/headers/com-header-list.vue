<template>
  <div>
    <van-nav-bar
      :title="headerConfig.title"
      :left-text="headerConfig.left ? headerConfig.left.text :''"
      left-arrow
      class="com-back"
      @click-left="onClickLeft"
      @click-right="onClickRight"
      v-if="headerConfig.right && headerConfig.right.icon"
    >
      <svg class="icon svg-icon" aria-hidden="true" v-if="headerConfig.right.svg">
        <use :xlink:href="headerConfig.right.svg"></use>
      </svg>
      <i :class="headerConfig.right.icon" slot="right" v-if="headerConfig.right.icon"/>
    </van-nav-bar>
    <van-nav-bar
      :title="headerConfig.title"
      class="com-back"
      :left-text="headerConfig.left ? headerConfig.left.text :''"
      :right-text="headerConfig.right ? headerConfig.right.text :''"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
      v-else
    />
  </div>
</template>
<script>
export default {
  name: "com-header",
  props: ['headerConfig'],
  methods: {
    onClickLeft() {
      if (this.headerConfig.left && this.headerConfig.left.todo) {
        this.headerConfig.left.todo();
      } else {
        this.$router.back();
      }
    },
    onClickRight() {
      console.log(this.headerConfig.right);
      if (this.headerConfig.right && this.headerConfig.right.path) {
        this.$router.push(this.headerConfig.right.path);
        return;
      }
      this.headerConfig.right &&
        this.headerConfig.right.todo &&
        this.headerConfig.right.todo();
      return null;
    }
  },
  mounted() {
    //this.headerConfig.right.todo()
  }
};
</script>
 <style lang="less" scoped>
@import "../../../public/static/styles/theme/index.less";
.van-nav-bar {
  .theme-back;
}

/deep/ .van-nav-bar__arrow{
  font-size: 18px;
}

/deep/ .van-nav-bar {
  height: 50px;
}

/deep/ .van-icon {
  color: #000000;
}
.van-nav-bar .van-icon,
.van-nav-bar__title,
.van-nav-bar__text {
  .theme-font-color;
}
</style>

