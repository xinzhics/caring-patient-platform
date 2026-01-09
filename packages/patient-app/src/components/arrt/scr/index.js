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
  watch:{
    ccrField:{
      handler(val,old){
        console.log(val,old)
        if (val.values[0].attrValue=='-'){
          val.values[0].attrValue=''
        }
      },
      deep:true
    },
    gfrField:{
      handler(val,old){
        console.log(val,old)
        if (val.values[0].attrValue=='-'){
          val.values[0].attrValue=''
        }
      },
      deep:true
    },
  },
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
    let obj = [this.ccrField, this.gfrField, this.courseOfDiseaseField]

    for (let i = 0; i < obj.length; i++) {

      if (obj[i] !== undefined) {
        this.num++
      }
    }
    if (this.num === 2) {
      this.margin = "16px"

    }
    if (this.num === 3) {
      this.margin = "16px"
    }
    // console.log("obj", obj);
  },
  beforeUpdate () {
    let obj = [this.ccrField, this.gfrField, this.courseOfDiseaseField]

    for (let i = 0; i < obj.length; i++) {

      if (obj[i] !== undefined) {
        this.num++
      }
    }
    if (this.num === 2) {
      this.margin = "16px"

    }
    if (this.num === 3) {
      this.margin = "16px"
    }

  },
  mounted() {
    if (this.field&&this.field.values[0]&&this.field.values[0].attrValue&&this.isHealth==='1') {
      this.noClick = true
    }
    this.scrHandle(this.field.values[0].attrValue)
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    scrHandle: {
      type: Function
    },
    ccrField: {
      type: Object,
      default: undefined
    },
    gfrField: {
      type: Object,
      default: undefined
    },
    courseOfDiseaseField: {
      type: Object,
      default: undefined
    },
    isHealth:{
      type:String,
      default:'0'
    }
  },
  data: function () {
    return {
      message: "",
      show: false,
      sex: undefined,
      user: '',
      margin:"",
      num:0,
      noClick:false
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
      this.scrHandle(this.field.values[0].attrValue)
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
    }
  }
}
