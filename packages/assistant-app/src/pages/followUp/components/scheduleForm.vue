<template>
    <div ref="searchBar" :style="{background: '#FFFFFF'}">
      <van-sticky :offset-top="0">
        <headNavigation leftIcon="arrow-left"  :title="title" @onBack="onBack"></headNavigation>
      </van-sticky>
      <div class="content">
          <div class="point-out">
              <img :src="require('@/assets/my/white-exclamation-mark.png')" width="20px" height="20px" alt="">
              <div>本表单仅为数据填写预览</div>
          </div>
      </div>
      <attrPage v-show="showAttrPage" ref="followUpAttrPage"></attrPage>
    </div>
</template>

<script>
import {getPlanForm} from '@/api/followUp.js'
import attrPage from './attrPage'
export default {
  components: {attrPage},
  name: 'scheduleForm',
  data () {
    return {
      title: '',
      planId: '',
      showAttrPage: false
    }
  },
  activated () {
    this.title = this.$route.query.palnName
    this.planId = this.$route.query.planId
    this.getPlanForm()
  },
  methods: {
    onBack () {
      this.$router.go(-1)
      this.$refs.followUpAttrPage.clearFields()
      this.showAttrPage = false
    },
    getPlanForm () {
      getPlanForm(this.planId).then(res => {
        console.log(res)
        if (res.code === 0) {
          if (res.data) {
            const fields = JSON.parse(res.data.fieldsJson)
            console.log('form activated getPlanForm', this.$refs.followUpAttrPage, fields)
            this.showAttrPage = true
            this.$refs.followUpAttrPage.showFields(fields)
          }
        }
      })
    }
  }
}
</script>

<style scoped lang="less">
.content{
  padding: 13px;
  .point-out{
    padding: 13px 18px 13px 15px;
    background: linear-gradient(to right, #5292FF, #6EA8FF);
    border-radius: 7px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #FFFFFF;
  }
}
</style>
