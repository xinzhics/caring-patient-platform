<template>
  <section style="background:#FAFAFA">
    <navBar @toHistoryPage="ToOtherPage" :pageTitle="pageTitle" :rightIcon="historyImg" :showRightIcon="showhis" />
    <attrPage v-if="fields && fields.length >= 1" :all-fields="fields" ref="attrPage" :submit="submit"
              :doctor-name="myallInfo.doctorName"
              :organ-name="myallInfo.organName"
              :show-doctor-name="statusAll.hasShowDoctor===0"
              :show-org-name="statusAll.hasShowOrgName===0"
              myTitle="提交"
              style="margin-top: 10px"
    ></attrPage>

  </section>
</template>
<script>
import Api from '@/api/Content.js'
export default {
  name: 'baseinfo',
  components: {
    attrPage:() => import('@/components/arrt/editorIndex'),
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      pageTitle: '基本信息',
      historyImg: require('@/images/set.png'),
      fields: [],
      allInfo: {},
      allArray: [],
      myallInfo: {},
      statusAll: {},
      formResultId: "",
      showhis: false,
      submitStatus: false,
      imMessageId: '',
      imRecommendReceipt: 0,
    }
  },
  created() {
    if (this.$route.query && this.$route.query.imMessageId) {
      // im点击跳转
      this.imMessageId = this.$route.query.imMessageId
      this.imRecommendReceipt = 1
    }
    this.getInfo()
    if (localStorage.getItem('myallInfo')) {
      this.myallInfo = JSON.parse(localStorage.getItem('myallInfo'))
      Api.searchDoctor({id: this.myallInfo.doctorId}).then(res => {
        this.myallInfo.doctorName=res.data.data.name
      })
    }
    this.getStatus()
    this.showhis = this.$getShowHist() === 'true'
  },
  methods: {
    ToOtherPage() {
      this.$router.push({
        path: `/baseinfo/index/history`,
        query: {
          id: this.formResultId,
        }
      })
    },
    getStatus() {
      Api.regGuidegetGuide({}).then((res) => {
        if (res.data.code === 0) {
          this.statusAll = res.data.data
          localStorage.setItem('statusAll', JSON.stringify(res.data.data))
        }
      })
    },
    getInfo() {
      const params = {
        formEnum: 'BASE_INFO',
        patientId: localStorage.getItem('userId')
      }
      Api.gethealthform(params).then((res) => {
        if (res.data.code === 0) {
          this.formResultId = res.data.data.id
          this.allInfo = res.data.data
          if (this.imMessageId) {
            this.allInfo.messageId = this.imMessageId
          }
          this.fields = []
          this.fields.push(...res.data.data.fieldList)
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
        if (this.submitStatus) {
          return
        }
        this.submitStatus = true
        if (k.content) {
          this.allArray = this.$cleanFieldRepeat(this.allArray, k.content)
        }
        let params = {
          "businessId": this.allInfo.businessId,
          "category": this.allInfo.category,
          "formId": this.allInfo.formId,
          "jsonContent": JSON.stringify(this.allArray),
          "messageId": this.allInfo.messageId,
          "name": this.allInfo.name,
          "userId": this.allInfo.userId,
          fillInIndex: -1,
          imRecommendReceipt: this.imRecommendReceipt
        }
        if (this.allInfo.id) {
          params.id = this.allInfo.id
          Api.gethealthformSubPut(params).then((res) => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              if (this.imRecommendReceipt === 1) {
                this.$router.go(-1)
              }else {
                this.$router.replace('/home')
              }
            }
          }).catch(error => {
            this.submitStatus = false
          })
        } else {
          Api.gethealthformSub(params).then((res) => {
            this.submitStatus = false
            if (res.data.code === 0) {
              this.$vux.toast.text('提交成功!', 'center')
              if (this.imRecommendReceipt === 1) {
                this.$router.go(-1)
              }else {
                this.$router.replace('/home')
              }
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
