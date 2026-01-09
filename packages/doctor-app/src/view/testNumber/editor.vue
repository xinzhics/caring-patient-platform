<template>
  <section>
    <x-header :left-options="{backText: ''}">{{pageTitle ? pageTitle : '检验数据'}}</x-header>
     <img src="../../images/set.png" @click="ToOtherPage" v-if="showhis==='true'">
    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit"></attrPage>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import attrPage from '@/components/arrt/editorIndex'
export default {
  components: {
    attrPage
  },
  data() {
    return {
      fields: [],
      pageTitle: localStorage.getItem('pageTitle'),
      editorData: {},
      allArray: [],
      DataId: '',
      showhis: '',
      submitStatus: false,
      imMessageId: undefined
    }
  },
  created() {
    this.showhis = this.$getShowHist()
    if(!this.$route.query.content){
      this.showhis='false'
    }
  },
  mounted() {
    if (this.$route.query && this.$route.query.imMessageId) {
      this.imMessageId = this.$route.query.imMessageId
    }
    if (this.$route.query.content) {
      this.DataId = this.$route.query.content
      this.getData()
    } else {
      this.getInfo()
    }
  },
  methods: {
    ToOtherPage() {
      this.$router.push({
        path: `/baseinfo/index/history`,
        query: {
          id: this.DataId
        }
      })
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
            params.imRecommendReceipt = 1
          }
          Api.gethealthformSubPut(params).then(res => {
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              this.$router.go(-1)
            }
          }).catch(error => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
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
            params.imRecommendReceipt = 1
          }
          Api.gethealthformSub(params).then(res => {
            console.log(res)
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功', 'center')
              this.$router.go(-1)
            }
          }).catch(error => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
          })
        }
      }
    },
    getInfo() {
      const that = this
      const planType = 3
      Api.formOrFormResult(null, planType, this.imMessageId).then(res => {
        if (res.data.code === 0) {
          that.editorData = res.data.data
          if (that.editorData.id) {
            that.dataId = that.editorData.id
          }
          that.fields = JSON.parse(res.data.data.jsonContent)
        }
      })
    },
    getData() {
      const that = this
      const params = {
        id: that.DataId
      }
      Api.getCheckDataformResult(params).then(res => {
        if (res.data.code === 0) {
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
