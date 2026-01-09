<template>
  <div>
    <van-popup v-model="show" position="bottom" :close-on-click-overlay="false">
      <van-picker
          show-toolbar
          :title="title"
          ref="multiPicker"
          @confirm="confirm"
          :columns="hourList"
          @cancel="cancel"
      />
    </van-popup>
  </div>
</template>

<script>
import {Picker, Popup} from 'vant'

export default {
  name: "hour-time-picker",
  props: {
    title: {
      type: String
    },
    value: {
      type: String,
      default: ''
    },
    show: {
      type: Boolean,
      default: false
    },
    hourClick: {
      type: Function
    },
    defaultIndex2:{
      type:Number
    },
    defaultIndex1:{
      type:Number
    },
  },
  components: {
    [Picker.name]: Picker,
    [Popup.name]: Popup,
  },
  data() {
    return {
      hourList: [
        {
          values: ["00","01", "02", '03', "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"],
          defaultIndex: 0,
        },
        {
          values: ["00", "15", "30", '45'],
          defaultIndex: 0,
        },
      ],

    }
  },
  beforeUpdate() {
    // if (this.value) {
    //   // let a = this.value.split(':')
    //   for (let i = 0; i < this.hourList[0].values.length; i++) {
    //     if (a[0] === this.hourList[0].values[i]) {
    //       this.hourList[0].defaultIndex = i
    //     }
    //   }
    //   for (let i = 0; i < this.hourList[1].values.length; i++) {
    //     if (a[1] === this.hourList[1].values[i]) {
    //       this.hourList[1].defaultIndex = i
    //     }
    //   }
    // }
    this.hourList[0].defaultIndex=this.defaultIndex1
    this.hourList[1].defaultIndex=this.defaultIndex2
  },
  methods: {
    cancel() {
      this.hourClick()
    },
    confirm(data) {
      this.hourClick(data)
    }
  }
}
</script>

<style scoped>

</style>
