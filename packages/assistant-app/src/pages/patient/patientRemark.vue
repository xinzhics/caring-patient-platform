<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" rightText="保存" :showRightText="true" :title="$getDictItem('patient') + '备注'"
                      @onBack="back" @showpop="savePatient" ></headNavigation>
    </van-sticky>
    <div class="myCenter">
      <van-field v-model="remark" placeholder="请输入备注" />
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { updatePatient } from '@/api/patient.js'
import {ImagePreview, Grid, GridItem, Sticky, Row, Col, Icon, Field} from 'vant'
Vue.use(Grid)
Vue.use(GridItem)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Icon)
Vue.use(Col)
Vue.use(Field)
Vue.use(ImagePreview)
export default {
  name: 'Home',
  data () {
    return {
      remark: '',
      patient: JSON.parse(localStorage.getItem('patientBaseInfo'))
    }
  },
  created () {
    const tempRemark = this.patient.remark
    this.remark = tempRemark
  },
  methods: {
    savePatient () {
      const data = {
        id: this.patient.id,
        remark: this.remark
      }
      updatePatient(data).then(res => {
        if (res.code === 0) {
          this.back()
        }
      })
    },
    /**
     * 页面上的返回
     */
    back () {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="less" scoped>
  .myCenter {
    height: 100vh;
    margin-top: 15px;
  }
</style>
