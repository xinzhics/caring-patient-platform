<template>
  <section :style="{width: pageWidth + 'px'}">
    <navBar :pageTitle=" pageTitle !== undefined ? pageTitle: '随访'" />
    <div v-if="fields.length > 0" style="padding: 10px; overflow-y: auto" :style="{height: pageHeight + 'px'}">
      <form-result-detail :field-list="fields"></form-result-detail>
    </div>
    <div v-if="planId" class="caring-form-edit-button" @click="goEditor">编辑</div>
  </section>
</template>
<script>
  import Vue from 'vue'
  import Api from '@/api/Content.js'
  import { Sticky,Icon,Col, Row } from 'vant'
  import FormResultDetail from "@/components/formDetail/formResultDetail";
  Vue.use(Sticky)
  Vue.use(Col)
  Vue.use(Row)
  Vue.use(Icon)
  export default {
    components: {
      FormResultDetail,
      attrPage:() => import('@/components/arrt/editorIndex'),
      navBar: () => import('@/components/headers/navBar')
    },
    data() {
      return {
        pageTitle: '',
        fields: [],
        allInfo: {},
        formId: this.$route.query.formId,
        planId: this.$route.query.planId,
        pageWidth: window.innerWidth,
        injectionModel: this.$route.query.injectionModel, // 是否是注射模式
        pageHeight: window.innerHeight - 46 - 32 - 47 - 20,
      }
    },
    mounted() {
      this.getInfo()
      /*setTimeout(() => {
        this.pageTitle = localStorage.getItem('pageTitle')
      }, 200)*/
    },
    methods: {
      goEditor() {
        const from = this.$route.query.from
        this.$router.push({
          path: `/custom/follow/${this.planId}/editor`,
          query: {
            content: this.formId,
            pageTitle: this.pageTitle,
            from: from,
            onback: this.$route.query.onback
          }
        })
      },
      getInfo() {
        Api.formResult(this.$route.query.formId).then((res) => {
          if (res.data.code === 0) {
            console.log(res)
            this.pageTitle = res.data.data.name
            this.allInfo = res.data.data
            if (!this.planId) {
              this.planId = res.data.data.businessId
            }
            this.fields = []
            this.fields.push(...JSON.parse(res.data.data.jsonContent))
          }
        })
      },
    }
  }
</script>


<style lang="less" scoped>
  /deep/ .vux-header {
    height: 50px;
    position: relative;
  }
  .caring-form-edit-button{
    height: 47px;
    text-align: center;
    background: rgb(103, 224, 167);
    color: rgb(255, 255, 255);
    margin: 19px auto 13px;
    line-height: 47px;
    border-radius: 24px;
    position: absolute;
    bottom: 1px;
    width: 90%;
    left: 0;
    right: 0;
  }
</style>
