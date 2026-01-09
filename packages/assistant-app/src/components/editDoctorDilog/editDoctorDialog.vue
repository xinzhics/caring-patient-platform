<template>
  <div>
    <van-dialog v-model="openStatus" :show-confirm-button="false" :show-cancel-button="false" :close-on-click-overlay="true">
      <div style="border-bottom:1px solid #3F86FF;margin-bottom: 5px">
        <van-field style="font-size: 18px" v-model="value" :type="title==='手机号'?'digit':'text'" placeholder="请输入" :maxlength="maxTextNumber" clearable />
      </div>
      <div style="display: flex;justify-content: space-between;padding-left: 15px;padding-right: 10px;color: #B8B8B8">
        <div>{{ title }}</div>
        <div>{{ value.length }}/{{ maxTextNumber }}</div>
      </div>
      <div style="width: 80%;margin: 0 auto;margin-bottom: 10px;margin-top: 20px " @click="setname">
        <van-button style="width: 100%;" round type="info">确定</van-button>
      </div>
    </van-dialog>

  </div>
</template>

<script>
import Vue from 'vue'
import {Field, Dialog, Button} from 'vant'

Vue.use(Field)
Vue.use(Button)
Vue.use(Dialog)
export default {
  props: {
    title: {
      type: String
    },
    type: {
      type: String
    },
    maxTextNumber: {
      type: String,
      defaultLevel: 100
    },
    propsValue: {
      type: String,
      defaultLevel: ''
    }
  },
  created () {
    console.log('propsValue', this.propsValue)
  },
  data () {
    return {
      value: this.propsValue ? this.propsValue : '',
      openStatus: false
    }
  },
  methods: {
    setname () {
      this.$emit('value', this.value)
      this.closeDialog()
    },
    openDialog () {
      this.openStatus = true
    },
    setValue (value) {
      if (value == null) {
        this.value = ''
      } else {
        this.value = value
      }
      console.log('value', value)
    },
    closeDialog () {
      this.openStatus = false
      this.$emit('closeDialog')
    }
  }
}
</script>

<style scoped>

</style>
