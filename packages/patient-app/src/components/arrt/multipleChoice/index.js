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
      noClick:false
    };
  },
  created() {
    console.log('多选!!!!!!!!!!!!!!!!!',this.field)
    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })
  },
  mounted() {
    console.log(this.field.values[0].attrValue&&this.isHealth === '1')
    if (this.field.values[0].attrValue&&this.isHealth === '1'){
      this.noClick = true
    }
    if (this.field) {
      if (this.field.values && this.field.values[0] && this.field.values.length > 1) {
        this.noValues = false
      } else if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
        this.noValues = false
      }
    }
    console.log('多选',this.field )
  },
  methods: {
    showPopup() {
      console.log('1111111111111')
      if (this.noClick) {
        this.$emit('noClikc')
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
     * 输入 input 的值
     * @param val
     * @param info
     */
    fieldInput(val, info) {
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
     * 提交多选题的答案
     */
    updata() {
      // 校验 选中的选项 输入框是否必填，且 填
      const options = this.field.options
      let values = []
      let closePopup = true
      for (let i = 0; i < options.length; i++) {
        if (options[i].select) {
          let value = { valueText: options[i].attrValue, attrValue: options[i].id, questions: options[i].questions }
          if (this.field.itemAfterHasEnter === 1 && this.field.itemAfterHasEnterRequired === 1) {
            if (!options[i].valuesRemark && options[i].attrValue !== '其他') {
              options[i].error = true
              closePopup = false
            }
          }
          if (options[i].attrValue === '其他' && this.field.hasOtherOption === 1 && this.field.otherEnterRequired === 1) {
            if (!this.field.otherValue) {
              this.field.error = true
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
      this.field.values = []
      this.field.values.push(...values)
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
