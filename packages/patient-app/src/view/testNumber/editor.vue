<template>
  <section>
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '检验数据'" :showRightIcon="showRightIcon"
            :rightIcon="rightIcon" @toHistoryPage="ToOtherPage" :backUrl="backUrl"></navBar>
    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>
<script>
import Vue from 'vue'
import Api from '@/api/Content.js'
import attrPage from '@/components/arrt/editorIndex'
import {Toast} from 'vant'
Vue.use(Toast);
export default {
  components: {
    attrPage,
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      fields: [],
      editorData: {},
      allArray: [],
      pageTitle: '',
      messageId: this.$route.query.messageId,
      dataId: undefined,
      showhis: '',
      rightIcon: require('@/images/set.png'),
      submitStatus: false,
      showRightIcon: false,
      backUrl: '/testNumber/index',
      imRecommendReceipt: 0
    }
  },
  mounted() {
    if (this.messageId) {
      Api.reminderLogOpenMessage(this.$route.query.messageId)
    }
    if (this.$route.query && this.$route.query.onback) {
      this.backUrl = undefined
    }else if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = undefined
      this.imRecommendReceipt = 1
    }
    if (this.$route.query.content) {
      this.dataId = this.$route.query.content
      this.getData()
    } else {
      this.getInfo()
    }
    if (this.showhis==='true' && this.dataId) {
      this.showRightIcon = true;
    }
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    submit(k) {
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
          k.content = []
        }
        let params = {
          businessId: this.editorData.businessId,
          category: this.editorData.category,
          formId: this.editorData.formId,
          jsonContent: JSON.stringify(this.allArray),
          messageId: this.editorData.messageId,
          name: this.editorData.name
        }
        if (this.editorData.id) {
          params.id = this.editorData.id
          params.userId = this.editorData.userId
          params.imRecommendReceipt = this.imRecommendReceipt
          Api.gethealthformSubPut(params).then(res => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              if (this.$route.query.onback) {
                this.$router.go(-this.$route.query.onback)
              }else if (this.imRecommendReceipt === 1) {
                // 是从在线咨询进入
                this.$router.go(-1)
              }else {
                this.$router.replace({ path: '/testNumber/index'})
              }
            }
          }).catch(error => {
            this.submitStatus = false
          })
        } else {
          params.userId =  localStorage.getItem('userId')
          params.planDetailTimeId = this.$route.query.planDetailTimeId
          if (this.$route.query && this.$route.query.imMessageId) {
            params.imRecommendReceipt = 1
          }
          // 过滤掉无用的参数
          const newObj = {};
          for (const key in params) {
            if (params[key] !== '' && typeof params[key] !== 'undefined') {
              newObj[key] = params[key];
            }
          }
          Api.gethealthformSub(newObj).then(res => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              // 从IM 或者从待办过来。提交后直接返回。
              if (this.imRecommendReceipt === 1 || this.$route.query.planDetailTimeId) {
                this.$router.go(-1)
              } else {
                this.$router.replace({ path: '/testNumber/index'})
              }
            }
          }).catch(error => {
            this.submitStatus = false
          })
        }
      }
    },
    getInfo() {
      const that = this
      const planType = 3
      let messageId = ''
      if (this.$route.query && this.$route.query.imMessageId) {
        messageId = this.$route.query.imMessageId
      } else if (this.$route.query && this.$route.query.messageId) {
        messageId =  this.$route.query.messageId
      }
      Api.formOrFormResult(null, planType, messageId).then(res => {
        if (res.data.code === 0) {
          if (res.data.data.oneQuestionPage === 1 || res.data.data.oneQuestionPage === '1') {
            this.$router.replace({ path: '/questionnaire/editquestion', query: { status: 5, messageId: this.$route.query.messageId } })
          } else {
            that.editorData = res.data.data
            if (this.$route.query && this.$route.query.imMessageId) {
              that.editorData.messageId = that.$route.query.imMessageId
            }
            if (that.editorData.id) {
              that.dataId = that.editorData.id
              if (that.showhis==='true' && that.dataId) {
                that.showRightIcon = true;
              }
            }
            that.fields = JSON.parse(res.data.data.jsonContent)
          }
        }
      })
    },
    getData() {
      const params = {
        id: this.dataId
      }
      Toast.loading('加载中');
      Api.getCheckDataformResult(params).then(res => {
        Toast.clear()
        if (res.data.code === 0) {
          this.editorData = res.data.data
          const list = JSON.parse(res.data.data.jsonContent)
          this.fields.push(...list)
        }
      })
    },
    ToOtherPage() {
      this.$router.push({
        path: `/baseinfo/index/history`,
        query: {
          id: this.dataId
        }
      })
    }
  },
  created() {
    this.showhis = this.$getShowHist()
    console.log('this.showhis', this.showhis)
  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
  position: relative;
}
img {
  width: 19px;
  height: 19px;
  position: absolute;
  top: 14px;
  right: 13px;
}
</style>
