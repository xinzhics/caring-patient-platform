<template>
  <section>
    <navBar :pageTitle="pageTitle !== undefined ? pageTitle : '疾病信息'" />
    <div style="padding: 0 15px;background: #F7F7F7 ;">
      <div class="card" v-for="(item,index) in fields" :key="index">
          <div style="font-size: 15px">{{item.label}}：</div>
        <div v-if="item.values.length === 1" style="width: 100%;border-radius: 9px;margin-top: 15px" v-for="(i,k) in item.values" :key="k">
          <img width="100%" style="border-radius: 9px" height="225px" :src="i.attrValue" alt="">
        </div>
        <div v-if="item.values.length>1" style="margin-top: 15px">
          <van-row gutter="20">
            <van-col v-for="(i,k) in item.values" :key="k"  span="12">
              <img width="151px" style="border-radius: 9px" height="78px" :src="i.attrValue" alt="">
            </van-col>
          </van-row>
        </div>
        <div style="font-size: 13px;font-weight: 600" v-if="item.values.length===0">
          请上传图片
        </div>
      </div>
      <div style="height: 47px;text-align: center;background: #67E0A7;color: #fff;margin: 19px 0 13px 0;line-height: 47px;border-radius: 24px" @click="goEditor">编辑</div>
    </div>
  </section>
</template>
<script>
import Vue from 'vue'
import Api from '@/api/Content.js'
import { Sticky,Icon,Col, Row } from 'vant'
Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Icon)
export default {
  components: {
    navBar: () => import('@/components/headers/navBar')
  },
  data() {
    return {
      pageTitle: '',
      fields: [],
    }
  },
  created() {
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 100)
  },
  mounted() {
    this.getInfo()
  },
  methods: {
    goEditor() {
      this.$router.push({
        path:'/health/editor',
        query:{
          formId:this.$route.query.formId,
          isHealth:1,
        }
      })
    },
    getInfo() {
      Api.formResult(this.$route.query.formId).then((res) => {
        if (res.data.code === 0) {
          this.fields = []
          this.fields.push(...JSON.parse(res.data.data.jsonContent))
          console.log(this.fields)
        }
      })
    },
  }
}
</script>


<style lang="less" scoped>
.card{
  margin-top: 19px;
  padding: 13px 19px;
  background: #fff;
  border-radius: 9px;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  align-items: center;
}
</style>
