<template>
    <div style="min-height: 100vh;background: #FFFFFF">
      <x-header  style="margin: 0 !important;" :left-options="{backText: ''}">
        {{title}}
      </x-header>
      <div class="content">
          <div class="point-out">
              <img :src="require('@/assets/my/White-exclamation-mark.png')" width="20px" height="20px" alt="">
              <div>本表单仅为数据填写预览</div>
          </div>
      </div>
      <attrPage :all-fields="fields"></attrPage>
    </div>
</template>

<script>
import Api from '@/api/followUp.js'
import attrPage from './attrPage'
export default {
  components:{attrPage},
  name: "scheduleForm",
  data(){
    return{
      title:this.$route.query.palnName,
      planId:this.$route.query.planId,
      fields:[]
    }
  },
  mounted() {
    this.getPlanForm()
  },
  methods:{
    getPlanForm(){
      Api.getPlanForm(this.planId).then(res=>{
        console.log(res)
        if (res.data.code === 0){
          if (res.data.data){
            this.fields =JSON.parse(res.data.data.fieldsJson)
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
