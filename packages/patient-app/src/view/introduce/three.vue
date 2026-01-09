<template>
  <section>
    <x-header :left-options="{backText: '',showBack: Canparent,preventGoBack:true}" @on-click-back="backParent"
              style="margin-bottom:0px!important">{{ allInfo.name }}
    </x-header>
    <attrPage v-if="fields && fields.length > 0" :all-fields="fields" ref="attrPage" myTitle="提交"
              :submit="submit"></attrPage>
  </section>
</template>
<script>
import Api from '@/api/Content.js'
import attrPage from '@/components/arrt/editorIndex'

export default {
  components: {attrPage},
  data() {
    return {
      fields: [],
      allInfo: {},
      allArray: [],
      Canparent: true,
    }
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    backParent() {
      this.$router.go(-1)
    },
    submit(k) {
      if (k.MarkTip === 'has') {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content)
        }
        this.allArray.push(k.page)
        this.$refs.attrPage.nextPage();
      } else {
        if (k.content) {
          this.allArray = this.allArray.concat(k.content)
        }
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
        if (this.allInfo.id) {
          params.id = this.allInfo.id
        }
        Api.saveFormResultStage(params).then((res) => {
          if (res.data.code === 0) {
            this.$vux.toast.text('提交成功!', 'center')
            this.$router.replace({
              path: '/home'
            })
          }
        })
      }
    },
    getInfo() {
      const params = {
        formEnum: 'HEALTH_RECORD',
        patientId: localStorage.getItem('userId')
      }
      Api.gethealthform(params).then((res) => {
        if (res.data.code === 0) {
          this.allInfo = res.data.data
          this.fields = res.data.data.fieldList
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
</style>
