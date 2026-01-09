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
  created() {
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
        if (this.field.values.length === 0) {
          this.field.values =[{attrValue: ''}]
        }
      }
    }
  },
  methods: {
    isCanClick() {
      if (this.noClick) {
        this.$emit('noClick')
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
      if (this.field.maxEnterNumber) {
        if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
          if(this.field.values[0].attrValue.length >= this.field.maxEnterNumber) {
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
  },
  props: {
    field: {
      type: Object,
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
