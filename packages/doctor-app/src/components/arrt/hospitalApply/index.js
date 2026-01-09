import hospital from "./hospitalList/hospitalList.vue"
export default {
  components: {
    hospital, //医院
  },
  data () {
    return {
      show: false,
      redBorder: true,
      scrollTop:0
    }
  },
  methods: {

    showPopup () {
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
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
  },
  created () {
    if (this.field && this.field.values[0] && !this.field.values[0].valueText) {
      this.field.values.splice(0, 1);
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
    }
  }
}
