import Vue from 'vue';
import FormCheckFuncEvent from '../formVue'

import {
  Picker,
  DatetimePicker
} from 'vant';

Vue.use(Picker);
Vue.use(DatetimePicker);


export default {
  data() {
    return {
      show: false,
      currentTime: '',
      btn: true,
      noClick: false
    };
  },
  beforeMount() {
    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })
    if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
      this.currentTime = this.field.values[0].attrValue
    } else {
      this.currentTime = '12:00'
    }
    if (this.field.values[0].attrValue&&this.isHealth === '1'){
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
  mounted() {
    if (this.field.values && this.field.values[0].attrValue === undefined) {
      this.$refs[this.field.id].firstChild.firstChild.style = ""

    } else {
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
    }
  },
  methods: {
    showPopup() {
      if (this.noClick){
        this.$emit('noClick')
        return
      }
      if (this.field.isUpdatable !== undefined && this.field.isUpdatable === 0) {
        return;
      }
      this.show = true;
    },
    close() {
      this.btn = false
      this.checkFieldValues()
    },
    updata() {
      this.show = false
      this.checkFieldValues()
      this.field.values = []
      this.field.values.push({
        attrValue: this.currentTime,
        attrText: this.currentTime
      })
      this.btn = true
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"

    },

    /**
     * 检查当前组件是否 有值。
     * 没有值时。 更新dom 中的颜色 为 未提交状态
     */
    checkFieldValues() {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          this.btn = false
        } else {
          this.btn = true
        }
      }
    }

  },

};
