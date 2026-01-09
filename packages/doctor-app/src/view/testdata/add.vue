<template>
  <section>
    <x-header :left-options="{backText: ''}">{{pageTitle ? pageTitle : '监测数据'}}</x-header>
    <img src="../../images/set.png" @click="ToOtherPage" v-if="showhis==='true'">
    <attrPage v-if="fields && fields.length > 0" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>

<script>
import Api from '@/api/Content.js'
import attrPage from '@/components/arrt/editorIndex'
import { Toast } from 'vant'

export default {
  components: {attrPage},
  name: "add",
  data() {
    return {
      fields: [],
      editorData: {},
      pageTitle: this.$route.query.title,
      allArray: [],
      dataId: '',
      showhis:"",
      submitStatus: false
    }
  },
  mounted(){
    console.log(this.$route.query.title);
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
        if (this.editorData.id) {
          let params = {
            "businessId": this.editorData.businessId,
            "category": this.editorData.category,
            "formId": this.editorData.formId,
            "jsonContent": JSON.stringify(this.allArray),
            "messageId": this.editorData.messageId,
            "name": this.editorData.name,
            "userId": localStorage.getItem('patientId'),
            "id": this.editorData.id,
          }
          if (this.$route.query && this.$route.query.imMessageId) {
            params.imRecommendReceipt = 1
          }

          Api.gethealthformSubPut(params).then((res) => {
            Toast.clear
            if (res.data.code === 0) {
              Toast('提交成功!')
              this.$router.go(-1)
            }
          }).catch(error => {
            Toast.clear
            this.submitStatus = false
          })
        } else {
          let params = {
            "businessId": this.editorData.businessId,
            "category": this.editorData.category,
            "fieldList": this.allArray,
            "formId": this.editorData.formId,
            "jsonContent": JSON.stringify(this.allArray),
            "messageId": this.editorData.messageId,
            "name": this.editorData.name,
            "userId": localStorage.getItem('patientId')
          }
          if (this.$route.query && this.$route.query.imMessageId) {
            params.imRecommendReceipt = 1
          }
          Api.gethealthformSub(params).then((res) => {
            Toast.clear
            if (res.data.code === 0) {
              Toast('提交成功!')
              if (this.editorData.scoreQuestionnaire === 1) {
                // 评分表单，需要跳转到结果页
                if (this.$route.query && this.$route.query.imMessageId) {
                  this.$router.go(-1)
                } else {
                  this.$router.replace({
                    path: '/score/result',
                    query: {id: this.$route.query.id, dataId: res.data.data.id, title: this.$route.query.title, backUrl: '/monitor/show'}
                  })
                }
              } else {
                this.$router.go(-1)
              }
            }
          }).catch(error => {
            Toast.clear
            this.submitStatus = false
          })
        }
      }
    },
    getInfo() {
      const params = {
        businessId: this.$route.query.id,
        messageId: this.$route.query.imMessageId,
        patientId: localStorage.getItem('patientId')
      }
      Api.monitoringIndicatorsFormResult(params).then((res) => {
        if (res.data.code === 0) {
          this.editorData = res.data.data
          //路径如果包含messageId, 表示从模板进入，在保存数据时也保存messageId
          if (this.$route.query && this.$route.query.messageId) {
            this.editorData.messageId = this.$route.query.messageId
          }
          this.fields = JSON.parse(res.data.data.jsonContent)
        }
      })
    },
    getData() {
      const params = {
        id: this.dataId
      }
      Api.getCheckDataformResult(params).then((res) => {

        if (res.data.code === 0) {
          this.editorData = res.data.data
          this.fields = JSON.parse(res.data.data.jsonContent)
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
    if (this.$route.query && this.$route.query.dataId) {
      this.dataId = this.$route.query.dataId
      this.getData()
    } else if (this.$route.query && this.$route.query.id) {
      this.getInfo()
    }
    this.showhis=this.$getShowHist()
    if(!this.$route.query.dataId){
        this.showhis='false'
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
