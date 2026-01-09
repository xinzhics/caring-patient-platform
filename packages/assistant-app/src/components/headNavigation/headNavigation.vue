<template>
  <van-row>
    <van-col span="24">
      <div class="top-box" :style="{padding: isBrowser ? '47px 13px 13px 13px' : '15px 13px 13px 13px'}">
        <van-col style="text-align: left; " :span="leftSpanWidth" @click="onBack()">
          <van-icon class="leftIcon" v-show="leftIcon" size="20" :name="leftIcon"/>
        </van-col>
        <van-col :span="centerSpanWidth">
          <div class="top-title" >{{ title }}</div>
        </van-col>
        <van-col style="text-align: right" :span="rightSpanWidth" @click="showPop">
          <img width="22px" height="22px" v-if="myIcon" :src="clearImg" alt="">
          <van-icon class="rightIcon"  v-if="rightIcon&&!myIcon"  size="22" :name="rightIcon"/>
          <div class="rightIcon" v-else-if="showRightText" style="font-size: 15px" >{{rightText}}</div>
          <van-icon v-else style="visibility:hidden; font-size: 16px" />
        </van-col>
      </div>
    </van-col>
  </van-row>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Sticky} from 'vant'
import {bgColor} from 'friendly-errors-webpack-plugin/src/utils/colors'

Vue.use(Icon)
Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
export default {
  data () {
    return {
      showpop: true,
      isBrowser: false,
      clearImg: require('@/assets/my/clear.png')
    }
  },
  props: {
    title: {
      type: String
    },
    leftIcon: {
      type: String
    },
    rightIcon: {
      type: String,
      default: ''
    },
    myIcon: { // 右侧是否自定义图标
      type: Boolean,
      default: false
    },
    showRightText: {
      type: Boolean,
      default: false
    },
    rightText: {
      type: String
    },
    leftSpanWidth: {
      type: Number,
      default: 4
    },
    rightSpanWidth: {
      type: Number,
      default: 4
    },
    centerSpanWidth: {
      type: Number,
      default: 16
    }
  },
  created () {
    console.log(this.rightText)
  },
  mounted () {
    this.detectBrowser()
  },
  methods: {
    bgColor,
    showPop () {
      this.$emit('showpop', this.showpop)
    },
    onBack () {
      this.$emit('onBack', '')
    },
    detectBrowser () {
      // 使用用户代理字符串判断是否为手机
      const device = localStorage.getItem('caringCurrentDevice')
      if (device === 'weixin') {
        this.isBrowser = false
      } else {
        const userAgent = navigator.userAgent.toLowerCase()
        this.isBrowser = /mobile|android|iphone|ipad|phone/i.test(userAgent)
      }
    }
  }

}
</script>
<style lang="less">
  .leftIcon {
    width: 25px;
    height: 25px;
    line-height: 25px;
  }
  .rightIcon {
    height: 25px;
    line-height: 25px;
    vertical-align: middle
  }
  .top-box{
    padding: 47px 13px 13px 13px;
  }
  .top-title {
    height: 25px;
    font-size: 16px;
    font-weight: 700;
    color: rgb(0, 0, 0);
    text-align: center;
    opacity: 1;
  }
  @font-face {
    font-family: 'my-icon';
    src: url('/src/assets/my/clear.png') format('truetype');
  }

  .my-icon {
    font-family: 'my-icon';
  }

  .my-icon-extra:before {
    content: '\e60c';
    font-family: 'my-icon';
  }
</style>
