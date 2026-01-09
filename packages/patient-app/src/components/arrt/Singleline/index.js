import Vue from 'vue'
import {
  Field,
  Form,
  Cell,
  CellGroup,
  Popup
} from 'vant'
import FormCheckFuncEvent from "../formVue";

Vue.use(Field)
Vue.use(Form)
Vue.use(Cell)
Vue.use(CellGroup)
Vue.use(Popup);
export default {
  created () {
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
  data: function () {
    return {
      show: false,
      noClick:false
    }
  },
  mounted() {
    if (this.field.values[0].attrValue&&this.isHealth === '1'){
      console.log('this.field',this.field,!this.field.values[0].attrValue&&this.isHealth === '1')
      this.noClick = true
    }
  },
  methods: {
    isCanClikc() {
      if (this.noClick) {
        this.$emit('noClikc')
      }
    },
    /**
     * 输入时判断
     */
    fieldInput() {
      this.checkFieldValues()
    },
    change() {
      this.checkFieldValues()
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
      if (this.field.maxEnterNumber) {
        if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
          if (this.field.values[0].attrValue.length >= this.field.maxEnterNumber) {
            this.$refs[this.field.id].$el.children[0].lastElementChild.lastElementChild.style = "color:#FF5555"
          } else {
            if (this.$refs[this.field.id] && this.$refs[this.field.id].$el && this.$refs[this.field.id].$el.children[0]) {
              this.$refs[this.field.id].$el.children[0].lastElementChild.lastElementChild.style = ""
            }
          }
        } else {
          if (this.$refs[this.field.id] && this.$refs[this.field.id].$el && this.$refs[this.field.id].$el.children[0]) {
            this.$refs[this.field.id].$el.children[0].lastElementChild.lastElementChild.style = ""
          }
        }
      }
    }
  }
}
