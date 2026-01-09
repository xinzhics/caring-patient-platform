<template>
  <div class="box1">
    <div>
      <!-- 头部 -->
      <div style="text-align:center;color: #333333;font-size: 18px;font-weight:500;position: relative;background:#fff;padding-bottom:16px;padding-top:13px">
        <img @click="closeBrowser" src="../../images/left.png" style="position:absolute ;left:10px;top:15px" />
        详情
      </div>
    </div>

    <!-- 头部下面图片 -->

    <div class="titleImg">
      <div class="top-remind">您的健康档案</div>
      <div class="top-remind">还未填写完成</div>
      <div class="doctor-name">{{docName}}{{ this.$getDictItem('doctor') }}</div>
      <div class="doctor-name1">提醒您尽快完善以下信息</div>
      <van-icon name="arrow-down" style="font-size:16px;color: #76A9FF;" />
    </div>

    <!-- 表单部分 -->
    <!-- 基本信息 -->

    <formComponents v-if="jbxx.length>0" :currentFields='jbxx' title='基本信息' />

    <!-- 健康档案 -->
    <formComponents v-if="jkda.length>0" :currentFields='jkda' title='健康档案' />

    <!-- 复查提醒 -->
    <formComponents v-if="fctx.length>0" :currentFields='fctx' title='复查提醒' />

    <!-- 健康日志 -->
    <formComponents v-if="jkrz.length>0" :currentFields='jkrz' title='健康日志' />

    <!-- 有二级分类的 -->

    <div v-for="(info,index) in formdata" v-if="formdata&&formdata.length>0">
      <planNameComponentss :currentFields='info.formdata' :title='info.planName' />
    </div>

    <!-- 提交按钮 -->
    <div class="btn-tijiao">
      <van-button :disabled='canClick' @click="Submit" round type="info">保存</van-button>
    </div>
  </div>
  </div>

</template>

<script>
import Vue from 'vue'
import { Toast } from 'vant'

Vue.use(Toast)
import Apis from '@/api/Content.js'
import wx from 'weixin-js-sdk'
import formComponents from './componts.vue'
import planNameComponentss from './compintss.vue'
export default {
  components: {
    formComponents,
    planNameComponentss
  },
  created() {
    this.getInfo()
  },
  data: function () {
    return {
      currentFields: [],
      ce: false,
      docName: '',
      jbxx: [],
      jkda: [],
      fctx: [],
      jkrz: [],
      formdata: [],
      params: [],
      canClick:false
    }
  },
  methods: {
    getInfo() {
      const params = {
        patientId: localStorage.getItem('userId')
      }
      const paramss = {
        id: localStorage.getItem('userId')
      }
      let arr = []
      // 获取患者的医生名字
      Apis.getContent(paramss).then(res => {
        // console.log(res.data.data,"=============================患者信息");
        if (res.data.code === 0) {
          this.docName = res.data.data.doctorName
        }
      })
      // 获取表单
      Apis.findIncompleteInformation(params).then(res => {
        if (res.data.code === 0) {
          arr = res.data.data.patientEditFields
          this.params = res.data.data

          // 总数据
          console.log(res.data.data, '===========================================')
          //debugger
          // json转对象
          for (let i = 0; i < arr.length; i++) {
            arr[i].formField = JSON.parse(arr[i].formField)

            // 基本信息数据
            if (arr[i].businessType === '基本信息') {
              this.jbxx.push(arr[i])
            } else if (arr[i].businessType === '疾病信息') {
              this.jkda.push(arr[i])
            } else if (arr[i].businessType === '复查提醒') {
              this.fctx.push(arr[i])
            } else if (arr[i].businessType === '健康日志') {
              this.jkrz.push(arr[i])
            } else if (arr[i].businessType !== arr[i].planName) {
              // 添加二级分类名称
              this.formdata.push({ planName: arr[i].planName, formdata: [] })

              // 去重
              const distinctValues = new Set()
              const withoutDuplicate = []
              for (const tempObj of this.formdata) {
                if (!distinctValues.has(tempObj.planName)) {
                  distinctValues.add(tempObj.planName)
                  withoutDuplicate.push(tempObj)
                }
              }
              this.formdata = withoutDuplicate
              // console.log(this.formdata)
            }
          }
          this.getFromdata(arr, this.formdata)
        }
      })
    },
    getFromdata(a, b) {
      for (let i = 0; i < a.length; i++) {
        for (let j = 0; j < b.length; j++) {
          if (a[i].planName === b[j].planName) {
            b[j].formdata.push(a[i])
          }
        }
      }
      console.log(b)
    },
    // 关闭微信浏览器

    closeBrowser() {
      wx.closeWindow()
    },

    // 提交数据
    Submit() {
      let arr = []

      this.canClick=true
      // console.log(this.formdata);
      let json = this.formdata
      for (let index = 0; index < this.formdata.length; index++) {

        this.formdata[index].formdata.forEach(item => {
          // console.log(item);
          arr.push(item)
        })
      }
      this.params.patientEditFields = []
      this.params.patientEditFields = [
        ...this.params.patientEditFields,
        ...this.jbxx,
        ...this.jkda,
        ...this.fctx,
        ...this.jkrz,
        ...arr
      ]
       let params1 = JSON.parse(JSON.stringify(this.params))

      params1.patientEditFields.forEach(item => {
        // console.log(item);
        item.formField = JSON.stringify(item.formField)
      })
       Toast.loading({
            message: '提交中',
            forbidClick: true
          })
      Apis.updateIncompleteInformationField(params1).then(res => {

        if (res.data.code === 0) {
          // this.canClick=false
          Toast('提交成功');
          setTimeout(() => {
            wx.closeWindow()
          }, 2000);

        }
      })
    }
  }
}
</script>
<style lang="less" scoped>
.btn-tijiao {
  margin-top: 12px;
  height: 70px;
  .van-button {
    display: block;
    width: 200px;
    margin: 0 auto;
    background-color: #1989fa;
  }
}
.box1 {
  background: #fff;
}

.titleImg {
  // width: 100%;
  height: 184px;
  background-image: url(../../images/topimg.png);
  // background-size:contain;
  background-size: 100%;
  margin-bottom: 29px;
  padding-top: 40px;
  padding-left: 32px;
  .top-remind {
    font-size: 22px;
    color: #ffffff;
    font-weight: bold;
  }
  .doctor-name {
    margin-top: 15px;
    color: #ffffff;
  }
  .doctor-name1 {
    color: #ffffff;
    margin-bottom: 11px;
  }
  .van-icon {
    color: #76a9ff !important ;
    font-weight: 700;
  }
}
</style>
