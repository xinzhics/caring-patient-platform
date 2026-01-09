<template>
    <div>
      <van-dialog :showCancelButton="false" :showConfirmButton="false" v-model="showPop"  show-cancel-button>
        <div style="margin-bottom: 20px;text-align: center;padding-top: 20px">发送确认</div>
        <div style="text-align: center;padding-top: 20px;border-top: 1px solid #dddddd;margin-bottom: 30px">将有<span style="color: #1989fa">{{peopleNumber}}</span>人收到您的群发消息</div>
        <div style="display:flex;justify-content: space-between;padding: 0 20px 20px 20px">
          <van-button plain @click="cancel"  size="small"  style="width: 120px;color: #2F8DEB" round  type="info">取消</van-button>
          <van-button @click="confirm"  size="small" round  style="width: 120px" :loading="submitStatue" type="info">确认发送</van-button>
        </div>
      </van-dialog>
    </div>
</template>

<script>
import Vue from 'vue'
import {Dialog, Toast} from 'vant'
import { sentMessage } from '@/api/massMessaging.js'

// 全局注册
Vue.use(Dialog)
export default {
  name: 'Dialog',
  data () {
    return {
      showPop: false,
      submitStatue: false
    }
  },
  watch: {
    show: {
      handler: function (val, old) {
        console.log(val)
        if (val) {
          this.showPop = val
        }
      }
    }
  },
  props: {
    show: {
      type: Boolean
    },
    peopleNumber: {
      type: Number
    },
    submitMessage: {
      type: Object
    },
    localMessageId: {
      type: String
    }
  },
  methods: {
    /**
     * 点击弹窗确认
     */
    confirm () {
      if (this.submitStatue) {
        return
      }
      this.submitStatue = true
      sentMessage(this.submitMessage).then(res => {
        this.submitStatue = false
        if (res.code === 0) {
          Toast({message: '发送成功', closeOnClick: true, duration: 1500})
          if (this.localMessageId) {
            localStorage.removeItem(this.localMessageId)
          }
          this.showPop = false
          this.$router.push({
            path: '/newsDispatch'
          })
        }
      }).catch((res) => {
        this.submitStatue = false
        Toast({message: '发送失败', closeOnClick: true, duration: 1500})
      })
      this.$emit('showPop')
    },
    /**
     * 点击弹窗取消按钮
     */
    cancel () {
      if (this.submitStatue) {
        Toast({message: '正在发送中，请稍等', closeOnClick: true, duration: 1500})
        return
      }
      this.$emit('showPop')
      this.showPop = false
    }
  }
}
</script>

<style scoped>

</style>
