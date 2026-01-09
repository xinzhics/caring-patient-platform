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
              <headPortrait :field='info' />
            </div>
          </div>
          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "hospital" '>
            <hospital :field='info' />
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Address '>
            <addresss :field='info' />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload'>
            <upphoto :field='info' />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Height"'>
            <height :field='info' :heightHandle="heightHandle" />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Weight"'>
            <weight :field='info' :weightHandle="weightHandle" />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "BMI"'>
            <bmi :field='info' :bmi="calculateBmi()" />
          </div>
          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.SCR'>
            <scr :field='info' :scrHandle="scrHandle" :ccrField="ccrField" :gfrField="gfrField"
                 :courseOfDiseaseField="courseOfDiseaseField" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Date'>
            <date1 :field='info' :ageHandle="ageHandle" />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Time'>
            <time1 :field='info' />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Number'>
            <number :field='info' />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType === Constants.FieldExactType.Gender'>
            <gender :field='info' :genderHandle="genderHandle" />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect && info.exactType !== Constants.FieldExactType.Diagnose'>
            <dropdown :field='info' />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
            <multipleChoice :field='info' />
          </div>

          <div v-else-if='(info.widgetType === Constants.CustomFormWidgetType.SingleLineText || info.widgetType === Constants.CustomFormWidgetType.FullName)
            && info.exactType !== Constants.FieldExactType.CCR
            && info.exactType !== Constants.FieldExactType.GFR
            && info.exactType !== Constants.FieldExactType.Mobile
            && info.exactType !== Constants.FieldExactType.CourseOfDisease'>
            <singleLine :field='info' />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.Mobile'>
            <caringMobile :field='info' />
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLineText'>
            <textArea :field='info' />
          </div>

          <div
              v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType !== Constants.FieldExactType.Gender'>
            <singleChoice :field='info' />
          </div>

          <!-- 诊断类型 使用单选的方式 -->
          <div v-else-if='info.exactType === Constants.FieldExactType.Diagnose'>
            <singleChoice :field='info' />
          </div>
          <!--线与提示-->
          <attrDesc v-else-if="info.widgetType === Constants.CustomFormWidgetType.Desc" :field="info"></attrDesc>

          <splitLine v-else-if='info.widgetType === Constants.CustomFormWidgetType.SplitLine' :field='info'></splitLine>
        </div>
        <caringLine v-if="index < currentFields.length -1
          && info.widgetType !== Constants.CustomFormWidgetType.SplitLine
          && info.widgetType !== Constants.CustomFormWidgetType.Desc"></caringLine>
      </div>

      <!--上一页与下一页-->
    </van-form>
  </div>

</template>

<script>
import {Button, Cell, Col, Field, Form, Row} from 'vant'
export default {
  components: {
    [Row.name]: Row,
    [Col.name]: Col,
    [Cell.name]: Cell,
    [Form.name]: Form,
    [Button.name]: Button,
    [Field.name]: Field,
    scr:() => import('@/components/arrt/scr/scr'),
    height:() => import('@/components/arrt/height/height'),
    weight:() => import('@/components/arrt/weight/weight'),
    Bmi:() => import('@/components/arrt/bmi/bmi'),
    addresss:() => import('@/components/arrt/address/address'),
    headPortrait:() => import('@/components/arrt/headPortrait/headPortrait'),
    selectPicker:() => import('@/components/select/index'),
    attrDesc:() => import('@/components/arrt/descindex'),
    splitLine:() => import('@/components/arrt/splitLineindex'),
    hospital:() => import('@/components/arrt/hospital/hospital'),
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
  props: {
    myTitle: {
      type: String
    },
    allFields: {
      type: Array
    },
    submit: {
      type: Function
    }
  },
  mounted() {
    this.$forceUpdate()
    console.log('mounted', this.allFields)
    this.initFields()
  },
  data() {
    return {
      headerConfig: {
        title: ''
      },
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

      currentFields: [], // 当前展示的元素
    }
  },
  methods: {
    ageHandle(val) {
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

    /**
     * 性别变化后 处理器
     */
    genderHandle(val) {
      this.userSex = val
      this.calculateGfr()
      this.calculateCcr()
      this.$forceUpdate()
    },

    /**
     * 身高处理器
     */
    heightHandle(values) {
      this.userHeight = values
      this.calculateBmi()
    },
    /**
     * 体重处理器
     */
    weightHandle(val) {
      this.userWeight = val
      this.calculateBmi()
      this.calculateCcr()
      this.$forceUpdate()
    },

    /**
     * scr 变动后的处理器
     */
    scrHandle(val) {
      this.userSrc = val
      this.calculateGfr()
      this.calculateCcr()
      this.$forceUpdate()
    },
    /**
     * bim 算法
     */
    calculateBmi() {
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
    calculateGfr() {
      let scr = this.userSrc,
          age = this.userAge,
          sex = this.userSex
      let that = this
      let index = undefined
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
    calculateCcr() {
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
        that.ccrField.values = [{attrValue: '-'}]
        return
      }
      let scr = this.userSrc
      if (!scr || scr === 0) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.ccrField.values = [{attrValue: '-'}]
        return
      }
      let temNum = ((140 - age) * weight) / (0.818 * scr)
      if (sex === 1) {
        temNum = temNum * 0.85
      }
      const num = temNum ? (temNum == Infinity ? 0 : temNum.toFixed(2)) : 0
      that.$set(that['currentFields'][index]['values'][0], 'attrValue', num)
      that.ccrField.values = [{attrValue: num}]
      that.userCcr = num
    },
    /**
     * 计算CKD
     */
    calculateCkd() {
      let gfr = this.userGfr
      const that = this
      let index = undefined
      this.currentFields.forEach((ele, i) => {
        if (ele.exactType === this.Constants.FieldExactType.CourseOfDisease) {
          index = i
        }
      })
      if (gfr === '-' || gfr === undefined) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.courseOfDiseaseField.values = [{attrValue: '-'}]
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
      that.courseOfDiseaseField.values = [{attrValue: ckd}]
    },

    hasOther(values) {
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
    jsGetAge(strBirthday, split) {
      // 传入形式yyyy-MM-dd
      // strBirthday = util.formatTime(strBirthday);转换成yyyy-MM-dd形式
      var returnAge
      var strBirthdayArr = strBirthday.split(split)
      var birthYear = strBirthdayArr[0]
      var birthMonth = strBirthdayArr[1]
      var birthDay = strBirthdayArr[2]
      var d = new Date()
      var nowYear = d.getFullYear()
      var nowMonth = d.getMonth() + 1
      var nowDay = d.getDate()
      if (nowYear == birthYear) {
        returnAge = 0 // 同年 则为0岁
      } else {
        var ageDiff = nowYear - birthYear // 年之差
        if (ageDiff > 0) {
          if (nowMonth == birthMonth) {
            var dayDiff = nowDay - birthDay // 日之差
            if (dayDiff < 0) {
              returnAge = ageDiff - 1
            } else {
              returnAge = ageDiff
            }
          } else {
            var monthDiff = nowMonth - birthMonth // 月之差
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

    initFields() {
      this.currentFields = []
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
          if (typeof that.allFields[i].values !== 'object') {
            that.allFields[i].values = eval('(' + that.allFields[i].values + ')')
          }
          if (that.allFields[i].widgetType !== that.Constants.CustomFormWidgetType.Page) {
            tempArray.push(that.allFields[i])
          }
        }
        this.currentFields = tempArray
      }
      this.$forceUpdate()
    },

  },
  created() {
    if (localStorage.getItem('myallInfo')) {
      const myallInfo = localStorage.getItem('myallInfo')
      const myallInfoObj = JSON.parse(myallInfo)
      // 先从个人信息中获取 身高。体重，出生日期，性别
      if (myallInfoObj.height) {
        this.userHeight = myallInfoObj.height
      }
      if (myallInfoObj.weight) {
        this.userWeight = myallInfoObj.weight
      }
      if (myallInfoObj.sex !== undefined) {
        this.userSex = myallInfoObj.sex
      }
      if (myallInfoObj.birthday) {
        this.Birthday = myallInfoObj.birthday
        if (this.Birthday.indexOf('-') > -1) {
          this.jsGetAge(this.Birthday, '-')
        } else if (this.Birthday.indexOf('/') > -1) {
          this.jsGetAge(this.Birthday, '/')
        }
      }

      // 然后从 当前表单的组件总 获取 身高。体重，出生日期，性别
      if (this.allFields && this.allFields.length > 0) {
        for (let i = 0; i < this.allFields.length; i++) {
          if (!this.allFields[i]) {
            continue
          }
          if (this.allFields[i].exactType === this.Constants.FieldExactType.Birthday) {
            if (this.allFields[i].values && this.allFields[i].values[0] && this.allFields[i].values[0].attrValue) {
              this.Birthday = this.allFields[i].values[0].attrValue
              if (this.Birthday.indexOf('-') > -1) {
                this.jsGetAge(this.Birthday, '-')
              } else if (this.Birthday.indexOf('/') > -1) {
                this.jsGetAge(this.Birthday, '/')
              }
            }
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.Gender) {
            if (this.allFields[i].values && this.allFields[i].values[0]) {
              if (this.allFields[i].values[0].valueText === '男') {
                this.userSex = 0
              } else if (this.allFields[i].values[0].valueText === '女') {
                this.userSex = 1
              }
            }
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.Weight) {
            if (this.allFields[i].values && this.allFields[i].values[0] && this.allFields[i].values[0].attrValue) {
              this.userWeight = this.allFields[i].values[0].attrValue
            }
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.Height) {
            if (this.allFields[i].values && this.allFields[i].values[0] && this.allFields[i].values[0].attrValue) {
              this.userHeight = this.allFields[i].values[0].attrValue
            } else {
              this.userHeight = undefined
            }
          } else if (this.allFields[i].exactType === this.Constants.FieldExactType.SCR) {
            if (this.allFields[i].values && this.allFields[i].values[0] && this.allFields[i].values[0].attrValue) {
              this.userSrc = this.allFields[i].values[0].attrValue
            }
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
      this.$forceUpdate()
    }
  }
}
</script>
<style lang='less' scoped>
#app {
  background-color: #fff;
  padding-bottom: 2rem;
}

/deep/ .van-form::-webkit-scrollbar {
  width: 0 !important;
}

/deep/ .attr-button {
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
