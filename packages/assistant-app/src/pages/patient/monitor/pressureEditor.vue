<template>
  <section>
    <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '血压监测'"
                    @onBack="$router.go(-1)"></headNavigation>
    <attrPage v-if="fields && fields.length > 0" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>
<script>
import {formOrFormResult, updateFormResult, createFormResult} from '@/api/formApi.js'
import {getCheckDataformResult} from '@/api/Content.js'
import {Toast} from 'vant'

export default {
  components: {
    attrPage: () => import('@/components/arrt/editorIndex')
  },
  data () {
    return {
      pageTitle: '',
      fields: [],
      editorData: {},
      allArray: [],
      dataId: undefined,
      imMessageId: undefined,
      planId: this.$route.query.planId,
      formId: '',
      submitStatus: false,
      backUrl: '/monitor/pressure'
    }
  },
  mounted () {
    if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = undefined
      this.imMessageId = this.$route.query.imMessageId
    }
    if (this.$route.query && this.$route.query.content) {
      this.dataId = this.$route.query.content
      this.getData()
    } else {
      this.getInfo()
    }
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    getInfo () {
      const that = this
      const planType = 1
      let messageId = ''
      if (this.imMessageId) {
        messageId = this.imMessageId
      } else if (this.$route.query.messageId) {
        messageId = this.$route.query.messageId
      }
      formOrFormResult(null, planType, messageId).then(res => {
        if (res.code === 0) {
          that.editorData = res.data
          if (this.imMessageId) {
            this.editorData.messageId = this.imMessageId
          }
          if (that.editorData.businessId) {
            this.planId = that.editorData.businessId
          }
          that.fields = JSON.parse(res.data.jsonContent)
        }
      })
    },
    submit (k) {
      if (k.MarkTip === 'has') {
        if (k.content) {
          this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
        }
        this.allArray.push(k.page)
        this.$refs.attrPage.nextPage()
      } else {
        if (this.submitStatus) {
          return
        }
        this.submitStatus = true
        if (k.content) {
          this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
        }
        this.$refs.attrPage.setLoading(true)
        if (this.editorData.id) {
          let params = {
            'businessId': this.editorData.businessId,
            'category': this.editorData.category,
            'formId': this.editorData.formId,
            'jsonContent': JSON.stringify(this.allArray),
            'messageId': this.editorData.messageId,
            'name': this.editorData.name,
            'userId': this.editorData.userId,
            'id': this.editorData.id
          }
          if (this.imMessageId) {
            params.imRecommendReceipt = 1
          }
          updateFormResult(params).then((res) => {
            this.submitStatus = false
            if (res.code === 0) {
              Toast.success('提交成功')
              this.$router.go(-1)
            }
          }).catch(() => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
          })
        } else {
          let params = {
            businessId: this.editorData.businessId,
            category: this.editorData.category,
            fieldList: this.allArray,
            formId: this.editorData.formId,
            jsonContent: JSON.stringify(this.allArray),
            messageId: this.editorData.messageId,
            name: this.editorData.name,
            userId: localStorage.getItem('patientId'),
            planDetailTimeId: this.$route.query.planDetailTimeId
          }
          if (this.imMessageId) {
            params.imRecommendReceipt = 1
          }
          // 过滤掉无用的参数
          const newObj = {}
          for (const key in params) {
            if (params[key] !== '' && typeof params[key] !== 'undefined') {
              newObj[key] = params[key]
            }
          }
          createFormResult(newObj).then((res) => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
            console.log('=2222=====================')
            if (res.code === 0) {
              Toast.success('提交成功')
              if (this.$route.query.planDetailTimeId) {
                this.$router.back()
              } else {
                if (this.imMessageId) {
                  this.$router.go(-1)
                } else {
                  this.$router.replace({path: '/patient/monitor/pressure', query: {planId: this.planId}})
                }
              }
            }
          }).catch(() => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
          })
        }
      }
    },
    getData () {
      const that = this
      let params = {}

      if (this.imMessageId) {
        params = {
          id: this.dataId,
          messageId: this.imMessageId
        }
      } else {
        params = {
          id: this.dataId
        }
      }
      getCheckDataformResult(params).then((res) => {
        if (res.code === 0) {
          this.formId = res.data.id
          that.editorData = res.data
          that.fields = JSON.parse(res.data.jsonContent)
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
}

</style>
