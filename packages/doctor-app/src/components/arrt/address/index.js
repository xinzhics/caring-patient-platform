import Vue from 'vue';
import {
  Picker
} from 'vant';
import FormCheckFuncEvent from "../formVue";
Vue.use(Picker);

export default {
  components: {
    textArea: () => import('@/components/arrt/textArea/textarea'),
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
  data() {
    return {
      show: false,
      add: [],
      columns: [],
      setAddress: false,
      values: "",
      addressDetailsFail: false,
      showred: false,
      noClick:false
    }
  },
  created() {
    // 解析服务器address.js数据
    fetch('https://caring.obs.cn-north-4.myhuaweicloud.com/public_js/address.js')
        .then(response => {
          return response.json();
        })
        .then(data => {
          Vue.set(this, 'columns', data);
        })
  },
  mounted() {
    if (this.field.values && this.field.values[0].attrValue.length === 0) {
      this.$refs[this.field.id].firstChild.firstChild.style = "color: #b6b6b6"
    } else {
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
      if (this.isHealth === '1') {
        this.noClick = true
      }
    }
  },
  beforeMount() {
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
    if (this.field.values && this.field.values.length > 0 && this.field.values[0].attrValue === undefined) {
      this.field.values = [{
        attrValue: []
      }]
    } else {
      this.values = this.field.values[0].attrValue.join("-")
      this.setColumnValue
    }
  },
  methods: {
    isCanClick() {
      if (this.noClick) {
        this.$emit('noClick')
      }
    },
    showPopup() {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      if (!this.field.isUpdatable || this.field.isUpdatable !== 1) {
        return
      }
      this.show = true;
    },
    // 点击确定按钮
    updata() {
      this.$refs.addressPicker.confirm()
      let values = this.$refs.addressPicker.getValues()
      let attrValue = []
      for (let i = 0; i < values.length; i++) {
        attrValue.push(values[i].name)
      }
      this.field.values = []
      this.field.values.push({
        attrValue: attrValue
      })
      this.values = attrValue.join('-')
      this.show = false
      this.checkFieldValues()
      this.$refs[this.field.id].firstChild.firstChild.style = "color:#333"
    },
    addressDetail() {
      this.checkFieldValues()
    },
    checkFieldValues() {
      if (this.field.required) {
        if (this.field.values && this.field.values[0] && this.field.values[0].attrValue && this.field.values[0].attrValue.length > 0) {
          // 选择了地址
          this.showred = false
        } else {
          // 没有选择地址。
          this.showred = true
        }
      }
      // 有详细输入框。并且 输入框必填
      if (this.field.hasAddressDetail === 1 && this.field.hasAddressDetailRequired === 1) {
        if (this.field.value && this.field.value.length > 0) {
          // 输入框填写了值
          this.addressDetailsFail = false
        } else {
          // 输入框没有填写值
          this.addressDetailsFail = true
        }
      }
    }
  }
}
