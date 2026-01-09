<template>
  <section :style="{width: pageWidth + 'px'}">
    <headNavigation leftIcon="arrow-left" :title="pageTitle ? pageTitle : '健康日志'"
                    @onBack="back"></headNavigation>
    <div v-if="fields.length > 0" style="padding: 10px; overflow-y: auto" :style="{height: pageHeight + 'px'}">
      <form-result-detail :field-list="fields"></form-result-detail>
    </div>
    <div class="caring-form-edit-button" @click="goEditor">编辑</div>
  </section>
</template>
<script>
import Vue from 'vue'
import { getCheckDataformResult } from '@/api/Content.js'
import {Sticky, Icon, Col, Row} from 'vant'
import FormResultDetail from '@/components/formDetail/formResultDetail'

Vue.use(Sticky)
Vue.use(Col)
Vue.use(Row)
Vue.use(Icon)
export default {
  components: {
    FormResultDetail,
    attrPage: () => import('@/components/arrt/editorIndex')
  },
  data () {
    return {
      pageTitle: '',
      fields: [],
      allInfo: {},
      formResultId: this.$route.query.formResultId,
      pageWidth: window.innerWidth,
      pageHeight: window.innerHeight - 46 - 32 - 47 - 20
    }
  },
  mounted () {
    this.getInfo()
    setTimeout(() => {
      this.pageTitle = localStorage.getItem('pageTitle')
    }, 200)
  },
  methods: {
    back () {
      this.$router.go(-1)
    },
    goEditor () {
      this.$router.push({
        path: `/healthCalendar/editor`,
        query: {
          content: this.formResultId
        }
      })
    },
    getInfo () {
      getCheckDataformResult({id: this.formResultId}).then((res) => {
        if (res.code === 0) {
          this.allInfo = res.data
          this.fields = []
          this.fields.push(...JSON.parse(res.data.jsonContent))
        }
      })
    }
  }
}
</script>

<style lang="less" scoped>
/deep/ .vux-header {
  height: 50px;
  position: relative;
}

.caring-form-edit-button {
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
