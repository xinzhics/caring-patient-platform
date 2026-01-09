import Vue from 'vue';
import FormCheckFuncEvent from '../formVue'
import {
  Picker,
  Button
} from 'vant';

Vue.use(Picker);
Vue.use(Button);


export default {
  data() {
    return {
      show: false,
      defaultIndex: 55,
      weights: new Map(),
      noClikc: false,
      columns: [
        // 第一列
        {
          values: []
        }
      ],
      btn: true
    };
  },
  mounted() {
    if (this.field.values && this.field.values[0].attrValue === undefined||!this.field.isUpdatable ||this.field.isUpdatable !== 1) {
      this.$refs[this.field.id].firstChild.firstChild.style = ""
    } else {
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
    }
    if (this.field.values && this.field.values[0].attrValue !== undefined&&this.isHealth === '1') {
      this.noClikc = true
    }
  },
  beforeMount() {

    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })

    var arr2 = new Array();

    for (var i = 5; i <= 200; i++) {

      arr2.push(i + this.field.rightUnit)
      this.weights.set(i + '', i - 5)
    }
    this.columns[0].values = arr2

  },

  props: {
    field: {
      type: Object,
      default: {}
    },
    weightHandle: {
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
  },
  methods: {
    showPopup() {
      if (this.noClikc) {
        this.$emit('noClick')
        return
      }
      if (!this.field.isUpdatable || this.field.isUpdatable !== 1) {
        return
      }
      if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
        let index =  this.weights.get(this.field.values[0].attrValue)
        this.defaultIndex = index
      }
      this.show = true;
    },
    close() {
      this.checkFieldValues()
    },
    updata() {
      this.$refs[this.field.id + 'value'].confirm()
      let value = this.$refs[this.field.id + 'value'].getValues()
      let a = value.toString()
      let values = a.slice(0, -2)
      this.field.values[0].attrValue = values
      this.weightHandle(values)
      this.show = false
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"

    },
    /**
     * 校验表单
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
