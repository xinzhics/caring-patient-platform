<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" title="群发内容" @onBack="goback"></headNavigation>
    </van-sticky>
    <div style="margin-top: 10px">
      <van-tabs color="#3F86FF" title-active-color="#3F86FF" v-model="active">
        <van-tab  title="文字">
          <van-field
            style="margin-top: 10px;"
            v-model="wordContent"
            rows="8"
            show-word-limit
            type="textarea"
            maxlength="800"
            placeholder="请输入群发内容"
          />
          <div @click="goCommonWords" style="margin-top: 10px;height: 50px;background: #fff;display: flex;align-items: center;justify-content: center;color: #666">
            <van-icon name="add-o" /><span style="margin-left: 10px">从常用语选取</span>
          </div>
          <div  style="margin-top: 75px;display: flex;justify-content: center" >
            <van-button :class="!!!wordContent?'disable':''"  :disabled="!!!wordContent" @click="submit()" style="width: 217px" round type="info">发送</van-button>
          </div>
        </van-tab>
        <van-tab title="图片">
            <add-pcitrue></add-pcitrue>
        </van-tab>
        <van-tab title="文章">
          <addarticle v-if="active === 2"></addarticle>
        </van-tab>
      </van-tabs>
    </div>
    <Dialog @showPop="showPop" :localMessageId="localMessageId" :submit-message="submitData" :show="dialogShow" :people-number="peopleNumber"></Dialog>
  </div>
</template>

<script>
import Vue from 'vue'
import Dialog from './Dialog'
import {Col, Row, Icon, Checkbox, CheckboxGroup, Tab, Tabs, Field, Sticky} from 'vant'
import addPcitrue from './addPcitrue'
import addarticle from './addArticle'
Vue.use(Icon)
Vue.use(Col)
Vue.use(Row)
Vue.use(Tab)
Vue.use(Tabs)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Field)
Vue.use(Sticky)
export default {
  components: {
    addPcitrue, addarticle, Dialog
  },
  data () {
    return {
      active: this.$route.query.active ? Number(this.$route.query.active) : 0,
      nursingId: localStorage.getItem('caringNursingId'),
      wordContent: this.$route.query.wordContent,
      localMessageId: this.$route.query.localMessageId, // 本地设置的群发信息的缓存 key
      dialogShow: false,
      submitData: {},
      peopleNumber: 0
    }
  },
  methods: {
    goback () {
      this.$router.replace({
        path: '/newsDispatch/patientList',
        query: {
          localMessageId: this.localMessageId
        }
      })
    },
    /**
     * 提交
     */
    submit () {
      if (this.wordContent) {
        const localMessageJSONString = localStorage.getItem(this.localMessageId)
        const localMessage = JSON.parse(localMessageJSONString)
        const params = {}
        this.peopleNumber = localMessage.peopleNumber
        params.receiverId = localMessage.receiverId
        params.senderId = this.nursingId
        params.type = 'text'
        params.content = this.wordContent
        this.submitData = params
        this.dialogShow = true
      }
    },
    /**
     * 跳转到选择常用语
     */
    goCommonWords () {
      this.$router.push({
        path: '/newsDispatch/commonWords',
        query: {
          localMessageId: this.localMessageId
        }
      })
    },
    /**
     * 弹窗传递数据
     * @param val 关闭弹窗false
     */
    showPop () {
      this.dialogShow = false
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
  line-height: 1;
  background: #fff;
  text-align: center;
}

</style>
