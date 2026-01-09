import Vue from 'vue'
import {
  NumberKeyboard,
  Field,
  Form,
  Cell,
  CellGroup,
  Popup,
} from 'vant'
import FormCheckFuncEvent from "../formVue";

Vue.use(Field)
Vue.use(NumberKeyboard)
Vue.use(Form)
Vue.use(Cell)
Vue.use(CellGroup)
Vue.use(Popup);
export default {
  created() {
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
    if (this.field.values === undefined) {
      this.field.values = [{
        attrValue: ''
      }]
    } else {
      if (this.field.values.length === 0) {
        this.field.values = [{
          attrValue: ''
        }]
      }
    }
    if (this.field.values &&this.field.values[0] &&this.field.values[0].attrValue&&this.isHealth==='1') {
      this.noClick = true
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
  data: function () {
    return {
      mobile: '',
      show: false,
      showDlog: false,
      noClick:false
    }
  },
  methods: {
    isCanClick() {
      if (this.noClick) {
        this.$emit('noClick')
      }
    },
    showDlogs(){
      this.showDlog=false
    },
    closeDlog(){
      this.showDlog=false
    },
    /**
     * 输入时判断
     */
    fieldInput() {
      this.checkFieldValues()
    },
    change(val) {
      if (val.length === 11) {
        this.mobile = val
      }
      this.field.values = []
      this.field.values.push({
        attrValue: val
      })
    },
    /**
     * 检查当前组件是否 有值。
     * 没有值时。 更新dom 中的颜色 为 未提交状态
     */
    checkFieldValues() {
      if (this.field.required) {

        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          this.show = true
        } else {
          this.show = false
        }
      }

      if (this.show) {
        return
      }



      if (this.field.values[0].attrValue && this.field.values[0].attrValue.length !== 11) {
        this.$toast.fail('请输入正确的手机号')
        this.show = true

      } else {
        this.show = false
      }
    }
  }
}
