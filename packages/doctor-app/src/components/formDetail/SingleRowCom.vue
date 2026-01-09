<!-- 此组件用于展示， 单行， 单选， 多选， 下拉框， 数字， 时间，日期， 姓名， 性别， 出生年月， 医院， 诊断类型， 身高体重BMI， 血肌酐， 检验日期, 监测事件 -->
<template>
  <van-row style="width: 100%;">
    <div style="min-height: 45px;display: flex;align-items: center;justify-content: center;">
      <!-- 图片组件 头像组件 地址组件 -->
      <template v-if="field.widgetType === Constants.CustomFormWidgetType.Avatar
        || field.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
        || field.widgetType === Constants.CustomFormWidgetType.MultiImageUpload">
        <div v-if="!field.values || field.values.length <= 1" style="width: 100%">
          <van-col span="12">
            <div class="caring-form-result-title" style="padding: 10px 0;">{{field.label}} :</div>
          </van-col>
          <van-col span="12" style="text-align: right">
            <div v-if="field.values && field.values[0] && field.values[0].attrValue" style="padding: 10px 0">
              <van-image
                @click="openImagePreview(field.id, 0)"
                style="border: 1px solid #D5D3D3;"
                width="93"
                height="77"
                lazy-load
                :src="field.values[0].attrValue"
              />
            </div>
            <div v-else>
              -
            </div>
          </van-col>
        </div>
        <div v-else style="width: 100%">
          <van-col span="24" style="min-height: 45px; display: flex;align-items: center;justify-content: center;">
            <div class="caring-form-result-title">{{field.label}} :</div>
          </van-col>
          <van-row>
            <van-col span="8" v-for="(val, index) in field.values" :key="field.id + index">
              <van-image
                @click="openImagePreview(field.id, index)"
                style="border: 1px solid #D5D3D3;"
                width="93"
                height="77"
                lazy-load
                :src="val.attrValue"
              />
            </van-col>
          </van-row>
        </div>
      </template>
      <template v-else-if="field.widgetType === Constants.CustomFormWidgetType.MultiLineText">
        <div style="width: 100%;">
          <van-col span="24" style="min-height: 45px; display: flex;align-items: center;justify-content: center;">
            <div class="caring-form-result-title">{{field.label}} :</div>
          </van-col>
          <van-col span="24" style="margin-top: 0px; margin-bottom: 10px">
            <div class="caring-form-textarea">
              {{ getTextareaValue() }}
            </div>
          </van-col>
        </div>
      </template>
      <template v-else-if="field.widgetType === Constants.CustomFormWidgetType.Address">
        <div style="width: 100%;">
          <van-col span="24" style="min-height: 45px; display: flex;align-items: center;justify-content: center;">
            <van-col span="12">
              <div class="caring-form-result-title">{{field.label}} :</div>
            </van-col>
            <van-col span="12" style="text-align: right">
              <div class="caring-form-result-content">{{ getAddressValues() }}</div>
            </van-col>
          </van-col>
          <van-col span="24" v-if="field.hasAddressDetail === 1" style="margin-top: 0px; margin-bottom: 10px">
            <div class="caring-form-textarea">
              {{field.value}}
            </div>
          </van-col>
        </div>
      </template>
      <!-- 单行， 单选， 多选， 下拉框， 数字， 时间，日期， 姓名， 性别， 出生年月， 医院， 诊断类型， 身高体重BMI， 血肌酐， 检验日期, 监测事件 -->
      <div v-else style="width: 100%">
        <van-col span="12">
          <div class="caring-form-result-title">{{field.label}} :</div>
        </van-col>
        <van-col span="12" style="text-align: right">
          <div class="caring-form-result-content">{{ getValues() }}</div>
        </van-col>
      </div>
    </div>
  </van-row>
</template>

<script>
  import Vue from 'vue';
  import { Col, Row, Lazyload } from 'vant';

  Vue.use(Lazyload);
  Vue.use(Col);
  Vue.use(Row);
  export default {
    props: {
      field: {
        type: Object,
        default: {}
      }
    },
    methods: {

      /**
       * 获取多行文本内容
       * @returns {string|*}
       */
      getTextareaValue() {
        const item = this.field
        const value = item.values
        let attrValue
        if (value[0].attrValue) {
          attrValue = value[0].attrValue
        } else {
          return '请输入内容'
        }
        return attrValue
      },
      /**
       * 打开图片预览
       */
      openImagePreview(fieldId, index) {
        this.$emit('openImagePreview', fieldId, index)
      },
      getValues() {
        const item = this.field
        const value = item.values
        console.log('item.exactType', item.widgetType, item.exactType, value)
        if (value&&value.length>0) {
          if (item.widgetType === 'SingleLineText' && (item.exactType === 'CourseOfDisease' || item.exactType==='BMI')) {
            if (value[0].attrValue==='-'||!value[0].attrValue) {
              return '请输入内容'
            }
            if (value[0].attrValue&&value[0].attrValue!=='-'){
              return value[0].attrValue
            }
          }
          if (item.exactType ==='hospital') {
            if (value[0].valueText) {
              return  value[0].valueText
            } else {
              return '请选择'
            }
          }
          if (item.widgetType === 'SingleLineText' || item.widgetType === 'Number'||item.widgetType==='Time'
            ||item.widgetType==='Date'||item.widgetType==='FullName') { // 单行  数字 日期 姓名
            let attrValue
            if (value[0].attrValue) {
              attrValue = value[0].attrValue
              if (item.rightUnit) {
                return attrValue + item.rightUnit
              }
            } else {
              attrValue = '请输入内容'
            }
            return attrValue
          }
          if (item.widgetType === 'Radio'|| item.widgetType === 'DropdownSelect') { // 单选 下拉  医院
            let attrValue
            if (value[0].valueText) {
              attrValue = value[0].valueText
              if (item.hasOtherOption === 1 && attrValue === '其他') {
                if (item.otherValue) {
                  attrValue += "(" + item.otherValue + ")"
                }
              } else if (value[0].valuesRemark) {
                attrValue += "(" + value[0].valuesRemark + ")"
              }
            } else {
              return '请选择'
            }
            return attrValue
          }
          if (item.widgetType === 'CheckBox') { // 多选
            let str = ''
            if (value.length > 0) {
              value.forEach((val,index)=>{
                if (val.valueText) {
                  if (str.length > 0) {
                    str += '、' + val.valueText
                  } else {
                    str += val.valueText
                  }
                  if (item.hasOtherOption === 1 && val.valueText === '其他') {
                    if (item.otherValue) {
                      str += "(" + item.otherValue + ")"
                    }
                  } else if (val.valuesRemark) {
                    str += "(" + val.valuesRemark + ")"
                  }
                }
              })
            }
            if (str === '') {
              return '请选择'
            }
            return str
          }

        } else {
          return '请输入内容'
        }
      },
      getAddressValues() {
        const item = this.field
        const value = item.values
        if (value[0].attrValue && value[0].attrValue.length > 0) {
          let str = ''
          value[0].attrValue.forEach(item=>{
            str += item + ' '
          })
          return str
        } else {
          return '-'
        }
      }
    }
  }

</script>

<style>
  .caring-form-result-title{
    width: 100%;
    font-size: 14px;
    font-family: PingFang SC, PingFang SC;
    font-weight: 400;
    color: #333333;
    line-height: 20px;
  }

  .caring-form-textarea{
    padding: 8px 10px;
    background: white;
    height: 109px;
    border-radius: 0;
    opacity: 1;
    border: 1px solid #D5D3D3;
  }

  .caring-form-result-content{
    width: 100%;
    font-size: 14px;
    font-family: PingFang SC, PingFang SC;
    font-weight: 500;
    color: #333333;
  }
</style>
