<template>
  <section style="background:#FAFAFA">
    <x-header style="margin: 0" :left-options="{backText: '',showBack: Canparent,preventGoBack:true}"
              @on-click-back="backParent">{{ this.$getDictItem("patient") }}注册
    </x-header>
    <attrPage v-if="fields && fields.length > 0" :all-fields="fields" ref="attrPage" myTitle="完成注册"
              :submit="submit"></attrPage>
  </section>
</template>
<script>
import {Group, PopupPicker, PopupRadio} from 'vux'
import attrPage from '@/components/arrt/editorIndex'
import Api from '@/api/Content.js'
import wx from "weixin-js-sdk";

export default {
  name: 'baseinfo',
  components: {
    Group,
    PopupRadio,
    PopupPicker,
    attrPage
  },
  data() {
    return {
      fields: [],
      allInfo: {},
      Canparent: true
    }
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    backParent() {
      const enableIntro = localStorage.getItem('enableIntro')
      if (enableIntro !== undefined && enableIntro === '0') {
        this.$router.replace('/')
      } else {
        wx.closeWindow()
      }
    },
    getInfo() {
      const params = {
        formEnum: 'BASE_INFO',
        patientId: localStorage.getItem('userId'),
        completeEnterGroup: 0
      }
      Api.gethealthform(params).then((res) => {
        if (res.data.code === 0) {
          this.allInfo = res.data.data
          this.fields = res.data.data.fieldList
        }
      })
    },
    submit(k) {
      if (k.MarkTip === 'has') {
        this.$refs.attrPage.nextPage();
      } else {
        console.log(this.allInfo,k)
        let params = {
          businessId: this.allInfo.businessId,
          category: this.allInfo.category,
          jsonContent:JSON.stringify(this.allInfo.fieldList),
          messageId: this.allInfo.messageId,
          name: this.allInfo.name,
          oneQuestionPage: this.allInfo.oneQuestionPage,
          showTrend: this.allInfo.showTrend,
          userId: localStorage.getItem('userId'),
          fillInIndex: -1,
          formId: this.allInfo.formId
        }
        if (this.allInfo.id) {
          params.id = this.allInfo.id
        }
        Api.saveFormResultStage(params).then((res) => {
          if (res.data.code === 0) {
            this.$vux.toast.text('提交成功!', 'center')
            this.$router.replace({
              path: '/questionnaire/loginSuccessful'
            })

          }
        })
      }
    },
  },
  beforeRouteLeave(to, from, next) {
    if (to.path === '/home') {
      wx.closeWindow()
    }else {
      next()
    }
  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
}
</style>
