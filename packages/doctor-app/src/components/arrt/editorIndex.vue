<template>
  <div>
    <van-form style="font-size:17px; background: #fff; padding-bottom:28px">

      <!-- <picture dropdown  DropdownSelec t Address Date/> -->
      <!-- <upphoto exactType==='SCR'/> -->
      <!-- <headPortrait /> -->
      <div v-for='(info,index) in currentFields' :key="info.id" v-if="
        info.exactType !== Constants.FieldExactType.CourseOfDisease
        && info.exactType !== Constants.FieldExactType.CCR
        && info.exactType !== Constants.FieldExactType.GFR">
        <div>

          <div class="bot-border" v-if="info.widgetType === Constants.CustomFormWidgetType.Avatar">
            <div>
              <headPortrait :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
            </div>
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "hospital" '>
            <hospital :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Address '>
            <addresss :field='info' :userRole="userRole"  @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleImageUpload
          || info.widgetType === Constants.CustomFormWidgetType.MultiImageUpload'>
            <upphoto :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Height"'>
            <height :field='info' :heightHandle="heightHandle" :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "Weight"'>
            <weight :field='info' :weightHandle="weightHandle" :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === "BMI"'>
            <bmi :field='info' :bmi="calculateBmi()" :userRole="userRole"/>
          </div>
          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.SCR'>
            <scr :field='info' :scrHandle="scrHandle" :ccrField="ccrField" :gfrField="gfrField" :courseOfDiseaseField="courseOfDiseaseField" :userRole="userRole" @noClikc="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Date'>
            <date1 :field='info' :ageHandle="ageHandle" :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Time'>
            <time1 :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Number'>
            <number :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType === Constants.FieldExactType.Gender'>
            <gender :field='info' :genderHandle="genderHandle" :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.DropdownSelect && info.exactType !== Constants.FieldExactType.Diagnose'>
            <dropdown :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.CheckBox'>
            <multipleChoice :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='(info.widgetType === Constants.CustomFormWidgetType.SingleLineText || info.widgetType === Constants.CustomFormWidgetType.FullName)
            && info.exactType !== Constants.FieldExactType.CCR
            && info.exactType !== Constants.FieldExactType.GFR
            && info.exactType !== Constants.FieldExactType.Mobile
            && info.exactType !== Constants.FieldExactType.CourseOfDisease'>
            <singleLine :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.SingleLineText && info.exactType === Constants.FieldExactType.Mobile'>
            <caringMobile :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.MultiLineText'>
            <textArea :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <div v-else-if='info.widgetType === Constants.CustomFormWidgetType.Radio && info.exactType !== Constants.FieldExactType.Gender'>
            <singleChoice :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
          </div>

          <!-- 诊断类型 使用单选的方式 -->
          <div v-else-if='info.exactType === Constants.FieldExactType.Diagnose'>
            <singleChoice :field='info' :userRole="userRole" @noClick="clickForm" :isHealth="$route.query.isHealth"/>
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
    <van-col span='24' style='text-align: center;background-color: #fff;position: relative'>
      <van-col v-if="totalPage > 1" span='24' style="
            text-align: right;
            background: rgb(247, 247, 247);
        ">
        <van-button :loading="isLoading" loading-type="spinner" loading-text="正在提交" :class="[lastPage?'tijiao2':'']" style="background-color: rgb(102, 114, 140) !important;margin-right: 13px;margin-top: 13px;margin-bottom: 13px;" round type='primary' class='attr-button btn tijiao' @click='saveCurrentFields'>{{
          lastPage ? '提交' : currentPage &&
          currentPage.label ? currentPage.label : "下一步" }}

        </van-button>
      </van-col>
      <van-col v-if='totalPage === 1' span='24' style="background: rgb(255, 255, 255);padding-bottom: 8.9375rem ;">
        <van-button :loading="isLoading" loading-type="spinner" loading-text="正在提交" style="border-radius: 42px;width: 13rem;background: rgb(102, 114, 139) !important;" @click='saveCurrentFields' class='attr-button btn' type='primary'>{{myTitle?myTitle:'提交'}}
        </van-button>
      </van-col>
      <van-col v-if='currentPage && currentPage.canBackPrevious === 1 && currentIndex > 1' span='24' style='text-align: left;padding-bottom: 13px;padding-top: 13px !important;padding-left: 13px;background: #F7F7F7;'>
        <van-button style="background: #fff !important;width: 35% ;" @click='previousPage' class='attr-button btn' round type='default'>{{
          currentPage.labelDesc ?
          currentPage.labelDesc : '上一页'
          }}
        </van-button>
      </van-col>
    </van-col>


    <van-overlay :show="showDialog" @click="showDialog = false">
      <div class="wrapper" @click.stop>
        <div>该内容不可修改</div>
        <div>如需修改请新建疾病信息</div>
      </div>
    </van-overlay>
  </div>

</template>

<script>
import { Row, Col, Cell, Form, Button, Field } from 'vant'
import attrDesc from '@/components/arrt/descindex'
import splitLine from '@/components/arrt/splitLineindex'

import scr from '@/components/arrt/scr/scr'
import height from '@/components/arrt/height/height'
import weight from '@/components/arrt/weight/weight'
import Bmi from '@/components/arrt/bmi/bmi'
import headPortrait from '@/components/arrt/headPortrait/headPortrait'
import addresss from '@/components/arrt/address/address'
import FormCheckFuncEvent from './formVue'
import hospital from"@/components/arrt/hospital/hospital"
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
  props: {
    myTitle: {
      type: String
    },
    allFields: {
      type: Array
    },
    submit: {
      type: Function
    },
  },
  mounted() {
    // this.$forceUpdate()
    // this.$nextTick(() => {
    //   // dom元素更新后执行，因此这里能正确打印更改之后的值
    //   // 改变了的值
    // })
    // this.initFields()
    this.$forceUpdate()
    setTimeout( ()=> {
    this.initFields()
      console.log(456)

    }, 500);
    console.log("this.currentFields",this.currentFields);

  },
  data() {
    return {
      headerConfig: {
        title: '健康档案'
      },
      userRole: 'doctor',
      userHeight: undefined,
      userWeight: undefined,
      userBmi: undefined,
      userSrc: undefined,
      userSex: undefined,
      userCcr: undefined,
      userGfr: undefined,
      userAge: undefined,
      showDialog: false,
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
      columns: [],
      isLoading: false,
    }
  },
  methods: {
    // 设置提交按钮的loading 状态
    setLoading(val) {
      this.isLoading = val
    },
    clickForm() {
      this.showDialog = true
      setTimeout(() =>{
        this.showDialog = false
      },3000)
    },
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
      // console.log(val)
      this.calculateGfr()
      this.calculateCcr()
      this.$forceUpdate()
    },

    /**
     * 身高处理器
     */
    heightHandle(values) {
      this.userHeight = values
      // console.log('userHeight', this.userHeight)
      this.calculateBmi()
    },
    /**
     * 体重处理器
     */
    weightHandle(val) {
      this.userWeight = val
      // console.log('userWeight', this.userWeight)
      this.calculateBmi()
      this.calculateCcr()
      this.$forceUpdate()
    },

    /**
     * scr 变动后的处理器
     */
    scrHandle(val) {
      this.userSrc = val
      // console.log('scrHandle', this.userSrc)
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
        // console.log('gfr age', '-')
        return
      }
      if (sex === undefined) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.$set(that['gfrField']['values'][0], 'attrValue', '-')
        this.calculateCkd()
        // console.log('gfr sex', '-')
        // that.gfrField.values= [{attrValue: '-'}]
        return
      }
      if (!scr || scr === 0) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
        that.$set(that['gfrField']['values'][0], 'attrValue', '-')
        this.calculateCkd()
        // console.log('gfr scr', '-')
        // that.gfrField.values= [{attrValue: '-'}]
        return
      }
      let temNum = 186 * Math.pow(scr / 88.4, -1.154) * Math.pow(age, -0.203)
      if (sex === 1) {
        temNum = temNum * 0.742
      }
      let num = temNum ? (temNum === Infinity ? 0 : temNum.toFixed(2)) : 0
      that.$set(that['currentFields'][index]['values'][0], 'attrValue', num)
      that.$set(that['gfrField']['values'][0], 'attrValue', num)
      // console.log('gfr', num)
      // that.gfrField.values= [{attrValue: num}]
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
      // console.log('calculateCcr', sex, age, weight)
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
        // console.log('Ccr sex', '-')
        return
      }
      let scr = this.userSrc
      if (that.currentFields[index]&&!scr || scr === 0) {
        console.log(that.currentFields[index])
          that.$set(that['currentFields'][index]['values'][0], 'attrValue', '-')
          that.ccrField.values = [{ attrValue: '-' }]
          console.log('Ccr src', '-')
      }
      let temNum = ((140 - age) * weight) / (0.818 * scr)
      if (sex === 1) {
        temNum = temNum * 0.85
      }
      const num = temNum ? (temNum == Infinity ? 0 : temNum.toFixed(2)) : 0
      if (that.currentFields[index]) {
        that.$set(that['currentFields'][index]['values'][0], 'attrValue', num)
      }

      that.ccrField.values = [{ attrValue: num }]
      // console.log('Ccr', num)
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
        that.courseOfDiseaseField.values = [{ attrValue: '-' }]
        // console.log('ckd gfr', '-')
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
      // console.log('ckd gfr', ckd)
    },

    surecbFuntion(i) {
      // console.log(i)
    },
    // 手动校验currentFields
    validate(currentFields) {
      FormCheckFuncEvent.$emit('formResultCheck', 0)
      if (currentFields === undefined) {
        return true
      }
      if (currentFields.length === 0) {
        return true
      }
      let validateResult = true
      for (let i = 0; i < currentFields.length; i++) {
        if (
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Date ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Time ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.FullName ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleLineText ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.MultiLineText ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleImageUpload ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.MultiImageUpload ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Avatar
        ) {
          if (currentFields[i].required) {
            // 校验是否已经有值
            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
            } else {
              validateResult = false
            }
          }
        } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Address) {
          if (currentFields[i].required) {
            if (
              currentFields[i].values &&
              currentFields[i].values.length > 0 &&
              currentFields[i].values[0].attrValue.length > 0
            ) {
            } else {
              validateResult = false
            }
          }
          if (currentFields[i].hasAddressDetail === 1 && currentFields[i].hasAddressDetailRequired === 1) {
            if (currentFields[i].value && currentFields[i].value.length > 0) {
            } else {
              validateResult = false
            }
          }
        } else if (
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.CheckBox ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Radio ||
          currentFields[i].widgetType === this.Constants.CustomFormWidgetType.DropdownSelect
        ) {
          if (currentFields[i].required) {
            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
              // 判断当前输入的选项是否为其他选项
              for (let j = 0; j < currentFields[i].values.length; j++) {
                if (currentFields[i].values[j].questions && currentFields[i].values[j].questions.length > 0) {
                  const result = this.validate(currentFields[i].values[j].questions)
                  if (!result) {
                    validateResult = false
                  }
                }
              }
            } else {
              validateResult = false
            }
          } else {
            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
              // 判断当前输入的选项是否为其他选项
              for (let j = 0; j < currentFields[i].values.length; j++) {
                if (currentFields[i].values[j].questions && currentFields[i].values[j].questions.length > 0) {
                  const result = this.validate(currentFields[i].values[j].questions)
                  if (!result) {
                    validateResult = false
                  }
                }
              }
            }
          }
        } else if (currentFields[i].widgetType === this.Constants.CustomFormWidgetType.Number) {
          if (currentFields[i].required) {
            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
            } else {
              validateResult = false
              if (currentFields[i].placeholder) {
                const message = currentFields[i].placeholder
                currentFields[i].errorMessage = message
              } else {
                currentFields[i].errorMessage = '请填写数值'
              }
            }
          }
          // 判断数字是否符合最大值最小值
          if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
            const values = parseFloat(currentFields[i].values[0].attrValue)
            this.maxValue = false
            this.minValue = false
            if (currentFields[i].maxValue !== undefined) {
              const maxValue = parseFloat(currentFields[i].maxValue)
              if (values > maxValue) {
                validateResult = false
              }
            }
            if (currentFields[i].minValue !== undefined) {
              // 组件有值， 判断组件 是否大于等于 最小值
              const minValue = parseFloat(currentFields[i].minValue)
              if (values < minValue) {
                validateResult = false
              }
            }
          }
        }
        if (
          (currentFields[i].exactType === this.Constants.FieldExactType.Phone &&
            currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleLineText) ||
          (currentFields[i].exactType === this.Constants.FieldExactType.Mobile &&
            currentFields[i].widgetType === this.Constants.CustomFormWidgetType.SingleLineText)
        ) {
          if (currentFields[i].required) {
            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
            } else {
              validateResult = false
            }
          } else {
            if (currentFields[i].values && currentFields[i].values.length > 0 && currentFields[i].values[0].attrValue) {
              // 判断手机号是否是 11 位
              if (currentFields[i].values[0].attrValue.length !== 11) {
                validateResult = false
              }
            }
          }
        }
      }
      return validateResult
    },

    saveCurrentFields() {
      // console.log('currentFields', this.currentFields)
      const validateStatue = this.validate(this.currentFields)
      // console.log(this.lastPage);
      if (!this.lastPage) {
        //  console.log(validateStatue);
        if (!validateStatue) {
          this.$forceUpdate()
          this.$toast.fail('您有表单项未完善，请检查完善后进行下一步')
          return
        } else {
          const thisObj = {
            content: this.currentFields,
            page: this.currentPage,
            MarkTip: 'has'
          }
          this.submit(thisObj)
        }
      } else {
        if (!validateStatue) {
          this.$forceUpdate()
          this.$toast.fail('您有表单项未完善，请检查完善后提交')
          return
        } else {
          const thisObj = {
            content: this.currentFields,
            MarkTip: 'end'
          }
          this.submit(thisObj)
        }
      }
    },

    /**
     * 获取省市区三级联动数据
     * */
    getAddress() {
      this.columns = address
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
          if (typeof that.allFields[i].values !== 'object') {
            that.allFields[i].values = eval('(' + that.allFields[i].values + ')')
          }
          // console.log(that.allFields[i])
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
      console.log('currentFields', this.currentFields)
      // console.log('fieldsMap', this.fieldsMap)
      // console.log('pageNo', this.pageNo)
      // console.log('currentIndex', this.currentIndex)
      // console.log('pageField', this.pageField)
      // console.log('totalPage', this.totalPage)

      this.$forceUpdate()
    },
    nextPage() {
      this.pageNo++
      this.currentIndex++
      if (this.pageNo === this.pageField.length) {
        this.lastPage = true
      } else {
        this.currentPage = this.pageField[this.pageNo]
      }
      this.currentFields = this.fieldsMap.get(this.currentIndex)
      // console.log('currentFields', this.currentFields)
      // console.log('fieldsMap', this.fieldsMap)
      // console.log('pageNo', this.pageNo)
      // console.log('currentIndex', this.currentIndex)
      // console.log('pageField', this.pageField)
      // console.log('totalPage', this.totalPage)
      window.scrollTo(0, 0)
    },
    previousPage() {
      this.pageNo--
      this.currentIndex--
      this.lastPage = false
      this.currentPage = this.pageField[this.pageNo]
      this.currentFields = this.fieldsMap.get(this.currentIndex)
      window.scrollTo(0, 0)
    }
  },
  created() {
    // this.getAddress()
    const myallInfo = localStorage.getItem('patientBaseInfo')
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
</script>
<style lang='less' scoped>

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
// .single {
//   display: -webkit-box;
//   -webkit-box-orient: vertical;
//   -webkit-line-clamp: 1;
//   overflow: hidden;
//   display: flex;
//   position: absolute;
//   padding-left: 16px;
//   height: 100%;
//   width: 28%;
//   font-size: 14px;
//   padding-top: 15px;
//   color: #333333;
// }

// /deep/ .van-cell-width50 {
//   width: 45%;
//   height: auto;
// }

// /deep/ .option-word-wrap {
//   word-wrap: break-word;
//   word-break: normal;
//   text-align: left;

//   .van-checkbox__label {
//     width: 100%;
//   }
// }

// /deep/ .attr-radio-group {
//   background-color: #fff;
//   width: 100%;
//   padding: 10px 16px 10px 16px;
// }

// /deep/ .radio-item-padding {
//   padding-top: 5px;
//   padding-bottom: 5px;
// }

// /deep/ .van-field__label {
//   color: #333333;
//   font-size: 14px;
// }

// /deep/ .van-cell {
//   // color: #333333;
//   // font-size: 14px;
// }

// .desc {
//   // color: #999999;
//   // font-size: 12px;
//   // background: #ffffff;
//   // padding: 0px 15px 10px 15px;
// }

// /deep/ .van-dropdown-menu {
//   display: flex;
//   font-size: 14px;
// }

// /deep/ .van-dropdown-menu__bar {
//   width: 100%;
//   background: #ffffff;
//   height: 22px;
//   box-shadow: 0 0px rgba(100, 101, 102, 0.08);
// }

// /deep/ .van-dropdown-menu__item {
//   justify-content: flex-end !important;
//   padding-right: 20px;
//   display: flex;
// }

// /deep/ .van-dropdown-menu van-hairline--top-bottom {
//   border-width: 0px 0;
// }

// /deep/ .van-dropdown-menu__title {
//   font-size: 14px;
//   max-width: 100%;
// }

// /deep/ .van-field__control {
//   // text-align: right;
//   color: #0e0e0e;
// }

// .bot-border {
//   position: relative;
//   border-bottom: 1px solid #f5f5f5;

//   &:after {
//     position: absolute;
//     box-sizing: border-box;
//     content: ' ';
//     pointer-events: none;
//     top: -50%;
//     right: -50%;
//     bottom: -50%;
//     left: -50%;
//     // border-bottom: 1px solid #ebedf0;
//     -webkit-transform: scale(0.5);
//     transform: scale(0.5);
//   }
// }

// .attr-button {
//   width: 60% !important;
//   border-radius: 6px;
//   margin-bottom: 20px;
//   height: 48px !important;
//   border-radius: 24px;
// }

// /deep/ .van-cell::after {
//   border-bottom: 0px solid #ebedf0;
// }
</style>
