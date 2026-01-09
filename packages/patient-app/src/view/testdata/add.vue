<template>
  <section>
    <navBar :pageTitle="title ? title : '新增/编辑'"
            :backQuery="{id: this.$route.query.id,title:this.$route.query.title}" :showRightIcon="showRightIcon"
            :rightIcon="rightIcon" @toHistoryPage="ToOtherPage"></navBar>
    <attrPage v-if="fields && fields.length > 0" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>

<script>
  import Api from '@/api/Content.js'
  import { Toast } from 'vant'
  export default {
    components: {
      attrPage: () => import('@/components/arrt/editorIndex'),
      navBar: () => import('@/components/headers/navBar'),
    },
    name: "add",
    data() {
      return {
        fields: [],
        editorData: {},
        allArray: [],
        title: this.$route.query.title,
        dataId: undefined,
        showhis:"",
        showRightIcon: false,
        rightIcon: require('@/images/set.png'),
        submitStatus: false,
        imRecommendReceipt: 0
      }
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
          Toast.loading({
            message: '提交中...',
            forbidClick: true,
          });
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
              Toast.clear
              if (res.data.code === 0) {
                Toast('提交成功!')
                if (this.imRecommendReceipt === 1) {
                  this.$router.go(-1)
                }else {
                  this.$router.replace({path: '/monitor/show', query: {id: this.$route.query.id,title:this.$route.query.title}})
                }
              }
            }).catch(error => {
              this.submitStatus = false
            })
          } else {
            params.userId = localStorage.getItem('userId')
            params.planDetailTimeId = this.$route.query.planDetailTimeId
            // 过滤掉无用的参数
            const newObj = {};
            for (const key in params) {
              if (params[key] !== '' && typeof params[key] !== 'undefined') {
                newObj[key] = params[key];
              }
            }
            Api.gethealthformSub(params).then((res) => {
              this.submitStatus = false
              Toast.clear
              if (res.data.code === 0) {
                Toast('提交成功!')
                if (this.editorData.scoreQuestionnaire === 1) {
                  // 评分表单，需要跳转到结果页
                  if (this.imRecommendReceipt === 1) {
                    this.$router.replace({
                      path: '/score/result',
                      query: {id: this.$route.query.id, dataId: res.data.data.id, title: this.$route.query.title, backUrl: '/im/index'}
                    })
                  } else {
                    this.$router.replace({
                      path: '/score/result',
                      query: {id: this.$route.query.id, dataId: res.data.data.id, title: this.$route.query.title, backUrl: '/monitor/show'}
                    })
                  }
                } else {
                  if (this.$route.query.planDetailTimeId || this.imRecommendReceipt === 1) {
                    this.$router.go(-1)
                  } else {
                    this.$router.replace({
                      path: '/monitor/show',
                      query: {id: this.$route.query.id, title: this.$route.query.title}
                    })
                  }
                }
              }
            }).catch(error => {
              this.submitStatus = false
            })
          }
        }
      },
      getInfo() {
        const params = {
          businessId: this.$route.query.id,
          messageId: this.$route.query.imMessageId ? this.$route.query.imMessageId : this.$route.query.messageId,
          patientId: localStorage.getItem('userId')
        }
        Api.monitoringIndicatorsFormResult(params).then((res) => {
          if (res.data.code === 0) {
            this.title = res.data.data.name
            if (res.data.data.id && res.data.data.scoreQuestionnaire === 1) {
              this.$router.replace({
                path: '/score/show',
                query: {
                  dataId: res.data.data.id,
                  title: this.title,
                  id: params.businessId,
                }
              })
            } else {
              this.editorData = res.data.data
              if (this.editorData.id) {
                this.dataId = this.editorData.id
                if (this.showhis==='true' && this.dataId) {
                  this.showRightIcon = true;
                }
              }
              //路径如果包含messageId, 表示从模板进入，在保存数据时也保存messageId
              if (this.$route.query && this.$route.query.messageId) {
                this.editorData.messageId = this.$route.query.messageId
              }
              const list = JSON.parse(res.data.data.jsonContent)
              this.fields = [...list]
            }
          }
        })
      },
      getData() {
        const params = {
          id: this.dataId
        }
        Api.getCheckDataformResult(params).then((res) => {
          this.title = res.data.data.name
          if (res.data.code === 0) {
            this.editorData = res.data.data
            const list = JSON.parse(res.data.data.jsonContent)
            this.fields = [...list]
          }
        })
      },
      ToOtherPage(){
        this.$router.push({
          path: `/baseinfo/index/history`,
          query: {
            id: this.dataId,
          }
        })
      },
    },
    created() {
      if (this.$route.query && this.$route.query.imMessageId) {
        // im点击跳转
        this.imRecommendReceipt = 1
      }
      if (this.$route.query.messageId) {
        Api.reminderLogOpenMessage(this.$route.query.messageId)
      }
      if (this.$route.query && this.$route.query.dataId) {
        this.dataId = this.$route.query.dataId
        this.getData()
      } else if (this.$route.query && this.$route.query.id) {
        this.getInfo()
      }
      this.showhis=this.$getShowHist()
      if (this.showhis==='true' && this.dataId) {
        this.showRightIcon = true;
      }
    }
  }
</script>

<style scoped lang="less">
  img {
    width: 19px;
    height: 19px;
    position: absolute;
    top: 14px;
    right: 13px;
  }

  /deep/ .vux-header {
    height: 50px;
    position: relative;
  }
</style>
