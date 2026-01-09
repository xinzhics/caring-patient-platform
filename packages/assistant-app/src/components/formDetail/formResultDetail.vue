<template>
  <div style="background: #FFFFFF; padding: 15px 18px; border-right: 8px">
    <div v-for="(field, index) in fieldList" :key="field.id" style="min-height: 45px; justify-items: center">
      <!-- 处理特殊组件  -->
      <descindex :field="field" v-if="field.widgetType === Constants.CustomFormWidgetType.Desc"></descindex>
      <!-- 分割线组件 -->
      <split-lineindex v-else-if="field.widgetType === Constants.CustomFormWidgetType.SplitLine"
                       :field="field"></split-lineindex>
      <!-- 用户输入值的组件 -->
      <div v-else>
        <single-row-com :field="field" @openImagePreview="openImagePreview"></single-row-com>
        <template v-if="field.widgetType === Constants.CustomFormWidgetType.Radio
        || field.widgetType === Constants.CustomFormWidgetType.CheckBox
        || field.widgetType === Constants.CustomFormWidgetType.DropdownSelect">
          <div v-if="field.values && field.values.length > 0">
            <div v-for="(val) in field.values" :key="val.attrValue">
              <div v-if="val.questions && val.questions.length > 0">
                <div class="caring-form-result"></div>
                <div v-for="(ques, quesIndex) in val.questions" :key="ques.id"
                     style="min-height: 45px; justify-items: center">
                  <single-row-com :field="ques" @openImagePreview="openImagePreview"></single-row-com>
                  <div class="caring-form-result"
                       v-if="quesIndex < val.questions.length - 1 && val.questions.length > 1 "></div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <!-- 如果是单选，多选，可以触发子题目的题型。走上面 -->
        <div class="caring-form-result" v-if="index < fieldList.length - 1 && fieldList.length > 1 "></div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import {Col, Row, ImagePreview} from 'vant'
import SingleRowCom from './SingleRowCom'
import Descindex from '../arrt/descindex'
import SplitLineindex from '../arrt/splitLineindex'
import '@vant/touch-emulator'

Vue.use(Col)
Vue.use(Row)
Vue.use(ImagePreview)

export default {
  components: {SplitLineindex, Descindex, SingleRowCom},
  props: {
    fieldList: {
      type: Array,
      // eslint-disable-next-line vue/require-valid-default-prop
      default: []
    }
  },
  data () {
    return {
      imageList: [],
      imageFieldMap: new Map()
    }
  },
  mounted () {
    this.queryImageList(this.fieldList)
  },
  methods: {
    queryImageList (fieldList) {
      if (fieldList) {
        for (let i = 0; i < fieldList.length; i++) {
          const field = fieldList[i]
          if (field.widgetType === this.Constants.CustomFormWidgetType.Avatar || field.widgetType === this.Constants.CustomFormWidgetType.SingleImageUpload ||
            field.widgetType === this.Constants.CustomFormWidgetType.MultiImageUpload) {
            if (field.values) {
              let values = field.values
              for (let j = 0; j < values.length; j++) {
                if (values[j].attrValue) {
                  if (this.imageFieldMap.get(field.id) === undefined) {
                    console.log(field.id, this.imageList.length, this.imageList)
                    this.imageFieldMap.set(field.id, this.imageList.length)
                  }
                  this.imageList.push(values[j].attrValue)
                }
              }
            }
          } else if (field.widgetType === this.Constants.CustomFormWidgetType.Radio || field.widgetType === this.Constants.CustomFormWidgetType.CheckBox ||
            field.exactType === this.Constants.FieldExactType.Diagnose) {
            if (field.values) {
              let values = field.values
              for (let j = 0; j < values.length; j++) {
                if (values[j].questions) {
                  this.queryImageList(values[j].questions)
                }
              }
            }
          }
        }
      }
    },
    openImagePreview (fieldId, index) {
      let startIndex = this.imageFieldMap.get(fieldId)
      console.log('openImagePreview', fieldId, index)
      console.log('this.imageFieldMap', this.imageFieldMap, startIndex)
      let resultIndex = startIndex + index
      ImagePreview({
        images: this.imageList,
        startPosition: resultIndex
      })
    }

  }
}

</script>

<style>
.caring-form-result {
  width: 100%;
  height: 0px;
  opacity: 1;
  border: 1px solid #EEEEEE;
}

</style>
