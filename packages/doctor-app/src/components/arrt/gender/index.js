import FormCheckFuncEvent from "../formVue";

export default {
  data() {
    return {
      show: false,
      selectOption: {},
      showred: false,
      noClick: false
    };
  },

  methods: {
    choosesex(info, index) {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      if (!this.field.isUpdatable || this.field.isUpdatable !== 1) {
        return
      }
      this.selectOption = {
        attrValue: info.attrValue,
        id: info.id
      }
      this.field.values = []
      this.field.values.push({
        valueText: info.attrValue,
        attrValue: info.id
      })
      if (info.attrValue === '男') {
        this.genderHandle(0)
      } else {
        this.genderHandle(1)
      }
      this.checkFieldValues()
    },
    checkFieldValues() {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          // 性别的选择框 变红
          this.showred=true
        } else {
          // 性别的选项框 取消变红
          this.showred=false
        }
      }
    }
  },
  mounted() {
    if (this.field.values&&this.field.values[0]&&this.field.values[0].attrValue&&this.isHealth==='1' ){
      this.noClick = true
    }
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
    if (this.field) {
      if (this.field.values && this.field.values.length > 0) {
        const value = this.field.values[0]
        this.selectOption = {
          attrValue: value.valueText,
          id: value.attrValue
        }
      }
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    genderHandle: {
      type: Function
    },
    userRole: {
      type: String,
      default: ''
    },
    isHealth:{
      type:String,
      default:'0'
    }
  }
}
