import Vue from 'vue';
import {
  Field
} from 'vant';
import FormCheckFuncEvent from "../formVue";

Vue.use(Field);
export default {
  data() {
    return {
      show: false,
      message: "",
      maxValue: false,
      minValue: false,
      noClick:false
    };
  },
  mounted() {
    if (this.field.values &&this.field.values[0] &&this.field.values[0].attrValue&&this.isHealth==='1') {
      this.noClick = true
    }
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
    // 如果这个组件是个  监测指标。则当用户进来填写是，查看是否有基准值。
    // 有基准值时， 默认设置基准值 为当前组件的 结果
    if (this.field.exactType === 'monitoringIndicators' && this.field.referenceValue) {
      // 是个监测指标。看看本题结果有没有填写。
      if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
        this.field.values = []
        this.field.values.push({
          attrValue: this.field.referenceValue
        })
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
  methods: {
    isCanClick() {
      if (this.noClick) {
        this.$emit('noClick')
      }
    },

    updata() {
      this.checkFieldValues()
    },
    /**
     * 检验 数字是否可以是小数， 数值小数点位数是否超出
     */
    input() {
      if (this.field.canDecimal === 1 && this.field.precision) {
        // 判断当前输入 是否符合要求。 超出小数后，不可输入
        let value = this.field.values[0].attrValue
        if (value.indexOf('.') > -1) {
          let integer = value.toString().substring(0, value.indexOf('.'))
          let numbers = value.toString().substring(value.indexOf('.') + 1)
          if (numbers.length >= this.field.precision) {
            this.field.values[0].attrValue = parseFloat(integer + '.' + numbers.substring(0, this.field.precision))
          }
        }
      }
    },
    /**
     * 检查当前的值是否符合要求
     */
    checkFieldValues() {
      let checkFail = false
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          checkFail = true
        }
      }
      if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
        const values = parseFloat(this.field.values[0].attrValue)
        // 组件有值。判断组件 是否小于等于 最大值
        this.maxValue = false
        this.minValue = false
        if (this.field.maxValue !== undefined) {
          const maxValue = parseFloat(this.field.maxValue)
          if (values > maxValue) {
            this.maxValue = true
            checkFail = true
          }
        }
        if (this.field.minValue !== undefined) {
          // 组件有值， 判断组件 是否大于等于 最小值
          const minValue = parseFloat(this.field.minValue)
          if (values < minValue) {
            this.minValue = true
            checkFail = true
          }
        }
      }
      if (checkFail) {
        this.show = true
      } else {
        this.show = false
      }
    }
  },
  watch: {
    field:
     {
      handler: function (newVal, oldVal) {
         let num =parseFloat(newVal.values[0].attrValue)
         this.message = ''
         let a ="1"
         let b ="1.0"
         console.log(a===b);
         if (newVal && newVal.dataFeedBacks) {
          for (let i = 0; i < newVal.dataFeedBacks.length; i++) {
            if (num <= parseFloat(newVal.dataFeedBacks[i].maxValue) && num >=parseFloat(newVal.dataFeedBacks[i].minValue) ) {
              this.message = newVal.dataFeedBacks[i].promptText
              console.log(num,i,parseFloat(newVal.dataFeedBacks[i].minValue));
            }
          }
         }

        },
        deep: true,
          immediate: true
      },
}
};
