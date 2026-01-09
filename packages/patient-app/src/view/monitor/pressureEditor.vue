<template>
  <section>
    <navBar :pageTitle="pageTitle" :backQuery="{planId: this.planId}" :isGroup="true" :backUrl="backUrl"></navBar>
    <attrPage v-if="fields && fields.length > 0" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>
<script>
import Api from '@/api/Content.js'

export default {
  components: {
    attrPage: () => import('@/components/arrt/editorIndex'),
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      pageTitle: '',
      fields: [],
      editorData: {},
      allArray: [],
      dataId: undefined,
      imMessageId: undefined,
      planId: this.$route.query.planId,
      formId: "",
      submitStatus: false,
      backUrl: '/monitor/pressure',
    }
  },
  mounted() {
    if (this.$route.query.messageId) {
      Api.reminderLogOpenMessage(this.$route.query.messageId)
    }
    if (this.$route.query && this.$route.query.onback) {
      this.backUrl = this.$route.query.onback
    }else if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = undefined
      this.imMessageId = this.$route.query.imMessageId
    }
    if (this.$route.query && this.$route.query.content) {
      this.dataId = this.$route.query.content
      this.getData()
    } else {
      this.getInfo()
    }
    console.log('======1111========', this.backUrl)
  },
  methods: {
    getInfo() {
      const that = this
      const planType = 1
      let messageId = ''
      if (this.imMessageId) {
        messageId = this.imMessageId
      } else if (this.$route.query.messageId) {
        messageId = this.$route.query.messageId
      }
      Api.formOrFormResult(null, planType, messageId).then(res => {
        if (res.data.code === 0) {
          this.pageTitle = res.data.data.name
          that.editorData = res.data.data
          if (this.imMessageId) {
            this.editorData.messageId = this.imMessageId
          }
          if (that.editorData.businessId) {
            this.planId = that.editorData.businessId
          }
          that.fields = JSON.parse(res.data.data.jsonContent)
        }
      })
    },
    submit(k) {
      if (k.MarkTip === 'has') {
        if (k.content) {
          this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
        }
        this.allArray.push(k.page)
        this.$refs.attrPage.nextPage();
      } else {
        if (this.submitStatus) {
          return
        }
        this.submitStatus = true
        if (k.content) {
          this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
        }
        if (this.editorData.id) {

          let params = {
            "businessId": this.editorData.businessId,
            "category": this.editorData.category,
            "formId": this.editorData.formId,
            "jsonContent": JSON.stringify(this.allArray),
            "messageId": this.editorData.messageId,
            "name": this.editorData.name,
            "userId": this.editorData.userId,
            "id": this.editorData.id,
          }

          if (this.imMessageId) {
            params.imRecommendReceipt = 1
          }

          Api.gethealthformSubPut(params).then((res) => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              if (this.$route.query.onback === '/patient/systemMessage') {
                this.$router.go(-1)
              }else if (this.imMessageId) {
                this.$router.go(-1)
              }else {
                this.$router.replace({path: '/monitor/pressure', query: {planId: this.planId}})
              }
            }
          }).catch(error => {
            this.submitStatus = false
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
            userId: localStorage.getItem('userId'),
            planDetailTimeId: this.$route.query.planDetailTimeId,
          }

          if (this.imMessageId) {
            params.imRecommendReceipt = 1
          }
          // 过滤掉无用的参数
          const newObj = {};
          for (const key in params) {
            if (params[key] !== '' && typeof params[key] !== 'undefined') {
              newObj[key] = params[key];
            }
          }
          Api.gethealthformSub(newObj).then((res) => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              if (this.$route.query.planDetailTimeId) {
                this.$router.back()
              } else {
                if (this.imMessageId) {
                  this.$router.go(-1)
                }else {
                  this.$router.replace({path: '/monitor/pressure', query: {planId: this.planId}})
                }
              }
            }
          }).catch(error => {
            this.submitStatus = false
          })
        }
      }
    },
    getData() {
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
      Api.getCheckDataformResult(params).then((res) => {
        if (res.data.code === 0) {
          this.formId = res.data.data.id
          this.pageTitle = res.data.data.name
          that.editorData = res.data.data
          that.fields = JSON.parse(res.data.data.jsonContent)
        }
      })
    },

  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
}

</style>
