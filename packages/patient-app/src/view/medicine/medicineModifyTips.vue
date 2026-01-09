<template>
  <section>
    <navBar pageTitle="修改提醒"></navBar>
    <div v-if="data.length>0" style="padding-top: 10px; width: 100%">
      <div v-for="(i, k) in data" :key="k" style="padding: 15px 25px; background: white">
        <div style="font-size: 16px; color: #666666">{{ i.medicineName }}</div>
        <div style="color: #B8b8b8; font-size: 14px;">1日{{ i.numberOfDay }}次</div>

        <div v-for="(item, index) in i.patientDrugsTimeSettingList" :key="index" @click="clickTime(k, index, item)"
             style="display: flex; align-items: center; margin-top: 15px; width: 100%;">
          <div style="color: #999999; font-size: 16px; max-width: 120px;">第{{ index + 1 }}次</div>
          <div
              style="padding: 8px 15px; background: #f5f5f5; border-radius: 5px; flex: 1; margin-left: 15px; display: flex; position: relative">
            <div style="color: #666666; font-size: 15px;">{{ item.triggerTimeOfTheDay }}</div>
            <div style="position: absolute; right: 0; margin-right: 15px;">
              <van-icon name="arrow" color="#B8B8B8"/>
            </div>
          </div>
        </div>
      </div>
      <div style="background: white; display: flex; justify-content: center; padding-top: 50px; padding-bottom: 50px;">
        <x-button type="primary" style="height: 44px; width: 200px; background: #66728B; font-size: 16px;"
                  @click.native="submit">确定
        </x-button>
      </div>

    </div>
    <div v-else style="display: flex;flex-direction: column;justify-content: center;align-items: center;padding-top: 50vh;transform: translateY(-25%)">
      <div><img width="152px" src="@/assets/my/no-medicine.png" alt=""></div>
      <div style="text-align: center;color: #999999;font-size: 15px">暂无用药数据</div>
    </div>
    <hour-time-picker :show="show" title="选择时间" :defaultIndex1="defaultIndex1" :defaultIndex2="defaultIndex2"
                      :hour-click="hourClick"/>
  </section>
</template>

<script>
import Vue from 'vue';
import {Toast} from 'vant';

Vue.use(Toast);
import Api from '@/api/Content.js'

export default {
  name: "medicineModifyTips",
  components: {
    hourTimePicker:() => import('@/components/picker/hourTimePicker'),
    navBar: () => import('@/components/headers/navBar'),
  },
  data() {
    return {
      data: [],
      show: false,
      value: '',
      index: 0,
      index1: 0,
      defaultIndex1: 0,
      defaultIndex2: 0
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    hourClick(data) {
      if (data) {
        let time = data[0] + ":" + data[1] + ':00'
        this.data[this.index].patientDrugsTimeSettingList[this.index1].triggerTimeOfTheDay = time
      }
      this.show = false
    },
    clickTime(key, index, value) {
      this.defaultIndex1 = Number(value.triggerTimeOfTheDay.substring(0, 2))
      if (Number(value.triggerTimeOfTheDay.substring(3, 5)) / 15 < 1) {
        this.defaultIndex2 = 0
      } else {
        this.defaultIndex2 = Number(value.triggerTimeOfTheDay.substring(3, 5)) / 15
      }
      // console.log(this.defaultIndex2)

      this.index = key;
      this.index1 = index
      this.value = value
      this.show = true
    },
    getArray(drugsTime) {
      let list = drugsTime.split(',')
      return list
    },
    submit() {
      if (this.data.length === 0) {
        return
      }
      console.log()
      let istrue = true
      this.data.forEach(item => {
        let timedata = new Map();
        item.patientDrugsTimeSettingList.forEach((i, index) => {
          timedata.set(i.triggerTimeOfTheDay, i.triggerTimeOfTheDay)
          console.log(timedata.size)
          if (index == item.patientDrugsTimeSettingList.length - 1 && timedata.size < item.numberOfDay) {
            Toast('时间重复请重新选择');
            istrue = false
          }
        })

      })
      if (istrue == true) {
        Api.putMedicine(this.data)
            .then(res => {
              if (res.data && res.data.code === 0) {
                this.$router.replace('/calendar/index')
              }
            })
      }

    },
    getData() {
      Api.getCurrentMedicine()
          .then(res => {
            if (res.data.data && res.data.data.length > 0) {
              this.data = res.data.data
            }
          })
    },
    getTime(data) {
      let a = data.substring(0, 5)
      return a
    }
  }
}
</script>

<style less="scss" scoped>

/deep/ .vux-header {
  margin-bottom: 0px;
  height: 50px;
  position: fixed;
  width: 100%;
  z-index: 999;
  top: 0;
  left: 0;
}

</style>
