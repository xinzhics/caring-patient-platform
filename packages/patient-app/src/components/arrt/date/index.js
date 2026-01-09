import Vue from 'vue';
import {
  Picker,
  DatetimePicker, Popup, Cell
} from 'vant';
import FormCheckFuncEvent from "../formVue";

Vue.use(DatetimePicker);
Vue.use(Picker);
Vue.use(Popup);
Vue.use(Cell);

export default {
  data() {
    return {
      show: false,
      currentDate: new Date(),
      btn: true,
      minDate: undefined,
      maxDate: new Date(2200, 1, 1),
      noClikc:false
    };
  },
  created () {

    if (this.field.minValue === undefined) {
      this.minDate = new Date(1949, 0, 1)
    } else {
      this.minDate = new Date(this.field.minValue)
    }
  },
  mounted() {
    if (this.field.values && this.field.values[0].attrValue&&this.isHealth==='1'){
      this.noClikc = true
    }
    if (this.field.values && this.field.values[0].attrValue === undefined) {
      this.$refs[this.field.id].firstChild.firstChild.style = ""

    } else {
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
    }

  },
  beforeMount () {
    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })
    // 有数据
    if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
      if (this.field.values[0].attrValue.indexOf('-') > -1) {
        const data =  this.field.values[0].attrValue.split('-')
        this.currentDate = new Date(data[0], data[1] - 1, data[2])
      } else if (this.field.values[0].attrValue.indexOf('/') > -1) {
        const data =  this.field.values[0].attrValue.split('/')
        this.currentDate = new Date(data[0], data[1] - 1, data[2])
      }
    } else {
      if (this.field.defaultValue!==undefined) {
        const data =  this.field.defaultValue.split('-')
        this.currentDate = new Date(data[0], data[1] - 1, data[2])
      } else if (this.field.defaultValue===undefined) {
        this.currentDate = new Date()
      }
    }
    // 判断是否限定了日期
    if (this.field.defineChooseDate && this.field.defineChooseDate === 1) {
      // 只能选当前及以前的时间 1
      if (this.field.defineChooseDateType && this.field.defineChooseDateType === 1) {
        this.maxDate = new Date();
        if (this.field.minValue) {
          this.minDate = new Date(this.field.minValue)
        }else {
          this.minDate = new Date(1945,0,1)
        }
      } else if (this.field.defineChooseDateType && this.field.defineChooseDateType === 2){
        // 只能选当前 及 以后的时间 2
        this.maxDate = new Date(2200, 0, 1);
        this.minDate = new Date()
      }
    } else {
      if (this.field.minValue) {
        this.minDate = new Date(this.field.minValue)
      }
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    ageHandle: {
      type: Function
    },
    isHealth:{
      type:String,
      default:'0'
    }
  },
  methods: {
    showPopup() {
      if (this.noClikc){
        this.$emit('noClikc')
        return
      }
      this.show = true;
    },
    close() {
      this.btn = false
      this.checkFieldValues()
    },
    updata() {
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333 !important"
      let value = this.$refs.value.getPicker().getValues();
      this.field.values = [];
      const date = value[0].substring(0, 4) + '-' + value[1].substring(0, 2) + '-' +  value[2].substring(0, 2)
      this.field.values.push({attrValue: date, attrText: date})
      this.show = false
      this.btn = true
      if (this.field.exactType === 'Birthday') {
        console.log(this.field.values[0].attrValue)
        this.ageHandle(this.field.values[0].attrValue)
      }
      this.checkFieldValues()
      const attrValue = this.field.values[0].attrValue
      if (attrValue) {
        FormCheckFuncEvent.$emit('CHECK_TIME_CHANGE', attrValue)
      }
    },

    dataValueFormatter() {
      if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
        if (this.field.values[0].attrValue.indexOf('-') > -1) {
          const data =  this.field.values[0].attrValue.split('-')
          return data[0] + '年' + data[1] + '月' + data[2] + '日'
        } else if (this.field.values[0].attrValue.indexOf('/') > -1) {
          const data =  this.field.values[0].attrValue.split('/')
          return data[0] + '年' + data[1] + '月' + data[2] + '日'
        }
      } else {
        return ''
      }
    },

    formatter(type, val) {
      if (type === 'year') {
        return val + '年';
      }
      if (type === 'month') {
        return val + '月';
      }
      if (type === 'day') {
        return val + '日';
      }
      return val;
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
