import Vue from 'vue';
import {
  Form,
  Cell,
  CellGroup,
  Icon
} from 'vant';
import FormCheckFuncEvent from "../formVue";

Vue.use(Form);
Vue.use(Cell);
Vue.use(Icon);
Vue.use(CellGroup);
export default {
  data() {
    return {
      checkFail: false,
      show: false,
      openSelected: false,
      otherValueFail: false,
      noClick: false
    };
  },
  created() {
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
  },
  mounted() {
    if (this.field.values[0].attrValue && this.isHealth === '1') {
      this.noClick = true
    }
    if (this.field.values && this.field.values[0].attrValue === undefined) {
      this.$refs.color.firstChild.firstChild.style = ""

    } else {
      this.$refs.color.firstChild.firstChild.style = "color:#333"
    }
  },
  methods: {
    showbox() {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      if (this.field.isUpdatable !== undefined && this.field.isUpdatable === 0) {
        return;
      }
      this.show = !this.show
      this.openSelected = true
    },
    /**
     * 选择
     */
    chooseBox(info, index) {
      if (this.field.values[0].attrValue === info.id) {
        this.field.values[0] = []
        this.show = false
        this.$refs.color.firstChild.firstChild.style = ""

      } else {
        this.field.values = []
        this.field.values.push({
          valueText: info.attrValue,
          attrValue: info.id
        })
        this.show = false
        this.$refs.color.firstChild.firstChild.style = "color:#333"
      }

    },
    /**
     * 选项是否被选中
     * @param info
     * @param index
     * @returns {boolean}
     */
    optionSelected(info, index) {
      if (this.field.values) {
        if (this.field.values[0]) {
          if (this.field.values[0].attrValue === info.id) {
            return true
          }
        }
      }
      return false

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
     * 检查当前组件是否 有值。
     * 没有值时。 更新dom 中的颜色 为 未提交状态
     */
    checkFieldValues() {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          this.checkFail = true
        } else {
          this.checkFail = false
        }
      }
      if (this.field.hasOtherOption === 1 && this.field.otherEnterRequired === 1) {
        if (this.hasOther(this.field.values)) {
          if (!this.field.otherValue) {
            this.otherValueFail = true
          } else {
            this.otherValueFail = false
          }
        } else {
          this.otherValueFail = false
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
    isHealth: {
      type: String,
      default: '0'
    }
  },
  watch: {
    show: function (val, old) {
      if (this.show === false) {
        if (this.openSelected) {
          this.checkFieldValues()
        }
      }
    }
  }
};
