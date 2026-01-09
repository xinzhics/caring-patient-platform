<template>
  <section>
    <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '健康日志'"
                    :rightIcon="showhis==='true'? historyImg : ''" @showpop="toHistory"
                    @onBack="back"></headNavigation>
    <attrPage v-if="fields&&fields.length>=1" :all-fields="fields" ref="attrPage" :submit="submit" style="margin-top: 1px"></attrPage>
  </section>
</template>
<script>

import { getCheckDataformResult, gethealthformSubPut, gethealthformSub } from '@/api/Content'
import { formOrFormResult } from '@/api/formApi'
import attrPage from '@/components/arrt/editorIndex'
import {Toast} from 'vant'

export default {
  components: {
    attrPage
  },
  data () {
    return {
      pageTitle: localStorage.getItem('pageTitle'),
      historyImg: require('@/assets/patient/set.png'),
      fields: [],
      ondeInfo: {},
      allInfo: {},
      editorData: {},
      DataId: '',
      allArray: [],
      formId: '',
      showhis: '',
      submitStatus: false,
      imMessageId: undefined
    }
  },
  created () {
    this.showhis = this.$getShowHist()
    if (!this.$route.query.content) {
      this.showhis = 'false'
    }
  },
  mounted () {
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
    toHistory () {
      this.$router.push({
        path: `/patient/baseinfo/index/history`,
        query: {
          id: this.DataId
        }
      })
    },
    back () {
      this.$router.go(-1)
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
        if (this.editorData.id) {
          let params = {
            'businessId': this.editorData.businessId,
            'category': this.editorData.category,
            'formId': this.editorData.formId,
            'jsonContent': JSON.stringify(this.allArray),
            'messageId': this.editorData.messageId,
            'name': this.editorData.name,
            'userId': this.editorData.userId,
            'id': this.editorData.id
          }
          if (this.imMessageId) {
            params.imRecommendReceipt = 1
          }
          gethealthformSubPut(params).then((res) => {
            if (res.code === 0) {
              Toast('提交成功')
              this.$router.go(-1)
            }
          }).catch(() => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
          })
        } else {
          let params = {
            'businessId': this.editorData.businessId,
            'category': this.editorData.category,
            'formId': this.editorData.formId,
            'jsonContent': JSON.stringify(this.allArray),
            'messageId': this.editorData.messageId,
            'name': this.editorData.name,
            'userId': localStorage.getItem('patientId')
          }
          if (this.imMessageId) {
            params.imRecommendReceipt = 1
          }
          gethealthformSub(params).then((res) => {
            if (res.code === 0) {
              Toast('提交成功')
              this.$router.go(-1)
            }
          }).catch(() => {
            this.submitStatus = false
            this.$refs.attrPage.setLoading(false)
          })
        }
      }
    },
    getInfo () {
      const that = this
      const planType = 5
      formOrFormResult(null, planType, this.imMessageId).then(res => {
        if (res.code === 0) {
          that.editorData = res.data
          if (that.editorData.id) {
            that.dataId = that.editorData.id
          }
          that.fields = JSON.parse(res.data.jsonContent)
        }
      })
    },
    getData () {
      const that = this
      const params = {
        id: that.DataId
      }
      getCheckDataformResult(params).then((res) => {
        if (res.code === 0) {
          that.editorData = res.data
          that.fields = JSON.parse(res.data.jsonContent)
          this.DataId = res.data.id
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
