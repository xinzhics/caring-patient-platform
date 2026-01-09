import hospital from './hospitalList/hospitalList.vue'

export default {
  components: {
    hospital
  },
  data () {
    return {
      show: false,
      redBorder: true,
      scrollTop: 0,
      noClick: false
    }
  },
  methods: {

    showPopup () {
      if (this.noClick) {
        this.$emit('noClick')
        return
      }
      // 当设置禁止修改时，
      if (this.field.isUpdatable !== undefined && this.field.isUpdatable === 0) {
        this.show = false
      } else {
        this.show = true
      }
      this.scrollTop = document.documentElement.scrollTop
      this.show = true
    },
    hide (showlist) {
      this.show = showlist
      this.redBorder = showlist
      setTimeout(() => {
        window.document.documentElement.scrollTop = this.scrollTop
      }, 2000)
    }
  },
  props: {
    field: {
      type: Object,
      default: {}
    },
    isHealth: {
      type: String,
      default: '0'
    }
  },
  watch: {
    show: function (val, old) {
      if (val === true) {
        document.body.style.overflow = 'hidden'
      } else {
        window.document.documentElement.scrollTop = this.scrollTop
        document.body.style.overflow = 'visible'
      }
    }
  },
  created (){
    if (this.field && this.field.values[0] && !this.field.values[0].valueText) {
      this.field.values.splice(0, 1)
    } else if (this.field && this.field.values[0] && this.field.values[0].valueText && this.isHealth === '1') {
      this.noClick = true
    }
  }
}
