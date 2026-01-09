<template>
  <section class="allContent">
    <x-header :left-options="{backText: ''}">修改
    </x-header>
    <div class="box1">
      <div class="title">
        <span class="username">基线值</span>
      </div>
      <div style="padding-left: 15px; padding-right: 15px">
        <van-field v-model="referenceValue" type="number" placeholder="请输入" maxlength="5"/>
      </div>
    </div>
    <div style="border-bottom: 1px solid #D6D6D6; margin-left: 12px; margin-right: 12px"></div>
    <div class="box1">
      <div class="title">
        <span class="username">目标值</span>
      </div>
      <div style="padding-left: 15px; padding-right: 15px">
        <van-field v-model="targetValue" type="number" placeholder="请输入" maxlength="5"/>
      </div>
    </div>

    <div class="loginBtn" @click="submitBtn">
      提交
    </div>
  </section>
</template>

<script>
import Api from '@/api/Content.js'

export default {
  name: "editReference",
  data() {
    return {
      referenceValue: '',
      targetValue: '',
    }
  },
  created() {
    if (this.$route.query.referenceValue) {
      this.referenceValue = this.$route.query.referenceValue
    }
    if (this.$route.query.targetValue) {
      this.targetValue = this.$route.query.targetValue
    }
  },
  methods: {
    submitBtn() {
      if (this.referenceValue === '') {
        this.$vux.toast.text("请输入基线值",'center');
        return;
      }

      if (this.targetValue === '') {
        this.$vux.toast.text("请输入目标值",'center');
        return;
      }
      let params = {
        businessId: this.$route.query.businessId,
        referenceValue: this.referenceValue,
        targetValue: this.targetValue,
        patientId: localStorage.getItem('userId'),
        fieldId: this.$route.query.fieldId,
      }
      Api.updateFieldReference(params)
        .then(res => {
          if (res.data && res.data.code == 0) {
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
