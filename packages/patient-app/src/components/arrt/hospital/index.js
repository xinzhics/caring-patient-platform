import hospital from "./hospitalList/hospitalList.vue"
import FormCheckFuncEvent from "../formVue";
export default {
  components: {
    hospital, //医院
  },
  data () {
    return {
      show: false,
      showred: false,
      redBorder: true,
      scrollTop:0,
      noClikc:false
    }
  },
  mounted() {
    FormCheckFuncEvent.$on('formResultCheck', () => {
      this.checkFieldValues()
    })
  },
  methods: {

    showPopup () {
      if (this.noClikc) {
        this.$emit('noClikc')
        return
      }
      this.scrollTop =document.documentElement.scrollTop
      this.show = true;
    },
    hide (showlist) {
      this.show = showlist
      this.redBorder = showlist
      setTimeout(() => {
        window.document.documentElement.scrollTop = this.scrollTop
      }, 2000);
    },
    checkFieldValues() {
      if (this.field){
        if (this.field.required){
          if (this.field.values.length===0){
            this.showred = true
            return
          } else {
            this.showred = false
          }
        }else {
          this.showred = false
        }
      }
      console.log(this.field)
      console.log(this.showred)
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    isHealth:{
      type:String,
      default:'0'
    }
  },
  created () {
      // console.log(typeof (this.field.values) === 'object');
    // console.log(this.field.values[0].valueText);
    if (this.field && this.field.values[0] && !this.field.values[0].valueText) {
      this.field.values.splice(0, 1);
    } else if (this.field && this.field.values[0] && this.field.values[0].valueText&&this.isHealth === '1') {
      this.noClikc = true
    }
  },
  watch: {
    show: function (val, old) {
      if (val === true) {
        document.body.style.overflow = "hidden";
      } else {
        window.document.documentElement.scrollTop = this.scrollTop
        document.body.style.overflow = "visible";
      }
    },
    field:{
      handler:function (val,old) {
        if (val){
          console.log(val)
          if (val.values.lenght>0&&val.values[0].valueText) {
            this.showred = false
            console.log(this.showred)
          }
        }
      },
      deep:true
    }
  }
}
