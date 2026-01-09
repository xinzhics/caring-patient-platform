import Vue from 'vue';
import {
  Form,
  Cell,
  CellGroup,
  Field,
  Button
} from 'vant';
import FormCheckFuncEvent from "../formVue";
Vue.use(Form);
Vue.use(Cell);
Vue.use(CellGroup);
Vue.use(Field);
Vue.use(Button);



export default {
  components: {
    editorChild: () => import('@/components/arrt/editorChild')
  },
  data() {
    return {
      show: false,
      noValues: true,
      openPopup: false,
      noClick: false
    };
  },
  created() {
    console.log(this.field);
    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })
  },
  mounted() {
    if (this.field.values[0].attrValue&&this.isHealth === '1'){
      this.noClick = true
    }
    if (this.field) {
      if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
        this.noValues = false
      }
    }
  },
  methods: {
    showPopup() {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      // 当设置禁止修改时，
      if (!this.field.isUpdatable || this.field.isUpdatable !== 1) {
        return
      }
      // 打开面板之前。
      // 吧values 中的备注。填充到 options 的 valuesRemark 去
      if (this.field) {
        if (this.field.values && this.field.values.length > 0) {
          const values = this.field.values;
          for (let i = 0; i < values.length; i++) {
            for (let j = 0; j < this.field.options.length; j++) {
              if (this.field.options[j].select) {
                if (this.field.options[j].id === values[i].attrValue) {
                  this.field.options[j].valuesRemark = values[i].desc
                }
              }
            }
          }
        }
      }
      this.openPopup = true;
      this.show = true;
    },
    /**
     * 判断 确认 按钮是否可以提交
     * @returns {boolean}
     */
    checkCheckBoxDisableSubmit() {
      if (!this.field.required) {
        return false
      } else {
        let returnValue = true
        for (let i = 0; i < this.field.options.length; i++) {
          if (this.field.options[i].select) {
            returnValue = false
          }
        }
        return returnValue
      }
    },

    /**
     * 输入 input 的值
     * @param val
     * @param info
     */
    fieldInput(val, info) {
      console.log('val', val)
      info.valuesRemark = val
      info.error = false
      this.$forceUpdate()
    },

    /**
     * 输入other 的值
     * @param val
     */
    fieldInputOther(val) {
      this.field.otherValue = val
      this.field.error = false
      this.$forceUpdate()
    },

    /**
     * 多选题选中和取消
     * @param option
     */
    choosebox(option) {
      if (option.select) {
        option.select = false
      } else {
        option.select = true
      }
      this.$forceUpdate()
    },
    /**
     * 提交多选题的答案
     */
    updata() {
      const options = this.field.options
      let values = []
      let closePopup = true
      for (let i = 0; i < options.length; i++) {
        if (options[i].select) {
          let value = { valueText: options[i].attrValue, attrValue: options[i].id, questions: options[i].questions }
          if (this.field.itemAfterHasEnter === 1 && this.field.itemAfterHasEnterRequired === 1) {
            if (!options[i].valuesRemark && options[i].attrValue !== '其他') {
              console.log('这个option的备注没有填写，不能关闭', options[i].attrValue)
              options[i].error = true
              closePopup = false
            }
          }
          if (options[i].attrValue === '其他' && this.field.hasOtherOption === 1 && this.field.otherEnterRequired === 1) {
            if (!this.field.otherValue) {
              this.field.error = true
              console.log('这个其他的备注没有填写，不能关闭')
              closePopup = false
            }
          }
          if (options[i].attrValue !== '其他') {
            value.desc = options[i].valuesRemark
          }
          values.push(value)
        }
      }
      if (!closePopup) {
        this.$forceUpdate()
        return;
      }
      this.field.values = values
      if (values.length > 0) {
        this.noValues = false
      } else {
        this.noValues = true
      }
      this.show = false
    },

    /**
     * 检查当前组件是否 有值。
     * 没有值时。 更新dom 中的颜色 为 未提交状态
     */
    checkFieldValues() {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          if (this.$refs[this.field.id] && this.$refs[this.field.id].firstChild) {
            this.$refs[this.field.id].firstChild.firstChild.style = "color:red "
            this.$refs[this.field.id].style = "border: 1px solid red !important;"
          }
        } else {
          if (this.$refs[this.field.id] && this.$refs[this.field.id].firstChild) {
            this.$refs[this.field.id].firstChild.firstChild.style = ""
            this.$refs[this.field.id].style = ""
          }
        }
      }
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
    isHealth:{
      type:String,
      default:'0'
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
