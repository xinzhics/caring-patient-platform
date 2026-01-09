<template>
  <section>
    <x-header :left-options="{backText: ''}">{{pageTitle}}</x-header>
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
import {queryPlanInfo, checkPatientAbleAddFormResult} from '@/api/plan.js'
import attrPage from '@/components/arrt/editorIndex'
import { Toast, Dialog, Button } from 'vant'
import Vue from 'vue'
import FormCheckFuncEvent from "../../components/arrt/formVue";
Vue.use(Dialog)
Vue.use(Button)
export default {
  components: { attrPage },
  data() {
    return {
      fields: [],
      editorData: {},
      allArray: [],
      pageTitle: '',
      patientId: localStorage.getItem('patientId'),
      planId: this.$route.params.planId,
      messageId: this.$route.params.messageId,
      injectionTimeOut: 0,
      injectionModel: false,  // 是否是注射表单
      injectionDisableSubmit: false, // 当监听到不可选择的日期不可提交时， 设置为true
      injectionDisableDialog: false,  // 显示日期选择不可提交的提示框
      DataId: ''
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.imMessageId) {
      this.backUrl = undefined
      this.messageId = this.$route.query.imMessageId
    }
    if (this.$route.query.content) {
      this.DataId = this.$route.query.content
      this.getData()
    }else{
       this.getInfo()
    }
    this.queryPlan(this.planId)
    // 监听用户选择的检验日期。是否可以满足注射记录的填写
    FormCheckFuncEvent.$on('CHECK_TIME_CHANGE', (val)=>{
      this.checkTimeChange(val)
    })
  },
  methods: {
    // 查询随访计划的设置
    queryPlan(planId) {
      queryPlanInfo(planId).then(res => {
        const plan = res.data.data
        this.pageTitle = plan.name
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
        if (k.content) {
          this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
        }
        if (this.submitStatus) {
          return
        }
        this.submitStatus = true
        if (this.injectionModel && this.injectionDisableSubmit) {
          this.injectionDisableDialog = true
          this.submitStatus = false
          return;
        }
        this.$refs.attrPage.setLoading(true)
        if (this.editorData.id) {
          let params = {
            businessId: this.editorData.businessId,
            category: this.editorData.category,
            formId: this.editorData.formId,
            jsonContent: JSON.stringify(this.allArray),
            messageId: this.editorData.messageId,
            name: this.editorData.name,
            userId: this.editorData.userId,
            id: this.editorData.id
          }
          if (this.$route.query && this.$route.query.imMessageId) {
            // im的推荐消息，才传这个为1
            params.imRecommendReceipt = 1
          }

          Api.gethealthformSubPut(params).then(res => {
            if (res.data.code === 0) {
              Toast('提交成功!')
              this.$router.go(-1)
            }
          }).catch(error => {
            this.$refs.attrPage.setLoading(false)
            this.submitStatus = false
          })
        } else {
          let params = {
            businessId: this.editorData.businessId,
            category: this.editorData.category,
            formId: this.editorData.formId,
            jsonContent: JSON.stringify(this.allArray),
            messageId: this.editorData.messageId,
            name: this.editorData.name,
            userId: localStorage.getItem('patientId')
          }

          if (this.$route.query && this.$route.query.imMessageId) {
            // im的推荐消息，才传这个为1
            params.imRecommendReceipt = 1
          }

          Api.gethealthformSub(params).then(res => {
            if (res.data.code === 0) {
              Toast('提交成功!')
              if (this.editorData.scoreQuestionnaire === 1) {
                // 评分表单，需要跳转到结果页
                this.$router.replace({
                  path: '/score/result',
                  query: { dataId: res.data.data.id, title: this.pageTitle, backUrl: '/mypatientHome?id=' + localStorage.getItem('patientId')}
                })
                return
              }
              this.$router.go(-1)
            }
          }).catch(error => {
            this.$refs.attrPage.setLoading(false)
            this.submitStatus = false
          })
        }
      }
    },
    getData() {
      const that = this
      const params = {
        id: that.DataId
      }
      Api.getCheckDataformResult(params).then(res => {
        console.log(res);
        if (res.data.code === 0) {
          that.editorData = res.data.data
          that.fields = JSON.parse(res.data.data.jsonContent)
        }
      })
    },
    getInfo() {
      const that = this
      Api.getCustomPlanFormResult(this.planId, this.messageId).then(res => {
        if (res.data.code === 0 && res.data.data) {
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
