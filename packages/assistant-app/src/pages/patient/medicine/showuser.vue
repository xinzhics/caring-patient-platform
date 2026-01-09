<template>
  <div>
    <van-sticky :offset-top="0">
      <headNavigation leftIcon="arrow-left" title="使用说明" @onBack="$router.back()" @showpop="toHistory" ></headNavigation>
    </van-sticky>
    <div style="padding:10px 15px 30px; margin-top: 15px; text-align:left;overflow-y: scroll;background:#fff">
      <div>
        <span style="color:rgba(0,0,0,0.85);font-size:13px">药品名称：</span><span style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.name}}</span>
      </div>
      <div style="margin-top:5px">
        <span style="color:rgba(0,0,0,0.85);font-size:13px">药品通用名称：</span><span style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.genericName}}</span>
      </div>
      <div style="margin:5px 0px">
        <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【适用症状】</p>
        <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.applicableDisease||'暂无'}}</p>
      </div>
      <div style="margin:5px 0px">
        <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【用法用量】</p>
        <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.dosage||'暂无'}}</p>
      </div>
      <div style="margin:5px 0px">
        <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【禁忌】</p>
        <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.taboo||'暂无'}}</p>
      </div>
      <div style="margin:5px 0px">
        <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【不良反应】</p>
        <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.sideEffects||'暂无'}}</p>
      </div>
      <div style="margin:5px 0px">
        <p style="color:rgba(0,0,0,0.85);font-size:13px;margin:5px 0px">【药物互作用】</p>
        <p style="color:rgba(0,0,0,0.65);font-size:12px">{{alldata.interaction||'暂无'}}</p>
      </div>
    </div>
  </div>
</template>
<script>
import { getSysDrugsDetail } from '@/api/drugsApi.js'
import Vue from 'vue'
import {Col, Icon, Row, Sticky} from 'vant'
Vue.use(Row)
Vue.use(Col)
Vue.use(Sticky)
Vue.use(Icon)
export default {
  data () {
    return {
      alldata: {}
    }
  },
  mounted () {
    let that = this
    if (that.$route.query && that.$route.query.drugsId) {
      that.getInfo(that.$route.query.drugsId)
    }
  },
  methods: {
    getInfo (id) {
      getSysDrugsDetail(id).then((res) => {
        if (res.code === 0) {
          this.alldata = res.data
        }
      })
    }
  }
}
</script>
