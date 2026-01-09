<template>
  <div>
    <van-form style="font-size:17px; background: #fff; padding-bottom:28px">
      <div v-for='(info,index) in currentFields' :key="info.id" v-if="
        info.exactType !== Constants.FieldExactType.CourseOfDisease
        && info.exactType !== Constants.FieldExactType.CCR
        && info.exactType !== Constants.FieldExactType.GFR">
        <div>
          <div class="bot-border" v-if="info.widgetType === Constants.CustomFormWidgetType.Avatar">
            <div>
              <headPortrait :field='info' :userRole="userRole" />
            </div>
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "hospital" '>
            <hospital :field='info' :userRole="userRole" />
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Address '>
            <addresss :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload'>
            <upphoto :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Height"'>
            <height :field='info' :heightHandle="heightHandle" :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Weight"'>
            <weight :field='info' :weightHandle="weightHandle" :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "BMI"'>
            <bmi :field='info' :bmi="calculateBmi()" :userRole="userRole" />
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.SCR'>
            <scr :style="{'pointer-events':info.isUpdatable===0?'none':'auto'}" :field='info' :scrHandle="scrHandle" :ccrField="ccrField" :gfrField="gfrField" :courseOfDiseaseField="courseOfDiseaseField" :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Date'>
            <date1 :field='info' :ageHandle="ageHandle" :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Time'>
            <time1 :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Number'>
            <number :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType === Constants.FieldExactType.Gender'>
            <gender :field='info' :genderHandle="genderHandle" :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect && info.exactType !== Constants.FieldExactType.Diagnose'>
            <dropdown :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
            <multipleChoice :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='(info.widgetType === Constants.CustomFormWidgetType.SingleLineText || info.widgetType === Constants.CustomFormWidgetType.FullName)
            && info.exactType !== Constants.FieldExactType.CCR
            && info.exactType !== Constants.FieldExactType.GFR
            && info.exactType !== Constants.FieldExactType.Mobile
            && info.exactType !== Constants.FieldExactType.CourseOfDisease'>
            <singleLine :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.Mobile'>
            <caringMobile :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLineText'>
            <textArea :field='info' :userRole="userRole" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType !== Constants.FieldExactType.Gender'>
            <singleChoice :field='info' :userRole="userRole" />
          </div>

          <!-- 诊断类型 使用单选的方式 -->
          <div v-else-if='info.exactType === Constants.FieldExactType.Diagnose'>
            <singleChoice :field='info' :userRole="userRole" />
          </div>
          <!--线与提示-->
          <attrDesc v-else-if="info.widgetType === Constants.CustomFormWidgetType.Desc" :field="info"></attrDesc>

          <splitLine v-else-if='info.widgetType === Constants.CustomFormWidgetType.SplitLine' :field='info'></splitLine>
        </div>
        <caringLine v-if="index < currentFields.length -1
          && info.widgetType !== Constants.CustomFormWidgetType.SplitLine
          && info.widgetType !== Constants.CustomFormWidgetType.Desc"></caringLine>
      </div>
    </van-form>
    <van-col span='24' style='text-align: center;position: relative' v-if="currentFields.length > 0">
      <van-col v-if="totalPage > 1" span='24' style="
            text-align: right;
            background: rgb(247, 247, 247);
        ">
        <van-button v-if="!lastPage" style="background-color: rgb(102, 114, 140) !important;margin-right: 13px;margin-top: 13px;margin-bottom: 13px; color: white" round class='attr-button btn tijiao' @click='saveCurrentFields'>{{
          currentPage && currentPage.label ? currentPage.label : "下一步" }}
        </van-button>
      </van-col>
      <van-col v-if='currentPage && currentPage.canBackPrevious === 1 && currentIndex > 1' span='24' style='text-align: left;padding-bottom: 13px;padding-top: 13px !important;padding-left: 13px;'>
        <van-button style="background: #fff !important;width: 35% ;" @click='previousPage' class='attr-button btn' round type='default'>{{
          currentPage.labelDesc ?
          currentPage.labelDesc : '上一页'
          }}
        </van-button>
      </van-col>
    </van-col>
  </div>

</template>

<script>
import { Row, Col, Cell, Form, Button, Field, Toast } from 'vant'
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
import Vue from 'vue'
Vue.use(Toast)
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
    upphoto: () => import('@/components/arrt/picture/picture'),
    dropdown: () => import('@/components/arrt/dropdown/dropdown'),
    gender: () => import('@/components/arrt/gender/gender'),
    time1: () => import('@/components/arrt/time/time'),
    date1: () => import('@/components/arrt/date/date'),
    caringLine: () => import('@/components/arrt/line')
  },
  name: 'attr-page',
  data () {
    return {
      userRole: 'doctor',
      userHeight: undefined,
      userWeight: undefined,
      userBmi: undefined,
      userSrc: undefined,
      userSex: undefined,
      userCcr: undefined,
      userGfr: undefined,
      userAge: undefined,
      Birthday: '',
      ccrField: undefined,
      ccrFieldIndex: undefined,
      gfrField: undefined,
      gfrFieldIndex: undefined,
      courseOfDiseaseField: undefined,
      courseOfDiseaseFieldIndex: undefined,

      totalPage: 1, // 总页数
      currentIndex: 1, // 当前下标
      lastPage: false, // 是否最后一页
      currentFields: [], // 当前展示的元素
      fieldsMap: new Map(), // 根据分页组件 对元素表单进行分组
      pageField: [], // 分页组件
      pageNo: 0,
      currentPage: undefined, // 当前分页
      allFields: []
    }
  },
  methods: {
    ageHandle (val) {
      this.Birthday = val
      if (val.indexOf('-') > -1) {
        this.jsGetAge(val, '-')
      } else if (val.indexOf('/') > -1) {
        this.jsGetAge(val, '/')
      }
      this.calculateGfr()
      this.calculateCcr()
      this.$forceUpdate()
    },
    saveCurrentFields () {
      if (!this.lastPage) {
        this.nextPage()
      }
    },
    /**
     * 性别变化后 处理器
     */
    genderHandle (val) {
      this.userSex = val
      this.calculateGfr()
      this.calculateCcr()
      this.$forceUpdate()
    },

    /**
     * 身高处理器
     */
    heightHandle (values) {
      this.userHeight = values
      this.calculateBmi()
    },
    /**
     * 体重处理器
     */
    weightHandle (val) {
      this.userWeight = val
      this.calculateBmi()
      this.calculateCcr()
      this.$forceUpdate()
    },

    /**
     * scr 变动后的处理器
     */
    scrHandle (val) {
      this.userSrc = val
      this.calculateGfr()
      this.calculateCcr()
      this.$forceUpdate()
    },
    /**
     * bim 算法
     */
    calculateBmi () {
      let height = Number(this.userHeight)
      let weight = Number(this.userWeight)
      const that = this
      let bmiIndex = -1
      this.currentFields.forEach((ele, i) => {
        if (ele.exactType === this.Constants.FieldExactType.BMI) {
          bmiIndex = i
        }
      })
      if (weight !== undefined && weight > 0 && height !== undefined && height > 0) {
        height = height / 100
        let bmi = weight / Math.pow(height, 2)
        let bmiValue = bmi ? (bmi === Infinity ? 0 : bmi.toFixed(2)) : 0
        if (bmiIndex > -1) {
          that.$set(that['currentFields'][bmiIndex]['values'][0], 'attrValue', bmiValue)
        }
        this.userBmi = bmiValue
      } else {
        that.$set(that['currentFields'][bmiIndex]['values'][0], 'attrValue', '-')
      }
    },
    /**
     * 计算 GFR
     */
    calculateGfr () {
      let scr = this.userSrc
      let age = this.userAge
      let sex = this.userSex
      let that = this
      let index
      this.currentFields.forEach((ele, i) => {
        if (ele.exactType === this.Constants.FieldExactType.GFR) {
          index = i
        }
      })
      if (index === undefined) {
        return
      }
      if (!age) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.$set(that['gfrField']['values'][0], 'attrValue', '-')
        this.calculateCkd()
        return
      }
      if (sex === undefined) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.$set(that['gfrField']['values'][0], 'attrValue', '-')
        this.calculateCkd()
        return
      }
      if (!scr || scr === 0) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.$set(that['gfrField']['values'][0], 'attrValue', '-')
        this.calculateCkd()
        return
      }
      let temNum = 186 * Math.pow(scr / 88.4, -1.154) * Math.pow(age, -0.203)
      if (sex === 1) {
        temNum = temNum * 0.742
      }
      let num = temNum ? (temNum === Infinity ? 0 : temNum.toFixed(2)) : 0
      that.$set(that['currentFields'][index]['values'][0], 'attrValue', num)
      that.$set(that['gfrField']['values'][0], 'attrValue', num)
      this.userGfr = num
      this.calculateCkd()
    },
    /**
     * 计算CCR
     */
    calculateCcr () {
      let sex = this.userSex
      let age = this.userAge
      let weight = this.userWeight
      let index = this.ccrFieldIndex
      const that = this
      this.currentFields.forEach((ele, i) => {
        if (ele.exactType === this.Constants.FieldExactType.CCR) {
          index = i
        }
      })
      if (index === undefined) {
        return
      }
      if (sex === undefined) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.ccrField.values = [{ attrValue: '-' }]
        return
      }
      let scr = this.userSrc
      if (!scr || scr === 0) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.ccrField.values = [{ attrValue: '-' }]
        return
      }
      let temNum = ((140 - age) * weight) / (0.818 * scr)
      if (sex === 1) {
        temNum = temNum * 0.85
      }
      const num = temNum ? (temNum === Infinity ? 0 : temNum.toFixed(2)) : 0
      that.$set(that['currentFields'][index]['values'][0], 'attrValue', num)
      that.ccrField.values = [{ attrValue: num }]
      that.userCcr = num
    },
    /**
     * 计算CKD
     */
    calculateCkd () {
      let gfr = this.userGfr
      const that = this
      let index
      this.currentFields.forEach((ele, i) => {
        if (ele.exactType === this.Constants.FieldExactType.CourseOfDisease) {
          index = i
        }
      })
      if (gfr === '-' || gfr === undefined) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.courseOfDiseaseField.values = [{ attrValue: '-' }]
        return
      }
      if (index === undefined) {
        return
      }
      let ckd = ''
      if (gfr >= 90) {
        ckd = 'CKD 1期'
      } else if (gfr >= 60) {
        ckd = 'CKD 2期'
      } else if (gfr >= 30) {
        ckd = 'CKD 3期'
      } else if (gfr >= 15) {
        ckd = 'CKD 4期'
      } else if (gfr < 15) {
        ckd = 'CKD 5期'
      }
      if (gfr === 0) {
        ckd = '-'
      }
      that.$set(that['currentFields'][index]['values'][0], 'attrValue', ckd)
      that.courseOfDiseaseField.values = [{ attrValue: ckd }]
    },

    hasOther (values) {
      let has = false
      if (values && values.length > 0) {
        for (let i = 0; i < values.length; i++) {
          if (values[i].valueText === '其他') {
            has = true
            break
          }
        }
      }
      return has
    },
    /**
     * 计算年龄
     * @param strBirthday
     * @returns {number}
     */
    jsGetAge (strBirthday, split) {
      var returnAge
      var strBirthdayArr = strBirthday.split(split)
      var birthYear = strBirthdayArr[0]
      var birthMonth = strBirthdayArr[1]
      var birthDay = strBirthdayArr[2]
      var d = new Date()
      var nowYear = d.getFullYear()
      var nowMonth = d.getMonth() + 1
      var nowDay = d.getDate()
      if (nowYear === birthYear) {
        returnAge = 0 // 同年 则为0岁
      } else {
        var ageDiff = nowYear - birthYear // 年之差
        if (ageDiff > 0) {
          if (nowMonth === birthMonth) {
            var dayDiff = nowDay - birthDay // 日之差
            if (dayDiff < 0) {
              returnAge = ageDiff - 1
            } else {
              returnAge = ageDiff
            }
          } else {
            let monthDiff = nowMonth - birthMonth // 月之差
            if (monthDiff < 0) {
              returnAge = ageDiff - 1
            } else {
              returnAge = ageDiff
            }
          }
        } else {
          returnAge = -1 // 返回-1 表示出生日期输入错误 晚于今天
        }
      }
      this.userAge = returnAge
    },
    /**
     * 父组件通知 清空 字段
     */
    clearFields () {
      this.allFields = []
      console.log('attrPage clearFields', this.allFields)
      this.userHeight = undefined
      this.userWeight = undefined
      this.userBmi = undefined
      this.userSrc = undefined
      this.userSex = undefined
      this.userCcr = undefined
      this.userGfr = undefined
      this.userAge = undefined
      this.Birthday = ''
      this.ccrField = undefined
      this.ccrFieldIndex = undefined
      this.gfrField = undefined
      this.gfrFieldIndex = undefined
      this.courseOfDiseaseField = undefined
      this.courseOfDiseaseFieldIndex = undefined

      this.totalPage = 1 // 总页数
      this.currentIndex = 1 // 当前下标
      this.lastPage = false // 是否最后一页
      this.currentFields = [] // 当前展示的元素
      this.fieldsMap = new Map() // 根据分页组件 对元素表单进行分组
      this.pageField = [] // 分页组件
      this.pageNo = 0
      this.currentPage = undefined // 当前分页
    },
    /**
     * 父组件通知 显示 新的 字段
     * @param fields
     */
    showFields (fields) {
      console.log('attrPage showFields', fields)
      this.allFields = []
      this.allFields = fields
      this.currentFields = []
      this.pageField = [] // 分页组件
      this.totalPage = 1
      this.pageNo = 0
      this.currentIndex = 1
      // 然后从 当前表单的组件总 获取 身高。体重，出生日期，性别
      if (this.allFields && this.allFields.length > 0) {
        for (let i = 0; i < this.allFields.length; i++) {
          if (!this.allFields[i]) {
            continue
          }
          if (this.allFields[i].exactType === this.Constants.FieldExactType.Height) {
            this.userHeight = undefined
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.CCR) {
            this.ccrField = this.allFields[i]
            this.ccrFieldIndex = i
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.GFR) {
            this.gfrField = this.allFields[i]
            this.gfrFieldIndex = i
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.CourseOfDisease) {
            this.courseOfDiseaseField = this.allFields[i]
            this.courseOfDiseaseFieldIndex = i
          }
        }
      }
      this.initFields()
      this.$forceUpdate()
    },
    initFields () {
      this.currentFields = []
      this.totalPage = 1
      this.pageNo = 0
      this.currentIndex = 1
      let allFieldSet = new Set()
      if (this.allFields && this.allFields.length > 0) {
        let tempArray = []
        const that = this
        for (let i = 0; i < that.allFields.length; i++) {
          if (!that.allFields[i]) {
            continue
          }
          if (allFieldSet.has(that.allFields[i].id + that.allFields[i].label)) {
            continue
          }
          allFieldSet.add(that.allFields[i].id + that.allFields[i].label)
          if (that.allFields[i].widgetType === that.Constants.CustomFormWidgetType.Page) {
            that.pageField.push(that.allFields[i])
            that.fieldsMap.set(that.totalPage, tempArray)
            that.totalPage++
            tempArray = []
          } else {
            tempArray.push(that.allFields[i])
          }
        }
        if (tempArray.length > 0) {
          that.fieldsMap.set(that.totalPage, tempArray)
        }
      }
      if (this.totalPage > 1) {
        this.currentPage = this.pageField[this.pageNo]
        this.lastPage = false
      } else {
        this.lastPage = true
      }
      this.currentFields = this.fieldsMap.get(this.currentIndex)
      this.$forceUpdate()
    },
    nextPage () {
      this.pageNo++
      this.currentIndex++
      if (this.pageNo === this.pageField.length) {
        this.lastPage = true
      } else {
        this.currentPage = this.pageField[this.pageNo]
      }
      this.currentFields = this.fieldsMap.get(this.currentIndex)
      window.scrollTo(0, 0)
    },
    previousPage () {
      this.pageNo--
      this.currentIndex--
      this.lastPage = false
      this.currentPage = this.pageField[this.pageNo]
      this.currentFields = this.fieldsMap.get(this.currentIndex)
      window.scrollTo(0, 0)
    }
  }
}
</script>
<style lang='less' scoped>
#app {
  background-color: #fff;
  padding-bottom: 2rem;
}
/deep/.van-form::-webkit-scrollbar {
  width: 0 !important;
}
/deep/.attr-button {
  width: 55% /* 208/16 */;
}
.tijiao {
  width: 55% !important;
  position: absolute;
  left: 40%;
}
tijiao2 {
  left: 23%;
}

</style>
