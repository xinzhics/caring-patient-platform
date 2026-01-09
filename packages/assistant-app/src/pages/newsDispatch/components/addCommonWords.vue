<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left"  title="添加常用语" @onBack="goback"></headNavigation>
    </van-sticky>
    <div style="margin-top: 15px">
      <van-field
        class="box-input"
        v-model="commonWord.content"
        rows="10"
        maxlength="800"
        type="textarea"
        show-word-limit
        placeholder="请输入"
      />
    </div>
    <div style="width: 217px;margin:0 auto;margin-top: 66px">
      <van-button :disabled="!!!commonWord.content" :class="!!!commonWord.content?'disable':''" style="width: 100%" round type="info" @click="submitCommonWord">保存</van-button>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Checkbox, CheckboxGroup, Tab, Tabs, Field, Toast, Button, Sticky} from 'vant'
import { createCommonWord, updateCommonWord, getCommonWord } from '@/api/commonWords.js'

Vue.use(Icon)
Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Button)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Field)
export default {
  data () {
    return {
      nursingId: localStorage.getItem('caringNursingId'),
      localMessageId: this.$route.query.localMessageId, // 本地设置的群发信息的缓存 key
      commonWordId: this.$route.query.commonWordId,
      commonWord: {}
    }
  },
  created () {
    if (this.commonWordId) {
      this.getCommonWord()
    }
  },
  methods: {
    getCommonWord () {
      getCommonWord(this.commonWordId).then(res => {
        if (res.code === 0) {
          this.commonWord = res.data
        }
      })
    },
    /**
     * 提交常用语
     */
    submitCommonWord () {
      if (this.commonWord.id) {
        updateCommonWord(this.commonWord).then(res => {
          if (res.code === 0) {
            Toast({message: '常用语修改成功', closeOnClick: true, duration: 1500})
            this.goback()
          }
        })
      } else {
        this.commonWord.accountId = this.nursingId
        this.commonWord.userType = 'NursingStaff'
        createCommonWord(this.commonWord).then(res => {
          if (res.code === 0) {
            Toast({message: '常用语添加成功', closeOnClick: true, duration: 1500})
            this.goback()
          }
        })
      }
    },
    goback () {
      this.$router.replace({
        path: '/newsDispatch/commonWordsList',
        query: {
          localMessageId: this.localMessageId
        }
      })
    }
  }
}
</script>

<style scoped>
.top-box {
  /*display: flex;*/
  /*justify-content: space-between;*/
  padding: 48px 13px 13px 13px;
  color: #333;
  font-size: 19px;
  background: #fff;
  text-align: center;
  line-height: 26px;
}
/deep/.box-input span {
  color:#337EFF
}
</style>
