<template>
  <div>
    <van-nav-bar style="height:46px" z-index="99" @click-left="goback" :fixed="true" :placeholder="true" left-arrow>
      <template slot="left"  style="padding: 0 13px !important;">
        <van-icon name="arrow-left" color="#333333" size="19"/>
      </template>
      <template slot="title" >
        <div v-if="showMedicineInput">
          <van-field @input="searchMedicine" class="medicineInput" v-model="value" placeholder="请输入药品名"/>
        </div>
        <div :style="{'font-size':!isIm? '19px':'16px'}" v-else>
          {{ pageTitle }}
          <div style="font-size: 12px" v-if="isIm">
            仅发送语音时录音
          </div>

        </div>
      </template>
      <template slot="right" >
        <img height="19px" width="19px" :src="rightIcon" @click="toHistoryPage" v-if="showRightIcon">
      </template>
    </van-nav-bar>
  </div>

</template>

<script>
import Vue from 'vue';
import {Field, NavBar,Icon} from 'vant';
import wx from 'weixin-js-sdk';

Vue.use(NavBar);
Vue.use(Field);
Vue.use(Icon);
export default {
  name: "navBar",
  props: {
    pageTitle: {
      type: String
    },
    rightIcon: {
      type: String
    },
    showRightIcon: {
      type: Boolean
    },
    height: {
      type: String
    },
    showMedicineInput: {
      type: Boolean
    },
    backUrl: {
      type: String,
      default: () => {
        return undefined
      }
    },
    backQuery: {
      type: Object,
      default: () => {
        return {}
      }
    },
    closeWindow: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    isCmsIndex: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    setTime: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    isIm:{
      type:Boolean,
      default: () => {
        return false
      }
    },
    isGroup:{
      type:Boolean,
      default: () => {
        return false
      }
    },
    yaoQing:{
      type:Boolean,
      default: () => {
        return false
      }
    }
  },
  data() {
    return {
      value: ''
    }
  },
  mounted() {
    console.log(this.showRightIcon,1111111111111111111)
  },
  methods: {
    toHistoryPage() {
      this.$emit('toHistoryPage')
    },
    goback() {
      // 邀请返回
      if (this.yaoQing) {
        this.$router.push('/home')
        return
      }
      if (this.closeWindow) {
        wx.closeWindow()
      }
      // 如果是cms的首页。执行window的返回
      if (this.isCmsIndex) {
        if (window.history.length  > 1) {
          window.history.back();
        } else {
          wx.closeWindow()
        }
        return
      }
      if (this.backUrl) {
        console.log(this.backQuery)
        this.$router.replace({
          path: this.backUrl,
          query: this.backQuery
        })
        return
      }
      console.log(this.setTime)
      if (!this.setTime) {
        this.$emit('setTime')
      } else {
        if (window.sessionStorage.firstUrl === window.location.href && !this.isGroup) {
          this.$router.push({ path: '/' })
          return false
        } else if (this.isGroup) { // 完成注册返回
          this.$router.push('/home')
        } else {
          console.log('走了么')
          window.history.back()
        }
      }
    },
    searchMedicine(e) {
      this.$emit('searchMedicine', e)
    }
  }
}
</script>

<style scoped lang="less">
/deep/ .van-nav-bar__placeholder {
  .van-nav-bar__content {
    .van-nav-bar__left {
      padding: 0 13px !important;
    }

    .van-nav-bar__right {
      padding: 0 13px !important;
    }
  }
}

/deep/ .van-cell {
  padding: 3px 16px !important;
}

/deep/ .medicineInput {
  border-radius: 20px;
  background: #f5f5f5;
}
</style>
