<template>
  <div>
    <van-nav-bar
        :title="headerConfig.title"
        :left-text="headerConfig.left ? headerConfig.left.text :''"
        left-arrow
        @click-left="onClickLeft"
        @click-right="onClickRight"
        v-if="headerConfig.right && headerConfig.right.icon"
    >
      <van-icon :name="headerConfig.right.icon" slot="right"/>
    </van-nav-bar>

    <van-nav-bar
        :title="headerConfig.title"
        :left-text="headerConfig.left ? headerConfig.left.text :''"
        :right-text="headerConfig.right ? headerConfig.right.text :''"
        @click-left="onClickLeft"
        @click-right="onClickRight"
        v-else-if="headerConfig.left.text === ''"
    />
    <van-nav-bar
        :title="headerConfig.title"
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
      console.log(this.headerConfig);
      if (this.headerConfig.left && this.headerConfig.left.todo) {
        this.headerConfig.left.todo();
      } else if (
          !(this.headerConfig.left && this.headerConfig.left.noBack)
      ) {
        this.$router.back();
      } else {
        this.$router.push("/ucenter");
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
  }
};
</script>
<style lang="less" scoped>
@import "../../../public/static/styles/theme/index.less";

.van-nav-bar,
/deep/ .van-nav-bar__text:active {
  .theme-back;
}

/deep/ .van-nav-bar {
  height: 50px;
}

/deep/ .van-nav-bar__arrow{
  font-size: 18px;
}

/deep/ .van-icon {
  color: #000000;
}

.van-nav-bar__title,
.van-nav-bar__text {
  .theme-font-color;
}

.van-nav-bar__left {
  line-height: 16px;
  height: 16px;
  top: 14px;
}

.van-nav-bar__right {
  .van-icon {
    font-size: 20px;
  }
}

// /deep/.van-nav-bar__title {
//     line-height: 48px;
// }
</style>

