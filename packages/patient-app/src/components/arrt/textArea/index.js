import Vue from 'vue';
import FormCheckFuncEvent from '../formVue'

import {
  Divider
} from 'vant';

Vue.use(Divider);
export default {
  data: function () {
    return {
      message: '',
      show: false,
      noClick:false
    }
  },
  mounted() {
    if (this.field.values[0]&&this.field.values[0].attrValue&&this.isHealth === '1'){
      this.noClick = true
    }
    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })
    if (this.field) {
      if (this.field.values === undefined) {
        this.field.values = [{attrValue: ''}]
      } else {
        if (this.field.values&&this.field.values.length === 0) {
          this.field.values =[{attrValue: ''}]
        }
      }
    }
  },
  methods: {
    isCanClikc() {
      if (this.noClick) {
        this.$emit('noClikc')
      }
    },
    fieldInput() {
      this.checkFieldValues()
    },
    change() {
      this.checkFieldValues()
    },
    checkFieldValues() {
      if (this.field.required) {
        if (!this.field.values || !this.field.values[0] || !this.field.values[0].attrValue) {
          this.show = true
        } else {
          this.show = false
        }
      }
    }
  },
  props: {
    field: {
      type: Object,
    },
    isHealth:{
      type:String,
      default:'0'
    }
  }


}
