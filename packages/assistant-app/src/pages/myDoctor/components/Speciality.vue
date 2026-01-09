<template>
  <div>
    <van-popup v-model="show" position="top" :style="{ height: '100%',padding:'0' }" >
      <headNavigation leftIcon="arrow-left" :rightIcon="''" title="专业特长" @onBack="goback"></headNavigation>
      <div style="margin-top: 10px">
        <van-field
          v-model="message"
          maxlength="500"
          :autosize="{ maxHeight: 300, minHeight: 180 }"
          style="line-height: 1.5"
          type="textarea"
          placeholder="请输入"
          show-word-limit
        />
      </div>
      <div @click="submit" style="width: 80%;margin: 20px auto ">
        <van-button style="width: 100%;" round type="info">确定</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, Icon, Field, Button} from 'vant'
Vue.use(Icon)
Vue.use(Col)
Vue.use(Row)
Vue.use(Field)
Vue.use(Button)
export default {
  data () {
    return {
      message: '',
      show: false
    }
  },
  methods: {
    goback () {
      this.message = ''
      this.show = false
    },
    submit () {
      this.$emit('message', this.message)
      this.show = false
    },
    setMessage (val) {
      this.show = true
      this.message = val
    }
  },
  // 没用了 给你留个修改的传值
  props: {
    showSpeciality: {
      type: Boolean
    },
    propsValue: {
      type: String
    }
  }
}
</script>
<style scoped lang="less">
/deep/ .van-field__word-limit{
  margin-right: 10px;
}
</style>
