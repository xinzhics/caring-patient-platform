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
      noClikc: false,
      defaultIndex: 55,
      weights: new Map(),
      columns: [
        // 第一列
        {
          values: []
        }
      ],
      btn: true
    };
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
    isHealth:{
      type:String,
      default:'0'
    }
  },
  mounted() {
    if (this.field.values && this.field.values[0].attrValue === undefined) {
      this.$refs[this.field.id].firstChild.firstChild.style = "color: #b6b6b6"

    } else {
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
    }
    if (this.field.values && this.field.values[0].attrValue !== undefined&&this.isHealth === '1') {
      this.noClikc = true
    }
  },
  methods: {
    showPopup() {
      if (this.noClikc) {
        this.$emit('noClikc')
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

      try {
        this.$refs[this.field.id + 'value'].confirm()
        let value = this.$refs[this.field.id + 'value'].getValues()
        let a = value.toString()
        let values = a.slice(0, -2)
        this.field.values[0].attrValue = values
        this.weightHandle(values)
        this.show = false
        this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
      } catch(err) {
      } finally {
        this.show = false
      }


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
