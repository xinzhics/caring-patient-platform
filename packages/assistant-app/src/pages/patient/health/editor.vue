<template>
  <section style="background:#FAFAFA">
    <van-sticky>
      <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '疾病信息'"
                      :rightIcon="showhis === 'true' ? historyImg : ''"
                      @onBack="back" @showpop="toHistory"></headNavigation>
    </van-sticky>
    <attrPage v-if="fields && fields.length >= 1" :all-fields="fields" ref="attrPage" :submit="submit"
              myTitle="提交"
              style="margin-top: 10px"
    ></attrPage>
    <van-overlay :show="showDialog" @click="showDialog = false">
      <div class="wrapper" @click.stop>
        <div style="font-weight: 600;font-size: 19px">信息导入提醒</div>
        <div style="margin-top: 23px;font-size: 15px">是否导入您上次所填写的疾病信息，便于
          重复填写相同的信息，节省您的填写时间！
        </div>

        <div style="margin-top: 28px;padding: 0 20px;display: flex">
          <div class="healthbtn" @click="showDialog=false"
               style="border: 1px solid #67E0A7;color: #67E0A7;margin-right: 43px">不导入
          </div>
          <div class="healthbtn"
               @click="addOldData"
               style="border: 1px solid #FFF;color: #FFF;background: #67E0A7">
            导入
          </div>
        </div>
      </div>
    </van-overlay>
  </section>
</template>
<script>
import Vue from 'vue'
import {
  queryHealthRecordByMessageId,
  patientGetForm,
  byCategory,
  getCheckDataformResult,
  saveFormResultStage,
  gethealthformSubPut,
  gethealthformSub
} from '@/api/Content.js'
import {Sticky, Overlay, Toast} from 'vant'
import {patientDetail} from '@/api/patient'

Vue.use(Sticky)
Vue.use(Overlay)
Vue.use(Toast)
export default {
  name: 'baseinfo',
  components: {
    attrPage: () => import('@/components/arrt/editorIndex')
  },
  data () {
    return {
      pageTitle: '',
      editorImg: require('@/assets/my/editor.png'),
      fields: [],
      allInfo: {},
      historyImg: require('@/assets/patient/set.png'),
      allArray: [],
      formResultId: '',
      showhis: false,
      submitStatus: false,
      diseaseInformationStatus: 0,
      oldFields: [],
      showDialog: false,
      imMessageId: undefined
    }
  },
  created () {
    if (this.$route.query && this.$route.query.imMessageId) {
      // im点击跳转
      this.imMessageId = this.$route.query.imMessageId
    }
    if (this.$route.query.formId) {
      this.showhis = this.$getShowHist()
      this.formResultId = this.$route.query.formId
      this.getInfo()
    } else if (this.imMessageId) {
      // 使用messageId 查询对应的疾病信息，不存在时 后端返回一个疾病信息的空表单
      this.queryHealthRecordByMessageId()
    } else {
      this.patientGetForm()
      this.byCategory()
    }
  },
  mounted () {
    this.getPatientInfo()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    toHistory () {
      this.$router.push({
        path: `/patient/baseinfo/index/history`,
        query: {
          id: this.formResultId
        }
      })
    },
    back () {
      this.$router.go(-1)
    },
    getPatientInfo () {
      patientDetail(localStorage.getItem('patientId')).then(res => {
        if (res.code === 0) {
          if (res.data.diseaseInformationStatus !== undefined) {
            this.diseaseInformationStatus = Number(res.data.diseaseInformationStatus)
          }
        }
      })
    },
    queryHealthRecordByMessageId () {
      queryHealthRecordByMessageId(this.imMessageId).then(res => {
        if (res.code === 0) {
          this.allInfo = res.data
          if (this.imMessageId) {
            this.allInfo.messageId = this.imMessageId
          }
          this.fields = []
          this.fields.push(...JSON.parse(res.data.jsonContent))
          if (!this.allInfo.id) {
            this.byCategory()
          } else {
            this.formResultId = this.allInfo.id
          }
        }
      })
    },
    addOldData () {
      this.fields = []
      setTimeout(() => {
        this.fields.push(...this.oldFields)
        this.showDialog = false
        console.log(this.fields)
      }, 200)
    },
    /**
     * 查询系统配置的表单
     */
    patientGetForm () {
      patientGetForm().then(res => {
        console.log('==========', res.data)
        if (res.code === 0) {
          this.allInfo = res.data
          this.allInfo.formId = this.allInfo.id
          if (this.imMessageId) {
            this.allInfo.messageId = this.imMessageId
          }
          this.fields = []
          this.fields.push(...JSON.parse(res.data.fieldsJson))
        }
      })
    },
    byCategory () {
      byCategory('HEALTH_RECORD').then(res => {
        if (res.code === 0) {
          if (res.data) {
            this.oldFields = JSON.parse(res.data.jsonContent)
            this.showDialog = true
          }
        }
      })
    },
    getInfo () {
      getCheckDataformResult({id: this.formResultId}).then((res) => {
        if (res.code === 0) {
          this.allInfo = res.data
          if (this.imMessageId) {
            this.allInfo.messageId = this.imMessageId
          }
          this.fields = []
          this.fields.push(...JSON.parse(res.data.jsonContent))
          this.formResultId = res.data.id
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
        let params = {
          'businessId': this.allInfo.businessId,
          'category': this.allInfo.category,
          'formId': this.allInfo.formId,
          'jsonContent': JSON.stringify(this.allArray),
          'messageId': this.allInfo.messageId,
          'name': this.allInfo.name,
          'userId': localStorage.getItem('patientId'),
          fillInIndex: -1
        }
        if (this.formResultId) {
          params.id = this.formResultId
        }
        if (this.imMessageId && this.diseaseInformationStatus === 1) {
          params.imRecommendReceipt = 1
        }
        if (this.diseaseInformationStatus === 0) {
          saveFormResultStage(params).then((res) => {
            this.submitStatus = false
            if (res.code === 0) {
              Toast('提交成功')
              this.$router.go(-1)
            }
          }).catch(error => {
            console.log(error)
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
          })
        } else {
          if (this.formResultId) {
            params.id = this.formResultId
            // 修改的时候走这里。才会产生表单历史记录
            gethealthformSubPut(params).then((res) => {
              this.submitStatus = false
              if (res.code === 0) {
                Toast('提交成功')
                this.$router.go(-1)
              }
            }).catch(error => {
              console.log(error)
              this.submitStatus = false
              this.$refs.attrPage.setLoading(false)
            })
          } else {
            gethealthformSub(params).then((res) => {
              this.submitStatus = false
              if (res.code === 0) {
                Toast('提交成功')
                this.$router.go(-1)
              }
            }).catch(error => {
              console.log(error)
              this.submitStatus = false
              this.$refs.attrPage.setLoading(false)
            })
          }
        }
      }
    }
  }
}
</script>
<style lang="less" scoped>
.healthbtn {
  width: 102px;
  height: 29px;
  border-radius: 15px;
  text-align: center;
  line-height: 29px;
  font-size: 15px;
  font-weight: 400;
}

img {
  width: 19px;
  height: 19px;
  position: absolute;
  top: 14px;
  right: 13px;
}

::v-deep .vux-label {
  font-size: 17px;
}

.endItem {
  margin-top: 10px;
  background-color: #fff;
}

/deep/ .vux-header {
  height: 50px;
  position: relative;
}

.wrapper {
  width: 80%;
  background: #fff;
  border-radius: 9px;
  font-size: 19px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 19px;
  margin: 300px auto 0;
}
</style>
