<template>
  <div>
    <van-dialog confirm-button-color="#2F8DEB" @confirm="confirm" @cancel="cancel" :showCancelButton="true" v-model="show"  show-cancel-button>
      <div style="width: 53px;margin: 18px auto">
        <img src="../../../assets/careful.png" style="width: 53px" alt="">
      </div>
      <div style="width: 90%;margin: 0 auto;text-align: center;color: #666666;font-size: 15px">
        <div style="margin-bottom: 10px">确定将该{{$getDictItem('patient')}}</div>
        <div style="margin-bottom: 19px">预约状态改为"已就诊"吗？</div>
      </div>
    </van-dialog>
  </div>
</template>

<script>
export default {
  watch: {
    showDialog: {
      handler: function (val, old) {
        if (val) {
          this.show = val
        }
      }
    }
  },
  name: 'signDialog',
  data () {
    return {
      show: false
    }
  },
  props: {
    showDialog: {
      type: Boolean
    }
  },
  methods: {
    /**
     * 点击确定
     */
    confirm () {
      this.$emit('showDialogs', true)
    },
    /**
     * 点击取消
     */
    cancel () {
      this.$emit('showDialogs', false)
    }
  }
}
</script>

<style scoped lang="less">
/deep/.van-dialog {
  .van-dialog__footer {
    height: 53px !important ;
    border-top: 1px solid #EBEBEB;
    .van-dialog__cancel{
      height: 53px !important ;
    }
    .van-dialog__confirm{
      height: 53px !important ;
    }
  }
}
</style>
