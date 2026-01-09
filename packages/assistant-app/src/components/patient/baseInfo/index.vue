<template>
  <div>
    <attrPage v-if='fields && fields.length >= 1' :all-fields='fields' ref='attrPage' :submit='submit'></attrPage>
  </div>
</template>

<script>
import { getHealthOrBaseInfoForm, createFormResult, updateFormResult } from '@/api/formApi.js'
import attrPage from '@/components/arrt/editorIndex'
import Vue from 'vue'
import { Row, Col, Icon, Toast, Sticky } from 'vant'

Vue.use(Toast)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
export default {
  name: 'baseinfo',
  components: {attrPage},
  data () {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      historyImg: require('@/assets/patient/set.png'),
      fields: [],
      allInfo: {},
      allArray: [],
      statusAll: {},
      formId: '',
      showHistory: ''
    }
  },
  created () {
    this.getFormResultBaseInfo()
    this.showHistory = this.$getShowHist()
    console.log('showHistory', this.showHistory)
  },
  methods: {
    toHistory () {
      this.$router.push({
        path: `/patient/baseinfo/index/history`,
        query: {
          id: this.formId
        }
      })
    },
    /**
     * 获取基本信息表单的结果
     */
    getFormResultBaseInfo () {
      const patientId = localStorage.getItem('patientId')
      getHealthOrBaseInfoForm(patientId, 'BASE_INFO').then((res) => {
        if (res.code === 0) {
          this.formId = res.data.id
          this.allInfo = res.data
          this.fields = res.data.fieldList
        }
      })
    },
    submit (k) {
      if (k.MarkTip === 'has') {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content)
        }
        this.allArray.push(k.page)
        this.$refs.attrPage.nextPage()
      } else {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content)
        }
        if (this.allInfo.id) {
          this.allInfo.jsonContent = JSON.stringify(this.allArray)
          updateFormResult(this.allInfo).then((res) => {
            if (res.code === 0) {
              Toast({ message: '提交成功!', position: 'bottom', closeOnClick: true })
              this.back()
            }
          })
        } else {
          this.allInfo.jsonContent = JSON.stringify(this.allArray)
          createFormResult(this.allInfo).then((res) => {
            if (res.code === 0) {
              Toast({ message: '提交成功!', position: 'bottom', closeOnClick: true })
              this.back()
            }
          })
        }
      }
    },
    back () {
      this.$router.replace({
        path: '/patient/center'
      })
    }
  }
}
</script>
<style lang='less' scoped>
</style>
