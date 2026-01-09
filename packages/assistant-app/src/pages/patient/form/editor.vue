<template>
  <div>
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :rightIcon="showHistory==='true' && formResultId ? historyImg : ''"
                      :title="title ? title : pageTitle"
                      @onBack="back" @showpop="toHistory"></headNavigation>
    </van-sticky>
    <div style='margin-top: 13px'>
      <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
    </div>
  </div>
</template>
<script>
import {updateFormResult, createFormResult, formResultDetail, formOrFormResult} from '@/api/formApi.js'
import attrPage from '@/components/arrt/editorIndex'
import {Toast, Sticky, Row, Col, Icon} from 'vant'

import Vue from 'vue'

Vue.use(Toast)
Vue.use(Sticky)
Vue.use(Row)
Vue.use(Icon)
Vue.use(Col)
export default {
  components: {attrPage},
  data () {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      title: this.$route.query.title,
      historyImg: require('@/assets/patient/set.png'),
      planId: this.$route.query.planId,
      planType: this.$route.query.planType,
      formResultId: this.$route.query.formResultId,
      fields: [],
      allInfo: {},
      editorData: {},
      allArray: [],
      showHistory: '',
      isHistory: ''
    }
  },
  created () {
    console.log('================')
    this.showHistory = this.$getShowHist()
    if (this.$route.query.formResultId) {
      if (this.$route.query.isHistory === '1') {
        this.showHistory = false
        this.isHistory = this.$route.query.isHistory
      }
      this.formResultId = this.$route.query.formResultId
      this.getData()
    } else {
      this.getInfo()
    }
  },
  methods: {
    /**
     * 返回到列表页面
     */
    back () {
      if (this.isHistory === '1') {
        // 表示从患者管理平台 直接 进入编辑表单界面，返回需要直接退回到患者管理平台
        this.$backPatientManage()
      } else {
        this.$router.go(-1)
      }
    },
    toHistory () {
      this.$router.push({
        path: `/patient/baseinfo/index/history`,
        query: {
          id: this.formResultId
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
        if (this.editorData.formId) {
          const params = {
            businessId: this.editorData.businessId,
            category: this.editorData.category,
            formId: this.editorData.formId,
            jsonContent: JSON.stringify(this.allArray),
            messageId: this.editorData.messageId,
            name: this.editorData.name,
            userId: this.editorData.userId,
            id: this.editorData.id
          }
          updateFormResult(params).then((res) => {
            if (res.code === 0) {
              Toast({message: '提交成功!', position: 'bottom', closeOnClick: true})
              this.back()
            }
          })
        } else {
          const params = {
            businessId: this.allInfo.businessId,
            category: this.allInfo.category,
            fieldList: this.allArray,
            formId: this.allInfo.formId,
            jsonContent: JSON.stringify(this.allArray),
            messageId: this.allInfo.messageId,
            name: this.allInfo.name,
            userId: localStorage.getItem('patientId')
          }
          createFormResult(params).then((res) => {
            if (res.code === 0) {
              Toast({message: '提交成功!', position: 'bottom', closeOnClick: true})
              if (this.allInfo.scoreQuestionnaire === 1) {
                // 评分表单，需要跳转到结果页
                this.$router.replace({
                  path: '/score/result',
                  query: {
                    dataId: res.data.id,
                    backUrl: this.$route.query.backUrl,
                    planId: this.$route.query.planId,
                    title: this.$route.query.title
                  }
                })
                return
              }
              this.back()
            }
          })
        }
      }
    },
    getInfo () {
      formOrFormResult(this.planId, this.planType, null).then(res => {
        if (res.code === 0) {
          this.allInfo = res.data
          this.fields = []
          const list = JSON.parse(res.data.jsonContent)
          this.fields.push(...list)
        }
      })
    },
    getData () {
      formResultDetail(this.formResultId).then((res) => {
        if (res.code === 0) {
          this.editorData = res.data
          const list = JSON.parse(res.data.jsonContent)
          this.fields.push(...list)
        }
      })
    }
  }
}
</script>
