<template>
  <section>
    <navBar @toHistoryPage="ToOtherPage" :pageTitle="pageTitle !== undefined ? pageTitle : '健康日志'" :rightIcon="setImg"
            :showRightIcon="showRightIcon" :backUrl="backUrl"/>
    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
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
        setImg: require('../../images/set.png'),
        fields: [],
        editorData: {},
        dataId: undefined,
        allArray: [],
        showhis: "",
        submitStatus: false,
        showRightIcon: false,
        backUrl: '/healthCalendar/index',
        imRecommendReceipt: 0
      }
    },
    mounted() {
      if (this.$route.query.messageId) {
        Api.reminderLogOpenMessage(this.$route.query.messageId)
      }
      if (this.$route.query && this.$route.query.imMessageId) {
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
          this.$refs.attrPage.nextPage();
        } else {
          if (this.submitStatus) {
            return
          }
          this.submitStatus = true
          if (k.content) {
            this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
          }
          let params = {
            businessId: this.editorData.businessId,
            category: this.editorData.category,
            formId: this.editorData.formId,
            jsonContent: JSON.stringify(this.allArray),
            messageId: this.editorData.messageId,
            name: this.editorData.name,
            imRecommendReceipt: this.imRecommendReceipt
          }
          if (this.editorData.id) {
            params.id = this.editorData.id
            params.userId = this.editorData.userId
            Api.gethealthformSubPut(params).then((res) => {
              this.submitStatus = false
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功!', 'center')
                // 从IM 消息过来的。会到IM 消息去
                if (this.imRecommendReceipt === 1) {
                  this.$router.go(-1)
                } else {
                  this.$router.replace(this.backUrl)
                }
              }
            }).catch(error => {
              this.submitStatus = false
            })
          } else {
            params.planDetailTimeId = this.$route.query.planDetailTimeId;
            params.userId = localStorage.getItem('userId');
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
                // 从IM 或者从待办过来。提交后直接返回。
                if (this.imRecommendReceipt === 1 || this.$route.query.planDetailTimeId) {
                  this.$router.go(-1)
                } else {
                  this.$router.replace(this.backUrl)
                }
              }
            }).catch(error => {
              this.submitStatus = false
            })
          }
        }
      },
      getInfo() {
        let messageId = ''
        if (this.$route.query && this.$route.query.imMessageId) {
          messageId = this.$route.query.imMessageId
        } else if (this.$route.query && this.$route.query.messageId) {
          messageId =  this.$route.query.messageId
        }

        Api.formOrFormResult(null, 5, messageId).then(res => {
          if (res.data.code === 0) {
            if (res.data.data.oneQuestionPage === 1 || res.data.data.oneQuestionPage === '1') {
              this.$router.replace({
                path: '/questionnaire/editquestion',
                query: {status: 4, messageId: this.$route.query.messageId}
              })
            } else {
              this.editorData = res.data.data
              if (this.$route.query && this.$route.query.imMessageId) {
                this.editorData.messageId = this.$route.query.imMessageId
              }
              if (this.editorData.id) {
                this.dataId = this.editorData.id
                if (this.showhis==='true' && this.dataId) {
                  this.showRightIcon = true;
                }
              }
              this.fields = []
              const list = JSON.parse(res.data.data.jsonContent)
              this.fields.push(...list)
              console.log(this.fields)
            }
          }
        })
      },
      getData() {
        const that = this
        const params = {
          id: that.dataId
        }
        Api.getCheckDataformResult(params).then((res) => {
          if (res.data.code === 0) {
            that.editorData = res.data.data
            that.fields = JSON.parse(res.data.data.jsonContent)
            console.log(that.fields)
          }
        })
      },
      ToOtherPage() {
        this.$router.push({
          path: `/baseinfo/index/history`,
          query: {
            id: this.dataId,
          }
        })
      },
    },
    created() {
      this.showhis = this.$getShowHist()
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
