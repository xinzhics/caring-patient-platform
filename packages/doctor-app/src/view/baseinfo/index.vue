<template>
  <section style="background:#FAFAFA">
    <x-header :left-options="{backText: ''}">{{pageTitle ? pageTitle : allInfo.name}}</x-header>
    <img src="../../images/set.png" @click="ToOtherPage" v-if="showhis==='true'">
    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>
<script>
  import Api from '@/api/Content.js'
  import attrPage from '@/components/arrt/editorIndex'
  import {
    Cell
  } from 'vux'

  export default {
    name: 'baseinfo',
    components: {
      attrPage,
      Cell
    },
    data() {
      return {
        editorImg: require('@/assets/my/editor.png'),
        headerImg: require('@/assets/logo.png'),
        pageTitle: localStorage.getItem('pageTitle'),
        fields: [],
        allInfo: {},
        allArray: [],
        myallInfo: {},
        statusAll: {},
        name: this.$getDictItem('doctor'),
        formId: "",
        showhis: ""
      }
    },
    created() {

      this.showhis = this.$getShowHist()
    },
    mounted() {
      this.getInfo()
      this.getStatus()
    },
    methods: {
      ToOtherPage() {
        this.$router.push({
          path: `/baseinfo/index/history`,
          query: {
            id: this.formId,
          }
        })
      },
      getStatus() {
        Api.regGuidegetGuide({}).then((res) => {
          if (res.data.code === 0) {
            this.statusAll = res.data.data
          }
        })
      },
      getInfo() {
        const params = {
          formEnum: 'BASE_INFO',
          patientId: localStorage.getItem('patientId')
        }
        Api.gethealthform(params).then((res) => {
          if (res.data.code === 0) {
            this.allInfo = res.data.data
            if (this.$route.query && this.$route.query.imMessageId) {
              this.allInfo.messageId = this.imMessageId
            }
            this.fields = res.data.data.fieldList
            this.formId = res.data.data.id

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
          if (k.content) {
            this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
          }
          if (this.allInfo.id) {
            let params = {
              "businessId": this.allInfo.businessId,
              "category": this.allInfo.category,
              "formId": this.allInfo.formId,
              "jsonContent": JSON.stringify(this.allArray),
              "messageId": this.allInfo.messageId,
              "name": this.allInfo.name,
              "userId": this.allInfo.userId,
              "id": this.allInfo.id,
            }
            if (this.$route.query && this.$route.query.imMessageId) {
              params.imRecommendReceipt = 1
            }
            Api.gethealthformSubPut(params).then((res) => {
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功!', 'center')
                this.$router.go(-1)
              }
            }).catch(error => {
              this.submitStatus = false
            })
          } else {
            let params = {
              businessId: this.allInfo.businessId,
              category: this.allInfo.category,
              jsonContent: JSON.stringify(this.allArray),
              messageId: this.allInfo.messageId,
              name: this.allInfo.name,
              oneQuestionPage: this.allInfo.oneQuestionPage,
              showTrend: this.allInfo.showTrend,
              fillInIndex: -1,
              formId: this.allInfo.formId,
              userId: this.allInfo.userId,
            }
            if (this.$route.query && this.$route.query.imMessageId) {
              params.imRecommendReceipt = 1
            }
            Api.saveFormResultStage(params).then((res) => {
              if (res.data.code === 0) {
                this.$vux.toast.text('提交成功!', 'center')
                this.$router.go(-1)
              }
            }).catch(error => {
              this.submitStatus = false
            })
          }
        }
      },
    }
  }

</script>
<style lang="less" scoped>
  img {
    width: 19px;
    height: 19px;
    position: absolute;
    top: 14px;
    right: 13px;
  }

  /deep/.vux-label {
    font-size: 16px !important;
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

</style>
