<template>
  <div style="min-height: 100vh;background: #FFFFFF">
    <navBar :pageTitle="title"></navBar>
    <div class="content">
      <div class="point-out">
        <img :src="require('@/assets/my/White-exclamation-mark.png')" width="20px" height="20px" alt="">
        <div style="margin-left: 11px">本表单仅为数据填写预览</div>
      </div>
    </div>
    <attrPage v-if="fields.length > 0" :all-fields="fields"></attrPage>
  </div>
</template>

<script>
import Api from '@/api/followUp.js'

export default {
  components: {
    attrPage: () => import('./attrPage'),
    navBar: () => import('@/components/headers/navBar'),
  },
  name: "scheduleForm",
  data() {
    return {
      title: this.$route.query.palnName,
      planId: this.$route.query.planId,
      fields: []
    }
  },
  created() {
    this.getPlanForm()
  },
  methods: {
    /**
     * 请求数据
     */
    getPlanForm() {
      Api.getPlanForm(this.planId).then(res => {
        console.log(res)
        if (res.data.code === 0) {
          if (res.data.data) {
            if (res.data.data.fieldsJson) {
              this.fields = JSON.parse(res.data.data.fieldsJson)
            } else {
              this.fields = JSON.parse(res.data.data.jsonContent)
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.content {
  padding: 13px;

  .point-out {
    padding: 13px 18px 13px 15px;
    background: #FFFDFA;
    border-radius: 7px;
    display: flex;
    color: #FFBE8B;
    //justify-content: space-between;
    border: 1px solid #FFBE8B;
    align-items: center;
  }
}
</style>
