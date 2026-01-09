<template>
  <div>
    <div v-for='(info,index) in questions' :key="index">
      <div
        v-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "hospital" '>
        <hospital @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info' />
      </div>
      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText'>
        <singleLine @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLineText'>
        <textArea @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio'>
        <newHisSingleChoice @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
        <newHisMultipleChoice @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect'>
        <dropdown @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Number'>
        <number @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Date'>
        <date @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>

      <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload'>
        <upphoto @noClikc="clickForm" :isHealth="$route.query.isHealth" :field='info'  />
      </div>
    </div>
    <van-overlay :show="showDialog" @click="showDialog = false">
      <div class="wrapper" @click.stop>
        <div>该内容不可修改</div>
        <div>如需修改请新建疾病信息</div>
      </div>
    </van-overlay>
  </div>
</template>

<script>
export default {
  components: {
    singleLine: () => import('@/components/arrt/Singleline/field-Singleline'),
    textArea: () => import('@/components/arrt/textArea/textarea'),
    newHisSingleChoice: () => import('@/components/arrt/newHisSingleChoice/singleChoice'),
    newHisMultipleChoice: () => import('@/components/arrt/newHisMultipleChoice/multipleChoice'),
    dropdown: () => import('@/components/arrt/dropdown/dropdown'),
    number: () => import('@/components/arrt/number/number'),
    upphoto: () => import('@/components/arrt/picture/picture'),
    date: () => import('@/components/arrt/date/date'),
    hospital: () => import('@/components/arrt/hospital/hospital'),
  },
  name: "editor-child",
  props: {
    questions: {
      type: Array
    }
  },
  data(){
    return{
      showDialog:false
    }
  },
  methods:{
    clickForm() {
      this.showDialog = true
      setTimeout(() =>{
        this.showDialog = false
      },3000)
    },
  }
}
</script>
<style lang="less" scoped>
.wrapper{
  width: 55%;
  background: #fff;
  border-radius: 9px;
  font-size: 19px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 67px 58px;
  margin: 300px auto 0;
}
</style>
