<template>
  <section>
    <navBar :pageTitle="pageTitle" :backUrl="backUrl" :backQuery="backQuery" @toHistoryPage="ToOtherPage" :rightIcon="historyImg" :showRightIcon="showhis"></navBar>
    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
    <van-dialog v-model="injectionDisableDialog" title="温馨提示" :showConfirmButton="false">
      <p class="dialog-p-content">距离上次注射时间已超过{{injectionTimeOut}}周，需重新口服阿立哌唑14天后才可再次注射</p>
      <div class="caring-form-button" style="background: #66E0A7; color: #FFFFFF; width: 60%; margin: 25px 20%;" @click="() => injectionDisableDialog = false">
        <span>我知道了</span>
      </div>
    </van-dialog>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import Vue from 'vue'
import {queryPlanInfo, checkPatientAbleAddFormResult} from '@/api/plan.js'
import { Toast, Button, Dialog } from 'vant'
import FormCheckFuncEvent from "../../components/arrt/formVue";

Vue.use(Button)
Vue.use(Dialog)
export default {
  components: {
    attrPage: () => import('@/components/arrt/editorIndex'),
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      fields: [],
      editorData: {},
      allArray: [],
      pageTitle: '',
      planId: this.$route.params.planId,
      backUrl: '/custom/follow/' +  this.$route.params.planId,
      backQuery: {},
      messageId: this.$route.params.messageId,
      dataId: undefined,
      submitStatus: false,
      patientId: localStorage.getItem('userId'),
      injectionTimeOut: 0,
      historyImg: require('@/images/set.png'),
      showhis: false,
      injectionModel: false,  // 是否是注射表单
      injectionDisableSubmit: false, // 当监听到不可选择的日期不可提交时， 设置为true
      injectionDisableDialog: false,  // 显示日期选择不可提交的提示框
      imRecommendReceipt: 0
    }
  },
  created() {
    if (this.$route.query && this.$route.query.onback) {
      this.backUrl = undefined
    }else if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = undefined
      this.messageId = this.$route.query.imMessageId
      this.imRecommendReceipt = 1
    } else {
      if (this.$route.query.from === 'calendar') {
        this.backUrl = undefined
        this.backQuery.title = this.$route.query.pageTitle
      }
    }

    const routerData = localStorage.getItem("routerData")
    const path = this.$route.path
    if (this.$route.query && this.$route.query.pageTitle) {
      this.pageTitle = this.$route.query.pageTitle
      this.backQuery.title = this.$route.query.pageTitle
    } else if (routerData && routerData.length > 0) {
      const routerDataArray = JSON.parse(routerData);
      let patientMyFile = routerDataArray.patientMyFile
      let patientMyFeatures = routerDataArray.patientMyFeatures
      let tempTitle = undefined
      if (patientMyFile && patientMyFile.length > 0) {
        for (let i = 0; i < patientMyFile.length; i++) {
          if (path.indexOf(patientMyFile[i].path) > -1) {
            tempTitle = patientMyFile[i].name;
          }
        }
      }
      if (!tempTitle && patientMyFeatures && patientMyFeatures.length > 0) {
        for (let i = 0; i < patientMyFeatures.length; i++) {
          if (path.indexOf(patientMyFeatures[i].path) > -1) {
            tempTitle = patientMyFeatures[i].name;
          }
        }
      }
      if (tempTitle) {
        this.pageTitle = tempTitle
      }
    }
    if (this.messageId) {
      Api.reminderLogOpenMessage(this.messageId)
    }
    this.queryPlan(this.planId)
  },
  mounted() {
    if (this.$route.query.content) {
      this.dataId = this.$route.query.content
      this.getData()
      this.showhis = this.$getShowHist() === 'true'
    } else {
      this.getInfo()
    }
    // 监听用户选择的检验日期。是否可以满足注射记录的填写
    FormCheckFuncEvent.$on('CHECK_TIME_CHANGE', (val)=>{
       this.checkTimeChange(val)
    })
  },
  methods: {
    ToOtherPage() {
      this.$router.push({
        path: `/baseinfo/index/history`,
        query: {
          id: this.dataId,
        }
      })
    },
    // 查询随访计划的设置
    queryPlan(planId) {
      queryPlanInfo(planId).then(res => {
        const plan = res.data.data
        if (plan.planModel === 1) {
          this.injectionModel = true
        }
      })
    },

    /**
     * 检查检验日期是否符合注射表单的提交要求
     * @param val
     */
    checkTimeChange(val) {
      if (this.injectionModel) {
        checkPatientAbleAddFormResult(this.planId, this.patientId, val).then(res => {
          console.log(res)
          if (res.data) {
            const data = res.data
            if (data.data > 0) {
              this.injectionTimeOut = data.data
              this.injectionDisableSubmit = true
              this.injectionDisableDialog = true
            } else {
              this.injectionDisableSubmit = false
            }
          }
        })
      }
    },

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
        }
        if (this.injectionModel && this.injectionDisableSubmit) {
          this.injectionDisableDialog = true
          this.submitStatus = false
          return;
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
          params.userId = this.editorData.userId
          params.id = this.editorData.id

          Api.gethealthformSubPut(params).then(res => {
            this.submitStatus = false
            Toast.clear
            if (res.data.code === 0) {
              Toast('提交成功!')
              if (this.$route.query.onback) {
                this.$router.go(-this.$route.query.onback)
              }else if (this.imRecommendReceipt === 1 || this.backUrl === undefined) {
                this.$router.go(-1)
              } else {
                this.$router.replace(`/custom/follow/${this.planId}`)
              }
              Toast.clear
            }
          }).catch(err => {
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
          Api.gethealthformSub(newObj).then(res => {
            this.submitStatus = false
            Toast.clear
            if (res.data.code === 0) {
              Toast('提交成功!')
              if (this.editorData.scoreQuestionnaire === 1) {
                // 评分表单，需要跳转到结果页
                if (this.imRecommendReceipt === 1) {
                  this.$router.replace({
                    path: '/score/result',
                    query: { dataId: res.data.data.id, title: this.pageTitle, backUrl: '/im/index'}
                  })
                }else {
                  this.$router.replace({
                    path: '/score/result',
                    query: { dataId: res.data.data.id, title: this.pageTitle, backUrl: this.backUrl}
                  })
                }
                Toast.clear
                return
              }
              if (this.$route.query.planDetailTimeId || this.imRecommendReceipt === 1 || this.backUrl === undefined) {
                this.$router.go(-1)
              } else {
                this.$router.replace(this.backUrl)
              }
            }
          }).catch(err => {
            this.submitStatus = false
          })
        }
      }
    },
    getInfo() {
      const that = this
      Api.getCustomPlanFormResult(this.planId, this.messageId).then(res => {
        if (res.data.code === 0 && res.data.data) {
          this.pageTitle = res.data.data.name
          // 发现ID存在。并且是评分问卷。那么认定为患者已经填写了。 评分问卷不能修改，所以直接去成绩页面
          if (res.data.data.id && res.data.data.scoreQuestionnaire === 1) {
            this.$router.replace({
              path: '/score/show',
              query: {
                dataId: res.data.data.id,
                title: this.pageTitle,
                id: this.planId,
              }
            })
          } else {
            if (res.data.data.oneQuestionPage === 1 || res.data.data.oneQuestionPage === '1') {
              this.$router.replace({
                path: '/questionnaire/editquestion',
                query: {status: 6, planId: this.planId, messageId: this.messageId}
              })
            } else {
              that.editorData = res.data.data
              if (that.editorData.id) {
                that.dataId = that.editorData.id
              }
              that.fields = JSON.parse(res.data.data.jsonContent)
            }
          }
        }
      })
    },
    getData() {
      const that = this
      const params = {
        id: that.dataId
      }
      Api.getCheckDataformResult(params).then(res => {
        if (res.data.code === 0) {
          that.pageTitle = res.data.data.name
          that.editorData = res.data.data
          that.fields = JSON.parse(res.data.data.jsonContent)
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
/deep/ .van-dialog__header{
    font-family: SourceHanSansCN, SourceHanSansCN;
    font-weight: 500;
    font-size: 18px;
    color: #000000;
    line-height: 27px;
    font-style: normal;
    background: linear-gradient( 180deg, #CDFFE8 0%, #FFFFFF 100%);
  }

.dialog-p-content{
  font-family: SourceHanSansCN, SourceHanSansCN;
  font-weight: 400;
  font-size: 14px;
  color: #666666;
  line-height: 20px;
  text-align: left;
  font-style: normal;
  padding: 27px 26px;
}
</style>
