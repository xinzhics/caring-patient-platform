import Vue from 'vue';
import {
  Picker,
  Button
} from 'vant';
import FormCheckFuncEvent from "../formVue";

Vue.use(Picker);
Vue.use(Button);


export default {
  data() {
    return {
      show: false,
      defaultIndex: 160,
      heights: new Map(),
      columns: [{
        values: []
      }, ],
      btn: true,
      noClikc:false
    };
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
  beforeMount() {
    FormCheckFuncEvent.$on('formResultCheck', ()=>{
      this.checkFieldValues()
    })

    let arr2 = new Array();
    // 分
    for (var i = 10; i <= 250; i++) {

      arr2.push(i + this.field.rightUnit)
      this.heights.set(i + '', i - 10)
    }
    this.columns[0].values = arr2
  },

  props: {
    field: {
      type: Object,
      default: {}
    },
    heightHandle: {
      type: Function
    },
    isHealth:{
      type:String,
      default:'0'
    }
  },
  methods: {
    showPopup() {
      if (this.noClikc) {
        this.$emit('noClikc')
        return
      }
      //打开时。 如果有值，查找选中的选项
      if (this.field.values && this.field.values[0] && this.field.values[0].attrValue) {
        let index = this.heights.get(this.field.values[0].attrValue)
        this.defaultIndex = index
      }
      this.show = true;
    },

    close() {
      this.btn = false
      this.checkFieldValues()
    },

    updata() {
      try {
        this.$refs.value.confirm()
        let value = this.$refs.value.getValues()
        let a = value.toString()
        let values = a.slice(0, -2)
        this.field.values[0].attrValue = values
        this.heightHandle(values)
        this.show = false
        this.btn = true
        this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
      } catch (e) {

      } finally {
        this.show = false
        this.btn = true
      }


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
