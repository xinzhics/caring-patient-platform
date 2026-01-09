export default {
  data() {
    return {
      show: false,
      selectOption: {},
      showred: false,
      border:true
    };
  },

  mounted () {
    if (this.field) {
      if (this.field.values && this.field.values.length > 0) {
        const value = this.field.values[0]
        this.selectOption = {
          attrValue: value.valueText,
          id: value.attrValue
        }
      }
    }
    if (this.field.modifyTags === true) {
      this.border = true
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    genderHandle: {
      type: Function
    }
  }
}
