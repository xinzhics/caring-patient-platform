import Vue from 'vue'
import {
  Form,
  Popup,
  Field,
  Cell,
  CellGroup,
  Button
} from 'vant'

Vue.use(Cell)
Vue.use(Form)
Vue.use(CellGroup)
Vue.use(Field)
Vue.use(Popup)
Vue.use(Button)

export default {
  components: {
    editorChild: () => import('@/components/arrt/editorChild')
  },
  data () {
    return {
      show: false,
      checkFail: false,
      option: '请选择',
      openPopup: false,
      selectOption: {},
      showbox: false,
      clock: false,
      showcolor: false,
      noClick: false
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    userRole: {
      type: String,
      default: ''
    },
    isHealth: {
      type: String,
      default: '0'
    }
  },
  mounted () {
    if (this.field && this.field.values[0] && this.field.values[0].valueText && this.isHealth === '1') {
      this.noClick = true
    }
    if (this.field) {
      if (this.field.values && this.field.values.length > 0) {
        const value = this.field.values[0]
        this.selectOption = {
          attrValue: value.valueText,
          id: value.attrValue,
          questions: value.questions,
          valuesRemark: value.desc
        }
      }
    }
  },
  methods: {
    /**
     * 输入 input 的值
     * @param val
     * @param info
     */
    fieldInput (val, info) {
      this.selectOption.valuesRemark = val
      this.selectOption.error = false
      this.$forceUpdate()
    },

    /**
     * 输入other 的值
     * @param val
     */
    fieldInputOther (val) {
      this.field.otherValue = val
      this.field.error = false
      this.$forceUpdate()
    },

    showPopup () {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      // 当设置禁止修改时，
      if (this.field.isUpdatable !== undefined && this.field.isUpdatable === 0) {
        return
      }
      this.selectOption = {}
      if (this.field) {
        if (this.field.values && this.field.values.length > 0) {
          const value = this.field.values[0]
          this.selectOption = {
            attrValue: value.valueText,
            id: value.attrValue,
            questions: value.questions,
            valuesRemark: value.desc
          }
        }
      }
      this.openPopup = true
      this.show = true
    },
    chooseOption (option, index) {
      if (option.id === this.selectOption.id) {
        this.selectOption = {}
      } else {
        this.selectOption = {
          attrValue: option.attrValue,
          id: option.id,
          desc: '',
          questions: option.questions
        }
      }
    },
    getLabel () {
      if (this.field.hasOtherOption === 1 && this.field.values[0].valueText === '其他') {
        return this.field.otherValue
      } else {
        return this.field.values[0].desc
      }
    },

    updata () {
      let closePopup = true
      if (this.field.itemAfterHasEnter === 1 && this.field.itemAfterHasEnterRequired === 1) {
        if (!this.selectOption.valuesRemark && this.selectOption.attrValue !== '其他') {
          this.selectOption.error = true
          closePopup = false
        }
      }
      if (this.selectOption.attrValue === '其他' && this.field.hasOtherOption === 1 && this.field.otherEnterRequired === 1) {
        if (!this.field.otherValue) {
          this.field.error = true
          closePopup = false
        }
      }
      if (!closePopup) {
        this.$forceUpdate()
        return
      }
      this.field.values = [{
        valueText: this.selectOption.attrValue,
        desc: this.selectOption.valuesRemark,
        attrValue: this.selectOption.id,
        questions: this.selectOption.questions
      }]
      this.show = false
      this.showcolor = true
    },
    // 检查单选 是否可以提交
    checkRadioDisableSubmit () {
      if (this.field.values[0].desc !== undefined && this.field.values[0].desc.length > 0) {
        this.showbox = true
      } else {
        this.showbox = false
      }

      if (!this.field.required) {
        return false
      } else {
        if (!this.selectOption.id) {
          return true
        }
      }
    },

    /**
     * 检查当前组件是否 有值。
     * 没有值时。 更新dom 中的颜色 为 未提交状态
     */
    checkFieldValues () {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          this.checkFail = true
        } else {
          this.checkFail = false
        }
      }
    }
  },
  watch: {
    show: function (val, old) {
      if (this.show === false) {
        if (this.openPopup) {
          this.checkFieldValues()
        }
      }
    }
  }
}
