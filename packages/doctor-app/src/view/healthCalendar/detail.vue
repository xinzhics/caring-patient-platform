<template>
  <section style="width: 100vw">
    <navBar :pageTitle=" pageTitle !== undefined ? pageTitle: '健康日志'" />
    <div v-if="fields.length > 0" style="padding: 10px; overflow-y: auto" :style="{height: pageHeight + 'px'}">
      <form-result-detail :field-list="fields"></form-result-detail>
    </div>
    <div v-if="messageId && allInfo.scoreQuestionnaire === 1">
      <!--  评分表单 只能评论   -->
      <van-row style="padding: 0px 20px; display: flex; align-items: center; margin: 19px 0 13px 0; justify-content: center">
        <div class="caring-form-button" style="height: 47px; background: #66E0A7; color: #FFFFFF; width: 94%; border-radius: 30px" @click="toDo">
          <span>评论</span>
        </div>
      </van-row>
    </div>
    <div v-else-if="messageId">
      <!--  评论和编辑   -->
      <van-row style="padding: 0px 20px; display: flex; align-items: center; margin: 19px 0 13px 0; justify-content: center">
        <div class="caring-form-button" style="height: 47px; background: #fff; color: #66E0A7; width: 47%; margin-right: 12px;; border-radius: 30px"  @click="goEditor">
          <span>编辑</span>
        </div>

        <div class="caring-form-button" style="height: 47px; background: #66E0A7; color: #FFFFFF; width: 47%; margin-left: 12px; border-radius: 30px" @click="toDo">
          <span>评论</span>
        </div>
      </van-row>
    </div>
    <div v-else class="caring-form-edit-button" @click="goEditor">编辑</div>
    <dialog-comment ref="commentRef" @submit="submitComment"></dialog-comment>
  </section>
</template>
<script>
  import Vue from 'vue'
  import Api from '@/api/Content.js'
  import { Sticky,Icon,Col, Row } from 'vant'
  import doctorApi from "@/api/doctor"
  import dialogComment from "@/components/systemMessage/systemCommentDialog"
  import FormResultDetail from "@/components/formDetail/formResultDetail";
  Vue.use(Sticky)
  Vue.use(Col)
  Vue.use(Row)
  Vue.use(Icon)
  export default {
    components: {
      FormResultDetail,
      dialogComment,
      attrPage:() => import('@/components/arrt/editorIndex'),
      navBar: () => import('@/components/headers/navBar')
    },
    data() {
      return {
        pageTitle: '',
        fields: [],
        allInfo: {},
        messageId: '',
        isLoading: false,
        formResultId: this.$route.query.formResultId,
        pageWidth: window.innerWidth,
        pageHeight: window.innerHeight - 46 - 32 - 47 - 20
      }
    },
    mounted() {
      if (this.$route.query && this.$route.query.messageId) {
        this.messageId = this.$route.query.messageId
      }
      this.getInfo()
      setTimeout(() => {
        this.pageTitle = localStorage.getItem('pageTitle')
      }, 200)
    },
    methods: {
      // 提交评论
      submitComment(val) {
        if (this.isLoading) {
          return
        }
        this.isLoading = true;
        let patientId = localStorage.getItem('patientId')
        doctorApi.doctorCommentMessage({
          commentContent: val,
          doctorId: this.$route.query.doctorId,
          doctorName: this.$route.query.doctorName,
          messageId: this.$route.query.messageId,
          patientId: patientId,
        })
          .then(res => {
            this.$refs.commentRef.close()
            this.$toast('评论成功')
            this.$router.go(-1)
          })
      },
      toDo() {
        this.$refs.commentRef.open()
      },
      goEditor() {
        this.$router.push({
          path: `/healthCalendar/editor`,
          query: {
            content: this.formResultId
          }
        })
      },
      getInfo() {
        Api.getCheckDataformResult({id: this.formResultId}).then((res) => {
          if (res.data.code === 0) {
            this.allInfo = res.data.data
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
