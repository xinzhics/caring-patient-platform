<template>
  <div class="allContent">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" title="修改基线值" @onBack="back"></headNavigation>
    </van-sticky>
    <div class="box1">
      <div class="title">
        <span class="username">基线值 <span style="color: red">*</span></span>
      </div>
      <div style="padding-left: 15px; padding-right: 15px">
        <van-field v-model="referenceValue" type="number" placeholder="请输入" maxlength="5"/>
      </div>
    </div>
    <div style="border-bottom: 1px solid #D6D6D6; margin-left: 12px; margin-right: 12px"></div>
    <div class="box1">
      <div class="title">
        <span class="username">目标值 <span style="color: red">*</span></span>
      </div>
      <div style="padding-left: 15px; padding-right: 15px">
        <van-field v-model="targetValue" type="number" placeholder="请输入" maxlength="5"/>
      </div>
    </div>
    <div class="loginBtn" @click="submitBtn">
      提交
    </div>
  </div>
</template>

<script>
import { updateFieldReference } from '@/api/formApi.js'
import Vue from 'vue'
import {Toast, Field, Sticky, Icon, Row, Col} from 'vant'

Vue.use(Field)
Vue.use(Toast)
Vue.use(Icon)
Vue.use(Row)
Vue.use(Col)
Vue.use(Sticky)
export default {
  name: 'editReference',
  data () {
    return {
      referenceValue: '',
      targetValue: ''
    }
  },
  created () {
    if (this.$route.query.referenceValue) {
      this.referenceValue = this.$route.query.referenceValue
    }
    if (this.$route.query.targetValue) {
      this.targetValue = this.$route.query.targetValue
    }
  },
  methods: {
    back () {
      this.$router.go(-1)
    },
    submitBtn () {
      if (this.referenceValue === '') {
        Toast({message: '请输入基线值', closeOnClick: true})
        return
      }

      if (this.targetValue === '') {
        Toast({message: '请输入目标值', closeOnClick: true})
        return
      }
      let params = {
        businessId: this.$route.query.businessId,
        referenceValue: this.referenceValue,
        targetValue: this.targetValue,
        patientId: localStorage.getItem('patientId'),
        fieldId: this.$route.query.fieldId
      }
      updateFieldReference(params).then(res => {
        if (res.code === 0) {
          this.$router.go(-1)
        }
      })
    }
  }
}
</script>

<style lang='less' scoped >
.box1 {
  padding-bottom: 1.5rem;
  background-color: #fff;
  position: relative;
}

.title {
  width: 100%;
  padding-top: 1rem;
  padding-bottom: 0.5rem;
  background-color: #fff;
  line-height: 2rem;
  height: 2rem;
}

.inputbox {
  border: 1px solid #fff5f5f5 !important;
}

.username {
  font-size: 1rem;
  color: #333;
  padding-left: 1rem;
}

/deep/.van-cell {
  padding-left: 1rem;
  padding-right: 1rem;
  height: 2.875rem;
  background: rgba(245, 245, 245, 0.39);
  border: 1px solid #D6D6D6;
  opacity: 1;
  border-radius: 8px;
  position: relative;
}

/deep/.van-field__body {
  position: absolute;
  bottom: -.75rem;
  top: -.8rem;
  width: 90%;
}

.allContent {
  width: 100vw;
  height: 100vh;
  background-color: #fafafa;
}

.loginBtn {
  background: #66728B;
  border: 1px solid #D6D6D6;
  border-radius: 40px;
  width: 56%;
  padding: 8px 6px;
  margin: 50px auto 0px;
  color: #fff;
  text-align: center;
}

</style>
