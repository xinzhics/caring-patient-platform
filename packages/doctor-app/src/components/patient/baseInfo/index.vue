<template>
  <div>
    <attrPage v-if="fields && fields.length >= 1" :all-fields="fields" ref="attrPage" :submit="submit"
              :doctor-name="patientInfo.doctorName"
              :organ-name="patientInfo.organName"
              :show-doctor-name="false"
              :show-org-name="false"
              myTitle="提交"
              style="margin-top: 10px"
    ></attrPage>
  </div>
</template>

<script>
import Api from '@/api/Content'
export default {
  props: {
    patientInfo: {
      type: Object,
      default: {}
    }
  },
  components: {
    attrPage: () => import('@/components/arrt/editorIndex')
  },
  data() {
    return {
      fields: [],
      allInfo: {},
      formId: '',
      // 医生不需要查看患者的医生和机构
      // statusAll: {},
      allArray: [],
    }
  },
  mounted() {
    this.getInfo()
    // this.getStatus()
  },
  methods: {
    // getStatus() {
    //   Api.regGuidegetGuide({}).then((res) => {
    //     if (res.data.code === 0) {
    //       this.statusAll = res.data.data
    //     }
    //   })
    // },
    getInfo() {
      const params = {
        formEnum: 'BASE_INFO',
        patientId: localStorage.getItem('patientId')
      }
      Api.gethealthform(params).then((res) => {
        if (res.data.code === 0) {
          this.allInfo = res.data.data
          if (this.$route.query && this.$route.query.imMessageId && this.$route.query.dictItemType === 'BASE_INFO') {
            this.allInfo.messageId = this.$route.query.imMessageId
          }
          this.fields = res.data.data.fieldList
          this.formId = res.data.data.id

        }
      })
    },

    submit(k) {
      if (k.MarkTip === 'has') {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content);
        }
        this.allArray.push(k.page)
        this.$refs.attrPage.nextPage();
      } else {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content);
        }
        if (this.allInfo.id) {
          const params = {
            "businessId": this.allInfo.businessId,
            "category": this.allInfo.category,
            "formId": this.allInfo.formId,
            "jsonContent": JSON.stringify(this.allArray),
            "messageId": this.allInfo.messageId,
            "name": this.allInfo.name,
            "userId": this.allInfo.userId,
            "id": this.allInfo.id,
            fillInIndex: -1
          }
          if (this.$route.query && this.$route.query.imMessageId && this.$route.query.dictItemType === 'BASE_INFO') {
            params.imRecommendReceipt = 1
          }

          Api.gethealthformSubPut(params).then((res) => {
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              this.$emit('baseInfoSubmit')
              if (this.$route.query && this.$route.query.imMessageId) {
                this.$router.back()
              }else {
                this.$emit('baseInfoSubmit')
              }
            }
          })
        } else {
          const params = {
            "businessId": this.allInfo.businessId,
            "category": this.allInfo.category,
            "formId": this.allInfo.formId,
            "jsonContent": JSON.stringify(this.allArray),
            "messageId": this.allInfo.messageId,
            "name": this.allInfo.name,
            "userId": this.allInfo.userId,
            fillInIndex: -1
          }
          if (this.$route.query && this.$route.query.imMessageId && this.$route.query.dictItemType === 'BASE_INFO') {
            params.imRecommendReceipt = 1
          }
          Api.gethealthformSub(params).then((res) => {
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              console.log('===========', (this.$route.query && this.$route.query.imMessageId))
              if (this.$route.query && this.$route.query.imMessageId) {
                console.log('===========')
                this.$router.back()
              }else {
                this.$emit('baseInfoSubmit')
              }
            }
          })
        }
      }
    },
  }
}

</script>

<style scoped lang="less">

</style>
