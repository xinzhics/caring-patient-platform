<template>
  <div>
    <van-form style="font-size:17px; background: #fff; padding-bottom:28px">

      <div v-for='(info, index) in currentFields' :key="index" v-if="
        info.exactType !== Constants.FieldExactType.CourseOfDisease
        && info.exactType !== Constants.FieldExactType.CCR
        && info.exactType !== Constants.FieldExactType.GFR">
        <div>

          <div class="bot-border" v-if="info.widgetType === Constants.CustomFormWidgetType.Avatar">
            <div>
              <headPortrait :field='info' style="pointer-events:none"/>
            </div>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "hospital" '>
            <hospital style="pointer-events:none" :field='info' :userRole="userRole"/>

          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Address '>
            <addresss :field='info' style="pointer-events:none"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload'>
            <upphoto :field='info' style="pointer-events:none"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Height"'>
            <height :field='info' style="pointer-events:none"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Weight"'>
            <weight :field='info' style="pointer-events:none"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "BMI"'>
            <bmi :field='info' style="pointer-events:none"/>
          </div>
          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.SCR'>
            <scr :field='info' style="pointer-events:none" :ccrField="ccrField" :gfrField="gfrField"
                 :courseOfDiseaseField="courseOfDiseaseField"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Date'>
            <date1 :field='info' style="pointer-events:none"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Time'>
            <time1 :field='info' style="pointer-events:none"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Number'>
            <number :field='info' style="pointer-events:none"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType === Constants.FieldExactType.Gender'>
            <gender :field='info' style="pointer-events:none"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect && info.exactType !== Constants.FieldExactType.Diagnose'>
            <dropdown :field='info' style="pointer-events:none"/>
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
            <multipleChoice :field='info' style="pointer-events:none"/>
          </div>

          <div v-else-if='(info.widgetType === Constants.CustomFormWidgetType.SingleLineText || info.widgetType === Constants.CustomFormWidgetType.FullName)
            && info.exactType !== Constants.FieldExactType.Mobile
            && info.exactType !== Constants.FieldExactType.CourseOfDisease'>
            <singleLine :field='info' style="pointer-events:none" :userRole="userRole"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.Mobile'>
            <caringMobile :field='info' style="pointer-events:none" :userRole="userRole"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLineText'>
            <textArea :field='info' style="pointer-events:none" :userRole="userRole"/>
          </div>

          <div
            v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType !== Constants.FieldExactType.Gender'>
            <singleChoice :field='info' style="pointer-events:none" :userRole="userRole"/>
          </div>

          <!-- 诊断类型 使用单选的方式 -->
          <div v-else-if='info.exactType === Constants.FieldExactType.Diagnose'>
            <singleChoice :field='info' style="pointer-events:none" :userRole="userRole"/>
          </div>
          <!--线与提示-->
          <attrDesc v-else-if="info.widgetType === Constants.CustomFormWidgetType.Desc" :field="info"></attrDesc>

          <splitLine v-else-if='info.widgetType === Constants.CustomFormWidgetType.SplitLine' :field='info'>
            style="pointer-events:none"
          </splitLine>
        </div>
      </div>
    </van-form>
  </div>

</template>

<script>
import {Row, Col, Cell, Form, Button, Field} from 'vant'
import selectPicker from '@/components/select/index'
import attrDesc from '@/components/arrt/descindex'
import splitLine from '@/components/arrt/splitLineindex'

import scr from '@/components/arrt/scr/scr'
import height from '@/components/arrt/height/height'
import weight from '@/components/arrt/weight/weight'
import Bmi from '@/components/arrt/bmi/bmi'
import headPortrait from '@/components/arrt/headPortrait/headPortrait'
import addresss from '@/components/arrt/address/address'
import hospital from '@/components/arrt/hospital/hospital'

export default {
  components: {
    [Row.name]: Row,
    [Col.name]: Col,
    [Cell.name]: Cell,
    [Form.name]: Form,
    [Button.name]: Button,
    [Field.name]: Field,
    scr,
    height,
    weight,
    Bmi,
    addresss,
    headPortrait,
    selectPicker,
    attrDesc,
    splitLine,
    hospital,
    singleLine: () => import('@/components/arrt/Singleline/field-Singleline'),
    caringMobile: () => import('@/components/arrt/mobile/caringMobile'),
    textArea: () => import('@/components/arrt/textArea/textarea'),
    singleChoice: () => import('@/components/arrt/singleChoice/singleChoice'),
    multipleChoice: () => import('@/components/arrt/multipleChoice/multipleChoice'),
    number: () => import('@/components/arrt/number/number'),
    upphoto: () => import('@/components/arrt/hisPicture/hisPicture'),
    dropdown: () => import('@/components/arrt/dropdown/dropdown'),
    gender: () => import('@/components/arrt/hisGender/gender'),
    time1: () => import('@/components/arrt/time/time'),
    date1: () => import('@/components/arrt/date/date'),
    caringLine: () => import('@/components/arrt/line')
  },
  name: 'attr-page',
  props: {
    myTitle: {
      type: String
    },
    // allFields
    allFields: {
      type: Array
    },
    submit: {
      type: Function
    },
    doctorName: {
      type: String
    },
    organName: {
      type: String
    },
    showDoctorName: {
      type: Boolean,
      default: false
    },
    currentFields: {
      type: Array
    }
  },
  data () {
    return {
      userRole: 'patient',
      ccrField: undefined,
      ccrFieldIndex: undefined,
      gfrField: undefined,
      gfrFieldIndex: undefined,
      courseOfDiseaseField: undefined,
      courseOfDiseaseFieldIndex: undefined,

      totalPage: 1, // 总页数
      currentIndex: 1, // 当前下标
      lastPage: false, // 是否最后一页
      // currentFields: [], // 当前展示的元素
      pageNo: 0,
      currentPage: undefined, // 当前分页
      columns: [],
      name: ''
    }
  },

  created () {
    // console.log(this.Constants.CustomFormWidgetType)
    // console.log(this.Constants.FieldExactType)
    this.name = this.$getDictItem('doctor')
    // console.log("this.currentFields",this.currentFields);
    // 先从个人信息中获取 身高。体重，出生日期，性别
    // 然后从 当前表单的组件总 获取 身高。体重，出生日期，性别
    if (this.currentFields && this.currentFields.length > 0) {
      for (let i = 0; i < this.currentFields.length; i++) {
        if (this.currentFields[i].exactType === this.Constants.FieldExactType.CCR) {
          this.ccrField = this.currentFields[i]
          // console.log('ccrField', this.ccrField)
        } else if (this.currentFields[i].exactType === this.Constants.FieldExactType.GFR) {
          this.gfrField = this.currentFields[i]
          // console.log('gfrField', this.gfrField)
        } else if (this.currentFields[i].exactType === this.Constants.FieldExactType.CourseOfDisease) {
          this.courseOfDiseaseField = this.currentFields[i]
          // console.log('courseOfDiseaseField', this.courseOfDiseaseField)
        } else if (this.currentFields[i].exactType === this.Constants.FieldExactType.scoringSingleChoice) {
          if (this.currentFields[i].options && this.currentFields[i].values) {
            let options = this.currentFields[i].options
            if (this.currentFields[i].values.length > 0) {
              for (let j = 0; j < options.length; j++) {
                if (options[j].id === this.currentFields[i].values[0].attrValue) {
                  this.currentFields[i].values[0].score = options[j].score
                }
              }
            }
          }
        }
      }
    }
    this.$forceUpdate()
  }
}
</script>
<style lang='less' scoped>
.endItem {
  /deep/ .vux-label {
    font-size: 16px !important;
    font-family: Source Han Sans CN;
  }

  /deep/ .weui-cell:before {
    width: 90% !important;
  }
}

/deep/ .attr-button {
  width: 100% /* 208/16 */;
}

.tijiao {
  width: 55% !important;
  position: absolute;
  left: 40%;
}
</style>
