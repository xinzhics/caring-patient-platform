<template>
  <section>
    <x-header :left-options="{backText: ''}">
      购药提醒
    </x-header>

    <div style="position: relative">
      <div style="width: 100%">
        <van-image style="width: 100%" :src="require('@/assets/my/MedicineReminder.png')" fit="fill"/>
      </div>
      <div style="position: absolute; bottom: 30px; width: 100%; ">
        <div style="position: relative">
          <div style="margin-left: 15px; margin-right: 15px; background: #B5D2FF; height: 16px; border-radius: 10px;">
          </div>
          <div style="position: absolute; top: 10px; width: 100%;">
            <div style="background: #ffffff; margin-left: 30px; margin-right: 30px; padding: 15px;">
              <div style="background: #f5f5f5; display: flex; align-items: center; padding: 10px;">
                <div style="width:72px;height:72px;border:1px solid rgba(0,0,0,0.1);overflow: hidden;">
                  <van-image  :src="alldata.icon" fit="fill"/>
                </div>
                <div style="margin-left: 8px;">
                  <p style="font-size:16px;color:rgba(102,102,102,1);"><span v-if="alldata.isOtc" style="margin-right:5px;background:#FF5555;color:#fff;padding:0px 5px;font-size:12px;border-radius:2px">OTC</span>{{ alldata.name }}
                  </p>
                  <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ alldata.genericName }}</p>
                  <p style="font-size:14px;color:rgba(102,102,102,0.6);">{{ alldata.manufactor }}</p>
                  <p style="font-size:12px;color:rgba(102,102,102,0.6);">规格:{{ alldata.spec }}</p>
                </div>

              </div>
            </div>
          </div>

        </div>

      </div>
    </div>

    <x-button type="primary" style="width: 200px; background: #6F7D97; margin-top: 250px;" v-if="buyDrugsUrlSwitch === 0"
              @click.native="submit()">立即购药</x-button>
  </section>
</template>

<script>
  import Api from '@/api/Content.js'

  export default {
    name: "PurchaseMedicineReminder",
    data() {
      return {
        alldata: {},
        buyDrugsUrl: '',
        firstBuyDrugsUrl: '',
        buyDrugsUrlSwitch: 0
      }
    },
    methods: {
      submit() {
        if (this.firstBuyDrugsUrl) {
          window.location = this.firstBuyDrugsUrl
        } else if (this.buyDrugsUrl) {
          window.location = this.buyDrugsUrl
        }
        /*this.$router.replace({
          path: '/medicine/detail',
          query: {drugsId: this.$route.query.drugId}
        })*/
      },
      getMedicineInfo(id) {
        const params = {
          id: id,
        }
        Api.sysDrugsPagedetail(params).then((res) => {
          if (res.data.code === 0 && res.data.data && res.data.data.length > 0) {
            this.alldata = res.data.data[0]
          }
        })
      },
      getMedicineMonitoringSetting(id) {
        Api.getMedicineMonitoringSetting(id).then((res) => {
          if (res.data.code === 0 && res.data.data) {
            this.firstBuyDrugsUrl = res.data.data.buyingMedicineUrl
          }
        })
      },
      getStatus(){
        Api.regGuidegetGuide({}).then((res) => {
          if(res.data.code === 0){
            console.log(res.data.data)
            this.buyDrugsUrl = res.data.data.buyDrugsUrl
            this.buyDrugsUrlSwitch = res.data.data.buyDrugsUrlSwitch
          }
        })
      },
    },
    mounted() {
      if (this.$route.query && this.$route.query.drugId) {
        this.getMedicineInfo(this.$route.query.drugId)

        // 获取购药预警的购药链接
        this.getMedicineMonitoringSetting(this.$route.query.drugId)


      }
      this.getStatus()
    }
  }
</script>

<style lang="less" scoped>

</style>
